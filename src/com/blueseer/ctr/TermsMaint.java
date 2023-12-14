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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.adm.admData.addChangeLog;
import com.blueseer.adm.admData.change_log;
import static com.blueseer.ctr.cusData.addTermsMstr;
import com.blueseer.ctr.cusData.cust_term;
import static com.blueseer.ctr.cusData.deleteTermsMstr;
import static com.blueseer.ctr.cusData.getTermsMstr;
import static com.blueseer.ctr.cusData.getTermsUsage;
import static com.blueseer.ctr.cusData.updateTermsMstr;
import static com.blueseer.utl.BlueSeerUtils.callChangeDialog;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.clog;
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
import static com.blueseer.utl.OVData.canUpdate;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
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
public class TermsMaint extends javax.swing.JPanel implements IBlueSeerT { 

    // global variable declarations
                boolean isLoad = false;
    private static cust_term x = null;
    // global datatablemodel declarations    
                
    public TermsMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    // interface functions implemented
    @Override
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
   
    @Override
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
    
    @Override
    public void setComponentDefaultValues() {
       isLoad = true;
       
       cbsystemcode.setEnabled(false);
       
        tbkey.setText("");
        tbdesc.setText("");
        duedays.setText("");
        discduedays.setText("");
        discpercent.setText("");
        
        cbsystemcode.setSelected(false);
        cbmfi.setSelected(false);
        ddmfimonth.removeAllItems();
        for (int i = 1; i <= 12; i++) {
            ddmfimonth.addItem(String.valueOf(i));
        }
        ddmfimonth.setSelectedIndex(0);
       
        ddmfiday.removeAllItems();
        for (int i = 1; i <= 31; i++) {
            ddmfiday.addItem(String.valueOf(i));
        }
        ddmfiday.setSelectedIndex(0);
        //YearMonth yearMonthObject = YearMonth.of(1999, 2);
        //int daysInMonth = yearMonthObject.lengthOfMonth(); //28  
        
       isLoad = false;
    }
    
