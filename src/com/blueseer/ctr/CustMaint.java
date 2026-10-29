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
package com.blueseer.ctr;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import static com.blueseer.ctr.cusData.addCMCDet;
import static com.blueseer.ctr.cusData.addCMSDet;
import static com.blueseer.ctr.cusData.addCustomerTransaction;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cmc_det;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.deleteCMCDet;
import static com.blueseer.ctr.cusData.deleteCMSDet;
import static com.blueseer.ctr.cusData.deleteCustMstr;
import static com.blueseer.ctr.cusData.getCMCDet;
import static com.blueseer.ctr.cusData.getCMSDet;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.ctr.cusData.updateCMCDet;
import static com.blueseer.ctr.cusData.updateCMSDet;
import static com.blueseer.ctr.cusData.updateCustMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.callChangeDialog;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.logChange;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.lurb3;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingWorker;


/**
 *
 * @author vaughnte
 */
public class CustMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
     boolean editmode = false;
     boolean isLoad = false;
     String basecurr = "";
     boolean autocust = false;
     ArrayList<String[]> initDataSets = null;
     String defaultsite = null;
     boolean canupdate = false;
     
     
     
     public static cm_mstr cm = null;
    
    // global datatablemodel declarations 
    javax.swing.table.DefaultTableModel contactmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
               getGlobalColumnTag("id"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("name"), 
                getGlobalColumnTag("phone"), 
                getGlobalColumnTag("fax"), 
                getGlobalColumnTag("email")
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
    
    
    public CustMaint() {
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
                case "getShipTo":
                    message = getShipTo(key);    
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
           } else if (this.type.equals("getShipTo")) {
           
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
      // BlueSeerUtils.startTask(new String[]{"","Running..."});
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
                
                // start reset background colors
                if (component instanceof JTextField) {
                    if (((JTextField) component).isEditable()) {
                     component.setBackground(Color.WHITE);
                    } else {
                     component.setBackground(bsmf.MainFrame.nonEditableColor);   
                    }
                }
                if (component instanceof JComboBox) {
                     component.setBackground(bsmf.MainFrame.ddbgcolor);
                }
                // end reset background colors
                
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
            
            overrideComponentState();
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
        
        initDataSets = cusData.getCustMaintInit(this.getClass().getName(), bsmf.MainFrame.userid);
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", mainPanel);
        jTabbedPane1.add("ShipTo", shiptoPanel);
        jTabbedPane1.add("Contact", contactPanel); 
        jTabbedPane1.add("Attachments", panelAttachment);
        
        attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
        
        tbname.setText("");
        tbline1.setText("");
        tbline2.setText("");
        tbline3.setText("");
        tbcity.setText("");
        tbmunicipality.setText("");
        tbcounty.setText("");
        tbzip.setText("");
        tbpricecode.setText("");
        tbgroup.setText("");
        tbcreditlimit.setText("0");
        tbdisccode.setText("");
        tbmarket.setText("");
        tbcontactname.setText("");
        tbphone.setText("");
        tbfax.setText("");
        tbemail.setText("");
        tbremarks.setText("");
        tbsalesrep.setText("");
        tbmainphone.setText("");
        tbmainemail.setText("");
        tbtaxid.setText("");
        tbhighbalance.setText("");
        tbhighbalance.setEditable(false);
        tbhighbalance.setBackground(bsmf.MainFrame.nonEditableColor);
        tbavgdays.setText("");
        tbavgdays.setEditable(false);
        tbavgdays.setBackground(bsmf.MainFrame.nonEditableColor);
        tblastpaydate.setText("");
        tblastpaydate.setEditable(false);
        tblastpaydate.setBackground(bsmf.MainFrame.nonEditableColor);
        tblastselldate.setText("");
        tblastselldate.setEditable(false);
        tblastselldate.setBackground(bsmf.MainFrame.nonEditableColor);
        
        java.util.Date now = new java.util.Date();
        DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        tbdateadded.setText(dtf.format(now));
        tbdatemod.setText(dtf.format(now));
        tbdateadded.setBackground(bsmf.MainFrame.nonEditableColor);
        tbdatemod.setBackground(bsmf.MainFrame.nonEditableColor);
         cbshipto.setSelected(true);
        cb855.setSelected(false);
        cb856.setSelected(false);
        cb810.setSelected(false);
        cbcascade.setSelected(false);
        cbtaxexempt.setSelected(false);
       
       
        
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        tbkey.setBackground(Color.white);
        
        /* cant add shipcode until billcode has been committed */
        btshipadd.setEnabled(false);
        btshipedit.setEnabled(false);
        btcopyshipto.setEnabled(false);
        
        contactmodel.setRowCount(0);
        contacttable.setModel(contactmodel);
        
        clearShipTo();
        clearContacts();
       
        
        
        ddsite.removeAllItems();
        ddcurr.removeAllItems();
        ddterms.removeAllItems();
        ddtax.removeAllItems();
        ddaccount.removeAllItems();
        ddcc.removeAllItems();
        ddbank.removeAllItems();
        ddlabel.removeAllItems();
        ddcarrier.removeAllItems();
        ddfreightterms.removeAllItems();
        ddstate.removeAllItems();
        ddcountry.removeAllItems();
        ddshipstate.removeAllItems();
        ddshipcountry.removeAllItems();
        
     
        
         for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              basecurr = s[1];  
            }
          
            if (s[0].equals("canupdate")) {
              canupdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("currencies")) {
              ddcurr.addItem(s[1]); 
            }
            
            if (s[0].equals("terms")) {
              ddterms.addItem(s[1]); 
            }
            
            if (s[0].equals("autocust")) {
              autocust = BlueSeerUtils.ConvertStringToBool(s[1]); 
            }
            
            if (s[0].equals("freight")) {
              ddfreightterms.addItem(s[1]); 
            }
            
            if (s[0].equals("accounts")) {
              ddaccount.addItem(s[1]); 
            }
            
            if (s[0].equals("depts")) {
              ddcc.addItem(s[1]); 
            }
            
            if (s[0].equals("banks")) {
              ddbank.addItem(s[1]); 
            }
            
            if (s[0].equals("labels")) {
              ddlabel.addItem(s[1]); 
            }
          
            if (s[0].equals("taxcodes")) {
              ddtax.addItem(s[1]); 
            }
            
            if (s[0].equals("carriers")) {
              ddcarrier.addItem(s[1]); 
            }
            
            if (s[0].equals("states")) {
              ddstate.addItem(s[1]); 
              ddshipstate.addItem(s[1]); 
            }
            
            if (s[0].equals("countries")) {
              ddcountry.addItem(s[1]);
              ddshipcountry.addItem(s[1]);
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            
        }
        
         ddsite.setSelectedItem(defaultsite);
         
         if (ddaccount.getItemCount() > 0) {
          ddaccount.setSelectedIndex(0);
         }
         if (ddcc.getItemCount() > 0) {
          ddcc.setSelectedIndex(0);
         }
         if (ddlabel.getItemCount() > 0) {
          ddlabel.setSelectedIndex(0);
         }
         if (ddbank.getItemCount() > 0) {
          ddbank.setSelectedIndex(0);
         }
         if (ddcurr.getItemCount() > 0) {
          ddcurr.setSelectedItem(basecurr);
         }
         if (ddterms.getItemCount() > 0) {
          ddterms.setSelectedIndex(0);
         }
         
         ddtax.insertItemAt("", 0);
         ddtax.setSelectedIndex(0);
         
         if (ddcarrier.getItemCount() > 0) {
          ddcarrier.setSelectedIndex(0);
         } else {
          ddcarrier.insertItemAt("", 0);
          ddcarrier.setSelectedIndex(0);   
         }
         
         if (ddfreightterms.getItemCount() > 0) {
          ddfreightterms.setSelectedIndex(0);
         } else {
          ddfreightterms.insertItemAt("", 0);
          ddfreightterms.setSelectedIndex(0);   
         }
        
         ddstate.insertItemAt("", 0);
         ddstate.setSelectedIndex(0);
         
         ddcountry.insertItemAt("", 0);
         ddcountry.setSelectedIndex(0);
         
         
         ddshipstate.insertItemAt("", 0);
         ddshipstate.setSelectedIndex(0);
         
         ddshipcountry.insertItemAt("", 0);
         ddshipcountry.setSelectedIndex(0);
        
        
        
        isLoad = false;
    }
    
    public void initvars(String[] arg) {
        
        
       
           setPanelComponentState(mainPanel, false); 
           setPanelComponentState(shiptoPanel, false); 
           setPanelComponentState(contactPanel, false); 
           setPanelComponentState(this, false); 

           setComponentDefaultValues();
           btnew.setEnabled(true);
           btlookup.setEnabled(true);
           tbkey.requestFocus();
       
        
           if (arg != null && arg.length > 0) {
            executeTask(dbaction.get, arg);
          } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
          }
       
        
    }
        
    public void newAction(String x) {
        setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        btcopy.setEnabled(false);
        btcopyshipto.setEnabled(false);
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
                   tbkey.setForeground(Color.blue);
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public String[] addRecord(String[] x) {
        String[] m = new String[2];
        ArrayList<String[]> contacts = tableToArrayList();
        if (cbshipto.isSelected()) {
        m = addCustomerTransaction(createRecord(), contacts, createCMSDet(true));
        } else { 
        m = addCustomerTransaction(createRecord(), contacts, null);    
        }   
        return m;   
    }
    
    public String[] updateRecord(String[] x) {
        cm_mstr _cm = createRecord();
        String[] m = updateCustMstr(_cm);
            // change log check
            if (m[0].equals("0")) {
              ArrayList<admData.change_log> c = logChange(tbkey.getText(), 0, this.getClass().getSimpleName(),cm,_cm);
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
        m = deleteCustMstr(createRecord());
        initvars(null);
        return m;   
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled};  
        }
        return m;
     }
    
    public String[] getRecord(String[] x) {
        cm_mstr z = getCustMstr(x);
        cm = z;
        getAttachments(x[0]);
        return cm.m();
    }
     
    public cm_mstr createRecord() { 
        cusData.cm_mstr x = new cusData.cm_mstr(null, 
                tbkey.getText().toString(),
                tbname.getText(),
                tbline1.getText(),
                tbline2.getText(),
                tbline3.getText(),
                tbcity.getText(),
                (ddstate.getSelectedItem() == null) ? "" : ddstate.getSelectedItem().toString(),
                tbzip.getText(),
                (ddcountry.getSelectedItem() == null) ? "" : ddcountry.getSelectedItem().toString(),
                tbdateadded.getText(),
                tbdatemod.getText(),
                bsmf.MainFrame.userid,
                tbgroup.getText(),
                tbmarket.getText(),
                tbcreditlimit.getText(),
                BlueSeerUtils.boolToString(cbonhold.isSelected()),
                (ddcarrier.getSelectedItem() == null) ? "" : ddcarrier.getSelectedItem().toString(),
                (ddterms.getSelectedItem() == null) ? "" : ddterms.getSelectedItem().toString(),
                (ddfreightterms.getSelectedItem() == null) ? "" : ddfreightterms.getSelectedItem().toString(),
                tbpricecode.getText(),
                tbdisccode.getText(),
                (ddtax.getSelectedItem() == null) ? "" : ddtax.getSelectedItem().toString(),
                tbsalesrep.getText(),
                ddaccount.getSelectedItem().toString(),
                ddcc.getSelectedItem().toString(),
                (ddbank.getSelectedItem() == null) ? "" : ddbank.getSelectedItem().toString(),
                (ddcurr.getSelectedItem() == null) ? "" : ddcurr.getSelectedItem().toString(),
                tbremarks.getText(),
                (ddlabel.getSelectedItem() == null) ? "" : ddlabel.getSelectedItem().toString(),
                tbshpformat.getText(),
                tbinvformat.getText(),
                tbmainphone.getText(),
                tbmainemail.getText(),
                BlueSeerUtils.boolToString(cb855.isSelected()),
                BlueSeerUtils.boolToString(cb856.isSelected()),
                BlueSeerUtils.boolToString(cb810.isSelected()),
                (ddsite.getSelectedItem() == null) ? "" : ddsite.getSelectedItem().toString(),
                tbtaxid.getText(), 
                BlueSeerUtils.boolToString(cbcascade.isSelected()),
                tbhighbalance.getText(),
                tbavgdays.getText(),
                tblastpaydate.getText(),
                tblastselldate.getText(),
                tbmunicipality.getText(),
                tbcounty.getText(),
                BlueSeerUtils.boolToString(cbtaxexempt.isSelected())
                );
        return x;
    }
    
    public cms_det createCMSDet(boolean sameAs) { 
        // added sameAs boolean to distinguish intial customer creation from post customer creation
        cusData.cms_det x = null;
        if (sameAs) {
        x = new cusData.cms_det(null, 
                tbkey.getText(),
                tbkey.getText(),
                tbname.getText(),
                tbline1.getText(),
                tbline2.getText(),
                tbline3.getText(),
                tbcity.getText(),
                (ddstate.getSelectedItem() == null) ? "" : ddstate.getSelectedItem().toString(),
                tbzip.getText(),
                (ddcountry.getSelectedItem() == null) ? "" : ddcountry.getSelectedItem().toString(),
                tbname.getText(),
                tbphone.getText(),
                tbemail.getText(),
                "", // misc
                "", // plantcode
                "", //type
                tbmunicipality.getText(),  // municipal
                tbcounty.getText() // county
            );
        } else {
        x = new cusData.cms_det(null, 
                tbkey.getText(),
                tbshipcode.getText(),
                tbshipname.getText(),
                tbshipline1.getText(),
                tbshipline2.getText(),
                tbshipline3.getText(),
                tbshipcity.getText(),
                (ddshipstate.getSelectedItem() == null) ? "" : ddshipstate.getSelectedItem().toString(),
                tbshipzip.getText(),
                (ddshipcountry.getSelectedItem() == null) ? "" : ddshipcountry.getSelectedItem().toString(),
                tbshipcontact.getText(),
                tbshipphone.getText(),
                tbshipemail.getText(),
                "", // misc
                tbxrefcode.getText(), // xref external code (plant code)
                "",  // cms_type
                tbshipmunicipality.getText(),
                tbshipcounty.getText()
                );
        }
        return x;
    }
    
    public cmc_det createCMCDet(String id) {
        cusData.cmc_det x = new cusData.cmc_det(null, 
                id,
                tbkey.getText(),
                ddcontacttype.getSelectedItem().toString(),
                tbcontactname.getText(),
                tbphone.getText(),
                tbfax.getText(),
                tbemail.getText()
                );
        return x;
    }
    
    public boolean validateInput(dbaction action) {
        
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"cm_mstr"});
        int fc;

        fc = checkLength(f,"cm_code");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            tbkey.setBackground(bsmf.MainFrame.invalidColor);
            return false;
        }  
        
        fc = checkLength(f,"cm_name");
        if (tbname.getText().length() > fc || tbname.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbname.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"cm_line1");
        if (tbline1.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbline1.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_line2");
        if (tbline2.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbline2.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_line3");
        if (tbline3.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbline3.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_city");
        if (tbcity.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbcity.requestFocus();
            tbcity.setBackground(bsmf.MainFrame.invalidColor);
            return false;
        }
        
        fc = checkLength(f,"cm_zip");
        if (tbzip.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbzip.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_phone");
        if (tbphone.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbphone.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_email");
        if (tbemail.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbemail.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_group");
        if (tblastselldate.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tblastselldate.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_market");
        if (tbgroup.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbgroup.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_salesperson");
        if (tbsalesrep.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbsalesrep.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cm_remarks");
        if (tbremarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            return false;
        }

      return true;
     }
    
    public boolean validateInputShipTo(dbaction action) {
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"cms_det"});
        int fc;

        fc = checkLength(f,"cms_shipto");
        if (tbshipcode.getText().length() > fc || tbshipcode.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbshipcode.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"cms_name");
        if (tbshipname.getText().length() > fc || tbshipname.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbshipname.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"cms_line1");
        if (tbshipline1.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline1.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cms_line2");
        if (tbshipline2.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline2.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cms_line3");
        if (tbshipline3.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipline3.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cms_city");
        if (tbshipcity.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipcity.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cms_zip");
        if (tbshipzip.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipzip.requestFocus();
            return false;
        }
        fc = checkLength(f,"cms_contact");
        if (tbshipcontact.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipcontact.requestFocus();
            return false;
        }
        fc = checkLength(f,"cms_phone");
        if (tbshipphone.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipphone.requestFocus();
            return false;
        }
        fc = checkLength(f,"cms_email");
        if (tbshipemail.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbshipemail.requestFocus();
            return false;
        }

      return true;
     }
    
    
    public void lookUpFrame(String option) {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (option.equals("shipto")) {
            if (lurb1.isSelected()) {  
             luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_name", tbkey.getText());
            } else if (lurb2.isSelected()) {
             luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_line1", tbkey.getText());   
            } else if (lurb3.isSelected()) {
             luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_zip", tbkey.getText());
            } else {
             luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_code", tbkey.getText());   
            }
        } else {
            if (lurb1.isSelected()) {  
             luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_name");
            } else if (lurb2.isSelected()) {
             luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_line1");   
            } else if (lurb3.isSelected()) {
             luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_zip");
            } else {
             luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_code");   
            }
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
                    if (option.equals("shipto")) {
                        getShipTo(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                    } else {
                    initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                    }
                }
            }
        };
        luTable.addMouseListener(luml);
         
        callDialog(getClassLabelTag("lblname", this.getClass().getSimpleName()), 
                getClassLabelTag("lbladdr1", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName()),
                getClassLabelTag("lblid", this.getClass().getSimpleName())); 
        
        
        
    }

    public String[] updateForm() {
      tbkey.setText(cm.cm_code());
      ddsite.setSelectedItem(cm.cm_site());
        tbname.setText(cm.cm_name());
        tbline1.setText(cm.cm_line1());
        tbline2.setText(cm.cm_line2());
        tbline3.setText(cm.cm_line3());
        tbcity.setText(cm.cm_city());
        tbmunicipality.setText(cm.cm_municipality());
        tbcounty.setText(cm.cm_county());
        ddstate.setSelectedItem(cm.cm_state());
        ddcountry.setSelectedItem(cm.cm_country());
        tbzip.setText(cm.cm_zip());
        tbdateadded.setText(cm.cm_dateadd());
        tbdatemod.setText(cm.cm_datemod());
        tbgroup.setText(cm.cm_group());
        tbmarket.setText(cm.cm_market());
        tbcreditlimit.setText(cm.cm_creditlimit());
        cbonhold.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_onhold()));
        cbtaxexempt.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_tax_exempt()));
        ddcarrier.setSelectedItem(cm.cm_carrier());
        ddbank.setSelectedItem(cm.cm_bank());
        ddcurr.setSelectedItem(cm.cm_curr());
        ddlabel.setSelectedItem(cm.cm_label());
        tbinvformat.setText(cm.cm_iv_jasper());
        tbshpformat.setText(cm.cm_ps_jasper());
        ddfreightterms.setSelectedItem(cm.cm_freight_type());
        ddterms.setSelectedItem(cm.cm_terms());
        tbpricecode.setText(cm.cm_price_code());
        tbdisccode.setText(cm.cm_disc_code());
        ddtax.setSelectedItem(cm.cm_tax_code());
        tbsalesrep.setText(cm.cm_salesperson());
        ddaccount.setSelectedItem(cm.cm_ar_acct());
        ddcc.setSelectedItem(cm.cm_ar_cc());
        tbremarks.setText(cm.cm_remarks());
        tbmainphone.setText(cm.cm_phone());
        tbmainemail.setText(cm.cm_email());
        cb855.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_is855export()));
        cb856.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_is856export()));
        cb810.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_is810export()));
        cbcascade.setSelected(BlueSeerUtils.ConvertStringToBool(cm.cm_cascade()));
        tbtaxid.setText(cm.cm_misc1());
        tbhighbalance.setText(cm.cm_highbal());
        tbavgdays.setText(cm.cm_avgdays());
        tblastpaydate.setText(cm.cm_lastpaydate());
        tblastselldate.setText(cm.cm_lastselldate()); 
        contactmodel.setRowCount(0);
        clearShipTo();
        clearContacts();
        refreshContactTable(cm.cm_code());
        setAction(cm.m());
        return cm.m();  
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
    public void overrideComponentState() {
         tbdateadded.setEditable(false);
         tbdatemod.setEditable(false);
    }
        
    public void addShipTo() {
        String[] m = addCMSDet(createCMSDet(false));
        bsmf.MainFrame.show(m[1]);
        btcopyshipto.setEnabled(false);
        tbshipcode.setBackground(Color.white);
        
    }
    
    public void updateShipTo() {
        String[] m = updateCMSDet(createCMSDet(false));
        bsmf.MainFrame.show(m[1]);
    }
    
    public void deleteShipTo(String cust, String shipto) {
        String[] m = deleteCMSDet(cust, shipto);
        clearShipTo();
        bsmf.MainFrame.show(m[1]);
    }
    
    public void addContact(String cust) {
        String[] m = addCMCDet(createCMCDet(""));
        bsmf.MainFrame.show(m[1]);
    }
    
    public void editContact(String cust, String z) {
        String[] m = updateCMCDet(createCMCDet(z));
        bsmf.MainFrame.show(m[1]);
    }
    
    public void deleteContact(String cust, String z) {
       String[] m = deleteCMCDet(createCMCDet(z));
        bsmf.MainFrame.show(m[1]);
    }
    
    public String[] getShipTo(String[] x) {
        String[] m = new String[2];
        cms_det k = getCMSDet(x[0], x[1]);
        tbshipcode.setText(k.cms_shipto());
        tbshipname.setText(k.cms_name());
        tbshipline1.setText(k.cms_line1());
        tbshipline2.setText(k.cms_line2());
        tbshipline3.setText(k.cms_line3());
        tbshipcity.setText(k.cms_city());
        tbshipzip.setText(k.cms_zip());
        ddshipstate.setSelectedItem(k.cms_state());
        ddshipcountry.setSelectedItem(k.cms_country());
        tbxrefcode.setText(k.cms_plantcode());
        tbshipemail.setText(k.cms_email());
        tbshipphone.setText(k.cms_phone());
        tbshipcontact.setText(k.cms_contact());
        tbshipmunicipality.setText(k.cms_municipality());
        tbshipcounty.setText(k.cms_county());
        if (k.m()[0].equals("0")) {
            btshipedit.setEnabled(true);
            btshipnew.setEnabled(true);
            btshipadd.setEnabled(false);
            tbshipcode.setEditable(false);
            btcopyshipto.setEnabled(true);
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};;
           } else {
            btshipedit.setEnabled(false);
            btshipnew.setEnabled(true);
            btshipadd.setEnabled(false);   
            btcopyshipto.setEnabled(false);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};
           }
     return m;
        
    }
        
    public void refreshContactTable(String cust) {
      contactmodel.setRowCount(0);
      ArrayList<cmc_det> k = getCMCDet(tbkey.getText());
      for (cmc_det cmc : k) {
          contactmodel.addRow(new Object[]{cmc.cmc_id(), 
              cmc.cmc_type(), 
              cmc.cmc_name(), 
              cmc.cmc_phone(), 
              cmc.cmc_fax(), 
              cmc.cmc_email() }); 
      }
    }
      
    public void clearShipTo() {
       tbshipname.setText("");
       tbshipline1.setText("");
       tbshipline2.setText("");
       tbshipline3.setText("");
       tbshipcity.setText("");
       tbshipzip.setText("");
       tbshipcode.setText("");
       tbxrefcode.setText("");
       tbshipcontact.setText("");
       tbshipphone.setText("");
       tbshipemail.setText("");
       tbshipmunicipality.setText("");
       tbshipcounty.setText("");
       btcopyshipto.setEnabled(false);
      
        if (ddshipstate.getItemCount() > 0) {
           ddshipstate.setSelectedIndex(0); 
        }
       
       if (ddshipcountry.getItemCount() > 0) {
       ddshipcountry.setSelectedItem("USA");
       }
       
     }
     
    
    
    public void clearContacts() {
        tbcontactname.setText("");
        tbphone.setText("");
        tbfax.setText("");
        tbemail.setText("");
        
         if (ddcontacttype.getItemCount() > 0)
        ddcontacttype.setSelectedIndex(0);
    }
   
    public ArrayList<String[]> tableToArrayList() {
        ArrayList<String[]> list = new ArrayList<String[]>();
         for (int j = 0; j < contacttable.getRowCount(); j++) {
             String[] s = new String[]{
                 contacttable.getValueAt(j, 0).toString(),
                 contacttable.getValueAt(j, 1).toString(),
                 contacttable.getValueAt(j, 2).toString(),
                 contacttable.getValueAt(j, 3).toString(),
                 contacttable.getValueAt(j, 4).toString(),
                 contacttable.getValueAt(j, 5).toString()};
             list.add(s);
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
        mainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        ddstate = new javax.swing.JComboBox();
        tbline2 = new javax.swing.JTextField();
        tbline3 = new javax.swing.JTextField();
        tbname = new javax.swing.JTextField();
        tbline1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbremarks = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        tbmainphone = new javax.swing.JTextField();
        tbmainemail = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        lblcountry = new javax.swing.JLabel();
        tbmunicipality = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        tbcounty = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        btcopy = new javax.swing.JButton();
        btchangelog = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbdatemod = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbgroup = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbonhold = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        tbdateadded = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tblastselldate = new javax.swing.JTextField();
        tbmarket = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        ddterms = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        ddcarrier = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        ddfreightterms = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        tbdisccode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        tbpricecode = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbsalesrep = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        ddbank = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        tbshpformat = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        tbinvformat = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        ddlabel = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        cbshipto = new javax.swing.JCheckBox();
        ddaccount = new javax.swing.JComboBox<>();
        ddcc = new javax.swing.JComboBox<>();
        ddtax = new javax.swing.JComboBox<>();
        btdelete = new javax.swing.JButton();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        cb855 = new javax.swing.JCheckBox();
        cb856 = new javax.swing.JCheckBox();
        cb810 = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        tbtaxid = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        cbcascade = new javax.swing.JCheckBox();
        tbavgdays = new javax.swing.JTextField();
        tbhighbalance = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        tblastpaydate = new javax.swing.JTextField();
        tbcreditlimit = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        cbtaxexempt = new javax.swing.JCheckBox();
        shiptoPanel = new javax.swing.JPanel();
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
        btshipadd = new javax.swing.JButton();
        btshipedit = new javax.swing.JButton();
        btshipnew = new javax.swing.JButton();
        tbshipcode = new javax.swing.JTextField();
        btlookupShipTo = new javax.swing.JButton();
        tbxrefcode = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        btclearshipto = new javax.swing.JButton();
        btdeleteshipto = new javax.swing.JButton();
        tbshipemail = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        tbshipphone = new javax.swing.JTextField();
        tbshipcontact = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        tbshipmunicipality = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        tbshipcounty = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        btcopyshipto = new javax.swing.JButton();
        contactPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contacttable = new javax.swing.JTable();
        btdeletecontact = new javax.swing.JButton();
        tbcontactname = new javax.swing.JTextField();
        btaddcontact = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        ddcontacttype = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tbfax = new javax.swing.JTextField();
        bteditcontact = new javax.swing.JButton();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Master Maintenance"));
        mainPanel.setName("panelmain"); // NOI18N

        jLabel7.setText("State");
        jLabel7.setName("lblstate"); // NOI18N

        jLabel5.setText("Line3");
        jLabel5.setName("lbladdr3"); // NOI18N

        jLabel8.setText("Zip/PostCode");
        jLabel8.setName("lblzip"); // NOI18N

        jLabel3.setText("Line1");
        jLabel3.setName("lbladdr1"); // NOI18N

        jLabel9.setText("Country");
        jLabel9.setName("lblcountry"); // NOI18N

        jLabel4.setText("Line2");
        jLabel4.setName("lbladdr2"); // NOI18N

        ddcountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcountryActionPerformed(evt);
            }
        });

        jLabel1.setText("CustCode");
        jLabel1.setName("lblid"); // NOI18N

        jLabel2.setText("Name");
        jLabel2.setName("lblname"); // NOI18N

        jLabel6.setText("City");
        jLabel6.setName("lblcity"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.setPreferredSize(new java.awt.Dimension(65, 23));
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        tbremarks.setColumns(20);
        tbremarks.setLineWrap(true);
        tbremarks.setRows(5);
        jScrollPane2.setViewportView(tbremarks);

        jLabel29.setText("Remarks");
        jLabel29.setName("lblremarks"); // NOI18N

        jLabel44.setText("Phone");
        jLabel44.setName("lblphone"); // NOI18N

        jLabel45.setText("Email");
        jLabel45.setName("lblemail"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.setToolTipText("Search");
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel56.setText("Municipality");
        jLabel56.setName("lblmunicipality"); // NOI18N

        jLabel58.setText("County");

        btcopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btcopy.setToolTipText("Copy");
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        btchangelog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btchangelog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchangelogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel56, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbline3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbmainemail, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblcountry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(tbmainphone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbcounty, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbmunicipality, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbzip, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcopy)
                .addGap(3, 3, 3)
                .addComponent(btchangelog)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnew, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btclear)
                            .addComponent(btnew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btcopy)
                            .addComponent(btlookup)
                            .addComponent(btchangelog))))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel4))
                    .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel5))
                    .addComponent(tbline3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel6))
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel7))
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel8))
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel44))
                    .addComponent(tbmainphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel45))
                    .addComponent(tbmainemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        tbdatemod.setEnabled(false);

        jLabel10.setText("DateAdded");
        jLabel10.setName("lbldateadded"); // NOI18N

        jLabel13.setText("Last Order Date");
        jLabel13.setName("lbllastsaledate"); // NOI18N

        cbonhold.setText("On Hold");
        cbonhold.setName("cbonhold"); // NOI18N

        jLabel12.setText("Credit Limit");
        jLabel12.setName("lblcreditlimit"); // NOI18N

        jLabel11.setText("LastMod");
        jLabel11.setName("lbllastmod"); // NOI18N

        jLabel14.setText("Market");
        jLabel14.setName("lblmarket"); // NOI18N

        jLabel15.setText("Terms");
        jLabel15.setName("lblterms"); // NOI18N

        jLabel16.setText("Carrier");
        jLabel16.setName("lblcarrier"); // NOI18N

        jLabel17.setText("Freight Type");
        jLabel17.setName("lblfreighttype"); // NOI18N

        jLabel18.setText("Disc Code");
        jLabel18.setName("lbldisccode"); // NOI18N

        jLabel19.setText("Price Code");
        jLabel19.setName("lblpricecode"); // NOI18N

        jLabel20.setText("Tax Code");
        jLabel20.setName("lbltaxcode"); // NOI18N

        jLabel26.setText("SalesRep");
        jLabel26.setName("lblsalesrep"); // NOI18N

        jLabel27.setText("AR Account");
        jLabel27.setName("lblaracct"); // NOI18N

        jLabel28.setText("CostCenter");
        jLabel28.setName("lblcostcenter"); // NOI18N

        jLabel39.setText("Bank");
        jLabel39.setName("lblbank"); // NOI18N

        jLabel40.setText("Label Format");
        jLabel40.setName("lbllabelformat"); // NOI18N

        jLabel42.setText("Shp Format");
        jLabel42.setName("lblshipformat"); // NOI18N

        jLabel43.setText("Inv Format");
        jLabel43.setName("lblinvformat"); // NOI18N

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

        cbshipto.setText("Create Shipto Same as Billto");
        cbshipto.setName("cbshipto"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel41.setText("Currency");
        jLabel41.setName("lblcurrency"); // NOI18N

        cb855.setText("Export 855");

        cb856.setText("Export 856");

        cb810.setText("Export 810");

        jLabel46.setText("Site");

        jLabel47.setText("TaxID");
        jLabel47.setName("lbltaxid"); // NOI18N

        cbcascade.setText("Cascade");

        jLabel52.setText("Avg Days To Pay");
        jLabel52.setName("lblavgdays"); // NOI18N

        jLabel53.setText("High Balance");
        jLabel53.setName("lblhighbalance"); // NOI18N

        tbcreditlimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcreditlimitFocusLost(evt);
            }
        });

        jLabel54.setText("Group");
        jLabel54.setName("lblgroup"); // NOI18N

        jLabel55.setText("Last Pay Date");
        jLabel55.setName("lbllastpaydate"); // NOI18N

        cbtaxexempt.setText("Tax Exempt");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel16)
                            .addComponent(jLabel19)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28)
                            .addComponent(jLabel39)
                            .addComponent(jLabel41)
                            .addComponent(jLabel40)
                            .addComponent(jLabel42)
                            .addComponent(jLabel46)
                            .addComponent(jLabel47)
                            .addComponent(jLabel52)
                            .addComponent(jLabel55)
                            .addComponent(jLabel13))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tbtaxid)
                                        .addComponent(tbdateadded, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tblastselldate, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                        .addComponent(ddcarrier, 0, 171, Short.MAX_VALUE)
                                        .addComponent(tbpricecode, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                        .addComponent(tbsalesrep, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                        .addComponent(ddaccount, 0, 171, Short.MAX_VALUE)
                                        .addComponent(ddcc, 0, 171, Short.MAX_VALUE)
                                        .addComponent(ddbank, 0, 171, Short.MAX_VALUE)
                                        .addComponent(ddcurr, 0, 171, Short.MAX_VALUE)
                                        .addComponent(ddlabel, 0, 171, Short.MAX_VALUE)
                                        .addComponent(tbshpformat, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbavgdays)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tblastpaydate, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel53)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel20)
                            .addComponent(jLabel43)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbdatemod, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbgroup, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddfreightterms, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdisccode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbinvformat, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb855)
                            .addComponent(cb856)
                            .addComponent(cb810)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cbonhold)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbcascade))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbcreditlimit, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbhighbalance, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbmarket, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                            .addComponent(cbtaxexempt)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbshipto)
                        .addGap(2, 2, 2)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addGap(6, 6, 6)
                        .addComponent(btadd)
                        .addGap(21, 21, 21)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbdateadded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdatemod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tblastselldate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tblastpaydate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbavgdays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbhighbalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbgroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcreditlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel16))
                    .addComponent(ddcarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel14))
                    .addComponent(tbmarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel26))
                    .addComponent(tbsalesrep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel17))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(ddfreightterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel27))
                    .addComponent(ddaccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel18))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbdisccode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel20)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbtaxexempt)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel39))
                    .addComponent(ddbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbonhold)
                        .addComponent(cbcascade)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel41))
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel43))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbinvformat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel40))
                    .addComponent(ddlabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb855))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel42))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbshpformat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cb856))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(cb810))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtaxid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbshipto)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(btadd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(mainPanel);

        shiptoPanel.setBackground(new java.awt.Color(220, 220, 220));

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

        ddshipcountry.setPreferredSize(new java.awt.Dimension(80, 22));

        jLabel36.setText("ShipCode");
        jLabel36.setName("lblshipto"); // NOI18N

        jLabel37.setText("Name");
        jLabel37.setName("lblname"); // NOI18N

        jLabel38.setText("City");
        jLabel38.setName("lblcity"); // NOI18N

        btshipadd.setText("Add");
        btshipadd.setName("btadd"); // NOI18N
        btshipadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipaddActionPerformed(evt);
            }
        });

        btshipedit.setText("Update");
        btshipedit.setName("btupdate"); // NOI18N
        btshipedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipeditActionPerformed(evt);
            }
        });

        btshipnew.setText("New");
        btshipnew.setName("btnew"); // NOI18N
        btshipnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshipnewActionPerformed(evt);
            }
        });

        btlookupShipTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupShipTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupShipToActionPerformed(evt);
            }
        });

        jLabel48.setText("External Code");

        btclearshipto.setText("Clear");
        btclearshipto.setName("btclear"); // NOI18N
        btclearshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearshiptoActionPerformed(evt);
            }
        });

        btdeleteshipto.setText("Delete");
        btdeleteshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteshiptoActionPerformed(evt);
            }
        });

        tbshipemail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbshipemailActionPerformed(evt);
            }
        });

        jLabel49.setText("Email");

        tbshipphone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbshipphoneActionPerformed(evt);
            }
        });

        jLabel50.setText("Phone");

        jLabel51.setText("Contact");

        jLabel57.setText("Municipality");

        jLabel59.setText("County");

        btcopyshipto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btcopyshipto.setToolTipText("Copy");
        btcopyshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyshiptoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout shiptoPanelLayout = new javax.swing.GroupLayout(shiptoPanel);
        shiptoPanel.setLayout(shiptoPanelLayout);
        shiptoPanelLayout.setHorizontalGroup(
            shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shiptoPanelLayout.createSequentialGroup()
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shiptoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel59, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(shiptoPanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel49)
                            .addComponent(jLabel50)
                            .addComponent(jLabel51))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbxrefcode, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipemail, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipzip, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipphone, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddshipcountry, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddshipstate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipname, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipline1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipline2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipline3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbshipcounty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                        .addComponent(tbshipcity, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbshipmunicipality, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(shiptoPanelLayout.createSequentialGroup()
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shiptoPanelLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(btdeleteshipto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btshipedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btshipadd))
                    .addGroup(shiptoPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookupShipTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcopyshipto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btshipnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclearshipto)))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        shiptoPanelLayout.setVerticalGroup(
            shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shiptoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btshipnew)
                        .addComponent(btclearshipto))
                    .addComponent(tbshipcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btlookupShipTo)
                    .addComponent(btcopyshipto))
                .addGap(15, 15, 15)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipline3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipmunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipcounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbxrefcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(tbshipemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(tbshipphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addGap(18, 18, 18)
                .addGroup(shiptoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btshipadd)
                    .addComponent(btshipedit)
                    .addComponent(btdeleteshipto))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        add(shiptoPanel);

        contactPanel.setBackground(new java.awt.Color(220, 220, 220));

        contacttable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Type", "Name", "Phone", "Fax", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        contacttable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contacttableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(contacttable);

        btdeletecontact.setText("DeleteContact");
        btdeletecontact.setName("btdelete"); // NOI18N
        btdeletecontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletecontactActionPerformed(evt);
            }
        });

        btaddcontact.setText("AddContact");
        btaddcontact.setName("btadd"); // NOI18N
        btaddcontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcontactActionPerformed(evt);
            }
        });

        jLabel24.setText("ContactType");
        jLabel24.setName("lbltype"); // NOI18N

        jLabel21.setText("ContactName");
        jLabel21.setName("lblname"); // NOI18N

        ddcontacttype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sales", "Finance", "IT", "Admin", "Shipping", "Engineering", "Quality" }));

        jLabel23.setText("Email");
        jLabel23.setName("lblemail"); // NOI18N

        jLabel22.setText("Phone");
        jLabel22.setName("lblphone"); // NOI18N

        jLabel25.setText("Fax");
        jLabel25.setName("lblfax"); // NOI18N

        bteditcontact.setText("EditContact");
        bteditcontact.setName("btupdate"); // NOI18N
        bteditcontact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditcontactActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(ddcontacttype, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbcontactname))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(contactPanelLayout.createSequentialGroup()
                                .addComponent(tbfax, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bteditcontact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddcontact)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletecontact))
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(ddcontacttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(tbfax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeletecontact)
                    .addComponent(btaddcontact)
                    .addComponent(bteditcontact))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(tbcontactname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE))
        );

        add(contactPanel);

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
        jScrollPane4.setViewportView(tableattachment);

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
                    .addComponent(jScrollPane4))
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
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(panelAttachment);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btaddcontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcontactActionPerformed
                         addContact(tbkey.getText());
                         refreshContactTable(tbkey.getText());
    }//GEN-LAST:event_btaddcontactActionPerformed

    private void btdeletecontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletecontactActionPerformed
         int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
           deleteContact(tbkey.getText(), contacttable.getValueAt(i, 0).toString());
        }
       refreshContactTable(tbkey.getText());
       clearContacts();
    }//GEN-LAST:event_btdeletecontactActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
           return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btshipaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipaddActionPerformed
        if (! validateInputShipTo(dbaction.add)) {
           return;
        }
        if (OVData.isValidCustShipTo(tbkey.getText(),tbshipcode.getText())) {
                  bsmf.MainFrame.show(getMessageTag(1014));
                  return;
              } 
        addShipTo();
    }//GEN-LAST:event_btshipaddActionPerformed

    private void btshipeditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipeditActionPerformed
       if (! validateInputShipTo(dbaction.update)) {
           return;
       }
       if (OVData.isValidCustShipTo(tbkey.getText(),tbshipcode.getText())) {
           updateShipTo();
       } 
    }//GEN-LAST:event_btshipeditActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        if (autocust) {
          newAction("customer");
        } else {
           newAction("");
           tbkey.requestFocus();
         }
      
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btshipnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshipnewActionPerformed
        clearShipTo();
         if (autocust) {
              tbshipcode.setText(String.valueOf(OVData.getNextNbr("shipto")));
              tbshipcode.setEditable(false);
              btshipadd.setEnabled(true);
              btshipedit.setEnabled(false);
              btcopyshipto.setEnabled(false);
        } else {
              tbshipcode.setEditable(true);
              tbshipcode.requestFocus();
              btshipadd.setEnabled(true);
              btshipedit.setEnabled(false);
              btcopyshipto.setEnabled(false);
              
         }
      
    }//GEN-LAST:event_btshipnewActionPerformed

    private void bteditcontactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditcontactActionPerformed
               
        int[] rows = contacttable.getSelectedRows();
        for (int i : rows) {
           editContact(tbkey.getText(), contacttable.getValueAt(i, 0).toString());
        }
       refreshContactTable(tbkey.getText());
       clearContacts();
    }//GEN-LAST:event_bteditcontactActionPerformed

    private void contacttableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contacttableMouseClicked
        int row = contacttable.rowAtPoint(evt.getPoint());
        ddcontacttype.setSelectedItem(contacttable.getValueAt(row, 1).toString());
        tbcontactname.setText(contacttable.getValueAt(row, 2).toString());
        tbphone.setText(contacttable.getValueAt(row, 3).toString());
        tbfax.setText(contacttable.getValueAt(row, 4).toString());
        tbemail.setText(contacttable.getValueAt(row, 5).toString());
        
        
    }//GEN-LAST:event_contacttableMouseClicked

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

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame("");
    }//GEN-LAST:event_btlookupActionPerformed

    private void btlookupShipToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupShipToActionPerformed
        lookUpFrame("shipto");
    }//GEN-LAST:event_btlookupShipToActionPerformed

    private void btaddattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattachmentActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
        }
        OVData.addFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), this );
        getAttachments(tbkey.getText());
    }//GEN-LAST:event_btaddattachmentActionPerformed

    private void btdeleteattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattachmentActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
        }
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

    private void btclearshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearshiptoActionPerformed
        clearShipTo();
    }//GEN-LAST:event_btclearshiptoActionPerformed

    private void btdeleteshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteshiptoActionPerformed
        deleteShipTo(tbkey.getText(),tbshipcode.getText());
    }//GEN-LAST:event_btdeleteshiptoActionPerformed

    private void ddcountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcountryActionPerformed
        if (! isLoad && ddcountry.getSelectedItem() != null) {
           lblcountry.setText(OVData.getCodeValueByCodeKey("country", ddcountry.getSelectedItem().toString()));
        }
    }//GEN-LAST:event_ddcountryActionPerformed

    private void tbcreditlimitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcreditlimitFocusLost
          if (! tbcreditlimit.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbcreditlimit.getText(), "0");
            if (x.equals("error")) {
                tbcreditlimit.setText("");
                tbcreditlimit.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));   
                tbcreditlimit.requestFocus();
            } else {
                tbcreditlimit.setText(x);
                tbcreditlimit.setBackground(Color.white);
            }
        } else {
             tbcreditlimit.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbcreditlimitFocusLost

    private void tbshipemailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbshipemailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbshipemailActionPerformed

    private void tbshipphoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbshipphoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbshipphoneActionPerformed

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
        if (! isLoad) {
            tbkey.setText(bsNumber(OVData.getNextNbr("customer")));
            tbkey.setBackground(Color.yellow);
            //bsmf.MainFrame.show("choose new parent key and adjust ISA/GS IDs accordingly");
            tbkey.requestFocus();
            btadd.setEnabled(true);
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            btdelete.setEnabled(false);
            btupdate.setEnabled(false);
            btcopy.setEnabled(false);
        }
    }//GEN-LAST:event_btcopyActionPerformed

    private void btcopyshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyshiptoActionPerformed
        if (! isLoad) {
            tbshipcode.setText(String.valueOf(OVData.getNextNbr("shipto")));
            tbshipcode.setBackground(Color.yellow);
            //bsmf.MainFrame.show("choose new parent key and adjust ISA/GS IDs accordingly");
            tbkey.requestFocus();
            btshipadd.setEnabled(true);
            tbshipcode.setEnabled(true);
            tbshipcode.setEditable(false);
            btdeleteshipto.setEnabled(false);
            btshipedit.setEnabled(false);
            btcopyshipto.setEnabled(false);
        }
    }//GEN-LAST:event_btcopyshiptoActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (! isLoad) {
        JTabbedPane tabSource = (JTabbedPane) evt.getSource();
       String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());
    //   if (tab.equals("buy")) {
    //       bsmf.MainFrame.show("mustbe:" + tab);
    //   }
      
       switch(tab) {
                case "ShipTo":
                    clearShipTo();
                    break;
                    
                default:
                    return;
            }
      
       }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void btchangelogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchangelogActionPerformed
        callChangeDialog(tbkey.getText(), this.getClass().getSimpleName());
    }//GEN-LAST:event_btchangelogActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btaddcontact;
    private javax.swing.JButton btchangelog;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearshipto;
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btcopyshipto;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdeletecontact;
    private javax.swing.JButton btdeleteshipto;
    private javax.swing.JButton bteditcontact;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupShipTo;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btshipadd;
    private javax.swing.JButton btshipedit;
    private javax.swing.JButton btshipnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cb810;
    private javax.swing.JCheckBox cb855;
    private javax.swing.JCheckBox cb856;
    private javax.swing.JCheckBox cbcascade;
    private javax.swing.JCheckBox cbonhold;
    private javax.swing.JCheckBox cbshipto;
    private javax.swing.JCheckBox cbtaxexempt;
    private javax.swing.JPanel contactPanel;
    private javax.swing.JTable contacttable;
    private javax.swing.JComboBox<String> ddaccount;
    private javax.swing.JComboBox ddbank;
    private javax.swing.JComboBox ddcarrier;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddcontacttype;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddfreightterms;
    private javax.swing.JComboBox ddlabel;
    private javax.swing.JComboBox ddshipcountry;
    private javax.swing.JComboBox ddshipstate;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox ddterms;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lblcountry;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JPanel shiptoPanel;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTextField tbavgdays;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontactname;
    private javax.swing.JTextField tbcounty;
    private javax.swing.JTextField tbcreditlimit;
    private javax.swing.JTextField tbdateadded;
    private javax.swing.JTextField tbdatemod;
    private javax.swing.JTextField tbdisccode;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbfax;
    private javax.swing.JTextField tbgroup;
    private javax.swing.JTextField tbhighbalance;
    private javax.swing.JTextField tbinvformat;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tblastpaydate;
    private javax.swing.JTextField tblastselldate;
    private javax.swing.JTextField tbline1;
    private javax.swing.JTextField tbline2;
    private javax.swing.JTextField tbline3;
    private javax.swing.JTextField tbmainemail;
    private javax.swing.JTextField tbmainphone;
    private javax.swing.JTextField tbmarket;
    private javax.swing.JTextField tbmunicipality;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbpricecode;
    private javax.swing.JTextArea tbremarks;
    private javax.swing.JTextField tbsalesrep;
    private javax.swing.JTextField tbshipcity;
    private javax.swing.JTextField tbshipcode;
    private javax.swing.JTextField tbshipcontact;
    private javax.swing.JTextField tbshipcounty;
    private javax.swing.JTextField tbshipemail;
    private javax.swing.JTextField tbshipline1;
    private javax.swing.JTextField tbshipline2;
    private javax.swing.JTextField tbshipline3;
    private javax.swing.JTextField tbshipmunicipality;
    private javax.swing.JTextField tbshipname;
    private javax.swing.JTextField tbshipphone;
    private javax.swing.JTextField tbshipzip;
    private javax.swing.JTextField tbshpformat;
    private javax.swing.JTextField tbtaxid;
    private javax.swing.JTextField tbxrefcode;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
