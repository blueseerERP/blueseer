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
package com.blueseer.frt;


import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDILogBrowse;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.getShipAddressInfo;
import static com.blueseer.edi.EDI.Create990;
import static com.blueseer.frt.frtData.addCFOTransaction;
import com.blueseer.frt.frtData.cfo_det;
import com.blueseer.frt.frtData.cfo_item;
import com.blueseer.frt.frtData.cfo_mstr;
import com.blueseer.frt.frtData.cfo_sos;
import static com.blueseer.frt.frtData.deleteCFOMstr;
import com.blueseer.frt.frtData.frt_ctrl;
import static com.blueseer.frt.frtData.getBrokerInfo;
import static com.blueseer.frt.frtData.getCFOCtrl;
import static com.blueseer.frt.frtData.getCFODefaultRevision;
import static com.blueseer.frt.frtData.getCFODet;
import static com.blueseer.frt.frtData.getCFOItem;
import static com.blueseer.frt.frtData.getCFOLines;
import static com.blueseer.frt.frtData.getCFOMstr;
import static com.blueseer.frt.frtData.getCFORevisions;
import static com.blueseer.frt.frtData.getDriverPhone;
import static com.blueseer.frt.frtData.updateCFOTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.getShipperHeader;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.clog;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import static com.blueseer.utl.BlueSeerUtils.currformatUS;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.logChange;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.hasEDIXref;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.isValidShipper;
import static com.blueseer.utl.OVData.updateFreightOrderStatus;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *
 * @author vaughnte
 */
public class CFOMaint extends javax.swing.JPanel implements IBlueSeerT {
    
