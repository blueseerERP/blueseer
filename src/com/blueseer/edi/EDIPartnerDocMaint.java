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

package com.blueseer.edi;

import com.blueseer.ctr.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.edi.ediData.getMapMstr;
import static com.blueseer.utl.BlueSeerUtils.asciivalues;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.getEDIPartnerDesc;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

/**
 *
 * @author vaughnte
 */
public class EDIPartnerDocMaint extends javax.swing.JPanel {

    DefaultListModel listmodel = new DefaultListModel();
    boolean isLoad = false;
    /**
     * Creates new form CarrierMaintPanel
     */
    public EDIPartnerDocMaint() {
        initComponents();
        setLanguageTags(this);
    }
    
    public void getCustEDI(String code, String doctype, String sndid, String rcvid) {
        
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
                int i = 0;
                res = st.executeQuery("select * from edi_mstr where edi_id = " + "'" + code + "'" +
                                      " AND edi_doc = " + "'" + doctype + "'" +
                                      " AND edi_sndgs = " + "'" + sndid + "'" + 
                                      " AND edi_rcvgs = " + "'" + rcvid + "'" +         
                                              ";");
                while (res.next()) {
                    i++;
                    ddkey.setSelectedItem(code);
                    dddoc.setSelectedItem(doctype);
                    ddoutdoctype.setSelectedItem(res.getString("edi_doctypeout"));
                    ddoutfiletype.setSelectedItem(res.getString("edi_filetypeout"));
                    tbIFS.setText(res.getString("edi_ifs"));
                    tbOFS.setText(res.getString("edi_ofs"));
                    tbrcvisa.setText(res.getString("edi_rcvisa"));
                    tbrcvq.setText(res.getString("edi_rcvq"));
                    tbrcvgs.setText(res.getString("edi_rcvgs"));
                    ddmap.setSelectedItem(res.getString("edi_map"));
                    tbelement.setText(res.getString("edi_eledelim"));
                    tbsegment.setText(res.getString("edi_segdelim"));
                    tbsub.setText(res.getString("edi_subdelim"));
                    tbfileprefix.setText(res.getString("edi_fileprefix"));
                    tbfilesuffix.setText(res.getString("edi_filesuffix"));
                    tbfilepath.setText(res.getString("edi_filepath"));
                    tbversion.setText(res.getString("edi_version"));
                    tbsndisa.setText(res.getString("edi_sndisa"));
                    tbsndgs.setText(res.getString("edi_sndgs"));
                    tbsndq.setText(res.getString("edi_sndq"));
                    tbsupplier.setText(res.getString("edi_supcode"));
                    cbfa.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_fa_required")));
                    cbenvelopeall.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_envelopeall")));
                    cbuna.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_una")));
                    cbung.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_ung")));
                    
                }
               
                
                if (i > 0) {
                   enableAll();
                   getAttributes(tbsndgs.getText(), tbrcvgs.getText(), doctype);
                   btadd.setEnabled(false);
                   ddkey.setEnabled(false);
                   dddoc.setEnabled(false);
                   tbrcvgs.setEnabled(false);
                   tbsndgs.setEnabled(false);
                }
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
    
    public void getAttributes(String sndid, String rcvid, String doctype) {
        listmodel.removeAllElements();
        ArrayList<String> list = EDData.getEDIAttributesList(doctype, sndid, rcvid);
        for (String x : list) {
            listmodel.addElement(x);
        }
    }
    
    public void clearAll() {
        isLoad = true;
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", panelMain);
        jTabbedPane1.add("Outbound", panelOutbound);
        
        lblmessg.setText("");
        lblmessg.setForeground(Color.black);
        lblpartner.setText("");
        
        lblelem.setText("");
        lblseg.setText("");
        lblsub.setText("");
        
       // jTabbedPane1.setEnabledAt(1, false);
        
        listAttributes.setModel(listmodel);
        
        dddoc.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
        }
        ddoutdoctype.removeAllItems();
        for (int i = 0; i < mylist.size(); i++) {
            ddoutdoctype.addItem(mylist.get(i));
        }
        
        ddmap.removeAllItems();
        ArrayList<String> maps = ediData.getMapMstrList();
        for (int i = 0; i < maps.size(); i++) {
            ddmap.addItem(maps.get(i));
        }
        ddmap.insertItemAt("", 0);
        
        
        ddkey.removeAllItems();
        ArrayList<String> keys = EDData.getEDIPartners();
        for (int i = 0; i < keys.size(); i++) {
            ddkey.addItem(keys.get(i));
        }
        ddkey.insertItemAt("", 0);
        ddkey.setSelectedIndex(0);
        
        tbrcvisa.setText("");
        tbrcvq.setText("");
        tbrcvgs.setText("");
        tbsndisa.setText("");
        tbsndgs.setText("");
        tbsndq.setText("");
        tbversion.setText("");
        tbsupplier.setText("");
        tbelement.setText("");
        tbsegment.setText("");
        tbsub.setText("");
        tbfilepath.setText("");
        tbIFS.setText("");
        tbOFS.setText("");
        tbfileprefix.setText("");
        tbfilesuffix.setText("");
        ddattributekey.setSelectedIndex(0);
        tbattributevalue.setText("");
        cbenvelopeall.setSelected(false);
        cbuna.setSelected(false);
        cbung.setSelected(false);
        isLoad = false;
    }
    
