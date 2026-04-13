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

package com.blueseer.vdr;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerV;
import com.blueseer.utl.OVData;
import static com.blueseer.vdr.venData.addVprMstr;
import static com.blueseer.vdr.venData.deleteVprMstr;
import static com.blueseer.vdr.venData.getVprMstr;
import static com.blueseer.vdr.venData.getVprPriceLists;
import static com.blueseer.vdr.venData.updateVprMstr;
import com.blueseer.vdr.venData.vpr_mstr;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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

/**
 *
 * @author vaughnte
 */
public class VendPriceMaint extends javax.swing.JPanel implements IBlueSeerV {

    
    
    // global variable declarations
        boolean isLoad = false;
        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
    
                private static vpr_mstr x = null;
    // global datatablemodel declarations   
     DefaultListModel pricelistmodel = new DefaultListModel();           
                
    public VendPriceMaint() {
        initComponents();
        setLanguageTags(this);
    }

    // interface functions implemented
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
                    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
               if (this.type.equals("get")) {
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
    
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
       
       if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "currencies,items,uoms");
        }
       
       tbkey.setText("");
        tbprice.setText("");
         
         pricelist.setModel(pricelistmodel);
         
         dduom.removeAllItems();
         ddcurr.removeAllItems();
         dditem.removeAllItems();
        pricelistmodel.removeAllElements();
        
         for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
           
            if (s[0].equals("currencies")) {
              ddcurr.addItem(s[1]); 
            }
            if (s[0].equals("items")) {
              dditem.addItem(s[1]); 
            }
            if (s[0].equals("uoms")) {
              dduom.addItem(s[1]); 
            }
        }
        
        if (ddcurr.getItemCount() > 0) {
          ddcurr.setSelectedItem(defaultCurrency);
        }        
        
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
    
       
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues(false);
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
       
    }
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
         
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
         
        
                if (dditem.getSelectedItem() == null || dditem.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    dditem.requestFocus();
                    return b;
                }
                
                if (dduom.getSelectedItem() == null || dduom.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    dduom.requestFocus();
                    return b;
                }
               
                if (ddcurr.getSelectedItem() == null || ddcurr.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1026));
                    ddcurr.requestFocus();
                    return b;
                }
                
                
                
                if (tbprice.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbprice.requestFocus();
                    return b;
                }
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues(initDataSets == null);
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
                
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
     String[] m = addVprMstr(createRecord());
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = updateVprMstr(createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        m = deleteVprMstr(createRecord());
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
     }
      
    public String[] getRecord(String[] key) {
        x = getVprMstr(key);
        return x.m();
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVendPriceBrowseUtil(luinput.getText(),0, "vpr_vend");
        } else {
         luModel = DTData.getVendPriceBrowseUtil(luinput.getText(),0, "vpr_item");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString(), target.getValueAt(row,3).toString(), target.getValueAt(row,4).toString(), "LIST"});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblvend", this.getClass().getSimpleName()), 
                getClassLabelTag("lblitem", this.getClass().getSimpleName())); 
        
        
    }

    public vpr_mstr createRecord() { 
                     
        vpr_mstr x = new vpr_mstr(null, 
        tbkey.getText(),
        dditem.getSelectedItem().toString(),
        "LIST",
        dditem.getSelectedItem().toString(),        
        dduom.getSelectedItem().toString(),
        ddcurr.getSelectedItem().toString(),
        tbprice.getText()
        );
      
        return x;
    }
    
    public String[] updateForm() {
        tbkey.setText(x.vpr_vend());
        dditem.setSelectedItem(x.vpr_item());
        dduom.setSelectedItem(x.vpr_uom());
        ddcurr.setSelectedItem(x.vpr_curr());
        tbprice.setText(x.vpr_price());
        setPriceList(x.vpr_vend());
        setAction(x.m());
        return x.m();  
    }
    
    
    // custom funcs
      
    public void setData() {
        if (dditem.getItemCount() > 0 && ! tbkey.getText().isEmpty() && dduom.getItemCount() > 0 && ddcurr.getItemCount() > 0) {
        String[] x = invData.getItemPriceFromVend(tbkey.getText(), dditem.getSelectedItem().toString(), 
                dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
         lbitem.setText(x[0]);
        if (x[1].isBlank() || x[1].equals("0")) {
            tbprice.setText("0");
            btadd.setEnabled(true);
            btupdate.setEnabled(false);
            btdelete.setEnabled(false);
            tbprice.setBackground(Color.YELLOW);
        } else {
            tbprice.setText(currformatDouble(bsParseDouble(x[1]))); 
            btadd.setEnabled(false);
            btupdate.setEnabled(true);
            btdelete.setEnabled(true); 
            tbprice.setBackground(Color.GREEN);
        }
        }
    }
    
    public void setPriceList(String x) {
        pricelistmodel.removeAllElements();
        String pricecode = "";
        ArrayList<vpr_mstr> vprlist = getVprPriceLists(tbkey.getText());
        for (vpr_mstr vpr : vprlist) {
        pricelistmodel.addElement(vpr.vpr_item() + ":" + vpr.vpr_uom() + ":" + vpr.vpr_curr());
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
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pricelist = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        dditem = new javax.swing.JComboBox<>();
        dduom = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        lbitem = new javax.swing.JLabel();
        lbvend = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        btlookup = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor Price Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel5.setText("Price");
        jLabel5.setName("lblprice"); // NOI18N

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel3.setText("Vend / GroupCode");
        jLabel3.setName("lblvend"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel2.setText("Item");
        jLabel2.setName("lblitem"); // NOI18N

        pricelist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pricelistMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(pricelist);

        jLabel4.setText("Applied");
        jLabel4.setName("lblapplied"); // NOI18N

        dditem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dditemItemStateChanged(evt);
            }
        });

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel1.setText("uom");
        jLabel1.setName("lbluom"); // NOI18N

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel6.setText("Currency");
        jLabel6.setName("lblcurrency"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dditem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclear)
                .addGap(235, 235, 235))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete))
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void pricelistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistMouseClicked
        if (! pricelist.isSelectionEmpty()) {
          String[] str  = pricelist.getSelectedValue().toString().split(":", -1);  //item, uom, curr
          vpr_mstr vpr = getVprMstr(new String[]{tbkey.getText(),
                    str[0], str[1], str[2], "LIST"});
                
               
                isLoad = true;
                dduom.setSelectedItem(vpr.vpr_uom());
                ddcurr.setSelectedItem(vpr.vpr_curr());
                dditem.setSelectedItem(vpr.vpr_item());
                tbprice.setText(vpr.vpr_price());
               
                btadd.setEnabled(false);
                btupdate.setEnabled(true);
                btdelete.setEnabled(true);
                tbprice.setBackground(Color.GREEN);
                isLoad = false;
        }
    }//GEN-LAST:event_pricelistMouseClicked

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText(), dditem.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString()});  
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText(), dditem.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString()}); 
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText(), dditem.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString()});
    }//GEN-LAST:event_btaddActionPerformed

    private void dditemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dditemItemStateChanged
       setData();
    }//GEN-LAST:event_dditemItemStateChanged

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        setData();
    }//GEN-LAST:event_dduomActionPerformed

    private void ddcurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcurrActionPerformed
         setData();
    }//GEN-LAST:event_ddcurrActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
            String x = BlueSeerUtils.bsformat("", tbprice.getText(), "4");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
        if (bsParseDouble(tbprice.getText()) == 0) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
       executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbitem;
    private javax.swing.JLabel lbvend;
    private javax.swing.JList pricelist;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbprice;
    // End of variables declaration//GEN-END:variables
}
