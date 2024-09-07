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



package com.blueseer.edi;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import static com.blueseer.edi.ediData.updateAS2Transaction;
import static com.blueseer.edi.ediData.addAS2Transaction;
import com.blueseer.edi.ediData.as2_mstr;
import static com.blueseer.edi.ediData.deleteAS2Mstr;
import static com.blueseer.edi.ediData.getAS2Mstr;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.util.Store;

/**
 *
 * @author vaughnte
 */


public class AS2Maint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
    DefaultListModel listmodel = new DefaultListModel();
                boolean isLoad = false;
                public static Store certs = null;
                public static as2_mstr x = null;
                public static String rData = null;
    // global datatablemodel declarations   
   
    
  
    public AS2Maint() {
        initComponents();
        setLanguageTags(this);
        
    }

    // interface functions implemented
    public void executeTask(dbaction x, String[] y) { 
      
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
                    rData = null;
                    ArrayList<String[]> arrx = new ArrayList<String[]>();
                    arrx.add(new String[]{"id","postAS2"});
                    arrx.add(new String[]{"key", tbkey.getText()});
                    arrx.add(new String[]{"debug", String.valueOf(cbdebug.isSelected())});
                    try {
                        rData = sendServerPost(arrx, "", null);
                    } catch (IOException ex) {
                        rData = "Failed to run: " + ex.getMessage();
                    }
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
           } else if (this.type.equals("run")) {
             processRunPostAfter(); 
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
           tbdesc.setText("");
           tburl.setText("");
           tbport.setText("");
           tbas2id.setText("");
           tbsysas2id.setText("");
           tbpass.setText("");
           tbpath.setText("");
           
           tbindir.setText("");
           tboutdir.setText("");
           cbenabled.setSelected(false);
           taoutput.setText("");
           lblurl.setText("");
           lblstatus.setText("");
           lblstatus.setForeground(Color.black);
           cboutputencryption.setSelected(false);
           cboutputsign.setSelected(false);
           cbforceencryption.setSelected(false);
           cbforcesigning.setSelected(false);
           cbdebug.setSelected(false);
           
           ddsigncert.removeAllItems();
           ddenccert.removeAllItems();
        ArrayList<String> keys = admData.getAllPKSKeysExceptStore();
        for (String code : keys) {
            ddsigncert.addItem(code);
            ddenccert.addItem(code);
        }
        ddsigncert.insertItemAt("", 0);
        ddenccert.insertItemAt("", 0);
        ddsigncert.setSelectedIndex(0);
        ddenccert.setSelectedIndex(0); 
        if (! ddcontenttype.getItemAt(0).isBlank()) {
         ddcontenttype.insertItemAt("", 0);
        }
        ddcontenttype.setSelectedIndex(0);
        
        headerlist.setModel(listmodel);
        
        ddsite.removeAllItems();
        OVData.getSiteList().stream().forEach((s) -> ddsite.addItem(s));  
        ddsite.insertItemAt("", 0);
        ddsite.setSelectedIndex(0);
        
        ddinworkflow.removeAllItems();
        ddoutworkflow.removeAllItems();
        ArrayList<String> ids = ediData.getWkfMstrList();
        for (String id : ids) {
        ddinworkflow.addItem(id);
        ddoutworkflow.addItem(id);
        }
        ddinworkflow.insertItemAt("", 0);
        ddinworkflow.setSelectedIndex(0);
        ddoutworkflow.insertItemAt("", 0);
        ddoutworkflow.setSelectedIndex(0);
        
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
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
               
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (tbdesc.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbdesc.requestFocus();
                    return b;
                }
                if (ddcontenttype.getSelectedItem() == null || ddcontenttype.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    ddcontenttype.requestFocus();
                    return b;
                }
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
       
     //   bsmf.MainFrame.show("This functionality is a work-in-progress and not intended for production");
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
    String[] m = addAS2Transaction(createRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
    String[] m = new String[2];
        m = updateAS2Transaction(tbkey.getText(), createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteAS2Mstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       as2_mstr z = getAS2Mstr(key);     
        x = z;
        return x.m();
    }
    
    public as2_mstr createRecord() { 
         String passwd = bsmf.MainFrame.PassWord("0", tbpass.getPassword());
        as2_mstr x = new as2_mstr(null, 
                tbkey.getText(),
                tbdesc.getText(),
                "", // version
                tburl.getText(),
                tbport.getText(),
                tbpath.getText(),
                tbas2id.getText(),
                passwd,
                tbapikey.getText(),
                ddprotocol.getSelectedItem().toString(),
                ddclass.getSelectedItem().toString(),
                tbindir.getText(),
                tboutdir.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cboutputencryption.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cboutputsign.isSelected())),
                ddenccert.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbforceencryption.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbforcesigning.isSelected())),
                ddsigncert.getSelectedItem().toString(),
                ddencalgo.getSelectedItem().toString(),
                ddsignalgo.getSelectedItem().toString(),
                ddmicalgo.getSelectedItem().toString(),
                ddcontenttype.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbenabled.isSelected())),
                tbsysas2id.getText(),
                ddsite.getSelectedItem().toString(),
                ddinworkflow.getSelectedItem().toString(),
                ddoutworkflow.getSelectedItem().toString()
                );
        return x;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAS2BrowseUtil(luinput.getText(),0, "as2_id"); 
        } else {
         luModel = DTData.getAS2BrowseUtil(luinput.getText(),0, "as2_desc");   
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
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()));
        
    }

    public void updateForm() {
        
        tbkey.setText(x.as2_id());
        tbdesc.setText(x.as2_desc());
        tburl.setText(x.as2_url());
        tbport.setText(x.as2_port());
        tbpath.setText(x.as2_path());
        tbas2id.setText(x.as2_user());
        tbpass.setText(bsmf.MainFrame.PassWord("1", x.as2_pass().toCharArray()));
        tbapikey.setText(x.as2_key());
        ddprotocol.setSelectedItem(x.as2_protocol());
        ddclass.setSelectedItem(x.as2_class());
        tbindir.setText(x.as2_indir());
        tboutdir.setText(x.as2_outdir());
        ddenccert.setSelectedItem(x.as2_enccert());
        cboutputsign.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.as2_signed())));
        cboutputencryption.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.as2_encrypted())));
        ddsigncert.setSelectedItem(x.as2_signcert());
        cbforcesigning.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.as2_forcesigned())));
        cbforceencryption.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.as2_forceencrypted())));
        ddencalgo.setSelectedItem(x.as2_encalgo());
        ddsignalgo.setSelectedItem(x.as2_signalgo());
        ddmicalgo.setSelectedItem(x.as2_micalgo());
        ddcontenttype.setSelectedItem(x.as2_contenttype());
        cbenabled.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.as2_enabled())));
        tbsysas2id.setText(x.as2_sysas2id());
        ddsite.setSelectedItem(x.as2_site());
        ddinworkflow.setSelectedItem(x.as2_inwkf());
        ddoutworkflow.setSelectedItem(x.as2_outwkf());
        getAttributes(x.as2_id());
        setAction(x.m());
    }
    
    public void getAttributes(String as2id) {
        listmodel.removeAllElements();
        ArrayList<String> list = EDData.getAS2AttributesList(as2id, "httpheader");
        for (String x : list) {
            listmodel.addElement(x);
        }
    }
    
    public void processRun() {
         
        String r = null;
        Path folderpath = FileSystems.getDefault().getPath(tbindir.getText());
        File folder = new File(folderpath.toString());
        File[] listOfFiles = folder.listFiles();
        taoutput.append("number of files to transmit: ");
        if (listOfFiles != null) {
         taoutput.append(String.valueOf(listOfFiles.length));
        } else {
         taoutput.append("null source directory");   
        }
        taoutput.append("\n");
        
        if (listOfFiles != null) {        
            try {
                r = apiUtils.postAS2(tbkey.getText(), cbdebug.isSelected());
            } catch (URISyntaxException ex) {
                bslog(ex);
            } catch (IOException ex) {
                bslog(ex);
            } catch (CertificateException ex) {
                bslog(ex);
            } catch (NoSuchProviderException ex) {
                bslog(ex);
            } catch (KeyStoreException ex) {
                bslog(ex);
            } catch (NoSuchAlgorithmException ex) {
                bslog(ex);
            } catch (UnrecoverableKeyException ex) {
                bslog(ex);
            } catch (CMSException ex) {
                bslog(ex);
            } catch (SMIMEException ex) {
                bslog(ex);
            } catch (Exception ex) {
                bslog(ex);
            }
        }  
            
        if (r != null) {
         taoutput.append(r);
         Pattern p = Pattern.compile("status__pass");
		Matcher m = p.matcher(r);
		if (m.find()) {
                    lblstatus.setText("PASS!");
                    lblstatus.setForeground(Color.blue);
                } else {
                    lblstatus.setText("FAIL!");
                    lblstatus.setForeground(Color.red);  
                }
        
        } else {
            taoutput.append("null return");
        }
    }
    
    public void processRunPostAfter() {
        if (rData != null) {
         taoutput.append(rData);
         Pattern p = Pattern.compile("status__pass");
		Matcher m = p.matcher(rData);
		if (m.find()) {
                    lblstatus.setText("PASS!");
                    lblstatus.setForeground(Color.blue);
                } else {
                    lblstatus.setText("FAIL!");
                    lblstatus.setForeground(Color.red);  
                }
        
        } else {
            taoutput.append("null return");
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

        jPanel1 = new javax.swing.JPanel();
        btupdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cbenabled = new javax.swing.JCheckBox();
        tbindir = new javax.swing.JTextField();
        tboutdir = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        headerlist = new javax.swing.JList<>();
        btdeleteheader = new javax.swing.JButton();
        btaddheader = new javax.swing.JButton();
        tbheadertag = new javax.swing.JTextField();
        tbheadervalue = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        ddinworkflow = new javax.swing.JComboBox<>();
        ddoutworkflow = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnew = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        tbdesc = new javax.swing.JTextField();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        tburl = new javax.swing.JTextField();
        tbport = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbpath = new javax.swing.JTextField();
        tbas2id = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ddprotocol = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        ddclass = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cbdebug = new javax.swing.JCheckBox();
        ddcontenttype = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        tbsysas2id = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        btrun = new javax.swing.JButton();
        lblurl = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cboutputencryption = new javax.swing.JCheckBox();
        cboutputsign = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbforceencryption = new javax.swing.JCheckBox();
        cbforcesigning = new javax.swing.JCheckBox();
        ddencalgo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        ddsignalgo = new javax.swing.JComboBox<>();
        ddmicalgo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        ddenccert = new javax.swing.JComboBox<>();
        ddsigncert = new javax.swing.JComboBox<>();
        tbapikey = new javax.swing.JTextField();
        tbpass = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("AS2 Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Additional Info"));
        jPanel2.setName("paneldetail"); // NOI18N

        cbenabled.setText("Enabled");
        cbenabled.setName("cbenabled"); // NOI18N

        jLabel13.setText("InDir");
        jLabel13.setName("lblsourcedir"); // NOI18N

        jLabel14.setText("OutDir");
        jLabel14.setName("lbldestdir"); // NOI18N

        jLabel19.setText("HTTP Tag");

        jLabel20.setText("HTTP Value");

        jScrollPane1.setViewportView(headerlist);

        btdeleteheader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteheader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteheaderActionPerformed(evt);
            }
        });

        btaddheader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddheader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddheaderActionPerformed(evt);
            }
        });

        jLabel23.setText("In Workflow");

        jLabel24.setText("Out Workflow");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbheadertag, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel23)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ddinworkflow, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addGap(219, 219, 219)
                                    .addComponent(jLabel24)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ddoutworkflow, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(69, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbheadervalue, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btdeleteheader, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddheader, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tboutdir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbenabled)
                        .addGap(36, 36, 36))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel23)
                    .addComponent(ddinworkflow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(ddoutworkflow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(tbheadertag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btaddheader, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeleteheader, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(cbenabled)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(tbheadervalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("AS2 Header Maintenance"));
        jPanel3.setName("panelmaster"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("Key");
        jLabel5.setName("lblid"); // NOI18N

        jLabel6.setText("Desc");
        jLabel6.setName("lbldesc"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel1.setText("Port");
        jLabel1.setName("lblport"); // NOI18N

        jLabel7.setText("URL");
        jLabel7.setName("lblurl"); // NOI18N

        jLabel9.setText("Path");
        jLabel9.setName("lblpath"); // NOI18N

        jLabel10.setText("AS2 ID");
        jLabel10.setName("lblas2id"); // NOI18N

        ddprotocol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "http", "https" }));

        jLabel17.setText("Protocol");

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AS2", "HTTPS", "SOAP" }));

        jLabel16.setText("Class");
        jLabel16.setName("lblclass"); // NOI18N

        cbdebug.setText("Debug");

        ddcontenttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "application/edi-x12", "application/edifact", "application/pdf", "application/json", "application/xml", "application/zip", "application/x-www-form-urlencoded", "image/gif", "image/jpeg", "image/png", "image/tiff", "multipart/mixed", "multipart/alternative", "text/csv", "text/html", "text/plain", "text/xml" }));

        jLabel15.setText("Content-Type");
        jLabel15.setName("lblcontenttype"); // NOI18N

        jLabel21.setText("System AS2 ID");

        jLabel22.setText("Site");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel15)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddcontenttype, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbdebug))
                            .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(tbas2id, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel16))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(tbsysas2id, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel22))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(tburl, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel17)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddclass, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbport)
                                    .addComponent(ddprotocol, 0, 100, Short.MAX_VALUE))))
                        .addGap(38, 38, 38))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear)
                        .addComponent(cbdebug))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tburl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(ddprotocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbas2id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsysas2id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcontenttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        taoutput.setColumns(20);
        taoutput.setRows(5);
        taoutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Text"));
        jScrollPane2.setViewportView(taoutput);

        btrun.setText("Run");
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Cert Details"));

        cboutputencryption.setText("Output Encryption");

        cboutputsign.setText("Output Sign");

        jLabel18.setText("Encryption Cert");

        jLabel2.setText("Signing Cert");

        cbforceencryption.setText("Force Inbound Encryption");

        cbforcesigning.setText("Force Inbound Signing");

        ddencalgo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AES128_CBC", "AES192_CBC", "AES256_CBC", "DES_CBC", "DES_EDE3_CBC" }));

        jLabel3.setText("Encryption Algorithm");

        ddsignalgo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RSA" }));

        ddmicalgo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SHA-1", "SHA-256", "MD5" }));

        jLabel4.setText("Signing Algorithm");

        jLabel12.setText("MIC Algorithm");

        jLabel8.setText("User");

        jLabel11.setText("Password");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboutputsign)
                                    .addComponent(cboutputencryption))
                                .addGap(40, 40, 40)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbforceencryption)
                                    .addComponent(cbforcesigning)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddsigncert, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddenccert, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddsignalgo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddencalgo, javax.swing.GroupLayout.Alignment.LEADING, 0, 130, Short.MAX_VALUE)
                                .addComponent(ddmicalgo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(16, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboutputencryption)
                    .addComponent(cbforceencryption))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboutputsign)
                    .addComponent(cbforcesigning))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(ddenccert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ddsigncert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddencalgo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsignalgo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmicalgo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblurl, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addGap(6, 6, 6)
                        .addComponent(btadd))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(714, 714, 714)
                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btrun)
                .addGap(64, 64, 64))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblurl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btdelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btupdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btadd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblstatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btrun, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
            newAction("task");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
       BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        taoutput.setText("");
        lblurl.setText("");
        lblstatus.setText("");
        
        if (bsmf.MainFrame.remoteDB) {
          executeTask(dbaction.run, new String[]{tbkey.getText()});  
        } else {
          processRun();  
        }
        
        
           
    }//GEN-LAST:event_btrunActionPerformed

    private void btdeleteheaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteheaderActionPerformed
        boolean proceed = true;

        if (headerlist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1029));
        } else {
            proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
            String[] z = headerlist.getSelectedValue().toString().split(":");
            EDData.deleteAS2attribute(tbkey.getText(), "httpheader", z[0]);
            getAttributes(tbkey.getText());
        }
    }//GEN-LAST:event_btdeleteheaderActionPerformed

    private void btaddheaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddheaderActionPerformed

        EDData.addAS2AttributeRecord(tbkey.getText(), "httpheader", tbheadertag.getText(), tbheadervalue.getText());
        getAttributes(tbkey.getText());
        tbheadertag.setText("");
        tbheadervalue.setText("");
    }//GEN-LAST:event_btaddheaderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddheader;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteheader;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbdebug;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JCheckBox cbforceencryption;
    private javax.swing.JCheckBox cbforcesigning;
    private javax.swing.JCheckBox cboutputencryption;
    private javax.swing.JCheckBox cboutputsign;
    private javax.swing.JComboBox<String> ddclass;
    private javax.swing.JComboBox<String> ddcontenttype;
    private javax.swing.JComboBox<String> ddencalgo;
    private javax.swing.JComboBox<String> ddenccert;
    private javax.swing.JComboBox<String> ddinworkflow;
    private javax.swing.JComboBox<String> ddmicalgo;
    private javax.swing.JComboBox<String> ddoutworkflow;
    private javax.swing.JComboBox<String> ddprotocol;
    private javax.swing.JComboBox<String> ddsignalgo;
    private javax.swing.JComboBox<String> ddsigncert;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JList<String> headerlist;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblurl;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextField tbapikey;
    private javax.swing.JTextField tbas2id;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbheadertag;
    private javax.swing.JTextField tbheadervalue;
    private javax.swing.JTextField tbindir;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tboutdir;
    private javax.swing.JPasswordField tbpass;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbport;
    private javax.swing.JTextField tbsysas2id;
    private javax.swing.JTextField tburl;
    // End of variables declaration//GEN-END:variables
}
