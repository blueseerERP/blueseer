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

import com.blueseer.fgl.*;
import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.reinitpanels;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */


public class EDIDocumentMaint extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
                boolean isLoad = false;
    
    // global datatablemodel declarations       
     javax.swing.table.DefaultTableModel rulemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Role", "RecordType", "ValueType", "Row", "Column", "Length", "Regex", "Value", "Tag", "Enabled"
            });
    
    
    public EDIDocumentMaint() {
        initComponents();
    }

    // interface functions implemented
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
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
    
    public void setComponentDefaultValues() {
       isLoad = true;
        rulemodel.setRowCount(0);
           tablerole.setModel(rulemodel);
           tbkey.setText("");
           tbdesc.setText("");
           cbenableddet.setSelected(false);
       dddoc.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
        } 
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
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
               
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a code");
                    tbkey.requestFocus();
                    return b;
                }
                
                if (tbdesc.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a description");
                    tbdesc.requestFocus();
                    return b;
                }
                
                if (tablerole.getRowCount() < 1) {
                    b = false;
                    bsmf.MainFrame.show("must have at least one rule in table");
                    ddrole.requestFocus();
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
            executeTask("get",arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     
     try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                proceed = validateInput("addRecord");
                String headerenabled = "0";
                if (cbenabledhdr.isSelected()) {
                    headerenabled = "1";
                }
                if (proceed) {

                    res = st.executeQuery("SELECT edd_id FROM  edi_doc where edd_id = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into edi_doc (edd_id, edd_desc, edd_type, edd_subtype, edd_segdelim, edd_priority, edd_landmark, edd_enabled) values (" + 
                            "'" + tbkey.getText() + "'" + "," +
                            "'" + tbdesc.getText() + "'"  +  "," + 
                            "'" + dddoc.getSelectedItem().toString() + "'"  +  "," + 
                            "'" + tbsubtype.getText() + "'"  + "," + 
                            "'" + tbsegdelimiter.getText() + "'"  +  "," + 
                            "'" + tbpriority.getText() + "'"  +  "," + 
                            "'" + tblandmark.getText() + "'"  + "," +
                            "'" + headerenabled + "'" +
                            ")" + ";");     
                
              
                 for (int j = 0; j < tablerole.getRowCount(); j++) {
                st.executeUpdate("insert into edi_docdet (edid_id, edid_role, edid_rectype, edid_valuetype, edid_row, edid_col, edid_length, edid_regex, edid_value, edid_tag, edid_enabled ) values ( " 
                        + "'" + tbkey.getText() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 2).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 3).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 4).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 5).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 6).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 7).toString() + "'" + ","
                        + "'" + tablerole.getValueAt(j, 8).toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tablerole.getValueAt(j, 9).toString())) + "'" 
                        + " );" );
                 }
                        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists}; 
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
     
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
            boolean proceed = true;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
               proceed = validateInput("updateRecord");
               String headerenabled = "0";
                if (cbenabledhdr.isSelected()) {
                    headerenabled = "1";
                }
                
                if (proceed) {
                       st.executeUpdate("update edi_doc set " + 
                           "edd_desc = " + "'" + tbdesc.getText() + "'" + "," +
                           "edd_type = " + "'" + dddoc.getSelectedItem().toString() + "'" + "," +
                           "edd_subtype = " + "'" + tbsubtype.getText() + "'" + "," +
                           "edd_segdelim = " + "'" + tbsegdelimiter.getText() + "'" + "," +
                           "edd_priority = " + "'" + tbpriority.getText() + "'" + "," +
                           "edd_landmark = " + "'" + tblandmark.getText() + "'" + "," +
                           "edd_enabled = " + "'" + headerenabled + "'" +     
                           " where edd_id = " + "'" + tbkey.getText() + "'" +
                             ";");     
                
                       //  now lets delete all stored actions of this master task...then add back from table
                        st.executeUpdate("delete from edi_docdet where edid_id = " + "'" + tbkey.getText() + "'" + ";");

                         for (int j = 0; j < tablerole.getRowCount(); j++) {
                        st.executeUpdate("insert into edi_docdet (edid_id, edid_role, edid_rectype, edid_valuetype, edid_row, edid_col, edid_length, edid_regex, edid_value, edid_tag, edid_enabled ) values ( " 
                                + "'" + tbkey.getText() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 0).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 1).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 2).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 3).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 4).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 5).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 6).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 7).toString() + "'" + ","
                                + "'" + tablerole.getValueAt(j, 8).toString() + "'" + ","        
                                + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tablerole.getValueAt(j, 9).toString())) + "'" 
                                + " );" );
                         }
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                  
                    int i = st.executeUpdate("delete from edi_doc where edd_id = " + "'" + tbkey.getText() + "'" + ";");
                    int j = st.executeUpdate("delete from edi_docdet where edid_id = " + "'" + tbkey.getText() + "'" + ";");
                    if (i > 0 && j > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    initvars(null);
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  edi_doc where edd_id = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                        i++;
                        tbdesc.setText(res.getString("edd_desc"));
                        tbkey.setText(res.getString("edd_id"));
                        dddoc.setSelectedItem(res.getString("edd_type"));
                        tbsubtype.setText(res.getString("edd_subtype"));
                        tbsegdelimiter.setText(res.getString("edd_segdelim"));
                        tblandmark.setText(res.getString("edd_landmark"));
                        cbenabledhdr.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edd_enabled")));
                    }
                    res = st.executeQuery("SELECT * FROM  edi_docdet where " +
                            " edid_id = " + "'" + x[0] + "'" + ";");
                    while (res.next()) {
                     rulemodel.addRow(new Object[]{res.getString("edid_role"), 
                         res.getString("edid_rectype"), 
                         res.getString("edid_valuetype"),
                         res.getString("edid_row"),
                         res.getString("edid_col"),
                         res.getString("edid_length"),
                         res.getString("edid_regex"),
                         res.getString("edid_value"),
                         res.getString("edid_tag"),
                         res.getBoolean("edid_enabled")});   
                    }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getDocRulesBrowseUtil(luinput.getText(),0, "edd_id");
        } else {
         luModel = DTData.getDocRulesBrowseUtil(luinput.getText(),0, "edd_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle("No Records Found!");
        } else {
            ludialog.setTitle(luModel.getRowCount() + " Records Found!");
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
      
        callDialog("RulesID", "Description"); 
        
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tablerole = new javax.swing.JTable();
        cbenableddet = new javax.swing.JCheckBox();
        btdeleteelement = new javax.swing.JButton();
        btaddelement = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        ddrectype = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        ddrole = new javax.swing.JComboBox<>();
        ddvaluetype = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tbrow = new javax.swing.JTextField();
        tbcolumn = new javax.swing.JTextField();
        tblength = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tbregex = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        tbvalue = new javax.swing.JTextField();
        tbtag = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnew = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        tbdesc = new javax.swing.JTextField();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        tbsubtype = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbsegdelimiter = new javax.swing.JTextField();
        tbpriority = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbenabledhdr = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        tblandmark = new javax.swing.JTextField();
        dddoc = new javax.swing.JComboBox<>();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Document Maintenance"));

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Rules"));

        tablerole.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablerole);

        cbenableddet.setText("Enabled?");

        btdeleteelement.setText("DeleteElement");
        btdeleteelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteelementActionPerformed(evt);
            }
        });

        btaddelement.setText("AddElement");
        btaddelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddelementActionPerformed(evt);
            }
        });

        jLabel4.setText("Role");

        ddrectype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "fixed", "regex" }));

        jLabel7.setText("RecordType");

        ddrole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "data", "selection" }));

        ddvaluetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "constant", "variable" }));

        jLabel11.setText("ValueType");

        jLabel3.setText("Row");

        jLabel12.setText("Column");

        jLabel13.setText("Length");

        jLabel14.setText("Regex");

        jLabel15.setText("Value");

        jLabel16.setText("Tag");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ddrole, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddrectype, javax.swing.GroupLayout.Alignment.LEADING, 0, 104, Short.MAX_VALUE)
                    .addComponent(ddvaluetype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbrow, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbcolumn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tblength, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbtag, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbregex, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btdeleteelement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbenableddet)
                    .addComponent(btaddelement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btdeleteelement)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddrole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(tbrow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(btaddelement)
                            .addComponent(tbregex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddrectype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(tbcolumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddvaluetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(tblength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(cbenableddet)
                            .addComponent(tbtag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Document Header Maintenance"));

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("Code");

        jLabel6.setText("Desc");

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
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

        jLabel1.setText("Type");

        jLabel2.setText("SubType");

        jLabel8.setText("SegDelimiter");

        jLabel9.setText("Prioirity");

        cbenabledhdr.setText("Enabled");

        jLabel10.setText("LandMark");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addComponent(tbdesc))
                        .addGap(26, 26, 26)
                        .addComponent(cbenabledhdr))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbsubtype, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                            .addComponent(dddoc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbsegdelimiter)
                            .addComponent(tbpriority, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                        .addGap(27, 27, 27)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tblandmark, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
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
                        .addComponent(cbenabledhdr))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tbsegdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(tblandmark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsubtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbpriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

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
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btupdateActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddelementActionPerformed
        
       if (! BlueSeerUtils.isParsableToInt(tbrow.getText())) {
          bsmf.MainFrame.show("row value must be an integer"); 
       }
       if (! BlueSeerUtils.isParsableToInt(tbcolumn.getText())) {
          bsmf.MainFrame.show("column value must be an integer"); 
       }
       if (! BlueSeerUtils.isParsableToInt(tblength.getText())) {
          bsmf.MainFrame.show("length value must be an integer"); 
       }
        //"Role", "RecordType", "ValueType", "Row", "Column", "Length", "Regex", "Value", "Tag", "Enabled"
        rulemodel.addRow(new Object[]{ ddrole.getSelectedItem().toString(), 
            ddrectype.getSelectedItem().toString(), 
            ddvaluetype.getSelectedItem().toString(), 
            tbrow.getText(), tbcolumn.getText(), 
            tblength.getText(), tbregex.getText(),
            tbvalue.getText(), tbtag.getText(),
            String.valueOf(cbenableddet.isSelected()) });
    }//GEN-LAST:event_btaddelementActionPerformed

    private void btdeleteelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteelementActionPerformed
       int[] rows = tablerole.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) tablerole.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeleteelementActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
         executeTask("get", new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddelement;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteelement;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbenableddet;
    private javax.swing.JCheckBox cbenabledhdr;
    private javax.swing.JComboBox<String> dddoc;
    private javax.swing.JComboBox<String> ddrectype;
    private javax.swing.JComboBox<String> ddrole;
    private javax.swing.JComboBox<String> ddvaluetype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JTable tablerole;
    private javax.swing.JTextField tbcolumn;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tblandmark;
    private javax.swing.JTextField tblength;
    private javax.swing.JTextField tbpriority;
    private javax.swing.JTextField tbregex;
    private javax.swing.JTextField tbrow;
    private javax.swing.JTextField tbsegdelimiter;
    private javax.swing.JTextField tbsubtype;
    private javax.swing.JTextField tbtag;
    private javax.swing.JTextField tbvalue;
    // End of variables declaration//GEN-END:variables
}
