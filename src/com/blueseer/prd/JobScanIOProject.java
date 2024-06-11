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

import com.blueseer.inv.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.hrm.hrmData;
import static com.blueseer.hrm.hrmData.getEmpIDByFormalName;
import static com.blueseer.inv.invData.addUOMMstr;
import static com.blueseer.inv.invData.deleteUOMMstr;
import static com.blueseer.inv.invData.getUOMMstr;
import com.blueseer.inv.invData.uom_mstr;
import static com.blueseer.inv.invData.updateUOMMstr;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.getServiceOrderMstr;
import static com.blueseer.ord.ordData.isServiceOrderGeneric;
import com.blueseer.ord.ordData.svd_det;
import static com.blueseer.prd.prdData.addPlanOpDet;
import static com.blueseer.prd.prdData.deletePlanOpDet;
import static com.blueseer.prd.prdData.getJobClockDetail;
import static com.blueseer.prd.prdData.getPlanOpDet;
import static com.blueseer.prd.prdData.getPlanOpLastOp;
import static com.blueseer.prd.prdData.updatePlanOPDate;
import static com.blueseer.prd.prdData.updatePlanOPDesc;
import static com.blueseer.prd.prdData.updatePlanOPNotes;
import static com.blueseer.prd.prdData.updatePlanOPOperator;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.addPlanOperation;
import static com.blueseer.sch.schData.getPlanMstr;
import static com.blueseer.sch.schData.getPlanOperation;
import com.blueseer.sch.schData.plan_mstr;
import com.blueseer.sch.schData.plan_operation;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.canUpdate;
import static com.blueseer.utl.OVData.getSysMetaValue;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
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
public class JobScanIOProject extends javax.swing.JPanel implements IBlueSeerT { 

    // global variable declarations
    boolean isLoad = false;
    boolean isOpScan = false;
    boolean requireOpScan = false;
    boolean hasInit = false;
    int planstatus = 0;
    String plantype = "";
    String chartfilepath = OVData.getSystemTempDirectory() + "/" + "jobm.jpg";
    
