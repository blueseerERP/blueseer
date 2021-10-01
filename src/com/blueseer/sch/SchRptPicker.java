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

import com.blueseer.ord.*;
import com.blueseer.ctr.*;
import com.blueseer.inv.*;
import com.blueseer.sch.*;
import com.blueseer.inv.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import com.blueseer.utl.DTData;
import com.blueseer.utl.RPData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class SchRptPicker extends javax.swing.JPanel {

    /* NOTES:
    These notes apply to all RptPicker classes.
    
    All subreport items in the drop down report list on the main panel
    are defined in Jasper Maintenance under the Admin Menu.  
    
    There is one function per report listed in the report drop down selection box.
    Each report listed must have a corresponding 'func' included here between
    the CUSTOM FUNCTIONS begin/end comments.
    The name of each function created is added to the 'func' field in the 
    Jasper Maintenance Menu for the report in the drop down list.
    One report, one title, one func
        
    Note:  this was developed in this manner to reduce the number of JPanel classes required
    per each sub report.   I'm all ears if have another option.  :)
    
    */
    Map<String, String> jaspermap = new HashMap<String, String>();
    String jasperGroup = "SchRptGroup";
    boolean isLoad = false;
    
     class renderer1 extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        return c;
    }
    }
    
     class renderer2 extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        int qoh = (int)table.getModel().getValueAt(table.convertRowIndexToModel(row), 6); 
        int stock = (int)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7);
        
        
        if (qoh < stock && column == 7) {
              c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);
        } else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }     
       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
    
    
    
    /**
     * Creates new form CustXrefRpt1
     */
    public SchRptPicker() {
        initComponents();
        setLanguageTags(this);
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
      ddreport.removeAllItems();
      jaspermap.clear();
      int k = 0;
      ArrayList<String[]> list = OVData.getJasperByGroup(jasperGroup);
      for (String[] x : list) { // list is string of desc, func, format
              jaspermap.put(x[0], x[2]); // desc, format
              ddreport.addItem(x[0]); // desc
          k++;
      }
      ddreport.insertItemAt("", 0);
      ddreport.setSelectedIndex(0);
      resetVariables();
      hidePanels();
           
      rbactive.setSelected(true);
      rbinactive.setSelected(false);
      buttonGroup1.add(rbactive);
      buttonGroup1.add(rbinactive);
      ((DefaultTableModel)tablereport.getModel()).setRowCount(0);
     isLoad = false;
    }
   
    
    
    /* misc methods */   
    public void hidePanels() {
        paneltb.setVisible(false);
        paneltb2.setVisible(false);
        paneldc.setVisible(false);
        paneldd.setVisible(false);
        panelrb.setVisible(false);
    }   
    
    public void showPanels(String[] panels) {
        for (String panel : panels) {
            if (panel.equals("tb1"))   // two textboxes tbkey1 & tbkey2
                paneltb.setVisible(true);
            if (panel.equals("tb2"))  // two textboxes tbkey3 & tbkey4
                paneltb2.setVisible(true);
            if (panel.equals("dc"))  // two datechoosers dcdate1 & dcdate2
                paneldc.setVisible(true);
            if (panel.equals("dd"))  // two dropdowns ddkey1 & ddkey2
                paneldd.setVisible(true);
            if (panel.equals("rb"))  // two radio buttosn  rbactive & rbinactive
                panelrb.setVisible(true);
        }
    }
    
    public void resetVariables() {
        tbkey1.setEnabled(true);
        tbkey2.setEnabled(true);
        tbkey3.setEnabled(true);
        tbkey4.setEnabled(true);
        dcdate1.setEnabled(true);
        dcdate2.setEnabled(true);
        ddkey1.setEnabled(true);
        ddkey2.setEnabled(true);
        rbactive.setEnabled(true);
        rbinactive.setEnabled(true);
        
        tbkey1.setVisible(true);
        tbkey2.setVisible(true);
        tbkey3.setVisible(true);
        tbkey4.setVisible(true);
        dcdate1.setVisible(true);
        dcdate2.setVisible(true);
        ddkey1.setVisible(true);
        ddkey2.setVisible(true);
        rbactive.setVisible(true);
        rbinactive.setVisible(true);
        
        tbkey1.setText("");
        tbkey2.setText("");
        tbkey3.setText("");
        tbkey4.setText("");
        dcdate1.setVisible(true);
        dcdate2.setVisible(true);
        ddkey1.setSelectedIndex(0);
        ddkey2.setSelectedIndex(0);
        rbactive.setSelected(true);
        rbinactive.setSelected(false);
        
        lbkey1.setText("");
        lbkey1.setVisible(true);
        lbkey2.setText("");
        lbkey2.setVisible(true);
        lbkey3.setText("");
        lbkey3.setVisible(true);
        lbkey4.setText("");
        lbkey4.setVisible(true);
        lbddkey1.setText("");
        lbddkey1.setVisible(true);
        lbddkey2.setText("");
        lbddkey2.setVisible(true);
        lbdate1.setText("");
        lbdate1.setVisible(true);
        lbdate2.setText("");
        lbdate2.setVisible(true);
    }
    
    /* CUSTOM FUNCTIONS BEGIN  */
    // one function per report to be added here
    // each function takes a boolean parameter.
    // if parameter is true....function creates layout for input variables
    // if parameter is false....function fills table with SQL query based in input variables
    // NOTE:  input variables (swing form components) are limited to:
    // four textboxes (tb1 & tb2 panels), 2 datechoosers, 2 dropdowns, 2 radiobuttons
    // see showPanels function for input panels layout mechanism
    
   
    /* Plan Orders by Item range */
    public void planOrdersByItem (boolean input) {
        
        if (input) { // input...draw variable input panel
           resetVariables();
           hidePanels();
           showPanels(new String[]{"tb1"});
           lbkey1.setText(getClassLabelTag("lblfromitem", this.getClass().getSimpleName()));
           lbkey2.setText(getClassLabelTag("lbltoitem", this.getClass().getSimpleName()));
          // java.util.Date now = new java.util.Date();
          // dcdate1.setDate(now);
          // dcdate2.setDate(now);
         } else { // output...fill report
            // colect variables from input
            String from = tbkey1.getText();
            String to = tbkey2.getText();
          //  String fromdate = BlueSeerUtils.setDateFormat(dcdate1.getDate());
           // String todate = BlueSeerUtils.setDateFormat(dcdate2.getDate());
            // cleanup variables
          
            if (from.isEmpty()) {
                  from = bsmf.MainFrame.lowchar;
            }
            if (to.isEmpty()) {
                  to = bsmf.MainFrame.hichar;
            }
          
            
            
            // create and fill tablemodel
            // column 1 is always 'select' and always type ImageIcon
            // the remaining columns are whatever you require
            
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
        new String[]{
            getGlobalColumnTag("item"), 
            getGlobalColumnTag("desc"), 
            getGlobalColumnTag("class"), 
            getGlobalColumnTag("planid"),
            getGlobalColumnTag("type"), 
            getGlobalColumnTag("order"), 
            getGlobalColumnTag("issched"), 
            getGlobalColumnTag("cell"), 
            getGlobalColumnTag("schedqty"), 
            getGlobalColumnTag("scheddate"), 
            getGlobalColumnTag("status")}); 
            
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{   
               
                 res = st.executeQuery("SELECT it_item, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  item_mstr left outer join plan_mstr on plan_part = it_item  " +
                        " where it_item  >= " + "'" + from + "'" +
                        " and it_item  <= " + "'" + to + "'" +
                        " order by plan_nbr ;");
                 
                    while (res.next()) {
                        mymodel.addRow(new Object[] { 
                                   res.getString("it_item"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   BlueSeerUtils.xNull(res.getString("plan_nbr")),
                                   BlueSeerUtils.xNull(res.getString("plan_type")),
                                   BlueSeerUtils.xNull(res.getString("plan_order")),
                                   BlueSeerUtils.xNull(res.getString("plan_is_sched")),
                                   BlueSeerUtils.xNull(res.getString("plan_cell")),
                                   BlueSeerUtils.xNull(res.getString("plan_qty_sched")),
                                   BlueSeerUtils.xNull(res.getString("plan_date_sched")),
                                   BlueSeerUtils.xNull(res.getString("plan_status"))
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
      
      // now assign tablemodel to table
            tablereport.setModel(mymodel);
            tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
            Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
              while (en.hasMoreElements()) {
                 TableColumn tc = en.nextElement();
                 if (tc.getClass().getSimpleName().equals("ImageIcon")) {
                     continue;
                 }
                 tc.setCellRenderer(new SchRptPicker.renderer1());
             }
        } // else run report
               
    }
    
     /* Plan Orders by Item range */
    public void planOrdersBySalesOrder (boolean input) {
        
        if (input) { // input...draw variable input panel
           resetVariables();
           hidePanels();
           showPanels(new String[]{"tb1"});
           lbkey1.setText(getClassLabelTag("lblfromorder", this.getClass().getSimpleName()));
           lbkey2.setText(getClassLabelTag("lbltoorder", this.getClass().getSimpleName()));
          // java.util.Date now = new java.util.Date();
          // dcdate1.setDate(now);
          // dcdate2.setDate(now);
         } else { // output...fill report
            // colect variables from input
            String from = tbkey1.getText();
            String to = tbkey2.getText();
          //  String fromdate = BlueSeerUtils.setDateFormat(dcdate1.getDate());
           // String todate = BlueSeerUtils.setDateFormat(dcdate2.getDate());
            // cleanup variables
          
            if (from.isEmpty()) {
                  from = bsmf.MainFrame.lowchar;
            }
            if (to.isEmpty()) {
                  to = bsmf.MainFrame.hichar;
            }
          
            
            
            // create and fill tablemodel
            // column 1 is always 'select' and always type ImageIcon
            // the remaining columns are whatever you require
          
        javax.swing.table.DefaultTableModel mymodel = mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
        new String[]{
            getGlobalColumnTag("order"), 
            getGlobalColumnTag("line"), 
            getGlobalColumnTag("item"), 
            getGlobalColumnTag("desc"), 
            getGlobalColumnTag("class"), 
            getGlobalColumnTag("planid"), 
            getGlobalColumnTag("issched"), 
            getGlobalColumnTag("cell"), 
            getGlobalColumnTag("schedqty"), 
            getGlobalColumnTag("scheddate"), 
            getGlobalColumnTag("status")}); 
            
      try{
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{   
                
                 res = st.executeQuery("SELECT sod_part, sod_nbr, sod_line, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  sod_det inner join item_mstr on sod_part = it_item left outer join plan_mstr on plan_order = sod_nbr and plan_line = sod_line  " +
                        " where cast(sod_nbr as decimal) >= " + "'" + from + "'" +
                        " and cast(sod_nbr as decimal) <= " + "'" + to + "'" +
                        " order by sod_line ;");
                 
                    while (res.next()) {
                        mymodel.addRow(new Object[] { 
                                   res.getString("sod_nbr"),
                                   res.getString("sod_line"),
                                   res.getString("sod_part"),
                                   res.getString("it_desc"),
                                   res.getString("it_code"),
                                   res.getString("plan_nbr"),
                                   res.getString("plan_is_sched"),
                                   res.getString("plan_cell"),
                                   res.getString("plan_qty_sched"),
                                   res.getString("plan_date_sched"),
                                   res.getString("plan_status")
                                  
                        });
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
              } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
      
      // now assign tablemodel to table
            tablereport.setModel(mymodel);
            tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
            Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
              while (en.hasMoreElements()) {
                 TableColumn tc = en.nextElement();
                 if (tc.getClass().getSimpleName().equals("ImageIcon")) {
                     continue;
                 }
                 tc.setCellRenderer(new SchRptPicker.renderer1());
             }
        } // else run report
               
    }
    
   
    /* CUSTOM FUNCTIONS END */
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btview = new javax.swing.JButton();
        ddreport = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btcsv = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        paneldd = new javax.swing.JPanel();
        ddkey1 = new javax.swing.JComboBox<>();
        ddkey2 = new javax.swing.JComboBox<>();
        lbddkey2 = new javax.swing.JLabel();
        lbddkey1 = new javax.swing.JLabel();
        paneldc = new javax.swing.JPanel();
        lbdate1 = new javax.swing.JLabel();
        lbdate2 = new javax.swing.JLabel();
        dcdate2 = new com.toedter.calendar.JDateChooser();
        dcdate1 = new com.toedter.calendar.JDateChooser();
        panelrb = new javax.swing.JPanel();
        rbinactive = new javax.swing.JRadioButton();
        rbactive = new javax.swing.JRadioButton();
        paneltb = new javax.swing.JPanel();
        lbkey2 = new javax.swing.JLabel();
        tbkey1 = new javax.swing.JTextField();
        lbkey1 = new javax.swing.JLabel();
        tbkey2 = new javax.swing.JTextField();
        paneltb2 = new javax.swing.JPanel();
        lbkey3 = new javax.swing.JLabel();
        tbkey3 = new javax.swing.JTextField();
        lbkey4 = new javax.swing.JLabel();
        tbkey4 = new javax.swing.JTextField();
        btprint = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Schedule Report Picker"));
        jPanel1.setName("panelmain"); // NOI18N

        btview.setText("View");
        btview.setName("btview"); // NOI18N
        btview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btviewActionPerformed(evt);
            }
        });

        ddreport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddreportActionPerformed(evt);
            }
        });

        jLabel3.setText("Report:");
        jLabel3.setName("lblreport"); // NOI18N

        btcsv.setText("CSV");
        btcsv.setName("btcsv"); // NOI18N
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        ddkey1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ddkey2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lbddkey2.setText("jLabel2");

        lbddkey1.setText("jLabel1");

        javax.swing.GroupLayout panelddLayout = new javax.swing.GroupLayout(paneldd);
        paneldd.setLayout(panelddLayout);
        panelddLayout.setHorizontalGroup(
            panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbddkey1)
                    .addComponent(lbddkey2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddkey1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddkey2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        panelddLayout.setVerticalGroup(
            panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddkey1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbddkey1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddkey2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbddkey2))
                .addContainerGap())
        );

        lbdate1.setText("jLabel1");

        lbdate2.setText("jLabel1");

        dcdate2.setDateFormatString("yyyy-MM-dd");

        dcdate1.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout paneldcLayout = new javax.swing.GroupLayout(paneldc);
        paneldc.setLayout(paneldcLayout);
        paneldcLayout.setHorizontalGroup(
            paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbdate1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        paneldcLayout.setVerticalGroup(
            paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldcLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate1)
                    .addComponent(dcdate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneldcLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbdate2)
                    .addComponent(dcdate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rbinactive.setText("Inactive");
        rbinactive.setName("cbinactive"); // NOI18N

        rbactive.setText("Active");
        rbactive.setName("cbactive"); // NOI18N

        javax.swing.GroupLayout panelrbLayout = new javax.swing.GroupLayout(panelrb);
        panelrb.setLayout(panelrbLayout);
        panelrbLayout.setHorizontalGroup(
            panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelrbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbactive)
                    .addComponent(rbinactive))
                .addContainerGap())
        );
        panelrbLayout.setVerticalGroup(
            panelrbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelrbLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbactive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbinactive)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        lbkey2.setText("Some Text:");

        lbkey1.setText("Some Text:");

        javax.swing.GroupLayout paneltbLayout = new javax.swing.GroupLayout(paneltb);
        paneltb.setLayout(paneltbLayout);
        paneltbLayout.setHorizontalGroup(
            paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbkey1)
                    .addComponent(lbkey2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        paneltbLayout.setVerticalGroup(
            paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltbLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey2))
                .addContainerGap())
        );

        lbkey3.setText("Some Text:");

        lbkey4.setText("Some Text:");

        javax.swing.GroupLayout paneltb2Layout = new javax.swing.GroupLayout(paneltb2);
        paneltb2.setLayout(paneltb2Layout);
        paneltb2Layout.setHorizontalGroup(
            paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltb2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbkey4)
                    .addComponent(lbkey3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbkey4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        paneltb2Layout.setVerticalGroup(
            paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltb2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneltb2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbkey3))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneltb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneltb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneldc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneldd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(panelrb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(paneltb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(paneldc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(paneldd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(panelrb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btprint.setText("Print/PDF");
        btprint.setName("btprintpdf"); // NOI18N
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
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcsv)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btview)
                    .addComponent(btcsv)
                    .addComponent(btprint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(null);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btviewActionPerformed
       String func = OVData.getJasperFuncByTitle(jasperGroup, ddreport.getSelectedItem().toString());
       Method mymethod;
           if (func != null && ! func.isEmpty()) {
               try {
                   mymethod = this.getClass().getMethod(func, Boolean.TYPE);
                   mymethod.invoke(this, false);
               } catch (NoSuchMethodException ex) {
                   ex.printStackTrace();
               } catch (SecurityException ex) {
                   ex.printStackTrace();
               } catch (IllegalAccessException ex) {
                   ex.printStackTrace();
               } catch (IllegalArgumentException ex) {
                   ex.printStackTrace();
               } catch (InvocationTargetException ex) {
                   ex.printStackTrace();
               }
           }
          
    }//GEN-LAST:event_btviewActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
       int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            reinitpanels("OrderMaint", true, new String[]{tablereport.getValueAt(row, 1).toString()});
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_btcsvActionPerformed

    private void ddreportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddreportActionPerformed
      
       if (! isLoad)  { 
       String func = OVData.getJasperFuncByTitle(jasperGroup, ddreport.getSelectedItem().toString());
       Method mymethod;
           if (func != null && ! func.isEmpty()) {
               try {
                   mymethod = this.getClass().getMethod(func, Boolean.TYPE);
                   mymethod.invoke(this, true);
               } catch (NoSuchMethodException ex) {
                   ex.printStackTrace();
               } catch (SecurityException ex) {
                   ex.printStackTrace();
               } catch (IllegalAccessException ex) {
                   ex.printStackTrace();
               } catch (IllegalArgumentException ex) {
                   ex.printStackTrace();
               } catch (InvocationTargetException ex) {
                   ex.printStackTrace();
               }
           }
       }
      
    }//GEN-LAST:event_ddreportActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper(ddreport.getSelectedItem().toString(), tablereport, jaspermap.get(ddreport.getSelectedItem().toString()) );
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btview;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcdate1;
    private com.toedter.calendar.JDateChooser dcdate2;
    private javax.swing.JComboBox<String> ddkey1;
    private javax.swing.JComboBox<String> ddkey2;
    private javax.swing.JComboBox<String> ddreport;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbdate1;
    private javax.swing.JLabel lbdate2;
    private javax.swing.JLabel lbddkey1;
    private javax.swing.JLabel lbddkey2;
    private javax.swing.JLabel lbkey1;
    private javax.swing.JLabel lbkey2;
    private javax.swing.JLabel lbkey3;
    private javax.swing.JLabel lbkey4;
    private javax.swing.JPanel paneldc;
    private javax.swing.JPanel paneldd;
    private javax.swing.JPanel panelrb;
    private javax.swing.JPanel paneltb;
    private javax.swing.JPanel paneltb2;
    private javax.swing.JRadioButton rbactive;
    private javax.swing.JRadioButton rbinactive;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbkey1;
    private javax.swing.JTextField tbkey2;
    private javax.swing.JTextField tbkey3;
    private javax.swing.JTextField tbkey4;
    // End of variables declaration//GEN-END:variables
}
