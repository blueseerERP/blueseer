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
package com.blueseer.ord;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.getCMSDet;
import static com.blueseer.ctr.cusData.getCustShipSet;
import static com.blueseer.ctr.cusData.getDiscountRecsByCust;
import static com.blueseer.edi.ediData.getEDIMetaValueAll;
import static com.blueseer.edi.ediData.getEDIMetaValueDetail;
import com.blueseer.fgl.fglData;
import static com.blueseer.fgl.fglData.getTaxDet;
import static com.blueseer.fgl.fglData.getTaxMetaByMunicipality;
import static com.blueseer.fgl.fglData.getTaxMetaByState;
import static com.blueseer.fgl.fglData.getTaxPercentByState;
import static com.blueseer.fgl.fglData.getTaxPercentByZip;
import com.blueseer.fgl.fglData.taxd_mstr;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getItemDataInit;
import static com.blueseer.inv.invData.getItemQOHTotal;
import static com.blueseer.inv.invData.getWareHouseMstr;
import static com.blueseer.ord.ordData.addOrderTransaction;
import static com.blueseer.ord.ordData.addUpdateSOMeta;
import static com.blueseer.ord.ordData.addUpdateSOMetaNotes;
import static com.blueseer.ord.ordData.deleteOrderMstr;
import static com.blueseer.ord.ordData.getOrderItemAllocatedQty;
import static com.blueseer.ord.ordData.getOrderLines;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import static com.blueseer.ord.ordData.getSOMetaNotes;
import static com.blueseer.ord.ordData.isDuplicatePO;
import static com.blueseer.ord.ordData.orderToInvoice;
import com.blueseer.ord.ordData.salesOrder;
import com.blueseer.ord.ordData.sod_det;
import com.blueseer.ord.ordData.so_mstr;
import com.blueseer.ord.ordData.so_tax;
import com.blueseer.ord.ordData.sod_tax;
import com.blueseer.ord.ordData.sos_det;
import static com.blueseer.ord.ordData.updateOrderTransaction;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData._addShipperTransaction;
import static com.blueseer.shp.shpData._confirmShipperTransaction;
import static com.blueseer.shp.shpData._updateShipperSAC;
import com.blueseer.shp.shpData.ship_mstr;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callChangeDialog;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleWithSymbol;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.logChange;
import static com.blueseer.utl.BlueSeerUtils.logChangeArrays;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.lurb2;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.timediff;
import static com.blueseer.utl.BlueSeerUtils.xZero;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.IBlueSeerV;
import static com.blueseer.utl.OVData.getPackQtyForItem;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.isValidOrder;
import static com.blueseer.utl.OVData.isVoucherShippingSO;
import static com.blueseer.utl.OVData.mailDoc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author vaughnte
 */
public class OrderMaint extends javax.swing.JPanel implements IBlueSeerV {

    // global variable declarations
                boolean isLoad = false;
                Object[][] rData;
                ArrayList<String[]> initDataSets = null;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String status = "";
                String curr = "";
                String basecurr = "";
                String[] rDataDetEvent = null;
                String[] TypeAndPriceAndDisc = new String[]{"","0", "0"};
                boolean custitemonly = true;
                boolean autoallocate = false;
                boolean autoinvoice = false;
                boolean hasInit = false;
                boolean canupdate = false;
                String allocationStatus = "";
                String currentline = "";
                boolean isSOCommitted = false;
                boolean isVoucherShipping = false;
                public static so_mstr so = null;
                public static ArrayList<sod_det> sodlist = null;
                public static ArrayList<sos_det> soslist = null;
                public static ArrayList<sod_tax> sodtaxlist = null;
                public static ArrayList<so_tax> sotaxlist = null;
                public static cms_det cms = null;
                public static cm_mstr cm = null;
                public static ArrayList<String[]> someta = null;
                
                Map<Integer, ArrayList<String[]>> linetax = new HashMap<Integer, ArrayList<String[]>>();
                ArrayList<taxd_mstr> headertax = new ArrayList<>();
     
               
    
    // global datatablemodel declarations
    OrderMaint.MyTableModel myorddetmodel = new OrderMaint.MyTableModel(new Object[][]{},
            new String[]{
               getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("custitem"), 
                getGlobalColumnTag("order"), 
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("uom"), 
                getGlobalColumnTag("listprice"), 
                getGlobalColumnTag("discount"), 
                getGlobalColumnTag("netprice"), 
                getGlobalColumnTag("shipqty"), 
                getGlobalColumnTag("status"), 
                getGlobalColumnTag("warehouse"),
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("tax"),
                getGlobalColumnTag("bom"),
                getGlobalColumnTag("shipcode"),
                getGlobalColumnTag("packqty"),
                getGlobalColumnTag("altitem")
            }
    );
    
