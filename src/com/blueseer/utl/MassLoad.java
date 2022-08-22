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

package com.blueseer.utl;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import static com.blueseer.ctr.cusData.addCustMstrMass;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.addItemMasterMass;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.OVData.createTestDataPO;
import static com.blueseer.utl.OVData.createTestDataSO;
import static com.blueseer.utl.OVData.createTestDataTC;
import com.blueseer.vdr.venData;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author vaughnte
 */
public class MassLoad extends javax.swing.JPanel {

     // global variable declarations
                boolean isLoad = false;
    /**
     * Creates new form FileOrderLoadPanel
     */
    public MassLoad() {
        initComponents();
        setLanguageTags(this);
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
        tacomments.setText("");
        tbdelimiter.setText(":");
       isLoad = false;
       
    }
    
    public void setState() {
        setPanelComponentState(this, true);
    }
    
    public void executeTask(String x, File y, String type) { 
      
        class Task extends SwingWorker<String[], Void> { 
       
          String type = "";
          String[] key = null;
          
        
          public Task(String x, File y, String type) { 
              this.type = type;
          }
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            if (type.equals("testdata")) {
            message = loadTestData();    
            } else {
            message = processfile(x, y);
            }
            return message;
        }
      
        
       public void done() {
            try {
            String[] message = get();
            if (message[0] == null) {
                message[0] = "1"; // cancel upload
            }
            BlueSeerUtils.endTask(message);
            setState(); 
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x,y,type); 
       z.execute();
       
       
    }
   
    
    public void initvars(String[] arg) {
      if (! ddtable.getItemAt(0).equals("")) {
          ddtable.insertItemAt("", 0);
      }  
      tbdelimiter.setText(":");
      tacomments.setText("");
    }
    
    
    // Item master stuff
    public ArrayList<String> defineItemMaster() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("it_item,s,36,mandatory,unvalidated");
        list.add("it_desc,s,50,optional,unvalidated");
        list.add("it_site,s,10,mandatory,validated");
        list.add("it_code,s,10,mandatory,validated (P or M or A)");
        list.add("it_prodline,s,4,mandatory,validated");
        list.add("it_loc,s,20,optional,validated");
        list.add("it_wh,s,20,optional,validated");
        list.add("it_lotsize,i,11,optional,unvalidated");
        list.add("it_sell_price,d,14,optional,unvalidated");
        list.add("it_pur_price,d,14,optional,unvalidated");
        list.add("it_mtl_cost,d,14,optional,unvalidated");
        list.add("it_ovh_cost,d,14,optional,unvalidated");
        list.add("it_out_cost,d,14,optional,unvalidated");
        list.add("it_type,s,20,optional,unvalidated");
        list.add("it_group,s,30,optional,unvalidated");
        list.add("it_drawing,s,30,optional,unvalidated");
        list.add("it_rev,s,30,optional,unvalidated");
        list.add("it_custrev,s,30,optional,unvalidated");
        list.add("it_comments,s,500,optional,unvalidated");
        list.add("it_uom,s,10,mandatory,validated");
        list.add("it_net_wt,d,10,optional,unvalidated");
        list.add("it_ship_wt,d,10,optional,unvalidated");
        list.add("it_leadtime,i,11,optional,unvalidated");
        list.add("it_safestock,i,8,optional,unvalidated");
        list.add("it_minordqty,i,8,optional,unvalidated");
        list.add("it_mrp,b,1,mandatory,validated (1 or 0)");
        list.add("it_sched,b,1,mandatory,validated (1 or 0)");
        list.add("it_plan,b,1,mandatory,validated (1 or 0)");
        list.add("it_wf,s,30,optional,validated");
        return list;
    }
    
