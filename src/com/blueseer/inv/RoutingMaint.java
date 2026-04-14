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
package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.hrm.hrmData.getEmpFormalNameByID;
import static com.blueseer.inv.invData.addInvMetaOperator;
import static com.blueseer.inv.invData.addRoutingMstr;
import static com.blueseer.inv.invData.deleteInvMetaOperator;
import static com.blueseer.inv.invData.deleteRoutingMstr;
import static com.blueseer.inv.invData.getInvMetaOperators;
import static com.blueseer.inv.invData.getRoutingMstrList;
import static com.blueseer.inv.invData.updateRoutingMstr;
import com.blueseer.inv.invData.wf_mstr;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerV;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
public class RoutingMaint extends javax.swing.JPanel implements IBlueSeerV {

    // global variable declarations
                boolean isLoad = false;
               // public static wf_mstr x = null;
                public static ArrayList<wf_mstr> x = null;
                ArrayList<String[]> initDataSets = null;
                String defaultSite = "";
                String defaultCurrency = "";
                boolean canUpdate = false;
    
   // global datatablemodel declarations   
    DefaultListModel listmodel = new DefaultListModel();
    
    javax.swing.table.DefaultTableModel operationmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Operation", "Description", "Backflush", "WorkCenter", "SetUp Hours", "Labor Run Rate"
            });
    
    public RoutingMaint() {
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
        tbkey.setText("");
        tbop.setText("");
        tbopdesc.setText("");
        tbrunhoursinverted.setText("0");
        lbloperatorname.setText("");
        
        operationmodel.setRowCount(0);
        tableoperation.setModel(operationmodel);
        tableoperation.getTableHeader().setReorderingAllowed(false);
        
        listmodel.removeAllElements();
        listOperators.setModel(listmodel);
        
        
        tbrunhours.setText("0");
        tbsetuphours.setText("0");
        cbmilestone.setSelected(false);
        
        ddsite.removeAllItems();
        ddwc.removeAllItems();
        ddoperator.removeAllItems();
        
        if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "workcenters,employees");
        }
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }            
            if (s[0].equals("site")) {
              defaultSite = s[1]; 
            }
            if (s[0].equals("workcenters")) {
              ddwc.addItem(s[1]);  
            }
            if (s[0].equals("employees")) {
              ddoperator.addItem(s[1]);  
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]);  
            }
        }
        
        ddsite.setSelectedItem(defaultSite);
        ddwc.insertItemAt("", 0);
        ddwc.setSelectedIndex(0);
        ddoperator.setSelectedIndex(0);
        
        
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
       
        if (! canUpdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"wf_mstr"});
        int fc;

        fc = checkLength(f,"wf_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"wf_site");
        if (ddsite.getSelectedItem().toString().length() > fc || ddsite.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            ddsite.requestFocus();
            return false;
        }        
        
        return true;
    }
    
    public boolean validateOPInput(dbaction x) {
       
                
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"wf_mstr"});
        int fc;
        
        fc = checkLength(f,"wf_op");
        if (tbop.getText().length() > fc || tbop.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbop.requestFocus();
            return false;
        }
        
        if (! BlueSeerUtils.isParsableToInt(tbop.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbop.requestFocus();
            return false;            
        }
        
        fc = checkLength(f,"wf_cell");
        if (ddwc.getSelectedItem().toString().length() > fc || ddwc.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            ddwc.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"wf_op_desc");
        if (tbopdesc.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbopdesc.requestFocus();
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
         // String[] m = addRoutingMstr(createRecord());
         String[] m = addRoutingMstr(createRecordArray());
        
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = updateRoutingMstr(createRecordArray());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteRoutingMstr(createRecord());  
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
        if (key == null && key.length < 1) { return new String[]{}; };
        x = getRoutingMstrList(key);
        if (x.size() > 0) {
            return new String[]{"0",""};
        } else {
            return new String[]{"1","no records found"};
        }
    }
    
    public wf_mstr createRecord() {
        wf_mstr x = new wf_mstr(null, 
           tbkey.getText(),
           tbopdesc.getText(),
            ddsite.getSelectedItem().toString(),
            tbop.getText(),
            String.valueOf(BlueSeerUtils.boolToInt(cbmilestone.isSelected())),
            tbopdesc.getText(),
            ddwc.getSelectedItem().toString(),    
            tbsetuphours.getText().replace(defaultDecimalSeparator, '.'),
            tbrunhours.getText().replace(defaultDecimalSeparator, '.')
        );
        return x;
    }
    
    public ArrayList<wf_mstr> createRecordArray() {
        ArrayList<wf_mstr> list = new ArrayList<wf_mstr>();
        double runhours = 0;
        for (int j = 0; j < tableoperation.getRowCount(); j++) {
        
        if (! tableoperation.getValueAt(j, 5).toString().isBlank() && Double.valueOf(tableoperation.getValueAt(j, 5).toString()) > 0) {
            runhours = (1 / Double.valueOf(tableoperation.getValueAt(j, 5).toString()));
        } else {
            runhours = 0;
        }   
            
        wf_mstr wf = new wf_mstr(null, 
           tbkey.getText(),
           tbopdesc.getText(),
           ddsite.getSelectedItem().toString(),
           tableoperation.getValueAt(j, 0).toString(),
           tableoperation.getValueAt(j, 1).toString(),
           tableoperation.getValueAt(j, 2).toString(),
           tableoperation.getValueAt(j, 3).toString(),
           tableoperation.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'),
           bsNumber(runhours) 
        );
        list.add(wf);
        }
        
        return list;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getRoutingBrowseUtil(luinput.getText(),0, "wf_id");
        } else {
         luModel = DTData.getRoutingBrowseUtil(luinput.getText(),0, "wf_op");   
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
                initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lbloperation", this.getClass().getSimpleName())); 
        
        
        
    }

    public void lookUpFrameWorkCenter() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getWorkCenterBrowseUtil(luinput.getText(),0, "wc_cell");
        } else {
         luModel = DTData.getWorkCenterBrowseUtil(luinput.getText(),0, "wc_desc");   
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
                ddwc.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lbloperation", this.getClass().getSimpleName())); 
         
        
        
    }

    public void updateForm() {
        if (x != null && x.size() > 0) {
        String runrate = "0";
       
        int i = 0;
        for (wf_mstr wf : x) {
        i++;
        
        if (i == 1) {
           tbkey.setText(wf.wf_id()); 
           ddsite.setSelectedItem(wf.wf_site());
        }
        
        if (! wf.wf_run_hours().isBlank() && Double.valueOf(wf.wf_run_hours()) > 0) {
            runrate = bsNumber((int) Math.round(1 / Double.valueOf(wf.wf_run_hours())));
        } else {
            runrate = "0";
        }
        
        operationmodel.addRow(new Object[]{ wf.wf_op(), wf.wf_op_desc(), wf.wf_assert(), wf.wf_cell(), wf.wf_setup_hours(), runrate });
        }
        
        //getOperators(tbkey.getText(), tbop.getText());
        
        setAction(new String[]{"0",""});  
        }
    }
    
    // custom funcs
    
    public void getOperators(String routing, String op) {
        listmodel.removeAllElements();
        ArrayList<String> operators = getInvMetaOperators(routing, op);
        for (String operator : operators) {
            listmodel.addElement(operator);
        } 
    }
    
    public boolean hasOperation(String op) {
        boolean x = false;
        for (int j = 0; j < tableoperation.getRowCount(); j++) {
           if (tableoperation.getValueAt(j, 0).toString().toLowerCase().equals(op.toLowerCase())) {
               x = true;
               break;
           }           
        }
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

        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        tbopdesc = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cbmilestone = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbrunhoursinverted = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbrunhours = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsetuphours = new javax.swing.JTextField();
        ddsite = new javax.swing.JComboBox<>();
        btnew = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        ddwc = new javax.swing.JComboBox<>();
        btlookup = new javax.swing.JButton();
        btlookupWorkCenter = new javax.swing.JButton();
        ddoperator = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        listOperators = new javax.swing.JList<>();
        btaddoperator = new javax.swing.JButton();
        btdeleteoperator = new javax.swing.JButton();
        lbloperatorname = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableoperation = new javax.swing.JTable();
        btaddop = new javax.swing.JButton();
        btupdateop = new javax.swing.JButton();
        btdeleteop = new javax.swing.JButton();
        tbop = new javax.swing.JTextField();
        btclearop = new javax.swing.JButton();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(0, 102, 204));
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Routing Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel3.setText("Operation Desc:");
        jLabel3.setName("lbloperationdesc"); // NOI18N

        cbmilestone.setText("Auto Backflush?");
        cbmilestone.setName("cbauto"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Routing ID:");
        jLabel1.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel6.setText("Operation:");
        jLabel6.setName("lbloperation"); // NOI18N

        tbrunhoursinverted.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrunhoursinvertedFocusLost(evt);
            }
        });

        jLabel7.setText("Work Cell:");
        jLabel7.setName("lblworkcell"); // NOI18N

        jLabel10.setText("Run Pieces/Hr");
        jLabel10.setName("lblrunpieces"); // NOI18N

        jLabel9.setText("Setup Hours Per (lotsize)");
        jLabel9.setName("lblsetuphours"); // NOI18N

        jLabel2.setText("Site:");
        jLabel2.setName("lblsite"); // NOI18N

        tbrunhours.setEditable(false);

        jLabel12.setText("Run Hours");
        jLabel12.setName("lblrunhours"); // NOI18N

        tbsetuphours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetuphoursFocusLost(evt);
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

        btlookupWorkCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupWorkCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupWorkCenterActionPerformed(evt);
            }
        });

        ddoperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddoperatorActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(listOperators);

        btaddoperator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddoperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddoperatorActionPerformed(evt);
            }
        });

        btdeleteoperator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteoperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteoperatorActionPerformed(evt);
            }
        });

        jLabel4.setText("Operator");
        jLabel4.setName("lbloperator"); // NOI18N

        tableoperation.setModel(new javax.swing.table.DefaultTableModel(
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
        tableoperation.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableoperationMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableoperation);

        btaddop.setText("Add Operation");
        btaddop.setName("btaddop"); // NOI18N
        btaddop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddopActionPerformed(evt);
            }
        });

        btupdateop.setText("Update Operation");
        btupdateop.setName("btupdateop"); // NOI18N
        btupdateop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateopActionPerformed(evt);
            }
        });

        btdeleteop.setText("Delete Operation");
        btdeleteop.setName("btdeleteop"); // NOI18N
        btdeleteop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteopActionPerformed(evt);
            }
        });

        tbop.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbopFocusLost(evt);
            }
        });

        btclearop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newfile.png"))); // NOI18N
        btclearop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear))
                                    .addComponent(cbmilestone, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ddwc, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbsetuphours, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbrunhoursinverted, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(26, 26, 26)
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbrunhours, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addComponent(btlookupWorkCenter, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(ddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btaddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btdeleteoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btaddop)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btupdateop)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btdeleteop))
                                    .addComponent(tbopdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tbop)
                                            .addComponent(ddsite, 0, 105, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclearop, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(271, 271, 271)
                                        .addComponent(lbloperatorname, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(tbop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbloperatorname, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btclearop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbopdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbmilestone)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(ddwc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btlookupWorkCenter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(tbsetuphours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addComponent(btaddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btdeleteoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrunhoursinverted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tbrunhours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddop)
                    .addComponent(btdeleteop)
                    .addComponent(btupdateop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
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
        executeTask(dbaction.update, new String[]{tbkey.getText(), tbop.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText(), tbop.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbrunhoursinvertedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrunhoursinvertedFocusLost
        if (! tbrunhoursinverted.getText().isEmpty() && bsParseDouble(tbrunhoursinverted.getText()) > 0)
        tbrunhours.setText(bsFormatDouble(1 / bsParseDouble(tbrunhoursinverted.getText()), "7"));
        else
        tbrunhours.setText("0");
    }//GEN-LAST:event_tbrunhoursinvertedFocusLost

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbsetuphoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetuphoursFocusLost
          String x = BlueSeerUtils.bsformat("", tbsetuphours.getText(), "5"); // updated to 5 decimal places 20220531
        if (x.equals("error")) {
            tbsetuphours.setText("");
            tbsetuphours.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbsetuphours.requestFocus();
        } else {
            tbsetuphours.setText(x);
            tbsetuphours.setBackground(Color.white);
        }
        if (tbsetuphours.getText().isEmpty()) {
            tbsetuphours.setText("0");
        }
    }//GEN-LAST:event_tbsetuphoursFocusLost

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

    private void btlookupWorkCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupWorkCenterActionPerformed
        lookUpFrameWorkCenter();
    }//GEN-LAST:event_btlookupWorkCenterActionPerformed

    private void btaddoperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddoperatorActionPerformed
        if (! lbloperatorname.getText().isBlank() && ! tbop.getText().isBlank()) {
        addInvMetaOperator(tbkey.getText(), tbop.getText(), lbloperatorname.getText());
        getOperators(tbkey.getText(), tbop.getText());
        ddoperator.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btaddoperatorActionPerformed

    private void btdeleteoperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteoperatorActionPerformed
        boolean proceed = true;

        if (listOperators.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1029));
        } else {
            proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed && ! tbop.getText().isBlank()) {
            deleteInvMetaOperator(tbkey.getText(), tbop.getText(), listOperators.getSelectedValue().toString());
            getOperators(tbkey.getText(), tbop.getText());
        }
    }//GEN-LAST:event_btdeleteoperatorActionPerformed

    private void ddoperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddoperatorActionPerformed
        if (! isLoad && ddoperator.getSelectedItem() != null ) {
            if (! ddoperator.getSelectedItem().toString().isBlank()) {
                lbloperatorname.setText(getEmpFormalNameByID(ddoperator.getSelectedItem().toString()));
            } else {
                lbloperatorname.setText("");
            }
        }
    }//GEN-LAST:event_ddoperatorActionPerformed

    private void tableoperationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableoperationMouseClicked
        int row = tableoperation.rowAtPoint(evt.getPoint());
        int col = tableoperation.columnAtPoint(evt.getPoint());
        // element, percent, type, enabled
        tbop.setText(tableoperation.getValueAt(row, 0).toString());
        tbopdesc.setText(tableoperation.getValueAt(row, 1).toString());
        cbmilestone.setSelected(BlueSeerUtils.ConvertStringToBool(tableoperation.getValueAt(row, 2).toString()));
        ddwc.setSelectedItem(tableoperation.getValueAt(row, 3).toString());
        tbsetuphours.setText(tableoperation.getValueAt(row, 4).toString());
        
        tbrunhoursinverted.setText(tableoperation.getValueAt(row, 5).toString());
        double runhours = 0;
        if (! tableoperation.getValueAt(row, 5).toString().isBlank() && Double.valueOf(tableoperation.getValueAt(row, 5).toString()) > 0) {
            runhours = (1 / Double.valueOf(tableoperation.getValueAt(row, 5).toString()));
        } else {
            runhours = 0;
        }
        tbrunhours.setText(bsNumber(runhours));
        getOperators(tbkey.getText(), tableoperation.getValueAt(row, 0).toString());
        
    }//GEN-LAST:event_tableoperationMouseClicked

    private void btaddopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddopActionPerformed
        
        if (! validateOPInput(dbaction.add)) {
           return;
       }
        
        String defaultvalue = String.valueOf(BlueSeerUtils.boolToInt(cbmilestone.isSelected()));
       
        if (! hasOperation(tbop.getText())) {
        operationmodel.addRow(new Object[]{ tbop.getText(), tbopdesc.getText(), defaultvalue, ddwc.getSelectedItem().toString(), tbsetuphours.getText(), tbrunhoursinverted.getText() });
        
        tbop.setText("");
        tbopdesc.setText("");
        ddwc.setSelectedIndex(0);
        tbsetuphours.setText("0");
        tbrunhoursinverted.setText("0");
        tbrunhours.setText("");
        cbmilestone.setSelected(false);
        } else {
            bsmf.MainFrame.show("Operation already exists");
        }
        
    }//GEN-LAST:event_btaddopActionPerformed

    private void btupdateopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateopActionPerformed
        int[] rows = tableoperation.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        for (int i : rows) {
            tableoperation.setValueAt(tbopdesc.getText(), i, 1);
            tableoperation.setValueAt(String.valueOf(BlueSeerUtils.boolToInt(cbmilestone.isSelected())), i, 2);
            tableoperation.setValueAt(ddwc.getSelectedItem().toString(), i, 3);
            tableoperation.setValueAt(tbsetuphours.getText(), i, 4);
            tableoperation.setValueAt(tbrunhoursinverted.getText(), i, 5);
        }
        
    }//GEN-LAST:event_btupdateopActionPerformed

    private void btdeleteopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteopActionPerformed
        int[] rows = tableoperation.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tableoperation.getModel()).removeRow(i);

        }
    }//GEN-LAST:event_btdeleteopActionPerformed

    private void btclearopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearopActionPerformed
        tbop.setText("");
        tbopdesc.setText("");
        ddwc.setSelectedIndex(0);
        tbsetuphours.setText("0");
        tbrunhoursinverted.setText("0");
        tbrunhours.setText("");
        cbmilestone.setSelected(false);
        
        tbop.setBackground(Color.yellow);
        tbop.requestFocus();
        
    }//GEN-LAST:event_btclearopActionPerformed

    private void tbopFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbopFocusLost
       
        String x = BlueSeerUtils.bsformat("s", tbop.getText(), "0"); // updated to 5 decimal places 20220531
        if (x.equals("error")) {
            tbop.setText("");
            tbop.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbop.requestFocus();
        } else {
            tbop.setText(x);
            tbop.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbopFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddop;
    private javax.swing.JButton btaddoperator;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearop;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteop;
    private javax.swing.JButton btdeleteoperator;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupWorkCenter;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateop;
    private javax.swing.JCheckBox cbmilestone;
    private javax.swing.JComboBox<String> ddoperator;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddwc;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbloperatorname;
    private javax.swing.JList<String> listOperators;
    private javax.swing.JTable tableoperation;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbop;
    private javax.swing.JTextField tbopdesc;
    private javax.swing.JTextField tbrunhours;
    private javax.swing.JTextField tbrunhoursinverted;
    private javax.swing.JTextField tbsetuphours;
    // End of variables declaration//GEN-END:variables
}
