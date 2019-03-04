/*
   Copyright 2005-2017 Terry Evans Vaughn ("VCSCode").

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

For all third party components incorporated into the GitLab Software, those 
components are licensed under the original license provided by the owner of the 
applicable component.

 */

package com.blueseer.utl;

import com.blueseer.utl.OVData;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.loadPanel;
import static bsmf.MainFrame.main;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author vaughnte
 */
public class Browse extends javax.swing.JPanel {
    
    String callingpanel = "";
    String searchfield = "";
    String tievar = "";

    /**
     * Creates new form CustXrefRpt1
     */
    public Browse() {
        initComponents();
    }

    
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
    
    
      public void initvars(String arg) {
          
       rbbegin.setSelected(true);
       rbend.setSelected(false);
       rbmatch.setSelected(false);
       buttonGroup1.add(rbbegin);
       buttonGroup1.add(rbend);
       buttonGroup1.add(rbmatch);
          
        
        tbtext.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.red);
        DefaultTableModel dtm = (DefaultTableModel) tablereport.getModel();
        dtm.setRowCount(0);
        
        String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            callingpanel = args[0];
            searchfield = args[1];
            if (args.length > 2) {
                tievar = args[2];
            }
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tbtext = new javax.swing.JTextField();
        btview = new javax.swing.JButton();
        lbmessage = new javax.swing.JLabel();
        rbbegin = new javax.swing.JRadioButton();
        rbend = new javax.swing.JRadioButton();
        rbmatch = new javax.swing.JRadioButton();
        btcsv = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();

        jLabel2.setText("Text Search:");

