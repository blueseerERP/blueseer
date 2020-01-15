/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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

package com.blueseer.prd;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author vaughnte
 */
public class ProdSchedPanel extends javax.swing.JPanel {
 
    
       // NOTE:  if you change this...you must also adjust APCheckRun...my advise....dont change it
       ProdSchedPanel.MyTableModel mymodel = new ProdSchedPanel.MyTableModel(new Object[][]{},
                        new String[]{"JobNbr", "Part", "DueDate", "Type", "isSched", "Cell", "QtySched", "QtyRequired", "QtyComp", "SchedDate", "Order", "line", "Status", "Print", "Update", "Void"})
               {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 13 || col == 14 || col == 15  ) {      
                            return ImageIcon.class;  
                        } else if (col == 6 || col == 7 || col == 8) {
                            return Integer.class;
                        } else if (col == 4) {
                            return Boolean.class;    
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }  
                        };
      
      


      
     
      double schtot = 0;
      double reqtot = 0;
      
      
      
      
    /**
     * Creates new form ScrapReportPanel
     */
    
     public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
          }
} 
      
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // plan is closed
            if (mytable.getModel().getValueAt(rowIndex, 12).equals("closed")) {   // 1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            }
            // plan is voided
            if (mytable.getModel().getValueAt(rowIndex, 12).equals("voided")) {   // -1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            }
            return canEdit[columnIndex];
        }
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
        
        public Class getColumnClass(int column) {
            
            
               if (column == 6)       
                return Double.class; 
               else if (column == 4) 
                   return Boolean.class;
            else return String.class;  //other columns accept String values 
            
       /*     
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }  
              
        return Object.class;
*/
               }
       
        
        
   }    
    
     
     
     
     
     
     
     class SomeRenderer extends DefaultTableCellRenderer {
         
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
     
        
        
        /*
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7);  // 7 = status column
        if ("error".equals(status)) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
        } else if ("close".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("backorder".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }   
        */
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(Color.BLACK);
           
        }
        else
        {
              String status = (String) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 12);  // 7 = status column
             
              if (status.equals("closed")) {
              setForeground(Color.blue);
             } else if (status.equals("voided")) {
              setForeground(Color.red);   
             } else {
              setBackground(table.getBackground());
              setForeground(table.getForeground());
              }
        }
        
            boolean issched = (Boolean) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
            if (( column == 5 || column == 6) && ! issched ) {
            c.setBackground(Color.green);
            c.setForeground(Color.BLACK);
            }
            else {
                c.setBackground(table.getBackground());
            }
        
           
            
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
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            if (mytable.getModel().getValueAt(row, 2).toString().compareTo("approved") == 0) {
            setBackground(Color.green);
            //setEnabled(false);
            setText("complete");
            }
            return this;
        }
    }

    
     class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private String columnname;
        private int myrow;
        private int mycol;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            columnname = String.valueOf(column);
            button.setText(label);
            //button.setText("approve");
            
            isPushed = true;
           
            return button;
        }
/*
        public Object getCellEditorValue() {
           
        }
*/
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    
    
