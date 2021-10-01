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

package com.blueseer.adm;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author vaughnte
 */
public class MenuTree extends javax.swing.JPanel {

    /**
     * Creates new form BOMMaintPanel
     */
    public MenuTree() {
        initComponents();
        setLanguageTags(this);
    }

    
       private static class MenuInfo {
        public String menuname;
        public String menuvisible;
 
        public MenuInfo(String name, String visible) {
            menuname = name;
            menuvisible = visible;
           
        }
 
        public String toString() {
            return menuname;
        }
    }
    
    
    public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        
         DefaultMutableTreeNode node = (DefaultMutableTreeNode ) value;
    OVData.MenuInfo myobj = (OVData.MenuInfo) node.getUserObject();
      
        // If the node is a leaf and ends with "xxx"
        if (myobj.menuvisible.compareTo("0") == 0) {
            // Paint the node in blue
            setForeground(new Color(13, 57 ,115));
        }
       
        return this;
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
    
    public void initvars(String[] arg) {
        tbpar.setText("");
        ddcomp.removeAllItems();
        tbinitvar.setText("");
        tbicon.setText("");
        tbfunc.setText("");
        tbindex.setText("");
        tbindex.setEnabled(true);
        tbname.setText("");
    
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        jTree1.removeAll();
   //     jTree1.setCellRenderer(new MyTreeCellRenderer());
       // Object root = model.getRoot();
    //	while(!model.isLeaf(root))
    //	{
    //		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    //	}
        jTree1.setVisible(false);
       
        bind_tree("root");
        
    }
    
   
    
    
    public void setcomponentlist() {
        
        ddcomp.removeAllItems();
         
        
        if (! tbpar.getText().toString().isEmpty()) {
          ArrayList<String> mylist = new ArrayList<String>();
          mylist = OVData.getmenutree(tbpar.getText().toString());
          for (String myrecs : mylist) {
              String[] recs = myrecs.split(",",-1);
              ddcomp.addItem(recs[0]);
          } 
     }
    }
    
