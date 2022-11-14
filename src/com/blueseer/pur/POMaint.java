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
package com.blueseer.pur;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.adm.admData.site_mstr;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import com.blueseer.ord.ordData;
import static com.blueseer.pur.purData.addPOAddr;
import static com.blueseer.pur.purData.addPOTransaction;
import static com.blueseer.pur.purData.deletePOAddr;
import static com.blueseer.pur.purData.getPOAddr;
import static com.blueseer.pur.purData.getPODet;
import static com.blueseer.pur.purData.getPOLines;
import static com.blueseer.pur.purData.getPOMstr;
import static com.blueseer.pur.purData.getPOMstrSet;
import com.blueseer.pur.purData.po_addr;
import com.blueseer.pur.purData.po_mstr;
import com.blueseer.pur.purData.pod_mstr;
import com.blueseer.pur.purData.purchaseOrder;
import static com.blueseer.pur.purData.updatePOAddr;
import static com.blueseer.pur.purData.updatePOTransaction;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsdate;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleWithSymbol;
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
import static com.blueseer.utl.BlueSeerUtils.priceformat;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import com.blueseer.vdr.venData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;


/**
 *
 * @author vaughnte
 */
public class POMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
     boolean editmode = false;
     boolean isLoad = false;
     String curr = "";
     String basecurr = OVData.getDefaultCurrency();
     String terms = "";
     String acct = "";
     String cc = "";
     String blanket = "";
     String status = "";
     boolean venditemonly = true;  
     public static po_mstr po = null;
     public static po_addr poaddr = null;
     public static ArrayList<pod_mstr> podlist = null;
   
     
     // global datatablemodel declarations  
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("venditem"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("uom"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("recvqty"), 
                getGlobalColumnTag("status")
            })
    {
        boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, true, false, true, false, false, false, false}; 
            return canEdit[columnIndex];
        }
    };
    
    
     javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 4 || tme.getColumn() == 6 )) {
                            retotal();
                            refreshDisplayTotals();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };  
      
    
    public POMaint() {
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
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
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
        
        ArrayList<String[]> initDataSets = purData.getPurchaseOrderInit();
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        jTabbedPane1.add("ShipTo", panelShipto);
        
        
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(0, true);
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        
        clearShipTo();
        tbshipcode.setEnabled(false);
                
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        
        listprice.setText("0");
        netprice.setText("0");
        netprice.setEditable(false);
        qtyshipped.setText("0");
        discount.setText("0");
        
        lbvend.setText("");
       
        userid.setText(bsmf.MainFrame.userid);
        duedate.setDate(now);
        orddate.setDate(now);
        
        orddate.setEnabled(false);
       
        
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        myorddetmodel.addTableModelListener(ml);
        
        
        lblcurr.setText("");
        remarks.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        lbltotdollars.setText("0.00");
        lbltotdollars.setForeground(Color.blue);
        totlines.setText("");
        vendnumber.setText("");
        tbbuyer.setText("");
        cbblanket.setSelected(true);
        cbconfirm.setSelected(false);
        
        ddpart.setForeground(Color.black);
        vendnumber.setForeground(Color.black);
        vendnumber.setEditable(false);
        
        ddsite.removeAllItems();
        ddshipvia.removeAllItems();
        ddcurr.removeAllItems();
        dduom.removeAllItems();
        ddvend.removeAllItems();
        ddstatus.removeAllItems();
        
        String defaultsite = null;
        
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              basecurr = s[1];  
            }
          
            if (s[0].equals("venditemonly")) {
              venditemonly = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("currencies")) {
              ddcurr.addItem(s[1]); 
            }
            if (s[0].equals("uoms")) {
              dduom.addItem(s[1]); 
            }
            if (s[0].equals("vendors")) {
              ddvend.addItem(s[1]); 
            }
            if (s[0].equals("carriers")) {
              ddshipvia.addItem(s[1]); 
            }
            if (s[0].equals("statuses")) {
              ddstatus.addItem(s[1]); 
            }
            if (s[0].equals("states")) {
              ddshipstate.addItem(s[1]); 
            }
            if (s[0].equals("items")) {
              ddpart.addItem(s[1]); 
            }
            
        }
        
        ddsite.setSelectedItem(defaultsite);
        ddcurr.insertItemAt("", 0);
        ddcurr.setSelectedIndex(0);
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        ddvend.insertItemAt("", 0);
        ddvend.setSelectedIndex(0);
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        ddtype.setSelectedIndex(0); // set outside isLoad for initial assignment of ship to address
        ddedistatus.setSelectedIndex(0);
        if (ddshipstate.getItemCount() > 0) {
           ddshipstate.setSelectedIndex(0); 
        }
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
        
        isLoad = false;
        
        
        
    }
    
    public void initvars(String[] arg) {
       
       
       setPanelComponentState(panelMain, false); 
       setPanelComponentState(panelDetail, false); 
       setPanelComponentState(panelShipto, false); 
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
  
    public void newAction(String x) {
        setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btpoprint.setEnabled(false);
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
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   ddvend.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                    refreshDisplayTotals();
                    
                    if (ddstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("closed")) == 0) {
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btclear.setEnabled(true);
                             btpoprint.setEnabled(true);
                             btupdate.setEnabled(false);
                             btdelete.setEnabled(false);
                    } 
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
        tbshipcode.setEnabled(false);
    }
        
    public String[] addRecord(String[] x) {
     String[] m = addPOTransaction(createDetRecord(), createPOAddr(), createRecord());
     return m;
     } 
      
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
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
 
                        // if this PO has 'any' line items already received then bale...cannot delete
                        res = st.executeQuery("select pod_nbr from pod_mstr where pod_nbr = " + "'" + x[0] + "'" + 
                                              " and pod_rcvd_qty > 0 " + ";");
                        int z = 0;
                        while (res.next()) {
                            z++;
                        }
                        if (z > 0) {
                           return m = new String[] {"1","cannot delete PO...some lines already received"};
                        }
                        
                        
                        
                        st.executeUpdate("delete from pod_mstr where pod_nbr = " + "'" + tbkey.getText() + "'" + ";");   
                        int i = st.executeUpdate("delete from po_mstr where po_nbr = " + "'" + tbkey.getText() + "'" + ";");
                            if (i > 0) {
                                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
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
                }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled};  
        }
           return m;
    }
    
    public String[] updateRecord(String[] x) {
      String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getPOLines(tbkey.getText());
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
        m = updatePOTransaction(tbkey.getText(), badlines, createDetRecord(), createPOAddr(), createRecord());
     return m;
    }
         
    public String[] getRecord(String[] key) {
      purchaseOrder z = getPOMstrSet(key);
      po = z.po();
      podlist = z.pod();
      poaddr = z.poa();
      return z.m();
    }
    
    public po_mstr createRecord() {
        po_mstr x = new po_mstr(null, tbkey.getText().toString(),
                        ddvend.getSelectedItem().toString(),
                bsdate.format(orddate.getDate()).toString(),
                bsdate.format(duedate.getDate()).toString(),
                remarks.getText(),
                ddshipvia.getSelectedItem().toString(), // shipvia
                ddstatus.getSelectedItem().toString(),
                userid.getText(),
                ddtype.getSelectedItem().toString(), // type
                curr,
                terms,
                ddsite.getSelectedItem().toString(),
                tbbuyer.getText(),  
                acct,
                cc, 
                tbshipcode.getText(), 
                ddedistatus.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbconfirm.isSelected()))
        );
        return x;  
    }
    
    public ArrayList<pod_mstr> createDetRecord() {
        ArrayList<pod_mstr> list = new ArrayList<pod_mstr>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             pod_mstr x = new pod_mstr(null, tbkey.getText().toString(),
                orddet.getValueAt(j, 0).toString(),
                orddet.getValueAt(j, 1).toString(),
                orddet.getValueAt(j, 3).toString(),
                orddet.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.'), // qty
                "0", // rcvd qty        
                orddet.getValueAt(j, 9).toString().replace(defaultDecimalSeparator, '.'), // netprice
                orddet.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.'), // disc
                orddet.getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.'), // listprice
                bsdate.format(duedate.getDate()).toString(),
                orddet.getValueAt(j, 11).toString(),
                ddsite.getSelectedItem().toString(),
                bsdate.format(orddate.getDate()).toString(),
                orddet.getValueAt(j, 6).toString(),
                orddet.getValueAt(j, 2).toString()     
                );
        list.add(x);
         }
        return list;   
    }
    
    public po_addr createPOAddr() { 
        String shipcode = "";
        if (tbshipcode.getText().isEmpty()) {
            shipcode = String.valueOf(OVData.getNextNbr("shipto"));
        } else {
            shipcode = tbshipcode.getText();
        }
        po_addr x = new po_addr(null, 
                tbkey.getText(),
                shipcode,
                tbshipname.getText(),
                tbshipline1.getText(),
                tbshipline2.getText(),
                tbshipline3.getText(),
                tbshipcity.getText(),
                ddshipstate.getSelectedItem().toString(),
                tbshipzip.getText(),
                ddshipcountry.getSelectedItem().toString(),
                tbshipcontact.getText(),
                tbshipphone.getText(),
                tbshipemail.getText()
                );
        
        return x;
    }
    
    
    public boolean validateInput(dbaction x) {
       
        Map<String,Integer> f = OVData.getTableInfo("po_mstr");
        int fc;        
        fc = checkLength(f,"po_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
        bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
        tbkey.requestFocus();
        return false;
        } 
                
        fc = checkLength(f,"po_rmks");
        if (remarks.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        remarks.requestFocus();
        return false;
        } 
        
        fc = checkLength(f,"po_buyer");
        if (tbbuyer.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        tbbuyer.requestFocus();
        return false;
        } 
        
            
         if (ddstatus.getSelectedItem().toString().isEmpty()) {
                    status = getGlobalProgTag("open");
                } else {
                    status = ddstatus.getSelectedItem().toString();
                }
                
                if (cbblanket.isSelected())
                    blanket = "BLANKET";
                else
                    blanket = "DISCRETE";
                
                if ( ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty() ) {
                   bsmf.MainFrame.show(getMessageTag(1024));
                    ddvend.requestFocus();
                    return false;
                }
               
                if ( duedate.getDate() == null) {
                   bsmf.MainFrame.show(getMessageTag(1024));
                    duedate.requestFocus();
                    return false;
                }
                
                terms = venData.getVendTerms(ddvend.getSelectedItem().toString()); 
                cc = venData.getVendAPCC(ddvend.getSelectedItem().toString());
                acct = venData.getVendAPAcct(ddvend.getSelectedItem().toString());
                curr = ddcurr.getSelectedItem().toString();
                
                if (terms == null   || acct == null   || cc == null || curr == null ||
                        terms.isEmpty() || acct.isEmpty() || cc.isEmpty() || curr.isEmpty()
                         ) {
                        bsmf.MainFrame.show(getMessageTag(1090));
                        return false;
                    }   
                
                if (orddet.getRowCount() == 0) {
                    bsmf.MainFrame.show(getMessageTag(1089));
                    return false;
                }
                
                
                 // lets check for foreign currency with no exchange rate
            if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
            if (OVData.getExchangeRate(basecurr, curr).isEmpty()) {
                bsmf.MainFrame.show(getMessageTag(1091, curr + "/" + basecurr));
                return false;
            }
            }
                
                    
        return true;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getPOBrowseUtil(luinput.getText(),0, "po_nbr");
        } else {
         luModel = DTData.getPOBrowseUtil(luinput.getText(),0, "po_vend");   
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
                getClassLabelTag("lblvend", this.getClass().getSimpleName())); 
        
    }

    public void lookUpFrameShipTo() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getPOAddrBrowseUtil(luinput.getText(), 0, "poa_name"); 
        } else {
         luModel = DTData.getPOAddrBrowseUtil(luinput.getText(), 0, "poa_zip");   
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
                getShipTo(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblvend", this.getClass().getSimpleName()), 
                getClassLabelTag("lblname", this.getClass().getSimpleName()));  
        
        
    }
 
    
    public void lookUpFrameVendItem() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        luModel = DTData.getVendXrefBrowseUtil(luinput.getText(), 0, "vdp_vitem", ddvend.getSelectedItem().toString());
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
                ddpart.setSelectedItem(target.getValueAt(row,1).toString()); }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblvenditem", this.getClass().getSimpleName())); 
        
        
    }
 
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_item");
        } else {
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_desc");   
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
                ddpart.setSelectedItem(target.getValueAt(row,1).toString());}
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()));  
        
        
    }
 
    public void lookUpFrameVendor() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVendBrowseUtil(luinput.getText(), 0, "vd_name"); 
        } else {
         luModel = DTData.getVendBrowseUtil(luinput.getText(), 0, "vd_zip");   
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
                ddvend.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblvend", this.getClass().getSimpleName()), 
                getClassLabelTag("lblname", this.getClass().getSimpleName()));  
        
        
    }
 
    public void updateForm() throws ParseException {
        tbkey.setText(po.po_nbr());
        ddvend.setSelectedItem(po.po_vend());
        ddvend.setEnabled(false);
        ddstatus.setSelectedItem(po.po_status());
        ddedistatus.setSelectedItem(po.po_edistatus());
        ddcurr.setSelectedItem(po.po_curr());
        ddshipvia.setSelectedItem(po.po_shipvia());
        ddtype.setSelectedItem(po.po_type());
        remarks.setText(po.po_rmks());
        duedate.setDate(bsmf.MainFrame.dfdate.parse(po.po_due_date()));
        blanket = po.po_type();
        cbconfirm.setSelected(BlueSeerUtils.ConvertStringToBool(po.po_confirm()));
        if (blanket != null && blanket.compareTo("BLANKET") == 0)
        cbblanket.setSelected(true);
        else
        cbblanket.setSelected(false);
        cbblanket.setEnabled(false);
        
        // now detail
        for (pod_mstr pod : podlist) {
        myorddetmodel.addRow(new Object[]{pod.pod_line(), 
                      pod.pod_item(),
                      pod.pod_desc(), 
                      pod.pod_venditem(), 
                      pod.pod_nbr(), 
                      bsNumber(pod.pod_ord_qty()), 
                      pod.pod_uom(), 
                      priceformat(pod.pod_listprice()),
                      priceformat(pod.pod_disc()),
                      priceformat(pod.pod_netprice()),
                      bsNumber(pod.pod_rcvd_qty()),  
                      pod.pod_status()});
        }
        
        // po_addr info
        tbshipcode.setText(poaddr.poa_shipto());
        tbshipname.setText(poaddr.poa_name());
        tbshipline1.setText(poaddr.poa_line1());
        tbshipline2.setText(poaddr.poa_line2());
        tbshipline3.setText(poaddr.poa_line3());
        tbshipcity.setText(poaddr.poa_city());
        ddshipstate.setSelectedItem(poaddr.poa_state());
        ddshipcountry.setSelectedItem(poaddr.poa_country());
        tbshipzip.setText(poaddr.poa_zip());
        tbshipcontact.setText(poaddr.poa_contact());
        tbshipphone.setText(poaddr.poa_phone());
        tbshipemail.setText(poaddr.poa_email());
        
        setAction(po.m()); 
        
        po = null;
        podlist = null;
        poaddr = null;
        
    }
       
     // additional functions
    public void setPrice() {
         if (dduom.getItemCount() > 0 && ddpart.getItemCount() > 0 && ddvend.getItemCount() > 0 && ! ddcurr.getSelectedItem().toString().isEmpty()) {
                String[] TypeAndPrice = invData.getItemPrice("v", ddvend.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                        dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
                String pricetype = TypeAndPrice[0].toString();
                double price = bsParseDouble(TypeAndPrice[1]);
             //   
                listprice.setText(bsFormatDouble(price));
                if (pricetype.equals("vend")) {
                    listprice.setBackground(Color.green);
                }
                if (pricetype.equals("item")) {
                    listprice.setBackground(Color.white);
                }
                 setnetprice();
         }
     }
    
    public boolean validateDetail() {
        // if user clicks on 'additem' before focuslost on each field
       // has time to fire, focuslost will have effectively set these fields to empty upon
       // seeing an error before this function is called
       // ...so we check for empty to prevent lines from being added
        
        if (qtyshipped.getText().isEmpty()) {
            return false;
        }
        if (listprice.getText().isEmpty()) {
            return false;
        }
        if (discount.getText().isEmpty()) {
            return false;
        }
        
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isValidUOMConversion(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1026));
                dduom.requestFocus();
                return false;
        }
        if (OVData.isValidItem(ddpart.getSelectedItem().toString()) && ! OVData.isBaseUOMOfItem(ddpart.getSelectedItem().toString(), ddsite.getSelectedItem().toString(), dduom.getSelectedItem().toString()) && ! OVData.isValidVendPriceRecordExists(ddvend.getSelectedItem().toString(),ddpart.getSelectedItem().toString(),dduom.getSelectedItem().toString(),ddcurr.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1094)); 
                dduom.requestFocus();
                return false;
        }
      return true;   
    }
   
    public boolean validateInputShipTo(dbaction action) {
        
        Map<String,Integer> f = OVData.getTableInfo("po_addr");
        int fc;

        fc = checkLength(f,"poa_shipto");
        if (tbshipcode.getText().length() > fc || tbshipcode.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbshipcode.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"poa_name");
        if (tbshipname.getText().length() > fc || tbshipname.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbshipname.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"poa_line1");
        if (tbshipline1.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline1.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"poa_line2");
        if (tbshipline2.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline2.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"poa_line3");
        if (tbshipline3.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline3.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"poa_city");
        if (tbshipcity.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipcity.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"poa_zip");
        if (tbshipzip.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipzip.requestFocus();
            return false;
        }

      return true;
     }
    
    public void addShipTo() {
        String[] m = addPOAddr(createPOAddr());
        bsmf.MainFrame.show(m[1]);
    }
    
    public void updateShipTo() {
        String[] m = updatePOAddr(createPOAddr());
        bsmf.MainFrame.show(m[1]);
    }
    
    public void deleteShipTo() {
        String[] m = deletePOAddr(createPOAddr());
        bsmf.MainFrame.show(m[1]);
        
    }
    
    public void clearShipTo() {
         tbshipname.setText("");
       tbshipline1.setText("");
       tbshipline2.setText("");
       tbshipline3.setText("");
       tbshipcity.setText("");
       tbshipzip.setText("");
       tbshipcode.setText("");
       tbshipcontact.setText("");
       tbshipphone.setText("");
       tbshipemail.setText("");
       
    ddshipstate.removeAllItems();
        ArrayList states = OVData.getCodeMstrKeyList("state");
        ddshipstate.addItem("");
        for (int i = 0; i < states.size(); i++) {
            ddshipstate.addItem(states.get(i).toString());
        }
        if (ddshipstate.getItemCount() > 0) {
           ddshipstate.setSelectedIndex(0); 
        }
       
       if (ddshipcountry.getItemCount() == 0)
       for (int i = 0; i < OVData.countries.length; i++) {
            ddshipcountry.addItem(OVData.countries[i]);
        }
       ddshipcountry.setSelectedItem("USA");
       
     }
    
    public String[] getShipTo(String[] x) {
        String[] m = new String[2];
        po_addr k = getPOAddr(x[1], x[0]);
        tbshipcode.setText(k.poa_shipto());
        tbshipname.setText(k.poa_code());
        tbshipline1.setText(k.poa_line1());
        tbshipline2.setText(k.poa_line2());
        tbshipline3.setText(k.poa_line3());
        tbshipcity.setText(k.poa_city());
        ddshipstate.setSelectedItem(k.poa_state());
        ddshipcountry.setSelectedItem(k.poa_country());
        tbshipzip.setText(k.poa_zip());
        tbshipcontact.setText(k.poa_contact());
        tbshipphone.setText(k.poa_phone());
        tbshipemail.setText(k.poa_email());
     return m;        
    }

    public void setShipTo(site_mstr x) {
        tbshipcode.setText(x.site_site());
        tbshipname.setText(x.site_desc());
        tbshipline1.setText(x.site_line1());
        tbshipline2.setText(x.site_line2());
        tbshipline3.setText("");
        tbshipcity.setText(x.site_city());
        ddshipstate.setSelectedItem(x.site_state());
        ddshipcountry.setSelectedItem(x.site_country());
        tbshipzip.setText(x.site_zip());
        tbshipcontact.setText("");
        tbshipphone.setText("");
        tbshipemail.setText("");
    }    
    
    public void getparts(String part) {
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
              
               // if part is not already in list
                int k = ddpart.getSelectedIndex();
              
                
                // lets first try as vend part...i.e....lets look up the item based on entering a vendor part number.
                if (k < 0) {
                    
                res = st.executeQuery("select vdp_item, vdp_vitem from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" 
                        + " AND vdp_vitem = " + "'" + part + "'" 
                        + ";");
                int i = 0;
                while (res.next()) {
                    i++;
                   // ddpart.setSelectedItem(res.getString("vdp_item"));
                    vendnumber.setText(part);
                    ddpart.setForeground(Color.blue);
                    vendnumber.setForeground(Color.blue);
                    vendnumber.setEditable(false);
                }
                
                // if i is still 0...then must be a misc item
                if (i == 0) {
                  //  partnbr.addItem(part);
                  //  partnbr.setSelectedItem(part);
                    vendnumber.setText("");
                    ddpart.setForeground(Color.red);
                    vendnumber.setForeground(Color.red);
                   // custnumber.setBorder(BorderFactory.createLineBorder(Color.red));
                    vendnumber.setEditable(true);
                    
                    discount.setText("0");
                    listprice.setText("0");
                    listprice.setBackground(Color.white);
                    
                    netprice.setText("0");
                    qtyshipped.setText("0");
                }
                
                }
                
             if (k >= 0) {   
            String[] det = invData.getItemDetail(ddpart.getSelectedItem().toString());
            discount.setText("0");
            listprice.setText("0");
            netprice.setText("0");
            qtyshipped.setText("0");
            tbdesc.setText(det[1]);
            vendnumber.setText(venData.getVendItemFromItem(ddvend.getSelectedItem().toString(),ddpart.getSelectedItem().toString()));
            dduom.setSelectedItem(det[2]);
            ddpart.setForeground(Color.blue);
            vendnumber.setForeground(Color.blue);
            vendnumber.setEditable(false);
            
            if (ddpart.getItemCount() > 0) {
                ddpart.setForeground(Color.blue);
                vendnumber.setForeground(Color.blue);
                setPrice();
                
            } // if part selected
             }  
                
                
                
               
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
    
    public void setnetprice() {
        double disc = 0;
        double list = 0;
        double net = 0;
         
        if (discount.getText().isEmpty() || bsParseDouble(discount.getText().toString()) == 0) {
            netprice.setText(listprice.getText());
        } else {
           
           if (listprice.getText().isEmpty() || bsParseDouble(listprice.getText().toString()) == 0) {
             listprice.setText("0");
             netprice.setText("0");
           } else {               
           disc = bsParseDouble(discount.getText().toString());
           list = bsParseDouble(listprice.getText().toString());
            
           net = list - ((disc / 100) * list);
           netprice.setText(currformatDouble(net));
           }
        }
    }
      
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
         if (orddet.getRowCount() > 0) {
             ddcurr.setEnabled(false);
             ddvend.setEnabled(false);
         } else {
             ddcurr.setEnabled(true);
             ddvend.setEnabled(true);
         }
    }
    
    public void sumqty() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + bsParseDouble(orddet.getValueAt(j, 5).toString()); 
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
    
    public void sumdollars() {
       double dol = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(orddet.getValueAt(j, 5).toString()) * bsParseDouble(orddet.getValueAt(j, 9).toString()) ); 
         }
         tbtotdollars.setText(currformatDouble(dol));
         lbltotdollars.setText(currformatDoubleWithSymbol(dol, ddcurr.getSelectedItem().toString()));
         lblcurr.setText(ddcurr.getSelectedItem().toString());
    }
   
     public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    public void vendChangeEvent(String mykey) {
        
        // reset
        lbvend.setText("");
        ddshipvia.setSelectedIndex(0);
        ddcurr.setSelectedIndex(0);
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
                if (venditemonly) {
                    ddpart.removeAllItems();
                    res = st.executeQuery("select distinct vdp_item from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        ddpart.addItem(res.getString("vdp_item"));
                    }
                }
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbvend.setText(res.getString("vd_name"));
                    ddshipvia.setSelectedItem(res.getString("vd_shipvia"));
                    ddcurr.setSelectedItem(res.getString("vd_curr"));
                    curr = ddcurr.getSelectedItem().toString();
                    if (res.getString("vd_is850export").equals("1")) {
                        ddedistatus.setSelectedItem("pending");
                    } else {
                        ddedistatus.setSelectedItem("N/A");
                    }
                }
                
              
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
    
     public void retotal() {
        
        double dol = 0;
        double newdisc = 0;
        double newprice = 0;
        double newtax = 0;
        double listprice = 0;
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             listprice = bsParseDouble(orddet.getValueAt(j, 7).toString());
             orddet.setValueAt(currformatDouble(newdisc), j, 8);
             if (newdisc > 0) {
             newprice = listprice - (listprice * (newdisc / 100));
             } else {
             newprice = listprice;    
             }
             orddet.setValueAt(currformatDouble(newprice), j, 9);
              
             
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel77 = new javax.swing.JLabel();
        remarks = new javax.swing.JTextField();
        userid = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        ddstatus = new javax.swing.JComboBox();
        ddvend = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        ddshipvia = new javax.swing.JComboBox();
        jLabel90 = new javax.swing.JLabel();
        cbblanket = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        btpoprint = new javax.swing.JButton();
        orddate = new com.toedter.calendar.JDateChooser();
        tbbuyer = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        ddcurr = new javax.swing.JComboBox<>();
        lbvend = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btLookUpVendor = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        ddedistatus = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        cbconfirm = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        lbltotdollars = new javax.swing.JLabel();
        panelDetail = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        qtyshipped = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox();
        vendnumber = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        tbdesc = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btLookUpCustItem = new javax.swing.JButton();
        btLookUpItemDesc = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        netprice = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        listprice = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        btdelitem = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        btupdateitem = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        lblcurr = new javax.swing.JLabel();
        panelShipto = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        tbshipcity = new javax.swing.JTextField();
        tbshipzip = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ddshipcountry = new javax.swing.JComboBox();
        ddshipstate = new javax.swing.JComboBox();
        tbshipline2 = new javax.swing.JTextField();
        tbshipline3 = new javax.swing.JTextField();
        tbshipname = new javax.swing.JTextField();
        tbshipline1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        tbshipcode = new javax.swing.JTextField();
        tbshipcontact = new javax.swing.JTextField();
        tbshipphone = new javax.swing.JTextField();
        tbshipemail = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase Order Maintenance"));
        panelMain.setName("panelmain"); // NOI18N
        panelMain.setPreferredSize(new java.awt.Dimension(850, 550));

        jLabel76.setText("OrderNbr");
        jLabel76.setName("lblid"); // NOI18N

        jLabel86.setText("Remarks");
        jLabel86.setName("lblremarks"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel77.setText("DateCreated");
        jLabel77.setName("lblcreatedate"); // NOI18N

        jLabel78.setText("UserID");
        jLabel78.setName("lbluserid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel85.setText("Status");
        jLabel85.setName("lblstatus"); // NOI18N

        jLabel82.setText("Vendor");
        jLabel82.setName("lblvend"); // NOI18N

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Due Date");
        jLabel81.setName("lblduedate"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel90.setText("ShipVia");
        jLabel90.setName("lblshipvia"); // NOI18N

        cbblanket.setText("Blanket");
        cbblanket.setName("cbblanket"); // NOI18N

        jLabel91.setText("Site");
        jLabel91.setName("lblsite"); // NOI18N

        btpoprint.setText("Print");
        btpoprint.setName("btprint"); // NOI18N
        btpoprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpoprintActionPerformed(evt);
            }
        });

        orddate.setDateFormatString("yyyy-MM-dd");

        jLabel92.setText("Buyer");
        jLabel92.setName("lblbuyer"); // NOI18N

        jLabel83.setText("Currency");
        jLabel83.setName("lblcurrency"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        lbvend.setName("lblname"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btLookUpVendor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpVendorActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "site", "dropship" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        jLabel10.setText("Type");
        jLabel10.setName("lbltype"); // NOI18N

        ddedistatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pending", "accepted", "rejected", "N/A" }));

        jLabel11.setText("EDI Status");

        cbconfirm.setText("Confirmed");

        jLabel12.setText("Total:");
        jLabel12.setName("lbltotalamt"); // NOI18N

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btLookUpVendor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addComponent(cbblanket)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel81)
                            .addComponent(jLabel77)
                            .addComponent(jLabel78)
                            .addComponent(jLabel83)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddedistatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddcurr, javax.swing.GroupLayout.Alignment.LEADING, 0, 99, Short.MAX_VALUE))
                            .addComponent(cbconfirm)))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(btpoprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addGap(10, 10, 10)
                        .addComponent(btadd)
                        .addGap(3, 3, 3)))
                .addGap(58, 58, 58)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbltotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnew)
                                    .addComponent(btclear))
                                .addGap(4, 4, 4))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btlookup)
                                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel76)))
                                .addGap(2, 2, 2)))
                        .addGap(4, 4, 4)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel91))
                        .addGap(6, 6, 6)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel82))
                                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel90))
                                    .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btLookUpVendor)))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel81))
                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(lbltotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel78)))
                            .addComponent(jLabel77))
                        .addGap(13, 13, 13)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel83))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel92))
                            .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel85))
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddedistatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))))
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(cbblanket)
                        .addGap(2, 2, 2)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel86)))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbconfirm)))
                .addGap(34, 34, 34)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btdelete)
                        .addComponent(btpoprint))
                    .addComponent(btupdate)
                    .addComponent(btadd))
                .addContainerGap())
        );

        add(panelMain);

        panelDetail.setName("panellines"); // NOI18N
        panelDetail.setPreferredSize(new java.awt.Dimension(850, 550));

        qtyshipped.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyshippedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                qtyshippedFocusLost(evt);
            }
        });

        jLabel79.setText("PartNumber");
        jLabel79.setName("lblitem"); // NOI18N

        ddpart.setEditable(true);
        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        jLabel87.setText("VendNumber");
        jLabel87.setName("lblvenditem"); // NOI18N

        jLabel84.setText("Order Qty");
        jLabel84.setName("lblordqty"); // NOI18N

        jLabel5.setText("uom");
        jLabel5.setName("lbluom"); // NOI18N

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel6.setText("Description");
        jLabel6.setName("lbldesc"); // NOI18N

        btLookUpCustItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpCustItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpCustItemActionPerformed(evt);
            }
        });

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vendnumber)
                    .addComponent(tbdesc)
                    .addComponent(ddpart, 0, 203, Short.MAX_VALUE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qtyshipped))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btLookUpCustItem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vendnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel87))
                    .addComponent(btLookUpCustItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(btLookUpItemDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        netprice.setEditable(false);
        netprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netpriceActionPerformed(evt);
            }
        });

        jLabel80.setText("ListPrice");
        jLabel80.setName("lbllistprice"); // NOI18N

        listprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                listpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                listpriceFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");
        jLabel88.setName("lbldisc"); // NOI18N

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel89.setText("NetPrice");
        jLabel89.setName("lblnetprice"); // NOI18N

        btdelitem.setText("Delete");
        btdelitem.setName("btdelete"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        btadditem.setText("Add");
        btadditem.setName("btadd"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btupdateitem.setText("Update");
        btupdateitem.setName("btupdate"); // NOI18N
        btupdateitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(discount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addComponent(listprice, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(netprice)))
                .addContainerGap(242, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem)
                    .addComponent(btupdateitem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        jLabel1.setText("Total Lines");
        jLabel1.setName("lbltotallines"); // NOI18N

        jLabel2.setText("Total Qty");
        jLabel2.setName("lbltotalqty"); // NOI18N

        jLabel3.setText("Total $");
        jLabel3.setName("lbltotalamt"); // NOI18N

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 309, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDetailLayout.createSequentialGroup()
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetailLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        add(panelDetail);

        panelShipto.setBackground(new java.awt.Color(220, 220, 220));
        panelShipto.setPreferredSize(new java.awt.Dimension(850, 550));

        jLabel30.setText("State");
        jLabel30.setName("lblstate"); // NOI18N

        jLabel31.setText("Line3");
        jLabel31.setName("lbladdr3"); // NOI18N

        jLabel32.setText("Zip");
        jLabel32.setName("lblzip"); // NOI18N

        jLabel33.setText("Line1");
        jLabel33.setName("lbladdr1"); // NOI18N

        jLabel34.setText("Country");
        jLabel34.setName("lblcountry"); // NOI18N

        jLabel35.setText("Line2");
        jLabel35.setName("lbladdr2"); // NOI18N

        jLabel36.setText("ShipCode");
        jLabel36.setName("lblshipto"); // NOI18N

        jLabel37.setText("Name");
        jLabel37.setName("lblname"); // NOI18N

        jLabel38.setText("City");
        jLabel38.setName("lblcity"); // NOI18N

        jLabel7.setText("Contact");

        jLabel8.setText("Phone");

        jLabel9.setText("Email");

        javax.swing.GroupLayout panelShiptoLayout = new javax.swing.GroupLayout(panelShipto);
        panelShipto.setLayout(panelShiptoLayout);
        panelShiptoLayout.setHorizontalGroup(
            panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelShiptoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelShiptoLayout.createSequentialGroup()
                        .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel35)
                                    .addComponent(jLabel31)
                                    .addComponent(jLabel38)
                                    .addComponent(jLabel30))
                                .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbshipline3, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipline2, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipline1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipcity, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addComponent(tbshipname)
                                .addComponent(ddshipstate, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbshipzip, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelShiptoLayout.createSequentialGroup()
                                .addComponent(ddshipcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(152, 152, 152)))
                        .addGap(88, 88, 88)
                        .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbshipcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbshipphone, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbshipemail, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelShiptoLayout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        panelShiptoLayout.setVerticalGroup(
            panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelShiptoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(tbshipcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(tbshipphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(tbshipemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelShiptoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addContainerGap(288, Short.MAX_VALUE))
        );

        add(panelShipto);
    }// </editor-fold>//GEN-END:initComponents

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        int line = 0;
        
        line = getmaxline();
        line++;
        
           if (line == 1 && ! editmode) {
            btadd.setEnabled(true);
        } 
        
         boolean canproceed = validateDetail();
        // "line", "Part", "Desc", "VendPart", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
       
        if (canproceed) {
         myorddetmodel.addRow(new Object[]{line, 
             ddpart.getSelectedItem().toString(), 
             tbdesc.getText(),  
             vendnumber.getText(), 
             tbkey.getText(),  
             qtyshipped.getText(), 
             dduom.getSelectedItem().toString(), 
             listprice.getText(), 
             discount.getText(), 
             netprice.getText(), 
             "0", 
             getGlobalProgTag("open")});
         sumqty();
         sumdollars();
         sumlinecount();
         listprice.setText("");
         netprice.setText("");
         discount.setText("");
         qtyshipped.setText("");
         ddpart.requestFocus();
        }
       
    }//GEN-LAST:event_btadditemActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed
         if (ddpart.getSelectedItem() != null && ! isLoad)
        getparts(ddpart.getSelectedItem().toString());
    }//GEN-LAST:event_ddpartActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
         int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
                            } else {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumdollars();
         sumlinecount();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void netpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netpriceActionPerformed

    private void listpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusLost
        if (! listprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", listprice.getText(), "4");
        if (x.equals("error")) {
            listprice.setText("");
            listprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            listprice.requestFocus();
        } else {
            listprice.setText(x);
            listprice.setBackground(Color.white);
        }
        setnetprice();
        }
    }//GEN-LAST:event_listpriceFocusLost

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
        if (! discount.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", discount.getText(), "4");
        if (x.equals("error")) {
            discount.setText("");
            discount.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            discount.requestFocus();
        } else {
            discount.setText(x);
            discount.setBackground(Color.white);
        }
        if (discount.getText().isEmpty())
            discount.setText("0");
        setnetprice();
        }
    }//GEN-LAST:event_discountFocusLost

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
       setPrice();
    }//GEN-LAST:event_dduomActionPerformed

    private void qtyshippedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusGained
        if (qtyshipped.getText().equals("0")) {
            qtyshipped.setText("");
        }
    }//GEN-LAST:event_qtyshippedFocusGained

    private void qtyshippedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusLost
        if (! qtyshipped.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", qtyshipped.getText(), "2");
        if (x.equals("error")) {
            qtyshipped.setText("");
            qtyshipped.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            qtyshipped.requestFocus();
        } else {
            qtyshipped.setText(x);
            qtyshipped.setBackground(Color.white);
        }
        }
        
    }//GEN-LAST:event_qtyshippedFocusLost

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
         JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (index == 1 && ddpart != null && ddpart.getItemCount() > 0) {
            ddpart.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void listpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusGained
         if (listprice.getText().equals("0")) {
            listprice.setText("");
        }
    }//GEN-LAST:event_listpriceFocusGained

    private void btupdateitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateitemActionPerformed
        int line = 0;
        
        line = getmaxline();
        line++;
        
        int[] rows = orddet.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else if (! orddet.getValueAt(i, 1).toString().equals(ddpart.getSelectedItem().toString())) {
                bsmf.MainFrame.show(getMessageTag(1096));
                return;
            }else {
                boolean canproceed = validateDetail();
                if (canproceed) {
         //"line", "Part", "Desc", "VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
                orddet.setValueAt(qtyshipped.getText(), i, 5);
                orddet.setValueAt(dduom.getSelectedItem().toString(), i, 6);
                orddet.setValueAt(listprice.getText(), i, 7);
                orddet.setValueAt(discount.getText(), i, 8);
                orddet.setValueAt(netprice.getText(), i, 9);
                orddet.setValueAt(tbdesc.getText(), i, 2);
                
                refreshDisplayTotals();         
                listprice.setText("");
                netprice.setText("");
                discount.setText("");
                qtyshipped.setText("");
                ddpart.requestFocus();
                
                }
            }
        }
    }//GEN-LAST:event_btupdateitemActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
         //"line", "Part", "Desc", "VendPart", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
         isLoad = true;  
        ddpart.setSelectedItem(orddet.getValueAt(row, 1).toString());
        dduom.setSelectedItem(orddet.getValueAt(row, 6).toString());
        qtyshipped.setText(orddet.getValueAt(row, 5).toString());
        vendnumber.setText(orddet.getValueAt(row, 3).toString());
        listprice.setText(orddet.getValueAt(row, 7).toString());
        netprice.setText(orddet.getValueAt(row, 9).toString());
        discount.setText(orddet.getValueAt(row, 8).toString());
        tbdesc.setText(orddet.getValueAt(row, 2).toString());
       
        isLoad = false;
    }//GEN-LAST:event_orddetMouseClicked

    private void btLookUpCustItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpCustItemActionPerformed
        lookUpFrameVendItem();
    }//GEN-LAST:event_btLookUpCustItemActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpVendorActionPerformed
        lookUpFrameVendor();
    }//GEN-LAST:event_btLookUpVendorActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btpoprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpoprintActionPerformed
        OVData.printPurchaseOrder(tbkey.getText());
    }//GEN-LAST:event_btpoprintActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
        if (! isLoad) {
            if (ddvend.getItemCount() > 0) {
                jTabbedPane1.setEnabledAt(1, true);
                jTabbedPane1.setEnabledAt(2, true);
                vendChangeEvent(ddvend.getSelectedItem().toString());
            } // if ddvend has a list
        }
    }//GEN-LAST:event_ddvendActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("po");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
        if (isLoad != true && ddtype.getSelectedItem() != null) {
            if (ddtype.getSelectedItem().toString().equals("site") && tbshipcode.getText().isEmpty()) {
            setShipTo(admData.getSiteMstr(new String[]{OVData.getDefaultSite(),""}));
            }
        }
    }//GEN-LAST:event_ddtypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpCustItem;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btLookUpVendor;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btpoprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JCheckBox cbconfirm;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox<String> ddedistatus;
    private static javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddshipcountry;
    private javax.swing.JComboBox ddshipstate;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JComboBox<String> dduom;
    private static javax.swing.JComboBox ddvend;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JLabel lbltotdollars;
    private javax.swing.JLabel lbvend;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelShipto;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTextField tbbuyer;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbshipcity;
    private javax.swing.JTextField tbshipcode;
    private javax.swing.JTextField tbshipcontact;
    private javax.swing.JTextField tbshipemail;
    private javax.swing.JTextField tbshipline1;
    private javax.swing.JTextField tbshipline2;
    private javax.swing.JTextField tbshipline3;
    private javax.swing.JTextField tbshipname;
    private javax.swing.JTextField tbshipphone;
    private javax.swing.JTextField tbshipzip;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    private javax.swing.JTextField userid;
    private javax.swing.JTextField vendnumber;
    // End of variables declaration//GEN-END:variables
}
