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
import static bsmf.MainFrame.tags;
import static com.blueseer.edi.ediData.addAPITransaction;
import com.blueseer.edi.ediData.api_det;
import com.blueseer.edi.ediData.api_mstr;
import com.blueseer.edi.ediData.apid_meta;
import static com.blueseer.edi.ediData.deleteAPIMstr;
import static com.blueseer.edi.ediData.getAPIDMeta;
import static com.blueseer.edi.ediData.getAPIDet;
import static com.blueseer.edi.ediData.getAPIMethodsList;
import static com.blueseer.edi.ediData.getAPIMstr;
import static com.blueseer.edi.ediData.isAPIMethodUnique;
import static com.blueseer.edi.ediData.updateAPIDetTransaction;
import static com.blueseer.edi.ediData.updateAPITransaction;
import static com.blueseer.utl.BlueSeerUtils.ConvertBoolToYesNo;
import static com.blueseer.utl.BlueSeerUtils.ConvertIntToBoolString;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.util.encoders.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.swing.BorderFactory;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.table.TableColumnModel;


import org.bouncycastle.util.Store;

/**
 *
 * @author vaughnte
 */


public class APIMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
                boolean isLoad = false;
                Locale locale = Locale.getDefault();
                public static Store certs = null;
                public static api_mstr x = null;
                public static ArrayList<apid_meta> apidmlist = null;
                public static LinkedHashMap<String, ArrayList<String[]>> apidm = new  LinkedHashMap<String, ArrayList<String[]>>();
                boolean hasInit = false;
     // global datatablemodel declarations   
     javax.swing.table.DefaultTableModel detailmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("method"), 
                getGlobalColumnTag("verb"),
                getGlobalColumnTag("type"),
                getGlobalColumnTag("sequence"),
                getGlobalColumnTag("value"),
                getGlobalColumnTag("source"),
                getGlobalColumnTag("destination"),
                getGlobalColumnTag("enabled")
                
            });
    
     DefaultListModel kvmodel = new DefaultListModel();
  
    public APIMaint() {
        initComponents();
        setLanguageTags(this);
       // bsmf.MainFrame.show("this is a work-in-progress...not for production");
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
                       if (locale.getLanguage().equals("ar")) {
                        component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                       }
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                       if (locale.getLanguage().equals("ar")) {
                        component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                       }
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                       if (locale.getLanguage().equals("ar")) {
                        component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                       }
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                       if (locale.getLanguage().equals("ar")) {
                        component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                       }
                    } 
                }
       }
    }
    
    public void setComponentDefaultValues() {
        isLoad = true;
        
        
        
        kvmodel.removeAllElements();
        listkv.setModel(kvmodel);
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Notes", panelNotes);
        jTabbedPane1.add("Output", panelOutput);
        
        tbkey.setText("");
        detailmodel.setRowCount(0);
        tabledetail.setModel(detailmodel);
        
        if (! hasInit) {
            TableColumnModel tcm = tabledetail.getColumnModel();
        tcm.removeColumn(tcm.getColumn(2));
        tcm.removeColumn(tcm.getColumn(2));
        tcm.removeColumn(tcm.getColumn(2));
        tcm.removeColumn(tcm.getColumn(2));
        tcm.removeColumn(tcm.getColumn(2));
        hasInit = true;
        }
        
        tbkey.setText("");
        tbdesc.setText("");
        tburl.setText("");
        tbport.setText("");
        tbuser.setText("");
        tbpass.setText("");
        tbpath.setText("");
        tbsequence.setText("");
        tbmethod.setText("");
        tbkvpair.setText("");
        tbsourcedir.setText("");
        tbdestdir.setText("");
        cbenabled.setSelected(false);
        taoutput.setText("");
        tanotes.setText("");
        tburlstring.setText("");
        ddcontenttype.setSelectedIndex(0);
        ddauth.setSelectedIndex(0);
        tbapikey.setText("");
        tbkeylabel.setText("");
        isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        btaddparam.setEnabled(false);
        btdeleteparam.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        tburlstring.setEnabled(false);
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
                   btaddparam.setEnabled(false);
                   btdeleteparam.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   btrun.setEnabled(false);
        } else {
                   tbkey.setForeground(Color.red); 
        }
        tburlstring.setEnabled(false);
    }
    
    public boolean validateInput(dbaction x) {
           
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"api_mstr"});
        
        int fc = checkLength(f,"api_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"api_desc");
        if (tbdesc.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"api_url");
        if (tburl.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tburl.requestFocus();
            return false;
        }
        fc = checkLength(f,"api_port");
        if (tbport.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbport.requestFocus();
            return false;
        }
        fc = checkLength(f,"api_path");
        if (tbpath.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbpath.requestFocus();
            return false;
        }
        fc = checkLength(f,"api_user");
        if (tbuser.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbuser.requestFocus();
            return false;
        }
        fc = checkLength(f,"api_pass");
        if (tbpass.getPassword().length > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbpass.requestFocus();
            return false;
        }
        fc = checkLength(f,"api_key");
        if (tbapikey.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbapikey.requestFocus();
            return false;
        }
                
                
                
               
        return true;
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
    String[] m = addAPITransaction(createAPIDMetaRecord(), createDetRecord(), createRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
    String[] m = new String[2];
        // first delete any api_det method key records that have been
        // disposed from the current tabledetail
        ArrayList<String> methods = new ArrayList<String>();
        ArrayList<String> badmethods = new ArrayList<String>();
        boolean goodMethod = false;
        methods = getAPIMethodsList(tbkey.getText());  
       for (String method : methods) {
          goodMethod = false;
          for (int j = 0; j < tabledetail.getRowCount(); j++) {
             if (tabledetail.getModel().getValueAt(j, 0).toString().toLowerCase().equals(method.toLowerCase())) {
                 goodMethod = true;
             }
          }
          if (! goodMethod) {
              badmethods.add(method);
          }
        }
        m = updateAPITransaction(tbkey.getText(), badmethods, createAPIDMetaRecord(), createDetRecord(), createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteAPIMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       api_mstr z = getAPIMstr(key);     
        x = z;
        apidmlist = getAPIDMeta(key[0]); 
        return x.m();
    }
    
    public api_mstr createRecord() { 
         String passwd = bsmf.MainFrame.PassWord("0", tbpass.getPassword());
        api_mstr x = new api_mstr(null, 
                tbkey.getText(),
                tbdesc.getText(),
                "", // version
                tburl.getText(),
                tbport.getText(),
                tbpath.getText(),
                tbuser.getText(),
                passwd,
                tbapikey.getText(),
                tbkeylabel.getText(),
                ddprotocol.getSelectedItem().toString(),
                ddclass.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbresponseheaders.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbrequestheaders.isSelected())),
                ddcontenttype.getSelectedItem().toString(),
                ddauth.getSelectedItem().toString(),
                "", //char1
                "", //char2
                "" // char3
                );
        return x;
    }
    
    public ArrayList<api_det> createDetRecord() {
        ArrayList<api_det> list = new ArrayList<api_det>();
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             api_det x = new api_det(null, 
                tbkey.getText(),
                tabledetail.getModel().getValueAt(j, 0).toString(),
                tabledetail.getModel().getValueAt(j, 3).toString(),
                tabledetail.getModel().getValueAt(j, 1).toString(),
                tabledetail.getModel().getValueAt(j, 2).toString(),
                tbpath.getText(),
                "", // key not used
                tabledetail.getModel().getValueAt(j, 4).toString(),
                tabledetail.getModel().getValueAt(j, 5).toString(),
                tabledetail.getModel().getValueAt(j, 6).toString(),
                String.valueOf(BlueSeerUtils.boolToInt(Boolean.valueOf(tabledetail.getModel().getValueAt(j, 7).toString()))),
                "", //char1
                "", //char2
                "" // char3
                );
       
        list.add(x);
         }
        return list;   
    }
    
    public ArrayList<apid_meta> createAPIDMetaRecord() {
        ArrayList<apid_meta> list = new ArrayList<apid_meta>();
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             for (Map.Entry<String, ArrayList<String[]>> z : apidm.entrySet()) {
    		 if (z.getKey().equals(tabledetail.getModel().getValueAt(j, 0).toString())) {
    			 ArrayList<String[]> k = z.getValue();
    			 for (String[] g : k) {
                                apid_meta x = new apid_meta(null, 
                                tbkey.getText(),
                                tabledetail.getModel().getValueAt(j, 0).toString(),
                                g[0],
                                g[1]); 
                            list.add(x);
    			 }
    		 }
    	    }
         }
        return list;   
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAPIBrowseUtil(luinput.getText(),0, "api_id"); 
        } else {
         luModel = DTData.getAPIBrowseUtil(luinput.getText(),0, "api_desc");   
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
        
        tbkey.setText(x.api_id());
        tbdesc.setText(x.api_desc());
        tburl.setText(x.api_url());
        tbport.setText(x.api_port());
        tbpath.setText(x.api_path());
        tbuser.setText(x.api_user());
        tbpass.setText(bsmf.MainFrame.PassWord("1", x.api_pass().toCharArray()));
        tbapikey.setText(x.api_key());
        tbkeylabel.setText(x.api_keylabel());
        ddprotocol.setSelectedItem(x.api_protocol());
        ddclass.setSelectedItem(x.api_class());
        ddcontenttype.setSelectedItem(x.api_contenttype()); 
        ddauth.setSelectedItem(x.api_auth());
        cbrequestheaders.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.api_signed())));
        cbresponseheaders.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.api_encrypted())));
        // now detail
        detailmodel.setRowCount(0);
        ArrayList<api_det> z = getAPIDet(x.api_id());
        for (api_det d : z) {
            detailmodel.addRow(new Object[]{d.apid_method(), d.apid_verb(), d.apid_type(),
                 d.apid_seq(), d.apid_value(), d.apid_source(), d.apid_destination(), ConvertIntToBoolString(Integer.valueOf(d.apid_enabled()))});
        }
        
        // now  meta
           Set<String> set = new LinkedHashSet<String>();
           for (apid_meta a : apidmlist) { 
                set.add(a.apidm_method());
           }
           for (String s : set) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            for (apid_meta a : apidmlist) { 
                 if (! a.apidm_method().equals(s)) {
                     continue;
                 }
                 list.add(new String[]{a.apidm_key(), a.apidm_value()});
            }
            apidm.put(s, list);
           }
        
          
           
        setAction(x.m());
    }
    
    public void setURL() {
        tburlstring.setText("");
        int i = tabledetail.getSelectedRow();
        String method = tabledetail.getModel().getValueAt(i, 0).toString();
        String verb = tabledetail.getModel().getValueAt(i, 1).toString();
        String methodpath = tabledetail.getModel().getValueAt(i, 4).toString();
        String urlstring = "";
        String port = "";
           
          
        //override methodpath if it's blank (not specified) with the meta params tables
        if (methodpath.isBlank() && ddclass.getSelectedItem().toString().equals("PARAM")) {
            ArrayList<String[]> list = apidm.get(tabledetail.getModel().getValueAt(i, 0).toString());
            if (list != null) {
                for (String[] s : list) {
                    methodpath = methodpath + s[0] + "=" + s[1] + "&";
                }
                if (methodpath.endsWith("&")) {
                    methodpath = methodpath.substring(0, methodpath.length() - 1);
                }
            }
        }
        
        // tack on api key in param string if APIKEY auth
        // note...if they want it in the header...then select ddauth = NONE and put in param table
        if (ddauth.getSelectedItem().toString().equals("APIKEY")) {
                if (! tbapikey.getText().isBlank()) {
                methodpath = methodpath + "&" + tbkeylabel.getText() + "=" + tbapikey.getText();   
                }
        }
        /*
        if (! tbapikey.getText().isBlank()) {
            value = value + "api_key=" + tbapikey.getText();
        }
        */
        if (! methodpath.isBlank() && ddclass.getSelectedItem().toString().equals("PARAM")) {
        methodpath = "?" + methodpath;
        }
        
        
        if (tbport.getText().isBlank()) {  
           port = ""; 
        } else {
           port = ":" + tbport.getText();
        }

       
        urlstring = ddprotocol.getSelectedItem().toString() + "://" + tburl.getText() + port + tbpath.getText() + methodpath;
       
        tburlstring.setText(urlstring);
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
        tbuser = new javax.swing.JTextField();
        tbpass = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbapikey = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ddprotocol = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        ddclass = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cbresponseheaders = new javax.swing.JCheckBox();
        cbrequestheaders = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        tburlstring = new javax.swing.JTextField();
        btupdateurl = new javax.swing.JButton();
        cbfile = new javax.swing.JCheckBox();
        btrun = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        ddcontenttype = new javax.swing.JComboBox<>();
        ddauth = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        tbkeylabel = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tbsourcedir = new javax.swing.JTextField();
        tbdestdir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        ddverb = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        tbsequence = new javax.swing.JTextField();
        cbenabled = new javax.swing.JCheckBox();
        tbmethod = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbkvpair = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btdeleteparam = new javax.swing.JButton();
        btaddparam = new javax.swing.JButton();
        tbattributevalue = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        listkv = new javax.swing.JList<>();
        tbattributekey = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btupdateparam = new javax.swing.JButton();
        btadddetail = new javax.swing.JButton();
        btdeletedetail = new javax.swing.JButton();
        btupdatedetail = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        panelOutput = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        panelNotes = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tanotes = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("API Maintenance"));
        panelMain.setName("panelmain"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("API Header Maintenance"));
        jPanel3.setName("panelmaster"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("API ID");
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

        jLabel8.setText("Pasword");
        jLabel8.setName("lblpass"); // NOI18N

        jLabel9.setText("Path");
        jLabel9.setName("lblpath"); // NOI18N

        jLabel10.setText("UserID");
        jLabel10.setName("lbluser"); // NOI18N

        jLabel11.setText("Key");
        jLabel11.setName("lblapikey"); // NOI18N

        ddprotocol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "http", "https" }));

        jLabel17.setText("Protocol");

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "REST", "SOAP", "PARAM" }));

        jLabel16.setText("Class");
        jLabel16.setName("lblclass"); // NOI18N

        cbresponseheaders.setText("Response Headers");

        cbrequestheaders.setText("Request Headers");

        jLabel18.setText("Content");

        jLabel21.setText("URL result");
        jLabel21.setName("lblresult"); // NOI18N

        btupdateurl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gear.png"))); // NOI18N
        btupdateurl.setToolTipText("URL Override");
        btupdateurl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateurlActionPerformed(evt);
            }
        });

        cbfile.setText("File");

        btrun.setText("Run");
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        ddcontenttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "text/plain", "text/css", "text/csv", "text/html", "text/javascript", "text/xml", "application/pdf", "application/json", "application/ld+json", "application/xml", "application/xhtml+xml", "application/zip", "application/EDI-X12", "application/EDIFACT", "application/octet-stream", "application/x-www-form-urlencoded", "audio/mpeg", "image/gif", "image/jpeg", "image/png", "image/tiff", "image/vnd.microsoft.icon", "image/x-icon", "image/vnd.djvu", "image/svg+xml", "multipart/mixed", "multipart/alternative", "multipart/form-data", "video/mpeg", "video/mp4", "video/quicktime", "video/x-ms-wmv", "video/x-msvideo", "video/x-flv", "video/webm" }));

        ddauth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "BASIC AUTH", "APIKEY", "OAUTH2", "JWT", "OIDC" }));

        jLabel22.setText("Authentication");

        jLabel23.setText("Key Label");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tburlstring, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdateurl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(79, 79, 79)
                        .addComponent(btrun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbfile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addGap(6, 6, 6)
                        .addComponent(btadd)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tburl, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddclass, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddprotocol, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel8)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbkeylabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddauth, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddcontenttype, 0, 239, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cbresponseheaders, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbrequestheaders)
                        .addGap(311, 311, 311))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tburlstring, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21))
                    .addComponent(btupdateurl)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btrun)
                        .addComponent(cbfile))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btdelete)
                        .addComponent(btupdate)
                        .addComponent(btadd)))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tburl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddprotocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(tbkeylabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddauth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel18)
                    .addComponent(ddcontenttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbresponseheaders)
                    .addComponent(cbrequestheaders))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Methods"));

        jLabel2.setText("Sequence");
        jLabel2.setName("lblsequence"); // NOI18N

        ddverb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "GET", "PUT", "POST", "DELETE" }));

        jLabel3.setText("Method");
        jLabel3.setName("lblmethod"); // NOI18N

        jLabel13.setText("SourceDir");
        jLabel13.setName("lblsourcedir"); // NOI18N

        jLabel14.setText("DestDir");
        jLabel14.setName("lbldestdir"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "json", "xml", "text" }));

        jLabel15.setText("Type");
        jLabel15.setName("lbltype"); // NOI18N

        cbenabled.setText("Enabled?");
        cbenabled.setName("cbenabled"); // NOI18N

        jLabel4.setText("Verb");
        jLabel4.setName("lblverb"); // NOI18N

        jLabel12.setText("QueryParam");
        jLabel12.setName("lblkvpair"); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));

        btdeleteparam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteparam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteparamActionPerformed(evt);
            }
        });

        btaddparam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddparam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddparamActionPerformed(evt);
            }
        });

        listkv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listkvMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(listkv);

        jLabel19.setText("Key");

        jLabel20.setText("Value");

        btupdateparam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btupdateparam.setToolTipText("Update Element");
        btupdateparam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateparamActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btupdateparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbattributekey, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbattributevalue, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbattributekey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbattributevalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btaddparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btdeleteparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btupdateparam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addContainerGap())
        );

        btadddetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btadddetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadddetailActionPerformed(evt);
            }
        });

        btdeletedetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeletedetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletedetailActionPerformed(evt);
            }
        });

        btupdatedetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btupdatedetail.setToolTipText("Update Row");
        btupdatedetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatedetailActionPerformed(evt);
            }
        });

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabledetail);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbenabled)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddverb, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdestdir, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsourcedir, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbmethod)
                        .addComponent(tbkvpair, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btadddetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btdeletedetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(btupdatedetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbenabled)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddverb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbkvpair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsourcedir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdestdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btadddetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btdeletedetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btupdatedetail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(panelMain);

        taoutput.setColumns(20);
        taoutput.setRows(5);
        taoutput.setBorder(javax.swing.BorderFactory.createTitledBorder("Text"));
        jScrollPane2.setViewportView(taoutput);

        javax.swing.GroupLayout panelOutputLayout = new javax.swing.GroupLayout(panelOutput);
        panelOutput.setLayout(panelOutputLayout);
        panelOutputLayout.setHorizontalGroup(
            panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
            .addGroup(panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelOutputLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelOutputLayout.setVerticalGroup(
            panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
            .addGroup(panelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelOutputLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        add(panelOutput);

        tanotes.setColumns(20);
        tanotes.setRows(5);
        tanotes.setBorder(javax.swing.BorderFactory.createTitledBorder("Text"));
        jScrollPane4.setViewportView(tanotes);

        javax.swing.GroupLayout panelNotesLayout = new javax.swing.GroupLayout(panelNotes);
        panelNotes.setLayout(panelNotesLayout);
        panelNotesLayout.setHorizontalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
            .addGroup(panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelNotesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelNotesLayout.setVerticalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
            .addGroup(panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelNotesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        add(panelNotes);
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
        
        jTabbedPane1.setSelectedIndex(1);
        
        int[] rows = tabledetail.getSelectedRows();
       // taoutput.removeAll();
        taoutput.setText("");
        if (rows.length == 0 || tburlstring.getText().isBlank()) {
            bsmf.MainFrame.show("no method selected in detail table");
            return;
        }
        
       
        int i = tabledetail.getSelectedRow();
        String type = tabledetail.getModel().getValueAt(i, 2).toString();
        String verb = tabledetail.getModel().getValueAt(i, 1).toString();
        String method = tabledetail.getModel().getValueAt(i, 0).toString();
        HttpURLConnection conn = null;
       
        try {
                       
            URL url = new URL(tburlstring.getText());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", ddcontenttype.getSelectedItem().toString());
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            if (ddauth.getSelectedItem().toString().equals("BASIC AUTH")) {
               if (! tbuser.getText().isBlank() && tbpass.getPassword().length > 0) {
                String userCredentials = new String(tbuser.getText() + ":" + String.valueOf(tbpass.getPassword()));
                String basicAuth = "Basic " + Base64.toBase64String(userCredentials.getBytes());
                conn.setRequestProperty("Authorization", basicAuth);
                } 
            }
            
            StringBuilder requestHeaders = new StringBuilder();
            
            
            if (ddclass.getSelectedItem().toString().equals("REST")) {
                conn.setRequestMethod(verb);
                if (verb.equals("POST") || verb.equals("PUT")) {
                conn.setDoOutput(true);
                }
            }
            
            if (! ddclass.getSelectedItem().toString().equals("PARAM")) {
                ArrayList<apid_meta> headertags = getAPIDMeta(tbkey.getText());
                for (apid_meta am : headertags) {
                        if (am.apidm_method().equals(method)) {
                       // System.out.println(am.apidm_key() + "=" + am.apidm_value());
                        conn.setRequestProperty(am.apidm_key(),am.apidm_value());
                        }
                }
            }
            
            // let's see what request headers look like
            // call getRequestProperties before opening connection...otherwise generates exception already connected
            Map<String, List<String>> headers = conn.getRequestProperties();
            for (Map.Entry<String, List<String>> z : headers.entrySet()) {
            requestHeaders.append(z.getKey() + " : " + String.join(";", z.getValue()) + "\n");
            }
                
                
                // if posting data...add file
                // calling this opens the connection
                if (ddclass.getSelectedItem().toString().equals("REST") && (verb.equals("POST") || verb.equals("PUT"))) {
                    if (! tbsourcedir.getText().isBlank()) {
                        DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                        Path sourcepath = FileSystems.getDefault().getPath(tbsourcedir.getText());
                        dos.write(Files.readAllBytes(sourcepath));
                        dos.flush();
                        dos.close();
                    }
                }
           
            
            
            BufferedReader br = null;
            StringBuilder responseHeaders = new StringBuilder();
            
            if (conn.getResponseCode() != 200) {
                    taoutput.append(conn.getResponseCode() + ": " + conn.getResponseMessage());
                    //throw new RuntimeException("Failed : HTTP error code : "
                    //		+ conn.getResponseCode());
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    
                    conn.getHeaderFields().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                          responseHeaders.append(entry.getKey()).append(": ");
                          List headerValues = entry.getValue();
                          Iterator it = headerValues.iterator();
                          if (it.hasNext()) {
                              responseHeaders.append(it.next());
                              while (it.hasNext()) {
                                  responseHeaders.append(", ").append(it.next());
                              }
                          }
                          responseHeaders.append("\n");
                    });
                    /*
                    conn.getRequestProperties().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                          requestHeaders.append(entry.getKey()).append(": ");
                          List headerValues = entry.getValue();
                          Iterator it = headerValues.iterator();
                          if (it.hasNext()) {
                              requestHeaders.append(it.next());
                              while (it.hasNext()) {
                                  requestHeaders.append(", ").append(it.next());
                              }
                          }
                          requestHeaders.append("\n");
                    });
                    */
                    
            }

            int filenumber;
            Path path;
            BufferedWriter outputfile = null;
            if (cbfile.isSelected()) {
                filenumber = OVData.getNextNbr("ediout");
                path = FileSystems.getDefault().getPath("edi/api/in" + "/" + "api." + tbkey.getText() + "." + String.valueOf(filenumber) + ".txt"); 
                outputfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
            }
            if (br != null) {
                String output;
                StringBuilder sb = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                
                if (cbrequestheaders.isSelected()) {
                taoutput.append("Request Headers: " + "\n");
                taoutput.append(requestHeaders.toString());
                }
                
                if (cbresponseheaders.isSelected()) {
                taoutput.append("Response Headers: " + "\n");
                taoutput.append(responseHeaders.toString());
                }
                
                if (type.equals("json")) { // prettify JSON output
                    ObjectMapper mapper = new ObjectMapper();
                    Object json = mapper.readValue(sb.toString(), Object.class);
                    taoutput.append(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
                    taoutput.setCaretPosition(0);
                    if (cbfile.isSelected() && outputfile != null) {
                       outputfile.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
                    }
                } else {
                    taoutput.append(sb.toString());
                    taoutput.setCaretPosition(0);
                    if (cbfile.isSelected() && outputfile != null) {
                       outputfile.write(sb.toString());
                    }
                }
                br.close();
            }
            
           
            if (outputfile != null) {
                outputfile.close(); 
            }



            } catch (MalformedURLException e) {
                taoutput.append("MalformedURLException: " + e + "\n");
            } catch (UnknownHostException ex) {
                taoutput.append("UnknownHostException: " + ex + "\n");
            } catch (IOException ex) {
                taoutput.append("IOException: " + ex + "\n");
            } catch (Exception ex) {
                taoutput.append("Exception: " + ex + "\n");
            } finally {
               if (conn != null) {
                conn.disconnect();
               }
            }
        
        
    }//GEN-LAST:event_btrunActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
       // method, verb, class, type, sequence, path, value, source, destination, enabled
        isLoad = true;  
        btaddparam.setEnabled(true);
        btdeleteparam.setEnabled(true);
        
        ddverb.setSelectedItem(tabledetail.getModel().getValueAt(row, 1).toString());
        tbmethod.setText(tabledetail.getModel().getValueAt(row, 0).toString());
        ddtype.setSelectedItem(tabledetail.getModel().getValueAt(row, 2).toString());
        tbsequence.setText(tabledetail.getModel().getValueAt(row, 3).toString());
        tbkvpair.setText(tabledetail.getModel().getValueAt(row, 4).toString());
        tbsourcedir.setText(tabledetail.getModel().getValueAt(row, 5).toString());
        tbdestdir.setText(tabledetail.getModel().getValueAt(row, 6).toString());
        cbenabled.setSelected(BlueSeerUtils.ConvertTrueFalseToBoolean(tabledetail.getModel().getValueAt(row, 7).toString()));
        
        kvmodel.removeAllElements();
       
        
        ArrayList<String[]> x = apidm.get(tabledetail.getModel().getValueAt(row, 0).toString());
        int i = 0;
        if (x != null) {
            for (String[] xs : x) {
            kvmodel.add(i, xs[0] + "=" + xs[1]);
            i++;
            }
        } 
       
        setURL();  
        tburlstring.setEnabled(false);
        btrun.setEnabled(true);
        isLoad = false;
    }//GEN-LAST:event_tabledetailMouseClicked

    private void btdeleteparamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteparamActionPerformed
        boolean proceed = true;

        if (listkv.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1029));
        } else {
            proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
            int i = listkv.getSelectedIndex();
            String[] kv = listkv.getSelectedValue().split("=", -1);
            ArrayList<String[]> list = apidm.get(tbmethod.getText());
            int k = 0;
            for (String[] x : list) {
                if (x[0].equals(kv[0])) {
                    list.remove(k);
                    break;
                }
                k++;
            }
            ArrayList<String[]> newlist = list;
            apidm.put(tbmethod.getText(), newlist);
            kvmodel.remove(i);            
        }
    }//GEN-LAST:event_btdeleteparamActionPerformed

    private void btaddparamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddparamActionPerformed
        kvmodel.addElement(tbattributekey.getText() + "=" + tbattributevalue.getText());
        ArrayList<String[]> list = new ArrayList<String[]>();
        if (apidm.containsKey(tbmethod.getText())) {
            list = apidm.get(tbmethod.getText());
            list.add(new String[]{tbattributekey.getText(), tbattributevalue.getText()});
            ArrayList<String[]> newlist = list;
            apidm.put(tbmethod.getText(), newlist);
        } else {
            list.add(new String[]{tbattributekey.getText(), tbattributevalue.getText()});
            apidm.put(tbmethod.getText(), list);
        }
        
        tbattributekey.setText("");
        tbattributevalue.setText("");
    }//GEN-LAST:event_btaddparamActionPerformed

    private void btupdateurlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateurlActionPerformed
       tburlstring.setEnabled(true);
    }//GEN-LAST:event_btupdateurlActionPerformed

    private void listkvMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listkvMouseClicked
        int index = listkv.locationToIndex(evt.getPoint());
          if (index >= 0) {
            String[] s = listkv.getModel().getElementAt(index).split("=");
            tbattributekey.setText(s[0]);
            tbattributevalue.setText(s[1]);
          }
    }//GEN-LAST:event_listkvMouseClicked

    private void btupdateparamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateparamActionPerformed
        int index = listkv.getSelectedIndex();
        kvmodel.setElementAt(tbattributekey.getText() + "=" + tbattributevalue.getText(), index);
        ArrayList<String[]> list = new ArrayList<String[]>();
        if (apidm.containsKey(tbmethod.getText())) {
            list = apidm.get(tbmethod.getText());
            list.set(index, new String[]{tbattributekey.getText(), tbattributevalue.getText()});
            ArrayList<String[]> newlist = list;
            apidm.put(tbmethod.getText(), newlist);
        } 
        tbattributekey.setText("");
        tbattributevalue.setText("");
    }//GEN-LAST:event_btupdateparamActionPerformed

    private void btadddetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadddetailActionPerformed
        if (! isAPIMethodUnique(tbkey.getText(), tbmethod.getText())) {
           bsmf.MainFrame.show("Method name must be unique for this API...already committed to DB");
           tbmethod.requestFocus();
           return;
        }
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
             if (tabledetail.getModel().getValueAt(j, 0).toString().toLowerCase().equals(tbmethod.getText().toLowerCase())) {
                 bsmf.MainFrame.show("Method name must be unique for this API...already in table");
                 tbmethod.requestFocus();
                 return;
             }
        }
        
            detailmodel.addRow(new Object[]{tbmethod.getText(), 
            ddverb.getSelectedItem().toString(),
            ddtype.getSelectedItem().toString(),
            tbsequence.getText(),
            tbkvpair.getText(),
            tbsourcedir.getText(),
            tbdestdir.getText(),
            cbenabled.isSelected()});
            
            // now clear detail fields
            tbmethod.setText("");
            tbsequence.setText("");
            tbkvpair.setText("");
            tbsourcedir.setText("");
            tbdestdir.setText("");
    }//GEN-LAST:event_btadddetailActionPerformed

    private void btdeletedetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletedetailActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeletedetailActionPerformed

    private void btupdatedetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatedetailActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        
        
        for (int i : rows) {
            tabledetail.getModel().setValueAt(tbmethod.getText(), i, 0);
            tabledetail.getModel().setValueAt(ddverb.getSelectedItem().toString(), i, 1);
            tabledetail.getModel().setValueAt(ddtype.getSelectedItem().toString(), i, 2);
            tabledetail.getModel().setValueAt(tbsequence.getText(), i, 3);
            tabledetail.getModel().setValueAt(tbkvpair.getText(), i, 4);
            tabledetail.getModel().setValueAt(tbsourcedir.getText(), i, 5);
            tabledetail.getModel().setValueAt(tbdestdir.getText(), i, 6);
            tabledetail.getModel().setValueAt(String.valueOf(cbenabled.isSelected()), i, 7);
        }
        
        updateAPIDetTransaction(tbkey.getText(), createAPIDMetaRecord(), createDetRecord());
        
        setURL();
    }//GEN-LAST:event_btupdatedetailActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadddetail;
    private javax.swing.JButton btaddparam;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeletedetail;
    private javax.swing.JButton btdeleteparam;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdatedetail;
    private javax.swing.JButton btupdateparam;
    private javax.swing.JButton btupdateurl;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JCheckBox cbfile;
    private javax.swing.JCheckBox cbrequestheaders;
    private javax.swing.JCheckBox cbresponseheaders;
    private javax.swing.JComboBox<String> ddauth;
    private javax.swing.JComboBox<String> ddclass;
    private javax.swing.JComboBox<String> ddcontenttype;
    private javax.swing.JComboBox<String> ddprotocol;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JComboBox<String> ddverb;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> listkv;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelNotes;
    private javax.swing.JPanel panelOutput;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextArea tanotes;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextField tbapikey;
    private javax.swing.JTextField tbattributekey;
    private javax.swing.JTextField tbattributevalue;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbdestdir;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbkeylabel;
    private javax.swing.JTextField tbkvpair;
    private javax.swing.JTextField tbmethod;
    private javax.swing.JPasswordField tbpass;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbport;
    private javax.swing.JTextField tbsequence;
    private javax.swing.JTextField tbsourcedir;
    private javax.swing.JTextField tburl;
    private javax.swing.JTextField tburlstring;
    private javax.swing.JTextField tbuser;
    // End of variables declaration//GEN-END:variables
}