    public void setcomponentattributes(String component) {
         tbinitvar.setText("");
          tbicon.setText("");
          tbname.setText("");
          tbfunc.setText("");
          tbindex.setText("");
          tbname.setText("");
          cbvisible.setSelected(false);
          cbenable.setSelected(false);
      
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String type = "";
           res = st.executeQuery("SELECT * FROM  menu_tree " +
                   " where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                                          " AND mt_child = " + "'" + component + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        tbinitvar.setText(res.getString("mt_initvar"));
                        tbicon.setText(res.getString("mt_icon"));
                        tbname.setText(res.getString("mt_label"));
                        tbfunc.setText(res.getString("mt_func"));
                        tbindex.setText(res.getString("mt_index"));
                        cbvisible.setSelected(res.getBoolean("mt_visible"));
                        cbenable.setSelected(res.getBoolean("mt_enable"));
                        tbindex.setText(res.getString("mt_index"));
                        ddtype.setSelectedItem(res.getString("mt_type"));
                     
                        
                        // set update button and disable add button
                        
                    }
                    if (i == 0) {
                       btadd.setEnabled(true);
                       tbindex.setText("-1");
                       tbindex.setEnabled(false);
                        btupdate.setEnabled(false);
                        btdelete.setEnabled(false); 
                    } else {
                       tbindex.setEnabled(true);
                      btadd.setEnabled(false);
                        btupdate.setEnabled(true);
                        btdelete.setEnabled(true);  
                    }
             } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        
    }
    
      public void bind_tree(String parentmenu) {
      //  jTree1.setModel(null);
       
        DefaultMutableTreeNode mynode = OVData.getMenusAsTree(parentmenu, "1");
       
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
         jTree1.setCellRenderer(new MyTreeCellRenderer());
        jTree1.setVisible(true);
        
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
        jLabel6 = new javax.swing.JLabel();
        tbfunc = new javax.swing.JTextField();
        tbname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbicon = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ddcomp = new javax.swing.JComboBox();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        btGetTree = new javax.swing.JButton();
        tbpar = new javax.swing.JTextField();
        tbinitvar = new javax.swing.JTextField();
        ddtype = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        tbindex = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbvisible = new javax.swing.JCheckBox();
        cbenable = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Menu Tree Maintenance"));
        jPanel2.setName("panelmain"); // NOI18N

        jLabel3.setText("Icon");
        jLabel3.setName("lblicon"); // NOI18N

        jLabel6.setText("Name");
        jLabel6.setName("lblname"); // NOI18N

        jLabel2.setText("Component");
        jLabel2.setName("lblcomponent"); // NOI18N

        jLabel5.setText("Function");
        jLabel5.setName("lblfunc"); // NOI18N

        jLabel1.setText("Parent Item");
        jLabel1.setName("lblparent"); // NOI18N

        ddcomp.setEditable(true);
        ddcomp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcompItemStateChanged(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel4.setText("InitVar");
        jLabel4.setName("lblinitvar"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btGetTree.setText("Tree");
        btGetTree.setName("btview"); // NOI18N
        btGetTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGetTreeActionPerformed(evt);
            }
        });

        tbpar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbparFocusLost(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "JMenu", "JMenuItem" }));

        jLabel8.setText("Type");
        jLabel8.setName("lbltype"); // NOI18N

        jLabel7.setText("Index");
        jLabel7.setName("lblindex"); // NOI18N

        cbvisible.setText("Visible?");
        cbvisible.setName("cbvisible"); // NOI18N

        cbenable.setText("Enabled?");
        cbenable.setName("cbenabled"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addGap(115, 115, 115))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbenable)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbvisible)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbfunc)
                                    .addComponent(tbindex, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbname)
                                    .addComponent(tbpar)
                                    .addComponent(tbinitvar)
                                    .addComponent(tbicon)
                                    .addComponent(ddcomp, 0, 186, Short.MAX_VALUE)
                                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btGetTree)
                                .addGap(24, 24, 24))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btGetTree)
                    .addComponent(tbpar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcomp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbicon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tbinitvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbfunc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbvisible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbenable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btGetTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGetTreeActionPerformed
         bind_tree("root");
    }//GEN-LAST:event_btGetTreeActionPerformed

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
                
                
                
                if (ddtype.getSelectedItem().toString().compareTo("JMenuItem") == 0) {
                // Check for menu item parent and child ...only for JMenuItem types
                res = st.executeQuery("SELECT menu_id FROM  menu_mstr where menu_id = " + "'" + ddcomp.getSelectedItem().toString() + "';" );
                while (res.next()) {
                    i++;
                }
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1116));
                    ddcomp.requestFocus();
                    return;
                }
                // now parent
                res = st.executeQuery("SELECT menu_id FROM  menu_mstr where menu_id = " + "'" + tbpar.getText() + "';" );
                while (res.next()) {
                    i++;
                }
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1116));
                    tbpar.requestFocus();
                    return;
                }
                
                }
                
                if (proceed) {

                    // we will add new leaf at end of index....get total count of children
                    res = st.executeQuery("SELECT mt_child FROM  menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" + ";");
                    int count = 0;
                    while (res.next()) {
                        count++;
                    }
                    
                    // add one to the count
                    count++;
                    
                    
                    res = st.executeQuery("SELECT mt_child FROM  menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                                          " AND mt_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into menu_tree "
                            + "(mt_par, mt_child, mt_type, mt_label, mt_index, mt_visible, mt_enable, mt_icon, mt_initvar, mt_func ) "
                            + " values ( " + "'" + tbpar.getText() + "'" + ","
                            + "'" + ddcomp.getSelectedItem().toString() + "'" + ","
                                + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                                + "'" + tbname.getText() + "'" + "," 
                                + "'" + count + "'" + ","
                                + "'" + BlueSeerUtils.boolToInt(cbvisible.isSelected()) + "'" + ","
                                + "'" + BlueSeerUtils.boolToInt(cbenable.isSelected()) + "'" + ","
                            + "'" + tbicon.getText() + "'" + "," 
                            + "'" + tbinitvar.getText() + "'" + ","
                            + "'" + tbfunc.getText() + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show(getMessageTag(1007));
                    } else {
                        bsmf.MainFrame.show(getMessageTag(1014));
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
       int index = 0;
       int count = 0;
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
                // lets get the index to use for reindexing the remaining menus within this parent
                res = st.executeQuery("select mt_index from menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                            " AND mt_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
               while (res.next()) {
                   index = res.getInt("mt_index");
               }
               
               // get total count
                res = st.executeQuery("SELECT mt_child FROM  menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" + ";");
                    while (res.next()) {
                        count++;
                    }
                    
               // get remaining children
               ArrayList<String> mylist = new ArrayList<String>();
                res = st.executeQuery("SELECT mt_child FROM  menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" +
                        " AND mt_index > " + "'" + index + "'" + " order by mt_index ;");
                    while (res.next()) {
                        mylist.add(res.getString("mt_child"));
                    }     
                    
               
                 int k = index;
                 for (String mymenu : mylist) {  
                  st.executeUpdate("update menu_tree set mt_index = " + "'" + k + "'" + " where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                                   " AND mt_child = " + "'" + mymenu + "'" + ";");
                  k++;
                 }
                 
                 
                 
                   int i = st.executeUpdate("delete from menu_tree where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                            " AND mt_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                   
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1009));
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1013));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {
            boolean proceed = true;
            int oldindex = 0;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                  ResultSet res = null;   
                if (tbindex.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbindex.requestFocus();
                    return;
                }
              
                if (proceed) {
              
                    
                    // lets get old index
                    res = st.executeQuery("SELECT mt_index FROM  menu_tree " +
                   " where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                                          " AND mt_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                     while (res.next()) {
                        oldindex = res.getInt("mt_index");
                    }
                    
                    
                    st.executeUpdate("update menu_tree set " 
                            + " mt_initvar = " + "'" + tbinitvar.getText() + "'" + "," 
                            + " mt_func = " + "'" + tbfunc.getText() + "'" + ","
                            + " mt_icon = " + "'" + tbicon.getText() + "'" + ","
                            + " mt_visible = " + "'" + BlueSeerUtils.boolToInt(cbvisible.isSelected()) + "'" + ","
                            + " mt_enable = " + "'" + BlueSeerUtils.boolToInt(cbenable.isSelected()) + "'" + ","
                            + " mt_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                            + " mt_label = " + "'" + tbname.getText() + "'"
                            + " where mt_par = " + "'" + tbpar.getText().toString() + "'" + 
                                          " AND mt_child = " + "'" + ddcomp.getSelectedItem().toString() + "'" + ";");
                    // if child menu is marked not visible...then disable all children down the tree
                    if (cbvisible.isSelected()) {
                        OVData.enablemenutree(ddcomp.getSelectedItem().toString());
                    } else {
                        OVData.disablemenutree(ddcomp.getSelectedItem().toString());
                    }
                    
                   // lets reindex the children of this parent
               OVData.indexMenuChildren(tbpar.getText().toString(), ddcomp.getSelectedItem().toString(), Integer.valueOf(tbindex.getText()), oldindex);
                   
                    bsmf.MainFrame.show(getMessageTag(1008));
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1012));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbparFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbparFocusLost
        setcomponentlist();
    }//GEN-LAST:event_tbparFocusLost

    private void ddcompItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcompItemStateChanged
       if (ddcomp.getSelectedItem() != null && ! ddcomp.getSelectedItem().toString().isEmpty()) {  
                   setcomponentattributes(ddcomp.getSelectedItem().toString());
       }
    }//GEN-LAST:event_ddcompItemStateChanged

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
     //   bsmf.MainFrame.show(jTree1.getLastSelectedPathComponent().toString());
      //  bsmf.MainFrame.show(jTree1.getAnchorSelectionPath().toString());
       // bsmf.MainFrame.show(jTree1.getLeadSelectionPath().toString());
              
       Object o = jTree1.getLastSelectedPathComponent();
       if (o != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        // bsmf.MainFrame.show(String.valueOf(parent.getLevel()));
        tbpar.setText(parent.toString());
        ddcomp.setSelectedItem(node.toString());
       }
       
    }//GEN-LAST:event_jTree1ValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btGetTree;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbenable;
    private javax.swing.JCheckBox cbvisible;
    private javax.swing.JComboBox ddcomp;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JTextField tbfunc;
    private javax.swing.JTextField tbicon;
    private javax.swing.JTextField tbindex;
    private javax.swing.JTextField tbinitvar;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbpar;
    // End of variables declaration//GEN-END:variables
}
