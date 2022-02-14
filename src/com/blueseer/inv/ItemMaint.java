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
import com.blueseer.inv.invData.item_mstr;
import static com.blueseer.inv.invData.addItemMstr;
import static com.blueseer.inv.invData.deleteItemMstr;
import static com.blueseer.inv.invData.getItemMstr;
import static com.blueseer.inv.invData.updateCurrentItemCost;
import static com.blueseer.inv.invData.updateItemMstr;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
public class ItemMaint extends javax.swing.JPanel implements IBlueSeerT  {

    // global variable declarations
                boolean isLoad = false;
    
   
                
   // global datatablemodel declarations    
    javax.swing.table.DefaultTableModel transmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                        getGlobalColumnTag("type"), 
                        getGlobalColumnTag("date"), 
                        getGlobalColumnTag("id"), 
                        getGlobalColumnTag("qty")});
    javax.swing.table.DefaultTableModel locmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                        getGlobalColumnTag("site"), 
                        getGlobalColumnTag("location"), 
                        getGlobalColumnTag("warehouse"), 
                        getGlobalColumnTag("qty"), 
                        getGlobalColumnTag("serial"),
                        getGlobalColumnTag("date")});
    
    
    public ItemMaint() {
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
    
    
    public void setComponentDefaultValues() {
       isLoad = true;
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add(getClassLabelTag("main", this.getClass().getSimpleName()), MainPanel);
       jTabbedPane1.add(getClassLabelTag("costbom", this.getClass().getSimpleName()), CostBOMPanel);
       jTabbedPane1.add(getClassLabelTag("images", this.getClass().getSimpleName()), ImagePanel);
       
       DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
       jTree1.setModel(new DefaultTreeModel(root));
       
       tablelocqty.setModel(locmodel);
       tabletrans.setModel(transmodel);
       
       transmodel.setNumRows(0);
       locmodel.setNumRows(0);
       
      
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
       
       tbsellprice.setText("");
       cbplan.setSelected(false);
       cbmrp.setSelected(false);
       cbschedule.setSelected(false);
       cbphantom.setSelected(false);
       tbqtyoh.setText("");
       tbqtyoh.setEditable(false);
       comments.setText("");
       tbpurchprice.setText("");
       tbdrawing.setText("");
       tblotsize.setText("");
     
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
      
      tbexpiredays.setText("");
      dcexpire.setDate(null);
      
      
       ddprodcode.removeAllItems();
       ddsite.removeAllItems();
       dduom.removeAllItems();
       ddtax.removeAllItems();
       ddloc.removeAllItems();
       ddwh.removeAllItems();
       ddtype.removeAllItems();
       ddrouting.removeAllItems();
       
       
       ArrayList<String[]> mylist = invData.getItemMaintInit();
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
            if (code[0].equals("routing"))
            ddrouting.addItem(code[1]);
        }
       
        ddrouting.insertItemAt("", 0);
        ddrouting.setSelectedIndex(0);
       
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
    
    public void setAction(String[] x) {
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
        boolean b = true;
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddsite.requestFocus();
                    return b;
                }
               
                if (ddcode.getSelectedItem() == null || ddcode.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddcode.requestFocus();
                    return b;
                }
                
                if (ddprodcode.getSelectedItem() == null || ddprodcode.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddprodcode.requestFocus();
                    return b;
                }
                
                if (tbdesc.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbdesc.requestFocus();
                    return b;
                }
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
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
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
        
   }
        
    public String[] addRecord(String[] x) {
     
        String[] m = new String[2];
         m = addItemMstr(createRecord());
         
            double mtlcost = 0;
            if (! tbmtlcost.getText().isEmpty()) {
                mtlcost = bsParseDouble(tbmtlcost.getText());
            }
             double ovhcost = 0;
            if (! tbovhcost.getText().isEmpty()) {
                ovhcost = bsParseDouble(tbovhcost.getText());
            }
             double outcost = 0;
            if (! tboutcost.getText().isEmpty()) {
                outcost = bsParseDouble(tboutcost.getText());
            }
          // now add item cost record for later use
          OVData.addItemCostRec(tbkey.getText(), ddsite.getSelectedItem().toString(), "standard", mtlcost, ovhcost, outcost, (mtlcost + ovhcost + outcost));
          OVData.addItemCostRec(tbkey.getText(), ddsite.getSelectedItem().toString(), "current", mtlcost, ovhcost, outcost, (mtlcost + ovhcost + outcost));
          return m;
       
     }
   
    public String[] updateRecord(String[] x) {
      
      
        double mtlcost = 0;
        if (! tbmtlcost.getText().isEmpty()) {
            mtlcost = bsParseDouble(tbmtlcost.getText());
        }
         double ovhcost = 0;
        if (! tbovhcost.getText().isEmpty()) {
            ovhcost = bsParseDouble(tbovhcost.getText());
        }
         double outcost = 0;
        if (! tboutcost.getText().isEmpty()) {
            outcost = bsParseDouble(tboutcost.getText());
        }
        // capture the current cost 'before' committing to table
        calcCost cur = new calcCost();
        double curcost = cur.getTotalCostSum(tbkey.getText(), OVData.getDefaultBomID(tbkey.getText()));
        
        // now update item master and current item cost record in item_cost
        String[] m = updateItemMstr(createRecord());
        updateCurrentItemCost(tbkey.getText());
          
        // if current item material cost has changed...blow back through all parents and set current price of parents
        // bsmf.MainFrame.show(curcost + "/" + mtlcost + "/" + ovhcost + "/" + outcost);
        if (curcost != (mtlcost + ovhcost + outcost)) {
            ArrayList<String> parents = new ArrayList<String>();
            parents = OVData.getpsmstrparents2(tbkey.getText());
            // Collections.sort(parents);
            for (String parent : parents) {
                updateCurrentItemCost(parent);
            }
        }
         
         return m;
    }
    
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteItemMstr(createRecord()); 
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
    
    public String[] getRecord(String[] key) {
        
        item_mstr x = getItemMstr(key);  
       
        tbkey.setText(x.it_item());
        tbdesc.setText(x.it_desc());
        ddprodcode.setSelectedItem(x.it_prodline());
        ddstatus.setSelectedItem(x.it_status());
        ddcode.setSelectedItem(x.it_code());
        dduom.setSelectedItem(x.it_uom());
        ddtax.setSelectedItem(x.it_taxcode());
        ddtype.setSelectedItem(x.it_type());
        comments.setText(x.it_comments());
        tbdrawing.setText(x.it_drawing());
        tbcreatedate.setText(x.it_createdate());
        ddwh.setSelectedItem(x.it_wh());
        ddloc.setSelectedItem(x.it_loc());
        ddrouting.setSelectedItem(x.it_wf());
        revlevel.setText(x.it_rev());
        tbdefaultcont.setText(x.it_cont());
        tbcontqty.setText(x.it_contqty());
        tbshipwt.setText(x.it_ship_wt());
        tbnetwt.setText(x.it_net_wt());
        ddsite.setSelectedItem(x.it_site());
        tbminordqty.setText(x.it_minordqty());
        tbsafestock.setText(x.it_safestock());
        tbleadtime.setText(x.it_leadtime());
        cbmrp.setSelected(BlueSeerUtils.ConvertStringToBool(x.it_mrp()));
        cbplan.setSelected(BlueSeerUtils.ConvertStringToBool(x.it_plan()));
        cbschedule.setSelected(BlueSeerUtils.ConvertStringToBool(x.it_sched())); 
        cbphantom.setSelected(BlueSeerUtils.ConvertStringToBool(x.it_phantom())); 
        tblotsize.setText(x.it_lotsize());
        tbsellprice.setText(currformat(x.it_sell_price()));
        tbpurchprice.setText(currformat(x.it_pur_price()));
        tbmtlcost.setText(currformat(x.it_mtl_cost()));
        tbovhcost.setText(currformat(x.it_ovh_cost()));
        tboutcost.setText(currformat(x.it_out_cost()));
        tbexpiredays.setText(x.it_expiredays());
        dcexpire.setDate(BlueSeerUtils.parseDate(x.it_expire()));
        bind_tree_op(key[0]);                    
        getrecenttrans(key[0]);                    
        getlocqty(key[0]);                    
        getItemImages(key[0]);
        setAction(x.m());
        return x.m();
    }
    
    public item_mstr createRecord() { 
        String expire = "";
        if (dcexpire.getDate() != null) {
            expire = bsmf.MainFrame.dfdate.format(dcexpire.getDate());
        }
        item_mstr x = new item_mstr(null, tbkey.getText().toString(),
                tbdesc.getText().toUpperCase(),
                bsformat("i", tblotsize.getText(), "").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbsellprice.getText(), "5").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbpurchprice.getText(), "5").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbovhcost.getText(), "5").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tboutcost.getText(), "5").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbmtlcost.getText(), "5").replace(defaultDecimalSeparator, '.'),
                ddcode.getSelectedItem().toString(),
                ddtype.getSelectedItem().toString(),
                tbgroup.getText(),
                ddprodcode.getSelectedItem().toString(),
                tbdrawing.getText().toString(),
                revlevel.getText(),
                custrevlevel.getText(),
                ddwh.getSelectedItem().toString(),
                ddloc.getSelectedItem().toString(),        
                ddsite.getSelectedItem().toString(),
                comments.getText().toString(),
                ddstatus.getSelectedItem().toString(),
                dduom.getSelectedItem().toString(),
                bsformat("d", tbnetwt.getText(), "2").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbshipwt.getText(), "2").replace(defaultDecimalSeparator, '.'),
                tbdefaultcont.getText(),
                bsformat("d", tbcontqty.getText(), "0").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbleadtime.getText(), "0").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbsafestock.getText(), "0").replace(defaultDecimalSeparator, '.'),
                bsformat("d", tbminordqty.getText(), "0").replace(defaultDecimalSeparator, '.'),
                BlueSeerUtils.boolToString(cbmrp.isSelected()),
                BlueSeerUtils.boolToString(cbschedule.isSelected()),
                BlueSeerUtils.boolToString(cbplan.isSelected()),
                ddrouting.getSelectedItem().toString(),
                ddtax.getSelectedItem().toString(),
                bsmf.MainFrame.dfdate.format(new Date()),
                expire,
                bsformat("d", tbexpiredays.getText(), "0").replace(defaultDecimalSeparator, '.'),
                BlueSeerUtils.boolToString(cbphantom.isSelected())
                );
        return x;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemBrowseUtil(luinput.getText(), 0, "it_item");
        } else {
         luModel = DTData.getItemBrowseUtil(luinput.getText(), 0, "it_desc");   
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
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
        
        
    }
   
    // custom functions  
    public void getItemImages(String item) {
        isLoad = true;
        imagelabel.setIcon(null);
        ArrayList<String> images = new ArrayList();
        ddimage.removeAllItems();
        labelmessage.setText("");
        images = invData.getItemImagesFile(item);
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
             
       double opcost = 0;
       double prevcost = 0;
      
        try {
           Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
              

                int i = 0;

                
                transmodel.setRowCount(0);
               
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT tr_type, tr_eff_date, tr_id, tr_base_qty  " +
                        " FROM  tran_mstr  " +
                        " where tr_part = " + "'" + parentpart + "'" + 
                        " order by tr_eff_date desc limit 25 ;");

                while (res.next()) {
                    i++;
                    transmodel.addRow(new Object[]{
                                res.getString("tr_type"),
                                res.getString("tr_eff_date"),
                                res.getInt("tr_id"),
                                res.getDouble("tr_base_qty")
                            });
              
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
             
    public void getlocqty(String parentpart) {
        try {
          Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                int i = 0;
                double tot = 0;

                
                locmodel.setRowCount(0);
                
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT in_site, in_wh, in_loc, in_qoh, in_serial, in_date  " +
                        " FROM  in_mstr  " +
                        " where in_part = " + "'" + parentpart + "'" + 
                        " order by in_wh, in_loc ;");

                while (res.next()) {
                    i++;
                    tot = tot + bsParseDouble(res.getString("in_qoh"));
                    locmodel.addRow(new Object[]{
                                res.getString("in_site"),
                                res.getString("in_loc"),
                                res.getString("in_wh"),
                                res.getInt("in_qoh"),
                                res.getString("in_serial"),
                                res.getString("in_date")
                            });
              
                }
                tbqtyoh.setText(String.valueOf(tot));
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
     
    public void getcurrentcost(String parentpart) {
        calcCost cur = new calcCost();
        ArrayList<Double> costlist = new ArrayList<Double>();
        costlist = cur.getTotalCost(tbkey.getText(), ""); // assume default bom
     tbmtlcur.setText(currformatDouble(costlist.get(0)));
     tblbrcur.setText(currformatDouble(costlist.get(1)));
     tbbdncur.setText(currformatDouble(costlist.get(2)));
     tbovhcur.setText(currformatDouble(costlist.get(3)));
     tboutcur.setText(currformatDouble(costlist.get(4)));
     tbtotcostcur.setText(currformatDouble(costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4)));
         }
    
    public void getstandardcost(String parentpart) {
    ArrayList<Double> costs = invData.getItemCostElements(tbkey.getText(), "standard", ddsite.getSelectedItem().toString());
   
     tbmtlstd.setText(currformatDouble(costs.get(0) + costs.get(5)));
     tblbrstd.setText(currformatDouble(costs.get(1) + costs.get(6)));
     tbbdnstd.setText(currformatDouble(costs.get(2) + costs.get(7)));
     tbovhstd.setText(currformatDouble(costs.get(3) + costs.get(8)));
     tboutstd.setText(currformatDouble(costs.get(4) + costs.get(9)));
     tbtotcoststd.setText(currformatDouble(costs.get(10)));
     }
        
    
    public void bind_tree_op(String parentpart) {
      //  jTree1.setModel(null);
       
      //  DefaultMutableTreeNode mynode = get_nodes(parentpart);
          DefaultMutableTreeNode mynode = new DefaultMutableTreeNode(parentpart);
          ArrayList<String> myops = new ArrayList<String>();
          myops = OVData.getOperationsByItem(parentpart);
          for (String myop : myops) {
              mynode.add(get_nodes_by_op(parentpart, myop));
          }

      //get_nodes(parentpart);
       
        DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        jTree1.setVisible(true);
        
    }
       
    public DefaultMutableTreeNode get_nodes_by_op(String mypart, String myop)  {
        String pattern = "#0.00000";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern); 
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
        String pattern = "#0.00000";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);  
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
        btnew = new javax.swing.JButton();
        ddloc = new javax.swing.JComboBox<>();
        jLabel78 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
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
        cbphantom = new javax.swing.JCheckBox();
        tbqtyoh = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        ddtax = new javax.swing.JComboBox<>();
        jLabel79 = new javax.swing.JLabel();
        tbcreatedate = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        ddrouting = new javax.swing.JComboBox<>();
        tbdefaultcont = new javax.swing.JTextField();
        tbcontqty = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btprintlabel = new javax.swing.JButton();
        tbexpiredays = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
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
        btstandard = new javax.swing.JButton();
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
        jPanel1.setName("panelmaster"); // NOI18N
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
        jLabel60.setName("lblgroup"); // NOI18N

        jLabel63.setText("Status");
        jLabel63.setName("lblstatus"); // NOI18N

        jLabel28.setText("Comments");
        jLabel28.setName("lblcomments"); // NOI18N

        jLabel34.setText("Prod Code");
        jLabel34.setName("lblprodcode"); // NOI18N

        jLabel88.setText("UnitOfMeas");
        jLabel88.setName("lbluom"); // NOI18N

        jLabel23.setText("Description");
        jLabel23.setName("lbldesc"); // NOI18N

        ddcode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "M", "P" }));
        ddcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcodeActionPerformed(evt);
            }
        });

        jLabel22.setText("PartNumber");
        jLabel22.setName("lblitem"); // NOI18N

        dduom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EA", "FT" }));

        jLabel37.setText("Class Code");
        jLabel37.setName("lblclass"); // NOI18N

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel72.setText("LotSize");
        jLabel72.setName("lbllotsize"); // NOI18N

        jLabel38.setText("Site");
        jLabel38.setName("lblsite"); // NOI18N

        jLabel39.setText("Type");
        jLabel39.setName("lbltype"); // NOI18N

        jLabel76.setText("Warehouse");
        jLabel76.setName("lblwarehouse"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel78.setText("Location");
        jLabel78.setName("lblloc"); // NOI18N

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

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel12.setText("expire");
        jLabel12.setName("lblexpiredate"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addComponent(jLabel88))
                                .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(63, 63, 63)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel78)
                    .addComponent(jLabel72)
                    .addComponent(jLabel76)
                    .addComponent(jLabel60)
                    .addComponent(jLabel34))
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
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addGap(38, 38, 38)))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88)
                    .addComponent(ddprodcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addGap(11, 11, 11)
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel12)
                        .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel78)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel28))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2);

        cbmrp.setText("MRP");
        cbmrp.setName("cbmrp"); // NOI18N
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
        jLabel61.setName("lbldrawing"); // NOI18N

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
        jLabel33.setName("lblsellprice"); // NOI18N

        tbleadtime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbleadtimeFocusLost(evt);
            }
        });

        jLabel62.setText("ShipWt");
        jLabel62.setName("lblshipwt"); // NOI18N

        jLabel67.setText("LeadTime");
        jLabel67.setName("lblleadtime"); // NOI18N

        jLabel68.setText("SafetyStock");
        jLabel68.setName("lblsafetystock"); // NOI18N

        jLabel69.setText("MinOrdQty");
        jLabel69.setName("lblminordqty"); // NOI18N

        tbnetwt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbnetwtFocusLost(evt);
            }
        });

        jLabel70.setText("NetWt");
        jLabel70.setName("lblnetwt"); // NOI18N

        jLabel65.setText("Eng Rev");
        jLabel65.setName("lblengrev"); // NOI18N

        jLabel66.setText("Cust Rev");
        jLabel66.setName("lblcustrev"); // NOI18N

        jLabel71.setText("Routing");
        jLabel71.setName("lblrouting"); // NOI18N

        cbplan.setText("Plan Orders");
        cbplan.setName("cbplan"); // NOI18N

        tbpurchprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpurchpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpurchpriceFocusLost(evt);
            }
        });

        jLabel64.setText("Purch Price");
        jLabel64.setName("lblpurchprice"); // NOI18N

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
        jLabel73.setName("lblovhcost"); // NOI18N

        jLabel74.setText("Outside Cost");
        jLabel74.setName("lbloutcost"); // NOI18N

        tbmtlcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmtlcostFocusLost(evt);
            }
        });

        jLabel75.setText("Material Cost");
        jLabel75.setName("lblmatcost"); // NOI18N

        cbschedule.setText("Scheduled?");
        cbschedule.setName("cbsched"); // NOI18N

        cbphantom.setText("Phantom");
        cbphantom.setName("cbphantom"); // NOI18N

        jLabel77.setText("QtyOnHand");
        jLabel77.setName("lblqoh"); // NOI18N

        jLabel79.setText("TaxCode");
        jLabel79.setName("lbltaxcode"); // NOI18N

        jLabel80.setText("CreateDate");
        jLabel80.setName("lblcreatedate"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel9.setText("DefaultCont");
        jLabel9.setName("lbldefaultcont"); // NOI18N

        jLabel10.setText("ContQty");
        jLabel10.setName("lblcontqty"); // NOI18N

        btprintlabel.setText("Print Label");
        btprintlabel.setName("btprintlabels"); // NOI18N
        btprintlabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintlabelActionPerformed(evt);
            }
        });

        jLabel11.setText("Expire Days");
        jLabel11.setName("lblexpiredays"); // NOI18N

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
                                .addComponent(tbovhcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(custrevlevel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel77)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbshipwt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbleadtime, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbnetwt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbqtyoh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(tbminordqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbdefaultcont, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btprintlabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addComponent(tbsafestock, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ddrouting, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbmrp)
                            .addComponent(cbschedule))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbphantom)
                            .addComponent(cbplan)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tboutcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(tbmtlcost, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbcontqty, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .addComponent(tbexpiredays))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
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
                    .addComponent(jLabel73)
                    .addComponent(tbdefaultcont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74)
                    .addComponent(tbcontqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmtlcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel75)
                    .addComponent(tbexpiredays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel71)
                            .addComponent(ddrouting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadd)
                            .addComponent(btupdate)
                            .addComponent(btdelete)
                            .addComponent(btprintlabel)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbmrp)
                            .addComponent(cbplan))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbschedule)
                            .addComponent(cbphantom))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel1.add(jPanel4);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Location Quantities"));
        jPanel6.setName("panellocqty"); // NOI18N

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
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Recent Inventory Transactions"));
        jPanel7.setName("paneltrans"); // NOI18N

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
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
        jPanel8.setName("panelcost"); // NOI18N

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
        btcurrent.setName("btcurrent"); // NOI18N
        btcurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcurrentActionPerformed(evt);
            }
        });

        jLabel1.setText("Material");
        jLabel1.setName("lblmat"); // NOI18N

        jLabel2.setText("Labor");
        jLabel2.setName("lbllbr"); // NOI18N

        jLabel3.setText("Burden");
        jLabel3.setName("lblbdn"); // NOI18N

        jLabel4.setText("Overhead");
        jLabel4.setName("lblovh"); // NOI18N

        jLabel5.setText("Outside");
        jLabel5.setName("lblout"); // NOI18N

        jLabel6.setText("TotalCost");
        jLabel6.setName("lbltotal"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        jLabel7.setText("Standard");
        jLabel7.setName("lblstandard"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Cantarell", 1, 18)); // NOI18N
        jLabel8.setText("Current");
        jLabel8.setName("lblcurrent"); // NOI18N

        btstandard.setText("Standard");
        btstandard.setName("btstandard"); // NOI18N
        btstandard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btstandardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addComponent(tblbrstd)
                            .addComponent(tbbdnstd)
                            .addComponent(tbovhstd)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(tbmtlstd, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btstandard)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblbrcur)
                    .addComponent(jLabel8)
                    .addComponent(tboutcur)
                    .addComponent(tbbdncur)
                    .addComponent(tbovhcur)
                    .addComponent(tbtotcostcur)
                    .addComponent(btcurrent)
                    .addComponent(tbmtlcur, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btcurrent)
                    .addComponent(btstandard))
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

        jTree1.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM"));
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
        btaddimage.setName("btaddimage"); // NOI18N
        btaddimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddimageActionPerformed(evt);
            }
        });

        btdeleteimage.setText("Delete Image");
        btdeleteimage.setName("btdeleteimage"); // NOI18N
        btdeleteimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteimageActionPerformed(evt);
            }
        });

        cbdefault.setText("Default?");
        cbdefault.setName("cbdefault"); // NOI18N
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
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btcurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcurrentActionPerformed
       getcurrentcost(tbkey.getText());
    }//GEN-LAST:event_btcurrentActionPerformed

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
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        
        if (ddimage.getSelectedItem() == null || ddimage.getSelectedItem().toString().isEmpty()) {
            return;
        }
        
        if (proceed) {
        try {

         Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
              
                   int i = st.executeUpdate("delete from item_image where iti_item = " + "'" + tbkey.getText() + "'" 
                           + " AND iti_file = " + "'" + ddimage.getSelectedItem().toString() + "'"
                           + " ;");
                    if (i > 0) {
                        String ImageDir = OVData.getSystemImageDirectory();
                        java.nio.file.Files.deleteIfExists(new File(ImageDir + ddimage.getSelectedItem().toString()).toPath());
                        getItemImages(tbkey.getText());
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;  
            try {
               
                int defaultvalue = BlueSeerUtils.boolToInt(cbdefault.isSelected());
              
                   int i = st.executeUpdate("update item_image set iti_default = " + "'" + defaultvalue + "'" + 
                           " where iti_item = " + "'" + tbkey.getText() + "'" 
                           + " AND iti_file = " + "'" + ddimage.getSelectedItem().toString() + "'"
                           + " ;");
                  
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                } finally {
                    if (res != null) {
                        res.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_cbdefaultActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
          if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void tbsellpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsellpriceFocusLost
        if (! tbsellprice.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbsellprice.getText(), "5");
            if (x.equals("error")) {
                tbsellprice.setText("");
                tbsellprice.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbsellprice.requestFocus();
            } else {
                tbsellprice.setText(x);
                tbsellprice.setBackground(Color.white);
            }
        } else {
             tbsellprice.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbsellpriceFocusLost

    private void tbsellpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsellpriceFocusGained
        if (tbsellprice.getText().equals("0")) {
            tbsellprice.setText("");
        }
    }//GEN-LAST:event_tbsellpriceFocusGained

    private void tbpurchpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpurchpriceFocusLost
        if (! tbpurchprice.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbpurchprice.getText(), "5");
            if (x.equals("error")) {
                tbpurchprice.setText("");
                tbpurchprice.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbpurchprice.requestFocus();
            } else {
                tbpurchprice.setText(x);
                tbmtlcost.setText(x);
                tbpurchprice.setBackground(Color.white);
            }
        } else {
             tbpurchprice.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbpurchpriceFocusLost

    private void tbpurchpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpurchpriceFocusGained
        if (tbpurchprice.getText().equals("0")) {
            tbpurchprice.setText("");
        }
    }//GEN-LAST:event_tbpurchpriceFocusGained

    private void tbmtlcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbmtlcostFocusLost
        if (! tbmtlcost.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbmtlcost.getText(), "5");
            if (x.equals("error")) {
                tbmtlcost.setText("");
                tbmtlcost.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbmtlcost.requestFocus();
            } else {
                tbmtlcost.setText(x);
                tbmtlcost.setBackground(Color.white);
            }
        } else {
             tbmtlcost.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbmtlcostFocusLost

    private void tboutcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tboutcostFocusLost
        if (! tboutcost.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tboutcost.getText(), "5");
        if (x.equals("error")) {
            tboutcost.setText("");
            tboutcost.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tboutcost.requestFocus();
        } else {
            tboutcost.setText(x);
            tboutcost.setBackground(Color.white);
        }
        }
    }//GEN-LAST:event_tboutcostFocusLost

    private void tbovhcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbovhcostFocusLost
        if (! tbovhcost.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbovhcost.getText(), "5");
            if (x.equals("error")) {
                tbovhcost.setText("");
                tbovhcost.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbovhcost.requestFocus();
            } else {
                tbovhcost.setText(x);
                tbovhcost.setBackground(Color.white);
            }
        } else {
             tbovhcost.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbovhcostFocusLost

    private void tbnetwtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbnetwtFocusLost
        if (! tbnetwt.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbnetwt.getText(), "3");
            if (x.equals("error")) {
                tbnetwt.setText("");
                tbnetwt.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbnetwt.requestFocus();
            } else {
                tbnetwt.setText(x);
                tbnetwt.setBackground(Color.white);
            }
        } else {
             tbnetwt.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbnetwtFocusLost

    private void tbshipwtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbshipwtFocusLost
        if (! tbshipwt.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbshipwt.getText(), "3");
            if (x.equals("error")) {
                tbshipwt.setText("");
                tbshipwt.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbshipwt.requestFocus();
            } else {
                tbshipwt.setText(x);
                tbshipwt.setBackground(Color.white);
            }
        } else {
             tbshipwt.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbshipwtFocusLost

    private void tbleadtimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbleadtimeFocusLost
        if (! tbleadtime.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbleadtime.getText(), "0");
            if (x.equals("error")) {
                tbleadtime.setText("");
                tbleadtime.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbleadtime.requestFocus();
            } else {
                tbleadtime.setText(x);
                tbleadtime.setBackground(Color.white);
            }
        } else {
             tbleadtime.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbleadtimeFocusLost

    private void tbsafestockFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsafestockFocusLost
       if (! tbsafestock.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbsafestock.getText(), "0");
            if (x.equals("error")) {
                tbsafestock.setText("");
                tbsafestock.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbsafestock.requestFocus();
            } else {
                tbsafestock.setText(x);
                tbsafestock.setBackground(Color.white);
            }
       } else {
             tbsafestock.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbsafestockFocusLost

    private void tbminordqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbminordqtyFocusLost
        if (! tbminordqty.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbminordqty.getText(), "0");
            if (x.equals("error")) {
                tbminordqty.setText("");
                tbminordqty.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbminordqty.requestFocus();
            } else {
                tbminordqty.setText(x);
                tbminordqty.setBackground(Color.white);
            }
        } else {
             tbminordqty.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbminordqtyFocusLost

    private void btprintlabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintlabelActionPerformed
        String printer = OVData.getDefaultLabelPrinter();
        if (OVData.isValidPrinter(printer)) {
            try {
                OVData.printLabelStream(tbkey.getText(), printer);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (PrintException ex) {
                ex.printStackTrace();
            } 
        } else {
            bsmf.MainFrame.show(getMessageTag(1139));
        }
    }//GEN-LAST:event_btprintlabelActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btstandardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btstandardActionPerformed
        getstandardcost(tbkey.getText());
    }//GEN-LAST:event_btstandardActionPerformed


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
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprintlabel;
    private javax.swing.JButton btstandard;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbdefault;
    private javax.swing.JCheckBox cbmrp;
    private javax.swing.JCheckBox cbphantom;
    private javax.swing.JCheckBox cbplan;
    private javax.swing.JCheckBox cbschedule;
    private javax.swing.JTextArea comments;
    private javax.swing.JTextField custrevlevel;
    private com.toedter.calendar.JDateChooser dcexpire;
    private javax.swing.JComboBox ddcode;
    private javax.swing.JComboBox<String> ddimage;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox ddprodcode;
    private javax.swing.JComboBox<String> ddrouting;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox dduom;
    private javax.swing.JComboBox ddwh;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel imagelabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JTextField tbcontqty;
    private javax.swing.JTextField tbcreatedate;
    private javax.swing.JTextField tbdefaultcont;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbdrawing;
    private javax.swing.JTextField tbexpiredays;
    private javax.swing.JTextField tbgroup;
    private static javax.swing.JTextField tbkey;
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
    private javax.swing.JTextField tbsafestock;
    private javax.swing.JTextField tbsellprice;
    private javax.swing.JTextField tbshipwt;
    private javax.swing.JTextField tbtotcostcur;
    private javax.swing.JTextField tbtotcoststd;
    // End of variables declaration//GEN-END:variables
}
