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

package com.blueseer.ctr;

import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import bsmf.MainFrame; 
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.ctr.cusData.addOrUpdateCprMstr;
import com.blueseer.ctr.cusData.cm_mstr;
import com.blueseer.ctr.cusData.cpr_mstr;
import static com.blueseer.ctr.cusData.deleteCprMstr;
import static com.blueseer.ctr.cusData.getCprDiscLists;
import static com.blueseer.ctr.cusData.getCprMstr;
import static com.blueseer.ctr.cusData.getCprPriceLists;
import static com.blueseer.ctr.cusData.getCustMstr;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
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

/**
 *
 * @author vaughnte
 */
public class CustPriceMaint extends javax.swing.JPanel {

    boolean isLoad = false;
    boolean canUpdate = false;
    boolean isAutoPost = false;
    ArrayList<String[]> initDataSets = null;
    String defaultSite = "";
    String defaultCurrency = "";
    String defaultCC = "";
      
    cm_mstr cm = null;
    public static cpr_mstr cprx = null;
    
    DefaultListModel disclistmodel = new DefaultListModel();
    DefaultListModel pricelistmodel = new DefaultListModel();
    
    
    /**
     * Creates new form CustXrefMaintPanel
     */
    public CustPriceMaint() {
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
                case "add":
                    message = addUpdateRecord(key);
                    break;
                case "update":
                    message = addUpdateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break; 
                    
                default:
                    message = new String[]{"1", "unknown action: " + this.type};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
               if (this.type.equals("get")) {
                updateForm();
                // tbkey.requestFocus();
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
        dcexpire.setDate(null);
         dcexpiredisc.setDate(null);
         lbldisccode.setVisible(false);
         lbldisccode.setForeground(Color.red);
         disclist.setModel(disclistmodel);
         lblpricecode.setVisible(false);
         lblpricecode.setForeground(Color.red);
         pricelist.setModel(pricelistmodel);
         tbqty.setEnabled(false);
         ddtype.setEnabled(true);
         ddtype.setSelectedIndex(0);
        lbcust.setText("");
        lbitem.setText("");
        lblpricecode.setText("");
        tbdisc.setText("");
        tbdisckey.setText("");
        tbqty.setText("");
        disclistmodel.removeAllElements();
        pricelistmodel.removeAllElements();;
        
         btUpdate.setEnabled(false);
         btDelete.setEnabled(false);
         btAdd.setEnabled(false);
         
         
         
         tbprice.setText("");
         tbprice.setBackground(Color.WHITE); 
        
        ddcustcode.removeAllItems();
        ddcustcode_disc.removeAllItems();
        dduom.removeAllItems();
        ddcurr.removeAllItems();
        dditem.removeAllItems();
        
        if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "currencies,items,customers,pricegroups,uoms");
        }
        
