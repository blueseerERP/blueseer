/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.pur;

import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;


/**
 *
 * @author vaughnte
 */
public class POMaintPanel extends javax.swing.JPanel {

    
     boolean editmode = false;
     boolean isLoad = false;
     String curr = "";
     String basecurr = OVData.getDefaultCurrency();
     DecimalFormat df = new DecimalFormat("#0.0000");
     
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "line", "Part", "VendPart", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyRecv", "Status"
            });
      javax.swing.table.DefaultTableModel modelsched = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Date", "Ref", "Qty", "Type"
            });
    
    
     public void getOrder(String mykey) {
        
        initvars("");
        
        btbrowse.setEnabled(false);
        btpovendbrowse.setEnabled(false);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(false);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                res = st.executeQuery("select * from po_mstr where po_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    ordernbr.setText(mykey);
                   
                    ddvend.setSelectedItem(res.getString("po_vend"));
                    ddvend.setEnabled(false);
                    ddstatus.setSelectedItem(res.getString("po_status"));
                    tbcurr.setText("po_curr");
                    ddshipvia.setSelectedItem(res.getString("po_shipvia"));
                    duedate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("po_due_date")));
                    blanket = res.getString("po_type");
                    if (blanket != null && blanket.compareTo("BLANKET") == 0)
                    cbblanket.setSelected(true);
                    else
                    cbblanket.setSelected(false);
                    cbblanket.setEnabled(false);
                }
               
                 // myorddetmodel  "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status"
                res = st.executeQuery("select * from pod_mstr where pod_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("pod_line"), res.getString("pod_part"),
                      res.getString("pod_vendpart"), res.getString("pod_nbr"), 
                      res.getString("pod_ord_qty"), res.getString("pod_listprice"),
                      res.getDouble("pod_disc"), res.getString("pod_netprice"), res.getString("pod_rcvd_qty"), res.getString("pod_status")});
                }
                orddet.setModel(myorddetmodel);
               
                 
                if (cbblanket.isSelected())
                    panelSchedule.setVisible(true);
                else
                    panelSchedule.setVisible(false);
               
                
                sumqty();
                 sumdollars();
                 sumlinecount();
               
                if (i == 0) {
                   bsmf.MainFrame.show("No Order Record found for " + mykey);
                } else {
                            if (ddstatus.getSelectedItem().toString().compareTo("close") == 0) {
                             disableAll();
                             btnew.setEnabled(true);
                         } else {
                             enableAll();
                              btadd.setEnabled(false);
                              btnew.setEnabled(false);
                         }
                    
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve so_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
     public void setPrice() {
         DecimalFormat df = new DecimalFormat("#0.00");
         if (dduom.getItemCount() > 0 && ddpart.getItemCount() > 0 && ddvend.getItemCount() > 0 && ! tbcurr.getText().isEmpty()) {
                listprice.setText(df.format(OVData.getPartPriceFromVend(ddvend.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                        dduom.getSelectedItem().toString(), tbcurr.getText())));
                setnetprice();
         }
     }
     
      public void getparts(String part) {
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
              
               // if part is not already in list
                int k = ddpart.getSelectedIndex();
              
                
                // lets first try as cust part...i.e....lets look up the item based on entering a customer part number.
                if (k < 0) {
                    
                res = st.executeQuery("select vdp_item, vdp_vitem from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" 
                        + " AND vdp_vitem = " + "'" + part + "'" 
                        + ";");
                int i = 0;
                while (res.next()) {
                    i++;
                    ddpart.setSelectedItem(res.getString("vdp_item"));
                    vendnumber.setText(part);
                    ddpart.setForeground(Color.blue);
                    vendnumber.setForeground(Color.blue);
                    vendnumber.setEditable(false);
                }
                
                // if i is still 0...then must be a misc item
                if (i == 0) {
                  //  partnbr.addItem(part);
                  //  partnbr.setSelectedItem(part);
                    vendnumber.setText("");
                    ddpart.setForeground(Color.red);
                    vendnumber.setForeground(Color.red);
                   // custnumber.setBorder(BorderFactory.createLineBorder(Color.red));
                    vendnumber.setEditable(true);
                    
                    discount.setText("0.00");
                    listprice.setText("0.00");
                    listprice.setBackground(Color.white);
                    
                    netprice.setText("0.00");
                    qtyshipped.setText("0");
                }
                
                }
                
             if (k >= 0) {   
            discount.setText("0.00");
            listprice.setText("0.00");
            netprice.setText("0.00");
            qtyshipped.setText("0");
            vendnumber.setText(OVData.getItemDesc(ddpart.getSelectedItem().toString()));
            ddpart.setForeground(Color.blue);
            vendnumber.setForeground(Color.blue);
            vendnumber.setEditable(false);
            
            if (ddpart.getItemCount() > 0) {
                ddpart.setForeground(Color.blue);
                vendnumber.setForeground(Color.blue);
                setPrice();
                
            } // if part selected
             }  
                
                
                
               
            } catch (SQLException s) {
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
        public void setnetprice() {
        Double disc = 0.00;
        Double list = 0.00;
        Double net = 0.00;
        DecimalFormat df = new DecimalFormat("#0.00");
        
        if (discount.getText().isEmpty() || Double.parseDouble(discount.getText().toString()) == 0) {
            netprice.setText(listprice.getText());
        } else {
           
           if (listprice.getText().isEmpty() || Double.parseDouble(listprice.getText().toString()) == 0) {
             listprice.setText("0");
             netprice.setText("0");
           } else {               
           disc = Double.parseDouble(discount.getText().toString());
           list = Double.parseDouble(listprice.getText().toString());
            
           net = list - ((disc / 100) * list);
           netprice.setText(df.format(net));
           }
        }
    }
      
     public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
    }
    
    public void sumqty() {
        int qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Integer.valueOf(orddet.getValueAt(j, 4).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
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
    public void sumdollars() {
        DecimalFormat df = new DecimalFormat("#.00");
        double dol = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( Double.valueOf(orddet.getValueAt(j, 4).toString()) * Double.valueOf(orddet.getValueAt(j, 7).toString()) ); 
         }
         tbtotdollars.setText(df.format(dol));
         lblcurr.setText(tbcurr.getText());
    }
   
     public void vendChangeEvent(String mykey) {
        
       
      try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                ddpart.removeAllItems();
                res = st.executeQuery("select vdp_item from vdp_mstr where vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    ddpart.addItem(res.getString("vdp_item"));
                }
                
                res = st.executeQuery("select * from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    ddshipvia.setSelectedItem(res.getString("vd_shipvia"));
                    tbcurr.setText(res.getString("vd_curr"));
                    curr = tbcurr.getText();
                }
                
              
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
 
    /**
     * Creates new form OrderMaintPanel
     */
    public POMaintPanel() {
        initComponents();
    }

     public void enableAll() {
     //   ordernbr.setEnabled(true);
        ddvend.setEnabled(true);
        ddshipvia.setEnabled(true);
        duedate.setEnabled(true);
        
        ddsite.setEnabled(true);
        ddstatus.setEnabled(true);
        cbblanket.setEnabled(true);
        remarks.setEnabled(true);
        ddpart.setEnabled(true);
        dduom.setEnabled(true);
        vendnumber.setEnabled(true);
        qtyshipped.setEnabled(true);
        listprice.setEnabled(true);
      
      
      netprice.setEnabled(true);
      totlines.setEnabled(true);
      tbtotqty.setEnabled(true);
      tbtotdollars.setEnabled(true);
      
      netprice.setEditable(false);
      totlines.setEditable(false);
      tbtotqty.setEditable(false);
      tbtotdollars.setEditable(false);
      
      
      discount.setEnabled(true);
        
        orddet.setEnabled(true);
        
       
        
          btbrowse.setEnabled(true);
          btpovendbrowse.setEnabled(true);
          btpoprint.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        tbbuyer.setEnabled(true);
        
    }
    
     public void disableAll() {
        ordernbr.setEnabled(false);
        ddvend.setEnabled(false);
        ddshipvia.setEnabled(false);
        duedate.setEnabled(false);
        orddate.setEnabled(false);
        ddsite.setEnabled(false);
        ddstatus.setEnabled(false);
        cbblanket.setEnabled(false);
        remarks.setEnabled(false);
        ddpart.setEnabled(false);
        dduom.setEnabled(false);
        qtyshipped.setEnabled(false);
        listprice.setEnabled(false);
        netprice.setEnabled(false);
        discount.setEnabled(false);
        tbbuyer.setEnabled(false);
        vendnumber.setEnabled(false);
        userid.setEnabled(false);
        tbcurr.setEnabled(false);
        orddet.setEnabled(false);
        
        totlines.setEnabled(false);
        tbtotqty.setEnabled(false);
        tbtotdollars.setEnabled(false);
        
          btbrowse.setEnabled(false);
           btpovendbrowse.setEnabled(false);
          btpoprint.setEnabled(false);
        btnew.setEnabled(false);
        btedit.setEnabled(false);
        btadd.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
    }
    
    public void initvars(String arg) {
       
         jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Lines", panelDetail);
        jTabbedPane1.add("Schedule", panelSchedule);
        
        
        jTabbedPane1.setEnabledAt(2, false);
        jTabbedPane1.setEnabledAt(1, false);
        
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        ordernbr.setText("");
        
        listprice.setText("0.00");
        netprice.setText("0.00");
        netprice.setEditable(false);
        qtyshipped.setText("0");
        discount.setText("0.00");
        
        
       
        userid.setText(bsmf.MainFrame.userid);
        duedate.setDate(now);
        orddate.setDate(now);
        
        orddate.setEnabled(false);
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        modelsched.setRowCount(0);
        tablesched.setModel(modelsched);
        
        btbrowse.setEnabled(true);
        btpovendbrowse.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
      
        lblcurr.setText("");
        remarks.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        totlines.setText("");
        vendnumber.setText("");
        cbblanket.setEnabled(true);
        
        
        
         ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddshipvia.removeAllItems();
        mylist = OVData.getScacCarrierOnly();   
        for (int i = 0; i < mylist.size(); i++) {
            ddshipvia.addItem(mylist.get(i));
        }
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
        
        
        
        dduom.removeAllItems();
        mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
         ddpart.setForeground(Color.black);
        vendnumber.setForeground(Color.black);
        vendnumber.setEditable(false);
        
        isLoad = true;
        ddvend.removeAllItems();
          ArrayList myvends = OVData.getvendmstrlist();
          for (int i = 0; i < myvends.size(); i++) {
            ddvend.addItem(myvends.get(i));
          }
          ddvend.insertItemAt("", 0);
          ddvend.setSelectedIndex(0);
        isLoad = false;
        
          
          if (! arg.isEmpty()) {
              getOrder(arg);
          }
         
               if (! arg.isEmpty()) {
            getOrder(arg);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btbrowse.setEnabled(true);
              btpovendbrowse.setEnabled(true);
            
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        ordernbr = new javax.swing.JTextField();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel77 = new javax.swing.JLabel();
        remarks = new javax.swing.JTextField();
        userid = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        ddstatus = new javax.swing.JComboBox();
        ddvend = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btedit = new javax.swing.JButton();
        ddshipvia = new javax.swing.JComboBox();
        jLabel90 = new javax.swing.JLabel();
        cbblanket = new javax.swing.JCheckBox();
        ddsite = new javax.swing.JComboBox();
        jLabel91 = new javax.swing.JLabel();
        btpoprint = new javax.swing.JButton();
        orddate = new com.toedter.calendar.JDateChooser();
        btbrowse = new javax.swing.JButton();
        tbbuyer = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        btpovendbrowse = new javax.swing.JButton();
        jLabel83 = new javax.swing.JLabel();
        tbcurr = new javax.swing.JTextField();
        panelSchedule = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablesched = new javax.swing.JTable();
        panelDetail = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        qtyshipped = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox();
        vendnumber = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        netprice = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        listprice = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        btdelitem = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        lblcurr = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Purchase Order Maintenance"));
        panelMain.setPreferredSize(new java.awt.Dimension(627, 550));

        jLabel76.setText("OrderNbr");

        jLabel86.setText("Remarks");

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel77.setText("DateCreated");

        jLabel78.setText("UserID");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel85.setText("Status");

        jLabel82.setText("Vendor");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Open", "Shipped", "PartialShipped", "Hold", "Canceled" }));

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Due Date");

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jLabel90.setText("ShipVia");

        cbblanket.setText("Blanket");

        jLabel91.setText("Site");

        btpoprint.setText("Print");
        btpoprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpoprintActionPerformed(evt);
            }
        });

        orddate.setDateFormatString("yyyy-MM-dd");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jLabel92.setText("Buyer");

        btpovendbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btpovendbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpovendbrowseActionPerformed(evt);
            }
        });

        jLabel83.setText("Currency");

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel91))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel82))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel90))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jLabel92))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jLabel85)))
                        .addGap(5, 5, 5)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbblanket)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(115, 115, 115)
                                .addComponent(jLabel77)
                                .addGap(9, 9, 9)
                                .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(144, 144, 144)
                                .addComponent(jLabel78)
                                .addGap(9, 9, 9)
                                .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(133, 133, 133)
                                .addComponent(jLabel83)
                                .addGap(9, 9, 9)
                                .addComponent(tbcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(422, 422, 422)
                        .addComponent(btpoprint)
                        .addGap(6, 6, 6)
                        .addComponent(btedit)
                        .addGap(10, 10, 10)
                        .addComponent(btadd))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel86)
                        .addGap(4, 4, 4)
                        .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel76)
                        .addGap(5, 5, 5)
                        .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btnew)
                        .addGap(99, 99, 99)
                        .addComponent(jLabel81)
                        .addGap(9, 9, 9)
                        .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btpovendbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel76))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btbrowse)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(btnew))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel81))
                    .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btpovendbrowse))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel91)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel77))
                    .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel82)
                            .addComponent(jLabel78))))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel90)
                            .addComponent(jLabel83))))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel92))
                    .addComponent(tbbuyer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel85))
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(cbblanket)
                .addGap(2, 2, 2)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel86))
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(179, 179, 179)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btpoprint)
                    .addComponent(btedit)
                    .addComponent(btadd))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        add(panelMain);

        panelSchedule.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule Releases"));
        panelSchedule.setPreferredSize(new java.awt.Dimension(640, 550));
        panelSchedule.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablesched.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tablesched);

        panelSchedule.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 27, 608, 440));

        add(panelSchedule);

        panelDetail.setPreferredSize(new java.awt.Dimension(640, 550));

        qtyshipped.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyshippedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                qtyshippedFocusLost(evt);
            }
        });

        jLabel79.setText("PartNumber");

        ddpart.setEditable(true);
        ddpart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpartActionPerformed(evt);
            }
        });

        jLabel87.setText("VendNumber");

        jLabel84.setText("Order Qty");

        jLabel5.setText("uom");

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel79)
                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(vendnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel84)
                            .addGap(3, 3, 3)
                            .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vendnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        netprice.setEditable(false);
        netprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netpriceActionPerformed(evt);
            }
        });

        jLabel80.setText("ListPrice");

        listprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                listpriceFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel89.setText("NetPrice");

        btdelitem.setText("Del Item");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(netprice)
                    .addComponent(listprice)
                    .addComponent(discount))
                .addGap(99, 99, 99))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btadditem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelitem))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem)))
        );

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
        jScrollPane8.setViewportView(orddet);

        jLabel1.setText("Total Lines");

        jLabel2.setText("Total Qty");

        jLabel3.setText("TotCost");

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetailLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        add(panelDetail);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     
         initvars("");
        
        if (ddvend.getItemCount() > 0) {
           vendChangeEvent(ddvend.getSelectedItem().toString());
        } 
        
         ordernbr.setText(String.valueOf(OVData.getNextNbr("order")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                userid.setText(clockdate);
                userid.setEnabled(false);
                duedate.setDate(now);
               
                
                // tbDateShippedSM.setEnabled(false);
                
                userid.setText(bsmf.MainFrame.userid.toString());
                
                userid.setEnabled(false);
                
                
                enableAll();
                
                ddstatus.setEnabled(false);
                btnew.setEnabled(false);
                btbrowse.setEnabled(false);
                btedit.setEnabled(false);
                btadd.setEnabled(false);
                btpoprint.setEnabled(false);
                btpovendbrowse.setEnabled(false);
                
                editmode = false;
               
               
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        
        String part = "";
        String custpart = "";
        
            part = ddpart.getSelectedItem().toString();
            custpart = vendnumber.getText().toString();
       
        orddet.setModel(myorddetmodel);
        
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(listprice.getText());
        if (!m.find() || listprice.getText() == null) {
            bsmf.MainFrame.show("Invalid List Price format");
            canproceed = false;
        }
        
        p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        m = p.matcher(discount.getText());
        if (!m.find() || discount.getText() == null) {
            bsmf.MainFrame.show("Invalid Discount format");
            canproceed = false;
        }
        
        p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        m = p.matcher(netprice.getText());
        if (!m.find() || netprice.getText() == null) {
            bsmf.MainFrame.show("Invalid Net Price format");
            canproceed = false;
        }
        
        p = Pattern.compile("^[1-9]\\d*$");
        m = p.matcher(qtyshipped.getText());
        if (!m.find() || qtyshipped.getText() == null) {
            bsmf.MainFrame.show("Invalid Qty");
            canproceed = false;
        }
        line = getmaxline();
        line++;
        
           if (line == 1 && ! editmode) {
            btadd.setEnabled(true);
        } 
        
        //    "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice"
        //  "Line, "Part", "VendPart", "PO", "Line", "Qty", "Rcvd Qty", "ListPrice", "Discount", "NetPrice"
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{line, part, custpart, ordernbr.getText(),  qtyshipped.getText(), listprice.getText(), discount.getText(), netprice.getText(), "0", "open"});
         sumqty();
         sumdollars();
         sumlinecount();
         listprice.setText("");
         netprice.setText("");
         discount.setText("");
         qtyshipped.setText("");
         ddpart.requestFocus();
         
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
                String terms = "";
                String acct = "";
                String cc = "";
                
                  res = st.executeQuery("Select * from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem() + "';" );
                    while (res.next()) {
                        i++;
                        terms = res.getString("vd_terms");
                        acct = res.getString("vd_ap_acct");
                        cc = res.getString("vd_ap_cc");
                    }
                    
                    if (i == 0) {
                        proceed = false;
                        bsmf.MainFrame.show("No Vendor Master found");
                    }
                    if (terms == null   || acct == null   || cc == null ||
                        terms.isEmpty() || acct.isEmpty() || cc.isEmpty() 
                         ) {
                        proceed = false;
                        bsmf.MainFrame.show("Terms or acct or cc is not defined for this vendor");
                    }   
                
                
                
                          // lets check for foreign currency with no exchange rate
                if (! curr.toUpperCase().equals(basecurr.toUpperCase())) {
                if (OVData.getExchangeRate(basecurr, curr).isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Foreign currency has no exchange rate " + curr + "/" + basecurr);
                }
                }
                    
                
                
                if (proceed) {
                    st.executeUpdate("insert into po_mstr "
                        + "(po_nbr, po_vend, po_site, po_type, po_curr, po_buyer, po_due_date, "
                        + " po_ord_date, po_userid, po_status,"
                        + " po_terms, po_ap_acct, po_ap_cc, po_rmks ) "
                        + " values ( " + "'" + ordernbr.getText().toString() + "'" + ","
                        + "'" + ddvend.getSelectedItem().toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbblanket.isSelected()) + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + tbbuyer.getText() + "'" + ","        
                        + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(orddate.getDate()).toString() + "'" + ","
                        + "'" + userid.getText() + "'" + ","
                        + "'" + ddstatus.getSelectedItem() + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + remarks.getText() + "'"
                        + ")"
                        + ";");

                  
                 
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                        st.executeUpdate("insert into pod_mstr "
                            + "(pod_line, pod_part, pod_vendpart, pod_nbr, pod_ord_qty, pod_listprice, pod_disc, pod_netprice, pod_due_date, "
                            + "pod_rcvd_qty, pod_status, pod_site) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                            + '0' + "," 
                            + "''" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");

                    }
                    bsmf.MainFrame.show("PO has been added");
                   initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot add PO");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void ddpartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpartActionPerformed
         if (ddpart.getSelectedItem() != null)
        getparts(ddpart.getSelectedItem().toString());
    }//GEN-LAST:event_ddpartActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed

        if (ddvend.getItemCount() > 0 && ! isLoad && ! ddvend.getSelectedItem().toString().isEmpty()) {
           vendChangeEvent(ddvend.getSelectedItem().toString());
           jTabbedPane1.setEnabledAt(1, true);
        } // if ddvend has a list
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
         int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 9).toString().equals("close") || orddet.getValueAt(i, 9).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumdollars();
         sumlinecount();
    }//GEN-LAST:event_btdelitemActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bteditActionPerformed

    private void netpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netpriceActionPerformed

    private void listpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusLost
      if (listprice.getText().isEmpty()) {
            listprice.setText("0");
        }
        listprice.setText(df.format(Double.valueOf(listprice.getText())));
        setnetprice();
    }//GEN-LAST:event_listpriceFocusLost

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
        if (discount.getText().isEmpty()) {
            discount.setText("0");
        }
        discount.setText(df.format(Double.valueOf(discount.getText()))); 
        setnetprice();
    }//GEN-LAST:event_discountFocusLost

    private void btpoprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpoprintActionPerformed
       OVData.printPurchaseOrder(ordernbr.getText());
    }//GEN-LAST:event_btpoprintActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "pomaint,po_nbr");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btpovendbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpovendbrowseActionPerformed
       reinitpanels("BrowseUtil", true, "pomaint,po_vend");
    }//GEN-LAST:event_btpovendbrowseActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
       setPrice();
    }//GEN-LAST:event_dduomActionPerformed

    private void qtyshippedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusGained
        if (qtyshipped.getText().equals("0")) {
            qtyshipped.setText("");
        }
    }//GEN-LAST:event_qtyshippedFocusGained

    private void qtyshippedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusLost
              String x = BlueSeerUtils.bsformat("", qtyshipped.getText(), "0");
        if (x.equals("error")) {
            qtyshipped.setText("");
            qtyshipped.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            qtyshipped.requestFocus();
        } else {
            qtyshipped.setText(x);
            qtyshipped.setBackground(Color.white);
        }
        
    }//GEN-LAST:event_qtyshippedFocusLost

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
         JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if (index == 1 && ddpart != null && ddpart.getItemCount() > 0) {
            ddpart.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btpoprint;
    private javax.swing.JButton btpovendbrowse;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField ordernbr;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelSchedule;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTable tablesched;
    private javax.swing.JTextField tbbuyer;
    private javax.swing.JTextField tbcurr;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    private javax.swing.JTextField userid;
    private javax.swing.JTextField vendnumber;
    // End of variables declaration//GEN-END:variables
}
