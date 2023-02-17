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
package com.blueseer.dst;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.dst.dstData.addDOTransaction;
import static com.blueseer.dst.dstData.deleteDOMstr;
import com.blueseer.dst.dstData.do_mstr;
import com.blueseer.dst.dstData.dod_mstr;
import static com.blueseer.dst.dstData.getDODet;
import static com.blueseer.dst.dstData.getDOLines;
import static com.blueseer.dst.dstData.getDOMstr;
import static com.blueseer.dst.dstData.updateDOTransaction;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsdate;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
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
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;


/**
 *
 * @author vaughnte
 */
public class DOMaint extends javax.swing.JPanel implements IBlueSeerT {

     // global variable declarations
                boolean isLoad = false;
                
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("quantity"),
                getGlobalColumnTag("uom"), 
                getGlobalColumnTag("reference"),
                getGlobalColumnTag("serial")
            });
     
 
    /**
     * Creates new form OrderMaintPanel
     */
    public DOMaint() {
        initComponents();
        setLanguageTags(this);
    }

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
        tbkey.setText("");
        tbkey.setText("");
        tbqty.setText("0");
        remarks.setText("");
        tbref.setText("");
        tbserial.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcshipdate.setDate(now);
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        dditem.removeAllItems();
         ArrayList<String> parts = invData.getItemMasterAlllist();
        for (String code : parts) {
            dditem.addItem(code);
        }
        
         ddwhto.removeAllItems();
        ArrayList<String> mylist = OVData.getWareHouseList();
        for (String code : mylist) {
            ddwhto.addItem(code);
        }
        
         ddwhfrom.removeAllItems();
         mylist = OVData.getWareHouseList();
        for (String code : mylist) {
            ddwhfrom.addItem(code);
        }
        
         ddsite.removeAllItems();
        ArrayList<String> site = OVData.getSiteList();
        for (int i = 0; i < site.size(); i++) {
            ddsite.addItem(site.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddshipvia.removeAllItems();
        mylist = OVData.getfreightlist();   
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
      
        
        dduom.removeAllItems();
        dduom.insertItemAt("", 0);
         dduom.setSelectedIndex(0);
        mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
        dditem.removeAllItems();
        ArrayList<String> items = invData.getItemMasterListBySite(ddsite.getSelectedItem().toString()); 
        for (String item : items) {
        dditem.addItem(item);
        }  
        
        ddshipvia.removeAllItems();
        mylist = OVData.getfreightlist();  
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
        ddstatus.removeAllItems();
        mylist = OVData.getCodeMstr("orderstatus");
        for (int i = 0; i < mylist.size(); i++) {
            ddstatus.addItem(mylist.get(i));
        }
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
        
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
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        if (x[0].equals("0")) { 
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
     
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        boolean b = true;
        
                String z = BlueSeerUtils.bsformat("", tbkey.getText(), "0");
                if (z.equals("error")) {
                    bsmf.MainFrame.show(getMessageTag(1000));
                    tbkey.requestFocus();
                    b = false;
                    return b;
                } 
        
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    BlueSeerUtils.message(new String[] {"1", "must enter a number"});
                    tbkey.requestFocus();
                    return b;
                }
                
               
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
      
       
        
        if (arg != null && arg.length > 0) {
            executeTask(BlueSeerUtils.dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
   
    public String[] getRecord(String[] key) {
        do_mstr x = getDOMstr(key); 
        tbkey.setText(x.do_nbr());
        ddsite.setSelectedItem(x.do_site());
        ddwhfrom.setSelectedItem(x.do_wh_from());
        ddwhto.setSelectedItem(x.do_wh_to());
        ddshipvia.setSelectedItem(x.do_shipvia());
        ddstatus.setSelectedItem(x.do_status());
        dcshipdate.setDate(BlueSeerUtils.parseDate(x.do_shipdate()));
        dcrecvdate.setDate(BlueSeerUtils.parseDate(x.do_recvdate()));
        remarks.setText(x.do_rmks());
        // now detail
        myorddetmodel.setRowCount(0);
        ArrayList<dod_mstr> z = getDODet(key[0]);
        for (dod_mstr d : z) {
            myorddetmodel.addRow(new Object[]{d.dod_line(), d.dod_item(), d.dod_qty(),
                 d.dod_uom(), d.dod_ref(), d.dod_serial()});
        }
        setAction(x.m());
        return x.m();
    }
    
    public do_mstr createRecord() { 
        do_mstr x = new do_mstr(null, 
                tbkey.getText().toString(),
                ddsite.getSelectedItem().toString(),
                "DO", // type
                ddwhfrom.getSelectedItem().toString(),
                ddwhto.getSelectedItem().toString(),
                BlueSeerUtils.setDateFormat(dcshipdate.getDate()),
                BlueSeerUtils.setDateFormat(dcrecvdate.getDate()), 
                ddstatus.getSelectedItem().toString(),
                "", //ref
                remarks.getText(),
                ddshipvia.getSelectedItem().toString(),
                "", // pallets
                "", // gross weight
                "", //char1
                "" // char2
                );
        return x;
    }
    
    public ArrayList<dod_mstr> createDetRecord() {
        ArrayList<dod_mstr> list = new ArrayList<dod_mstr>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dod_mstr x = new dod_mstr(null, 
                tbkey.getText(),
                orddet.getValueAt(j, 0).toString(),
                orddet.getValueAt(j, 1).toString(),
                orddet.getValueAt(j, 2).toString().replace(defaultDecimalSeparator, '.'), // qty
                orddet.getValueAt(j, 3).toString(), // uom
                orddet.getValueAt(j, 4).toString(), // ref
                orddet.getValueAt(j, 5).toString(), // serial
                "" // char1
                );
        list.add(x);
         }
        return list;   
    }
    
    public String[] addRecord(String[] key) {
         String[] m = addDOTransaction(createDetRecord(), createRecord());
         return m;
    }
        
    public String[] updateRecord(String[] key) {
         String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getDOLines(tbkey.getText());
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
        m = updateDOTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord());
     return m;
    }
    
    public String[] deleteRecord(String[] key) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteDOMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
    }
        
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getDOBrowseUtil(luinput.getText(),0, "do_nbr");
        } else {
         luModel = DTData.getDOBrowseUtil(luinput.getText(),0, "do_wh_from");   
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
                getClassLabelTag("lblfromwh", this.getClass().getSimpleName())); 
        
        
    }

    // misc functions
       
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
    }
    
    public void sumqty() {
        int qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Integer.valueOf(orddet.getValueAt(j, 2).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
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
   
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        dcshipdate = new com.toedter.calendar.JDateChooser();
        remarks = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btdelitem = new javax.swing.JButton();
        ddstatus = new javax.swing.JComboBox();
        ddwhfrom = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        ddshipvia = new javax.swing.JComboBox();
        jLabel90 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tbqty = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        dditem = new javax.swing.JComboBox();
        jLabel84 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        tbserial = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        tbtotqty = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ddwhto = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        dcrecvdate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();

        jLabel4.setText("jLabel4");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Distribution Order Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel76.setText("OrderNbr");
        jLabel76.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        dcshipdate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel85.setText("Status");
        jLabel85.setName("lblstatus"); // NOI18N

        jLabel82.setText("From WH");
        jLabel82.setName("lblfromwh"); // NOI18N

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
        jScrollPane8.setViewportView(orddet);

        btdelitem.setText("Del Item");
        btdelitem.setName("btdeleteitem"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Ship Date");
        jLabel81.setName("lblshipdate"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel90.setText("ShipVia");
        jLabel90.setName("lblshipvia"); // NOI18N

        jLabel79.setText("Item");
        jLabel79.setName("lblitem"); // NOI18N

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        jLabel84.setText("Qty Ship");
        jLabel84.setName("lblqty"); // NOI18N

        jLabel5.setText("UOM");
        jLabel5.setName("lbluom"); // NOI18N

        jLabel8.setText("Serial");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel79)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addComponent(jLabel84)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(jLabel5)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Total Lines");
        jLabel1.setName("lbltotlines"); // NOI18N

        jLabel2.setText("Total Qty");
        jLabel2.setName("lbltotqty"); // NOI18N

        jLabel91.setText("To WH");
        jLabel91.setName("lbltowh"); // NOI18N

        jLabel92.setText("Reference");
        jLabel92.setName("lblreference"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        dcrecvdate.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("Receive Date");

        jLabel6.setText("Remarks");

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        jLabel7.setText("Site");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddwhfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddwhto, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tbref, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(dcrecvdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(20, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelitem))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(63, 63, 63))
                                    .addComponent(totlines, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(149, 149, 149)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addGap(4, 4, 4)
                                .addComponent(btadd))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel81)
                                .addGap(139, 139, 139))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane8))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81)
                            .addComponent(dcshipdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcrecvdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel92)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel76)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnew)
                                            .addComponent(btclear))
                                        .addGap(15, 15, 15))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btlookup)
                                        .addGap(13, 13, 13)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddwhfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel82))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddwhto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel91))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      newAction("do");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        
        String part = "";
        String custpart = "";
        
            part = dditem.getSelectedItem().toString();
          
        orddet.setModel(myorddetmodel);
        
        Pattern p = Pattern.compile("^[1-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1028));
            canproceed = false;
        }
        line = getmaxline();
        line++;
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{line, part, tbqty.getText(), dduom.getSelectedItem().toString(), tbref.getText(), tbserial.getText()});
         sumqty();
         sumlinecount();
         dditem.requestFocus();
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
           if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
         if (dditem.getSelectedItem() != null)
        dduom.setSelectedItem(OVData.getUOMByItem(dditem.getSelectedItem().toString()));
    }//GEN-LAST:event_dditemActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        /*
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 10).toString().equals("close") || orddet.getValueAt(i, 10).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumlinecount();
         */
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(BlueSeerUtils.dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         if (! validateInput(BlueSeerUtils.dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        if (! btadd.isEnabled())
        executeTask(BlueSeerUtils.dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
       lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dcrecvdate;
    private com.toedter.calendar.JDateChooser dcshipdate;
    private javax.swing.JComboBox dditem;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox ddwhfrom;
    private javax.swing.JComboBox ddwhto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField remarks;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbserial;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
