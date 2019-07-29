/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.frt;


import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDILogBrowse;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class FOMaint extends javax.swing.JPanel {
    
    
    public String globalstatus = "Open";

   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "line", "FONbr", "Type", "Shipper", "Ref", "Name", "Addr1", "Addr2", "City", "State", "Zip", "Contact", "Phone", "Email", "Units", "Weight", "DelvDate", "DelvTime", "ShipDate", "ShipTime"
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
        
        initvars("");
        
        btbrowse.setEnabled(false);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(false);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        
        String dir = "";  // values are "In" or "Out"...both EDI in and out transactions are accomodated by this maintenance screen..so direction is critical
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from fo_mstr where fo_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    freightorder.setText(mykey);
                    ddstatus.setSelectedItem(res.getString("fo_status"));
                    ddcarrierproposed.setSelectedItem(res.getString("fo_carrier"));
                    ddcarrierassigned.setSelectedItem(res.getString("fo_carrier_assigned"));
                    shipdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("fo_date")));
                    tbcustfrtnbr.setText(res.getString("fo_custfo"));
                    tbcust.setText(res.getString("fo_cust"));
                    tbtpid.setText(res.getString("fo_tpid"));
                    dir = res.getString("fo_dir");
                   
                }
               
          
                res = st.executeQuery("select * from fod_det where fod_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("fod_line"), res.getString("fod_nbr"),
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
                  
                  });
                }
                
               
                orddet.setModel(myorddetmodel);
                
                // don't need tos how all these...but need to keep columns for field updates on tableclick
                // this is the only way I know of to 'hide' columns.
                orddet.getColumnModel().getColumn(6).setMaxWidth(0);
                orddet.getColumnModel().getColumn(6).setMinWidth(0);
                orddet.getColumnModel().getColumn(6).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(7).setMaxWidth(0);
                orddet.getColumnModel().getColumn(7).setMinWidth(0);
                orddet.getColumnModel().getColumn(7).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(8).setMaxWidth(0);
                orddet.getColumnModel().getColumn(8).setMinWidth(0);
                orddet.getColumnModel().getColumn(8).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(9).setMaxWidth(0);
                orddet.getColumnModel().getColumn(9).setMinWidth(0);
                orddet.getColumnModel().getColumn(9).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(10).setMaxWidth(0);
                orddet.getColumnModel().getColumn(10).setMinWidth(0);
                orddet.getColumnModel().getColumn(10).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(11).setMaxWidth(0);
                orddet.getColumnModel().getColumn(11).setMinWidth(0);
                orddet.getColumnModel().getColumn(11).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(12).setMaxWidth(0);
                orddet.getColumnModel().getColumn(12).setMinWidth(0);
                orddet.getColumnModel().getColumn(12).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(13).setMaxWidth(0);
                orddet.getColumnModel().getColumn(13).setMinWidth(0);
                orddet.getColumnModel().getColumn(13).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(16).setMaxWidth(0);
                orddet.getColumnModel().getColumn(16).setMinWidth(0);
                orddet.getColumnModel().getColumn(16).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(17).setMaxWidth(0);
                orddet.getColumnModel().getColumn(17).setMinWidth(0);
                orddet.getColumnModel().getColumn(17).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(18).setMaxWidth(0);
                orddet.getColumnModel().getColumn(18).setMinWidth(0);
                orddet.getColumnModel().getColumn(18).setPreferredWidth(0);
                orddet.getColumnModel().getColumn(19).setMaxWidth(0);
                orddet.getColumnModel().getColumn(19).setMinWidth(0);
                orddet.getColumnModel().getColumn(19).setPreferredWidth(0);
                 
               
                sumweight();
                sumunits();
               
                // set appropriate color status and enablement
                String status = ddstatus.getSelectedItem().toString();
                globalstatus = status;
               
                if (i == 0) {
                   bsmf.MainFrame.show("No Order Record found for " + mykey);
                } else {
                    
                            if (status.compareTo("Open") == 0) {
                                ddstatus.setBackground(null); 
                                enableAll();
                                btadd.setEnabled(false);
                            } else if (status.compareTo("Quoted") == 0) {
                                enableAll();
                                ddstatus.setBackground(Color.yellow);
                                btquote.setEnabled(false);
                                btadd.setEnabled(false);
                            } else if (status.compareTo("Tendered") == 0) {
                                enableAll();
                                ddstatus.setBackground(Color.blue);
                                btquote.setEnabled(false);
                                bttender.setEnabled(false);
                                btadd.setEnabled(false);
                            } else if (status.compareTo("Close") == 0) {
                                ddstatus.setBackground(Color.gray);
                                disableAll();
                                btnew.setEnabled(true);
                            } else if (status.compareTo("Declined") == 0) {
                                ddstatus.setBackground(Color.red);
                                enableAll();
                                btquote.setEnabled(false);
                                bttender.setEnabled(false);
                                btadd.setEnabled(false);
                                btaccept.setEnabled(false);
                                btdecline.setEnabled(false);
                            } else if (status.compareTo("Accepted") == 0) {
                                enableAll();
                                ddstatus.setBackground(Color.green);
                                btquote.setEnabled(false);
                                bttender.setEnabled(false);
                                btadd.setEnabled(false);
                                btaccept.setEnabled(false);
                                btdecline.setEnabled(false);
                               
                            } else {
                                ddstatus.setBackground(null); 
                                enableAll();
                            }
                            
                           
                    
                    // now lets get any quotes to the quotes table
                   getQuotes(mykey);
                   
                   // now get tenders
                   getTenders(mykey);
                   
                   // now get status 214s
                   getStatus(mykey);
                            
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve fo_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void showMap(String latitude, String longitude) {
        StatusTablePanel.setVisible(false);
        RawFileStatusPanel.setVisible(false);
        
        try {
       // String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=40.714%2c%20-73.998&zoom=12&size=400x400&key=AIzaSyAG7xKaSEqdME6sCnRhSn_4ya_T59ihnIM";
        String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
        + latitude
        + ","
        + longitude
        //+ "&zoom=11&size=400x400&key=AIzaSyAG7xKaSEqdME6sCnRhSn_4ya_T59ihnIM" ;
       + "&zoom=8&size=840x580&scale=2&maptype=roadmap&markers=color:green|size:mid|label:H|" + latitude + "," + longitude ;
        String destinationFile = "fomap.jpg";
        // read the map image from Google
        // then save it to a local file: image.jpg
        //
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
        os.write(b, 0, length);
        }
        is.close();
        os.close();
        } catch (IOException e) {
        MainFrame.bslog(e);
        System.exit(1);
        }
        
        
        
        ImageIcon imageIcon = new ImageIcon((new ImageIcon("fomap.jpg"))
        .getImage().getScaledInstance(840, 580,
        java.awt.Image.SCALE_SMOOTH));
        labelmap.setIcon(imageIcon);
        labelmap.setVisible(true);
        bthidemap.setVisible(true);
    }
  
    public void hideMap() {
        StatusTablePanel.setVisible(true);
        RawFileStatusPanel.setVisible(false);
        labelmap.setVisible(false);
        bthidemap.setVisible(false);
    }
    
    
    public void getQuotes(String nbr) {
        
          tablequotes.setModel(OVData.getFreightOrderQuotesTable(nbr));
          //tablereport.getColumnModel().getColumn(0).setCellRenderer(new ItemMastSearch.SomeRenderer());       
                tablequotes.getColumn("select").setCellRenderer(new ButtonRenderer()); 
                tablequotes.getColumnModel().getColumn(0).setMaxWidth(100);
                if (tablequotes.getModel().getRowCount() == 0) {
                    lbmessagequote.setText("No records found."); 
                }
    }  
    
    public void getTenders(String nbr) {
        
          tabletenders.setModel(OVData.getFreightOrderTendersTable(nbr));
          //tablereport.getColumnModel().getColumn(0).setCellRenderer(new ItemMastSearch.SomeRenderer());       
                tabletenders.getColumn("select").setCellRenderer(new ButtonRenderer()); 
                tabletenders.getColumnModel().getColumn(0).setMaxWidth(100);
                if (tabletenders.getModel().getRowCount() == 0) {
                    lbmessagetender.setText("No records found."); 
                }
    }  
    
     public void getStatus(String nbr) {
        
          tablestatus.setModel(OVData.getFreightOrderStatusTable(nbr));
          //tablereport.getColumnModel().getColumn(0).setCellRenderer(new ItemMastSearch.SomeRenderer());       
                tablestatus.getColumn("select").setCellRenderer(new ButtonRenderer()); 
                tablestatus.getColumnModel().getColumn(0).setMaxWidth(100);
                if (tablestatus.getModel().getRowCount() == 0) {
                    lbmessagestatus.setText("No records found."); 
                }
    }  
    
    public void sumweight() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Double.valueOf(orddet.getValueAt(j, 15).toString()); 
         }
         totweight.setText(String.valueOf(qty));
    }
    
     public void sumunits() {
        int qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Integer.valueOf(orddet.getValueAt(j, 14).toString()); 
         }
         totunits.setText(String.valueOf(qty));
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

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                res = st.executeQuery("select * from ship_mstr inner join cm_mstr on cm_code = sh_cust inner join cms_det on cms_code = cm_code and cms_shipto = sh_ship " +
                        " inner join wh_mstr on wh_id = sh_wh " +
                        " where sh_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    tbname.setText(res.getString("cms_name"));
                    tbaddr1.setText(res.getString("cms_line1"));
                    tbaddr2.setText(res.getString("cms_line2"));
                    tbcity.setText(res.getString("cms_city"));
                    delvdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    shipdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    tbdelvtime.setText("1200");
                    tbshiptime.setText("1200");
                    tbzip.setText(res.getString("cms_zip"));
                    ddstate.setSelectedItem(res.getString("cms_state"));
                    ddcarrierproposed.setSelectedItem((res.getString("sh_shipvia")));
                    ddshipfrom.setSelectedItem((res.getString("wh_id")));
                    
                    lbshipfrom.setText(res.getString("wh_name") + " " + res.getString("wh_addr1") + "... " + res.getString("wh_city") + ", " + res.getString("wh_state") +
                            " " + res.getString("wh_zip"));
                }
                     
            
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     
    
    }
    
    
      
   
    public FOMaint() {
        initComponents();
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
                    tbdelvtime.setText("");
                    tbshiptime.setText("");
                    tbunits.setText("");
                    tbweight.setText("");
                    tbpucontact.setText("");
                    tbpuphone.setText("");
                    lbshipfrom.setText("");
                    
                    ddshipfrom.setSelectedIndex(0);
                    ddstate.setSelectedIndex(0);
                    ddcarrierproposed.setSelectedIndex(0);
     }
    
     public void enableAll() {
         
         
     //   freightorder.setEnabled(true);
        ddreasoncode.setEnabled(true);
        ddcarrierassigned.setEnabled(true);
        btquote.setEnabled(true);
        bttender.setEnabled(true);
        btaccept.setEnabled(true);
        btdecline.setEnabled(true);
          
        tbcustfrtnbr.setEnabled(true);
        tbcust.setEnabled(true);
        tbtpid.setEnabled(true);
        
        shipdate.setEnabled(true);
        delvdate.setEnabled(true);
        tbpuphone.setEnabled(true);
        tbpucontact.setEnabled(true);
        ddstatus.setEnabled(true);
        ddshipfrom.setEnabled(true);
        lbshipfrom.setEnabled(true);
        tbname.setEnabled(true);
        tbaddr1.setEnabled(true);
        tbaddr2.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        tbcontact.setEnabled(true);
        tbphone.setEnabled(true);
        tbremarks.setEnabled(true);
        tbemail.setEnabled(true);
        tbdelvtime.setEnabled(true);
        tbshiptime.setEnabled(true);
        tbref.setEnabled(true);
        totweight.setEnabled(true);
        totunits.setEnabled(true);
       tbweight.setEnabled(true);
        tbunits.setEnabled(true);
        tbmisc.setEnabled(true);
        ddcarrierproposed.setEnabled(true);
        ddshipper.setEnabled(true);
        ddequiptype.setEnabled(true);
         ddstate.setEnabled(true);
             
        
        orddet.setEnabled(true);
        
        totunits.setEnabled(true);
        totweight.setEnabled(true);
       
        
          btbrowse.setEnabled(true);
        
          btpoprint.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        
    }
    
     public void disableAll() {
        freightorder.setEnabled(false);
        shipdate.setEnabled(false);
        delvdate.setEnabled(false);
       ddreasoncode.setEnabled(false);
        tbpuphone.setEnabled(false);
        tbpucontact.setEnabled(false);
      
        ddcarrierassigned.setEnabled(false);
        btquote.setEnabled(false);
        bttender.setEnabled(false);
        btaccept.setEnabled(false);
        btdecline.setEnabled(false);
        
        tbcustfrtnbr.setEnabled(false);
        tbcust.setEnabled(false);
        tbtpid.setEnabled(false);
        
        ddstatus.setEnabled(false);
      ddshipfrom.setEnabled(false);
        lbshipfrom.setEnabled(false);
         tbname.setEnabled(false);
        tbaddr1.setEnabled(false);
        tbaddr2.setEnabled(false);
        tbcity.setEnabled(false);
        tbzip.setEnabled(false);
        tbcontact.setEnabled(false);
        tbphone.setEnabled(false);
        tbremarks.setEnabled(false);
        tbemail.setEnabled(false);
        tbdelvtime.setEnabled(false);
        tbshiptime.setEnabled(false);
        totweight.setEnabled(false);
        totunits.setEnabled(false);
        tbweight.setEnabled(false);
        tbunits.setEnabled(false);
        tbweight.setEnabled(false);
        tbunits.setEnabled(false);
        tbref.setEnabled(false);
       
        tbmisc.setEnabled(false);
        ddcarrierproposed.setEnabled(false);
        ddshipper.setEnabled(false);
        ddequiptype.setEnabled(false);
        ddstate.setEnabled(false);
       
      
      
        
        orddet.setEnabled(false);
        
        totunits.setEnabled(false);
        totweight.setEnabled(false);
       
        
          btbrowse.setEnabled(false);
         
          btpoprint.setEnabled(false);
        btnew.setEnabled(false);
        btedit.setEnabled(false);
        btadd.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
    }
    
    public void initvars(String arg) {
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("Load", jPanelLoad);
        jTabbedPane1.add("Quotes", jPanelQuotes);
        jTabbedPane1.add("Tenders", jPanelTenders);
        jTabbedPane1.add("Status", jPanelStatus);
        
         RawFileQuotesPanel.setVisible(false);
         RawFileTenderPanel.setVisible(false);
         RawFileStatusPanel.setVisible(false);
         bthidemap.setVisible(false);
         
         
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
        freightorder.setText("");
       
        ddstatus.setBackground(null);
      
        shipdate.setDate(now);
        delvdate.setDate(now);
       
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
       
        
        btbrowse.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
      
        
        lbshipfrom.setText("");
        
        lbmessagequote.setText("");
        lbmessagetender.setText("");
        lbmessagestatus.setText("");
        
        
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbcontact.setText("");
        tbphone.setText("");
        tbremarks.setText("");
        tbemail.setText("");
        tbdelvtime.setText("");
        tbshiptime.setText("");
        tbref.setText("");
        totweight.setText("");
        totunits.setText("");
        
        
        
        
        ddcarrierproposed.removeAllItems();
         ArrayList<String> carriers = OVData.getScacAll();
        for (String code : carriers) {
            ddcarrierproposed.addItem(code);
        }
        ddcarrierproposed.insertItemAt("", 0);
        ddcarrierproposed.setSelectedIndex(0);
        
        ddcarrierassigned.removeAllItems();
          carriers = OVData.getScacCarrierOnly();  
        for (String code : carriers) {
            ddcarrierassigned.addItem(code);
        }
        ddcarrierassigned.insertItemAt("", 0);
        ddcarrierassigned.setSelectedIndex(0);
        
        
        
        
        ddshipfrom.removeAllItems();
         ArrayList<String> shipfroms = OVData.getWareHouseList();
        for (String code : shipfroms) {
            ddshipfrom.addItem(code);
        }
         ddshipfrom.insertItemAt("", 0);
         ddshipfrom.setSelectedIndex(0);
        
        ddshipper.removeAllItems();
         ArrayList<String> shippers = OVData.getShippersOpenListForFreight();
         lock_ddshipper = true;
        for (String code : shippers) {
            ddshipper.addItem(code);
        }
            ddshipper.insertItemAt("", 0);
            ddshipper.setSelectedIndex(0);
            lock_ddshipper = false;
            
        
        
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
        ArrayList states = OVData.getCodeMstrKeyList("state");
        for (int i = 0; i < states.size(); i++) {
            ddstate.addItem(states.get(i).toString());
        }
        if (ddstate.getItemCount() > 0) {
           ddstate.setSelectedIndex(0); 
        }
        
         
        if (! arg.isEmpty()) {
            getOrder(arg);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btbrowse.setEnabled(true);
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

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        freightorder = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btdelitem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        totunits = new javax.swing.JTextField();
        totweight = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btpoprint = new javax.swing.JButton();
        btbrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbweight = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        tbref = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        jLabel98 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        tbmisc = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        shipdate = new com.toedter.calendar.JDateChooser();
        tbphone = new javax.swing.JTextField();
        tbemail = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        tbremarks = new javax.swing.JTextField();
        ddshipper = new javax.swing.JComboBox<>();
        jLabel96 = new javax.swing.JLabel();
        ddstate = new javax.swing.JComboBox();
        tbunits = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        delvdate = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        tbshiptime = new javax.swing.JTextField();
        tbdelvtime = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btquote = new javax.swing.JButton();
        bttender = new javax.swing.JButton();
        ddstatus = new javax.swing.JComboBox();
        jLabel87 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        ddcarrierproposed = new javax.swing.JComboBox();
        jLabel92 = new javax.swing.JLabel();
        ddequiptype = new javax.swing.JComboBox();
        ddcarrierassigned = new javax.swing.JComboBox();
        jLabel101 = new javax.swing.JLabel();
        tbcustfrtnbr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbcust = new javax.swing.JTextField();
        tbtpid = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ddreasoncode = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        btaccept = new javax.swing.JButton();
        btdecline = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelLoad = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        ddshipfrom = new javax.swing.JComboBox<>();
        tbpucontact = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        lbshipfrom = new javax.swing.JLabel();
        tbpuphone = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jPanelQuotes = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        bthidequotefiles = new javax.swing.JButton();
        lbmessagequote = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablequotes = new javax.swing.JTable();
        RawFileQuotesPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tafilequotes = new javax.swing.JTextArea();
        jPanelTenders = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        bthidetendersfiles = new javax.swing.JButton();
        lbmessagetender = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabletenders = new javax.swing.JTable();
        RawFileTenderPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tafiletenders = new javax.swing.JTextArea();
        jPanelStatus = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        bthideEDIStatus = new javax.swing.JButton();
        lbmessagestatus = new javax.swing.JLabel();
        bthidemap = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        labelmap = new javax.swing.JLabel();
        StatusTablePanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tablestatus = new javax.swing.JTable();
        RawFileStatusPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tafilestatus = new javax.swing.JTextArea();

        jLabel4.setText("jLabel4");

        jTextField1.setText("jTextField1");

        jLabel3.setText("jLabel3");

        jLabel10.setText("jLabel10");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Freight Order Maintenance"));

        jLabel76.setText("FreightNbr");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
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

        btdelitem.setText("Del Item");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        btadd.setText("Save");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jLabel1.setText("Total Units");

        jLabel2.setText("Total Weight");

        btpoprint.setText("Print");
        btpoprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpoprintActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel83.setText("Reference");

        jLabel89.setText("Phone");

        jLabel99.setText("Units");

        jLabel77.setText("Shipper");

        jLabel98.setText("Weight");

        jLabel88.setText("Contact");

        jLabel91.setText("Addr1");

        jLabel94.setText("State");

        jLabel81.setText("Ship Date");

        shipdate.setDateFormatString("yyyy-MM-dd");

        jLabel93.setText("City");

        jLabel86.setText("Misc");

        jLabel82.setText("Name");

        jLabel90.setText("Addr2");

        ddshipper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshipperActionPerformed(evt);
            }
        });

        jLabel96.setText("Email");

        jLabel97.setText("Remarks");

        delvdate.setDateFormatString("yyyy-MM-dd");

        jLabel8.setText("Delivery Date");

        jLabel11.setText("Zip");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel82))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel91))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel90))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(jLabel93))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel94))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel77)))
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel97)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbremarks, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbaddr1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbaddr2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(tbname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel89)
                            .addComponent(jLabel96)
                            .addComponent(jLabel88)
                            .addComponent(jLabel8)
                            .addComponent(jLabel81)
                            .addComponent(jLabel83))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tbref, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(shipdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                    .addComponent(delvdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbdelvtime, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(tbshiptime, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel99, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel98, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tbunits, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tbweight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jLabel86)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(36, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel77)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel82))
                            .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel81)
                            .addComponent(shipdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbshiptime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel83)
                        .addComponent(ddshipper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbunits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel99))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel91))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel90))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel93))
                            .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel94))
                            .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(delvdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(tbdelvtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel96))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel88))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel89)
                            .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel86))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addContainerGap())
        );

        btquote.setText("RFQ");
        btquote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btquoteActionPerformed(evt);
            }
        });

        bttender.setText("Tender");
        bttender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttenderActionPerformed(evt);
            }
        });

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Open", "Quoted", "Tendered", "Accepted", "Declined", "Cancelled", "InTransit", "Delivered", "Close" }));

        jLabel87.setText("Status");

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel85.setText("EquipType");

        jLabel92.setText("Proposed Carrier");

        jLabel101.setText("Assigned Carrier");

        jLabel5.setText("Cust Frt Nbr");

        jLabel6.setText("Customer");

        jLabel7.setText("TPID");

        ddreasoncode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CPT Capacity Type", "CPU Capacity Unavailable", "EQT Equipment Type", "EQU Equipment Unavailable", "LNH Length of Haul", "PRM Permits", "WGT Weight" }));

        jLabel12.setText("ReasonCode");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel92)
                    .addComponent(jLabel101)
                    .addComponent(jLabel12))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddreasoncode, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(ddcarrierproposed, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(ddcarrierassigned, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddequiptype, 0, 133, Short.MAX_VALUE)
                    .addComponent(tbcustfrtnbr))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbtpid, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(tbcust))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcarrierproposed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel92)
                    .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85)
                    .addComponent(jLabel6)
                    .addComponent(tbcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcarrierassigned, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbcustfrtnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(tbtpid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(jLabel101))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddreasoncode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        btaccept.setText("Accept");
        btaccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btacceptActionPerformed(evt);
            }
        });

        btdecline.setText("Decline");
        btdecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeclineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                .addGap(185, 185, 185)
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(totunits, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel2)
                .addGap(5, 5, 5)
                .addComponent(totweight, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149)
                .addComponent(btpoprint)
                .addGap(7, 7, 7)
                .addComponent(btedit)
                .addGap(12, 12, 12)
                .addComponent(btadd)
                .addContainerGap(36, Short.MAX_VALUE))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btadditem)
                        .addGap(7, 7, 7)
                        .addComponent(btdelitem))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(jLabel76)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(freightorder, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnew)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel87)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(bttender)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btquote)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaccept)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdecline)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bttender)
                        .addComponent(btquote)
                        .addComponent(btaccept)
                        .addComponent(btdecline))
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addComponent(jLabel76))
                        .addComponent(btbrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel87))
                        .addComponent(btnew)
                        .addComponent(freightorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btadditem)
                    .addComponent(btdelitem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel1))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(totunits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel2))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(totweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btpoprint)
                    .addComponent(btedit)
                    .addComponent(btadd)))
        );

        add(jPanelMain);
        add(jTabbedPane1);

        jPanelLoad.setPreferredSize(new java.awt.Dimension(840, 634));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel84.setText("Pickup Phone");

        jLabel79.setText("Pickup Contact");

        jLabel78.setText("Pickup");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbpucontact, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel84)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tbpuphone))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel78))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpucontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(tbpuphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelLoadLayout = new javax.swing.GroupLayout(jPanelLoad);
        jPanelLoad.setLayout(jPanelLoadLayout);
        jPanelLoadLayout.setHorizontalGroup(
            jPanelLoadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLoadLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
        );
        jPanelLoadLayout.setVerticalGroup(
            jPanelLoadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLoadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(679, 679, 679))
        );

        add(jPanelLoad);

        jPanelQuotes.setPreferredSize(new java.awt.Dimension(840, 634));

        bthidequotefiles.setText("Hide EDI");
        bthidequotefiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidequotefilesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bthidequotefiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbmessagequote, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lbmessagequote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(bthidequotefiles, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setLayout(new java.awt.BorderLayout());

        tablequotes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablequotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablequotesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablequotes);

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel6);

        RawFileQuotesPanel.setLayout(new java.awt.BorderLayout());

        tafilequotes.setColumns(20);
        tafilequotes.setRows(5);
        jScrollPane1.setViewportView(tafilequotes);

        RawFileQuotesPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel8.add(RawFileQuotesPanel);

        javax.swing.GroupLayout jPanelQuotesLayout = new javax.swing.GroupLayout(jPanelQuotes);
        jPanelQuotes.setLayout(jPanelQuotesLayout);
        jPanelQuotesLayout.setHorizontalGroup(
            jPanelQuotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQuotesLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
        );
        jPanelQuotesLayout.setVerticalGroup(
            jPanelQuotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelQuotesLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
        );

        add(jPanelQuotes);

        jPanelTenders.setPreferredSize(new java.awt.Dimension(840, 634));

        bthidetendersfiles.setText("Hide EDI");
        bthidetendersfiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidetendersfilesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bthidetendersfiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbmessagetender, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lbmessagetender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(bthidetendersfiles, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setLayout(new java.awt.BorderLayout());

        tabletenders.setModel(new javax.swing.table.DefaultTableModel(
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
        tabletenders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabletendersMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabletenders);

        jPanel7.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jPanel9.add(jPanel7);

        RawFileTenderPanel.setLayout(new java.awt.BorderLayout());

        tafiletenders.setColumns(20);
        tafiletenders.setRows(5);
        jScrollPane2.setViewportView(tafiletenders);

        RawFileTenderPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel9.add(RawFileTenderPanel);

        javax.swing.GroupLayout jPanelTendersLayout = new javax.swing.GroupLayout(jPanelTenders);
        jPanelTenders.setLayout(jPanelTendersLayout);
        jPanelTendersLayout.setHorizontalGroup(
            jPanelTendersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTendersLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
        );
        jPanelTendersLayout.setVerticalGroup(
            jPanelTendersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTendersLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        add(jPanelTenders);

        jPanelStatus.setPreferredSize(new java.awt.Dimension(840, 634));

        bthideEDIStatus.setText("Hide EDI");
        bthideEDIStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthideEDIStatusActionPerformed(evt);
            }
        });

        bthidemap.setText("Hide Map");
        bthidemap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidemapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bthideEDIStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbmessagestatus, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(105, 105, 105)
                .addComponent(bthidemap)
                .addGap(17, 17, 17))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bthidemap)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addGap(4, 4, 4)
                            .addComponent(lbmessagestatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(bthideEDIStatus, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addGap(17, 17, 17))
        );

        jPanel11.setLayout(new javax.swing.BoxLayout(jPanel11, javax.swing.BoxLayout.LINE_AXIS));
        jPanel11.add(labelmap);

        StatusTablePanel.setLayout(new java.awt.BorderLayout());

        tablestatus.setModel(new javax.swing.table.DefaultTableModel(
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
        tablestatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablestatusMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tablestatus);

        StatusTablePanel.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        jPanel11.add(StatusTablePanel);

        RawFileStatusPanel.setLayout(new java.awt.BorderLayout());

        tafilestatus.setColumns(20);
        tafilestatus.setRows(5);
        jScrollPane6.setViewportView(tafilestatus);

        RawFileStatusPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jPanel11.add(RawFileStatusPanel);

        javax.swing.GroupLayout jPanelStatusLayout = new javax.swing.GroupLayout(jPanelStatus);
        jPanelStatus.setLayout(jPanelStatusLayout);
        jPanelStatusLayout.setHorizontalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStatusLayout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
        );

        add(jPanelStatus);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     
         initvars("");
        
        
         freightorder.setText(String.valueOf(OVData.getNextNbr("fo")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
           
                shipdate.setDate(now);
               
                
                // tbDateShippedSM.setEnabled(false);
                
                enableAll();
                
                ddstatus.setEnabled(false);
                btnew.setEnabled(false);
                btbrowse.setEnabled(false);
                btedit.setEnabled(false);
                btpoprint.setEnabled(false);
                ddshipfrom.setEnabled(false); 
               
                
                 
               
               
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String shipper = "";
        String custpart = "";
        
        shipper = ddshipper.getSelectedItem().toString();
          
        
        if (tbzip.getText().toString().isEmpty() ||
            tbname.getText().toString().isEmpty() ||
            tbaddr1.getText().toString().isEmpty() ||    
            tbcity.getText().toString().isEmpty() ||
            ddstate.getSelectedItem().toString().isEmpty()    
             ) {
            bsmf.MainFrame.show("Insufficient Shipto Info");
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
          if (tbweight.getText().toString().isEmpty()) {
              tbweight.setText("0");
          }
           if (tbunits.getText().toString().isEmpty()) {
              tbunits.setText("0");
          }
                 
        // check formatting
         Pattern p = Pattern.compile("^[0-9]\\d*$");
                 // Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbweight.getText());
        if (!m.find() || tbweight.getText() == null) {
            bsmf.MainFrame.show("Invalid Weight");
            canproceed = false;
        }
        
         p = Pattern.compile("^[0-9]\\d*$");
        m = p.matcher(tbunits.getText());
        if (!m.find() || tbunits.getText() == null) {
            bsmf.MainFrame.show("Invalid Pallet Count");
            canproceed = false;
        }
        
       
        line = getmaxline();
        line++;
        if (canproceed) {
            
            // let's first add the pickup...only if this is first time add
            if (myorddetmodel.getRowCount() == 0) {
                String[] addr = OVData.getWareHouseAddressElements(ddshipfrom.getSelectedItem().toString());
                 myorddetmodel.addRow(new Object[]{0, freightorder.getText(), "LD", "", "", addr[1],
            addr[2], addr[3], addr[5], addr[6], addr[7], tbpucontact.getText(),
            tbpuphone.getText(), "", "0", "0", "0000-00-00", "", dfdate.format(shipdate.getDate()), tbshiptime.getText()});
            }
        //     + "(fod_line, fod_nbr, fod_shipper, fod_ref, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, "
        //                    + " fod_contact, fod_phone, fod_email, fod_units, fod_weight ) "
            myorddetmodel.addRow(new Object[]{line, freightorder.getText(), "UL", ddshipper.getSelectedItem().toString(), tbref.getText(), tbname.getText(),
            tbaddr1.getText(), tbaddr2.getText(), tbcity.getText(), ddstate.getSelectedItem().toString(), tbzip.getText(), tbcontact.getText(),
            tbphone.getText(), tbemail.getText(), tbunits.getText(), tbweight.getText(), dfdate.format(delvdate.getDate()), tbshiptime.getText(), "0000-00-00", ""});
         sumweight();
         sumunits();
         
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
           
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                String site = OVData.getDefaultSite();
                if (proceed) {
                    st.executeUpdate("insert into fo_mstr "
                        + "(fo_nbr, fo_date, fo_ref, fo_carrier, fo_carrier_assigned, fo_equipment_type, fo_status, fo_rmks ) "
                        + " values ( " + "'" + freightorder.getText().toString() + "'" + ","
                         + "'" + dfdate.format(now) + "'" + ","
                        + "'" + tbref.getText() + "'" + ","
                        + "'" + ddcarrierproposed.getSelectedItem() + "'" + ","
                        + "'" + ddcarrierassigned.getSelectedItem() + "'" + ","
                        + "'" + ddequiptype.getSelectedItem() + "'" + ","
                        + "'" + ddstatus.getSelectedItem() + "'" + ","  
                        + "'" + tbremarks.getText() + "'"
                        + ")"
                        + ";");

                  
                 
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                        st.executeUpdate("insert into fod_det "
                            + "(fod_line, fod_nbr, fod_type, fod_shipper, fod_ref, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, "
                            + " fod_contact, fod_phone, fod_email, fod_units, fod_weight, fod_delvdate, fod_delvtime, fod_shipdate, fod_shiptime ) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 11).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 12).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 13).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 14).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 15).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 16).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 17).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 18).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 19).toString() + "'"         
                            + ")"
                            + ";");

                    }
                    
                    // update every shipper with freight order number assignment...sh_freight = freightorder
                    OVData.updateShipperWithFreightOrder(orddet);
                    
                    bsmf.MainFrame.show("Freight Order has been added");
                   initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot add Freight Order");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 2).toString().equals("LD")) {
                bsmf.MainFrame.show("Cannot Delete Load Line");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         
         
    }//GEN-LAST:event_btdelitemActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
           
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String site = OVData.getDefaultSite();
                if (proceed) {
                    st.executeUpdate("update fo_mstr set "
                            + " fo_ref = " + "'" + tbref.getText() + "'" + ","
                            + " fo_carrier = " + "'" + ddcarrierproposed.getSelectedItem() + "'" + ","
                            + " fo_carrier_assigned = " + "'" + ddcarrierassigned.getSelectedItem() + "'" + ","   
                            + " fo_rmks = " + "'" + tbremarks.getText() + "'" + ","
                            + " fo_equipment_type = " + "'" + ddequiptype.getSelectedItem() + "'" 
                            + " where fo_nbr = " + "'" + freightorder.getText() + "'" 
                        + ";");

                   st.executeUpdate(" delete from fod_det where fod_nbr = " + "'" + freightorder.getText() + "'" + ";");
                 
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                        st.executeUpdate("insert into fod_det "
                            + "(fod_line, fod_nbr, fod_type, fod_shipper, fod_ref, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, "
                            + " fod_contact, fod_phone, fod_email, fod_units, fod_weight, fod_delvdate, fod_delvtime, fod_shipdate, fod_shiptime ) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 11).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 12).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 13).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 14).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 15).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 16).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 17).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 18).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 19).toString() + "'"         
                            + ")"
                            + ";");

                    }
                    
                    // update every shipper with freight order number assignment...sh_freight = freightorder
                    OVData.updateShipperWithFreightOrder(orddet);
                    
                    bsmf.MainFrame.show("Freight Order has been edited");
                   initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot add Freight Order");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btpoprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpoprintActionPerformed
       OVData.printPurchaseOrder(freightorder.getText());
    }//GEN-LAST:event_btpoprintActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "fomaint,fo_nbr");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void ddshipperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipperActionPerformed
        if (ddshipper.getItemCount() > 0  && ! lock_ddshipper) {
            clearAddrFields();
           shipperChangeEvent(ddshipper.getSelectedItem().toString());
        } // if ddcust has a list
    }//GEN-LAST:event_ddshipperActionPerformed

    private void btquoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btquoteActionPerformed
        OVData.quoteFreightOrder(freightorder.getText());
        initvars(freightorder.getText());
    }//GEN-LAST:event_btquoteActionPerformed

    private void bttenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttenderActionPerformed
        OVData.tenderFreightOrder(freightorder.getText());
        initvars(freightorder.getText());
    }//GEN-LAST:event_bttenderActionPerformed

    private void bthidequotefilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidequotefilesActionPerformed
       RawFileQuotesPanel.setVisible(false);
    }//GEN-LAST:event_bthidequotefilesActionPerformed

    private void tablequotesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablequotesMouseClicked
         int row = tablequotes.rowAtPoint(evt.getPoint());
        int col = tablequotes.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             try {
                 tafilequotes.setText("");
                  ArrayList<String> segments = OVData.readEDIRawFileIntoArrayList(tablequotes.getValueAt(row, 5).toString(), tablequotes.getValueAt(row, 6).toString());  
                    for (String segment : segments ) {
                        tafilequotes.append(segment);
                        tafilequotes.append("\n");
                    }
                 
                  RawFileQuotesPanel.setVisible(true);
             } catch (MalformedURLException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SmbException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(FOMaint.class.getName()).log(Level.SEVERE, null, ex);
             }
           
        }
    }//GEN-LAST:event_tablequotesMouseClicked

    private void bthidetendersfilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidetendersfilesActionPerformed
        RawFileTenderPanel.setVisible(false);
    }//GEN-LAST:event_bthidetendersfilesActionPerformed

    private void tabletendersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabletendersMouseClicked
         int row = tabletenders.rowAtPoint(evt.getPoint());
        int col = tabletenders.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             try {
                  tafiletenders.setText("");
                  ArrayList<String> segments = OVData.readEDIRawFileIntoArrayList(tabletenders.getValueAt(row, 5).toString(), tabletenders.getValueAt(row, 6).toString());  
                    for (String segment : segments ) {
                        tafiletenders.append(segment);
                        tafiletenders.append("\n");
                    }
                 
                  RawFileTenderPanel.setVisible(true);
             } catch (MalformedURLException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SmbException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(FOMaint.class.getName()).log(Level.SEVERE, null, ex);
             }
           
        }
    }//GEN-LAST:event_tabletendersMouseClicked

    private void bthideEDIStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthideEDIStatusActionPerformed
       RawFileStatusPanel.setVisible(false);
    }//GEN-LAST:event_bthideEDIStatusActionPerformed

    private void tablestatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablestatusMouseClicked
         int row = tablestatus.rowAtPoint(evt.getPoint());
        int col = tablestatus.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             try {
                 tafilestatus.setText("");
                  ArrayList<String> segments = OVData.readEDIRawFileIntoArrayList(tablestatus.getValueAt(row, 5).toString(), tablestatus.getValueAt(row, 10).toString());  
                    for (String segment : segments ) {
                        tafilestatus.append(segment);
                        tafilestatus.append("\n");
                    } 
                  RawFileStatusPanel.setVisible(true);
             } catch (MalformedURLException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (SmbException ex) {
                 Logger.getLogger(EDILogBrowse.class.getName()).log(Level.SEVERE, null, ex);
             } catch (IOException ex) {
                 Logger.getLogger(FOMaint.class.getName()).log(Level.SEVERE, null, ex);
             }
           
        }
        
        if ( col == 8 || col == 9) {
             showMap(tablestatus.getValueAt(row, 8).toString(), tablestatus.getValueAt(row, 9).toString());
           
        }
        
    }//GEN-LAST:event_tablestatusMouseClicked

    private void bthidemapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidemapActionPerformed
        hideMap();
    }//GEN-LAST:event_bthidemapActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
         int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        
        tbname.setText(orddet.getValueAt(row, 5).toString());
        tbaddr1.setText(orddet.getValueAt(row, 6).toString());
        tbaddr2.setText(orddet.getValueAt(row, 7).toString());
        tbcity.setText(orddet.getValueAt(row, 8).toString());
        ddstate.setSelectedItem(orddet.getValueAt(row, 9).toString());
        tbzip.setText(orddet.getValueAt(row, 10).toString());
        
        tbcontact.setText(orddet.getValueAt(row, 11).toString());
        tbphone.setText(orddet.getValueAt(row, 12).toString());
        tbemail.setText(orddet.getValueAt(row, 13).toString());
        tbunits.setText(orddet.getValueAt(row, 14).toString());
        tbweight.setText(orddet.getValueAt(row, 15).toString());
        if (orddet.getValueAt(row, 16).toString().isEmpty() || orddet.getValueAt(row, 16).toString().equals("0000-00-00")) {
         delvdate.setDate(null);   
        } else {
         delvdate.setDate(Date.valueOf(orddet.getValueAt(row, 16).toString()));   
        }
        tbdelvtime.setText(orddet.getValueAt(row, 17).toString());
        if (orddet.getValueAt(row, 18).toString().isEmpty() || orddet.getValueAt(row, 18).toString().equals("0000-00-00") ) {
         shipdate.setDate(null);   
        } else {
         shipdate.setDate(Date.valueOf(orddet.getValueAt(row, 18).toString()));   
        }
        tbshiptime.setText(orddet.getValueAt(row, 19).toString());
    }//GEN-LAST:event_orddetMouseClicked

    private void btacceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btacceptActionPerformed
        OVData.tenderResponse(freightorder.getText(), "Accepted");
        initvars(freightorder.getText());
    }//GEN-LAST:event_btacceptActionPerformed

    private void btdeclineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeclineActionPerformed
        boolean proceed = bsmf.MainFrame.warn("Are you sure you want to Decline?");
        
        if (ddreasoncode.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show("Must choose a reasoncode if declining");
            proceed = false;
        }
        
        if (proceed) {
           OVData.updateFreightOrderReasonCode(freightorder.getText(), ddreasoncode.getSelectedItem().toString().substring(0, 4));
           OVData.tenderResponse(freightorder.getText(), "Declined");
           initvars(freightorder.getText()); 
        }
    }//GEN-LAST:event_btdeclineActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel RawFileQuotesPanel;
    private javax.swing.JPanel RawFileStatusPanel;
    private javax.swing.JPanel RawFileTenderPanel;
    private javax.swing.JPanel StatusTablePanel;
    private javax.swing.JButton btaccept;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdecline;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton bthideEDIStatus;
    private javax.swing.JButton bthidemap;
    private javax.swing.JButton bthidequotefiles;
    private javax.swing.JButton bthidetendersfiles;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btpoprint;
    private javax.swing.JButton btquote;
    private javax.swing.JButton bttender;
    private javax.swing.JComboBox ddcarrierassigned;
    private javax.swing.JComboBox ddcarrierproposed;
    private javax.swing.JComboBox ddequiptype;
    private javax.swing.JComboBox<String> ddreasoncode;
    private javax.swing.JComboBox<String> ddshipfrom;
    private javax.swing.JComboBox<String> ddshipper;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddstatus;
    private com.toedter.calendar.JDateChooser delvdate;
    private javax.swing.JTextField freightorder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelLoad;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelQuotes;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelTenders;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelmap;
    private javax.swing.JLabel lbmessagequote;
    private javax.swing.JLabel lbmessagestatus;
    private javax.swing.JLabel lbmessagetender;
    private javax.swing.JLabel lbshipfrom;
    private javax.swing.JTable orddet;
    private com.toedter.calendar.JDateChooser shipdate;
    private javax.swing.JTable tablequotes;
    private javax.swing.JTable tablestatus;
    private javax.swing.JTable tabletenders;
    private javax.swing.JTextArea tafilequotes;
    private javax.swing.JTextArea tafilestatus;
    private javax.swing.JTextArea tafiletenders;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcust;
    private javax.swing.JTextField tbcustfrtnbr;
    private javax.swing.JTextField tbdelvtime;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbpucontact;
    private javax.swing.JTextField tbpuphone;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbshiptime;
    private javax.swing.JTextField tbtpid;
    private javax.swing.JTextField tbunits;
    private javax.swing.JTextField tbweight;
    private javax.swing.JTextField tbzip;
    private javax.swing.JTextField totunits;
    private javax.swing.JTextField totweight;
    // End of variables declaration//GEN-END:variables
}
