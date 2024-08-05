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
package com.blueseer.rcv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.VoucherTransaction;
import com.blueseer.fap.fapData.ap_mstr;
import com.blueseer.fap.fapData.vod_mstr;
import com.blueseer.fgl.fglData;
import com.blueseer.pur.purData;
import static com.blueseer.pur.purData.updateReceivers;
import com.blueseer.rcv.rcvData.Receiver;
import static com.blueseer.rcv.rcvData.addReceiverTransaction;
import static com.blueseer.rcv.rcvData.getReceiverLines;
import static com.blueseer.rcv.rcvData.getReceiverMstrSet;
import static com.blueseer.rcv.rcvData.isReceived;
import com.blueseer.rcv.rcvData.recv_det;
import com.blueseer.rcv.rcvData.recv_mstr;
import static com.blueseer.rcv.rcvData.updateReceiverTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import com.blueseer.utl.BlueSeerUtils.dbaction;
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
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.lurb3;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.getSystemAttachmentDirectory;
import com.blueseer.vdr.venData;
import static com.blueseer.vdr.venData.getVendInfo;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;


/**
 *
 * @author vaughnte
 */
public class RecvMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
                boolean isLoad = false;
                public static recv_mstr rv = null;
                public static ArrayList<recv_det> rvdlist = null;
               
            
            // global datatablemodel declarations    
                
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
             
            
                javax.swing.table.DefaultTableModel myrecvdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("uom"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("lot"), 
                getGlobalColumnTag("cost")
            })
                        {
               boolean[] canEdit = new boolean[]{
               false, false, false, true, false, false, false, false, true, true, true, true, true
               };       
           @Override  
           public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
           }
                        };
    
   
    public RecvMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    

    // interface functions implemented
     public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
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
             updateForm();  
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             updateForm();  
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
        
        jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", panelMain);
       jTabbedPane1.add("Attachments", panelAttachment);
       
       attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
        
        lblmessage.setText("");
        lblmessage.setForeground(Color.black);
        
        rvdet.setModel(myrecvdetmodel);
        rvdet.getTableHeader().setReorderingAllowed(false);
        ArrayList<String[]> initDataSets = rcvData.getReceiverInit();
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
        
        tbpackingslip.setBackground(Color.white);
        tbpackingslip.setText("");
        tbqty.setText("0");
        tbserial.setText("");
        tblot.setText("");
        tbkey.setText("");
        tbkey.setForeground(Color.black);
       
        lbvendor.setText("");
        lblvendpart.setText("");
        
        tbcost.setDisabledTextColor(Color.black);
        tbcost.setText("");
        
        duedate.setDisabledTextColor(Color.black);
        duedate.setText("");
        
        tbqtyrcvd.setEditable(false);
        tbqtyrcvd.setForeground(Color.blue);
        tbqtyrcvd.setText("");
        
        tbqtyord.setEditable(false);
        tbqtyord.setForeground(Color.blue);
        tbqtyord.setText("");
        
        tbuom.setEditable(false);
        tbuom.setForeground(Color.blue);
        tbuom.setText("");
        
        tbline.setEditable(false);
        tbline.setForeground(Color.blue);
        tbline.setText("");
       
        tbprice.setEditable(false);
        tbprice.setForeground(Color.blue);
        tbprice.setText("");
        
        orddate.setDisabledTextColor(Color.black);
        orddate.setText("");
        ddsite.setForeground(Color.black);
        
        
        ddpo.removeAllItems();
        ddline.removeAllItems();
        ddwh.removeAllItems();
        ddloc.removeAllItems();
        ddvend.removeAllItems();
        ddsite.removeAllItems();
        
        String defaultsite = null;
        
         for (String[] s : initDataSets) {
            
            if (s[0].equals("voucher")) {
              cbautovoucher.setSelected(bsmf.MainFrame.ConvertStringToBool(s[1]));  
            }
            
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
           
            if (s[0].equals("warehouses")) {
              ddwh.addItem(s[1]); 
            }
            if (s[0].equals("locations")) {
              ddloc.addItem(s[1]); 
            }
           
            if (s[0].equals("vendors")) {
              ddvend.addItem(s[1]); 
            }
            
        }
        
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
         
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        
        ddsite.setSelectedItem(defaultsite);
        
        ddvend.insertItemAt("", 0);
        ddvend.setSelectedIndex(0);
       
        myrecvdetmodel.setRowCount(0);
        
        isLoad = false;
    }
    
    public void newAction(String x) {
      setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
    }
   
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   cbautovoucher.setSelected(false);
                   cbautovoucher.setEnabled(false);
                   tbkey.setForeground(Color.blue);
                   
                   if (rv.rv_status().equals("1")) {
                       btadd.setEnabled(false);
                       btupdate.setEnabled(false);
                       btdelete.setEnabled(false);
                       lblmessage.setText("Receiver has been vouchered and cannot be updated");
                       lblmessage.setForeground(Color.blue);
                   }
                   
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
    }
    
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get, arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
        
    }
    
    public String[] getRecord(String[] key) {
      Receiver z = getReceiverMstrSet(key);
      rv = z.rv();
      rvdlist = z.rvd();
      return z.m();
    }

    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     
     if (cbautovoucher.isSelected()) {
      String vonbr = String.valueOf(OVData.getNextNbr("voucher")); 
      String[] vendinfo = getVendInfo(ddvend.getSelectedItem().toString());
      ArrayList<vod_mstr> vodlist = createVodMstr(vonbr, vendinfo);
      ap_mstr ap = createAPMstr(vonbr, vendinfo);
        m = addReceiverTransaction(createDetRecord(), createRecord(), ap, vodlist);
        if (m[0].equals("0")) {
            updateReceivers(vodlist, ap.ap_subtype());
        }
     } else {
        m = addReceiverTransaction(createDetRecord(), createRecord(), null, null);  
     }
     
      // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        } 
     
    return m;
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     String[] vendinfo = getVendInfo(ddvend.getSelectedItem().toString());
     if (isReceived(tbkey.getText())) { 
         return new String[] {"1",getMessageTag(1152)};
     }
     // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getReceiverLines(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < rvdet.getRowCount(); j++) {
             if (rvdet.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
        m = updateReceiverTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord());
     
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
            ResultSet res = null;
            try {
                 res = st.executeQuery("select rvd_id, rvd_rline, rvd_item from recv_det where rvd_id = " + "'" + x[0] + "'" + 
                                      " and rvd_voqty > 0 " + ";");
                int i = 0;
                while (res.next()) {
                    i++;
                }
                if (i > 0) {
                   return m = new String[] {"1","cannot delete receiver...some lines already vouchered"};
                }
                
                
                   int k = st.executeUpdate("delete from recv_mstr where rv_id = " + "'" + tbkey.getText() + "'" + ";");
                   int j = st.executeUpdate("delete from recv_det where rvd_id = " + "'" + tbkey.getText() + "'" + ";");
                    if (k > 0 && j > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    initvars(null);
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
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
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
    }
    
    public recv_mstr createRecord() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String[] v = getVendInfo(ddvend.getSelectedItem().toString());
        recv_mstr x = new recv_mstr(null, 
                tbkey.getText(),
                ddvend.getSelectedItem().toString(),
                setDateDB(dcdate.getDate()),
                "", // status
                tbpackingslip.getText(),
                bsmf.MainFrame.userid.toString(),
                v[1],
                v[2],
                v[5],
                ddsite.getSelectedItem().toString(),
                "", // confdate
                "", // ref
                "" // remarks
                );
                
        return x;        
    }
    
    public ArrayList<recv_det> createDetRecord() {
        ArrayList<recv_det> list = new ArrayList<recv_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        // line, item, po, poline, qty, uom, listprice, disc, netprice, loc, wh, serial, lot, cost
        for (int j = 0; j < rvdet.getRowCount(); j++) { 
            recv_det x = new recv_det(null, 
                tbkey.getText(), // shipper
                rvdet.getValueAt(j, 2).toString(), // po
                bsParseInt(rvdet.getValueAt(j, 3).toString()), // poline
                tbpackingslip.getText(), // packingslip
                rvdet.getValueAt(j, 1).toString(), // item
                bsParseDouble(rvdet.getValueAt(j, 4).toString()),  // qty
                setDateDB(dcdate.getDate()),
                bsParseDouble(rvdet.getValueAt(j, 6).toString()),
                bsParseDouble(rvdet.getValueAt(j, 8).toString()),
                bsParseDouble(rvdet.getValueAt(j, 7).toString()),  
                rvdet.getValueAt(j, 12).toString(), // lot
                rvdet.getValueAt(j, 10).toString(), // wh
                rvdet.getValueAt(j, 11).toString(), // serial
                rvdet.getValueAt(j, 9).toString(),  // loc
                "", // jobnbr
                ddsite.getSelectedItem().toString(),
                "", // status
                bsParseInt(rvdet.getValueAt(j, 0).toString()), // rline
                0, // voqty
                bsParseDouble(rvdet.getValueAt(j, 13).toString()), // cost
                rvdet.getValueAt(j, 5).toString() // uom    
                );
        list.add(x);
        }      
        return list;        
    }
           
    public boolean validateInput(dbaction x) {
       Map<String,Integer> f = OVData.getTableInfo(new String[]{"recv_mstr","recv_det"});
        int fc;
        
        if (ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddvend.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"rv_packingslip");
        if (tbpackingslip.getText().length() > fc || tbpackingslip.getText().isEmpty()) {
        bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
        tbpackingslip.requestFocus();
        return false;
        } 
                
        fc = checkLength(f,"rvd_lot");
        if (tblot.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        tblot.requestFocus();
        return false;
        } 
        
        fc = checkLength(f,"rvd_serial");
        if (tbserial.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        tbserial.requestFocus();
        return false;
        } 
        
        
        
                if (! x.name().equals("update")) {  // if adjusting fields in the table....this validation is not necessary as they cannot change the PO
                    if (ddpo.getSelectedItem() == null || ddpo.getSelectedItem().toString().isEmpty()) {
                        bsmf.MainFrame.show(getMessageTag(1026));
                        ddpo.requestFocus();
                        return false;
                    }
                }
              
                
                if (! x.name().equals("addItem")) {
                    if (rvdet.getRowCount() <= 0) {
                        bsmf.MainFrame.show(getMessageTag(1089));
                        return false;
                    }
                }
                
                if (x.name().equals("addItem")) {
                    if (tbqty.getText().isEmpty()) {
                        bsmf.MainFrame.show(getMessageTag(1024));
                        tbqty.requestFocus();
                        return false;
                    }
                }
               
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return false;
                }
        return true;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getReceiverBrowseUtil(luinput.getText(),0, "rv_id");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getReceiverBrowseUtil(luinput.getText(),0, "rv_vend");   
        } else if (lurb3.isSelected()) {
         luModel = DTData.getReceiverBrowseUtil(luinput.getText(),0, "rvd_po");   
        } else {
         luModel = DTData.getReceiverBrowseUtil(luinput.getText(),0, "rvd_packingslip");   
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
                getClassLabelTag("lblvend", this.getClass().getSimpleName()),
                getClassLabelTag("lblpo", this.getClass().getSimpleName()),
                getClassLabelTag("lblps", this.getClass().getSimpleName()));
        
    }

    public void updateForm() throws ParseException {
        
        myrecvdetmodel.setRowCount(0);
        ddvend.setSelectedItem(rv.rv_vend());
        dcdate.setDate(parseDate(rv.rv_recvdate()));
        tbpackingslip.setText(rv.rv_packingslip());
        ddsite.setSelectedItem(rv.rv_site());
        tbkey.setText(rv.rv_id());
        
        for (recv_det rvd : rvdlist) {
            myrecvdetmodel.addRow(new Object[]{rvd.rvd_rline(), rvd.rvd_item(), rvd.rvd_po(), 
                rvd.rvd_poline(), 
                bsFormatDouble(rvd.rvd_qty()), 
                rvd.rvd_uom(), 
                bsFormatDouble(rvd.rvd_listprice()),
                bsFormatDouble(rvd.rvd_disc()), 
                bsFormatDouble(rvd.rvd_netprice()), 
                rvd.rvd_loc(), 
                rvd.rvd_wh(),
                rvd.rvd_serial(), 
                rvd.rvd_lot(), 
                bsFormatDouble(rvd.rvd_cost())});
        }
        
        getAttachments(tbkey.getText());
        
        setAction(rv.m()); 
        
        rv = null;
        rvdlist = null;

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
   
    
    // additional functions
     
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < rvdet.getRowCount(); j++) {
            current = Integer.valueOf(rvdet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
   
    public JTable createVoucherTable() {
        javax.swing.table.DefaultTableModel tempmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                new String[]{
                "RecvID", "RecvLine", "Part", "Qty", "Price"
                });
                JTable temptable = new JTable(tempmodel); 
        for (int j = 0; j < rvdet.getRowCount(); j++) {
            tempmodel.addRow(new Object[] {tbkey.getText(), 
            rvdet.getValueAt(j, 0).toString(),
            rvdet.getValueAt(j, 1).toString(),
            rvdet.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'),
            rvdet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.')
                        });
        }
        return temptable;
    }
   
    public fapData.ap_mstr createAPMstr(String vonbr, String[] v) {
        int batchid = OVData.getNextNbr("batch");
        double actamt = 0.00;
        for (int j = 0; j < rvdet.getRowCount(); j++) {
        actamt += bsParseDouble(rvdet.getModel().getValueAt(j,4).toString()) * bsParseDouble(rvdet.getModel().getValueAt(j,8).toString());
        }
        
        fapData.ap_mstr x = new fapData.ap_mstr(null, 
                "", //ap_id
                ddvend.getSelectedItem().toString(), // ap_vend, 
                vonbr, // ap_nbr
                actamt, // ap_amt
                actamt, // ap_base_amt
                setDateDB(dcdate.getDate()), // ap_effdate
                setDateDB(dcdate.getDate()), // ap_entdate
                setDateDB(OVData.getDueDateFromTerms(dcdate.getDate(), v[5])), // ap_duedate         
                "V", // ap_type
                "auto-voucher", //ap_rmks
                tbkey.getText(), //ap_ref
                v[5], //ap_terms
                v[1], //ap_acct
                v[2], //ap_cc
                "0", //ap_applied
                "o", //ap_status
                v[4], //ap_bank
                v[3], //ap_curr
                OVData.getDefaultCurrency(), //ap_base_curr
                vonbr, //ap_check // in this case voucher number is reference field
                String.valueOf(batchid), //ap_batch
                ddsite.getSelectedItem().toString(), //ap_site
                "Receipt",
                "",
                "1",
                "");  
        return x;  
    }
    
    public ArrayList<fapData.vod_mstr> createVodMstr(String vodnbr, String[] v) {
        ArrayList<fapData.vod_mstr> list = new ArrayList<fapData.vod_mstr>();
         for (int j = 0; j < rvdet.getRowCount(); j++) {
             fapData.vod_mstr x = new fapData.vod_mstr(null, 
                vodnbr,
                tbkey.getText(),
                bsParseInt(rvdet.getValueAt(j, 0).toString()),
                rvdet.getValueAt(j, 1).toString(),
                bsParseDouble(rvdet.getValueAt(j, 4).toString()),
                bsParseDouble(rvdet.getValueAt(j, 8).toString()),
                setDateDB(dcdate.getDate()),
                ddvend.getSelectedItem().toString(),
                tbpackingslip.getText(), // ap_check 
                v[1],
                v[2],
                rvdet.getValueAt(j, 2).toString(),
                bsParseInt(rvdet.getValueAt(j, 3).toString()),
                "1"    // auto approved
                );
        list.add(x);
         }
        return list;   
    }
     
    
 /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbpackingslip = new javax.swing.JTextField();
        orddate = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        ddline = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        tbqtyrcvd = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        rvdet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        ddpo = new javax.swing.JComboBox();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        tbline = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        tbqtyord = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        duedate = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        tbserial = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        tblot = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        tbcost = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();
        lblvendpart = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        ddloc = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        cbautovoucher = new javax.swing.JCheckBox();
        btdelete = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        tbuom = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        btgenerate = new javax.swing.JButton();
        lblitem = new javax.swing.JLabel();
        lblmessage = new javax.swing.JLabel();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Receiver Maintenance"));
        panelMain.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Receiver#");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel26.setText("DueDate");
        jLabel26.setName("lblduedate"); // NOI18N

        jLabel30.setText("PO Line");
        jLabel30.setName("lblpoline"); // NOI18N

        jLabel32.setText("Price");
        jLabel32.setName("lblprice"); // NOI18N

        jLabel36.setText("Vendor");
        jLabel36.setName("lblvendor"); // NOI18N

        jLabel38.setText("PO Number");
        jLabel38.setName("lblpo"); // NOI18N

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        ddline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddlineActionPerformed(evt);
            }
        });

        jLabel43.setText("Qty Recvd");
        jLabel43.setName("lblqtyrecv"); // NOI18N

        rvdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(rvdet);

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("PackingSlip");
        jLabel27.setName("lblps"); // NOI18N

        jLabel33.setText("Line");
        jLabel33.setName("lblline"); // NOI18N

        jLabel34.setText("Qty Ordered");
        jLabel34.setName("lblqtyord"); // NOI18N

        jLabel28.setText("OrdDate");
        jLabel28.setName("lblorddate"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel29.setText("Qty");
        jLabel29.setName("lblqty"); // NOI18N

        jLabel31.setText("WareHouse");
        jLabel31.setName("lblwh"); // NOI18N

        jLabel35.setText("DateRecvd");
        jLabel35.setName("lblrecvdate"); // NOI18N

        jLabel37.setText("Site");
        jLabel37.setName("lblsite"); // NOI18N

        jLabel39.setText("Serial");
        jLabel39.setName("lblserial"); // NOI18N

        jLabel40.setText("Lot");
        jLabel40.setName("lbllot"); // NOI18N

        jLabel41.setText("Cost");
        jLabel41.setName("lblcost"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel42.setText("Location");
        jLabel42.setName("lblloc"); // NOI18N

        cbautovoucher.setText("AutoVoucher?");
        cbautovoucher.setName("cbautovoucher"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        jLabel1.setText("uom");
        jLabel1.setName("lbluom"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("Expire");
        jLabel2.setName("lblexpire"); // NOI18N

        btgenerate.setText("generate");
        btgenerate.setName("btgenerate"); // NOI18N
        btgenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgenerateActionPerformed(evt);
            }
        });

        lblmessage.setName("lblmessage"); // NOI18N

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addComponent(jScrollPane7)
                .addContainerGap())
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelMainLayout.createSequentialGroup()
                                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(panelMainLayout.createSequentialGroup()
                                                        .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel33))
                                                    .addGroup(panelMainLayout.createSequentialGroup()
                                                        .addComponent(tbqtyrcvd, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel34))
                                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(tbuom)
                                                    .addComponent(tbqtyord, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                                    .addComponent(tbline)))
                                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                                .addComponent(btgenerate)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel39))
                                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tblot)
                                            .addComponent(tbserial)
                                            .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblvendpart, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelMainLayout.createSequentialGroup()
                                                .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(panelMainLayout.createSequentialGroup()
                                                .addComponent(ddline, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(tbpackingslip, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dcdate, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                                            .addComponent(tbqty)
                                            .addComponent(dcexpire, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelMainLayout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear)
                                        .addGap(30, 30, 30)
                                        .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(78, 78, 78))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                .addComponent(cbautovoucher)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)
                                .addGap(23, 23, 23))))))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24))
                    .addComponent(btlookup)
                    .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear)))
                .addGap(8, 8, 8)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel36)
                                .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel38)
                            .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddline, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel30))
                            .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblvendpart, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpackingslip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29)))
                            .addComponent(jLabel35))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel32))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel43)
                                    .addComponent(tbqtyrcvd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel33))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbqtyord, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbuom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39)
                            .addComponent(btgenerate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tblot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))))
                .addGap(4, 4, 4)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete)
                    .addComponent(cbautovoucher))
                .addContainerGap())
        );

        add(panelMain);

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
        jScrollPane2.setViewportView(tableattachment);

        javax.swing.GroupLayout panelAttachmentLayout = new javax.swing.GroupLayout(panelAttachment);
        panelAttachment.setLayout(panelAttachmentLayout);
        panelAttachmentLayout.setHorizontalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAttachmentLayout.createSequentialGroup()
                        .addComponent(btaddattachment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteattachment)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 446, Short.MAX_VALUE)
                        .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAttachmentLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(157, 157, 157))
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
         newAction("receiver");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
          
          boolean proceed = validateInput(dbaction.addItem); 
          int line = 0;
          line = getmaxline();
          line++;      
       //   "Part", "PO", "line", "Qty", uom,  listprice, disc, netprice, loc, serial, lot, cost
            if (proceed)
            myrecvdetmodel.addRow(new Object[]{line, lblitem.getText(), ddpo.getSelectedItem(), 
                tbline.getText(), tbqty.getText(), tbuom.getText(), tbprice.getText(), "0", 
                tbprice.getText(), ddloc.getSelectedItem().toString(), ddwh.getSelectedItem().toString(), 
                tbserial.getText(), tblot.getText(), tbcost.getText()});
       
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddlineActionPerformed
        if (! isLoad) {
        try {
            String mypart = "";
            String mypo = "";
            String myline = "";
            
                    tbline.setText("");
                    tbprice.setText("");
                    ddwh.setSelectedIndex(0);
                    ddloc.setSelectedIndex(0);
                  //  ddsite.setSelectedIndex(0);
                    lblvendpart.setText("");
                    duedate.setText("");
                    tbqtyrcvd.setText("");
                    tbqtyord.setText("");
                    tbuom.setText("");
                    orddate.setText("");
                    tbqty.setText("0");
                    tbserial.setText("");
                    tblot.setText("");
                    tbcost.setText("");
          
            if (ddline.getItemCount() > 0 && ddpo.getItemCount() > 0) {
                myline = ddline.getSelectedItem().toString();
                mypo = ddpo.getSelectedItem().toString();
            }
           
            
               
           
            
            if (! mypo.toString().isEmpty() || ! myline.toString().isEmpty()  ) {
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                    // at this time you cannot have the same item on the PO more than once
                
                res = st.executeQuery("select itc_total, it_loc, it_wh, pod_nbr, pod_line, pod_item, pod_uom, pod_venditem, pod_netprice, pod_rcvd_qty, pod_ord_qty, pod_ord_date, pod_due_date, pod_status, pod_site from pod_mstr " +
                       " inner join po_mstr on po_nbr = pod_nbr " +
                       " left outer join item_mstr on it_item = pod_item " +
                       " left outer join item_cost on itc_item = pod_item and itc_set = 'standard' and itc_site = po_site " + 
                       " where " +
                       " pod_nbr = " + "'" + mypo + "'" + 
                       " AND pod_line = " + "'" + myline + "'" +
                       ";");
                while (res.next()) {
                    lblitem.setText(res.getString("pod_item"));
                    tbline.setText(res.getString("pod_line"));
                    tbprice.setText(res.getString("pod_netprice"));
                    if (res.getString("itc_total") != null)
                    tbcost.setText(res.getString("itc_total"));
                  //  if (res.getString("it_loc") != null)
                  //  ddloc.setSelectedItem(res.getString("it_loc"));
                  //  if (res.getString("it_wh") != null)
                  //  ddwh.setSelectedItem(res.getString("it_wh"));
                  //  ddsite.setSelectedItem(res.getString("pod_site"));
                    lblvendpart.setText(res.getString("pod_venditem"));
                    duedate.setText(res.getString("pod_due_date"));
                    tbqtyrcvd.setText(res.getString("pod_rcvd_qty"));
                    tbqtyord.setText(res.getString("pod_ord_qty"));
                    tbuom.setText(res.getString("pod_uom"));
                    orddate.setText(res.getString("pod_ord_date"));
                }
                if (tbcost.getText().isEmpty()) {
                    tbcost.setText("0");
                }
             res.close();
            } catch (SQLException s) {
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
            };
            } // if mypart and mypo
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_ddlineActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
      if (! isLoad) {
        ddpo.removeAllItems();
        ddline.removeAllItems();
       if (ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty()) {
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

                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbvendor.setText(res.getString("vd_name"));
                }
                res = st.executeQuery("select po_nbr from po_mstr where po_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + 
                        " AND po_status <> " + "'" + getGlobalProgTag("closed") + "'" + 
                        " AND po_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                        ";");
                while (res.next()) {
                    ddpo.addItem(res.getString("po_nbr"));
                }
                ddpo.insertItemAt("", 0);
                ddpo.setSelectedIndex(0);
                res.close();
            } catch (SQLException s) {
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
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       }
      } // not isLoad
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = rvdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) rvdet.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       if (! isLoad) {
       ddline.removeAllItems();
       
       String mypo = "";
            if (ddpo.getItemCount() > 0) {
                mypo = ddpo.getSelectedItem().toString();
            }
            if (! mypo.isEmpty()) {
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
                    // at this time you cannot have the same item on the PO more than once
                res = st.executeQuery("select pod_line, pod_item, pod_site from pod_mstr " +
                       " inner join po_mstr on po_nbr = pod_nbr where pod_nbr = " + "'" + mypo + "'" + ";");
                while (res.next()) {
                   ddline.addItem(res.getString("pod_line"));
                   // ddsite.setSelectedItem(res.getString("pod_site"));
                }
                res.close();
            } catch (SQLException s) {
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
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       } // if mypo is not empty 
       }
    }//GEN-LAST:event_ddpoActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
        if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        if (! isLoad) {
        String x = BlueSeerUtils.bsformat("", tbqty.getText(), "5");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0") && ! isLoad) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btgenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgenerateActionPerformed
        tbserial.setText(String.valueOf(OVData.getNextNbr("serial")));
    }//GEN-LAST:event_btgenerateActionPerformed

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

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        if (! isLoad && ddsite.getSelectedItem() != null) {
            if (ddvend.getSelectedItem() != null && ddvend.getItemCount() > 0) {
                ddvend.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_ddsiteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btgenerate;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbautovoucher;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcexpire;
    private javax.swing.JComboBox ddline;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox ddpo;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JTextField duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
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
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lblitem;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JLabel lblvendpart;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTextField orddate;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JPanel panelMain;
    private javax.swing.JTable rvdet;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tblot;
    private javax.swing.JTextField tbpackingslip;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbqtyord;
    private javax.swing.JTextField tbqtyrcvd;
    private javax.swing.JTextField tbserial;
    private javax.swing.JTextField tbuom;
    // End of variables declaration//GEN-END:variables
}
