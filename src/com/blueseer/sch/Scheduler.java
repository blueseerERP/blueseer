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
package com.blueseer.sch;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import com.toedter.calendar.DateUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.hrm.hrmData.getEmpNameByDept;
import static com.blueseer.inv.invData.getDeptByItemOperation;
import static com.blueseer.sch.schData.getPlanOperation;
import com.blueseer.sch.schData.plan_operation;
import static com.blueseer.sch.schData.updatePlanOperation;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.OVData.getSysMetaValue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author vaughnte
 */
public class Scheduler extends javax.swing.JPanel {

    int currplan = 0;
    int currop = 0;
    String curritem = "";
    // NOTE:  if you change this...you must also adjust APCheckRun...my advise....dont change it
       Scheduler.MyTableModel mymodel = new Scheduler.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"),
                            getGlobalColumnTag("planid"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("isscheduled"), 
                            getGlobalColumnTag("cell"), 
                            getGlobalColumnTag("schedqty"), 
                            getGlobalColumnTag("reqdqty"), 
                            getGlobalColumnTag("compqty"), 
                            getGlobalColumnTag("scheddate"), 
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("line"), 
                            getGlobalColumnTag("status")})
               {
                   @Override
                public void setValueAt(Object aValue, int row, int column) {
                    if (mymodel.getRowCount() < 1) {
                        return;
                    }
                    super.setValueAt(aValue, row, column);
                    /*
                    if (column == 6) {
                       // String value = aValue == null ? null : aValue.toString();
                        if (aValue == null) {
                            super.setValueAt(null, row, 6);
                        } else {
                            super.setValueAt(cellsonly.indexOf(aValue), row, 6);
                        }
                    }
                    */
                }
                   
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 7 || col == 8 || col == 9) {
                            return Integer.class;
                        } else if (col == 4) {
                            return Boolean.class;  
                        } else if (col == 0) {
                            return ImageIcon.class;      
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }  
                        };
      
      
    MyTableModelDetail modeldetail = new MyTableModelDetail(new Object[][]{},
           new String[]{
               getGlobalColumnTag("planid"), 
               getGlobalColumnTag("item"), 
               getGlobalColumnTag("type"), 
               getGlobalColumnTag("cell"), 
               getGlobalColumnTag("schedqty"), 
               getGlobalColumnTag("status")});
    
    MyTableModelDetail modeloperations = new MyTableModelDetail(new Object[][]{},
           new String[]{
               getGlobalColumnTag("operation"), 
               getGlobalColumnTag("cell"), 
               getGlobalColumnTag("qty"), 
               getGlobalColumnTag("compqty"), 
               getGlobalColumnTag("status"), 
               getGlobalColumnTag("operator")})
            {       
                @Override
                public boolean isCellEditable(int row, int column) {
                      return false;
                      //Only the first column
                      // return column == 1;
                }

            };
     
     DefaultTableModel modelavailable = new DefaultTableModel(new Object[][]{},
           new String[]{
               getGlobalColumnTag("cell"), 
               getGlobalColumnTag("capacity"), 
               getGlobalColumnTag("schedqty"), 
               getGlobalColumnTag("availqty")})
             {       
                @Override
                public boolean isCellEditable(int row, int column) {
                      return false;
                      //Only the first column
                      // return column == 1;
                }

            };
            
    
    boolean isLoad = false;
    ArrayList<String[]>  cells = new ArrayList<String[]>();  // contains cell and capacity
    ArrayList<String>  cellsonly = new ArrayList<String>(); 
    String thisCell = "";
    double thisCellCapacity = 0;
    String startdate = "";
    String enddate = "";
    String cumstartdate = "";
    String cumenddate = "";
    double schtot = 0;
    double reqtot = 0;
    double sumOfAllCells = 0;
    
   
  class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

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
                false, false, false, false, false, false, true, true, false, false, true, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // plan is closed
            if (mytable.getModel().getValueAt(rowIndex, 12).equals(getGlobalProgTag("closed"))) {   // 1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            } else if (mytable.getModel().getValueAt(rowIndex, 12).equals(getGlobalProgTag("void"))) {   // -1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            } else {
               canEdit = new boolean[]{false, false, false, false, false, false, true, true, false, false, true, false, false, false, false, false, false};  
            }
            
            return canEdit[columnIndex];
        }
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
        
        public Class getColumnClass(int column) {
            
            
               if (column == 7)       
                return Double.class; 
               else if (column == 5) 
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
    
  class MyTableModelDetail extends DefaultTableModel {  
      
        public MyTableModelDetail(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 4 )       
                return Double.class;  
            else return String.class;  //other columns accept String values  
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
      
      List<Color> rowColours = Arrays.asList(
        Color.RED,
        Color.GREEN,
        Color.CYAN
    );

    public void setRowColour(int row, Color c) {
        rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }

    public Color getRowColour(int row) {
        return rowColours.get(row);
    }

      
        }    
  
    
  
  class SomeRenderer extends DefaultTableCellRenderer {
         
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
      
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(Color.BLACK);
           
        }
        else
        {
              String status = (String) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 13);  // 7 = status column
             /*
              if (status.equals(getGlobalProgTag("closed"))) {
              setForeground(Color.blue);
             } else if (status.equals(getGlobalProgTag("void"))) {
              setForeground(Color.red);   
             } else {
              setBackground(table.getBackground());
              setForeground(table.getForeground());
              }
              */
              setBackground(table.getBackground());
              setForeground(table.getForeground());
        }
        /*
            boolean issched = (Boolean) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
            if (( column == 5 || column == 6) && ! issched ) {
            c.setBackground(Color.green);
            c.setForeground(Color.BLACK);
            }
            else {
                c.setBackground(table.getBackground());
            }
        */
           
            
            return c;
    }
    }
    
   class AvailableRenderer extends DefaultTableCellRenderer {
         
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
      
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(Color.BLACK);
           
        }
        else
        {
              double diff = Double.valueOf(tableavailable.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString());  // 7 = status column
             
              if (diff < 0) {
              setForeground(Color.red);  
              } else if (diff == 0) {
              setForeground(Color.green); 
             } else {
              setBackground(table.getBackground());
              setForeground(table.getForeground());
              }
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
     * Creates new form MRPBrowse1
     */
    public Scheduler() {
        initComponents();
        setLanguageTags(this);   
    jc.getDayChooser().addPropertyChangeListener("day", new PropertyChangeListener() {
   @Override
   public void propertyChange(PropertyChangeEvent e) {
       int z = (int) e.getNewValue();
       adjustCalendar(z);
       getDetail();
       PanelDetail.setVisible(true);
      /*
       JPanel jPanel = jc.getDayChooser().getDayPanel();
       Component[] component = jPanel.getComponents();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       Calendar cal = Calendar.getInstance();
        cal.setTime(jc.getDate());
        
        // first day of month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int offset = cal.get(Calendar.DAY_OF_WEEK);
        int z = (int) e.getNewValue();
      component[z + 6 + (offset - 1)].setForeground(Color.blue);
      */
        //bsmf.MainFrame.show(e.getPropertyName()+ ": " + e.getNewValue() + " " + c.getBackground());
   }
});     
         
    }

    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
     
    public void printtickets(String fromjob, String tojob ) {
        
       try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "CUT TICKET");
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("fromjob",  fromjob);
                hm.put("tojob", tojob);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/jobticketmulti.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
              //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticketmulti.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
            
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    public void printticket(String jobid, String bustitle) {
        
       try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
                String jasperfile = getSysMetaValue("system", "inventorycontrol", "jasper_job_ticket");  
                if (jasperfile.isBlank()) {
                    jasperfile = "jobticket.jasper";
                }
                
                HashMap hm = new HashMap();
                hm.put("BUSINESSTITLE", bustitle);
                hm.put("REPORT_TITLE", jasperfile);
                hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags); 
                hm.put("myid",  jobid);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
              //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticket.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
           con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    public void printOperationTicket(String jobid, String op, String bustitle) {
        
       try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
                String jasperfile = getSysMetaValue("system", "inventorycontrol", "jasper_operation_ticket");  
                if (jasperfile.isBlank()) {
                    jasperfile = "operationticket.jasper";
                }
                
                HashMap hm = new HashMap();
                hm.put("BUSINESSTITLE", bustitle);
                hm.put("REPORT_TITLE", jasperfile);
                hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags); 
                hm.put("myid",  jobid);
                hm.put("myop",  op);
                hm.put("myidop", jobid + "-" + op);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
              //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticket.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
           con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    
    public void postcommit(int count) {
         /*
         Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -7);
         dcfrom.setDate(calfrom.getTime());
         
         
         Calendar calto = Calendar.getInstance();
         calto.add(Calendar.DATE, 14);
         dcto.setDate(calto.getTime());
         */
        
         frompart.setText("");
         topart.setText("");
         
         
         
        // mytable.setModel(mymodel);
          
          
         bsmf.MainFrame.show(getMessageTag(1121, String.valueOf(count)));
    }
    
    public void initvars(String[] arg) {
        
        isLoad = true;
        
        PanelDetail.setVisible(false);
        panelOp.setVisible(false);
        
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
         
        lblThisDateQtySched.setText("0");
        lblThisDateQtyCapacity.setText("0");
         
        frompart.setText("");
        topart.setText("");
         
        mymodel.setRowCount(0);
        mytable.setModel(mymodel);
        mytable.getTableHeader().setReorderingAllowed(false);
        
        modeldetail.setRowCount(0);
        tabledetail.setModel(modeldetail);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        modeloperations.setRowCount(0);
        tableoperations.setModel(modeloperations);
        tableoperations.getTableHeader().setReorderingAllowed(false);
        
        modelavailable.setRowCount(0);
        tableavailable.setModel(modelavailable);
        tableavailable.getTableHeader().setReorderingAllowed(false);
        
         Enumeration<TableColumn> en = tableavailable.getColumnModel().getColumns();
                while (en.hasMoreElements()) {
                    TableColumn tc = en.nextElement();
                    tc.setCellRenderer(new Scheduler.AvailableRenderer());
                }
         
        ddsite.removeAllItems();
        ArrayList<String>  mylist = OVData.getSiteList();
        for (String code : mylist) {
          ddsite.addItem(code);
        } 
         
        ddcellchoice.removeAllItems();
        ddopcell.removeAllItems();
        cells = OVData.getCodeAndValueMstr("CELL");
        cellsonly.clear();
        sumOfAllCells = 0;
      //  cells = cells_list.toArray(new String[cells_list.size()]);
        for (String[] code : cells) {
          ddcellchoice.addItem(code[0]);
          ddopcell.addItem(code[0]);
          cellsonly.add(code[0]);
          if (code[1] != null && ! code[1].isEmpty()) {
           sumOfAllCells += Double.valueOf(code[1]);
          }
        } 
        cellsonly.add(0, "");
        ddcellchoice.insertItemAt("ALL", 0);
        ddcellchoice.setSelectedIndex(0);
        
        isLoad = false;
       
     
         
    }
    
    public void getDetail() {
         modeldetail.setRowCount(0);
         modelavailable.setRowCount(0);
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
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
                double totqty = 0;
                double totcap = 0;
                
                if (ddcellchoice.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + dfdate.format(jc.getDate()) + "'" +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " order by plan_item, plan_cell;");
                } else {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + dfdate.format(jc.getDate()) + "'" +
                        " AND plan_cell = " + "'" + ddcellchoice.getSelectedItem().toString() + "'" +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " order by plan_item, plan_cell;");    
                }
                while (res.next()) {
                    totqty += res.getDouble("plan_qty_sched");
                   modeldetail.addRow(new Object[]{ 
                      res.getString("plan_nbr"), 
                       res.getString("plan_item"),
                       res.getString("plan_type"),
                       res.getString("plan_cell"),
                       res.getDouble("plan_qty_sched"),
                      res.getString("plan_status")});
                }
                
                
                // now get available
                double qty = 0;
                double diff = 0;
                
               for (String[] cell : cells) {
                   qty = 0;
                   diff = 0;
                   
                   for (int j = 0; j < tabledetail.getRowCount(); j++) {
                        if (cell[0].equals(tabledetail.getValueAt(j, 3).toString())) {
                         qty += Double.valueOf(tabledetail.getValueAt(j, 4).toString());   
                        }
                    }
                    
                    diff = Double.valueOf(cell[1].toString()) - qty;
                    totcap += diff;
                   
                    modelavailable.addRow(new Object[]{ 
                      cell[0].toString(), 
                      cell[1].toString(),
                       String.valueOf(qty),
                       String.valueOf(diff) });
                    
               }
             
               lblThisDateQtySched.setText(String.valueOf(totqty));
               lblThisDateQtyCapacity.setText(String.valueOf(totcap));
               // this.repaint();

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
    
    public void getOperations(int planid) {
        PanelDetail.setVisible(true);
        panelOp.setVisible(true);
        panelDet.setVisible(false);
        modeloperations.setRowCount(0);
        //jPanel2.repaint();
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
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
                double totqty = 0;
                double totcap = 0;
                
                res = st.executeQuery("select * from plan_operation " +
                        " where plo_parent = " + "'" + planid + "'" + " order by plo_op ;");
                while (res.next()) {
                 modeloperations.addRow(new Object[]{ 
                      res.getString("plo_op"), 
                       res.getString("plo_cell"),
                       res.getString("plo_qty"),
                       res.getString("plo_qty_comp"),
                       res.getDouble("plo_status"),
                      res.getString("plo_operator")});
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
    
    public ArrayList<String[]> getSummaryByDate(String fromdate, String todate) {
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String[]> arr = new ArrayList<String[]>();
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
                
                if (ddcellchoice.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT sum(plan_qty_sched) as 'sum', plan_date_sched, plan_cell " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched >= " + "'" + fromdate + "'" +
                        " AND plan_date_sched <= " + "'" + todate + "'"  +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " group by plan_date_sched, plan_cell order by plan_date_sched;");
                } else {
                   res = st.executeQuery("SELECT sum(plan_qty_sched) as 'sum', plan_date_sched, plan_cell " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched >= " + "'" + fromdate + "'" +
                        " AND plan_date_sched <= " + "'" + todate + "'"  +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " AND plan_cell = " + "'" + ddcellchoice.getSelectedItem().toString() + "'" +
                        " group by plan_date_sched, plan_cell order by plan_date_sched;");  
                }
                while (res.next()) {
                    String[] s = new String[]{res.getString("plan_date_sched"), res.getString("plan_cell"),res.getString("sum") };
                    arr.add(s);
                }

            } catch (SQLException s) {
                s.printStackTrace();
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

        return arr;
    }    
    
    public void adjustCalendar(int dateClicked) {
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        int today = Integer.valueOf(dfdate.format(now).toString().substring(8,10));
        JPanel jPanel = jc.getDayChooser().getDayPanel();
        Component component[] = jPanel.getComponents();
        Calendar cal = Calendar.getInstance();
        cal.setTime(jc.getDate());
        
        
        
        // first day of month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstday = dfdate.format(cal.getTime());
        int offset = cal.get(Calendar.DAY_OF_WEEK);
        // last day of month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastday = dfdate.format(cal.getTime());
        
        // reset calendar to current date clicked
        cal.setTime(jc.getDate());
        String x = "";
        double sum = 0.00;
        boolean isFull = false;
        ArrayList<String[]> list = getSummaryByDate(firstday,lastday); // returns date,cell,sum
        
        for (int z = 1; z <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); z++) {
            if (z == dateClicked) {
            component[z + 6 + (offset - 1)].setForeground(Color.red);
            } else {
            component[z + 6 + (offset - 1)].setForeground(Color.black);
            }
            x = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", z) ;
            sum = 0;
            if (ddcellchoice.getSelectedItem().toString().equals("ALL")) {
            for (String[] k : list) {
                if (k[0].equals(x)) {
                    sum += Double.valueOf(k[2]);
                }
            }
                if (sum >= sumOfAllCells) {
                     cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.green);
                     // bsmf.MainFrame.show(x + "/" + z + "/" + offset);
                } else if (sum > 0 && sum < sumOfAllCells) {
                     cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.yellow);
                }  else {
                    cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.LIGHT_GRAY);
                } 
            
            } else {
               for (String[] k : list) {
                if (k[0].equals(x) && k[1].equals(ddcellchoice.getSelectedItem().toString())) {
                    sum += Double.valueOf(k[2]);
                }
            }
            //   bsmf.MainFrame.show(thisCell + "/" + sum + "/" + thisCellCapacity);
               
                if (sum >= thisCellCapacity) {
                     cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.green);
                    
                } else if (sum > 0 && sum < thisCellCapacity) {
                     cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.yellow);
                     // bsmf.MainFrame.show(x + "/" + z + "/" + offset);
                }  else {
                    cal.set(Calendar.DAY_OF_MONTH,z);
                     component[z + 6 + (offset - 1)].setBackground(Color.LIGHT_GRAY);
                }
            }
            
        }
        
        // if dateclicked = 0 ; refresh to current day
        if (dateClicked == 0) {
           component[today + 6 + (offset - 1)].setForeground(Color.red);
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
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jc = new com.toedter.calendar.JCalendar();
        ddcellchoice = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        frompart = new javax.swing.JTextField();
        labelqtyreqd = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        labelcount = new javax.swing.JLabel();
        labelqtysched = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        cbclosed = new javax.swing.JCheckBox();
        topart = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbsched = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        bthide = new javax.swing.JButton();
        lblThisDateQtySched = new javax.swing.JLabel();
        lblThisDateQtyCapacity = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btvoid = new javax.swing.JButton();
        PanelReport = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();
        PanelDetail = new javax.swing.JPanel();
        panelDet = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableavailable = new javax.swing.JTable();
        panelOp = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableoperations = new javax.swing.JTable();
        panelOpMaint = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        ddopcell = new javax.swing.JComboBox<>();
        ddopoperator = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        dcopdate = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        tbopqty = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ddopstatus = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        btopupdate = new javax.swing.JButton();
        btopprint = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));
        setPreferredSize(new java.awt.Dimension(1211, 744));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduler"));
        jPanel2.setName("panelmain"); // NOI18N

        jc.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jc.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jcPropertyChange(evt);
            }
        });

        ddcellchoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcellchoiceActionPerformed(evt);
            }
        });

        jLabel4.setText("Cell:");
        jLabel4.setName("lblcell"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelqtyreqd.setText("0");

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel2.setText("Tot Qty Reqd");
        jLabel2.setName("lbltotqtyreqd"); // NOI18N

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("Site");
        jLabel3.setName("lblsite"); // NOI18N

        jLabel1.setText("Tot Qty Sched");
        jLabel1.setName("lbltotqtysched"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        labelcount.setText("0");

        labelqtysched.setText("0");

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        cbclosed.setText("OpenOnly?");
        cbclosed.setName("cbopenonly"); // NOI18N
        cbclosed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbclosedActionPerformed(evt);
            }
        });

        jLabel7.setText("From:");
        jLabel7.setName("lblfrom"); // NOI18N

        cbsched.setText("Unscheduled Only?");
        cbsched.setName("cbunscheduled"); // NOI18N

        jLabel9.setText("Rows");
        jLabel9.setName("lblrows"); // NOI18N

        jLabel8.setText("To:");
        jLabel8.setName("lblto"); // NOI18N

        bthide.setText("Hide Detail");
        bthide.setName("bthidedetail"); // NOI18N
        bthide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthideActionPerformed(evt);
            }
        });

        lblThisDateQtySched.setText("0");

        lblThisDateQtyCapacity.setText("0");

        jLabel5.setText("Selected Date Tot Qty");
        jLabel5.setName("lblselecteddatetotqty"); // NOI18N

        jLabel6.setText("Selected Date Tot Avail");
        jLabel6.setName("lblselecteddatetotavail"); // NOI18N

        jLabel10.setText("From Item:");

        jLabel11.setText("To Item:");

        btprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print.png"))); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btupdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/refresh.png"))); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btvoid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/void.png"))); // NOI18N
        btvoid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btvoidActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btRun)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(labelqtyreqd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblThisDateQtySched, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblThisDateQtyCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(topart, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(frompart, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dcfrom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                            .addComponent(dcto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(cbsched)
                                                .addGap(5, 5, 5)
                                                .addComponent(cbclosed, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(btcommit)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(bthide)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btprint, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btvoid, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE)))))))
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel7)
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frompart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(topart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbsched)
                    .addComponent(cbclosed))
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btcommit)
                        .addComponent(bthide))
                    .addComponent(btprint)
                    .addComponent(btupdate)
                    .addComponent(btvoid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelqtyreqd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblThisDateQtySched, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblThisDateQtyCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddcellchoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcellchoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jc, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(172, 172, 172))
        );

        PanelReport.setPreferredSize(new java.awt.Dimension(609, 402));
        PanelReport.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 401));

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
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
        mytable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mytableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mytable);

        PanelReport.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        PanelDetail.setLayout(new java.awt.CardLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduled On This Date"));

        tabledetail.setAutoCreateRowSorter(true);
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
        jScrollPane2.setViewportView(tabledetail);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Cells On This Date"));

        tableavailable.setAutoCreateRowSorter(true);
        tableavailable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tableavailable);

        javax.swing.GroupLayout panelDetLayout = new javax.swing.GroupLayout(panelDet);
        panelDet.setLayout(panelDetLayout);
        panelDetLayout.setHorizontalGroup(
            panelDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDetLayout.setVerticalGroup(
            panelDetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PanelDetail.add(panelDet, "card2");

        panelOp.setLayout(new javax.swing.BoxLayout(panelOp, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(300, 422));

        tableoperations.setAutoCreateRowSorter(true);
        tableoperations.setModel(new javax.swing.table.DefaultTableModel(
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
        tableoperations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableoperationsMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableoperations);

        panelOp.add(jScrollPane4);

        panelOpMaint.setBorder(javax.swing.BorderFactory.createTitledBorder("Operation Maintenance"));
        panelOpMaint.setPreferredSize(new java.awt.Dimension(300, 422));

        jLabel12.setText("Cell:");

        jLabel13.setText("Operator:");

        dcopdate.setDateFormatString("yyyy-MM-dd");

        jLabel14.setText("Date:");

        jLabel15.setText("Qty:");

        ddopstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "open", "closed", "void" }));

        jLabel16.setText("Status:");

        btopupdate.setText("Update");
        btopupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btopupdateActionPerformed(evt);
            }
        });

        btopprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print.png"))); // NOI18N
        btopprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btopprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOpMaintLayout = new javax.swing.GroupLayout(panelOpMaint);
        panelOpMaint.setLayout(panelOpMaintLayout);
        panelOpMaintLayout.setHorizontalGroup(
            panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOpMaintLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOpMaintLayout.createSequentialGroup()
                        .addComponent(btopupdate)
                        .addGap(31, 31, 31)
                        .addComponent(btopprint, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(dcopdate, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addComponent(ddopcell, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddopoperator, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(tbopqty, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddopstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(258, Short.MAX_VALUE))
        );
        panelOpMaintLayout.setVerticalGroup(
            panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOpMaintLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(ddopcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddopoperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcopdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelOpMaintLayout.createSequentialGroup()
                        .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbopqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOpMaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddopstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btopupdate))
                    .addComponent(btopprint))
                .addContainerGap(119, Short.MAX_VALUE))
        );

        panelOp.add(panelOpMaint);

        PanelDetail.add(panelOp, "card3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 549, Short.MAX_VALUE)
            .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 549, Short.MAX_VALUE)
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void bthideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthideActionPerformed
       PanelDetail.setVisible(false);
       panelOp.setVisible(false);
       panelDet.setVisible(false);
       // tabledetail.setVisible(false);
       // btdetail.setEnabled(false);
    }//GEN-LAST:event_bthideActionPerformed

    private void mytableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mytableMouseClicked
        int row = mytable.rowAtPoint(evt.getPoint());
        int col = mytable.columnAtPoint(evt.getPoint());
        
        if ( col == 0) {
              currplan = Integer.valueOf(mytable.getValueAt(row, 1).toString());
              curritem = mytable.getValueAt(row, 2).toString();
              getOperations(Integer.valueOf(mytable.getValueAt(row, 1).toString()));
        }
    }//GEN-LAST:event_mytableMouseClicked

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
        schtot = 0;
        reqtot = 0;

        labelqtysched.setText("0");
        labelqtyreqd.setText("0");
        labelcount.setText("0");

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

                double amt = 0;

                 int i = 0;
                String fpart = "";
                String tpart = "";
                String fcell = bsmf.MainFrame.lowchar;
                String tcell = bsmf.MainFrame.hichar;
                String status = "";

                if (frompart.getText().isEmpty()) {
                    fpart = bsmf.MainFrame.lowchar;
                } else {
                    fpart = frompart.getText().replace("'", "");
                }
                if (topart.getText().isEmpty()) {
                    tpart = bsmf.MainFrame.hichar;
                } else {
                    tpart = topart.getText().replace("'", "");
                }
               
                
                //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
                    //         new String[]{"Acct", "Description", "Amt"});
                // tablescrap.setModel(mymodel);

                //  mytable.getColumnModel().getColumn(0).setCellRenderer(new ProdSchedPanel.SomeRenderer());

                CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
                mytable.getColumnModel().getColumn(5).setCellRenderer(checkBoxRenderer);

                //  ComboBoxRenderer comboBoxRenderer = new ComboBoxRenderer(new String[]{"1","2"});
                //  mytable.getColumnModel().getColumn(5).setCellRenderer(comboBoxRenderer);
                
                TableColumn col = mytable.getColumnModel().getColumn(6);
               // col.setCellEditor(new ComboBoxEditor(cellsonly.toArray(new String[cellsonly.size()])));
              //  col.setCellEditor(new TestCellEditor(cellsonly.toArray(new String[cellsonly.size()])));
              col.setCellEditor(new DefaultCellEditor(new JComboBox(cellsonly.toArray(new String[cellsonly.size()]))));
             // col.setCellRenderer(new ComboBoxRenderer(cellsonly.toArray(new String[cellsonly.size()])));
                
                Enumeration<TableColumn> en = mytable.getColumnModel().getColumns();
                while (en.hasMoreElements()) {
                    TableColumn tc = en.nextElement();
                    if (mymodel.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                    if (tc.getIdentifier().toString().equals("isSched") ||
                        tc.getIdentifier().toString().equals("Cell") ) {
                        continue;
                    }
                    tc.setCellRenderer(new Scheduler.SomeRenderer());
                }

                //       mytable.getColumn("Update").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
                //        mytable.getColumn("Print").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
                //        mytable.getColumn("Void").setCellRenderer(new ProdSchedPanel.ButtonRenderer());

                DefaultCellEditor singleClick = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(7));
                singleClick.setClickCountToStart(1);
                mytable.setDefaultEditor(mytable.getColumnClass(7), singleClick);
                //   DefaultCellEditor singleClick2 = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(5));
                //  singleClick2.setClickCountToStart(1);
                //   mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick2);

                //  mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick);

                //    mytable.getColumn("Update").setCellEditor(
                    //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                //     mytable.getColumn("Print").setCellEditor(
                    //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                //      mytable.getColumn("Void").setCellEditor(
                    //              new ProdSchedPanel.ButtonEditor(new JCheckBox()));
               // mytable.getColumn(getGlobalColumnTag("update")).setMaxWidth(100);
               // mytable.getColumn(getGlobalColumnTag("print")).setMaxWidth(100);
               // mytable.getColumn(getGlobalColumnTag("void")).setMaxWidth(100);

                mymodel.setRowCount(0);

                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

                // tcm.getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                if (cbsched.isSelected()) {
                    res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                      //  + "( select coalesce(sum(pland_qty),0) as qtycomp from pland_mstr where pland_parent = plan_nbr) as qtycomp,"
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND plan_item >= " + "'" + fpart + "'" +
                        " AND plan_item <= " + "'" + tpart + "'" +
                        " AND plan_cell >= " + "'" + fcell + "'" +
                        " AND plan_cell <= " + "'" + tcell + "'" +
                        " AND plan_is_sched = " + "'0' "  +
                        " order by plan_item, plan_date_due;");
                } else {
                    res = st.executeQuery("SELECT plan_nbr, plan_item, plan_type, plan_qty_req, plan_qty_comp, "
                      //  + "( select coalesce(sum(pland_qty),0) as qtycomp from pland_mstr where pland_parent = plan_nbr) as qtycomp,"     
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND plan_item >= " + "'" + fpart + "'" +
                        " AND plan_item <= " + "'" + tpart + "'" +
                        " AND plan_cell >= " + "'" + fcell + "'" +
                        " AND plan_cell <= " + "'" + tcell + "'" +
                        " order by plan_item, plan_date_due ;");
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
                    if (res.getString("plan_status").equals("0")) { status = getGlobalProgTag("open"); }
                    if (res.getString("plan_status").equals("1")) { status = getGlobalProgTag("closed"); }
                    if (res.getString("plan_status").equals("-1")) { status = getGlobalProgTag("void"); }

                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, 
                        res.getString("plan_nbr"),
                        res.getString("plan_item"),
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
                        status
                    });
                }
                labelqtysched.setText(String.valueOf(schtot));
                labelqtyreqd.setText(String.valueOf(reqtot));
                labelcount.setText(String.valueOf(i));

        adjustCalendar(0);
                
                
                //    RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mymodel);
                //     mytable.setRowSorter(sorter);

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

    }//GEN-LAST:event_btRunActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        boolean commit = true;
        int count = 0;
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        // clean out the unchecked rows
//Double.valueOf(tableavailable.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString());  // 7 = status column
             
        for (int i = 0 ; i < mymodel.getRowCount(); i++) {
            if (! mymodel.getValueAt(i, 13).equals(getGlobalProgTag("open")) && ! mymodel.getValueAt(i, 13).equals(getGlobalProgTag("closed")) && ! mymodel.getValueAt(i, 13).equals(getGlobalProgTag("void"))) {
                bsmf.MainFrame.show(getMessageTag(1124));
                commit = false;
                break;
            }
        }

        if (commit) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            for (int i = 0 ; i < mymodel.getRowCount(); i++) {
                if ( (boolean) mymodel.getValueAt(i, 5) == true ) {
                    continue;
                }
                if (    mymodel.getValueAt(i, 6).toString().isEmpty() || 
                        mymodel.getValueAt(i, 6) == null || 
                        mymodel.getValueAt(i, 7).toString().isEmpty() || 
                        mymodel.getValueAt(i, 7) == null ||
                        mymodel.getValueAt(i, 7).toString().equals("0") ) {
                  continue;  
                }
                list.add(new String[]{mymodel.getValueAt(i, 1).toString(),
                    mymodel.getValueAt(i, 6).toString(),
                    mymodel.getValueAt(i, 7).toString(),
                    mymodel.getValueAt(i, 13).toString()});
            }
            
            mymodel.setRowCount(0);
         
            if (list.size() > 0) {
                count = OVData.CommitSchedules(list, dtf.format(jc.getDate()) ); 
            }
            postcommit(count);
            
        }
    }//GEN-LAST:event_btcommitActionPerformed

    private void cbclosedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbclosedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbclosedActionPerformed

    private void jcPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcPropertyChange
       if (evt.getOldValue() != null && evt.getNewValue() != null && ! evt.getPropertyName().toLowerCase().equals("ancestor")) {
           String[] o = evt.getOldValue().toString().split(",");
           String[] n = evt.getNewValue().toString().split(",");
           
           if (! o[30].equals(n[30])) {
               adjustCalendar(0);
           }
       }
    }//GEN-LAST:event_jcPropertyChange

    private void ddcellchoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcellchoiceActionPerformed
        if (! isLoad) {
          //  bsmf.MainFrame.show("firing");
            for (String[] x : cells) {
                if (ddcellchoice.getSelectedItem().toString().equals(x[0])) {
                    thisCell = x[0];
                  //  bsmf.MainFrame.show(x[0]);
                    if (x[1] != null && ! x[1].isEmpty()) {
                    thisCellCapacity = Double.valueOf(x[1]);
                    } else {
                    thisCellCapacity = 0;    
                    }
                }
            }
            adjustCalendar(0);
            getDetail();
            PanelDetail.setVisible(true);
        }
    }//GEN-LAST:event_ddcellchoiceActionPerformed

    private void btopupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btopupdateActionPerformed
        
        if (currplan > 0 && currop > 0) {
            String operator = (ddopoperator.getSelectedItem() == null) ? "" : ddopoperator.getSelectedItem().toString();
            String cell = (ddopcell.getSelectedItem() == null) ? "" : ddopcell.getSelectedItem().toString();
            schData.plan_operation x = new schData.plan_operation(null, 
                    0, // id
                    currplan, // parent
                    currop, // op
                    bsParseDouble(tbopqty.getText().replace(defaultDecimalSeparator, '.')), // qty
                    0, // comp qty
                    cell, // cell
                    operator, // operator
                    setDateDB(dcopdate.getDate()), // date
                    ddopstatus.getSelectedItem().toString(), //status
                    bsmf.MainFrame.userid // userid
                    );
            updatePlanOperation(x);
            getOperations(currplan);
        }
    }//GEN-LAST:event_btopupdateActionPerformed

    private void tableoperationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableoperationsMouseClicked
        int row = tableoperations.rowAtPoint(evt.getPoint());
        int col = tableoperations.columnAtPoint(evt.getPoint());
        currop = Integer.valueOf(tableoperations.getValueAt(row, 0).toString());
        plan_operation x = getPlanOperation(currplan, currop);
        String dept = getDeptByItemOperation(curritem, currop);
        
        if ( x.m()[0].equals("0")) {
              ArrayList<String> operators = getEmpNameByDept(dept);
              
              ddopoperator.removeAllItems();
              for (String operator : operators) {
                  ddopoperator.addItem(operator);
              }
              ddopoperator.insertItemAt("", 0);
              ddopoperator.setSelectedItem(x.plo_operator());
              
              ddopcell.setSelectedItem(x.plo_cell());
              tbopqty.setText(bsNumber(x.plo_qty()));
              dcopdate.setDate(parseDate(x.plo_date()));
              ddopstatus.setSelectedItem(x.plo_status());              
        }
    }//GEN-LAST:event_tableoperationsMouseClicked

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        if (mytable.getSelectedRowCount() == 0) {
        bsmf.MainFrame.show(getMessageTag(1188));
        return;
        }
        
        if (mytable.getSelectedRowCount() > 1) {
        bsmf.MainFrame.show(getMessageTag(1095));
        return;
        }
        
        if (mytable.getSelectedRowCount() == 1) {
        int row = mytable.getSelectedRow();
        printticket(mytable.getValueAt(row, 1).toString(), "Work Order");
        }
        
    }//GEN-LAST:event_btprintActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (mytable.getSelectedRowCount() == 0) {
        bsmf.MainFrame.show(getMessageTag(1188));
        return;
        }
        
        if (mytable.getSelectedRowCount() > 1) {
        bsmf.MainFrame.show(getMessageTag(1095));
        return;
        }
        
        if (mytable.getSelectedRowCount() == 1) {
        int row = mytable.getSelectedRow();
          if (mytable.getValueAt(row, 5).toString().equals("false")) {
              bsmf.MainFrame.show(getMessageTag(1182));
              return;
          }
            if ( mytable.getValueAt(row, 13).equals(getGlobalProgTag("open"))) {
                        // lets confirm valid date has been entered
                        if (! BlueSeerUtils.isValidDateStr(mytable.getValueAt(row, 10).toString())) {
                            bsmf.MainFrame.show(getMessageTag(1123));
                            return;
                        }
                        boolean isGood = schData.updatePlanOrder(mytable.getValueAt(row, 1).toString(), 
                        mytable.getValueAt(row, 7).toString(),
                        mytable.getValueAt(row, 6).toString(),
                        mytable.getValueAt(row, 10).toString(),
                        mytable.getValueAt(row,13).toString() 
                         );  
                           if (! isGood) {
                               bsmf.MainFrame.show(getMessageTag(1012));
                           } else {
                               bsmf.MainFrame.show(getMessageTag(1008));
                           }
            } 
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btvoidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btvoidActionPerformed
        
        if (mytable.getSelectedRowCount() == 0) {
        bsmf.MainFrame.show(getMessageTag(1188));
        return;
        }
        
        if (mytable.getSelectedRowCount() > 1) {
        bsmf.MainFrame.show(getMessageTag(1095));
        return;
        }
        
        if (mytable.getSelectedRowCount() == 1) {
        int row = mytable.getSelectedRow();
            if ( mytable.getValueAt(row, 13).equals(getGlobalProgTag("open"))) {
                            schData.updatePlanStatus(mytable.getValueAt(row, 1).toString(), "-1");
                            bsmf.MainFrame.show(getMessageTag(1072, mytable.getValueAt(row, 1).toString()));
                            mytable.setValueAt(getGlobalProgTag("void"), row, 13);
            }
        }
    }//GEN-LAST:event_btvoidActionPerformed

    private void btopprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btopprintActionPerformed
        if (currplan > 0 && currop > 0) {
           printOperationTicket(String.valueOf(currplan), String.valueOf(currop), "Operation Ticket"); 
        }
    }//GEN-LAST:event_btopprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDetail;
    private javax.swing.JPanel PanelReport;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton bthide;
    private javax.swing.JButton btopprint;
    private javax.swing.JButton btopupdate;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btvoid;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbclosed;
    private javax.swing.JCheckBox cbsched;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcopdate;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddcellchoice;
    private javax.swing.JComboBox<String> ddopcell;
    private javax.swing.JComboBox<String> ddopoperator;
    private javax.swing.JComboBox<String> ddopstatus;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JTextField frompart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private com.toedter.calendar.JCalendar jc;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqtyreqd;
    private javax.swing.JLabel labelqtysched;
    private javax.swing.JLabel lblThisDateQtyCapacity;
    private javax.swing.JLabel lblThisDateQtySched;
    private javax.swing.JTable mytable;
    private javax.swing.JPanel panelDet;
    private javax.swing.JPanel panelOp;
    private javax.swing.JPanel panelOpMaint;
    private javax.swing.JTable tableavailable;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTable tableoperations;
    private javax.swing.JTextField tbopqty;
    private javax.swing.JTextField topart;
    // End of variables declaration//GEN-END:variables
}
