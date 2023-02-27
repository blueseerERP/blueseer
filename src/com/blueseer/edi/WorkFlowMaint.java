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

import com.blueseer.ctr.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import static com.blueseer.edi.ediData.addWkfTransaction;
import static com.blueseer.edi.ediData.deleteWkfMstr;
import static com.blueseer.edi.ediData.getWkfDet;
import static com.blueseer.edi.ediData.getWkfMstr;
import static com.blueseer.edi.ediData.getWkfdMeta;
import static com.blueseer.edi.ediData.updateWkfMstrTransaction;
import com.blueseer.edi.ediData.wkf_det;
import com.blueseer.edi.ediData.wkf_mstr;
import com.blueseer.edi.ediData.wkfd_meta;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callChangeDialog;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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

/**
 *
 * @author vaughnte
 */
public class WorkFlowMaint extends javax.swing.JPanel implements IBlueSeerT {

    // global variable declarations
                boolean isLoad = false;
                int currentkvm = 0;
                public static wkf_mstr x = null;
                public static ArrayList<wkf_det> wkfdetlist = null;
                public static ArrayList<wkfd_meta> wkfdmetalist = null;
                public static LinkedHashMap<String, ArrayList<String[]>> kvs = new  LinkedHashMap<String, ArrayList<String[]>>();
                public static LinkedHashMap<String, ArrayList<String[]>> kvm = new  LinkedHashMap<String, ArrayList<String[]>>();
    // global datatablemodel declarations       
   javax.swing.table.DefaultTableModel keyvaluemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Key", "Value"
            });
   
   DefaultListModel actionlistmodel = new DefaultListModel();
                
                
    public WorkFlowMaint() {
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
    
    public void setComponentDefaultValues() {
       isLoad = true;
       tbkey.setText("");
        tbdesc.setText("");
        cbenabled.setSelected(false);
        keyvaluemodel.setRowCount(0);
        keyvaluetable.setModel(keyvaluemodel);
        actionlistmodel.removeAllElements();
        actionlist.setModel(actionlistmodel);
        setddactions();
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
       
               
        Map<String,Integer> f = OVData.getTableInfo("car_mstr");
        int fc;

        fc = checkLength(f,"car_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }     
         
        fc = checkLength(f,"car_desc");
        if (tbdesc.getText().length() > fc ) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        } 
               
        return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
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
     String[] m = addWkfTransaction(createDetMetaRecord(), createDetRecord(), createRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
     
     wkf_mstr _x = this.x;
     wkf_mstr _y = createRecord();   
     String[] m = updateWkfMstrTransaction(x[0], createDetMetaRecord(), createDetRecord(), _y);
     
      // change log check
     if (m[0].equals("0")) {
       ArrayList<admData.change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),_x,_y);
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
         m = deleteWkfMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
        // change log check
        if (m[0].equals("0")) {
            ArrayList<admData.change_log> c = new ArrayList<admData.change_log>();
            c.add(clog(this.x.wkf_id(), 
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
       x = getWkfMstr(key);  
        wkfdetlist = getWkfDet(key[0]); 
        wkfdmetalist = getWkfdMeta(key[0]); 
        return x.m();
    }
    
    public wkf_mstr createRecord() { 
        wkf_mstr x = new wkf_mstr(null, 
                tbkey.getText(),
                tbdesc.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbenabled.isSelected()))
                );
        return x;
    }
    
    public ArrayList<wkf_det> createDetRecord() {
        ArrayList<wkf_det> list = new ArrayList<wkf_det>();
        for (int i = 0 ; i < actionlistmodel.size(); i++) {
            wkf_det x = new wkf_det(null, 
                tbkey.getText(),
                actionlistmodel.getElementAt(i).toString(),
                String.valueOf(i));
            list.add(x);
        }
        return list;   
    }
    
    public ArrayList<wkfd_meta> createDetMetaRecord() {
        ArrayList<wkfd_meta> list = new ArrayList<wkfd_meta>();
        for (int i = 0 ; i < actionlistmodel.size(); i++) {
            for (Map.Entry<String, ArrayList<String[]>> z : kvm.entrySet()) {
    		 if (z.getKey().equals(String.valueOf(i))) {
    			 ArrayList<String[]> k = z.getValue();
    			 for (String[] g : k) {
                                wkfd_meta x = new wkfd_meta(null, 
                                tbkey.getText(),
                                String.valueOf(i),
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
         luModel = DTData.getWkfMstrBrowseUtil(luinput.getText(),0, "wkf_id");
        } else {
         luModel = DTData.getWkfMstrBrowseUtil(luinput.getText(),0, "wkf_desc");   
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
        tbkey.setText(x.wkf_id());
        tbdesc.setText(x.wkf_desc());
        cbenabled.setSelected(BlueSeerUtils.ConvertStringToBool(x.wkf_enabled()));
        setAction(x.m());
        
        // now detail
        actionlistmodel.removeAllElements();
        for (wkf_det dfsd : wkfdetlist) {
            actionlistmodel.addElement(dfsd.wkfd_action());
        }
        
        // now meta
        
        for (int i = 0; i < actionlistmodel.getSize(); i++) {
           ArrayList<String[]> list = new ArrayList<String[]>();
           for (wkfd_meta dfsdm : wkfdmetalist) { 
            if (dfsdm.wkfdm_line().equals(String.valueOf(i))) {
                list.add(new String[]{dfsdm.wkfdm_key(), dfsdm.wkfdm_value()});
            } 
           }
           kvm.put(String.valueOf(i), list);
        }     
        
    }
    
    public void setddactions() {
        ddactions.removeAllItems();
        ddactions.addItem("FileCopy");
        ddactions.addItem("FileMove");
        ddactions.addItem("FileDelete");
        ddactions.addItem("FileCopyAll");
        ddactions.addItem("FileMoveAll");
        ddactions.addItem("FileDeleteAll");
        ddactions.addItem("FileMap");
        
        for (int i = 0; i < ddactions.getItemCount(); i++) {
            
            if (i == 0) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source", ""});
                x.add(new String[]{"destination", ""});
                x.add(new String[]{"append", ""}); 
                kvs.put("FileCopy", x);
            }
            if (i == 1) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source", ""});
                x.add(new String[]{"destination", ""});
                kvs.put("FileMove", x);
            }
            if (i == 2) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source", ""});
                kvs.put("FileDelete", x);
            }
            if (i == 3) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source dir", ""});
                x.add(new String[]{"filter", ""});
                x.add(new String[]{"destination dir", ""});
                x.add(new String[]{"overwrite", ""}); 
                kvs.put("FileCopyAll", x);
            }
            if (i == 4) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source dir", ""});
                x.add(new String[]{"filter", ""});
                x.add(new String[]{"destination dir", ""});
                x.add(new String[]{"overwrite", ""}); 
                kvs.put("FileMoveAll", x);
            }
            if (i == 5) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source dir", ""});
                kvs.put("FileDeleteAll", x);
            }
            if (i == 6) {
                ArrayList<String[]> x = new ArrayList<String[]>();
                x.add(new String[]{"source file", ""});
                kvs.put("FileMap", x);
            }
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
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        actionlist = new javax.swing.JList<>();
        ddactions = new javax.swing.JComboBox<>();
        btup = new javax.swing.JButton();
        btdown = new javax.swing.JButton();
        btaddrow = new javax.swing.JButton();
        btdeleterow = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btlookup = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btclear = new javax.swing.JButton();
        tbdesc = new javax.swing.JTextField();
        btchangelog = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cbenabled = new javax.swing.JCheckBox();
        btnew = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        keyvaluetable = new javax.swing.JTable();
        btcommit = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("WorkFlow Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        btdelete.setText("delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Action Maintenance"));
        jPanel2.setName("panelaction"); // NOI18N

        actionlist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                actionlistMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(actionlist);

        btup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/upload.png"))); // NOI18N
        btup.setToolTipText("Move Up");
        btup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupActionPerformed(evt);
            }
        });

        btdown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/download.png"))); // NOI18N
        btdown.setToolTipText("Move Down");
        btdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdownActionPerformed(evt);
            }
        });

        btaddrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddrow.setToolTipText("Add Row");
        btaddrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddrowActionPerformed(evt);
            }
        });

        btdeleterow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleterow.setToolTipText("Delete Row");
        btdeleterow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleterowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddactions, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddrow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleterow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btup, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btdown, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddactions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btaddrow)
                    .addComponent(btdeleterow))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdown)))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Header Maintenance"));
        jPanel3.setName("panelheader"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel2.setText("Description:");
        jLabel2.setName("lbldesc"); // NOI18N

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

        btchangelog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btchangelog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchangelogActionPerformed(evt);
            }
        });

        jLabel1.setText("Key:");
        jLabel1.setName("lblid"); // NOI18N

        cbenabled.setText("Enabled");
        cbenabled.setName("cbenabled"); // NOI18N

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbenabled)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btchangelog, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(btclear)
                        .addComponent(btnew))
                    .addComponent(btlookup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btchangelog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbenabled)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Key / Value Maintenance"));
        jPanel4.setName("panelkeyvalue"); // NOI18N

        keyvaluetable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(keyvaluetable);

        btcommit.setText("Commit Key/Value");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btcommit)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btcommit))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(47, 47, 47)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btadd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addGap(36, 36, 36))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap())
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

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
     if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btupdateActionPerformed

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

    private void btchangelogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchangelogActionPerformed
        callChangeDialog(tbkey.getText(), this.getClass().getSimpleName());
    }//GEN-LAST:event_btchangelogActionPerformed

    private void btupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupActionPerformed
        int[] elements = actionlist.getSelectedIndices();
        if (elements == null || elements.length == 0) {
            bsmf.MainFrame.show(getMessageTag(1029));
            return;
        }
        if (elements.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        for (int i : elements) {
            if (i > 0 && elements.length == 1) {
                Object b = actionlistmodel.get(i - 1);
                Object s = actionlistmodel.get(i);
                actionlistmodel.setElementAt(s, i - 1);
                actionlistmodel.setElementAt(b, i);
                actionlist.setSelectedIndex(i - 1);
                currentkvm = i - 1;
                
                ArrayList<String[]> blist = kvm.get(String.valueOf(i - 1));
                ArrayList<String[]> slist = kvm.get(String.valueOf(i));
                kvm.replace(String.valueOf(i - 1), slist);
                kvm.replace(String.valueOf(i), blist);
                
            }
        }
    }//GEN-LAST:event_btupActionPerformed

    private void btdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdownActionPerformed
        int[] elements = actionlist.getSelectedIndices();
        if (elements == null || elements.length == 0) {
            bsmf.MainFrame.show(getMessageTag(1029));
            return;
        }
        if (elements.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        for (int i : elements) {
            Object a = null;
            Object s = null;
            if ((i + 1) < actionlistmodel.size()) {
                a = actionlistmodel.get(i + 1);
                s = actionlistmodel.get(i);
                actionlistmodel.setElementAt(a, i);
                actionlistmodel.setElementAt(s, i + 1);
                actionlist.setSelectedIndex(i + 1);
                currentkvm = i + 1;
                
                ArrayList<String[]> alist = kvm.get(String.valueOf(i + 1));
                ArrayList<String[]> slist = kvm.get(String.valueOf(i));
                kvm.replace(String.valueOf(i), alist);
                kvm.replace(String.valueOf(i + 1), slist);
                
            }
        }
    }//GEN-LAST:event_btdownActionPerformed

    private void btaddrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddrowActionPerformed
        
        if (ddactions.getSelectedItem() != null) {
            actionlistmodel.addElement(ddactions.getSelectedItem().toString());
            int index = actionlistmodel.getSize() - 1;
            ArrayList<String[]> x = kvs.get(ddactions.getSelectedItem().toString());
            kvm.put(String.valueOf(index), x);
        }
        
    }//GEN-LAST:event_btaddrowActionPerformed

    private void btdeleterowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleterowActionPerformed
        int[] rows = actionlist.getSelectedIndices();
        for (int i : rows) {
            actionlistmodel.removeElementAt(i);
            kvm.remove(String.valueOf(i));
           // bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
          //  ((javax.swing.table.DefaultTableModel) keyvaluetable.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleterowActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void actionlistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_actionlistMouseClicked
        if (actionlist != null && ! actionlist.isSelectionEmpty()) {
            int x = actionlist.getSelectedIndex();
            currentkvm = x;
            String label = actionlist.getSelectedValue();
           
            keyvaluemodel.setRowCount(0);
            
            jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Key/Value Action: " + label));
            
            /*
            for (Map.Entry<String, ArrayList<String[]>> z : kvm.entrySet()) {
    		System.out.println("HERE: " + z.getKey());
    	    }
            */
            
            ArrayList<String[]> list = kvm.get(String.valueOf(x));
            for (String[] v : list) {
                keyvaluemodel.addRow(new Object[]{
                v[0],
                v[1]
                });
            }
        }
    }//GEN-LAST:event_actionlistMouseClicked

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int j = 0; j < keyvaluetable.getRowCount(); j++) {
            String[] arr = new String[]{keyvaluetable.getValueAt(j, 0).toString(), keyvaluetable.getValueAt(j, 1).toString()};
            list.add(arr);
        }
        kvm.put(String.valueOf(currentkvm), list);
        bsmf.MainFrame.show("key/values committed");
    }//GEN-LAST:event_btcommitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> actionlist;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddrow;
    private javax.swing.JButton btchangelog;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleterow;
    private javax.swing.JButton btdown;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btup;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox<String> ddactions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable keyvaluetable;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    // End of variables declaration//GEN-END:variables
}
