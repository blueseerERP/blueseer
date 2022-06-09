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
import static com.blueseer.edi.ediData.addAPITransaction;
import com.blueseer.edi.ediData.api_det;
import com.blueseer.edi.ediData.api_mstr;
import static com.blueseer.edi.ediData.deleteAPIMstr;
import static com.blueseer.edi.ediData.getAPIDet;
import static com.blueseer.edi.ediData.getAPIMethodsList;
import static com.blueseer.edi.ediData.getAPIMstr;
import static com.blueseer.edi.ediData.isAPIMethodUnique;
import static com.blueseer.edi.ediData.updateAPITransaction;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import static com.blueseer.utl.EDData.getAS2id;
import static com.blueseer.utl.EDData.getAS2url;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

/**
 *
 * @author vaughnte
 */


public class APIMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
                boolean isLoad = false;
    
    // global datatablemodel declarations   
     javax.swing.table.DefaultTableModel detailmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("method"), 
                getGlobalColumnTag("verb"),
                getGlobalColumnTag("type"),
                getGlobalColumnTag("sequence"),
                getGlobalColumnTag("path"),
                getGlobalColumnTag("value"),
                getGlobalColumnTag("source"),
                getGlobalColumnTag("destination"),
                getGlobalColumnTag("enabled")
                
            });
    
  
    public APIMaint() {
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
        detailmodel.setRowCount(0);
           tabledetail.setModel(detailmodel);
           
          
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
           lblurl.setText("");
        
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
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
       
        bsmf.MainFrame.show("This functionality is a work-in-progress and not intended for production");
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
    String[] m = addAPITransaction(createDetRecord(), createRecord());
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
             if (tabledetail.getValueAt(j, 0).toString().toLowerCase().equals(method.toLowerCase())) {
                 goodMethod = true;
             }
          }
          if (! goodMethod) {
              badmethods.add(method);
          }
        }
        m = updateAPITransaction(tbkey.getText(), badmethods, createDetRecord(), createRecord());
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
       
        api_mstr x = getAPIMstr(key); 
        tbkey.setText(x.api_id());
        tbdesc.setText(x.api_desc());
        tburl.setText(x.api_url());
        tbport.setText(x.api_port());
        tbpath.setText(x.api_path());
        tbuser.setText(x.api_user());
        tbpass.setText(bsmf.MainFrame.PassWord("1", x.api_pass().toCharArray()));
        tbapikey.setText(x.api_key());
        ddprotocol.setSelectedItem(x.api_protocol());
        ddclass.setSelectedItem(x.api_class());
        // now detail
        detailmodel.setRowCount(0);
        ArrayList<api_det> z = getAPIDet(key[0]);
        for (api_det d : z) {
            detailmodel.addRow(new Object[]{d.apid_method(), d.apid_verb(), d.apid_type(),
                 d.apid_seq(), d.apid_path(), d.apid_value(), d.apid_source(), d.apid_destination(), d.apid_enabled()});
        }
      
        setAction(x.m());
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
                ddprotocol.getSelectedItem().toString(),
                ddclass.getSelectedItem().toString()
                );
        return x;
    }
    
    public ArrayList<api_det> createDetRecord() {
        ArrayList<api_det> list = new ArrayList<api_det>();
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             api_det x = new api_det(null, 
                tbkey.getText(),
                tabledetail.getValueAt(j, 0).toString(),
                tabledetail.getValueAt(j, 3).toString(),
                tabledetail.getValueAt(j, 1).toString(),
                tabledetail.getValueAt(j, 2).toString(),
                tabledetail.getValueAt(j, 4).toString(),
                "", // key not used
                tabledetail.getValueAt(j, 5).toString(),
                tabledetail.getValueAt(j, 6).toString(),
                tabledetail.getValueAt(j, 7).toString(),
                tabledetail.getValueAt(j, 8).toString()
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

    
    public void postAS2( URL url, String verb, String as2From, String as2To, String internalURL) {
        File textFile = new File(tbsourcedir.getText());
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String messageid = "<BLUESEER-" + now + "." + boundary + "@Blueseer Software>";
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        try {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("User-Agent", "RPT-HTTPClient/0.3-3I (Windows Server 2016)"); 
        conn.setRequestProperty("AS2-To", as2To);
        conn.setRequestProperty("AS2-From", as2From); 
        conn.setRequestProperty("AS2-Version", "1.2"); 
        conn.setRequestProperty("Subject", "as2");
        
        conn.setRequestProperty("Accept-Encoding", "deflate, gzip, x-gzip, compress, x-compress");
        conn.setRequestProperty("Disposition-Notification-Options", "signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1");
        conn.setRequestProperty("Disposition-Notification-To", internalURL);
        
        conn.setRequestProperty("Message-ID", messageid);
        conn.setRequestProperty("Recipient-Address", url.toString());
        conn.setRequestProperty("EDIINT-Features", "CEM, multiple-attachments, AS2-Reliability");

        
        conn.setRequestMethod("POST");
       // conn.setRequestProperty("Accept", "application/json");

       try (
        OutputStream output = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF8"), true);
        ) {
            // Send normal param.
           /*
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();
            */
            // Send text file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"" + textFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + "UTF8").append(CRLF); // Text file itself must be saved in this charset!
            writer.append(CRLF).flush();
            Files.copy(textFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // Send binary file.
            /*
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
            */
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }
       
       
        BufferedReader br = null;
        if (conn.getResponseCode() != 200) {
                taoutput.append(conn.getResponseCode() + ": " + conn.getResponseMessage());
                //throw new RuntimeException("Failed : HTTP error code : "
                //		+ conn.getResponseCode());
        } else {
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        }
         String output;
         if (br != null) {
            while ((output = br.readLine()) != null) {
                    taoutput.append(output + "\n");
            }
            br.close();
        }
        
       
        conn.disconnect();
        } catch (MalformedURLException e) {
            bslog(e);
            bsmf.MainFrame.show("MalformedURLException");
        } catch (IOException ex) {
            bslog(ex);
            bsmf.MainFrame.show("IOException");
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
        jLabel2 = new javax.swing.JLabel();
        ddverb = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        tbsequence = new javax.swing.JTextField();
        cbenabled = new javax.swing.JCheckBox();
        btdeletemethod = new javax.swing.JButton();
        tbmethod = new javax.swing.JTextField();
        btaddmethod = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tbkvpair = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsourcedir = new javax.swing.JTextField();
        tbdestdir = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
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
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taoutput = new javax.swing.JTextArea();
        btrun = new javax.swing.JButton();
        cbfile = new javax.swing.JCheckBox();
        lblurl = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("API Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("API Methods Detail"));
        jPanel2.setName("paneldetail"); // NOI18N

        jLabel2.setText("Sequence");
        jLabel2.setName("lblsequence"); // NOI18N

        ddverb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GET", "PUT", "POST", "DELETE", "NONE" }));

        jLabel3.setText("Method");
        jLabel3.setName("lblmethod"); // NOI18N

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

        cbenabled.setText("Enabled?");
        cbenabled.setName("cbenabled"); // NOI18N

        btdeletemethod.setText("Delete");
        btdeletemethod.setName("btdelete"); // NOI18N
        btdeletemethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletemethodActionPerformed(evt);
            }
        });

        btaddmethod.setText("Add");
        btaddmethod.setName("btadd"); // NOI18N
        btaddmethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddmethodActionPerformed(evt);
            }
        });

        jLabel4.setText("Verb");
        jLabel4.setName("lblverb"); // NOI18N

        jLabel12.setText("KVPair");
        jLabel12.setName("lblkvpair"); // NOI18N

        jLabel13.setText("SourceDir");
        jLabel13.setName("lblsourcedir"); // NOI18N

        jLabel14.setText("DestDir");
        jLabel14.setName("lbldestdir"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "json", "xml", "text" }));

        jLabel15.setText("Type");
        jLabel15.setName("lbltype"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbmethod)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ddverb, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(cbenabled)
                                .addGap(27, 27, 27)
                                .addComponent(btaddmethod)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletemethod))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tbdestdir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                    .addComponent(tbsourcedir, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbkvpair, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(5, 5, 5)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddverb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbenabled)
                    .addComponent(btaddmethod)
                    .addComponent(btdeletemethod)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkvpair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbsourcedir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdestdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AS2", "HTTPS", "SOAP" }));

        jLabel16.setText("Class");
        jLabel16.setName("lblclass"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbapikey, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(tbuser, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(119, 119, 119)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddclass, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbdesc)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addComponent(tbport)
                            .addComponent(ddprotocol, 0, 100, Short.MAX_VALUE))))
                .addGap(38, 38, 38))
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
                        .addComponent(btclear))
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
                    .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        cbfile.setText("File");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("AS2 Details"));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 318, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 178, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblurl, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(383, 383, 383)
                .addComponent(btdelete)
                .addGap(6, 6, 6)
                .addComponent(btupdate)
                .addGap(6, 6, 6)
                .addComponent(btadd)
                .addGap(231, 231, 231)
                .addComponent(cbfile)
                .addGap(18, 18, 18)
                .addComponent(btrun))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblurl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(cbfile)
                    .addComponent(btrun)))
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

    private void btaddmethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddmethodActionPerformed
            
        if (! isAPIMethodUnique(tbkey.getText(), tbmethod.getText())) {
           bsmf.MainFrame.show("Method name must be unique for this API...already committed to DB");
           tbmethod.requestFocus();
           return;
        }
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
             if (tabledetail.getValueAt(j, 0).toString().toLowerCase().equals(tbmethod.getText().toLowerCase())) {
                 bsmf.MainFrame.show("Method name must be unique for this API...already in table");
                 tbmethod.requestFocus();
                 return;
             }
        }
        
            detailmodel.addRow(new Object[]{tbmethod.getText(), 
            ddverb.getSelectedItem().toString(),
            ddtype.getSelectedItem().toString(),
            tbsequence.getText(),
            tbpath.getText(), // header path
            tbkvpair.getText(),
            tbsourcedir.getText(),
            tbdestdir.getText(),
            cbenabled.isSelected()});
            
            // now clear detail fields
            tbmethod.setText("");
            tbsequence.setText("");
            tbpath.setText("");
            tbkvpair.setText("");
            tbsourcedir.setText("");
            tbdestdir.setText("");
    }//GEN-LAST:event_btaddmethodActionPerformed

    private void btdeletemethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletemethodActionPerformed
       int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeletemethodActionPerformed

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
        int[] rows = tabledetail.getSelectedRows();
       // taoutput.removeAll();
        taoutput.setText("");
        lblurl.setText("");
        if (rows.length == 0) {
            bsmf.MainFrame.show("no method selected in detail table");
            return;
        }
        
        int k = 0;
        String method = "";
        String verb = "";
        String value = "";
        for (int i : rows) {
            k++;
            method = tabledetail.getValueAt(i, 0).toString();
            verb = tabledetail.getValueAt(i, 1).toString();
            value = tabledetail.getValueAt(i, 5).toString();
            if (k > 0) {
                break;
            }
        }  
            try {
                String urlstring = "";
                String port = "";
                if (! value.isBlank()) {
                    value = "/" + value;
                }
                if (tbport.getText().isBlank()) {  
                   port = ""; 
                } else {
                   port = ":" + tbport.getText();
                }
                
                if (verb.equals("NONE")) {
                    urlstring = ddprotocol.getSelectedItem().toString() + "://" + tburl.getText() + port + "/" + tbpath.getText() + value;
                } else {
                    urlstring = ddprotocol.getSelectedItem().toString() + "://" + tburl.getText() + port + "/" + tbpath.getText() + verb.toLowerCase() ;
                }
                lblurl.setText(urlstring);
                URL url = new URL(urlstring);
                
                if (ddclass.getSelectedItem().toString().equals("AS2")) {
                    postAS2(url, verb, getAS2id(), tbuser.getText(), getAS2url());
                    return;
                }
                
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (! verb.equals("NONE")) {
                conn.setRequestMethod(verb);
                }
		conn.setRequestProperty("Accept", "application/json");

                BufferedReader br = null;
		if (conn.getResponseCode() != 200) {
                        taoutput.append(conn.getResponseCode() + ": " + conn.getResponseMessage());
			//throw new RuntimeException("Failed : HTTP error code : "
			//		+ conn.getResponseCode());
		} else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                }

		int filenumber;
                Path path;
                BufferedWriter outputfile = null;
                if (cbfile.isSelected()) {
                    filenumber = OVData.getNextNbr("ediout");
                    path = FileSystems.getDefault().getPath("edi/api/in" + "/" + "api." + tbkey.getText() + "." + String.valueOf(filenumber) + ".txt"); 
                    outputfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
                }
		String output;
                if (br != null) {
                    while ((output = br.readLine()) != null) {
                            taoutput.append(output + "\n");
                            if (cbfile.isSelected() && outputfile != null) {
                             outputfile.write(output);
                            }
                    }
                    br.close();
                }
                if (outputfile != null) {
                    outputfile.close(); 
                }
                conn.disconnect();
                
                
                } catch (MalformedURLException e) {
		    bslog(e);
                    bsmf.MainFrame.show("MalformedURLException");
	        } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("IOException");
                } 
    }//GEN-LAST:event_btrunActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
       // method, verb, class, type, sequence, path, value, source, destination, enabled
        isLoad = true;  
        ddverb.setSelectedItem(tabledetail.getValueAt(row, 1).toString());
        tbmethod.setText(tabledetail.getValueAt(row, 0).toString());
        ddtype.setSelectedItem(tabledetail.getValueAt(row, 2).toString());
        tbsequence.setText(tabledetail.getValueAt(row, 3).toString());
        tbpath.setText(tabledetail.getValueAt(row, 4).toString());
        tbkvpair.setText(tabledetail.getValueAt(row, 5).toString());
        tbsourcedir.setText(tabledetail.getValueAt(row, 6).toString());
        tbdestdir.setText(tabledetail.getValueAt(row, 7).toString());
        cbenabled.setSelected(bsmf.MainFrame.ConvertStringToBool(tabledetail.getValueAt(row, 8).toString()));
        isLoad = false;
    }//GEN-LAST:event_tabledetailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddmethod;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeletemethod;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JCheckBox cbfile;
    private javax.swing.JComboBox<String> ddclass;
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel lblurl;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextArea taoutput;
    private javax.swing.JTextField tbapikey;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbdestdir;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbkvpair;
    private javax.swing.JTextField tbmethod;
    private javax.swing.JPasswordField tbpass;
    private javax.swing.JTextField tbpath;
    private javax.swing.JTextField tbport;
    private javax.swing.JTextField tbsequence;
    private javax.swing.JTextField tbsourcedir;
    private javax.swing.JTextField tburl;
    private javax.swing.JTextField tbuser;
    // End of variables declaration//GEN-END:variables
}
