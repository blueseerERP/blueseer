/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.inv;

import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author vaughnte
 */
public class BOMMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form BOMMaintPanel
     */
    
    DefaultListModel complistmodel = new DefaultListModel();
    
    
    public BOMMaintPanel() {
        initComponents();
        
        
        
    }

   
    
    public void clearAll() {
        tbqtyper.setText("");
        tbref.setText("");
        ddcomp.setSelectedIndex(0);
    }
    
    public void initvars(String arg) {
        tbpart.setText("");
        ddcomp.removeAllItems();
        ddop.removeAllItems();
        tbqtyper.setText("");
        tbref.setText("");
        lblcomp.setText("");
        lblparent.setText("");
        complistmodel.removeAllElements();
        
       
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(true);
        
        
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        Object root = model.getRoot();
    	while(!model.isLeaf(root))
    	{
    		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    	}
        jTree1.setVisible(false);
       
        complist.setModel(complistmodel);
        
        ArrayList<String> mylist = new ArrayList<String>();
              mylist = OVData.getItemMasterAlllist();
               for (int i = 0; i < mylist.size(); i++) {
                    if (! mylist.get(i).toString().toLowerCase().equals(arg.toString().toLowerCase()))   
                    ddcomp.addItem(mylist.get(i));
               }
        
         if (! arg.isEmpty()) {
           tbpart.setText(arg);
           getComponents(arg);
           getOPs(arg);
           bind_tree(arg);
       }
         
         
         
    }
    
   
    public void getOPs(String parent) {
        ddop.removeAllItems();
         ArrayList<String> ops = new ArrayList<String>();
              ops = OVData.getOperationsByPart(parent);
               for (int i = 0; i < ops.size(); i++) {
                ddop.addItem(ops.get(i));
               }
    }
    
   
    
    public void getComponents(String parent) {
        
        complistmodel.removeAllElements();         
        
            if (! parent.isEmpty()) {
              ArrayList<String> mylist = new ArrayList<String>();
              mylist = OVData.getpsmstrcomp(parent);
               for (int i = 0; i < mylist.size(); i++) {
                complistmodel.addElement(mylist.get(i));
               }
            }
        
    }
    
    public void setcomponentattributes(String component) {
       
          tbqtyper.setText("");
          tbref.setText("");
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String type = "";
           res = st.executeQuery("SELECT ps_parent, ps_child, ps_op, ps_qty_per, it_desc, it_uom,  ps_ref FROM  pbm_mstr " +
                   " inner join item_mstr on it_item = ps_child " +
                   " where ps_parent = " + "'" + tbpart.getText().toString() + "'" + 
                                          " AND ps_child = " + "'" + component + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        ddop.setSelectedItem(res.getString("ps_op"));
                        tbqtyper.setText(res.getString("ps_qty_per"));
                        tbref.setText(res.getString("ps_ref"));
                        ddcomp.setSelectedItem(res.getString("ps_child"));
                        
                        // set update button and disable add button
                        
                    }
                    if (i == 0) {
                       btadd.setEnabled(true);
                        btupdate.setEnabled(false);
                        btdelete.setEnabled(false); 
                    } else {
                      btadd.setEnabled(false);
                        btupdate.setEnabled(true);
                        btdelete.setEnabled(true);  
                    }
             } catch (SQLException s) {
                s.printStackTrace();
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
      public void bind_tree(String parentpart) {
      //  jTree1.setModel(null);
       
       // DefaultMutableTreeNode mynode = OVData.get_nodes_without_op(parentpart);
       DefaultMutableTreeNode mynode = OVData.get_op_nodes_experimental(parentpart);
      
       DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        jTree1.setVisible(true);
        // expand entire tree
        for (int i = 0; i < jTree1.getRowCount(); i++) {
        jTree1.expandRow(i);
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbqtyper = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ddcomp = new javax.swing.JComboBox();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        tbpart = new javax.swing.JTextField();
        ddop = new javax.swing.JComboBox<>();
        lblparent = new javax.swing.JLabel();
        lblcomp = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        complist = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM Maintenance"));

        jLabel3.setText("Qty Per");

        jLabel2.setText("Component");

        jLabel5.setText("Reference");

        jLabel1.setText("Parent Item");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel4.setText("Operation");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        complist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                complistMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(complist);

        jLabel6.setText("List of Current Child Elements");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbqtyper, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblcomp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblparent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbpart)
                                    .addComponent(ddcomp, 0, 158, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)))
                        .addGap(25, 25, 25))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addComponent(lblparent, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btbrowse))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddcomp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblcomp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnew))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqtyper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(51, 51, 51))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String type = "";
                
                
                if (tbqtyper.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a Quantity Per");
                    tbqtyper.requestFocus();
                    return;
                }
                
                 if (tbpart.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a Valid Parent");
                    tbpart.requestFocus();
                    return;
                }
                 
                 if (! OVData.isValidItem(tbpart.getText())) {
                tbpart.requestFocus();
                proceed = false;
                return;
                }
                
                 if (ddcomp.getSelectedItem() == null) {
                   proceed = false;
                   bsmf.MainFrame.show("Must enter a legitimate component");
                   ddcomp.requestFocus();
                   return;
               } 
                 
                if (! OVData.isValidItem(ddcomp.getSelectedItem().toString())) {
                ddcomp.requestFocus();
                bsmf.MainFrame.show("Must enter a legitimate component");
                proceed = false;
                return;
                }
                
               
               if (ddop.getSelectedItem() == null) {
                   proceed = false;
                   bsmf.MainFrame.show("Must enter a legitimate operation");
                   ddop.requestFocus();
                   return;
               } 
               
               if (ddcomp.getSelectedItem().toString().toLowerCase().equals(tbpart.getText().toLowerCase())) {
                   proceed = false;
                   bsmf.MainFrame.show("Cannot map parent to parent");
                   ddcomp.requestFocus();
                   return;
               } 
                
                // Check for component item and type value in item master
                res = st.executeQuery("SELECT it_code FROM  item_mstr where it_item = " + "'" + ddcomp.getSelectedItem().toString() + "';" );
                while (res.next()) {
                    i++;
                    type = res.getString("it_code");
                }
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show("Component item is not defined in item master");
                    ddcomp.requestFocus();
                    return;
                }
                if (type.isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Component item class is not defined in item master");
                    ddcomp.requestFocus();
                    return;
                }
                
                
                
                
                if (proceed) {

                    res = st.executeQuery("SELECT ps_child FROM  pbm_mstr where ps_parent = " + "'" + tbpart.getText().toString() + "'" + 
                                          " AND ps_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" +
                                          " AND ps_op = " + "'" + ddop.getSelectedItem().toString() + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into pbm_mstr "
                            + "(ps_parent, ps_child, ps_qty_per, ps_op, ps_type) "
                            + " values ( " + "'" + tbpart.getText() + "'" + ","
                            + "'" + ddcomp.getSelectedItem().toString() + "'" + ","
                            + "'" + tbqtyper.getText() + "'" + "," 
                            + "'" + ddop.getSelectedItem().toString() + "'" + ","
                            + "'" + type + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added BOM Record");
                        bind_tree(tbpart.getText());
                        getComponents(tbpart.getText());
                    } else {
                        bsmf.MainFrame.show("BOM Record Already Exists");
                    }

                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to Add to BOM Master Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from pbm_mstr where ps_parent = " + "'" + tbpart.getText().toString() + "'" + 
                            " AND ps_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted BOM record");
                    bind_tree(tbpart.getText());
                    getComponents(tbpart.getText());
                    
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete BOM Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {
            boolean proceed = true;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
                if (tbqtyper.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a Quantity Per");
                    tbqtyper.requestFocus();
                    return;
                }
                
                if (ddcomp.getSelectedItem() == null) {
                   proceed = false;
                   bsmf.MainFrame.show("Must enter a legitimate component");
                   ddcomp.requestFocus();
                   return;
               } 
                
                if (ddcomp.getSelectedItem().toString().toLowerCase().equals(tbpart.getText().toLowerCase())) {
                   proceed = false;
                   bsmf.MainFrame.show("Cannot map parent to parent");
                   ddcomp.requestFocus();
                   return;
               } 
                
                
                
                if (proceed) {
                    st.executeUpdate("update pbm_mstr set ps_qty_per = " + "'" + tbqtyper.getText() + "'" + ","
                            + " ps_op = " + "'" + ddop.getSelectedItem().toString() + "'" 
                            + " where ps_parent = " + "'" + tbpart.getText().toString() + "'" + 
                                          " AND ps_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                    bsmf.MainFrame.show("Updated BOM Record");
                    bind_tree(tbpart.getText());
                    getComponents(tbpart.getText());
                } 
         
            } catch (SQLException s) {
                bsmf.MainFrame.show("Problem updating pbm_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
        if (OVData.isValidItem(tbpart.getText())) {
            getComponents(tbpart.getText());
            getOPs(tbpart.getText());
            bind_tree(tbpart.getText());
            lblparent.setText(OVData.getItemDesc(tbpart.getText()));
            } else {
                lblparent.setText("Invalid Parent Part");
                tbpart.requestFocus();
            }
    }//GEN-LAST:event_tbpartFocusLost

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
     //   bsmf.MainFrame.show(jTree1.getLastSelectedPathComponent().toString());
      //  bsmf.MainFrame.show(jTree1.getAnchorSelectionPath().toString());
       // bsmf.MainFrame.show(jTree1.getLeadSelectionPath().toString());
     /*
       DefaultMutableTreeNode node = null;
       DefaultMutableTreeNode parent = null;
         Object o = jTree1.getLastSelectedPathComponent();
         
        if (o != null ) {
        node = (DefaultMutableTreeNode)o;
            if (! node.isRoot()) {
                if (node != null) {
                parent = (DefaultMutableTreeNode)node.getParent();
                }
               // bsmf.MainFrame.show(String.valueOf(parent.getLevel()));
                if (parent.getLevel() == 1 && parent != null) {
                setcomponentattributes(node.toString());
                }
            }
         }
       */
    }//GEN-LAST:event_jTree1ValueChanged

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "bommaint,it_item");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void complistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_complistMouseClicked
       if (! complist.isSelectionEmpty()) {
        setcomponentattributes(complist.getSelectedValue().toString());
       }
    }//GEN-LAST:event_complistMouseClicked

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        btadd.setEnabled(true);
        clearAll();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JList<String> complist;
    private javax.swing.JComboBox ddcomp;
    private javax.swing.JComboBox<String> ddop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblcomp;
    private javax.swing.JLabel lblparent;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqtyper;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
