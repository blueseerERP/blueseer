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
package com.blueseer.shp;


import com.blueseer.lbl.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.getCMSDet;
import static com.blueseer.ctr.cusData.getCustInfo;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.lbl.lblData.addLabelMstr;
import static com.blueseer.lbl.lblData.addMixedLabelTransaction;
import static com.blueseer.lbl.lblData.addMultiLabelTransaction;
import static com.blueseer.lbl.lblData.deleteLabelByShipper;
import static com.blueseer.lbl.lblData.getLabelSerialDisplay;
import static com.blueseer.lbl.lblData.getLabelZebraMstr;
import com.blueseer.lbl.lblData.label_det;
import com.blueseer.lbl.lblData.label_mstr;
import com.blueseer.lbl.lblData.label_zebra;
import static com.blueseer.lbl.lblData.updateLabelStatus;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.getOrderLineInfo;
import static com.blueseer.ord.ordData.getOrderMstr;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import com.blueseer.ord.ordData.so_mstr;
import com.blueseer.shp.shpData.Shipper;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import static com.blueseer.shp.shpData.deleteShipMstr;
import static com.blueseer.shp.shpData.getShipperBillto;
import static com.blueseer.shp.shpData.getShipperMstrSet;
import static com.blueseer.shp.shpData.getShipperStatus;
import com.blueseer.shp.shpData.ship_det;
import com.blueseer.shp.shpData.ship_mstr;
import static com.blueseer.shp.shpData.updateShipTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkDigitUCC18;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.isNumbersEqual;
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
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import com.blueseer.utl.DTData;
import static com.blueseer.utl.EDData.updateEDIASNStatus;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.checkForCustomPath;
import static com.blueseer.utl.OVData.getSystemLabelDirectory;
import static com.blueseer.utl.OVData.printJasperLabel;
import static com.blueseer.utl.OVData.printJasperLabelMulti;
import static com.blueseer.utl.OVData.printJasperLabelMultiNew;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author vaughnte
 */
public class ShipOrderLine extends javax.swing.JPanel {


