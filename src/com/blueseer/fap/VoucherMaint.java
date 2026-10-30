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
package com.blueseer.fap;

import com.blueseer.utl.OVData;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.fap.fapData.VoucherAP;
import static com.blueseer.fap.fapData.VoucherTransaction;
import static com.blueseer.fap.fapData.approveAPVoucher;
import static com.blueseer.fap.fapData.getAPVoucherSet;
import static com.blueseer.fap.fapData.getPOsummaryChargesTaxes;
import static com.blueseer.fap.fapData.updateAPVoucherStatus;
import com.blueseer.fap.fapData.vod_mstr;

import com.blueseer.fgl.fglData;
import static com.blueseer.pur.purData.getPOListByVend;
import com.blueseer.pur.purData.po_mstr;
import com.blueseer.rcv.rcvData;
import com.blueseer.rcv.rcvData.Receiver;
import static com.blueseer.rcv.rcvData.getReceiverMstrSet;
import static com.blueseer.rcv.rcvData.getReceiversFromPO;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
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
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerV;
import static com.blueseer.utl.OVData.getTermsResults;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static com.blueseer.vdr.venData.getVendMstr;
import com.blueseer.vdr.venData.vd_mstr;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.event.TableModelEvent;


/**
 *
 * @author vaughnte
 */
public class VoucherMaint extends javax.swing.JPanel implements IBlueSeerV {

                boolean isLoad = false;
                
