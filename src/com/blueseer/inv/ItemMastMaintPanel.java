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
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.IBlueSeer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author vaughnte
 */
public class ItemMastMaintPanel extends javax.swing.JPanel implements IBlueSeer {

    // global variable declarations
                boolean isLoad = false;
    
   // global datatablemodel declarations    
    javax.swing.table.DefaultTableModel transmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"type", "date", "tranID", "qty"});
    javax.swing.table.DefaultTableModel locmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"Site", "Loc", "WH", "Qty", "Date"});
    javax.swing.table.DefaultTableModel routingmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"Op", "Desc", "Mtl", "Lbr", "Bdn", "Ovh", "Svc", "Tot"});
    
    public ItemMastMaintPanel() {
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
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", MainPanel);
       jTabbedPane1.add("Cost/BOM", CostBOMPanel);
       jTabbedPane1.add("Images", ImagePanel);
       
       DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
       jTree1.setModel(new DefaultTreeModel(root));
       transmodel.setNumRows(0);
       locmodel.setNumRows(0);
       routingmodel.setNumRows(0);
      
       labelmessage.setText("");
       imagelabel.setIcon(null);
       
       
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
       revlevel.setText("");
       custrevlevel.setText("");
       tbminordqty.setText("");
       tbleadtime.setText("");
       tbsafestock.setText("");
       tbnetwt.setText("");
       tbshipwt.setText("");
       tbdesc.setText("");
       tbgroup.setText("");
       tbcreatedate.setText("");
       tbcreatedate.setEditable(false);
       tbroutingcode.setText("");
       tbsellprice.setText("");
       cbplan.setSelected(false);
       cbmrp.setSelected(false);
       cbschedule.setSelected(false);
       cbaltbom.setSelected(false);
       tbqtyoh.setText("");
       tbqtyoh.setEditable(false);
       comments.setText("");
       tbpurchprice.setText("");
       tbdrawing.setText("");
       tblotsize.setText("");
       tbroutingcode.setText("");
      tbmtlstd.setText("");
      tblbrstd.setText("");
      tbbdnstd.setText("");
      tbovhstd.setText("");
      tboutstd.setText("");
      tbtotcoststd.setText("");
       
      tbmtlcur.setText("");
      tblbrcur.setText("");
      tbbdncur.setText("");
      tbovhcur.setText("");
      tboutcur.setText("");
      tbtotcostcur.setText("");
      
      tboutcost.setText("");
       tbovhcost.setText("");
       tbmtlcost.setText("");
       
        ddprodcode.removeAllItems();
       ddsite.removeAllItems();
       dduom.removeAllItems();
       ddtax.removeAllItems();
       ddloc.removeAllItems();
       ddwh.removeAllItems();
       ddtype.removeAllItems();
       
       
       ArrayList<String[]> mylist = OVData.getItemMaintInit();
       for (String[] code : mylist) {
            if (code[0].equals("prodline"))
            ddprodcode.addItem(code[1]);
            if (code[0].equals("site"))
            ddsite.addItem(code[1]);
            if (code[0].equals("uom"))
            dduom.addItem(code[1]);
            if (code[0].equals("tax"))
            ddtax.addItem(code[1]);
            if (code[0].equals("loc"))
            ddloc.addItem(code[1]);
            if (code[0].equals("wh"))
            ddwh.addItem(code[1]);
            if (code[0].equals("type"))
            ddtype.addItem(code[1]);
            
        }
       
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
        
       
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        
       
        ddtax.insertItemAt("", 0);
        ddtax.setSelectedIndex(0);
        
       
        ddsite.setSelectedItem(OVData.getDefaultSite());
        ddcode.setSelectedIndex(0);
        
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
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must choose a site");
                    return b;
                }
               
                if (ddcode.getSelectedItem() == null || ddcode.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must choose an item code");
                    ddcode.requestFocus();
                    return b;
                }
                
                if (ddprodcode.getSelectedItem() == null || ddprodcode.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must choose a product code");
                    ddprodcode.requestFocus();
                    return b;
                }
                
                if (tbdesc.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a description");
                    tbdesc.requestFocus();
                    return b;
                }
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a code");
                    tbkey.requestFocus();
                    return b;
                }
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
        
        setPanelComponentState(this, false); 
        setComponentDefaultValues();
        btnew.setEnabled(true);
        btitembrowse.setEnabled(true);
        
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
                String status = "";
                String uom = "";
                String type = "";
                String code = "";
                String prodcode = "";
                DecimalFormat df = new DecimalFormat("0.00000");
                
                proceed = validateInput("addRecord");
                
                if (proceed) {
                    
                    res = st.executeQuery("SELECT it_item FROM  item_mstr where it_item = " + "'" + tbkey.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                
                            if (dduom.getItemCount() > 0)
                                uom = dduom.getSelectedItem().toString();

                            if (ddcode.getItemCount() > 0)
                                code = ddcode.getSelectedItem().toString();

                            if (ddtype.getItemCount() > 0)
                                type = ddtype.getSelectedItem().toString();

                            if (ddstatus.getItemCount() > 0)
                                status = ddstatus.getSelectedItem().toString();

                            if (ddprodcode.getItemCount() > 0)
                                prodcode = ddprodcode.getSelectedItem().toString();
                            double lotsize = 0.00;
                            if (! tblotsize.getText().isEmpty()) {
                                lotsize = Double.valueOf(tblotsize.getText());
                            }
                            double sellprice = 0.00;
                            if (! tbsellprice.getText().isEmpty()) {
                                sellprice = Double.valueOf(tbsellprice.getText());
                            }
                            double purprice = 0.00;
                            if (! tbpurchprice.getText().isEmpty()) {
                                purprice = Double.valueOf(tbpurchprice.getText());
                            }
                              double mtlcost = 0.00;
                            if (! tbmtlcost.getText().isEmpty()) {
                                mtlcost = Double.valueOf(tbmtlcost.getText());
                            }
                             double ovhcost = 0.00;
                            if (! tbovhcost.getText().isEmpty()) {
                                ovhcost = Double.valueOf(tbovhcost.getText());
                            }
                             double outcost = 0.00;
                            if (! tboutcost.getText().isEmpty()) {
                                outcost = Double.valueOf(tboutcost.getText());
                            }
                            double netwgt = 0.00;
                            if (! tbnetwt.getText().isEmpty()) {
                                netwgt = Double.valueOf(tbnetwt.getText());
                            }
                            double shipwgt = 0.00;
                            if (! tbshipwt.getText().isEmpty()) {
                                shipwgt = Double.valueOf(tbshipwt.getText());
                            }
                            int leadtime = 0;
                            if (! tbleadtime.getText().isEmpty()) {
                                leadtime = Integer.valueOf(tbleadtime.getText());
                            }
                            int safestock = 0;
                            if (! tbsafestock.getText().isEmpty()) {
                                safestock = Integer.valueOf(tbsafestock.getText());
                            }
                            int minordqty = 0;
                            if (! tbminordqty.getText().isEmpty()) {
                                minordqty = Integer.valueOf(tbminordqty.getText());
                            }
                        st.executeUpdate("insert into item_mstr "
                            + "(it_item, it_desc, it_lotsize, "
                            + "it_sell_price, it_pur_price, it_ovh_cost, it_out_cost, it_mtl_cost, it_code, it_type, it_group, "
                            + "it_prodline, it_drawing, it_rev, it_custrev, it_wh, it_loc, it_site, it_comments, "
                            + "it_status, it_uom, it_net_wt, it_ship_wt, "
                            + "it_leadtime, it_safestock, it_minordqty, it_mrp, it_sched, it_plan, it_wf, it_taxcode, it_createdate ) "
                            + " values ( " + "'" + tbkey.getText().toString().replace("'", "") + "'" + ","
                            + "'" + tbdesc.getText().toString().replace("'", "") + "'" + ","
                            + "'" + lotsize + "'" + ","
                            + "'" + df.format(sellprice) + "'" + ","
                            + "'" + df.format(purprice) + "'" + ","
                            + "'" + df.format(ovhcost) + "'" + ","
                            + "'" + df.format(outcost) + "'" + ","
                            + "'" + df.format(mtlcost) + "'" + ","
                            + "'" + code + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + tbgroup.getText().toString().replace("'", "") + "'" + ","
                            + "'" + prodcode + "'" + ","
                            + "'" + tbdrawing.getText().toString().replace("'", "") + "'" + ","
                            + "'" + revlevel.getText().toString().replace("'", "") + "'" + ","
                            + "'" + custrevlevel.getText().toString().replace("'", "") + "'" + ","
                            + "'" + ddwh.getSelectedItem().toString() + "'" + ","
                            + "'" + ddloc.getSelectedItem().toString() + "'" + ","        
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + comments.getText().toString().replace("'", "") + "'" + ","
                            + "'" + status + "'" + ","
                            + "'" + uom + "'" + ","
                            + "'" + netwgt + "'" + ","
                            + "'" + shipwgt + "'" + ","
                            + "'" + leadtime + "'" + ","
                            + "'" + safestock + "'" + ","
                            + "'" + minordqty + "'" + ","    
                            + "'" + BlueSeerUtils.boolToInt(cbmrp.isSelected()) + "'" + ","
                            + "'" + BlueSeerUtils.boolToInt(cbschedule.isSelected()) + "'" + ","
                            + "'" + BlueSeerUtils.boolToInt(cbplan.isSelected()) + "'" + ","
                            + "'" + tbroutingcode.getText().replace("'", "") + "'" + ","
                            + "'" + ddtax.getSelectedItem().toString() + "'" + ","
                            + "'" + bsmf.MainFrame.dfdate.format(new Date()) + "'"
                            + ")"
                            + ";"); 
                        
                        // now add item cost record for later use
                        OVData.addItemCostRec(tbkey.getText(), ddsite.getSelectedItem().toString(), "standard", mtlcost, ovhcost, outcost, (mtlcost + ovhcost + outcost));
                        
                        
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
            DecimalFormat df = new DecimalFormat("0.00000");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
               proceed = validateInput("updateRecord");
                
                if (proceed) {
                   
                    double lotsize = 0.00;
                if (! tblotsize.getText().isEmpty()) {
                    lotsize = Double.valueOf(tblotsize.getText());
                }
                double sellprice = 0.00;
                if (! tbsellprice.getText().isEmpty()) {
                    sellprice = Double.valueOf(tbsellprice.getText());
                }
                double purprice = 0.00;
                if (! tbpurchprice.getText().isEmpty()) {
                    purprice = Double.valueOf(tbpurchprice.getText());
                }
                 double ovhcost = 0.00;
                if (! tbovhcost.getText().isEmpty()) {
                    ovhcost = Double.valueOf(tbovhcost.getText());
                }
                 double mtlcost = 0.00;
                if (! tbmtlcost.getText().isEmpty()) {
                    mtlcost = Double.valueOf(tbmtlcost.getText());
                }
                 double outcost = 0.00;
                if (! tboutcost.getText().isEmpty()) {
                    outcost = Double.valueOf(tboutcost.getText());
                }
                double netwgt = 0.00;
                if (! tbnetwt.getText().isEmpty()) {
                    netwgt = Double.valueOf(tbnetwt.getText());
                }
                double shipwgt = 0.00;
                if (! tbshipwt.getText().isEmpty()) {
                    shipwgt = Double.valueOf(tbshipwt.getText());
                }
                int leadtime = 0;
                if (! tbleadtime.getText().isEmpty()) {
                    leadtime = Integer.valueOf(tbleadtime.getText());
                }
                int safestock = 0;
                if (! tbsafestock.getText().isEmpty()) {
                    safestock = Integer.valueOf(tbsafestock.getText());
                }
                int minordqty = 0;
                if (! tbminordqty.getText().isEmpty()) {
                    minordqty = Integer.valueOf(tbminordqty.getText());
                }
                
                st.executeUpdate("update item_mstr "
                        + " set it_desc = " + "'" + tbdesc.getText().toString().replace("'", "") + "'" + ","
                        + "it_lotsize = " + "'" + lotsize + "'" + ","
                        + "it_sell_price = " + "'" + df.format(sellprice) + "'" + ","
                        + "it_pur_price = " + "'" + df.format(purprice) + "'" + ","
                        + "it_ovh_cost = " + "'" + df.format(ovhcost) + "'" + ","
                        + "it_out_cost = " + "'" + df.format(outcost) + "'" + ","
                        + "it_mtl_cost = " + "'" + df.format(mtlcost) + "'" + ","
                        + "it_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                        + "it_code = " + "'" + ddcode.getSelectedItem().toString() + "'" + ","
                        + "it_group = " + "'" + tbgroup.getText().toString().replace("'", "") + "'" + ","
                        + "it_prodline = " + "'" + ddprodcode.getSelectedItem().toString() + "'" + ","
                        + "it_rev = " + "'" + revlevel.getText().toString().replace("'", "") + "'" + ","
                        + "it_custrev = " + "'" + custrevlevel.getText().toString().replace("'", "") + "'" + ","
                        + "it_net_wt = " + "'" + netwgt + "'" + ","
                        + "it_ship_wt = " + "'" + shipwgt + "'" + ","
                        + "it_loc = " + "'" + ddloc.getSelectedItem().toString() + "'" + ","
                        + "it_wh = " + "'" + ddwh.getSelectedItem().toString() + "'" + ","        
                        + "it_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "it_wf = " + "'" + tbroutingcode.getText().toString() + "'" + ","  
                        + "it_leadtime = " + "'" + leadtime + "'" + ","
                        + "it_safestock = " + "'" + safestock + "'" + ","
                        + "it_minordqty = " + "'" + minordqty + "'" + ","
                        + "it_mrp = " + "'" + BlueSeerUtils.boolToInt(cbmrp.isSelected()) + "'" + ","
                        + "it_plan = " + "'" + BlueSeerUtils.boolToInt(cbplan.isSelected()) + "'" + ","
                        + "it_sched = " + "'" + BlueSeerUtils.boolToInt(cbschedule.isSelected()) + "'" + ","
                        + "it_status = " + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                        + "it_comments = " + "'" + comments.getText().toString().replace("'", "") + "'" + ","
                        + "it_uom = " + "'" + dduom.getSelectedItem().toString() + "'" + ","
                        + "it_taxcode = " + "'" + ddtax.getSelectedItem().toString() + "'"        
                        + " where it_item = " + "'" + tbkey.getText().toString() + "'"
                        + ";");
                
                    
                    
                    
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
              
                   int i = st.executeUpdate("delete from item_mstr where it_item = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from item_cost where itc_item = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from item_image where iti_item = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from in_mstr where in_part = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from pbm_mstr where ps_parent = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from pbm_mstr where ps_child = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from plan_mstr where plan_part = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from pland_mstr where pland_part = " + "'" + x[0] + "'" + ";");
                   st.executeUpdate("delete from mrp_mstr where mrp_part = " + "'" + x[0] + "'" + ";");
                    if (i > 0) {
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
                 double mtl = 0.00;
                double lbr = 0.00;
                double bdn = 0.00;
                double ovh = 0.00;
                double out = 0.00;
                double tot = 0.00;
                
                DecimalFormat df = new DecimalFormat("0.00000"); 
                
                    res = st.executeQuery("SELECT * from item_mstr left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                            " where it_item = " + "'" + 
                            x[0] + "'" + ";");
                    while (res.next()) {
                        i++;
                        
                        mtl = res.getDouble("itc_mtl_low") + res.getDouble("itc_mtl_top");
                        lbr = res.getDouble("itc_lbr_low") + res.getDouble("itc_lbr_top");
                        bdn = res.getDouble("itc_bdn_low") + res.getDouble("itc_bdn_top");
                        ovh = res.getDouble("itc_ovh_low") + res.getDouble("itc_ovh_top");
                        out = res.getDouble("itc_out_low") + res.getDouble("itc_out_top");
                        tot = res.getDouble("itc_total");
                        tbkey.setText(res.getString("it_item"));
                        tbdesc.setText(res.getString("it_desc"));
                      ddprodcode.setSelectedItem(res.getString("it_prodline"));
                      ddstatus.setSelectedItem(res.getString("it_status"));
                      ddcode.setSelectedItem(res.getString("it_code"));
                      dduom.setSelectedItem(res.getString("it_uom"));
                      ddtax.setSelectedItem(res.getString("it_taxcode"));
                      ddtype.setSelectedItem(res.getString("it_type"));
                      comments.setText(res.getString("it_comments"));
                      tbdrawing.setText(res.getString("it_drawing"));
                       tbcreatedate.setText(res.getString("it_createdate"));
                      ddwh.setSelectedItem(res.getString("it_wh"));
                      ddloc.setSelectedItem(res.getString("it_loc"));
                      tbroutingcode.setText(res.getString("it_wf"));
                      revlevel.setText(res.getString("it_rev"));
                      tbshipwt.setText(res.getString("it_ship_wt"));
                      tbnetwt.setText(res.getString("it_net_wt"));
                      ddsite.setSelectedItem(res.getString("it_site"));
                      
                      
                      tbminordqty.setText(res.getString("it_minordqty"));
                      tbsafestock.setText(res.getString("it_safestock"));
                      tbleadtime.setText(res.getString("it_leadtime"));
                      cbmrp.setSelected(res.getBoolean("it_mrp"));
                      cbplan.setSelected(res.getBoolean("it_plan"));
                      cbschedule.setSelected(res.getBoolean("it_sched")); 
                      tblotsize.setText(res.getString("it_lotsize"));
                      tbmtlstd.setText(df.format(mtl));
                      tblbrstd.setText(df.format(lbr));
                      tbbdnstd.setText(df.format(bdn));
                      tbovhstd.setText(df.format(ovh));
                      tboutstd.setText(df.format(out));
                      tbtotcoststd.setText(df.format(tot));
                      
                      tbsellprice.setText(df.format(Double.valueOf(res.getString("it_sell_price"))));
                      tbpurchprice.setText(df.format(Double.valueOf(res.getString("it_pur_price"))));
                      tbmtlcost.setText(df.format(Double.valueOf(res.getString("it_mtl_cost"))));
                      tbovhcost.setText(df.format(Double.valueOf(res.getString("it_ovh_cost"))));
                      tboutcost.setText(df.format(Double.valueOf(res.getString("it_out_cost"))));
                      
                      
                  }
                    if (i > 0) {
                    bind_tree_op(x[0]);                    
                    getrecenttrans(x[0]);                    
                    getlocqty(x[0]);                    
                    getItemImages(x[0]);
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
    
      
    // custom functions  
    public void getItemImages(String item) {
        isLoad = true;
        imagelabel.setIcon(null);
        ArrayList<String> images = new ArrayList();
        ddimage.removeAllItems();
        labelmessage.setText("");
        images = OVData.getItemImagesFile(item);
        for (String code : images) {
            ddimage.addItem(code);
        }
        ddimage.insertItemAt("", 0);
        ddimage.setSelectedIndex(0);
        btdeleteimage.setEnabled(false);
        isLoad = false;
        
        if (ddimage.getItemCount() == 1) {
            labelmessage.setText("No images available for this item.");
        }
        
      }
    
    public void getrecenttrans(String parentpart) {
             
       Double opcost = 0.00;
       Double prevcost = 0.00;
       DecimalFormat df = new DecimalFormat("#.####"); 
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

                
                transmodel.setRowCount(0);
                tabletrans.setModel(transmodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT tr_type, tr_eff_date, tr_id, tr_qty  " +
                        " FROM  tran_mstr  " +
                        " where tr_part = " + "'" + parentpart.toString() + "'" + 
                        " order by tr_eff_date desc limit 25 ;");

                while (res.next()) {
                    i++;
                    transmodel.addRow(new Object[]{
                                res.getString("tr_type"),
                                res.getString("tr_eff_date"),
                                res.getInt("tr_id"),
                                res.getDouble("tr_qty")
                            });
              
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to get tran_mstr info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
             
    public void getlocqty(String parentpart) {
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;
                double tot = 0.00;

                
                locmodel.setRowCount(0);
                tablelocqty.setModel(locmodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT in_site, in_wh, in_loc, in_qoh, in_date  " +
                        " FROM  in_mstr  " +
                        " where in_part = " + "'" + parentpart.toString() + "'" + 
                        " order by in_wh, in_loc ;");

                while (res.next()) {
                    i++;
                    tot = tot + Double.valueOf(res.getInt("in_qoh"));
                    locmodel.addRow(new Object[]{
                                res.getString("in_site"),
                                res.getString("in_loc"),
                                res.getString("in_wh"),
                                res.getInt("in_qoh"),
                                res.getString("in_date")
                            });
              
                }
                tbqtyoh.setText(String.valueOf(tot));
            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to get location qty");
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
      
    public void getroutingcost(String parentpart) {
             
              Double opcost = 0.00;
              Double totcost = 0.00;
       Double prevcost = 0.00;
       Double mtl = 0.00;
       Double lbr = 0.00;
       Double bdn = 0.00;
       Double ovh = 0.00;
       Double svc = 0.00;
       Double lastmtl = 0.00;
       Double lastlbr = 0.00;
       Double lastbdn = 0.00;
       Double lastovh = 0.00;
       Double lastsvc = 0.00;
       
       DecimalFormat df = new DecimalFormat("#.####"); 
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

                
                routingmodel.setRowCount(0);
             //   tablerouting.setModel(routingmodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT it_wf, wf_desc, it_item, itr_total, itr_op,  " +
                        " itr_mtl_top, itr_mtl_low, itr_lbr_top, itr_lbr_low, itr_bdn_top, itr_bdn_low, itr_ovh_top, itr_ovh_low, " +
                        " itr_out_top, itr_out_low " +
                        " FROM  item_mstr  " +
                        " inner join itemr_cost on itr_item = it_item and itr_routing = item_mstr.it_wf " +
                        " inner join wf_mstr on wf_id = it_wf and wf_op = itr_op " +
                        " where it_item = " + "'" + parentpart.toString() + "'" + 
                        " order by it_item desc ;");

               /*
                while (res.next()) {
                    i++;
                    if (i == 1) {
                    opcost = res.getDouble("itr_total");
                    mtl = res.getDouble("itr_mtl_top") + res.getDouble("itr_mtl_low");
                    lbr = res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low");
                    bdn = res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low");
                    ovh = res.getDouble("itr_ovh_top") + res.getDouble("itr_ovh_low");
                    svc = res.getDouble("itr_out_top") + res.getDouble("itr_out_low");
                    }
                    else {
                    opcost = res.getDouble("itr_total") - prevcost;
                    mtl = res.getDouble("itr_mtl_top") + res.getDouble("itr_mtl_low") - lastmtl;
                    lbr = res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low") - lastlbr;
                    bdn = res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low") - lastbdn;
                    ovh = res.getDouble("itr_ovh_top") + res.getDouble("itr_ovh_low") - lastovh;
                    svc = res.getDouble("itr_out_top") + res.getDouble("itr_out_low") - lastsvc;
                    }        
                    routingmodel.addRow(new Object[]{
                                res.getString("itr_op"),
                                res.getString("wf_desc"),
                                (df.format(mtl)),
                                (df.format(lbr)),
                                (df.format(bdn)),
                                (df.format(ovh)),
                                (df.format(svc)),
                                df.format(opcost)
                            });
               prevcost = res.getDouble("itr_total");
               lastmtl = res.getDouble("itr_mtl_top") + res.getDouble("itr_mtl_low");
               lastlbr = res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low");
               lastbdn = res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low");
               lastovh = res.getDouble("itr_ovh_top") + res.getDouble("itr_ovh_low");
               lastsvc = res.getDouble("itr_out_top") + res.getDouble("itr_out_low");
                }
                */
               totcost = 0.00;
               while (res.next()) {
                    i++;
                opcost = res.getDouble("itr_total");
                totcost += opcost;
                    mtl = res.getDouble("itr_mtl_top") + res.getDouble("itr_mtl_low");
                    lbr = res.getDouble("itr_lbr_top") + res.getDouble("itr_lbr_low");
                    bdn = res.getDouble("itr_bdn_top") + res.getDouble("itr_bdn_low");
                    ovh = res.getDouble("itr_ovh_top") + res.getDouble("itr_ovh_low");
                    svc = res.getDouble("itr_out_top") + res.getDouble("itr_out_low");
               
               routingmodel.addRow(new Object[]{
                                res.getString("itr_op"),
                                res.getString("wf_desc"),
                                (df.format(mtl)),
                                (df.format(lbr)),
                                (df.format(bdn)),
                                (df.format(ovh)),
                                (df.format(svc)),
                                df.format(opcost)
                            });
               
               }
                
                
                
                
                
                routingmodel.addRow(new Object[]{"","","","","","","",df.format(totcost)});

               // if (i == 0  && ddcode.getSelectedItem() != null && ddcode.getSelectedItem().equals("M"))
                   // bsmf.MainFrame.show("No Routing Cost Defined");
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("problem with getroutingcost");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
      
    public void getcurrentcost(String parentpart) {
                calcCost cur = new calcCost();
        DecimalFormat df = new DecimalFormat("0.00000"); 
        ArrayList<Double> costlist = new ArrayList<Double>();
        costlist = cur.getTotalCost(tbkey.getText());
     tbmtlcur.setText(df.format(costlist.get(0)));
     tblbrcur.setText(df.format(costlist.get(1)));
     tbbdncur.setText(df.format(costlist.get(2)));
     tbovhcur.setText(df.format(costlist.get(3)));
     tboutcur.setText(df.format(costlist.get(4)));
     tbtotcostcur.setText(df.format(costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4)));
         }
         
    public void bind_tree_op(String parentpart) {
      //  jTree1.setModel(null);
       
      //  DefaultMutableTreeNode mynode = get_nodes(parentpart);
          DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(parentpart);
          ArrayList<String> myops = new ArrayList<String>();
          myops = OVData.getOperationsByPart(parentpart);
          for (String myop : myops) {
              mynode.add(get_nodes_by_op(parentpart, myop));
          }

      //get_nodes(parentpart);
       
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        jTree1.setVisible(true);
        
    }
       
    public DefaultMutableTreeNode get_nodes_by_op(String mypart, String myop)  {
           DecimalFormat df = new DecimalFormat("0.00000");
       DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(myop);
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist = OVData.getpsmstrlistbyopWCost(newpart[0], myop);
     //   mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                   mfgnode = get_nodes_op(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));
                   
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
      
    public DefaultMutableTreeNode get_nodes_op(String mypart)  {
        DecimalFormat df = new DecimalFormat("0.00000"); 
        DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(mypart);
        String[] newpart = mypart.split("___");
        ArrayList<String> mylist = new ArrayList<String>();
        ArrayList<String> myops = new ArrayList<String>();
        myops = OVData.getpsmstrlist(newpart[0]);
        mylist = OVData.getpsmstrlist(newpart[0]);
        for ( String myvalue : mylist) {
            String[] value = myvalue.toUpperCase().split(",");
              if (value[0].toUpperCase().compareTo(newpart[0].toUpperCase().toString()) == 0) {
               
                  if (value[2].toUpperCase().compareTo("M") == 0) {
                    DefaultMutableTreeNode mfgnode = new DefaultMutableTreeNode();   
                    mfgnode = get_nodes_op(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));
                    mynode.add(mfgnode);
                  } else {
                  DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(value[1] + "___" + value[4] + "___" + df.format(Double.valueOf(value[3])) + "___" + df.format(Double.valueOf(value[5])));   
                 
                  mynode.add(childnode);
                  }
              }
        }
        return mynode;
     }
    
    
    /**
     * This method is called from within the bsmf.MainFrame.constructor to initialize the form.
     * WARNING: Do NOT modify this code. The bsmf.MainFrame.content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new javax.swing.JFileChooser();
        MainPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        ddstatus = new javax.swing.JComboBox();
        jScrollPane12 = new javax.swing.JScrollPane();
        comments = new javax.swing.JTextArea();
        ddprodcode = new javax.swing.JComboBox();
        jLabel60 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        tbgroup = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        ddcode = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox();
        jLabel72 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        tblotsize = new javax.swing.JTextField();
        jLabel76 = new javax.swing.JLabel();
        btitembrowse = new javax.swing.JButton();
        btdescbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        ddloc = new javax.swing.JComboBox<>();
        jLabel78 = new javax.swing.JLabel();
        btprintlabel = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        cbmrp = new javax.swing.JCheckBox();
        tbsafestock = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        tbshipwt = new javax.swing.JTextField();
        tbsellprice = new javax.swing.JTextField();
        tbminordqty = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        tbdrawing = new javax.swing.JTextField();
        tbleadtime = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        tbnetwt = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        revlevel = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        custrevlevel = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        tbroutingcode = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        cbplan = new javax.swing.JCheckBox();
        tbpurchprice = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        tbovhcost = new javax.swing.JTextField();
        tboutcost = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        tbmtlcost = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        cbschedule = new javax.swing.JCheckBox();
        cbaltbom = new javax.swing.JCheckBox();
        tbqtyoh = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        ddtax = new javax.swing.JComboBox<>();
        jLabel79 = new javax.swing.JLabel();
        tbcreatedate = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablelocqty = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabletrans = new javax.swing.JTable();
        CostBOMPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        tbmtlstd = new javax.swing.JTextField();
        tblbrstd = new javax.swing.JTextField();
        tbbdnstd = new javax.swing.JTextField();
        tbovhstd = new javax.swing.JTextField();
        tboutstd = new javax.swing.JTextField();
        tbmtlcur = new javax.swing.JTextField();
        tblbrcur = new javax.swing.JTextField();
        tbbdncur = new javax.swing.JTextField();
        tbovhcur = new javax.swing.JTextField();
        tboutcur = new javax.swing.JTextField();
        tbtotcoststd = new javax.swing.JTextField();
        tbtotcostcur = new javax.swing.JTextField();
        btcurrent = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        ImagePanel = new javax.swing.JPanel();
        ddimage = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        imagelabel = new javax.swing.JLabel();
        labelmessage = new javax.swing.JLabel();
        btaddimage = new javax.swing.JButton();
        btdeleteimage = new javax.swing.JButton();
        cbdefault = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Item Master Data"));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ACTIVE", "INACTIVE", "OBSOLETE" }));

        comments.setColumns(20);
        comments.setRows(5);
        jScrollPane12.setViewportView(comments);

        jLabel60.setText("GroupCode");

        jLabel63.setText("Status");

        jLabel28.setText("Comments");

        jLabel34.setText("Prod Code");

        jLabel88.setText("UnitOfMeas");

        jLabel23.setText("Description");

        ddcode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "M", "P", "S" }));
        ddcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcodeActionPerformed(evt);
            }
        });

        jLabel22.setText("PartNumber");

        dduom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EA", "FT" }));

        jLabel37.setText("Class Code");

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel72.setText("LotSize");

        jLabel38.setText("Site");

        jLabel39.setText("Type");

        jLabel76.setText("Warehouse");

        btitembrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btitembrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btitembrowseActionPerformed(evt);
            }
        });

        btdescbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btdescbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdescbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel78.setText("Location");

        btprintlabel.setText("Print Label");
        btprintlabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintlabelActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dduom, 0, 112, Short.MAX_VALUE)
                            .addComponent(ddcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddtype, 0, 112, Short.MAX_VALUE)
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(110, 110, 110)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel72, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel60, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbgroup)
                    .addComponent(tblotsize)
                    .addComponent(ddwh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddprodcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel88)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btitembrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel63)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbdesc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdescbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btprintlabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btitembrowse)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63)
                        .addComponent(btnew)
                        .addComponent(btclear)))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23))
                    .addComponent(btdescbrowse))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88)
                    .addComponent(ddprodcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbgroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60)
                    .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(tblotsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(jLabel76)
                    .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(39, 39, 39)
                .addComponent(btprintlabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);

        cbmrp.setText("MRP");
        cbmrp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmrpActionPerformed(evt);
            }
        });

        tbsafestock.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsafestockFocusLost(evt);
            }
        });

        jLabel61.setText("Drawing");

        tbshipwt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbshipwtFocusLost(evt);
            }
        });

        tbsellprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbsellpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsellpriceFocusLost(evt);
            }
        });

        tbminordqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbminordqtyFocusLost(evt);
            }
        });

        jLabel33.setText("Sell Price");

        tbleadtime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbleadtimeFocusLost(evt);
            }
        });

        jLabel62.setText("ShipWt");

        jLabel67.setText("LeadTime");

        jLabel68.setText("SafetyStock");

        jLabel69.setText("MinOrdQty");

        tbnetwt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbnetwtFocusLost(evt);
            }
        });

        jLabel70.setText("NetWt");

        jLabel65.setText("Eng Rev");

        jLabel66.setText("Cust Rev");

        jLabel71.setText("Routing");

        cbplan.setText("Plan Orders");

        tbpurchprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpurchpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpurchpriceFocusLost(evt);
            }
        });

        jLabel64.setText("Purch Price");

        tbovhcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbovhcostFocusLost(evt);
            }
        });

        tboutcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tboutcostFocusLost(evt);
            }
        });

        jLabel73.setText("Overhead Cost");

        jLabel74.setText("Outside Cost");

        tbmtlcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmtlcostFocusLost(evt);
            }
        });

        jLabel75.setText("Material Cost");

        cbschedule.setText("Scheduled?");

        cbaltbom.setText("Alt BOM");

        jLabel77.setText("QtyOnHand");

        jLabel79.setText("TaxCode");

        jLabel80.setText("CreateDate");

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel65)
                    .addComponent(jLabel64)
                    .addComponent(jLabel61)
                    .addComponent(jLabel33)
                    .addComponent(jLabel66)
                    .addComponent(jLabel73)
                    .addComponent(jLabel74)
                    .addComponent(jLabel75)
                    .addComponent(jLabel71)
                    .addComponent(jLabel79)
                    .addComponent(jLabel80))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tbsellprice, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel70))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tbpurchprice, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel62))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tbdrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel67))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(revlevel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel69))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbovhcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbroutingcode, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(custrevlevel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel77)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbshipwt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbleadtime, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbnetwt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbqtyoh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbminordqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tboutcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbmtlcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(74, 74, 74)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbmrp)
                            .addComponent(cbschedule))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbaltbom)
                            .addComponent(cbplan)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tbcreatedate, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbsafestock, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnetwt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsellprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel70))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbshipwt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpurchprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64)
                    .addComponent(jLabel62))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbleadtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdrawing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61)
                    .addComponent(jLabel67))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcreatedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80)
                    .addComponent(tbsafestock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(revlevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65)
                    .addComponent(tbminordqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69))
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(custrevlevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66)
                    .addComponent(tbqtyoh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbovhcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74)
                    .addComponent(cbmrp)
                    .addComponent(cbplan))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmtlcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel75)
                    .addComponent(cbschedule)
                    .addComponent(cbaltbom))
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbroutingcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel71))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Location Quantities"));

        tablelocqty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Site", "Loc", "Qty", "LastDate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tablelocqty);
        if (tablelocqty.getColumnModel().getColumnCount() > 0) {
            tablelocqty.getColumnModel().getColumn(0).setResizable(false);
            tablelocqty.getColumnModel().getColumn(1).setResizable(false);
            tablelocqty.getColumnModel().getColumn(2).setResizable(false);
            tablelocqty.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Recent Inventory Transactions"));

        tabletrans.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "TranType", "Date", "TranNbr", "Qty"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tabletrans);
        if (tabletrans.getColumnModel().getColumnCount() > 0) {
            tabletrans.getColumnModel().getColumn(0).setResizable(false);
            tabletrans.getColumnModel().getColumn(1).setResizable(false);
            tabletrans.getColumnModel().getColumn(2).setResizable(false);
            tabletrans.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(MainPanel);

        CostBOMPanel.setPreferredSize(new java.awt.Dimension(1041, 631));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Cost"));

        tbmtlstd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tblbrstd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbbdnstd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbovhstd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tboutstd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbmtlcur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tblbrcur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbbdncur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbovhcur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tboutcur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbtotcoststd.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        tbtotcostcur.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N

        btcurrent.setText("Current");
        btcurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcurrentActionPerformed(evt);
            }
        });

        jLabel1.setText("Material");

        jLabel2.setText("Labor");

        jLabel3.setText("Burden");

        jLabel4.setText("Overhead");

        jLabel5.setText("Outside");

        jLabel6.setText("TotalCost");

        jLabel7.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        jLabel7.setText("Standard");

        jLabel8.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        jLabel8.setText("Current");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tboutstd)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbtotcoststd)
                    .addComponent(tbmtlstd)
                    .addComponent(tblbrstd)
                    .addComponent(tbbdnstd)
                    .addComponent(tbovhstd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblbrcur)
                    .addComponent(tbmtlcur)
                    .addComponent(jLabel8)
                    .addComponent(tboutcur)
                    .addComponent(tbbdncur)
                    .addComponent(tbovhcur)
                    .addComponent(tbtotcostcur)
                    .addComponent(btcurrent))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmtlstd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbmtlcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblbrstd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tblbrcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbdnstd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbbdncur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbovhstd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbovhcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutstd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tboutcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotcoststd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tbtotcostcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcurrent)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(293, Short.MAX_VALUE))
        );

        jTree1.setBorder(javax.swing.BorderFactory.createTitledBorder("Bill Of Material"));
        jTree1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("JTree");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(jTree1);

        javax.swing.GroupLayout CostBOMPanelLayout = new javax.swing.GroupLayout(CostBOMPanel);
        CostBOMPanel.setLayout(CostBOMPanelLayout);
        CostBOMPanelLayout.setHorizontalGroup(
            CostBOMPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CostBOMPanelLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE))
        );
        CostBOMPanelLayout.setVerticalGroup(
            CostBOMPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );

        add(CostBOMPanel);

        ImagePanel.setPreferredSize(new java.awt.Dimension(1041, 631));

        ddimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddimageActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(imagelabel);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
        );

        btaddimage.setText("Add Image");
        btaddimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddimageActionPerformed(evt);
            }
        });

        btdeleteimage.setText("Delete Image");
        btdeleteimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteimageActionPerformed(evt);
            }
        });

        cbdefault.setText("Default?");
        cbdefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbdefaultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ImagePanelLayout = new javax.swing.GroupLayout(ImagePanel);
        ImagePanel.setLayout(ImagePanelLayout);
        ImagePanelLayout.setHorizontalGroup(
            ImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ImagePanelLayout.createSequentialGroup()
                .addComponent(ddimage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btaddimage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeleteimage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbdefault)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
                .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ImagePanelLayout.setVerticalGroup(
            ImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ImagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelmessage, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addGroup(ImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddimage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btaddimage)
                        .addComponent(btdeleteimage)
                        .addComponent(cbdefault)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(ImagePanel);
        add(jTabbedPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void ddcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcodeActionPerformed
        
    }//GEN-LAST:event_ddcodeActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btcurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcurrentActionPerformed
       getcurrentcost(tbkey.getText());
    }//GEN-LAST:event_btcurrentActionPerformed

    private void btitembrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btitembrowseActionPerformed
       reinitpanels("BrowseUtil", true, new String[]{"itemmaint","it_item"});
    }//GEN-LAST:event_btitembrowseActionPerformed

    private void btdescbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdescbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"itemmaint","it_desc"});
    }//GEN-LAST:event_btdescbrowseActionPerformed

    private void cbmrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmrpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbmrpActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        if (OVData.isAutoItem()) {
          newAction("item");
        } else {
           newAction("");
           tbkey.requestFocus();
         }
    }//GEN-LAST:event_btnewActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
         if (ddwh.getSelectedItem() != null && ! isLoad) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void ddimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddimageActionPerformed
        if (! isLoad) {
            imagelabel.setIcon(null);
        if (ddimage.getSelectedItem() != null && ! ddimage.getSelectedItem().toString().isEmpty()) {
            String ImageDir = OVData.getSystemImageDirectory();
          ImageIcon imageIcon = new ImageIcon(ImageDir + ddimage.getSelectedItem().toString());
          btdeleteimage.setEnabled(true);
      /* 
      ImageIcon imageIcon = new ImageIcon((new ImageIcon("images/vcs.png"))
        .getImage().getScaledInstance(600, 600,
        java.awt.Image.SCALE_SMOOTH));
          */
        Image newimage = imageIcon.getImage().getScaledInstance(jPanel5.getWidth() - 5, jPanel5.getHeight() - 5, Image.SCALE_DEFAULT);
        imageIcon.setImage(newimage);
        imagelabel.setIcon(imageIcon);
        } else {
         btdeleteimage.setEnabled(false);  
        }
       }
    }//GEN-LAST:event_ddimageActionPerformed

    private void btaddimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddimageActionPerformed
          
        DateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        File file = null;
        String ImageDir = OVData.getSystemImageDirectory();
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            String suffix = FilenameUtils.getExtension(file.getName()); 
            String newFileName = tbkey.getText() + "_" + dfdate.format(now) + "." + suffix;
            // insert image filename into database
            OVData.addItemImage(tbkey.getText(), newFileName);
            
            // now lets copy the file over to the appropriate directory  
            file = new File(SourceDir);
            
       //     java.nio.file.Files.copy(file.toPath(), new File("images/" + newFileName).toPath(), 
                 java.nio.file.Files.copy(file.toPath(), new File(ImageDir + newFileName).toPath(), 
                 java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                 java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                 java.nio.file.LinkOption.NOFOLLOW_LINKS);
            
            getItemImages(tbkey.getText());
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
       
    }//GEN-LAST:event_btaddimageActionPerformed

    private void btdeleteimageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteimageActionPerformed
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        
        if (ddimage.getSelectedItem() == null || ddimage.getSelectedItem().toString().isEmpty()) {
            return;
        }
        
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from item_image where iti_item = " + "'" + tbkey.getText() + "'" 
                           + " AND iti_file = " + "'" + ddimage.getSelectedItem().toString() + "'"
                           + " ;");
                    if (i > 0) {
                        String ImageDir = OVData.getSystemImageDirectory();
                        java.nio.file.Files.deleteIfExists(new File(ImageDir + ddimage.getSelectedItem().toString()).toPath());
                    bsmf.MainFrame.show("deleted image file " + ddimage.getSelectedItem().toString());
                   
                    getItemImages(tbkey.getText());
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Image File");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteimageActionPerformed

    private void cbdefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbdefaultActionPerformed
         if (ddimage.getSelectedItem() == null || ddimage.getSelectedItem().toString().isEmpty()) {
            return;
        }
         
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                int defaultvalue = BlueSeerUtils.boolToInt(cbdefault.isSelected());
              
                   int i = st.executeUpdate("update item_image set iti_default = " + "'" + defaultvalue + "'" + 
                           " where iti_item = " + "'" + tbkey.getText() + "'" 
                           + " AND iti_file = " + "'" + ddimage.getSelectedItem().toString() + "'"
                           + " ;");
                  
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to update default Image File");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_cbdefaultActionPerformed

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

    private void tbsellpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsellpriceFocusLost
            String x = BlueSeerUtils.bsformat("", tbsellprice.getText(), "5");
        if (x.equals("error")) {
            tbsellprice.setText("");
            tbsellprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsellprice.requestFocus();
        } else {
            tbsellprice.setText(x);
            tbsellprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbsellpriceFocusLost

    private void tbsellpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsellpriceFocusGained
        if (tbsellprice.getText().equals("0")) {
            tbsellprice.setText("");
        }
    }//GEN-LAST:event_tbsellpriceFocusGained

    private void tbpurchpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpurchpriceFocusLost
              String x = BlueSeerUtils.bsformat("", tbpurchprice.getText(), "5");
        if (x.equals("error")) {
            tbpurchprice.setText("");
            tbpurchprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbpurchprice.requestFocus();
        } else {
            tbpurchprice.setText(x);
            tbmtlcost.setText(x);
            tbpurchprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpurchpriceFocusLost

    private void tbpurchpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpurchpriceFocusGained
        if (tbpurchprice.getText().equals("0")) {
            tbpurchprice.setText("");
        }
    }//GEN-LAST:event_tbpurchpriceFocusGained

    private void tbmtlcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbmtlcostFocusLost
               String x = BlueSeerUtils.bsformat("", tbmtlcost.getText(), "5");
        if (x.equals("error")) {
            tbmtlcost.setText("");
            tbmtlcost.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbmtlcost.requestFocus();
        } else {
            tbmtlcost.setText(x);
            tbmtlcost.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbmtlcostFocusLost

    private void tboutcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tboutcostFocusLost
                  String x = BlueSeerUtils.bsformat("", tboutcost.getText(), "5");
        if (x.equals("error")) {
            tboutcost.setText("");
            tboutcost.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tboutcost.requestFocus();
        } else {
            tboutcost.setText(x);
            tboutcost.setBackground(Color.white);
        }
    }//GEN-LAST:event_tboutcostFocusLost

    private void tbovhcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbovhcostFocusLost
                  String x = BlueSeerUtils.bsformat("", tbovhcost.getText(), "5");
        if (x.equals("error")) {
            tbovhcost.setText("");
            tbovhcost.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbovhcost.requestFocus();
        } else {
            tbovhcost.setText(x);
            tbovhcost.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbovhcostFocusLost

    private void tbnetwtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbnetwtFocusLost
          String x = BlueSeerUtils.bsformat("", tbnetwt.getText(), "3");
        if (x.equals("error")) {
            tbnetwt.setText("");
            tbnetwt.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbnetwt.requestFocus();
        } else {
            tbnetwt.setText(x);
            tbnetwt.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbnetwtFocusLost

    private void tbshipwtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbshipwtFocusLost
        String x = BlueSeerUtils.bsformat("", tbshipwt.getText(), "3");
        if (x.equals("error")) {
            tbshipwt.setText("");
            tbshipwt.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbshipwt.requestFocus();
        } else {
            tbshipwt.setText(x);
            tbshipwt.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbshipwtFocusLost

    private void tbleadtimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbleadtimeFocusLost
        String x = BlueSeerUtils.bsformat("", tbleadtime.getText(), "0");
        if (x.equals("error")) {
            tbleadtime.setText("");
            tbleadtime.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbleadtime.requestFocus();
        } else {
            tbleadtime.setText(x);
            tbleadtime.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbleadtimeFocusLost

    private void tbsafestockFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsafestockFocusLost
       String x = BlueSeerUtils.bsformat("", tbsafestock.getText(), "0");
        if (x.equals("error")) {
            tbsafestock.setText("");
            tbsafestock.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsafestock.requestFocus();
        } else {
            tbsafestock.setText(x);
            tbsafestock.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbsafestockFocusLost

    private void tbminordqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbminordqtyFocusLost
        String x = BlueSeerUtils.bsformat("", tbminordqty.getText(), "0");
        if (x.equals("error")) {
            tbminordqty.setText("");
            tbminordqty.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbminordqty.requestFocus();
        } else {
            tbminordqty.setText(x);
            tbminordqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbminordqtyFocusLost

    private void btprintlabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintlabelActionPerformed
        String printer = OVData.getDefaultLabelPrinter();
        if (OVData.isValidPrinter(printer)) {
            OVData.printLabelItem(tbkey.getText(), printer);
        } else {
            bsmf.MainFrame.show("no default label printer defined");
        }
    }//GEN-LAST:event_btprintlabelActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CostBOMPanel;
    private javax.swing.JPanel ImagePanel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddimage;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcurrent;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteimage;
    private javax.swing.JButton btdescbrowse;
    private javax.swing.JButton btitembrowse;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprintlabel;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbaltbom;
    private javax.swing.JCheckBox cbdefault;
    private javax.swing.JCheckBox cbmrp;
    private javax.swing.JCheckBox cbplan;
    private javax.swing.JCheckBox cbschedule;
    private javax.swing.JTextArea comments;
    private javax.swing.JTextField custrevlevel;
    private javax.swing.JComboBox ddcode;
    private javax.swing.JComboBox<String> ddimage;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox ddprodcode;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox dduom;
    private javax.swing.JComboBox ddwh;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel imagelabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JTextField revlevel;
    private javax.swing.JTable tablelocqty;
    private javax.swing.JTable tabletrans;
    private javax.swing.JTextField tbbdncur;
    private javax.swing.JTextField tbbdnstd;
    private javax.swing.JTextField tbcreatedate;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbdrawing;
    private javax.swing.JTextField tbgroup;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tblbrcur;
    private javax.swing.JTextField tblbrstd;
    private javax.swing.JTextField tbleadtime;
    private javax.swing.JTextField tblotsize;
    private javax.swing.JTextField tbminordqty;
    private javax.swing.JTextField tbmtlcost;
    private javax.swing.JTextField tbmtlcur;
    private javax.swing.JTextField tbmtlstd;
    private javax.swing.JTextField tbnetwt;
    private javax.swing.JTextField tboutcost;
    private javax.swing.JTextField tboutcur;
    private javax.swing.JTextField tboutstd;
    private javax.swing.JTextField tbovhcost;
    private javax.swing.JTextField tbovhcur;
    private javax.swing.JTextField tbovhstd;
    private javax.swing.JTextField tbpurchprice;
    private javax.swing.JTextField tbqtyoh;
    private javax.swing.JTextField tbroutingcode;
    private javax.swing.JTextField tbsafestock;
    private javax.swing.JTextField tbsellprice;
    private javax.swing.JTextField tbshipwt;
    private javax.swing.JTextField tbtotcostcur;
    private javax.swing.JTextField tbtotcoststd;
    // End of variables declaration//GEN-END:variables
}