     // global variable declarations
    boolean isLoad = false;
    public static ship_mstr sh = null;
    public static ArrayList<ship_det> shdlist = null;
    HashSet<String> assignedlabels = new HashSet<String>();
    boolean autoconfirm = false;
    boolean autonumber = true;
    String firstshipto = "";
    String firstbillto = "";
    boolean hasInit = false;
    public static ordData.salesOrder orderSet = null;
  
   
   ShipTableModel shipmodel = new ShipTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("po"), 
                getGlobalColumnTag("order"),
                getGlobalColumnTag("line"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("custitem"), 
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("packqty"),
                getGlobalColumnTag("contqty"),
                getGlobalColumnTag("remainder"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("listprice"),
                getGlobalColumnTag("discount"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location")
            });
    
   class ShipTableModel extends DefaultTableModel {  
      
        public ShipTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }
        public boolean isCellEditable(int rowIndex, int columnIndex) {
             boolean[] canEdit = new boolean[]{false, false, false, false, false, false, true, false, false, false}; 
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
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public ShipOrderLine() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(String x, String[] y) { 
      
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
                    if (this.key[0].equals("getOrderMstrSet")) {
                      message = run_getOrderMstrSet(this.key[1]);
                    }
                        
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
    
    public void setComponentDefaultValues() {
        lblcust.setText("");
        lblship.setText("");
        lblitem.setText("");
        tbqty.setText("");
        tbordnbr.setText("");
        tbline.setText("");
        tbpackqty.setText("");
        tbkey.setText("");
        tbkey.setEditable(false);
        cbcomplete.setSelected(false);
       assignedlabels.clear();
        
        shipmodel.setRowCount(0);
        shiptable.setModel(shipmodel);
        shiptable.getTableHeader().setReorderingAllowed(false);
        
        if (! hasInit) {
            TableColumnModel tcm = shiptable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(10));   // remove last four columns from view
        tcm.removeColumn(tcm.getColumn(10));
        tcm.removeColumn(tcm.getColumn(10));
        tcm.removeColumn(tcm.getColumn(10));
        hasInit = true;
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
    
    public String[] setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
                   if (sh.sh_status().equals("1")) {
                      btadd.setEnabled(false);
                      btupdate.setEnabled(false);
                      btdelete.setEnabled(false);
                   }
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public void newAction(String x) {
      setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btprintlabels.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setForeground(Color.blue);
        tbkey.requestFocus();
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } else {
          tbkey.setEditable(true);  
        }
    }
    
    public String[] addRecord(String[] x) {
        orderSet = getOrderMstrSet(new String[]{shiptable.getValueAt(0, 1).toString()});
       
        ArrayList<label_mstr> lmarray = createLabelRecord(orderSet.cm(), orderSet.cms());
        // add label records
        String[] m = addMultiLabelTransaction(null, lmarray);
        if (! m[0].equals("0")) {
            return m;
        }
        
        // now add shipper
        ArrayList<ship_det> sdarray = createDetRecord(orderSet.so(), orderSet.cm());
        m = addShipperTransaction(sdarray, createRecord(orderSet.so(), orderSet.cm()), createTreeRecord(orderSet.so(), orderSet.cm(), lmarray, sdarray));
        for (String label : assignedlabels) {
            updateLabelStatus(label, "1");
        }
        shpData.updateShipperSAC(tbkey.getText());
        if (autoconfirm) {
        confirmShipperTransaction("", tbkey.getText(), new java.util.Date());
        }
        return m;
    }
     
    public String[] updateRecord(String[] x) {
     orderSet = getOrderMstrSet(new String[]{shiptable.getValueAt(0, 1).toString()});
     String[] m = updateShipTransaction(null, createDetRecord(orderSet.so(), orderSet.cm()), createRecord(orderSet.so(), orderSet.cm()));
     
     if (m[0].equals("0") && changed()) {
         deleteLabelByShipper(x[0]);
         addMultiLabelTransaction(null, createLabelRecord(orderSet.cm(), orderSet.cms()));
         updateEDIASNStatus(x[0], "0");
     }
     
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
        String[] m = null;
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
            deleteLabelByShipper(x[0]);
            m = deleteShipMstr(x[0]);
        } 
     return m;
     }
      
    public String[] getRecord(String[] x) {
      Shipper z = getShipperMstrSet(x);
      sh = z.sh();
      shdlist = z.shd();
      return z.m();
    }
   
    public shpData.ship_mstr createRecord(so_mstr so, cm_mstr cm) {
       
        shpData.ship_mstr x = new shpData.ship_mstr(null, 
                tbkey.getText(),
                so.so_cust(),
                so.so_ship(),
                0, // pallets
                0, // boxes
                so.so_shipvia(), // shipvia  
                setDateDB(new java.util.Date()),
                so.so_ord_date(), // po date
                tbref.getText().replace("'", ""),
                so.so_po(), // po number
                so.so_rmks(),
                bsmf.MainFrame.userid,
                so.so_site(),
                so.so_curr(),
                "", // wh
                cm.cm_terms(), // terms
                "", // taxcode
                cm.cm_ar_acct(), // aracct
                cm.cm_ar_cc(), // arcc
                "S", // type
                so.so_nbr(), // sh_so 
                so.so_site(),
                "", // tracking number
                "", // status
                "", // sh_char1
                String.valueOf(BlueSeerUtils.boolToInt(cbcomplete.isSelected())), // sh_char2 
                "" // sh_char3
                );
                
        return x;        
    }
    
    public ArrayList<shpData.ship_det> createDetRecord(so_mstr so, cm_mstr cm) {
        ArrayList<shpData.ship_det> list = new ArrayList<shpData.ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        // shipmodel   po, order, line, item, desc, custitem, wh, loc, qty, uom, price, packqty, contqty, remainder
        
        for (int j = 0; j < shiptable.getRowCount(); j++) { 
            shpData.ship_det x = new shpData.ship_det(null, 
                tbkey.getText(), // shipper
                j + 1, //shline
                shiptable.getModel().getValueAt(j, 3).toString(), // item
                shiptable.getModel().getValueAt(j, 5).toString(), // custimtem
                shiptable.getModel().getValueAt(j, 1).toString(),  // order
                bsParseInt(shiptable.getModel().getValueAt(j, 2).toString()), //soline    
                setDateDB(new java.util.Date()),
                shiptable.getModel().getValueAt(j, 0).toString(), // po
                bsParseDouble(shiptable.getModel().getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.')), // qty
                shiptable.getModel().getValueAt(j, 10).toString(), //uom
                so.so_curr(), //currency 
                bsParseDouble(shiptable.getModel().getValueAt(j, 13).toString().replace(defaultDecimalSeparator, '.')), // net price
                bsParseDouble(shiptable.getModel().getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.')), // disc
                bsParseDouble(shiptable.getModel().getValueAt(j, 11).toString().replace(defaultDecimalSeparator, '.')), // list price
                shiptable.getModel().getValueAt(j, 4).toString(), // desc
                shiptable.getModel().getValueAt(j, 14).toString(), // wh
                shiptable.getModel().getValueAt(j, 15).toString(), // loc
                0, // taxamt
                "0", // cont
                tbref.getText(), // ref
                shiptable.getModel().getValueAt(j, 5).toString(), // serial   
                so.so_site(),
                "", // bom
                bsParseDouble(shiptable.getModel().getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.')),  // packqty
                "" // kvpair    
                );
        list.add(x);
        }      
        return list;        
    }
    
    public ArrayList<shpData.ship_tree> createTreeRecord(so_mstr so, cm_mstr cm, ArrayList<label_mstr> lmarray, ArrayList<ship_det> sdarray) {
        ArrayList<shpData.ship_tree> list = new ArrayList<shpData.ship_tree>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // create shipper parent node with child containers
        for (String s : assignedlabels) {
            shpData.ship_tree x = new shpData.ship_tree(null,
            tbkey.getText(), // parent
            s,   // child
            so.so_site(), // site
            "c", // type container
            tbkey.getText(), // shipper
            "",  // shipperline
            "",  // so
            "",  // soline
            "",  // po
            "container",  // item
            1.0,  // qty
            getLabelSerialDisplay(s) // get display serial
            );
            
            list.add(x);
            // now items of container
            int j = 0;
            for (label_mstr lm : lmarray) { 
                if (lm.lbl_id().equals(s)) {
                    j++;
                    shpData.ship_tree y = new shpData.ship_tree(null,
                    s,
                    lm.lbl_order() + "," + lm.lbl_line() + "," + lm.lbl_item(),
                    so.so_site(),
                    "i",
                    tbkey.getText(),
                    getShipDetLine(sdarray, lm.lbl_order(), lm.lbl_line()),
                    lm.lbl_order(),
                    lm.lbl_line(),
                    lm.lbl_po(),
                    lm.lbl_item(),
                    bsParseDouble(lm.lbl_qty()),
                    "" // get display serial
                    );
                    list.add(y);
                }
            }
        }
       
        return list;        
    }
      
    public void updateForm() throws ParseException {
        
        shipmodel.setRowCount(0);
        
        tbkey.setText(sh.sh_id());
        cbcomplete.setSelected(BlueSeerUtils.ConvertStringToBool(sh.sh_char2()));
        
        for (ship_det shd : shdlist) {
                      
        int nbrOfContainers = 0;
        int remainder = 0;
        if (shd.shd_packqty() > 0) {
            nbrOfContainers = ( (int) shd.shd_qty() / (int) shd.shd_packqty());
            remainder = ( (int) shd.shd_qty() % (int) shd.shd_packqty());
        } 
       
            
            shipmodel.addRow(new Object[]{
                shd.shd_po(), 
                shd.shd_so(),
                shd.shd_soline(),
                shd.shd_item(),
                shd.shd_desc(),
                shd.shd_custitem(),
                bsFormatDouble(shd.shd_qty()),
                bsFormatDouble(shd.shd_packqty()),
                bsFormatDouble(nbrOfContainers), 
                bsFormatDouble(remainder),
                shd.shd_uom(),
                shd.shd_listprice(),
                shd.shd_disc(),
                shd.shd_netprice(),
                shd.shd_wh(),
                shd.shd_loc()});
            
        }
        
       // getAttachments(tbkey.getText());
        
        setAction(sh.m()); 
        
        //sh = null;
        //shdlist = null;

    }
   
    public String[] run_getOrderMstrSet(String order) {
        orderSet = getOrderMstrSet(new String[]{order});
        if (orderSet == null) {
           bsmf.MainFrame.show(getMessageTag(1034,order));   
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }
    
    public void initvars(String[] arg) {
        
        
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask("get",arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
        
        
    }
    
    public void lookUpFrameOrder() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
                
        
        if (lurb1.isSelected()) {  
         luModel = DTData.getOpenOrderBrowseUtil(luinput.getText(),0, "so_nbr");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getOpenOrderBrowseUtil(luinput.getText(),0, "so_po");   
        } else {
         luModel = DTData.getOpenOrderBrowseUtil(luinput.getText(),0, "so_cust");   
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
                tbordnbr.setText(target.getValueAt(row,1).toString());
                tbordnbr.setEditable(false);
                
                //tbline.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("order"), 
                getGlobalColumnTag("po"),        
                getGlobalColumnTag("customer"));
        
        
    }

    public void lookUpFrameLine() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderLineBrowseUtil(luinput.getText(), "sod_item", tbordnbr.getText() );
        } else {
         luModel = DTData.getOrderLineBrowseUtil(luinput.getText(), "sod_desc", tbordnbr.getText());
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
                tbline.setText(target.getValueAt(row,1).toString());
                tbqty.setText(BlueSeerUtils.bsNumber(target.getValueAt(row,4).toString()));
                lblitem.setText(target.getValueAt(row,2).toString() + " " + target.getValueAt(row,3).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"));
        
        
    }

    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_id");
        } else if (lurb2.isSelected()) {
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_cust");   
        } else {
         luModel = DTData.getShipperBrowseUtil(luinput.getText(),0, "sh_po");   
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
      
        
        callDialog(getGlobalColumnTag("key"), getGlobalColumnTag("customer"), getGlobalColumnTag("po")); 
        
    }
    
    public boolean validateInput(boolean itemlevel) {
        
        if (itemlevel) {
            if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
                bsmf.MainFrame.show(getMessageTag(1028));
                tbqty.requestFocus();
                tbqty.setBackground(Color.yellow);
                return false;
            } else {
                tbqty.setBackground(Color.white);
            }
        }
        
        if (itemlevel && tbordnbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbordnbr.requestFocus();
            return false;
        }
        if (itemlevel && tbline.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbline.requestFocus();
            return false;
        }
        
       
        
       
        if (! itemlevel) {
            
            Map<String,Integer> f = OVData.getTableInfo(new String[]{"ship_mstr"});
            int fc;

            fc = checkLength(f,"sh_id");
            if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
                bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
                tbkey.requestFocus();
                return false;
            } 
        
            if (shiptable.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1164));
            return false;
            }
        }
        
        return true;
    }
    
    public ArrayList<label_mstr> createLabelRecord(cm_mstr cm, cms_det cms) { 
        ArrayList<label_mstr> mstr = new ArrayList<label_mstr>();
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
        int conts = 0;
        int remainder = 0;
        int serialno = 0;
        String label = cm.cm_label();
        String serialno_str = "";
        String serialno_display = "";
        for (int j = 0; j < shiptable.getRowCount(); j++) {
             conts = Integer.parseInt(shiptable.getModel().getValueAt(j, 8).toString());
             remainder = Integer.parseInt(shiptable.getModel().getValueAt(j, 9).toString());
            for (int k = 0; k < conts; k++ ) {
                
            serialno = OVData.getNextNbr("label");            
            serialno_str = String.valueOf(serialno);
            if (label.startsWith("sscc18")) {
             serialno_display = checkDigitUCC18(serialno);
            } else {
             serialno_display = serialno_str;   
            }    
                 
             label_mstr x = new label_mstr(null, 
                     serialno_str, 
                     shiptable.getModel().getValueAt(j, 3).toString(), //item
                     shiptable.getModel().getValueAt(j, 5).toString(), // custitem 
                     serialno_display, 
                     "XX", 
                     label,
                     shiptable.getModel().getValueAt(j, 7).toString(), // qty (pack) 
                     shiptable.getModel().getValueAt(j, 0).toString(), // po 
                     cm.cm_code(),
                     shiptable.getModel().getValueAt(j, 1).toString(), // order
                     shiptable.getModel().getValueAt(j, 2).toString(), // line
                     tbkey.getText(), // ref 
                     "",  // lot 
                     "0", 
                     "0", 
                     cms.cms_shipto(),
                     cms.cms_name(), 
                     cms.cms_line1(), 
                     cms.cms_line2(), 
                     cms.cms_city(), 
                     cms.cms_state(), 
                     cms.cms_zip(), 
                     cms.cms_country(), 
                     setDateFormat(now), 
                     setDateFormat(now), 
                     bsmf.MainFrame.userid, 
                     "", // printer 
                     "ShipOrderLine", 
                     cm.cm_site(), 
                     "", // loc
                     "CONT",
                     "", // type
                     cms.cms_shipto(),
                     "0", // scan
                     "0",  // void
                     "0"  // post
                    );
             mstr.add(x);
             assignedlabels.add(serialno_str);
             }
             // then add one more for remainder > 0
             if (remainder > 0) {
                serialno = OVData.getNextNbr("label");            
                serialno_str = String.valueOf(serialno);
                if (label.startsWith("sscc18")) {
                 serialno_display = checkDigitUCC18(serialno);
                } else {
                 serialno_display = serialno_str;   
                }    
                 
                label_mstr x = new label_mstr(null, 
                     serialno_str, 
                     shiptable.getModel().getValueAt(j, 5).toString(), //item
                     shiptable.getModel().getValueAt(j, 5).toString(), // custitem 
                     serialno_display, 
                     "XX", 
                     label,
                     String.valueOf(remainder), // qty (remainder) 
                     shiptable.getModel().getValueAt(j, 0).toString(), // po 
                     cm.cm_code(),
                     shiptable.getModel().getValueAt(j, 1).toString(), // order
                     shiptable.getModel().getValueAt(j, 2).toString(), // line
                     tbkey.getText(), // ref 
                     "",  // lot 
                     "0", 
                     "0", 
                     cms.cms_shipto(),
                     cms.cms_name(), 
                     cms.cms_line1(), 
                     cms.cms_line2(), 
                     cms.cms_city(), 
                     cms.cms_state(), 
                     cms.cms_zip(), 
                     cms.cms_country(), 
                     setDateFormat(now), 
                     setDateFormat(now), 
                     bsmf.MainFrame.userid, 
                     "", // printer 
                     "ShipOrderLine", 
                     cm.cm_site(), 
                     "", // loc
                     "CONT",
                     "", // type
                     cms.cms_shipto(),
                     "0", // scan
                     "0",  // void
                     "0"  // post
                    );
             mstr.add(x);
             assignedlabels.add(serialno_str);
             } // if remainder > 0
         }       
        
        return mstr;
    }
   
    public String getShipDetLine(ArrayList<ship_det> sdarray, String soorder, String soline) {
        String r = "0";
        for (ship_det sd : sdarray ) { 
            if (sd.shd_so().equals(soorder) && sd.shd_soline() == bsParseInt(soline)) {
                r = bsNumber(sd.shd_line());
            }
        }
        return r;
    }
    public boolean changed() {
        boolean r = false;
        int i = 0;
        if (shdlist.size() != shipmodel.getRowCount()) {
            r = true;
        }
        for (ship_det shd : shdlist) {
            if (! shiptable.getModel().getValueAt(i, 0).toString().equals(shd.shd_po())) {
                r = true;
            }
            if (! shiptable.getModel().getValueAt(i, 3).toString().equals(shd.shd_item())) {
                r = true;
            }
            if (! isNumbersEqual(shiptable.getModel().getValueAt(i, 6).toString(), String.valueOf(shd.shd_qty()))) {
                r = true; 
            } 
            if (! isNumbersEqual(shiptable.getModel().getValueAt(i, 7).toString(), String.valueOf(shd.shd_packqty()))) {
                r = true; 
            }
            /*
            if (! shiptable.getModel().getValueAt(i, 6).toString().equals(shd.shd_qty())) {
                r = true;
            }
            if (! shiptable.getModel().getValueAt(i, 7).toString().equals(shd.shd_packqty())) {
                r = true;
            }
            */
            i++; 
        }
       
        return r;
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
        btprintlabels = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        tbordnbr = new javax.swing.JTextField();
        tbline = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btlookupOrder = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        shiptable = new javax.swing.JTable();
        btadditem = new javax.swing.JButton();
        btdeleteitem = new javax.swing.JButton();
        btlookupLine = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        lblcust = new javax.swing.JLabel();
        lblship = new javax.swing.JLabel();
        lblitem = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbpackqty = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        cbcomplete = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper By Order/Line With Labels"));
        jPanel1.setName("panelmain"); // NOI18N

        btprintlabels.setText("Print");
        btprintlabels.setName("btprintlabels"); // NOI18N
        btprintlabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintlabelsActionPerformed(evt);
            }
        });

        jLabel3.setText("Order Number");
        jLabel3.setName("lblorder"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel4.setText("Total Ship Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        tbline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tblineActionPerformed(evt);
            }
        });

        jLabel6.setText("Order Line");
        jLabel6.setName("lblorderline"); // NOI18N

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

        btlookupOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderActionPerformed(evt);
            }
        });

        shiptable.setModel(new javax.swing.table.DefaultTableModel(
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
        shiptable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                shiptableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(shiptable);

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Delete Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btlookupLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupLineActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel1.setText("Pack Quantity");

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

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel5.setText("Shipper Key");

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        cbcomplete.setText("Complete");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(btprintlabels)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblship, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblcust, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookupOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookupLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(tbpackqty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(21, 21, 21)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbcomplete)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(12, 12, 12)
                .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnew)
                        .addComponent(btclear)
                        .addComponent(jLabel5)
                        .addComponent(cbcomplete))
                    .addComponent(btlookup))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btlookupOrder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(btlookupLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblitem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpackqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblcust, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblship, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadditem)
                            .addComponent(btdeleteitem))
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btprintlabels)
                            .addComponent(btadd)
                            .addComponent(btupdate)
                            .addComponent(btdelete))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(34, 34, 34))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintlabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintlabelsActionPerformed

        try {

           // String label = cusData.getCustLabel(sh.sh_cust());
           // label  = (label.isBlank()) ? "sscc18Jm" : label; 
           
            String label = "sscc18Jm";  // hardcoded as this specific jasper is required for this class usage
            label_zebra lz = getLabelZebraMstr(new String[]{label});
            if (lz.lblz_file().endsWith("jasper")) {
                printJasperLabelMultiNew(tbkey.getText(), lz.lblz_file());
            } else {
                bsmf.MainFrame.show(getMessageTag(1206));  
            }

        } catch (Exception e) {
        MainFrame.bslog(e);
        }
              
        
    }//GEN-LAST:event_btprintlabelsActionPerformed

    private void btlookupOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderActionPerformed
        lookUpFrameOrder();
    }//GEN-LAST:event_btlookupOrderActionPerformed

    private void btlookupLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupLineActionPerformed
        lookUpFrameLine();
    }//GEN-LAST:event_btlookupLineActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        if (! validateInput(true)) {
            return;
        }
        if (firstshipto.isEmpty()) {
            so_mstr so = getOrderMstr(new String[]{tbordnbr.getText()});            
                  firstshipto = so.so_ship();
                  firstbillto = so.so_cust();
                  btadd.setEnabled(true);
        } else {
            so_mstr so = getOrderMstr(new String[]{tbordnbr.getText()});
            if (! firstshipto.equals(so.so_ship()) && ! firstbillto.equals(so.so_cust())) {
                bsmf.MainFrame.show("Different shipto/billto than first entry...all entries must be to same billto/shipto");
                return;
            }
        }
        
        String[] x = getOrderLineInfo(tbordnbr.getText(), tbline.getText());  // returns item, desc, ordqty, uom, listprice, disc, netprice, custitem, wh, loc, po
        
        if (x == null) {
            return;
        }     
        
        int nbrOfContainers = 0;
        int remainder = 0;
        if (! tbpackqty.getText().isEmpty()) {
            nbrOfContainers = Integer.valueOf(tbqty.getText()) / Integer.valueOf(tbpackqty.getText());
            remainder = Integer.valueOf(tbqty.getText()) % Integer.valueOf(tbpackqty.getText());
        }    
         /*     
        shipmodel.addRow(new Object[]{ x[8], // po 
            tbordnbr.getText(), 
            tbline.getText(), 
            x[0], 
            x[1], 
            x[5], 
            x[6], 
            x[7], 
            tbqty.getText(), 
            x[3], 
            x[4],              
            tbpackqty.getText(), 
            nbrOfContainers, 
            remainder }); 
        */
        shipmodel.addRow(new Object[]{ x[10], // po 
            tbordnbr.getText(), 
            tbline.getText(), 
            x[0], 
            x[1], 
            x[7], 
            tbqty.getText(),   
            tbpackqty.getText(), 
            nbrOfContainers, 
            remainder,
            x[3], 
            x[4],
            x[5],
            x[6],
            x[8], 
            x[9]}); 
        
        tbqty.setText("");
        tbpackqty.setText("");
        tbref.setText("");
        tbordnbr.setText("");
        tbline.setText("");
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = shiptable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) shiptable.getModel()).removeRow(i);
            
        }
        
        tbqty.setText("");
        tbpackqty.setText("");
        tbref.setText("");
        tbordnbr.setText("");
        tbline.setText("");
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tblineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tblineActionPerformed
       if (! tbordnbr.getText().isBlank() && ! tbline.getText().isBlank()) {
       String[] info = getOrderLineInfo(tbordnbr.getText(), tbline.getText());
       tbqty.setText(info[2]);
       lblitem.setText(info[0] + " " + info[1]);
       }
    }//GEN-LAST:event_tblineActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
         if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            tbqty.setBackground(Color.yellow);
            return;
        } else {
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(false)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(false)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(false)) {
            return;
        }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText()});
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void shiptableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_shiptableMouseClicked
        int row = shiptable.rowAtPoint(evt.getPoint());
        tbqty.setText(shiptable.getModel().getValueAt(row, 6).toString());
        tbpackqty.setText(shiptable.getModel().getValueAt(row, 7).toString());
        tbref.setText("");
        tbordnbr.setText(shiptable.getModel().getValueAt(row, 1).toString());
        tbline.setText(shiptable.getModel().getValueAt(row, 2).toString());  
    }//GEN-LAST:event_shiptableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupLine;
    private javax.swing.JButton btlookupOrder;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprintlabels;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbcomplete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblcust;
    private javax.swing.JLabel lblitem;
    private javax.swing.JLabel lblship;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTable shiptable;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbpackqty;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