        tbtext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtextActionPerformed(evt);
            }
        });

        btview.setText("Find It");
        btview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btviewActionPerformed(evt);
            }
        });

        rbbegin.setText("BeginsWith");

        rbend.setText("EndsWith");

        rbmatch.setText("Match");

        btcsv.setText("CSV");
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbtext, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbbegin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbmatch)
                .addGap(28, 28, 28)
                .addComponent(btcsv)
                .addContainerGap(203, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btview)
                    .addComponent(rbbegin)
                    .addComponent(rbend)
                    .addComponent(rbmatch)
                    .addComponent(btcsv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        jPanel2.add(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btviewActionPerformed
                lbmessage.setText("");
                int state = 0;  // default 'match'
                if (rbbegin.isSelected()) {
                    state = 1; // begins
                }
                else if (rbend.isSelected()) {
                    state = 2; // ends
                }
                else {
                    state = 0; // match
                }
                if (callingpanel.equals("glmaint")) {
                tablereport.setModel(OVData.getGLTranBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("bommaint")) {
                tablereport.setModel(OVData.getItemBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("costmaint")) {
                tablereport.setModel(OVData.getItemBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("editpmaint")) {
                tablereport.setModel(OVData.getEDITPBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("edicustmaint")) {
                tablereport.setModel(OVData.getEDICustBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("editpdocmaint")) {
                tablereport.setModel(OVData.getEDITPDOCBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("taskmaint")) {
                tablereport.setModel(OVData.getTaskBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("taxmaint")) {
                tablereport.setModel(OVData.getTaxBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("ecnmaint")) {
                tablereport.setModel(OVData.getECNBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("uommaint")) {
                tablereport.setModel(OVData.getUOMBrowseUtil(tbtext.getText(), state, searchfield)); 
                }
                if (callingpanel.equals("curmaint")) {
                tablereport.setModel(OVData.getCurrencyBrowseUtil(tbtext.getText(), state, searchfield));  
                }
                if (callingpanel.equals("prodcodemaint")) {
                tablereport.setModel(OVData.getProdCodeBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("locationmaint")) {
                tablereport.setModel(OVData.getLocationBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("ftpmaint")) {
                tablereport.setModel(OVData.getFTPBrowseUtil(tbtext.getText(), state, searchfield)); 
                }
                if (callingpanel.equals("warehousemaint")) {
                tablereport.setModel(OVData.getWareHouseBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("deptccmaint")) {
                tablereport.setModel(OVData.getDeptCCBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("recvmaint")) {
                tablereport.setModel(OVData.getReceiverBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("vouchmaint")) {
                tablereport.setModel(OVData.getVoucherBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("bankmaint")) {
                tablereport.setModel(OVData.getBankBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("calendarmaint")) {
                tablereport.setModel(OVData.getCalendarBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("termmaint")) {
                tablereport.setModel(OVData.getTermBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("carriermaint")) {
                tablereport.setModel(OVData.getCarrierBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("empmaint")) {
                tablereport.setModel(OVData.getEmpBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("clockrecupdate")) {
                tablereport.setModel(OVData.getClockRecBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("acctmaint")) {
                tablereport.setModel(OVData.getAcctBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("usermaint")) {
                tablereport.setModel(OVData.getUserBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("itemmaint")) {
                tablereport.setModel(OVData.getItemBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("panelmaint")) {
                tablereport.setModel(OVData.getPanelBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("sitemaint")) {
                tablereport.setModel(OVData.getSiteBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("menumaint")) {
                tablereport.setModel(OVData.getMenuBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("ordermaint")) {
                tablereport.setModel(OVData.getOrderBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("pomaint")) {
                tablereport.setModel(OVData.getPOBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("domaint")) {
                tablereport.setModel(OVData.getDOBrowseUtil(tbtext.getText(), state, searchfield)); 
                }
                if (callingpanel.equals("fomaint")) {
                tablereport.setModel(OVData.getFOBrowseUtil(tbtext.getText(), state, searchfield));  
                }
                if (callingpanel.equals("shipmaint")) {
                tablereport.setModel(OVData.getShipperBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("vendmaint")) {
                tablereport.setModel(OVData.getVendBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("custmaint")) {
                tablereport.setModel(OVData.getCustBrowseUtil(tbtext.getText(), state, searchfield));
                }
                 if (callingpanel.equals("shiftmaint")) {
                tablereport.setModel(OVData.getShiftBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("shiptomaint")) {
                tablereport.setModel(OVData.getShipToBrowseUtil(tbtext.getText(), state, searchfield, tievar));
                }
                if (callingpanel.equals("routingmaint")) {
                tablereport.setModel(OVData.getRoutingBrowseUtil(tbtext.getText(), state, searchfield));
                }
                if (callingpanel.equals("wcmaint")) {
                tablereport.setModel(OVData.getWorkCenterBrowseUtil(tbtext.getText(), state, searchfield));
                }
                 //tablereport.getColumnModel().getColumn(0).setCellRenderer(new ItemMastSearch.SomeRenderer());       
                // tablereport.getColumn("select").setCellRenderer(new Browse.ButtonRenderer());
                tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
                if (tablereport.getModel().getRowCount() == 0) {
                    lbmessage.setText("No records found.");
                }
      
    }//GEN-LAST:event_btviewActionPerformed

    private void tbtextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbtextActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
         int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        String myparameter = "";
        if ( col == 0) {
            if (callingpanel.equals("glmaint")) {
              if (! checkperms("GLTranMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 2).toString();
              reinitpanels("GLTranMaint", true, myparameter);
            }
            if (callingpanel.equals("costmaint")) {
              if (! checkperms("CostRoll")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("CostRoll", true, myparameter);
            }
            if (callingpanel.equals("bommaint")) {
              if (! checkperms("BOMMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("BOMMaint", true, myparameter);
            }
            if (callingpanel.equals("editpmaint")) {
              if (! checkperms("EDITPMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("EDITPMaint", true, myparameter);
            }
            if (callingpanel.equals("editpdocmaint")) {
              if (! checkperms("EDITPDocMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() + "," + tablereport.getValueAt(row, 2).toString();
              reinitpanels("EDITPDocMaint", true, myparameter);
            }
            if (callingpanel.equals("edicustmaint")) {
              if (! checkperms("CustEDIMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() + "," + tablereport.getValueAt(row, 2).toString() + "," + tablereport.getValueAt(row, 3).toString();
              reinitpanels("CustEDIMaint", true, myparameter);
            }
            if (callingpanel.equals("uommaint")) {
              if (! checkperms("UOMMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("UOMMaint", true, myparameter);
            }
            if (callingpanel.equals("curmaint")) {
              if (! checkperms("CurrencyMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("CurrencyMaint", true, myparameter);
            }
             if (callingpanel.equals("domaint")) {
              if (! checkperms("DOMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("DOMaint", true, myparameter);
            }
              if (callingpanel.equals("fomaint")) {
              if (! checkperms("FOMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("FOMaint", true, myparameter);
            }
            if (callingpanel.equals("taskmaint")) {
              if (! checkperms("TaskMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("TaskMaint", true, myparameter);
            }
            if (callingpanel.equals("taxmaint")) {
              if (! checkperms("TaxMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("TaxMaint", true, myparameter);
            }
            if (callingpanel.equals("ecnmaint")) {
              if (! checkperms("ECNMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("ECNMaint", true, myparameter);
            }
            if (callingpanel.equals("sitemaint")) {
              if (! checkperms("SiteMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("SiteMaint", true, myparameter);
            }
            if (callingpanel.equals("prodcodemaint")) {
              if (! checkperms("ProdCodeMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("ProdCodeMaint", true, myparameter);
            }
            if (callingpanel.equals("recvmaint")) {
              if (! checkperms("ReceiverMaintMenu")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("ReceiverMaintMenu", true, myparameter);
            }
            if (callingpanel.equals("vouchmaint")) {
              if (! checkperms("VouchMaintPanel")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("VouchMaintPanel", true, myparameter);
            }
            if (callingpanel.equals("bankmaint")) {
              if (! checkperms("BankMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("BankMaint", true, myparameter);
            }
            if (callingpanel.equals("calendarmaint")) {
              if (! checkperms("GLCalMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() + "," + tablereport.getValueAt(row, 2).toString();
              reinitpanels("GLCalMaint", true, myparameter);
            }
            if (callingpanel.equals("deptccmaint")) {
              if (! checkperms("DeptMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("DeptMaint", true, myparameter);
            }
            if (callingpanel.equals("shiftmaint")) {
              if (! checkperms("ShiftMaintenance")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("ShiftMaintenance", true, myparameter);
            }
            if (callingpanel.equals("locationmaint")) {
              if (! checkperms("LocationMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("LocationMaint", true, myparameter);
            }
            if (callingpanel.equals("ftpmaint")) {
              if (! checkperms("FTPMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("FTPMaint", true, myparameter);
            }
             if (callingpanel.equals("warehousemaint")) {
              if (! checkperms("WareHouseMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("WareHouseMaint", true, myparameter);
            }
            if (callingpanel.equals("itemmaint")) {
              if (! checkperms("ItemMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("ItemMaint", true, myparameter);
            }
             if (callingpanel.equals("panelmaint")) {
              if (! checkperms("PanelMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString();
              reinitpanels("PanelMaint", true, myparameter);
            }
            if (callingpanel.equals("termmaint")) {
              if (! checkperms("TermsMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("TermsMaint", true, myparameter);
            }
            if (callingpanel.equals("carriermaint")) {
              if (! checkperms("CarrierMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("CarrierMaint", true, myparameter);
            }
            if (callingpanel.equals("empmaint")) {
              if (! checkperms("EmployeeMaster")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("EmployeeMaster", true, myparameter);
            }
             if (callingpanel.equals("clockrecupdate")) {
              if (! checkperms("TimeClockAdjust")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("TimeClockAdjust", true, myparameter);
            }
             if (callingpanel.equals("acctmaint")) {
              if (! checkperms("AcctMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("AcctMaint", true, myparameter);
            }
            if (callingpanel.equals("usermaint")) {
              if (! checkperms("UserMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("UserMaint", true, myparameter);
            }
             if (callingpanel.equals("menumaint")) {
              if (! checkperms("MenuMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("MenuMaint", true, myparameter);
            }
               if (callingpanel.equals("vendmaint")) {
              if (! checkperms("VendMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("VendMaint", true, myparameter);
            }
              if (callingpanel.equals("custmaint")) {
              if (! checkperms("CustMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("CustMaint", true, myparameter);
            }
                if (callingpanel.equals("ordermaint")) {
              if (! checkperms("OrderMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("OrderMaint", true, myparameter);
            }
                     if (callingpanel.equals("pomaint")) {
              if (! checkperms("POMaintMenu")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("POMaintMenu", true, myparameter);
            }
                    if (callingpanel.equals("shipmaint")) {
              if (! checkperms("ShipMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("ShipMaint", true, myparameter);
            }
               if (callingpanel.equals("shiptomaint")) {
              if (! checkperms("CustMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 2).toString() + "," + tablereport.getValueAt(row, 1).toString() ;
              reinitpanels("CustMaint", true, myparameter);
            }
                  if (callingpanel.equals("routingmaint")) {
              if (! checkperms("RoutingMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() + "," + tablereport.getValueAt(row, 2).toString() ;
              reinitpanels("RoutingMaint", true, myparameter);
            }
                        if (callingpanel.equals("wcmaint")) {
              if (! checkperms("WorkCellMaint")) { return; }
              myparameter = tablereport.getValueAt(row, 1).toString() + "," + tablereport.getValueAt(row, 2).toString() ;
              reinitpanels("WorkCellMaint", true, myparameter);
            }
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_btcsvActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btview;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JRadioButton rbbegin;
    private javax.swing.JRadioButton rbend;
    private javax.swing.JRadioButton rbmatch;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbtext;
    // End of variables declaration//GEN-END:variables
}