        ddcurr.removeAllItems();
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("customers")) {
              ddcustcode.addItem(s[1]); 
              ddcustcode_disc.addItem(s[1]); 
            }
            if (s[0].equals("pricegroups")) {
              ddcustcode.addItem(s[1]); 
              ddcustcode_disc.addItem(s[1]); 
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
        
        ddcustcode.insertItemAt("", 0);
        ddcustcode.setSelectedIndex(0);
        
        ddcustcode_disc.insertItemAt("", 0);
        ddcustcode_disc.setSelectedIndex(0);
        
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
       
        
        
       isLoad = false;
    }
    
    public void initvars(String[] arg) {
       // setPanelComponentState(this, false); 
        setComponentDefaultValues(initDataSets == null);
        
        if (arg != null && arg.length > 0) {
            executeTask(BlueSeerUtils.dbaction.get,arg);
        } 
        
       // if (arg != null && arg.length > 5) {
      //     getCustPriceRecord(arg[0], arg[1], arg[2], arg[3], arg[4], arg[5]);
      //  }
         
    }
    
    public cpr_mstr createRecord() { 
        String expiredate = null;
          if (dcexpire.getDate() != null) {
              expiredate = BlueSeerUtils.setDateFormatNull(dcexpire.getDate());
          }
              
        cpr_mstr x = new cpr_mstr(null, 
        ddcustcode.getSelectedItem().toString(),
        dditem.getSelectedItem().toString(),
        ddtype.getSelectedItem().toString(),
        dditem.getSelectedItem().toString(),        
        dduom.getSelectedItem().toString(),
        ddcurr.getSelectedItem().toString(),
        bsParseDouble(tbprice.getText()),        
        bsParseDouble(tbqty.getText()), 
        expiredate
                );
      
        return x;
    }
    
    public cpr_mstr createDiscountRecord() { 
        String expiredate = null;
          if (dcexpiredisc.getDate() != null) {
              expiredate = BlueSeerUtils.setDateFormatNull(dcexpiredisc.getDate());
          }
              
        cpr_mstr x = new cpr_mstr(null, 
        ddcustcode_disc.getSelectedItem().toString(),
        tbdisckey.getText(),
        "DISCOUNT",
        tbdisckey.getText(),        
        "",
        "",
        bsParseDouble(tbdisc.getText()),        
        0, 
        expiredate
                );
      
        return x;
    }
    
    public cpr_mstr createDeleteRecord() { 
        String[] z = pricelist.getSelectedValue().toString().split(":",-1); //item, uom, curr, volqty
        String expiredate = null;
          if (dcexpire.getDate() != null) {
              expiredate = "'" + BlueSeerUtils.setDateFormatNull(dcexpire.getDate()) + "'";
          }
              
        cpr_mstr x = new cpr_mstr(null, 
        ddcustcode.getSelectedItem().toString(),
        z[0],
        ddtype.getSelectedItem().toString(),
        dditem.getSelectedItem().toString(),        
        z[1],
        z[2],
        bsParseDouble(tbprice.getText()),        
        bsParseDouble((z.length == 4) ? z[3] : "0"), 
        expiredate
                );
      
        return x;
    }
    
    
    public String[] addUpdateRecord(String[] key) {
        String[] m; 
        if (key[0].equals("discount")) {
             m = addOrUpdateCprMstr(createDiscountRecord());
         } else {
             m = addOrUpdateCprMstr(createRecord());
         }
         
         return m;
    }
    
    public String[] deleteRecord(String[] key) {
        String[] m = new String[2];
         m = deleteCprMstr(createDeleteRecord()); 
         initvars(null);
         return m;
    }
    
    public String[] getRecord(String[] x) {
        cprx = getCprMstr(new String[]{x[0],
                    x[1], x[2], x[3], x[4], x[5]}); // cust, item, uom, curr, type, volqty
        //System.out.println(cprx.cpr_cust() + "/" + cprx.cpr_item() + "/" + cprx.cpr_type());
        return cprx.m();
    }
    
    public void updateForm() {
        if (cprx != null) {
            if (cprx.cpr_type().equals("DISCOUNT")) {
            disclistmodel.addElement(cprx.cpr_item());
            ddcustcode_disc.setSelectedItem(cprx.cpr_cust());
            tbdisckey.setText(cprx.cpr_item());
            tbdisc.setText(bsNumber(cprx.cpr_price()));
            dcexpiredisc.setDate(BlueSeerUtils.parseDate(cprx.cpr_expire())); 
            } else {            
            ddcustcode.setSelectedItem(cprx.cpr_cust());
            dduom.setSelectedItem(cprx.cpr_uom());
            ddcurr.setSelectedItem(cprx.cpr_curr());
            dditem.setSelectedItem(cprx.cpr_item());
            ddtype.setSelectedItem(cprx.cpr_type());
            dcexpire.setDate(BlueSeerUtils.parseDate(cprx.cpr_expire())); 
            tbprice.setText(bsNumber(cprx.cpr_price())); 
            tbqty.setText(bsNumber(cprx.cpr_volqty()));
            }
            
        }
    }
    public void setDiscList() {
   
        if (! isLoad) {
        disclistmodel.removeAllElements();
        ArrayList<cpr_mstr> cprlist = getCprDiscLists(ddcustcode_disc.getSelectedItem().toString());
            for (cpr_mstr cpr : cprlist) {
                  disclistmodel.addElement(cpr.cpr_item());
                if (! cm.cm_price_code().isBlank()) {
                    lbldisccode.setText("Belongs to Group " + cm.cm_disc_code());
                    lbldisccode.setVisible(true); 
                   } else {
                    lbldisccode.setVisible(false);  
                   }
            }
        }
    }
    
    public void setData() {
        if (! isLoad) { 
            if (dditem.getItemCount() > 0 && ddcustcode.getItemCount() > 0 && dduom.getItemCount() > 0 && ddcurr.getItemCount() > 0) {
            String[] d = invData.getItemPriceFromCust(ddcustcode.getSelectedItem().toString(), dditem.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString(), ddtype.getSelectedItem().toString(), tbqty.getText());
            double myprice = bsParseDouble(d[0]);
            lbitem.setText(d[1]);
                if (myprice == 0) {
                    tbprice.setText("0");
                    btAdd.setEnabled(true);
                    btUpdate.setEnabled(false);
                    btDelete.setEnabled(false);
                    tbprice.setBackground(Color.YELLOW);
                } else {
                    tbprice.setText(bsFormatDouble(myprice,"4"));
                    btAdd.setEnabled(false);
                    btUpdate.setEnabled(false);
                    btDelete.setEnabled(false); 
                    tbprice.setBackground(Color.GREEN);
                }
            }
        }
    }
    
     public void setPriceList() {
        if (! isLoad) {
        pricelistmodel.removeAllElements();
        ArrayList<cpr_mstr> cprlist = getCprPriceLists(ddcustcode.getSelectedItem().toString());
            for (cpr_mstr cpr : cprlist) {
                if (cpr.cpr_type().equals("LIST")) {
                  pricelistmodel.addElement(cpr.cpr_item() + ":" + cpr.cpr_uom() + ":" + cpr.cpr_curr());
                } else {
                  pricelistmodel.addElement(cpr.cpr_item() + ":" + cpr.cpr_uom() + ":" + cpr.cpr_curr() + ":" + cpr.cpr_volqty());    
                }

                if (! cm.cm_price_code().isBlank()) {
                    lblpricecode.setText("Belongs to Group " + cm.cm_price_code());
                    lblpricecode.setVisible(true); 
                   } else {
                    lblpricecode.setVisible(false);  
                   }
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

        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        ddcustcode = new javax.swing.JComboBox();
        btDelete = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();
        lblpricecode = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pricelist = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        dditem = new javax.swing.JComboBox<>();
        dduom = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        lbitem = new javax.swing.JLabel();
        lbcust = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        lblqty = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox<>();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbdisckey = new javax.swing.JTextField();
        ddcustcode_disc = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        lbltype = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbdisc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        disclist = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        btadddisc = new javax.swing.JButton();
        btupdatedisc = new javax.swing.JButton();
        btdeletedisc = new javax.swing.JButton();
        lbldisccode = new javax.swing.JLabel();
        dcexpiredisc = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setBackground(new java.awt.Color(0, 102, 204));
        setPreferredSize(new java.awt.Dimension(980, 424));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Pricing Maintenance"));
        jPanel3.setName("panelmain"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(950, 500));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("List Price Maintenance"));
        jPanel1.setName("panellist"); // NOI18N

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

        jLabel3.setText("Cust / GroupCode");
        jLabel3.setName("lblcust"); // NOI18N

        btAdd.setText("Add");
        btAdd.setName("btadd"); // NOI18N
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        ddcustcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcustcodeItemStateChanged(evt);
            }
        });

        btDelete.setText("Delete");
        btDelete.setName("btdelete"); // NOI18N
        btDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeleteActionPerformed(evt);
            }
        });

        btUpdate.setText("Update");
        btUpdate.setName("btupdate"); // NOI18N
        btUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUpdateActionPerformed(evt);
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

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel6.setText("UOM");
        jLabel6.setName("lbluom"); // NOI18N

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel11.setText("Currency");
        jLabel11.setName("lblcurrency"); // NOI18N

        lblqty.setText("Quantity");
        lblqty.setName("lblqty"); // NOI18N

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LIST", "VOLUME" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel12.setText("Expire");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5)
                    .addComponent(lblqty)
                    .addComponent(jLabel12)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbprice, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(tbqty))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAdd)
                            .addComponent(btUpdate)
                            .addComponent(btDelete)
                            .addComponent(btclear)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblqty)))
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear))
                    .addComponent(jLabel4))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Discount Maintenance"));
        jPanel2.setName("paneldiscount"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(414, 350));

        ddcustcode_disc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcustcode_discItemStateChanged(evt);
            }
        });

        jLabel7.setText("Cust / GroupCode");

        jLabel8.setText("Key Desc (unique)");
        jLabel8.setName("lblkeydesc"); // NOI18N

        jLabel9.setText("Disc Percent");
        jLabel9.setName("lblpercent"); // NOI18N

        disclist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disclistMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(disclist);

        jLabel10.setText("Applied");
        jLabel10.setName("lblapplied"); // NOI18N

        btadddisc.setText("Add");
        btadddisc.setName("btadd"); // NOI18N
        btadddisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadddiscActionPerformed(evt);
            }
        });

        btupdatedisc.setText("Update");
        btupdatedisc.setName("btupdate"); // NOI18N
        btupdatedisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatediscActionPerformed(evt);
            }
        });

        btdeletedisc.setText("Delete");
        btdeletedisc.setName("btdelete"); // NOI18N
        btdeletedisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletediscActionPerformed(evt);
            }
        });

        dcexpiredisc.setDateFormatString("yyyy-MM-dd");

        jLabel13.setText("Expire");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcexpiredisc, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbldisccode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(tbdisckey, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcustcode_disc, javax.swing.GroupLayout.Alignment.LEADING, 0, 181, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbltype, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btadddisc)
                            .addComponent(btupdatedisc)
                            .addComponent(btdeletedisc)))
                    .addComponent(tbdisc, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcustcode_disc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(lbltype, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbldisccode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisckey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcexpiredisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(btadddisc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdatedisc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletedisc))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addGap(39, 39, 39))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel3);
    }// </editor-fold>//GEN-END:initComponents

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        executeTask(BlueSeerUtils.dbaction.add, new String[]{""});
    }//GEN-LAST:event_btAddActionPerformed

    private void ddcustcodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcustcodeItemStateChanged
    
      if (! isLoad && ddcustcode.getSelectedItem() != null && ! ddcustcode.getSelectedItem().toString().isBlank()) {
        btAdd.setEnabled(true);
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);
        cm = getCustMstr(new String[]{ddcustcode.getSelectedItem().toString()});
        setPriceList();
        setData();
        lbcust.setText(cm.cm_name());
      }  
    }//GEN-LAST:event_ddcustcodeItemStateChanged

    private void btDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeleteActionPerformed
        boolean proceed; 
        if (pricelist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1081));
        } else {
           proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{""});
        } 
    }//GEN-LAST:event_btDeleteActionPerformed

    private void ddcustcode_discItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcustcode_discItemStateChanged
      if (! isLoad && ddcustcode_disc.getSelectedItem() != null && ! ddcustcode_disc.getSelectedItem().toString().isBlank()) {
        cm = getCustMstr(new String[]{ddcustcode_disc.getSelectedItem().toString()});
        setDiscList();
        tbdisckey.setText("");
        tbdisc.setText("");
      }
    }//GEN-LAST:event_ddcustcode_discItemStateChanged

    private void btadddiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadddiscActionPerformed
        
        executeTask(BlueSeerUtils.dbaction.add, new String[]{"discount"});
    }//GEN-LAST:event_btadddiscActionPerformed

    private void btdeletediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletediscActionPerformed
        boolean proceed; 
        if (disclist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1081));
        } else {
           proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
        executeTask(BlueSeerUtils.dbaction.delete, new String[]{"discount"});
        }
    }//GEN-LAST:event_btdeletediscActionPerformed

    private void btupdatediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatediscActionPerformed
        executeTask(BlueSeerUtils.dbaction.add, new String[]{"discount"});
        
    }//GEN-LAST:event_btupdatediscActionPerformed

    private void disclistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disclistMouseClicked
        if (! disclist.isSelectionEmpty()) {
            cpr_mstr cpr = getCprMstr(new String[]{ddcustcode_disc.getSelectedItem().toString(),
            disclist.getSelectedValue().toString(), "", "", "DISCOUNT", "0"}); 
            tbdisc.setText(bsNumber(cpr.cpr_price()));
            tbdisckey.setText(cpr.cpr_item());
            dcexpiredisc.setDate(BlueSeerUtils.parseDate(cpr.cpr_expire())); 
        }
        
    }//GEN-LAST:event_disclistMouseClicked

    private void btUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUpdateActionPerformed
        executeTask(BlueSeerUtils.dbaction.add, new String[]{""}); // update uses 'add' and calls addUpdate sql
    }//GEN-LAST:event_btUpdateActionPerformed

    private void pricelistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistMouseClicked
        if (! pricelist.isSelectionEmpty()) {
          String[] str  = pricelist.getSelectedValue().toString().split(":", -1);  //item, uom, curr, volqty
          cpr_mstr cpr = null;
                if (str.length > 3) { 
                    cpr = getCprMstr(new String[]{ddcustcode.getSelectedItem().toString(),
                    str[0], str[1], str[2], "VOLUME", str[3]});
                } else {
                    cpr = getCprMstr(new String[]{ddcustcode.getSelectedItem().toString(),
                    str[0], str[1], str[2], "LIST", "0"});
                }
               
                isLoad = true;
                dduom.setSelectedItem(cpr.cpr_uom());
                ddcurr.setSelectedItem(cpr.cpr_curr());
                dditem.setSelectedItem(cpr.cpr_item());
                ddtype.setSelectedItem(cpr.cpr_type());
                dcexpire.setDate(BlueSeerUtils.parseDate(cpr.cpr_expire())); 
                tbprice.setText(bsNumber(cpr.cpr_price())); 
                tbqty.setText(bsNumber(cpr.cpr_volqty()));
               
                btAdd.setEnabled(false);
                btUpdate.setEnabled(true);
                btDelete.setEnabled(true);
                tbprice.setBackground(Color.GREEN);
                isLoad = false;
        }
         
    }//GEN-LAST:event_pricelistMouseClicked

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
        if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        setData();
       
    }//GEN-LAST:event_dditemActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
        if (ddtype.getSelectedItem().toString().equals("VOLUME")) {
            tbqty.setEnabled(true);
           // btAdd.setEnabled(true);
        } else {
            tbqty.setEnabled(false);
           // btAdd.setEnabled(false);
        }
        setData();
    }//GEN-LAST:event_ddtypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btUpdate;
    private javax.swing.JButton btadddisc;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeletedisc;
    private javax.swing.JButton btupdatedisc;
    private com.toedter.calendar.JDateChooser dcexpire;
    private com.toedter.calendar.JDateChooser dcexpiredisc;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcustcode;
    private javax.swing.JComboBox ddcustcode_disc;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JList disclist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbitem;
    private javax.swing.JLabel lbldisccode;
    private javax.swing.JLabel lblpricecode;
    private javax.swing.JLabel lblqty;
    private javax.swing.JLabel lbltype;
    private javax.swing.JList pricelist;
    private javax.swing.JTextField tbdisc;
    private javax.swing.JTextField tbdisckey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    // End of variables declaration//GEN-END:variables
}