    @Override
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        cbsystemcode.setEnabled(false);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    @Override
    public void setAction(String[] x) {
        if (x[0].equals("0")) { 
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   cbsystemcode.setEnabled(false);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
    
    @Override
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        
        if (! canUpdate(this.getClass().getName())) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo("cust_term");
        int fc;
        
        if ((x.equals(x.delete) || x.equals(x.update)) && cbsystemcode.isSelected()) {
            bsmf.MainFrame.show(getMessageTag(1175));
            return false;
        }
        
        fc = checkLength(f,"cut_code");        
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) { 
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"cut_desc");
        if (tbdesc.getText().length() > fc) { 
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        }
               
                
        if (! BlueSeerUtils.isParsableToInt(duedays.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            duedays.requestFocus();
            return false;
        }

        if (! BlueSeerUtils.isParsableToInt(discduedays.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            discduedays.requestFocus();
            return false;
        }

        if (! BlueSeerUtils.isParsableToInt(discpercent.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            discpercent.requestFocus();
            return false;
        }
               
        return true;
    }
    
    @Override
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
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
    
    @Override
    public String[] addRecord(String[] x) {
     String[] m = addTermsMstr(createRecord());
         return m;
     }
    
    @Override
    public String[] updateRecord(String[] x) {
     cust_term _x = this.x;
     cust_term _y = createRecord();
     String[] m = updateTermsMstr(_y);
     
     // change log check
     if (m[0].equals("0")) {
       ArrayList<change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),_x,_y);
       if (! c.isEmpty()) {
           addChangeLog(c);
       } 
     }
     
     return m;
     }
    
    @Override
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         if (! getTermsUsage(x[0]).isEmpty()) {
             return m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1086)}; 
         }   
         m = deleteTermsMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
        // change log check
        if (m[0].equals("0")) {
            ArrayList<change_log> c = new ArrayList<change_log>();
            c.add(clog(this.x.cut_code(), 
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
    
    @Override
    public String[] getRecord(String[] key) {
       x = getTermsMstr(key);
        return x.m();
    }
    
    @Override
    public cust_term createRecord() { 
        cust_term x = new cust_term(null, tbkey.getText().toString(),
                tbdesc.getText().toUpperCase(),
                duedays.getText(),
                discduedays.getText(),
                discpercent.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbsystemcode.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbmfi.isSelected())),
                ddmfimonth.getSelectedItem().toString(),
                ddmfiday.getSelectedItem().toString()
                );
        return x;
    }
    
    public String[] updateForm() {
        tbkey.setText(x.cut_code());
        tbdesc.setText(x.cut_desc());
        duedays.setText(x.cut_days());
        discduedays.setText(x.cut_discdays());
        discpercent.setText(x.cut_discpercent());
        cbsystemcode.setSelected(BlueSeerUtils.ConvertStringToBool(x.cut_syscode()));   
        cbmfi.setSelected(BlueSeerUtils.ConvertStringToBool(x.cut_mfi())); 
        ddmfimonth.setSelectedItem(x.cut_mfimonth());
        ddmfiday.setSelectedItem(x.cut_mfiday());
        setAction(x.m());
        return x.m();  
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getTermBrowseUtil(luinput.getText(),0, "cut_code");
        } else {
         luModel = DTData.getTermBrowseUtil(luinput.getText(),0, "cut_desc");   
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

    // remove this when new changeLog is effective
    public ArrayList<change_log> oldlogChangeExperimental(cust_term x)  {
        Field[] fs = x.getClass().getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                System.out.println("Name: " + f.getName() + " Value: " + f.get(x));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                bslog(ex);
            } 
        }
                   
        ArrayList<change_log> c = new ArrayList<change_log>();
        
        if (! tbdesc.getText().equals(x.cut_desc())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + tbdesc.getName()), x.cut_desc(), tbdesc.getText())); 
        }
        if (! duedays.getText().equals(x.cut_days())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + duedays.getName()), x.cut_days(), duedays.getText())); 
        }
        if (! discduedays.getText().equals(x.cut_discdays())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + discduedays.getName()), x.cut_discdays(), discduedays.getText())); 
        }
        if (! discpercent.getText().equals(x.cut_discpercent())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + discpercent.getName()), x.cut_discpercent(), discpercent.getText())); 
        }
        if (! ddmfimonth.getSelectedItem().toString().equals(x.cut_mfimonth())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + ddmfimonth.getName()), x.cut_mfimonth(), ddmfimonth.getSelectedItem().toString())); 
        }
        if (! ddmfiday.getSelectedItem().toString().equals(x.cut_mfiday())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + ddmfiday.getName()), x.cut_mfiday(), ddmfiday.getSelectedItem().toString())); 
        }
        if (! String.valueOf(BlueSeerUtils.boolToInt(cbsystemcode.isSelected())).equals(x.cut_syscode())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + cbsystemcode.getName()), x.cut_syscode(), String.valueOf(BlueSeerUtils.boolToInt(cbsystemcode.isSelected())))); 
        } 
        if (! String.valueOf(BlueSeerUtils.boolToInt(cbmfi.isSelected())).equals(x.cut_mfi())) {
          c.add(clog(tbkey.getText(), x.getClass().getSimpleName(), this.getClass().getSimpleName(), tags.getString(this.getClass().getSimpleName() +".label." + cbmfi.getName()), x.cut_mfi(), String.valueOf(BlueSeerUtils.boolToInt(cbmfi.isSelected())))); 
        } 
                
        return c;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        duedays = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        discduedays = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        discpercent = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        ddmfimonth = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        ddmfiday = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cbmfi = new javax.swing.JCheckBox();
        cbsystemcode = new javax.swing.JCheckBox();
        btchangelog = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Terms Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel1.setText("Terms Code:");
        jLabel1.setName("lblid"); // NOI18N

        jLabel2.setText("Description:");
        jLabel2.setName("lbldesc"); // NOI18N

        tbdesc.setName("lbldesc"); // NOI18N

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

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        duedays.setName("lblduedays"); // NOI18N

        jLabel3.setText("Due Days:");
        jLabel3.setName("lblduedays"); // NOI18N

        discduedays.setName("lbldiscduedays"); // NOI18N

        jLabel4.setText("Disc Due Days:");
        jLabel4.setName("lbldiscduedays"); // NOI18N

        discpercent.setName("lbldiscpercent"); // NOI18N

        jLabel5.setText("Disc Percent%");
        jLabel5.setName("lbldiscpercent"); // NOI18N

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

        ddmfimonth.setName("lblmfimonth"); // NOI18N

        jLabel6.setText("MFI Month");
        jLabel6.setName("lblmfimonth"); // NOI18N

        ddmfiday.setName("lbimfiday"); // NOI18N

        jLabel7.setText("MFI Day");
        jLabel7.setName("lbimfiday"); // NOI18N

        cbmfi.setText("use MFI?");
        cbmfi.setName("cbusemfi"); // NOI18N

        cbsystemcode.setText("System Code");
        cbsystemcode.setName("cbsyscode"); // NOI18N

        btchangelog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btchangelog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchangelogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btchangelog, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btclear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbmfi)
                            .addComponent(discpercent, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(discduedays, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(duedays, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddmfimonth, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddmfiday, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btadd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate))
                            .addComponent(cbsystemcode)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup)
                    .addComponent(btchangelog))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbsystemcode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(duedays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discduedays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discpercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbmfi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmfimonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmfiday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       if (! validateInput(BlueSeerUtils.dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(BlueSeerUtils.dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(BlueSeerUtils.dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{tbkey.getText()}); 
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
         newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(BlueSeerUtils.dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btchangelogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchangelogActionPerformed
        callChangeDialog(tbkey.getText(), this.getClass().getSimpleName());
    }//GEN-LAST:event_btchangelogActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btchangelog;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbmfi;
    private javax.swing.JCheckBox cbsystemcode;
    private javax.swing.JComboBox<String> ddmfiday;
    private javax.swing.JComboBox<String> ddmfimonth;
    private javax.swing.JTextField discduedays;
    private javax.swing.JTextField discpercent;
    private javax.swing.JTextField duedays;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    // End of variables declaration//GEN-END:variables
}
