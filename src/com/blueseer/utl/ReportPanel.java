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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.loadPanel;
import static bsmf.MainFrame.main;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.reinitpanels;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import javax.swing.JPanel;
import java.awt.Container;



/**
 *
 * @author vaughnte
 */
public class ReportPanel extends javax.swing.JPanel {
public int mypage = 0;
public int[] mywidth;

    /**
     * Creates new form ReportPanel
     */
    public ReportPanel() {
        initComponents();
    }

     class BlueRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
           // if (table.getValueAt(row, 0).toString().compareTo("1923") == 0)   
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
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
     
    public void initvars(String[] arg) {
       // TableReport.setModel(new javax.swing.table.DefaultTableModel());
        
         javax.swing.table.DefaultTableModel mymodel = null;
        java.util.Date now = new java.util.Date();
       
        if (mymodel != null) {
        mymodel.setRowCount(0);
        }
        
        if (arg == null || arg.length < 1) {
            return;
        } 
         
        if (arg[0].equals("ReqPendingApproval")) {
             mymodel = DTData.getReqByApprover(bsmf.MainFrame.userid);
             TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
         if (arg[0].equals("ReqBrowseAll")) {
             mymodel = DTData.getReqAll();
             TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
       
        if (arg[0].equals("ReqPendRpt1")) {
             mymodel = DTData.getReqPending();
              TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg[0].equals("SchemaBrowse")) {
             mymodel = DTData.getDBSchema();
        }
        if (arg[0].equals("UserBrowse")) {
             mymodel = DTData.getUserAll();
        }
        if (arg[0].equals("ProdCodeBrowse")) {
             mymodel = DTData.getProdCodeAll();
        }
        if (arg[0].equals("QPRBrowse")) {
             mymodel = DTData.getQPRAll();
        }
        if (arg[0].equals("ShipperBrowse")) {
             mymodel = DTData.getShipperAll();
        }
        if (arg[0].equals("OpenOrdReport")) {
             mymodel = DTData.getOrderOpen();
        }
        if (arg[0].equals("ReqApprovedBrowse")) {
             mymodel = DTData.getReqApproved();
              TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg[0].equals("PlantDirectoryMenu")) {
             mymodel = DTData.getPlantDirectory();
        }
        if (arg[0].equals("NavCodeBrowse")) {
             mymodel = DTData.getNavCodeList();
        }
        
        if (arg[0].equals("AcctBrowse")) {
             mymodel = DTData.getGLAcctAll();
        }
        if (arg[0].equals("ItemBrowse")) {
             mymodel = DTData.getItemBrowse(); 
             TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
             TableReport.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg[0].equals("ItemRoutingRpt")) {
             mymodel = DTData.getItemRoutingAll();
        }
       
        if (arg[0].equals("EmpBrowse")) {
             mymodel = DTData.getEmployeeAll();
        }
        if (arg[0].equals("GenCodeBrowse")) {
             mymodel = DTData.getGenCodeAll();
        }
        if (arg[0].equals("WorkCellBrowse")) {
             mymodel = DTData.getWorkCellAll();
        }
        if (arg[0].equals("RoutingBrowse")) {
             mymodel = DTData.getRoutingsAll();
        }
        if (arg[0].equals("LocationBrowse")) {
             mymodel = DTData.getLocationsAll();
        }
        if (arg[0].equals("WareHouseBrowse")) {
             mymodel = DTData.getWareHousesAll();
        }
        if (arg[0].equals("DeptBrowse")) {
             mymodel = DTData.getDeptsAll();
        }
        if (arg[0].equals("CustReport1")) {
             mymodel = DTData.getCustAddrInfoAll();
        }
        if (arg[0].equals("VendBrowse")) {
             mymodel = DTData.getVendorAll();
        }
         if (arg[0].equals("MenuBrowse")) {
             mymodel = DTData.getMenusAll();
        }
        if (arg[0].equals("PanelBrowse")) {
             mymodel = DTData.getPanelsAll();
        }
        if (arg[0].equals("TermsBrowse")) {
             mymodel = DTData.getTermsAll();
        }
        if (arg[0].equals("AS2Browse")) {
             mymodel = DTData.getAS2All();
        }
        if (arg[0].equals("WorkFlowBrowse")) {
             mymodel = DTData.getWorkFlowAll();
        }
        if (arg[0].equals("CronBrowse")) {
             mymodel = DTData.getCronAll();
        }
        if (arg[0].equals("PKSBrowse")) {
             mymodel = DTData.getPKSAll();
        }
        if (arg[0].equals("FreightBrowse")) {
             mymodel = DTData.getFreightAll();
        }
        if (arg[0].equals("VehicleBrowse")) {
             mymodel = DTData.getVehicleAll(); 
        }
        if (arg[0].equals("DriverBrowse")) {
             mymodel = DTData.getDriverAll(); 
        }
        if (arg[0].equals("BrokerBrowse")) {
             mymodel = DTData.getBrokerAll(); 
        }
        if (arg[0].equals("CarrierBrowse")) {
             mymodel = DTData.getCarrierAll();
        }
        if (arg[0].equals("TaxBrowse")) {
             mymodel = DTData.getTaxAll();
        }
         if (arg[0].equals("PayProfileBrowse")) {
             mymodel = DTData.getPayProfileAll(); 
        }
        if (arg[0].equals("EDIPartnerBrowse")) {
             mymodel = DTData.getEDITPAll();
        }
        if (arg[0].equals("EDITPDocBrowse")) {
             mymodel = DTData.getEDITPDOCAll();
        }
        if (arg[0].equals("noStdCostBrowse")) {
             mymodel = DTData.getNoStdCostItems();
        }
        if (arg[0].equals("BankBrowse")) {
             mymodel = DTData.getBankAll();
        }
        if (arg[0].equals("UnPostedTransRpt")) {
             mymodel = DTData.getUnPostedGLTrans();
              TableReport.setModel(mymodel);
              TableReport.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg[0].equals("CalendarBrowse")) {
             mymodel = DTData.getGLCalendar();
        }
         if (arg[0].equals("SiteBrowse")) {
             mymodel = DTData.getSitesAll();
        }
         if (arg[0].equals("ForecastBrowse")) {
            // mymodel = DTData.getForecast13weeks(OVData.getForecastWeek(now));
            mymodel = DTData.getForecastAll();
        }
        if (arg[0].equals("PrinterBrowse")) {
             mymodel = DTData.getPrintersAll();
        } 
        if (arg[0].equals("EDIPartnerDocBrowse")) {
             mymodel = DTData.getEDIPartnerDocAll();
        } 
        if (arg[0].equals("LabelFileBrowse")) {
             mymodel = DTData.getLabelFileAll();
        } 
         if (arg[0].equals("ShiftBrowse")) {
             mymodel = DTData.getShiftAll();
        } 
          if (arg[0].equals("ClockCodeBrowse")) {
             mymodel = DTData.getClockCodesAll();
        } 
          if (arg[0].equals("ClockCode66Browse")) {
             mymodel = DTData.getClockRecords66All();
        } 
         if (arg[0].equals("ARPaymentBrowse")) {
             mymodel = DTData.getARPaymentBrowse();
        }  
         
        if (mymodel != null) { 
            TableReport.setModel(mymodel);
            TableReport.setName(arg[0]);
            if (TableReport.getColumnModel().getColumn(0).getIdentifier().equals(getGlobalColumnTag("select"))) {
                TableReport.getColumnModel().getColumn(0).setMaxWidth(100);
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

        jScrollPaneReport = new javax.swing.JScrollPane();
        TableReport = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));
        setLayout(new java.awt.BorderLayout());

        jScrollPaneReport.setBorder(null);
        jScrollPaneReport.setPreferredSize(new java.awt.Dimension(500, 600));

        TableReport.setAutoCreateRowSorter(true);
        TableReport.setModel(new javax.swing.table.DefaultTableModel(
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
        TableReport.setAutoscrolls(false);
        TableReport.setPreferredSize(null);
        TableReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TableReportMouseClicked(evt);
            }
        });
        jScrollPaneReport.setViewportView(TableReport);

        add(jScrollPaneReport, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void TableReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableReportMouseClicked
      
        int row = TableReport.rowAtPoint(evt.getPoint());
        int col = TableReport.columnAtPoint(evt.getPoint());
        String[] args = null;
        String mypanel = "";
        if (col == 0 && TableReport.getColumnModel().getColumn(0).getIdentifier().equals(getGlobalColumnTag("select"))) {
             // if (! checkperms("MenuReqMaint")) { return; }
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqBrowseAll") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqPendingApproval") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqPendRpt1") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }   
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqApproveBrowse") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("AcctBrowse") == 0) {
                mypanel = "AcctMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("ItemBrowse") == 0) {
                mypanel = "ItemMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("UserBrowse") == 0) {
                mypanel = "UserMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("EmpBrowse") == 0) {
                 mypanel = "EmployeeMaster";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("WorkCellBrowse") == 0) {
                mypanel = "WorkCellMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
            }  
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("RoutingBrowse") == 0) {
                mypanel = "RoutingMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
            }  
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("CalendarBrowse") == 0) {
                 mypanel = "GLCalMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
            }
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("LocationBrowse") == 0) {
                mypanel = "LocationMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("WareHouseBrowse") == 0) {
                mypanel = "WareHouseMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ProdCodeBrowse") == 0) {
                mypanel = "ProdCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("DeptBrowse") == 0) {
                 mypanel = "DeptMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("MenuBrowse") == 0) {
                mypanel = "MenuMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("PanelBrowse") == 0) {
                 mypanel = "PanelMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("CustReport1") == 0) {
                 mypanel = "CustMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("VendBrowse") == 0) {
                 mypanel = "VendMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("TermsBrowse") == 0) {
                mypanel = "TermsMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("AS2Browse") == 0) {
                mypanel = "AS2Maint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("WorkFlowBrowse") == 0) {
                mypanel = "WorkFlowMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("CronBrowse") == 0) {
                mypanel = "CronMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("PKSBrowse") == 0) {
                mypanel = "PKSMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("FreightBrowse") == 0) {
               mypanel = "FreightMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("CarrierBrowse") == 0) {
               mypanel = "CarrierMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("VehicleBrowse") == 0) {
               mypanel = "VehicleMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("DriverBrowse") == 0) {
               mypanel = "DriverMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("BrokerBrowse") == 0) {
               mypanel = "BrokerMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("TaxBrowse") == 0) {
               mypanel = "TaxMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("PayProfileBrowse") == 0) {
               mypanel = "PayProfileMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("QPRBrowse") == 0) {
               mypanel = "QPRMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("BankBrowse") == 0) {
                 mypanel = "BankMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("SiteBrowse") == 0) {
               mypanel = "SiteMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("PrinterBrowse") == 0) {
               mypanel = "PrinterMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("EDIPartnerBrowse") == 0) {
                 mypanel = "EDIPartnerMaint";
                if (! checkperms(mypanel)) { return; }
              args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("EDITPDocBrowse") == 0) {
                 mypanel = "EDITPDocMaint";
                if (! checkperms(mypanel)) { return; }
              args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("LabelFileBrowse") == 0) {
                 mypanel = "LabelFileMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("GenCodeBrowse") == 0) {
                 mypanel = "GenericCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ForecastBrowse") == 0) {
                 mypanel = "ForecastMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString(),TableReport.getValueAt(row, 3).toString()};
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("EDIPartnerDocBrowse") == 0) {
                 mypanel = "EDIPartnerDocMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString(),TableReport.getValueAt(row, 2).toString(),TableReport.getValueAt(row, 7).toString(), TableReport.getValueAt(row, 8).toString()};
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ShiftBrowse") == 0) {
                 mypanel = "ShiftMaintenance";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ClockCodeBrowse") == 0) {
                mypanel = "ClockCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ClockCode66Browse") == 0) {
                mypanel = "ClockApprovalMaint";
                if (! checkperms(mypanel)) { return; }
              args = new String[]{TableReport.getValueAt(row, 1).toString()};
               reinitpanels(mypanel, true, args);
            }
            
        }
    }//GEN-LAST:event_TableReportMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable TableReport;
    private javax.swing.JScrollPane jScrollPaneReport;
    // End of variables declaration//GEN-END:variables
}
