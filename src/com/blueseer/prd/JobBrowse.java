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

package com.blueseer.prd;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
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
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.hrm.hrmData.getEmpFormalNameByID;
import static com.blueseer.hrm.hrmData.getEmpNameAll;
import static com.blueseer.hrm.hrmData.getEmpNameByDept;
import static com.blueseer.hrm.hrmData.getempmstrlist;
import static com.blueseer.inv.invData.getAllOperationIDs;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import com.blueseer.utl.OVData;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class JobBrowse extends javax.swing.JPanel {
 
    
       // NOTE:  if you change this...you must also adjust APCheckRun...my advise....dont change it
       MasterModel mymodel = new MasterModel(new Object[][]{},
                        new String[]{ 
                            getGlobalColumnTag("planid"), 
                            getGlobalColumnTag("type"),
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("operation"),
                            getGlobalColumnTag("operator"),
                            getGlobalColumnTag("cell"),
                            getGlobalColumnTag("scheddate"), 
                            getGlobalColumnTag("schedqty"),
                            getGlobalColumnTag("compqty"),
                            getGlobalColumnTag("hours"), 
                            getGlobalColumnTag("status"),
                            getGlobalColumnTag("code")
                            })
               {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 7 || col == 8 || col == 9)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
      
      
       DetailModel modeldetail = new DetailModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                            getGlobalColumnTag("planid"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("operation"), 
                            getGlobalColumnTag("cell"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("reference"), 
                            getGlobalColumnTag("quantity")});

      
     
      double schtot = 0;
      double comptot = 0;
      double avghours = 0;
      double tothours = 0;
      boolean isLoad = false;
      boolean multiOp = false;
      String chartfilepath = OVData.getSystemTempDirectory() + "/" + "jobbrowse.jpg";
      
      
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
      
    class MasterModel extends DefaultTableModel {  
      
        public MasterModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 7 || col == 8 || col == 9)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
           
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {
            return false;  
      }  
       
        }    
    
    class DetailModel extends DefaultTableModel {  
      
        public DetailModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 7 )       
                return Double.class;  
            else return String.class;  //other columns accept String values  
           
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
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
       
       
              String status = mastertable.getModel().getValueAt(table.convertRowIndexToModel(row), 11).toString();  // 7 = status column
              String plostatus = mastertable.getModel().getValueAt(table.convertRowIndexToModel(row), 10).toString();  // 7 = status column
              String plotype = mastertable.getModel().getValueAt(table.convertRowIndexToModel(row), 1).toString();
              if (status != null && status.equals("in")) {
              setForeground(Color.blue);
              setBackground(table.getBackground());
              } else if (plostatus != null && plostatus.equals("unscheduled") && ! plotype.equals("SRVC")) {
              setForeground(Color.red);
              setBackground(table.getBackground());
              } else {
              setBackground(table.getBackground());
              setForeground(table.getForeground());
              }
             
            
       // c.setBackground(table.getBackground());
            
        
           
            
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
            if (mastertable.getModel().getValueAt(row, 2).toString().compareTo("approved") == 0) {
            setBackground(Color.green);
            //setEnabled(false);
            setText("complete");
            }
            return this;
        }
    }

    
    
    