    javax.swing.table.DefaultTableModel modelsched = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("date"), 
                getGlobalColumnTag("reference"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("type")
            });
    javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("value"), 
                getGlobalColumnTag("amount")
            });
    
    javax.swing.table.DefaultTableModel attachmentmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                getGlobalColumnTag("file")})
            {
              @Override  
              public Class getColumnClass(int col) {  
                if (col == 0)       
                    return ImageIcon.class; 
                else return String.class;  //other columns accept String values  
              }  
            };
    
    javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 5 || tme.getColumn() == 7 )) {
                            retotal();
                            refreshDisplayTotals();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
    
    class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}; 
            return canEdit[columnIndex];
        }
   
        /*
        public Class getColumnClass(int column) {
               if (column == 6 || column == 7)       
                return Double.class; 
            else return String.class;  //other columns accept String values 
        }
       
        */
        
   }    
    
     
    public OrderMaint() {
        initComponents();
        setLanguageTags(this);
        
    }
   
     // interface functions implemented  
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
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
                case "init":
                    message = getInitialization(); // not getting called...need to remove or consider thread option
                    break; 
                case "add":
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
                case "run":
                    message = Run_autoInvoice();
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
           } else if (this.type.equals("get") && message[0].equals("1")) {
             updateForm();
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             updateForm();
             tbkey.requestFocus();
           } else if (this.type.equals("add") && message[0].equals("0")) {
             initvars(key);
           } else if (this.type.equals("update") && message[0].equals("0")) {
             initvars(key);    
           } else if (this.type.equals("run")) {
             initvars(null); 
           } else if (this.type.equals("init")) {
             done_Initialization();  
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
    
    public void eventTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "uomchange":
                    getOrderMaintDetailEvent(key[0], key[1], key[2], key[3], key[4], key[5]);
                    break; 
                case "qtychange":
                    getOrderMaintDetailEvent(key[0], key[1], key[2], key[3], key[4], key[5]);
                    break;
                case "getPrice":
                    getPrice();
                    break;
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            if (this.type.equals("uomchange")) {
              done_uomchange();  
            } 
            if (this.type.equals("qtychange")) {
              done_qtychange(); 
            }
            if (this.type.equals("locchange")) {
              done_locchange(); 
            }
            if (this.type.equals("getPrice")) {
              done_getPrice(); 
            }
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    } 
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
    
    
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                 // start reset background colors
                if (component instanceof JTextField) {
                    if (((JTextField) component).isEditable()) {
                     component.setBackground(Color.WHITE);
                    } else {
                     component.setBackground(bsmf.MainFrame.nonEditableColor);   
                    }
                }
                if (component instanceof JComboBox) {
                     component.setBackground(bsmf.MainFrame.ddbgcolor);
                }
                // end reset background colors
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
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
    
    public void setComponentDefaultValues(boolean init) {
        
        
        if (init) {
        initDataSets = ordData.getSalesOrderInit(this.getClass().getName(), bsmf.MainFrame.userid);
        }
        
        clearShipAddress();
        
        tbuserid.setText(bsmf.MainFrame.userid);
        tbuserid.setEditable(false);
        tbuserid.setBackground(bsmf.MainFrame.nonEditableColor);
        
        dccreate.setEnabled(false);
        dccreate.setBackground(bsmf.MainFrame.nonEditableColor);
        
        currentline = "";
        btitemkv.setEnabled(false);
        
        tbkey.setText("");
        tbkey.setEditable(true);
        tbkey.setForeground(Color.black);
        tbkey.setBackground(Color.white);
        
        isSOCommitted = false;
        
       
        attachmentmodel.setNumRows(0);
        tableattachment.setModel(attachmentmodel);
        tableattachment.getTableHeader().setReorderingAllowed(false);
        tableattachment.getColumnModel().getColumn(0).setMaxWidth(100);
        
        cbisallocated.setText("Allocation?");
        
        
        ArrayList<String> mylist = new ArrayList<String>();
         jPanelSched.setVisible(false);
        java.util.Date now = new java.util.Date();
       
        lblstatus.setText("");
        lblstatus.setForeground(Color.black); 
       
        cbissourced.setSelected(false);
        cbisallocated.setSelected(false);
        cbconfirm.setSelected(false);
        cbplan.setSelected(true);
        cbedi.setSelected(false);
        cbcascade.setSelected(false);
        
        listprice.setText("0");
        netprice.setText("0");
        qtyshipped.setText("0");
        discount.setText("0");
        ponbr.setText("");
        lblcustname.setText("");
        lblcurr.setText("");
        tbshipto.setText("");
        tbitemshipto.setText("");
        tbsacdesc.setText("");
        tbsacamt.setText("");
        duedate.setDate(now);
        duedate.setDateFormatString("yyyy-MM-dd");
        
        
        orddate.setDate(now);
        
        dccreate.setDate(now);
        
        
        myorddetmodel.setRowCount(0);
        myorddetmodel.addTableModelListener(ml);
        orddet.setModel(myorddetmodel);
        orddet.getTableHeader().setReorderingAllowed(false);
        
        
        
        
        //hide columns
        orddet.getColumnModel().getColumn(2).setMaxWidth(0);
        orddet.getColumnModel().getColumn(2).setMinWidth(0);
        orddet.getColumnModel().getColumn(3).setMaxWidth(0);
        orddet.getColumnModel().getColumn(3).setMinWidth(0);
        orddet.getColumnModel().getColumn(4).setMaxWidth(0);
        orddet.getColumnModel().getColumn(4).setMinWidth(0);
        
        // hide columns with remove
        if (! hasInit) {
        TableColumnModel tcm = orddet.getColumnModel();
        tcm.removeColumn(tcm.getColumn(19));
        tcm.removeColumn(tcm.getColumn(18));
        tcm.removeColumn(tcm.getColumn(17));
        hasInit = true;
        }
        
        
        
        
        sacmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        sactable.getTableHeader().setReorderingAllowed(false);
        modelsched.setRowCount(0);
       // tablesched.setModel(modelsched);
        
        
        lblIsSourced.setIcon(null);
        remarks.setText("");
        tbtracking.setText("");
        tbtotqty.setText("");
        tbtotdollars.setText("");
        lbltotdollars.setText("0.00");
        lbltotdollars.setForeground(Color.blue);
        tbtottax.setText("");
        totlines.setText("");
        custnumber.setText("");
        
        lbuomtext.setText("");
        
        custnumber.setForeground(Color.black);
        // custnumber.setEditable(false);
        custnumber.setText("");
        
        tbdesc.setForeground(Color.black);
       // tbdesc.setEditable(false);
        tbdesc.setText("");
        
        tbpackqty.setEditable(false);
        tbpackqty.setText("");
        
        tbaltitem.setText("");
        
        tbitem.setText("");
        tbitem.setForeground(Color.black);
        discount.setEditable(false);
        
        String defaultsite = null;
        
    if (init) {    
        ddshipfrom.removeAllItems();
        ddsite.removeAllItems();
        ddwh.removeAllItems();
        ddloc.removeAllItems();
        ddcurr.removeAllItems();
        dduom.removeAllItems();
        ddtax.removeAllItems();
        ddcust.removeAllItems();
        ddshipvia.removeAllItems();
        ddstatus.removeAllItems();
        ddsalesrep.removeAllItems();
        ddsalesrep2.removeAllItems();
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              basecurr = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canupdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("allocate")) {
              autoallocate = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("autoinvoice")) {
              autoinvoice = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("orcvarchar")) {
                String[] delimfields = s[1].split(",",-1);
                if (delimfields != null) {
                    for (int i = 0; i < delimfields.length; i++) {
                        if (i == 0) { // cbvouchershipping
                          isVoucherShipping = BlueSeerUtils.ConvertStringToBool(delimfields[i]);  
                        }
                    }
                } 
            }
            if (s[0].equals("custitemonly")) {
              custitemonly = bsmf.MainFrame.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("salesreps")) {
              ddsalesrep.addItem(s[1]); 
              ddsalesrep2.addItem(s[1]);
            }
            if (s[0].equals("warehouses")) {
              ddshipfrom.addItem(s[1]); 
              ddwh.addItem(s[1]); // item level
            }
            
            if (s[0].equals("locations")) {
              ddloc.addItem(s[1]); 
            }
            if (s[0].equals("currencies")) {
              ddcurr.addItem(s[1]); 
            }
            if (s[0].equals("uoms")) {
              dduom.addItem(s[1]); 
            }
            if (s[0].equals("taxcodes")) {
              ddtax.addItem(s[1]); 
            }
            if (s[0].equals("customers")) {
              ddcust.addItem(s[1]); 
            }
            if (s[0].equals("carriers")) {
              ddshipvia.addItem(s[1]); 
            }
            if (s[0].equals("statuses")) {
              ddstatus.addItem(s[1]); 
            }
           
            
        }
        
        cbisallocated.setSelected(autoallocate);
        ddsite.setSelectedItem(defaultsite);
        ddwh.insertItemAt("", 0);
        ddwh.setSelectedIndex(0);
        ddshipfrom.insertItemAt("", 0);
        ddshipfrom.setSelectedIndex(0);
        ddsalesrep.insertItemAt("", 0);
        ddsalesrep.setSelectedIndex(0);
        ddsalesrep2.insertItemAt("", 0);
        ddsalesrep2.setSelectedIndex(0);
        ddloc.insertItemAt("", 0);
        ddloc.setSelectedIndex(0);
        ddcurr.insertItemAt("", 0);
        ddcurr.setSelectedIndex(0);
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        ddtax.insertItemAt("", 0);
        ddtax.setSelectedIndex(0);
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ddshipvia.insertItemAt("", 0);
        ddshipvia.setSelectedIndex(0);
       
      
        ddstatus.setSelectedItem(getGlobalProgTag("open"));
        
        
        ddsactype.removeAllItems();
        ddsactype.addItem("discount");
        ddsactype.addItem("charge");
        ddsactype.addItem("passive");
        if (isVoucherShipping) {
            ddsactype.addItem("shipping ADD");
            ddsactype.addItem("shipping PPD");
            ddsactype.addItem("shipping BIL");
       }
        ddsactype.setSelectedIndex(0);
    }
        
        
        lbqtyavailable.setBackground(Color.gray);
        lbqtyavailable.setText("");
        
       
        
        
    }
        
    
    public void newAction(String x) {
        isLoad = true;
        setPanelComponentState(this, true);
        setComponentDefaultValues(false);
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btinvoice.setEnabled(false);
        btmail.setEnabled(false);
        btcontacts.setEnabled(false);
        btprintinvoice.setEnabled(false);
        btprintorder.setEnabled(false);
        btprintpick.setEnabled(false);
        btprintps.setEnabled(false);
        btupdate.setEnabled(false);
        btcopy.setEnabled(false);
        btdelete.setEnabled(false);
        cbedi.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        disableShipAddress();
        
        if (! x.isEmpty()) {
          tbkey.setText(bsNumber(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
        isLoad = false;
    }
    
    public void setAction(String[] x) {
        
        if (x[0].equals("0")) {
           
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   ddcust.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   disableShipAddress();
                   btitemkv.setEnabled(false);
                    // custom set
                    
                    refreshDisplayTotals();
                    
                    if (ddstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("closed")) == 0) {
                             lblstatus.setText(getMessageTag(1097));
                             lblstatus.setForeground(Color.blue);
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btcopy.setEnabled(true);
                             btclear.setEnabled(true);
                             btprintinvoice.setEnabled(true);
                             btprintps.setEnabled(true);
                             btprintorder.setEnabled(true);
                             btprintpick.setEnabled(true);
                             btadd.setEnabled(false);
                             btupdate.setEnabled(false);
                             btdelete.setEnabled(false);
                             btinvoice.setEnabled(false);
                    } else if (ddstatus.getSelectedItem().toString().compareTo(getGlobalProgTag("cancel")) == 0) {
                             lblstatus.setText(getMessageTag(1192));
                             lblstatus.setForeground(Color.blue);
                             btnew.setEnabled(true);
                             btlookup.setEnabled(true);
                             btclear.setEnabled(true);
                             btprintinvoice.setEnabled(true);
                             btprintps.setEnabled(true);
                             btprintorder.setEnabled(true);
                             btprintpick.setEnabled(true);
                             btadd.setEnabled(false);
                             btupdate.setEnabled(false);
                             btcopy.setEnabled(true);
                             btdelete.setEnabled(false);
                             btinvoice.setEnabled(false);
                    } else {
                             
                             lblstatus.setText(getMessageTag(1098));
                             lblstatus.setForeground(Color.red);
                              btadd.setEnabled(false);
                              btprintinvoice.setEnabled(false);
                              btprintps.setEnabled(false);
                    }
                    
                    if (ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
                      ddstatus.setBackground(Color.red); 
                    }
           
                    
                    if (cbblanket.isSelected())
                    jPanelSched.setVisible(true);
                    else
                    jPanelSched.setVisible(false);
                   
                   
        } else {
                   tbkey.setForeground(Color.red); 
        }
        
        cbedi.setEnabled(false);
       
    }
    
    public boolean validateInput(dbaction x) {
        
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
        
        if (x == dbaction.delete) {  // maintain order after canupdate check
           return true;
        }
        
        Map<String,Integer> f = OVData.getTableInfo(new String[]{"so_mstr"});
        int fc;

        fc = checkLength(f,"so_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }  
        
        fc = checkLength(f,"so_po");
        if (ponbr.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            ponbr.requestFocus();
            return false;
        } 
        
        fc = checkLength(f,"so_rmks");
        if (remarks.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            remarks.requestFocus();
            return false;
        }

        if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024, "site"));
            ddsite.requestFocus();
            return false;
        }

        if ( ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
            bsmf.MainFrame.show(getMessageTag(1024, "bill-to"));
            ddcust.requestFocus();
            return false;
        }
        if (tbshipto.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024, "ship-to"));
            tbshipto.requestFocus();
            return false;
        }


        if (ddcurr.getSelectedItem() == null || ddcurr.getSelectedItem().toString().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024, "currency"));
            return false;
        }

        String SuppressDuplicate = getSysMetaValue("system", "ordercontrol", "suppressduplicate");
        if (! SuppressDuplicate.isBlank() && SuppressDuplicate.equals("1") && x == dbaction.add) {
            if (isDuplicatePO(ddcust.getSelectedItem().toString(), ponbr.getText())) {
                bsmf.MainFrame.show(getMessageTag(1197));
                return false;
            }
        }

        
        if (terms == null   || aracct == null   || arcc == null || ddcurr.getSelectedItem() == null ||
                terms.isEmpty() || aracct.isEmpty() || arcc.isEmpty() || ddcurr.getSelectedItem().toString().isEmpty()
                 ) {
                bsmf.MainFrame.show(getMessageTag(1090));
                return false;
            }   
                
                
                
                
             // lets check for foreign currency with no exchange rate
        if (! ddcurr.getSelectedItem().toString().toUpperCase().equals(basecurr.toUpperCase())) {
            if (OVData.getExchangeRate(basecurr, ddcurr.getSelectedItem().toString()).isEmpty()) {
                bsmf.MainFrame.show(getMessageTag(1091, ddcurr.getSelectedItem().toString() + "/" + basecurr));
                return false;
            }
        }
        
        if (orddet.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1089));
            tbshipto.requestFocus();
            return false;
        }
              
        return true;
    }
    
    public void initvars(String[] arg) {
       
       isLoad = true;
       /*
       setPanelComponentState(jPanelMain, false); 
       setPanelComponentState(jPanelLines, false); 
       setPanelComponentState(jPanelSched, false); 
       setPanelComponentState(panelAttachment, false);
       setPanelComponentState(panelNotes, false);
       */
       
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add(getClassLabelTag("main", this.getClass().getSimpleName()), jPanelMain);
       jTabbedPane1.add(getClassLabelTag("lines", this.getClass().getSimpleName()), jPanelLines);
       jTabbedPane1.add(getClassLabelTag("summary", this.getClass().getSimpleName()), jPanelSched);
       jTabbedPane1.add(getClassLabelTag("attachments", this.getClass().getSimpleName()), panelAttachment);
       jTabbedPane1.add(getClassLabelTag("notes", this.getClass().getSimpleName()), panelNotes);
       setPanelComponentState(this, false); 
       
       setComponentDefaultValues(initDataSets == null);
       
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
       // executeTask(BlueSeerUtils.dbaction.init, null);
        isLoad = false;
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get, arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
        
       
    }
    
    public String[] getInitialization() {
        initDataSets = ordData.getSalesOrderInit(this.getClass().getName(), bsmf.MainFrame.userid);
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
        
    }  
    
    public void done_Initialization() {
        isLoad = true;
        setComponentDefaultValues(false);
        isLoad = false;
        
    }

    public String[] addRecord(String[] x) {
     String[] m = new String[2];
     m = addOrderTransaction(createDetRecord(), createRecord(), createTaxRecord(), createTaxDetRecord(), createSOSRecord());
     
      //  add someta
        if (m[0].equals("0") && ! tbtracking.getText().isBlank()) {
            addUpdateSOMeta(tbkey.getText(), "header", "trackingnumber", tbtracking.getText());
        }
     
      // if autoinvoice
        if (m[0].equals("0") && autoinvoice) {
        boolean sure = bsmf.MainFrame.warn("This is an auto-invoice order...Are you sure you want to auto-invoice?");     
            if (sure) {     
               m = Run_autoInvoice();
            }
        } // if autoinvoice
     return m;
     }
     
    public String[] updateRecord(String[] x) {
        String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> badlines = getBadLines(tbkey.getText());
        
        // now update
        so_mstr _so = createRecord();
        ArrayList<sod_det> _sodlist = createDetRecord();
        m = updateOrderTransaction(tbkey.getText(), badlines, _sodlist, _so, createTaxRecord(), createTaxDetRecord(), createSOSRecord()); 
     
        //  update someta
        if (! tbtracking.getText().isBlank()) {
            addUpdateSOMeta(tbkey.getText(), "header", "trackingnumber", tbtracking.getText());
        }
        
        // change log check
     if (m[0].equals("0")) {
       ArrayList<admData.change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),so,_so);
       if (! c.isEmpty()) {
           addChangeLog(c);
       } 
       ArrayList<admData.change_log> c2 = logChangeArrays(tbkey.getText(), this.getClass().getSimpleName(),sodlist,_sodlist);
       if (! c2.isEmpty()) {
           addChangeLog(c2);
       }
     }
        
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
            m = deleteOrderMstr(x[0]);
        }
     return m;
     }
      
    public String[] getRecord(String[] key) {
       salesOrder z = getOrderMstrSet(key);
     
      so = z.so();
      sodlist = z.sod();
      soslist = z.sos();
      sodtaxlist = z.sodtax();
      cms = z.cms();
      cm = z.cm();
      someta = z.someta();
      
      if (so != null) {
          isSOCommitted = true;
      }
      
      getAttachments(key[0]);
     
      return z.m();
    }
    
    public so_mstr createRecord() { 
        // lets collect single or multiple Warehouse status
        int d = 0;
        String uniqwh = "";
        for (int j = 0; j < orddet.getRowCount(); j++) {
         if (d > 0) {
           if ( uniqwh.compareTo(orddet.getValueAt(j, 12).toString()) != 0) {
           uniqwh = "multi-WH";
           break;
           }
         }
         d++;
         uniqwh = orddet.getValueAt(j, 12).toString();
        }
        
        String ordertype = "";
        if (cbblanket.isSelected()) {
           ordertype = "BLANKET";
        } else {
           ordertype = "DISCRETE";
        }
                
        so_mstr x = new so_mstr(null, 
                 bsNumberToUS(tbkey.getText()),
                 ddcust.getSelectedItem().toString(),
                 tbshipto.getText(),
                 ddsite.getSelectedItem().toString(),
                 ddcurr.getSelectedItem().toString(),   
                 ddshipvia.getSelectedItem().toString(),
                 ddshipfrom.getSelectedItem().toString(), // uniqwh
                 ponbr.getText(),
                 setDateDB(duedate.getDate()),
                 setDateDB(orddate.getDate()),
                 setDateDB(dccreate.getDate()),
                 bsmf.MainFrame.userid,
                 ddstatus.getSelectedItem().toString(),
                 allocationStatus,   // order level allocation status (global variable) set by createDetRecord 
                 terms,
                 aracct,
                 arcc,
                 remarks.getText(),
                 ordertype,
                 ddtax.getSelectedItem().toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbissourced.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbconfirm.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbplan.isSelected())),
                "", // entrytype...blank for manual; 'edi' for EDI entry
                "0",
                setDateDB(new Date()),
                String.valueOf(BlueSeerUtils.boolToInt(cbcascade.isSelected())),
                ddsalesrep.getSelectedItem().toString(),
                ddsalesrep2.getSelectedItem().toString()
                );
        return x;
    }
   
    public ArrayList<sod_det> createDetRecord() {
        ArrayList<sod_det> list = new ArrayList<sod_det>();
       
            double invqty = 0;
            double allqty = 0;
            double qohunall = 0;
            double allocationvalue = 0;
            boolean completeAllocation = true;
            for (int j = 0; j < orddet.getRowCount(); j++) {
                // get total inventory for line item
                // get allocated on current 'open' orders
                 // now get QOH
                invqty = getItemQOHTotal(orddet.getValueAt(j, 1).toString(), ddsite.getSelectedItem().toString());
                allqty = getOrderItemAllocatedQty(orddet.getValueAt(j, 1).toString(), ddsite.getSelectedItem().toString());        


                 qohunall = invqty - allqty; 

                 if (bsParseDouble(orddet.getValueAt(j,5).toString()) <= qohunall) {
                     allocationvalue = bsParseDouble(orddet.getValueAt(j,5).toString());
                 } else {
                     allocationvalue = qohunall;
                     completeAllocation = false;
                 }
                            
                sod_det x = new sod_det(null, 
                bsNumberToUS(tbkey.getText()),
                bsParseInt(orddet.getValueAt(j, 0).toString()),
                orddet.getValueAt(j, 1).toString(),
                orddet.getValueAt(j, 2).toString(),
                orddet.getValueAt(j, 4).toString(),
                bsParseDouble(xZero(orddet.getValueAt(j, 5).toString())), // qty
                orddet.getValueAt(j, 6).toString(),
                allocationvalue,
                bsParseDouble(xZero(orddet.getValueAt(j, 7).toString())), // list
                bsParseDouble(xZero(orddet.getValueAt(j, 8).toString())), // disc
                bsParseDouble(xZero(orddet.getValueAt(j, 9).toString())), // net
                setDateDB(orddate.getDate()),
                setDateDB(duedate.getDate()),   
                bsParseDouble(xZero(orddet.getValueAt(j, 10).toString())),
                orddet.getValueAt(j, 11).toString(),
                orddet.getValueAt(j, 12).toString(),
                orddet.getValueAt(j, 13).toString(),
                orddet.getValueAt(j, 14).toString(),  
                bsParseDouble(xZero(orddet.getValueAt(j, 15).toString())), 
                ddsite.getSelectedItem().toString(),  
                orddet.getValueAt(j, 16).toString(),
                orddet.getModel().getValueAt(j, 17).toString(),
                orddet.getModel().getValueAt(j, 19).toString(), // sod_char1   altitem
                orddet.getModel().getValueAt(j, 18).toString(), // sod_char2   packqty
                "",  // sod_char3
                "", // sod_custline..edi only
                "", // sod_custuom..edi only
                ""  // sod_custprice..edi only
                );  
                list.add(x);
            }    
            // set global variable status of total order allocation
            if (cbisallocated.isSelected()) {
              allocationStatus = "c";
              if (! completeAllocation) {
                  allocationStatus = "p";
              }
            }
            
        return list;
    }
    
    public ArrayList<sos_det> createSOSRecord() {
         ArrayList<sos_det> list = new ArrayList<sos_det>();
         for (int j = 0; j < sactable.getRowCount(); j++) {
             sos_det x = new sos_det(null, bsNumberToUS(tbkey.getText()),
                sactable.getValueAt(j, 1).toString(),
                sactable.getValueAt(j, 0).toString(),
                sactable.getValueAt(j, 2).toString(),
                bsParseDouble(xZero(sactable.getValueAt(j, 3).toString())));     
                list.add(x);
         }
       
        return list;
    }
    
    public ArrayList<so_tax> createTaxRecord() {
         ArrayList<so_tax> list = new ArrayList<so_tax>();
         if (! headertax.isEmpty()) {
          for (taxd_mstr tdm : headertax) {
              so_tax x = new so_tax(null, bsNumberToUS(tbkey.getText()),
                tdm.taxd_desc(),
                bsParseDouble(xZero(tdm.taxd_percent())),
                tdm.taxd_type());   
                list.add(x);
          }
         }
        return list;
    }
    
    public ArrayList<sod_tax> createTaxDetRecord() {
         ArrayList<sod_tax> list = new ArrayList<sod_tax>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             if (linetax.containsKey(orddet.getValueAt(j,0))) {
                  for (String[] s : (ArrayList<String[]>)linetax.get(orddet.getValueAt(j,0))) {
                      sod_tax x = new sod_tax(null, bsNumberToUS(tbkey.getText()),
                        xZero(orddet.getValueAt(j, 0).toString()),
                        s[0],
                        bsParseDouble(xZero(s[1])),
                        xZero(s[2]));     
                        list.add(x);
                  }
            }
        }
       
        return list;
    }
    
    public ArrayList<shpData.ship_tree> createTreeRecord(String shipper) {
        ArrayList<shpData.ship_tree> list = new ArrayList<shpData.ship_tree>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // create shipper parent node with child containers
        
            shpData.ship_tree x = new shpData.ship_tree(null,
            shipper,
            "", // label serial number ... no labels
            ddsite.getSelectedItem().toString(),
            "f", // flat ...no labels
            shipper,
            "",
            "",
            "",
            "",
            "", // empty item
            1.0,
            "" // get display serial
            );
            
            list.add(x);
            // now items of container
            for (int j = 0; j < orddet.getRowCount(); j++) {
                shpData.ship_tree y = new shpData.ship_tree(null,
                shipper,
                orddet.getValueAt(j, 3).toString() + "," + orddet.getValueAt(j, 1).toString() + "," + orddet.getValueAt(j, 0).toString(),
                ddsite.getSelectedItem().toString(),
                "i",
                shipper,
                orddet.getValueAt(j, 0).toString(),
                orddet.getValueAt(j, 3).toString(),
                orddet.getValueAt(j, 0).toString(),
                orddet.getValueAt(j, 4).toString(),
                orddet.getValueAt(j, 1).toString(),
                bsParseDouble(orddet.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.')),
                "" // get display serial
                );
                list.add(y);
            }
       
       
        return list;        
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_nbr");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_po");   
        } else {
         luModel = DTData.getOrderBrowseUtil(luinput.getText(),0, "so_cust");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lblponbr", this.getClass().getSimpleName()),
                getClassLabelTag("lblbillto", this.getClass().getSimpleName())); 
        
    }
 
    public void lookUpFrameBillTo() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_name");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_code");   
        } else {
         luModel = DTData.getCustBrowseUtil(luinput.getText(),0, "cm_zip");   
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
                DefaultComboBoxModel model = (DefaultComboBoxModel) ddcust.getModel();
                if (model.getIndexOf(target.getValueAt(row,1).toString()) < 0) {
                    ddcust.addItem(target.getValueAt(row,1).toString());
                } 
                ddcust.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblname", this.getClass().getSimpleName()), 
                getClassLabelTag("lblcode", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName())); 
        
        
    }
 
    public void lookUpFrameShipTo(String caller) {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_code", ddcust.getSelectedItem().toString());
        } else if (lurb2.isSelected()) {
         luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_name", ddcust.getSelectedItem().toString());  
        } else {
         luModel = DTData.getShipToBrowseUtil(luinput.getText(),0, "cms_zip", ddcust.getSelectedItem().toString());   
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
                    if (caller.equals("shipto")) {
                        updateFormShipto(target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString());
                    } else {
                     tbitemshipto.setText(target.getValueAt(row,1).toString());   
                     itemshipaddrlbl.setText(target.getValueAt(row,3).toString() + "..." + target.getValueAt(row,4).toString() + "..." + target.getValueAt(row,5).toString() + ", " + target.getValueAt(row,6).toString() + " " + target.getValueAt(row,7).toString());
                    }
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblcode", this.getClass().getSimpleName()), 
                getClassLabelTag("lblname", this.getClass().getSimpleName()),
                getClassLabelTag("lblzip", this.getClass().getSimpleName())); 
        
        
    }
     
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemBrowseUtil(luinput.getText(),0, "it_item");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getItemBrowseUtil(luinput.getText(),0, "it_desc");   
        } else {
         luModel = DTData.getCustXrefBrowseUtil(luinput.getText(), 0, "cup_citem", ddcust.getSelectedItem().toString());   
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
                tbitem.setText(target.getValueAt(row,1).toString());
                    if (! tbitem.getText().isBlank()) {
                     getItemInfo(tbitem.getText());
                    }
                }
            }
        };
        luTable.addMouseListener(luml);
      
         
        callDialog(getGlobalLabelTag("lblitem"), 
                getGlobalLabelTag("lbldesc"),
                getClassLabelTag("lblcustnumber", this.getClass().getSimpleName())); 
        
    }
    
    public void lookUpFrameCustItem() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        luModel = DTData.getCustXrefBrowseUtil(luinput.getText(), 0, "cup_citem", ddcust.getSelectedItem().toString());
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
                tbitem.setText(target.getValueAt(row,3).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblcustnumber", this.getClass().getSimpleName())); 
        
        
    }
    
    public void lookUpFrameContacts() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCustContactsBrowseUtil(luinput.getText(), 0, "cmc_name", ddcust.getSelectedItem().toString());
        } else {
         luModel = DTData.getCustContactsBrowseUtil(luinput.getText(), 0, "cmc_type", ddcust.getSelectedItem().toString());   
        } 
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        luTable.getColumnModel().getColumn(1).setMaxWidth(75);
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
                    if (isValidOrder(tbkey.getText())) {
                        Path filepath = OVData.printOrderRemote(tbkey.getText(), isMultiShip(), true);
                        mailDoc(target.getValueAt(row,4).toString(), "Sales Order Info", "This is the content of the body", filepath);
                    }
                }
            }
        };
        luTable.addMouseListener(luml);
        
        callDialog(getGlobalColumnTag("name"), 
                getGlobalColumnTag("type"));  
        
        
    }
    
    public void updateForm() throws ParseException {
        
        boolean canInvoice = true;
        
        isLoad = true; 
        
        tbkey.setText(bsNumber(so.so_nbr()));
        tbkey.setEditable(false);
        ddcust.setSelectedItem(so.so_cust());
        ddcust.setEnabled(false);
        cbissourced.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_issourced()));
        cbcascade.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_cascade()));
        cbconfirm.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_confirm()));
        cbedi.setSelected((so.so_entrytype().equals("edi") ? true : false));
        cbplan.setSelected(BlueSeerUtils.ConvertStringToBool(so.so_plan()));
        ddstatus.setSelectedItem(so.so_status());
        ddcurr.setSelectedItem(so.so_curr());
        ddshipvia.setSelectedItem(so.so_shipvia());
       // ddtax.setSelectedItem(so.so_taxcode());
        ddsite.setSelectedItem(so.so_site());
        tbshipto.setText(so.so_ship());
        ponbr.setText(so.so_po());
        remarks.setText(so.so_rmks());
        ddshipfrom.setSelectedItem(so.so_wh());
        duedate.setDate(parseDate(so.so_due_date()));
        orddate.setDate(parseDate(so.so_ord_date()));
        dccreate.setDate(parseDate(so.so_create_date()));
        setShipAddress();
        terms = cm.cm_terms();
        aracct = cm.cm_ar_acct();
        arcc = cm.cm_ar_cc();
        
        
        if (so.so_isallocated().equals("c")) {
            cbisallocated.setSelected(true);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocation"));
        } 
        else if (so.so_isallocated().equals("p")) {
            cbisallocated.setSelected(true);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocationpartial"));
        } else {
            cbisallocated.setSelected(false);
            cbisallocated.setText(tags.getString(this.getClass().getSimpleName() +".label.cballocation"));
        }

        if (so.so_type().compareTo("BLANKET") == 0) {
        cbblanket.setSelected(true);
        cbblanket.setEnabled(false);
        } else {
        cbblanket.setSelected(false);
        cbblanket.setEnabled(false);
        } 
       
        
        isLoad = false;
        ddtax.setSelectedItem(so.so_taxcode()); // fire after isLoad=false so it will trigger header tax retrieval
        // now detail
        myorddetmodel.setRowCount(0);
        for (sod_det sod : sodlist) {
                    myorddetmodel.addRow(new Object[]{
                      sod.sod_line(), 
                      sod.sod_item(),
                      sod.sod_custitem(), 
                      sod.sod_nbr(), 
                      sod.sod_po(), 
                      bsNumber(sod.sod_ord_qty()), 
                      sod.sod_uom(), 
                      bsFormatDouble(sod.sod_listprice()),
                      bsFormatDouble(sod.sod_disc()), 
                      bsFormatDouble(sod.sod_netprice()), 
                      bsNumber(sod.sod_shipped_qty()), 
                      sod.sod_status(),
                      sod.sod_wh(), 
                      sod.sod_loc(), 
                      sod.sod_desc(), 
                      bsNumber(sod.sod_taxamt()),
                      sod.sod_bom(),
                      sod.sod_ship(),
                      sod.sod_char2(), // packqty
                      sod.sod_char1()  // altitem
                  });
                    if (! sod.sod_status().equals(getGlobalProgTag("open"))) {
                        canInvoice = false;
                    }
                }
        
        // summary charges and discounts
        if (soslist != null) {
        for (sos_det sos : soslist) {
            if (! sos.sos_type().equals("tax")) {  // don't show header tax again...
                boolean z = true;
                for (int j = 0; j < sactable.getRowCount(); j++) { // do not add again if already added during custchangeevent
                          if (sactable.getValueAt(j, 0).toString().equals(sos.sos_type()) &&
                              sactable.getValueAt(j, 1).toString().equals(sos.sos_desc())) {
                              z = false;
                          }
                }
                if (z) {
                    sacmodel.addRow(new Object[]{
                              sos.sos_type(), 
                              sos.sos_desc(),
                              sos.sos_amttype(),
                              sos.sos_amt()});
                    }
                }
        }
        }
        
        // now any someta 
        if (someta != null) {
            for (String[] s : someta) {
                if (s != null && s.length > 3) {
                    if (s[2].equals("trackingnumber")) {  // order, type, key, value  ...key = trackingnumber
                        tbtracking.setText(s[3]);
                    }
                }
            }
        }
        
        
        
        // line tax
        linetax.clear();
        if (sodtaxlist != null) {
        for (sod_tax sodt : sodtaxlist) {
           ArrayList<String[]> list = OVData.getTaxPercentElementsApplicableByItem(ordData.getOrderItem(sodt.sodt_nbr(), sodt.sodt_line()));
           if (list != null) {
               if (! linetax.containsKey(Integer.valueOf(sodt.sodt_line()))) {
                   linetax.put(Integer.valueOf(sodt.sodt_line()), list); 
               }
           }
        }
        }
        
        
        
        // header tax
        /* done by ddtax change event when ddtax is assigned above
        headertax = OVData.getTaxPercentElementsApplicableByTaxCode(ddtax.getSelectedItem().toString());
        */
        
        
        setAction(so.m()); 
        
       
        
        
        btinvoice.setEnabled(canInvoice);
              
       // so = null;
       // sodlist = null;
        soslist = null;
        sodtaxlist = null;
        
        
        
        
    }
   
    public void updateFormShipto(String shipto, String cust) {
        cms = getCMSDet(shipto, cust);
        
        tbshipto.setText(cms.cms_code());
        tbname.setText(cms.cms_name());
        tbaddr1.setText(cms.cms_line1());
        tbaddr2.setText(cms.cms_line2());
        tbcity.setText(cms.cms_city());
        tbstate.setText(cms.cms_state());
        tbzip.setText(cms.cms_zip());
        tbcountry.setText(cms.cms_country());
        tbcontact.setText(cms.cms_contact());
        tbphone.setText(cms.cms_phone());
        tbemail.setText(cms.cms_email());
        tbmisc1.setText(cms.cms_misc());
        
        refreshTaxRecords();
        
    }
    
    public void getAttachments(String id) {
        attachmentmodel.setNumRows(0);
        ArrayList<String> list = OVData.getSysMetaData(id, this.getClass().getSimpleName(), "attachments");
        for (String file : list) {
        attachmentmodel.addRow(new Object[]{BlueSeerUtils.clickflag,  
                               file
            });
        }
    }
    
    // custom funcs 
    public ArrayList<String> getBadLines(String key) {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        
        lines = getOrderLines(key);
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < orddet.getRowCount(); j++) {
             if (orddet.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
       return badlines;
    }
    
    public String[] Run_autoInvoice() {
      //  String[] m = autoInvoice();
        String[] m = orderToInvoice(tbkey.getText(), bsmf.MainFrame.userid, tbtracking.getText());
        return m;
    }
        
    public void custChangeEvent(String mykey) {
        
        if (! isLoad) {
        
        clearShipAddress();  
            
           if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
               ddcust.setBackground(Color.red);
           } else {
               ddcust.setBackground(null);
           }
            
           String disckey = "";
        
        // the intial shipto is assumed to be the same address as the billto...they can search for other shiptos with the lookup option   
        cusData.CustShipSet css = getCustShipSet(new String[]{ddcust.getSelectedItem().toString(), ddcust.getSelectedItem().toString()});
        if (css == null || css.cm() == null) {
            return;
        }
        
        lblcustname.setText(css.cm().cm_name());
        cbcascade.setSelected(BlueSeerUtils.ConvertStringToBool(css.cm().cm_cascade()));
        ddshipvia.setSelectedItem((css.cm().cm_carrier()));
        
        ddcurr.setSelectedItem(css.cm().cm_curr());
        remarks.setText(css.cm().cm_remarks());
        disckey = css.cm().cm_disc_code();
        terms = css.cm().cm_terms();
        aracct = css.cm().cm_ar_acct();
        arcc = css.cm().cm_ar_cc();
        
        if (css.cms() != null) {
            tbshipto.setText(css.cms().cms_shipto());
            tbitemshipto.setText(css.cms().cms_shipto());
            tbname.setText(css.cms().cms_name());
            tbaddr1.setText(css.cms().cms_line1());
            tbaddr2.setText(css.cms().cms_line2());
            tbcity.setText(css.cms().cms_city());
            tbzip.setText(css.cms().cms_zip());
            tbcontact.setText(css.cms().cms_contact());
            tbphone.setText(css.cms().cms_phone());
            tbemail.setText(css.cms().cms_email());
            tbmisc1.setText(css.cms().cms_misc());
            tbstate.setText(css.cms().cms_state());
            tbcountry.setText(css.cms().cms_country());
            
            cms = css.cms();
            refreshTaxRecords();
        } 
        
        if (! disckey.isBlank()) {
             mykey = disckey;  
        }
        ArrayList<String[]> discs = getDiscountRecsByCust(mykey);
        for (String[] s : discs) {
           sacmodel.addRow(new Object[]{ "discount", s[0], "percent", s[1] });
        }
        
        if (css.cm().cm_tax_exempt().equals("1")) {
            ddtax.setEnabled(false);
        } else {
            ddtax.setEnabled(true);
            ddtax.setSelectedItem((css.cm().cm_tax_code())); // fire this after tbshipto is set
        }
        
        
        } // if ! isLoad
    }
      
    public void clearShipAddress() {
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        tbphone.setText("");
        tbemail.setText("");
        tbcontact.setText("");
        tbmisc1.setText("");
        tbstate.setText("");
        tbcountry.setText("");
    }
    
    public void setShipAddress() {
        tbname.setText(cms.cms_name());
        tbaddr1.setText(cms.cms_line1());
        tbaddr2.setText(cms.cms_line2());
        tbcity.setText(cms.cms_city());
        tbzip.setText(cms.cms_zip());
        tbcontact.setText(cms.cms_contact());
        tbphone.setText(cms.cms_phone());
        tbemail.setText(cms.cms_email());
        tbmisc1.setText(cms.cms_misc());
        tbstate.setText(cms.cms_state());
        tbcountry.setText(cms.cms_country());
    }
    
    public void enableShipAddress() {
        tbname.setEnabled(true);
        tbaddr1.setEnabled(true);
        tbaddr2.setEnabled(true);
        tbcity.setEnabled(true);
        tbzip.setEnabled(true);
        tbphone.setEnabled(true);
        tbemail.setEnabled(true);
        tbcontact.setEnabled(true);
        tbmisc1.setEnabled(true);
        tbstate.setEnabled(true);
        tbcountry.setEnabled(true);
    }
    
    public void disableShipAddress() {
        tbname.setEditable(false);
        tbname.setBackground(bsmf.MainFrame.nonEditableColor);
        tbaddr1.setEditable(false);
        tbaddr1.setBackground(bsmf.MainFrame.nonEditableColor);
        tbaddr2.setEditable(false);
        tbaddr2.setBackground(bsmf.MainFrame.nonEditableColor);
        tbcity.setEditable(false);
        tbcity.setBackground(bsmf.MainFrame.nonEditableColor);
        tbzip.setEditable(false);
        tbzip.setBackground(bsmf.MainFrame.nonEditableColor);
        tbphone.setEditable(false);
        tbphone.setBackground(bsmf.MainFrame.nonEditableColor);
        tbemail.setEditable(false);
        tbemail.setBackground(bsmf.MainFrame.nonEditableColor);
        tbcontact.setEditable(false);
        tbcontact.setBackground(bsmf.MainFrame.nonEditableColor);
        tbmisc1.setEditable(false);
        tbmisc1.setBackground(bsmf.MainFrame.nonEditableColor);
        tbstate.setEditable(false);
        tbstate.setBackground(bsmf.MainFrame.nonEditableColor);
        tbcountry.setEditable(false);
        tbcountry.setBackground(bsmf.MainFrame.nonEditableColor);
    }
        
    public void getSchedRecords(String order, String po, String part, String line) {
        modelsched.setNumRows(0);
        
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
                res = st.executeQuery("select * from srl_mstr where " +
                        " srl_so = " + "'" + order + "'" + 
                        " AND srl_po = " + "'" + po + "'" + 
                        " AND srl_item = " + "'" + part + "'" + 
                        " AND srl_line = " + "'" + line + "'" + 
                         ";");
                 while (res.next()) {
                  modelsched.addRow(new Object[]{ 
                      res.getString("srl_duedate"), 
                      res.getString("srl_ref"), res.getString("srl_qtyord"), res.getString("srl_type")});
                }
             //   tablesched.setModel(modelsched);
               
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
         
    public void getItemInfo(String part) {
       // if part is not already in list
       HashMap<String, String[]> hm =  getItemDataInit(part, ddsite.getSelectedItem().toString(), ddcust.getSelectedItem().toString(), "cust");
        
        int i = 0;
        if (! tbitem.getText().isBlank()) {
            ddbom.removeAllItems();
            ddbom.insertItemAt("", 0);
            
            for (Map.Entry<String, String[]> entry : hm.entrySet()) {
           
                if (entry.getKey().equals("itemdata") && entry.getValue() != null) {
                    String[] det = entry.getValue();
                    if (! det[0].isBlank()) {
                    i++;
                    }
                    discount.setText("0");
                    listprice.setText("0");
                    netprice.setText("0");
                    qtyshipped.setText("0");
                    tbdesc.setText(det[1]);
                    dduom.setSelectedItem(det[2]);   // triggers setprice
                    tbitem.setForeground(Color.blue);
                    custnumber.setForeground(Color.blue);
                    //custnumber.setEditable(false);
                    tbdesc.setForeground(Color.blue);
                    //tbdesc.setEditable(false);
                    if (det[11].equals("SERVICE")) {
                        tbaltitem.setText(det[12]); // service type description from code_mstr where code_code = 'servicetype'
                    }
                }
            
                if (entry.getKey().equals("itemcust")) {
                    if (entry.getValue() != null && entry.getValue().length > 0 && ! entry.getValue()[0].isBlank()) {
                      custnumber.setText(entry.getValue()[0]);
                    }
                }
            
            
            // do BOM alternates
            if (entry.getKey().equals("boms")) {
                if (entry.getValue() != null && ! entry.getValue()[0].isBlank()) {
                ddbom.addItem(entry.getValue()[0].toString());
                }
            }
            
            if (entry.getKey().equals("defaultbom")) {
                if (entry.getValue() != null && ! entry.getValue()[0].isBlank()) {
                ddbom.setSelectedItem(entry.getValue());
                }
            }
            
           
            if (entry.getKey().equals("topwhloc")) {
                if (entry.getValue() != null && entry.getValue().length > 1) {
                ddwh.setSelectedItem(entry.getValue()[0]);
                ddloc.setSelectedItem(entry.getValue()[1]);
                }
            }
                         
            } // for each entry
            
            if (i == 0) {
                custnumber.setText("");
                tbitem.setForeground(Color.red);
                custnumber.setForeground(Color.red);
                custnumber.setEditable(true);
                tbdesc.setForeground(Color.red);
                tbdesc.setEditable(true);

                discount.setText("0");
                listprice.setText("0");
                listprice.setBackground(Color.white);

                netprice.setText("0");
                qtyshipped.setText("0"); 
            }
        }  
        
    }
    
    public void getOrderMaintDetailEvent(String item, String site, String uom, String wh, String loc, String key) {
      rDataDetEvent = invData.getOrderMaintDetailEvent(item, site, uom, wh, loc, key); 
    }
    
    public void done_getOrderMaintDetailEvent() {
       
               
               
               
               
               
               
       
        
    }
    
    public void done_uomchange() {
        // uom
        if (rDataDetEvent != null) { // rDataDetEvent array = qoh, uomdesc, packqty, qtyunalloc
           if (bsParseDouble(qtyshipped.getText()) > bsParseDouble(rDataDetEvent[0])) {
               lbqtyavailable.setBackground(Color.red);
           } else {
               lbqtyavailable.setBackground(Color.green);
           }

           tbpackqty.setText(rDataDetEvent[2]);
           lbuomtext.setText(rDataDetEvent[1]);
        }
    }
    
    public void done_qtychange() {
        if (rDataDetEvent != null) { // rDataDetEvent array = qoh, uomdesc, packqty, qtyunalloc
           if (bsParseDouble(qtyshipped.getText()) > bsParseDouble(rDataDetEvent[0])) {
               lbqtyavailable.setBackground(Color.red);
           } else {
               lbqtyavailable.setBackground(Color.green);
           }

           tbpackqty.setText(rDataDetEvent[2]);
           lbuomtext.setText(rDataDetEvent[1]);
        }
    }
    
    public void done_locchange() {
       // loc
       if (rDataDetEvent != null) { // rDataDetEvent array = qoh, uomdesc, packqty, qtyunalloc
        String prefix;
        double qtycheck;
        if (cbisallocated.isSelected()) {
          prefix = "QOH Unallocated=";
          qtycheck = bsParseDouble(rDataDetEvent[3]);

         } else {
          prefix = "QOH Available=";
          qtycheck = bsParseDouble(rDataDetEvent[0]);
         }

         lbqtyavailable.setText(prefix + String.valueOf(qtycheck));
         if (! qtyshipped.getText().isEmpty()) {
             if (bsParseDouble(qtyshipped.getText()) > qtycheck || qtycheck == 0 ) {
                 lbqtyavailable.setBackground(Color.red);
             } else {
                 lbqtyavailable.setBackground(Color.green);
             }
         } 
       }
    }
    
    public void getPrice() {
          String cust = (ddcust.getSelectedItem() == null) ? "" : ddcust.getSelectedItem().toString();
          String curr = (ddcurr.getSelectedItem() == null) ? "" : ddcurr.getSelectedItem().toString();
          String uom = (dduom.getSelectedItem() == null) ? "" : dduom.getSelectedItem().toString();
        TypeAndPriceAndDisc = invData.getItemPrice("c", cust, tbitem.getText(), 
                        uom, curr, qtyshipped.getText());
    }
    
    public void done_getPrice() {
        
        // save current price if this is a line item update
        String cur_listprice = listprice.getText();
        String cur_discount = discount.getText();
                
        
        listprice.setText("0");
        netprice.setText("0");
        discount.setText("0");
        String pricetype = "";
        double price = 0.00;
        double disc = 0;
        
             
                if (TypeAndPriceAndDisc[0] != null) {
                pricetype = TypeAndPriceAndDisc[0];
                }
                
                if (TypeAndPriceAndDisc[1] != null) {
                    price = bsParseDouble(TypeAndPriceAndDisc[1]);
                }
                listprice.setText(bsFormatDouble(price));
                if (pricetype.equals("cust")) {
                    listprice.setBackground(Color.green);
                }
                if (pricetype.equals("item")) {
                    listprice.setBackground(Color.white);
                }
                
                if (TypeAndPriceAndDisc[2] != null) {
                    disc = bsParseDouble(TypeAndPriceAndDisc[2]);
                }
                discount.setText(bsFormatDouble(disc));
                
                // override if line item update and price not found...line update due to qty change
                if (disc == 0) {
                    discount.setText(cur_discount);
                }
                if (price == 0) {
                    listprice.setText(cur_listprice);
                }
                
                setNetPrice();
        
    }
    
    public void setNetPrice() {
        double disc = 0;
        double list = 0;
        double net = 0;
         if (! discount.getText().isEmpty()) {
            disc = bsParseDouble(discount.getText());
        }
        if (! listprice.getText().isEmpty()) {
            list = bsParseDouble(listprice.getText());
        }
        
        if (disc == 0) {
            netprice.setText(listprice.getText());
        } else {
           if (list == 0) {
             listprice.setText("0");
             netprice.setText("0");
           } else {  
            net = list + ((disc / 100) * list); 
            netprice.setText(bsNumber(net));
           }
        }

    }
         
    public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
         if (orddet.getRowCount() > 0) {
             ddcurr.setEnabled(false);
             ddcust.setEnabled(false);
         } else {
             ddcurr.setEnabled(true);
             ddcust.setEnabled(true);
         }
    }
            
    public void sumqty() {
        double qty = 0.00;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + bsParseDouble(orddet.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(bsNumber(qty));
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
    
    public void sumdollars() {
        double dol = 0;
        double summaryTaxPercent = 0;
        double summaryTaxAmount = 0;
        double headertaxamt = 0;
        double matltax = 0;
        double totaltax = 0;
        
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             dol = dol + ( bsParseDouble(orddet.getValueAt(j, 5).toString()) * bsParseDouble(orddet.getValueAt(j, 9).toString()) );  
             matltax += bsParseDouble(orddet.getValueAt(j, 15).toString()); // now get material tax at the line level
         }
         
         // now lets get summary tax
         // now add trailer/summary charges if any
         for (int j = 0; j < sactable.getRowCount(); j++) {
            if (sactable.getValueAt(j,0).toString().equals("passive")) { // skip passive (info only)
            continue;
            } 
            
            if (! sactable.getValueAt(j,0).toString().equals("tax") &&
                    ! sactable.getValueAt(j,0).toString().equals("shipping PPD") &&
                    ! sactable.getValueAt(j,0).toString().equals("shipping BIL") && ! sactable.getValueAt(j,2).toString().equals("percent") ) {
            
            dol += bsParseDouble(sactable.getValueAt(j,3).toString());  // add charges to total net charge
            }
            if (sactable.getValueAt(j,0).toString().equals("tax") && sactable.getValueAt(j,2).toString().equals("percent")) {
            summaryTaxPercent += bsParseDouble(sactable.getValueAt(j,3).toString());
            }
            if (sactable.getValueAt(j,0).toString().equals("tax") && sactable.getValueAt(j,2).toString().equals("amount")) {
            summaryTaxAmount += bsParseDouble(sactable.getValueAt(j,3).toString());
            }
        }
         
         if (summaryTaxPercent > 0) {
              headertaxamt = (dol * (summaryTaxPercent / 100) );
         }
         headertaxamt += summaryTaxAmount; // header tax amount is percent tax plus non-percent fixed amount
         
         totaltax = headertaxamt + matltax;  // combine header tax and matl tax
         
         
         // add tax to total
         dol += totaltax;
         
        
         tbtottax.setText(currformatDouble(totaltax));
         tbtotdollars.setText(currformatDouble(dol));
         lbltotdollars.setText(currformatDoubleWithSymbol(dol, ddcurr.getSelectedItem().toString()));
         lblcurr.setText(ddcurr.getSelectedItem().toString());
    }
      
    public void refreshDisplayTotals() {
        sumqty();
        sumdollars();
        sumlinecount();
    }
    
    public void refreshTaxRecords() {
        // headertax = OVData.getTaxPercentElementsApplicableByTaxCode(ddtax.getSelectedItem().toString());
            headertax = getTaxDet(ddtax.getSelectedItem().toString());
            // remove all 'tax' records and refresh
            for (int j = 0; j < sactable.getRowCount(); j++) {
                if (sactable.getValueAt(j, 0).toString().equals("tax"))
               ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(j); 
            }
            //refresh tax records
            double taxpercent = 0.00;
            for (taxd_mstr taxd : headertax) {
                if (taxd.taxd_conditional().equals("NONE")) {
                    taxpercent = bsParseDouble(taxd.taxd_percent());
                }

                if (taxd.taxd_conditional().equals("STATE")) {
                   if (taxd.taxd_method().equals("Destination ShipTo")) { 
                    taxpercent = getTaxMetaByState(tbstate.getText());
                   }
                   if (taxd.taxd_method().equals("Origin ShipFrom")) { 
                    if (ddshipfrom.getSelectedItem() != null && ! ddshipfrom.getSelectedItem().toString().isBlank()) {   
                        invData.wh_mstr wm = getWareHouseMstr(new String[]{ddshipfrom.getSelectedItem().toString()});
                        taxpercent = getTaxMetaByState(wm.wh_state());
                    }
                   }
                   if (taxd.taxd_method().equals("Origin Billing")) { 
                    taxpercent = getTaxMetaByState(cm.cm_state());
                   }
                }
                
                if (taxd.taxd_conditional().equals("MUNICIPALITY")) {
                   if (taxd.taxd_method().equals("Destination ShipTo")) { 
                    taxpercent = getTaxMetaByMunicipality(cms.cms_municipality());
                   }
                   if (taxd.taxd_method().equals("Origin ShipFrom")) { 
                    if (ddshipfrom.getSelectedItem() != null && ! ddshipfrom.getSelectedItem().toString().isBlank()) {   
                        invData.wh_mstr wm = getWareHouseMstr(new String[]{ddshipfrom.getSelectedItem().toString()});
                        taxpercent = getTaxMetaByMunicipality(wm.wh_state());
                    }
                   }
                   if (taxd.taxd_method().equals("Origin Billing")) { 
                    taxpercent = getTaxMetaByMunicipality(cm.cm_municipality());
                   }
                }


                if (taxd.taxd_conditional().equals("ZIP")) {
                   // To be done... taxpercent = getTaxPercentByZip(shipper, taxd.taxd_method()); 
                }    
            sacmodel.addRow(new Object[]{ "tax", taxd.taxd_desc(), "percent", bsNumber(taxpercent)});
            }
    }
    
    public void retotal() {
        
        double dol = 0;
        double newdisc = 0;
        double newprice = 0;
        double newtax = 0;
        double listprice = 0;
         //"Line", "Part", "CustPart", "SO", "PO", "Qty", "uom", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc"
        
         
         
         if (cbcascade.isSelected()) {
             // get gross total first in case of cascade
             double grosslistprice = 0.00;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             grosslistprice = (bsParseDouble(orddet.getValueAt(j, 7).toString()));
            for (int k = 0; k < sactable.getRowCount(); k++) {
               if (sactable.getValueAt(k,2).toString().equals("percent")) {
                    grosslistprice = (grosslistprice + (grosslistprice * (bsParseDouble(sactable.getValueAt(k,3).toString()) / 100)));
               }
            } 
            newdisc = (grosslistprice / bsParseDouble(orddet.getValueAt(j, 7).toString()));
          }
         } 
         
         
         if (! cbcascade.isSelected()) {
            for (int j = 0; j < sactable.getRowCount(); j++) {
               if (sactable.getValueAt(j,0).toString().equals("discount") &&
                   sactable.getValueAt(j,2).toString().equals("percent")) {
               newdisc += bsParseDouble(sactable.getValueAt(j,3).toString());
               }
               if (sactable.getValueAt(j,0).toString().equals("charge") &&
                   sactable.getValueAt(j,2).toString().equals("percent")) {
               newdisc -= bsParseDouble(sactable.getValueAt(j,3).toString());
               }
            }
         }
         
         // check for customer specific discounts
        // newdisc += invData.getItemDiscFromCust(ddcust.getSelectedItem().toString());
        
         for (int j = 0; j < orddet.getRowCount(); j++) {
             listprice = bsParseDouble(orddet.getValueAt(j, 7).toString());
             orddet.setValueAt(bsNumber(newdisc), j, 8);
             if (newdisc == 0) {
             newprice = listprice;    
             } else {
                 if (cbcascade.isSelected()) {
                     newprice = listprice * newdisc;  // calculated cascading discount is absolute increase/decrease...must be multiplied
                 } else {
                     newprice = listprice + (listprice * (newdisc / 100));  // minus a negative disc increases newprice...aka charge
                 }
             }
             orddet.setValueAt(bsNumber(newprice), j, 9);
         }
               
         
    }
    
    public boolean validateDetail() {
       // if user clicks on 'additem' before focuslost on each field
       // has time to fire, focuslost will have effectively set these fields to empty upon
       // seeing an error before this function is called
       // ...so we check for empty to prevent lines from being added
        
        if (qtyshipped.getText().isEmpty()) {
            return false;
        }
        if (listprice.getText().isEmpty()) {
            return false;
        }
        if (discount.getText().isEmpty()) {
            return false;
        }
        
        if (tbitem.getText().isBlank()) {
            bsmf.MainFrame.show(getMessageTag(1081));
            tbitem.requestFocus();
            return false;
        }
        
        
        
        String[] v = ordData.validateOrderDetail(tbkey.getText(),  // returns boolean, tagnbr
                ddcust.getSelectedItem().toString(), 
                tbitem.getText(), 
                qtyshipped.getText(), 
                ddsite.getSelectedItem().toString(), 
                dduom.getSelectedItem().toString(),
                ddcurr.getSelectedItem().toString());
        
         
        if (v[1].equals("1092")) {
            bsmf.MainFrame.show(getMessageTag(Integer.parseInt(v[1]))); 
            qtyshipped.requestFocus();
            return false;
        }
        if (v[1].equals("1093")) {
            bsmf.MainFrame.show(getMessageTag(Integer.parseInt(v[1]))); 
            dduom.requestFocus();
            return false;
        }
        if (v[1].equals("1094")) {
            bsmf.MainFrame.show(getMessageTag(Integer.parseInt(v[1]))); 
            dduom.requestFocus();
            return false;
        }
        
      return true;   
    }
    
    public ArrayList<String[]> tableToArrayList() {
        ArrayList<String[]> list = new ArrayList<String[]>();
         for (int j = 0; j < orddet.getRowCount(); j++) {
             String[] s = new String[]{
                 orddet.getValueAt(j, 0).toString(),
                 orddet.getValueAt(j, 1).toString(),
                 orddet.getValueAt(j, 2).toString(),
                 orddet.getValueAt(j, 3).toString(),
                 orddet.getValueAt(j, 4).toString(),
                 orddet.getValueAt(j, 5).toString(),
                 orddet.getValueAt(j, 6).toString(),
                 orddet.getValueAt(j, 7).toString(),
                 orddet.getValueAt(j, 8).toString(),
                 orddet.getValueAt(j, 9).toString(),
                 orddet.getValueAt(j, 10).toString(),
                 orddet.getValueAt(j, 11).toString(),
                 orddet.getValueAt(j, 12).toString(),
                 orddet.getValueAt(j, 13).toString(),
                 orddet.getValueAt(j, 14).toString(),
                 orddet.getValueAt(j, 15).toString(),
                 orddet.getValueAt(j, 16).toString()};
             list.add(s);
         }
        
        return list;
    }
      
    public void showEDIKV(String key, String line) {
        javax.swing.JTextArea ta = new javax.swing.JTextArea();
        
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
                
        ArrayList<String[]> list = getEDIMetaValueDetail(key, line);
        
        if (list == null || list.isEmpty()) {
            bsmf.MainFrame.show("no item level edi kv data to show");
            return;
        }
        
        ta.setText("  " + "\n\n");
        for (String[] s : list) {
          ta.append("Key: " + s[2] + " \t\t  Value: " + s[3] + "  \n");
        }
        
        ta.setCaretPosition(0);
        ta.setEditable(false);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Item level Key/Value Pair Information : " + key + " line: " + line);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints panelGBC = new GridBagConstraints();

        panelGBC.weightx = 1;                    //I want to fill whole panel with JTextArea
        panelGBC.weighty = 1;                    //so both weights =1
        panelGBC.fill = GridBagConstraints.BOTH; //and fill is set to BOTH
        
        panel.add(scroll, panelGBC);
        dialog.add(panel);
        dialog.setPreferredSize(new Dimension(500, 400));
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    public boolean isMultiShip() {
        Set<String> shiptos = new LinkedHashSet<String>();
        boolean isMultiShip = false;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            if (orddet.getModel().getValueAt(j, 17).toString().isBlank()) {
                continue;
            }
            shiptos.add(orddet.getModel().getValueAt(j, 17).toString());
        } 
        if (shiptos.size() > 1) {
           isMultiShip = true;
        }
        return isMultiShip;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelSched = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        tbsacamt = new javax.swing.JTextField();
        tbsacdesc = new javax.swing.JTextField();
        percentlabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btsacadd = new javax.swing.JButton();
        btsacdelete = new javax.swing.JButton();
        ddsactype = new javax.swing.JComboBox<>();
        ddsacamttype = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        jLabel82 = new javax.swing.JLabel();
        ddcust = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        lblcustname = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ponbr = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        ddshipvia = new javax.swing.JComboBox();
        remarks = new javax.swing.JTextField();
        ddstatus = new javax.swing.JComboBox();
        jLabel83 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel86 = new javax.swing.JLabel();
        orddate = new com.toedter.calendar.JDateChooser();
        jLabel87 = new javax.swing.JLabel();
        cbblanket = new javax.swing.JCheckBox();
        jLabel92 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        ddtax = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        lblIsSourced = new javax.swing.JLabel();
        cbissourced = new javax.swing.JCheckBox();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel98 = new javax.swing.JLabel();
        cbisallocated = new javax.swing.JCheckBox();
        cbconfirm = new javax.swing.JCheckBox();
        cbplan = new javax.swing.JCheckBox();
        cbedi = new javax.swing.JCheckBox();
        tbtracking = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        dccreate = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        cbcascade = new javax.swing.JCheckBox();
        ddsalesrep = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        tbuserid = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        ddshipfrom = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        ddsalesrep2 = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        tbemail = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel104 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        tbmisc1 = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbshipto = new javax.swing.JTextField();
        btLookUpShipTo = new javax.swing.JButton();
        jLabel91 = new javax.swing.JLabel();
        tbstate = new javax.swing.JTextField();
        tbcountry = new javax.swing.JTextField();
        btinvoice = new javax.swing.JButton();
        btprintorder = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btprintinvoice = new javax.swing.JButton();
        btprintps = new javax.swing.JButton();
        btLookUpBillTo = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        lbltotdollars = new javax.swing.JLabel();
        lbldollars = new javax.swing.JLabel();
        btmail = new javax.swing.JButton();
        btcontacts = new javax.swing.JButton();
        btprintpick = new javax.swing.JButton();
        btcopy = new javax.swing.JButton();
        btchangelog = new javax.swing.JButton();
        jPanelLines = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btadditem = new javax.swing.JButton();
        btdelitem = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel84 = new javax.swing.JLabel();
        lblCustItemAndDesc = new javax.swing.JLabel();
        qtyshipped = new javax.swing.JTextField();
        custnumber = new javax.swing.JTextField();
        lblpart1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dduom = new javax.swing.JComboBox<>();
        lbqtyavailable = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        btLookUpItemDesc = new javax.swing.JButton();
        tbitem = new javax.swing.JTextField();
        tbaltitem = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tbpackqty = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        lbuomtext = new javax.swing.JLabel();
        btclearitem = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        discount = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        netprice = new javax.swing.JTextField();
        listprice = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        tbmisc = new javax.swing.JTextField();
        ddwh = new javax.swing.JComboBox<>();
        ddloc = new javax.swing.JComboBox<>();
        ddbom = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbitemshipto = new javax.swing.JTextField();
        btLookUpItemShipTo = new javax.swing.JButton();
        itemshipaddrlbl = new javax.swing.JLabel();
        btupdateitem = new javax.swing.JButton();
        btitemkv = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbtottax = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        lblcurr = new javax.swing.JLabel();
        panelAttachment = new javax.swing.JPanel();
        labelmessage = new javax.swing.JLabel();
        btaddattachment = new javax.swing.JButton();
        btdeleteattachment = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableattachment = new javax.swing.JTable();
        panelNotes = new javax.swing.JPanel();
        btnotes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tanotes = new javax.swing.JTextArea();

        jTextField1.setText("jTextField1");

        jLabel6.setText("jLabel6");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        jPanelSched.setBorder(javax.swing.BorderFactory.createTitledBorder("Summary Charges and Allowances"));
        jPanelSched.setPreferredSize(new java.awt.Dimension(940, 700));

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(sactable);

        percentlabel.setText("Percent/Amount");
        percentlabel.setName("lblpercent"); // NOI18N

        jLabel8.setText("Desc");
        jLabel8.setName("lbldesc"); // NOI18N

        btsacadd.setText("add");
        btsacadd.setName("btadd"); // NOI18N
        btsacadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacaddActionPerformed(evt);
            }
        });

        btsacdelete.setText("delete");
        btsacdelete.setName("btdelete"); // NOI18N
        btsacdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacdeleteActionPerformed(evt);
            }
        });

        ddsacamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "amount", "percent" }));
        ddsacamttype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsacamttypeActionPerformed(evt);
            }
        });

        jLabel17.setText("Summary Type");

        jLabel18.setText("Amount Type");

        javax.swing.GroupLayout jPanelSchedLayout = new javax.swing.GroupLayout(jPanelSched);
        jPanelSched.setLayout(jPanelSchedLayout);
        jPanelSchedLayout.setHorizontalGroup(
            jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSchedLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSchedLayout.createSequentialGroup()
                        .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(percentlabel)
                            .addComponent(jLabel8)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelSchedLayout.createSequentialGroup()
                                .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(btsacadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btsacdelete))
                            .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddsacamttype, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddsactype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 906, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );
        jPanelSchedLayout.setVerticalGroup(
            jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSchedLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSchedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btsacadd)
                    .addComponent(btsacdelete)
                    .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentlabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        add(jPanelSched);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Order Maintenance"));
        jPanelMain.setName("panelmain"); // NOI18N
        jPanelMain.setPreferredSize(new java.awt.Dimension(940, 700));

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel82.setText("bill-to");
        jLabel82.setName("lblbillto"); // NOI18N

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        lblcustname.setName("lblbilltoaddress"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel81.setText("Due Date");
        jLabel81.setName("lblduedate"); // NOI18N

        jLabel90.setText("ShipVia");
        jLabel90.setName("lblshipvia"); // NOI18N

        ddstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddstatusActionPerformed(evt);
            }
        });

        jLabel83.setText("PO Number");
        jLabel83.setName("lblponbr"); // NOI18N

        jLabel85.setText("Status");
        jLabel85.setName("lblstatus"); // NOI18N

        duedate.setDateFormatString("yyyy-MM-dd");

        jLabel86.setText("Remarks");
        jLabel86.setName("lblremarks"); // NOI18N

        orddate.setDateFormatString("yyyy-MM-dd");

        jLabel87.setText("Ord Date");
        jLabel87.setName("lblorddate"); // NOI18N

        cbblanket.setText("Blanket?");
        cbblanket.setName("cbblanket"); // NOI18N

        jLabel92.setText("Site:");
        jLabel92.setName("lblsite"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        ddtax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtaxActionPerformed(evt);
            }
        });

        jLabel93.setText("Tax Code");
        jLabel93.setName("lbltaxcode"); // NOI18N

        lblIsSourced.setText("   ");

        cbissourced.setText("is Sourced?");
        cbissourced.setName("cbissourced"); // NOI18N

        jLabel98.setText("Currency");
        jLabel98.setName("lblcurrency"); // NOI18N

        cbisallocated.setText("Allocation?");
        cbisallocated.setName("cballocation"); // NOI18N

        cbconfirm.setText("Confirmed");
        cbconfirm.setName("cbconfirm"); // NOI18N

        cbplan.setText("Plan");
        cbplan.setName("cbplan"); // NOI18N

        cbedi.setText("EDI");

        jLabel13.setText("Tracking");
        jLabel13.setName("lbltracking"); // NOI18N

        dccreate.setDateFormatString("yyyy-MM-dd");

        jLabel14.setText("Create Date");
        jLabel14.setName("lblcreatedate"); // NOI18N

        cbcascade.setText("Cascade");

        jLabel19.setText("SalesRep1");

        jLabel20.setText("UserID");

        jLabel21.setText("ShipFrom");

        jLabel22.setText("SalesRep2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel90)
                    .addComponent(jLabel13)
                    .addComponent(jLabel98)
                    .addComponent(jLabel86)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtracking, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddshipfrom, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(cbblanket)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cbedi))
                                    .addComponent(ddshipvia, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ponbr, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                                .addGap(52, 52, 52)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel85)
                                    .addComponent(jLabel92)
                                    .addComponent(jLabel20))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddstatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(85, 85, 85)
                                        .addComponent(lblIsSourced, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel81, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel87, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel93, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel14)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(orddate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dccreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cbcascade)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(cbisallocated)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbissourced)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbconfirm))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbplan)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddsalesrep2, javax.swing.GroupLayout.Alignment.TRAILING, 0, 129, Short.MAX_VALUE)
                                    .addComponent(ddsalesrep, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(18, 18, 18))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbblanket)
                                .addComponent(cbedi))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ponbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel83))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipvia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel92)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel81))
                            .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddsalesrep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(orddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel85)
                                .addComponent(jLabel87))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddsalesrep2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel22)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dccreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20)
                                .addComponent(jLabel14)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIsSourced)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddtax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel93)))))
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtracking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98)
                            .addComponent(cbcascade)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbisallocated)
                            .addComponent(cbissourced)
                            .addComponent(cbconfirm)
                            .addComponent(cbplan))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addContainerGap())
        );

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(758, 181));

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel99.setText("City");
        jLabel99.setName("lblcity"); // NOI18N

        jLabel100.setText("Misc");
        jLabel100.setName("lblmisc"); // NOI18N

        jLabel101.setText("Name");
        jLabel101.setName("lblname"); // NOI18N

        jLabel102.setText("Addr2");
        jLabel102.setName("lbladdr2"); // NOI18N

        jLabel104.setText("Phone");
        jLabel104.setName("lblphone"); // NOI18N

        jLabel106.setText("Email");
        jLabel106.setName("lblemail"); // NOI18N

        jLabel109.setText("Contact");
        jLabel109.setName("lblcontact"); // NOI18N

        jLabel110.setText("Addr1");
        jLabel110.setName("lbladdr1"); // NOI18N

        jLabel111.setText("State");
        jLabel111.setName("lblstate"); // NOI18N

        jLabel12.setText("Country");
        jLabel12.setName("lblcountry"); // NOI18N

        btLookUpShipTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpShipTo.setToolTipText("lookup");
        btLookUpShipTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpShipToActionPerformed(evt);
            }
        });

        jLabel91.setText("ship-to");
        jLabel91.setName("lblshipto"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel111, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel99, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel102, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel110, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel101, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel91, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbshipto)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(tbstate, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(tbaddr2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btLookUpShipTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel104)
                    .addComponent(jLabel106)
                    .addComponent(jLabel109)
                    .addComponent(jLabel100))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbmisc1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(321, 321, 321))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel109))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel104))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbmisc1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel100)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel91))
                            .addComponent(btLookUpShipTo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel110))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel102))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel99))
                            .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(tbcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel111)
                                .addComponent(tbstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btinvoice.setText("Invoice");
        btinvoice.setName("btinvoice"); // NOI18N
        btinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinvoiceActionPerformed(evt);
            }
        });

        btprintorder.setText("Print Order");
        btprintorder.setName("btprintorder"); // NOI18N
        btprintorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintorderActionPerformed(evt);
            }
        });

        lblstatus.setName("lblmessage"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btprintinvoice.setText("Print Invoice");
        btprintinvoice.setName("btprintinvoice"); // NOI18N
        btprintinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintinvoiceActionPerformed(evt);
            }
        });

        btprintps.setText("Print PackingSlip");
        btprintps.setName("btprintpackingslip"); // NOI18N
        btprintps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintpsActionPerformed(evt);
            }
        });

        btLookUpBillTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpBillTo.setToolTipText("lookup");
        btLookUpBillTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpBillToActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.setToolTipText("search");
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        lbldollars.setText("Total $");
        lbldollars.setName("lbltotalamount"); // NOI18N

        btmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mail.png"))); // NOI18N
        btmail.setToolTipText("mail");
        btmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmailActionPerformed(evt);
            }
        });

        btcontacts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/contacts.png"))); // NOI18N
        btcontacts.setToolTipText("contacts");
        btcontacts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcontactsActionPerformed(evt);
            }
        });

        btprintpick.setText("Print PIckTicket");
        btprintpick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintpickActionPerformed(evt);
            }
        });

        btcopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addfile.png"))); // NOI18N
        btcopy.setToolTipText("Copy");
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        btchangelog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btchangelog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchangelogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbkey)
                    .addComponent(ddcust, 0, 133, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(btLookUpBillTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(lblcustname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(63, 63, 63)
                        .addComponent(lbldollars)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbltotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btmail)
                        .addGap(4, 4, 4)
                        .addComponent(btcontacts)
                        .addGap(4, 4, 4)
                        .addComponent(btcopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btchangelog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(87, 87, 87))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 909, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(36, Short.MAX_VALUE))
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(btinvoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprintinvoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprintps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btprintpick)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btprintorder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel76))
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(btclear)
                                .addComponent(btmail)))
                        .addComponent(btlookup))
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btcontacts)
                    .addComponent(btcopy)
                    .addComponent(btchangelog))
                .addGap(8, 8, 8)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblcustname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btLookUpBillTo)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel82))))
                    .addComponent(lbltotdollars, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbldollars, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btinvoice)
                    .addComponent(btprintorder)
                    .addComponent(btprintinvoice)
                    .addComponent(btprintps)
                    .addComponent(btprintpick))
                .addGap(26, 26, 26))
        );

        add(jPanelMain);

        jPanelLines.setBorder(javax.swing.BorderFactory.createTitledBorder("Lines"));
        jPanelLines.setName("panellines"); // NOI18N
        jPanelLines.setPreferredSize(new java.awt.Dimension(940, 680));

        btadditem.setText("Insert");
        btadditem.setName("btinsert"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdelitem.setText("Remove");
        btdelitem.setName("btremove"); // NOI18N
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        jLabel84.setText("Order Qty");
        jLabel84.setName("lblqtyship"); // NOI18N

        lblCustItemAndDesc.setText("Item Number");
        lblCustItemAndDesc.setName("lblitem"); // NOI18N

        qtyshipped.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyshippedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                qtyshippedFocusLost(evt);
            }
        });

        lblpart1.setText("Sku Number");
        lblpart1.setName("lblcustnumber"); // NOI18N

        jLabel5.setText("uom");
        jLabel5.setName("lbluom"); // NOI18N

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel9.setText("Description");
        jLabel9.setName("lbldesc"); // NOI18N

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        tbitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbitemActionPerformed(evt);
            }
        });

        jLabel15.setText("Alternate Number");
        jLabel15.setName("lblaltitem"); // NOI18N

        tbpackqty.setBackground(new java.awt.Color(204, 204, 204));
        tbpackqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpackqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpackqtyFocusLost(evt);
            }
        });

        jLabel16.setText("Pack Qty");

        btclearitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/file.png"))); // NOI18N
        btclearitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblpart1)
                    .addComponent(lblCustItemAndDesc)
                    .addComponent(jLabel84)
                    .addComponent(jLabel9)
                    .addComponent(jLabel5)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbqtyavailable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbuomtext, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbaltitem, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(custnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpackqty, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btclearitem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCustItemAndDesc)
                                .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btclearitem))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(custnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblpart1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbaltitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)))
                    .addComponent(btLookUpItemDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(lbuomtext, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(qtyshipped, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel84))
                    .addComponent(lbqtyavailable, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpackqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel89.setText("NetPrice");
        jLabel89.setName("lblnetprice"); // NOI18N

        jLabel80.setText("ListPrice");
        jLabel80.setName("lbllistprice"); // NOI18N

        discount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                discountFocusLost(evt);
            }
        });

        jLabel88.setText("Disc%");
        jLabel88.setName("lbldiscount"); // NOI18N

        netprice.setEditable(false);
        netprice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netpriceActionPerformed(evt);
            }
        });

        listprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                listpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                listpriceFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel80)
                    .addComponent(jLabel88)
                    .addComponent(jLabel89))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(discount, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addComponent(netprice))
                    .addComponent(listprice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel89))
                .addContainerGap())
        );

        jLabel94.setText("Misc");
        jLabel94.setName("lblmisc"); // NOI18N

        jLabel95.setText("Plant/WH");
        jLabel95.setName("lblwh"); // NOI18N

        jLabel96.setText("Location");
        jLabel96.setName("lblloc"); // NOI18N

        tbmisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbmiscActionPerformed(evt);
            }
        });

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        ddloc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddlocActionPerformed(evt);
            }
        });

        ddbom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbomActionPerformed(evt);
            }
        });

        jLabel7.setText("BOM");
        jLabel7.setName("lblbom"); // NOI18N

        jLabel10.setText("ShipTo");
        jLabel10.setName("lblshipto"); // NOI18N

        btLookUpItemShipTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemShipTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemShipToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(itemshipaddrlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel95, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel96, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddbom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddwh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbmisc)
                            .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(tbitemshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btLookUpItemShipTo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel96))
                .addGap(8, 8, 8)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(tbitemshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btLookUpItemShipTo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(itemshipaddrlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btupdateitem.setText("Update");
        btupdateitem.setName("btupdate"); // NOI18N
        btupdateitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateitemActionPerformed(evt);
            }
        });

        btitemkv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notes.png"))); // NOI18N
        btitemkv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btitemkvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btitemkv, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdateitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelitem)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btitemkv)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btdelitem)
                                .addComponent(btadditem)
                                .addComponent(btupdateitem))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        orddet.setModel(new javax.swing.table.DefaultTableModel(
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
        orddet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orddetMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(orddet);

        jLabel3.setText("Total Lines:");
        jLabel3.setName("lbltotallines"); // NOI18N

        totlines.setEditable(false);

        jLabel1.setText("Total Qty:");
        jLabel1.setName("lbltotalquantity"); // NOI18N

        tbtotqty.setEditable(false);

        jLabel4.setText("Total Tax:");
        jLabel4.setName("lbltotaltax"); // NOI18N

        tbtottax.setEditable(false);

        jLabel2.setText("Total $");
        jLabel2.setName("lbltotalamount"); // NOI18N

        tbtotdollars.setEditable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(lblcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLinesLayout = new javax.swing.GroupLayout(jPanelLines);
        jPanelLines.setLayout(jPanelLinesLayout);
        jPanelLinesLayout.setHorizontalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanelLinesLayout.setVerticalGroup(
            jPanelLinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLinesLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        add(jPanelLines);

        panelAttachment.setBorder(javax.swing.BorderFactory.createTitledBorder("Attachment Panel"));
        panelAttachment.setName("panelAttachment"); // NOI18N
        panelAttachment.setPreferredSize(new java.awt.Dimension(974, 560));

        btaddattachment.setText("Add Attachment");
        btaddattachment.setName("btaddattachment"); // NOI18N
        btaddattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattachmentActionPerformed(evt);
            }
        });

        btdeleteattachment.setText("Delete Attachment");
        btdeleteattachment.setName("btdeleteattachment"); // NOI18N
        btdeleteattachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattachmentActionPerformed(evt);
            }
        });

        tableattachment.setModel(new javax.swing.table.DefaultTableModel(
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
        tableattachment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableattachmentMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableattachment);

        javax.swing.GroupLayout panelAttachmentLayout = new javax.swing.GroupLayout(panelAttachment);
        panelAttachment.setLayout(panelAttachmentLayout);
        panelAttachmentLayout.setHorizontalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addComponent(btaddattachment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeleteattachment)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 446, Short.MAX_VALUE)
                .addComponent(labelmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane4)
        );
        panelAttachmentLayout.setVerticalGroup(
            panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAttachmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelAttachmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btaddattachment)
                        .addComponent(btdeleteattachment)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(panelAttachment);

        panelNotes.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes Panel"));
        panelNotes.setName("panelAttachment"); // NOI18N
        panelNotes.setPreferredSize(new java.awt.Dimension(974, 560));

        btnotes.setText("Update Notes");
        btnotes.setName("btnotes"); // NOI18N
        btnotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnotesActionPerformed(evt);
            }
        });

        tanotes.setColumns(20);
        tanotes.setRows(5);
        tanotes.setName("panelnotes"); // NOI18N
        jScrollPane1.setViewportView(tanotes);

        javax.swing.GroupLayout panelNotesLayout = new javax.swing.GroupLayout(panelNotes);
        panelNotes.setLayout(panelNotesLayout);
        panelNotesLayout.setHorizontalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNotesLayout.createSequentialGroup()
                .addComponent(btnotes)
                .addContainerGap(863, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        panelNotesLayout.setVerticalGroup(
            panelNotesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNotesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnotes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(panelNotes);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      newAction("order");
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return;
        }
        double np = 0;
        double qty = 0;
        np = bsParseDouble(netprice.getText());
        qty = bsParseDouble(qtyshipped.getText());
        int line = 0;
        line = getmaxline();
        line++;
        
        boolean canproceed = validateDetail();
        String bom = "";
        if (ddbom.getSelectedItem() != null) {
            bom = ddbom.getSelectedItem().toString();
        }
        String uom = "";
        if (dduom.getSelectedItem() != null) {
            uom = dduom.getSelectedItem().toString();
        }
        //    "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice"
        if (canproceed) {
            myorddetmodel.addRow(new Object[]{line, tbitem.getText(), custnumber.getText(), tbkey.getText(), ponbr.getText(), 
                qtyshipped.getText(), uom, listprice.getText(), 
                discount.getText(), netprice.getText(), 
                "0", getGlobalProgTag("open"),
                ddwh.getSelectedItem().toString(), ddloc.getSelectedItem().toString(), tbdesc.getText(), 
                String.valueOf(bsFormatDouble(OVData.getTaxAmtApplicableByItem(tbitem.getText(), (np * qty) ))),
                bom, tbitemshipto.getText(), tbpackqty.getText(), tbaltitem.getText()
            });
            
            // lets collect tax elements for each item
            ArrayList<String[]> list = OVData.getTaxPercentElementsApplicableByItem(tbitem.getText());
            if (list != null) {
            linetax.put(line, list);
            } 
            
            
        refreshDisplayTotals();         
        listprice.setText("0");
        netprice.setText("0");
        discount.setText("0");
        qtyshipped.setText("0");
        tbitem.setText("");
        custnumber.setText("");
        tbdesc.setText("");
        tbitem.requestFocus();
        lbuomtext.setText("");
        tbpackqty.setText("");
        tbaltitem.setText("");
        
        // now update
        if (isSOCommitted) {   // assuming the order already exists in DB
           ArrayList<String> badlines = getBadLines(tbkey.getText());
           updateOrderTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord(), null, createTaxDetRecord(), null);
        }
        
        
        } else {// can proceed
        bsmf.MainFrame.show("validate detail: " + canproceed + " unable to proceed");
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
        
        if (! isLoad && ddcust.getItemCount() > 0) {
           custChangeEvent(ddcust.getSelectedItem().toString());
          
        } // if ddcust has a list
        
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return;
        }
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
            linetax.remove(orddet.getValueAt(i, 0));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
        
       // now update
        if (isSOCommitted) {   // assuming the order already exists in DB
           ArrayList<String> badlines = getBadLines(tbkey.getText());
           updateOrderTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord(), null, createTaxDetRecord(), null);
        } 
        
                refreshDisplayTotals();         
                listprice.setText("0");
                netprice.setText("0");
                discount.setText("0");
                qtyshipped.setText("0");
                tbitem.setText("");
                custnumber.setText("");
                tbdesc.setText("");
                tbitem.requestFocus();
                lbuomtext.setText("");
                tbpackqty.setText("");
                tbaltitem.setText("");
                
    }//GEN-LAST:event_btdelitemActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
           return;
        }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void netpriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netpriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netpriceActionPerformed

    private void qtyshippedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusGained
        if (qtyshipped.getText().equals("0")) {
            qtyshipped.setText("");
        }
    }//GEN-LAST:event_qtyshippedFocusGained

    private void qtyshippedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyshippedFocusLost
       
        if (isLoad) {
            return;
        }
        
        String x = BlueSeerUtils.bsformat("", qtyshipped.getText(), "5");
        if (x.equals("error")) {
            qtyshipped.setText("");
            qtyshipped.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            qtyshipped.requestFocus();
        } else {
            qtyshipped.setText(x);
            qtyshipped.setBackground(Color.white);
            //setPrice();
            eventTask("getPrice", new String[]{});
        }
    }//GEN-LAST:event_qtyshippedFocusLost

    private void listpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusLost
       
        if (isLoad) {
            return;
        }
        
        if (! listprice.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", listprice.getText(), "4");
        if (x.equals("error")) {
            listprice.setText("");
            listprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            listprice.requestFocus();
        } else {
            listprice.setText(x);
            listprice.setBackground(Color.white);
        }
        setNetPrice();
        }
    }//GEN-LAST:event_listpriceFocusLost

    private void discountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_discountFocusLost
        
        if (isLoad) {
            return;
        }
        /*
        if (! discount.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", discount.getText(), "4");
        if (x.equals("error")) {
            discount.setText("");
            discount.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            discount.requestFocus();
        } else {
            discount.setText(x);
            discount.setBackground(Color.white);
        }
        setNetPrice();
        }
        */
    }//GEN-LAST:event_discountFocusLost

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
        }
        executeTask(dbaction.delete, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbmiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbmiscActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbmiscActionPerformed

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
        if (! isLoad && ddwh.getSelectedItem() != null) {
           
             isLoad = true;
             ddloc.removeAllItems();
             ArrayList<String> loc = invData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
             ddloc.insertItemAt("", 0);
             ddloc.setSelectedIndex(0);
             isLoad = false;
             
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        if (! isLoad) {
          String loc = (ddloc.getSelectedItem() == null) ? "" : ddloc.getSelectedItem().toString();
          String wh = (ddwh.getSelectedItem() == null) ? "" : ddwh.getSelectedItem().toString();
          String uom = (dduom.getSelectedItem() == null) ? "" : dduom.getSelectedItem().toString();
          String site = (ddsite.getSelectedItem() == null) ? "" : ddsite.getSelectedItem().toString();
            eventTask("uomchange", new String[]{tbitem.getText(), site, uom, wh, loc, tbkey.getText()});
            
            if (dduom.getItemCount() > 0 && ! tbitem.getText().isBlank() && ddcust.getItemCount() > 0) {
                eventTask("getPrice", new String[]{});
            } 
            //setPrice();
        }    
    }//GEN-LAST:event_dduomActionPerformed

    private void ddlocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddlocActionPerformed
       
        if (! isLoad) {
          String loc = (ddloc.getSelectedItem() == null) ? "" : ddloc.getSelectedItem().toString();
          String wh = (ddwh.getSelectedItem() == null) ? "" : ddwh.getSelectedItem().toString();
          String uom = (dduom.getSelectedItem() == null) ? "" : dduom.getSelectedItem().toString();
          String site = (ddsite.getSelectedItem() == null) ? "" : ddsite.getSelectedItem().toString();
          eventTask("locchange", new String[]{tbitem.getText(), site, uom, wh, loc, tbkey.getText()});
        }
        
    }//GEN-LAST:event_ddlocActionPerformed

    private void btsacdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacdeleteActionPerformed
         int[] rows = sactable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(i);
        }
        retotal(); 
        refreshDisplayTotals();
         
    }//GEN-LAST:event_btsacdeleteActionPerformed

    private void btsacaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacaddActionPerformed
        boolean proceed = true;
        double amount = 0;
        Pattern p = Pattern.compile("^-?[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbsacamt.getText());
        if (!m.find() || tbsacamt.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1033));
            proceed = false;
            tbsacamt.requestFocus();
            return;
        }
        
        if (tbsacdesc.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            proceed = false;
            tbsacdesc.requestFocus();
            return;
        }
        
        if (ddsactype.getSelectedItem().toString().startsWith("shipping") &&
                (ddshipvia.getSelectedItem() == null || ddshipvia.getSelectedItem().toString().isBlank()) ) {
            bsmf.MainFrame.show(getMessageTag(1181));
            proceed = false;
            return;
        } 
        
        if (ddsactype.getSelectedItem().toString().equals("discount") &&
                ddsacamttype.getSelectedItem().toString().equals("amount")) {
            amount = -1 * bsParseDouble(tbsacamt.getText());
        } else {
            amount = bsParseDouble(tbsacamt.getText());
        }
        
        if (proceed)
        sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), tbsacdesc.getText(), ddsacamttype.getSelectedItem().toString(), String.valueOf(amount)});
        retotal();
        refreshDisplayTotals();
    }//GEN-LAST:event_btsacaddActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        //   "Line", "Part", "CustPart", "SO", "PO", "Qty", "UOM", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Tax"
        isLoad = true;  
        tbitem.setText(orddet.getValueAt(row, 1).toString());
        dduom.setSelectedItem(orddet.getValueAt(row, 6).toString());
        ddwh.setSelectedItem(orddet.getValueAt(row, 12).toString());
        ddloc.setSelectedItem(orddet.getValueAt(row, 13).toString());
        qtyshipped.setText(orddet.getValueAt(row, 5).toString());
        custnumber.setText(orddet.getValueAt(row, 2).toString());
        tbdesc.setText(orddet.getValueAt(row, 14).toString());
        listprice.setText(orddet.getValueAt(row, 7).toString());
        netprice.setText(orddet.getValueAt(row, 9).toString());
        discount.setText(orddet.getValueAt(row, 8).toString());
        
        currentline = orddet.getValueAt(row, 0).toString();
         btitemkv.setEnabled(true);
        // ddbom.setSelectedIndex(0);
        
        ddbom.removeAllItems();
        ddbom.insertItemAt("", 0);
        String primary = "";
        ArrayList<String[]> boms = invData.getBOMsByItemSite_mg(tbitem.getText());
        for (String[] wh : boms) {
            if (wh[0].equals("boms")) {
            ddbom.addItem(wh[1]);
            }
            if (wh[0].equals("bomprimary")) {
             primary = wh[1];
            }
        }
        ddbom.setSelectedItem(primary);
        
        
        if(orddet.getModel().getValueAt(row, 17).toString().isBlank()) {
          tbitemshipto.setText(tbshipto.getText());
        } else {
          tbitemshipto.setText(orddet.getModel().getValueAt(row, 17).toString());  
        }
        tbpackqty.setText(orddet.getModel().getValueAt(row, 18).toString());
        tbaltitem.setText(orddet.getModel().getValueAt(row, 19).toString());
       
        isLoad = false;
        //tbmisc.setText(orddet.getValueAt(row, 5).toString());
        
       /* blanket stuff not implemented yet
        String order = "";
        String po = "";
        String part = "";
        String line = "";
        
        if ( col == 0 && cbblanket.isSelected()) {
            line = orddet.getValueAt(row, 0).toString();  // line
            order = orddet.getValueAt(row, 3).toString();  // order
            po = orddet.getValueAt(row, 4).toString();  // po
            part = orddet.getValueAt(row, 1).toString();  // part
            this.getSchedRecords(order, po, part, line);
           
        }
       */
    }//GEN-LAST:event_orddetMouseClicked

    private void ddtaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtaxActionPerformed
        if (! isLoad && cms != null) {
           refreshTaxRecords();
        }
    }//GEN-LAST:event_ddtaxActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        
        if (! isLoad) {
        JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
            if (index == 4) {
                tanotes.setText("");
                ArrayList<String> notes = getSOMetaNotes(tbkey.getText(), "ordernotes");
                for (String n : notes) {
                    tanotes.append(n);
                    tanotes.append("\n");
                }
                
                // add EDI turn around data to Notes if flagged
                if (OVData.getSysMetaValue("system", "ordercontrol", "edita_to_notes").equals("1")) {
                    ArrayList<String[]> edita = getEDIMetaValueAll(ponbr.getText());
                    for (String[] x : edita) {
                        if (x[1].startsWith("detail")) {
                            continue;
                        }
                        tanotes.append(x[1] + "  " + x[2] + ":  " + x[3]);
                        tanotes.append("\n");
                    }
                }
                
            }
        }
        
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void btinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinvoiceActionPerformed
        
        
        
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return;
        }
        
        if (ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
          bsmf.MainFrame.show(getMessageTag(1184));  
          return;
        }
        
        Set<String> shiptos = new LinkedHashSet<String>();
        for (int j = 0; j < orddet.getRowCount(); j++) {
            shiptos.add(orddet.getModel().getValueAt(j, 17).toString());
        } 
        if (shiptos.size() > 1) {
           bsmf.MainFrame.show(getMessageTag(1177));
           return;
        }
        
        setPanelComponentState(this, false);
        
        executeTask(dbaction.run, new String[]{tbkey.getText()});      
       // String[] message = autoInvoice();
       
        
        
       
    }//GEN-LAST:event_btinvoiceActionPerformed

    private void btprintorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintorderActionPerformed
       OVData.printOrderRemote(tbkey.getText(), isMultiShip(), false);
      //  OVData.printCustomerOrder(tbkey.getText(), isMultiShip); 
    }//GEN-LAST:event_btprintorderActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void listpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_listpriceFocusGained
        if (listprice.getText().equals("0")) {
            listprice.setText("");
        }
    }//GEN-LAST:event_listpriceFocusGained

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset(); 
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btprintinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintinvoiceActionPerformed
       // OVData.printInvoiceByOrder(tbkey.getText());
        OVData.printInvoiceRemote(tbkey.getText(), "order", true);
      
    }//GEN-LAST:event_btprintinvoiceActionPerformed

    private void btprintpsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintpsActionPerformed
        OVData.printShipperRemote(tbkey.getText(), "order");
    }//GEN-LAST:event_btprintpsActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
            if (! isLoad) {
           
            }
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btupdateitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateitemActionPerformed
        if (! canupdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return;
        }
        int line = 0;
        String bom = "";
        if (ddbom.getSelectedItem() != null) {
            bom = ddbom.getSelectedItem().toString();
        }
        
        String shipto = "";
        if (! tbitemshipto.getText().isEmpty()) {
            shipto = tbitemshipto.getText();
        }
        
        line = getmaxline();
        line++;
        
        int[] rows = orddet.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        for (int i : rows) {
            if (orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("closed")) || orddet.getValueAt(i, 11).toString().equals(getGlobalProgTag("partial"))) {
                bsmf.MainFrame.show(getMessageTag(1088));
                return;
            } else if (! orddet.getValueAt(i, 1).toString().equals(tbitem.getText())) {
                bsmf.MainFrame.show(getMessageTag(1096));
                return;
            }else {
                boolean canproceed = validateDetail();
                if (canproceed) {
                orddet.setValueAt(qtyshipped.getText(), i, 5);
                orddet.setValueAt(dduom.getSelectedItem().toString(), i, 6);
                orddet.setValueAt(listprice.getText(), i, 7);
                orddet.setValueAt(discount.getText(), i, 8);
                orddet.setValueAt(netprice.getText(), i, 9);
                orddet.setValueAt(custnumber.getText(), i, 2);
                orddet.setValueAt(tbdesc.getText(), i, 14);
                orddet.setValueAt(ddwh.getSelectedItem().toString(), i, 12);
                orddet.setValueAt(ddloc.getSelectedItem().toString(), i, 13);
                orddet.setValueAt(bom, i, 16);
                orddet.getModel().setValueAt(shipto, i, 17);
                orddet.getModel().setValueAt(tbpackqty.getText(), i, 18);
                orddet.getModel().setValueAt(tbaltitem.getText(), i, 19);
                
                refreshDisplayTotals();         
                listprice.setText("0");
                netprice.setText("0");
                discount.setText("0");
                qtyshipped.setText("0");
                tbitem.setText("");
                custnumber.setText("");
                tbdesc.setText("");
                tbitem.requestFocus();
                lbuomtext.setText("");
                tbpackqty.setText("");
                tbaltitem.setText("");
                
                }
            }
        }
        
       
        
        // now update
        ArrayList<String> badlines = getBadLines(tbkey.getText());
        
        updateOrderTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord(), null, createTaxDetRecord(), null);
        
        
    }//GEN-LAST:event_btupdateitemActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpBillToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpBillToActionPerformed
       lookUpFrameBillTo();
    }//GEN-LAST:event_btLookUpBillToActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void ddbomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddbomActionPerformed

    private void ddsacamttypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsacamttypeActionPerformed
         if (ddsacamttype.getSelectedItem().toString().equals("percent")) {
            percentlabel.setText("percent");
        } else {
            percentlabel.setText("amount");
        }
    }//GEN-LAST:event_ddsacamttypeActionPerformed

    private void ddstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddstatusActionPerformed
        if (! isLoad && ddstatus.getSelectedItem() != null) {
            if (ddstatus.getSelectedItem().toString().equals(getGlobalProgTag("hold"))) {
                ddstatus.setBackground(Color.red); 
            } else {
                ddstatus.setBackground(null);  
            }
        }
    }//GEN-LAST:event_ddstatusActionPerformed

    private void btaddattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattachmentActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
        }
        OVData.addFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), this );
        getAttachments(tbkey.getText());
    }//GEN-LAST:event_btaddattachmentActionPerformed

    private void btdeleteattachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattachmentActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
        }
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
            int[] rows = tableattachment.getSelectedRows();
            String filename = null;
            for (int i : rows) {
                filename = tableattachment.getValueAt(i, 1).toString();
            }
            OVData.deleteFileAttachment(tbkey.getText(),this.getClass().getSimpleName(),filename);
            getAttachments(tbkey.getText());
        }
    }//GEN-LAST:event_btdeleteattachmentActionPerformed

    private void tableattachmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableattachmentMouseClicked
        int row = tableattachment.rowAtPoint(evt.getPoint());
        int col = tableattachment.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            OVData.openFileAttachment(tbkey.getText(), this.getClass().getSimpleName(), tableattachment.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_tableattachmentMouseClicked

    private void btLookUpShipToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpShipToActionPerformed
        lookUpFrameShipTo("shipto");
    }//GEN-LAST:event_btLookUpShipToActionPerformed

    private void btLookUpItemShipToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemShipToActionPerformed
        lookUpFrameShipTo("itemshipto");
    }//GEN-LAST:event_btLookUpItemShipToActionPerformed

    private void tbitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbitemActionPerformed
       if (isLoad) {
            return;
        }
        if (! tbitem.getText().isBlank()) {
         getItemInfo(tbitem.getText());
        }
    }//GEN-LAST:event_tbitemActionPerformed

    private void btnotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnotesActionPerformed
        String[] x = null;
        String text = tanotes.getText();
        if (text != null && ! text.isBlank()) {
            x = text.split("\n");
        }
        addUpdateSOMetaNotes(tbkey.getText(), "ordernotes", x);  
    }//GEN-LAST:event_btnotesActionPerformed

    private void btitemkvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btitemkvActionPerformed
        showEDIKV(ponbr.getText(), currentline);
    }//GEN-LAST:event_btitemkvActionPerformed

    private void tbpackqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpackqtyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tbpackqtyFocusGained

    private void tbpackqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpackqtyFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbpackqtyFocusLost

    private void btmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmailActionPerformed
        if (isValidOrder(tbkey.getText())) {
            Path filepath = OVData.printOrderRemote(tbkey.getText(), isMultiShip(), true);
            mailDoc(tbemail.getText(), "Sales Order Number: " + tbkey.getText(), "This is the content of the body", filepath);
        }
    }//GEN-LAST:event_btmailActionPerformed

    private void btcontactsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcontactsActionPerformed
        if (ddcust.getSelectedItem() != null && ddcust.getItemCount() > 0) {
        lookUpFrameContacts();
        }
    }//GEN-LAST:event_btcontactsActionPerformed

    private void btprintpickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintpickActionPerformed
        OVData.printPickRemote(tbkey.getText(), isMultiShip(), false);
    }//GEN-LAST:event_btprintpickActionPerformed

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
         if (! isLoad) {
            tbkey.setText(bsNumber(OVData.getNextNbr("order")));
            tbkey.setBackground(Color.yellow);
            //bsmf.MainFrame.show("choose new parent key and adjust ISA/GS IDs accordingly");
            tbkey.requestFocus();
            btadd.setEnabled(true);
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            btdelete.setEnabled(false);
            btupdate.setEnabled(false);
            btcopy.setEnabled(false);
            ddstatus.setSelectedItem(getGlobalProgTag("open"));
                for (int j = 0; j < orddet.getRowCount(); j++) {
                 orddet.setValueAt(0, j, 10);
                 orddet.setValueAt(getGlobalProgTag("open"), j, 11);
                }

            }
    }//GEN-LAST:event_btcopyActionPerformed

    private void btchangelogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchangelogActionPerformed
        callChangeDialog(tbkey.getText(), this.getClass().getSimpleName());
    }//GEN-LAST:event_btchangelogActionPerformed

    private void btclearitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearitemActionPerformed
        listprice.setText("0");
        netprice.setText("0");
        discount.setText("0");
        qtyshipped.setText("0");
        tbitem.setText("");
        custnumber.setText("");
        tbdesc.setText("");
        tbitem.requestFocus();
        lbuomtext.setText("");
        tbpackqty.setText("");
        tbaltitem.setText("");
        tbitem.requestFocus();
    }//GEN-LAST:event_btclearitemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpBillTo;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btLookUpItemShipTo;
    private javax.swing.JButton btLookUpShipTo;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattachment;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btchangelog;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearitem;
    private javax.swing.JButton btcontacts;
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattachment;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btinvoice;
    private javax.swing.JButton btitemkv;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btmail;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnotes;
    private javax.swing.JButton btprintinvoice;
    private javax.swing.JButton btprintorder;
    private javax.swing.JButton btprintpick;
    private javax.swing.JButton btprintps;
    private javax.swing.JButton btsacadd;
    private javax.swing.JButton btsacdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdateitem;
    private javax.swing.JCheckBox cbblanket;
    private javax.swing.JCheckBox cbcascade;
    private javax.swing.JCheckBox cbconfirm;
    private javax.swing.JCheckBox cbedi;
    private javax.swing.JCheckBox cbisallocated;
    private javax.swing.JCheckBox cbissourced;
    private javax.swing.JCheckBox cbplan;
    private javax.swing.JTextField custnumber;
    private com.toedter.calendar.JDateChooser dccreate;
    private javax.swing.JComboBox<String> ddbom;
    private javax.swing.JComboBox<String> ddcurr;
    private static javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> ddloc;
    private javax.swing.JComboBox<String> ddsacamttype;
    private javax.swing.JComboBox<String> ddsactype;
    private javax.swing.JComboBox<String> ddsalesrep;
    private javax.swing.JComboBox<String> ddsalesrep2;
    private javax.swing.JComboBox<String> ddshipfrom;
    private javax.swing.JComboBox ddshipvia;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox<String> ddtax;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JTextField discount;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel itemshipaddrlbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelLines;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSched;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel labelmessage;
    private javax.swing.JLabel lblCustItemAndDesc;
    private javax.swing.JLabel lblIsSourced;
    private javax.swing.JLabel lblcurr;
    private javax.swing.JLabel lblcustname;
    private javax.swing.JLabel lbldollars;
    private javax.swing.JLabel lblpart1;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel lbltotdollars;
    private javax.swing.JLabel lbqtyavailable;
    private javax.swing.JLabel lbuomtext;
    private javax.swing.JTextField listprice;
    private javax.swing.JTextField netprice;
    private com.toedter.calendar.JDateChooser orddate;
    private javax.swing.JTable orddet;
    private javax.swing.JPanel panelAttachment;
    private javax.swing.JPanel panelNotes;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTextField ponbr;
    private javax.swing.JTextField qtyshipped;
    private javax.swing.JTextField remarks;
    private javax.swing.JTable sactable;
    private javax.swing.JTable tableattachment;
    private javax.swing.JTextArea tanotes;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbaltitem;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcountry;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbitem;
    private javax.swing.JTextField tbitemshipto;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbmisc1;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbpackqty;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbsacamt;
    private javax.swing.JTextField tbsacdesc;
    private javax.swing.JTextField tbshipto;
    private javax.swing.JTextField tbstate;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField tbtottax;
    private javax.swing.JTextField tbtracking;
    private javax.swing.JTextField tbuserid;
    private javax.swing.JTextField tbzip;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