/**
 * @version 1.0 11/09/98
 */



     
     
    public ProdSchedPanel() {
        initComponents();
    }

    
    public void printtickets(String fromjob, String tojob ) {
        
       try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "CUT TICKET");
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("fromjob",  fromjob);
                hm.put("tojob", tojob);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/jobticketmulti.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticketmulti.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem printing ticket");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    public void printticket(String jobid, String bustitle) {
        
       try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                String jasperfile = "jobticket.jasper";  // need to make this changeable via site_mstr

                HashMap hm = new HashMap();
                hm.put("BUSINESSTITLE", bustitle);
                hm.put("REPORT_TITLE", jasperfile);
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("myid",  jobid);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticket.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem printing single ticket");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
      public void postcommit() {
        java.util.Date now = new java.util.Date();
         
         Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -7);
         dcfrom.setDate(calfrom.getTime());
         
         
         Calendar calto = Calendar.getInstance();
         calto.add(Calendar.DATE, 14);
         dcto.setDate(calto.getTime());
         
         fromcell.setText("");
         tocell.setText("");
         frompart.setText("");
         topart.setText("");
         
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
          
          
          
             
         
         
    }
    
    public void initvars(String[] arg) {
         java.util.Date now = new java.util.Date();
         
         Calendar calfrom = Calendar.getInstance();
         // from previous Sunday
         calfrom.add(Calendar.DAY_OF_WEEK, -(calfrom.get(Calendar.DAY_OF_WEEK)-1));
         dcfrom.setDate(calfrom.getTime());
         
         
         Calendar calto = calfrom;
         calto.add(Calendar.DATE, 21);
         // to following Saturday
        // while (calto.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
       //  calto.add(Calendar.DATE, 1);
       //  }
         dcto.setDate(calto.getTime());
         
         fromcell.setText("");
         tocell.setText("");
         frompart.setText("");
         topart.setText("");
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
        
          
          
          
             
         
         
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labelqtysched = new javax.swing.JLabel();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        labelqtyreqd = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        frompart = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        btexport = new javax.swing.JButton();
        topart = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        tocell = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        fromcell = new javax.swing.JTextField();
        cbsched = new javax.swing.JCheckBox();
        cbclosed = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        labelqtysched.setBackground(new java.awt.Color(195, 129, 129));
        labelqtysched.setText("0");

        labelcount.setText("0");

        jLabel7.setText("Rows");

        jLabel9.setText("Total Sched Qty");

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        labelqtyreqd.setBackground(new java.awt.Color(195, 129, 129));
        labelqtyreqd.setText("0");

        jLabel11.setText("Total Reqd Qty");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btcommit)
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelqtyreqd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelqtysched, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(labelcount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btcommit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelqtyreqd, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setText("To Cell:");

        jLabel4.setText("To Part:");

        jLabel3.setText("To DueDate");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        btexport.setText("Export");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        jLabel1.setText("From Part:");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("From DueDate");

        jLabel5.setText("From Cell:");

        dcfrom.setDateFormatString("yyyy-MM-dd");

        cbsched.setText("Unscheduled Only?");

        cbclosed.setText("OpenOnly?");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(frompart)
                    .addComponent(topart, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromcell, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tocell, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbclosed)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexport))
                    .addComponent(cbsched))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(frompart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(topart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fromcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tocell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cbsched)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btexport)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
                .addComponent(cbclosed)
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        mytable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mytableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mytableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(mytable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
        schtot = 0;
        reqtot = 0;
       
        
        labelqtysched.setText("0");
        labelqtyreqd.setText("0");
        labelcount.setText("0");
    
try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                double amt = 0;
               
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                int i = 0;
                String fpart = "";
                String tpart = "";
                String fcell = "";
                String tcell = "";
                String status = "";
                
                if (frompart.getText().isEmpty()) {
                    fpart = bsmf.MainFrame.lowchar;
                } else {
                    fpart = frompart.getText();
                }
                 if (topart.getText().isEmpty()) {
                    tpart = bsmf.MainFrame.hichar;
                } else {
                    tpart = topart.getText();
                }
                 if (fromcell.getText().isEmpty()) {
                    fcell = bsmf.MainFrame.lowchar;
                } else {
                    fcell = fromcell.getText();
                }
                 if (tocell.getText().isEmpty()) {
                    tcell = bsmf.MainFrame.hichar;
                } else {
                    tcell = tocell.getText();
                }
               
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
              //  mytable.getColumnModel().getColumn(0).setCellRenderer(new ProdSchedPanel.SomeRenderer());  
              
                  CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
                mytable.getColumnModel().getColumn(4).setCellRenderer(checkBoxRenderer); 
                 
                 Enumeration<TableColumn> en = mytable.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (tc.getIdentifier().toString().equals("isSched") || 
                             tc.getIdentifier().toString().equals("Print") ||
                             tc.getIdentifier().toString().equals("Update") ||
                             tc.getIdentifier().toString().equals("Void")) {
                         continue;
                     }
                     tc.setCellRenderer(new ProdSchedPanel.SomeRenderer());
                 }   
                 
               
                
         //       mytable.getColumn("Update").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
        //        mytable.getColumn("Print").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
        //        mytable.getColumn("Void").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
                
                
                
                DefaultCellEditor singleClick = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(6));
                singleClick.setClickCountToStart(1);
                mytable.setDefaultEditor(mytable.getColumnClass(6), singleClick);
                DefaultCellEditor singleClick2 = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(5));
                singleClick2.setClickCountToStart(1);
                mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick2);
              //  mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick);
                
                
                
            //    mytable.getColumn("Update").setCellEditor(
           //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
           //     mytable.getColumn("Print").setCellEditor(
           //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
          //      mytable.getColumn("Void").setCellEditor(
          //              new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                mytable.getColumn("Update").setMaxWidth(100);
                mytable.getColumn("Print").setMaxWidth(100);
                mytable.getColumn("Void").setMaxWidth(100);
               
            
                mymodel.setRowCount(0);
                
       
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
               
              // tcm.getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
            
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                 
                 if (cbsched.isSelected()) {
                 res = st.executeQuery("SELECT plan_nbr, plan_type, plan_part, plan_qty_req, plan_qty_comp, "
                         + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                        " AND plan_part >= " + "'" + fpart + "'" + 
                        " AND plan_part <= " + "'" + tpart + "'" + 
                         " AND plan_cell >= " + "'" + fcell + "'" + 
                        " AND plan_cell <= " + "'" + tcell + "'" + 
                        " AND plan_is_sched = " + "'0' "  +
                        " order by plan_part, plan_date_due;");    
                 } else {
                     res = st.executeQuery("SELECT plan_nbr, plan_part, plan_type, plan_qty_req, plan_qty_comp, "
                         + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                        " AND plan_part >= " + "'" + fpart + "'" + 
                        " AND plan_part <= " + "'" + tpart + "'" + 
                         " AND plan_cell >= " + "'" + fcell + "'" + 
                        " AND plan_cell <= " + "'" + tcell + "'" + 
                        " order by plan_part, plan_date_due ;");     
                 }
                while (res.next()) {
                    
                    if (cbclosed.isSelected() && res.getInt("plan_status") == 1) {
                        continue;
                    }
                    
                    // plan can be voided by setting to -1
                    if (cbclosed.isSelected() && res.getInt("plan_status") == -1) {
                        continue;
                    }
                    
                    i++;
                    reqtot = reqtot + res.getInt("plan_qty_req");
                    schtot = schtot + res.getInt("plan_qty_sched");
                    if (res.getString("plan_status").equals("0")) { status = "open"; }
                    if (res.getString("plan_status").equals("1")) { status = "closed"; }
                    if (res.getString("plan_status").equals("-1")) { status = "voided"; }
                    
                                 
                    
                    mymodel.addRow(new Object[]{
                                res.getString("plan_nbr"),
                                res.getString("plan_part"),
                                res.getString("plan_date_due"),
                                res.getString("plan_type"),
                                res.getBoolean("plan_is_sched"),
                                res.getString("plan_cell"),
                                res.getInt("plan_qty_sched"),
                                res.getInt("plan_qty_req"),
                                res.getInt("plan_qty_comp"),
                                res.getString("plan_date_sched"),
                                res.getString("plan_order"),
                                res.getString("plan_line"),
                                status,
                                BlueSeerUtils.clickprint,
                                BlueSeerUtils.clickrefresh,
                                BlueSeerUtils.clicktrash
                            });
                }
                labelqtysched.setText(String.valueOf(schtot));
                labelqtyreqd.setText(String.valueOf(reqtot));
                labelcount.setText(String.valueOf(i));
               
                 
                
                
             //    RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mymodel);
            //     mytable.setRowSorter(sorter);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem running view");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
       FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;
        
         int i = 0;
               String fpart = "";
               String tpart = "";
                
                 if (frompart.getText().isEmpty()) {
                    fpart = bsmf.MainFrame.lowchar;
                } else {
                    fpart = frompart.getText();
                }
                 if (topart.getText().isEmpty()) {
                    tpart = bsmf.MainFrame.hichar;
                } else {
                    tpart = topart.getText();
                }
                   
           DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       //"JobNbr", "Part", "DueDate", "isSched?", "Cell", "QtyRequired","QtySched", "QtyComp", "SchedDate", "Order", "line", "Status"
           
           try {
            output = new BufferedWriter(new FileWriter(f));
      
               String myheader = "JobNbr,Part,DueDate,isched,cell,qtyreq,qtysched,qtycomp,scheddate,order,line,status";
                
                 output.write(myheader + '\n');
                 
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

              res = st.executeQuery("SELECT plan_nbr, plan_part, plan_qty_req, plan_qty_comp, "
                         + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                        " AND plan_part >= " + "'" + fpart + "'" + 
                        " AND plan_part <= " + "'" + tpart + "'" + 
                        " AND plan_is_sched = " + "'" + BlueSeerUtils.boolToInt(cbsched.isSelected()) + "'"  +
                        " And  plan_status = " + "'" +  BlueSeerUtils.boolToInt(cbclosed.isSelected()) + "'" +
                        " order by plan_nbr;");    


                while (res.next()) {
                    String newstring = res.getString("plan_nbr") + "," + res.getString("plan_part").replace(",","") + "," + 
                            res.getString("plan_date_due") + "," + res.getString("plan_is_sched") + "," + res.getString("plan_cell") + "," + 
                            res.getString("plan_qty_req") + "," + res.getString("plan_qty_sched") + "," + res.getString("plan_qty_comp") + "," +
                            res.getString("plan_date_sched") + "," + res.getString("plan_order") + "," + res.getString("plan_line") + "," +
                            res.getString("plan_status");
                            
                    output.write(newstring + '\n');
                }
             bsmf.MainFrame.show("file has been created");
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot extract tran_mstr data");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        
        output.close();
        
        
        
        } catch (IOException ex) {
            Logger.getLogger(bsmf.MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btexportActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
             boolean commit = true;
             int count = 0;
             java.util.Date now = new java.util.Date();
             // clean out the unchecked rows
             
             for (int i = 0 ; i < mymodel.getRowCount(); i++) {
                 
                 if (! mymodel.getValueAt(i, 12).equals("open") && ! mymodel.getValueAt(i, 12).equals("closed") && ! mymodel.getValueAt(i, 12).equals("voided")) {
                   bsmf.MainFrame.show("Invalid entry in status field...must be 'closed', 'open', 'voided'");
                   commit = false;
                   break;
                 }
             }
             
             if (commit) {
                 // remove rows that are already scheduled 'true'
             for (int i = 0 ; i < mymodel.getRowCount(); i++) {  
             if ( (boolean) mymodel.getValueAt(i, 4) == true ) {
             //    bsmf.MainFrame.show("yep1:" + mymodel.getValueAt(i, 0).toString());
                 mymodel.removeRow(i);
                 i--;
                 
             }
             }
             
                 // now remove rows that have blank cell and QtySched
             for (int i = 0 ; i < mymodel.getRowCount(); i++) {  
             if ( mymodel.getValueAt(i, 5).toString().isEmpty() || mymodel.getValueAt(i, 5).toString() == null || mymodel.getValueAt(i, 6).toString().isEmpty() || mymodel.getValueAt(i, 6).toString() == null  ) {
            //    bsmf.MainFrame.show("yep2:" + mymodel.getValueAt(i, 0).toString());
                mymodel.removeRow(i);
                 i--;
                
             }
             }
             
             
             // all that should be left are lines to be scheduled            
             if (mytable.getRowCount() > 0) {
              count = OVData.CommitSchedules(mytable);
             }
             postcommit();
             bsmf.MainFrame.show(String.valueOf(count) + " " + "Schedules Committed ");
             } 
    }//GEN-LAST:event_btcommitActionPerformed

    private void mytableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mytableMouseReleased
      
    }//GEN-LAST:event_mytableMouseReleased

    private void mytableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mytableMouseClicked
         int row = mytable.rowAtPoint(evt.getPoint());
        int col = mytable.columnAtPoint(evt.getPoint());
        if ( col == 13) {
              printticket(mytable.getValueAt(row, 0).toString(), "Work Order");
        }
        
        if (col == 14)   {
                    if ( mytable.getValueAt(row, 12).equals("open")) {
                        boolean isGood = OVData.updatePlanOrder(mytable.getValueAt(row, 0).toString(), 
                        mytable.getValueAt(row, 6).toString(),
                        mytable.getValueAt(row, 5).toString(),
                        mytable.getValueAt(row,12).toString() 
                         );
                           if (! isGood) {
                               bsmf.MainFrame.show("Unable to update");
                           } else {
                               bsmf.MainFrame.show("Record Updated");
                           }
                   } 
        }
        if (col == 15)   {
                    if ( mytable.getValueAt(row, 12).equals("open")) {
                        OVData.updatePlanStatus(mytable.getValueAt(row, 0).toString(), "-1");
                        bsmf.MainFrame.show("JobNbr Voided");
                        mytable.setValueAt("voided", row, 12);
                   } 
        }
        
         
         
    }//GEN-LAST:event_mytableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btexport;
    private javax.swing.JCheckBox cbclosed;
    private javax.swing.JCheckBox cbsched;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JTextField fromcell;
    private javax.swing.JTextField frompart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqtyreqd;
    private javax.swing.JLabel labelqtysched;
    private javax.swing.JTable mytable;
    private javax.swing.JTextField tocell;
    private javax.swing.JTextField topart;
    // End of variables declaration//GEN-END:variables
}
