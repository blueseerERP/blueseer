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

package com.blueseer.fgl;

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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import static com.blueseer.utl.BlueSeerUtils.sendServerRequest;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
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
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class GLAcctBalRpt3 extends javax.swing.JPanel {
 
    public String xData = null;
    
     String chartfilepath = OVData.getSystemTempDirectory() + "/" + "chartexpinc.jpg";
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Chart", getGlobalColumnTag("account"), 
                            getGlobalColumnTag("description"), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","12"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    
    class Task extends SwingWorker<Void, Void> {
       
          String[] key = null;
          
          public Task(String[] key) { 
              this.key = key;
          }
        
        @Override
        public Void doInBackground() throws Exception {
            if (bsmf.MainFrame.remoteDB) {
               ArrayList<String[]> list = new ArrayList<String[]>();
               list.add(new String[]{"id","getAccountActivityYear"});
               list.add(new String[]{"year",ddyear.getSelectedItem().toString()});
               list.add(new String[]{"site",ddsite.getSelectedItem().toString()});
               list.add(new String[]{"fromacct",ddacctfrom.getSelectedItem().toString()});
               list.add(new String[]{"toacct",ddacctto.getSelectedItem().toString()});               
               xData = sendServerRequest(list, "dataServFIN");
            } else {
               xData = fglData.getAccountActivityYear(key); 
            }
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            BlueSeerUtils.endTask(new String[]{"0",getMessageTag(1125)});
            enableAll();
            updateForm();
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
    public GLAcctBalRpt3() {
        initComponents();
        setLanguageTags(this);
    }

    public void chartacct(String acct, ArrayList<String> values) {
         File f = new File(bsmf.MainFrame.temp + "chart.jpg");
                if(f.exists()) {
                    f.delete();
                }
              String acctdesc = fglData.getGLAcctDesc(acct);
              DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
              int i = 1;
              double doublevalue = 0;
               for (String value : values) {
                        doublevalue = bsParseDouble(value);
                           if (doublevalue < 0) {
                               doublevalue *= -1;
                           }
                        dataset.setValue(doublevalue, acct, String.valueOf(i));
                        i++;
                    }  
               JFreeChart chart = ChartFactory.createBarChart(acctdesc, getGlobalColumnTag("period"), getGlobalColumnTag("amount"), dataset, PlotOrientation.VERTICAL, true, true, false);
                  //  CategoryItemRenderer renderer = new ScrapChartView.CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     chartpanel.setVisible(true);
                   //  p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.5));
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
                    myicon.getImage().flush();
                 //   this.chartlabel.setHorizontalAlignment(SwingConstants.LEFT);
                 //   this.chartlabel.setVerticalAlignment(SwingConstants.CENTER);
                 //   this.chartlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
       
                    
                    this.chartlabel.setIcon(myicon);
                    
                    
                    this.repaint();
    }
    
    public void chartExpAndInc() {
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
                java.util.Date now = new java.util.Date();
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                  Calendar cal = new GregorianCalendar();
                  cal.set(Calendar.DAY_OF_YEAR, 1);
                  java.util.Date firstday = cal.getTime();
                  
                res = st.executeQuery("select ac_type, sum(glh_base_amt) as 'sum' from gl_hist inner join ac_mstr on ac_id = glh_acct " +
                        " where glh_effdate >= " + "'" + dfdate.format(firstday) + "'" +
                        " AND glh_effdate <= " + "'" + dfdate.format(now) + "'" +
                        " AND glh_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " AND ( ac_type = 'E' or ac_type = 'I' ) " +
                        " group by ac_type   ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                    if (res.getString("ac_type") == null || res.getString("ac_type").isEmpty()) {
                      acct = "Unassigned";
                    } else {
                      acct = res.getString("ac_type");   
                    }
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5026), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.5));
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.pielabel.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
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
    
     public void disableAll() {
       
       btRun.setEnabled(false);
       bthidechart.setEnabled(false);
       cbzero.setEnabled(false);
       ddyear.setEnabled(false);
       ddsum.setEnabled(false);
       ddsite.setEnabled(false);
       ddacctfrom.setEnabled(false);
       ddacctto.setEnabled(false);
       tablereport.setEnabled(false);
    }
    
    public void enableAll() {
      btRun.setEnabled(true);
       bthidechart.setEnabled(true);
       cbzero.setEnabled(true);
       ddyear.setEnabled(true);
       ddsum.setEnabled(true);
       ddsite.setEnabled(true);
       ddacctfrom.setEnabled(true);
       ddacctto.setEnabled(true);
       tablereport.setEnabled(true); 
    }
    
    public void updateForm() {
        
        mymodel.setNumRows(0);
        
        if (xData != null) {  
        String[] arrdata = xData.split("\n", -1);
        for (String x : arrdata) {
            String[] s = x.split(",",-1);
            
            if (s.length < 8) {
                continue;  // must be blank lines
            }
           
            
            if (cbzero.isSelected() && ( ( bsParseDouble(s[2]) +
                       bsParseDouble(s[3]) +
                       bsParseDouble(s[4]) +
                       bsParseDouble(s[5]) +       
                       bsParseDouble(s[6]) +
                       bsParseDouble(s[7]) +       
                       bsParseDouble(s[8]) +       
                       bsParseDouble(s[9]) +        
                       bsParseDouble(s[10]) +       
                       bsParseDouble(s[11]) +       
                       bsParseDouble(s[12]) +
                       bsParseDouble(s[13]) ) == 0) ) {
                     continue;
                 }
            
            mymodel.addRow(new Object[]{BlueSeerUtils.clickchart, s[0],
                                s[1],
                                s[2],
                                s[3],
                                s[4],
                                s[5],
                                s[6],
                                s[7],
                                s[8],
                                s[9],
                                s[10],
                                s[11],
                                s[12],
                                s[13]
                             }); 
            }
        
        chartExpAndInc();
        ddsum.setSelectedIndex(0);
        }
    }
    
    public void initvars(String[] arg) {
        chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
        java.util.Date now = new java.util.Date();
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
        
        mymodel.setNumRows(0);
        tablereport.setModel(mymodel);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        
       //  tablereport.getColumnModel().getColumn(0).setCellRenderer(new GLAcctBalRpt3.ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        
        ddyear.removeAllItems();
        for (int i = 1967 ; i < 2222; i++) {
            ddyear.addItem(String.valueOf(i));
        }
        ddyear.setSelectedItem(bsNumber(dfyear.format(now)));
            
       cbzero.setSelected(false);
        
        ArrayList myacct = fglData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacctfrom.addItem(myacct.get(i));
            ddacctto.addItem(myacct.get(i));
        }
            ddacctfrom.setSelectedIndex(0);
            ddacctto.setSelectedIndex(ddacctto.getItemCount() - 1);
        
        ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelsum = new javax.swing.JLabel();
        ddacctfrom = new javax.swing.JComboBox();
        ddacctto = new javax.swing.JComboBox();
        cbzero = new javax.swing.JCheckBox();
        ddyear = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        chartpanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        pielabel = new javax.swing.JLabel();
        bthidechart = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        ddsum = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Year");
        jLabel2.setName("lblyear"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Acct");
        jLabel1.setName("lblfromacct"); // NOI18N

        jLabel4.setText("To Acct");
        jLabel4.setName("lbltoacct"); // NOI18N

        cbzero.setText("Supress Zeros");
        cbzero.setName("cbsuppresszeros"); // NOI18N

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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new java.awt.BorderLayout());

        chartlabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chartlabel.setMinimumSize(new java.awt.Dimension(50, 50));
        chartpanel.add(chartlabel, java.awt.BorderLayout.NORTH);

        pielabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pielabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        chartpanel.add(pielabel, java.awt.BorderLayout.SOUTH);

        jPanel2.add(chartpanel);

        bthidechart.setText("Hide Chart");
        bthidechart.setName("bthidechart"); // NOI18N
        bthidechart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidechartActionPerformed(evt);
            }
        });

        jLabel3.setText("Site");
        jLabel3.setName("lblsite"); // NOI18N

        ddsum.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        ddsum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsumActionPerformed(evt);
            }
        });

        jLabel5.setText("Sum Period");
        jLabel5.setName("lblsumperiod"); // NOI18N

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
                    .addComponent(ddyear, 0, 115, Short.MAX_VALUE)
                    .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddacctfrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddacctto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bthidechart)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(7, 7, 7)
                        .addComponent(ddsum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbzero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelsum, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(436, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btRun)
                    .addComponent(bthidechart)
                    .addComponent(ddsum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(cbzero))
                    .addComponent(labelsum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
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
    
        String[] key = new String[]{
      ddyear.getSelectedItem().toString(),
      ddsite.getSelectedItem().toString(),
      ddacctfrom.getSelectedItem().toString(),
      ddacctto.getSelectedItem().toString()
    };
    BlueSeerUtils.startTask(new String[]{"",getMessageTag(1189)});
    disableAll();
    Task task = new Task(key);
    task.execute();  
    
  /*  
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
                int qty = 0;
                double dol = 0;
                int i = 0;
               
               mymodel.setNumRows(0);
                   
                
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 
                 int year = bsParseInt(ddyear.getSelectedItem().toString());
                 
                 
                 String[] str_activity = {"","","","","","","","","","","","",""};
                 double activity = 0.00;
                
                 Date p_datestart = null;
                 Date p_dateend = null;
                 
                 ArrayList<String> accounts = fglData.getGLAcctListRangeWTypeDesc(ddacctfrom.getSelectedItem().toString(), ddacctto.getSelectedItem().toString());
                
                 
                 String acctid = "";
                 String accttype = "";
                 String acctdesc = "";
                 String[] ac = null;
                 
                 
                 ACCTS:    for (String account : accounts) {
                  ac = account.split(",", -1);
                  acctid = ac[0];
                  accttype = ac[1];
                  acctdesc = ac[2];
                  str_activity[0] = "";
                  str_activity[1] = "";
                  str_activity[2] = "";
                  str_activity[3] = "";
                  str_activity[4] = "";
                  str_activity[5] = "";
                  str_activity[6] = "";
                  str_activity[7] = "";
                  str_activity[8] = "";
                  str_activity[9] = "";
                  str_activity[10] = "";
                  str_activity[11] = "";
                  
                  for (int k = 1 ; k<= 12 ; k++) {
                  
                  activity = 0.00;
                   
                  // calculate period(s) activity defined by date range 
                  // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
               
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                        "'" + String.valueOf(year) + "'" + 
                        " AND acb_per = " +
                        "'" + String.valueOf(k) + "'" +
                        " AND acb_acct = " +
                        "'" + acctid + "'" +
                        " AND acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          activity += res.getDouble(("sum"));
                       }
                 
                     str_activity[k - 1] = currformatDouble(activity);
                 
                 
                  } // k
                 
                 if (cbzero.isSelected() && ( ( bsParseDouble(str_activity[0]) +
                       bsParseDouble(str_activity[1]) +
                       bsParseDouble(str_activity[2]) +
                       bsParseDouble(str_activity[3]) +       
                       bsParseDouble(str_activity[4]) +
                       bsParseDouble(str_activity[5]) +       
                       bsParseDouble(str_activity[6]) +       
                       bsParseDouble(str_activity[7]) +        
                       bsParseDouble(str_activity[8]) +       
                       bsParseDouble(str_activity[9]) +       
                       bsParseDouble(str_activity[10]) +
                       bsParseDouble(str_activity[11]) ) == 0) ) {
                     continue;
                 }
                               
               //      bsmf.MainFrame.show(account);
                 mymodel.addRow(new Object[]{BlueSeerUtils.clickchart, acctid,
                                acctdesc,
                                str_activity[0],
                                str_activity[1],
                                str_activity[2],
                                str_activity[3],
                                str_activity[4],
                                str_activity[5],
                                str_activity[6],
                                str_activity[7],
                                str_activity[8],
                                str_activity[9],
                                str_activity[10],
                                str_activity[11]
                             });
                
              
                 } // account
             
                 
                  chartExpAndInc();
                 
                 ddsum.setSelectedIndex(0);
               
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
       */
    }//GEN-LAST:event_btRunActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
             
              //  bsmf.MainFrame.itemmastmaintpanel.initvars(tablescrap.getValueAt(row, col).toString());
                ArrayList<String> mylist = new ArrayList<String>();
                for (int i = 3 ; i <= 14; i++) {
                mylist.add(tablereport.getValueAt(row, i).toString());
                }
                chartacct(tablereport.getValueAt(row, 1).toString(), mylist);
               
                
                 bthidechart.setEnabled(true);
           
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void bthidechartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidechartActionPerformed
        chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
    }//GEN-LAST:event_bthidechartActionPerformed

    private void ddsumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsumActionPerformed
        double amt = 0.00;
        for (int i = 0 ; i < tablereport.getRowCount() ; i++) {
               amt += bsParseDouble(tablereport.getValueAt(i, bsParseInt(ddsum.getSelectedItem().toString()) + 2).toString());
       }
        labelsum.setText(currformatDouble(amt));
    }//GEN-LAST:event_ddsumActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton bthidechart;
    private javax.swing.JCheckBox cbzero;
    private javax.swing.JLabel chartlabel;
    private javax.swing.JPanel chartpanel;
    private javax.swing.JComboBox ddacctfrom;
    private javax.swing.JComboBox ddacctto;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddsum;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelsum;
    private javax.swing.JLabel pielabel;
    private javax.swing.JTable tablereport;
    // End of variables declaration//GEN-END:variables
}