    public static plan_mstr x = null;
    public static ArrayList<plan_operation> plolist = null;
    ArrayList<String[]> plodmatl = new ArrayList<String[]>();
    String[] jobop = new String[]{"",""};
    // global datatablemodel declarations    
    javax.swing.table.DefaultTableModel materialmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("id"), 
                getGlobalColumnTag("operation"), 
                getGlobalColumnTag("type"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("quantity"), 
                getGlobalColumnTag("cost"),
                getGlobalColumnTag("operator"),
                getGlobalColumnTag("consumable")
            }){
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 4 || col == 5)       
                    return Double.class; 
                else if (col == 0) {
                    return Integer.class;
                } else return String.class;  //other columns accept String values  
              }  
            };
    
    javax.swing.table.DefaultTableModel clockmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("operation"), 
                getGlobalColumnTag("empid"),
                getGlobalColumnTag("name"),
                getGlobalColumnTag("rate"),
                getGlobalColumnTag("indate"), 
                getGlobalColumnTag("intime"), 
                getGlobalColumnTag("outdate"),
                getGlobalColumnTag("outtime"),
                getGlobalColumnTag("totalhours"),
                getGlobalColumnTag("status")
                
            }){
              @Override  
              public Class getColumnClass(int col) {  
                return String.class;  //other columns accept String values  
              }  
            };
                
    /**
     * Creates new form CarrierMaintPanel
     */
    public JobScanIOProject() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type;
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
             switch(this.type) {
                case "add" :
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get")) {
             updateForm();
             tbkey.requestFocus();
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
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
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
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
         
    public void setComponentDefaultValues() {
       isLoad = true;
       
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Notes", panelNotes); 
        jTabbedPane1.add("TimeClock", panelClock);
        jTabbedPane1.add("Charts", panelChart);
       
        lblchart.setHorizontalTextPosition(SwingConstants.CENTER);
        ddchart.removeAllItems();
        ddchart.addItem("Total Hours By Operator");
        ddchart.addItem("Total Cost By Operator");
        ddchart.addItem("Total Operational Cost");
        ddchart.addItem("Total Cost Material vs Labor");
        ddchart.setSelectedIndex(0);
        cbconsumable.setSelected(true);
        
        tbkey.setText("");
        tbitem.setText("");
        tbopdesc.setText("");
        lblmessage.setText("");
        
        plodmatl.clear();
        
        buttonGroup1.add(rbmaterial);
        buttonGroup1.add(rbtooling);
        buttonGroup1.add(rbservice);
        rbmaterial.setEnabled(true);
        rbtooling.setEnabled(true);
        rbservice.setEnabled(true);
        tbcost.setText("");
        tbqty.setText("");
        rbmaterial.setSelected(true);
        
        clockmodel.setNumRows(0);
        materialmodel.setNumRows(0);
        materialtable.setModel(materialmodel);
        historytable.setModel(clockmodel);
        materialtable.getTableHeader().setReorderingAllowed(false);
        
        if (! hasInit) {
            TableColumnModel tcm = historytable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(3));
        hasInit = true;
        }
        
        ArrayList<String> operators = hrmData.getEmpNameAll();
        ddoperator.removeAllItems();
        for (String operator : operators) {
            ddoperator.addItem(operator);
          }
        ddoperator.insertItemAt("", 0);
        
        ddmaterial.removeAllItems();
        ArrayList<String> mylist = invData.getItemsByTypeExcept(new String[]{"TOOLING"});
        for (int i = 0; i < mylist.size(); i++) {
            ddmaterial.addItem(mylist.get(i));
        }
        ddmaterial.insertItemAt("", 0);
        ddmaterial.setSelectedIndex(0);
        
        ddtooling.removeAllItems();
        ArrayList<String> tooling = invData.getItemsByType("TOOLING");
        for (int i = 0; i < tooling.size(); i++) {
            ddtooling.addItem(tooling.get(i));
        }
        ddtooling.insertItemAt("", 0);
        ddtooling.setSelectedIndex(0);
        
       isLoad = false;
    }
    
    public void newAction(String x) {
        setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        lblmessage.setText("");
        if (x[0].equals("0")) { 
           setPanelComponentState(this, true);
           tbkey.setEditable(false);
           tbkey.setForeground(Color.blue);
           tbitem.setEnabled(false);
           ddtooling.setEnabled(false);
           ddmaterial.setEnabled(true);
           if (! isServiceOrderGeneric(tborder.getText())) {
               btaddop.setEnabled(false);
           }
           if (isOpScan) {
               ddop.setEnabled(false);
           }
           
           if (planstatus > 0 || planstatus < 0) {
               lblmessage.setText("job is closed");
               lblmessage.setForeground(Color.blue);
               btcommit.setEnabled(false);
               btaddmatl.setEnabled(false);
               btdeletematl.setEnabled(false);
               btupdate.setEnabled(false);
           }
           
           if (planstatus == 0 && ! plantype.equals("SRVC")) {
               lblmessage.setText("inventory job type...view only");
               lblmessage.setForeground(Color.red);
               btcommit.setEnabled(false);
               btaddmatl.setEnabled(false);
               btdeletematl.setEnabled(false);
               btupdate.setEnabled(false);
           }
           
        } else {
           tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(dbaction x) {
        if (! canUpdate(this.getClass().getName())) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        return true;
    }
    
    public void initvars(String[] arg) {
       isLoad = true;
       requireOpScan = BlueSeerUtils.ConvertStringToBool(getSysMetaValue("system", "inventorycontrol", "operation_scan_required"));
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
        isLoad = false;
    }
    
    public String[] addRecord(String[] x) {
     String[] m = addUOMMstr(createRecord());
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = updateUOMMstr(createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
      String[] m;
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteUOMMstr(createRecord()); 
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
      
      if (key[0].contains("-")) {
          isOpScan = true;
          jobop = key[0].split("-",-1);
          jobop[1] = "SRVC"; // override jobop[1] assignment
          x = getPlanMstr(jobop);
      } else {
          isOpScan = false;
          jobop[0] = key[0];
          jobop[1] = "SRVC";
          if (key.length > 1 && ! key[1].isBlank()) {
              jobop[1] = key[1];
          }
          
          x = getPlanMstr(jobop); 
      }
      
      plolist = getPlanOperation(jobop[0]);
      getClockRecords();
      
      return x.m();  
    }
    
    public invData.uom_mstr createRecord() {
        invData.uom_mstr x = new invData.uom_mstr(null, 
           tbkey.getText(),
           tbitem.getText()
        );
        return x;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getJobSRVCBrowseUtil(luinput.getText(),0, "plan_nbr"); 
        } else if (lurb2.isSelected()) {
         luModel = DTData.getJobSRVCBrowseUtil(luinput.getText(),0, "plan_order");
        } else {
         luModel = DTData.getJobSRVCBrowseUtil(luinput.getText(),0, "sv_cust");    
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lblorder", this.getClass().getSimpleName()),
                getClassLabelTag("lblcust", this.getClass().getSimpleName()) ); 
        
    }

    public void updateForm() {
        
        tbkey.setText(String.valueOf(x.plan_nbr()));
        tborder.setText(x.plan_order());
        planstatus = x.plan_status();
        plantype = x.plan_type();
        
       
      //  cbapply.setSelected(BlueSeerUtils.ConvertStringToBool(x.car_apply()));
        
        
       
        ddop.removeAllItems();
        ddopnotes.removeAllItems();
        
       
        for (plan_operation plo : plolist) {
            ddop.addItem(String.valueOf(plo.plo_op()));
            ddopnotes.addItem(String.valueOf(plo.plo_op()));
        }

        ddop.insertItemAt("", 0);
        ddopnotes.insertItemAt("", 0);

        if (! jobop[1].isBlank()) {
         ddop.setSelectedItem(jobop[1]);
         ddopnotes.setSelectedItem(jobop[1]);
        } else {
         ddop.setSelectedIndex(0);  
         ddopnotes.setSelectedIndex(0);
        }
            
       
        plodmatl = getPlanOpDet(jobop[0]);
        ddop.setSelectedIndex(0);
        getClockRecords();
        displayTotals();  
        
        setAction(x.m());
    }
    
    // misc methods
    
    public void chartIt() {
               
        DefaultPieDataset dataset = new DefaultPieDataset();
               
        double totmatl = 0.00;
        double opmatl = 0.00;
        double totlbr = 0.00;
        double oplbr = 0.00;
        double tothours = 0.00;
        double ophours = 0.00;
        String title = "";
        NumberFormat nf = null;
        LinkedHashMap<String, Double> lhm = new LinkedHashMap<String, Double>();
         /*
         for (int j = 0; j < plodmatl.size(); j++) {
             if (! ddop.getSelectedItem().toString().isBlank() && ddop.getSelectedItem().toString().equals(plodmatl.get(j)[2])) {
               opmatl = opmatl + (bsParseDouble(plodmatl.get(j)[5]) * bsParseDouble(plodmatl.get(j)[6])   );   
             }
             totmatl = totmatl + (bsParseDouble(plodmatl.get(j)[5]) * bsParseDouble(plodmatl.get(j)[6])   ); 
         }
         */
         if (ddchart.getSelectedIndex() == 0) {
            nf = NumberFormat.getNumberInstance();
            title = "Hours By Operator";
            for (int j = 0; j < clockmodel.getRowCount(); j++) {
                if (lhm.containsKey(clockmodel.getValueAt(j, 2).toString())) {
                   lhm.put(clockmodel.getValueAt(j, 2).toString(), lhm.get(clockmodel.getValueAt(j, 2).toString()) + Double.valueOf(clockmodel.getValueAt(j, 8).toString()));
                } else {
                   lhm.put(clockmodel.getValueAt(j, 2).toString(), Double.valueOf(clockmodel.getValueAt(j, 8).toString()));
                }
            }   
         }
         if (ddchart.getSelectedIndex() == 1) {
            nf = NumberFormat.getCurrencyInstance();
            title = "Cost By Operator";
            for (int j = 0; j < clockmodel.getRowCount(); j++) {
                if (lhm.containsKey(clockmodel.getValueAt(j, 2).toString())) {
                   lhm.put(clockmodel.getValueAt(j, 2).toString(), lhm.get(clockmodel.getValueAt(j, 2).toString()) + (Double.valueOf(clockmodel.getValueAt(j, 3).toString()) * Double.valueOf(clockmodel.getValueAt(j, 8).toString())));
                } else {
                   lhm.put(clockmodel.getValueAt(j, 2).toString(), (Double.valueOf(clockmodel.getValueAt(j, 3).toString()) * Double.valueOf(clockmodel.getValueAt(j, 8).toString())));
                }
            }   
         }
         if (ddchart.getSelectedIndex() == 2) {
             nf = NumberFormat.getCurrencyInstance();
             title = "Total Cost By Operation";
            for (int j = 0; j < plodmatl.size(); j++) {
                String[] s = plodmatl.get(j);
                if (lhm.containsKey(s[2])) {
                   lhm.put(s[2], lhm.get(s[2]) + (Double.valueOf(s[5]) * Double.valueOf(s[6]) ));
                } else {
                   lhm.put(s[2], (Double.valueOf(s[5]) * Double.valueOf(s[6]) ));
                }
            } 
            for (int j = 0; j < clockmodel.getRowCount(); j++) {
                if (lhm.containsKey(clockmodel.getValueAt(j, 0).toString())) {
                   lhm.put(clockmodel.getValueAt(j, 0).toString(), lhm.get(clockmodel.getValueAt(j, 0).toString()) + ( Double.valueOf(clockmodel.getValueAt(j, 3).toString()) * Double.valueOf(clockmodel.getValueAt(j, 8).toString()) )  );
                } else {
                   lhm.put(clockmodel.getValueAt(j, 0).toString(), ( Double.valueOf(clockmodel.getValueAt(j, 3).toString()) * Double.valueOf(clockmodel.getValueAt(j, 8).toString()) ));
                }
            }   
         }
         
         if (ddchart.getSelectedIndex() == 3) {
             nf = NumberFormat.getCurrencyInstance();
             title = "Total Cost Material vs Labor";
            for (int j = 0; j < plodmatl.size(); j++) {
                if (! plodmatl.get(j)[10].equals("1")) {  // if not consumable...do not add in calc
                 continue;
                }
                String[] s = plodmatl.get(j);
                totmatl += (Double.valueOf(s[5]) * Double.valueOf(s[6]) );
            } 
            for (int j = 0; j < clockmodel.getRowCount(); j++) {
                totlbr += (Double.valueOf(clockmodel.getValueAt(j, 3).toString()) * Double.valueOf(clockmodel.getValueAt(j, 8).toString()));
               
            }  
            lhm.put("material", totmatl);
            lhm.put("labor", totlbr);
         }
                
                for (Map.Entry<String, Double> z : lhm.entrySet()) {
                  dataset.setValue(z.getKey(), z.getValue());
                }
                
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", nf, new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, (panelChart.getWidth() - 70), this.getHeight() - 200);
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        lblchart.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              
       
    }
    
    
    public void getClockRecords() {
        clockmodel.setNumRows(0);
        ArrayList<String[]> clockd = getJobClockDetail(Integer.valueOf(jobop[0]));
        String status = "";
        for (String[] c : clockd) {
            if (c[11].equals("01")) {
                status = getGlobalProgTag("clockin");
            } else {
                status = getGlobalProgTag("clockout");
            }
            clockmodel.addRow(new Object[] { 
                c[1],
                c[2], 
                c[3], 
                c[4],
                c[6],
                c[7],
                c[8],
                c[9],
                c[10],
                status});
            
        }
    }
    
    public void displayTotals() {
        double totmatl = 0.00;
        double opmatl = 0.00;
        double totlbr = 0.00;
        double oplbr = 0.00;
        double tothours = 0.00;
        double ophours = 0.00;
        
         for (int j = 0; j < plodmatl.size(); j++) {
             if (! plodmatl.get(j)[10].equals("1")) {  // if not consumable...do not add in calc
                 continue;
             }
             if (! ddop.getSelectedItem().toString().isBlank() && ddop.getSelectedItem().toString().equals(plodmatl.get(j)[2])) {
               opmatl = opmatl + (bsParseDouble(plodmatl.get(j)[5]) * bsParseDouble(plodmatl.get(j)[6])   );   
             }
             totmatl = totmatl + (bsParseDouble(plodmatl.get(j)[5]) * bsParseDouble(plodmatl.get(j)[6])   ); 
         }
         
         for (int j = 0; j < clockmodel.getRowCount(); j++) {
             if (! ddop.getSelectedItem().toString().isBlank() && ddop.getSelectedItem().toString().equals(clockmodel.getValueAt(j, 1))) {
               oplbr = oplbr + (bsParseDouble(clockmodel.getValueAt(j, 3).toString()) * bsParseDouble(clockmodel.getValueAt(j, 8).toString())   );   
               ophours = ophours +  bsParseDouble(clockmodel.getValueAt(j, 8).toString());
             }
             totlbr = totlbr + (bsParseDouble(clockmodel.getValueAt(j, 3).toString()) * bsParseDouble(clockmodel.getValueAt(j, 8).toString())   ); 
             tothours = tothours +  bsParseDouble(clockmodel.getValueAt(j, 8).toString());
         }
         
         
         tbopmatlcost.setText(bsFormatDouble(opmatl));
         tbopmatlcosttot.setText(bsFormatDouble(totmatl));
         tboplaborcost.setText(bsFormatDouble(oplbr));
         tboplaborcosttot.setText(bsFormatDouble(totlbr));
         tbtotalcost.setText(bsFormatDouble(totmatl + totlbr));
         tbophours.setText(bsFormatDouble(ophours));
         tbtothours.setText(bsFormatDouble(tothours));
    }
    
    public void printticket(String jobid, String bustitle, String operation) {
        
       try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
                String jasperfile = "jobSVticket.jasper";
                if (! operation.isBlank()) {
                 jasperfile = "operationSVticket.jasper";
                 bustitle = "Operation Ticket";
                } 
                
                HashMap hm = new HashMap();
                hm.put("BUSINESSTITLE", bustitle);
                hm.put("REPORT_TITLE", jasperfile);
                hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags); 
                hm.put("myid",  jobid);
                if (! operation.isBlank()) {
                    hm.put("myop",  operation);
                    hm.put("myidop", jobid + "-" + operation);
                }
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
    
    public void printOperationTicket(String jobid, String op, String plantype, String bustitle) {
        
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
                
                if (plantype.equals("SRVC")) {
                    jasperfile = "operationSVticket.jasper";
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
    
    public String[] autoInvoiceServiceOrder(String nbr, String jobid) {
        
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        int shipperid = OVData.getNextNbr("shipper");   
        ordData.sv_mstr sv = getServiceOrderMstr(new String[]{nbr});  
        shpData.ship_mstr sh = shpData.createShipMstrJRT(String.valueOf(shipperid), sv.sv_site(),
                             String.valueOf(shipperid), 
                              sv.sv_cust(),
                              sv.sv_ship(),
                              bsNumberToUS(sv.sv_nbr()),
                              sv.sv_po(),  // po
                              jobid,  // ref
                              sv.sv_due_date(),
                              sv.sv_create_date(),
                              sv.sv_rmks(),
                              "",
                              "S", "", sv.sv_site()); 
        
        ArrayList<svd_det> svdarray = ordData.getServiceOrderDet(new String[]{nbr});
        ArrayList<String[]> detail = new ArrayList<String[]>();
        for (svd_det svd : svdarray) {
            detail.add(new String[]{
                String.valueOf(svd.svd_line()),
                svd.svd_item(),
                svd.svd_type(),
                svd.svd_desc(),
                svd.svd_nbr(),
                String.valueOf(svd.svd_qty()),
                String.valueOf(svd.svd_netprice()),
                svd.svd_uom()
            });
        }
        
        ArrayList<shpData.ship_det> shd = shpData.createShipDetJRTmin(detail, String.valueOf(shipperid), sv.sv_create_date(), sv.sv_site());
        shpData.addShipperTransaction(shd, sh);
        shpData.updateShipperSAC(String.valueOf(shipperid));
        schData.updatePlanOrderStatus(tbkey.getText(),"closed");
        // now confirm shipment
        String[] message = confirmShipperTransaction("serviceorder", String.valueOf(shipperid), now);
        message = new String[]{"0", "Service Order has been invoiced"};    
       
        // autopost
        if (OVData.isAutoPost()) {
            fglData.PostGL();
        }        
        return message;
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbitem = new javax.swing.JTextField();
        tbkey = new javax.swing.JTextField();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        ddop = new javax.swing.JComboBox<>();
        tborder = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ddmaterial = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btaddmatl = new javax.swing.JButton();
        tbqty = new javax.swing.JTextField();
        tbcost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        tbopmatlcost = new javax.swing.JTextField();
        tboplaborcosttot = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tbopmatlcosttot = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tboplaborcost = new javax.swing.JTextField();
        tbophours = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbtothours = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tbtotalcost = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        materialtable = new javax.swing.JTable();
        ddtooling = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        rbmaterial = new javax.swing.JRadioButton();
        rbtooling = new javax.swing.JRadioButton();
        rbservice = new javax.swing.JRadioButton();
        btdeletematl = new javax.swing.JButton();
        lblmaterial = new javax.swing.JLabel();
        lbltooling = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        btcommit = new javax.swing.JButton();
        lblmessage = new javax.swing.JLabel();
        ddoperator = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        btclock = new javax.swing.JButton();
        btaddop = new javax.swing.JButton();
        tbopdesc = new javax.swing.JTextField();
        btbrowse = new javax.swing.JButton();
        cbconsumable = new javax.swing.JCheckBox();
        panelNotes = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tanotes = new javax.swing.JTextArea();
        btupdatenotes = new javax.swing.JButton();
        ddopnotes = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        panelClock = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        historytable = new javax.swing.JTable();
        panelChart = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btrefreshchart = new javax.swing.JButton();
        ddchart = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblchart = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Project / Service Job View"));
        panelMain.setName("panelmain"); // NOI18N
        panelMain.setPreferredSize(new java.awt.Dimension(820, 550));

        jLabel1.setText("Job:");
        jLabel1.setName("lblid"); // NOI18N

        jLabel2.setText("Service:");
        jLabel2.setName("lbldesc"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        ddop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddopActionPerformed(evt);
            }
        });

        jLabel3.setText("Order:");

        jLabel4.setText("Operation:");

        ddmaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddmaterialActionPerformed(evt);
            }
        });

        jLabel5.setText("Material:");

        btaddmatl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddmatl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddmatlActionPerformed(evt);
            }
        });

        tbqty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbqtyActionPerformed(evt);
            }
        });

        jLabel6.setText("Qty:");

        jLabel7.setText("Cost:");

        jLabel9.setText("Op Labor Cost:");

        jLabel11.setText("Total Labor Cost:");

        jLabel12.setText("Op Hours:");

        jLabel8.setText("Op Matl Cost:");

        jLabel10.setText("Total Matl Cost:");

        jLabel13.setText("Total Hours:");

        jLabel14.setText("Total Cost:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbopmatlcost, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(tbophours)
                    .addComponent(tbopmatlcosttot))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbtotalcost, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(tboplaborcosttot)
                    .addComponent(tboplaborcost)
                    .addComponent(tbtothours))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbopmatlcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tboplaborcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbopmatlcosttot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tboplaborcosttot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbophours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(tbtothours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotalcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Material / Tooling Usage"));

        materialtable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(materialtable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                .addContainerGap())
        );

        ddtooling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtoolingActionPerformed(evt);
            }
        });

        jLabel15.setText("Tooling:");

        rbmaterial.setText("Material");
        rbmaterial.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbmaterialStateChanged(evt);
            }
        });
        rbmaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmaterialActionPerformed(evt);
            }
        });

        rbtooling.setText("Tooling");
        rbtooling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtoolingActionPerformed(evt);
            }
        });

        rbservice.setText("Service");
        rbservice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbserviceActionPerformed(evt);
            }
        });

        btdeletematl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeletematl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletematlActionPerformed(evt);
            }
        });

        btprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/print.png"))); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel16.setText("Operator:");

        btupdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel19.setText("Date:");

        btclock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clock.png"))); // NOI18N
        btclock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclockActionPerformed(evt);
            }
        });

        btaddop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddopActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        cbconsumable.setText("Consumable");

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddop, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbopdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(259, 259, 259))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tborder, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbkey, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addGap(34, 34, 34)
                                .addComponent(btprint, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclock, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btcommit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(ddtooling, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbltooling, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btaddmatl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btdeletematl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelMainLayout.createSequentialGroup()
                                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(panelMainLayout.createSequentialGroup()
                                                    .addComponent(rbmaterial)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(rbtooling))
                                                .addComponent(ddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbservice)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbconsumable)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
            .addGroup(panelMainLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addComponent(btlookup)
                        .addComponent(btclear, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(btprint)
                    .addComponent(btcommit)
                    .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btclock)
                    .addComponent(btbrowse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tborder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(9, 9, 9)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btaddop, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(tbopdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddoperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbmaterial)
                            .addComponent(rbtooling)
                            .addComponent(rbservice)
                            .addComponent(cbconsumable))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddtooling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15)))
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(lblmaterial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbltooling, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btaddmatl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btdeletematl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(panelMain);

        panelNotes.setPreferredSize(new java.awt.Dimension(800, 550));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Operational Note"));

        tanotes.setColumns(20);
        tanotes.setRows(5);
        jScrollPane3.setViewportView(tanotes);

        btupdatenotes.setText("Update Notes");
        btupdatenotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatenotesActionPerformed(evt);
            }
        });

        ddopnotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddopnotesActionPerformed(evt);
            }
        });

        jLabel17.setText("Operation:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddopnotes, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 457, Short.MAX_VALUE)
                .addComponent(btupdatenotes)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdatenotes)
                    .addComponent(ddopnotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelNotesLayout = new javax.swing.GroupLayout(panelNotes);
        panelNotes.setLayout(panelNotesLayout);
        panelNotesLayout.setHorizontalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelNotesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelNotesLayout.setVerticalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
            .addGroup(panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelNotesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        add(panelNotes);

        panelClock.setBorder(javax.swing.BorderFactory.createTitledBorder("JobID Scan History"));

        historytable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(historytable);

        javax.swing.GroupLayout panelClockLayout = new javax.swing.GroupLayout(panelClock);
        panelClock.setLayout(panelClockLayout);
        panelClockLayout.setHorizontalGroup(
            panelClockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClockLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        panelClockLayout.setVerticalGroup(
            panelClockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClockLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(panelClock);

        panelChart.setPreferredSize(new java.awt.Dimension(800, 550));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Charts"));

        btrefreshchart.setText("Refresh");
        btrefreshchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrefreshchartActionPerformed(evt);
            }
        });

        ddchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddchartActionPerformed(evt);
            }
        });

        jLabel18.setText("Selection:");

        jPanel1.setLayout(new java.awt.CardLayout());
        jPanel1.add(lblchart, "card2");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddchart, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 409, Short.MAX_VALUE)
                .addComponent(btrefreshchart)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btrefreshchart)
                    .addComponent(ddchart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelChartLayout = new javax.swing.GroupLayout(panelChart);
        panelChart.setLayout(panelChartLayout);
        panelChartLayout.setHorizontalGroup(
            panelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(panelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelChartLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelChartLayout.setVerticalGroup(
            panelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
            .addGroup(panelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelChartLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        add(panelChart);
    }// </editor-fold>//GEN-END:initComponents
    
    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btaddmatlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddmatlActionPerformed
        String datatype = "";
        String item = "";
        String itemdesc = "";
        
        if (ddop.getSelectedItem().toString().isBlank()) {
            bsmf.MainFrame.show("An operation must be selected");
            return;
        }
        
        if (rbmaterial.isSelected()) {
            datatype = "material";
            item = ddmaterial.getSelectedItem().toString();
            itemdesc = ddmaterial.getSelectedItem().toString() + " -- " + lblmaterial.getText();
        }
        if (rbtooling.isSelected()) {
            datatype = "tooling";
            item = ddtooling.getSelectedItem().toString();
            itemdesc = ddtooling.getSelectedItem().toString() + " -- " + lbltooling.getText();
        }
        if (rbservice.isSelected()) {
            datatype = "service";
            item = tbitem.getText().replace("'", "");
            itemdesc = item;
        }
        int id = addPlanOpDet(tbkey.getText(), 
                ddop.getSelectedItem().toString(), 
                datatype, 
                item, 
                itemdesc,
                Double.valueOf(tbqty.getText()), 
                Double.valueOf(tbcost.getText()), 
                "",
                BlueSeerUtils.boolToInt(cbconsumable.isSelected()));
        materialmodel.addRow(new Object[] { 
                id,
                ddop.getSelectedItem().toString(), 
                datatype, 
                item, 
                Double.valueOf(tbqty.getText()), 
                Double.valueOf(tbcost.getText()), 
                "",
                cbconsumable.isSelected()});
        
        plodmatl = getPlanOpDet(jobop[0]);
        getClockRecords();
        displayTotals();
       
    }//GEN-LAST:event_btaddmatlActionPerformed

    private void tbqtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbqtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbqtyActionPerformed

    private void btdeletematlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletematlActionPerformed
          int[] rows = materialtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) materialtable.getModel()).removeRow(i);
            deletePlanOpDet(materialtable.getValueAt(i, 0).toString()); 
        }
        
        getClockRecords();
        displayTotals();

    }//GEN-LAST:event_btdeletematlActionPerformed

    private void rbmaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmaterialActionPerformed
        if (! isLoad && rbmaterial.isSelected()) {
            tbitem.setEnabled(false);
            ddtooling.setEnabled(false);
            ddmaterial.setEnabled(true);
        } 
    }//GEN-LAST:event_rbmaterialActionPerformed

    private void rbtoolingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtoolingActionPerformed
        if (! isLoad && rbtooling.isSelected()) {
            tbitem.setEnabled(false);
            ddtooling.setEnabled(true);
            ddmaterial.setEnabled(false);
        } 
    }//GEN-LAST:event_rbtoolingActionPerformed

    private void rbserviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbserviceActionPerformed
        if (! isLoad && rbservice.isSelected()) {
            tbitem.setEnabled(true);
            ddtooling.setEnabled(false);
            ddmaterial.setEnabled(false);
        } 
    }//GEN-LAST:event_rbserviceActionPerformed

    private void ddopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddopActionPerformed
         if (! isLoad && ddop.getSelectedItem() != null) {
             if (ddop.getSelectedItem().toString().isBlank()) {
                tbopdesc.setText("");
                materialmodel.setNumRows(0);
                ddoperator.setSelectedItem("");
                dcdate.setDate(null);
                for (String[] s : plodmatl) {
                    if (s[3].equals("material") || s[3].equals("tooling") || s[3].equals("service")) {
                    materialmodel.addRow(new Object[] { 
                    s[0],
                    s[2], 
                    s[3], 
                    s[4], 
                    Double.valueOf(s[5]), 
                    Double.valueOf(s[6]), 
                    s[7],
                    BlueSeerUtils.ConvertStringToBool(s[10])}); 
                    }
                }
             } else {
                plan_operation plo = getPlanOperation(Integer.valueOf(jobop[0]), Integer.valueOf(ddop.getSelectedItem().toString()));
                tbopdesc.setText(plo.plo_desc()); 
                materialmodel.setNumRows(0);
                ddoperator.setSelectedItem(plo.plo_operatorname());
                dcdate.setDate(parseDate(plo.plo_date()));
                for (String[] s : plodmatl) {
                    if (s[2].equals(ddop.getSelectedItem().toString()) && (s[3].equals("material") || s[3].equals("tooling") || s[3].equals("service"))) {
                    materialmodel.addRow(new Object[] { 
                    s[0],
                    s[2], 
                    s[3], 
                    s[4], 
                    Double.valueOf(s[5]), 
                    Double.valueOf(s[6]), 
                    s[7],
                    BlueSeerUtils.ConvertStringToBool(s[10])}); 
                    }
                }
                
             }
             
            getClockRecords();
            displayTotals();
        }
    }//GEN-LAST:event_ddopActionPerformed

    private void ddmaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddmaterialActionPerformed
        if (! isLoad && ddmaterial.getSelectedItem() != null) {
            lblmaterial.setText(invData.getItemDesc(ddmaterial.getSelectedItem().toString()));
            tbcost.setText(bsNumber(invData.getItemPurchPrice(ddmaterial.getSelectedItem().toString())));
        }
    }//GEN-LAST:event_ddmaterialActionPerformed

    private void ddtoolingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtoolingActionPerformed
        if (! isLoad && ddtooling.getSelectedItem() != null) {
            lbltooling.setText(invData.getItemDesc(ddtooling.getSelectedItem().toString()));
            tbcost.setText(bsNumber(invData.getItemPurchPrice(ddtooling.getSelectedItem().toString())));
        }
    }//GEN-LAST:event_ddtoolingActionPerformed

    private void rbmaterialStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbmaterialStateChanged
        if (! isLoad && rbmaterial.isSelected()) {
            tbitem.setEnabled(false);
            ddtooling.setEnabled(false);
            ddmaterial.setEnabled(true);
        } 
    }//GEN-LAST:event_rbmaterialStateChanged

    private void ddopnotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddopnotesActionPerformed
        if (ddopnotes.getSelectedItem() != null && ! ddopnotes.getSelectedItem().toString().isBlank()) {
        plan_operation plo = getPlanOperation(Integer.valueOf(jobop[0]), Integer.valueOf(ddopnotes.getSelectedItem().toString()));
        tanotes.setText(plo.plo_notes());
        }
    }//GEN-LAST:event_ddopnotesActionPerformed

    private void btupdatenotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatenotesActionPerformed
        updatePlanOPNotes(jobop[0], ddopnotes.getSelectedItem().toString(), tanotes.getText());
        bsmf.MainFrame.show("Notes update complete");
    }//GEN-LAST:event_btupdatenotesActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        printticket(jobop[0], "Job Service Ticket", ddop.getSelectedItem().toString());
    }//GEN-LAST:event_btprintActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         String[] message = autoInvoiceServiceOrder(tborder.getText(), tbkey.getText());
         if (message[0].equals("1")) { // if error
           bsmf.MainFrame.show(message[1]);
         } else {
           executeTask(dbaction.get, new String[]{tbkey.getText()});
         }
         initvars(null);
        } 
    }//GEN-LAST:event_btcommitActionPerformed

    private void btrefreshchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrefreshchartActionPerformed
        chartIt();
    }//GEN-LAST:event_btrefreshchartActionPerformed

    private void ddchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddchartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddchartActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (ddop.getItemCount() > 0 && ddop.getSelectedItem().toString().isBlank()) {
            bsmf.MainFrame.show("Must choose an operation");
            return;
        }
        
        updatePlanOPDesc(tbkey.getText(), ddop.getSelectedItem().toString(), tbopdesc.getText());
        
        if (ddoperator.getItemCount() > 0 && ddoperator.getSelectedItem() != null) {
            if (ddoperator.getSelectedItem().toString().isBlank()) {
                updatePlanOPOperator(tbkey.getText(), ddop.getSelectedItem().toString(), "", "");
            } else {
                updatePlanOPOperator(tbkey.getText(), ddop.getSelectedItem().toString(), getEmpIDByFormalName(ddoperator.getSelectedItem().toString()), ddoperator.getSelectedItem().toString());
            }
            
        }
        if (dcdate.getDate() != null) {
            updatePlanOPDate(tbkey.getText(), ddop.getSelectedItem().toString(), setDateDB(dcdate.getDate())); 
        }
        bsmf.MainFrame.show("Operation has been updated");
    }//GEN-LAST:event_btupdateActionPerformed

    private void btclockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclockActionPerformed
       
               String mypanel = "JobScanIO";
               if (! checkperms(mypanel)) { return; }
               String[] args = new String[]{""};
               reinitpanels(mypanel, true, args);
        
    }//GEN-LAST:event_btclockActionPerformed

    private void btaddopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddopActionPerformed
        if (! tbkey.getText().isBlank()) {
        int newop = getPlanOpLastOp(tbkey.getText());
        String opdesc = bsmf.MainFrame.input("Enter Operation Description: ");
        plan_operation x = new plan_operation(null, 
                0, // id
                bsParseInt(tbkey.getText()), // parent
                newop + 1, // op
                1, // qty
                0, // comp qty
                "", // cell
                "", // operator
                "", // operatorname
                null, // date 
                "", //status
                bsmf.MainFrame.userid, // userid
                opdesc, // operation description
                "" // notes   
             );
        String[] m = addPlanOperation(x);
            if (m[0].equals("0")) {
                bsmf.MainFrame.show("operation added");
                initvars(new String[]{tbkey.getText()});
            } else {
                bsmf.MainFrame.show("operation not added..." + m[1]);
            }
        }
    }//GEN-LAST:event_btaddopActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        String mypanel = "JobBrowse";
        if (! checkperms(mypanel)) { return; }
        String[] args = new String[]{""};
        reinitpanels(mypanel, true, args);
    }//GEN-LAST:event_btbrowseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btaddmatl;
    private javax.swing.JButton btaddop;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclock;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdeletematl;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btrefreshchart;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdatenotes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbconsumable;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddchart;
    private javax.swing.JComboBox<String> ddmaterial;
    private javax.swing.JComboBox<String> ddop;
    private javax.swing.JComboBox<String> ddoperator;
    private javax.swing.JComboBox<String> ddopnotes;
    private javax.swing.JComboBox<String> ddtooling;
    private javax.swing.JTable historytable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblchart;
    private javax.swing.JLabel lblmaterial;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JLabel lbltooling;
    private javax.swing.JTable materialtable;
    private javax.swing.JPanel panelChart;
    private javax.swing.JPanel panelClock;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelNotes;
    private javax.swing.JRadioButton rbmaterial;
    private javax.swing.JRadioButton rbservice;
    private javax.swing.JRadioButton rbtooling;
    private javax.swing.JTextArea tanotes;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbopdesc;
    private javax.swing.JTextField tbophours;
    private javax.swing.JTextField tboplaborcost;
    private javax.swing.JTextField tboplaborcosttot;
    private javax.swing.JTextField tbopmatlcost;
    private javax.swing.JTextField tbopmatlcosttot;
    private javax.swing.JTextField tborder;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbtotalcost;
    private javax.swing.JTextField tbtothours;
    // End of variables declaration//GEN-END:variables
}
