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
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.getShipAddressInfo;
import static com.blueseer.edi.EDI.Create990;
import com.blueseer.fgl.fglData;
import static com.blueseer.frt.frtData.addCFOTransaction;
import com.blueseer.frt.frtData.brk_mstr;
import com.blueseer.frt.frtData.cfo_det;
import com.blueseer.frt.frtData.cfo_item;
import com.blueseer.frt.frtData.cfo_mstr;
import com.blueseer.frt.frtData.cfo_sos;
import static com.blueseer.frt.frtData.deleteCFOMstr;
import com.blueseer.frt.frtData.frt_ctrl;
import static com.blueseer.frt.frtData.getBrokerMstr;
import static com.blueseer.frt.frtData.getCFOCtrl;
import static com.blueseer.frt.frtData.getCFODet;
import static com.blueseer.frt.frtData.getCFOItem;
import static com.blueseer.frt.frtData.getCFOLines;
import static com.blueseer.frt.frtData.getCFOMstr;
import static com.blueseer.frt.frtData.getCFORevisions;
import static com.blueseer.frt.frtData.getCFOSOS;
import static com.blueseer.frt.frtData.getFreightCodeByCodeKey;
import static com.blueseer.frt.frtData.updateCFOTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.getShipperHeader;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.clog;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleUS;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.isParsableToInt;
import static com.blueseer.utl.BlueSeerUtils.logChange;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.DTData;
import static com.blueseer.utl.EDData.hasEDIXref;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.isValidShipper;
import static com.blueseer.utl.OVData.updateFreightOrderStatus;
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
import java.io.IOException;
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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
import net.sf.jasperreports.engine.JRException;


/**
 *
 * @author vaughnte
 */
public class CFOMaint extends javax.swing.JPanel implements IBlueSeerT {
    