    public void enableAll() {
        btadd.setEnabled(true);
        btnew.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btlookup.setEnabled(true);
        btdeleteattribute.setEnabled(true);
        btaddattribute.setEnabled(true);
        tbrcvisa.setEnabled(true);
        tbrcvq.setEnabled(true);
        tbrcvgs.setEnabled(true);
        tbsndisa.setEnabled(true);
        tbsndgs.setEnabled(true);
        tbsndq.setEnabled(true);
        ddmap.setEnabled(true);
        dddoc.setEnabled(true);
     //   ddoutfiletype.setEnabled(true);
     //   ddoutdoctype.setEnabled(true);
        tbversion.setEnabled(true);
        tbsupplier.setEnabled(true);
        tbelement.setEnabled(true);
        tbsegment.setEnabled(true);
        tbsub.setEnabled(true);
        tbfilepath.setEnabled(true);
      //  tbIFS.setEnabled(true);
      //  tbOFS.setEnabled(true);
        tbfileprefix.setEnabled(true);
        tbfilesuffix.setEnabled(true);
        ddkey.setEnabled(true);
        cbenvelopeall.setEnabled(true);
        cbuna.setEnabled(true);
        cbung.setEnabled(true);
        cbfa.setEnabled(true);
     
        ddattributekey.setEnabled(true);
        tbattributevalue.setEnabled(true);
    }
    
