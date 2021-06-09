/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JTable;

/**
 *
 * @author vaughnte
 */
public class CustEDIMaint extends javax.swing.JPanel {

    DefaultListModel listmodel = new DefaultListModel();
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public CustEDIMaint() {
        initComponents();
    }

    public boolean isFile(String myfile) {
         // lets check and see if class exists in package
       boolean isgood = false;
       
       try {
           Class.forName(myfile);
           isgood = true;
           
       } catch( ClassNotFoundException e ) {
           isgood = false;
        //my class isn't there!
       }
       
        return isgood;
    }
    
    public void getCustEDI(String code, String doctype) {
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from edi_mstr where edi_id = " + "'" + code + "'" +
                                      " AND edi_doc = " + "'" + doctype + "'" +
                                              ";");
                while (res.next()) {
                    i++;
                    tbkey.setText(code);
                    dddoc.setSelectedItem(doctype);
                    tbisa.setText(res.getString("edi_isa"));
                    tbisaq.setText(res.getString("edi_isaq"));
                    tbgs.setText(res.getString("edi_gs"));
                    tbmap.setText(res.getString("edi_map"));
                    tbelement.setText(res.getString("edi_eledelim"));
                    tbsegment.setText(res.getString("edi_segdelim"));
                    tbsub.setText(res.getString("edi_subdelim"));
                    tbfileprefix.setText(res.getString("edi_fileprefix"));
                    tbfilesuffix.setText(res.getString("edi_filesuffix"));
                    tbfilepath.setText(res.getString("edi_filepath"));
                    tbversion.setText(res.getString("edi_version"));
                    tbovisa.setText(res.getString("edi_bsisa"));
                    tbovgs.setText(res.getString("edi_bsgs"));
                    tbovisaq.setText(res.getString("edi_bsq"));
                    tbsupplier.setText(res.getString("edi_supcode"));
                    cbfa.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_fa_required")));
                   
                    
                }
               
                
                if (i > 0) {
                   enableAll();
                   getAttributes(code, doctype);
                   btadd.setEnabled(false);
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
     public void getAttributes(String code, String doctype) {
        listmodel.removeAllElements();
        ArrayList<String> list = OVData.getEDIAttributesList(code, doctype);
        for (String x : list) {
            listmodel.addElement(x);
        }
    }
    
    public void clearAll() {
       
      
        
        listAttributes.setModel(listmodel);
        
        dddoc.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
        }
        
        tbisa.setText("");
        tbisaq.setText("");
        tbgs.setText("");
        tbovisa.setText("");
        tbovgs.setText("");
        tbovisaq.setText("");
        tbmap.setText("");
        tbversion.setText("");
        tbsupplier.setText("");
        tbelement.setText("");
        tbsegment.setText("");
        tbsub.setText("");
        tbfilepath.setText("");
        tbfileprefix.setText("");
        tbfilesuffix.setText("");
        tbkey.setText("");
        tbattributekey.setText("");
        tbattributevalue.setText("");
        
    }
    
    public void enableAll() {
        btadd.setEnabled(true);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btlookup.setEnabled(true);
        btdeleteattribute.setEnabled(true);
        btaddattribute.setEnabled(true);
        tbisa.setEnabled(true);
        tbisaq.setEnabled(true);
        tbgs.setEnabled(true);
        tbovisa.setEnabled(true);
        tbovgs.setEnabled(true);
        tbovisaq.setEnabled(true);
        tbmap.setEnabled(true);
        dddoc.setEnabled(true);
        tbversion.setEnabled(true);
        tbsupplier.setEnabled(true);
        tbelement.setEnabled(true);
        tbsegment.setEnabled(true);
        tbsub.setEnabled(true);
        tbfilepath.setEnabled(true);
        tbfileprefix.setEnabled(true);
        tbfilesuffix.setEnabled(true);
        tbkey.setEnabled(true);
     
        tbattributekey.setEnabled(true);
        tbattributevalue.setEnabled(true);
    }
    
