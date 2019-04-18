/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.shp;

import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;


/**
 *
 * @author vaughnte
 */
public class ShipMaintPanel extends javax.swing.JPanel {

                String terms = "";
                String taxcode = "";
                String aracct = "";
                String arcc = "";
                String podate = "";
                int ordercount = 0;
                String status = "";
                String curr = "";
                boolean itemlock = false;
                
    
    /**
     * Creates new form ShipMaintPanel
     */
    public ShipMaintPanel() {
        initComponents();
    }
   
    
   // shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt 
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "SO", "Desc", "Type", "AmtType", "Amt"
            });
    
    javax.swing.table.DefaultTableModel myshipdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                 "Line", "Part", "SO", "PO", "Qty", "Price", "Desc", "WH", "LOC", "Disc", "ListPrice", "MatlTax"
            });
    
    
    
     public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
            current = Integer.valueOf(tabledetail.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
    
     public void sumlinecount() {
         totlines.setText(String.valueOf(tabledetail.getRowCount()));
    }
     
     public void sumqty() {
        int qty = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             qty = qty + Integer.valueOf(tabledetail.getValueAt(j, 4).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
    }
    
     public void sumdollars() {
        DecimalFormat df = new DecimalFormat("#.00");
        double dol = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             dol = dol + ( Double.valueOf(tabledetail.getValueAt(j, 4).toString()) * Double.valueOf(tabledetail.getValueAt(j, 5).toString()) ); 
         }
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,2).toString().equals("charge"))
            dol += Double.valueOf(sactable.getValueAt(j,4).toString());
        }
         tbtotdollars.setText(df.format(dol));
    }
     
      public void retotal() {
         sumqty();
         sumdollars();
         sumlinecount();
    }
     
     
    public void initvars(String arg) {
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        
        buttonGroup1.add(rborder);
        buttonGroup1.add(rbnonorder);
        
        sacmodel.setRowCount(0);
        
        rborder.setEnabled(false);
        rbnonorder.setEnabled(false);
        
        ordercount = 0;
        
        tbshipper.setText("");
        tbshipper.setEnabled(false);
      
        
        ddwh.removeAllItems();
        ArrayList<String> whs = OVData.getWareHouseList();
        for (String wh : whs) {
            ddwh.addItem(wh);
        }
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
        
        
         ddloc.removeAllItems();
        ArrayList<String> loc = OVData.getLocationList();
        for (String lc : loc) {
            ddloc.addItem(lc);
        }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        
        dduom.removeAllItems();
        ArrayList<String> uom = OVData.getUOMList();
        for (String code : uom) {
            dduom.addItem(code);
        }
        
         ddshipto.removeAllItems();
         ddshipto.insertItemAt("", 0);
        ddshipto.setSelectedIndex(0);
        ArrayList<String> myshipto = OVData.getCustShipToListAll(); 
        for (int i = 0; i < myshipto.size(); i++) {
            ddshipto.addItem(myshipto.get(i));
        }
        ddshipto.setEnabled(false);
        btshipto.setEnabled(false);
        
        
          ddbillto.removeAllItems();
         ddbillto.insertItemAt("", 0);
        ddbillto.setSelectedIndex(0);
        ArrayList<String> mybillto = OVData.getcustmstrlist(); 
        for (int i = 0; i < mybillto.size(); i++) {
            ddbillto.addItem(mybillto.get(i));
        }
        ddbillto.setEnabled(false);
        
        
        ddorder.removeAllItems();
         ddorder.insertItemAt("", 0);
         ddorder.insertItemAt("none",1);
        ddorder.setSelectedIndex(0);
        ArrayList<String> myorders = OVData.getOpenOrdersList(); 
        for (int i = 0; i < myorders.size(); i++) {
            ddorder.addItem(myorders.get(i));
        }
        btorder.setEnabled(false);
        ddorder.setEnabled(false);
        
        tbtotdollars.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        
        tbuserid.setText("");
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbmisc.setText("");
        tbmisc2.setText("");
        
        lbladdr.setText("");
        
        ddshipvia.removeAllItems();
        ArrayList<String> mylist = OVData.getScacCarrierOnly();   
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
         ddsite.removeAllItems();
        mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        tbqty.setText("");
        tbdesc.setText("");
        tbprice.setText("");
        if (ddpart.getItemCount() > 0)
        ddpart.setSelectedIndex(0);
        
         btbrowse.setEnabled(true);
         btcustbrowse.setEnabled(true);
         btorderbrowse.setEnabled(true);
         btnewshipper.setEnabled(true);
         btedit.setEnabled(true);
         btadd.setEnabled(true);
         btPrintShp.setEnabled(false);
         btPrintInv.setEnabled(false);
         btconfirm.setEnabled(false);
         
         lblstatus.setText("");
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        tabledetail.getColumnModel().getColumn(9).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(9).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(9).setPreferredWidth(0);
        tabledetail.getColumnModel().getColumn(10).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(10).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(10).setPreferredWidth(0);
        
        disableLowerInputs();
        
        if (! arg.isEmpty()) {
            getshipperinfo(arg);
        }
      
    }
    
       public void enableLowerInputs() {
       
        ddsite.setEnabled(true);
        ddwh.setEnabled(true);
        ddloc.setEnabled(true);
        dcshipdate.setEnabled(true);
        tbtrailer.setEnabled(true);
        ddshipvia.setEnabled(true);
        tbuserid.setEnabled(true);
        tbmisc.setEnabled(true);
        tbmisc2.setEnabled(true);
        tbremarks.setEnabled(true);
        tbref.setEnabled(true);
        
        dduom.setEnabled(true);
       
        
        tbqty.setEnabled(true);
        tbdesc.setEnabled(true);
        ddpo.setEnabled(true);
        ddpart.setEnabled(true);
        tbprice.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        
        
        tabledetail.setEnabled(true);
        
        
    }
   
       public void disableLowerInputs() {
     
        ddsite.setEnabled(false);
        ddwh.setEnabled(false);
        ddloc.setEnabled(false);
        dcshipdate.setEnabled(false);
        tbtrailer.setEnabled(false);
        ddshipvia.setEnabled(false);
        tbuserid.setEnabled(false);
        tbmisc.setEnabled(false);
        tbmisc2.setEnabled(false);
        tbremarks.setEnabled(false);
        tbref.setEnabled(false);
        
        dduom.setEnabled(false);
        
        tbtotdollars.setEnabled(false);
        tbtotqty.setEnabled(false);
        totlines.setEnabled(false);
        
        tbqty.setEnabled(false);
        tbdesc.setEnabled(false);
        ddpo.setEnabled(false);
        ddpart.setEnabled(false);
        tbprice.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
        btadd.setEnabled(false);
        btedit.setEnabled(false);
        
        tabledetail.setEnabled(false);
    }
    
    public void initnew() {
       
        ordercount = 0;
        
        rborder.setEnabled(true);
        rbnonorder.setEnabled(true);
        
        rborder.setSelected(true);
        
        tbshipper.setText("");
        
        lborder.setText("");
        lbqtyshipped.setText("");
        lbline.setText("");
        
        
        ddshipto.removeAllItems();
                
        ddbillto.setSelectedIndex(0);
        ddorder.setSelectedIndex(0);
       
        sacmodel.setRowCount(0);
        
        tbuserid.setText("");
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbmisc.setText("");
        tbmisc2.setText("");
        
        
        
        tbqty.setText("");
        tbdesc.setText("");
       
        tbprice.setText("");
        if (ddpart.getItemCount() > 0)
        ddpart.setSelectedIndex(0);
        
         btbrowse.setEnabled(false);
         btcustbrowse.setEnabled(false);
         btorderbrowse.setEnabled(false);
         btnewshipper.setEnabled(false);
         btedit.setEnabled(false);
         btadd.setEnabled(true);
         btPrintShp.setEnabled(false);
         btPrintInv.setEnabled(false);
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
      
    }
    
     public void disableshipto() {
        btshipto.setEnabled(false);
        ddshipto.setEnabled(false);
        ddbillto.setEnabled(false);
         btorder.setEnabled(true);
         ddorder.setEnabled(true);
    }
     
     public void enableshipto() {
         ddshipto.setEnabled(true);
         ddbillto.setEnabled(true);
         btshipto.setEnabled(true);
         btorder.setEnabled(false);
         ddorder.setEnabled(false);
         
         
         if (ddbillto.getItemCount() > 0) {
             ddbillto.setSelectedIndex(0);
         }
    }
    
    public void disablechoices() {
        btorder.setEnabled(false);
        btshipto.setEnabled(false);
        ddorder.setEnabled(false);
        ddshipto.setEnabled(false);
        ddbillto.setEnabled(false);
        
    }
    
    public void disableradiobuttons() {
        rborder.setEnabled(false);
        rbnonorder.setEnabled(false);
    }
    
    public void reinitshippervariables(String myshipper) {
       
        tbshipper.setText(myshipper);
        if (myshipper.compareTo("") == 0) {
            btadd.setEnabled(true);

        } else {
            btadd.setEnabled(false);
        }


       
        tbshipper.setText(myshipper);
        
        tbqty.setText("");
        tbdesc.setText("");
        tbref.setText("");
        
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        btbrowse.setEnabled(true);
        btcustbrowse.setEnabled(true);
        btorderbrowse.setEnabled(true);
        btnewshipper.setEnabled(true);
        btPrintShp.setEnabled(true);

     
    }
     
      
      public void getshipperinfo(String myshipper) {
        initvars("");
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                String status = "";

                res = st.executeQuery("select * from ship_mstr left outer join cms_det on cms_shipto = sh_ship where sh_id = " + "'" + myshipper + "'" + ";");
                while (res.next()) {
                    i++;
                    
                    tbshipper.setText(res.getString("sh_id"));
                    ddshipto.setSelectedItem(res.getString("sh_cust"));
                    ddshipto.setSelectedItem(res.getString("sh_ship"));
                    tbref.setText(res.getString("sh_ref"));
                    tbremarks.setText(res.getString("sh_rmks"));
                   // ddpo.setSelectedItem(res.getString("sh_po"));
                    dcshipdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    ddshipvia.setSelectedItem(res.getString("sh_shipvia"));
                    ddsite.setSelectedItem(res.getString("sh_site"));
                    status = res.getString("sh_status");
                    lbladdr.setText(res.getString("cms_name") + "  " + res.getString("cms_line1") + "..." + res.getString("cms_city") +
                                    ", " + res.getString("cms_state") + " " + res.getString("cms_zip"));
                }

                res = st.executeQuery("select * from ship_det where shd_id = " + "'" + myshipper + "'" + ";");
                while (res.next()) {
                  myshipdetmodel.addRow(new Object[]{res.getString("shd_line"), res.getString("shd_part"), 
                      res.getString("shd_so"), 
                      res.getString("shd_po"), 
                      res.getString("shd_qty"), 
                      res.getString("shd_netprice"),
                      res.getString("shd_desc"),
                      res.getString("shd_wh"),
                      res.getString("shd_loc"),
                      res.getString("shd_disc"),
                      res.getString("shd_listprice"),
                      res.getString("shd_taxamt")
                  });
                }
                tabledetail.setModel(myshipdetmodel);
                if (i > 0) {
                    btnewshipper.setEnabled(false);
                    btadd.setEnabled(false);
                }
                i = 0;
                
                if (status.equals("1")) {
                    btadd.setEnabled(false);
                    btedit.setEnabled(false);
                    btconfirm.setEnabled(false);
                    lblstatus.setText("Shipper has been Invoiced");
                    lblstatus.setForeground(Color.blue);
                    btPrintShp.setEnabled(true);
                    btPrintInv.setEnabled(true);
                   
                } else {
                    btadd.setEnabled(false);
                    btedit.setEnabled(true);
                     if (OVData.isConfirmInShipMaint()) {
                        btconfirm.setEnabled(true);
                    }
                    lblstatus.setText("Shipper has not been Invoiced");
                    lblstatus.setForeground(Color.red);
                    btPrintShp.setEnabled(true);
                    btPrintInv.setEnabled(false);
                    
                    
                }
                i = 0;

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve shipper");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  

    public void custChangeEvent(String billto) {
         ddshipto.removeAllItems();
            ArrayList<String> mycusts = OVData.getcustshipmstrlist(billto);
            for (int i = 0; i < mycusts.size(); i++) {
                ddshipto.addItem(mycusts.get(i));
            }
            
            if (ddbillto.getSelectedItem().toString() != null && ! ddbillto.getSelectedItem().toString().isEmpty() && ddshipto.getItemCount() <= 0) {
           bsmf.MainFrame.show("No ship-to codes for this customer...either add one in customer maint or select another customer");
           ddbillto.requestFocus();
       }
            
    }
    
      public void getParts(String cust) {
        try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                itemlock = true;
                ddpart.removeAllItems();
                res = st.executeQuery("select cup_item from cup_mstr where cup_cust = " + "'" + cust + "'" + ";");
                while (res.next()) {
                    ddpart.addItem(res.getString("cup_item"));
                }
                itemlock = false;
            
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot retrieve cup_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
      public void getItemOrderInfo(String cust, String item) {
        try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                ddpo.removeAllItems();
                res = st.executeQuery("select sod_po from sod_det inner join so_mstr on so_nbr = sod_nbr where so_cust = " + "'" + cust + "'" +
                        " and sod_part = " + "'" + item + "'" + " order by sod_po ;");
                while (res.next()) {
                    ddpo.addItem(res.getString("sod_po"));
                }
            
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot retrieve sod_det");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
      public void getOrderInfoByPO(String cust, String po, String item) {
        try {
             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                
                res = st.executeQuery("select so_nbr, sod_line, sod_wh, sod_loc, sod_netprice, sod_shipped_qty, (sod_ord_qty - sod_shipped_qty) as qty from sod_det inner join so_mstr on so_nbr = sod_nbr where so_cust = " + "'" + cust + "'" +
                        " and sod_part = " + "'" + item + "'" + 
                        " and sod_po = " + "'" + po + "'" +         
                        " order by sod_part ;");
                while (res.next()) {
                    tbprice.setText(res.getString("sod_netprice"));
                    tbqty.setText(res.getString("qty"));
                    lbqtyshipped.setText(res.getString("sod_shipped_qty"));
                    lborder.setText(res.getString("so_nbr"));
                    lbline.setText(res.getString("sod_line"));
                    ddwh.setSelectedItem(res.getString("sod_wh"));
                    ddloc.setSelectedItem(res.getString("sod_loc"));
                }
            
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot retrieve sod_det");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
      
      public void setLabelByShipTo(String shipto) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select * from cms_det where cms_shipto = " + "'" + shipto + "'" + ";");
                while (res.next()) {
                    i++;
                }
              
                
            } catch (SQLException s) {
                bsmf.MainFrame.show("sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
      
       public void setCustShipCodes(String nbr) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select * from so_mstr inner join cms_det on cms_shipto = so_ship where so_nbr = " + "'" + nbr + "'" + ";");
                while (res.next()) {
                    i++;
                    ddbillto.setSelectedItem(res.getString("so_cust"));
                    ddshipto.setSelectedItem(res.getString("so_ship"));
                }
                
            } catch (SQLException s) {
                bsmf.MainFrame.show("sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
      
    public void setShipperByShipTo(String shipto) {
        
        // created shipchoice to see override rborder state change problem
        disableradiobuttons();
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select * from cms_det inner join cm_mstr on cm_code = cms_code where cms_shipto = " + "'" + shipto + "'" + ";");
                while (res.next()) {
                    i++;
                    curr = res.getString("cm_curr");
                    ddbillto.setSelectedItem(res.getString("cms_code"));
                    ddshipto.setSelectedItem(res.getString("cms_shipto"));
                    lbladdr.setText(res.getString("cms_name") + "  " + res.getString("cms_line1") + "..." + res.getString("cms_city") +
                                    ", " + res.getString("cms_state") + " " + res.getString("cms_zip"));
                    ddshipvia.setSelectedItem(res.getString("cm_carrier"));
                    ddsite.setSelectedItem(OVData.getDefaultSite());
                    terms = res.getString("cm_terms");
                    taxcode = res.getString("cm_tax_code");
                    aracct = res.getString("cm_ar_acct");
                    arcc = res.getString("cm_ar_cc");
                    getParts(ddbillto.getSelectedItem().toString());
                }
                
                
                if (! OVData.isCustItemOnlySHIP()) {
                    itemlock = true;
                    ddpart.removeAllItems();
                    ArrayList<String> items = OVData.getItemMasterAlllist();
                    for (String item : items) {
                    ddpart.addItem(item);
                    }  
                    itemlock = false;
                }
                
                if (i == 0) {
                    bsmf.MainFrame.show("shipto code does not exist");
                } 
                if (ddbillto.getSelectedItem().toString().isEmpty() || terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty()) {
                    bsmf.MainFrame.show("billto of shipto missing AR required fields...terms, etc");
                }
                
                disablechoices();
                
                enableLowerInputs();
                btadd.setEnabled(true);
                btedit.setEnabled(false);
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setShipperByOrder(String myorder) {
         disableradiobuttons();
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int qtyavailable = 0;
                status = "";
                

                res = st.executeQuery("select * from so_mstr left outer join cms_det on cms_shipto = so_ship where so_nbr = " + "'" + myorder + "'" + ";");
                while (res.next()) {
                    i++;
                    status = res.getString("so_status");
                    curr = res.getString("so_curr");
                    
                    if (ordercount == 0 && ! status.equals("close")) {
                    ddbillto.setSelectedItem(res.getString("so_cust"));
                    ddshipto.setSelectedItem(res.getString("so_ship"));
                    ddshipvia.setSelectedItem(res.getString("so_shipvia"));
                    lbladdr.setText(res.getString("cms_name") + "  " + res.getString("cms_line1") + "..." + res.getString("cms_city") +
                                    ", " + res.getString("cms_state") + " " + res.getString("cms_zip"));
                    ddsite.setSelectedItem(res.getString("so_site"));
                    terms = res.getString("so_terms");
                    taxcode = res.getString("so_taxcode");
                    aracct = res.getString("so_ar_acct");
                    arcc = res.getString("so_ar_cc");
                    podate = res.getString("so_ord_date");
                    // tbDateShippedSM.setText(res.getString("ship_ship_date"));
                    ordercount++;
                    }
                    
                    
                    if (! ddshipto.getSelectedItem().toString().toLowerCase().equals(res.getString("so_ship").toLowerCase()) && ordercount > 0) {
                        bsmf.MainFrame.show("Order does not belong to Billto/Shipto");
                        proceed = false;
                        ddshipto.setSelectedIndex(0);
                        ddorder.requestFocus();
                        return;
                    }
                    
                    
                }
                
                   if (i == 0) {
                    bsmf.MainFrame.show("order does not exist");
                    proceed = false;
                    ddshipto.setSelectedIndex(0);
                     ddorder.requestFocus(); 
                    return;
                    } 
                    
                    if (i > 0 && status.equals("close")) {
                    bsmf.MainFrame.show("order is closed");
                    proceed = false;
                    ddshipto.setSelectedIndex(0);
                    ddorder.requestFocus(); 
                    return;
                    }
                
                
                
                if (proceed) {
                    res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + myorder + "'" + ";");
                    while (res.next()) {
                        qtyavailable = res.getInt("sod_ord_qty") - res.getInt("sod_shipped_qty");
                     myshipdetmodel.addRow(new Object[]{res.getString("sod_line"), 
                         res.getString("sod_part"), 
                         res.getString("sod_nbr"), 
                         res.getString("sod_po"), 
                         String.valueOf(qtyavailable), 
                         res.getString("sod_netprice"),
                         res.getString("sod_desc"),
                         res.getString("sod_wh"),
                         res.getString("sod_loc"),
                         res.getString("sod_disc"),
                         res.getString("sod_listprice"),
                         res.getString("sod_taxamt")
                         
                     });
                    }
                    tabledetail.setModel(myshipdetmodel);
                    i = 0;
                    
                    // now get sac table
                    ArrayList<String[]> sac = OVData.getOrderSAC(myorder);
                 //write to shs_det
                     for (String[] s : sac) {
                         sacmodel.addRow(new Object[]{
                         s[0], s[1], s[2], s[3], s[4]
                         });
                     }
                   sactable.setModel(sacmodel);
                    
                    
                }
                
            retotal();
            
            disableshipto();
            
            enableLowerInputs();
            btadd.setEnabled(true);
            btedit.setEnabled(false);
            
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSOstatus(javax.swing.JTable mytable) throws SQLException {
            
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
             ResultSet res = null;
             Statement st = bsmf.MainFrame.con.createStatement();
             String sonbr = null;
             int qty = 0;
             boolean iscomplete = true;
             String sodstatus = "";
             
        // find the sod record for each line / So pair
        for (int j = 0; j < mytable.getRowCount(); j++) {
               //    mytable.getModel().getValueAt(j, 0);  
             i = 0;
             String thispart = mytable.getModel().getValueAt(j, 0).toString();
             String thisorder = mytable.getModel().getValueAt(j, 1).toString();
             String thispo = mytable.getModel().getValueAt(j, 2).toString();
             int thisshipqty = Integer.valueOf(mytable.getModel().getValueAt(j, 3).toString());
             String thislinestatus = "";
             int thisshippedtotal = 0;
            
             try {
            
                 /* ok....let's get the current state of this line item on the sales order */
                 res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + thisorder + "'" + 
                                     " AND sod_part = " + "'" + thispart + "'" + ";");
               while (res.next()) {     
                 i++;
                   if (Integer.valueOf(res.getString("sod_shipped_qty") + thisshipqty) < Integer.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Partial"; 
                   }
                   if (Integer.valueOf(res.getString("sod_shipped_qty") + thisshipqty) >= Integer.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Shipped"; 
                   }
                   thisshippedtotal = thisshipqty + Integer.valueOf(res.getString("sod_shipped_qty"));
                   
                }
                 
                 
                 
                 /* ok...now lets update the status of this sod_det line item */
                 st.executeUpdate("update sod_det set sod_shipped_qty = " + "'" + thisshippedtotal + "'" + 
                                  "," + " sod_status = " + "'" + thislinestatus + "'" +
                                  " where sod_nbr = " + "'" + thisorder + "'" + 
                                  " and sod_part = " + "'" + thispart + "'" + 
                                  " and sod_po = " + "'" + thispo + "'" +
                     ";");
                 
                 
                 
             } catch (SQLException s) {
             JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Unable to update sod_det");
             }
              // JOptionPane.showMessageDialog(mydialog, mytable.getModel().getValueAt(j,1).toString());
              
                
            //if (iscomplete) {
            //    st.executeUpdate("update so_mstr set so_status = 'Shipped' where so_nbr = " + "'" + mytable.getModel().getValueAt(j, 1).toString() + "'" );
           // }
            
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
        jLabel5 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        tbshipper = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnewshipper = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btPrintShp = new javax.swing.JButton();
        btPrintInv = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        dcshipdate = new com.toedter.calendar.JDateChooser();
        tbtrailer = new javax.swing.JTextField();
        ddshipvia = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        tbuserid = new javax.swing.JTextField();
        tbmisc = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        lbladdr = new javax.swing.JLabel();
        tbmisc2 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        btorder = new javax.swing.JButton();
        btshipto = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        ddorder = new javax.swing.JComboBox<>();
        ddbillto = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        btcustbrowse = new javax.swing.JButton();
        btorderbrowse = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        btconfirm = new javax.swing.JButton();
        rborder = new javax.swing.JRadioButton();
        rbnonorder = new javax.swing.JRadioButton();
        btbrowse = new javax.swing.JButton();
        panelDetail = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btadditem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblwhqty = new javax.swing.JLabel();
        lbllocqty = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        ddpart = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        ddpo = new javax.swing.JComboBox<>();
        lborder = new javax.swing.JLabel();
        lbline = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        dduom = new javax.swing.JComboBox<>();
        tbqty = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        lbqtyshipped = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        ddloc = new javax.swing.JComboBox<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();

        jLabel5.setText("jLabel5");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Maintenance"));

        jLabel24.setText("Shipper#");

        btnewshipper.setText("New");
        btnewshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewshipperActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btPrintShp.setText("Print Shipper");
        btPrintShp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintShpActionPerformed(evt);
            }
        });

        btPrintInv.setText("Print Invoice");
        btPrintInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintInvActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));

        jLabel25.setText("Site");

        jLabel35.setText("Shipdate");

        jLabel26.setText("UserID");

        dcshipdate.setDateFormatString("yyyy-MM-dd");

        jLabel39.setText("ShipVia");

        jLabel40.setText("Trailer");

        jLabel27.setText("Ref");

        jLabel28.setText("Misc");

        lbladdr.setBackground(java.awt.Color.lightGray);
        lbladdr.setBorder(javax.swing.BorderFactory.createTitledBorder("ShipTo Addr"));

        jLabel29.setText("Misc2");

        jLabel41.setText("Remarks");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbuserid)
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(lbladdr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel29)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbmisc2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(69, 69, 69)
                                            .addComponent(jLabel27)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(80, 80, 80)
                                            .addComponent(jLabel28)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(tbremarks))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26))))
                .addGap(101, 101, 101)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel39))
                    .addComponent(tbmisc2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addContainerGap(115, Short.MAX_VALUE))
        );

        jLabel36.setText("ShipTo");

        btorder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btorderActionPerformed(evt);
            }
        });

        btshipto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btshiptoActionPerformed(evt);
            }
        });

        jLabel104.setText("Order");

        ddorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddorderActionPerformed(evt);
            }
        });

        ddbillto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbilltoActionPerformed(evt);
            }
        });

        jLabel2.setText("Billto");

        btcustbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btcustbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcustbrowseActionPerformed(evt);
            }
        });

        btorderbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btorderbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btorderbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addComponent(jLabel104)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btorderbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btorder)
                .addGap(64, 64, 64)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcustbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36)
                .addGap(7, 7, 7)
                .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btshipto)
                .addGap(49, 49, 49))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel36)
                        .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btshipto)
                    .addComponent(btorder)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel104))
                    .addComponent(btcustbrowse)
                    .addComponent(btorderbrowse))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblstatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        btconfirm.setText("Confirm");
        btconfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btconfirmActionPerformed(evt);
            }
        });

        rborder.setText("Whole Order");
        rborder.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rborderStateChanged(evt);
            }
        });
        rborder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rborderActionPerformed(evt);
            }
        });

        rbnonorder.setText("Customer Based");
        rbnonorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnonorderActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel24)
                        .addGap(5, 5, 5)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rborder))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbnonorder)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnewshipper)))
                        .addGap(95, 95, 95)
                        .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btPrintInv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btPrintShp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 530, Short.MAX_VALUE)
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btconfirm)))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbshipper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24))
                            .addComponent(btbrowse)
                            .addComponent(btnewshipper))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rborder)
                            .addComponent(rbnonorder)))
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btPrintShp)
                    .addComponent(btPrintInv)
                    .addComponent(btconfirm)))
        );

        add(panelMain);

        panelDetail.setMinimumSize(new java.awt.Dimension(958, 609));
        panelDetail.setPreferredSize(new java.awt.Dimension(958, 577));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdelitem.setText("Del Item");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jPanel5.setPreferredSize(new java.awt.Dimension(762, 110));

        jLabel42.setText("PONbr");

        ddpart.setEditable(true);
        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        jLabel30.setText("Part");

        jLabel38.setText("Desc");

        ddpo.setEditable(true);
        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        jLabel48.setText("Ord");

        jLabel6.setText("line");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddpart, 0, 216, Short.MAX_VALUE)
                            .addComponent(tbdesc)
                            .addComponent(ddpo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lborder, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbline, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lborder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel47.setText("UOM");

        jLabel44.setText("NetPrice");

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel43.setText("Qty");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbprice)
                            .addComponent(dduom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbqtyshipped, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbqtyshipped, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addContainerGap())
        );

        jLabel46.setText("Location");

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel45.setText("Warehouse");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddwh, 0, 76, Short.MAX_VALUE)
                    .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblwhqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbllocqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblwhqty, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbllocqty, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 756, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btdelitem)
                    .addComponent(btadditem))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btadditem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelitem)
                .addGap(0, 93, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(tabledetail);

        jLabel3.setText("Total Lines:");

        jLabel1.setText("Total Qty:");

        jLabel4.setText("Total $");

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        sactable.setEnabled(false);
        jScrollPane2.setViewportView(sactable);

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(178, 178, 178)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        add(panelDetail);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewshipperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewshipperActionPerformed
                initnew();
                tbshipper.setText(String.valueOf(OVData.getNextNbr("shipper")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcshipdate.setDate(now);
                podate = dfdate.format(now);
                
                tbuserid.setText(bsmf.MainFrame.userid.toString());
                tbuserid.setEnabled(false);
                btbrowse.setEnabled(false);
                btcustbrowse.setEnabled(false);
                btorderbrowse.setEnabled(false);
                btnewshipper.setEnabled(false);
                btedit.setEnabled(false);
                btadd.setEnabled(false);
                btPrintShp.setEnabled(false);

        
    }//GEN-LAST:event_btnewshipperActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        tabledetail.setModel(myshipdetmodel);
        String part = "";
        int line = 0;
        
         Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbprice.getText());
        if (!m.find() || tbprice.getText() == null) {
            bsmf.MainFrame.show("Invalid List Price format");
            canproceed = false;
        }
        
        
        p = Pattern.compile("^[1-9]\\d*$");
        m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show("Invalid Qty");
            canproceed = false;
        }
        line = getmaxline();
        line++;
        
        if (ddpart.getSelectedItem().toString().length() > 100) {
           part =  ddpart.getSelectedItem().toString().substring(0, 100);
        } else {
            part = ddpart.getSelectedItem().toString();
        }
        
        if (! lbline.getText().isEmpty()) {
            line = Integer.valueOf(lbline.getText());
        }
       
        if (canproceed) {
            myshipdetmodel.addRow(new Object[]{line, part, 
                lborder.getText(), 
                ddpo.getSelectedItem().toString(), 
                tbqty.getText(), 
                tbprice.getText(),
                tbdesc.getText(),
                ddwh.getSelectedItem().toString(),
                ddloc.getSelectedItem().toString(),
                0,
                tbprice.getText(), 
                ""  // matltax 
            });
        }
        
        retotal();
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                int d = 0;
                String uniqpo = "";
                String uniqwh = "";
                
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcshipdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show("Period is closed");
                    return;
                }

                if (proceed) {
                    
                       // lets collect single or multiple PO status
                       for (int j = 0; j < tabledetail.getRowCount(); j++) {
                         if (d > 0) {
                           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 3).toString()) != 0) {
                           uniqpo = "multi-PO";
                           break;
                           }
                         }
                         d++;
                         uniqpo = tabledetail.getValueAt(j, 3).toString();
                       }
                       
                        // lets collect single or multiple Warehouse status
                        d = 0;
                       for (int j = 0; j < tabledetail.getRowCount(); j++) {
                         if (d > 0) {
                           if ( uniqwh.compareTo(tabledetail.getValueAt(j, 7).toString()) != 0) {
                           uniqwh = "multi-WH";
                           break;
                           }
                         }
                         d++;
                         uniqwh = tabledetail.getValueAt(j, 7).toString();
                       }
                       
                    
                    
                    st.executeUpdate("insert into ship_mstr " 
                        + " (sh_id, sh_cust, sh_ship,"
                        + " sh_shipdate, sh_po_date, sh_ref, sh_po, sh_rmks, sh_userid, sh_site, sh_curr, sh_wh, sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc ) "
                        + " values ( " + "'" + tbshipper.getText() + "'" + "," 
                        + "'" + ddbillto.getSelectedItem().toString() + "'" + "," 
                        + "'" + ddshipto.getSelectedItem().toString() + "'" + ","
                        + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","
                        + "'" + podate + "'" + ","
                        + "'" + tbref.getText().replace("'", "") + "'" + "," 
                        + "'" + uniqpo + "'" + "," 
                        + "'" + tbremarks.getText().replace("'", "") + "'" + "," 
                        + "'" + tbuserid.getText() + "'" + "," 
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + uniqwh + "'" + ","        
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + aracct + "'" + ","
                        + "'" + arcc + "'"
                        + ");" );
                       
                
                    
                
                 
                    for (int j = 0; j < tabledetail.getRowCount(); j++) {
                        
                        st.executeUpdate("insert into ship_det "
                            + "(shd_id, shd_line, shd_part, shd_so, shd_date, shd_po, shd_qty,"
                            + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_site ) "
                            + " values ( " + "'" + tbshipper.getText() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 3).toString().replace("'", "") + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 6).toString().replace("'", "") + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 11).toString() + "'" + ","        
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");
                    }
                    
                    // now update shs_det
                    OVData.updateShipperSAC(tbshipper.getText());
                    
                    
                    bsmf.MainFrame.show("Added Shipper Record");
                    initvars(tbshipper.getText());
                   
                    
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Removing row " + i);
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
        }
        retotal();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btPrintShpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintShpActionPerformed
        OVData.printShipper(tbshipper.getText());

    }//GEN-LAST:event_btPrintShpActionPerformed

    private void btPrintInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintInvActionPerformed
       OVData.printInvoice(tbshipper.getText());
    }//GEN-LAST:event_btPrintInvActionPerformed

    private void btorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btorderActionPerformed
        setShipperByOrder(ddorder.getSelectedItem().toString());
    }//GEN-LAST:event_btorderActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                int d = 0;
                String uniqpo = "";
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcshipdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show("Period is closed");
                    return;
                }
                
                if (proceed) {
                    
                     // lets collect single or multiple PO status
                       for (int j = 0; j < tabledetail.getRowCount(); j++) {
                         if (d > 0) {
                           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 3).toString()) != 0) {
                           uniqpo = "multi-PO";
                           break;
                           }
                         }
                         d++;
                         uniqpo = tabledetail.getValueAt(j, 3).toString();
                       }
                    
                    
                    
                    st.executeUpdate("update ship_mstr set " 
                        + " sh_shipdate = " + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","
                        + " sh_ref = " + "'" + tbref.getText() + "'" + ","
                        + " sh_rmks = " + "'" + tbremarks.getText() + "'" + ","
                        + " sh_userid = " + "'" + tbuserid.getText() + "'" + ","
                        + " sh_site = " + "'" + ddsite.getSelectedItem().toString() + "'"   
                        + " where sh_id = " + "'" + tbshipper.getText().toString() + "'"
                        + ";");
                    // delete the sod_det records and add back.
                    //  "Line", "Part", "SO", "PO", "Qty", "Price", "Desc"
                    st.executeUpdate("delete from ship_det where shd_id = " + "'" + tbshipper.getText() + "'"  );
                    for (int j = 0; j < tabledetail.getRowCount(); j++) {
                       st.executeUpdate("insert into ship_det "
                            + "(shd_id, shd_line, shd_part, shd_so, shd_date, shd_po, shd_qty,"
                            + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_site ) "
                            + " values ( " + "'" + tbshipper.getText() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 10).toString() + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");
                    }
                    
                     // now update shs_det
                    OVData.updateShipperSAC(tbshipper.getText());
                    
                    
                    bsmf.MainFrame.show("Edited Shipper Record");
                    reinitshippervariables("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to edit shipper");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiptoActionPerformed
        setShipperByShipTo(ddshipto.getSelectedItem().toString());
    }//GEN-LAST:event_btshiptoActionPerformed

    private void btconfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btconfirmActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
                String[] message = OVData.confirmShipment(tbshipper.getText(), dcshipdate.getDate() );
                bsmf.MainFrame.show(message[1]);
            bsmf.MainFrame.con.close();
             initvars(tbshipper.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btconfirmActionPerformed

    private void ddorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddorderActionPerformed
              if (ddorder.getItemCount() > 0) {
                  setCustShipCodes(ddorder.getSelectedItem().toString());
              }
    }//GEN-LAST:event_ddorderActionPerformed

    private void ddbilltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbilltoActionPerformed
         if (ddbillto.getItemCount() > 0) {
           custChangeEvent(ddbillto.getSelectedItem().toString());
        } // if ddcust has a list
    }//GEN-LAST:event_ddbilltoActionPerformed

    private void rborderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rborderActionPerformed
        if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
    }//GEN-LAST:event_rborderActionPerformed

    private void rbnonorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbnonorderActionPerformed
       if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
    }//GEN-LAST:event_rbnonorderActionPerformed

    private void rborderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rborderStateChanged
          if (rborder.isSelected()) {
            disableshipto();
        }
        if (rbnonorder.isSelected()) {
            enableshipto();
        }
    }//GEN-LAST:event_rborderStateChanged

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "shipmaint,sh_id");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btcustbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcustbrowseActionPerformed
       reinitpanels("BrowseUtil", true, "shipmaint,sh_cust");
    }//GEN-LAST:event_btcustbrowseActionPerformed

    private void btorderbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btorderbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "shipmaint,sh_so");
    }//GEN-LAST:event_btorderbrowseActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
         if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        
    }//GEN-LAST:event_dduomActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed
        if (ddbillto.getSelectedItem() != null && ddpart.getSelectedItem() != null && ! itemlock ) {
        getItemOrderInfo(ddbillto.getSelectedItem().toString(), ddpart.getSelectedItem().toString());
        tbdesc.setText(OVData.getItemDesc(ddpart.getSelectedItem().toString()));
        }
    }//GEN-LAST:event_ddpartActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       if (ddbillto.getSelectedItem() != null && ddpart.getSelectedItem() != null && ddpo.getSelectedItem() != null ) {
        getOrderInfoByPO(ddbillto.getSelectedItem().toString(), ddpo.getSelectedItem().toString(), ddpart.getSelectedItem().toString());
       }
    }//GEN-LAST:event_ddpoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btPrintInv;
    private javax.swing.JButton btPrintShp;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btconfirm;
    private javax.swing.JButton btcustbrowse;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnewshipper;
    private javax.swing.JButton btorder;
    private javax.swing.JButton btorderbrowse;
    private javax.swing.JButton btshipto;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcshipdate;
    private javax.swing.JComboBox<String> ddbillto;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox<String> ddorder;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox<String> ddpo;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbladdr;
    private javax.swing.JLabel lbline;
    private javax.swing.JLabel lbllocqty;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblwhqty;
    private javax.swing.JLabel lborder;
    private javax.swing.JLabel lbqtyshipped;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JRadioButton rbnonorder;
    private javax.swing.JRadioButton rborder;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbmisc2;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbshipper;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
