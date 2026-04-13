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

package com.blueseer.vdr;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import com.blueseer.adm.admData.change_log;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.clog;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.IBlueSeerV;
import com.blueseer.utl.OVData;
import static com.blueseer.vdr.venData.addVdpMstr;
import static com.blueseer.vdr.venData.deleteVdpMstr;
import static com.blueseer.vdr.venData.getVdpMstr;
import static com.blueseer.vdr.venData.updateVdpMstr;
import com.blueseer.vdr.venData.vdp_mstr;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class VendXrefMaint extends javax.swing.JPanel implements IBlueSeerV {

   // global variable declarations
                 // global variable declarations
        boolean isLoad = false;
        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
        private static vdp_mstr x = null;
    // global datatablemodel declarations       
                
    public VendXrefMaint() {
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
    
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
         if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "vendors,items");
        }
        
        ddvend.removeAllItems();
        ddpart.removeAllItems();
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
          
            if (s[0].equals("vendors")) {
              ddvend.addItem(s[1]); 
            }
            if (s[0].equals("items")) {
              ddpart.addItem(s[1]); 
            }
        }
        
        
        if (ddvend.getItemCount()> 0) {        
         ddvend.setSelectedIndex(0);
        }
        if (ddpart.getItemCount() > 0) { 
        ddpart.setSelectedIndex(0);
        }

        
        tbkey.setText("");
        skunbr.setText("");
        upcnbr.setText("");
        misc.setText("");
     
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues(false);
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
        String[] m = new String[2];
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
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"vdp_mstr"});
        int fc;
        
        if (ddvend.getSelectedItem() == null || ddvend.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddvend.requestFocus();
            return false;
        }

        if (ddpart.getSelectedItem() == null || ddpart.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            ddpart.requestFocus();
            return false;
        }

        fc = checkLength(f,"vdp_vitem");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
        bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
        tbkey.requestFocus();
        return false;
        } 


        fc = checkLength(f,"vdp_sku");
        if (skunbr.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        skunbr.requestFocus();
        return false;
        } 

        fc = checkLength(f,"vdp_upc");
        if (upcnbr.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        upcnbr.requestFocus();
        return false;
        } 

        fc = checkLength(f,"vdp_misc");
        if (misc.getText().length() > fc) {
        bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
        misc.requestFocus();
        return false;
        } 
               
               
        return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues(initDataSets == null);
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
     String[] m = addVdpMstr(createRecord());
         return m; 
     }
     
    public String[] updateRecord(String[] x) {
     vdp_mstr _x = this.x;
     vdp_mstr _y = createRecord();
     String[] m = updateVdpMstr(_y);
     
     // change log check
     if (m[0].equals("0")) {
       ArrayList<change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),_x,_y);
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
         m = deleteVdpMstr(createRecord());  
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
        // change log check
        if (m[0].equals("0")) {
            ArrayList<change_log> c = new ArrayList<change_log>();
            c.add(clog(this.x.vdp_vend() + "/" + this.x.vdp_item(), 
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
       x = getVdpMstr(key);
        return x.m();
    }
    
    public String[] updateForm() {
        ddvend.setSelectedItem(x.vdp_vend());
        ddpart.setSelectedItem(x.vdp_item());
        tbkey.setText(x.vdp_vitem());
        skunbr.setText(x.vdp_sku());
        upcnbr.setText(x.vdp_upc());
        misc.setText(x.vdp_misc());
        setAction(x.m());
        return x.m();  
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVendXrefBrowseUtil(luinput.getText(),0, "vdp_vend");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getVendXrefBrowseUtil(luinput.getText(),0, "vdp_vitem");   
        } else {
         luModel = DTData.getVendXrefBrowseUtil(luinput.getText(),0, "vdp_item");   
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
      
        callDialog("Vendor", "Vendor Item", "Item"); 
        /*
        callDialog(getClassLabelTag("lblvend", this.getClass().getSimpleName()), 
                getClassLabelTag("lblvenditem", this.getClass().getSimpleName()),
                getClassLabelTag("lblitem", this.getClass().getSimpleName())); 
        */
        
    }

    public vdp_mstr createRecord() {        
        vdp_mstr x = new vdp_mstr(null, 
                ddvend.getSelectedItem().toString(),
                ddpart.getSelectedItem().toString(),
                tbkey.getText(),
                upcnbr.getText(),
                bsmf.MainFrame.userid,
                misc.getText(),
                skunbr.getText()
                );
        return x;
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
        jLabel2 = new javax.swing.JLabel();
        skunbr = new javax.swing.JTextField();
        misc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        upcnbr = new javax.swing.JTextField();
        ddpart = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddvend = new javax.swing.JComboBox();
        btdelete = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor Cross Reference Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Vendor Item");
        jLabel2.setName("lblvenditem"); // NOI18N

        jLabel5.setText("UPC Number");
        jLabel5.setName("lblupc"); // NOI18N

        jLabel4.setText("Sku Number");
        jLabel4.setName("lblsku"); // NOI18N

        jLabel3.setText("VendCode");
        jLabel3.setName("lblcode"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel1.setText("Internal Item");
        jLabel1.setName("lblitem"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel6.setText("Misc");
        jLabel6.setName("lblmisc"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ddvend, javax.swing.GroupLayout.Alignment.LEADING, 0, 143, Short.MAX_VALUE)
                            .addComponent(tbkey, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(misc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(upcnbr, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(skunbr, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ddpart, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(100, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(btclear)))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(btlookup))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skunbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upcnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(misc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

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

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField misc;
    private javax.swing.JTextField skunbr;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField upcnbr;
    // End of variables declaration//GEN-END:variables
}
