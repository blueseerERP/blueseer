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

import com.blueseer.fgl.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import com.blueseer.utl.ChartView;
import com.blueseer.vdr.venData;
import java.awt.Paint;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
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
public class ProjectionBrowse extends javax.swing.JPanel {
 
     String chartfilepath = OVData.getSystemTempDirectory() + "/" + "chartprojection.jpg";
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("item"), 
                getGlobalColumnTag("qoh"),            
                getGlobalColumnTag("week+") + "1", 
                getGlobalColumnTag("week+") + "2", 
                getGlobalColumnTag("week+") + "3", 
                getGlobalColumnTag("week+") + "4",
                getGlobalColumnTag("week+") + "5", 
                getGlobalColumnTag("week+") + "6", 
                getGlobalColumnTag("week+") + "7", 
                getGlobalColumnTag("week+") + "8", 
                getGlobalColumnTag("week+") + "9", 
                getGlobalColumnTag("week+") + "10",
                getGlobalColumnTag("week+") + "11",
                getGlobalColumnTag("week+") + "12"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return String.class;  
                        else return Double.class;  //other columns accept String values  
                      }  
                        };
                
    
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
      
      
      if (l_value < 0 )
   //   if (l_colKey.toString().matches("(.*)Mon(.*)"))
      {
          mycolor = Color.RED;
      } else {
          mycolor = Color.BLUE;
      }
    }
       return mycolor;
       
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
    
    /**
     * Creates new form ScrapReportPanel
     */
    public ProjectionBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void chartProjection(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5028), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer(); 

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
        try {
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, (int) (this.getWidth() / 2), (int) (this.getHeight() / 1.2));
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.chartlabel.setIcon(myicon);
        this.repaint();
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
        chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
        btchart.setEnabled(true);
        java.util.Date now = new java.util.Date();
        tbfromitem.setText("");
        tbtoitem.setText("");
        mymodel.setNumRows(0);
        cbqoh.setSelected(false);
        cbpo.setSelected(false);
        tablereport.setModel(mymodel);
        lblmrpruntime.setText("");
        lblmrpuserid.setText("");
        tablereport.getTableHeader().setReorderingAllowed(false);
        
       //  tablereport.getColumnModel().getColumn(0).setCellRenderer(new GLAcctBalRpt3.ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        
        
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
        jLabel2 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        chartpanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        bthidechart = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btchart = new javax.swing.JButton();
        ddclass = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        tbfromitem = new javax.swing.JTextField();
        tbtoitem = new javax.swing.JTextField();
        cbqoh = new javax.swing.JCheckBox();
        cbpo = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        lblmrpruntime = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblmrpuserid = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("From Item");
        jLabel2.setName("lblfromitem"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jPanel2.setPreferredSize(new java.awt.Dimension(904, 402));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jPanel3.setLayout(new java.awt.BorderLayout());

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
        ));
        jScrollPane1.setViewportView(tablereport);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new java.awt.CardLayout());

        chartlabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chartlabel.setToolTipText("");
        chartpanel.add(chartlabel, "card2");

        jPanel2.add(chartpanel);

        bthidechart.setText("Hide Chart");
        bthidechart.setName("bthidechart"); // NOI18N
        bthidechart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidechartActionPerformed(evt);
            }
        });

        jLabel3.setText("To Item");
        jLabel3.setName("lbltoitem"); // NOI18N

        btchart.setText("Chart");
        btchart.setName("btchart"); // NOI18N
        btchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchartActionPerformed(evt);
            }
        });

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "P", "M" }));

        jLabel1.setText("Class");
        jLabel1.setName("lblclass"); // NOI18N

        cbqoh.setText("Reduce By QOH");
        cbqoh.setName("reducebyqoh"); // NOI18N

        cbpo.setText("Reduce By POs");
        cbpo.setName("reducebypo"); // NOI18N

        jLabel4.setText("MRP RunTime:");
        jLabel4.setName("mrpruntime"); // NOI18N

        jLabel5.setText("MRP Userid:");
        jLabel5.setName("mrpuserid"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbfromitem)
                    .addComponent(tbtoitem, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btchart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bthidechart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblmrpruntime, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblmrpuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbqoh)
                        .addGap(10, 10, 10)
                        .addComponent(cbpo)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(btRun)
                        .addComponent(bthidechart)
                        .addComponent(btchart)
                        .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(tbfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4)
                    .addComponent(lblmrpruntime, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(lblmrpuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tbtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbqoh)
                        .addComponent(cbpo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
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

        String fromitem = bsmf.MainFrame.lowchar;
        String toitem = bsmf.MainFrame.hichar;
        
        if (! tbfromitem.getText().isEmpty()) {
            fromitem = tbfromitem.getText();
        }
        if (! tbtoitem.getText().isEmpty()) {
            fromitem = tbtoitem.getText();
        }
        String runtime = OVData.getCodeValueByCodeKey("mrpinfo", "mrpruntime");
        String shorttime = null;
        if (! runtime.isEmpty()) {
            shorttime = runtime.substring(0, 16);
        }
        lblmrpruntime.setText(shorttime);
        lblmrpuserid.setText(OVData.getCodeValueByCodeKey("mrpinfo", "mrpuserid"));
        
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                   
                 mymodel.setNumRows(0);
                tablereport.setModel(mymodel);
            //    tablereport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                 
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           
                LocalDate date = LocalDate.now();
                TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
                int weekOfYear = date.get(woy) - 1;  // need to subtract 1 to match sqlite/mysql week calc
            
                res = st.executeQuery("select mrp_part, it_code, strftime('%W',mrp_date) as 'w', " + 
                        " sum(mrp_qty) as 'sum', " +
                        " (select sum(in_qoh) from in_mstr where in_part = mrp_part) as 'qoh' " +
                        " from mrp_mstr " +
                        " inner join item_mstr on it_item = mrp_part " +
                        " where mrp_part >= " + "'" + fromitem + "'" +
                        " and mrp_part <= " + "'" + toitem + "'" +
                        " group by w, mrp_part;");
                Map<String, String> hm = new HashMap<>();
                Map<String, String> itemqoh = new HashMap<>();
                Map<String, String> pos = new HashMap<>();
                Set<String> set = new LinkedHashSet<String>();
                    while (res.next()) {
                      if (! res.getString("it_code").equals(ddclass.getSelectedItem().toString())) {
                          continue;
                      }
                      hm.put(res.getString("mrp_part") + "+" + res.getString("w"), res.getString("sum"));
                      set.add(res.getString("mrp_part"));
                      if (! itemqoh.containsKey(res.getString("mrp_part"))) {
			itemqoh.put(res.getString("mrp_part"), res.getString("qoh"));
		      }
                    } 
                    
                    // now POs
                    res = st.executeQuery("select pod_part, it_code, strftime('%W',pod_due_date) as 'w', " + 
                        " sum(pod_ord_qty) as 'sum' " +
                        " from pod_mstr " +
                        " inner join item_mstr on it_item = pod_part " +    
                        " where pod_part >= " + "'" + fromitem + "'" +
                        " and pod_part <= " + "'" + toitem + "'" +
                        " and pod_status <> 'closed' " +
                        " group by w, pod_part;");
                    while (res.next()) {
                      if (! res.getString("it_code").equals(ddclass.getSelectedItem().toString())) {
                          continue;
                      }
                      pos.put(res.getString("pod_part") + "+" + res.getString("w"), res.getString("sum"));
                    } 
                    
                    double[] qty = new double[]{0,0,0,0,0,0,0,0,0,0,0,0};
                    double qoh = 0;
                    double rm = 0;
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                    for (String s : set) {
                        if (itemqoh.containsKey(s) && itemqoh.get(s) != null) {
                        qoh = Double.valueOf(itemqoh.get(s));
                        } 
                        rm = 0;
                        for (int i = 0; i < 12; i++) {
                            qty[i] = 0;
                        }
                        for (int i = weekOfYear; i <= weekOfYear+11 ; i++) {
                          if (hm.containsKey(s + "+" + String.valueOf(i))) {
                              qty[i - weekOfYear] = Double.valueOf(hm.get(s + "+" + String.valueOf(i)));
                              if (cbqoh.isSelected() && qoh > 0) {
                                  rm = qty[i-weekOfYear] - qoh;
                                  if (rm >= 0) {
                                      qty[i-weekOfYear] = rm;
                                      qoh = 0;
                                  } else {
                                      qty[i-weekOfYear] = 0;
                                      qoh = rm;
                                  }
                              }
                          } 
                          if (cbpo.isSelected() && pos.containsKey(s + "+" + String.valueOf(i))) {
                              if (Double.valueOf(pos.get(s + "+" + String.valueOf(i))) < 0)
                                qty[i - weekOfYear] -= Double.valueOf(pos.get(s + "+" + String.valueOf(i)));
                              else
                                qty[i - weekOfYear] += Double.valueOf(pos.get(s + "+" + String.valueOf(i)));
                          } 
                          dataset.setValue(Double.valueOf(qty[i - weekOfYear]), "Week Sum", String.valueOf((i - weekOfYear) + 1)); 
                        }
                        mymodel.addRow(new Object[] {
                            s,
                            qoh,
                            qty[0],
                            qty[1],
                            qty[2],
                            qty[3],
                            qty[4],
                            qty[5],
                            qty[6],
                            qty[7],
                            qty[8],
                            qty[9],
                            qty[10],
                            qty[11]
                        });
                    }
                    chartProjection(dataset);
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

    private void bthidechartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidechartActionPerformed
       chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
        btchart.setEnabled(true);
    }//GEN-LAST:event_bthidechartActionPerformed

    private void btchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchartActionPerformed
       // chartProjection();
        chartpanel.setVisible(true);
        bthidechart.setEnabled(true);
        btchart.setEnabled(false);
    }//GEN-LAST:event_btchartActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btchart;
    private javax.swing.JButton bthidechart;
    private javax.swing.JCheckBox cbpo;
    private javax.swing.JCheckBox cbqoh;
    private javax.swing.JLabel chartlabel;
    private javax.swing.JPanel chartpanel;
    private javax.swing.JComboBox<String> ddclass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblmrpruntime;
    private javax.swing.JLabel lblmrpuserid;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbfromitem;
    private javax.swing.JTextField tbtoitem;
    // End of variables declaration//GEN-END:variables
}
