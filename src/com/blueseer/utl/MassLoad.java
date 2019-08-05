/*
   Copyright 2005-2018 Terry Evans Vaughn ("VCSCode").

With regard to the Blueseer Software:

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.


 */

package com.blueseer.utl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author vaughnte
 */
public class MassLoad extends javax.swing.JPanel {

    /**
     * Creates new form FileOrderLoadPanel
     */
    public MassLoad() {
        initComponents();
    }

    
      public void reinitddcustcode() {
        try {
            ddtable.removeAllItems();
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
                while (res.next()) {
                    ddtable.addItem(res.getString("cm_code"));
                }
                
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Sql code reinitddcustcode");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void initvars(String arg) {
        
      tacomments.setText("");
    }
    
    
    
    public void ftpfile(String mypath, String myfile) {
        
        try {
         FTPClient client = new FTPClient();
         client.connect("2.2.2.2");
         client.login("user", "passwd");
         client.enterLocalPassiveMode();
         client.setFileType(FTP.BINARY_FILE_TYPE);
         FileInputStream file = new FileInputStream(mypath + myfile);
         client.changeWorkingDirectory("/apps/edi/generic/inqueue");
         client.storeFile(myfile, file);
         client.logout();
         client.disconnect();
         if (file != null) {
	  file.close();
	 }
         bsmf.MainFrame.show("File has been FTP'd ");
        } catch (IOException e) {
            bsmf.MainFrame.show("Unable to FTP file ");
        }
         
    }
    
    // Item master stuff
    public ArrayList<String> defineItemMaster() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("it_item,s,30,mandatory,unvalidated");
        list.add("it_desc,s,50,optional,unvalidated");
        list.add("it_site,s,10,mandatory,validated");
        list.add("it_code,s,10,mandatory,validated (P or M or A)");
        list.add("it_prodline,s,4,mandatory,validated");
        list.add("it_loc,s,20,mandatory,validated");
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
        
        // first check for correct number of fields
        if (rs.length != 28) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == 28) {
            // now check individual fields
            ArrayList<String> list = defineItemMaster();
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
    
    public void processItemMaster (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               if (ddtable.getSelectedItem().toString().compareTo("Item Master") == 0) {
                   temp = checkItemMaster(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
            }
            
             if (proceed) {
                   if(OVData.addItemMaster(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
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
        
        // first check for correct number of fields
        if (rs.length != 3) {
                   tacomments.append("line " + i + " does not have correct number of fields. " + String.valueOf(rs.length) + "\n" );
                   proceed = false;
        }
        
       
        
        if (rs.length == 3) {
            // now check individual fields
            ArrayList<String> list = defineGenericCode();
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
    
    public void processGenericCode (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               if (ddtable.getSelectedItem().toString().compareTo("Generic Code") == 0) {
                   temp = checkGenericCode(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               }
            }
            
             if (proceed) {
                   if(OVData.addGenericCode(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
    
    
    // cust xref stuff
    public ArrayList<String> defineCustXref() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cup_cust,s,10,mandatory,validated");
        list.add("cup_item,s,20,mandatory,validated");
        list.add("cup_citem,s,30,mandatory,unvalidated");
        list.add("cup_citem2,s,30,optional,unvalidated");
        list.add("cup_upc,s,30,optional,unvalidated");
        list.add("cup_sku,s,30,optional,unvalidated");
        list.add("cup_misc,s,30,optional,unvalidated");
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
    
    public void processCustXref (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkCustXref(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if (OVData.addCustXref(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
    
     // vend xref stuff
    public ArrayList<String> defineVendXref() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vdp_vend,s,10,mandatory,validated");
        list.add("vdp_item,s,20,mandatory,validated");
        list.add("vdp_vitem,s,30,mandatory,unvalidated");
        list.add("vdp_upc,s,30,optional,unvalidated");
        list.add("vdp_sku,s,30,optional,unvalidated");
        list.add("vdp_misc,s,30,optional,unvalidated");
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
    
    public void processVendXref (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkVendXref(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if(OVData.addVendXref(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
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
                if (ld[0].compareTo("item") == 0 && (OVData.getItemCost(rs[j], "standard", rs[j+1]) == 0) ) {
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
    
    public void processInvAdjustment (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkInvAdjustment(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if(OVData.addInvAdjustments(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
    
     // cust price list stuff
    public ArrayList<String> defineCustPriceList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cpr_cust,s,10,mandatory,validated");
        list.add("cpr_item,s,20,mandatory,validated");
        list.add("cpr_desc,s,30,optional,unvalidated");
        list.add("cpr_type,s,20,mandatory,validated");
        list.add("cpr_price,d,12,optional,unvalidated");
        list.add("cpr_volqty,d,12,optional,unvalidated");
        list.add("cpr_volprice,d,12,optional,unvalidated");
        list.add("cpr_disc,d,12,optional,unvalidated");
        list.add("cpr_uom,s,3,mandatory,unvalidated");
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
    
    public void processCustPriceList (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkCustPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if (OVData.addCustPriceList(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
     // cust price list stuff
    public ArrayList<String> defineVendPriceList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vpr_cust,s,10,mandatory,validated");
        list.add("vpr_item,s,20,mandatory,validated");
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
    
    public void processVendPriceList (File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkVendPriceList(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if (OVData.addVendPriceList(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
      // vendor Master
    public ArrayList<String> defineVendMstr() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("vd_addr,s,30,mandatory,unvalidated");
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
    
    public void processVendMstr(File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkVendMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            fsr.close();
             if (proceed) {
                   if (OVData.addVendMstr(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
    }
    
        // customer Master
    public ArrayList<String> defineCustMstr() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("cm_code,s,10,mandatory,unvalidated");
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
    
    public void processCustMstr(File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkCustMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            fsr.close();
            
            
             if (proceed) {
                   if (OVData.addCustMstrWShipTo(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
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
    
    public void processCustShipToMstr(File myfile) throws FileNotFoundException, IOException {
         tacomments.setText("");
            boolean proceed = true;
            boolean temp = true;
            ArrayList<String> list = new ArrayList<String>();
            BufferedReader fsr = new BufferedReader(new FileReader(myfile));
            String line = "";
            int i = 0;
            while ((line = fsr.readLine()) != null) {
                i++;
                list.add(line);
               String[] recs = line.split(":", -1);
               temp = checkCustShipToMstr(recs, i);
                   if (! temp) {
                       proceed = false;
                   }
               
            }
            
             if (proceed) {
                   if (OVData.addCustShipToMstr(list))
                   bsmf.MainFrame.show("File is clean " + i + " lines have been loaded");
            } else {
                bsmf.MainFrame.show("File has errors...correct file and try again.");
            }
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
               String[] recs = line.split(":", -1);
               if (ddtable.getSelectedItem().toString().compareTo("Item Master") == 0) {
                   temp = checkItemMaster(recs, i);
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
            }
            
            // now we should have a clean file....attempt to load
            if (proceed) {
                   bsmf.MainFrame.show("File is clean.");
               
            } else {
                bsmf.MainFrame.show("File has errors.");
            }
        
       
    }
    
     public void processfile(File myfile) throws IOException { 
         if (myfile != null) {
               if (ddtable.getSelectedItem().toString().compareTo("Item Master") == 0) {
                  processItemMaster(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Xref") == 0) {
                  processCustXref(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Price List") == 0) {
                  processCustPriceList(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Xref") == 0) {
                  processVendXref(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Price List") == 0) {
                  processVendPriceList(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Vendor Master") == 0) {
                  processVendMstr(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer Master") == 0) {
                  processCustMstr(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Customer ShipTo Master") == 0) {
                  processCustShipToMstr(myfile);
               }
               if (ddtable.getSelectedItem().toString().compareTo("Inventory Adjustment") == 0) {
                  processInvAdjustment(myfile);
               }
         }
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
        
        for (String rec : list) {
                tacomments.append(rec + "\n");
            }
            tacomments.setCaretPosition(0);
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
        btaudit = new javax.swing.JButton();
        btdescribe = new javax.swing.JButton();
        cboverride = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        btupload.setText("Audit and Load");
        btupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btuploadActionPerformed(evt);
            }
        });

        jLabel1.setText("Master Table:");

        ddtable.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item Master", "Customer Master", "Customer ShipTo Master", "Vendor Master", "Customer Xref", "Customer Price List", "Vendor Xref", "Vendor Price List", "Inventory Adjustment", "Generic Code" }));
        ddtable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtableActionPerformed(evt);
            }
        });

        tacomments.setColumns(20);
        tacomments.setRows(5);
        jScrollPane1.setViewportView(tacomments);

        btaudit.setText("Audit Only");
        btaudit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btauditActionPerformed(evt);
            }
        });

        btdescribe.setText("Define");
        btdescribe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdescribeActionPerformed(evt);
            }
        });

        cboverride.setText("Menu Integrity Override");

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
                                .addComponent(cboverride)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btdescribe)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaudit)
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
                    .addComponent(btaudit)
                    .addComponent(btdescribe)
                    .addComponent(cboverride))
                .addGap(30, 30, 30))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btuploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btuploadActionPerformed
        try {
            tacomments.setText("");
            processfile(getfile());
        } catch (IOException ex) {
            Logger.getLogger(MassLoad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btuploadActionPerformed

    private void ddtableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtableActionPerformed
        tacomments.setText("");
    }//GEN-LAST:event_ddtableActionPerformed

    private void btauditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btauditActionPerformed
         try {
             tacomments.setText("");
            auditOnly(getfile());
        } catch (IOException ex) {
            Logger.getLogger(MassLoad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btauditActionPerformed

    private void btdescribeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdescribeActionPerformed
       describeFile(ddtable.getSelectedItem().toString());
    }//GEN-LAST:event_btdescribeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btaudit;
    private javax.swing.JButton btdescribe;
    private javax.swing.JButton btupload;
    private javax.swing.JCheckBox cboverride;
    private javax.swing.JComboBox ddtable;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea tacomments;
    // End of variables declaration//GEN-END:variables
}
