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
import com.blueseer.utl.BlueSeerUtils;
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
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

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
                
     javax.swing.table.DefaultTableModel matlmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"Comp", "Type", "Op", "QtyPer", "Cost"});
      
      javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 3 || tme.getColumn() == 4 )) {
                            callSimulateCost();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };    
     
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
        
        
        tbrunrate.setEditable(false);
        tbsetuprate.setEditable(false);
        tbburdenrate.setEditable(false);
        tbcrewsize.setEditable(false);
        tbsetupsize.setEditable(false);
        tbpph.setEditable(false);
        tbpps.setEditable(false);
       
        matlmodel.setRowCount(0);
        matlmodel.addTableModelListener(ml);
        
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
        DecimalFormat df5 = new DecimalFormat("#.00000"); 
          boolean validItem =  OVData.isValidItem(item);
            boolean hasRouting =  getRouting(item);
             if (validItem && hasRouting) {
              tbpart.setEditable(false);
              tbpart.setForeground(Color.blue);
              lblparent.setText(OVData.getItemDesc(item));
              tblotsize.setText(OVData.getItemLotSize(item));
              tbparentcostSTD.setText(String.valueOf(df5.format(OVData.getItemCost(item, "STANDARD", OVData.getDefaultSite()))));
              getComponents(item);
                  getOPs(item);
                  bind_tree(item);
                  getCurrentCost(item);
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
    
    public void getCurrentCost(String parent) {
                calcCost cur = new calcCost();
        DecimalFormat df = new DecimalFormat("#.00000"); 
        ArrayList<Double> costlist = new ArrayList<Double>();
        costlist = cur.getTotalCost(parent);
     
     tbparentcostCUR.setText(df.format(costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4)));
       
    }
    
    public void callSimulateCost() {
        DecimalFormat df = new DecimalFormat("#.00000"); 
        double pph = 0.00;
        double pps = 0.00;
        if (Double.valueOf(tbpphsim.getText()) != 0) {
            pph = 1 / Double.valueOf(tbpphsim.getText());
        }
        if (Double.valueOf(tbppssim.getText()) != 0) {
            pps = 1 / Double.valueOf(tbppssim.getText());
        }
        Object o = jTree1.getLastSelectedPathComponent();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
      //  bsmf.MainFrame.show(pph + "/" + pps);
        double totalcost = 0.00;
        totalcost = OVData.simulateCost("", 
                tbpart.getText(), 
                node.toString(), 
                Double.valueOf(tbrunratesim.getText()), 
                Double.valueOf(tbsetupratesim.getText()),
                Double.valueOf(tbburdenratesim.getText()),
                pph,
                pps,
                Double.valueOf(tbcrewsizesim.getText()),
                Double.valueOf(tbsetupsizesim.getText()),
                Double.valueOf(tblotsize.getText()),
                false
        );
        
        // now add material
        double matl = 0.00;
        for (int j = 0; j < matltable.getRowCount(); j++) {
             matl = matl + (Double.valueOf(matltable.getValueAt(j, 3).toString()) * Double.valueOf(matltable.getValueAt(j, 4).toString()) ); 
         }
        tbtotoperationalsim.setText(String.valueOf(df.format(totalcost)));
        totalcost += matl;
        tbtotmaterialsim.setText(String.valueOf(df.format(matl)));
        tbparentcostsim.setText(String.valueOf(df.format(totalcost)));
                
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
    
   public static Integer level(TreeNode tree, TreeNode target){
    return level(tree, target, 0);
}

private static Integer level(TreeNode tree, TreeNode target, int currentLevel) {
    Integer returnLevel = -1;        
    if(tree.toString().equals(target.toString())) {
        returnLevel = currentLevel;
    } else {
         Enumeration enumeration = tree.children();
        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            if((returnLevel = level((TreeNode)object, target, currentLevel + 1)) != -1){
                break;
            }                
        }
    }
    return returnLevel;

}
    
