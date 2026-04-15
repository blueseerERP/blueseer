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


import bsmf.MainFrame;
import static bsmf.MainFrame.defaultDecimalSeparator;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JTable;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import com.blueseer.ctr.cusData.cm_mstr;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.lbl.lblData.getLabelMstrByStrID;
import static com.blueseer.lbl.lblData.getLabelSerialDisplay;
import com.blueseer.lbl.lblData.label_mstr;
import static com.blueseer.lbl.lblData.updateLabelStatus;
import static com.blueseer.ord.ordData.getOrderLineInfo;
import static com.blueseer.shp.shpData.addShipperTransaction;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import java.awt.Component;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import java.util.HashSet;

/**
 *
 * @author vaughnte
 */
public class ShipScanMaint extends javax.swing.JPanel {

 boolean isLoad = false;
        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
                
                int j = 0;
                HashSet<String> assignedlabels = new HashSet<String>();
                HashSet<String> assigneditems = new HashSet<String>();
                String firstshipto = "";
                String firstbillto = "";
                String shipper = "";
                boolean autoconfirm = false;
                cm_mstr cm = null;
    
    // global datatablemodel declarations 
    javax.swing.table.DefaultTableModel serialmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("label"),
                getGlobalColumnTag("customer"),
                getGlobalColumnTag("shipcode"),
                getGlobalColumnTag("order"),
                getGlobalColumnTag("line"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("custitem"),
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("listprice"),
                getGlobalColumnTag("discount"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("po"),
                getGlobalColumnTag("site")});
    
