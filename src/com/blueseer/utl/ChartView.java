/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.utl;


import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import static javax.swing.text.StyleConstants.Orientation;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.ReportPanel.TableReport;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class ChartView extends javax.swing.JPanel {
 String whichreport = "";
 String chartfilepath = OVData.getSystemTempDirectory() + "/" + "chart.jpg";
    /**
     * Creates new form CCChartView
     */
    public ChartView() {
        initComponents();
    }

     public void initvars(String rpt) {
        ChartPanel.setVisible(false);
        CodePanel.setVisible(false);
        whichreport = rpt;
        
         int year = Calendar.getInstance().get(Calendar.YEAR);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.DAY_OF_YEAR, 1);    
                Date start = cal.getTime();

                //set date to last day of 2014
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, 11); // 11 = december
                cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve

                Date end = cal.getTime();
                
                dcFrom.setDate(start);
                dcTo.setDate(end);
        
        
        
        
        
        ddcode.removeAllItems();
        ArrayList<String> codes = OVData.getClockCodes();
        for (String code : codes) {
            ddcode.addItem(code);
        }
        ddcode.setEnabled(true);
        if (rpt.equals("ClockChartByCode")) {
            ddcode.setEnabled(false);
        }
        if (rpt.equals("ClockChartByWeek")) {
            ddcode.setEnabled(false);
        }
        
        ddcode.setVisible(false);
        
        
    }
     
   
     
     
     class CustomRenderer extends BarRenderer
{

   public CustomRenderer()
   {
   }

   public Paint getItemPaint(final int row, final int column)
   {
       Color mycolor = null;
     CategoryDataset cd = getPlot().getDataset();
    if(cd != null)
    {
      String l_rowKey = (String)cd.getRowKey(row);
      String l_colKey = (String)cd.getColumnKey(column);
      int l_value  = cd.getValue(l_rowKey, l_colKey).intValue();
      
      
   //   if (l_value > 20 )
      if (l_colKey.toString().matches("(.*)Mon(.*)"))
      {
          mycolor = Color.GREEN;
      } else {
          mycolor = Color.RED;
      }
    }
       return mycolor;
       
   }
}
     
     
       public void cleanUpOldChartFile() {
          File f = new File(chartfilepath);
        if(f.exists()) { 
            f.delete();
        }
     }
     
     
     // finance
        public void piechart_expensebyaccount() {
         try {
           
               
             
             
         cleanUpOldChartFile();
            
            
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
               
                res = st.executeQuery("select glh_acct, ac_desc, sum(glh_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ac_type = 'E' " +
                        " group by glh_acct order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("glh_acct") == null || res.getString("glh_acct").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("ac_desc");   
                    }
                    Double amt = res.getDouble("sum");
                   
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Top 10 Expense By Account", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
       
        public void piechart_incomebyaccountcc() {
         try {
           
               
             
             
         cleanUpOldChartFile();
            
            
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
               
                res = st.executeQuery("select glh_acct, glh_cc,  sum(glh_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ac_type = 'I' " +
                        " group by glh_acct, glh_cc order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("glh_acct") == null || res.getString("glh_acct").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("glh_acct") + "-" + res.getString("glh_cc");   
                    }
                    Double amt = -1 * res.getDouble("sum");
                   
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Top 10 Income By Account-ProdLine", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
     // shipments
        public void ShipPerWeekDollarsChart() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }
//itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                  
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                   res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty * shd_netprice) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'myweek' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '-3 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek) as c " +
                                         " left outer join ship_det on strftime('%W',shd_date) = c.myweek " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.myweek;");
                    } else {
                    res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty * shd_netprice) as 'sum' from ( select boo.mydate, week(mydate) as 'myweek' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek) as c " +
                                         " left outer join ship_det on week(shd_date) = c.myweek " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.myweek;");
                    }
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                    int i = 0;
                    int start = 0;
                    int last = 0;
                    int curr = 0;
                    int diff = 0;
                    
                    while (res.next()) {
                     i++;
                     if (i == 1) {
                       last = res.getInt("myweek");
                       curr = res.getInt("myweek");
                     } else {
                       curr = res.getInt("myweek");
                     }
                     
                     diff = curr - last;
                     
                     dol = res.getDouble("sum");         
                     
                     if (diff > 1) {
                         for (int j = 1; j < diff; j++) {
                            dataset.setValue(0, "Sum", String.valueOf(last + 1)); 
                         }
                         dataset.setValue(dol, "Sum", String.valueOf(curr));
                     } else {
                         dataset.setValue(dol, "Sum", String.valueOf(curr));
                     }
                       
                     last = curr;
                    }
                    
                    
                    
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Shipments (Dollars)", "Week Number", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
       
        public void ShipPerWeekUnitsChart() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }
//itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select sum(sod_ord_qty) as 'sum', week(sod_ord_date) as 'myweek' from sod_det " +
                        " inner join so_mstr on so_nbr = sod_nbr " +
                        " where sod_ord_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND sod_ord_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND so_type = 'DISCRETE' " + 
                        " group by week(sod_ord_date) order by myweek asc;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("myweek"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Discrete Order Chart (Units)", "Week Number", "Units", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
     
     // production
        public void ProdByWeekFGUnits() {
        try {
                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }
//itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                     if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                    res = st.executeQuery(" select c.d as 't', sum(tr_qty) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                     } else {
                      res = st.executeQuery(" select c.d as 't', sum(tr_qty) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " +
                        " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                        " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                        " group by c.d;");
                     }
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("t"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly FG Production (Units)", "Week Number", "Units", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Problem with ProdByWeekFGUnits");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
             
        public void ProdByWeekFGDollars() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }
//itr_routing = it_wf and itr_set = 'standard' and itr_site = it_site
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                    
                    res = st.executeQuery(" select c.d as 't', sum(tr_qty * itr_total) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                         " left outer join itemr_cost on itemr_cost.itr_item = tran_mstr.tr_part " +
                                         " and itr_op = tr_op and itr_set = 'standard' and itr_site = tr_site " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                    } else {
                     res = st.executeQuery(" select c.d as 't', sum(tr_qty * itr_total) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " + 
                        " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                         " left outer join itemr_cost on itemr_cost.itr_item = tran_mstr.tr_part " +
                                         " and itr_op = tr_op and itr_set = 'standard' and itr_site = tr_site " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                    }
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly FG Production (Dollars)", "Week Number", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                     MainFrame.bslog(s);
                    bsmf.MainFrame.show("Problem with ProdByWeekFGDollars");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
             
      
     // order
        public void DiscreteOrderPerWeekUnits() {
        try {
                
               cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    ArrayList mylist = OVData.getWeekNbrByFromDateToDate(dfdate.format(dcFrom.getDate()), dfdate.format(dcTo.getDate()));
                    ArrayList newlist = new ArrayList();
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                  
                   int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                   //not sure why -3 days was in there...but changed to 0 days
                   res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '0 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join sod_Det on strftime('%W',sod_ord_date) = c.d " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                    
                    } else {
                    res = st.executeQuery("select sum(sod_ord_qty) as 'sum', week(sod_ord_date) as 't' from sod_det " +
                        " inner join so_mstr on so_nbr = sod_nbr " +
                        " where sod_ord_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND sod_ord_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND so_type = 'DISCRETE' " + 
                        " group by week(sod_ord_date) order by myweek asc;");
                    }
                    while (res.next()) {
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("t"));
                    }
                 
                    
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Discrete Order Chart (Units)", "Week Number", "Units", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("unable to process DiscreteOrderPerWeekUnits");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
             
        public void DiscreteOrderPerWeekDollars() {
        try {

              cleanUpOldChartFile();
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                    res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty * sod_netprice) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '0 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join sod_det on strftime('%W',sod_ord_date) = c.d " +
                                         " inner join so_mstr on so_mstr.so_nbr = sod_det.sod_nbr and so_type = 'DISCRETE' " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                    } else {
                    res = st.executeQuery("select sum(sod_ord_qty * sod_netprice) as 'sum', week(sod_ord_date) as 'myweek' from sod_det " +
                        " inner join so_mstr on so_nbr = sod_nbr " +
                        " where sod_ord_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND sod_ord_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND so_type = 'DISCRETE' " + 
                        " group by week(sod_ord_date) order by myweek asc;");    
                    }

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Discrete Order Volume (Dollars)", "Week Number", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("unable to process DiscreteOrderPerWeekDollars");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }  
        
    
     // sales
        public void piechart_salesbycust() {
         try {
           
               
             
             
         cleanUpOldChartFile();
            
            
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
                res = st.executeQuery("select ar_cust, sum(ar_amt) as 'sum' from ar_mstr " +
                        " where ar_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND ar_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ar_type = 'I' " +
                        " group by ar_cust order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("ar_cust") == null || res.getString("ar_cust").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("ar_cust");   
                    }
                    Double amt = res.getDouble("sum");
                   
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Sales Per Customer YTD", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      
        public void piechart_custAR() {
         try {
           
               
             
          cleanUpOldChartFile();
            
            
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                DecimalFormat df = new DecimalFormat("$ #,##0.00");
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
                res = st.executeQuery("select ar_cust, sum(ar_amt) as 'sum' from ar_mstr " +
                    " where ar_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND ar_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ar_type = 'I' " +
                        " AND ar_status = 'o' " + 
                        " group by ar_cust order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                double tot = 0.00;
                while (res.next()) {
                    if (res.getString("ar_cust") == null || res.getString("ar_cust").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("ar_cust");   
                    }
                    Double amt = res.getDouble("sum");
                    tot += amt;
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Accounts Receivable By Customer " + String.valueOf(df.format(tot)), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
        
       // Clock 
        public void ClockChartByDept() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select dept, sum(tothrs) as 'sum' from time_clock " + 
                        " where indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND code_id = " + "'" + ddcode.getSelectedItem().toString() + "'" +  
                        " group by dept order by dept  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("dept"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Hours By Dept", "Dept", "Hours", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
        public void ClockChartByCode() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select code_id, sum(tothrs) as 'sum' from time_clock " +
                        " where indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by code_id order by code_id  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("code_id"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Hours By Code", "Code", "Hours", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
        public void ClockChartByEmp() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select emp_nbr, sum(tothrs) as 'sum' from time_clock " +
                        " where indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND code_id = " + "'" + ddcode.getSelectedItem().toString() + "'" +  
                        " group by emp_nbr order by emp_nbr  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("emp_nbr"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Hours By Employee", "Employee Nbr", "Hours", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
        
        public void HoursPerWeek() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select sum(tothrs) as 'sum', week(indate) as 'myweek' from time_clock " +
                        " where indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by week(indate) order by myweek asc;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("myweek"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Hours", "Week Number", "Hours", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("unable to get HoursPerWeek");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
        
        
        // Requisition charts
        public void ReqDollarsByAcct() {
          try {
            
          cleanUpOldChartFile();
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
                res = st.executeQuery("select req_acct, sum(req_amt) as 'sum' from req_mstr " +
                        " where req_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND req_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by req_acct order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("req_acct") == null || res.getString("req_acct").isEmpty()) {
                      acct = "MRO";
                    } else {
                      acct = res.getString("req_acct");   
                    }
                    Double amt = res.getDouble("sum");
                   
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Top 10 Accounts By Spending", dataset, true, true, false);
         
                   PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     
        public void ReqDollarsByDept() {
          try {
            
        cleanUpOldChartFile();
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
                res = st.executeQuery("select req_dept, sum(req_amt) as 'sum' from req_mstr " +
                        " where req_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND req_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by req_dept order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("req_dept") == null || res.getString("req_dept").isEmpty()) {
                      acct = "Unknown";
                    } else {
                      acct = res.getString("req_dept");   
                    }
                    Double amt = res.getDouble("sum");
                   
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Top 10 Departments By Spending", dataset, true, true, false);
         
                   PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     
        public void ReqDollarsByUser() {
          try {
            
     cleanUpOldChartFile();
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
                res = st.executeQuery("select req_name, sum(req_amt) as 'sum' from req_mstr " +
                        " where req_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND req_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by req_name order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String name = "";
                while (res.next()) {
                    if (res.getString("req_name") == null || res.getString("req_name").isEmpty()) {
                      name = "Unknown";
                    } else {
                      name = res.getString("req_name");   
                    }
                    Double amt = res.getDouble("sum");
                   
                  dataset.setValue(name, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Top 10 Users By Spending", dataset, true, true, false);
         
                   PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        ChartPanel.setVisible(true);
        this.repaint();
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     
        
        // Scrap charts
        public void ScrapPerWeek() {
        try {

                File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                     if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                    res = st.executeQuery(" select c.d as 't', sum(tr_qty * tr_cost) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'ISS-SCRAP' " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                     } else {
                    res = st.executeQuery(" select c.d as 't', sum(tr_qty * tr_cost) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                         " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'ISS-SCRAP' " +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.d;");
                     }
                    
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Weekly Scrap Cost", "Week Number", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Unable to run ScrapPerWeek");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
         
        public void CodesAccumQty() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_ref, sum(tr_qty) as 'sum' from tran_mstr " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_ref order by tr_ref  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                       
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_ref"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Qty By Code", "Code", "Qty", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
        public void PartAccumQty() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_part, sum(tr_qty) as 'sum' from tran_mstr " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_part order by sum desc limit 20 ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                       
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_part"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Qty By Part (Top 20)", "Part", "Qty", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
        
        public void RWPartAccumQty() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_part, sum(tr_qty) as 'sum' from tran_mstr " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-REWK' " + 
                        " group by tr_part order by sum desc limit 20 ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                       
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_part"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Rework Qty By Part (Top 20)", "Part", "Qty", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
        
        public void PartAccumDollar() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_part, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                             " inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_part order by sum desc limit 20 ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_part"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Dollars By Part (Top 20)", "Part", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
          
        public void CodesAccumDollar() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_ref, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                             " inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_ref order by sum desc limit 20 ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_ref"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Dollars By Code (Top 20)", "Scrap Code", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
         
        public void DeptAccumDollar() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);
                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_actcell, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                             " inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_actcell order by sum desc limit 20 ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                   //    qty = qty + res.getInt("tr_qty");
                  //  dol = dol + (res.getInt("tr_qty") * res.getDouble("itr_total"));
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_actcell"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Dollars By Dept (Top 20)", "Dept (Cell)", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
        
        public void DeptAccumQty() {
        try {

              cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select tr_actcell, sum(tr_qty) as 'sum' from tran_mstr " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                            " AND tr_type = 'ISS-SCRAP' " + 
                        " group by tr_actcell order by tr_actcell  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                       
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_actcell"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Qty By Dept" + "\n" +"from " + 
                            dfdate.format(dcFrom.getDate()).toString() + " thru " +
                            dfdate.format(dcTo.getDate()).toString(), "Dept", "Qty", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
        // misc
        public void avgcallsperday() {
        try {

                cleanUpOldChartFile();

                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url + db, user, pass);

                try {
                    Statement st = con.createStatement();
                    ResultSet res = null;
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    res = st.executeQuery("select DayOfWeek(call_date) as dayofweek, (count(*)/count(distinct(weekday(call_date)))) as count from call_mstr " +
                        " where call_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND call_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " group by DayOfWeek(call_date)  ;");

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                        //String day = new SimpleDateFormat("EE").format(res.getDate("DayOfWeek"));
                        String day = res.getString("DayofWeek");
                        if (day.equals("1")) 
                            day = "Sun";
                        if (day.equals("2")) 
                            day = "Mon";
                        if (day.equals("3")) 
                            day = "Tue";
                        if (day.equals("4")) 
                            day = "Wed";
                        if (day.equals("5")) 
                            day = "Thu";
                        if (day.equals("6")) 
                            day = "Fri";
                        if (day.equals("7")) 
                            day = "Sat";
                        
                        
                        
                        
                        dataset.setValue(res.getDouble("count"), "Count", day);
                    }
                    JFreeChart chart = ChartFactory.createBarChart("Average Calls Per Day", "DayOfWeek", "Avg Calls", dataset, PlotOrientation.VERTICAL, true, true, false);
                                        
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                   
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(chartfilepath);
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    ChartPanel.setVisible(true);
                    this.repaint();

                    // bsmf.MainFrame.show("your chart is complete...go to chartview");

                } catch (SQLException s) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            } 
     }
     
       
     
      class MyPrintable implements Printable {
  ImageIcon printImage = new javax.swing.ImageIcon(chartfilepath);

  
  
  public int print(Graphics g, PageFormat pf, int pageIndex) {
    Graphics2D g2d = (Graphics2D) g;
   
    g.translate((int) (pf.getImageableX()), (int) (pf.getImageableY()));
    if (pageIndex == 0) {
    
        double pageWidth = pf.getImageableWidth();
      double pageHeight = pf.getImageableHeight();
      double imageWidth = printImage.getIconWidth();
      double imageHeight = printImage.getIconHeight();
      double scaleX = pageWidth / imageWidth;
      double scaleY = pageHeight / imageHeight;
      double scaleFactor = Math.min(scaleX, scaleY);
      g2d.scale(scaleFactor, scaleFactor);
    // pf.setOrientation(PageFormat.LANDSCAPE);
        g.drawImage(printImage.getImage(), 0, 0, null);
      return Printable.PAGE_EXISTS;
    }
    return Printable.NO_SUCH_PAGE;
  }
}
      
      
        public class ImagePrintable implements Printable {

        private double          x, y, width;

        private int             orientation;

        private BufferedImage   image;

        public ImagePrintable(PrinterJob printJob, BufferedImage image) {
            PageFormat pageFormat = printJob.defaultPage();
            this.x = pageFormat.getImageableX();
            this.y = pageFormat.getImageableY();
            this.width = pageFormat.getImageableWidth();
            this.orientation = pageFormat.getOrientation();
            this.image = image;
            
        }

        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
            if (pageIndex == 0) {
                int pWidth = 0;
                int pHeight = 0;
                if (orientation == PageFormat.PORTRAIT) {
                    pWidth = (int) Math.min(width, (double) image.getWidth());
                    pHeight = pWidth * image.getHeight() / image.getWidth();
                } else {
                    pHeight = (int) Math.min(width, (double) image.getHeight());
                    pWidth = pHeight * image.getWidth() / image.getHeight();
                }
                pWidth = 600;
                pHeight = 400;
                g.drawImage(image, (int) x, (int) y, pWidth, pHeight, null);
                return PAGE_EXISTS;
            } else {
                return NO_SUCH_PAGE;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btChart = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        ddcode = new javax.swing.JComboBox();
        ChartPanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        CodePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tacodes = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setText("From Date");

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");

        btChart.setText("Chart");
        btChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChartActionPerformed(evt);
            }
        });

        btprint.setText("Print");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(29, 29, 29)
                .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btChart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btChart)
                        .addComponent(btprint)
                        .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        javax.swing.GroupLayout ChartPanelLayout = new javax.swing.GroupLayout(ChartPanel);
        ChartPanel.setLayout(ChartPanelLayout);
        ChartPanelLayout.setHorizontalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChartPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chartlabel))
        );
        ChartPanelLayout.setVerticalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartlabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tacodes.setColumns(20);
        tacodes.setRows(5);
        jScrollPane2.setViewportView(tacodes);

        javax.swing.GroupLayout CodePanelLayout = new javax.swing.GroupLayout(CodePanel);
        CodePanel.setLayout(CodePanelLayout);
        CodePanelLayout.setHorizontalGroup(
            CodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        CodePanelLayout.setVerticalGroup(
            CodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CodePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CodePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChartActionPerformed
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        if (dcTo.getDate() == null || dcFrom.getDate() == null) {
                bsmf.MainFrame.show("Must choose a date for both From and To");
                return;
            }
        
         int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
        
        if (whichreport.equals("")) {
            bsmf.MainFrame.show("whichreport is empty");
        }
        
       
         if (whichreport.equals("ReqAmtByAcctChart")) {
            ReqDollarsByAcct();
        }
        
        if (whichreport.equals("ReqAmtByDeptChart")) {
            ReqDollarsByDept();
        }
        
        if (whichreport.equals("ReqFreqByUserChart")) {
            ReqDollarsByUser();
        }
        
        
         if (whichreport.equals("ShipPerWeekUnitsChart")) {
            ShipPerWeekUnitsChart();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("ShipPerWeekDollarsChart")) {
            ShipPerWeekDollarsChart();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
          if (whichreport.equals("ProdByWeekFGUnits")) {
            
            ProdByWeekFGUnits();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("ProdByWeekFGDollars")) {
            
            ProdByWeekFGDollars();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
       
        
        if (whichreport.equals("DiscreteOrderPerWeekUnits")) {
            DiscreteOrderPerWeekUnits();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("DiscreteOrderPerWeekDollars")) {
            DiscreteOrderPerWeekDollars();
            tacodes.setText("");
            ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < weekcodes.size(); i++) {
              str += (weekcodes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
       
        
       
        
        
        if (whichreport.equals("RWPartAccumQty")) {
            RWPartAccumQty();
        }
        
        if (whichreport.equals("piechart_salesbycust")) {
            piechart_salesbycust();
        }
        
        if (whichreport.equals("piechart_expensebyaccount")) {
            piechart_expensebyaccount();
        }
        if (whichreport.equals("piechart_incomebyaccountcc")) {
            piechart_incomebyaccountcc();
        }
        
        if (whichreport.equals("piechart_custAR")) {
            piechart_custAR();
        }
        
         if (whichreport.equals("ClockChartByDept")) {
            ClockChartByDept();
            tacodes.setText("");
            ArrayList codes = OVData.getClockCodesAndDesc();
            String str = "";
            
            for (int i = 0; i < codes.size(); i++) {
            String[] element = codes.get(i).toString().split(",");
                str += (element[0] + " = " + element[1]) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("ClockChartByCode")) {
            ClockChartByCode();
            tacodes.setText("");
            ArrayList codes = OVData.getClockCodesAndDesc();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
            String[] element = codes.get(i).toString().split(",");
                str += (element[0] + " = " + element[1]) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
         if (whichreport.equals("ClockChartByEmp")) {
            ClockChartByEmp();
            tacodes.setText("");
            ArrayList codes = OVData.getEmployeeIDAndName();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
            String[] element = codes.get(i).toString().split(",");
                str += (element[0] + " = " + element[1]) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        
       if (whichreport.equals("ClockChartByWeek")) {
            HoursPerWeek();
            tacodes.setText("");
           // ArrayList codes = OVData.getWeekNbrByDateTimeClock(dfdate.format(dcFrom.getDate()));
            String str = "";
           // for (int i = 0; i < codes.size(); i++) {
        //     str += (codes.get(i)) + "\n";
         //   }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
   
        
       
        
        if (whichreport.equals("ScrapPerWeek")) {
            ScrapPerWeek();
            tacodes.setText("");
            ArrayList codes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
         if (whichreport.equals("ScrapChartQtyByCode")) {
            CodesAccumQty();
            tacodes.setText("");
            ArrayList codes = OVData.getCodeAndDescMstr("Scrap");
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
         
          if (whichreport.equals("ScrapChartDolByCode")) {
            CodesAccumDollar();
            tacodes.setText("");
            ArrayList codes = OVData.getCodeAndDescMstr("Scrap");
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } 
        
        if (whichreport.equals("ScrapChartQtyByPart")) {
            PartAccumQty();
        }
        
       
        
        if (whichreport.equals("ScrapChartDolByPart")) {
            PartAccumDollar();
        }
        
        if (whichreport.equals("ScrapChartQtyByDept")) {
            DeptAccumQty();
        
            tacodes.setText("");
            ArrayList codes = OVData.getdeptanddesclist();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
        
            CodePanel.setVisible(true);
        } 
        if (whichreport.equals("ScrapChartDolByDept")) {
            DeptAccumDollar();
        
            tacodes.setText("");
            ArrayList codes = OVData.getdeptanddesclist();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
        
            CodePanel.setVisible(true);
        } 
        
        
    }//GEN-LAST:event_btChartActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
   /*      
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    DocPrintJob job = service.createPrintJob();
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    SimpleDoc doc = new SimpleDoc(new MyPrintable(), flavor, null);
    try {
         job.print(doc, null);
     } catch (PrintException ex) {
         Logger.getLogger(ScrapChartView.class.getName()).log(Level.SEVERE, null, ex);
     }
     */
        
        BufferedImage image = null;
        try {
        image = ImageIO.read(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"));
        } catch (IOException e) {
        }
        
          PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new ImagePrintable(printJob, image));
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(OrientationRequested.LANDSCAPE);
        if (printJob.printDialog()) {
            try {
                
                printJob.print(aset);
            } catch (PrinterException prt) {
                prt.printStackTrace();
            }
        }
        
        
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChartPanel;
    private javax.swing.JPanel CodePanel;
    private javax.swing.JButton btChart;
    private javax.swing.JButton btprint;
    private javax.swing.JLabel chartlabel;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddcode;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea tacodes;
    // End of variables declaration//GEN-END:variables
}
