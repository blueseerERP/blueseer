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

package com.blueseer.inv;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
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
    
    // global variable declarations
                boolean isLoad = false;
                
    
    public BOMMaintPanel() {
        initComponents();
        
        
        
    }

    public void disableAll() {
        tbqtyper.setEnabled(false);
        tbref.setEnabled(false);
        ddcomp.setEnabled(false);
        ddop.setEnabled(false);
        jTree1.setEnabled(false);
        btdelete.setEnabled(false);
        btupdate.setEnabled(false);
        btadd.setEnabled(false);
        btclear.setEnabled(false);
        btpdf.setEnabled(false);
    }
    
    public void enableAll() {
        tbqtyper.setEnabled(true);
        tbref.setEnabled(true);
        ddcomp.setEnabled(true);
        ddop.setEnabled(true);
        jTree1.setEnabled(true);
        btdelete.setEnabled(true);
        btupdate.setEnabled(true);
        btadd.setEnabled(true);
        btclear.setEnabled(true);
        btpdf.setEnabled(true);
    }
    
    public void clearAll() {
       
       isLoad = true; 
       tbpart.setText("");
       tbpart.setEditable(true);
       tbpart.setForeground(Color.black);    
        ddcomp.removeAllItems();
        ddop.removeAllItems();
        tbqtyper.setText("");
        tbref.setText("");
        lblcomp.setText("");
        lblparent.setText("");
       
        
          DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        Object root = model.getRoot();
    	while(!model.isLeaf(root))
    	{
    		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    	}
        jTree1.setVisible(false);
       
       
        
          ArrayList<String> mylist = new ArrayList<String>();
              mylist = OVData.getItemMasterAlllist();
               for (int i = 0; i < mylist.size(); i++) {
                    ddcomp.addItem(mylist.get(i));
               }
        
       isLoad = false; 
    }
    
    public void initvars(String[] arg) {
        
        
       clearAll();
       disableAll();
    
       btbrowse.setEnabled(true);
       tbpart.setEnabled(true);
       
        
         
         if (arg != null && arg.length > 0) {
           tbpart.setText(arg[0]);
           establishParent(arg[0]);
         }  
    }
    
    public void establishParent(String item) {
          boolean validItem =  OVData.isValidItem(item);
            boolean hasRouting =  getRouting(item);
             if (validItem && hasRouting) {
              tbpart.setEditable(false);
              tbpart.setForeground(Color.blue);
              if (ddcomp.getSelectedItem() != null && ! isLoad)
              lblparent.setText(OVData.getItemDesc(ddcomp.getSelectedItem().toString()));
                  getComponents(item);
                  getOPs(item);
                  bind_tree(item);
                  ddcomp.removeItem(tbpart.getText());  // remove parent from component list
                  enableAll();
                  
             } else if (validItem && ! hasRouting) {
               tbpart.setEditable(true);
               tbpart.setForeground(Color.red);  
               lblparent.setText("Item has no Routing");
                disableAll();
                
             } else {
               tbpart.setEditable(true);
               tbpart.setForeground(Color.black);    
               lblparent.setText("Invalid Item");
                disableAll();
                
             }
    }
    
    public boolean getRouting(String item) {
        boolean hasRouting = false;
        String routing = "";
        routing = OVData.getItemRouting(item);
        hasRouting = OVData.isValidRouting(routing);
        return hasRouting;
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
        
         
        
            if (! parent.isEmpty()) {
              ArrayList<String> mylist = new ArrayList<String>();
              mylist = OVData.getpsmstrcomp(parent);
               for (int i = 0; i < mylist.size(); i++) {
                //complistmodel.addElement(mylist.get(i));
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
                        
                        // set update button 
                        
                    }
                    if (i == 0) {
                        btadd.setEnabled(true);
                        btupdate.setEnabled(false);
                        btdelete.setEnabled(false); 
                        btpdf.setEnabled(false);
                    } else {
                        btadd.setEnabled(true);
                        btupdate.setEnabled(true);
                        btdelete.setEnabled(true); 
                        btpdf.setEnabled(true);
                    }
             } catch (SQLException s) {
                 bsmf.MainFrame.show("Unable to select pbm_mstr");
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
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

        jButton1 = new javax.swing.JButton();
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
        btclear = new javax.swing.JButton();
        btpdf = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbcompcost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbcomptype = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tblotsize = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tbpps = new javax.swing.JTextField();
        tbpph = new javax.swing.JTextField();
        tbppssim = new javax.swing.JTextField();
        tbpphsim = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        tbsetupRate = new javax.swing.JTextField();
        tbrunratesim = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsetupRateSim = new javax.swing.JTextField();
        tbsetupsize = new javax.swing.JTextField();
        tbrunrate = new javax.swing.JTextField();
        tbsetupsizesim = new javax.swing.JTextField();
        tbcrewsizesim = new javax.swing.JTextField();
        tbburdenratesim = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbburdenrate = new javax.swing.JTextField();
        tbcrewsize = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbparentcost = new javax.swing.JTextField();
        tbparentcostsim = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tbparentcost1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM Maintenance"));

        jLabel3.setText("Qty Per");

        jLabel2.setText("Component");

        jLabel5.setText("Reference");

        jLabel1.setText("Parent Item");

        ddcomp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcompActionPerformed(evt);
            }
        });

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
        tbpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbpartActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btclear.setText("clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btpdf.setText("PDF");
        btpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpdfActionPerformed(evt);
            }
        });

        jLabel7.setText("Parent Desc");

        jLabel8.setText("Comp Desc");

        jLabel6.setText("Comp Cost:");

        jLabel9.setText("Comp Type:");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Operational Routing"));

        jLabel13.setText("pcs/HR");

        jLabel17.setText("Setup");

        jLabel18.setText("LotSize");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tblotsize, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbpph, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbpps, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbpphsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbppssim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpphsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbppssim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Operational WorkCell/Dept"));

        jLabel12.setText("BurdenRate");

        jLabel10.setText("RunRate");

        jLabel11.setText("SetupRate");

        jLabel14.setText("Simulation");

        jLabel15.setText("Production");

        jLabel19.setText("CrewSize");

        jLabel20.setText("SetupSize");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbsetupRate, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbsetupRateSim, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbburdenrate, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbburdenratesim, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbcrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbcrewsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbsetupsize, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbsetupsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbrunrate, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(tbrunratesim, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrunrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tbrunratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsetupRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsetupRateSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbburdenrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbburdenratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcrewsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsetupsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsetupsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap())
        );

        jLabel16.setText("Current Cost:");

        jLabel21.setText("Standard Cost:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel7))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel8)))
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbpart)
                                    .addComponent(ddcomp, 0, 205, Short.MAX_VALUE)
                                    .addComponent(tbqtyper, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(lblcomp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(lblparent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btclear)))
                                    .addComponent(tbcompcost, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcomptype, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(29, 29, 29))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btpdf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(64, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbparentcost1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbparentcost, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(tbparentcostsim, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(78, 78, 78))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btbrowse))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblparent, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)))
                            .addComponent(btclear))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblcomp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcomp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcompcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcomptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadd)
                            .addComponent(btdelete)
                            .addComponent(btupdate)
                            .addComponent(btpdf)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbparentcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16))
                    .addComponent(tbparentcostsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbparentcost1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
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
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to BOM Master Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
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
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete BOM Record");
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
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating pbm_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
      /*
        if (OVData.isValidItem(tbpart.getText())) {
            getComponents(tbpart.getText());
            getOPs(tbpart.getText());
            bind_tree(tbpart.getText());
            lblparent.setText(OVData.getItemDesc(tbpart.getText()));
            } else {
                lblparent.setText("Invalid Parent Part");
                tbpart.requestFocus();
            }
        */
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
        reinitpanels("BrowseUtil", true, new String[]{"bommaint","it_item"});
    }//GEN-LAST:event_btbrowseActionPerformed

    private void tbpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbpartActionPerformed
        establishParent(tbpart.getText());
    }//GEN-LAST:event_tbpartActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpdfActionPerformed
       if (! tbpart.getText().isEmpty())
        OVData.printBOMJasper(tbpart.getText());
    }//GEN-LAST:event_btpdfActionPerformed

    private void ddcompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcompActionPerformed
         if (ddcomp.getSelectedItem() != null && ! isLoad)
            lblcomp.setText(OVData.getItemDesc(ddcomp.getSelectedItem().toString()));
    }//GEN-LAST:event_ddcompActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btpdf;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddcomp;
    private javax.swing.JComboBox<String> ddop;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblcomp;
    private javax.swing.JLabel lblparent;
    private javax.swing.JTextField tbburdenrate;
    private javax.swing.JTextField tbburdenratesim;
    private javax.swing.JTextField tbcompcost;
    private javax.swing.JTextField tbcomptype;
    private javax.swing.JTextField tbcrewsize;
    private javax.swing.JTextField tbcrewsizesim;
    private javax.swing.JTextField tblotsize;
    private javax.swing.JTextField tbparentcost;
    private javax.swing.JTextField tbparentcost1;
    private javax.swing.JTextField tbparentcostsim;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbpph;
    private javax.swing.JTextField tbpphsim;
    private javax.swing.JTextField tbpps;
    private javax.swing.JTextField tbppssim;
    private javax.swing.JTextField tbqtyper;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrunrate;
    private javax.swing.JTextField tbrunratesim;
    private javax.swing.JTextField tbsetupRate;
    private javax.swing.JTextField tbsetupRateSim;
    private javax.swing.JTextField tbsetupsize;
    private javax.swing.JTextField tbsetupsizesim;
    // End of variables declaration//GEN-END:variables
}
