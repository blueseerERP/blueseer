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

import com.blueseer.eng.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.eng.engData.addTaskTransaction;
import static com.blueseer.eng.engData.deleteTaskMstr;
import static com.blueseer.eng.engData.getTaskDet;
import static com.blueseer.eng.engData.getTaskMstr;
import static com.blueseer.eng.engData.getTaskSequences;
import com.blueseer.eng.engData.task_det;
import com.blueseer.eng.engData.task_mstr;
import static com.blueseer.eng.engData.updateTaskTransaction;
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
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
     javax.swing.table.DefaultTableModel taskmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("owner"), 
                getGlobalColumnTag("description"),                 
                getGlobalColumnTag("enabled"),
                getGlobalColumnTag("sequence") 
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
        taskmodel.setRowCount(0);
           tabletasks.setModel(taskmodel);
           
          
           tbkey.setText("");
           tbdesc.setText("");
           tbsequence.setText("");
           tbmethod.setText("");
           cbenabled.setSelected(false);
           
        
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
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
    String[] m = addTaskTransaction(createDetRecord(), createRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
    String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getTaskSequences(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < tabletasks.getRowCount(); j++) {
             if (tabletasks.getValueAt(j, 3).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
        m = updateTaskTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteTaskMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       
        task_mstr x = getTaskMstr(key); 
        tbkey.setText(x.task_id());
        tbdesc.setText(x.task_desc());
       
       
        // now detail
        taskmodel.setRowCount(0);
        ArrayList<task_det> z = getTaskDet(key[0]);
        for (task_det d : z) {
            taskmodel.addRow(new Object[]{d.taskd_owner(), d.taskd_desc(), d.taskd_enabled(),
                 d.taskd_sequence()});
        }
       // getTasks(ddtask.getSelectedItem().toString());
        setAction(x.m());
        return x.m();
       
    }
    
     public task_mstr createRecord() { 
        task_mstr x = new task_mstr(null, 
                tbkey.getText(),
                tbdesc.getText()
                );
        return x;
    }
    
    public ArrayList<task_det> createDetRecord() {
        ArrayList<task_det> list = new ArrayList<task_det>();
         for (int j = 0; j < tabletasks.getRowCount(); j++) {
             task_det x = new task_det(null, 
                tbkey.getText(),
                tabletasks.getValueAt(j, 0).toString(),
                tabletasks.getValueAt(j, 1).toString(),
                tabletasks.getValueAt(j, 2).toString(),
                tabletasks.getValueAt(j, 3).toString()
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
         luModel = DTData.getTaskBrowseUtil(luinput.getText(),0, "task_id");
        } else {
         luModel = DTData.getTaskBrowseUtil(luinput.getText(),0, "task_desc");   
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
        tabletasks = new javax.swing.JTable();
        tbsequence = new javax.swing.JTextField();
        cbenabled = new javax.swing.JCheckBox();
        btdeletetask = new javax.swing.JButton();
        tbmethod = new javax.swing.JTextField();
        btaddtask = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tbkvpair = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsourcedir = new javax.swing.JTextField();
        tbdestdir = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
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
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

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
        jPanel2.setName("panelitems"); // NOI18N

        jLabel2.setText("Sequence");
        jLabel2.setName("lblsequence"); // NOI18N

        ddverb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GET", "PUT", "POST", "DELETE" }));

        jLabel3.setText("Method");
        jLabel3.setName("lblaction"); // NOI18N

        tabletasks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabletasks);

        cbenabled.setText("Enabled?");
        cbenabled.setName("cbenabled"); // NOI18N

        btdeletetask.setText("Delete");
        btdeletetask.setName("btdelete"); // NOI18N
        btdeletetask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletetaskActionPerformed(evt);
            }
        });

        btaddtask.setText("Add");
        btaddtask.setName("btadd"); // NOI18N
        btaddtask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddtaskActionPerformed(evt);
            }
        });

        jLabel4.setText("Verb");
        jLabel4.setName("lblowner"); // NOI18N

        jLabel12.setText("KVPair");

        jLabel13.setText("SourceDir");

        jLabel14.setText("DestDir");

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
                                .addGap(34, 34, 34)
                                .addComponent(cbenabled)
                                .addGap(27, 27, 27)
                                .addComponent(btaddtask)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletetask))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbdestdir, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                .addComponent(tbsourcedir, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbkvpair, javax.swing.GroupLayout.Alignment.LEADING)))
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
                    .addComponent(btaddtask)
                    .addComponent(btdeletetask)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkvpair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsourcedir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
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

        jLabel7.setText("URL");

        jLabel8.setText("Pasword");

        jLabel9.setText("Path");

        jLabel10.setText("UserID");

        jLabel11.setText("Key");

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
                        .addComponent(tbpath, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(tburl, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(38, 38, 38))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbpass))
                            .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbapikey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
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

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Text"));
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btaddtaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddtaskActionPerformed
         taskmodel.addRow(new Object[]{ddverb.getSelectedItem().toString(), tbmethod.getText(), cbenabled.isSelected(), tbsequence.getText()});
    }//GEN-LAST:event_btaddtaskActionPerformed

    private void btdeletetaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletetaskActionPerformed
       int[] rows = tabletasks.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabletasks.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeletetaskActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddtask;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeletetask;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox<String> ddverb;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTable tabletasks;
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