    public boolean checkItemMaster(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineItemMaster();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
         if (rs.length == list.size()) {
            
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_site") == 0 && ! OVData.isValidSite(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid site" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_loc") == 0 && ! OVData.isValidLocation(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid location" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_wh") == 0 && ! OVData.isValidWarehouse(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid warehouse" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_prodline") == 0 && ! OVData.isValidProdLine(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid prodline" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_wf") == 0 && ! rs[j].isEmpty() && ! OVData.isValidRouting(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid routing code" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_code") == 0) {
                    if (rs[j].toUpperCase().compareTo("M") != 0 && rs[j].toUpperCase().compareTo("P") != 0 && rs[j].toUpperCase().compareTo("A") != 0) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " it_code must be either M or P or A " + "\n" );
                       proceed = false;
                    }
                }
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processItemMaster (File myfile) throws FileNotFoundException, IOException {
            String[] m = new String[2];
            
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("Item Master") == 0) {
                   temp = checkItemMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
             if (proceed) {
                   if(! addItemMasterMass(list)) {
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151, String.valueOf(i))};
                   } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
             }
             return m;
    }
    
     // BOM master stuff
    public ArrayList<String> defineBOMMaster() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("bom_id,s,36,mandatory,unvalidated");
        list.add("bom_desc,s,100,mandatory,unvalidated");
        list.add("bom_item,s,36,mandatory,validated");
        list.add("bom_enabled,b,1,mandatory,unvalidated");
        list.add("bom_primary,b,1,mandatory,unvalidated");
        list.add("ps_parent,s,36,mandatory,validated");
        list.add("ps_child,s,36,mandatory,validated");
        list.add("ps_type,s,10,mandatory,validated (P or M or A)");
        list.add("ps_qty_per,d,10,mandatory,unvalidated");
        list.add("ps_desc,s,30,mandatory,unvalidated");
        list.add("ps_op,s,3,mandatory,validated");
        list.add("ps_sequence,i,4,optional,unvalidated");
        list.add("ps_userid,s,8,optional,unvalidated");
        list.add("ps_misc1,s,30,optional,unvalidated");
        list.add("ps_ref,s,30,optional,unvalidated");
        list.add("ps_bom,s,36,optional,unvalidated");
        
        return list;
    }
    
    public boolean checkBOMMaster(String[] rs, int i) {
        boolean proceed = true;
        
       ArrayList<String> list = defineBOMMaster();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
       
        if (rs.length == list.size()) {
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer (set 0 if unknown)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processBOMMaster (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2];  
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkBOMMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addBOMMstrRecord(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
           
    }
    
    
       // GL Account Balances stuff
    public ArrayList<String> defineGLAcctBalances() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("acb_acct,s,10,mandatory,validated");
        list.add("acb_cc,s,4,mandatory,validated");
        list.add("acb_site,s,10,mandatory,validated");
        list.add("acb_amt,d,14,mandatory,validated");
        return list;
    }
    
    public boolean checkGLAcctBalances(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineGLAcctBalances();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
               
                if (ld[0].compareTo("acb_acct") == 0 && ! OVData.isValidGLAcct(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid account code" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("acb_cc") == 0 && ! OVData.isValidGLcc(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid cost center / dept" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("acb_site") == 0 && ! OVData.isValidSite(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Site" + "\n" );
                       proceed = false;
                }
               
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processGLAcctBalances (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("GL Account Balances") == 0) {
                   temp = checkGLAcctBalances(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addGLAcctBalances(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
    }
    
    
      // Generic Code stuff
    public ArrayList<String> defineGenericCode() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("code_code,s,30,mandatory,unvalidated");
        list.add("code_key,s,30,mandatory,unvalidated(field dependent)");
        list.add("code_value,s,50,optional,unvalidated");
        return list;
    }
    
    public boolean checkGenericCode(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineGenericCode();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
               
               
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processGenericCode (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("Generic Code") == 0) {
                   temp = checkGenericCode(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
            if (proceed) {
                   if(! OVData.addGenericCode(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    
    
    // cust xref stuff
    public ArrayList<String> defineCustXref() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cup_cust,s,10,mandatory,validated");
        list.add("cup_item,s,36,mandatory,validated");
        list.add("cup_citem,s,50,mandatory,unvalidated");
        list.add("cup_citem2,s,50,optional,unvalidated");
        list.add("cup_upc,s,50,optional,unvalidated");
        list.add("cup_sku,s,50,optional,unvalidated");
        list.add("cup_misc,s,50,optional,unvalidated");
        return list;
    }
    
    public boolean checkCustXref(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCustXref();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cup_cust") == 0 && ! OVData.isValidCustomer(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid customer" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cup_item") == 0 && ! OVData.isValidItem(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processCustXref (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2];
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkCustXref(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! OVData.addCustXref(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
   
      // Carrier stuff
    public ArrayList<String> defineCarrier() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("car_code,s,30,mandatory,validated");
        list.add("car_desc,s,50,optional,unvalidated");
        list.add("car_scac,s,10,optional,unvalidated");
        list.add("car_phone,s,15,optional,unvalidated");
        list.add("car_email,s,100,optional,unvalidated");
        list.add("car_contact,s,100,optional,unvalidated");
        list.add("car_type,s,10,mandatory,validated");
        list.add("car_acct,s,20,optional,unvalidated");
        return list;
    }
    
    public boolean checkCarrier(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCarrier();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                j++;
            } 
        }
        
        
        return proceed;
    }
    
    public String[] processCarrier(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkCarrier(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! OVData.addCarrier(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    
     // EDI Partners
    public ArrayList<String> defineEDIPartners() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("edp_id,s,15,mandatory,unvalidated");
        list.add("edp_desc,s,50,mandatory,unvalidated");
        list.add("edpd_alias,s,15,mandatory,unvalidated");
        list.add("edpd_default,b,1,mandatory,unvalidated");
        return list;
    }
    
    public boolean checkEDIPartners(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineEDIPartners();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                j++;
            } 
        }
        
        
        return proceed;
    }
    
    public String[] processEDIPartners(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkEDIPartners(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! EDData.addEDIPartner(list))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
           
    }
    
     // EDI Document Structures
    public ArrayList<String> defineEDIDocumentStructures() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("edd_id,s,30,mandatory,unvalidated");
        list.add("edd_desc,s,200,mandatory,unvalidated");
        list.add("edd_type,s,50,mandatory,unvalidated");
        list.add("edd_subtype,s,50,mandatory,unvalidated");
        list.add("edd_segdelim,i,3,mandatory,unvalidated");
        list.add("edd_priority,i,3,mandatory,unvalidated");
        list.add("edd_landmark,s,50,mandatory,unvalidated");
        list.add("edd_enabled,b,1,mandatory,unvalidated");
        list.add("edid_role,s,20,mandatory,unvalidated");
        list.add("edid_rectype,s,10,mandatory,unvalidated");
        list.add("edid_valuetype,s,10,mandatory,unvalidated");
        list.add("edid_row,i,4,mandatory,unvalidated");
        list.add("edid_col,i,4,mandatory,unvalidated");
        list.add("edid_length,i,4,mandatory,unvalidated");
        list.add("edid_regex,s,100,mandatory,unvalidated");
        list.add("edid_value,s,100,mandatory,unvalidated");
        list.add("edid_tag,s,100,mandatory,unvalidated");
        list.add("edid_enabled,b,1,mandatory,unvalidated");
        
        return list;
    }
    
    public boolean checkEDIDocumentStructures(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineEDIDocumentStructures();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                j++;
            } 
        }
        
        
        return proceed;
    }
    
    public String[] processEDIDocumentStructures(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkEDIDocumentStructures(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! EDData.addEDIDocumentStructures(list))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    
    
  
     // EDI Partner stuff
    public ArrayList<String> defineEDIPartnerTransactions() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("edi_id,s,30,mandatory,unvalidated");
        list.add("edi_doc,s,10,mandatory,unvalidated");
        list.add("edi_sndisa,s,15,mandatory,unvalidated");
        list.add("edi_map,s,30,mandatory,unvalidated");
        list.add("edi_sndisaq,s,2,mandatory,unvalidated");
        list.add("edi_sndgs,s,15,mandatory,unvalidated");
        list.add("edi_rcvisa,s,15,mandatory,unvalidated");
        list.add("edi_rcvq,s,2,mandatory,unvalidated");
        list.add("edi_rcvgs,s,15,mandatory,unvalidated");
        list.add("edi_eledelim,i,3,mandatory,unvalidated");
        list.add("edi_segdelim,i,3,mandatory,unvalidated");
        list.add("edi_subdelim,i,3,mandatory,unvalidated");
        list.add("edi_fileprefix,s,30,mandatory,unvalidated");
        list.add("edi_filesuffix,s,30,mandatory,unvalidated");
        list.add("edi_filepath,s,70,mandatory,unvalidated");
        list.add("edi_version,s,10,mandatory,unvalidated");
        list.add("edi_supcode,s,30,mandatory,unvalidated");
        list.add("edi_doctypeout,s,10,mandatory,unvalidated");
        list.add("edi_filetypeout,s,10,mandatory,unvalidated");
        list.add("edi_ifs,s,50,mandatory,unvalidated");
        list.add("edi_ofs,s,50,mandatory,unvalidated");
        list.add("edi_fa_required,b,1,mandatory,unvalidated");
        return list;
    }
    
    public boolean checkEDIPartnerTransactions(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineEDIPartnerTransactions();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                j++;
            } 
        }
        
        
        return proceed;
    }
    
    public String[] processEDIPartnerTransactions (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkEDIPartnerTransactions(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! EDData.addEDIMstrRecord(list))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    
    
     // vend xref stuff
    public ArrayList<String> defineVendXref() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vdp_vend,s,10,mandatory,validated");
        list.add("vdp_item,s,36,mandatory,validated");
        list.add("vdp_vitem,s,50,mandatory,unvalidated");
        list.add("vdp_upc,s,50,optional,unvalidated");
        list.add("vdp_sku,s,50,optional,unvalidated");
        list.add("vdp_misc,s,50,optional,unvalidated");
        return list;
    }
    
    public boolean checkVendXref(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCustXref();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vdp_vend") == 0 && ! OVData.isValidVendor(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid customer" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vdp_item") == 0 && ! OVData.isValidItem(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processVendXref (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkVendXref(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
           fsr.close();
            if (proceed) {
                   if(! OVData.addVendXref(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    
      // Inventory adjustments
    public ArrayList<String> defineInvAdjustment() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("type,s,7,mandatory,validated");
        list.add("item,s,20,mandatory,validated");
        list.add("site,s,10,mandatory,validated");
        list.add("location,s,30,mandatory,validated");
        list.add("account,s,10,mandatory,validated");
        list.add("costcenter,s,4,mandatory,validated");
        list.add("quantity,i,9,mandatory,validated");
        list.add("effdate,s,10,mandatory,validated");
        list.add("serial,s,30,optional,unvalidated");
        list.add("reference,s,30,mandatory,unvalidated");
        list.add("remarks,s,50,optional,unvalidated");
        list.add("warehouse,s,10,mandatory,validated");
        list.add("expire,s,10,optional,unvalidated");
        return list;
    }
    
    public boolean checkInvAdjustment(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineInvAdjustment();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("site") == 0 && ! OVData.isValidSite(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid site" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("item") == 0 && ! OVData.isValidItem(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("item") == 0 && (invData.getItemCost(rs[j], "standard", rs[j+1]) == 0) ) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item cost" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("account") == 0 && ! OVData.isValidGLAcct(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid account" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("costcenter") == 0 && ! OVData.isValidGLcc(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid cost center" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("effdate") == 0 && ! BlueSeerUtils.isValidDateStr(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid date string yyyy-mm-dd" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("type") == 0 && ! ("receipt".equals(rs[j]) || "issue".equals(rs[j]))) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid type 'issue' or 'receipt'" + "\n" );
                       proceed = false;
                }
                
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processInvAdjustment (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkInvAdjustment(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! OVData.addInvAdjustments(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
             
    }
    
    
     // cust price list stuff
    public ArrayList<String> defineCustPriceList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cpr_cust,s,10,mandatory,validated");
        list.add("cpr_item,s,36,mandatory,validated");
        list.add("cpr_desc,s,50,optional,unvalidated");
        list.add("cpr_type,s,20,mandatory,validated");
        list.add("cpr_price,d,12,optional,unvalidated");
        list.add("cpr_volqty,d,12,optional,unvalidated");
        list.add("cpr_volprice,d,12,optional,unvalidated");
        list.add("cpr_disc,d,12,optional,unvalidated");
        list.add("cpr_uom,s,3,mandatory,unvalidated");
        list.add("cpr_curr,s,3,mandatory,unvalidated");
        return list;
    }
    
    public boolean checkCustPriceList(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCustPriceList();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cpr_cust") == 0 && ! OVData.isValidCustomer(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid customer" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cpr_item") == 0 && ! OVData.isValidItem(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processCustPriceList (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkCustPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addCustPriceList(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
             
    }
    
     // cust price list stuff
    public ArrayList<String> defineVendPriceList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vpr_vend,s,30,mandatory,validated");
        list.add("vpr_item,s,36,mandatory,validated");
        list.add("vpr_desc,s,30,optional,unvalidated");
        list.add("vpr_type,s,20,mandatory,validated");
        list.add("vpr_price,d,12,optional,unvalidated");
        list.add("vpr_volqty,d,12,optional,unvalidated");
        list.add("vpr_volprice,d,12,optional,unvalidated");
        list.add("vpr_disc,d,12,optional,unvalidated");
        return list;
    }
    
    public boolean checkVendPriceList(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineVendPriceList();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cpr_cust") == 0 && ! OVData.isValidCustomer(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid customer" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cpr_item") == 0 && ! OVData.isValidItem(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid item" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processVendPriceList (File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkVendPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addVendPriceList(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
      // vendor Master
    public ArrayList<String> defineVendMstr() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vd_addr,s,30,mandatory,unvalidated");
        list.add("vd_site,s,10,mandatory,unvalidated");
        list.add("vd_name,s,30,optional,unvalidated");
        list.add("vd_line1,s,30,optional,unvalidated");
        list.add("vd_line2,s,30,optional,unvalidated");
        list.add("vd_line3,s,30,optional,unvalidated");
        list.add("vd_city,s,30,optional,unvalidated");
        list.add("vd_state,s,2,optional,validated");
        list.add("vd_zip,s,10,optional,unvalidated");
        list.add("vd_country,s,30,optional,validated");
        list.add("vd_group,s,30,optional,unvalidated");
        list.add("vd_market,s,30,optional,unvalidated");
        list.add("vd_buyer,s,30,optional,unvalidated");
        list.add("vd_terms,s,30,mandatory,validated");
        list.add("vd_shipvia,s,30,mandatory,validated");
        list.add("vd_price_code,s,30,optional,unvalidated");
        list.add("vd_disc_code,s,30,optional,unvalidated");
        list.add("vd_tax_code,s,30,optional,unvalidated");
        list.add("vd_ap_acct,s,10,mandatory,validated");
        list.add("vd_ap_cc,s,4,mandatory,validated");
        list.add("vd_remarks,s,300,optional,unvalidated");
        list.add("vd_freight_type,s,30,optional,unvalidated");
        list.add("vd_bank,s,30,mandatory,validated");
        list.add("vd_curr,s,3,mandatory,validated");
        list.add("vd_misc,s,50,optional,unvalidated");
        list.add("vd_phone,s,15,optional,unvalidated");
        list.add("vd_email,s,60,optional,unvalidated");
        list.add("vd_is850export,i,1,optional,unvalidated");
        return list;
    }
    
    public boolean checkVendMstr(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineVendMstr();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vd_terms") == 0 && ! OVData.isValidTerms(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Terms Code" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vd_ap_acct") == 0 && ! OVData.isValidGLAcct(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Account" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vd_ap_cc") == 0 && ! OVData.isValidGLcc(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid CC" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vd_bank") == 0 && ! OVData.isValidBank(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Bank" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("vd_curr") == 0 && ! OVData.isValidCurrency(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Currency" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processVendMstr(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkVendMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if(! venData.addVendMstrMass(list))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
        // customer Master
    public ArrayList<String> defineCustMstr() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cm_code,s,10,mandatory,unvalidated");
        list.add("cm_site,s,10,optional,unvalidated");
        list.add("cm_name,s,30,optional,unvalidated");
        list.add("cm_line1,s,30,optional,unvalidated");
        list.add("cm_line2,s,30,optional,unvalidated");
        list.add("cm_line3,s,30,optional,unvalidated");
        list.add("cm_city,s,30,optional,unvalidated");
        list.add("cm_state,s,2,mandatory,validated");
        list.add("cm_zip,s,10,optional,unvalidated");
        list.add("cm_country,s,50,mandatory,validated");
        list.add("cm_group,s,50,optional,unvalidated");
        list.add("cm_market,s,50,optional,unvalidated");
        list.add("cm_creditlimit,i,8,optional,validated");
        list.add("cm_onhold,i,1,optional,validated");
        list.add("cm_carrier,s,50,optional,unvalidated");
        list.add("cm_terms,s,30,mandatory,validated");
        list.add("cm_freight_type,s,30,optional,unvalidated");
        list.add("cm_price_code,s,10,optional,unvalidated");
        list.add("cm_disc_code,s,10,optional,unvalidated");
        list.add("cm_tax_code,s,10,optional,unvalidated");
        list.add("cm_salesperson,s,50,optional,unvalidated");
        list.add("cm_ar_acct,s,10,mandatory,validated");
        list.add("cm_ar_cc,s,4,mandatory,validated");
        list.add("cm_remarks,s,500,optional,unvalidated");
        list.add("cm_misc1,s,100,optional,unvalidated");
        list.add("cm_bank,s,10,mandatory,validated");
        list.add("cm_curr,s,3,mandatory,validated");
        list.add("cm_logo,s,100,optional,unvalidated");
        list.add("cm_ps_jasper,s,100,optional,unvalidated");
        list.add("cm_iv_jasper,s,100,optional,unvalidated");
        list.add("cm_label,s,100,optional,unvalidated");
        list.add("cm_phone,s,15,optional,unvalidated");
        list.add("cm_email,s,60,optional,unvalidated");
        list.add("cm_is855export,i,1,optional,unvalidated");
        list.add("cm_is856export,i,1,optional,unvalidated");
        list.add("cm_is810export,i,1,optional,unvalidated");
        return list;
    }
    
    public boolean checkCustMstr(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCustMstr();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cm_terms") == 0 && ! OVData.isValidTerms(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Terms Code" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cm_ar_acct") == 0 && ! OVData.isValidGLAcct(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Account" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cm_ar_cc") == 0 && ! OVData.isValidGLcc(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid CC" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cm_bank") == 0 && ! OVData.isValidBank(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Bank" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cm_curr") == 0 && ! OVData.isValidCurrency(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Currency" + "\n" );
                       proceed = false;
                }
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processCustMstr(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkCustMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if(! addCustMstrMass(list))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
            
    }
    
         // customer shipto
    public ArrayList<String> defineCustShipToMstr() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cms_code,s,10,mandatory,validated");
        list.add("cms_shipto,s,10,mandatory,unvalidated");
        list.add("cms_name,s,50,optional,unvalidated");
        list.add("cms_line1,s,50,optional,unvalidated");
        list.add("cms_line2,s,50,optional,unvalidated");
        list.add("cms_line3,s,50,optional,unvalidated");
        list.add("cms_city,s,50,optional,unvalidated");
        list.add("cms_state,s,2,mandatory,validated");
        list.add("cms_zip,s,10,optional,unvalidated");
        list.add("cms_country,s,50,mandatory,validated");
        list.add("cms_plantcode,s,30,optional,unvalidated");
        list.add("cms_contact,s,50,optional,unvalidated");
        list.add("cms_phone,s,15,optional,unvalidated");
        list.add("cms_email,s,100,optional,unvalidated");
        list.add("cms_misc,s,50,optional,unvalidated");
        return list;
    }
    
    public boolean checkCustShipToMstr(String[] rs, int i) {
        boolean proceed = true;
        ArrayList<String> list = defineCustShipToMstr();
        // first check for correct number of fields
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + " ...should have " + String.valueOf(list.size()) + " fields \n" );
                   proceed = false;
        }
        
        
        if (rs.length == list.size()) {
            // now check individual fields
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("cms_code") == 0 && ! OVData.isValidCustomer(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid Customer BillTo Code" + "\n" );
                       proceed = false;
                }
               
                             // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processCustShipToMstr(File myfile) throws FileNotFoundException, IOException {
        String[] m = new String[2]; 
        
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               temp = checkCustShipToMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               
            }
            fsr.close();
            if (proceed) {
                   if(! OVData.addCustShipToMstr(list, tbdelimiter.getText().trim()))
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151,String.valueOf(i))};
                   } else {
                  m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
            }
             return m;
            
    }
    
    // Routing master stuff
    public ArrayList<String> defineRoutingMaster() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("wf_id,s,30,mandatory,unvalidated");
        list.add("wf_desc,s,100,optional,unvalidated");
        list.add("wf_site,s,10,mandatory,validated");
        list.add("wf_op,i,8,mandatory,unvalidated");
        list.add("wf_assert,b,1,mandatory,unvalidated");
        list.add("wf_op_desc,s,100,optional,unvalidated");
        list.add("wf_cell,s,20,mandator,validated");
        list.add("wf_setup_hours,d,13,optional,unvalidated");
        list.add("wf_run_hours,d,13,optional,unvalidated");
        return list;
    }
    
    public boolean checkRoutingMaster(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineRoutingMaster();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
         if (rs.length == list.size()) {
            
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_site") == 0 && ! OVData.isValidSite(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid site" + "\n" );
                       proceed = false;
                }
               
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processRoutingMaster (File myfile) throws FileNotFoundException, IOException {
            String[] m = new String[2];
            
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("Routing Master") == 0) {
                   temp = checkRoutingMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addRoutingMaster(list, tbdelimiter.getText().trim())) { 
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151, String.valueOf(i))};
                   } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
             }
             return m;
    }
    
    // WorkCenter master stuff
    public ArrayList<String> defineWorkCenterMaster() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("wc_cell,s,20,mandatory,unvalidated");
        list.add("wc_desc,s,100,optional,unvalidated");
        list.add("wc_site,s,10,mandatory,validated");
        list.add("wc_cc,s,10,mandatory,unvalidated");
        list.add("wc_run_rate,d,5,mandatory,unvalidated");
        list.add("wc_run_crew,d,5,mandatory,unvalidated");
        list.add("wc_setup_rate,d,5,mandatory,unvalidated");
        list.add("wc_setup,d,5,mandatory,unvalidated");
        list.add("wc_bdn_rate,d,5,mandatory,unvalidated");
        list.add("wc_remarks,s,50,optional,unvalidated");
        return list;
    }
    
    public boolean checkWorkCenterMaster(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineWorkCenterMaster();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
         if (rs.length == list.size()) {
            
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
                if (ld[0].compareTo("it_site") == 0 && ! OVData.isValidSite(rs[j]) && ! cboverride.isSelected()) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be valid site" + "\n" );
                       proceed = false;
                }
               
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
    
    public String[] processWorkCenterMaster (File myfile) throws FileNotFoundException, IOException {
            String[] m = new String[2];
            
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("WorkCenter Master") == 0) {
                   temp = checkWorkCenterMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addWorkCenterMaster(list, tbdelimiter.getText().trim())) { 
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151, String.valueOf(i))};
                   } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
             }
             return m;
    }
    
    // Shopify - Order Load
    public ArrayList<String> defineShopifyOrderCSV() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Name,s,30,optional,unvalidated");
        list.add("OrderID,s,30,mandatory,unvalidated");
        list.add("Email,s,50,optional,unvalidated");
        list.add("Status,s,30,mandatory,validated");
        list.add("Currency,s,3,mandatory,validated");
        list.add("Discount,d,30,optional,unvalidated");
        list.add("Subtotal,d,30,optional,unvalidated");
        list.add("Shipping,d,30,optional,unvalidated");
        list.add("Taxes,d,30,optional,unvalidated");
        list.add("TaxesIncluded,b,1,optional,unvalidated");
        list.add("Total,d,30,mandatory,unvalidated");
        list.add("ShippingMethod,s,50,optional,unvalidated");
        list.add("CustomShipping,s,50,optional,unvalidated");
        list.add("TotalWeight,d,30,optional,unvalidated");
        list.add("CreateDate,s,30,optional,unvalidated");
        list.add("ItemQuantity,d,30,optional,unvalidated");
        list.add("ItemName,s,30,optional,unvalidated");
        list.add("ItemPrice,d,30,optional,unvalidated");
        list.add("ItemTotalDiscount,d,30,optional,unvalidated");
        list.add("ItemTotalPrice,d,30,optional,unvalidated");
        list.add("ItemSku,s,30,optional,unvalidated");
        list.add("ItemRequiresShipping,b,1,optional,unvalidated");
        list.add("ItemTaxable,b,1,optional,unvalidated");
        list.add("ItemVendor,s,30,optional,unvalidated");
        list.add("ItemProperties,s,30,optional,unvalidated");
        list.add("BillingName,s,30,optional,unvalidated");
        list.add("BillingStreet,s,30,optional,unvalidated");
        list.add("BillingAddress1,s,30,optional,unvalidated");
        list.add("BillingAddress2,s,30,optional,unvalidated");
        list.add("BillingCompany,s,30,optional,unvalidated");
        list.add("BillingCity,s,30,optional,unvalidated");
        list.add("BillingZip,s,30,optional,unvalidated");
        list.add("BillingProvince,s,30,optional,unvalidated");
        list.add("BillingCountry,s,30,optional,unvalidated");
        list.add("BillingPhone,s,30,optional,unvalidated");
        list.add("ShippingName,s,30,optional,unvalidated");
        list.add("ShippingStreet,s,30,optional,unvalidated");
        list.add("ShippingAddress1,s,30,optional,unvalidated");
        list.add("ShippingAddress2,s,30,optional,unvalidated");
        list.add("ShippingCompany,s,30,optional,unvalidated");
        list.add("ShippingCity,s,30,optional,unvalidated");
        list.add("ShippingZip,s,30,optional,unvalidated");
        list.add("ShippingProvince,s,30,optional,unvalidated");
        list.add("ShippingCountry,s,30,optional,unvalidated");
        list.add("ShippingPhone,s,30,optional,unvalidated");
        list.add("InvoiceSentDate,s,30,optional,unvalidated");
        list.add("CompletedAtDate,s,30,optional,unvalidated");
        list.add("OrderIDAux,s,30,optional,unvalidated");
        list.add("Notes,s,30,optional,unvalidated");
        list.add("NotesAttributes,s,30,optional,unvalidated");
        list.add("Tags,s,30,optional,unvalidated");
        list.add("Tax1Name,s,30,optional,unvalidated");
        list.add("Tax1Value,d,10,optional,unvalidated");
        list.add("Tax2Name,s,30,optional,unvalidated");
        list.add("Tax2Value,d,10,optional,unvalidated");
        list.add("Tax3Name,s,30,optional,unvalidated");
        list.add("Tax3Value,d,10,optional,unvalidated");
        list.add("Tax4Name,s,30,optional,unvalidated");
        list.add("Tax4Value,d,10,optional,unvalidated");
        list.add("Tax5Name,s,30,optional,unvalidated");
        list.add("Tax5Value,d,10,optional,unvalidated");
        list.add("BillingProvinceName,s,30,optional,unvalidated");
        list.add("ShippingProvinceName,s,30,optional,unvalidated");
        list.add("PaymentTermsName,s,30,optional,unvalidated");
        list.add("NextPaymentDueDate,s,30,optional,unvalidated");
        return list;
    }
    
    public boolean checkShopifyOrderCSV(String[] rs, int i) {
        boolean proceed = true;
        
        ArrayList<String> list = defineShopifyOrderCSV();
        if (rs.length != list.size()) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
         if (rs.length == list.size()) {
            
            String[] ld = null;
            int j = 0;
            for (String rec : list) {
            ld = rec.split(",", -1);
                if (rs[j].length() > Integer.valueOf(ld[2])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field length too long" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("i") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type integer" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("b") == 0 && ! BlueSeerUtils.isParsableToInt(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " must be integer 1 or 0...(true or false)" + "\n" );
                       proceed = false;
                }
                if (ld[1].compareTo("d") == 0 && ! BlueSeerUtils.isParsableToDouble(rs[j])) {
                    tacomments.append("line:field " + i + ":" + j + " " + String.valueOf(rs[j]) + " field must be of type double" + "\n" );
                       proceed = false;
                }
               
               
                // bsmf.MainFrame.show(rs[j] + " " + String.valueOf(proceed));
                j++;
                
               
            }
           
                   
        }
        
        
        return proceed;
    }
   
    public String[] processShopifyOrderCSV (File myfile) throws FileNotFoundException, IOException {
            String[] m = new String[2];
            
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("Order - Shopify") == 0) {
                   temp = checkShopifyOrderCSV(recs, i);
                   if (! temp) {
                       proceed = false;
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
               }
            }
            fsr.close();
             if (proceed) {
                   if(! OVData.addShopifyOrderCSV(list, tbdelimiter.getText().trim())) { 
                       m = new String[] {BlueSeerUtils.SuccessBit, getMessageTag(1151, String.valueOf(i))};
                   } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, getMessageTag(1150)}; 
                   }
             }
             return m;
    }
    
    
    
    public void auditOnly(File myfile) throws FileNotFoundException, IOException { 
            
        if (myfile == null) {
           return;
        }
        
        boolean proceed = true;
            boolean temp = true;
            tacomments.setText("");
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(tbdelimiter.getText().trim(), -1);
               if (ddtable.getSelectedItem().toString().compareTo("Item Master") == 0) {
                   temp = checkItemMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Routing Master") == 0) {
                   temp = checkRoutingMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("WorkCenter Master") == 0) {
                   temp = checkWorkCenterMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Master") == 0) {
                   temp = checkVendMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Master") == 0) {
                   temp = checkCustMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer ShipTo Master") == 0) {
                   temp = checkCustShipToMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Xref") == 0) {
                   temp = checkCustXref(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Price List") == 0) {
                   temp = checkCustPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Xref") == 0) {
                   temp = checkVendXref(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Inventory Adjustment") == 0) {
                   temp = checkInvAdjustment(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Price List") == 0) {
                   temp = checkVendPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("EDI Partners") == 0) {
                   temp = checkEDIPartners(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("EDI Document Structures") == 0) {
                   temp = checkEDIDocumentStructures(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("EDI Partner Transactions") == 0) {
                   temp = checkEDIPartnerTransactions(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
              if (ddtable.getSelectedItem().toString().compareTo("Carrier Master") == 0) {
                   temp = checkCarrier(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("BOM Master") == 0) {
                   temp = checkBOMMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
               if (ddtable.getSelectedItem().toString().compareTo("GL Account Balances") == 0) {
                   temp = checkGLAcctBalances(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
            }
            fsr.close();
            // now we should have a clean file....attempt to load
            if (proceed) {
                   bsmf.MainFrame.show(getMessageTag(1151));
               
            } else {
                bsmf.MainFrame.show(getMessageTag(1150));
            }
        
       
    }
    
     public String[] processfile(String x, File myfile) throws IOException { 
         String[] m = new String[2];
         if (myfile != null) {
               if (x.compareTo("Item Master") == 0) {
                 m = processItemMaster(myfile);
               }
               if (x.compareTo("Routing Master") == 0) {
                 m = processRoutingMaster(myfile);
               }
               if (x.compareTo("WorkCenter Master") == 0) {
                 m = processWorkCenterMaster(myfile);
               }
               if (x.compareTo("Customer Xref") == 0) {
                m =  processCustXref(myfile);
               }
               if (x.compareTo("Customer Price List") == 0) {
                m =  processCustPriceList(myfile);
               }
               if (x.compareTo("Vendor Xref") == 0) {
                m =  processVendXref(myfile);
               }
               if (x.compareTo("Vendor Price List") == 0) {
                 m = processVendPriceList(myfile);
               }
               if (x.compareTo("Vendor Master") == 0) {
                 m = processVendMstr(myfile);
               }
               if (x.compareTo("Customer Master") == 0) {
                 m = processCustMstr(myfile);
               }
               if (x.compareTo("Customer ShipTo Master") == 0) {
                 m = processCustShipToMstr(myfile);
               }
               if (x.compareTo("Inventory Adjustment") == 0) {
                 m = processInvAdjustment(myfile);
               }
               if (x.compareTo("EDI Partners") == 0) {
                 m = processEDIPartners(myfile);
               }
               if (x.compareTo("EDI Document Structures") == 0) {
                 m = processEDIDocumentStructures(myfile);
               }
               if (x.compareTo("EDI Partner Transactions") == 0) {
                 m = processEDIPartnerTransactions(myfile);
               }
               if (x.compareTo("Carrier Master") == 0) {
                 m = processCarrier(myfile);
               }
               if (x.compareTo("BOM Master") == 0) {
                 m = processBOMMaster(myfile);
               }
               if (x.compareTo("GL Account Balances") == 0) {
                 m = processGLAcctBalances(myfile);
               }
               if (x.compareTo("Order - Shopify") == 0) {
                 m = processShopifyOrderCSV(myfile);
               }
              
         }
         
                  
         return m; 
    }
    
    public File getfile() {
        
        File file = null;
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
    
    public void describeFile(String key) {
        ArrayList<String> list = new ArrayList();
        
        tacomments.setText("");
            tacomments.append("ALL FIELDS MUST BE DELIMITED BY A COLON ':' \n");
            tacomments.append("NOTE:  THE FIELD VALUES CANNOT CONTAIN A COLON! \n");
            tacomments.append("DEFINITION: Field, Data Type, Field Size \n" );
        
        if (key.compareTo("Item Master") == 0) { 
             list = defineItemMaster();
         }
        if (key.compareTo("Routing Master") == 0) { 
             list = defineRoutingMaster();
         }
        if (key.compareTo("WorkCenter Master") == 0) { 
             list = defineWorkCenterMaster();
         }
        if (key.compareTo("Generic Code") == 0) { 
             list = defineGenericCode();
         }
        if (key.compareTo("Inventory Adjustment") == 0) { 
             list = defineInvAdjustment();
         }
        if (key.compareTo("Customer Xref") == 0) { 
             list = defineCustXref();
         }
        if (key.compareTo("Customer Price List") == 0) { 
             list = defineCustPriceList();
         }
        if (key.compareTo("Vendor Xref") == 0) { 
             list = defineVendXref();
         }
        if (key.compareTo("Vendor Price List") == 0) { 
             list = defineVendPriceList();
         }
        if (key.compareTo("Vendor Master") == 0) { 
             list = defineVendMstr();
         }
        if (key.compareTo("Customer Master") == 0) { 
             list = defineCustMstr();
         }
        if (key.compareTo("Customer ShipTo Master") == 0) { 
             list = defineCustShipToMstr();
         }
         if (key.compareTo("Carrier Master") == 0) { 
             list = defineCarrier();
         }
         if (key.compareTo("BOM Master") == 0) { 
             list = defineBOMMaster();
         }
         if (key.compareTo("EDI Partners") == 0) { 
             list = defineEDIPartners();
        }
          if (key.compareTo("EDI Document Structures") == 0) { 
             list = defineEDIDocumentStructures();
        }
        if (key.compareTo("EDI Partner Transactions") == 0) { 
             list = defineEDIPartnerTransactions();
        }
        if (key.compareTo("GL Account Balances") == 0) { 
             list = defineGLAcctBalances();
        }
        if (key.compareTo("Order - Shopify") == 0) { 
             list = defineShopifyOrderCSV();
         }
       
        
        for (String rec : list) {
                tacomments.append(rec + "\n");
            }
            tacomments.setCaretPosition(0);
    }
    
    
    
    public String[] loadTestData() throws InterruptedException {
        
        // set ddtable to index 0 for testdata
        ddtable.setSelectedIndex(0);
        String[] m = new String[2];
           // String filename = "c:\\bs\\blueseer\\sf\\data\\sampledir\\workcenter.txt";
            Path filename = FileSystems.getDefault().getPath("data/sampledir/workcenter.txt");
            File file = new File(filename.toString());
             try {
                 tacomments.append("Creating work centers...\n");
                 Thread.sleep(1000);
                 processWorkCenterMaster(file);
                 
             } catch (IOException ex) {
                 bsmf.MainFrame.show(getMessageTag(1145,filename.toString()));
             }
            filename = FileSystems.getDefault().getPath("data/sampledir/routing.txt");
            file = new File(filename.toString());
             try {
                 tacomments.append("Creating routings...\n");
                 Thread.sleep(1000);
                 processRoutingMaster(file);
                 
             } catch (IOException ex) {
                 bsmf.MainFrame.show(getMessageTag(1145,filename.toString()));
             }
             filename = FileSystems.getDefault().getPath("data/sampledir/itemmstr.txt");
            file = new File(filename.toString());
             try {
                 tacomments.append("Creating items...\n");
                 Thread.sleep(1000);
                 processItemMaster(file);
                 
             } catch (IOException ex) {
                 bsmf.MainFrame.show(getMessageTag(1145,filename.toString()));
             }
             filename = FileSystems.getDefault().getPath("data/sampledir/bommstr.txt");
            file = new File(filename.toString());
             try {
                 tacomments.append("Creating BOMs...\n");
                 Thread.sleep(1000);
                 processBOMMaster(file);
                 
             } catch (IOException ex) {
                 bsmf.MainFrame.show(getMessageTag(1145,filename.toString()));
             }
             
              filename = FileSystems.getDefault().getPath("data/sampledir/custmstr.txt");
            file = new File(filename.toString());
             try {
                 tacomments.append("Creating customers...\n");
                 Thread.sleep(1000);
                 processCustMstr(file);
                 
             } catch (IOException ex) {
                 bsmf.MainFrame.show(getMessageTag(1145,filename.toString()));
             }
             
             tacomments.append("Creating timeclock data...\n");
             if (createTestDataTC()) { 
                 m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
             } else {
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             }
             
             if (m[0].equals("1")) {
                 return m;
             }
             
             tacomments.append("Creating random sales orders...\n");
             if (createTestDataSO()) { 
                 m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
             } else {
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             }
             
             if (m[0].equals("1")) {
                 return m;
             }
             
             
             tacomments.append("Creating random purchase orders...\n");
             if (createTestDataPO()) { 
                 m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
             } else {
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             }
             
             String fromitem = bsmf.MainFrame.lowchar;
             String toitem = bsmf.MainFrame.hichar;
             ArrayList<String> items = invData.getItemRange("1000", fromitem, toitem);
        
          for (String p : items) {
          OVData.setStandardCosts("1000", p);
            tacomments.append("Rolling Cost for: " + p + " \n");
          }
       
        return m;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        btupload = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddtable = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tacomments = new javax.swing.JTextArea();
        btdescribe = new javax.swing.JButton();
        cboverride = new javax.swing.JCheckBox();
        bttestdata = new javax.swing.JButton();
        tbdelimiter = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        btupload.setText("Upload");
        btupload.setName("btupload"); // NOI18N
        btupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btuploadActionPerformed(evt);
            }
        });

        jLabel1.setText("Master Table:");
        jLabel1.setName("lblid"); // NOI18N

        ddtable.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item Master", "BOM Master", "Customer Master", "Customer ShipTo Master", "Customer Xref", "Customer Price List", "Vendor Master", "Vendor Xref", "Vendor Price List", "Inventory Adjustment", "GL Account Balances", "Generic Code", "EDI Partners", "EDI Partner Transactions", "EDI Document Structures", "Carrier Master", "Routing Master", "WorkCenter Master", "Order - Shopify" }));
        ddtable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtableActionPerformed(evt);
            }
        });

        tacomments.setColumns(20);
        tacomments.setRows(5);
        jScrollPane1.setViewportView(tacomments);

        btdescribe.setText("Define");
        btdescribe.setName("btdefine"); // NOI18N
        btdescribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdescribeActionPerformed(evt);
            }
        });

        cboverride.setText("Menu Integrity Override");
        cboverride.setName("cboverride"); // NOI18N

        bttestdata.setText("Test Data");
        bttestdata.setName("bttestdata"); // NOI18N
        bttestdata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttestdataActionPerformed(evt);
            }
        });

        jLabel2.setText("Delimiter:");
        jLabel2.setName("lbdelim"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 106, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddtable, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bttestdata)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboverride)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(btdescribe)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupload))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupload)
                    .addComponent(btdescribe)
                    .addComponent(cboverride)
                    .addComponent(tbdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttestdata)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btuploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btuploadActionPerformed
        File myfile = getfile();
        setPanelComponentState(this, false);
        tacomments.setText("");
        executeTask(ddtable.getSelectedItem().toString(), myfile, "");
    }//GEN-LAST:event_btuploadActionPerformed

    private void ddtableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtableActionPerformed
        tacomments.setText("");
    }//GEN-LAST:event_ddtableActionPerformed

    private void btdescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdescribeActionPerformed
       describeFile(ddtable.getSelectedItem().toString());
    }//GEN-LAST:event_btdescribeActionPerformed

    private void bttestdataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttestdataActionPerformed
       bsmf.MainFrame.show(getMessageTag(1172));
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1173));
        if (proceed) {
        tacomments.setText("");
        setPanelComponentState(this, false);
        executeTask("", null, "testdata");
        }
    }//GEN-LAST:event_bttestdataActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btdescribe;
    private javax.swing.JButton bttestdata;
    private javax.swing.JButton btupload;
    private javax.swing.JCheckBox cboverride;
    private javax.swing.JComboBox ddtable;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea tacomments;
    private javax.swing.JTextField tbdelimiter;
    // End of variables declaration//GEN-END:variables
}
