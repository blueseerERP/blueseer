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


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import static javax.swing.text.StyleConstants.Orientation;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleWithSymbol;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import static com.blueseer.utl.ReportPanel.TableReport;
import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
public class ChartMain extends javax.swing.JPanel {
 String whichreport = "";
 String curr = OVData.getDefaultCurrency();
 Currency currency = Currency.getInstance(Locale.getDefault());
 String symbol = currency.getSymbol(Locale.getDefault());
 String chartfilepath = OVData.getSystemTempDirectory() + "/" + "chart.jpg";
 ArrayList<String> ml = new ArrayList<String>();
 
 BufferedImage myimage = null;
 
    /**
     * Creates new form CCChartView
     */
    public ChartMain() {
        initComponents();
    }

    public void initvars(String[] rpt) {
        setLanguageTags(this);
        ChartPanel.setVisible(false);
        CodePanel.setVisible(false);
        mainPanel.setBorder(BorderFactory.createTitledBorder(rpt[0]));
        
        ml.clear();
        
        if (rpt[0].equals("chart_scrap")) {
        ml.add("Scrap -- per week");
        ml.add("Scrap -- quantity by code");
        ml.add("Scrap -- dollars by code");
        ml.add("Scrap -- quantity by item");
        ml.add("Scrap -- dollars by item");
        ml.add("Scrap -- quantity by department");
        ml.add("Scrap -- dollars by department");
        }
        
        if (rpt[0].equals("chart_clock")) {
        ml.add("Clock -- by department");
        ml.add("Clock -- by code");
        ml.add("Clock -- by employee");
        ml.add("Clock -- by week");        
        }
        
        if (rpt[0].equals("chart_requisition")) {
        ml.add("Requisition -- amount by account");
        ml.add("Requisition -- amount by department");
        ml.add("Requisition -- frequency per userid");
        }
        
        if (rpt[0].equals("chart_shipping")) {
        ml.add("Shipping -- units per week");
        ml.add("Shipping -- dollars per week");
        }
        
        if (rpt[0].equals("chart_production")) {
        ml.add("Production -- total units per week");
        ml.add("Production -- total cost per week");
        }
        if (rpt[0].equals("chart_sales")) {
        ml.add("Sales -- total sales by date");
        ml.add("Sales -- current accounts receivable");
        }
        
        if (rpt[0].equals("chart_finance")) {
        ml.add("Finance -- income versus expense");
        ml.add("Finance -- expense by account");
        ml.add("Finance -- income by account");
        }
        if (rpt[0].equals("chart_order")) {
        ml.add("Order -- open orders");
        ml.add("Order -- orders per week total units");
        ml.add("Order -- orders per week total dollars");
        }
        if (rpt[0].equals("ChartMain")) {
        ml.add("Clock -- by department");
        ml.add("Clock -- by code");
        ml.add("Clock -- by employee");
        ml.add("Clock -- by week");   
        ml.add("Finance -- income versus expense");
        ml.add("Finance -- expense by account");
        ml.add("Finance -- income by account");
        ml.add("Order -- open orders");
        ml.add("Order -- orders per week total units");
        ml.add("Order -- orders per week total dollars");
        ml.add("Production -- total units per week");
        ml.add("Production -- total cost per week");
        ml.add("Requisition -- amount by account");
        ml.add("Requisition -- amount by department");
        ml.add("Requisition -- frequency per userid");
        ml.add("Sales -- total sales by date");
        ml.add("Sales -- current accounts receivable");
        ml.add("Scrap -- per week");
        ml.add("Scrap -- quantity by code");
        ml.add("Scrap -- dollars by code");
        ml.add("Scrap -- quantity by item");
        ml.add("Scrap -- dollars by item");
        ml.add("Scrap -- quantity by department");
        ml.add("Scrap -- dollars by department");
        ml.add("Shipping -- units per week");
        ml.add("Shipping -- dollars per week");
        
        
        }
        
        ddreport.removeAllItems();
        for (String key : ml) {
         ddreport.addItem(key);
        }
        
        
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
        myimage = null;
        ChartPanel.setVisible(false);
        CodePanel.setVisible(false);
        chartlabel.setIcon(null);
        /*
        File f = new File(chartfilepath);
        if(f.exists()) { 
            f.delete();
        }
        */
     }
     
     
     // finance
    public void piechart_expensebyaccount() {
         try {
         cleanUpOldChartFile();
         ChartPanel.setVisible(true);
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
                res = st.executeQuery("select glh_acct, ac_desc, sum(glh_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ac_type = 'E' " +
                        " group by glh_acct, ac_desc order by sum desc;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                double total = 0.00;
                double displayed = 0.00;
                int i = 0;
                while (res.next()) {
                    i++;
                    if (res.getString("glh_acct") == null || res.getString("glh_acct").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("ac_desc");   
                    }
                    total += res.getDouble("sum");
                    Double amt = res.getDouble("sum");
                    if (i <= Integer.valueOf(ddlimit.getSelectedItem().toString())) {
                       displayed += res.getDouble("sum"); 
                       dataset.setValue(acct, amt);
                    }
                }
                // other
                if (total > displayed) {
                    dataset.setValue("other", (total - displayed));
                }
                JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5000), dataset, true, true, false);
                PiePlot plot = (PiePlot) chart.getPlot();
                PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
                plot.setLabelGenerator(gen);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
       
                } catch (SQLException s) {
                  MainFrame.bslog(s);
                } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
    public void piechart_incomebyaccountcc() {
         try {
         cleanUpOldChartFile();
         ChartPanel.setVisible(true);
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
                res = st.executeQuery("select glh_acct, glh_cc,  sum(glh_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND ac_type = 'I' " +
                        " group by glh_acct, glh_cc order by sum desc;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                double total = 0.00;
                double abssum = 0.00;
                double displayed = 0.00;
                int i = 0;
                while (res.next()) {
                    i++;
                    if (res.getDouble("sum") < 0) {
                        abssum = -1 * res.getDouble("sum");
                    } else {
                        abssum = res.getDouble("sum");
                    }
                    
                    if (res.getString("glh_acct") == null || res.getString("glh_acct").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("glh_acct") + "-" + res.getString("glh_cc");   
                    }
                    total += abssum;
                    Double amt = abssum;
                    if (i <= Integer.valueOf(ddlimit.getSelectedItem().toString())) {
                       displayed += abssum; 
                       dataset.setValue(acct, amt);
                    }
                }
                
                // other
                if (total > displayed) {
                    dataset.setValue("other", (total - displayed));
                }
                
                JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5001), dataset, true, true, false);
                PiePlot plot = (PiePlot) chart.getPlot();
               PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
                plot.setLabelGenerator(gen);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }

              } catch (SQLException s) {
                  MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void piechart_profitandloss() {
        
        try {
         cleanUpOldChartFile();
         ChartPanel.setVisible(true);
            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
                res = st.executeQuery("select ac_type, sum(glh_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                        " AND (ac_type = 'E' or ac_type = 'I') " +
                        " group by ac_type order by sum desc limit 10  ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String type = "";
                double amt = 0.00;
                while (res.next()) {
                    if (res.getString("ac_type").equals("E")) {
                        type = "Expense";
                    } else {
                        type = "Income";
                    }
                    if (res.getDouble("sum") < 0.00) {
                    amt = (-1 * res.getDouble("sum"));    
                    } else {
                    amt = res.getDouble("sum");    
                    }
                    dataset.setValue(type, amt);
                }
                JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5026), dataset, true, true, false);
                PiePlot plot = (PiePlot) chart.getPlot();
                PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
                plot.setLabelGenerator(gen);

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
       
                } catch (SQLException s) {
                  MainFrame.bslog(s);
                } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
         
    
     // shipments
    public void ShipPerWeekDollarsChart() {
        
       
        try {
                cleanUpOldChartFile();
                ChartPanel.setVisible(true);
                CodePanel.setVisible(true);
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                ResultSet res = null;
                int qty = 0;
                double dol = 0;
                try {
                    
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                  
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                   res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty * shd_netprice) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'myweek' " +
                                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                         ", '-3 days', '+' || mock_nbr || ' days') as mydate " +
                                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek) as c " +
                                         " left outer join ship_det on strftime('%W',shd_date) = c.myweek " +
                                         " and shd_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                         " and shd_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.myweek;");
                    } else {
                    res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty * shd_netprice) as 'sum' from ( select boo.mydate, week(mydate) as 'myweek' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek, boo.mydate) as c " +
                        " left outer join ship_det on week(shd_date) = c.myweek " +
                        " and shd_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        " and shd_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
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
                    
                    JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5002), getGlobalColumnTag("week"), getGlobalColumnTag("totalsales") + " " + symbol, dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
     }
       
    public void ShipPerWeekUnitsChart() {
        
        try {
                cleanUpOldChartFile();
                ChartPanel.setVisible(true);
                CodePanel.setVisible(true);
                Connection con = null;
                if (ds != null) {
                  con = ds.getConnection();
                } else {
                  con = DriverManager.getConnection(url + db, user, pass);  
                }
                Statement st = con.createStatement();
                ResultSet res = null;
                int qty = 0;
                double dol = 0;
                try {
                    DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                    int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                    
                    if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                      res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'myweek' " +
                         " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                         ", '-3 days', '+' || mock_nbr || ' days') as mydate " +
                         " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek) as c " +
                         " left outer join ship_det on strftime('%W',shd_date) = c.myweek " +
                         " and shd_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                         " and shd_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                         //" where mock_nbr <= 10 " +
                         " group by c.myweek;");   
                    } else {
                     res = st.executeQuery(" select c.myweek as 'myweek', sum(shd_qty) as 'sum' from ( select boo.mydate, week(mydate) as 'myweek' " +
                        " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                        ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by myweek, boo.mydate) as c " +
                         " left outer join ship_det on week(shd_date) = c.myweek " +
                         " and shd_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                         " and shd_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                         //" where mock_nbr <= 10 " +
                                         " group by c.myweek;"); 
                    }
                    

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                    while (res.next()) {
                        dataset.setValue(res.getDouble("sum"), "Sum", res.getString("myweek"));
                    }
                    JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5003), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }

                } catch (SQLException s) {
                    MainFrame.bslog(s);
                } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
     }
     
     
     // production
    public void ProdByWeekFGUnits() {
      
    try {
            cleanUpOldChartFile();
            ChartPanel.setVisible(true);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                res = st.executeQuery(" select c.d as 't', sum(tr_qty) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                     " and tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " group by c.d;");
                 } else {
                  res = st.executeQuery(" select c.d as 't', sum(tr_qty) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " +
                    " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                    " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                    " and tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " and tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +        
                    " group by c.d;");
                 }
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                   dataset.setValue(res.getDouble("sum"), "Sum", res.getString("t"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5004), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryItemRenderer renderer = new CustomRenderer();

                Font font = new Font("Dialog", Font.PLAIN, 30);
                CategoryPlot p = chart.getCategoryPlot();

                CategoryAxis axis = p.getDomainAxis();
                 ValueAxis axisv = p.getRangeAxis();
                 axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                 axisv.setVerticalTickLabels(false);

                 p.setRenderer(renderer);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void ProdByWeekFGDollars() {
    
        try {

            cleanUpOldChartFile();
            ChartPanel.setVisible(true);
            CodePanel.setVisible(true);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {

                res = st.executeQuery(" select c.d as 't', sum(tr_qty * itr_total) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                     " and tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " left outer join itemr_cost on itemr_cost.itr_item = tran_mstr.tr_item " +
                                     " and itr_op = tr_op and itr_set = 'standard' and itr_site = tr_site " +
                                     //" where mock_nbr <= 10 " +
                                     " group by c.d;");
                } else {
                 res = st.executeQuery(" select c.d as 't', sum(tr_qty * itr_total) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " + 
                    " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                                     " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'RCT-FG' " +
                                     " and tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " left outer join itemr_cost on itemr_cost.itr_item = tran_mstr.tr_item " +
                                     " and itr_op = tr_op and itr_set = 'standard' and itr_site = tr_site " +
                                     //" where mock_nbr <= 10 " +
                                     " group by c.d;");
                }
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                     dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5005), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryItemRenderer renderer = new CustomRenderer();

                Font font = new Font("Dialog", Font.PLAIN, 30);
                CategoryPlot p = chart.getCategoryPlot();

                CategoryAxis axis = p.getDomainAxis();
                 ValueAxis axisv = p.getRangeAxis();
                 axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                 axisv.setVerticalTickLabels(false);

                 p.setRenderer(renderer);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
            } catch (SQLException s) {
                 MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

      
     // order
    public void DiscreteOrderPerWeekUnits() {
    
    try {

            cleanUpOldChartFile();
            ChartPanel.setVisible(true);
            CodePanel.setVisible(true);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
              DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            try {
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                ArrayList mylist = OVData.getWeekNbrByFromDateToDate(dfdate.format(dcFrom.getDate()), dfdate.format(dcTo.getDate()));
                ArrayList newlist = new ArrayList();
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {

               //not sure why -3 days was in there...but changed to 0 days
               res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '0 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join sod_Det on strftime('%W',sod_due_date) = c.d " +
                                     " and sod_due_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and sod_due_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " group by c.d;");

                } else {
                    res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                                     " left outer join sod_det on week(sod_due_date) = c.d  " +
                                     " and sod_due_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and sod_due_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " inner join so_mstr on so_nbr = sod_nbr and so_type = 'DISCRETE' " +        
                                     " group by c.d;"); 
                }
                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("t"));
                }


                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5006), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryItemRenderer renderer = new CustomRenderer();

                Font font = new Font("Dialog", Font.PLAIN, 30);
                CategoryPlot p = chart.getCategoryPlot();

                CategoryAxis axis = p.getDomainAxis();
                 ValueAxis axisv = p.getRangeAxis();
                 axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                 axisv.setVerticalTickLabels(false);

                 p.setRenderer(renderer);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void DiscreteOrderPerWeekDollars() {
    
        try {

            cleanUpOldChartFile();
            ChartPanel.setVisible(true);
            CodePanel.setVisible(true);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty * sod_netprice) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '0 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join sod_det on strftime('%W',sod_due_date) = c.d " +
                                     " and sod_due_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and sod_due_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " left outer join so_mstr on so_mstr.so_nbr = sod_det.sod_nbr and so_type = 'DISCRETE' " +
                                     //" where mock_nbr <= 10 " +
                                     " group by c.d;");
                } else {
                res = st.executeQuery(" select c.d as 't', sum(sod_ord_qty * sod_netprice) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                     " left outer join sod_det on week(sod_due_date) = c.d  " +
                     " and sod_due_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                     " and sod_due_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                     " inner join so_mstr on so_nbr = sod_nbr and so_type = 'DISCRETE' " +        
                     " group by c.d;");
                }

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5007), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
                CategoryItemRenderer renderer = new CustomRenderer();

                Font font = new Font("Dialog", Font.PLAIN, 30);
                CategoryPlot p = chart.getCategoryPlot();

                CategoryAxis axis = p.getDomainAxis();
                 ValueAxis axisv = p.getRangeAxis();
                 axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                 axisv.setVerticalTickLabels(false);

                 p.setRenderer(renderer);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, ChartPanel.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }  

    public void pcOpenOrdersByCust() {
       
    try {
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;

        try {
            
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
            res = st.executeQuery("select so_cust, cm_name, sum( (sod_ord_qty - sod_shipped_qty) * sod_netprice) as 'sum' from so_mstr " +
                    " inner join sod_det on sod_nbr = so_nbr " +
                    " inner join cm_mstr on cm_code = so_cust " +
                    " where so_due_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND so_due_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " group by so_cust order by sum desc;");

            DefaultPieDataset dataset = new DefaultPieDataset();

            String acct = "";
            double total = 0.00;
            double displayed = 0.00;
            int i = 0;
            while (res.next()) {
                i++;
                if (res.getString("so_cust") == null || res.getString("so_cust").isEmpty()) {
                  acct = "Unassigned";
                } else {
                  acct = res.getString("cm_name");   
                }
                total += res.getDouble("sum");
                Double amt = res.getDouble("sum");
                if (i <= Integer.valueOf(ddlimit.getSelectedItem().toString())) {
                   displayed += res.getDouble("sum"); 
                   dataset.setValue(acct, amt);
                }
            }
            
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
            
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5008) + " -- Total: " + currformatDoubleWithSymbol(total,curr), dataset, true, true, false);
    PiePlot plot = (PiePlot) chart.getPlot();
    PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);
            try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            myimage = ImageIO.read(bais);
            ImageIcon myicon = new ImageIcon(myimage);
            myicon.getImage().flush();   
            chartlabel.setIcon(myicon);
            bais.close();
            baos.close();
            } catch (IOException e) {
            MainFrame.bslog(e);
            }
          } catch (SQLException s) {
           MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
}

    
     // sales
    public void piechart_salesbycust() {
        
     try {
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;

        try {
            
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
            res = st.executeQuery("select ar_cust, cm_name, sum(ar_amt) as 'sum' from ar_mstr " +
                    " inner join cm_mstr on cm_code = ar_cust " +
                    " where ar_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND ar_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND ar_type = 'I' " +
                    " group by ar_cust order by sum desc;");

            DefaultPieDataset dataset = new DefaultPieDataset();

            String acct = "";
            double total = 0.00;
            double displayed = 0.00;
            int i = 0;
            while (res.next()) {
                i++;
                if (res.getString("ar_cust") == null || res.getString("ar_cust").isEmpty()) {
                  acct = "Unassigned";
                } else {
                  acct = res.getString("cm_name");   
                }
                total += res.getDouble("sum");
                Double amt = res.getDouble("sum");
                if (i <= Integer.valueOf(ddlimit.getSelectedItem().toString())) {
                   displayed += res.getDouble("sum"); 
                   dataset.setValue(acct, amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5009) + " -- Total: " + currformatDoubleWithSymbol(total,curr), dataset, true, true, false);
    PiePlot plot = (PiePlot) chart.getPlot();

    PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);

    
    
    try {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    myimage = ImageIO.read(bais);
    ImageIcon myicon = new ImageIcon(myimage);
    myicon.getImage().flush();   
    chartlabel.setIcon(myicon);
    bais.close();
    baos.close();
    } catch (IOException e) {
    MainFrame.bslog(e);
    }
    
    
    
    this.repaint();
          } catch (SQLException s) {
           MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
}

    public void piechart_custAR() {
     try {
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;

        try {
            
            DecimalFormat df = new DecimalFormat("$ #,##0.00", new DecimalFormatSymbols(Locale.getDefault()));
             DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");            
            res = st.executeQuery("select ar_cust, cm_name, sum(ar_amt) as 'sum' from ar_mstr " +
                " inner join cm_mstr on cm_code = ar_cust " +    
                " where ar_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND ar_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND ar_type = 'I' " +
                    " AND ar_status = 'o' " + 
                    " group by ar_cust order by sum desc ;");

            DefaultPieDataset dataset = new DefaultPieDataset();

            String acct = "";
            double total = 0.00;
            double displayed = 0.00;
            int i = 0;
            while (res.next()) {
                i++;
                if (res.getString("ar_cust") == null || res.getString("ar_cust").isEmpty()) {
                  acct = "Unassigned";
                } else {
                  acct = res.getString("cm_name");   
                }
                total += res.getDouble("sum");
                Double amt = res.getDouble("sum");
                if (i <= Integer.valueOf(ddlimit.getSelectedItem().toString())) {
                   displayed += res.getDouble("sum"); 
                   dataset.setValue(acct, amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5009) + " -- Total: " + currformatDoubleWithSymbol(total,curr), dataset, true, true, false);
    PiePlot plot = (PiePlot) chart.getPlot();
   PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);

                    try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
          } catch (SQLException s) {
            MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
}  
        
       // Clock 
    public void ClockChartByDept() {
    try {
        cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
               
                   res = st.executeQuery("select dept_id as 'dept', sum(tothrs) as 'sum' from dept_mstr " + 
                    " left outer join time_clock on dept = dept_id " +    
                    " and indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " group by dept_id order by dept_id  ;"); 
               
                

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("dept"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5011), getGlobalColumnTag("dept"), getGlobalColumnTag("hours"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void ClockChartByCode() {
    try {
        cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select clc_code, sum(tothrs) as 'sum' from clock_code " +
                    " left outer join time_clock on code_id = clc_code " +    
                    " and indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " group by clc_code order by clc_code  ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("clc_code"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5012), getGlobalColumnTag("code"), getGlobalColumnTag("hours"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
               MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void ClockChartByEmp() {
    try { 
        cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select emp_mstr.emp_nbr as 'emp_nbr', sum(tothrs) as 'sum' from emp_mstr " +
                    " left outer join time_clock on time_clock.emp_nbr = emp_mstr.emp_nbr " +    
                    " and indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " group by emp_mstr.emp_nbr order by emp_mstr.emp_nbr  ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("emp_nbr"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5013), getGlobalColumnTag("empid"), getGlobalColumnTag("hours"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void HoursPerWeek() {
    try {
        cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            DecimalFormat df = new DecimalFormat("###,###,###.##", new DecimalFormatSymbols(Locale.getDefault()));
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                 
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery(" select c.d as 't', sum(tothrs) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join time_clock on strftime('%W',indate) = c.d  " +
                                     " and indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " group by c.d;");   
                } else {
                   res = st.executeQuery(" select c.d as 't', sum(tothrs) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                                     " left outer join time_clock on week(indate) = c.d  " +
                                     " and indate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and indate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " group by c.d;"); 
                }
                

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("t"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5014), getGlobalColumnTag("week"), getGlobalColumnTag("hours"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                myicon.getImage().flush();

                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

        
    // Requisition charts
    public void ReqDollarsByAcct() {
      try {
      cleanUpOldChartFile();
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;

        try {
            
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
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5015), dataset, true, true, false);

               PiePlot plot = (PiePlot) chart.getPlot();

   PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);

    try {
    ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
    } catch (IOException e) {
    MainFrame.bslog(e);
    }
    ImageIcon myicon = new ImageIcon(chartfilepath);
    myicon.getImage().flush();   
    this.chartlabel.setIcon(myicon);
    ChartPanel.setVisible(true);
    this.repaint();

          } catch (SQLException s) {
              MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
 }

    public void ReqDollarsByDept() {
      try {
    cleanUpOldChartFile();
        Class.forName(driver).newInstance();
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;

        try {
            
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
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5016), dataset, true, true, false);

               PiePlot plot = (PiePlot) chart.getPlot();

   PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);

    try {
    ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
    } catch (IOException e) {
    MainFrame.bslog(e);
    }
    ImageIcon myicon = new ImageIcon(chartfilepath);
    myicon.getImage().flush();   
    this.chartlabel.setIcon(myicon);
    ChartPanel.setVisible(true);
    this.repaint();

          } catch (SQLException s) {
             MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
 }

    public void ReqDollarsByUser() {
      try {
        cleanUpOldChartFile();
        Class.forName(driver).newInstance();
        Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
        Statement st = con.createStatement();
            ResultSet res = null;
        try {
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
    JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5017), dataset, true, true, false);

               PiePlot plot = (PiePlot) chart.getPlot();

   PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
    plot.setLabelGenerator(gen);

    try {
    ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, 900, this.getHeight()/2);
    } catch (IOException e) {
    MainFrame.bslog(e);
    }
    ImageIcon myicon = new ImageIcon(chartfilepath);
    myicon.getImage().flush();   
    this.chartlabel.setIcon(myicon);
    ChartPanel.setVisible(true);
    this.repaint();

          } catch (SQLException s) {
              MainFrame.bslog(s);
        } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
 }

        
    // Scrap charts
    public void ScrapPerWeek() {
    try { 
            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                res = st.executeQuery(" select c.d as 't', sum(tr_qty * tr_cost) as 'sum' from ( select boo.mydate, strftime('%W',mydate) as 'd' " +
                                     " from (select date(julianday( " + "'" + dfdate.format(dcFrom.getDate()) + "' )" +
                                     ", '-6 days', '+' || mock_nbr || ' days') as mydate " +
                                     " from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d) as c " +
                                     " left outer join tran_mstr on strftime('%W',tr_eff_date) = c.d and tr_type = 'ISS-SCRAP' " +
                                     " and tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                                     " and tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                                     " group by c.d;");
                 } else {
                res = st.executeQuery(" select c.d as 't', sum(tr_qty * tr_cost) as 'sum' from ( select boo.mydate, week(mydate) as 'd' " +
                    " from (select date_add( " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    ", interval mock_nbr day) as 'mydate' " +" from mock_mstr where mock_nbr <= " + "'" + days + "'" + " ) as boo group by d, boo.mydate) as c " +
                                     " left outer join tran_mstr on week(tr_eff_date) = c.d and tr_type = 'ISS-SCRAP' " +
                                     //" where mock_nbr <= 10 " +
                                     " group by c.d;");
                 }

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Dollars", res.getString("t"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5018), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void CodesAccumQty() {
    try {
            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
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
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5019), getGlobalColumnTag("code"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void PartAccumQty() {
    try {

            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select tr_item, sum(tr_qty) as 'sum' from tran_mstr " +
                    " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND tr_type = 'ISS-SCRAP' " + 
                    " group by tr_item order by sum desc limit 20 ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {

                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_item"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5020), getGlobalColumnTag("item"), getGlobalColumnTag("qty"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();

                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void RWPartAccumQty() {
    try {

            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;

            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select tr_item, sum(tr_qty) as 'sum' from tran_mstr " +
                    " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND tr_type = 'ISS-REWK' " + 
                    " group by tr_item order by sum desc limit 20 ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {

                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_item"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5021), getGlobalColumnTag("item"), getGlobalColumnTag("qty"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void PartAccumDollar() {
    try {

            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select tr_item, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                         " inner join item_mstr on it_item = tr_item " +
                    " left outer join itemr_cost on itr_item = tr_item and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                    " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND tr_type = 'ISS-SCRAP' " + 
                    " group by tr_item order by sum desc limit 20 ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_item"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5022), getGlobalColumnTag("item"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void CodesAccumDollar() {
    try {

            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
               
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select tr_ref, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                         " inner join item_mstr on it_item = tr_item " +
                    " left outer join itemr_cost on itr_item = tr_item and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                    " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND tr_type = 'ISS-SCRAP' " + 
                    " group by tr_ref order by sum desc limit 20 ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_ref"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5023), getGlobalColumnTag("code"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void DeptAccumDollar() {
    try {

            cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            int qty = 0;
            double dol = 0;
            try {
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                res = st.executeQuery("select tr_actcell, sum(tr_qty * itr_total) as 'sum' from tran_mstr " +
                         " inner join item_mstr on it_item = tr_item " +
                    " left outer join itemr_cost on itr_item = tr_item and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                    " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                    " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                    " AND tr_type = 'ISS-SCRAP' " + 
                    " group by tr_actcell order by sum desc limit 20 ;");

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                while (res.next()) {
                    dataset.setValue(res.getDouble("sum"), "Sum", res.getString("tr_actcell"));
                }
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5024), getGlobalColumnTag("cell"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public void DeptAccumQty() {
    try {

          cleanUpOldChartFile();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
                ResultSet res = null;
            try {
                
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
                JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5025) + "\n" +"from " + 
                        dfdate.format(dcFrom.getDate()).toString() + " thru " +
                        dfdate.format(dcTo.getDate()).toString(), getGlobalColumnTag("dept"), getGlobalColumnTag("qty"), dataset, PlotOrientation.VERTICAL, true, true, false);
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
                    MainFrame.bslog(e);
                }
                ImageIcon myicon = new ImageIcon(chartfilepath);
                myicon.getImage().flush();
                this.chartlabel.setIcon(myicon);
                ChartPanel.setVisible(true);
                this.repaint();
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                   if (res != null) res.close();
                   if (st != null) st.close();
                   con.close();
                }
        } catch (Exception e) {
            MainFrame.bslog(e);
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
        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btChart = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        ddlimit = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        ddreport = new javax.swing.JComboBox<>();
        cbcodes = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        ChartPanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        CodePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tacodes = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setBackground(new java.awt.Color(0, 102, 204));

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chart View"));
        mainPanel.setName("panelmain"); // NOI18N

        jLabel2.setText("From Date");
        jLabel2.setName("lblfromdate"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");
        jLabel3.setName("lbltodate"); // NOI18N

        btChart.setText("Chart");
        btChart.setName("btchart"); // NOI18N
        btChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChartActionPerformed(evt);
            }
        });

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        ddlimit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "5", "10", "25" }));

        jLabel1.setText("Limit");

        cbcodes.setText("codes");
        cbcodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbcodesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btChart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbcodes)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btChart)
                        .addComponent(btprint)
                        .addComponent(ddlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(cbcodes))
                    .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3)
                        .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout ChartPanelLayout = new javax.swing.GroupLayout(ChartPanel);
        ChartPanel.setLayout(ChartPanelLayout);
        ChartPanelLayout.setHorizontalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChartPanelLayout.createSequentialGroup()
                .addContainerGap(398, Short.MAX_VALUE)
                .addComponent(chartlabel))
        );
        ChartPanelLayout.setVerticalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chartlabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(ChartPanel);

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
                .addContainerGap(398, Short.MAX_VALUE))
        );
        CodePanelLayout.setVerticalGroup(
            CodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(CodePanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChartActionPerformed
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        whichreport = ddreport.getSelectedItem().toString();
        
        if (dcTo.getDate() == null || dcFrom.getDate() == null) {
                bsmf.MainFrame.show("Must choose a date for both From and To");
                return;
            }
        
        int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
        
        tacodes.setText("");
        ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
        String weeknumbers = "";

        for (int i = 0; i < weekcodes.size(); i++) {
          weeknumbers += (weekcodes.get(i)) + "\n";
        }
               
        if (whichreport.equals("")) {
            bsmf.MainFrame.show("whichreport is empty");
        }
        
       
         if (whichreport.equals("Requisition -- amount by account")) {
            ReqDollarsByAcct();
        }
        
        if (whichreport.equals("Requisition -- amount by department")) {
            ReqDollarsByDept();
        }
        
        if (whichreport.equals("Requisition -- frequency per userid")) {
            ReqDollarsByUser();
        }
        
        
         if (whichreport.equals("Shipping -- units per week")) {
            ShipPerWeekUnitsChart();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Shipping -- dollars per week")) {
            ShipPerWeekDollarsChart();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
          if (whichreport.equals("Production -- total units per week")) {
            ProdByWeekFGUnits();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Production -- total cost per week")) {
            
            ProdByWeekFGDollars();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
       
        
        if (whichreport.equals("Order -- orders per week total units")) {
            DiscreteOrderPerWeekUnits();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Order -- open orders")) {
            pcOpenOrdersByCust();
        } // if whichreport
        
        if (whichreport.equals("Order -- orders per week total dollars")) {
            DiscreteOrderPerWeekDollars();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
       
        
       
        
        
        if (whichreport.equals("RWPartAccumQty")) {
            RWPartAccumQty();
        }
        
        if (whichreport.equals("Sales -- total sales by date")) {
            piechart_salesbycust();
        }
        
        if (whichreport.equals("Finance -- income versus expense")) {
            piechart_profitandloss();
        }
        
        if (whichreport.equals("Finance -- expense by account")) {
            piechart_expensebyaccount();
        }
        if (whichreport.equals("Finance -- income by account")) {
            piechart_incomebyaccountcc();
        }
        
        if (whichreport.equals("Sales -- current accounts receivable")) {
            piechart_custAR();
        }
        
         if (whichreport.equals("Clock -- by department")) {
            ClockChartByDept();
            tacodes.setText("");
            ArrayList codes = OVData.getdeptanddesclist();
            String str = "";
            
            for (int i = 0; i < codes.size(); i++) {
                str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Clock -- by code")) {
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
        
         if (whichreport.equals("Clock -- by employee")) {
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
        
        
       if (whichreport.equals("Clock -- by week")) {
            HoursPerWeek();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
   
        
       
        
        if (whichreport.equals("Scrap -- per week")) {
            ScrapPerWeek();
            tacodes.setText(weeknumbers);
            CodePanel.setVisible(true);
        } // if whichreport
        
         if (whichreport.equals("Scrap -- quantity by code")) {
            CodesAccumQty();
            tacodes.setText("");
            ArrayList<String[]> codes = OVData.getCodeAndValueMstr("Scrap");
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              String[] c = codes.get(i);  
              str += (c[0] + " = " + c[1]) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } // if whichreport
         
          if (whichreport.equals("Scrap -- dollars by code")) {
            CodesAccumDollar();
            tacodes.setText("");
            ArrayList<String[]> codes = OVData.getCodeAndValueMstr("Scrap"); 
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
              String[] c = codes.get(i);  
              str += (c[0] + " = " + c[1]) + "\n";
            }
            tacodes.setText(str);
            CodePanel.setVisible(true);
        } 
        
        if (whichreport.equals("Scrap -- quantity by item")) {
            PartAccumQty();
        }
        
       
        
        if (whichreport.equals("Scrap -- dollars by item")) {
            PartAccumDollar();
        }
        
        if (whichreport.equals("Scrap -- quantity by department")) {
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
        if (whichreport.equals("Scrap -- dollars by department")) {
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
        /*
        BufferedImage image = null;
        try {
        image = ImageIO.read(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"));
        } catch (IOException e) {
        }
        */
        if (myimage != null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(new ImagePrintable(printJob, myimage));
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(OrientationRequested.LANDSCAPE);
            if (printJob.printDialog()) {
                try {
                    printJob.print(aset);
                } catch (PrinterException prt) {
                    prt.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_btprintActionPerformed

    private void cbcodesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbcodesActionPerformed
        if (cbcodes.isSelected()) {
            CodePanel.setVisible(true);
        } else {
            CodePanel.setVisible(false);
        }
    }//GEN-LAST:event_cbcodesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChartPanel;
    private javax.swing.JPanel CodePanel;
    private javax.swing.JButton btChart;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbcodes;
    private javax.swing.JLabel chartlabel;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox<String> ddlimit;
    private javax.swing.JComboBox<String> ddreport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextArea tacodes;
    // End of variables declaration//GEN-END:variables
}
