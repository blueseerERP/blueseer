/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     
    public void initvars(String arg) {
       // TableReport.setModel(new javax.swing.table.DefaultTableModel());
        
         javax.swing.table.DefaultTableModel mymodel = null;
        java.util.Date now = new java.util.Date();
       
        
         
         
        if (arg.equals("ReqPendingApproval")) {
             mymodel = OVData.getReqByApprover(bsmf.MainFrame.userid);
             TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg.equals("ReqBrowseAll")) {
             mymodel = OVData.getReqAll();
              TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg.equals("ReqPendRpt1")) {
             mymodel = OVData.getReqPending();
              TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg.equals("SchemaBrowse")) {
             mymodel = OVData.getDBSchema();
        }
        if (arg.equals("UserBrowse")) {
             mymodel = OVData.getUserAll();
        }
        if (arg.equals("ProdCodeBrowse")) {
             mymodel = OVData.getProdCodeAll();
        }
        if (arg.equals("QPRBrowse")) {
             mymodel = OVData.getQPRAll();
        }
        if (arg.equals("ShipperBrowse")) {
             mymodel = OVData.getShipperAll();
        }
        if (arg.equals("OpenOrdReport")) {
             mymodel = OVData.getOrderOpen();
        }
        if (arg.equals("ReqApprovedBrowse")) {
             mymodel = OVData.getReqApproved();
              TableReport.setModel(mymodel);
             TableReport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg.equals("PlantDirectoryMenu")) {
             mymodel = OVData.getPlantDirectory();
        }
        if (arg.equals("NavCodeBrowse")) {
             mymodel = OVData.getNavCodeList();
        }
        
        if (arg.equals("AcctBrowse")) {
             mymodel = OVData.getGLAcctAll();
        }
        if (arg.equals("ItemRoutingRpt")) {
             mymodel = OVData.getItemRoutingAll();
        }
       
        if (arg.equals("EmpBrowse")) {
             mymodel = OVData.getEmployeeAll();
        }
        if (arg.equals("GenCodeBrowse")) {
             mymodel = OVData.getGenCodeAll();
        }
        if (arg.equals("WorkCellBrowse")) {
             mymodel = OVData.getWorkCellAll();
        }
        if (arg.equals("RoutingBrowse")) {
             mymodel = OVData.getRoutingsAll();
        }
        if (arg.equals("LocationBrowse")) {
             mymodel = OVData.getLocationsAll();
        }
        if (arg.equals("WareHouseBrowse")) {
             mymodel = OVData.getWareHousesAll();
        }
        if (arg.equals("DeptBrowse")) {
             mymodel = OVData.getDeptsAll();
        }
        if (arg.equals("CustReport1")) {
             mymodel = OVData.getCustAddrInfoAll();
        }
         if (arg.equals("MenuBrowse")) {
             mymodel = OVData.getMenusAll();
        }
        if (arg.equals("PanelBrowse")) {
             mymodel = OVData.getPanelsAll();
        }
        if (arg.equals("TermsBrowse")) {
             mymodel = OVData.getTermsAll();
        }
        if (arg.equals("FreightBrowse")) {
             mymodel = OVData.getFreightAll();
        }
        if (arg.equals("CarrierBrowse")) {
             mymodel = OVData.getCarrierAll();
        }
        if (arg.equals("TaxBrowse")) {
             mymodel = OVData.getTaxAll();
        }
        if (arg.equals("EDITPMaintBrowse")) {
             mymodel = OVData.getEDITPAll();
        }
        if (arg.equals("noStdCostBrowse")) {
             mymodel = OVData.getNoStdCostItems();
        }
        if (arg.equals("BankBrowse")) {
             mymodel = OVData.getBankAll();
        }
        if (arg.equals("UnPostedTransRpt")) {
             mymodel = OVData.getUnPostedGLTrans();
              TableReport.setModel(mymodel);
              TableReport.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        }
        if (arg.equals("CalendarBrowse")) {
             mymodel = OVData.getGLCalendar();
        }
         if (arg.equals("SiteBrowse")) {
             mymodel = OVData.getSitesAll();
        }
         if (arg.equals("ForecastBrowse")) {
             mymodel = OVData.getForecast13weeks(OVData.getForecastWeek(now));
        }
        if (arg.equals("PrinterBrowse")) {
             mymodel = OVData.getPrintersAll();
        } 
        if (arg.equals("CustEDIBrowse")) {
             mymodel = OVData.getCustEDIAll();
        } 
        if (arg.equals("LabelFileBrowse")) {
             mymodel = OVData.getLabelFileAll();
        } 
         if (arg.equals("ShiftBrowse")) {
             mymodel = OVData.getShiftAll();
        } 
          if (arg.equals("ClockCodeBrowse")) {
             mymodel = OVData.getClockCodesAll();
        } 
          if (arg.equals("ClockCode66Browse")) {
             mymodel = OVData.getClockRecords66All();
        } 
         if (arg.equals("ARPaymentBrowse")) {
             mymodel = OVData.getARPaymentBrowse();
        }  
         
        if (mymodel != null) { 
            TableReport.setModel(mymodel);
            TableReport.setName(arg);
            if (TableReport.getColumnModel().getColumn(0).getIdentifier().equals("select")) {
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

        jScrollPaneReport.setBackground(new java.awt.Color(0, 102, 204));
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
        String args = "";
        String mypanel = "";
        if (col == 0 && TableReport.getColumnModel().getColumn(0).getIdentifier().equals("select")) {
             // if (! checkperms("MenuReqMaint")) { return; }
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqBrowseAll") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqPendingApproval") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqPendRpt1") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }   
            if (TableReport.getName() != null && TableReport.getName().compareTo("ReqApproveBrowse") == 0) {
                mypanel = "ReqMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("AcctBrowse") == 0) {
                mypanel = "AcctMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("EmpBrowse") == 0) {
                 mypanel = "EmployeeMaster";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }  
            if (TableReport.getName() != null && TableReport.getName().compareTo("WorkCellBrowse") == 0) {
                mypanel = "WorkCellMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString();
               reinitpanels(mypanel, true, args);
            }  
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("RoutingBrowse") == 0) {
                mypanel = "RoutingMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString();
               reinitpanels(mypanel, true, args);
            }  
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("CalendarBrowse") == 0) {
                 mypanel = "GLCalMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString();
               reinitpanels(mypanel, true, args);
            }
            
            if (TableReport.getName() != null && TableReport.getName().compareTo("LocationBrowse") == 0) {
                mypanel = "LocationMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("WareHouseBrowse") == 0) {
                mypanel = "WareHouseMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ProdCodeBrowse") == 0) {
                mypanel = "ProdCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("DeptBrowse") == 0) {
                 mypanel = "DeptMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("MenuBrowse") == 0) {
                mypanel = "MenuMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("PanelBrowse") == 0) {
                 mypanel = "PanelMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("CustReport1") == 0) {
                 mypanel = "CustMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("TermsBrowse") == 0) {
                mypanel = "TermsMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("FreightBrowse") == 0) {
               mypanel = "FreightMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("CarrierBrowse") == 0) {
               mypanel = "CarrierMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("TaxBrowse") == 0) {
               mypanel = "TaxMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("QPRBrowse") == 0) {
               mypanel = "QPRMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("BankBrowse") == 0) {
                 mypanel = "BankMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("SiteBrowse") == 0) {
               mypanel = "SiteMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("PrinterBrowse") == 0) {
               mypanel = "PrinterMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("EDITPMaintBrowse") == 0) {
                 mypanel = "EDITPMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("LabelFileBrowse") == 0) {
                 mypanel = "LabelFileMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("GenCodeBrowse") == 0) {
                 mypanel = "GenericCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString() ;
               reinitpanels(mypanel, true, args);
            }
            if (TableReport.getName() != null && TableReport.getName().compareTo("ForecastBrowse") == 0) {
                 mypanel = "ForecastMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString() + "," + TableReport.getValueAt(row, 3).toString() ;
               reinitpanels(mypanel, true, args);
            } 
            if (TableReport.getName() != null && TableReport.getName().compareTo("CustEDIBrowse") == 0) {
                 mypanel = "CustEDIMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString() + "," + TableReport.getValueAt(row, 2).toString() + "," + TableReport.getValueAt(row, 3).toString() ;
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ShiftBrowse") == 0) {
                 mypanel = "ShiftMaintenance";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ClockCodeBrowse") == 0) {
                mypanel = "ClockCodeMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
             if (TableReport.getName() != null && TableReport.getName().compareTo("ClockCode66Browse") == 0) {
                mypanel = "ClockApprovalMaint";
                if (! checkperms(mypanel)) { return; }
               args = TableReport.getValueAt(row, 1).toString();
               reinitpanels(mypanel, true, args);
            }
            
        }
    }//GEN-LAST:event_TableReportMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable TableReport;
    private javax.swing.JScrollPane jScrollPaneReport;
    // End of variables declaration//GEN-END:variables
}
