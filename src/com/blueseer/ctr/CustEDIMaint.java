/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.ctr;

import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class CustEDIMaint extends javax.swing.JPanel {

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
    
    public void getCustEDI(String code, String doctype, String dir) {
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from cmedi_mstr where cme_code = " + "'" + code + "'" +
                                      " AND cme_doc = " + "'" + doctype + "'" +
                                              " AND cme_dir = " + "'" + dir + "'" + 
                                              ";");
                while (res.next()) {
                    i++;
                    tbcust.setText(code);
                    dddoc.setSelectedItem(doctype);
                    ddtpid.setSelectedItem(res.getString("cme_isa"));
                    tbisaq.setText(res.getString("cme_isaqual"));
                    tbgs.setText(res.getString("cme_gs"));
                    tbmap.setText(res.getString("cme_map"));
                    tbelement.setText(res.getString("cme_eledelim"));
                    tbsegment.setText(res.getString("cme_segdelim"));
                    tbsub.setText(res.getString("cme_subdelim"));
                    tbfileprefix.setText(res.getString("cme_fileprefix"));
                    tbfilesuffix.setText(res.getString("cme_filesuffix"));
                    tbfilepath.setText(res.getString("cme_filepath"));
                    tbversion.setText(res.getString("cme_version"));
                    tbovisa.setText(res.getString("cme_ov_isa"));
                    tbovgs.setText(res.getString("cme_ov_gs"));
                    tbovisaq.setText(res.getString("cme_ov_isaqual"));
                    tbsupplier.setText(res.getString("cme_supplier_code"));
                    if (res.getString("cme_dir").equals("1")) {
                        rbin.setSelected(true);
                    } else {
                        rbout.setSelected(true);
                    }
                    
                }
               
                
                if (i > 0) {
                   enableAll();
                   btadd.setEnabled(false);
                }
            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve cmedi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void clearAll() {
       
        buttonGroup1.add(rbin);
        buttonGroup1.add(rbout);
        
        
        ddtpid.removeAllItems();
        ArrayList<String> mylist = OVData.getEDIUniqueTPID();
        for (int i = 0; i < mylist.size(); i++) {
            ddtpid.addItem(mylist.get(i).toUpperCase());
        }  
        dddoc.removeAllItems();
        mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
        }
        
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
        tbcust.setText("");
    }
    
    public void enableAll() {
        btadd.setEnabled(true);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btbrowse.setEnabled(true);
        ddtpid.setEnabled(true);
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
        tbcust.setEnabled(true);
        rbin.setEnabled(true);
        rbout.setEnabled(true);
    }
    
    public void disableAll() {
         btadd.setEnabled(false);
          btnew.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
        ddtpid.setEnabled(false);
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
        tbcust.setEnabled(false);
         rbin.setEnabled(false);
        rbout.setEnabled(false);
    }
    
      public void initvars(String arg) {
        
       clearAll();
       disableAll();
       
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
       
        if (! arg.isEmpty()) {
            String[] args = arg.split(",",-1);
            getCustEDI(args[0], args[1], args[2]);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbovgs = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbcust = new javax.swing.JTextField();
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
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        ddtpid = new javax.swing.JComboBox<>();
        dddoc = new javax.swing.JComboBox<>();
        rbin = new javax.swing.JRadioButton();
        rbout = new javax.swing.JRadioButton();
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

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Entity EDI Maintenance"));

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

        jLabel6.setText("ISA Q");

        jLabel5.setText("Code:");

        jLabel15.setText("OV GS");

        jLabel7.setText("GS ID");

        jLabel1.setText("TPID / ISA ID");

        jLabel13.setText("OV ISA");

        jLabel9.setText("Map");

        jLabel16.setText("OV ISA Q");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        rbin.setText("Inbound");

        rbout.setText("Outbound");

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
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddtpid, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(tbmap, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tbgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tbovisa, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(tbisaq, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbovgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rbin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbout))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btbrowse)
                    .addComponent(btnew)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbin)
                    .addComponent(rbout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ddtpid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbisaq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tbgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jLabel4.setText("Elem Sep");

        jLabel10.setText("Seg Sep");

        jLabel12.setText("Prefix");

        lblsuffix.setText("Suffix");

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
                        .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(tbsupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)))
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
                .addGap(27, 27, 27))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                boolean dir = false;
                int i = 0;
                
                // check the site field
                if (tbcust.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a TP ID");
                    return;
                }
                if (tbmap.getText().isEmpty() && rbout.isSelected()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a map name for outbound type");
                    return;
                }
                if (! isFile(tbmap.getText()) && rbout.isSelected()) {
                    proceed = false;
                    bsmf.MainFrame.show("Map does not exist");
                    return;
                }
                
                if (rbin.isSelected()) {
                    dir = false;
                } else {
                    dir = true;
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT cme_code, cme_doc FROM  cmedi_mstr where cme_code = " + "'" + tbcust.getText() + "'" +
                                          " AND cme_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" + 
                                          " AND cme_dir = " + "'" + BlueSeerUtils.boolToInt(dir) + "'" + 
                                          ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into cmedi_mstr "
                            + "(cme_code, cme_doc, cme_isa, cme_isaqual, " 
                            + "cme_gs, cme_map, cme_eledelim, cme_segdelim, cme_subdelim, cme_fileprefix, cme_filesuffix, cme_filepath, "
                            + "cme_version, cme_ov_isa, cme_ov_gs, cme_ov_isaqual, cme_supplier_code, cme_dir ) "
                            + " values ( " + "'" + tbcust.getText().toString() + "'" + ","
                                + "'" + dddoc.getSelectedItem().toString() + "'" + ","
                                + "'" + ddtpid.getSelectedItem().toString() + "'" + ","
                                + "'" + tbisaq.getText().toString() + "'" + ","
                                + "'" + tbgs.getText().toString() + "'" + ","
                                + "'" + tbmap.getText().toString() + "'" + ","
                                + "'" + tbelement.getText().toString() + "'" + ","
                                + "'" + tbsegment.getText().toString() + "'" + ","
                                + "'" + tbsub.getText().toString() + "'" + ","
                                + "'" + tbfileprefix.getText().toString() + "'" + ","
                                + "'" + tbfilesuffix.getText().toString() + "'" + ","
                                + "'" + tbfilepath.getText().toString() + "'" + ","
                                + "'" + tbversion.getText().toString() + "'" + ","
                                + "'" + tbovisa.getText().toString() + "'" + ","
                                + "'" + tbovgs.getText().toString() + "'" + ","
                                + "'" + tbovisaq.getText().toString() + "'" + ","
                                + "'" + tbsupplier.getText().toString() + "'"  + ","
                                + "'" + BlueSeerUtils.boolToInt(dir) + "'"
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added EDI Cust Record");
                    } else {
                        bsmf.MainFrame.show("Cust / Doc Already Exists");
                    }

                   initvars("");
                   
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Add to edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       try {
            boolean proceed = true;
            boolean dir = false;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
                // check the code field
               if (tbcust.getText().isEmpty()) {
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
                
                 if (rbin.isSelected()) {
                    dir = false;
                } else {
                    dir = true;
                }
                
                
                if (proceed) {
                    st.executeUpdate("update cmedi_mstr set cme_isa = " + "'" + ddtpid.getSelectedItem().toString() + "'" + ","
                            + "cme_isaqual = " + "'" + tbisaq.getText() + "'" + ","
                            + "cme_gs = " + "'" + tbgs.getText() + "'" + ","
                            + "cme_map = " + "'" + tbmap.getText() + "'" + ","
                            + "cme_eledelim = " + "'" + tbelement.getText() + "'" + ","
                            + "cme_segdelim = " + "'" + tbsegment.getText() + "'" + ","
                            + "cme_subdelim = " + "'" + tbsub.getText() + "'" + ","
                            + "cme_fileprefix = " + "'" + tbfileprefix.getText() + "'" + ","
                            + "cme_filesuffix = " + "'" + tbfilesuffix.getText() + "'" + ","
                            + "cme_filepath = " + "'" + tbfilepath.getText() + "'" + ","
                            + "cme_version = " + "'" + tbversion.getText() + "'" + ","
                            + "cme_ov_isa = " + "'" + tbovisa.getText() + "'" + ","
                            + "cme_ov_gs = " + "'" + tbovgs.getText() + "'" + ","
                            + "cme_ov_isaqual = " + "'" + tbovisaq.getText() + "'" + ","
                            + "cme_supplier_code = " + "'" + tbsupplier.getText() + "'" 
                            + " where cme_code = " + "'" + tbcust.getText() + "'"     
                            + " AND cme_doc = " + "'" + dddoc.getSelectedItem().toString() + "'"
                            + " AND cme_dir = " + "'" + BlueSeerUtils.boolToInt(dir) + "'"
                            + ";");
                    bsmf.MainFrame.show("Updated Cust / doc");
                    initvars("");
                } 
         
            } catch (SQLException s) {
                bsmf.MainFrame.show("Problem updating cmedi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        boolean dir = false;
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                  if (rbin.isSelected()) {
                    dir = true;
                } else {
                    dir = false;
                }
                
                   int i = st.executeUpdate("delete from cmedi_mstr where cme_code = " + "'" + tbcust.getText() + "'" + 
                                            " and cme_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                                            " and cme_dir = " + "'" + BlueSeerUtils.boolToInt(dir) + "'" +
                                            ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbcust.getText() + "/" + dddoc.getSelectedItem().toString() + "/" + String.valueOf(BlueSeerUtils.boolToInt(dir)));
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete cmedi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "edicustmaint,cme_code");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        clearAll();
        enableAll();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
        btnew.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> dddoc;
    private javax.swing.JComboBox<String> ddtpid;
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
    private javax.swing.JLabel lblsuffix;
    private javax.swing.JRadioButton rbin;
    private javax.swing.JRadioButton rbout;
    private javax.swing.JTextField tbcust;
    private javax.swing.JTextField tbelement;
    private javax.swing.JTextField tbfilepath;
    private javax.swing.JTextField tbfileprefix;
    private javax.swing.JTextField tbfilesuffix;
    private javax.swing.JTextField tbgs;
    private javax.swing.JTextField tbisaq;
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