    // global variable declarations
    public String globalstatus = "Open";
    public boolean lock_ddshipper = false;
    public int currentstopline = 0;
    boolean isLoad = false;
    boolean carrierPOV = true;
    public static cfo_mstr x = null;
    public static frt_ctrl fc = null;
    public static ArrayList<cfo_det> cfodetlist = null;
    public static ArrayList<cfo_sos> soslist = null;
    public static ArrayList<cfo_item> cfoitemlist = null;
    public static LinkedHashMap<String, String[]> kvstop = new  LinkedHashMap<String, String[]>();
    public static LinkedHashMap<String, ArrayList<String[]>> itemmap = new  LinkedHashMap<String, ArrayList<String[]>>();
     public static LinkedHashMap<String, String> stk = new  LinkedHashMap<String, String>();
    
                
    // global datatablemodel declarations       
    
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("type"),
                getGlobalColumnTag("date"),
                getGlobalColumnTag("name"), 
                getGlobalColumnTag("addr1"), 
                getGlobalColumnTag("city"), 
                getGlobalColumnTag("state"), 
                getGlobalColumnTag("zip"),
                getGlobalColumnTag("reference")
            });
      
    javax.swing.table.DefaultTableModel itemdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                new String[]{
                getGlobalColumnTag("stopline"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("order"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("pallets"),
                getGlobalColumnTag("weight"),
                getGlobalColumnTag("description")
            });
    
   javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("value"), 
                getGlobalColumnTag("amount")
            });
   
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
   
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 2);  
        
         if ("LD".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("UL".equals(status)) {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        } else if ("PU".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
   
   
    
    
      
   
    public CFOMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
      // interface functions implemented
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
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
           } else if (this.type.equals("get")) {
             updateForm();  
             tbkey.requestFocus();
            } else if (this.type.equals("add") && message[0].equals("0")) {
             initvars(key);
           } else if (this.type.equals("update") && message[0].equals("0")) {
             initvars(key);    
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
       isLoad = true;
       
       kvstop.clear();
       itemmap.clear();
       
       clearStopFields();
       
       fc = getCFOCtrl(null);
       // note:  fc.frtc_function() = 1 for Trucking POV...and 0 for Customer POV
       carrierPOV = (fc.frtc_function().equals("1"));
       
       
       ArrayList<String[]> initDataSets = frtData.getCFOMaintInit(fc.frtc_function());
       
       
       
       if (! carrierPOV) {
           lblclient.setText(getGlobalColumnTag("carrier"));
           lblnumber.setText(getGlobalColumnTag("shipper"));
           btaddshipper.setVisible(true);
       } else {
           lblclient.setText(getGlobalColumnTag("customer"));
           lblnumber.setText(getGlobalColumnTag("custorder"));
           btaddshipper.setVisible(false);
       }
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", jPanelMain);
       jTabbedPane1.add("Location", jPanelLocation);
       jTabbedPane1.add("Charges", jPanelCharges);
       
       lblstatus.setText("");
       lblstatus.setForeground(Color.black);
       lblrevision.setText("");
       lblrevision.setForeground(Color.black);
       
       tbkey.setText("");
       cbhazmat.setSelected(false);
       cbrev.setSelected(false);
       cbedi.setSelected(false);
       cbedi.setEnabled(false);
       
       ddorderstatus.setBackground(null);
       ddorderstatus.setSelectedItem("open");
       
       ddstopsequence.removeAllItems();
       ddstopsequence.addItem("");
       
       dcdate.setDate(new java.util.Date());
       
        tbkey.setText("");
        tbnumber.setText("");
        cbhazmat.setSelected(false);
        
        ddvehicle.setSelectedIndex(0);
        tbtrailer.setText("");
        ddorderstatus.setSelectedIndex(0);
        dddriver.setSelectedIndex(0);
        tbdrivercell.setText("");
        ddfotype.setSelectedIndex(0);
        ddbroker.setSelectedIndex(0);
        tbbrokercontact.setText("");
        tbbrokercell.setText("");
        ddratetype.setSelectedIndex(0);
        tbforate.setText("");
        tbmileage.setText("");
        tbdriverrate.setText("");
        cbstandard.setSelected(false);
        tbtotweight.setText("");
        dcorddate.setDate(bsmf.MainFrame.now);
        dcconfdate.setDate(bsmf.MainFrame.now);
        tbcharges.setText("");
        tbcharges.setEditable(false);
        tbcost.setText("");
        tbcost.setEditable(false);
        
       // tablelist.clear();
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        orddet.getTableHeader().setReorderingAllowed(false);
      
        
        itemdetmodel.setRowCount(0);
        itemdet.setModel(itemdetmodel);
        
        sacmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        sactable.getTableHeader().setReorderingAllowed(false);
       
        String defaultsite = null;
        
        ddrevision.removeAllItems();
        ddsite.removeAllItems();
        ddstate.removeAllItems();
        ddcust.removeAllItems();
        ddequiptype.removeAllItems();
        ddservicetype.removeAllItems();
        ddvehicle.removeAllItems();
        dddriver.removeAllItems();
        ddbroker.removeAllItems();
        
        for (String[] s : initDataSets) {
                      
            if (s[0].equals("states")) {
              ddstate.addItem(s[1]); 
            }
            if (s[0].equals("countries")) {
            
            }
            if (s[0].equals("customers")) {
              ddcust.addItem(s[1]); 
            }
            if (s[0].equals("carriers")) {
              ddcust.addItem(s[1]); 
            }
            if (s[0].equals("vehicle")) {
              ddvehicle.addItem(s[1]); 
            }
            if (s[0].equals("driver")) {
              dddriver.addItem(s[1]); 
            }
            if (s[0].equals("broker")) {
              ddbroker.addItem(s[1]); 
            }
            if (s[0].equals("servicetypes")) {
              ddservicetype.addItem(s[1]); 
            }
            if (s[0].equals("equipmenttypes")) {
              ddequiptype.addItem(s[1]); 
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            
        }
        
        ddsite.setSelectedItem(defaultsite);
        ddstate.insertItemAt("", 0);
        ddstate.setSelectedIndex(0);
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ddvehicle.insertItemAt("", 0);
        ddvehicle.setSelectedIndex(0);
        dddriver.insertItemAt("", 0);
        dddriver.setSelectedIndex(0);
        ddbroker.insertItemAt("", 0);
        ddbroker.setSelectedIndex(0);
        ddservicetype.insertItemAt("", 0);
        ddservicetype.setSelectedIndex(0);
        ddequiptype.insertItemAt("", 0);
        ddequiptype.setSelectedIndex(0);
       
        ddsactype.removeAllItems();
        ddsactype.addItem("charge");
        ddsactype.addItem("discount");
        ddsactype.addItem("passive");
        ddsactype.setSelectedIndex(0);
        
        // add schedule types to stk (schedule type key) LinkedHashMap
        stk.put("", pass);
        dddatetype.removeAllItems();
        dddatetype.addItem("Scheduled Pickup Date");
        dddatetype.addItem("Earliest Pickup Date");
        dddatetype.addItem("Latest Pickup Date");
        dddatetype.addItem("Scheduled Pickup Date, Needs Confirmation");
        dddatetype.addItem("Scheduled Pickup Date, Appointment Confirmed");
        dddatetype.addItem("Scheduled Delivery Date");
        dddatetype.addItem("Scheduled Delivery Date, Needs Confirmation");
        dddatetype.addItem("Scheduled Delivery Date, Appointment Confirmed");
        
        ddtimetype1.removeAllItems();
        ddtimetype1.addItem("Scheduled Pickup Time");
        ddtimetype1.addItem("Earliest Pickup Time");
        ddtimetype1.addItem("Latest Pickup Time");
        ddtimetype1.addItem("Scheduled Delivery Time");
        ddtimetype1.addItem("Earliest Delivery Time");
        ddtimetype1.addItem("Latest Delivery Time");
        
        ddtimetype2.removeAllItems();
        ddtimetype2.addItem("Scheduled Pickup Time");
        ddtimetype2.addItem("Earliest Pickup Time");
        ddtimetype2.addItem("Latest Pickup Time");
        ddtimetype2.addItem("Scheduled Delivery Time");
        ddtimetype2.addItem("Earliest Delivery Time");
        ddtimetype2.addItem("Latest Delivery Time");
        ddtimetype1.insertItemAt("", 0);
        ddtimetype1.setSelectedIndex(0);
        ddtimetype2.insertItemAt("", 0);
        ddtimetype2.setSelectedIndex(0);
        
        
        ddtime1.removeAllItems();
        ddtime2.removeAllItems();
        String timevar = "";
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 12; m++) {
                timevar = String.format("%02d", (h)) + ":" + String.format("%02d", (m * 5));
                ddtime1.addItem(timevar);
                ddtime2.addItem(timevar);
            }
        }
        ddtime1.insertItemAt("", 0);
        ddtime1.setSelectedIndex(0);
        ddtime2.insertItemAt("", 0);
        ddtime2.setSelectedIndex(0);
        
        ddtimezone.removeAllItems();
        ddtimezone.addItem("AST");
        ddtimezone.addItem("EST");
        ddtimezone.addItem("CST");
        ddtimezone.addItem("MST");
        ddtimezone.addItem("PST");
        ddtimezone.addItem("AKST");
        ddtimezone.addItem("HST");
        
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
        ddrevision.addItem("1");
        ddrevision.setSelectedIndex(0);
        
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   cbedi.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   if (ddorderstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("closed")) == 0) {
                        btadd.setEnabled(false);
                        btupdate.setEnabled(false);
                        btdelete.setEnabled(false);
                        btcommit.setEnabled(false);   
                        lblstatus.setText(getMessageTag(1097));
                        lblstatus.setForeground(Color.blue);
                   }
        } else {
                   tbkey.setForeground(Color.red); 
                   lblstatus.setText(getMessageTag(1098));
                   lblstatus.setForeground(Color.red);
        }
    }
    
    public boolean validateInput(dbaction x) {
       
               
        Map<String,Integer> f = OVData.getTableInfo("cfo_mstr");
        int fc;

        fc = checkLength(f,"cfo_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }     
         
        fc = checkLength(f,"cfo_rmks");
        if (tbremarks.getText().length() > fc ) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            return false;
        } 
               
        return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
    // String[] m = addCFOMstr(createRecord());
     String[] m = addCFOTransaction(createDetRecord(), createRecord(), createItemRecord(), createSOSRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
      String[] m = new String[2];
     // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getCFOLines(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < orddet.getRowCount(); j++) {
             if (orddet.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
     
       m = updateCFOTransaction(tbkey.getText(), ddrevision.getSelectedItem().toString(), badlines, createDetRecord(), createRecord(), createItemRecord(), createSOSRecord());
     
     
        // change log check
       if (m[0].equals("0")) {
           cfo_mstr _x = this.x;
           cfo_mstr _y = createRecord();     
         ArrayList<admData.change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),_x,_y);
         if (! c.isEmpty()) {
             addChangeLog(c);
         } 
       }
       
       return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteCFOMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
        // change log check
        if (m[0].equals("0")) {
            ArrayList<admData.change_log> c = new ArrayList<admData.change_log>();
            c.add(clog(this.x.cfo_nbr(), 
                     this.x.getClass().getName(), 
                     this.getClass().getSimpleName(), 
                     "deletion", 
                     "", 
                     ""));
            if (! c.isEmpty()) {
               addChangeLog(c);
            } 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getCFOMstr(key);
       cfodetlist = getCFODet(key[0], key[1]); 
       cfoitemlist = getCFOItem(key[0], key[1]); 
        return x.m();
    }
    
    public cfo_mstr createRecord() { 
        String derived = String.valueOf(BlueSeerUtils.boolToInt(cbderivedrate.isSelected())) + "," +
                String.valueOf(BlueSeerUtils.boolToInt(cbderivedmiles.isSelected())) + "," +
                String.valueOf(BlueSeerUtils.boolToInt(cbderivedweight.isSelected()));
        
        String logic = "";
        
        cfo_mstr x = new cfo_mstr(null, 
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                tbnumber.getText(),
                ddrevision.getSelectedItem().toString(),
                ddservicetype.getSelectedItem().toString(),
                ddequiptype.getSelectedItem().toString(),
                ddvehicle.getSelectedItem().toString(),
                tbtrailer.getText(),
                ddorderstatus.getSelectedItem().toString(),
                "", // delivery status
                dddriver.getSelectedItem().toString(),
                tbdrivercell.getText(),
                ddfotype.getSelectedItem().toString(),
                ddbroker.getSelectedItem().toString(),
                tbbrokercontact.getText(),
                tbbrokercell.getText(),
                ddratetype.getSelectedItem().toString(),
                tbforate.getText(),
                tbmileage.getText(),
                tbdriverrate.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbstandard.isSelected())),
                tbtotweight.getText(),
                BlueSeerUtils.setDateFormat(dcorddate.getDate()).toString(),
                BlueSeerUtils.setDateFormat(dcconfdate.getDate()).toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbhazmat.isSelected())),
                "0", // expenses
                tbcharges.getText(),
                tbcost.getText(),
                "", // bol
                tbremarks.getText(),
                derived,
                logic,
                ddsite.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbedi.isSelected())),
                "", // edi rejection reason..to be added
                String.valueOf(BlueSeerUtils.boolToInt(cbrev.isSelected()))
        );
        return x;
    }

    public ArrayList<cfo_det> createDetRecord() {
        ArrayList<cfo_det> list = new ArrayList<cfo_det>();
         //   for (int j = 0; j < orddet.getRowCount(); j++) {               
         for (Map.Entry<String, String[]> z : kvstop.entrySet()) { 
         String[] v = z.getValue();
         cfo_det x = new cfo_det(null, 
                tbkey.getText().toString(),
                ddrevision.getSelectedItem().toString(), 
                v[0],
                v[1],
                v[2],
                v[3],
                v[4],
                v[5],
                v[6],
                v[7],
                v[8],
                v[9],
                v[10],
                v[11],
                v[12],
                v[13],
                v[14],
                v[15],
                v[16],
                v[17],
                v[18],
                v[19].replace(defaultDecimalSeparator, '.'),
                v[20].replace(defaultDecimalSeparator, '.'),
                v[21].replace(defaultDecimalSeparator, '.'),
                v[22],
                v[23],
                v[24],
                v[25],
                v[26],
                v[27],
                v[28],
                v[29],
                v[30].replace(defaultDecimalSeparator, '.'),
                v[31].replace(defaultDecimalSeparator, '.')
                );  
                list.add(x);
            }    
           
            
        return list;
    }
     
    public ArrayList<cfo_item> createItemRecord() {
        ArrayList<cfo_item> list = new ArrayList<cfo_item>();
          for (Map.Entry<String, ArrayList<String[]>> z : itemmap.entrySet()) { 
           ArrayList<String[]> itemlist = z.getValue();
                for (String[] s : itemlist) {
                cfo_item x = new cfo_item(null, 
                tbkey.getText().toString(), // nbr
                ddrevision.getSelectedItem().toString(),        
                s[0], // stopline
                s[1], // itemline
                s[2], // item
                s[3], // desc
                s[4], // order
                s[5], // qty
                s[6], // pallets
                s[7], // weight
                s[8], // ref
                s[9] // remarks
                );  
                list.add(x);
                }
          }
           
            
        return list;
    }
    
    public ArrayList<cfo_sos> createSOSRecord() {
         ArrayList<cfo_sos> list = new ArrayList<cfo_sos>();
         for (int j = 0; j < sactable.getRowCount(); j++) {
             cfo_sos x = new cfo_sos(null, tbkey.getText().toString(),
                ddrevision.getSelectedItem().toString(),
                sactable.getValueAt(j, 1).toString(),
                sactable.getValueAt(j, 0).toString(),
                sactable.getValueAt(j, 2).toString(),
                sactable.getValueAt(j, 3).toString().replace(defaultDecimalSeparator, '.'));     
                list.add(x);
         }
       
        return list;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCFOBrowseUtil(luinput.getText(),0, "cfo_nbr");
        } else {
         luModel = DTData.getCFOBrowseUtil(luinput.getText(),0, "cfo_cust");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lblclient", this.getClass().getSimpleName())); 
        
    }

    public void updateForm() throws ParseException {
        isLoad = true;
        
        ArrayList<String> revlist = getCFORevisions(x.cfo_nbr());
        for (String r : revlist) {
         ddrevision.addItem(r);
        }
        ddrevision.setSelectedItem(x.cfo_revision());
        /*
        if (! x.cfo_revision().equals(getCFODefaultRevision(x.cfo_nbr()))) {
            lblrevision.setText("Not Default");
        } else {
            lblrevision.setText("Default");
        }
        */
        
        
        String[] delimfields = x.cfo_derived().split(",",-1);
        if (delimfields != null) {
            for (int i = 0; i < delimfields.length; i++) {
                if (i == 0) { // rate
                  cbderivedrate.setSelected(BlueSeerUtils.ConvertStringToBool(delimfields[i]));  
                }
                if (i == 1) { // miles
                  cbderivedmiles.setSelected(BlueSeerUtils.ConvertStringToBool(delimfields[i]));  
                }
                if (i == 2) { // weight
                  cbderivedweight.setSelected(BlueSeerUtils.ConvertStringToBool(delimfields[i]));  
                }
            }
        }
        
        
        
        tbkey.setText(x.cfo_nbr());
        ddcust.setSelectedItem(x.cfo_cust());
        tbnumber.setText(x.cfo_custfonbr());
        ddrevision.setSelectedItem(x.cfo_revision());
        cbhazmat.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_ishazmat()));
        cbrev.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_defaultrev()));
        cbedi.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_edi()));
        ddservicetype.setSelectedItem(x.cfo_servicetype());
        ddequiptype.setSelectedItem(x.cfo_equipmenttype());
        ddvehicle.setSelectedItem(x.cfo_truckid());
        tbtrailer.setText(x.cfo_trailernbr());
        ddorderstatus.setSelectedItem(x.cfo_orderstatus());
        dddriver.setSelectedItem(x.cfo_driver());
        tbdrivercell.setText(x.cfo_drivercell());
        ddfotype.setSelectedItem(x.cfo_type());
        ddbroker.setSelectedItem(x.cfo_brokerid());
        tbbrokercontact.setText(x.cfo_brokercontact());
        tbbrokercell.setText(x.cfo_brokercell());
        ddratetype.setSelectedItem(x.cfo_ratetype());
        tbforate.setText(x.cfo_rate());
        tbmileage.setText(x.cfo_mileage());
        tbdriverrate.setText(x.cfo_driverrate());
        cbstandard.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_driverstd()));
        tbtotweight.setText(x.cfo_weight());
        dcorddate.setDate(BlueSeerUtils.parseDate(x.cfo_orddate()));
        dcconfdate.setDate(BlueSeerUtils.parseDate(x.cfo_confdate()));
        tbcharges.setText(x.cfo_misccharges());
        tbcost.setText(x.cfo_cost());
        ddsite.setSelectedItem(x.cfo_site());
        
        // now detail
        kvstop.clear();
        for (cfo_det cfod : cfodetlist) {
            // det table first
            myorddetmodel.addRow(new Object[]{
            cfod.cfod_stopline(), 
            cfod.cfod_type(), 
            cfod.cfod_date(),
            cfod.cfod_name(), 
            cfod.cfod_line1(), 
            cfod.cfod_city(), 
            cfod.cfod_state(),
            cfod.cfod_zip(),
            x.cfo_custfonbr()
            });
            
            // kvstop map
            String[] v = new String[]{
                        cfod.cfod_stopline(),
                        cfod.cfod_seq(),
                        cfod.cfod_type(),
                        cfod.cfod_code(), 
                        cfod.cfod_name(), 
                        cfod.cfod_line1(),
                        cfod.cfod_line2(), 
                        cfod.cfod_line3(),
                        cfod.cfod_city(), 
                        cfod.cfod_state(),
                        cfod.cfod_zip(), 
                        cfod.cfod_country(),
                        cfod.cfod_phone(),
                        cfod.cfod_email(),
                        cfod.cfod_contact(),
                        cfod.cfod_misc(), 
                        cfod.cfod_rmks(),
                        cfod.cfod_reference(), 
                        cfod.cfod_ordnum(), 
                        cfod.cfod_weight(), 
                        cfod.cfod_pallet(), 
                        cfod.cfod_ladingqty(),
                        cfod.cfod_hazmat(), 
                        cfod.cfod_datetype(), 
                        cfod.cfod_date(), 
                        cfod.cfod_timetype1(),
                        cfod.cfod_time1(), 
                        cfod.cfod_timetype2(), 
                        cfod.cfod_time2(),
                        cfod.cfod_timezone(),
                        cfod.cfod_rate(),
                        cfod.cfod_miles()};
            kvstop.put(v[0], v);
            
            // now dropdown sequence
            ddstopsequence.addItem("STOP: " + cfod.cfod_stopline());
        }
        
        // now items
        itemmap.clear();
        for (cfo_item cfoi : cfoitemlist) {
            String[] v = new String[]{
                        cfoi.cfoi_stopline(),
                        cfoi.cfoi_itemline(),
                        cfoi.cfoi_item(),
                        cfoi.cfoi_itemdesc(), 
                        cfoi.cfoi_order(), 
                        cfoi.cfoi_qty(),
                        cfoi.cfoi_pallets(), 
                        cfoi.cfoi_weight(),
                        cfoi.cfoi_ref(), 
                        cfoi.cfoi_rmks()};
           
            if (itemmap.containsKey(cfoi.cfoi_stopline())) {
               ArrayList<String[]> t = itemmap.get(cfoi.cfoi_stopline());
               t.add(v);
               itemmap.put(cfoi.cfoi_stopline(), t);
            } else {
               ArrayList<String[]> g = new ArrayList<String[]>();
               g.add(v);
               itemmap.put(cfoi.cfoi_stopline(), g); 
            }
            
        }
        
        // summary charges and discounts
        if (soslist != null) {
        for (cfo_sos sos : soslist) {
            if (! sos.cfos_type().equals("tax")) {  // don't show header tax again...
            sacmodel.addRow(new Object[]{
                      sos.cfos_type(), 
                      sos.cfos_desc(),
                      sos.cfos_amttype(),
                      sos.cfos_amt()});
            }
        }
        }
        
        isLoad = false;
        if (ddstopsequence.getItemCount() >= 1) {
            ddstopsequence.setSelectedIndex(0);
        }
        setAction(x.m());
    }
    
    
    // misc
    public ArrayList<String[]> costToDetail() {
        
        double totamt = bsParseDouble(tbforate.getText());
        
        ArrayList<String[]> list = new ArrayList<String[]>();
        // create line item 1 with bulk rate
             String[] s = new String[]{
             "1", // shline
             ddratetype.getSelectedItem().toString(), // item
             tbkey.getText(), // order
             tbnumber.getText(), // cust fo
             currformatUS(tbcost.getText()), // netprice
             "0" // taxamt
             };
        list.add(s);
        
        // additional line items contain charges/surcharges from cfo_sos table
        ArrayList<String[]> sac = OVData.getFreightSAC(tbkey.getText()); 
        int cnt = 1;
        String myamttype = "";
        double myamt = 0.00;
        if (sac != null && ! sac.isEmpty()) {
            for (String[] ss : sac) {
                myamttype = ss[3].toString();
                myamt = bsParseDoubleUS(ss[4].toString());
                // adjust if percent based
                 if (ss[3].toString().equals("percent") && bsParseDoubleUS(ss[4].toString()) > 0) {
                   myamttype = "amount";
                   if (ss[2].equals("discount")) {
                     myamt = -1 * (bsParseDoubleUS(ss[4].toString()) / 100) * totamt;
                   } else {
                     myamt = (bsParseDoubleUS(ss[4].toString()) / 100) * totamt;  
                   }
                 }    
                cnt++;
                String[] c1 = new String[]{
                 String.valueOf(cnt), // shline
                 ss[1], // item
                 tbkey.getText(), // order
                 tbnumber.getText(), // cust fo
                 currformatDoubleUS(myamt), // netprice
                 "0" // taxamt
                 };
                 list.add(c1);
            }
        }
        return list;
    }
       
    
    public void getStatus(String nbr) {
        
        
    }  
    
    public void summarize() {
      double weight = 0.00;
      double rate = 0.00;
      double pallets = 0.00;
      double miles = 0.00;
      
      double dol = 0.00;
      
      double totalcharges = 0.00;
      
      for (int j = 0; j < sactable.getRowCount(); j++) {
             totalcharges += Double.valueOf(sactable.getValueAt(j, 3).toString()); 
      }  
      tbcharges.setText(String.valueOf(totalcharges));
      
      
      for (Map.Entry<String, ArrayList<String[]>> z : itemmap.entrySet()) { 
           ArrayList<String[]> itemlist = z.getValue();
           for (String[] s : itemlist) {
               weight += Double.valueOf(s[7]);
               pallets += Double.valueOf(s[6]);
           }
      }
      
      for (Map.Entry<String, String[]> z : kvstop.entrySet()) { 
           String[] v = z.getValue();
           if (! v[30].isBlank() && ! v[31].isBlank()) {
           rate += Double.valueOf(v[30]);
           miles += Double.valueOf(v[31]);
           }
      }
      
      
      // updates header fields
      if (cbderivedweight.isSelected()) {
        tbtotweight.setText(String.valueOf(weight));
      }
      if (cbderivedrate.isSelected()) {
        tbforate.setText(String.valueOf(rate));
      }
      if (cbderivedmiles.isSelected()) {
        tbmileage.setText(String.valueOf(miles));
      } else {
        if (! tbmileage.getText().isBlank()) {
         miles = Double.valueOf(tbmileage.getText());  // if not derived...take text value
        }
      }
      
      if (! tbforate.getText().isBlank()) {
            if (ddratetype.getSelectedItem().toString().equals("Flat Rate")) {
                dol = Double.valueOf(tbforate.getText()) + Double.valueOf(tbcharges.getText());
            } else {
                dol = (Double.valueOf(tbforate.getText()) * miles) + Double.valueOf(tbcharges.getText());
            }
      } 
      tbcost.setText(String.valueOf(dol));
      
    }
    
        
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
      
    public void shipperChangeEvent(String mykey) {
            
          //initialize weight and unites
          
     
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
                
                res = st.executeQuery("select * from ship_mstr inner join cm_mstr on cm_code = sh_cust inner join cms_det on cms_code = cm_code and cms_shipto = sh_ship " +
                        " inner join wh_mstr on wh_id = sh_wh " +
                        " where sh_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    tbname.setText(res.getString("cms_name"));
                    tbaddr1.setText(res.getString("cms_line1"));
                    tbaddr2.setText(res.getString("cms_line2"));
                    tbcity.setText(res.getString("cms_city"));
                    dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    tbzip.setText(res.getString("cms_zip"));
                    ddstate.setSelectedItem(res.getString("cms_state"));
                 
                }
                     
            
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     
    
    }
    
    public void clearStopFields() {
        isLoad = true;
        ddstopsequence.setSelectedIndex(0);
        ddstoptype.setSelectedIndex(0); 
        if (ddshipto.getItemCount() > 0) {
          ddshipto.setSelectedIndex(0); 
        }
        tbname.setText(""); 
        tbaddr1.setText(""); 
        tbaddr2.setText(""); 
        tbcity.setText(""); 
        if (ddstate.getItemCount() > 0) {
          ddstate.setSelectedIndex(0); 
        }
        
        tbzip.setText(""); 
        
        if (ddcountry.getItemCount() > 0) {
          ddcountry.setSelectedIndex(0); 
        }
        tbphone.setText(""); 
        tbemail.setText(""); 
        tbcontact.setText(""); 
        tbmisc.setText(""); 
        tbremarks.setText(""); 
        
        tbstopitem.setText("");
        tbstopqty.setText("");
        tbstoppallets.setText("");
        tbstopordernbr.setText("");
        tbstopweight.setText("");
        tbstopitemdesc.setText("");
        tbstopmiles.setText("");
        tbstoprate.setText("");
        lblstop.setText("");
        
        itemdetmodel.setRowCount(0);
        
        
        if (ddtime1.getItemCount() > 0) {
          ddtime1.setSelectedIndex(0); 
        }
        if (ddtime2.getItemCount() > 0) {
          ddtime2.setSelectedIndex(0); 
        }
        if (ddtimetype1.getItemCount() > 0) {
          ddtimetype1.setSelectedIndex(0); 
        }
        if (ddtimetype2.getItemCount() > 0) {
          ddtimetype2.setSelectedIndex(0); 
        }
        isLoad = false;
    }
    
    public void setStopState(boolean state) {
        
        // ddstopsequence.setEnabled(state);
       
        
        ddshipto.setEnabled(state);
        tbname.setEnabled(state);
        tbcity.setEnabled(state);
        tbzip.setEnabled(state);
        tbaddr1.setEnabled(state);
        tbaddr2.setEnabled(state);
        ddstate.setEnabled(state);
        ddcountry.setEnabled(state);
        tbmisc.setEnabled(state);
        tbcontact.setEnabled(state);
        tbemail.setEnabled(state);
        tbphone.setEnabled(state);
        tbremarks.setEnabled(state);
        
        btclearstop.setEnabled(state);
        btaddstop.setEnabled(state);
        btupdatestop.setEnabled(state);
        btdeletestop.setEnabled(state);
        
                
        dddatetype.setEnabled(state);
        dcdate.setEnabled(state);
        ddtimetype1.setEnabled(state);
        ddtime1.setEnabled(state);
        ddtimetype2.setEnabled(state);
        ddtime2.setEnabled(state);
        ddtimezone.setEnabled(state);
        
        tbstopitem.setEnabled(state);
        tbstopqty.setEnabled(state);
        tbstoppallets.setEnabled(state);
        tbstopitemdesc.setEnabled(state);
        tbstopweight.setEnabled(state);
        tbstopordernbr.setEnabled(state);
        tbstoprate.setEnabled(state);
        tbstopmiles.setEnabled(state);
        btdeleteitem.setEnabled(state);
        btadditem.setEnabled(state);
        
    }
    
    public void clientChangeEvent(String mykey) {
            
       if (! isLoad && ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isEmpty() ) {
           lbclientname.setText(cusData.getCustName(ddcust.getSelectedItem().toString()));
           ddshipto.removeAllItems();
           ArrayList<String> list = cusData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (String s : list) {
                ddshipto.addItem(s);
            }
            ddshipto.insertItemAt("",0);
           
           
       } else {
           lbclientname.setText("");
       }
    }
  
    public void clearShipAddress() {
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbphone.setText("");
        tbemail.setText("");
        tbcontact.setText("");
        tbmisc.setText("");
        if (ddstate.getItemCount() > 0) {
        ddstate.setSelectedIndex(0);
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

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        ddvehicle = new javax.swing.JComboBox();
        jLabel101 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        ddequiptype = new javax.swing.JComboBox();
        ddservicetype = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tbtrailer = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        lblnumber = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbforate = new javax.swing.JTextField();
        tbnumber = new javax.swing.JTextField();
        ddcust = new javax.swing.JComboBox<>();
        lblclient = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbdrivercell = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ddfotype = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        ddbroker = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        tbbrokercontact = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tbbrokercell = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        ddratetype = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        tbdriverrate = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        tbtotweight = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        dcorddate = new com.toedter.calendar.JDateChooser();
        dcconfdate = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cbhazmat = new javax.swing.JCheckBox();
        ddorderstatus = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        tbmileage = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        cbstandard = new javax.swing.JCheckBox();
        dddriver = new javax.swing.JComboBox<>();
        lbclientname = new javax.swing.JLabel();
        cbderivedrate = new javax.swing.JCheckBox();
        cbderivedmiles = new javax.swing.JCheckBox();
        cbderivedweight = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        cbedi = new javax.swing.JCheckBox();
        ddrevision = new javax.swing.JComboBox<>();
        lblrevision = new javax.swing.JLabel();
        cbrev = new javax.swing.JCheckBox();
        btaddshipper = new javax.swing.JButton();
        tbcharges = new javax.swing.JTextField();
        tbcost = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btclear = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        jPanelLocation = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btaddstop = new javax.swing.JButton();
        btupdatestop = new javax.swing.JButton();
        btdeletestop = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        ddstate = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        tbmisc = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        ddstoptype = new javax.swing.JComboBox<>();
        jPanel16 = new javax.swing.JPanel();
        dcdate = new com.toedter.calendar.JDateChooser();
        dddatetype = new javax.swing.JComboBox<>();
        ddtime1 = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        ddtimezone = new javax.swing.JComboBox<>();
        ddtime2 = new javax.swing.JComboBox<>();
        ddtimetype1 = new javax.swing.JComboBox<>();
        ddtimetype2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        tbstoprate = new javax.swing.JTextField();
        tbstopmiles = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        tbstopqty = new javax.swing.JTextField();
        tbstopitem = new javax.swing.JTextField();
        tbstoppallets = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbstopitemdesc = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbstopweight = new javax.swing.JTextField();
        tbstopordernbr = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemdet = new javax.swing.JTable();
        btadditem = new javax.swing.JButton();
        btdeleteitem = new javax.swing.JButton();
        ddstopsequence = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        btnewstop = new javax.swing.JButton();
        btclearstop = new javax.swing.JButton();
        lblstop = new javax.swing.JLabel();
        jPanelCharges = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        tbsacamt = new javax.swing.JTextField();
        tbsacdesc = new javax.swing.JTextField();
        percentlabel = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        btsacadd = new javax.swing.JButton();
        btsacdelete = new javax.swing.JButton();
        ddsactype = new javax.swing.JComboBox<>();
        ddsacamttype = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        jTextField1.setText("jTextField1");

        jLabel3.setText("jLabel3");

        jLabel10.setText("jLabel10");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Freight Order Maintenance"));
        jPanelMain.setName("panelmain"); // NOI18N

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddvehicle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test1", "test2", "test3" }));

        jLabel101.setText("Truck ID");
        jLabel101.setName("lblcarrier"); // NOI18N

        jLabel85.setText("EquipType");
        jLabel85.setName("lblequipmenttype"); // NOI18N

        ddequiptype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test1", "test2", "test3" }));
        ddequiptype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddequiptypeActionPerformed(evt);
            }
        });

        ddservicetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2", "test3" }));

        jLabel9.setText("Service");

        jLabel15.setText("Driver");

        jLabel16.setText("Trailer");

        lblnumber.setText("Client Order");
        lblnumber.setName("lblshipper"); // NOI18N

        jLabel23.setText("Rate");

        tbforate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbforateFocusLost(evt);
            }
        });

        ddcust.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));
        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        lblclient.setText("Client");
        lblclient.setName("lblclient"); // NOI18N

        jLabel6.setText("Revision");

        jLabel7.setText("Cell#");

        ddfotype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Spot", "Broker" }));

        jLabel24.setText("Type");

        ddbroker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));
        ddbroker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbrokerActionPerformed(evt);
            }
        });

        jLabel28.setText("Broker");

        jLabel29.setText("Broker Contact");

        jLabel30.setText("Broker Cell#");

        ddratetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flat Rate", "Mileage Rate" }));

        jLabel31.setText("Rate Type");

        tbdriverrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdriverrateFocusLost(evt);
            }
        });

        jLabel32.setText("Miles");

        jLabel33.setText("Weight");

        dcorddate.setDateFormatString("yyyy-MM-dd");

        dcconfdate.setDateFormatString("yyyy-MM-dd");

        jLabel34.setText("Order Date");

        jLabel35.setText("Commit Date");

        cbhazmat.setText("Hazmat");

        ddorderstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "open", "pending", "declined", "cancelled", "intransit", "closed" }));
        ddorderstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddorderstatusActionPerformed(evt);
            }
        });

        jLabel36.setText("Status");

        tbmileage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmileageFocusLost(evt);
            }
        });

        jLabel37.setText("Driver Rate");

        cbstandard.setText("Standard");

        dddriver.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));
        dddriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dddriverActionPerformed(evt);
            }
        });

        cbderivedrate.setText("Derived");
        cbderivedrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbderivedrateActionPerformed(evt);
            }
        });

        cbderivedmiles.setText("Derived");
        cbderivedmiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbderivedmilesActionPerformed(evt);
            }
        });

        cbderivedweight.setText("Derived");

        jLabel40.setText("Site");

        cbedi.setText("EDI");

        cbrev.setText("Default");

        btaddshipper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddshipperActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblclient)
                    .addComponent(lblnumber)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel85)
                    .addComponent(jLabel101)
                    .addComponent(jLabel16)
                    .addComponent(jLabel40))
                .addGap(4, 4, 4)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(ddservicetype, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel24))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(tbnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btaddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(ddrevision, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbrev)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbedi)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblrevision, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbclientname, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel36)))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbbrokercell, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(tbdrivercell, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ddorderstatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(dddriver, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel12Layout.createSequentialGroup()
                                            .addGap(76, 76, 76)
                                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)))
                                        .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)))))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel33))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbhazmat)
                    .addComponent(dcconfdate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcorddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddratetype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbforate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(tbdriverrate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(tbtotweight, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                            .addComponent(tbmileage, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbstandard)
                            .addComponent(cbderivedrate)
                            .addComponent(cbderivedmiles)
                            .addComponent(cbderivedweight))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(lblrevision, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(160, 160, 160))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbclientname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblclient)
                                .addComponent(ddorderstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel36)
                                .addComponent(ddratetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel31)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblnumber)
                                .addComponent(jLabel15)
                                .addComponent(tbforate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23)
                                .addComponent(dddriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbderivedrate))
                            .addComponent(btaddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddrevision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addComponent(cbrev)
                            .addComponent(cbedi))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtotweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33)
                            .addComponent(ddservicetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(cbderivedweight))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdriverrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(cbstandard)
                            .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85)
                            .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel34)
                                .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel101)
                                .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel29))
                            .addComponent(dcorddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcconfdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbbrokercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel30)
                                .addComponent(jLabel35))
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel16)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbhazmat)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel40))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(cbderivedmiles))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel32))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(tbmileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel7))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(tbdrivercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel13.setText("Charges");

        jLabel14.setText("Total");

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        orddet.setModel(new javax.swing.table.DefaultTableModel(
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
        orddet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orddetMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(orddet);

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane8)))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(jLabel76)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addGap(39, 39, 39)
                                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(btcommit)
                                .addGap(153, 153, 153)
                                .addComponent(jLabel13)
                                .addGap(3, 3, 3)
                                .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(79, 79, 79)
                                .addComponent(btprint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel76)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnew)
                            .addComponent(btclear)
                            .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btprint)
                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btupdate)
                        .addComponent(btadd))
                    .addComponent(btdelete)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(btcommit))))
        );

        add(jPanelMain);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName(""); // NOI18N

        btaddstop.setText("Add Stop");
        btaddstop.setName("btaddstop"); // NOI18N
        btaddstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddstopActionPerformed(evt);
            }
        });

        btupdatestop.setText("Update Stop");
        btupdatestop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatestopActionPerformed(evt);
            }
        });

        btdeletestop.setText("Delete Stop");
        btdeletestop.setName("btdeleteitem"); // NOI18N
        btdeletestop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletestopActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));
        jPanel14.setName(""); // NOI18N

        jLabel18.setText("Country");

        jLabel88.setText("Contact");
        jLabel88.setName("lblcontact"); // NOI18N

        jLabel96.setText("Email");
        jLabel96.setName("lblemail"); // NOI18N

        jLabel86.setText("Misc");
        jLabel86.setName("lblmisc"); // NOI18N

        jLabel93.setText("City");
        jLabel93.setName("lblcity"); // NOI18N

        jLabel97.setText("Remarks");
        jLabel97.setName("lblremarks"); // NOI18N

        jLabel17.setText("Location");

        jLabel91.setText("Addr1");
        jLabel91.setName("lbladdr1"); // NOI18N

        jLabel94.setText("State");
        jLabel94.setName("lblstate"); // NOI18N

        jLabel89.setText("Phone");
        jLabel89.setName("lblphone"); // NOI18N

        jLabel82.setText("Name");
        jLabel82.setName("lblname"); // NOI18N

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel90.setText("Addr2");
        jLabel90.setName("lbladdr2"); // NOI18N

        ddstoptype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Load", "Unload Complete", "Unload Partial" }));

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduling"));

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel26.setText("Date");

        jLabel27.setText("Type");

        jLabel2.setText("TimeZone");

        jLabel21.setText("Time Event 1");

        jLabel25.setText("Time Event 2");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel16Layout.createSequentialGroup()
                                    .addComponent(jLabel26)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(dddatetype, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddtimezone, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ddtimetype2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddtimetype1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddtime2, 0, 71, Short.MAX_VALUE)
                                    .addComponent(ddtime1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddatetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtimezone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtimetype1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addGap(2, 2, 2)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtimetype2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ddshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshiptoActionPerformed(evt);
            }
        });

        jLabel12.setText("Stop Type");

        jLabel1.setText("Rate");

        jLabel39.setText("Miles");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82)
                    .addComponent(jLabel91)
                    .addComponent(jLabel90)
                    .addComponent(jLabel93)
                    .addComponent(jLabel94)
                    .addComponent(jLabel97)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbremarks)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tbaddr1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                            .addComponent(tbname, javax.swing.GroupLayout.Alignment.LEADING)))
                                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel89)
                                    .addComponent(jLabel96)
                                    .addComponent(jLabel88)
                                    .addComponent(jLabel86)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel1)))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel39)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbstopmiles, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbcontact, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                .addComponent(tbemail)
                                .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbstoprate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel82))
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel96)))
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addComponent(jLabel91))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel90))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel93))
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel88))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel89))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel86))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel94)
                            .addComponent(tbstoprate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(tbstopmiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39)))
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Location Item/Order Info"));

        jLabel95.setText("Weight");
        jLabel95.setName("lblref"); // NOI18N

        jLabel8.setText("Desc");

        jLabel83.setText("Item");
        jLabel83.setName("lblref"); // NOI18N

        jLabel19.setText("Pallets");

        jLabel20.setText("Quantity");

        jLabel84.setText("Order Number");
        jLabel84.setName("lblref"); // NOI18N

        itemdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(itemdet);

        btadditem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdeleteitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbstopitem, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                            .addComponent(tbstopitemdesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel95)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbstopweight, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbstoppallets, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(tbstopordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(127, 127, 127))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(tbstopqty, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btdeleteitem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadditem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstopitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83)
                    .addComponent(tbstopordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84)
                    .addComponent(tbstoppallets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btadditem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeleteitem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbstopitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(tbstopweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel95)
                            .addComponent(tbstopqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        ddstopsequence.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ddstopsequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddstopsequenceActionPerformed(evt);
            }
        });

        jLabel22.setText("Stop Sequence");

        btnewstop.setText("New Stop");
        btnewstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewstopActionPerformed(evt);
            }
        });

        btclearstop.setText("Clear");
        btclearstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearstopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btaddstop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdatestop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeletestop)
                .addContainerGap())
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddstopsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnewstop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclearstop)
                .addGap(43, 43, 43)
                .addComponent(lblstop, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblstop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddstopsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)
                        .addComponent(btnewstop)
                        .addComponent(btclearstop)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeletestop)
                    .addComponent(btaddstop)
                    .addComponent(btupdatestop))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLocationLayout = new javax.swing.GroupLayout(jPanelLocation);
        jPanelLocation.setLayout(jPanelLocationLayout);
        jPanelLocationLayout.setHorizontalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLocationLayout.setVerticalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelLocation);

        jPanelCharges.setBorder(javax.swing.BorderFactory.createTitledBorder("Charges and Taxes"));
        jPanelCharges.setName("panelsummary"); // NOI18N

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(sactable);

        percentlabel.setText("Percent/Amount");
        percentlabel.setName("lblpercent"); // NOI18N

        jLabel41.setText("Desc");
        jLabel41.setName("lbldesc"); // NOI18N

        btsacadd.setText("add");
        btsacadd.setName("btadd"); // NOI18N
        btsacadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacaddActionPerformed(evt);
            }
        });

        btsacdelete.setText("delete");
        btsacdelete.setName("btdelete"); // NOI18N
        btsacdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacdeleteActionPerformed(evt);
            }
        });

        ddsacamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "amount", "percent" }));
        ddsacamttype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsacamttypeActionPerformed(evt);
            }
        });

        jLabel42.setText("Summary Type");

        jLabel43.setText("Amount Type");

        javax.swing.GroupLayout jPanelChargesLayout = new javax.swing.GroupLayout(jPanelCharges);
        jPanelCharges.setLayout(jPanelChargesLayout);
        jPanelChargesLayout.setHorizontalGroup(
            jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChargesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChargesLayout.createSequentialGroup()
                        .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(percentlabel)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelChargesLayout.createSequentialGroup()
                                .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(btsacadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btsacdelete))
                            .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelChargesLayout.setVerticalGroup(
            jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChargesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btsacadd)
                    .addComponent(btsacdelete)
                    .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentlabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
        );

        add(jPanelCharges);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     newAction("cfo");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddstopActionPerformed
        
        String datestr = "0000-00-00";
        double weight = 0.00;
        double ladingqty = 0.00;
        double pallets = 0.00;
        
        if (dcdate.getDate() != null) {
            datestr = bsmf.MainFrame.dfdate.format(dcdate.getDate()).toString();
        }
        
        String shipper = "";
        
        String shiptocode = "";
        if ( ddshipto.getSelectedItem() != null) {
            shiptocode = ddshipto.getSelectedItem().toString();
        }
      
        String country = "";
        if ( ddcountry.getSelectedItem() != null) {
            country = ddcountry.getSelectedItem().toString();
        }
       
         Enumeration<TableColumn> en = orddet.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new SomeRenderer()); 
                 }
        
       
       // line = getmaxline();
       // line++;
            
            
        myorddetmodel.addRow(new Object[]{
            currentstopline, 
            ddstoptype.getSelectedItem().toString(), 
            datestr,
            tbname.getText(), 
            tbaddr1.getText(), 
            tbcity.getText(), 
            ddstate.getSelectedItem().toString(),
            tbzip.getText(),
            tbnumber.getText()
         });
        
        
         // itemmap
        ArrayList<String[]> z = new ArrayList<String[]>();
        for (int j = 0; j < itemdet.getRowCount(); j++) {
            
            weight += Double.valueOf(itemdet.getValueAt(j, 6).toString());
            ladingqty += Double.valueOf(itemdet.getValueAt(j, 4).toString());
            pallets += Double.valueOf(itemdet.getValueAt(j, 5).toString());
          
            String[] v = new String[]{
              itemdet.getValueAt(j, 0).toString(),
              itemdet.getValueAt(j, 1).toString(),
              itemdet.getValueAt(j, 2).toString(),
              itemdet.getValueAt(j, 7).toString(),
              itemdet.getValueAt(j, 3).toString(),
              itemdet.getValueAt(j, 4).toString(),
              itemdet.getValueAt(j, 5).toString(),
              itemdet.getValueAt(j, 6).toString(),
              "", // ref
              "" // rmks
            };
            z.add(v);
         }
        itemmap.put(String.valueOf(currentstopline), z);
        
        // kvstop
        String[] stoparray = new String[]{String.valueOf(currentstopline), 
            String.valueOf(currentstopline), 
            ddstoptype.getSelectedItem().toString(), 
            shiptocode,
            tbname.getText(), 
            tbaddr1.getText(), 
            tbaddr2.getText(), 
            "", // line3 
            tbcity.getText(), 
            ddstate.getSelectedItem().toString(),
            tbzip.getText(),
            country,
            tbphone.getText(),
            tbemail.getText(),
            tbcontact.getText(),
            tbmisc.getText(),
            tbremarks.getText(),
            "", // ref
            "", // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            dddatetype.getSelectedItem().toString(),
            datestr,
            ddtimetype1.getSelectedItem().toString(),
            ddtime1.getSelectedItem().toString(),
            ddtimetype2.getSelectedItem().toString(),
            ddtime2.getSelectedItem().toString(),
            ddtimezone.getSelectedItem().toString(),
            tbstoprate.getText(),
            tbstopmiles.getText()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        
       
         

        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false;
        
        summarize();
        clearStopFields();    
        setStopState(false);
        bsmf.MainFrame.show("STOP: " + currentstopline + " has been added");
        
    }//GEN-LAST:event_btaddstopActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText(), ddrevision.getSelectedItem().toString()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeletestopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletestopActionPerformed
        
        int[] rows = orddet.getSelectedRows();
        ArrayList<String[]> newlist = new ArrayList<String[]>();
        for (int i : rows) {
            if (orddet.getValueAt(i, 1).toString().equals("LD")) {
                bsmf.MainFrame.show(getMessageTag(1046));
                return;
            } else {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            itemmap.remove(orddet.getValueAt(i, 0).toString());
            kvstop.remove(orddet.getValueAt(i, 0).toString());
            }
        }
        summarize();
    }//GEN-LAST:event_btdeletestopActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText(), ddrevision.getSelectedItem().toString()});  
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       OVData.printPurchaseOrder(tbkey.getText(), false);
    }//GEN-LAST:event_btprintActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
       /*
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        String[] v = null;
        for (String[] x : tablelist) {
          //  System.out.println("HERE: " + x[0] + "/" + orddet.getValueAt(row, 0).toString() );
            if (x[0].equals(orddet.getValueAt(row, 0).toString())) {
                v = x;
            }
        }
        
        
        tbname.setText(v[5]);
        tbaddr1.setText(v[6]);
        tbaddr2.setText(v[7]);
        tbcity.setText(v[8]);
        ddstate.setSelectedItem(v[9]);
        tbzip.setText(v[10]);
        
        tbcontact.setText(v[11]);
        tbphone.setText(v[12]);
        tbemail.setText(v[13]);
        tbforate.setText(v[14]);
       // tbweight.setText(orddet.getValueAt(row, 15).toString());
        if (v[16].isEmpty() || v[16].equals("0000-00-00")) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[16]));   
        }
        if (v[18].isEmpty() || v[18].equals("0000-00-00") ) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[18]));   
        }
        ddtime.setSelectedItem(v[19]);
        */
    }//GEN-LAST:event_orddetMouseClicked

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        if (carrierPOV) {
        int shipperid = OVData.getNextNbr("shipper"); 
        shpData.ship_mstr sh = shpData.createShipMstrJRT(
                             String.valueOf(shipperid), 
                              ddsite.getSelectedItem().toString(),
                              tbkey.getText(), // bol
                              ddcust.getSelectedItem().toString(),
                              ddcust.getSelectedItem().toString(),
                              tbkey.getText(),
                              tbnumber.getText().replace("'", ""),  // po
                              tbkey.getText().replace("'", ""),  // ref
                              dfdate.format(now), // delivery date
                              dfdate.format(now), // ord date
                              ddrevision.getSelectedItem().toString(),
                              ddsite.getSelectedItem().toString(), // shipvia
                              "F", 
                              "", // tax
                              ddsite.getSelectedItem().toString()); 
        ArrayList<String[]> detail = costToDetail();
        ArrayList<shpData.ship_det> shd = shpData.createShipDetFreight(detail, String.valueOf(shipperid), dfdate.format(bsmf.MainFrame.now), ddsite.getSelectedItem().toString());
        shpData.addShipperTransaction(shd, sh);
       // shpData.updateShipperSAC(String.valueOf(shipperid));
        
        String[] message = confirmShipperTransaction("freight", String.valueOf(shipperid), now);
        updateFreightOrderStatus(tbkey.getText(),"closed");
        bsmf.MainFrame.show("committed freight order to invoice number: " + String.valueOf(shipperid));
        } else {
            for (int j = 0; j < orddet.getRowCount(); j++) {
                if (orddet.getValueAt(j, 1).toString().equals("Load")) {
                    continue;
                }
                if (! orddet.getValueAt(j, 8).toString().isBlank()) {
                String[] message = confirmShipperTransaction("order", orddet.getValueAt(j, 8).toString(), now);
                updateFreightOrderStatus(tbkey.getText(),"closed");
                bsmf.MainFrame.show("committed shipper: " + orddet.getValueAt(j, 8).toString());
                }
            } 
        }
        
        
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbforateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbforateFocusLost
        if (! tbforate.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbforate.getText(), "2");
        if (x.equals("error")) {
            tbforate.setText("");
            tbforate.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbforate.requestFocus();
        } else {
            tbforate.setText(x);
            tbforate.setBackground(Color.white);
        }
        } 
        summarize();
    }//GEN-LAST:event_tbforateFocusLost

    private void tbdriverrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdriverrateFocusLost
        if (! tbdriverrate.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbdriverrate.getText(), "2");
        if (x.equals("error")) {
            tbdriverrate.setText("");
            tbdriverrate.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbdriverrate.requestFocus();
        } else {
            tbdriverrate.setText(x);
            tbdriverrate.setBackground(Color.white);
        }
        summarize();
        }
    }//GEN-LAST:event_tbdriverrateFocusLost

    private void tbmileageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbmileageFocusLost
         if (! tbmileage.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbmileage.getText(), "0");
        if (x.equals("error")) {
            tbmileage.setText("");
            tbmileage.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbmileage.requestFocus();
        } else {
            tbmileage.setText(x);
            tbmileage.setBackground(Color.white);
        }
        summarize();
        }
    }//GEN-LAST:event_tbmileageFocusLost

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void ddequiptypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddequiptypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddequiptypeActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        int i = itemdetmodel.getRowCount();
        i++;
        itemdetmodel.addRow(new Object[]{
                    String.valueOf(currentstopline), 
                    String.valueOf(i), 
                    tbstopitem.getText(),
                    tbstopordernbr.getText(), // ordernumber
                    tbstopqty.getText(), 
                    tbstoppallets.getText(), 
                    tbstopweight.getText(),
                    tbstopitemdesc.getText() 
                    });
                    
    }//GEN-LAST:event_btadditemActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
      int[] rows = itemdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) itemdet.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btnewstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewstopActionPerformed
        currentstopline = orddet.getRowCount() + 1;
        clearStopFields();
        setStopState(true);
        if (currentstopline > 1) {
            ddstoptype.setSelectedItem("Unload Complete");
        }
        lblstop.setText("Stop: " + currentstopline);
        btupdatestop.setEnabled(false);
        btdeletestop.setEnabled(false);
        btaddstop.setEnabled(true);
    }//GEN-LAST:event_btnewstopActionPerformed

    private void btclearstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearstopActionPerformed
        clearStopFields();
    }//GEN-LAST:event_btclearstopActionPerformed

    private void ddstopsequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddstopsequenceActionPerformed
        if (! isLoad && ddstopsequence.getItemCount() > 0 && ddstopsequence.getSelectedItem() != null && ! ddstopsequence.getSelectedItem().toString().isBlank()) {
           int stopnumber = Integer.valueOf(ddstopsequence.getSelectedItem().toString().substring(6));
           //for (int j = 0; j < orddet.getRowCount(); j++) {
            String[] v = kvstop.get(String.valueOf(stopnumber));
                
                ddstoptype.setSelectedItem(v[2]); 
                ddshipto.setSelectedItem(v[3]); 
                tbname.setText(v[4]); 
                tbaddr1.setText(v[5]); 
                tbaddr2.setText(v[6]);  
                tbcity.setText(v[8]);  
                ddstate.setSelectedItem(v[9]);
                tbzip.setText(v[10]); 
                ddcountry.setSelectedItem(v[11]);
                tbphone.setText(v[12]);
                tbemail.setText(v[13]);
                tbcontact.setText(v[14]);
                tbmisc.setText(v[15]); 
                tbremarks.setText(v[16]);
                dddatetype.setSelectedItem(v[23]);
                dcdate.setDate(BlueSeerUtils.parseDate(v[24]));
                ddtimezone.setSelectedItem(v[29]);
                ddtimetype1.setSelectedItem(v[25]);
                ddtime1.setSelectedItem(v[26]);
                ddtimetype2.setSelectedItem(v[27]);
                ddtime2.setSelectedItem(v[28]);
          setStopState(true); 
          
          lblstop.setText("Stop: " + stopnumber);
          
          // lets get items of this stop
          itemdetmodel.setRowCount(0);
          if (itemmap.containsKey(String.valueOf(stopnumber))) {
           ArrayList<String[]> itemlist = itemmap.get(String.valueOf(stopnumber));
                for (String[] s : itemlist) {
                    itemdetmodel.addRow(new Object[]{
                    s[0], // stopline
                    s[1], // itemline
                    s[2], // item
                    s[4], // order
                    s[5], // qty
                    s[6], // pallets
                    s[7], // weight
                    s[3] // desc
                    });
                }
          }
          
        btupdatestop.setEnabled(true);
        btdeletestop.setEnabled(true);
        btaddstop.setEnabled(false);
            
        }
        if (! isLoad && ddstopsequence.getItemCount() > 0 && ddstopsequence.getSelectedItem() != null && ddstopsequence.getSelectedItem().toString().isBlank()) {
          clearStopFields();
          setStopState(false);
        }
        
        
    }//GEN-LAST:event_ddstopsequenceActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
       if (! isLoad && jTabbedPane1.getSelectedIndex() == 1) {
            if (orddet.getRowCount() == 0) {
                setStopState(false);
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
          if (ddcust.getItemCount() > 0) {
           clientChangeEvent(ddcust.getSelectedItem().toString());
        } // if ddcust has a list
    }//GEN-LAST:event_ddcustActionPerformed

    private void dddriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dddriverActionPerformed
        if (isLoad == false && dddriver.getSelectedItem() != null && ! dddriver.getSelectedItem().toString().isBlank()) {
          tbdrivercell.setText(getDriverPhone(dddriver.getSelectedItem().toString()));
        }
    }//GEN-LAST:event_dddriverActionPerformed

    private void ddbrokerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbrokerActionPerformed
        if (isLoad == false && ddbroker.getSelectedItem() != null && ! ddbroker.getSelectedItem().toString().isBlank()) {
            String[] x = getBrokerInfo(ddbroker.getSelectedItem().toString());
            tbbrokercell.setText(x[2]);
            tbbrokercontact.setText(x[3]);
        }
    }//GEN-LAST:event_ddbrokerActionPerformed

    private void ddshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshiptoActionPerformed
         if (ddshipto.getItemCount() > 0)  {
            clearShipAddress();
            
            
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

                            res = st.executeQuery("select * from cms_det where cms_code = " + "'" + ddcust.getSelectedItem().toString() + "'" +
                                    " AND cms_shipto = " + "'" + ddshipto.getSelectedItem().toString() + "'" + ";");
                            while (res.next()) {
                                tbname.setText(res.getString("cms_name"));
                                tbaddr1.setText(res.getString("cms_line1"));
                                tbaddr2.setText(res.getString("cms_line2"));
                                tbcity.setText(res.getString("cms_city"));
                                tbzip.setText(res.getString("cms_zip"));
                                tbcontact.setText(res.getString("cms_contact"));
                                tbphone.setText(res.getString("cms_phone"));
                                tbemail.setText(res.getString("cms_email"));
                                tbmisc.setText(res.getString("cms_misc"));
                                ddstate.setSelectedItem(res.getString("cms_state"));
                            }

                        } catch (SQLException s) {
                            MainFrame.bslog(s);
                        } finally {
                            if (res != null) {
                                res.close();
                            }
                            if (st != null) {
                                st.close();
                            }
                            con.close();
                        }
                    } catch (Exception e) {
                        MainFrame.bslog(e);
                    }
              
        }
    }//GEN-LAST:event_ddshiptoActionPerformed

    private void btupdatestopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatestopActionPerformed
        
        if (ddstopsequence.getSelectedItem().toString().isBlank()) {
            return;
        }
        
        String shiptocode = "";
        String datestr = "0000-00-00";
        double weight = 0.00;
        double ladingqty = 0.00;
        double pallets = 0.00;
        
        if (dcdate.getDate() != null) {
            datestr = bsmf.MainFrame.dfdate.format(dcdate.getDate()).toString();
        }
        
        if ( ddshipto.getSelectedItem() != null) {
            shiptocode = ddshipto.getSelectedItem().toString();
        }
      
        String country = "";
        if ( ddcountry.getSelectedItem() != null) {
            country = ddcountry.getSelectedItem().toString();
        }
        
        int stopnumber = Integer.valueOf(ddstopsequence.getSelectedItem().toString().substring(6));
        
         // now update itemmap
        // now item detail...removing all records for this current stop record
        itemmap.remove(String.valueOf(currentstopline));
        ArrayList<String[]> z = new ArrayList<String[]>();
        for (int j = 0; j < itemdet.getRowCount(); j++) {
            weight += Double.valueOf(itemdet.getValueAt(j, 6).toString());
            ladingqty += Double.valueOf(itemdet.getValueAt(j, 4).toString());
            pallets += Double.valueOf(itemdet.getValueAt(j, 5).toString());
            String[] v = new String[]{
              itemdet.getValueAt(j, 0).toString(),
              itemdet.getValueAt(j, 1).toString(),
              itemdet.getValueAt(j, 2).toString(),
              itemdet.getValueAt(j, 7).toString(),
              itemdet.getValueAt(j, 3).toString(),
              itemdet.getValueAt(j, 4).toString(),
              itemdet.getValueAt(j, 5).toString(),
              itemdet.getValueAt(j, 6).toString(),
              "",
              ""
            };
            z.add(v);
         }
        if (itemmap.containsKey(String.valueOf(stopnumber))) {
           itemmap.replace(String.valueOf(stopnumber), z);
        } else {
           itemmap.put(String.valueOf(stopnumber), z); 
        }
        
            String[] stoparray = new String[]{String.valueOf(stopnumber), 
            String.valueOf(String.valueOf(stopnumber)), 
            ddstoptype.getSelectedItem().toString(), 
            shiptocode,
            tbname.getText(), 
            tbaddr1.getText(), 
            tbaddr2.getText(), 
            "", // line3 
            tbcity.getText(), 
            ddstate.getSelectedItem().toString(),
            tbzip.getText(),
            country,
            tbphone.getText(),
            tbemail.getText(),
            tbcontact.getText(),
            tbmisc.getText(),
            tbremarks.getText(),
            "", // ref
            "", // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            dddatetype.getSelectedItem().toString(),
            datestr,
            ddtimetype1.getSelectedItem().toString(),
            ddtime1.getSelectedItem().toString(),
            ddtimetype2.getSelectedItem().toString(),
            ddtime2.getSelectedItem().toString(),
            ddtimezone.getSelectedItem().toString(),
            tbstoprate.getText(),
            tbstopmiles.getText()
         };
        kvstop.replace(String.valueOf(stopnumber), stoparray);
        
        // now update orddet table
        for (int j = 0; j < orddet.getRowCount(); j++) {
            if (orddet.getValueAt(j, 0).toString().equals(String.valueOf(stopnumber))) {
                orddet.setValueAt(ddstoptype.getSelectedItem().toString(), j, 1);
                orddet.setValueAt(datestr, j, 2);
                orddet.setValueAt(tbname.getText(), j, 3);
                orddet.setValueAt(tbaddr1.getText(), j, 4);
                orddet.setValueAt(tbcity.getText(), j, 5);
                orddet.setValueAt(ddstate.getSelectedItem().toString(), j, 6);
                orddet.setValueAt(tbzip.getText(), j, 7);
            }
         }
        
       summarize();
       clearStopFields();
        
        
    }//GEN-LAST:event_btupdatestopActionPerformed

    private void cbderivedrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbderivedrateActionPerformed
        if (cbderivedrate.isSelected()) {
            tbforate.setEnabled(false);
        } else {
            tbforate.setEnabled(true);
        }
    }//GEN-LAST:event_cbderivedrateActionPerformed

    private void cbderivedmilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbderivedmilesActionPerformed
        if (cbderivedmiles.isSelected()) {
            tbmileage.setEnabled(false);
        } else {
            tbmileage.setEnabled(true);
        }
    }//GEN-LAST:event_cbderivedmilesActionPerformed

    private void btsacaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacaddActionPerformed
        boolean proceed = true;
        double amount = 0;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbsacamt.getText());
        if (!m.find() || tbsacamt.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1033));
            proceed = false;
            tbsacamt.requestFocus();
            return;
        }

        if (tbsacdesc.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            proceed = false;
            tbsacdesc.requestFocus();
            return;
        }

        

        if (ddsactype.getSelectedItem().toString().equals("discount") &&
            ddsacamttype.getSelectedItem().toString().equals("amount")) {
            amount = -1 * bsParseDouble(tbsacamt.getText());
        } else {
            amount = bsParseDouble(tbsacamt.getText());
        }

        if (proceed) {
        sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), tbsacdesc.getText(), ddsacamttype.getSelectedItem().toString(), String.valueOf(amount)});
        }
        summarize();
        
    }//GEN-LAST:event_btsacaddActionPerformed

    private void btsacdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacdeleteActionPerformed
        int[] rows = sactable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(i);
        }
        summarize(); 

    }//GEN-LAST:event_btsacdeleteActionPerformed

    private void ddsacamttypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsacamttypeActionPerformed
        if (ddsacamttype.getSelectedItem().toString().equals("percent")) {
            percentlabel.setText("percent");
        } else {
            percentlabel.setText("amount");
        }
    }//GEN-LAST:event_ddsacamttypeActionPerformed

    private void ddorderstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddorderstatusActionPerformed
        if (! isLoad) {
            if (cbedi.isSelected() && hasEDIXref(ddcust.getSelectedItem().toString(),"GF")) {
                if (ddorderstatus.getSelectedItem().toString().equals("open") ||
                    ddorderstatus.getSelectedItem().toString().equals("declined") ||
                    ddorderstatus.getSelectedItem().toString().equals("declined")) {
                    boolean proceed = bsmf.MainFrame.warn(getMessageTag(1183));
                    if (proceed) {
                       Create990(tbkey.getText());
                    }
                }
            }
        }
    }//GEN-LAST:event_ddorderstatusActionPerformed

    private void btaddshipperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddshipperActionPerformed
        String[] sh = getShipperHeader(tbnumber.getText());
        String[] wh = OVData.getWareHouseAddressElements(sh[14]);
        
        if (tbnumber.getText().isBlank() || ! isValidShipper(tbnumber.getText())) {
            bsmf.MainFrame.show("invalid shipper");
            return;
        }
        
        
        String datestr = "0000-00-00";
        double weight = 0.00;
        double ladingqty = 0.00;
        double pallets = 0.00;
        
        
       
        Enumeration<TableColumn> en = orddet.getColumnModel().getColumns();
         while (en.hasMoreElements()) {
             TableColumn tc = en.nextElement();
             tc.setCellRenderer(new SomeRenderer()); 
         }
        
         // do Load first...from info in sh_shipfrom
        currentstopline = orddet.getRowCount() + 1;
        
        myorddetmodel.addRow(new Object[]{
            currentstopline, 
            "Load", 
            datestr,
            wh[1], 
            wh[2], 
            wh[5], 
            wh[6],
            wh[7],
            tbnumber.getText()
         });
        
        String[] stoparray = new String[]{String.valueOf(currentstopline), 
            String.valueOf(currentstopline), 
            "Load", 
            wh[0],
            wh[1], 
            wh[2],  
            wh[3], 
            wh[4], 
            wh[5], 
            wh[6],
            wh[7],
            wh[8],
            "", // phone
            "", // email
            "", // contact
            "", // misc
            "", // remarks
            "", // ref
            "", // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            dddatetype.getSelectedItem().toString(),
            datestr,
            ddtimetype1.getSelectedItem().toString(),
            ddtime1.getSelectedItem().toString(),
            ddtimetype2.getSelectedItem().toString(),
            ddtime2.getSelectedItem().toString(),
            ddtimezone.getSelectedItem().toString(),
            tbstoprate.getText(),
            tbstopmiles.getText()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false; 
         
        // Now shipto of shipper...
        String[] st = getShipAddressInfo(sh[0], sh[1]);
        currentstopline = orddet.getRowCount() + 1;
        
        myorddetmodel.addRow(new Object[]{
            currentstopline, 
            "Unload Complete", 
            datestr,
            st[1], 
            st[2], 
            st[5], 
            st[6],
            st[7],
            tbnumber.getText()
         });
        
            stoparray = new String[]{String.valueOf(currentstopline), 
            String.valueOf(currentstopline), 
            "Unload Complete", 
            st[0],
            st[1], 
            st[2],  
            st[3], 
            st[4], 
            st[5], 
            st[6],
            st[7],
            st[8],
            "", // phone
            "", // email
            "", // contact
            "", // misc
            sh[6], // remarks
            sh[7], // ref
            sh[2], // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            dddatetype.getSelectedItem().toString(),
            datestr,
            ddtimetype1.getSelectedItem().toString(),
            ddtime1.getSelectedItem().toString(),
            ddtimetype2.getSelectedItem().toString(),
            ddtime2.getSelectedItem().toString(),
            ddtimezone.getSelectedItem().toString(),
            tbstoprate.getText(),
            tbstopmiles.getText()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        
        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false;
        
        summarize();
        
        
    }//GEN-LAST:event_btaddshipperActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btaddshipper;
    private javax.swing.JButton btaddstop;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearstop;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btdeletestop;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnewstop;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btsacadd;
    private javax.swing.JButton btsacdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdatestop;
    private javax.swing.JCheckBox cbderivedmiles;
    private javax.swing.JCheckBox cbderivedrate;
    private javax.swing.JCheckBox cbderivedweight;
    private javax.swing.JCheckBox cbedi;
    private javax.swing.JCheckBox cbhazmat;
    private javax.swing.JCheckBox cbrev;
    private javax.swing.JCheckBox cbstandard;
    private com.toedter.calendar.JDateChooser dcconfdate;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcorddate;
    private javax.swing.JComboBox<String> ddbroker;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcust;
    private javax.swing.JComboBox<String> dddatetype;
    private javax.swing.JComboBox<String> dddriver;
    private javax.swing.JComboBox ddequiptype;
    private javax.swing.JComboBox<String> ddfotype;
    private javax.swing.JComboBox ddorderstatus;
    private javax.swing.JComboBox<String> ddratetype;
    private javax.swing.JComboBox<String> ddrevision;
    private javax.swing.JComboBox<String> ddsacamttype;
    private javax.swing.JComboBox<String> ddsactype;
    private javax.swing.JComboBox<String> ddservicetype;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox<String> ddstopsequence;
    private javax.swing.JComboBox<String> ddstoptype;
    private javax.swing.JComboBox<String> ddtime1;
    private javax.swing.JComboBox<String> ddtime2;
    private javax.swing.JComboBox<String> ddtimetype1;
    private javax.swing.JComboBox<String> ddtimetype2;
    private javax.swing.JComboBox<String> ddtimezone;
    private javax.swing.JComboBox ddvehicle;
    private javax.swing.JTable itemdet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCharges;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lbclientname;
    private javax.swing.JLabel lblclient;
    private javax.swing.JLabel lblnumber;
    private javax.swing.JLabel lblrevision;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstop;
    private javax.swing.JTable orddet;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTable sactable;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbbrokercell;
    private javax.swing.JTextField tbbrokercontact;
    private javax.swing.JTextField tbcharges;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbdrivercell;
    private javax.swing.JTextField tbdriverrate;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbforate;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmileage;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbnumber;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbsacamt;
    private javax.swing.JTextField tbsacdesc;
    private javax.swing.JTextField tbstopitem;
    private javax.swing.JTextField tbstopitemdesc;
    private javax.swing.JTextField tbstopmiles;
    private javax.swing.JTextField tbstopordernbr;
    private javax.swing.JTextField tbstoppallets;
    private javax.swing.JTextField tbstopqty;
    private javax.swing.JTextField tbstoprate;
    private javax.swing.JTextField tbstopweight;
    private javax.swing.JTextField tbtotweight;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