    public void disableAll() {
         btadd.setEnabled(false);
          btnew.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btlookup.setEnabled(false);
        btdeleteattribute.setEnabled(false);
        btaddattribute.setEnabled(false);
        tbrcvisa.setEnabled(false);
        tbrcvq.setEnabled(false);
        tbrcvgs.setEnabled(false);
        tbsndisa.setEnabled(false);
        tbsndgs.setEnabled(false);
        tbsndq.setEnabled(false);
        ddmap.setEnabled(false);
        dddoc.setEnabled(false);
        ddoutfiletype.setEnabled(false);
        ddoutdoctype.setEnabled(false);
        tbversion.setEnabled(false);
        tbsupplier.setEnabled(false);
        tbelement.setEnabled(false);
        tbsegment.setEnabled(false);
        tbsub.setEnabled(false);
        tbfilepath.setEnabled(false);
        tbIFS.setEnabled(false);
        tbOFS.setEnabled(false);
        tbfileprefix.setEnabled(false);
        tbfilesuffix.setEnabled(false);
        ddkey.setEnabled(false);
        cbenvelopeall.setEnabled(false);
        cbuna.setEnabled(false);
        cbung.setEnabled(false);
        cbfa.setEnabled(false);
        ddattributekey.setEnabled(false);
        tbattributevalue.setEnabled(false);
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
       clearAll();
       disableAll();
       btnew.setEnabled(true);
       btlookup.setEnabled(true);
       isLoad = false; 
       
      // ddmap.setSelectedIndex(0);
       
        if (arg != null && arg.length > 0) {
            getCustEDI(arg[0], arg[1], arg[2], arg[3]);
        }
       
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getEDICustBrowseUtil(luinput.getText(),0, "edi_id");
        } else {
         luModel = DTData.getEDICustBrowseUtil(luinput.getText(),0, "edi_doc");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString(), target.getValueAt(row,4).toString(), target.getValueAt(row,6).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
       
        callDialog(getClassLabelTag("lblcode", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldoctype", this.getClass().getSimpleName()),
                getClassLabelTag("lblsndisa", this.getClass().getSimpleName()));
        
    }

    public void lookUpFrameASCII() {
        luTable.removeMouseListener(luml);
        luModel = DTData.getASCIIChartDT(0, 128);
        luTable.setModel(luModel);
        callDialog();
        
        
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
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbsndgs = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbrcvgs = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbrcvq = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tbsndisa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tbsndq = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        dddoc = new javax.swing.JComboBox<>();
        tbrcvisa = new javax.swing.JTextField();
        btlookup = new javax.swing.JButton();
        ddmap = new javax.swing.JComboBox<>();
        ddkey = new javax.swing.JComboBox<>();
        btclear = new javax.swing.JButton();
        lblpartner = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ddoutdoctype = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        ddoutfiletype = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        tbIFS = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tbOFS = new javax.swing.JTextField();
        lblmessg = new javax.swing.JLabel();
        panelOutbound = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tbfilepath = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tbsupplier = new javax.swing.JTextField();
        tbelement = new javax.swing.JTextField();
        tbfilesuffix = new javax.swing.JTextField();
        tbsegment = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbversion = new javax.swing.JTextField();
        tbfileprefix = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbsub = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblsuffix = new javax.swing.JLabel();
        cbenvelopeall = new javax.swing.JCheckBox();
        cbuna = new javax.swing.JCheckBox();
        cbfa = new javax.swing.JCheckBox();
        cbung = new javax.swing.JCheckBox();
        lblelem = new javax.swing.JLabel();
        lblseg = new javax.swing.JLabel();
        lblsub = new javax.swing.JLabel();
        btlookupOrderLine = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btdeleteattribute = new javax.swing.JButton();
        btaddattribute = new javax.swing.JButton();
        tbattributevalue = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        listAttributes = new javax.swing.JList<>();
        ddattributekey = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Partner Transaction Maintenance"));
        panelMain.setName("panelmain"); // NOI18N
        panelMain.setPreferredSize(new java.awt.Dimension(752, 543));

        btdelete.setText("delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel3.setText("DocType");
        jLabel3.setName("lbldoctype"); // NOI18N

        jLabel6.setText("Rcv Q");
        jLabel6.setName("lblrcvq"); // NOI18N

        jLabel5.setText("Code:");
        jLabel5.setName("lblcode"); // NOI18N

        jLabel15.setText("Snd GS/UNG");
        jLabel15.setName("lblsndgs"); // NOI18N

        jLabel7.setText("Rcv GS/UNG");
        jLabel7.setName("lblrcvgs"); // NOI18N

        jLabel1.setText("Rcv ISA/UNB");
        jLabel1.setName("lblrcvisa"); // NOI18N

        jLabel13.setText("Snd ISA/UNB");
        jLabel13.setName("lblsndisa"); // NOI18N

        jLabel9.setText("Map");
        jLabel9.setName("lblmap"); // NOI18N

        jLabel16.setText("Snd Q");
        jLabel16.setName("lblsndq"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        dddoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dddocActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddmap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddmapActionPerformed(evt);
            }
        });

        ddkey.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ddkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddkeyActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(3, 3, 3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btclear))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbrcvisa, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrcvgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrcvq, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsndisa, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsndgs, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsndq, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddkey, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblpartner, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lblpartner, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tbrcvisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrcvgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbrcvq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(tbsndisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsndgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsndq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(53, 53, 53))
        );

        jLabel2.setText("outDocType");
        jLabel2.setName("lbloutdoctype"); // NOI18N

        jLabel8.setText("outFileType");
        jLabel8.setName("lbloutfiletype"); // NOI18N

        ddoutfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FF", "X12", "DB" }));

        jLabel19.setText("ISF File");
        jLabel19.setName("lblifs"); // NOI18N

        jLabel20.setText("OSF File");
        jLabel20.setName("lblofs"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddoutdoctype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddoutfiletype, 0, 112, Short.MAX_VALUE)
                    .addComponent(tbIFS)
                    .addComponent(tbOFS))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ddoutdoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(ddoutfiletype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbIFS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbOFS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblmessg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelMainLayout.createSequentialGroup()
                        .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelMainLayout.createSequentialGroup()
                                .addComponent(btdelete)
                                .addGap(6, 6, 6)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblmessg, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btdelete)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btupdate)
                        .addComponent(btadd)))
                .addContainerGap())
        );

        add(panelMain);

        panelOutbound.setBorder(javax.swing.BorderFactory.createTitledBorder("Outbound Options"));
        panelOutbound.setPreferredSize(new java.awt.Dimension(752, 543));

        jLabel11.setText("Sub Sep");
        jLabel11.setName("lblsubsep"); // NOI18N

        jLabel17.setText("Version");
        jLabel17.setName("lblversion"); // NOI18N

        jLabel18.setText("SupplierCode");
        jLabel18.setName("lblsuppcode"); // NOI18N

        jLabel14.setText("FilePath");
        jLabel14.setName("lblfilepath"); // NOI18N

        tbelement.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbelementFocusLost(evt);
            }
        });

        tbsegment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsegmentFocusLost(evt);
            }
        });

        jLabel4.setText("Elem Sep");
        jLabel4.setName("lblelemsep"); // NOI18N

        jLabel10.setText("Seg Sep");
        jLabel10.setName("lblsegsep"); // NOI18N

        tbsub.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsubFocusLost(evt);
            }
        });

        jLabel12.setText("Prefix");
        jLabel12.setName("lblprefix"); // NOI18N

        lblsuffix.setText("Suffix");
        lblsuffix.setName("lblsuffix"); // NOI18N

        cbenvelopeall.setText("Multi-Envelope");

        cbuna.setText("UNA Segment");

        cbfa.setText("Acknowledgment");
        cbfa.setName("cbfunctional"); // NOI18N

        cbung.setText("UNG Segment");

        btlookupOrderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrderLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderLineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btlookupOrderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(lblsuffix, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbfilepath, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(tbfilesuffix, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(tbfileprefix)
                    .addComponent(tbsupplier, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(tbsub, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(lblsub, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(tbelement, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(lblelem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(lblseg, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 41, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbenvelopeall)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbung))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbfa)
                        .addGap(27, 27, 27)
                        .addComponent(cbuna)))
                .addGap(28, 28, 28))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblelem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(tbelement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel10))
                            .addComponent(lblseg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11))
                            .addComponent(lblsub, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbfileprefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel12)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbfilesuffix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblsuffix))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbfilepath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(btlookupOrderLine)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbuna)
                    .addComponent(cbfa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbung)
                    .addComponent(cbenvelopeall))
                .addContainerGap(230, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributes"));

        btdeleteattribute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteattribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattributeActionPerformed(evt);
            }
        });

        btaddattribute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddattribute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattributeActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(listAttributes);

        ddattributekey.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ISA01", "ISA02", "ISA03", "ISA04", "ISA05", "ISA06", "ISA07", "ISA08", "ISA11", "ISA12", "ISA14", "ISA15", "GS01", "GS02", "GS03", "GS07", "GS08", "UNB01", "UNB02", "UNB03", "UNH02", "envctrlnbr", "grpctrlnbr" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbattributevalue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btdeleteattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(ddattributekey, 0, 168, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddattributekey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbattributevalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btaddattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeleteattribute, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelOutboundLayout = new javax.swing.GroupLayout(panelOutbound);
        panelOutbound.setLayout(panelOutboundLayout);
        panelOutboundLayout.setHorizontalGroup(
            panelOutboundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOutboundLayout.createSequentialGroup()
                .addContainerGap(504, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(panelOutboundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelOutboundLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelOutboundLayout.setVerticalGroup(
            panelOutboundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOutboundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
            .addGroup(panelOutboundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelOutboundLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        add(panelOutbound);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
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
                boolean proceed = true;
                String fa = "";
                String envelopeall = "";
                String una = "";
                String ung = "";
                int i = 0;
                String infiletype = "";
                
                if (dddoc.getSelectedItem().toString().endsWith("db")) {
                    infiletype = "DB";
                }
                if (dddoc.getSelectedItem().toString().endsWith("x12")) {
                    infiletype = "X12";
                }
                if (dddoc.getSelectedItem().toString().endsWith("csv")) {
                    infiletype = "CSV";
                }
                if (dddoc.getSelectedItem().toString().endsWith("une")) {
                    infiletype = "UNE";
                }
                if (dddoc.getSelectedItem().toString().endsWith("xml")) {
                    infiletype = "XML";
                }
                if (dddoc.getSelectedItem().toString().endsWith("json")) {
                    infiletype = "JSON";
                }
                if (infiletype.isBlank()) {
                    infiletype = "FF";
                }
               
                if (ddkey.getSelectedItem() == null || ddkey.getSelectedItem().toString().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    ddkey.requestFocus();
                    return;
                }
              
                
                if (tbrcvgs.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbrcvgs.requestFocus();
                    return;
                }
               
                if (tbsndgs.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbsndgs.requestFocus();
                    return;
                }
                
                if ( cbfa.isSelected() ) {
                   fa = "1";    
                } else {
                    fa = "0";
                }
                
                envelopeall = (cbenvelopeall.isSelected()) ? "1" : "0";
                una = (cbuna.isSelected()) ? "1" : "0";
                ung = (cbung.isSelected()) ? "1" : "0";
                
                
                if (proceed) {

                    res = st.executeQuery("SELECT edi_id, edi_doc FROM  edi_mstr where edi_id = " + "'" + ddkey.getSelectedItem().toString() + "'" +
                                          " AND edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" + 
                                          " AND edi_rcvgs = " + "'" + tbrcvgs.getText() + "'" +
                                          " AND edi_sndgs = " + "'" + tbsndgs.getText() + "'" +        
                                          ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        
                        st.executeUpdate("insert into edi_mstr "
                            + "(edi_id, edi_doc, edi_sndisa, edi_sndq, " 
                            + "edi_sndgs, edi_map, edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, edi_filesuffix, edi_filepath, "
                            + "edi_version, edi_rcvisa, edi_rcvgs, edi_rcvq, edi_supcode, edi_doctypeout, edi_filetypeout, edi_ifs, edi_ofs, edi_filetype, " +
                                " edi_fa_required, edi_envelopeall, edi_una, edi_ung ) "
                            + " values ( " + "'" + ddkey.getSelectedItem().toString() + "'" + ","
                                + "'" + dddoc.getSelectedItem().toString() + "'" + ","
                                + "'" + tbsndisa.getText() + "'" + ","
                                + "'" + tbsndq.getText() + "'" + ","
                                + "'" + tbsndgs.getText() + "'" + ","
                                + "'" + ddmap.getSelectedItem().toString() + "'" + ","
                                + "'" + tbelement.getText() + "'" + ","
                                + "'" + tbsegment.getText() + "'" + ","
                                + "'" + tbsub.getText() + "'" + ","
                                + "'" + tbfileprefix.getText() + "'" + ","
                                + "'" + tbfilesuffix.getText() + "'" + ","
                                + "'" + tbfilepath.getText() + "'" + ","
                                + "'" + tbversion.getText() + "'" + ","
                                + "'" + tbrcvisa.getText() + "'" + ","
                                + "'" + tbrcvgs.getText() + "'" + ","
                                + "'" + tbrcvq.getText() + "'" + ","
                                + "'" + tbsupplier.getText() + "'"  + ","
                                + "'" + ddoutdoctype.getSelectedItem().toString() + "'" + ","
                                + "'" + ddoutfiletype.getSelectedItem().toString() + "'" + ","
                                + "'" + tbIFS.getText() + "'"  + ","
                                + "'" + tbOFS.getText() + "'"  + ","      
                                + "'" + infiletype + "'"  + ","        
                                + "'" + fa + "'" + ","
                                + "'" + envelopeall + "'" + ","
                                + "'" + una + "'" + ","
                                + "'" + ung + "'"        
                            + ")"
                            + ";");
                        bsmf.MainFrame.show(getMessageTag(1007));
                    } else {
                        bsmf.MainFrame.show(getMessageTag(1014));
                    }

                   initvars(null);
                   
                } // if proceed
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
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       try {
            boolean proceed = true;
           
            String fa = "";
            String envelopeall = "";
            String una = "";
            String ung = "";
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                   
               if (ddkey.getSelectedItem() == null || ddkey.getSelectedItem().toString().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    ddkey.requestFocus();
                    return;
                }
                /*
                if (ddmap.getSelectedItem() == null || ddmap.getSelectedItem().toString().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    ddmap.requestFocus();
                    return;
                }
                */
                
                if (tbrcvgs.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbrcvgs.requestFocus();
                    return;
                }
               
                if (tbsndgs.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbsndgs.requestFocus();
                    return;
                }
              
              
                
               
                if ( cbfa.isSelected() ) {
                   fa = "1";    
                } else {
                    fa = "0";
                }
                
                envelopeall = (cbenvelopeall.isSelected()) ? "1" : "0";
                una = (cbuna.isSelected()) ? "1" : "0";
                ung = (cbung.isSelected()) ? "1" : "0";
                
                if (proceed) {
                    st.executeUpdate("update edi_mstr set edi_sndisa = " + "'" + tbsndisa.getText() + "'" + ","
                            + "edi_sndq = " + "'" + tbsndq.getText() + "'" + ","
                            + "edi_sndgs = " + "'" + tbsndgs.getText() + "'" + ","
                            + "edi_map = " + "'" + ddmap.getSelectedItem().toString() + "'" + ","
                            + "edi_eledelim = " + "'" + tbelement.getText() + "'" + ","
                            + "edi_segdelim = " + "'" + tbsegment.getText() + "'" + ","
                            + "edi_subdelim = " + "'" + tbsub.getText() + "'" + ","
                            + "edi_fileprefix = " + "'" + tbfileprefix.getText() + "'" + ","
                            + "edi_filesuffix = " + "'" + tbfilesuffix.getText() + "'" + ","
                            + "edi_filepath = " + "'" + tbfilepath.getText() + "'" + ","
                            + "edi_version = " + "'" + tbversion.getText() + "'" + ","
                            + "edi_rcvisa = " + "'" + tbrcvisa.getText() + "'" + ","
                            + "edi_rcvgs = " + "'" + tbrcvgs.getText() + "'" + ","
                            + "edi_rcvq = " + "'" + tbrcvq.getText() + "'" + ","
                            + "edi_supcode = " + "'" + tbsupplier.getText() + "'"  + ","
                            + "edi_doctypeout = " + "'" + ddoutdoctype.getSelectedItem() + "'"  + ","
                            + "edi_filetypeout = " + "'" + ddoutfiletype.getSelectedItem() + "'"  + ","        
                            + "edi_ifs = " + "'" + tbIFS.getText() + "'" + ","
                            + "edi_ofs = " + "'" + tbOFS.getText() + "'" + ","        
                            + "edi_fa_required = " + "'" + fa + "'" + ","
                            + "edi_envelopeall = " + "'" + envelopeall + "'" + ","
                            + "edi_una = " + "'" + envelopeall + "'" + ","
                            + "edi_ung = " + "'" + envelopeall + "'"        
                            + " where edi_id = " + "'" + ddkey.getSelectedItem().toString() + "'"     
                            + " AND edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'"
                            + " AND edi_rcvgs = " + "'" + tbrcvgs.getText() + "'"
                            + " AND edi_sndgs = " + "'" + tbsndgs.getText() + "'"        
                            + ";");
                    bsmf.MainFrame.show(getMessageTag(1008));
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
       
        if (proceed) {
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
              
                 
                
                   int i = st.executeUpdate("delete from edi_mstr where edi_id = " + "'" + ddkey.getSelectedItem().toString() + "'" + 
                                            " and edi_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                                            " and edi_sndgs = " + "'" + tbsndgs.getText() + "'" +
                                            " and edi_rcvgs = " + "'" + tbrcvgs.getText() + "'" +        
                                            ";");
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1031, ddkey.getSelectedItem().toString() + "/" + dddoc.getSelectedItem().toString() + "/" + tbsndgs.getText() + "/" + tbrcvgs.getText()));
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        isLoad = true;
        clearAll();
        dddoc.setSelectedIndex(0);
        isLoad = false;
        
       // ddmap.setSelectedIndex(0);
        
        enableAll();
        
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btlookup.setEnabled(false);
        btnew.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed

    private void tbelementFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbelementFocusLost
            String x = BlueSeerUtils.bsformat("", tbelement.getText(), "0");
        if (x.equals("error")) {
            tbelement.setText("");
            tbelement.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbelement.requestFocus();
            lblelem.setText("");
        } else {
            tbelement.setText(x);
            tbelement.setBackground(Color.white);
            int t = Integer.valueOf(x);
            lblelem.setText(asciivalues(t));
        }
    }//GEN-LAST:event_tbelementFocusLost

    private void tbsegmentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsegmentFocusLost
           String x = BlueSeerUtils.bsformat("", tbsegment.getText(), "0");
        if (x.equals("error")) {
            tbsegment.setText("");
            tbsegment.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbsegment.requestFocus();
            lblseg.setText("");
        } else {
            tbsegment.setText(x);
            tbsegment.setBackground(Color.white);
            int t = Integer.valueOf(x);            
            lblseg.setText(asciivalues(t));
        }
    }//GEN-LAST:event_tbsegmentFocusLost

    private void tbsubFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsubFocusLost
         String x = BlueSeerUtils.bsformat("", tbsub.getText(), "0");
        if (x.equals("error")) {
            tbsub.setText("");
            tbsub.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbsub.requestFocus();
            lblsub.setText("");
        } else {
            tbsub.setText(x);
            tbsub.setBackground(Color.white);
            int t = Integer.valueOf(x);
            lblsub.setText(asciivalues(t));
        }
    }//GEN-LAST:event_tbsubFocusLost

    private void btaddattributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattributeActionPerformed
       
        EDData.addEDIAttributeRecord(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString(), ddattributekey.getSelectedItem().toString(), tbattributevalue.getText());
        getAttributes(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString());
        ddattributekey.setSelectedIndex(0);
        tbattributevalue.setText("");
    }//GEN-LAST:event_btaddattributeActionPerformed

    private void btdeleteattributeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattributeActionPerformed
        boolean proceed = true; 
        
        if (listAttributes.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1029));
        } else {
           proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
            String[] z = listAttributes.getSelectedValue().toString().split(":");
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
              
                   int i = st.executeUpdate("delete from edi_attr where exa_sndid = " + "'" + tbsndgs.getText() + "'" + 
                                            " and exa_rcvid = " + "'" + tbrcvgs.getText() + "'" +
                                            " and exa_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                                            " and exa_key = " + "'" + z[0].toString() + "'" +
                                             ";");
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1031,listAttributes.getSelectedValue().toString()));
                    getAttributes(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString());
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteattributeActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void ddmapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddmapActionPerformed
        if (! isLoad && ddmap.getSelectedItem() != null && ! ddmap.getSelectedItem().toString().isBlank()) {
            ediData.map_mstr x = getMapMstr(new String[]{ddmap.getSelectedItem().toString()});
            ddoutdoctype.setSelectedItem(x.map_outdoctype());
            ddoutfiletype.setSelectedItem(x.map_outfiletype());
            tbIFS.setText(x.map_ifs());
            tbOFS.setText(x.map_ofs());
        }
    }//GEN-LAST:event_ddmapActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void dddocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dddocActionPerformed
        if (! isLoad && dddoc.getSelectedItem() != null) {            
        isLoad = true;
        ddmap.removeAllItems();
        ArrayList<String> maps = ediData.getMapMstrList(dddoc.getSelectedItem().toString());
        for (int i = 0; i < maps.size(); i++) {
            ddmap.addItem(maps.get(i));
        }
        lblmessg.setText("Map Count for this type: " + ddmap.getItemCount());
        if (ddmap.getItemCount() <= 0) {
            lblmessg.setForeground(Color.red);
        } else {
            lblmessg.setForeground(Color.black);
        }
        
        ddmap.insertItemAt("", 0);
        ddmap.setSelectedIndex(0);
        isLoad = false;
        }
    }//GEN-LAST:event_dddocActionPerformed

    private void btlookupOrderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderLineActionPerformed
        lookUpFrameASCII();
    }//GEN-LAST:event_btlookupOrderLineActionPerformed

    private void ddkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddkeyActionPerformed
        
        if (! isLoad && ddkey.getSelectedItem() != null) {
        lblpartner.setText(getEDIPartnerDesc(ddkey.getSelectedItem().toString()));
        }
        
    }//GEN-LAST:event_ddkeyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattribute;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattribute;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupOrderLine;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbenvelopeall;
    private javax.swing.JCheckBox cbfa;
    private javax.swing.JCheckBox cbuna;
    private javax.swing.JCheckBox cbung;
    private javax.swing.JComboBox<String> ddattributekey;
    private javax.swing.JComboBox<String> dddoc;
    private javax.swing.JComboBox<String> ddkey;
    private javax.swing.JComboBox<String> ddmap;
    private javax.swing.JComboBox<String> ddoutdoctype;
    private javax.swing.JComboBox<String> ddoutfiletype;
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
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblelem;
    private javax.swing.JLabel lblmessg;
    private javax.swing.JLabel lblpartner;
    private javax.swing.JLabel lblseg;
    private javax.swing.JLabel lblsub;
    private javax.swing.JLabel lblsuffix;
    private javax.swing.JList<String> listAttributes;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelOutbound;
    private javax.swing.JTextField tbIFS;
    private javax.swing.JTextField tbOFS;
    private javax.swing.JTextField tbattributevalue;
    private javax.swing.JTextField tbelement;
    private javax.swing.JTextField tbfilepath;
    private javax.swing.JTextField tbfileprefix;
    private javax.swing.JTextField tbfilesuffix;
    private javax.swing.JTextField tbrcvgs;
    private javax.swing.JTextField tbrcvisa;
    private javax.swing.JTextField tbrcvq;
    private javax.swing.JTextField tbsegment;
    private javax.swing.JTextField tbsndgs;
    private javax.swing.JTextField tbsndisa;
    private javax.swing.JTextField tbsndq;
    private javax.swing.JTextField tbsub;
    private javax.swing.JTextField tbsupplier;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