public void getComponents(String parent) {
        
         
       String site = OVData.getItemSite(parent);
       DecimalFormat df = new DecimalFormat("#.00000"); 
       double matlcost = 0.00;
       tbtotmaterial.setText(String.valueOf(matlcost));
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

                
                matlmodel.setRowCount(0);
                matltable.setModel(matlmodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT ps_child, ps_qty_per, ps_type, ps_op, itc_total " +
                        " FROM  pbm_mstr  " +
                        " left outer join item_cost on itc_item = ps_child and itc_set = 'standard' and itc_site = " + "'" + site + "'" +
                        " where ps_parent = " + "'" + parent + "'" + 
                        " order by ps_child ;");

                while (res.next()) {
                    i++;
                    matlcost += res.getDouble("ps_qty_per") * res.getDouble("itc_total");
                    matlmodel.addRow(new Object[]{
                                res.getString("ps_child"),
                                res.getString("ps_type"),
                                res.getString("ps_op"),
                                res.getDouble("ps_qty_per"),
                                res.getDouble("itc_total")
                            });
              
                }
                res.close();
               tbtotmaterial.setText(String.valueOf(df.format(matlcost)));
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to get pbm_mstr info");
            }
            
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
public void getComponentDetail(String component) {
        DecimalFormat df5 = new DecimalFormat("#.00000"); 
        tbcomptype.setText("");
        lblcomp.setText("");
        tbcompcost.setText("");
         
         try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String type = "";
                
           res = st.executeQuery("SELECT it_item, it_desc, it_code, itc_total from item_mstr left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                   " where it_item = " + "'" + 
                            ddcomp.getSelectedItem().toString() + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        tbcomptype.setText(res.getString("it_code"));
                        lblcomp.setText(res.getString("it_desc"));
                        tbcompcost.setText(String.valueOf(df5.format(res.getDouble("itc_total"))));
                        
                    }
                   
             } catch (SQLException s) {
                 bsmf.MainFrame.show("Unable to select component detail");
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        
    }
   
    