    public void disableAll() {
         btadd.setEnabled(false);
          btnew.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btlookup.setEnabled(false);
        btdeleteattribute.setEnabled(false);
        btaddattribute.setEnabled(false);
        tbisa.setEnabled(false);
        tbisaq.setEnabled(false);
        tbgs.setEnabled(false);
        tbovisa.setEnabled(false);
        tbovgs.setEnabled(false);
        tbovisaq.setEnabled(false);
        tbmap.setEnabled(false);
        dddoc.setEnabled(false);
        tbversion.setEnabled(false);
        tbsupplier.setEnabled(false);
        tbelement.setEnabled(false);
        tbsegment.setEnabled(false);
        tbsub.setEnabled(false);
        tbfilepath.setEnabled(false);
        tbfileprefix.setEnabled(false);
        tbfilesuffix.setEnabled(false);
        tbkey.setEnabled(false);
      
        tbattributekey.setEnabled(false);
        tbattributevalue.setEnabled(false);
    }
    
    public void initvars(String[] arg) {
        
       clearAll();
       disableAll();
       
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
       
        if (arg != null && arg.length > 0) {
            getCustEDI(arg[0], arg[1]);
        }
        
       
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getEDICustBrowseUtil(luinput.getText(),0, "edi_id");
        } else {
         luModel = DTData.getEDICustBrowseUtil(luinput.getText(),0, "edi_doc");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString(), target.getValueAt(row,3).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog("ID", "DocType", "Direction"); 
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbovgs = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbgs = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbisaq = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tbovisa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tbovisaq = new javax.swing.JTextField();
        tbmap = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        dddoc = new javax.swing.JComboBox<>();
        tbisa = new javax.swing.JTextField();
        btlookup = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tbfilepath = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tbsupplier = new javax.swing.JTextField();
        tbelement = new javax.swing.JTextField();
        tbfilesuffix = new javax.swing.JTextField();
        tbsegment = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbversion = new javax.swing.JTextField();
        tbfileprefix = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbsub = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblsuffix = new javax.swing.JLabel();
        cbfa = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        btdeleteattribute = new javax.swing.JButton();
        btaddattribute = new javax.swing.JButton();
        tbattributevalue = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAttributes = new javax.swing.JList<>();
        tbattributekey = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Partner Transaction Maintenance"));

        btdelete.setText("delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel3.setText("DocType:");

        jLabel6.setText("Q");

        jLabel5.setText("Code:");

        jLabel15.setText("BS GS");

        jLabel7.setText("GS");

        jLabel1.setText("ISA");

        jLabel13.setText("BS ISA");

        jLabel9.setText("Map");

        jLabel16.setText("BS Q");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbovisaq, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbmap, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbovisa, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(tbisaq, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbovgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)
                                .addComponent(btnew))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbisa, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dddoc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnew)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btlookup))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tbisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbisaq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(tbovisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbovgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbovisaq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap())
        );

        jLabel11.setText("Sub Sep");

        jLabel17.setText("Version");

        jLabel18.setText("SupplierCode");

        jLabel14.setText("FilePath");

        tbelement.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbelementFocusLost(evt);
            }
        });

        tbsegment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsegmentFocusLost(evt);
            }
        });

        jLabel4.setText("Elem Sep");

        jLabel10.setText("Seg Sep");

        tbsub.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsubFocusLost(evt);
            }
        });

        jLabel12.setText("Prefix");

        lblsuffix.setText("Suffix");

        cbfa.setText("Functional Acknowledgement?");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblsuffix, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbfa)
                    .addComponent(tbsub, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbfilepath, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbelement, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbfilesuffix, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .addComponent(tbfileprefix, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbelement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel4)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel10)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbsub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel11)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbfileprefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel12)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbfilesuffix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblsuffix))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbfilepath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel14)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(cbfa)
                .addContainerGap())
        );

        btdeleteattribute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteattribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattributeActionPerformed(evt);
            }
        });

        btaddattribute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddattribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattributeActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(listAttributes);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbattributevalue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btdeleteattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbattributekey))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbattributekey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbattributevalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btaddattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeleteattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addGap(6, 6, 6)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addGap(3, 3, 3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btdelete)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btupdate)
                        .addComponent(btadd)))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                String fa = "";
                int i = 0;
                
                // check the site field
                if (tbkey.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a TP ID");
                    return;
                }
                if (tbmap.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a map name for outbound type");
                    return;
                }
               
               
                
                if ( cbfa.isSelected() ) {
                   fa = "1";    
                } else {
                    fa = "0";
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT edi_id, edi_doc FROM  edi_mstr where edi_id = " + "'" + tbkey.getText() + "'" +
                                          " AND edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" + 
                                          " AND edi_isa = " + "'" + tbisa.getText() + "'" +
                                          ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        
                        st.executeUpdate("insert into edi_mstr "
                            + "(edi_id, edi_doc, edi_isa, edi_isaq, " 
                            + "edi_gs, edi_map, edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, edi_filesuffix, edi_filepath, "
                            + "edi_version, edi_bsisa, edi_bsgs, edi_bsq, edi_supcode, edi_fa_required ) "
                            + " values ( " + "'" + tbkey.getText() + "'" + ","
                                + "'" + dddoc.getSelectedItem().toString() + "'" + ","
                                + "'" + tbisa.getText() + "'" + ","
                                + "'" + tbisaq.getText() + "'" + ","
                                + "'" + tbgs.getText() + "'" + ","
                                + "'" + tbmap.getText() + "'" + ","
                                + "'" + tbelement.getText() + "'" + ","
                                + "'" + tbsegment.getText() + "'" + ","
                                + "'" + tbsub.getText() + "'" + ","
                                + "'" + tbfileprefix.getText() + "'" + ","
                                + "'" + tbfilesuffix.getText() + "'" + ","
                                + "'" + tbfilepath.getText() + "'" + ","
                                + "'" + tbversion.getText() + "'" + ","
                                + "'" + tbovisa.getText() + "'" + ","
                                + "'" + tbovgs.getText() + "'" + ","
                                + "'" + tbovisaq.getText() + "'" + ","
                                + "'" + tbsupplier.getText() + "'"  + ","
                                + "'" + fa + "'"
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added EDI Cust Record");
                    } else {
                        bsmf.MainFrame.show("Code/ISA/Doc/Dir Already Exists");
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       try {
            boolean proceed = true;
           
            String fa = "";
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
                // check the code field
               if (tbkey.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a TP ID");
                    return;
                }
               
                if (tbmap.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a map name");
                    return;
                }
                if (! isFile(tbmap.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Map does not exist");
                    return;
                }
                
               
                if ( cbfa.isSelected() ) {
                   fa = "1";    
                } else {
                    fa = "0";
                }
                
                if (proceed) {
                    st.executeUpdate("update edi_mstr set edi_isa = " + "'" + tbisa.getText() + "'" + ","
                            + "edi_isaq = " + "'" + tbisaq.getText() + "'" + ","
                            + "edi_gs = " + "'" + tbgs.getText() + "'" + ","
                            + "edi_map = " + "'" + tbmap.getText() + "'" + ","
                            + "edi_eledelim = " + "'" + tbelement.getText() + "'" + ","
                            + "edi_segdelim = " + "'" + tbsegment.getText() + "'" + ","
                            + "edi_subdelim = " + "'" + tbsub.getText() + "'" + ","
                            + "edi_fileprefix = " + "'" + tbfileprefix.getText() + "'" + ","
                            + "edi_filesuffix = " + "'" + tbfilesuffix.getText() + "'" + ","
                            + "edi_filepath = " + "'" + tbfilepath.getText() + "'" + ","
                            + "edi_version = " + "'" + tbversion.getText() + "'" + ","
                            + "edi_bsisa = " + "'" + tbovisa.getText() + "'" + ","
                            + "edi_bsgs = " + "'" + tbovgs.getText() + "'" + ","
                            + "edi_bsq = " + "'" + tbovisaq.getText() + "'" + ","
                            + "edi_supcode = " + "'" + tbsupplier.getText() + "'"  + ","
                            + "edi_fa_required = " + "'" + fa + "'"
                            + " where edi_id = " + "'" + tbkey.getText() + "'"     
                            + " AND edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'"
                            + ";");
                    bsmf.MainFrame.show("Updated EDI Master");
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
       
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                 
                
                   int i = st.executeUpdate("delete from edi_mstr where edi_id = " + "'" + tbkey.getText() + "'" + 
                                            " and edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                                            ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbkey.getText() + "/" + dddoc.getSelectedItem().toString());
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        clearAll();
        enableAll();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btlookup.setEnabled(false);
        btnew.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed

    private void tbelementFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbelementFocusLost
            String x = BlueSeerUtils.bsformat("", tbelement.getText(), "0");
        if (x.equals("error")) {
            tbelement.setText("");
            tbelement.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbelement.requestFocus();
        } else {
            tbelement.setText(x);
            tbelement.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbelementFocusLost

    private void tbsegmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsegmentFocusLost
           String x = BlueSeerUtils.bsformat("", tbsegment.getText(), "0");
        if (x.equals("error")) {
            tbsegment.setText("");
            tbsegment.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsegment.requestFocus();
        } else {
            tbsegment.setText(x);
            tbsegment.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbsegmentFocusLost

    private void tbsubFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsubFocusLost
         String x = BlueSeerUtils.bsformat("", tbsub.getText(), "0");
        if (x.equals("error")) {
            tbsub.setText("");
            tbsub.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsub.requestFocus();
        } else {
            tbsub.setText(x);
            tbsub.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbsubFocusLost

    private void btaddattributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattributeActionPerformed
       
        OVData.addEDIAttributeRecord(tbkey.getText(), dddoc.getSelectedItem().toString(), tbattributekey.getText(), tbattributevalue.getText());
        getAttributes(tbkey.getText(), dddoc.getSelectedItem().toString());
        tbattributekey.setText("");
        tbattributevalue.setText("");
    }//GEN-LAST:event_btaddattributeActionPerformed

    private void btdeleteattributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattributeActionPerformed
        boolean proceed = true; 
        
      
       
        
        
        if (listAttributes.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show("nothing is selected");
        } else {
           proceed = bsmf.MainFrame.warn("Are you sure?");
        }
        if (proceed) {
            String[] z = listAttributes.getSelectedValue().toString().split(":");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from edi_attr where exa_tpid = " + "'" + tbkey.getText() + "'" + 
                                            " and exa_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                                            " and exa_key = " + "'" + z[0].toString() + "'" +
                                             ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + listAttributes.getSelectedValue().toString());
                    getAttributes(tbkey.getText(), dddoc.getSelectedItem().toString());
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Unable to Delete edi_attr Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteattributeActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattribute;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattribute;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbfa;
    private javax.swing.JComboBox<String> dddoc;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblsuffix;
    private javax.swing.JList<String> listAttributes;
    private javax.swing.JTextField tbattributekey;
    private javax.swing.JTextField tbattributevalue;
    private javax.swing.JTextField tbelement;
    private javax.swing.JTextField tbfilepath;
    private javax.swing.JTextField tbfileprefix;
    private javax.swing.JTextField tbfilesuffix;
    private javax.swing.JTextField tbgs;
    private javax.swing.JTextField tbisa;
    private javax.swing.JTextField tbisaq;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmap;
    private javax.swing.JTextField tbovgs;
    private javax.swing.JTextField tbovisa;
    private javax.swing.JTextField tbovisaq;
    private javax.swing.JTextField tbsegment;
    private javax.swing.JTextField tbsub;
    private javax.swing.JTextField tbsupplier;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