    javax.swing.table.DefaultTableModel itemmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("qty")});
  
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public ShipScanMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "run":
                    message = postShip();    
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
            if (message[0].equals("0")) {
            bsmf.MainFrame.show(getMessageTag(1205,shipper));
            }
            initvars(null);
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
    
    public void setComponentDefaultValues(boolean init) {
      if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "autoshipconfirm");
        }
       
        
      tbscan.setText("");
       
      serialmodel.setRowCount(0);
      serialdet.setModel(serialmodel);
      serialdet.getTableHeader().setReorderingAllowed(false);
      
      itemmodel.setRowCount(0);
      itemdet.setModel(itemmodel);
      itemdet.getTableHeader().setReorderingAllowed(false);
      firstshipto = "";
       
      assigneditems.clear();
      assignedlabels.clear();
      lblmessage.setText("");
      shipper = "";
      cm = null;
      
      btcommit.setEnabled(false);
      
      for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("autopost")) {
              isAutoPost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("autoshipconfirm")) {
              autoconfirm = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
        }
       
      
        
      tbscan.requestFocusInWindow();
    }
     
    public String sumQtyByItem() {
        String x = "";
        double qty = 0;
        boolean doesExist = false;
        for (String s : assigneditems) {
          qty = 0;
          for (int j = 0; j < serialdet.getRowCount(); j++) {
            if (serialdet.getModel().getValueAt(j, 5).equals(s)) {
                qty += bsParseDouble(serialdet.getModel().getValueAt(j, 10).toString());
            }             
          }
          for (int j = 0; j < itemdet.getRowCount(); j++) {
            if (itemdet.getModel().getValueAt(j, 0).equals(s)) {
                doesExist = true;
                itemdet.getModel().setValueAt(String.valueOf(qty), j, 2);
                break;
            }             
          }
          if (! doesExist) {
              itemmodel.addRow(new Object[] { 
                s, // item
                "", // desc
                qty // qty
                }); 
          }
        }
        return x;
    }
    
    public shpData.ship_mstr createRecord(String shipper) {
                
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
        // get header info from first record in serialdet table
        shpData.ship_mstr x = new shpData.ship_mstr(null, 
                shipper,
                serialdet.getValueAt(0, 1).toString(),
                serialdet.getValueAt(0, 2).toString(),
                0, // pallets
                0, // boxes
                "", // shipvia  
                setDateDB(now),
                null, // po date
                "", // ref
                "", // po number
                "", // remarks
                bsmf.MainFrame.userid,
                serialdet.getValueAt(0,16).toString(),
                cm.cm_curr(), // currency
                "", // wh
                cm.cm_terms(),  // terms
                "", // taxcode
                cm.cm_ar_acct(),  // aracct
                cm.cm_ar_cc(),  // arcc
                "S", // type
                "", // sh_so 
                serialdet.getValueAt(0,16).toString(),
                "", // tracking number
                "", // status 
                "", // sh_char1
                "1", // sh_char2 shipper is confirmed ASN ready upon creation
                "" // sh_char3
                );
                
        return x;        
    }
    
    public ArrayList<shpData.ship_det> createDetRecord(String shipper) {
        ArrayList<shpData.ship_det> list = new ArrayList<shpData.ship_det>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // line, item, order, orderline, po, qty, netprice, desc, wh, loc, disc, listprice, tax, cont, serial
        for (int j = 0; j < serialdet.getRowCount(); j++) { 
            shpData.ship_det x = new shpData.ship_det(null, 
                shipper, // shipper
                j + 1, //shline
                serialdet.getValueAt(j, 5).toString(), // item
                serialdet.getValueAt(j, 7).toString(), // custimtem
                serialdet.getValueAt(j, 3).toString(),  // order
                bsParseInt(serialdet.getValueAt(j, 4).toString()), //soline    
                setDateDB(new java.util.Date()),
                serialdet.getValueAt(j, 15).toString(), // po
                bsParseDouble(serialdet.getValueAt(j, 10).toString().replace(defaultDecimalSeparator, '.')), // qty
                serialdet.getValueAt(j, 11).toString(), //uom
                "USD", //currency
                bsParseDouble(serialdet.getValueAt(j, 14).toString().replace(defaultDecimalSeparator, '.')), // net price
                bsParseDouble(serialdet.getValueAt(j, 13).toString().replace(defaultDecimalSeparator, '.')), // disc
                bsParseDouble(serialdet.getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.')), // list price
                serialdet.getValueAt(j, 6).toString(), // desc
                serialdet.getValueAt(j, 8).toString(), // wh
                serialdet.getValueAt(j, 9).toString(), // loc
                0, // taxamt
                "0", // cont
                "", // ref
                serialdet.getValueAt(j, 0).toString(), // serial   
                serialdet.getValueAt(j, 16).toString(),
                "", // bom
                0,  // packqty
                "" // kvpair    
                );
        list.add(x);
        }      
        return list;        
    }
   
    public ArrayList<shpData.ship_tree> createTreeRecord(String shipper) {
        ArrayList<shpData.ship_tree> list = new ArrayList<shpData.ship_tree>();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        // create shipper parent node with child containers
        for (String s : assignedlabels) {
            shpData.ship_tree x = new shpData.ship_tree(null,
            shipper,
            s,
            serialdet.getValueAt(0,16).toString(),
            "c",
            shipper,
            "",
            "",
            "",
            "",
            "container",
            1.0,
            getLabelSerialDisplay(s) // get display serial
            );
            
            list.add(x);
            // now items of container
            for (int j = 0; j < serialdet.getRowCount(); j++) { 
                if (serialdet.getValueAt(j, 0).toString().equals(s)) {
                    shpData.ship_tree y = new shpData.ship_tree(null,
                    s,
                    serialdet.getValueAt(j, 3).toString() + "," + serialdet.getValueAt(j, 4).toString() + "," + serialdet.getValueAt(j, 5).toString(),
                    serialdet.getValueAt(0,16).toString(),
                    "i",
                    shipper,
                    String.valueOf(j + 1),
                    serialdet.getValueAt(j, 3).toString(),
                    serialdet.getValueAt(j, 4).toString(),
                    serialdet.getValueAt(j, 15).toString(),
                    serialdet.getValueAt(j, 5).toString(),
                    bsParseDouble(serialdet.getValueAt(j, 10).toString().replace(defaultDecimalSeparator, '.')),
                    "" // get display serial
                    );
                    list.add(y);
                }
            }
        }
       
        return list;        
    }
    

        
    public boolean isDuplicate(String serial_id_str) {
        for (int j = 0; j < serialdet.getRowCount(); j++) {
            if (serialdet.getValueAt(j, 0).toString().equals(serial_id_str)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] postShip() {
      int shipperid = OVData.getNextNbr("shipper");  
      shipper = String.valueOf(shipperid);
      String[] m = addShipperTransaction(createDetRecord(String.valueOf(shipperid)), createRecord(String.valueOf(shipperid)), createTreeRecord(String.valueOf(shipperid)));
        for (String label : assignedlabels) {
            updateLabelStatus(label, "1");
        }
        shpData.updateShipperSAC(String.valueOf(shipperid));
        
        if (autoconfirm) {
        confirmShipperTransaction("", String.valueOf(shipperid), new java.util.Date());
        }
        return m;
    }
    
    public void validateScan(String scan) {
               
        
        lblmessage.setText("");
        if (scan.isEmpty()) {
            return;
        }
        
        label_mstr label = getLabelMstrByStrID(scan);
        if (label.m()[0].equals("0")) {
            
            if (bsParseInt(label.lbl_scan()) > 0) {
            tbscan.setText("");
            lblmessage.setText("Label already scanned: " + scan);
            lblmessage.setForeground(Color.red);
            tbscan.requestFocusInWindow();
            return;  
            }
            
            if (firstshipto.isEmpty()) {
                  firstshipto = label.lbl_shipto();
                  cm = getCustMstr(new String[]{label.lbl_billto()});
                  btcommit.setEnabled(true);
            }
            
         //   bsmf.MainFrame.show("HERE:  " + firstshipto + "/" + label.lbl_shipto());
            
            if (! label.lbl_shipto().equals(firstshipto)) {
                tbscan.setText("");
                lblmessage.setText("ShipTo must be same as first label scanned: " + firstshipto);
                lblmessage.setForeground(Color.red);
                tbscan.requestFocusInWindow();
                return;
            }
            
            String[] orderlineinfo = getOrderLineInfo(label.lbl_order(), label.lbl_line());  //  item, desc, ordqty, uom, netprice
            if (orderlineinfo == null) {
                tbscan.setText("");
                lblmessage.setText("cannot retrieve order line info: " + label.lbl_order() + "/" + label.lbl_line());
                lblmessage.setForeground(Color.red);
                tbscan.requestFocusInWindow();
                return;
            }
            
            if (! isDuplicate(label.lbl_id_str())) {
                serialmodel.addRow(new Object[] { 
                    label.lbl_id_str(), // serial
                    label.lbl_billto(), // billto
                    label.lbl_shipto(), // shipto
                    label.lbl_order(), // order
                    label.lbl_line(), // orderline
                    label.lbl_item(), // item
                    orderlineinfo[1], // desc
                    label.lbl_custitem(), // custitem
                    "", // wh
                    label.lbl_loc(), // loc
                    label.lbl_qty(), // qty
                    orderlineinfo[3], // uom
                    orderlineinfo[4], // listprice
                    orderlineinfo[5], // disc
                    orderlineinfo[6], // netprice
                    label.lbl_po(), // po
                    label.lbl_site()});

                if (! assigneditems.contains(label.lbl_item())) {
                  assigneditems.add(label.lbl_item());
                }
                
                if (! assignedlabels.contains(label.lbl_id())) {
                  assignedlabels.add(label.lbl_id());
                }
                
                

                sumQtyByItem();

              tbscan.setText("");
              lblmessage.setText("scanned: " + scan);
              lblmessage.setForeground(Color.black);
              tbscan.requestFocusInWindow();
              return;  
            } else {
              tbscan.setText("");
              lblmessage.setText("Duplicate Serial Number: " + scan);
              lblmessage.setForeground(Color.red);
              tbscan.requestFocusInWindow();
              return;  
            }
        } else {
          tbscan.setText("");
          lblmessage.setText("Bad / Unknown Serial Number: " + scan);
          lblmessage.setForeground(Color.red);
          tbscan.requestFocusInWindow();
          return;  
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
       setPanelComponentState(this, true); 
       setComponentDefaultValues(initDataSets == null);
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
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        lblmessage = new javax.swing.JLabel();
        btdeleteitem = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        labelPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        serialdet = new javax.swing.JTable();
        itemPanel = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        itemdet = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Scan Menu"));
        jPanel1.setName("panelmain"); // NOI18N

        tbscan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbscanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbscanFocusLost(evt);
            }
        });

        jLabel5.setText("Scan");
        jLabel5.setName("lblscan"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Remove Label");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        labelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Scanned Labels"));

        serialdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(serialdet);

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        itemPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Summarized Items"));

        itemdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane9.setViewportView(itemdet);

        javax.swing.GroupLayout itemPanelLayout = new javax.swing.GroupLayout(itemPanel);
        itemPanel.setLayout(itemPanelLayout);
        itemPanelLayout.setHorizontalGroup(
            itemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(itemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );
        itemPanelLayout.setVerticalGroup(
            itemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btdeleteitem, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btcommit, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(itemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btclear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(btdeleteitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                .addComponent(itemPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addGap(94, 94, 94))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(126, 126, 126)
                    .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(304, Short.MAX_VALUE)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        // aracct, arcc, currency, bank, terms, carrier, onhold, site, taxcode
        if (cm == null) {
            bsmf.MainFrame.show(getMessageTag(1104)); 
            return;
        }
        
        if (serialdet.getRowCount() < 1) {
            bsmf.MainFrame.show(getMessageTag(1188));
            return;
        }
        
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.run, new String[]{""});
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbscanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusGained
       tbscan.setBackground(Color.yellow);
       
    }//GEN-LAST:event_tbscanFocusGained

    private void tbscanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusLost
        tbscan.setBackground(Color.white);
        validateScan(tbscan.getText());        
    }//GEN-LAST:event_tbscanFocusLost

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = serialdet.getSelectedRows();
        String targetlabel = "";
        for (int i : rows) {
            targetlabel = serialdet.getModel().getValueAt(i, 0).toString();
        }

        ArrayList<Integer> rowsToDelete = new ArrayList<Integer>();
        for (int i = 0; i < serialdet.getRowCount(); i++) {
            if (serialdet.getModel().getValueAt(i, 0).toString().equals(targetlabel)) {
                rowsToDelete.add(i);

            }
        }
        Collections.reverse(rowsToDelete);
        for (int j : rowsToDelete) {
            ((javax.swing.table.DefaultTableModel) serialdet.getModel()).removeRow(j);
        }

        assignedlabels.remove(targetlabel);
        sumQtyByItem();

    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JPanel itemPanel;
    private javax.swing.JTable itemdet;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JTable serialdet;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