public void setcomponentattributes(String parent, String component, String op) {
       DecimalFormat df5 = new DecimalFormat("#.00000");
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
                   " where ps_parent = " + "'" + parent + "'" + 
                   " AND ps_child = " + "'" + component + "'" + 
                   " AND ps_op = " + "'" + op + "'" +
                   ";");
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
                        btpdf.setEnabled(false);
                    } else {
                        btadd.setEnabled(true);
                        btpdf.setEnabled(true);
                    }
                    
              res = st.executeQuery("SELECT it_item, it_desc, it_code, itc_total from item_mstr left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                   " where it_item = " + "'" + 
                            component + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        tbcomptype.setText(res.getString("it_code"));
                        lblcomp.setText(res.getString("it_desc"));
                        tbcompcost.setText(String.valueOf(df5.format(res.getDouble("itc_total"))));
                        
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
        jPanel5 = new javax.swing.JPanel();
        tbsetuprate = new javax.swing.JTextField();
        tbrunratesim = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsetupratesim = new javax.swing.JTextField();
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
        tbpph = new javax.swing.JTextField();
        tbpps = new javax.swing.JTextField();
        tblotsize = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tbpphsim = new javax.swing.JTextField();
        tbppssim = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        matltable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        tbparentcostSTD = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbtotoperational = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        tbtotoperationalsim = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        tbparentcostsim = new javax.swing.JTextField();
        tbparentcostCUR = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        tbtotmaterial = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tbtotmaterialsim = new javax.swing.JTextField();
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

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Operational WorkCell/Dept"));

        tbrunratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrunratesimFocusLost(evt);
            }
        });

        jLabel12.setText("BurdenRate");

        tbsetupratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetupratesimFocusLost(evt);
            }
        });

        tbsetupsizesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetupsizesimFocusLost(evt);
            }
        });

        tbcrewsizesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcrewsizesimFocusLost(evt);
            }
        });

        tbburdenratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbburdenratesimFocusLost(evt);
            }
        });

        jLabel10.setText("RunRate");

        jLabel11.setText("SetupRate");

        jLabel14.setText("Simulation");

        jLabel15.setText("Production");

        jLabel19.setText("CrewSize");

        jLabel20.setText("SetupSize");

        jLabel13.setText("pcs/HR");

        jLabel17.setText("Setup");

        jLabel18.setText("LotSize");

        tbpphsim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpphsimFocusLost(evt);
            }
        });

        tbppssim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbppssimFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel15)
                .addGap(118, 118, 118))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel13)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbcrewsize, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(tbburdenrate)
                    .addComponent(tbrunrate)
                    .addComponent(tbsetuprate)
                    .addComponent(tbsetupsize, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbpph)
                    .addComponent(tbpps)
                    .addComponent(tblotsize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbppssim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbpphsim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsetupsizesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbcrewsizesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbburdenratesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbrunratesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsetupratesim, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())))
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
                    .addComponent(tbsetuprate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsetupratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(tbpphsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(tbppssim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        matltable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(matltable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
        );

        jLabel22.setText("Sim Cost:");

        jLabel23.setText("Material:");

        jLabel25.setText("Sim Mat:");

        jLabel16.setText("Current Cost:");

        jLabel24.setText("Operational:");

        jLabel26.setText("Sim Oper:");

        jLabel21.setText("Standard Cost:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbparentcostCUR)
                            .addComponent(tbparentcostSTD, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbtotmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtotoperational, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbparentcostsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtotmaterialsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtotoperationalsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23)
                        .addComponent(tbtotmaterialsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotoperational, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(tbtotoperationalsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbparentcostCUR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel22)
                    .addComponent(tbparentcostsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbparentcostSTD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap())
        );

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
                                    .addComponent(ddcomp, 0, 225, Short.MAX_VALUE)
                                    .addComponent(tbqtyper, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
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
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btpdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addGap(45, 45, 45))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
                            .addComponent(jLabel9)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(btpdf))
                .addContainerGap())
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
            .addComponent(jScrollPane1)
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
            
      Object o = jTree1.getLastSelectedPathComponent();
      if (o != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 2)) {
      DefaultMutableTreeNode comp = (DefaultMutableTreeNode)o;
            
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from pbm_mstr where ps_parent = " + "'" + tbpart.getText().toString() + "'" + 
                            " AND ps_child = " + "'" + comp.toString() + "'" + ";");
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
     //  bsmf.MainFrame.show("select:" + jTree1.getLastSelectedPathComponent().toString());
     //   bsmf.MainFrame.show("root:" + jTree1.getModel().getRoot());
     //   bsmf.MainFrame.show("isLeaf:" + jTree1.getModel().isLeaf(jTree1.getLastSelectedPathComponent()));
         
     //   Integer level = level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)jTree1.getLastSelectedPathComponent());
     //   bsmf.MainFrame.show("Level:" + level);
     
     DecimalFormat df = new DecimalFormat("#"); 
     DecimalFormat df2 = new DecimalFormat("#.00"); 
     
            tbrunrate.setText("");
            tbsetuprate.setText("");
            tbburdenrate.setText("");
            tbcrewsize.setText("");
            tbsetupsize.setText("");
            tbpph.setText("");
            tbpps.setText("");
            
            tbrunratesim.setText("");
            tbsetupratesim.setText("");
            tbburdenratesim.setText("");
            tbcrewsizesim.setText("");
            tbsetupsizesim.setText("");
            tbpphsim.setText("");
            tbppssim.setText("");
            
             
            
       Object o = jTree1.getLastSelectedPathComponent();
       if (o != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        
        // let's lookup Operation elements if parent operation is clicked on
        if (parent != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 1) && (parent.toString().equals(tbpart.getText()))) {
            String[] e = OVData.getBOMParentOpElements(parent.toString(), node.toString());
            if (e != null) {
                double pph = Double.valueOf(e[5]);
                pph = 1 / pph;
                double pps = Double.valueOf(e[6]);
                
                if (pps != 0) 
                pps = 1 / pps;
                
                
                tbrunrate.setText(String.valueOf(df2.format(Double.valueOf(e[0]))));
                tbsetuprate.setText(String.valueOf(df2.format(Double.valueOf(e[1]))));
                tbburdenrate.setText(String.valueOf(df2.format(Double.valueOf(e[2]))));
                tbcrewsize.setText(e[3]);
                tbsetupsize.setText(e[4]);
                tbpph.setText(String.valueOf(df.format(pph)));
                tbpps.setText(String.valueOf(df.format(pps)));
                
                // init simulation
                 tbrunratesim.setText(String.valueOf(df2.format(Double.valueOf(e[0]))));
                tbsetupratesim.setText(String.valueOf(df2.format(Double.valueOf(e[1]))));
                tbburdenratesim.setText(String.valueOf(df2.format(Double.valueOf(e[2]))));
                tbcrewsizesim.setText(e[3]);
                tbsetupsizesim.setText(e[4]);
                tbpphsim.setText(String.valueOf(df.format(pph)));
                tbppssim.setText(String.valueOf(df.format(pps)));
                
            }
        }
        
         // let's set child selection fields and enable update/delete for immediate children only
        if (parent != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 2) ) {
           btupdate.setEnabled(true);
           btdelete.setEnabled(true);
           setcomponentattributes(tbpart.getText(), node.toString(), parent.toString());
        } else {
           btupdate.setEnabled(false);
           btdelete.setEnabled(false); 
        }
        
        
       }
       
     
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
            getComponentDetail(ddcomp.getSelectedItem().toString());
    }//GEN-LAST:event_ddcompActionPerformed

    private void tbrunratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrunratesimFocusLost
             String x = BlueSeerUtils.bsformat("", tbrunratesim.getText(), "2");
        if (x.equals("error")) {
            tbrunratesim.setText("");
            tbrunratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbrunratesim.requestFocus();
        } else {
            tbrunratesim.setText(x);
            tbrunratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbrunratesimFocusLost

    private void tbsetupratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetupratesimFocusLost
              String x = BlueSeerUtils.bsformat("", tbsetupratesim.getText(), "2");
        if (x.equals("error")) {
            tbsetupratesim.setText("");
            tbsetupratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsetupratesim.requestFocus();
        } else {
            tbsetupratesim.setText(x);
            tbsetupratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbsetupratesimFocusLost

    private void tbburdenratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbburdenratesimFocusLost
              String x = BlueSeerUtils.bsformat("", tbburdenratesim.getText(), "2");
        if (x.equals("error")) {
            tbburdenratesim.setText("");
            tbburdenratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbburdenratesim.requestFocus();
        } else {
            tbburdenratesim.setText(x);
            tbburdenratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbburdenratesimFocusLost

    private void tbcrewsizesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcrewsizesimFocusLost
        String x = BlueSeerUtils.bsformat("", tbcrewsizesim.getText(), "2");
        if (x.equals("error")) {
            tbcrewsizesim.setText("");
            tbcrewsizesim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbcrewsizesim.requestFocus();
        } else {
            tbcrewsizesim.setText(x);
            tbburdenratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbcrewsizesimFocusLost

    private void tbsetupsizesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetupsizesimFocusLost
        String x = BlueSeerUtils.bsformat("", tbsetupsizesim.getText(), "2");
        if (x.equals("error")) {
            tbsetupsizesim.setText("");
            tbsetupsizesim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsetupsizesim.requestFocus();
        } else {
            tbsetupsizesim.setText(x);
            tbsetupsizesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbsetupsizesimFocusLost

    private void tbpphsimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpphsimFocusLost
        String x = BlueSeerUtils.bsformat("", tbpphsim.getText(), "2");
        if (x.equals("error")) {
            tbpphsim.setText("");
            tbpphsim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbpphsim.requestFocus();
        } else {
            tbpphsim.setText(x);
            tbpphsim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbpphsimFocusLost

    private void tbppssimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbppssimFocusLost
         String x = BlueSeerUtils.bsformat("", tbppssim.getText(), "2");
        if (x.equals("error")) {
            tbppssim.setText("");
            tbppssim.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbppssim.requestFocus();
        } else {
            tbppssim.setText(x);
            tbppssim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbppssimFocusLost


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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblcomp;
    private javax.swing.JLabel lblparent;
    private javax.swing.JTable matltable;
    private javax.swing.JTextField tbburdenrate;
    private javax.swing.JTextField tbburdenratesim;
    private javax.swing.JTextField tbcompcost;
    private javax.swing.JTextField tbcomptype;
    private javax.swing.JTextField tbcrewsize;
    private javax.swing.JTextField tbcrewsizesim;
    private javax.swing.JTextField tblotsize;
    private javax.swing.JTextField tbparentcostCUR;
    private javax.swing.JTextField tbparentcostSTD;
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
    private javax.swing.JTextField tbsetuprate;
    private javax.swing.JTextField tbsetupratesim;
    private javax.swing.JTextField tbsetupsize;
    private javax.swing.JTextField tbsetupsizesim;
    private javax.swing.JTextField tbtotmaterial;
    private javax.swing.JTextField tbtotmaterialsim;
    private javax.swing.JTextField tbtotoperational;
    private javax.swing.JTextField tbtotoperationalsim;
    // End of variables declaration//GEN-END:variables
}
