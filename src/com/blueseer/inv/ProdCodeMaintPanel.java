/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.inv;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class ProdCodeMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProdCodeMaintPanel
     */
    public ProdCodeMaintPanel() {
        initComponents();
    }

    
    public boolean checkAccts() {
        boolean proceed = true;
         if (! OVData.isValidGLAcct(tbinvacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Inv Acct Nbr!");
                   tbinvacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbinvdescrepancyacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Inv Desc Acct Nbr!");
                   tbinvdescrepancyacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbwipacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Wip Acct Nbr!");
                   tbwipacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbwipvaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Wip Var Acct Nbr!");
                   tbwipvaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbscrapacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Scrap Acct Nbr!");
                   tbscrapacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbinvchangeacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Inv Change Acct Nbr!");
                   tbinvchangeacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbsalesacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Sales Acct Nbr!");
                   tbsalesacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbsalesdiscacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Acct Nbr!");
                   tbsalesdiscacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcogsmtlacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cogs Mtl Acct Nbr!");
                   tbcogsmtlacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcogslbracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cogs Lbr Acct Nbr!");
                   tbcogslbracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcogsbdnacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cogs Bdn Acct Nbr!");
                   tbcogsbdnacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcogsovhacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cogs Ovh Acct Nbr!");
                   tbcogsovhacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcogsoutacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cogs Outside Acct Nbr!");
                   tbcogsoutacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbpurchacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Purch Acct Nbr!");
                   tbpurchacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbporcptacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid PO Rcpts Acct Nbr!");
                   tbporcptacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbpoovhacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid PO Ovh Acct Nbr!");
                   tbpoovhacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbpopricevaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid PO Price Var Acct Nbr!");
                   tbpopricevaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbapusageacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid AP Usage Acct Nbr!");
                   tbapusageacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbapratevaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid AP Rate Var Acct Nbr!");
                   tbapratevaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbjobstockacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid job stock Acct Nbr!");
                   tbjobstockacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbmtlusagevaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Mtl Usage Var Acct Nbr!");
                   tbmtlusagevaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbmtlratevaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Mtl Rate Var Acct Nbr!");
                   tbmtlratevaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbmixedvaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Mixed Var Acct Nbr!");
                   tbmixedvaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tbcopacct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Cost Of Production Acct Nbr!");
                   tbcopacct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tboutusgvaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Outsie Usage Var Acct Nbr!");
                   tboutusgvaracct.requestFocus();
                   return proceed;
                }
         if (! OVData.isValidGLAcct(tboutratevaracct.getText().toString())) {
                   proceed = false;
                   bsmf.MainFrame.show("Invalid Outside Rate Var Acct Nbr!");
                   tboutratevaracct.requestFocus();
                   return proceed;
                }
         
         
        return proceed;
    }
    
    public void getProdCode(String arg) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;

                    res = st.executeQuery("SELECT * FROM  pl_mstr where pl_line = " + "'" + arg + "'" + ";");
                    while (res.next()) {
                        i++;
                         tbnbr.setText(res.getString("pl_line"));
                         tbdesc.setText(res.getString("pl_desc"));
                         tbinvacct.setText(res.getString("pl_inventory"));
                         tbinvdescrepancyacct.setText(res.getString("pl_inv_discr"));
                         tbscrapacct.setText(res.getString("pl_scrap"));
                         tbwipacct.setText(res.getString("pl_wip"));
                         tbwipvaracct.setText(res.getString("pl_wip_var"));
                         tbinvchangeacct.setText(res.getString("pl_inv_change"));
                         tbsalesacct.setText(res.getString("pl_sales"));
                         tbsalesdiscacct.setText(res.getString("pl_sales_disc"));
                         tbcogsmtlacct.setText(res.getString("pl_cogs_mtl"));
                         tbcogslbracct.setText(res.getString("pl_cogs_lbr"));
                         tbcogsbdnacct.setText(res.getString("pl_cogs_bdn"));
                         tbcogsovhacct.setText(res.getString("pl_cogs_ovh"));
                         tbcogsoutacct.setText(res.getString("pl_cogs_out"));
                         tbpurchacct.setText(res.getString("pl_purchases"));
                         tbporcptacct.setText(res.getString("pl_po_rcpt"));
                         tbpoovhacct.setText(res.getString("pl_po_ovh"));
                         tbpopricevaracct.setText(res.getString("pl_po_pricevar"));
                         tbapusageacct.setText(res.getString("pl_ap_usage"));
                         tbapratevaracct.setText(res.getString("pl_ap_ratevar"));
                         tbjobstockacct.setText(res.getString("pl_job_stock"));
                         tbmtlusagevaracct.setText(res.getString("pl_mtl_usagevar"));
                         tbmtlratevaracct.setText(res.getString("pl_mtl_ratevar"));
                         tbmixedvaracct.setText(res.getString("pl_mix_var"));
                         tbcopacct.setText(res.getString("pl_cop"));
                         tboutusgvaracct.setText(res.getString("pl_out_usagevar"));
                         tboutratevaracct.setText(res.getString("pl_out_ratevar"));
                         
                       
                         
                    }
                  if (i > 0) {
                    enableAll();
                    tbnbr.setEnabled(false);
                    btadd.setEnabled(false);
                }
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Get Prod Code");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void clearAll() {
        tbnbr.setText("");
         tbdesc.setText("");
         tbinvacct.setText("");
         tbinvdescrepancyacct.setText("");
         tbscrapacct.setText("");
         tbwipacct.setText("");
         tbwipvaracct.setText("");
         tbinvchangeacct.setText("");
         tbsalesacct.setText("");
         tbsalesdiscacct.setText("");
         tbcogsmtlacct.setText("");
         tbcogslbracct.setText("");
         tbcogsbdnacct.setText("");
         tbcogsovhacct.setText("");
         tbcogsoutacct.setText("");
         tbpurchacct.setText("");
         tbporcptacct.setText("");
         tbpoovhacct.setText("");
         tbpopricevaracct.setText("");
         tbapusageacct.setText("");
         tbapratevaracct.setText("");
         tbjobstockacct.setText("");
         tbmtlusagevaracct.setText("");
         tbmtlratevaracct.setText("");
         tbmixedvaracct.setText("");
         tbcopacct.setText("");
         tboutusgvaracct.setText("");
         tboutratevaracct.setText("");
         tbtbd.setText("");
    }
    
    public void enableAll() {
         btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        tbnbr.setEnabled(true);
         tbdesc.setEnabled(true);
         tbinvacct.setEnabled(true);
         tbinvdescrepancyacct.setEnabled(true);
         tbscrapacct.setEnabled(true);
         tbwipacct.setEnabled(true);
         tbwipvaracct.setEnabled(true);
         tbinvchangeacct.setEnabled(true);
         tbsalesacct.setEnabled(true);
         tbsalesdiscacct.setEnabled(true);
         tbcogsmtlacct.setEnabled(true);
         tbcogslbracct.setEnabled(true);
         tbcogsbdnacct.setEnabled(true);
         tbcogsovhacct.setEnabled(true);
         tbcogsoutacct.setEnabled(true);
         tbpurchacct.setEnabled(true);
         tbporcptacct.setEnabled(true);
         tbpoovhacct.setEnabled(true);
         tbpopricevaracct.setEnabled(true);
         tbapusageacct.setEnabled(true);
         tbapratevaracct.setEnabled(true);
         tbjobstockacct.setEnabled(true);
         tbmtlusagevaracct.setEnabled(true);
         tbmtlratevaracct.setEnabled(true);
         tbmixedvaracct.setEnabled(true);
         tbcopacct.setEnabled(true);
         tboutusgvaracct.setEnabled(true);
         tboutratevaracct.setEnabled(true);
         tbtbd.setEnabled(true);
    }
    
    public void disableAll() {
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        tbnbr.setEnabled(false);
         tbdesc.setEnabled(false);
         tbinvacct.setEnabled(false);
         tbinvdescrepancyacct.setEnabled(false);
         tbscrapacct.setEnabled(false);
         tbwipacct.setEnabled(false);
         tbwipvaracct.setEnabled(false);
         tbinvchangeacct.setEnabled(false);
         tbsalesacct.setEnabled(false);
         tbsalesdiscacct.setEnabled(false);
         tbcogsmtlacct.setEnabled(false);
         tbcogslbracct.setEnabled(false);
         tbcogsbdnacct.setEnabled(false);
         tbcogsovhacct.setEnabled(false);
         tbcogsoutacct.setEnabled(false);
         tbpurchacct.setEnabled(false);
         tbporcptacct.setEnabled(false);
         tbpoovhacct.setEnabled(false);
         tbpopricevaracct.setEnabled(false);
         tbapusageacct.setEnabled(false);
         tbapratevaracct.setEnabled(false);
         tbjobstockacct.setEnabled(false);
         tbmtlusagevaracct.setEnabled(false);
         tbmtlratevaracct.setEnabled(false);
         tbmixedvaracct.setEnabled(false);
         tbcopacct.setEnabled(false);
         tboutusgvaracct.setEnabled(false);
         tboutratevaracct.setEnabled(false);
         tbtbd.setEnabled(false);
    }
    
    public void initvars(String arg) {
         
         
        clearAll();
          disableAll();
          btbrowse.setEnabled(true);
          btnew.setEnabled(true);
         
         if (! arg.isEmpty()) {
             getProdCode(arg);
         }
       
    }
    /**
     * This method is called from within the bsmf.MainFrame.constructor to initialize the form.
     * WARNING: Do NOT modify this code. The bsmf.MainFrame.content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tbwipacct = new javax.swing.JTextField();
        tbsalesacct = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        tbinvacct = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbdesc = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        tbwipvaracct = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        tbscrapacct = new javax.swing.JTextField();
        tbnbr = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        tbinvchangeacct = new javax.swing.JTextField();
        tbsalesdiscacct = new javax.swing.JTextField();
        tbinvdescrepancyacct = new javax.swing.JTextField();
        tbcogsmtlacct = new javax.swing.JTextField();
        jLabel76 = new javax.swing.JLabel();
        tbcogslbracct = new javax.swing.JTextField();
        tbcogsbdnacct = new javax.swing.JTextField();
        tbcogsovhacct = new javax.swing.JTextField();
        tbcogsoutacct = new javax.swing.JTextField();
        tbpurchacct = new javax.swing.JTextField();
        tbporcptacct = new javax.swing.JTextField();
        tbpoovhacct = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        tbpopricevaracct = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        tboutratevaracct = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        tbtbd = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        tbapusageacct = new javax.swing.JTextField();
        tbcopacct = new javax.swing.JTextField();
        tbmixedvaracct = new javax.swing.JTextField();
        tboutusgvaracct = new javax.swing.JTextField();
        tbapratevaracct = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        tbmtlratevaracct = new javax.swing.JTextField();
        tbmtlusagevaracct = new javax.swing.JTextField();
        tbjobstockacct = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Code Maintenance"));

        jLabel75.setText("Inv Descrepancy");

        jLabel71.setText("Wip Variance Acct");

        jLabel66.setText("ProdCode");

        btupdate.setText("Edit");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel69.setText("Scrap Acct");

        jLabel73.setText("Sales Acct");

        jLabel72.setText("Inventory Change Acct");

        jLabel70.setText("Wip Acct");

        jLabel74.setText("Sales Discount Acct");

        jLabel67.setText("Description");

        jLabel68.setText("Inventory Acct");

        jLabel76.setText("COGS Mtl Acct");

        jLabel77.setText("COGS Lbr Acct");

        jLabel78.setText("COGS Bdn Acct");

        jLabel79.setText("COGS Ovh Acct");

        jLabel80.setText("COGS Out Acct");

        jLabel81.setText("Purchases Acct");

        jLabel82.setText("PO Receipt Acct");

        jLabel83.setText("PO Ovh Acct");

        jLabel84.setText("PO Pricevar Acct");

        jLabel85.setText("Matl Usage Var Acct");

        jLabel86.setText("Matl Rate Var Acct");

        jLabel87.setText("AP Rate Var Acct");

        jLabel88.setText("TBD");

        jLabel89.setText("Outside Rate Var Acct");

        jLabel90.setText("Outside Usage Var Acct");

        jLabel91.setText("Cost Of Prod Acct");

        jLabel92.setText("Mixed Var Acct");

        jLabel93.setText("AP Usage Acct");

        jLabel94.setText("Job Stock Acct");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbwipacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel69)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbscrapacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbinvacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbwipvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel72)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbinvchangeacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbsalesacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbsalesdiscacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel75)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbinvdescrepancyacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbjobstockacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel78, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel80, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel83, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel67)
                                .addGap(9, 9, 9)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbcogslbracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcogsmtlacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcogsbdnacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcogsovhacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcogsoutacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbpurchacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbporcptacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbpoovhacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbpopricevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel93, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel86, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel92, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel90, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel89, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel88, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btdelete, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbapratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbapusageacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbmtlusagevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbmtlratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbmixedvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcopacct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tboutusgvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tboutratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtbd, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel67)
                        .addComponent(tbnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel66)
                        .addComponent(btnew))
                    .addComponent(btbrowse))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbinvacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68)
                            .addComponent(tbcogsmtlacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel76))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbinvdescrepancyacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel75)
                            .addComponent(tbcogslbracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbscrapacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel69)
                            .addComponent(tbcogsbdnacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel78))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbwipacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel70)
                            .addComponent(tbcogsovhacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbwipvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71)
                            .addComponent(tbcogsoutacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbinvchangeacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72)
                            .addComponent(tbpurchacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel81))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsalesacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel73)
                            .addComponent(tbporcptacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel82))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsalesdiscacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74)
                            .addComponent(tbpoovhacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel83))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpopricevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel84)
                            .addComponent(tbjobstockacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel94)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbapusageacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel93))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbapratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel87))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmtlusagevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmtlratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel86))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmixedvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel92))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcopacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel91))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tboutusgvaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tboutratevaracct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel89))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtbd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel88))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                
                if (tbnbr.getText().length() > 4) {
                    bsmf.MainFrame.show("ProdLine Code Must be 4 chars or less");
                    return;
                }
                
                
               proceed = checkAccts();
                
                if (proceed) {

                    res = st.executeQuery("SELECT pl_line FROM  pl_mstr where pl_line = " + "'" + tbnbr.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }

                    if (i == 0) {
                        st.executeUpdate("insert into pl_mstr "
                            + "(pl_line, pl_desc, pl_inventory, pl_inv_discr, "
                            + "pl_scrap, pl_wip, pl_wip_var, pl_inv_change, pl_sales, pl_sales_disc, "
                            + "pl_cogs_mtl, pl_cogs_lbr, pl_cogs_bdn, pl_cogs_ovh, pl_cogs_out, "
                            + "pl_purchases, pl_po_rcpt, pl_po_ovh, pl_po_pricevar, pl_ap_usage, pl_ap_ratevar, "
                            + "pl_job_stock, pl_mtl_usagevar, pl_mtl_ratevar, pl_mix_var, pl_cop, pl_out_usagevar, pl_out_ratevar )"
                            + " values ( " + "'" + tbnbr.getText().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString() + "'" + ","
                            + "'" + tbinvacct.getText().toString() + "'" + ","
                            + "'" + tbinvdescrepancyacct.getText().toString() + "'" + ","
                            + "'" + tbscrapacct.getText().toString() + "'" + ","
                            + "'" + tbwipacct.getText().toString() + "'" + ","
                            + "'" + tbwipvaracct.getText().toString() + "'" + ","
                            + "'" + tbinvchangeacct.getText().toString() + "'" + ","
                            + "'" + tbsalesacct.getText().toString() + "'" + ","
                            + "'" + tbsalesdiscacct.getText().toString() + "'" + ","
                            + "'" + tbcogsmtlacct.getText().toString() + "'" + ","
                            + "'" + tbcogslbracct.getText().toString() + "'" + ","
                            + "'" + tbcogsbdnacct.getText().toString() + "'" + ","
                            + "'" + tbcogsovhacct.getText().toString() + "'" + ","
                            + "'" + tbcogsoutacct.getText().toString() + "'" + ","
                            + "'" + tbpurchacct.getText().toString() + "'" + ","
                            + "'" + tbporcptacct.getText().toString() + "'" + ","
                            + "'" + tbpoovhacct.getText().toString() + "'" + ","
                            + "'" + tbpopricevaracct.getText().toString() + "'" + ","
                            + "'" + tbapusageacct.getText().toString() + "'" + ","
                            + "'" + tbapratevaracct.getText().toString() + "'" + ","
                            + "'" + tbjobstockacct.getText().toString() + "'" + ","
                            + "'" + tbmtlusagevaracct.getText().toString() + "'" + ","
                            + "'" + tbmtlratevaracct.getText().toString() + "'" + ","
                            + "'" + tbmixedvaracct.getText().toString() + "'" + ","
                            + "'" + tbcopacct.getText().toString() + "'" + ","
                            + "'" + tboutusgvaracct.getText().toString() + "'" + ","
                            + "'" + tboutratevaracct.getText().toString() + "'"
                            + ")"
                            + ";");

                        bsmf.MainFrame.show("Added Prod Code Record");
                        initvars("");
                    } else {
                    bsmf.MainFrame.show("Prod Code already exists");
                }
                    
                } // if proceed
                
            } 
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add Prod Code");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                
                if (tbnbr.getText().length() > 4) {
                    bsmf.MainFrame.show("ProdLine Code Must be 4 chars or less");
                    return;
                }
                
                proceed = checkAccts();
                
                
                if (proceed) {

                    res = st.executeQuery("SELECT pl_line FROM  pl_mstr where pl_line = " + "'" + tbnbr.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }

                    if (i > 0) {
                        st.executeUpdate("update pl_mstr set "
                            + "pl_desc = " + "'" + tbdesc.getText().toString() + "'" + ","
                            + "pl_inventory = " + "'" + tbinvacct.getText().toString() + "'" + ","
                            + "pl_inv_discr = " + "'" + tbinvdescrepancyacct.getText().toString() + "'" + ","
                            + "pl_scrap = " + "'" + tbscrapacct.getText().toString() + "'" + ","
                            + "pl_wip = " + "'" + tbwipacct.getText().toString() + "'" + ","
                            + "pl_wip_var = " + "'" + tbwipvaracct.getText().toString() + "'" + ","
                            + "pl_inv_change = " + "'" + tbinvchangeacct.getText().toString() + "'" + ","
                            + "pl_sales = " + "'" + tbsalesacct.getText().toString() + "'" + ","
                            + "pl_sales_disc = " + "'" + tbsalesdiscacct.getText().toString() + "'" + ","
                            + "pl_cogs_mtl = " + "'" + tbcogsmtlacct.getText().toString() + "'" + ","
                            + "pl_cogs_lbr = " + "'" + tbcogslbracct.getText().toString() + "'" + ","
                            + "pl_cogs_bdn = " + "'" + tbcogsbdnacct.getText().toString() + "'" + ","
                            + "pl_cogs_ovh = " + "'" + tbcogsovhacct.getText().toString() + "'" + ","
                            + "pl_cogs_out = " + "'" + tbcogsoutacct.getText().toString() + "'" + ","
                            + "pl_purchases = " + "'" + tbpurchacct.getText().toString() + "'" + ","
                            + "pl_po_rcpt = " + "'" + tbporcptacct.getText().toString() + "'" + ","
                            + "pl_po_ovh = " + "'" + tbpoovhacct.getText().toString() + "'" + ","
                            + "pl_po_pricevar = " + "'" + tbpopricevaracct.getText().toString() + "'" + ","
                            + "pl_ap_usage = " + "'" + tbapusageacct.getText().toString() + "'" + ","
                            + "pl_ap_ratevar = " + "'" + tbapratevaracct.getText().toString() + "'" + ","
                            + "pl_job_stock = " + "'" + tbjobstockacct.getText().toString() + "'" + ","
                            + "pl_mtl_usagevar = " + "'" + tbmtlusagevaracct.getText().toString() + "'" + ","
                            + "pl_mtl_ratevar = " + "'" + tbmtlratevaracct.getText().toString() + "'" + ","
                            + "pl_mix_var = " + "'" + tbmixedvaracct.getText().toString() + "'" + ","
                            + "pl_cop = " + "'" + tbcopacct.getText().toString() + "'" + ","
                            + "pl_out_usagevar = " + "'" + tboutusgvaracct.getText().toString() + "'" + ","
                            + "pl_out_ratevar = " + "'" + tboutratevaracct.getText().toString() + "'"
                            + " where pl_line = " + "'" + tbnbr.getText().toString() + "'"
                            + ";");

                        bsmf.MainFrame.show("Updated Prod Code Record");
                        initvars("");
                    }

                } 
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Edit Prod Code");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "prodcodemaint,pl_line");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        tbnbr.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from pl_mstr where pl_line = " + "'" + tbnbr.getText() + "'" +  ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted Product Code " + tbnbr.getText());
                    initvars("");
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete ProductCode Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
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
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbapratevaracct;
    private javax.swing.JTextField tbapusageacct;
    private javax.swing.JTextField tbcogsbdnacct;
    private javax.swing.JTextField tbcogslbracct;
    private javax.swing.JTextField tbcogsmtlacct;
    private javax.swing.JTextField tbcogsoutacct;
    private javax.swing.JTextField tbcogsovhacct;
    private javax.swing.JTextField tbcopacct;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbinvacct;
    private javax.swing.JTextField tbinvchangeacct;
    private javax.swing.JTextField tbinvdescrepancyacct;
    private javax.swing.JTextField tbjobstockacct;
    private javax.swing.JTextField tbmixedvaracct;
    private javax.swing.JTextField tbmtlratevaracct;
    private javax.swing.JTextField tbmtlusagevaracct;
    private javax.swing.JTextField tbnbr;
    private javax.swing.JTextField tboutratevaracct;
    private javax.swing.JTextField tboutusgvaracct;
    private javax.swing.JTextField tbpoovhacct;
    private javax.swing.JTextField tbpopricevaracct;
    private javax.swing.JTextField tbporcptacct;
    private javax.swing.JTextField tbpurchacct;
    private javax.swing.JTextField tbsalesacct;
    private javax.swing.JTextField tbsalesdiscacct;
    private javax.swing.JTextField tbscrapacct;
    private javax.swing.JTextField tbtbd;
    private javax.swing.JTextField tbwipacct;
    private javax.swing.JTextField tbwipvaracct;
    // End of variables declaration//GEN-END:variables
}