    // global variable declarations
    public boolean receipt990 = false;
    public String rejectioncode = "";
    public String rejectionreason = "";
    public boolean lock_ddshipper = false;
    public int currentstopline = 0;
    boolean isCFOCommitted = false;
    boolean isLoad = false;
    public boolean carrierPOV = true;
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
                getGlobalColumnTag("stopline"), 
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
                getGlobalColumnTag("code"), 
                getGlobalColumnTag("description"),
                getGlobalColumnTag("value"), 
                getGlobalColumnTag("amount")
            });
   
   javax.swing.table.DefaultTableModel attachmentmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                getGlobalColumnTag("file")})
            {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class; 
                else return String.class;  //other columns accept String values  
              }  
            };
   
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
                case "run":
                    message = Run_autoInvoice();
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
           } else if (this.type.equals("run")) {
             initvars(null);    
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
      
              
    
       // clear global fields
    receipt990 = false;
    rejectioncode = "";
    rejectionreason = "";
    lock_ddshipper = false;
    currentstopline = 0;
    carrierPOV = true;
    x = null;
    fc = null;  
    cfodetlist = null;
    soslist = null;
    cfoitemlist = null;
    kvstop.clear();
    itemmap.clear();
    stk.clear();
    
    isCFOCommitted = false;
       
       fc = getCFOCtrl(new String[]{""});
       // note:  fc.frtc_function() = 1 for Trucking POV...and 0 for Customer POV
       carrierPOV = (fc.frtc_function().equals("1"));
       
       
       ArrayList<String[]> initDataSets = frtData.getCFOMaintInit(fc.frtc_function());
       
       
       
       if (! carrierPOV) {
           lblclient.setText(getGlobalColumnTag("carrier"));
           lblnumber.setText(getGlobalColumnTag("shipper"));
           btaddshipper.setVisible(true);
       } else {
          // lblclient.setText(getGlobalColumnTag("customer"));
          // lblnumber.setText(getGlobalColumnTag("custorder"));
           btaddshipper.setVisible(false);
       }
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", jPanelMain);
       jTabbedPane1.add("Stops", jPanelLocation);
       jTabbedPane1.add("Charges", jPanelCharges);
       jTabbedPane1.add("Attachments", panelAttachment);
       
       attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
       
       lblstatus.setText("");
       lblstatus.setForeground(Color.black);
       
       tbkey.setText("");
       cbhazmat.setSelected(false);
       cbrev.setSelected(false);
       cbedi.setSelected(false);
       cbedi.setEnabled(false);
       cbstandard.setSelected(false);
       
       ddorderstatus.setBackground(null);
       ddorderstatus.setSelectedItem("pending");
       
       ddstopsequence.removeAllItems();
       ddstopsequence.addItem("");
       
       dcdate.setDate(new java.util.Date());
       
        tbkey.setText("");
        tbnumber.setText("");
        cbhazmat.setSelected(false);
        tbvehicle.setText("");
        tbvehicle.setEditable(false);
        tbtrailer.setText("");
        ddorderstatus.setSelectedIndex(0);
        tbDriver.setText("");
        tbDriver.setEditable(false);
        tbdrivercell.setText("");
        tbdrivername.setText("");
        ddfotype.setSelectedIndex(0);
        ddbroker.setSelectedIndex(0);
        tbbrokercontact.setText("");
        tbbrokercell.setText("");
        ddratetype.setSelectedIndex(0);
        tbforate.setText("0");
        tbmileage.setText("0");
        tbdriverrate.setText("0");
        cbstandard.setSelected(false);
        tbtotweight.setText("0");
        dcorddate.setDate(bsmf.MainFrame.now);
        dcconfdate.setDate(bsmf.MainFrame.now);
        tbcharges.setText("0");
        tbcharges.setEditable(false);
        tbcost.setText("0");
        tbcost.setEditable(false);
        tbtime1.setText("");
        tbtime2.setText("");
        tbsacamt.setText("");
        tbsacdesc.setText("");
        tbequiptype.setEditable(false);
        tbequiptype.setText("");
        lblequiptype.setText("");
        lbclientname.setText("");
        tanotes.setText("");
        
        
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
        ddshipto.removeAllItems();
        ddservicetype.removeAllItems();
        ddbroker.removeAllItems();
        ddtimezone.removeAllItems();
        ddchargecode.removeAllItems();
        dddatetype.removeAllItems();
        ddtimetype1.removeAllItems();
        ddtimetype2.removeAllItems();
        
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
            if (s[0].equals("broker")) {
              ddbroker.addItem(s[1]); 
            }
            if (s[0].equals("servicetypes")) {
              ddservicetype.addItem(s[1]); 
            }
            if (s[0].equals("chargecodes")) {
              ddchargecode.addItem(s[1]); 
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("timezones")) {
              ddtimezone.addItem(s[1]); 
            }
            if (s[0].equals("freightdatecodes")) {
              dddatetype.addItem(s[1]); 
            }
            if (s[0].equals("freighttimecodes")) {
              ddtimetype1.addItem(s[1]); 
            }
            if (s[0].equals("freighttimecodes")) {
              ddtimetype2.addItem(s[1]); 
            }
            
        }
        
        ddsite.setSelectedItem(defaultsite);
        ddstate.insertItemAt("", 0);
        ddstate.setSelectedIndex(0);
        
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        
       
        ddbroker.insertItemAt("", 0);
        ddbroker.setSelectedIndex(0);
        ddservicetype.insertItemAt("", 0);
        ddservicetype.setSelectedIndex(0);
        ddchargecode.insertItemAt("", 0);
        ddchargecode.setSelectedIndex(0);
        dddatetype.insertItemAt("", 0);
        dddatetype.setSelectedIndex(0);
        ddtimetype1.insertItemAt("", 0);
        ddtimetype1.setSelectedIndex(0);
        ddtimetype2.insertItemAt("", 0);
        ddtimetype2.setSelectedIndex(0);
       
        ddsactype.removeAllItems();
        ddsactype.addItem("charge");
        ddsactype.addItem("discount");
        ddsactype.addItem("passive");
        ddsactype.setSelectedIndex(0);
        
        // add schedule types to stk (schedule type key) LinkedHashMap
        stk.put("", pass);
        
        /*
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
        */
                
        DateFormat getTimeZoneShort = new SimpleDateFormat("z", Locale.US);
        String timeZoneShort = getTimeZoneShort.format(Calendar.getInstance().getTime());
        ddtimezone.setSelectedItem(timeZoneShort);
        
      
      clearStopFields();  // leave at bottom...so as not to reset the isLoad bool to false too soon. 
       
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
        cbrev.setSelected(true);
        cbstandard.setSelected(true);
        
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
       
               
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"cfo_mstr"});
        int fc;

        fc = checkLength(f,"cfo_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            tbkey.setBackground(Color.yellow);
            return false;
        } else {
            tbkey.setBackground(null);
        }
        
        fc = checkLength(f,"cfo_cust");
        if (ddcust.getSelectedItem().toString().length() > fc || ddcust.getSelectedItem().toString().isBlank()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            ddcust.requestFocus();
            ddcust.setBackground(Color.yellow);
            return false;
        } else {
            ddcust.setBackground(null);
        }
        
        fc = checkLength(f,"cfo_custfonbr");
        if (tbnumber.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbnumber.requestFocus();
            tbnumber.setBackground(Color.yellow);
            return false;
        } else {
            tbnumber.setBackground(null);
        }
       
         
        fc = checkLength(f,"cfo_rmks");
        if (tbremarks.getText().length() > fc ) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            tbremarks.setBackground(Color.yellow);
            return false;
        } else {
            tbremarks.setBackground(null);
        } 
        
        if (orddet.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1187));
            return false;
        }
               
        return true;
    }
    
    public boolean validateStopInput(dbaction x) {
       
               
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"cfo_det"});
        int fc;

        fc = checkLength(f,"cfod_name");
        if (tbname.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbname.requestFocus();
            tbname.setBackground(Color.yellow);
            return false;
        } else {
            tbname.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_line1");
        if (tbaddr1.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbaddr1.requestFocus();
            tbaddr1.setBackground(Color.yellow);
            return false;
        } else {
            tbaddr1.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_line2");
        if (tbaddr2.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbaddr2.requestFocus();
            tbaddr2.setBackground(Color.yellow);
            return false;
        } else {
            tbaddr2.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_city");
        if (tbcity.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbcity.requestFocus();
            tbcity.setBackground(Color.yellow);
            return false;
        } else {
            tbcity.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_zip");
        if (tbzip.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbzip.requestFocus();
            tbzip.setBackground(Color.yellow);
            return false;
        } else {
            tbzip.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_email");
        if (tbemail.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbemail.requestFocus();
            tbemail.setBackground(Color.yellow);
            return false;
        } else {
            tbemail.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_contact");
        if (tbcontact.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbcontact.requestFocus();
            tbcontact.setBackground(Color.yellow);
            return false;
        } else {
            tbcontact.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_phone");
        if (tbphone.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbphone.requestFocus();
            tbphone.setBackground(Color.yellow);
            return false;
        } else {
            tbphone.setBackground(null);
        }
        
        fc = checkLength(f,"cfod_rmks");
        if (tbremarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            tbremarks.setBackground(Color.yellow);
            return false;
        } else {
            tbremarks.setBackground(null);
        }
        
        
        
               
        return true;
    }
    
    
    public void initvars(String[] arg) {
       isLoad = true;
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
       isLoad = false;
       
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
           
        ArrayList<String> badlines = getBadLines(tbkey.getText());
     
       m = updateCFOTransaction(tbkey.getText(), ddrevision.getSelectedItem().toString(), badlines, createDetRecord(), createRecord(), createItemRecord(), createSOSRecord());
     
        if (m[0].equals("0") && receipt990) {
               /*
               if (ddorderstatus.getSelectedItem().equals("declined")) {
                if (! rejectioncode.isBlank()) {
                    updateCFORejection(tbkey.getText(),rejectioncode, rejectionreason);
                }
               }
               */
               if (bsmf.MainFrame.remoteDB) {
                ArrayList<String[]> arrx = new ArrayList<String[]>();
                    arrx.add(new String[]{"id","send990"});
                    arrx.add(new String[]{"key", tbkey.getText()});
                   try {   
                       sendServerPost(arrx, "", null, "dataServ");
                   } catch (IOException ex) {
                       bslog(ex);
                   }
               } else {
                Create990(tbkey.getText());
               }
               receipt990 = false;
        }
       
        
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
        boolean proceed = bsmf.MainFrame.warn("this will delete all revisions of this freight order...are you sure you want to delete?");
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
       cfodetlist = getCFODet(key[0], x.cfo_revision()); 
       cfoitemlist = getCFOItem(key[0], x.cfo_revision()); 
       soslist = getCFOSOS(key[0], x.cfo_revision());
       getAttachments(key[0]);
       
       if (x != null) {
          isCFOCommitted = true;
      }
       
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
                tbequiptype.getText(),
                tbvehicle.getText(),
                tbtrailer.getText(),
                ddorderstatus.getSelectedItem().toString(),
                "", // delivery status
                tbDriver.getText(),
                tbdrivercell.getText(),
                ddfotype.getSelectedItem().toString(),
                ddbroker.getSelectedItem().toString(),
                tbbrokercontact.getText(),
                tbbrokercell.getText(),
                ddratetype.getSelectedItem().toString(),
                tbforate.getText().isBlank() ? "0" : tbforate.getText(),
                tbmileage.getText().isBlank() ? "0" : tbmileage.getText(),
                tbdriverrate.getText().isBlank() ? "0" : tbdriverrate.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbstandard.isSelected())),
                tbtotweight.getText().isBlank() ? "0" : tbtotweight.getText(),
                BlueSeerUtils.setDateFormatNull(dcorddate.getDate()),
                BlueSeerUtils.setDateFormatNull(dcconfdate.getDate()),
                String.valueOf(BlueSeerUtils.boolToInt(cbhazmat.isSelected())),
                "0", // expenses
                tbcharges.getText().isBlank() ? "0" : tbcharges.getText(),
                tbcost.getText().isBlank() ? "0" : tbcost.getText(),
                "", // bol
                tanotes.getText(),
                derived,
                logic,
                ddsite.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbedi.isSelected())),
                "", // edi rejection reason..to be added
                String.valueOf(BlueSeerUtils.boolToInt(cbrev.isSelected())),
                rejectioncode, // rejectcode
                rejectionreason, // rejection
                "" // uniquekey
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
                v[23], // datecode
                v[24], // datetype
                v[25],  // date
                v[26], // datecode2
                v[27], // datetype2
                v[28], // date2
                v[29], //timecode1
                v[30], // timetype1
                v[31], // time1
                v[32], //timezone1
                v[33], // timecode2
                v[34], // timetype2
                v[35], // time2
                v[36],  // timezone2
                v[37].replace(defaultDecimalSeparator, '.'),  // rate
                v[38].replace(defaultDecimalSeparator, '.'),  // miles
                v[39].replace(defaultDecimalSeparator, '.')  // volume
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
                (s[5].isBlank()) ? "0" : s[5], // qty
                (s[6].isBlank()) ? "0" : s[6], // pallets
                (s[7].isBlank()) ? "0" : s[7], // weight
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
                sactable.getValueAt(j, 2).toString(),
                sactable.getValueAt(j, 0).toString(),
                sactable.getValueAt(j, 3).toString(),
                sactable.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'),
                sactable.getValueAt(j, 1).toString(), // key
                sactable.getValueAt(j, 2).toString()); // value    
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

    public void lookUpFrameDriver() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getDriverBrowseUtil(luinput.getText(),0, "drv_id"); 
        } else {
         luModel = DTData.getDriverBrowseUtil(luinput.getText(),0, "drv_lname");   
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
                tbDriver.setText(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lookupid", this.getClass().getSimpleName()), 
                getClassLabelTag("lbllastname", this.getClass().getSimpleName()), 2);  
        
        
    }

    public void lookUpFrameVehicle() {
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVehicleBrowseUtil(luinput.getText(),0, "veh_id"); 
        } else {
         luModel = DTData.getVehicleBrowseUtil(luinput.getText(),0, "veh_desc");   
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
                tbvehicle.setText(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lookupid", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()), 2);  
    }
    
    public void lookUpFrameRejection() {
        luTable.removeMouseListener(luml);
        rejectioncode = "";
        rejectionreason = "";
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0 || column == 1) {
                ludialog.dispose();
                rejectioncode = (target.getValueAt(row,0).toString());
                rejectionreason = (target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
        luModel = DTData.getFreightRejectionCodeDT();
        luTable.setModel(luModel);
        
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
        ludialog.setTitle("Choose Decline Option:");
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
   
     public void lookUpFrameChargeCode() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getFreightCodeBrowseUtilByCode(luinput.getText(),0, "freight_key", "freightchargecodes"); 
        } else {
         luModel = DTData.getFreightCodeBrowseUtilByCode(luinput.getText(),0, "freight_value", "freightchargecodes");    
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
                ddchargecode.setSelectedItem(target.getValueAt(row,1).toString());
                tbsacdesc.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblcode", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()), 2);  
        
        
    }

     public void lookUpFrameEquipType() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getFreightCodeBrowseUtilByCode(luinput.getText(),0, "freight_key", "freighteqptype"); 
        } else {
         luModel = DTData.getFreightCodeBrowseUtilByCode(luinput.getText(),0, "freight_value", "freighteqptype");    
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
                tbequiptype.setText(target.getValueAt(row,1).toString());
                lblequiptype.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lookupid", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()), 2);  
        
        
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
        lbclientname.setText(cusData.getCustName(x.cfo_cust()));
        
        tbnumber.setText(x.cfo_custfonbr());
        ddrevision.setSelectedItem(x.cfo_revision());
        cbhazmat.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_ishazmat()));
        cbrev.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_defaultrev()));
        cbedi.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_edi()));
        ddservicetype.setSelectedItem(x.cfo_servicetype());
        tbequiptype.setText(x.cfo_equipmenttype());
        lblequiptype.setText(getFreightCodeByCodeKey("freighteqptype", x.cfo_equipmenttype()));
        tbvehicle.setText(x.cfo_truckid());
        tbtrailer.setText(x.cfo_trailernbr());
        ddorderstatus.setSelectedItem(x.cfo_orderstatus());
        tbDriver.setText(x.cfo_driver());
        tbdrivercell.setText(x.cfo_drivercell());
        ddfotype.setSelectedItem(x.cfo_type());
        ddbroker.setSelectedItem(x.cfo_brokerid());
        tbbrokercontact.setText(x.cfo_brokercontact());
        tbbrokercell.setText(x.cfo_brokercell());
        ddratetype.setSelectedItem(x.cfo_ratetype());
        tbforate.setText(currformat(x.cfo_rate()));
        tbmileage.setText(x.cfo_mileage());
        tbdriverrate.setText(currformat(x.cfo_driverrate()));
        cbstandard.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_driverstd()));
        tbtotweight.setText(x.cfo_weight());
        dcorddate.setDate(BlueSeerUtils.parseDate(x.cfo_orddate()));
        dcconfdate.setDate(BlueSeerUtils.parseDate(x.cfo_confdate()));
        tbcharges.setText(currformat(x.cfo_misccharges()));
        tbcost.setText(currformat(x.cfo_cost()));
        ddsite.setSelectedItem(x.cfo_site());
        tanotes.setText(x.cfo_rmks());
        
        if (ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isBlank()) {
            clientChangeEvent(ddcust.getSelectedItem().toString());
        }
        
        // now detail
        kvstop.clear();
        myorddetmodel.setRowCount(0);
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
                        cfod.cfod_datecode(), 
                        cfod.cfod_datetype(), 
                        cfod.cfod_date(), 
                        cfod.cfod_datecode2(), 
                        cfod.cfod_datetype2(), 
                        cfod.cfod_date2(),
                        cfod.cfod_timecode1(),
                        cfod.cfod_timetype1(),
                        cfod.cfod_time1(), 
                        cfod.cfod_timezone1(),
                        cfod.cfod_timecode2(),
                        cfod.cfod_timetype2(),
                        cfod.cfod_time2(), 
                        cfod.cfod_timezone2(),
                        cfod.cfod_rate(),    // 38
                        cfod.cfod_miles(),   // 39
                        cfod.cfod_volume()};
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
                      sos.cfos_key(), // item
                      sos.cfos_desc(),   // desc
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
    
    public void getAttachments(String id) {
        attachmentmodel.setNumRows(0);
        ArrayList<String> list = OVData.getSysMetaData(id, this.getClass().getSimpleName(), "attachments");
        for (String file : list) {
        attachmentmodel.addRow(new Object[]{BlueSeerUtils.clickflag,  
                               file
            });
        }
    }
    
    
    // misc
    public ArrayList<String> getBadLines(String key) {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getCFOLines(key);
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
       return badlines;
    }
    
    
    public String[] Run_autoInvoice() {
        
        String[] m = autoInvoice();
        // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        }
        return m;
    }
    
    public String[] autoInvoice() {
       java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       String[] m = null; 
        
        if (carrierPOV) {
        int shipperid = 0;
        if (OVData.getSysMetaValue("system", "freightcontrol", "shipperkey").equals("1")) {
            shipperid = Integer.valueOf(tbkey.getText());
        } else {
            shipperid = OVData.getNextNbr("shipper");
        }
         
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
                              ddsite.getSelectedItem().toString(),
                              "" // tracking
                              ); 
        ArrayList<String[]> detail = costToDetail();
        ArrayList<shpData.ship_det> shd = shpData.createShipDetFreight(detail, String.valueOf(shipperid), dfdate.format(bsmf.MainFrame.now), ddsite.getSelectedItem().toString());
        shpData.addShipperTransaction(shd, sh, null);
       // shpData.updateShipperSAC(String.valueOf(shipperid));
        
        m = confirmShipperTransaction("freight", String.valueOf(shipperid), now);
        updateFreightOrderStatus(tbkey.getText(),"closed");
        if (m[0].equals("0")) {
            m[1] = "committed freight order to invoice number: " + String.valueOf(shipperid);
        }
         /*
         bsmf.MainFrame.show("committed freight order to invoice number: " + String.valueOf(shipperid));
         if (m[0].equals("1")) { // if error
           bsmf.MainFrame.show(m[1]);
         } else {
           executeTask(dbaction.get, new String[]{tbkey.getText()});
         }
         */
        } else {
            for (int j = 0; j < orddet.getRowCount(); j++) {
                if (orddet.getValueAt(j, 1).toString().equals("Load")) {
                    continue;
                }
                if (! orddet.getValueAt(j, 8).toString().isBlank()) {
                m = confirmShipperTransaction("order", orddet.getValueAt(j, 8).toString(), now);
                updateFreightOrderStatus(tbkey.getText(),"closed");
                if (m[0].equals("0")) {
                  m[1] = "committed shipper: " + orddet.getValueAt(j, 8).toString();
                }
                  /*
                  bsmf.MainFrame.show("committed shipper: " + orddet.getValueAt(j, 8).toString());
                  if (m[0].equals("1")) { // if error
                    bsmf.MainFrame.show(m[1]);
                  } else {
                    executeTask(dbaction.get, new String[]{tbkey.getText()});
                  }
                  */
                }
            } 
        } 
        return m;
    }
    
    public ArrayList<String[]> costToDetail() {
        
        double totamt = bsParseDouble(tbforate.getText());
        double totamtLessCharge = bsParseDouble(tbcost.getText()) - bsParseDouble(tbcharges.getText());
        
        ArrayList<String[]> list = new ArrayList<String[]>();
        // create line item 1 with bulk rate
             String[] s = new String[]{
             "1", // shline
             "DLH", // item
             tbkey.getText(), // order
             tbnumber.getText(), // cust fo
             currformatDoubleUS(totamtLessCharge),  // formatUSC(tbcost.getText()), // netprice
             "0", // taxamt
             getFreightCodeByCodeKey("freightchargecodes", "DLH"),  // desc
             ddratetype.getSelectedItem().toString() // sku -- holds rate type 
             };
        list.add(s);
        
        // additional line items contain charges/surcharges from cfo_sos table
        ArrayList<String[]> sac = OVData.getFreightSAC(tbkey.getText(), ddrevision.getSelectedItem().toString()); 
        int cnt = 1;
        String myamttype = "";
        double myamt = 0.00;
        if (sac != null && ! sac.isEmpty()) {
            for (String[] ss : sac) {
                myamttype = ss[3].toString();
                myamt = bsParseDouble(ss[4].toString());
                // adjust if percent based
                 if (ss[3].toString().equals("percent") && bsParseDouble(ss[4].toString()) > 0) {
                   myamttype = "amount";
                   if (ss[2].equals("discount")) {
                     myamt = -1 * (bsParseDouble(ss[4].toString()) / 100) * totamt;
                   } else {
                     myamt = (bsParseDouble(ss[4].toString()) / 100) * totamt;  
                   }
                 }    
                cnt++;
                String[] c1 = new String[]{
                 String.valueOf(cnt), // shline
                 ss[5], // item
                 tbkey.getText(), // order
                 tbnumber.getText(), // cust fo
                 currformatDoubleUS(myamt), // netprice
                 "0", // taxamt
                 ss[1], // desc 
                 "Flat Rate" // sku -- holds rate type
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
             totalcharges += Double.valueOf(sactable.getValueAt(j, 4).toString()); 
      }  
      tbcharges.setText(currformatDouble(totalcharges));
      
      
      for (Map.Entry<String, ArrayList<String[]>> z : itemmap.entrySet()) { 
           ArrayList<String[]> itemlist = z.getValue();
           for (String[] s : itemlist) {
               weight += Double.valueOf(s[7]);
               pallets += Double.valueOf(s[6]);
           }
      }
      
      for (Map.Entry<String, String[]> z : kvstop.entrySet()) { 
           String[] v = z.getValue();
           if (! v[37].isBlank() && ! v[38].isBlank()) {
           rate += Double.valueOf(v[37]);
           miles += Double.valueOf(v[38]);
           }
      }
      
      
      // updates header fields
      if (cbderivedweight.isSelected()) {
        tbtotweight.setText(String.valueOf(weight));
      } else {
        if (! tbtotweight.getText().isBlank()) {
         weight = Double.valueOf(tbtotweight.getText());  // if not derived...take text value
        }
      }
      
      if (cbderivedrate.isSelected()) {
        tbforate.setText(currformatDouble(rate));
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
            } else if (ddratetype.getSelectedItem().toString().equals("Mileage Rate")) {
                dol = (Double.valueOf(tbforate.getText()) * miles) + Double.valueOf(tbcharges.getText());
            } else {
                dol = (Double.valueOf(tbforate.getText()) * weight) + Double.valueOf(tbcharges.getText());
            }
      } 
      tbcost.setText(currformatDouble(dol));
      
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
                    dcdate.setDate(parseDate(res.getString("sh_shipdate")));
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
        tbweight.setText(""); 
        tbremarks.setText(""); 
        tbvolume.setText("");
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
        
        tbtime1.setText("");
        tbtime2.setText("");
        
     
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
        tbweight.setEnabled(state);
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
        tbtime1.setEnabled(state);
        ddtimetype2.setEnabled(state);
        tbtime2.setEnabled(state);
        ddtimezone.setEnabled(state);
        
        tbstopitem.setEnabled(state);
        tbstopqty.setEnabled(state);
        tbstoppallets.setEnabled(state);
        tbstopitemdesc.setEnabled(state);
        tbstopweight.setEnabled(state);
        tbstopordernbr.setEnabled(state);
        tbstoprate.setEnabled(state);
        tbstopmiles.setEnabled(state);
        tbvolume.setEnabled(state);
        btdeleteitem.setEnabled(state);
        btadditem.setEnabled(state);
        
    }
    
    public void clientChangeEvent(String mykey) {
          
       if (! isLoad && ! mykey.isBlank() ) {
           ddshipto.removeAllItems();
           lbclientname.setText(cusData.getCustName(mykey));
           
           
           ArrayList<String> list = cusData.getcustshipmstrlist(mykey);
            for (String s : list) {
                ddshipto.addItem(s);
            }
            ddshipto.insertItemAt("",0);
       } 
       if (mykey.isBlank() ) {
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
        tbweight.setText("");
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
        jPanel1 = new javax.swing.JPanel();
        tbdrivercell = new javax.swing.JTextField();
        tbbrokercontact = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddfotype = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        ddbroker = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        lbldriverid = new javax.swing.JLabel();
        ddorderstatus = new javax.swing.JComboBox();
        tbbrokercell = new javax.swing.JTextField();
        tbdrivername = new javax.swing.JTextField();
        btfinddriver = new javax.swing.JButton();
        lbldrivername = new javax.swing.JLabel();
        tbDriver = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblnumber = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbedi = new javax.swing.JCheckBox();
        jLabel85 = new javax.swing.JLabel();
        lblclient = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbrev = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox<>();
        ddcust = new javax.swing.JComboBox<>();
        tbtrailer = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        btaddshipper = new javax.swing.JButton();
        tbnumber = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        ddrevision = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        ddservicetype = new javax.swing.JComboBox<>();
        lbclientname = new javax.swing.JLabel();
        btfindvehicle = new javax.swing.JButton();
        tbvehicle = new javax.swing.JTextField();
        lblequiptype = new javax.swing.JLabel();
        tbequiptype = new javax.swing.JTextField();
        btfindequiptype = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        ddratetype = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        dcorddate = new com.toedter.calendar.JDateChooser();
        cbhazmat = new javax.swing.JCheckBox();
        cbderivedrate = new javax.swing.JCheckBox();
        cbstandard = new javax.swing.JCheckBox();
        cbderivedweight = new javax.swing.JCheckBox();
        cbderivedmiles = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbmileage = new javax.swing.JTextField();
        dcconfdate = new com.toedter.calendar.JDateChooser();
        tbtotweight = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        tbdriverrate = new javax.swing.JTextField();
        tbforate = new javax.swing.JTextField();
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
        jScrollPane4 = new javax.swing.JScrollPane();
        tanotes = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        jPanelLocation = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btaddstop = new javax.swing.JButton();
        btupdatestop = new javax.swing.JButton();
        btdeletestop = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        tbremarks = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        dcdate = new com.toedter.calendar.JDateChooser();
        dddatetype = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        ddtimezone = new javax.swing.JComboBox<>();
        ddtimetype1 = new javax.swing.JComboBox<>();
        ddtimetype2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tbtime1 = new javax.swing.JTextField();
        tbtime2 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        ddstoptype = new javax.swing.JComboBox<>();
        jLabel88 = new javax.swing.JLabel();
        tbstoprate = new javax.swing.JTextField();
        tbcontact = new javax.swing.JTextField();
        tbphone = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        tbstopmiles = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tbweight = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        tbvolume = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel82 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        tbaddr1 = new javax.swing.JTextField();
        ddcountry = new javax.swing.JComboBox();
        tbname = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        ddshipto = new javax.swing.JComboBox<>();
        tbcity = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ddstate = new javax.swing.JComboBox();
        jLabel93 = new javax.swing.JLabel();
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
        ddchargecode = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btfindchargecode = new javax.swing.JButton();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

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

        jLabel30.setText("Broker Cell#");

        jLabel7.setText("Cell#");

        ddfotype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Spot", "Broker" }));

        jLabel36.setText("Status");

        ddbroker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));
        ddbroker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbrokerActionPerformed(evt);
            }
        });

        jLabel28.setText("Broker");

        jLabel24.setText("Type");

        jLabel29.setText("Broker Contact");

        lbldriverid.setText("Driver ID");
        lbldriverid.setName("lbldriverid"); // NOI18N

        ddorderstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "pending", "accepted", "scheduled", "delivered", "declined", "cancelled", "closed" }));
        ddorderstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddorderstatusActionPerformed(evt);
            }
        });

        btfinddriver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btfinddriver.setFocusable(false);
        btfinddriver.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btfinddriver.setName("btlookup"); // NOI18N
        btfinddriver.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btfinddriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfinddriverActionPerformed(evt);
            }
        });

        lbldrivername.setText("Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36)
                    .addComponent(lbldriverid)
                    .addComponent(jLabel7)
                    .addComponent(jLabel24)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(lbldrivername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbbrokercontact)
                            .addComponent(ddbroker, 0, 132, Short.MAX_VALUE)
                            .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdrivername)
                            .addComponent(tbdrivercell)
                            .addComponent(tbbrokercell))
                        .addGap(39, 39, 39))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddorderstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btfinddriver, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddorderstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbldriverid))
                    .addComponent(btfinddriver, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdrivername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldrivername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel7))
                    .addComponent(tbdrivercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbrokercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addContainerGap())
        );

        lblnumber.setText("Cust Order");
        lblnumber.setName(""); // NOI18N

        jLabel9.setText("Service");

        cbedi.setText("EDI");

        jLabel85.setText("EquipType");
        jLabel85.setName("lblequipmenttype"); // NOI18N

        lblclient.setText("Shipper");
        lblclient.setName(""); // NOI18N

        jLabel6.setText("Revision");

        cbrev.setText("Default");

        ddcust.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));
        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        jLabel101.setText("Truck ID");
        jLabel101.setName("lblcarrier"); // NOI18N

        btaddshipper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddshipperActionPerformed(evt);
            }
        });

        jLabel16.setText("Trailer");

        jLabel40.setText("Site");

        ddservicetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2", "test3" }));

        btfindvehicle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btfindvehicle.setFocusable(false);
        btfindvehicle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btfindvehicle.setName("btlookup"); // NOI18N
        btfindvehicle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btfindvehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfindvehicleActionPerformed(evt);
            }
        });

        btfindequiptype.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btfindequiptype.setFocusable(false);
        btfindequiptype.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btfindequiptype.setName("btlookup"); // NOI18N
        btfindequiptype.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btfindequiptype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfindequiptypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblclient)
                    .addComponent(lblnumber)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel85)
                    .addComponent(jLabel101)
                    .addComponent(jLabel16)
                    .addComponent(jLabel40))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddrevision, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbrev)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbedi))
                    .addComponent(ddservicetype, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbclientname, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbequiptype)
                            .addComponent(tbvehicle, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btfindequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblequiptype, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btfindvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbclientname, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblclient)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblnumber))
                    .addComponent(btaddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddrevision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cbedi)
                    .addComponent(cbrev))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddservicetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btfindequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel85)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel101)
                        .addComponent(tbvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btfindvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addContainerGap())
        );

        ddratetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flat Rate", "Mileage Rate", "Weight Rate" }));
        ddratetype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddratetypeActionPerformed(evt);
            }
        });

        jLabel37.setText("Driver Rate");

        dcorddate.setDateFormatString("yyyy-MM-dd");

        cbhazmat.setText("Hazmat");

        cbderivedrate.setText("Derived");
        cbderivedrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbderivedrateActionPerformed(evt);
            }
        });

        cbstandard.setText("Standard");

        cbderivedweight.setText("Derived");

        cbderivedmiles.setText("Derived");
        cbderivedmiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbderivedmilesActionPerformed(evt);
            }
        });

        jLabel34.setText("Order Date");

        jLabel33.setText("Weight");

        jLabel31.setText("Rate Type");

        jLabel23.setText("Rate");

        tbmileage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmileageFocusLost(evt);
            }
        });

        dcconfdate.setDateFormatString("yyyy-MM-dd");

        tbtotweight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtotweightFocusLost(evt);
            }
        });

        jLabel35.setText("Commit Date");

        jLabel32.setText("Miles");

        tbdriverrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdriverrateFocusLost(evt);
            }
        });

        tbforate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbforateFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddratetype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbforate)
                    .addComponent(tbmileage, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtotweight)
                    .addComponent(tbdriverrate)
                    .addComponent(dcorddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcconfdate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbhazmat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbstandard)
                    .addComponent(cbderivedrate)
                    .addComponent(cbderivedmiles)
                    .addComponent(cbderivedweight))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddratetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbforate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(cbderivedrate))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtotweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33)
                            .addComponent(cbderivedweight))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdriverrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(cbstandard))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(dcorddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcconfdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbhazmat))
                    .addComponent(cbderivedmiles))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(60, 60, 60)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(18, Short.MAX_VALUE))))
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

        tanotes.setColumns(20);
        tanotes.setRows(5);
        jScrollPane4.setViewportView(tanotes);

        jLabel15.setText("Notes:");

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane8)
                            .addComponent(jScrollPane4))))
                .addContainerGap())
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(4, 4, 4)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        jLabel97.setText("Remarks");
        jLabel97.setName("lblremarks"); // NOI18N

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduling"));

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel26.setText("Date");

        jLabel27.setText("Type");

        jLabel2.setText("TimeZone");

        jLabel21.setText("Time Event 1");

        jLabel25.setText("Time Event 2");

        tbtime1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtime1FocusLost(evt);
            }
        });

        tbtime2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtime2FocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddtimezone, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel21)
                        .addGroup(jPanel16Layout.createSequentialGroup()
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ddtimetype1, 0, 236, Short.MAX_VALUE)
                                .addComponent(ddtimetype2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbtime1)
                                .addComponent(tbtime2, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)))
                        .addComponent(jLabel25)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(ddtimetype1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtimetype2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ddstoptype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Load", "Unload Complete", "Unload Partial" }));

        jLabel88.setText("Contact");
        jLabel88.setName("lblcontact"); // NOI18N

        jLabel12.setText("Stop Type");

        jLabel39.setText("Miles");

        jLabel89.setText("Phone");
        jLabel89.setName("lblphone"); // NOI18N

        jLabel96.setText("Email");
        jLabel96.setName("lblemail"); // NOI18N

        jLabel1.setText("Rate");

        jLabel86.setText("Weight");
        jLabel86.setName("lblmisc"); // NOI18N

        jLabel38.setText("Volume");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel89)
                            .addComponent(jLabel96)
                            .addComponent(jLabel88)
                            .addComponent(jLabel86)
                            .addComponent(jLabel12)
                            .addComponent(jLabel1))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addComponent(jLabel39)))
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(tbstopmiles, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbweight, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbstoprate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbemail)
                        .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbvolume, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstoprate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstopmiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbvolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addContainerGap())
        );

        jLabel82.setText("Name");
        jLabel82.setName("lblname"); // NOI18N

        jLabel90.setText("Addr2");
        jLabel90.setName("lbladdr2"); // NOI18N

        jLabel91.setText("Addr1");
        jLabel91.setName("lbladdr1"); // NOI18N

        jLabel18.setText("Country");

        ddshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshiptoActionPerformed(evt);
            }
        });

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel94.setText("State");
        jLabel94.setName("lblstate"); // NOI18N

        jLabel17.setText("Location");

        jLabel93.setText("City");
        jLabel93.setName("lblcity"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82)
                    .addComponent(jLabel91)
                    .addComponent(jLabel90)
                    .addComponent(jLabel93)
                    .addComponent(jLabel94)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbaddr1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel82))
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel91))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel90))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel93))
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel94))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel97)
                        .addGap(4, 4, 4)
                        .addComponent(tbremarks))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        ddchargecode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddchargecodeActionPerformed(evt);
            }
        });

        jLabel5.setText("Charge Code");

        btfindchargecode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btfindchargecode.setFocusable(false);
        btfindchargecode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btfindchargecode.setName("btlookup"); // NOI18N
        btfindchargecode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btfindchargecode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btfindchargecodeActionPerformed(evt);
            }
        });

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
                            .addComponent(jLabel43)
                            .addComponent(jLabel5))
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
                            .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelChargesLayout.createSequentialGroup()
                                .addComponent(ddchargecode, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btfindchargecode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChargesLayout.createSequentialGroup()
                        .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddchargecode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                    .addGroup(jPanelChargesLayout.createSequentialGroup()
                        .addComponent(btfindchargecode, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanelCharges);

        panelAttachment.setBorder(javax.swing.BorderFactory.createTitledBorder("Attachment Panel"));
        panelAttachment.setName("panelAttachment"); // NOI18N
        panelAttachment.setPreferredSize(new java.awt.Dimension(974, 560));

        btaddattachment.setText("Add Attachment");
        btaddattachment.setName("btaddattachment"); // NOI18N
        btaddattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattachmentActionPerformed(evt);
            }
        });

        btdeleteattachment.setText("Delete Attachment");
        btdeleteattachment.setName("btdeleteattachment"); // NOI18N
        btdeleteattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattachmentActionPerformed(evt);
            }
        });

        tableattachment.setModel(new javax.swing.table.DefaultTableModel(
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
        tableattachment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableattachmentMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableattachment);

        javax.swing.GroupLayout panelAttachmentLayout = new javax.swing.GroupLayout(panelAttachment);
        panelAttachment.setLayout(panelAttachmentLayout);
        panelAttachmentLayout.setHorizontalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addComponent(btaddattachment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeleteattachment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 446, Short.MAX_VALUE)
                .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane3)
        );
        panelAttachmentLayout.setVerticalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btaddattachment)
                        .addComponent(btdeleteattachment)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     newAction("cfo");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddstopActionPerformed
        
        if (! validateStopInput(dbaction.add)) {
           return;
        }
        
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
        String datecode = "";
        String datetype = "";
        String timecode1 = "";
        String timetype1 = "";
        String timecode2 = "";
        String timetype2 = "";
        if (dddatetype.getSelectedItem() != null && ! dddatetype.getSelectedItem().toString().isBlank()) {
            datecode = dddatetype.getSelectedItem().toString().split("-")[0];
            datetype = dddatetype.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype1.getSelectedItem() != null && ! ddtimetype1.getSelectedItem().toString().isBlank()) {
            timecode1 = ddtimetype1.getSelectedItem().toString().split("-")[0];
            timetype1 = ddtimetype1.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype2.getSelectedItem() != null && ! ddtimetype2.getSelectedItem().toString().isBlank()) {
            timecode2 = ddtimetype2.getSelectedItem().toString().split("-")[0];
            timetype2 = ddtimetype2.getSelectedItem().toString().split("-")[1];
        }
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
            tbweight.getText(),
            tbremarks.getText(),
            "", // ref
            "", // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            datecode, // datecode
            datetype, // datetype
            datestr,
            "",  // datecode2
            "",  // datetype2
            null,  // date2
            timecode1, // timecode1
            timetype1,
            tbtime1.getText(),
            ddtimezone.getSelectedItem().toString(),
            timecode2, // timecode2
            timetype2,  // timetype2
            tbtime2.getText(), // time2
            ddtimezone.getSelectedItem().toString(), // timezone2
            tbstoprate.getText().isBlank() ? "0" : tbstoprate.getText(),
            tbstopmiles.getText().isBlank() ? "0" : tbstopmiles.getText(),
            tbvolume.getText().isBlank() ? "0" : tbvolume.getText()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        
       
         

        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false;
        
        summarize();
        clearStopFields();    
        setStopState(false);
        
        // now update
        if (isCFOCommitted) {   // assuming the order already exists in DB
           ArrayList<String> badlines = getBadLines(tbkey.getText());
           updateCFOTransaction(tbkey.getText(), ddrevision.getSelectedItem().toString(), badlines, createDetRecord(), createRecord(), createItemRecord(), null);
        }
        
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
        // now update
        if (isCFOCommitted) {   // assuming the order already exists in DB
           ArrayList<String> badlines = getBadLines(tbkey.getText());
           updateCFOTransaction(tbkey.getText(), ddrevision.getSelectedItem().toString(), badlines, createDetRecord(), createRecord(), createItemRecord(), null);
        }
    }//GEN-LAST:event_btdeletestopActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText(), ddrevision.getSelectedItem().toString()});  
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        try {
            OVData.printCFO(tbkey.getText());
        } catch (SQLException ex) {
            bslog(ex);
        } catch (JRException ex) {
            bslog(ex);
        }
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
      setPanelComponentState(this, false);
      executeTask(dbaction.run, new String[]{tbkey.getText()});  
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
            dddatetype.setSelectedItem("Scheduled Delivery Date");
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
            currentstopline = stopnumber;
            
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
                tbweight.setText(v[15]); 
                tbremarks.setText(v[16]);
                if (v[23].isBlank()) {
                  dddatetype.setSelectedIndex(0);
                } else {
                  dddatetype.setSelectedItem(v[23] + "-" + v[24]);
                }
                dcdate.setDate(BlueSeerUtils.parseDate(v[25]));
                ddtimezone.setSelectedItem(v[32]);
                if (v[29].isBlank()) {
                  ddtimetype1.setSelectedIndex(0);
                } else {
                  ddtimetype1.setSelectedItem(v[29] + "-" + v[30]);
                }
                tbtime1.setText(v[31]);
                if (v[29].isBlank()) {
                  ddtimetype2.setSelectedIndex(0);
                } else {
                  ddtimetype2.setSelectedItem(v[33] + "-" + v[34]);
                }
                tbtime2.setText(v[35]);
                tbstoprate.setText(v[37]);
                tbstopmiles.setText(v[38]);
                tbvolume.setText(v[39]);
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
          if (! isLoad && ddcust.getItemCount() > 0 ) {
           clientChangeEvent(ddcust.getSelectedItem().toString());
        } // if ddcust has a list
    }//GEN-LAST:event_ddcustActionPerformed

    private void ddbrokerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbrokerActionPerformed
        if (! isLoad && ddbroker.getSelectedItem() != null && ! ddbroker.getSelectedItem().toString().isBlank()) {
            brk_mstr brk = getBrokerMstr(new String[]{ddbroker.getSelectedItem().toString()});
            tbbrokercell.setText(brk.brk_phone());
            tbbrokercontact.setText(brk.brk_contact());
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
                                tbweight.setText(res.getString("cms_misc"));
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
        String datecode = "";
        String datetype = "";
        String timecode1 = "";
        String timetype1 = "";
        String timecode2 = "";
        String timetype2 = "";
        if (dddatetype.getSelectedItem() != null && ! dddatetype.getSelectedItem().toString().isBlank()) {
            datecode = dddatetype.getSelectedItem().toString().split("-")[0];
            datetype = dddatetype.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype1.getSelectedItem() != null && ! ddtimetype1.getSelectedItem().toString().isBlank()) {
            timecode1 = ddtimetype1.getSelectedItem().toString().split("-")[0];
            timetype1 = ddtimetype1.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype2.getSelectedItem() != null && ! ddtimetype2.getSelectedItem().toString().isBlank()) {
            timecode2 = ddtimetype2.getSelectedItem().toString().split("-")[0];
            timetype2 = ddtimetype2.getSelectedItem().toString().split("-")[1];
        }
        
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
            weight += bsParseDouble(itemdet.getValueAt(j, 6).toString());
            ladingqty += bsParseDouble(itemdet.getValueAt(j, 4).toString());
            pallets += bsParseDouble(itemdet.getValueAt(j, 5).toString());
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
            tbweight.getText(),
            tbremarks.getText(),
            "", // ref
            "", // ordnum
            String.valueOf(weight), // weight
            String.valueOf(pallets), // pallets
            String.valueOf(ladingqty), // ladingqty
            "", // hazmat
            datecode, // datecode
            datetype, // datetype
            datestr,
            "",  // datecode2
            "",  // datetype2
            "",  // date2
            timecode1, // timecode1
            timetype1,
            tbtime1.getText(),
            ddtimezone.getSelectedItem().toString(),
            timecode2, // timecode2
            timetype2,  // timetype2
            tbtime2.getText(), // time2
            ddtimezone.getSelectedItem().toString(), // timezone2
            tbstoprate.getText(),
            tbstopmiles.getText(),
            tbvolume.getText()
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
       
       // now update
        if (isCFOCommitted) {   // assuming the order already exists in DB
           ArrayList<String> badlines = getBadLines(tbkey.getText());
           updateCFOTransaction(tbkey.getText(), ddrevision.getSelectedItem().toString(), badlines, createDetRecord(), createRecord(), createItemRecord(), null);
        }
        
        
    }//GEN-LAST:event_btupdatestopActionPerformed

    private void cbderivedrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbderivedrateActionPerformed
        if (cbderivedrate.isSelected()) {
            tbforate.setEnabled(false);
        } else {
            tbforate.setEnabled(true);
        }
        summarize();
    }//GEN-LAST:event_cbderivedrateActionPerformed

    private void cbderivedmilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbderivedmilesActionPerformed
        if (cbderivedmiles.isSelected()) {
            tbmileage.setEnabled(false);
        } else {
            tbmileage.setEnabled(true);
        }
        summarize();
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
        sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), ddchargecode.getSelectedItem().toString(), tbsacdesc.getText(), ddsacamttype.getSelectedItem().toString(), String.valueOf(amount)});
        }
        tbsacdesc.setText("");
        ddchargecode.setSelectedIndex(0);
        tbsacamt.setText("");
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
                if (ddorderstatus.getSelectedItem().toString().equals("scheduled") ||
                    ddorderstatus.getSelectedItem().toString().equals("accepted") ||    
                    ddorderstatus.getSelectedItem().toString().equals("declined")) {
                    boolean proceed = bsmf.MainFrame.warn(getMessageTag(1183));
                    if (proceed) {
                        receipt990 = true;
                        if (ddorderstatus.getSelectedItem().toString().equals("declined")) {
                        lookUpFrameRejection();
                        }
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
         String datecode = "";
        String datetype = "";
        String timecode1 = "";
        String timetype1 = "";
        String timecode2 = "";
        String timetype2 = "";
        if (dddatetype.getSelectedItem() != null && ! dddatetype.getSelectedItem().toString().isBlank()) {
            datecode = dddatetype.getSelectedItem().toString().split("-")[0];
            datetype = dddatetype.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype1.getSelectedItem() != null && ! ddtimetype1.getSelectedItem().toString().isBlank()) {
            timecode1 = ddtimetype1.getSelectedItem().toString().split("-")[0];
            timetype1 = ddtimetype1.getSelectedItem().toString().split("-")[1];
        }
        if (ddtimetype2.getSelectedItem() != null && ! ddtimetype2.getSelectedItem().toString().isBlank()) {
            timecode2 = ddtimetype2.getSelectedItem().toString().split("-")[0];
            timetype2 = ddtimetype2.getSelectedItem().toString().split("-")[1];
        }
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
            datecode, // datecode
            datetype, // datetype
            datestr,
            "",  // datecode2
            "",  // datetype2
            null,  // date2
            timecode1, // timecode1
            timetype1,
            tbtime1.getText(),
            ddtimezone.getSelectedItem().toString(),
            timecode2, // timecode2
            timetype2,  // timetype2
            tbtime2.getText(), // time2
            ddtimezone.getSelectedItem().toString(), // timezone2
            tbstoprate.getText().isBlank() ? "0" : tbstoprate.getText(),
            tbstopmiles.getText().isBlank() ? "0" : tbstopmiles.getText(),
            tbvolume.getText().isBlank() ? "0" : tbvolume.getText()
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
            tbtime1.getText(),
            ddtimetype2.getSelectedItem().toString(),
            tbtime2.getText(),
            ddtimezone.getSelectedItem().toString(),
            tbstoprate.getText(),
            tbstopmiles.getText(),
            tbvolume.getText()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        
        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false;
        
        summarize();
        
        
    }//GEN-LAST:event_btaddshipperActionPerformed

    private void ddratetypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddratetypeActionPerformed
        summarize();
    }//GEN-LAST:event_ddratetypeActionPerformed

    private void btaddattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattachmentActionPerformed
        OVData.addFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), this );
        getAttachments(tbkey.getText());
    }//GEN-LAST:event_btaddattachmentActionPerformed

    private void btdeleteattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattachmentActionPerformed
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
            int[] rows = tableattachment.getSelectedRows();
            String filename = null;
            for (int i : rows) {
                filename = tableattachment.getValueAt(i, 1).toString();
            }
            OVData.deleteFileAttachment(tbkey.getText(),this.getClass().getSimpleName(),filename);
            getAttachments(tbkey.getText());
        }
    }//GEN-LAST:event_btdeleteattachmentActionPerformed

    private void tableattachmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableattachmentMouseClicked
        int row = tableattachment.rowAtPoint(evt.getPoint());
        int col = tableattachment.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            OVData.openFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), tableattachment.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_tableattachmentMouseClicked

    private void ddchargecodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddchargecodeActionPerformed
        if (! isLoad) {
            if (ddchargecode.getSelectedItem() != null && ! ddchargecode.getSelectedItem().toString().isBlank() ) {
                tbsacdesc.setText(getFreightCodeByCodeKey("freightchargecodes", ddchargecode.getSelectedItem().toString()));
            }
            if (ddchargecode.getSelectedItem() != null && ddchargecode.getSelectedItem().toString().isBlank() ) {
                tbsacdesc.setText("");
            }
        }
    }//GEN-LAST:event_ddchargecodeActionPerformed

    private void btfinddriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfinddriverActionPerformed
      lookUpFrameDriver();
       
    }//GEN-LAST:event_btfinddriverActionPerformed

    private void btfindvehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfindvehicleActionPerformed
        lookUpFrameVehicle();
    }//GEN-LAST:event_btfindvehicleActionPerformed

    private void btfindchargecodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfindchargecodeActionPerformed
        lookUpFrameChargeCode();
    }//GEN-LAST:event_btfindchargecodeActionPerformed

    private void tbtotweightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtotweightFocusLost
          if (! tbtotweight.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbtotweight.getText(), "0");
        if (x.equals("error")) {
            tbtotweight.setText("");
            tbtotweight.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbtotweight.requestFocus();
        } else {
            tbtotweight.setText(x);
            tbtotweight.setBackground(Color.white);
        }
        summarize();
        }
    }//GEN-LAST:event_tbtotweightFocusLost

    private void tbtime1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtime1FocusLost
        if (tbtime1.getText().isBlank()) {
            return;
        }
        String x = tbtime1.getText();
        
        if (x.length() != 5 && x.indexOf(":") != 2 ) {
            tbtime1.setText("");
            tbtime1.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ... 00:00 to 23:59");
            tbtime1.requestFocus();
        } else if (! isParsableToInt(x.substring(0,2)) || ! isParsableToInt(x.substring(3,5))) {
            tbtime1.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ...valid time range 00:00 to 23:59");
            tbtime1.requestFocus();
        } else if (Integer.valueOf(x.substring(0,2)) > 23 || Integer.valueOf(x.substring(3,5)) > 59) {
            tbtime1.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ...valid time range 00:00 to 23:59");
            tbtime1.requestFocus();    
        } else {
           tbtime1.setText(x);
           tbtime1.setBackground(Color.white); 
        }
    }//GEN-LAST:event_tbtime1FocusLost

    private void tbtime2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtime2FocusLost
        if (tbtime2.getText().isBlank()) {
            return;
        }
        String x = tbtime2.getText();
        
        if (x.length() != 5 && x.indexOf(":") != 2 ) {
            tbtime2.setText("");
            tbtime2.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ... 00:00 to 23:59");
            tbtime2.requestFocus();
        } else if (! isParsableToInt(x.substring(0,2)) || ! isParsableToInt(x.substring(3,5))) {
            tbtime2.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ...valid time range 00:00 to 23:59");
            tbtime2.requestFocus();
        } else if (Integer.valueOf(x.substring(0,2)) > 23 || Integer.valueOf(x.substring(3,5)) > 59) {
            tbtime2.setBackground(Color.yellow);
            bsmf.MainFrame.show("time format must be xx:xx ...valid time range 00:00 to 23:59");
            tbtime2.requestFocus();    
        } else {
           tbtime2.setText(x);
           tbtime2.setBackground(Color.white); 
        }
    }//GEN-LAST:event_tbtime2FocusLost

    private void btfindequiptypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btfindequiptypeActionPerformed
        lookUpFrameEquipType();
    }//GEN-LAST:event_btfindequiptypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btaddshipper;
    private javax.swing.JButton btaddstop;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearstop;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btdeletestop;
    private javax.swing.JButton btfindchargecode;
    private javax.swing.JButton btfinddriver;
    private javax.swing.JButton btfindequiptype;
    private javax.swing.JButton btfindvehicle;
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
    private javax.swing.JComboBox<String> ddchargecode;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcust;
    private javax.swing.JComboBox<String> dddatetype;
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
    private javax.swing.JComboBox<String> ddtimetype1;
    private javax.swing.JComboBox<String> ddtimetype2;
    private javax.swing.JComboBox<String> ddtimezone;
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
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelCharges;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lbclientname;
    private javax.swing.JLabel lblclient;
    private javax.swing.JLabel lbldriverid;
    private javax.swing.JLabel lbldrivername;
    private javax.swing.JLabel lblequiptype;
    private javax.swing.JLabel lblnumber;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblstop;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTextArea tanotes;
    private javax.swing.JTextField tbDriver;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbbrokercell;
    private javax.swing.JTextField tbbrokercontact;
    private javax.swing.JTextField tbcharges;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbdrivercell;
    private javax.swing.JTextField tbdrivername;
    private javax.swing.JTextField tbdriverrate;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbequiptype;
    private javax.swing.JTextField tbforate;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmileage;
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
    private javax.swing.JTextField tbtime1;
    private javax.swing.JTextField tbtime2;
    private javax.swing.JTextField tbtotweight;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbvehicle;
    private javax.swing.JTextField tbvolume;
    private javax.swing.JTextField tbweight;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
