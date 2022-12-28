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
package com.blueseer.shp;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.inv.invData;
import com.blueseer.ord.ordData;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.shp.shpData.ship_det;
import com.blueseer.shp.shpData.ship_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;


/**
 *
 * @author vaughnte
 */
public class ShipperMaint extends javax.swing.JPanel {

                String terms = "";
                String taxcode = "";
                String aracct = "";
                String arcc = "";
                String podate = "";
                int ordercount = 0;
                String status = "";
                String curr = "";
                boolean isLoad = false;
                
                
                
    
    /**
     * Creates new form ShipMaintPanel
     */
    public ShipperMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    
   // shs_nbr, shs_so, shs_desc, shs_type, shs_amttype, shs_amt 
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("amounttype"), 
                getGlobalColumnTag("amount")
            });
    
    javax.swing.table.DefaultTableModel myshipdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                 getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("orderline"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("price"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("tax"), 
                getGlobalColumnTag("cont"), 
                getGlobalColumnTag("serial"),
                getGlobalColumnTag("bom")
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
             qty = qty + Integer.valueOf(tabledetail.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
    }
    
    public void sumdollars() throws ParseException {
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        double dol = 0;
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             dol = dol + ( nf.parse(tabledetail.getValueAt(j, 5).toString()).doubleValue() * nf.parse(tabledetail.getValueAt(j, 6).toString()).doubleValue() ); 
         }
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,2).toString().equals("charge"))
            dol += nf.parse(sactable.getValueAt(j,4).toString()).doubleValue();
        }
         tbtotdollars.setText(bsFormatDouble(dol));
    }
     
    public void retotal() {
         sumqty();
                    try {
                        sumdollars();
                    } catch (ParseException ex) {
                        bslog(ex);
                        bsmf.MainFrame.show(getMessageTag(1017));
                    }
         sumlinecount();
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
        
        isLoad = true;
        
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        
        buttonGroup1.add(rborder);
        buttonGroup1.add(rbnonorder);
        
        sacmodel.setRowCount(0);
        
        rborder.setEnabled(false);
        rbnonorder.setSelected(true);
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
        
        ddcont.removeAllItems();
        ArrayList<String> conts = invData.getItemsByType("CONT");
        for (String cont : conts) {
            ddcont.addItem(cont);
        }
        ddcont.insertItemAt("", 0);
        ddcont.setSelectedIndex(0);
        
        
         ddshipto.removeAllItems();
         ddshipto.insertItemAt("", 0);
        ddshipto.setSelectedIndex(0);
        ArrayList<String> myshipto = cusData.getCustShipToListAll(); 
        for (int i = 0; i < myshipto.size(); i++) {
            ddshipto.addItem(myshipto.get(i));
        }
        ddshipto.setEnabled(false);
        btshipto.setEnabled(false);
        
        
          ddbillto.removeAllItems();
         ddbillto.insertItemAt("", 0);
        ddbillto.setSelectedIndex(0);
        ArrayList<String> mybillto = cusData.getcustmstrlist(); 
        for (int i = 0; i < mybillto.size(); i++) {
            ddbillto.addItem(mybillto.get(i));
        }
        ddbillto.setEnabled(false);
        
        
        ddorder.removeAllItems();
         ddorder.insertItemAt("", 0);
         ddorder.insertItemAt("none",1);
        ddorder.setSelectedIndex(0);
        ArrayList<String> myorders = ordData.getOpenOrdersList(); 
        for (int i = 0; i < myorders.size(); i++) {
            ddorder.addItem(myorders.get(i));
        }
        btorder.setEnabled(false);
        ddorder.setEnabled(false);
        
        tbtotdollars.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        
        tbitem.setText("");
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbpallets.setText("");
        tbboxes.setText("");
        
        
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
       
         btlookup.setEnabled(true);
         btnewshipper.setEnabled(true);
         btedit.setEnabled(true);
         btadd.setEnabled(true);
         btPrintShp.setEnabled(false);
         btPrintInv.setEnabled(false);
         btconfirm.setEnabled(false);
         
         lblstatus.setText("");
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        tabledetail.getColumnModel().getColumn(10).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(10).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(10).setPreferredWidth(0);
        tabledetail.getColumnModel().getColumn(11).setMaxWidth(0);
        tabledetail.getColumnModel().getColumn(11).setMinWidth(0);
        tabledetail.getColumnModel().getColumn(11).setPreferredWidth(0);
        
        disableLowerInputs();
     
        isLoad = false;
        
         if (arg != null && arg.length > 0) {
            getshipperinfo(arg[0]);
        }
     
    }
    
    public void enableLowerInputs() {
       
        ddsite.setEnabled(true);
        ddwh.setEnabled(true);
        ddloc.setEnabled(true);
        dcshipdate.setEnabled(true);
        tbtrailer.setEnabled(true);
        ddshipvia.setEnabled(true);
       
        tbremarks.setEnabled(true);
        tbref.setEnabled(true);
        tbpallets.setEnabled(true);
        tbboxes.setEnabled(true);
       
        tbtotdollars.setEnabled(true);
        tbtotqty.setEnabled(true);
        totlines.setEnabled(true);
        tbtotdollars.setEditable(false);
        tbtotqty.setEditable(false);
        totlines.setEditable(false);
        
        
        dduom.setEnabled(true);
        tborderline.setEnabled(true);
        tbordernbr.setEnabled(true);
        ddcont.setEnabled(true);
        tbserial.setEnabled(true);
        
        tbqty.setEnabled(true);
        tbpo.setEnabled(true);
        tbdesc.setEnabled(true);
        tbitem.setEnabled(true);
        tbprice.setEnabled(true);
        tbdesc.setEditable(false);
        tbitem.setEditable(false);
        tbprice.setEditable(false);
        tborderline.setEditable(false);
        tbordernbr.setEditable(false);
        
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
        
        tbremarks.setEnabled(false);
        tbref.setEnabled(false);
        tbpallets.setEnabled(false);
        tbboxes.setEnabled(false);
       
        tborderline.setEnabled(false);
        tbordernbr.setEnabled(false);
        ddcont.setEnabled(false);
        tbserial.setEnabled(false);
        
        dduom.setEnabled(false);
        
        tbtotdollars.setEnabled(false);
        tbtotqty.setEnabled(false);
        totlines.setEnabled(false);
        
        tbqty.setEnabled(false);
        tbdesc.setEnabled(false);
        tbpo.setEnabled(false);
        tbitem.setEnabled(false);
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
       // rbnonorder.setSelected(true);
        
        isLoad = true;
        tbshipper.setText("");
        tbordernbr.setText("");
        tborderline.setText("");
        
        
        lbqtyshipped.setText("");
        
        
        
        ddshipto.removeAllItems();
                
        ddbillto.setSelectedIndex(0);
        ddorder.setSelectedIndex(0);
       
        sacmodel.setRowCount(0);
        
        
        tbtrailer.setText("");
        tbremarks.setText("");
        tbref.setText("");
        tbpallets.setText("");
        tbboxes.setText("");
       
        
        
        
        tbqty.setText("");
        tbdesc.setText("");
       
        tbprice.setText("");
        tbitem.setText("");
         btlookup.setEnabled(false);
         btnewshipper.setEnabled(false);
         btedit.setEnabled(false);
         btadd.setEnabled(true);
         btPrintShp.setEnabled(false);
         btPrintInv.setEnabled(false);
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        
        isLoad = false;
      
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
        tbboxes.setText("");
        tbpallets.setText("");
        
        
        
        tabledetail.setModel(myshipdetmodel);
        myshipdetmodel.setRowCount(0);
        btlookup.setEnabled(true);
        btnewshipper.setEnabled(true);
        btPrintShp.setEnabled(true);

     
    }
     
      
    public void getshipperinfo(String myshipper) {
        initvars(null);
        try {
     
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                String status = "";

                res = st.executeQuery("select * from ship_mstr left outer join cms_det on cms_shipto = sh_ship where sh_id = " + "'" + myshipper + "'" + ";");
                while (res.next()) {
                    i++;
                    
                    tbshipper.setText(res.getString("sh_id"));
                    ddbillto.setSelectedItem(res.getString("sh_cust"));
                    ddshipto.setSelectedItem(res.getString("sh_ship"));
                    tbref.setText(res.getString("sh_ref"));
                    tbboxes.setText(res.getString("sh_boxes"));
                    tbpallets.setText(res.getString("sh_pallets"));
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
                  myshipdetmodel.addRow(new Object[]{res.getString("shd_soline"), res.getString("shd_item"), 
                      res.getString("shd_so"),
                      res.getString("shd_soline"),
                      res.getString("shd_po"), 
                      res.getString("shd_qty").replace('.', defaultDecimalSeparator), 
                      res.getString("shd_netprice").replace('.', defaultDecimalSeparator),
                      res.getString("shd_desc"),
                      res.getString("shd_wh"),
                      res.getString("shd_loc"),
                      res.getString("shd_disc").replace('.', defaultDecimalSeparator),
                      res.getString("shd_listprice").replace('.', defaultDecimalSeparator),
                      res.getString("shd_taxamt").replace('.', defaultDecimalSeparator),
                      res.getString("shd_serial"),
                      res.getString("shd_cont"),
                      res.getString("shd_bom")
                  });
                
                }
                tabledetail.setModel(myshipdetmodel);
                if (i > 0) {
                    enableLowerInputs();
                    btnewshipper.setEnabled(false);
                    btadd.setEnabled(false);
                }
                i = 0;
                
                if (status.equals("1")) {
                    btadd.setEnabled(false);
                    btedit.setEnabled(false);
                    btconfirm.setEnabled(false);
                    lblstatus.setText(getMessageTag(1148));
                    lblstatus.setForeground(Color.blue);
                    btPrintShp.setEnabled(true);
                    btPrintInv.setEnabled(true);
                   
                } else {
                    btadd.setEnabled(false);
                    btedit.setEnabled(true);
                     if (OVData.isConfirmInShipMaint()) {
                        btconfirm.setEnabled(true);
                    }
                    lblstatus.setText(getMessageTag(1149));
                    lblstatus.setForeground(Color.red);
                    btPrintShp.setEnabled(true);
                    btPrintInv.setEnabled(false);
                    
                    
                }
                i = 0;

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
  

    public void custChangeEvent(String billto) {
         ddshipto.removeAllItems();
            ArrayList<String> mycusts = cusData.getcustshipmstrlist(billto);
            for (int i = 0; i < mycusts.size(); i++) {
                ddshipto.addItem(mycusts.get(i));
            }
        
            
            
            if (ddbillto.getSelectedItem().toString() != null && ! ddbillto.getSelectedItem().toString().isEmpty() && ddshipto.getItemCount() <= 0) {
           bsmf.MainFrame.show(getMessageTag(1108));
           ddbillto.requestFocus();
       }
            
    }
    
     
    
    public void getOrderInfoByPO(String cust, String po, String item) {
        try {
             Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                
                res = st.executeQuery("select so_nbr, sod_line, sod_wh, sod_loc, sod_netprice, sod_shipped_qty, (sod_ord_qty - sod_shipped_qty) as qty from sod_det inner join so_mstr on so_nbr = sod_nbr where so_cust = " + "'" + cust + "'" +
                        " and sod_item = " + "'" + item + "'" + 
                        " and sod_po = " + "'" + po + "'" +         
                        " order by sod_item ;");
                while (res.next()) {
                    tbprice.setText(res.getString("sod_netprice"));
                    tbqty.setText(res.getString("qty"));
                    lbqtyshipped.setText(res.getString("sod_shipped_qty"));
                    ddwh.setSelectedItem(res.getString("sod_wh"));
                    ddloc.setSelectedItem(res.getString("sod_loc"));
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
    
    public String getUniquePO() {
        int d = 0;
        String uniqpo = "";
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 4).toString()) != 0) {
           uniqpo = "multi-PO";
           break;
           }
         }
         d++;
         uniqpo = tabledetail.getValueAt(j, 4).toString();
       }
        return uniqpo;
    }  
    
    public String getUniqueWH() {
           // lets collect single or multiple Warehouse status
        int d = 0;
        String uniqwh = "";
       for (int j = 0; j < tabledetail.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqwh.compareTo(tabledetail.getValueAt(j, 8).toString()) != 0) {
           uniqwh = "multi-WH";
           break;
           }
         }
         d++;
         uniqwh = tabledetail.getValueAt(j, 8).toString();
       }
       return uniqwh;
    }
    
    public void setLabelByShipTo(String shipto) {
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;int i = 0;
            try {


                res = st.executeQuery("select * from cms_det where cms_shipto = " + "'" + shipto + "'" + ";");
                while (res.next()) {
                    i++;
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
      
    public void setCustShipCodes(String nbr) {
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
            try {

                res = st.executeQuery("select * from so_mstr inner join cms_det on cms_shipto = so_ship where so_nbr = " + "'" + nbr + "'" + ";");
                while (res.next()) {
                    i++;
                    ddbillto.setSelectedItem(res.getString("so_cust"));
                    ddshipto.setSelectedItem(res.getString("so_ship"));
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
      
    public void setShipperByShipTo(String shipto) {
        
        // created shipchoice to see override rborder state change problem
        disableradiobuttons();
        
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
            try {


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
                   
                }
                
                
                if (i == 0) {
                    bsmf.MainFrame.show(getMessageTag(1034,shipto));
                } 
                if (ddbillto.getSelectedItem().toString().isEmpty() || terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty()) {
                    bsmf.MainFrame.show(getMessageTag(1090));
                }
                
                disablechoices();
                
                enableLowerInputs();
                btadd.setEnabled(true);
                btedit.setEnabled(false);
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
        
    public void setDetail(String nbr, String line) {
         try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                tbitem.setText("");
                tbpo.setText("");
                tbserial.setText("");
                tbdesc.setText("");
                tbqty.setText("");
                tbprice.setText("");
                dduom.setSelectedIndex(0);
                ddcont.setSelectedIndex(0);
                
                ddbom.removeAllItems();
                
                String bom = "";
                res = st.executeQuery("select * from sod_det " + 
                        " inner join item_mstr on it_item = sod_item "  +
                        " where sod_nbr = " + "'" + nbr + "'" +
                        " AND sod_line = " + "'" + line + "'" +  
                        " AND sod_status <> " + "'" + getGlobalProgTag("closed") + "'" + 
                        " order by sod_line ;");
                while (res.next()) {
                tbitem.setText(res.getString("sod_item"));
                tbpo.setText(res.getString("sod_po"));
                tbdesc.setText(res.getString("sod_desc"));
                int qty = res.getInt("sod_ord_qty") - res.getInt("sod_shipped_qty");
                tbqty.setText(String.valueOf(qty));
                tbprice.setText(BlueSeerUtils.priceformat(res.getString("sod_netprice")));
                dduom.setSelectedItem(res.getString("sod_uom"));
                ddcont.setSelectedItem(res.getString("it_cont"));
                tbcontqty.setText(res.getString("it_contqty"));
                bom = res.getString("sod_bom");
                }
                
                ddbom.insertItemAt("", 0);
                ddbom.setSelectedIndex(0);
                ArrayList<String[]> boms = invData.getBOMsByItemSite(tbitem.getText());
                for (String[] wh : boms) {
                ddbom.addItem(wh[0]);
                }
                if (! bom.isEmpty())
                ddbom.setSelectedItem(bom);
            
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
    
    public void setShipperByOrder(String myorder) {
         disableradiobuttons();
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;int i = 0;
            try {
                boolean proceed = true;
                int qtyavailable = 0;
                status = "";
                

                res = st.executeQuery("select * from so_mstr left outer join cms_det on cms_shipto = so_ship where so_nbr = " + "'" + myorder + "'" + ";");
                while (res.next()) {
                    i++;
                    status = res.getString("so_status");
                    curr = res.getString("so_curr");
                    
                    if (ordercount == 0 && ! status.equals(getGlobalProgTag("closed"))) {
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
                        bsmf.MainFrame.show(getMessageTag(1109));
                        proceed = false;
                        ddshipto.setSelectedIndex(0);
                        ddorder.requestFocus();
                        return;
                    }
                    
                    
                }
                
                   if (i == 0) {
                    bsmf.MainFrame.show(getMessageTag(1034,myorder));
                    proceed = false;
                    ddshipto.setSelectedIndex(0);
                     ddorder.requestFocus(); 
                    return;
                    } 
                    
                    if (i > 0 && status.equals(getGlobalProgTag("closed"))) {
                    bsmf.MainFrame.show(getMessageTag(1097));
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
                         res.getString("sod_item"), 
                         res.getString("sod_nbr"),
                         res.getString("sod_line"),
                         res.getString("sod_po"), 
                         String.valueOf(qtyavailable), 
                         res.getString("sod_netprice"),
                         res.getString("sod_desc"),
                         res.getString("sod_wh"),
                         res.getString("sod_loc"),
                         res.getString("sod_disc"),
                         res.getString("sod_listprice"),
                         res.getString("sod_taxamt"),
                         "", // cont
                         "",  // serialno
                         res.getString("sod_bom")
                         
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

    public void setSOstatus(javax.swing.JTable mytable) {
    try {  
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
             String sonbr = null;
             int qty = 0;
             boolean iscomplete = true;
             String sodstatus = "";
             
         
        try {
                 
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
            
            
                 /* ok....let's get the current state of this line item on the sales order */
                 res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + thisorder + "'" + 
                                     " AND sod_item = " + "'" + thispart + "'" + ";");
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
                                  " and sod_item = " + "'" + thispart + "'" + 
                                  " and sod_po = " + "'" + thispo + "'" +
                     ";");
                 
              } // foreach    
                 
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
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_id");
        } else {
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_cust");   
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
      
        
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lblbillto", this.getClass().getSimpleName())); 
        
    }

    public void lookUpFrameOrderLine() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderDetailBrowseUtil(luinput.getText(), "sod_item", ddbillto.getSelectedItem().toString(), ddshipto.getSelectedItem().toString() );
        } else {
         luModel = DTData.getOrderDetailBrowseUtil(luinput.getText(), "sod_desc", ddbillto.getSelectedItem().toString(), ddshipto.getSelectedItem().toString() );
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
                tbordernbr.setText(target.getValueAt(row,1).toString());
                tborderline.setText(target.getValueAt(row,2).toString());
                setDetail(tbordernbr.getText(), tborderline.getText());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()));
        
        
    }

    public ship_mstr createRecord() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String uniqwh = getUniqueWH();
        String uniqpo = getUniquePO();
               
        ship_mstr x = new ship_mstr(null, 
                tbshipper.getText(),
                ddbillto.getSelectedItem().toString(),
                ddshipto.getSelectedItem().toString(),
                tbpallets.getText(),
                tbboxes.getText(),
                ddshipvia.getSelectedItem().toString(),  
                dfdate.format(dcshipdate.getDate()),
                podate,
                tbref.getText().replace("'", ""),
                uniqpo,
                tbremarks.getText(),
                bsmf.MainFrame.userid,
                ddsite.getSelectedItem().toString(),
                curr,
                uniqwh,
                terms,
                taxcode,
                aracct,
                arcc );
                
        return x;        
    }
    
    public ArrayList<ship_det> createDetRecord() {
        ArrayList<ship_det> list = new ArrayList<ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String uniqwh = getUniqueWH();
        String uniqpo = getUniquePO();
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        for (int j = 0; j < tabledetail.getRowCount(); j++) { 
            ship_det x = new ship_det(null, 
                tbshipper.getText(), // shipper
                tabledetail.getValueAt(j, 0).toString(), //shline
                tabledetail.getValueAt(j, 1).toString(), // item
                tabledetail.getValueAt(j, 1).toString(), // custimtem
                tabledetail.getValueAt(j, 2).toString(),  // order
                tabledetail.getValueAt(j, 3).toString(), //soline    
                dfdate.format(dcshipdate.getDate()),
                tabledetail.getValueAt(j, 4).toString(),
                tabledetail.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.'), // qty
                "", //uom
                "", //currency
                tabledetail.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'), // netprice
                tabledetail.getValueAt(j, 10).toString().replace(defaultDecimalSeparator, '.'), // disc
                tabledetail.getValueAt(j, 11).toString().replace(defaultDecimalSeparator, '.'), // listprice
                tabledetail.getValueAt(j, 7).toString(), // desc
                tabledetail.getValueAt(j, 8).toString(), // wh
                tabledetail.getValueAt(j, 9).toString(), // loc
                tabledetail.getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.'), // taxamt
                tabledetail.getValueAt(j, 13).toString(), // cont
                "", // ref
                tabledetail.getValueAt(j, 14).toString(),  // serial   
                ddsite.getSelectedItem().toString(),
                tabledetail.getValueAt(j, 15).toString() // bom
                );
        list.add(x);
        }      
        return list;        
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
        HeaderPanel = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        dcshipdate = new com.toedter.calendar.JDateChooser();
        tbtrailer = new javax.swing.JTextField();
        ddshipvia = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        tbpallets = new javax.swing.JTextField();
        tbboxes = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ChoicePanel = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        btorder = new javax.swing.JButton();
        btshipto = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        ddorder = new javax.swing.JComboBox<>();
        ddbillto = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        lbbilltoname = new javax.swing.JLabel();
        lbladdr = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        btconfirm = new javax.swing.JButton();
        rborder = new javax.swing.JRadioButton();
        rbnonorder = new javax.swing.JRadioButton();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
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
        jLabel30 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddcont = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tbserial = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbpo = new javax.swing.JTextField();
        tbitem = new javax.swing.JTextField();
        tbcontqty = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tbordernbr = new javax.swing.JTextField();
        tborderline = new javax.swing.JTextField();
        btlookupOrderLine = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        dduom = new javax.swing.JComboBox<>();
        tbqty = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        lbqtyshipped = new javax.swing.JLabel();
        cbexplode = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        ddloc = new javax.swing.JComboBox<>();
        ddbom = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
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
        panelMain.setName("panelmain"); // NOI18N

        jLabel24.setText("Shipper#");
        jLabel24.setName("lblid"); // NOI18N

        btnewshipper.setText("New");
        btnewshipper.setName("btnew"); // NOI18N
        btnewshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewshipperActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btPrintShp.setText("Print Shipper");
        btPrintShp.setName("btprintshipper"); // NOI18N
        btPrintShp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintShpActionPerformed(evt);
            }
        });

        btPrintInv.setText("Print Invoice");
        btPrintInv.setName("btprintinvoice"); // NOI18N
        btPrintInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintInvActionPerformed(evt);
            }
        });

        btedit.setText("Update");
        btedit.setName("btupdate"); // NOI18N
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        HeaderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Header"));
        HeaderPanel.setName("panelheader"); // NOI18N

        jLabel25.setText("Site:");
        jLabel25.setName("lblsite"); // NOI18N

        jLabel35.setText("Shipdate:");
        jLabel35.setName("lblshipdate"); // NOI18N

        dcshipdate.setDateFormatString("yyyy-MM-dd");

        jLabel39.setText("ShipVia:");
        jLabel39.setName("lblshipvia"); // NOI18N

        jLabel40.setText("Trailer:");
        jLabel40.setName("lbltrailer"); // NOI18N

        jLabel27.setText("Ref:");
        jLabel27.setName("lblref"); // NOI18N

        jLabel41.setText("Remarks:");
        jLabel41.setName("lblremarks"); // NOI18N

        jLabel7.setText("Pallets:");
        jLabel7.setName("lblpallets"); // NOI18N

        jLabel8.setText("Boxes:");
        jLabel8.setName("lblboxes"); // NOI18N

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39)
                    .addComponent(jLabel41)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbboxes, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(HeaderPanelLayout.createSequentialGroup()
                                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(62, 62, 62)
                                        .addComponent(jLabel35))
                                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                                        .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel27))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderPanelLayout.createSequentialGroup()
                                        .addComponent(tbpallets, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel40)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35))
                    .addComponent(dcshipdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addComponent(tbpallets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbboxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        jLabel36.setText("ShipTo:");
        jLabel36.setName("lblshipto"); // NOI18N

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

        jLabel104.setText("Order:");
        jLabel104.setName("lblorder"); // NOI18N

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

        jLabel2.setText("Bill To:");
        jLabel2.setName("lblbillto"); // NOI18N

        lbladdr.setBackground(java.awt.Color.lightGray);

        javax.swing.GroupLayout ChoicePanelLayout = new javax.swing.GroupLayout(ChoicePanel);
        ChoicePanel.setLayout(ChoicePanelLayout);
        ChoicePanelLayout.setHorizontalGroup(
            ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel104, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ChoicePanelLayout.createSequentialGroup()
                                .addComponent(btshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btorder, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(ChoicePanelLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(lbbilltoname, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        ChoicePanelLayout.setVerticalGroup(
            ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ChoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(lbbilltoname, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel36)
                        .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btshipto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ChoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel104))
                    .addComponent(btorder))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblstatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        btconfirm.setText("Confirm");
        btconfirm.setName("btconfirm"); // NOI18N
        btconfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btconfirmActionPerformed(evt);
            }
        });

        rborder.setText("Single Order");
        rborder.setName("cbsingle"); // NOI18N
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

        rbnonorder.setText("Multi Order");
        rbnonorder.setName("cbmulti"); // NOI18N
        rbnonorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbnonorderActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel24)
                .addGap(5, 5, 5)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbnonorder)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(tbshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rborder)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addComponent(btnewshipper)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)))
                .addGap(143, 143, 143)
                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btPrintInv)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btPrintShp)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btadd)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btedit)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btconfirm))
                .addComponent(HeaderPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnewshipper)
                                    .addComponent(btclear))
                                .addGap(9, 9, 9))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbshipper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel24))
                                    .addComponent(btlookup))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rborder)
                            .addComponent(rbnonorder)))
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btPrintShp)
                    .addComponent(btPrintInv)
                    .addComponent(btconfirm)))
        );

        add(panelMain);

        panelDetail.setMinimumSize(new java.awt.Dimension(958, 477));
        panelDetail.setPreferredSize(new java.awt.Dimension(958, 500));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        jPanel2.setName("paneldetail"); // NOI18N

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
        jLabel42.setName("lblponbr"); // NOI18N

        jLabel30.setText("Item");
        jLabel30.setName("lblitem"); // NOI18N

        jLabel38.setText("Desc");
        jLabel38.setName("lbldesc"); // NOI18N

        jLabel48.setText("Order");
        jLabel48.setName("lblorder"); // NOI18N

        jLabel6.setText("OrderLine");
        jLabel6.setName("lblorderline"); // NOI18N

        jLabel11.setText("Cont");
        jLabel11.setName("lblcont"); // NOI18N

        jLabel10.setText("Serial");
        jLabel10.setName("lblserial"); // NOI18N

        jLabel9.setText("ContQty");
        jLabel9.setName("lblcontqty"); // NOI18N

        btlookupOrderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrderLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderLineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel48)
                    .addComponent(jLabel42)
                    .addComponent(jLabel11)
                    .addComponent(jLabel30)
                    .addComponent(jLabel38)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(tbserial)
                        .addComponent(ddcont, 0, 217, Short.MAX_VALUE)
                        .addComponent(tbdesc)
                        .addComponent(tbpo)
                        .addComponent(tbitem))
                    .addComponent(tbcontqty, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookupOrderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tborderline, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btlookupOrderLine)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tborderline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcontqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel47.setText("UOM");
        jLabel47.setName("lbluom"); // NOI18N

        jLabel44.setText("NetPrice");
        jLabel44.setName("lblnetprice"); // NOI18N

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel43.setText("Qty");
        jLabel43.setName("lblqty"); // NOI18N

        cbexplode.setText("Explode");
        cbexplode.setName("cbexplode"); // NOI18N

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
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbqtyshipped, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(cbexplode))
                            .addComponent(tbprice, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dduom, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
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
                .addGap(11, 11, 11)
                .addComponent(cbexplode)
                .addContainerGap())
        );

        jLabel46.setText("Location");
        jLabel46.setName("lblloc"); // NOI18N

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel45.setText("Warehouse");
        jLabel45.setName("lblwh"); // NOI18N

        jLabel12.setText("BOM");
        jLabel12.setName("lblbom"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddwh, 0, 76, Short.MAX_VALUE)
                    .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddbom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
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
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblwhqty, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbllocqty, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
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
        jLabel3.setName("lbltotallines"); // NOI18N

        jLabel1.setText("Total Qty:");
        jLabel1.setName("lbltotalqty"); // NOI18N

        jLabel4.setText("Total $");
        jLabel4.setName("lbltotalamt"); // NOI18N

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
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(154, 154, 154)
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
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(0, 0, 0))
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
                
              
                btlookup.setEnabled(false);
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
        line = getmaxline();
        
        
        int nbrOfContainers = 0;
        int remainder = 0;
        if (cbexplode.isSelected() && ! tbcontqty.getText().isEmpty()) {
            nbrOfContainers = Integer.valueOf(tbqty.getText()) / Integer.valueOf(tbcontqty.getText());
            remainder = Integer.valueOf(tbqty.getText()) % Integer.valueOf(tbcontqty.getText());
        }
                
        if (canproceed) {
            if (nbrOfContainers == 0) {
            line++;    
            myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                tbordernbr.getText(), 
                tborderline.getText(),
                tbpo.getText(), 
                tbqty.getText(), 
                tbprice.getText(),
                tbdesc.getText(),
                ddwh.getSelectedItem().toString(),
                ddloc.getSelectedItem().toString(),
                0,
                tbprice.getText(), 
                "0",  // matltax 
                ddcont.getSelectedItem().toString(),
                tbserial.getText(),
                ddbom.getSelectedItem().toString()
            });
            } else {
                   for (int x = 0; x < nbrOfContainers; x++) {
                   line++;
                   myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                    tbordernbr.getText(), 
                    tborderline.getText(),
                    tbpo.getText(), 
                    tbcontqty.getText(), 
                    tbprice.getText(),
                    tbdesc.getText(),
                    ddwh.getSelectedItem().toString(),
                    ddloc.getSelectedItem().toString(),
                    0,
                    tbprice.getText(), 
                    "0",  // matltax 
                    ddcont.getSelectedItem().toString(),
                    tbserial.getText(),
                    ddbom.getSelectedItem().toString()
                }); 
                }
                   // now add extra if remainder greater than zero
                   if (remainder > 0) {
                    line++;   
                    myshipdetmodel.addRow(new Object[]{line, tbitem.getText(), 
                    tbordernbr.getText(), 
                    tborderline.getText(), 
                    tbpo.getText(), 
                    remainder, 
                    tbprice.getText(),
                    tbdesc.getText(),
                    ddwh.getSelectedItem().toString(),
                    ddloc.getSelectedItem().toString(),
                    0,
                    tbprice.getText(), 
                    "0",  // matltax 
                    ddcont.getSelectedItem().toString(),
                    tbserial.getText(),
                    ddbom.getSelectedItem().toString()
                });    
                   }
            }
        }
        
        retotal();
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if ( OVData.isGLPeriodClosed(BlueSeerUtils.setDateFormat(dcshipdate.getDate()))) { 
                bsmf.MainFrame.show(getMessageTag(1035));
                return;
        } 
        String[] m = new String[2];
        m = addShipperTransaction(createDetRecord(), createRecord());
        shpData.updateShipperSAC(tbshipper.getText());
        initvars(new String[]{tbshipper.getText()});
        
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
        }
        retotal();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btPrintShpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintShpActionPerformed
         OVData.printShipper(tbshipper.getText());
       // OVData.printJTableToJasper("Shipper Report", tabledetail ); 

    }//GEN-LAST:event_btPrintShpActionPerformed

    private void btPrintInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintInvActionPerformed
       OVData.printInvoice(tbshipper.getText(), true);
    }//GEN-LAST:event_btPrintInvActionPerformed

    private void btorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btorderActionPerformed
        setShipperByOrder(ddorder.getSelectedItem().toString());
    }//GEN-LAST:event_btorderActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                int i = 0;
                int d = 0;
                String uniqpo = "";
                
                if ( OVData.isGLPeriodClosed(dfdate.format(dcshipdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return;
                }
                
                if (proceed) {
                    
                     // lets collect single or multiple PO status
                       for (int j = 0; j < tabledetail.getRowCount(); j++) {
                         if (d > 0) {
                           if ( uniqpo.compareTo(tabledetail.getValueAt(j, 4).toString()) != 0) {
                           uniqpo = "multi-PO";
                           break;
                           }
                         }
                         d++;
                         uniqpo = tabledetail.getValueAt(j, 4).toString();
                       }
                   
                int pallets = 0;
                if (! tbpallets.getText().isEmpty()) {
                    pallets = Integer.valueOf(tbpallets.getText());
                }  
                
                int boxes = 0;
                if (! tbboxes.getText().isEmpty()) {
                    boxes = Integer.valueOf(tbboxes.getText());
                }
                       
                    st.executeUpdate("update ship_mstr set " 
                        + " sh_shipdate = " + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","
                        + " sh_ref = " + "'" + tbref.getText() + "'" + ","
                        + " sh_rmks = " + "'" + tbremarks.getText() + "'" + ","
                        + " sh_shipvia = " + "'" + ddshipvia.getSelectedItem() + "'" + ","         
                        + " sh_pallets = " + "'" + pallets + "'" + ","
                        + " sh_boxes = " + "'" + boxes + "'" + ","        
                        + " sh_site = " + "'" + ddsite.getSelectedItem().toString() + "'"   
                        + " where sh_id = " + "'" + tbshipper.getText().toString() + "'"
                        + ";");
                    // delete the sod_det records and add back.
                    //  "Line", "Part", "SO", "PO", "Qty", "Price", "Desc"
                    st.executeUpdate("delete from ship_det where shd_id = " + "'" + tbshipper.getText() + "'"  );
                    for (int j = 0; j < tabledetail.getRowCount(); j++) {
                       st.executeUpdate("insert into ship_det "
                            + "(shd_id, shd_line, shd_item, shd_so, shd_soline, shd_date, shd_po, shd_qty,"
                            + "shd_netprice, shd_disc, shd_listprice, shd_desc, shd_wh, shd_loc, shd_taxamt, shd_cont, shd_serial, shd_site ) "
                            + " values ( " + "'" + tbshipper.getText() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + dfdate.format(dcshipdate.getDate()) + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 11).toString() + "'" + ","        
                            + "'" + tabledetail.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 12).toString() + "'" + ","   
                            + "'" + tabledetail.getValueAt(j, 13).toString() + "'" + ","
                            + "'" + tabledetail.getValueAt(j, 14).toString() + "'" + ","         
                            + "'" + ddsite.getSelectedItem().toString() + "'" 
                            + ")"
                            + ";");
                    }
                    
                    
                     // now update shs_det
                    shpData.updateShipperSAC(tbshipper.getText());
                    
                    
                   
                    reinitshippervariables("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
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
    }//GEN-LAST:event_bteditActionPerformed

    private void btshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btshiptoActionPerformed
        setShipperByShipTo(ddshipto.getSelectedItem().toString());
    }//GEN-LAST:event_btshiptoActionPerformed

    private void btconfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btconfirmActionPerformed
        String[] message = confirmShipperTransaction("order", tbshipper.getText(), dcshipdate.getDate());
        bsmf.MainFrame.show(message[1]);
        initvars(new String[]{tbshipper.getText()});
    }//GEN-LAST:event_btconfirmActionPerformed

    private void ddorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddorderActionPerformed
              if (ddorder.getItemCount() > 0 && ! isLoad) {
                  setCustShipCodes(ddorder.getSelectedItem().toString());
              }
    }//GEN-LAST:event_ddorderActionPerformed

    private void ddbilltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbilltoActionPerformed
         if (ddbillto.getItemCount() > 0 && ! isLoad) {
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

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        
    }//GEN-LAST:event_dduomActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        if (! tbqty.getText().isEmpty()) {
            String x = BlueSeerUtils.bsformat("", tbqty.getText(), "2");
            if (x.equals("error")) {
                tbqty.setText("");
                tbqty.setBackground(Color.yellow);
                bsmf.MainFrame.show(getMessageTag(1000));
                tbqty.requestFocus();
            } else {
                tbqty.setText(x);
                tbqty.setBackground(Color.white);
            }
        } else {
             tbqty.setBackground(Color.white);
         }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btlookupOrderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderLineActionPerformed
        lookUpFrameOrderLine();
    }//GEN-LAST:event_btlookupOrderLineActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChoicePanel;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JButton btPrintInv;
    private javax.swing.JButton btPrintShp;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btconfirm;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupOrderLine;
    private javax.swing.JButton btnewshipper;
    private javax.swing.JButton btorder;
    private javax.swing.JButton btshipto;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbexplode;
    private com.toedter.calendar.JDateChooser dcshipdate;
    private javax.swing.JComboBox<String> ddbillto;
    private javax.swing.JComboBox<String> ddbom;
    private javax.swing.JComboBox<String> ddcont;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox<String> ddorder;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbbilltoname;
    private javax.swing.JLabel lbladdr;
    private javax.swing.JLabel lbllocqty;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lblwhqty;
    private javax.swing.JLabel lbqtyshipped;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JRadioButton rbnonorder;
    private javax.swing.JRadioButton rborder;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField tbboxes;
    private javax.swing.JTextField tbcontqty;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tborderline;
    private javax.swing.JTextField tbordernbr;
    private javax.swing.JTextField tbpallets;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbserial;
    private javax.swing.JTextField tbshipper;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