                double actamt = 0;
                double control = 0.00;
                double baseamt = 0;
                double rcvamt = 0;
                int voucherline = 0;
                boolean canUpdate = false;
                boolean isAutoPost = false;
                ArrayList<String[]> initDataSets = null;
                String defaultSite = "";
                String defaultCurrency = "";
                String defaultCC = "";
                public static vd_mstr vd = null;
                public static po_mstr po = null;
                VoucherAP vapset = null;
                
                
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
                
        
                
                
                 javax.swing.table.DefaultTableModel receivermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("lot"), 
                getGlobalColumnTag("receiver"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("costcenter")
            });
    javax.swing.table.DefaultTableModel vouchermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("receiver"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("account"), 
                getGlobalColumnTag("costcenter")
            });
 javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && 
                                (tme.getColumn() == 4 || tme.getColumn() == 3)) {
                            sumdollars();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };            
                 
                
    /**
     * Creates new form ShipMaintPanel
     */
    public VoucherMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    
    public void setComponentDefaultValues(boolean init) {
        
        
        isLoad = true; 
       
        if (init) {
          initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "depts,vendors");
        }
       
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", jPanel1);
       jTabbedPane1.add("Attachments", panelAttachment);
       
       attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
       
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        
               
        dcdate.setDate(now);
         
         actamt = 0;
         rcvamt = 0;
         tbkey.setText("");
         tbinvoice.setText("");
        tbrmks.setText("");
        cbapproved.setSelected(false);
        tbrecvamt.setText("");
        
        tbqty.setText("");
        lbvendor.setText("");
        lbacct.setText("");
        tbprice.setDisabledTextColor(Color.black);
        tbprice.setText("");
        
        tbcontrolamt.setText("0");
        tbcontrolamt.setBackground(Color.white);
        tbactualamt.setText("0");
        tbactualamt.setBackground(Color.white);
        tbactualamt.setEditable(false);
        
        tbtax.setEditable(false);
        tbtax.setBackground(bsmf.MainFrame.nonEditableColor);
        
        tbservice.setEditable(false);
        tbservice.setBackground(bsmf.MainFrame.nonEditableColor);
        
        lblstatus.setText("");
        lblstatus.setForeground(Color.black);
        
        ddsite.setForeground(Color.black);
        ddpo.removeAllItems();
        ddreceiver.removeAllItems();
       
         receivermodel.setRowCount(0);
        vouchermodel.setRowCount(0);
        vouchermodel.addTableModelListener(ml);
        receiverdet.setModel(receivermodel);
        voucherdet.setModel(vouchermodel);
        receiverdet.getTableHeader().setReorderingAllowed(false);
        voucherdet.getTableHeader().setReorderingAllowed(false);
        
        
        tbacct.setText("");
        
        ddcc.removeAllItems();
        ddvend.removeAllItems();
        ddsite.removeAllItems();
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            
            if (s[0].equals("autopost")) {
              isAutoPost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            
            if (s[0].equals("cc")) {
              defaultCC = s[1];  
            }
            
            if (s[0].equals("vendors")) {
              ddvend.addItem(s[1]);  
            }
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]);  
            }
            
            if (s[0].equals("depts")) {
              ddcc.addItem(s[1]);  
            }
           
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
        }
        
        ddvend.insertItemAt("", 0);
        ddvend.setSelectedIndex(0);
        ddcc.setSelectedItem(defaultCC);
        ddsite.setSelectedItem(defaultSite);
        
        ddstatus.removeAllItems();
        ddstatus.addItem(getGlobalProgTag("open"));
        ddstatus.addItem(getGlobalProgTag("closed"));
        ddstatus.addItem(getGlobalProgTag("void"));
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
        
       isLoad = false;
    }
         
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues(false);
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btnew.setEnabled(false);
        btvoid.setEnabled(false);
        btupdate.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
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
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                    tbactualamt.setText(currformatDouble(actamt));
                   tbcontrolamt.setText(currformatDouble(actamt));
                   control = actamt;
                   String status = fapData.getVoucherStatus(tbkey.getText());
                   if (status.equals("x")) {
                       lblstatus.setText(getMessageTag(1083));
                       lblstatus.setForeground(Color.blue);
                       btvoid.setEnabled(false);
                       btadd.setEnabled(false);
                       btupdate.setEnabled(false);
                   }
                   if (status.equals("c")) {
                       lblstatus.setText(getMessageTag(1097));
                       lblstatus.setForeground(Color.blue);
                       btvoid.setEnabled(false);
                       btadd.setEnabled(false);
                       btupdate.setEnabled(false);
                   }
                   if (status.equals("o") || status.isBlank()) {
                       lblstatus.setText("");
                       lblstatus.setForeground(Color.black);
                   }
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
       
    }
    
    public boolean validateInput(BlueSeerUtils.dbaction x) {
       
        if (! canUpdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        boolean b = true;
                
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "ID"));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Vendor"));
                    ddvend.requestFocus();
                    return b;
                }
                
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Site"));
                    ddsite.requestFocus();
                    return b;
                }
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return b;
                }
                
                
                 if ( ! currformatDouble(control).equals(currformatDouble(actamt)) || control == 0.00 || actamt == 0.00 ) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1039,String.valueOf(control + " / " + actamt)));
                    return b;
                }
                
                
                
               
        return b;
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
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues(initDataSets == null);
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
    
    
    public String[] addRecord(String[] x) {
     String[] m = VoucherTransaction(ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), false);
      // autopost
        if (isAutoPost) {
            fglData.PostGL();
        } 
     return m;
     }
    
    public String[] updateRecord(String[] x) {
    approveAPVoucher(x[0], BlueSeerUtils.boolToString(cbapproved.isSelected()));
    return new String[]{"0", "status updated"};  // no update available...only add and void (reverse)
    }
     
    public String[] deleteRecord(String[] x) {
     // same function used for add...but with 'true' for void as last parameter
     String[] m = VoucherTransaction(ddtype.getSelectedItem().toString() , createDetRecord(), createRecord(), true);
     updateAPVoucherStatus(tbkey.getText(), "x");
     return m;
     }
      
    public String[] getRecord(String[] x) {
       vapset = getAPVoucherSet(x);
       return vapset.m();
    }
    
    public fapData.ap_mstr createRecord() {
        int batchid = OVData.getNextNbr("batch");
        String[] tr = getTermsResults(dcdate.getDate(), vd.vd_terms());
        String po = "";
        for (int j = 0; j < voucherdet.getRowCount(); j++) {
            po = voucherdet.getValueAt(j, 0).toString(); // assumes one PO on invoice to be vouched
            break;
        }
        // add SAC charges or discounts
             //   String[] d = getPOsummaryChargesTaxes(po); // summary = grossamt, taxamt, sacamt ...initialized as 0,0,0
             //   actamt +=  bsParseDouble(d[1]) + bsParseDouble(d[2]);
       
        fapData.ap_mstr x = new fapData.ap_mstr(null, 
                "", //ap_id
                ddvend.getSelectedItem().toString(), // ap_vend, 
                tbkey.getText(), // ap_nbr
                actamt, // ap_amt
                actamt, // ap_base_amt
                setDateDB(dcdate.getDate()), // ap_effdate
                setDateDB(dcdate.getDate()), // ap_entdate
                setDateDB(parseDate(tr[0])), // ap_duedate         
                "V", // ap_type
                tbrmks.getText(), //ap_rmks
                tbinvoice.getText(), //ap_ref
                vd.vd_terms(), //ap_terms
                vd.vd_ap_acct(), //ap_acct
                vd.vd_ap_cc(), //ap_cc
                "0", //ap_applied
                "o", //ap_status
                vd.vd_bank(), //ap_bank
                vd.vd_curr(), //ap_curr
                defaultCurrency, //ap_base_curr
                tbkey.getText(), //ap_check // in this case voucher number is reference field
                String.valueOf(batchid), //ap_batch
                ddsite.getSelectedItem().toString(), //ap_site
                ddtype.getSelectedItem().toString(),
                "",
                "1",
                bsmf.MainFrame.userid,
                bsParseDouble(tbtax.getText()),
                bsParseDouble(tbservice.getText()),
                "" // ap_paymethod
                );   
        return x;  
    }
    
    public ArrayList<fapData.vod_mstr> createDetRecord() {
        ArrayList<fapData.vod_mstr> list = new ArrayList<fapData.vod_mstr>();
         for (int j = 0; j < voucherdet.getRowCount(); j++) {
             fapData.vod_mstr x = new fapData.vod_mstr(null, 
                tbkey.getText(),
                voucherdet.getValueAt(j, 5).toString(),
                bsParseInt(voucherdet.getValueAt(j, 6).toString()),
                voucherdet.getValueAt(j, 2).toString(),
                bsParseDouble(voucherdet.getValueAt(j, 3).toString().replace(defaultDecimalSeparator, '.')),
                bsParseDouble(voucherdet.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.')),
                setDateDB(dcdate.getDate()),
                ddvend.getSelectedItem().toString(),
                tbinvoice.getText(), // ap_check 
                voucherdet.getValueAt(j, 7).toString(),
                voucherdet.getValueAt(j, 8).toString(),
                voucherdet.getValueAt(j, 0).toString(),
                bsParseInt(voucherdet.getValueAt(j, 1).toString()),
                "1"    // auto approved
                );
        list.add(x);
         }
        return list;   
    }
        
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "ap_nbr");
        } else {
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "ap_vend");   
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
                getClassLabelTag("lblvendor", this.getClass().getSimpleName())); 
        
        
    }

    public void lookUpFrameAcctDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_id");
        } else {
         luModel = DTData.getAcctBrowseUtil(luinput.getText(), 0, "ac_desc");   
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
                    tbacct.setText(target.getValueAt(row,1).toString());
                    lbacct.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
         
        callDialog(getGlobalColumnTag("id"), 
                getGlobalColumnTag("description")); 
        
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
   
    public void updateForm() {
        tbkey.setText(vapset.ap().ap_nbr());
         dcdate.setDate(parseDate(vapset.ap().ap_effdate()));
         tbinvoice.setText(vapset.ap().ap_ref());
         tbrmks.setText(vapset.ap().ap_rmks());
         ddvend.setSelectedItem(vapset.ap().ap_vend());
         ddsite.setSelectedItem(vapset.ap().ap_site());
         ddtype.setSelectedItem(vapset.ap().ap_subtype());
         cbapproved.setSelected(BlueSeerUtils.ConvertStringToBool(vapset.ap().ap_approved()));
         if (vapset.ap().ap_status().equals("c")) { 
         ddstatus.setSelectedItem(getGlobalProgTag("closed"));
         }
         if (vapset.ap().ap_status().equals("o")) { 
         ddstatus.setSelectedItem(getGlobalProgTag("open"));
         }
         if (vapset.ap().ap_status().equals("x")) { 
         ddstatus.setSelectedItem(getGlobalProgTag("void"));
         }
                     actamt = 0.00;
                     for (vod_mstr vod : vapset.vod()) {
                     vouchermodel.addRow(new Object[] { vod.vod_id(),
                                              vod.vod_rvdline(),
                                              vod.vod_item(),
                                              vod.vod_qty(),
                                              vod.vod_voprice(),
                                              vod.vod_rvdid(),
                                              vod.vod_rvdline(),
                                              vod.vod_expense_acct(),
                                              vod.vod_expense_cc()
                                              });
                         actamt += (vod.vod_qty() * vod.vod_voprice());
                     }
                     
                  getAttachments(tbkey.getText());
                  
                  setAction(vapset.m());
    }
    
    // misc functions
    
    public void sumdollars() {
        double dol = 0;
        double summaryTaxPercent = 0;
        double headertax = 0;
        double matltax = 0;
        double totaltax = 0;
        
        actamt = 0;
         for (int j = 0; j < voucherdet.getRowCount(); j++) {
             actamt += bsParseDouble(voucherdet.getModel().getValueAt(j,3).toString()) * bsParseDouble(voucherdet.getModel().getValueAt(j,4).toString());
         }
         
         if (! tbtax.getText().isBlank()) {
             actamt += bsParseDouble(tbtax.getText());
         }
         if (! tbservice.getText().isBlank()) {
             actamt += bsParseDouble(tbservice.getText());
         }
          
         if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
        tbactualamt.setText(currformatDouble(actamt));
        
    }
          
    public void setType(String type) {
          if (type.equals("Receipt")) {
              ddpo.setEnabled(true);
              ddreceiver.setEnabled(true);
              tbitemservice.setEnabled(false);
              tbprice.setEnabled(false);
              btaddall.setEnabled(true);
              tbrecvamt.setEnabled(true);
          } else {
              btaddall.setEnabled(false);
              tbrecvamt.setEnabled(false);
              ddpo.setEnabled(false);
              ddreceiver.setEnabled(false);
              tbitemservice.setEnabled(true);
              tbprice.setEnabled(true);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        lblreceiver = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        voucherdet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        ddreceiver = new javax.swing.JComboBox();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        receiverdet = new javax.swing.JTable();
        ddpo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tbinvoice = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        btaddall = new javax.swing.JButton();
        tbrecvamt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        lbacct = new javax.swing.JLabel();
        btvoid = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        ddcc = new javax.swing.JComboBox<>();
        ddstatus = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tbcheck = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cbapproved = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        tbtax = new javax.swing.JTextField();
        tbservice = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btLookUpAccount = new javax.swing.JButton();
        tbacct = new javax.swing.JTextField();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Voucher Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Voucher Nbr");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        tbcontrolamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusLost(evt);
            }
        });

        jLabel36.setText("Vendor");
        jLabel36.setName("lblvendor"); // NOI18N

        lblreceiver.setText("Receivers");
        lblreceiver.setName("lblreceivers"); // NOI18N

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

        voucherdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(voucherdet);

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

        ddreceiver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddreceiverActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Control Amt");
        jLabel27.setName("lblcontrol"); // NOI18N

        jLabel35.setText("VoucherDate");
        jLabel35.setName("lblvoucherdate"); // NOI18N

        receiverdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(receiverdet);

        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        jLabel1.setText("PO");
        jLabel1.setName("lblpo"); // NOI18N

        jLabel2.setText("Invoice");
        jLabel2.setName("lblinvoice"); // NOI18N

        jLabel28.setText("Actual Amt");
        jLabel28.setName("lblactual"); // NOI18N

        btaddall.setText("Add All");
        btaddall.setName("btaddall"); // NOI18N
        btaddall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddallActionPerformed(evt);
            }
        });

        jLabel3.setText("Receiver Total");
        jLabel3.setName("lblreceivertotal"); // NOI18N

        jLabel4.setText("Rmks");
        jLabel4.setName("lblremarks"); // NOI18N

        jLabel5.setText("Item/Service");
        jLabel5.setName("lblitem"); // NOI18N

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel6.setText("Price");
        jLabel6.setName("lblprice"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Receipt", "Expense" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        jLabel37.setText("Type");
        jLabel37.setName("lbltype"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Qty");
        jLabel7.setName("lblqty"); // NOI18N

        jLabel8.setText("CC");
        jLabel8.setName("lblcc"); // NOI18N

        jLabel9.setText("Expense Account");
        jLabel9.setName("lblacct"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");
        jLabel10.setName("lblsite"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btvoid.setText("Void");
        btvoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvoidActionPerformed(evt);
            }
        });

        jLabel11.setText("Status");
        jLabel11.setName("lblstatus"); // NOI18N

        jLabel12.setText("Check");
        jLabel12.setName("lblcheck"); // NOI18N

        cbapproved.setText("Approve");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel13.setText("Tax");

        jLabel14.setText("Service Charge");

        btLookUpAccount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAccountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(559, 559, 559)
                                    .addComponent(jLabel35)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel24)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(13, 13, 13)
                                    .addComponent(btnew)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btclear)
                                    .addGap(77, 77, 77)
                                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(29, 29, 29)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btvoid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel9))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel6))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btLookUpAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(28, 28, 28)
                                    .addComponent(btadditem)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btdeleteitem))
                                .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel37)
                                .addComponent(jLabel36)
                                .addComponent(jLabel11)
                                .addComponent(jLabel13))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(jLabel27)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblreceiver)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(cbapproved)
                                                .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(18, 18, 18)
                                            .addComponent(jLabel28)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(5, 5, 5))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(ddstatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, 0, 117, Short.MAX_VALUE))
                                        .addComponent(tbtax, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(0, 0, 0)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(tbservice, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(btaddall))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(0, 0, Short.MAX_VALUE)))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 889, Short.MAX_VALUE)
                            .addComponent(jScrollPane7))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24)
                                .addComponent(btclear))
                            .addComponent(btlookup))
                        .addGap(1, 1, 1)))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cbapproved))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(lblreceiver)
                        .addComponent(jLabel36))
                    .addComponent(lbvendor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel35)
                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddall)
                    .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btdeleteitem)
                            .addComponent(btadditem)
                            .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9)
                                .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(btLookUpAccount))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btvoid)
                    .addComponent(btupdate))
                .addGap(0, 0, 0))
        );

        add(jPanel1);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 439, Short.MAX_VALUE)
                        .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("voucher"); 
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
     
        if (! isLoad && ddtype.getSelectedItem().toString().equals(("Expense")) && tbacct.getText().isBlank()) {
            bsmf.MainFrame.show("Expense Account must be assigned for Expense Type Voucher");
            tbacct.requestFocus();
            return;
        }
       // receiverdet  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
        if (ddtype.getSelectedItem().toString().equals(("Receipt"))) {
        int[] rows = receiverdet.getSelectedRows();
            for (int i : rows) {
                actamt += bsParseDouble(receiverdet.getModel().getValueAt(i,3).toString()) * 
                          bsParseDouble(receiverdet.getModel().getValueAt(i,6).toString());

               vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                                  receiverdet.getModel().getValueAt(i, 2),
                                                  receiverdet.getModel().getValueAt(i, 0),
                                                  receiverdet.getModel().getValueAt(i, 3),
                                                  receiverdet.getModel().getValueAt(i, 6),
                                                  receiverdet.getModel().getValueAt(i, 10),
                                                  receiverdet.getModel().getValueAt(i, 11),
                                                  receiverdet.getModel().getValueAt(i, 12),
                                                  receiverdet.getModel().getValueAt(i, 13)
                                                  });
            }
        } else {
            voucherline++;
            actamt += bsParseDouble(tbqty.getText()) * 
                          bsParseDouble(tbprice.getText());
            vouchermodel.addRow(new Object[] { "", voucherline,
                                                  tbitemservice.getText(),
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  "Expense",
                                                  voucherline,
                                                  tbacct.getText(),
                                                  ddcc.getSelectedItem().toString()
                                                  });
        }
        
        sumdollars();
        
        tbitemservice.setText("");
        tbqty.setText("");
        tbprice.setText("");
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
       
      if (! isLoad && ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty()) { 
           ddreceiver.removeAllItems();
           ddpo.removeAllItems();
           receivermodel.setRowCount(0);
        
        
            vd = getVendMstr(new String[]{ddvend.getSelectedItem().toString()});
            ArrayList<String> polist = getPOListByVend(ddvend.getSelectedItem().toString());
            isLoad = true;
            for (String po : polist) {
                ddpo.addItem(po);
            }
            ddpo.insertItemAt("", 0);
            ddpo.setSelectedIndex(0);
            isLoad = false;
           
      }
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = voucherdet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
             actamt -= bsParseDouble(voucherdet.getModel().getValueAt(i,3).toString()) * bsParseDouble(voucherdet.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) voucherdet.getModel()).removeRow(i);
           voucherline--;
        }
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void ddreceiverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddreceiverActionPerformed
        if ( ddreceiver.getItemCount() != 0 && ! ddreceiver.getSelectedItem().toString().isEmpty()) {
            receivermodel.setRowCount(0);
            rcvamt = 0;
            
            Receiver rcvset = getReceiverMstrSet(new String[]{ddreceiver.getSelectedItem().toString()});
            for (rcvData.recv_det rvd : rcvset.rvd()) {
               receivermodel.addRow(new Object[]{rvd.rvd_item(), 
                   rvd.rvd_po(),
                   rvd.rvd_poline(), 
                   bsFormatDouble((rvd.rvd_qty() - rvd.rvd_voqty())), 
                   rvd.rvd_listprice(),
                   rvd.rvd_disc(), 
                   rvd.rvd_netprice(), 
                   rvd.rvd_loc(),
                   rvd.rvd_serial(), 
                   rvd.rvd_lot(), 
                   rvd.rvd_id(), 
                   rvd.rvd_rline(),
                   rcvset.rv().rv_ap_acct(), 
                   rcvset.rv().rv_ap_cc()});
               
                  rcvamt += rvd.rvd_netprice() * rvd.rvd_qty(); 
            }
            tbrecvamt.setText(currformatDouble(rcvamt).replace('.',defaultDecimalSeparator));
                
            
        }
       
    }//GEN-LAST:event_ddreceiverActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       if (! isLoad && ddpo.getSelectedItem() != null && ! ddpo.getSelectedItem().toString().isBlank()) {
           
           // purchaseOrder poset = getPOMstrSet(new String[]{ddpo.getSelectedItem().toString()});
            String[] d = getPOsummaryChargesTaxes(ddpo.getSelectedItem().toString()); // summary = grossamt, taxamt, sacamt ...initialized as 0,0,0
            tbtax.setText(currformatDouble(bsParseDouble(d[1])));
            tbservice.setText(currformatDouble(bsParseDouble(d[2])));
            
            // get receivers
            ddreceiver.removeAllItems();
            receivermodel.setRowCount(0);
            ArrayList<String> list = getReceiversFromPO(ddpo.getSelectedItem().toString(), "0");
            isLoad = true;
            for (String s : list) {
               ddreceiver.addItem(s); 
            }            
            ddreceiver.insertItemAt("", 0);
            ddreceiver.setSelectedIndex(0);
            isLoad = false;
            
       }
   
    }//GEN-LAST:event_ddpoActionPerformed

    private void btaddallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddallActionPerformed
         for (int i = 0; i < receiverdet.getRowCount(); i++) {
            actamt += bsParseDouble(receiverdet.getModel().getValueAt(i,3).toString()) * bsParseDouble(receiverdet.getModel().getValueAt(i,6).toString());
            
           vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                              receiverdet.getModel().getValueAt(i, 2),
                                              receiverdet.getModel().getValueAt(i, 0),
                                              receiverdet.getModel().getValueAt(i, 3),
                                              receiverdet.getModel().getValueAt(i, 6),
                                              receiverdet.getModel().getValueAt(i, 10),
                                              receiverdet.getModel().getValueAt(i, 11),
                                              receiverdet.getModel().getValueAt(i, 12),
                                              receiverdet.getModel().getValueAt(i, 13)
                                              });
        }
         if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
        tbactualamt.setText(currformatDouble(actamt));
    }//GEN-LAST:event_btaddallActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
       setType(ddtype.getSelectedItem().toString());
    }//GEN-LAST:event_ddtypeActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
         if (! btadd.isEnabled())
        executeTask(BlueSeerUtils.dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
         BlueSeerUtils.messagereset();
         initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
        if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
                  String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
                 String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0")) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void btvoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvoidActionPerformed
         if (! validateInput(BlueSeerUtils.dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btvoidActionPerformed

    private void tbcontrolamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcontrolamtFocusLost
          String x = BlueSeerUtils.bsformat("", tbcontrolamt.getText(), "2");
        if (x.equals("error")) {
            tbcontrolamt.setText("");
            tbcontrolamt.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbcontrolamt.requestFocus();
        } else {
            tbcontrolamt.setText(x);
            tbcontrolamt.setBackground(Color.white);
        }
        
        if (! tbcontrolamt.getText().isEmpty()) {
            control = bsParseDouble(tbcontrolamt.getText());
        } else {
            tbcontrolamt.setText("0.00");
            control = 0.00;
        }
        
       if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbcontrolamtFocusLost

    private void tbcontrolamtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcontrolamtFocusGained
       if (tbcontrolamt.getText().equals("0")) {
            tbcontrolamt.setText("");
        }
    }//GEN-LAST:event_tbcontrolamtFocusGained

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
            OVData.openFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), tableattachment.getValueAt(row, 1).toString() );
        }
    }//GEN-LAST:event_tableattachmentMouseClicked

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput(BlueSeerUtils.dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btLookUpAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAccountActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpAccountActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpAccount;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddall;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btvoid;
    private javax.swing.JCheckBox cbapproved;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddpo;
    private javax.swing.JComboBox ddreceiver;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> ddstatus;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lblreceiver;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JTable receiverdet;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcheck;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbinvoice;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrecvamt;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTextField tbservice;
    private javax.swing.JTextField tbtax;
    private javax.swing.JTable voucherdet;
    // End of variables declaration//GEN-END:variables
}
