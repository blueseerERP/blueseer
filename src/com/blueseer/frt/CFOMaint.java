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
package com.blueseer.frt;


import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDILogBrowse;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import com.blueseer.utl.EDData;
import static com.blueseer.utl.OVData.updateFreightOrderStatus;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *
 * @author vaughnte
 */
public class CFOMaint extends javax.swing.JPanel {
    
    
    public String globalstatus = "Open";
    public ArrayList<String[]> tablelist = new ArrayList<String[]>();
    
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("number"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("shipper"), 
                getGlobalColumnTag("city"), 
                getGlobalColumnTag("state"), 
                getGlobalColumnTag("zip")
            });
      
   public boolean lock_ddshipper = false;
    
   
   
     class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
   
     class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 2);  
        
         if ("LD".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("UL".equals(status)) {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        } else if ("PU".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
   
   
     public void getOrder(String mykey) {
        
        initvars(null);
        
        btlookup.setEnabled(false);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btadd.setEnabled(false);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        
        String dir = "";  // values are "In" or "Out"...both EDI in and out transactions are accomodated by this maintenance screen..so direction is critical
        
        
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
                int i = 0;
                res = st.executeQuery("select * from fo_mstr where fo_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    freightorder.setText(mykey);
                    ddstatus.setSelectedItem(res.getString("fo_status"));
                    ddvehicle.setSelectedItem(res.getString("fo_carrier_assigned"));
                    dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("fo_date")));
                    tbforevision.setText(res.getString("fo_weight"));
                    tbref.setText(res.getString("fo_ref"));
                    tbtrailer.setText(res.getString("fo_trailer"));
                    tbmileage.setText(res.getString("fo_milerate"));
                    tbcharges.setText(res.getString("fo_totmiles"));
                    tbnetprice.setText(res.getString("fo_totcost"));
                    dir = res.getString("fo_dir");
                   
                }
               
          
                res = st.executeQuery("select * from fod_det where fod_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{
                      res.getString("fod_line"), 
                      res.getString("fod_nbr"),
                      res.getString("fod_type"), 
                      res.getString("fod_shipper"),
                      res.getString("fod_city"),
                      res.getString("fod_state"),
                      res.getString("fod_zip")
                  });
                  String[] k = new String[]{
                      res.getString("fod_line"),
                      res.getString("fod_nbr"),
                      res.getString("fod_type"), 
                      res.getString("fod_shipper"), 
                      res.getString("fod_ref"),
                      res.getString("fod_name"),
                      res.getString("fod_addr1"),
                      res.getString("fod_addr2"),
                      res.getString("fod_city"),
                      res.getString("fod_state"),
                      res.getString("fod_zip"),
                      res.getString("fod_contact"),
                      res.getString("fod_phone"),
                      res.getString("fod_email"),
                      res.getString("fod_units"), 
                      res.getString("fod_weight"),
                      res.getString("fod_delvdate"),
                      res.getString("fod_delvtime"),
                      res.getString("fod_shipdate"),
                      res.getString("fod_shiptime")
                  };
                  tablelist.add(k);
                  
                }
                
               
                orddet.setModel(myorddetmodel);
               
                sumweight();
               
               
                // set appropriate color status and enablement
                String status = ddstatus.getSelectedItem().toString();
                globalstatus = status;
               
                if (i == 0) {
                   bsmf.MainFrame.show(getMessageTag(1001));
                } else {
                    
                            if (status.compareTo("Open") == 0) {
                                ddstatus.setBackground(null); 
                                enableAll();
                                btadd.setEnabled(false);
                            } else if (status.compareTo("Tendered") == 0) {
                                enableAll();
                                ddstatus.setBackground(Color.blue);
                                btadd.setEnabled(false);
                            } else if (status.compareTo("Close") == 0) {
                                ddstatus.setBackground(Color.gray);
                                // disableAll();
                                btnew.setEnabled(true);
                                btlookup.setEnabled(true);
                                btprint.setEnabled(true);
                                btadd.setEnabled(false);
                                btcommit.setEnabled(false);
                                btupdate.setEnabled(false);
                                btadditem.setEnabled(false);
                                btupdateitem.setEnabled(false);
                                btdelitem.setEnabled(false);
                            } else if (status.compareTo("Declined") == 0) {
                                ddstatus.setBackground(Color.red);
                                enableAll();
                                btadd.setEnabled(false);
                                
                            } else if (status.compareTo("Accepted") == 0) {
                                enableAll();
                                ddstatus.setBackground(Color.green);
                                btadd.setEnabled(false);
                               
                               
                            } else {
                                ddstatus.setBackground(null); 
                                enableAll();
                            }
                          
                   
                   // now get tenders
               //    getTenders(mykey);
                   
                   // now get status 214s
                 //  getStatus(mykey);
                            
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
    
    
    
    
    
    public void getStatus(String nbr) {
        
        
    }  
    
    public void sumweight() {
        double qty = 0;
        for (String[] x : tablelist) {
            qty = qty + Double.valueOf(x[15]); 
        }
        totweight.setText(String.valueOf(qty));
    }
    
    
    public void sumTotal() {
        double dol = 0;
        if (! tbforate.getText().isBlank() && ! tbcharges.getText().isBlank()) {
        dol = Double.valueOf(tbforate.getText()) * Double.valueOf(tbcharges.getText());
        tbnetprice.setText(String.valueOf(dol));
        }
    }
    
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
   
       
    
    public void shipperChangeEvent(String mykey) {
            
          //initialize weight and unites
          
     
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
                
                res = st.executeQuery("select * from ship_mstr inner join cm_mstr on cm_code = sh_cust inner join cms_det on cms_code = cm_code and cms_shipto = sh_ship " +
                        " inner join wh_mstr on wh_id = sh_wh " +
                        " where sh_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    tbname.setText(res.getString("cms_name"));
                    tbaddr1.setText(res.getString("cms_line1"));
                    tbaddr2.setText(res.getString("cms_line2"));
                    tbcity.setText(res.getString("cms_city"));
                    dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    tbzip.setText(res.getString("cms_zip"));
                    ddstate.setSelectedItem(res.getString("cms_state"));
                 
                }
                     
            
            } catch (SQLException s) {
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
    
    
      
   
    public CFOMaint() {
        initComponents();
        setLanguageTags(this);
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
    
    
     public void clearAddrFields() {
                    tbname.setText("");
                    tbaddr1.setText("");
                    tbaddr2.setText("");
                    tbcity.setText("");
                    tbzip.setText("");
                    tbphone.setText("");
                    tbemail.setText("");
                    tbcontact.setText("");
                    tbforate.setText("");
                    tbforevision.setText("");
                  
                   
                    ddstate.setSelectedIndex(0);
                    
     }
    
     public void enableAll() {
         
         
     //   freightorder.setEnabled(true);
       
        ddvehicle.setEnabled(true);
        btcommit.setEnabled(true);
       
       
        dcdate.setEnabled(true);
       
        ddstatus.setEnabled(true);
       
        tbname.setEnabled(true);
        tbaddr1.setEnabled(true);
        tbaddr2.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        tbcontact.setEnabled(true);
        tbphone.setEnabled(true);
        tbremarks.setEnabled(true);
        tbemail.setEnabled(true);
        ddtime.setEnabled(true);
        tbref.setEnabled(true);
        totweight.setEnabled(true);
       tbforevision.setEnabled(true);
        tbforate.setEnabled(true);
        tbmisc.setEnabled(true);
        ddcust.setEnabled(true);
        ddequiptype.setEnabled(true);
        ddstate.setEnabled(true);
        ddservice.setEnabled(true);
        
        orddet.setEnabled(true);
        
       
        totweight.setEnabled(true);
        tbcharges.setEnabled(true);
        tbnetprice.setEnabled(true);
        tbforate.setEnabled(true);
        tbtrailer.setEnabled(true);
          btlookup.setEnabled(true);
        
          btprint.setEnabled(true);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        btupdateitem.setEnabled(true);
        
    }
    
     public void disableAll() {
        freightorder.setEnabled(false);
        dcdate.setEnabled(false);
      
        ddvehicle.setEnabled(false);
        btcommit.setEnabled(false);
        
        ddstatus.setEnabled(false);
         tbname.setEnabled(false);
        tbaddr1.setEnabled(false);
        tbaddr2.setEnabled(false);
        tbcity.setEnabled(false);
        tbzip.setEnabled(false);
        tbcontact.setEnabled(false);
        tbphone.setEnabled(false);
        tbremarks.setEnabled(false);
        tbemail.setEnabled(false);
        ddtime.setEnabled(false);
        totweight.setEnabled(false);
        tbforevision.setEnabled(false);
        tbforate.setEnabled(false);
        tbforevision.setEnabled(false);
        tbforate.setEnabled(false);
        tbref.setEnabled(false);
        tbtrailer.setEnabled(false);
       
        tbmisc.setEnabled(false);
        ddcust.setEnabled(false);
        ddequiptype.setEnabled(false);
        ddstate.setEnabled(false);
        ddservice.setEnabled(false);
        tbcharges.setEnabled(false);
        tbnetprice.setEnabled(false);
        tbforate.setEnabled(false);
      
      
        
        orddet.setEnabled(false);
        
        totweight.setEnabled(false);
       
        
          btlookup.setEnabled(false);
         
          btprint.setEnabled(false);
        btnew.setEnabled(false);
        btupdate.setEnabled(false);
        btadd.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
        btupdateitem.setEnabled(false);
    }
    
    public void initvars(String[] arg) {
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("Location", jPanelLocation);
      //  jTabbedPane1.add("Load", jPanelLoad);
      //  jTabbedPane1.add("Quotes", jPanelQuotes);
    //    jTabbedPane1.add("Tenders", jPanelTenders);
    //    jTabbedPane1.add("Status", jPanelStatus);
        
   
         
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
        freightorder.setText("");
       
        ddstatus.setBackground(null);
        ddstatus.setSelectedItem("open");
        dcdate.setDate(now);
       
        tablelist.clear();
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
       
        
        btlookup.setEnabled(true);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btupdateitem.setEnabled(true);
        btdelitem.setEnabled(true);
      
        
        
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbcontact.setText("");
        tbphone.setText("");
        tbremarks.setText("");
        tbemail.setText("");
        tbref.setText("");
        totweight.setText("");
        tbcharges.setText("");
        tbnetprice.setText("");
        tbforate.setText("");
        tbtrailer.setText("");
        
        
     
       
        
        
        
        
       
        
      
        
        
         ddequiptype.removeAllItems();
         ArrayList<String> equiptypes = OVData.getCodeMstrKeyList("equiptype");
        for (String code : equiptypes) {
            ddequiptype.addItem(code);
        }
        if (ddequiptype.getItemCount() == 0) {
            ddequiptype.addItem("None");
            ddequiptype.setSelectedIndex(0);
        }
        
        
        
      
        
        ddstate.removeAllItems();
        ddstate.addItem("");
        OVData.getCodeMstrKeyList("state").stream().forEach((s) -> ddstate.addItem(s));
        ddstate.setSelectedIndex(0);
        
        
         
        if (arg != null && arg.length > 0) {
            getOrder(arg[0]);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btlookup.setEnabled(true);
              btcommit.setEnabled(false);
        }
          
          
    }
    
     public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getFOBrowseUtil(luinput.getText(),0, "fo_nbr");
        } else {
         luModel = DTData.getFOBrowseUtil(luinput.getText(),0, "fo_carrier");   
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
                getClassLabelTag("lblcarrier", this.getClass().getSimpleName())); 
        
    }

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        freightorder = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        ddvehicle = new javax.swing.JComboBox();
        jLabel101 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        ddequiptype = new javax.swing.JComboBox();
        ddservice = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tbtrailer = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbforevision = new javax.swing.JTextField();
        tbforate = new javax.swing.JTextField();
        tbcustfo = new javax.swing.JTextField();
        ddcust = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbdrivercell = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ddfotype = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        ddbroker = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        tbbrokercontact = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tbbrokercell = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        ddratetype = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        tbdriverrate = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        totweight = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        dcloaddate = new com.toedter.calendar.JDateChooser();
        dcunloaddate = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cbhazmat = new javax.swing.JCheckBox();
        ddstatus = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        tbmileage = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        cbstandard = new javax.swing.JCheckBox();
        dddriver = new javax.swing.JComboBox<>();
        tbcharges = new javax.swing.JTextField();
        tbnetprice = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btclear = new javax.swing.JButton();
        tbcost = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jPanelLocation = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btadditem = new javax.swing.JButton();
        btupdateitem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        ddstate = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ddstate1 = new javax.swing.JComboBox();
        tbmisc = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        ddstoptype = new javax.swing.JComboBox<>();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listdatetime = new javax.swing.JList<>();
        dcdate = new com.toedter.calendar.JDateChooser();
        dddatetimetype = new javax.swing.JComboBox<>();
        ddtime = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        tbref4 = new javax.swing.JTextField();
        tbref = new javax.swing.JTextField();
        tbref3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbref1 = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        tbref5 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tbref2 = new javax.swing.JTextField();
        jComboBox7 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        jTextField1.setText("jTextField1");

        jLabel3.setText("jLabel3");

        jLabel10.setText("jLabel10");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Freight Order Maintenance"));
        jPanelMain.setName("panelmain"); // NOI18N

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

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

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel101.setText("Truck ID");
        jLabel101.setName("lblcarrier"); // NOI18N

        jLabel85.setText("EquipType");
        jLabel85.setName("lblequipmenttype"); // NOI18N

        jLabel9.setText("Service");

        jLabel15.setText("Driver");

        jLabel16.setText("Trailer");

        jLabel77.setText("Client Order");
        jLabel77.setName("lblshipper"); // NOI18N

        jLabel23.setText("Rate");

        tbforevision.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbforevisionFocusLost(evt);
            }
        });

        tbforate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbforateFocusLost(evt);
            }
        });

        jLabel5.setText("Client");

        jLabel6.setText("Revision");

        tbdrivercell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdrivercellFocusLost(evt);
            }
        });

        jLabel7.setText("Cell#");

        ddfotype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Spot", "Broker" }));

        jLabel24.setText("Type");

        jLabel28.setText("Broker");

        jLabel29.setText("Broker Contact");

        jLabel30.setText("Broker Cell#");

        ddratetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flat Rate", "Mileage Rate" }));

        jLabel31.setText("Rate Type");

        tbdriverrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdriverrateFocusLost(evt);
            }
        });

        jLabel32.setText("Mileage");

        totweight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                totweightFocusLost(evt);
            }
        });

        jLabel33.setText("Weight");

        dcloaddate.setDateFormatString("yyyy-MM-dd");

        dcunloaddate.setDateFormatString("yyyy-MM-dd");

        jLabel34.setText("Load Date");

        jLabel35.setText("Unload Date");

        cbhazmat.setText("Hazmat");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Open", "Quoted", "Tendered", "Accepted", "Declined", "Cancelled", "InTransit", "Delivered", "Close" }));

        jLabel36.setText("Status");

        tbmileage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmileageFocusLost(evt);
            }
        });

        jLabel37.setText("Driver Rate");

        cbstandard.setText("Standard");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel101, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbforevision, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddservice, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbcustfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                                .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel7)
                            .addComponent(jLabel24)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30)
                            .addComponent(jLabel36))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbbrokercell, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddfotype, javax.swing.GroupLayout.Alignment.LEADING, 0, 131, Short.MAX_VALUE)
                                    .addComponent(tbdrivercell, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dddriver, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcunloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddratetype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbforate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(tbdriverrate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(totweight, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(tbmileage, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbstandard))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(cbhazmat)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(ddratetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcustfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77)
                            .addComponent(jLabel15)
                            .addComponent(tbforate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(dddriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbforevision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(tbdrivercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(tbmileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(tbdriverrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(cbstandard))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85)
                            .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)
                            .addComponent(totweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101)
                            .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)
                            .addComponent(jLabel34)))
                    .addComponent(dcloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbbrokercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(jLabel35))
                    .addComponent(jLabel16)
                    .addComponent(dcunloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbhazmat)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        tbcharges.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbchargesFocusLost(evt);
            }
        });

        jLabel13.setText("Charges");

        jLabel14.setText("Net Price");

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        orddet.setModel(new javax.swing.table.DefaultTableModel(
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
        orddet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orddetMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(orddet);

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        tbcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcostFocusLost(evt);
            }
        });

        jLabel38.setText("Cost");

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(jLabel76)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(freightorder, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel13)
                                .addGap(3, 3, 3)
                                .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbnetprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btcommit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btprint)
                                .addGap(7, 7, 7)
                                .addComponent(btupdate)
                                .addGap(12, 12, 12)
                                .addComponent(btadd)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel76)
                                .addComponent(freightorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnew)
                            .addComponent(btclear)))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btprint)
                        .addComponent(tbnetprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(btcommit))
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38))))
        );

        add(jPanelMain);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btadditem.setText("Add Line");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btupdateitem.setText("Update Line");

        btdelitem.setText("Delete Line");
        btdelitem.setName("btdeleteitem"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        jLabel18.setText("Country");

        jLabel88.setText("Contact");
        jLabel88.setName("lblcontact"); // NOI18N

        jLabel96.setText("Email");
        jLabel96.setName("lblemail"); // NOI18N

        jLabel86.setText("Misc");
        jLabel86.setName("lblmisc"); // NOI18N

        jLabel93.setText("City");
        jLabel93.setName("lblcity"); // NOI18N

        jLabel97.setText("Remarks");
        jLabel97.setName("lblremarks"); // NOI18N

        jLabel17.setText("Location");

        jLabel91.setText("Addr1");
        jLabel91.setName("lbladdr1"); // NOI18N

        jLabel94.setText("State");
        jLabel94.setName("lblstate"); // NOI18N

        jLabel89.setText("Phone");
        jLabel89.setName("lblphone"); // NOI18N

        jLabel82.setText("Name");
        jLabel82.setName("lblname"); // NOI18N

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel90.setText("Addr2");
        jLabel90.setName("lbladdr2"); // NOI18N

        ddstoptype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Load", "Unload Complete", "Unload Partial" }));

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduling"));

        jScrollPane7.setViewportView(listdatetime);

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel25.setText("Time");

        jLabel26.setText("Date");

        jLabel27.setText("Event");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddtime, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dddatetimetype, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddatetimetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setText("Stop Type");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82)
                    .addComponent(jLabel91)
                    .addComponent(jLabel90)
                    .addComponent(jLabel93)
                    .addComponent(jLabel94)
                    .addComponent(jLabel97)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbremarks)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tbaddr1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                            .addComponent(tbname, javax.swing.GroupLayout.Alignment.LEADING)))
                                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel89)
                                    .addComponent(jLabel96)
                                    .addComponent(jLabel88)
                                    .addComponent(jLabel86)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcontact, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                    .addComponent(tbemail)
                                    .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(ddstate1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel82))
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel96)))
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addComponent(jLabel91))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel90))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel93))
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel88))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel89))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel86))))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel94))
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddstate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)))
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Location Order Info"));

        jLabel95.setText("Weight");
        jLabel95.setName("lblref"); // NOI18N

        jLabel8.setText("Order Number");

        jLabel83.setText("Reference");
        jLabel83.setName("lblref"); // NOI18N

        jLabel21.setText("Hazard Code");

        jLabel19.setText("Pallet Count");

        jLabel20.setText("Lading Qty");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel95))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(tbref1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(tbref3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbref4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbref5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(tbref2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83)
                    .addComponent(tbref2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel95)
                    .addComponent(tbref4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tbref3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(tbref5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGap(92, 92, 92))
        );

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel22.setText("Stop Sequence");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btadditem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdateitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelitem)
                .addContainerGap())
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem)
                    .addComponent(btupdateitem))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLocationLayout = new javax.swing.GroupLayout(jPanelLocation);
        jPanelLocation.setLayout(jPanelLocationLayout);
        jPanelLocationLayout.setHorizontalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLocationLayout.setVerticalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        add(jPanelLocation);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     
         initvars(null);
        
        
         freightorder.setText(String.valueOf(OVData.getNextNbr("fo")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
           
                dcdate.setDate(now);
               
                
                // tbDateShippedSM.setEnabled(false);
                
                enableAll();
                
                ddstatus.setEnabled(false);
                btnew.setEnabled(false);
                btlookup.setEnabled(false);
                btupdate.setEnabled(false);
                btprint.setEnabled(false);
               
                
                 
               
               
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String shipper = "";
        String custpart = "";
        
      
        
        if (tbzip.getText().toString().isEmpty() ||
            tbname.getText().toString().isEmpty() ||
            tbaddr1.getText().toString().isEmpty() ||    
            tbcity.getText().toString().isEmpty() ||
            ddstate.getSelectedItem().toString().isEmpty()    
             ) {
            bsmf.MainFrame.show(getMessageTag(1138));
            canproceed = false;
            return;
        }
        
        
        orddet.setModel(myorddetmodel);
         Enumeration<TableColumn> en = orddet.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new SomeRenderer()); 
                 }
        
                 
          // if weight and units are blank....make 0
          if (tbforevision.getText().toString().isEmpty()) {
              tbforevision.setText("0");
          }
           if (tbforate.getText().toString().isEmpty()) {
              tbforate.setText("0");
          }
                 
        // check formatting
         Pattern p = Pattern.compile("^[0-9]\\d*$");
                 // Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbforevision.getText());
        if (!m.find() || tbforevision.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbforevision.requestFocus();
            return;
        }
        
         p = Pattern.compile("^[0-9]\\d*$");
        m = p.matcher(tbforate.getText());
        if (!m.find() || tbforate.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbforate.requestFocus();
            return;
        }
        
       
        line = getmaxline();
        line++;
        if (canproceed) {
            
            // let's first add the pickup...only if this is first time add
            if (myorddetmodel.getRowCount() == 0) {
                String[] addr = OVData.getWareHouseAddressElements(OVData.getDefaultSite());
                 myorddetmodel.addRow(new Object[]{0, freightorder.getText(), "LD", "", addr[5], addr[6], addr[7]});
            
                String[] k = new String[]{     
                "0", freightorder.getText(), "LD", "", "", addr[1],
                addr[2], addr[3], addr[5], addr[6], addr[7], tbcontact.getText(),
                tbphone.getText(), "", "0", "0", "0000-00-00", "", dfdate.format(dcdate.getDate()), ddtime.getSelectedItem().toString()                };
                tablelist.add(k);
            }
            
            String[] j = new String[]{String.valueOf(line), freightorder.getText(), "UL", ddcust.getSelectedItem().toString(), tbref.getText(), tbname.getText(),
            tbaddr1.getText(), tbaddr2.getText(), tbcity.getText(), ddstate.getSelectedItem().toString(), tbzip.getText(), tbcontact.getText(),
            tbphone.getText(), tbemail.getText(), tbforate.getText(), tbforevision.getText(), dfdate.format(dcdate.getDate()), ddtime.getSelectedItem().toString(), "0000-00-00", ""};
            tablelist.add(j);
            
                myorddetmodel.addRow(new Object[]{line, freightorder.getText(), "UL", ddcust.getSelectedItem().toString(), 
                tbcity.getText(), ddstate.getSelectedItem().toString(), tbzip.getText()});
       
                sumweight();
         
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
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
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                String site = OVData.getDefaultSite();
                if (proceed) {
                    st.executeUpdate("insert into fo_mstr "
                        + "(fo_nbr, fo_date, fo_ref, fo_carrier, fo_carrier_assigned, fo_equipment_type, fo_service_type, fo_status, fo_trailer, fo_rmks, fo_milerate, fo_totmiles, fo_totcost ) "
                        + " values ( " + "'" + freightorder.getText().toString() + "'" + ","
                         + "'" + dfdate.format(now) + "'" + ","
                        + "'" + tbref.getText() + "'" + ","
                        + "'" + ddvehicle.getSelectedItem() + "'" + ","
                        + "'" + ddvehicle.getSelectedItem() + "'" + ","
                        + "'" + ddequiptype.getSelectedItem() + "'" + ","
                        + "'" + ddservice.getSelectedItem() + "'" + ","        
                        + "'" + ddstatus.getSelectedItem() + "'" + ","  
                        + "'" + tbtrailer.getText() + "'" + "," 
                        + "'" + tbremarks.getText() + "'" + ","         
                        + "'" + tbforate.getText() + "'" + "," 
                        + "'" + tbcharges.getText() + "'" + "," 
                        + "'" + tbnetprice.getText() + "'"     
                        + ")"
                        + ";");

                  
                        for (String[] v : tablelist) {
                        st.executeUpdate("insert into fod_det "
                            + "(fod_line, fod_nbr, fod_type, fod_shipper, fod_ref, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, "
                            + " fod_contact, fod_phone, fod_email, fod_units, fod_weight, fod_delvdate, fod_delvtime, fod_shipdate, fod_shiptime ) "
                            + " values ( " 
                            + "'" + v[0] + "'" + ","
                            + "'" + v[1] + "'" + ","
                            + "'" + v[2] + "'" + ","
                            + "'" + v[3] + "'" + ","
                            + "'" + v[4] + "'" + ","
                            + "'" + v[5] + "'" + ","
                            + "'" + v[6] + "'" + ","
                            + "'" + v[7] + "'" + ","
                            + "'" + v[8] + "'" + ","
                            + "'" + v[9] + "'" + ","
                            + "'" + v[10] + "'" + ","
                            + "'" + v[11] + "'" + ","
                            + "'" + v[12] + "'" + ","
                            + "'" + v[13] + "'" + ","
                            + "'" + v[14] + "'" + ","
                            + "'" + v[15] + "'" + ","
                            + "'" + v[16] + "'" + ","
                            + "'" + v[17] + "'" + ","
                            + "'" + v[18] + "'" + ","
                            + "'" + v[19] + "'"         
                            + ")"
                            + ";");
                       
                        }
                   
                    
                    // update every shipper with freight order number assignment...sh_freight = freightorder
                    shpData.updateShipperWithFreightOrder(tablelist);
                    
                    bsmf.MainFrame.show(getMessageTag(1007));
                   initvars(null);
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
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        
        int[] rows = orddet.getSelectedRows();
        ArrayList<String[]> newlist = new ArrayList<String[]>();
        for (int i : rows) {
            if (orddet.getValueAt(i, 2).toString().equals("LD")) {
                bsmf.MainFrame.show(getMessageTag(1046));
                return;
            } else {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            
            for (String[] x : tablelist) {
                if (Integer.valueOf(x[0]) != i) {
                    newlist.add(x);
                }
            }
            
            }
        }
        tablelist = newlist;
       
         
         
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
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
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String site = OVData.getDefaultSite();
                if (proceed) {
                    st.executeUpdate("update fo_mstr set "
                            + " fo_ref = " + "'" + tbref.getText() + "'" + ","
                            + " fo_carrier = " + "'" + ddvehicle.getSelectedItem() + "'" + ","
                            + " fo_carrier_assigned = " + "'" + ddvehicle.getSelectedItem() + "'" + ","   
                            + " fo_rmks = " + "'" + tbremarks.getText() + "'" + ","
                            + " fo_equipment_type = " + "'" + ddequiptype.getSelectedItem() + "'" + ","
                            + " fo_service_type = " + "'" + ddservice.getSelectedItem() + "'" + ","
                            + " fo_trailer = " + "'" + tbtrailer.getText() + "'" + ","        
                            + " fo_milerate = " + "'" + tbforate.getText() + "'" + ","
                            + " fo_totmiles = " + "'" + tbcharges.getText() + "'" + ","
                            + " fo_totcost = " + "'" + tbnetprice.getText() + "'"         
                            + " where fo_nbr = " + "'" + freightorder.getText() + "'" 
                        + ";");

                   st.executeUpdate(" delete from fod_det where fod_nbr = " + "'" + freightorder.getText() + "'" + ";");
                 
                    for (String[] v : tablelist) {
                        st.executeUpdate("insert into fod_det "
                            + "(fod_line, fod_nbr, fod_type, fod_shipper, fod_ref, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, "
                            + " fod_contact, fod_phone, fod_email, fod_units, fod_weight, fod_delvdate, fod_delvtime, fod_shipdate, fod_shiptime ) "
                            + " values ( " 
                            + "'" + v[0] + "'" + ","
                            + "'" + v[1] + "'" + ","
                            + "'" + v[2] + "'" + ","
                            + "'" + v[3] + "'" + ","
                            + "'" + v[4] + "'" + ","
                            + "'" + v[5] + "'" + ","
                            + "'" + v[6] + "'" + ","
                            + "'" + v[7] + "'" + ","
                            + "'" + v[8] + "'" + ","
                            + "'" + v[9] + "'" + ","
                            + "'" + v[10] + "'" + ","
                            + "'" + v[11] + "'" + ","
                            + "'" + v[12] + "'" + ","
                            + "'" + v[13] + "'" + ","
                            + "'" + v[14] + "'" + ","
                            + "'" + v[15] + "'" + ","
                            + "'" + v[16] + "'" + ","
                            + "'" + v[17] + "'" + ","
                            + "'" + v[18] + "'" + ","
                            + "'" + v[19] + "'"         
                            + ")"
                            + ";");
                       
                        }
                   
                    
                    // update every shipper with freight order number assignment...sh_freight = freightorder
                    shpData.updateShipperWithFreightOrder(tablelist);
                    
                    bsmf.MainFrame.show(getMessageTag(1008));
                   initvars(null);
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
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       OVData.printPurchaseOrder(freightorder.getText(), false);
    }//GEN-LAST:event_btprintActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        String[] v = null;
        for (String[] x : tablelist) {
          //  System.out.println("HERE: " + x[0] + "/" + orddet.getValueAt(row, 0).toString() );
            if (x[0].equals(orddet.getValueAt(row, 0).toString())) {
                v = x;
            }
        }
        
        
        tbname.setText(v[5]);
        tbaddr1.setText(v[6]);
        tbaddr2.setText(v[7]);
        tbcity.setText(v[8]);
        ddstate.setSelectedItem(v[9]);
        tbzip.setText(v[10]);
        
        tbcontact.setText(v[11]);
        tbphone.setText(v[12]);
        tbemail.setText(v[13]);
        tbforate.setText(v[14]);
       // tbweight.setText(orddet.getValueAt(row, 15).toString());
        if (v[16].isEmpty() || v[16].equals("0000-00-00")) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[16]));   
        }
        if (v[18].isEmpty() || v[18].equals("0000-00-00") ) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[18]));   
        }
        ddtime.setSelectedItem(v[19]);
    }//GEN-LAST:event_orddetMouseClicked

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbchargesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbchargesFocusLost
        if (! tbcharges.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbcharges.getText(), "0");
        if (x.equals("error")) {
            tbcharges.setText("");
            tbcharges.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbcharges.requestFocus();
        } else {
            tbcharges.setText(x);
            tbcharges.setBackground(Color.white);
        }
        sumTotal();
        }
    }//GEN-LAST:event_tbchargesFocusLost

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        java.util.Date now = new java.util.Date();
        for (int j = 0; j < orddet.getRowCount(); j++) {
            if (! orddet.getValueAt(j, 3).toString().isBlank()) {
            String[] message = confirmShipperTransaction("order", orddet.getValueAt(j, 3).toString(), now);
            updateFreightOrderStatus(freightorder.getText(),"Close");
            bsmf.MainFrame.show("committed shipper: " + orddet.getValueAt(j, 3).toString());
            }
         } 
        
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbforevisionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbforevisionFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbforevisionFocusLost

    private void tbforateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbforateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbforateFocusLost

    private void tbdrivercellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdrivercellFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbdrivercellFocusLost

    private void tbdriverrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdriverrateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbdriverrateFocusLost

    private void totweightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totweightFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_totweightFocusLost

    private void tbmileageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbmileageFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbmileageFocusLost

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcostFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbcostFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbhazmat;
    private javax.swing.JCheckBox cbstandard;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcloaddate;
    private com.toedter.calendar.JDateChooser dcunloaddate;
    private javax.swing.JComboBox<String> ddbroker;
    private javax.swing.JComboBox<String> ddcust;
    private javax.swing.JComboBox<String> dddatetimetype;
    private javax.swing.JComboBox<String> dddriver;
    private javax.swing.JComboBox ddequiptype;
    private javax.swing.JComboBox<String> ddfotype;
    private javax.swing.JComboBox<String> ddratetype;
    private javax.swing.JComboBox<String> ddservice;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddstate1;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddstoptype;
    private javax.swing.JComboBox<String> ddtime;
    private javax.swing.JComboBox ddvehicle;
    private javax.swing.JTextField freightorder;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JList<String> listdatetime;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbbrokercell;
    private javax.swing.JTextField tbbrokercontact;
    private javax.swing.JTextField tbcharges;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbcustfo;
    private javax.swing.JTextField tbdrivercell;
    private javax.swing.JTextField tbdriverrate;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbforate;
    private javax.swing.JTextField tbforevision;
    private javax.swing.JTextField tbmileage;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbnetprice;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbref1;
    private javax.swing.JTextField tbref2;
    private javax.swing.JTextField tbref3;
    private javax.swing.JTextField tbref4;
    private javax.swing.JTextField tbref5;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbzip;
    private javax.swing.JTextField totweight;
    // End of variables declaration//GEN-END:variables
}
