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
package com.blueseer.ctr;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.ord.ordData.addPOSTransaction;
import com.blueseer.ord.ordData.pos_det;
import com.blueseer.ord.ordData.pos_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


/**
 *
 * @author vaughnte
 */
public class POSMaint extends javax.swing.JPanel {

   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
               getGlobalColumnTag("number"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"),  
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("tax")
            });
    
    
    String clockdate = "";
    String clocktime = "";
  
  
  
    
  
     
    public void getItemInfo(String item) {
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               // partnbr.removeAllItems();
               int i = 0;
               
                // lets first try as cust part
               
                res = st.executeQuery("select * from item_mstr where it_item = " + "'" + item.toString() + "'" + ";");
                while (res.next()) {
                    i++;
                    dditem.setSelectedItem(res.getString("it_item"));
                    lbdesc.setText(res.getString("it_desc"));
                    lbdesc.setForeground(Color.black);
                    dditem.setForeground(Color.blue);
                   
                }
                
                // if i is still 0...then must be a misc item
                if (i == 0) {
                  //  partnbr.addItem(part);
                  //  partnbr.setSelectedItem(part);
                    lbdesc.setText("Item not in inventory");
                    lbdesc.setForeground(Color.red);
                    dditem.setForeground(Color.red);
                     tbdisc.setText("0");
            tblistprice.setText("0");
            tbnetprice.setText("0");
            tbtax.setText("0");
            tbqty.setText("1");
                   
                    
                }
               
                
             if (i > 0) {   
            tbdisc.setText("0");
            tblistprice.setText("0");
            tbnetprice.setText("0");
            tbtax.setText("0");
            tbqty.setText("1");
            double discpercent = 0;
            String mypart = "";
            if (dditem.getItemCount() > 0) {
                mypart = dditem.getSelectedItem().toString();
                tblistprice.setText(currformatDouble(invData.getItemPOSPrice(dditem.getSelectedItem().toString())));
                tbdisc.setText(currformatDouble(invData.getItemPOSDisc(dditem.getSelectedItem().toString())));
                dditem.setForeground(Color.blue);
                setnetprice();
                 tbqty.setText("1");
             
            } // if part selected
             }  
                
                
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void setnetprice() {
        double disc = 0;
        double list = 0;
        double net = 0;
        
        if (tbdisc.getText().isEmpty() || bsParseDouble(tbdisc.getText().toString()) == 0) {
            tbnetprice.setText(tblistprice.getText());
        } else {
           
           if (tblistprice.getText().isEmpty() || bsParseDouble(tblistprice.getText().toString()) == 0) {
             tblistprice.setText("0");
             tbnetprice.setText("0");
           } else {               
           disc = bsParseDouble(tbdisc.getText().toString());
           list = bsParseDouble(tblistprice.getText().toString());
            
           net = list - ((disc / 100) * list);
           tbnetprice.setText(currformatDouble(net));
           }
        }
    }
   
    public void disable() {
       tblistprice.setEnabled(false);
       tbnetprice.setEnabled(false);
       tbtax.setEnabled(false);
       tbdisc.setEnabled(false);
       tbqty.setEnabled(false);
       tbtotqty.setEnabled(false);
       tbtotnet.setEnabled(false);
       tbtotgross.setEnabled(false);
       tbtottax.setEnabled(false);
       tbtotlines.setEnabled(false);
       tbcash.setEnabled(false);
       tbchange.setEnabled(false);
       dditem.setEnabled(false);
       tblistprice.setEnabled(false);
       ordernbr.setEnabled(false);
       
        
        btcommit.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
        
        rbcash.setEnabled(false);
        rbcard.setEnabled(false);
        
        orddet.setEnabled(false);
   }
    
    public void enable() {
       tblistprice.setEnabled(true);
       tbnetprice.setEnabled(true);
       tbtax.setEnabled(true);
       tbdisc.setEnabled(true);
       tbqty.setEnabled(true);
       tbtotqty.setEnabled(true);
       tbtotnet.setEnabled(true);
       tbtotgross.setEnabled(true);
       tbtottax.setEnabled(true);
       tbtotlines.setEnabled(true);
       tbcash.setEnabled(true);
       tbchange.setEnabled(true);
       dditem.setEnabled(true);
       tblistprice.setEnabled(true);
       ordernbr.setEnabled(true);
        
        btcommit.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        rbcash.setEnabled(true);
        rbcard.setEnabled(true);
        
        orddet.setEnabled(true);
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
        ArrayList<String> mylist = new ArrayList<String>();
       
        disable();
        
       
        ordernbr.setText("");
        
        tblistprice.setText("0");
        tbnetprice.setText("0");
        tbnetprice.setEditable(false);
        tbqty.setText("0");
        tbdisc.setText("0");
       
        
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
       
        
       
        btnew.setEnabled(true);
        btnew.requestFocus();
        
        
       
       
        tbtotqty.setText("");
        tbtotnet.setText("");
        tbtotgross.setText("");
        tbtottax.setText("");
        tbtotlines.setText("");
        tbcash.setText("");
        tbchange.setText("");
       
        
        tbcash.setBackground(Color.white);
        
        tbtotqty.setEditable(false);
        tbtotnet.setEditable(false);
        tbtotgross.setEditable(false);
        tbtottax.setEditable(false);
        tbtotlines.setEditable(false);
        ordernbr.setEditable(false);
        tbchange.setEditable(false);
        tbcash.setEditable(false);
     
        buttonGroup1.add(rbcash);
        buttonGroup1.add(rbcard);
        
        
        dditem.setForeground(Color.black);
         lbdesc.setForeground(Color.black);
        //custnumber.setEnabled(false);
        
        
        
         dditem.removeAllItems();
        mylist = invData.getItemMasterAlllist();
        for (String item : mylist) {
            dditem.addItem(item);
        }
        
        
        // disable certain fields
     
      
          if (arg != null && arg.length > 0) {
            //getOrder(arg);
        }

    }
    
    public void sumlinecount() {
         tbtotlines.setText(String.valueOf(orddet.getRowCount()));
    }
    
    public void sumqty() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + bsParseDouble(orddet.getValueAt(j, 3).toString()); 
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
    
    public pos_mstr createRecord() {
        String bank = OVData.getPOSBank(); 
        String aracct = OVData.getDefaultARAcct();
        String arcc = OVData.getDefaultARCC();
        pos_mstr x = new pos_mstr(null, 
                ordernbr.getText().toString(),
                clockdate.toString(),
                clocktime.toString(),
                aracct,
                arcc,
                bank,
                tbtotqty.getText().replace(defaultDecimalSeparator, '.'),
                tbtotlines.getText(),
                currformatDouble(bsParseDouble(tbtotgross.getText())).replace(defaultDecimalSeparator, '.'),
                currformatDouble(bsParseDouble(tbtottax.getText())).replace(defaultDecimalSeparator, '.'),
                currformatDouble(bsParseDouble(tbtotnet.getText())).replace(defaultDecimalSeparator, '.')
                );
        return x;
    }
    
     public ArrayList<pos_det> createDetRecord() {
        ArrayList<pos_det> list = new ArrayList<pos_det>();
            for (int j = 0; j < orddet.getRowCount(); j++) {
                pos_det x = new pos_det(null, 
                orddet.getValueAt(j, 0).toString(),
                orddet.getValueAt(j, 1).toString(),
                orddet.getValueAt(j, 2).toString(),
                orddet.getValueAt(j, 3).toString().replace(defaultDecimalSeparator, '.'),
                orddet.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'),
                orddet.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.'),
                orddet.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'),
                orddet.getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.')
                );  
                list.add(x);
            } 
        return list;
    }
    
    
    
    
    public double calcGrossTotal() {
        double dol = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(orddet.getValueAt(j, 3).toString()) * bsParseDouble(orddet.getValueAt(j, 6).toString()) ); 
         }
         return dol;
    }
    
    public double calcTotalTax(double grossamt) {
        double dol = 0;
        double pct = OVData.getPOSSalesTaxPct();
        dol = (pct / 100) * grossamt;
         return dol;
    }
    
    public void commitTransactions(String nbr) {
         java.util.Date now = new java.util.Date();
        Connection bscon = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            fglData.glEntryFromPOS(nbr, now, bscon);
            OVData.TRHistIssSalesPOS(nbr, now, false, bscon); 
            OVData.UpdateInventoryFromPOS(nbr, false, bscon);
           // OVData.voidPOSStatus(nbr, bscon);
            bscon.commit();
            bsmf.MainFrame.show(getMessageTag(1083));
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             }
        } finally {
            if (bscon != null) {
                try {
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    }
  
    /**
     * Creates new form OrderMaintPanel
     */
    public POSMaint() {
        initComponents();
        setLanguageTags(this);
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
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        ordernbr = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btcommit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        dditem = new javax.swing.JComboBox();
        lblpart1 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        lbdesc = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tblistprice = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel80 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        tbtax = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        tbdisc = new javax.swing.JTextField();
        tbnetprice = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        tbtotqty = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtottax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotlines = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbtotgross = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbcash = new javax.swing.JTextField();
        tbchange = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbtotnet = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rbcash = new javax.swing.JRadioButton();
        rbcard = new javax.swing.JRadioButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Point Of Sales Entry"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Maintenance"));

        jLabel76.setText("Transaction Number");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
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
        jScrollPane8.setViewportView(orddet);

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        dditem.setEditable(true);
        dditem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                dditemFocusLost(evt);
            }
        });
        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        lblpart1.setText("Item");

        tblistprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblistpriceFocusLost(evt);
            }
        });

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel80.setText("ListPrice");

        jLabel84.setText("Quantity");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel84)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel80)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblistprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblistprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel90.setText("Tax");

        tbdisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdiscFocusLost(evt);
            }
        });

        tbnetprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbnetpriceActionPerformed(evt);
            }
        });

        jLabel89.setText("NetPrice");

        jLabel88.setText("Disc%");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addGap(7, 7, 7)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbdisc)
                    .addComponent(tbtax)
                    .addComponent(tbnetprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbnetprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addContainerGap())
        );

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblpart1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(btadditem)
                        .addGap(8, 8, 8)
                        .addComponent(btdelitem)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblpart1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btadditem)
                            .addComponent(btdelitem)))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(74, 74, 74))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(46, 46, 46))
        );

        jLabel1.setText("Total Qty:");

        jLabel2.setText("Tax:");

        jLabel3.setText("Total Lines:");

        jLabel4.setText("Gross Total:");

        tbcash.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcashFocusLost(evt);
            }
        });

        jLabel5.setText("Cash");

        jLabel6.setText("Change");

        jLabel7.setText("Total Due:");

        rbcash.setText("Cash");
        rbcash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbcashActionPerformed(evt);
            }
        });

        rbcard.setText("Card");
        rbcard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbcardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnew)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbtotlines)
                            .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbtotnet, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbtottax)
                            .addComponent(tbtotgross, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbcash)
                            .addComponent(rbcard))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbcash, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btcommit))
                            .addComponent(tbchange, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotgross, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(tbcash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(rbcash)
                    .addComponent(btcommit))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbchange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(rbcard))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotnet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        add(jPanel6);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      
        enable();
        
      
        
         ordernbr.setText(String.valueOf(OVData.getNextNbr("pos")));
               
                
            
                
                // tbDateShippedSM.setEnabled(false);
                
              
                
               
                btnew.setEnabled(false);
               
                btcommit.setEnabled(true);
                btadditem.setEnabled(true);
                btdelitem.setEnabled(true);
               
               
        
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        int line = 0;
         String part = "";
        String custpart = "";
        
            
        
        
        part = dditem.getSelectedItem().toString();
       
        orddet.setModel(myorddetmodel);
       
      
        line = getmaxline();
        line++;
       
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{ordernbr.getText(), line, part, tbqty.getText(), tblistprice.getText(), tbdisc.getText(), tbnetprice.getText(), tbtax.getText()});
         sumqty();
         sumlinecount();
         double grosstotal = calcGrossTotal();
         double tax = calcTotalTax(grosstotal);
         double nettotal = grosstotal + tax;
         tbtotgross.setText(currformatDouble(grosstotal));
         tbtottax.setText(currformatDouble(tax));
         tbtotnet.setText(currformatDouble(nettotal));
        
         dditem.requestFocus();
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        clockdate = dfdate.format(now);
        clocktime = dftime.format(now);
        String[] m = new String[2];
        m = addPOSTransaction(createDetRecord(), createRecord());
        commitTransactions(ordernbr.getText());
    
    }//GEN-LAST:event_btcommitActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 10).toString().equals("close") || orddet.getValueAt(i, 10).toString().equals("partial")) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
                            } else {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumlinecount();
         double grosstotal = calcGrossTotal();
         double tax = calcTotalTax(grosstotal);
         double nettotal = grosstotal + tax;
         tbtotgross.setText(currformatDouble(grosstotal));
         tbtottax.setText(currformatDouble(nettotal));
         tbtotnet.setText(currformatDouble(tax));
    }//GEN-LAST:event_btdelitemActionPerformed

    private void tbnetpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbnetpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbnetpriceActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        if (dditem.getSelectedItem() != null)
        getItemInfo(dditem.getSelectedItem().toString()); 
    }//GEN-LAST:event_dditemActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0")) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
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
        
    }//GEN-LAST:event_tbqtyFocusLost

    private void tblistpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblistpriceFocusLost
         if (! tblistprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tblistprice.getText(), "4");
        if (x.equals("error")) {
            tblistprice.setText("");
            tblistprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tblistprice.requestFocus();
        } else {
            tblistprice.setText(x);
            tblistprice.setBackground(Color.white);
        }
        setnetprice();
        }
    }//GEN-LAST:event_tblistpriceFocusLost

    private void tbdiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdiscFocusLost
           if (! tbdisc.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbdisc.getText(), "4");
        if (x.equals("error")) {
            tbdisc.setText("");
            tbdisc.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbdisc.requestFocus();
        } else {
            tbdisc.setText(x);
            tbdisc.setBackground(Color.white);
        }
        setnetprice();
        }
    }//GEN-LAST:event_tbdiscFocusLost

    private void rbcashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbcashActionPerformed
       tbcash.setEditable(true);
       tbcash.setBackground(Color.yellow);
       tbcash.requestFocus();
    }//GEN-LAST:event_rbcashActionPerformed

    private void rbcardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbcardActionPerformed
         tbcash.setBackground(Color.white);
         tbcash.setEditable(false);
         tbcash.setText("");
         tbchange.setText("");
    }//GEN-LAST:event_rbcardActionPerformed

    private void tbcashFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcashFocusLost
          double change = bsParseDouble(tbcash.getText()) - bsParseDouble(tbtotnet.getText());
        tbchange.setText(currformatDouble(change));
        tbcash.setBackground(Color.white);
        
    }//GEN-LAST:event_tbcashFocusLost

    private void dditemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dditemFocusLost
       tbqty.requestFocus();
    }//GEN-LAST:event_dditemFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btnew;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox dditem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbdesc;
    private javax.swing.JLabel lblpart1;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField ordernbr;
    private javax.swing.JRadioButton rbcard;
    private javax.swing.JRadioButton rbcash;
    private javax.swing.JTextField tbcash;
    private javax.swing.JTextField tbchange;
    private javax.swing.JTextField tbdisc;
    private javax.swing.JTextField tblistprice;
    private javax.swing.JTextField tbnetprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbtax;
    private javax.swing.JTextField tbtotgross;
    private javax.swing.JTextField tbtotlines;
    private javax.swing.JTextField tbtotnet;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtottax;
    // End of variables declaration//GEN-END:variables
}