/**
 * @version 1.0 11/09/98
 */



     
     
    public JobBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void chartHours() {
               
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                LinkedHashMap<String, Double> lhm = new LinkedHashMap<String, Double>();
                for (int j = 0; j < mastertable.getRowCount(); j++) {
                    if (lhm.containsKey(mastertable.getValueAt(j, 4).toString())) {
                       lhm.put(mastertable.getValueAt(j, 4).toString(), lhm.get(mastertable.getValueAt(j, 4).toString()) + Double.valueOf(mastertable.getValueAt(j, 9).toString()));
                    } else {
                        lhm.put(mastertable.getValueAt(j, 4).toString(), Double.valueOf(mastertable.getValueAt(j, 9).toString()));
                    }
                }
                
                for (Map.Entry<String, Double> z : lhm.entrySet()) {
                  dataset.setValue(z.getKey(), z.getValue());
                }
        JFreeChart chart = ChartFactory.createPieChart("Hours By Operator", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, (tablepanel.getWidth() - 70) / 2, this.getHeight() - 200);
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        pielabel.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              
       
    }
    
    
    public void getdetail(String jobnbr) {
      
         modeldetail.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        
        
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
                double dol = 0.00;
                int qty = 0;
                
                
                
                 res = st.executeQuery("SELECT * from plan_operation   " +
                        " inner join plan_mstr on plan_nbr = plo_parent " +
                        " where pland_parent = " + "'" + jobnbr + "'" + 
                         " order by pland_id desc ;"); 
               
                 
             
                
                 
                while (res.next()) {
                    qty = qty + 0;
                    i++;
                        modeldetail.addRow(new Object[]{
                            res.getString("pland_id"),
                            res.getString("pland_parent"),
                            res.getString("pland_item"),
                            res.getString("pland_op"),
                            res.getString("pland_cell"),
                            res.getString("pland_date"),
                            res.getString("pland_ref"),
                            res.getDouble("pland_qty")
                            });
                   }
               
               
                
              //  tabledetail.setModel(modeldetail);
                this.repaint();

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
                 File mytemplate = new File("jasper/jobticketnitridemulti.jasper");
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
            
                HashMap hm = new HashMap();
                String jasperfile = "jobticket.jasper";
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
               // JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticket.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
            
            con.close();
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
         mastertable.setModel(mymodel);
          
          
          
             
         
         
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
         
        isLoad = true;
        
        pielabel.setHorizontalTextPosition(SwingConstants.CENTER);
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
         lblempname.setText("");
         
         mymodel.setRowCount(0);
         mastertable.setModel(mymodel);
         mastertable.getTableHeader().setReorderingAllowed(false);
        
        ArrayList<String> operators = getempmstrlist();
        ddoperator.removeAllItems();
        for (String operator : operators) {
            ddoperator.addItem(operator);
        }
        ddoperator.insertItemAt("", 0);
        ddoperator.setSelectedIndex(0);
        
        Set<String> set = getAllOperationIDs();
        ddop.removeAllItems();
        for (String s : set) {
              ddop.addItem(s);
        }
        ddop.insertItemAt("1", 0);
        ddop.insertItemAt("", 0);
        ddop.setSelectedIndex(0);
        
        detailpanel.setVisible(false);
          
         
        isLoad = false; 
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
        labelqtycomp = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelavghours = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lbltothours = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        frompart = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        topart = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        tocell = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        fromcell = new javax.swing.JTextField();
        cbclosed = new javax.swing.JCheckBox();
        ddop = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        ddoperator = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        lblempname = new javax.swing.JLabel();
        tbjobid = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbchart = new javax.swing.JCheckBox();
        btcsv = new javax.swing.JButton();
        tablepanel = new javax.swing.JPanel();
        masterpanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mastertable = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        pielabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        labelqtysched.setBackground(new java.awt.Color(195, 129, 129));
        labelqtysched.setText("0");

        labelcount.setText("0");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Rows");
        jLabel7.setName("lblcount"); // NOI18N

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Total Sched Qty");
        jLabel9.setName("lbltotalschedqty"); // NOI18N

        labelqtycomp.setBackground(new java.awt.Color(195, 129, 129));
        labelqtycomp.setText("0");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Total Comp Qty");
        jLabel11.setName("lbltotalreqdqty"); // NOI18N

        labelavghours.setBackground(new java.awt.Color(195, 129, 129));
        labelavghours.setText("0");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Average Hours");
        jLabel12.setToolTipText("");
        jLabel12.setName("lbltotalreqdqty"); // NOI18N

        lbltothours.setBackground(new java.awt.Color(195, 129, 129));
        lbltothours.setText("0");

        jLabel14.setText("Total Hours");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelavghours, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelqtycomp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelqtysched, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                    .addComponent(labelcount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbltothours, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelqtycomp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbltothours, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelavghours, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setText("To Cell:");
        jLabel6.setName("lbltocell"); // NOI18N

        jLabel4.setText("To Item");
        jLabel4.setName("lbltoitem"); // NOI18N

        jLabel3.setText("To Date");
        jLabel3.setName("lbltodate"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Item");
        jLabel1.setName("lblfromitem"); // NOI18N

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("From Date");
        jLabel2.setName("lblfromdate"); // NOI18N

        jLabel5.setText("From Cell:");
        jLabel5.setName("lblfromcell"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        cbclosed.setText("Closed Only?");
        cbclosed.setName("cbopen"); // NOI18N

        jLabel10.setText("Operation");

        ddoperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddoperatorActionPerformed(evt);
            }
        });

        jLabel8.setText("Operator");

        jLabel13.setText("Job ID");

        cbchart.setText("Chart");
        cbchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbchartActionPerformed(evt);
            }
        });

        btcsv.setText("CSV");
        btcsv.setName("btcsv"); // NOI18N
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbjobid, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(frompart)
                    .addComponent(topart, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addComponent(ddoperator, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tocell, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fromcell, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblempname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbclosed)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbchart)))
                .addGap(60, 60, 60))
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
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(tbjobid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(topart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fromcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tocell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(cbclosed)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(cbchart)
                        .addComponent(btcsv)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblempname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddoperator, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
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

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        masterpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Table"));
        masterpanel.setPreferredSize(new java.awt.Dimension(464, 400));
        masterpanel.setLayout(new java.awt.BorderLayout());

        mastertable.setModel(new javax.swing.table.DefaultTableModel(
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
        mastertable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mastertableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mastertable);

        masterpanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(masterpanel);

        detailpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chart Panel"));
        detailpanel.setPreferredSize(new java.awt.Dimension(464, 400));
        detailpanel.setLayout(new java.awt.CardLayout());
        detailpanel.add(pielabel, "card2");

        tablepanel.add(detailpanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tablepanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        comptot = 0;
        avghours = 0;
        tothours = 0;
        
        labelqtysched.setText("0");
        labelqtycomp.setText("0");
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
                String fpart;
                String tpart;
                String fcell;
                String tcell;
                String clockstatus;
                String plostatus;
                String operatorname;
                
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
                
                String jobid = "";
                if (! tbjobid.getText().isBlank()) {
                    if (tbjobid.getText().contains("-")) {
                       String[] sc = tbjobid.getText().split("-",-1);
                        if (sc != null && sc.length == 2) {
                          jobid = sc[0];
                        } 
                    } else {
                        jobid = tbjobid.getText();
                    }
                }
                 
                
             
              //    CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
             //   mastertable.getColumnModel().getColumn(4).setCellRenderer(checkBoxRenderer); 
                 
                 Enumeration<TableColumn> en = mastertable.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (mymodel.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new SomeRenderer());
                 }   
                
                mastertable.getColumnModel().getColumn(0).setMaxWidth(100);
               
            
                mymodel.setRowCount(0);
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                 
                 if (! jobid.isBlank()) {
                   res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plo_op, plo_operator, plo_operatorname, plo_cell, " +
                         "plo_qty, plo_qty_comp, plo_date, plo_status, " +
                         " jobc_empnbr, jobc_qty, coalesce(jobc_tothrs,0) as jobc_tothrs, jobc_code,  " +
                         " emp_lname, emp_fname " +   
                        " FROM  plan_operation " +
                        " inner join plan_mstr on plan_nbr = plo_parent " +
                        " left outer join job_clock on jobc_planid = plo_parent and jobc_op = plo_op " + 
                        " left outer join emp_mstr on emp_nbr = jobc_empnbr " +   
                        " where plo_parent = " + "'" + jobid + "'" + 
                        " order by plo_op;");    
  
                 } else {
                   res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plo_op, plo_operator, plo_operatorname, plo_cell, " +
                         "plo_qty, plo_qty_comp, plo_date, plo_status, " +
                         " jobc_empnbr, jobc_qty, coalesce(jobc_tothrs,0) as jobc_tothrs, jobc_code,  " +
                         " emp_lname, emp_fname " +  
                        " FROM  plan_operation " +
                        " inner join plan_mstr on plan_nbr = plo_parent " +
                        " left outer join job_clock on jobc_planid = plo_parent and jobc_op = plo_op " + 
                        " left outer join emp_mstr on emp_nbr = jobc_empnbr " +    
                        " where " +
                        " (( plo_date >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND plo_date <= " + "'" + dfdate.format(dcto.getDate()) + "'" + " ) OR plo_date is null )" + 
                        " AND plan_item >= " + "'" + fpart + "'" + 
                        " AND plan_item <= " + "'" + tpart + "'" + 
                         " AND ( ( plo_cell >= " + "'" + fcell + "'" + 
                        " AND plo_cell <= " + "'" + tcell + "'" + ") OR plo_cell = '' ) " + 
                      //  " AND plan_is_sched = " + "'1' "  +
                        " order by plan_nbr, plo_op;");    
  
                 }
                 
                                  
                 
                while (res.next()) {
                    
                    if (cbclosed.isSelected() && ! res.getString("plo_status").equals("closed") ) {
                        continue;
                    }
                    
                    if (! ddop.getSelectedItem().toString().isBlank() && ! res.getString("plo_op").equals(ddop.getSelectedItem().toString())) {
                        continue;
                    }
                    if (! ddoperator.getSelectedItem().toString().isBlank() && ! res.getString("jobc_empnbr").equals(ddoperator.getSelectedItem().toString())) {
                        continue;
                    }
                    
                    i++;
                    if (res.getString("jobc_code") == null) {
                        clockstatus = "n/c";
                    } else {
                        clockstatus = (res.getString("jobc_code").equals("01")) ? "in" : "out";
                    }
                    if (res.getString("plo_status").isBlank()) {
                        plostatus = "unscheduled";
                    } else {
                        plostatus = res.getString("plo_status");
                    }
                    
                    operatorname = res.getString("emp_lname") + ", " + res.getString("emp_fname");
                    
                    schtot = schtot + res.getInt("plo_qty");
                    comptot = comptot + res.getInt("plo_qty_comp");
                    avghours = avghours + res.getDouble("jobc_tothrs");
                    tothours = tothours + res.getDouble("jobc_tothrs");
                    mymodel.addRow(new Object[]{
                                res.getString("plan_nbr"),
                                res.getString("plan_type"),
                                res.getString("plan_item"),
                                res.getString("plo_op"),
                                operatorname,
                                res.getString("plo_cell"),
                                res.getString("plo_date"),
                                res.getDouble("plo_qty"),
                                res.getDouble("plo_qty_comp"),
                                res.getDouble("jobc_tothrs"),
                                plostatus,
                                clockstatus
                            });
                    
                    
                    
                }
                avghours = (i > 0) ? (avghours / i) : 0;
                if (ddop.getSelectedItem().toString().isBlank()) {
                  labelavghours.setText("N/A");  
                } else {
                  labelavghours.setText(String.valueOf(avghours));  
                }
                lbltothours.setText(String.valueOf(tothours));
                labelqtysched.setText(String.valueOf(schtot));
                labelqtycomp.setText(String.valueOf(comptot));
                
                labelcount.setText(String.valueOf(i));
               
                chartHours(); 
                
                
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

    private void mastertableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mastertableMouseClicked
        int row = mastertable.rowAtPoint(evt.getPoint());
        int col = mastertable.columnAtPoint(evt.getPoint());
         // select any field in a row grabs the vendor for that row...so open the possibility of payment for that row/vendor
        String jobnbr = mastertable.getValueAt(row, 1).toString();
        
       
        /*
        if ( col == 14) {
            printticket(mastertable.getValueAt(row, 1).toString(), "Work Order");
        }
        */
    }//GEN-LAST:event_mastertableMouseClicked

    private void ddoperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddoperatorActionPerformed
        if (! isLoad && ddoperator.getSelectedItem() != null ) {
            if (! ddoperator.getSelectedItem().toString().isBlank()) {
                lblempname.setText(getEmpFormalNameByID(ddoperator.getSelectedItem().toString()));
            } else {
                lblempname.setText("");
            }
        }
    }//GEN-LAST:event_ddoperatorActionPerformed

    private void cbchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbchartActionPerformed
        if (! isLoad) {
            if (cbchart.isSelected()) {
                chartHours();
                detailpanel.setVisible(true);
            } else {
                detailpanel.setVisible(false);
            }
        }
    }//GEN-LAST:event_cbchartActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (mastertable != null && mymodel.getRowCount() > 0)
        OVData.exportCSV(mastertable);
    }//GEN-LAST:event_btcsvActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JCheckBox cbchart;
    private javax.swing.JCheckBox cbclosed;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddop;
    private javax.swing.JComboBox<String> ddoperator;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JTextField fromcell;
    private javax.swing.JTextField frompart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelavghours;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqtycomp;
    private javax.swing.JLabel labelqtysched;
    private javax.swing.JLabel lblempname;
    private javax.swing.JLabel lbltothours;
    private javax.swing.JPanel masterpanel;
    private javax.swing.JTable mastertable;
    private javax.swing.JLabel pielabel;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTextField tbjobid;
    private javax.swing.JTextField tocell;
    private javax.swing.JTextField topart;
    // End of variables declaration//GEN-END:variables
}
