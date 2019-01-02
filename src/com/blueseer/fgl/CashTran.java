/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.fgl;

import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import static com.blueseer.utl.OVData.getDueDateFromTerms;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import org.apache.commons.io.FilenameUtils;


/**
 *
 * @author vaughnte
 */
public class CashTran extends javax.swing.JPanel {

                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                String curr = "";
                Double actamt = 0.00;
                Double actqty = 0.00;
               
                int voucherline = 0;
                boolean isLoad = false;
                String partnumber = "";
                String newFileName = "";
                
                javax.swing.table.DefaultTableModel sellmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
               "Line", "Item", "Qty", "Price", "Desc", "Ref"
            });
                
                 javax.swing.table.DefaultTableModel buymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Item", "Qty", "Price", "Desc", "Ref"
            });
                 javax.swing.table.DefaultTableModel expensemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Line", "Item", "Qty", "Price", "Ref", "Acct"
            });
                
                
    /**
     * Creates new form ShipMaintPanel
     */
    public CashTran() {
        initComponents();
      
        
       
       
    }
   
    
    public void addExpenseAccount(String desc) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                int acctnbr = OVData.getNextNbr("expenseaccount");
                if (acctnbr >= 99000000 && acctnbr <= 99999999) {
                    proceed = true;
                } else {
                    bsmf.MainFrame.show("expense account generated number is beyond limits of 99000000 to 99999999");
                    return;
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT ac_id FROM  ac_mstr where ac_id = " + "'" + String.valueOf(acctnbr) + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into ac_mstr "
                            + "( ac_id, ac_desc, ac_type, ac_cur, ac_display ) "
                            + " values ( " + "'" + String.valueOf(acctnbr) + "'" + ","
                            + "'" + desc.replace("'", "") + "'" + ","
                            + "'" + "E" + "'" + ","
                            + "'" + OVData.getDefaultCurrency() + "'" + ","
                            + "'" + '1' + "'"        
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Acct Number: " + String.valueOf(acctnbr));
                        dditem.addItem(String.valueOf(acctnbr));
                    } else {
                        bsmf.MainFrame.show("Acct Number Already Exists");
                    }

                    //reinitapmvariables();
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("unable to insert into ac_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initvars(String arg) {
        
         isLoad = true;
       clearAll(); 
       disableAll();
      
        if (buttonGroup1.getButtonCount() == 0) {
        buttonGroup1.add(rbBuy);
        buttonGroup1.add(rbSell);
         buttonGroup1.add(rbexpense);
        }
        buttonGroup1.clearSelection();
       
        
       btnew.setEnabled(true);
       isLoad = false;
       
    }
    
    public void disableAll() {
         rbBuy.setEnabled(false);
        rbSell.setEnabled(false);
        rbexpense.setEnabled(false);
        tbactualamt.setEnabled(false);
        ddentity.setEnabled(false);
        
        dcdate.setEnabled(false);
        tbrmks.setEnabled(false);
        tbpo.setEnabled(false);
        dditem.setEnabled(false);
        tbitemservice.setEnabled(false);
        tbprice.setEnabled(false);
        tbref.setEnabled(false);
        tbqty.setEnabled(false);
        btadditem.setEnabled(false);
        btdeleteitem.setEnabled(false);
        btadd.setEnabled(false);
         btaddaccount.setEnabled(false);
        btaddentity.setEnabled(false);
        detailtable.setEnabled(false);
        expensenbr.setEnabled(false);
       
    }
    
    public void enableAll() {
         rbBuy.setEnabled(true);
        rbSell.setEnabled(true);
        rbexpense.setEnabled(true);
        tbactualamt.setEnabled(true);
        ddentity.setEnabled(true);
      
        dcdate.setEnabled(true);
        tbrmks.setEnabled(true);
        tbpo.setEnabled(true);
        dditem.setEnabled(true);
        tbitemservice.setEnabled(true);
        tbprice.setEnabled(true);
        tbref.setEnabled(true);
        tbqty.setEnabled(true);
        btadditem.setEnabled(true);
        btaddentity.setEnabled(true);
        btdeleteitem.setEnabled(true);
        btadd.setEnabled(true);
        btaddaccount.setEnabled(true);
        detailtable.setEnabled(true);
       // expensenbr.setEnabled(true);
    }
    
    public void clearAll() {
         
         btaddaccount.setVisible(false);
         tbqty.setText("");
         tbprice.setText("");
         terms = "";
         apacct = "";
         apcc = "";
         apbank = "";
         actamt = 0.00;
         actqty = 0.00;
         expensenbr.setText("");
         tbpo.setText("");
        tbrmks.setText("");
        tbref.setText("");
        rbBuy.setSelected(false);
        rbSell.setSelected(false);
        rbexpense.setSelected(false);
       
        
         jPanel3.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        
        tbactualamt.setText("");
        tbactualamt.setEditable(false);
       
         lbtitle.setText("");
       
        lbname.setText("");
      
       
        buymodel.setRowCount(0);
        sellmodel.setRowCount(0);
        expensemodel.setRowCount(0);
       
        detailtable.setModel(buymodel);
       
        
        ddentity.removeAllItems();
        ArrayList entity = new ArrayList();
        if (rbSell.isSelected()) {
        entity = OVData.getcustmstrlist();
        lblentity.setText("Sell To");
        } else if (rbBuy.isSelected()) {
        entity = OVData.getvendmstrlist();   
        lblentity.setText("Buy From");
        } else {
        entity = OVData.getvendmstrlist();   
        lblentity.setText("Buy From");    
        }
        for (int i = 0; i < entity.size(); i++) {
            ddentity.addItem(entity.get(i));
        }
            ddentity.setSelectedIndex(0);
        
         
        
        
        dditem.removeAllItems();
        ArrayList<String> myitems = OVData.getItemMasterACodeForCashTran();
        for (String code : myitems) {
            dditem.addItem(code);
        }
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
        
    }
  
    public void loadImage() {
         DateFormat dfdate = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        File file = null;
        String ImageDir = OVData.getSystemImageDirectory();
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            String suffix = FilenameUtils.getExtension(file.getName()); 
            newFileName = dditem.getSelectedItem().toString() + "_" + dfdate.format(now) + "." + suffix;
            // insert image filename into database
          //  OVData.addItemImage(dditem.getSelectedItem().toString(), newFileName);
            
            // now lets copy the file over to the appropriate directory  
            file = new File(SourceDir);
            
       //     java.nio.file.Files.copy(file.toPath(), new File("images/" + newFileName).toPath(), 
                 java.nio.file.Files.copy(file.toPath(), new File(ImageDir + newFileName).toPath(), 
                 java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                 java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                 java.nio.file.LinkOption.NOFOLLOW_LINKS);
                 
       // now update lblpic with new image
            ImageIcon imageIcon = new ImageIcon(ImageDir + newFileName);
          
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
    }   
     
      
      public void setvendorvariables(String vendor) {
        
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select vd_ap_acct, vd_ap_cc, vd_terms, vd_bank from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("vd_ap_acct");
                   apcc = res.getString("vd_ap_cc");
                   terms = res.getString("vd_terms");
                   apbank = res.getString("vd_bank");
                }

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("cannot set vendor variables");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        fc = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        detailtable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        dcdate = new com.toedter.calendar.JDateChooser();
        expensenbr = new javax.swing.JTextField();
        rbBuy = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        lblentity = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        ddentity = new javax.swing.JComboBox();
        lbname = new javax.swing.JLabel();
        tbpo = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        rbSell = new javax.swing.JRadioButton();
        lbtitle = new javax.swing.JLabel();
        btaddentity = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lbitem = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        dditem = new javax.swing.JComboBox<>();
        btdeleteitem = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        tbref = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lbacct = new javax.swing.JLabel();
        btaddaccount = new javax.swing.JButton();
        rbexpense = new javax.swing.JRadioButton();
        tbactualamt = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cash Transaction"));

        btadd.setText("Commit");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        detailtable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(detailtable);

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        dcdate.setDateFormatString("yyyy-MM-dd");

        rbBuy.setText("buy");
        rbBuy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbBuyItemStateChanged(evt);
            }
        });

        jLabel2.setText("PO#");

        lblentity.setText("Entity");

        ddentity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddentityActionPerformed(evt);
            }
        });

        jLabel24.setText("TransNbr");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel4.setText("Rmks");

        jLabel35.setText("Date");

        rbSell.setText("sell");
        rbSell.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbSellItemStateChanged(evt);
            }
        });

        lbtitle.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N

        btaddentity.setText("add new buyer/seller");
        btaddentity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddentityActionPerformed(evt);
            }
        });

        lbitem.setText("Item Nbr");

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel6.setText("Price");

        jLabel5.setText("Item Description");

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel3.setText("Tag/Lot#");

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Qty");

        btaddaccount.setText("Add Account");
        btaddaccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddaccountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbitem)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteitem))
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btaddaccount))
                        .addComponent(tbitemservice, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(473, 473, 473))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbitem)
                        .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btaddaccount))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem)
                    .addComponent(btdeleteitem))
                .addContainerGap())
        );

        rbexpense.setText("expense");
        rbexpense.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbexpenseItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblentity, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnew))
                                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(285, 285, 285))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(241, 241, 241)
                                        .addComponent(rbSell)
                                        .addGap(18, 18, 18)
                                        .addComponent(rbBuy)
                                        .addGap(22, 22, 22)
                                        .addComponent(rbexpense)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(ddentity, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btaddentity)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(177, 177, 177)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel24)
                                    .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rbSell)
                                    .addComponent(btnew)
                                    .addComponent(rbBuy)
                                    .addComponent(rbexpense))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddentity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblentity)
                                    .addComponent(btaddentity))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel35))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tbactualamt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbactualamtActionPerformed(evt);
            }
        });

        jLabel27.setText("Total Amt");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addComponent(jScrollPane7))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        expensenbr.setText(String.valueOf(OVData.getNextNbr("voucher")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
                
                enableAll();
               btnew.setEnabled(false);
               rbBuy.setSelected(true);  
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
       
       if (tbprice.getText().isEmpty()) {
           bsmf.MainFrame.show("price field must be numeric");
           tbprice.requestFocus();
           return;
       }
       
       if (tbitemservice.getText().isEmpty()) {
           bsmf.MainFrame.show("Description field cannot be blank");
           tbitemservice.requestFocus();
           return;
       }
        
       partnumber = "z" + String.valueOf(OVData.getNextNbr("asset"));
              
        
        
        // cannot duplicate itemnumber in table
        boolean canproceed = true;
        for (int j = 0; j < detailtable.getRowCount(); j++) {
            if (dditem.getSelectedItem().toString().equals(detailtable.getValueAt(j, 1).toString())) {
                bsmf.MainFrame.show("cannot add duplicate item number");
                canproceed = false;
            }
        }
        
        if (! canproceed) {
            return;
        }
        
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
       // shipperdet   "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "shippedqty", "status", "WH", "LOC", "Desc"
            DecimalFormat df = new DecimalFormat("#0.00"); 
            voucherline++;
            actqty += Double.valueOf(tbqty.getText()); 
            actamt += Double.valueOf(tbqty.getText()) * 
                          Double.valueOf(tbprice.getText());
           
            
            if (rbBuy.isSelected()) {
            buymodel.addRow(new Object[] { voucherline,
                                                  partnumber,
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  tbitemservice.getText().replace("'",""),
                                                  tbref.getText()
                                                  });
            }
            if (rbSell.isSelected()) {
            sellmodel.addRow(new Object[] { voucherline, 
                                            dditem.getSelectedItem().toString(),
                                            tbqty.getText(),
                                            tbprice.getText(),
                                            tbitemservice.getText().replace("'",""),
                                            tbref.getText()
                                          });
            }
             if (rbexpense.isSelected()) {
                 //"Line", "Item", "Qty", "Price", "Ref", "Acct"
            expensemodel.addRow(new Object[] { voucherline, 
                                            tbitemservice.getText().replace("'",""),
                                            tbqty.getText(),
                                            tbprice.getText(),
                                            tbref.getText(),
                                            dditem.getSelectedItem().toString()
                                          });
            }
            
            
        if (detailtable.getRowCount() > 0) {
            rbBuy.setEnabled(false);
            rbSell.setEnabled(false);
            rbexpense.setEnabled(false);
        }    else {
            rbBuy.setEnabled(true);
            rbSell.setEnabled(true);
            rbexpense.setEnabled(true);
        }
            
            
        tbitemservice.setText("");
        tbprice.setText("");
        tbactualamt.setText(df.format(actamt));
        dditem.setSelectedIndex(0);
        tbitemservice.requestFocus();
        
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                boolean error = false;
                String trantype = "buy";
                String key = "";
                
                
                if (rbSell.isSelected()) { trantype = "sell"; }
                if (rbBuy.isSelected()) { trantype = "buy"; }
                if (rbexpense.isSelected()) { trantype = "expense"; }
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                DecimalFormat df = new DecimalFormat("#0.00");   
                setvendorvariables(ddentity.getSelectedItem().toString());
                    
                curr = OVData.getDefaultCurrency();
                String site = OVData.getDefaultSite();   
                String po = tbpo.getText();
                if (po.isEmpty()) {
                    po = "cashtran";
                }
                    
                    
                    if (proceed && rbSell.isSelected()) {
                          int shipperid = OVData.getNextNbr("shipper");   
                          key = String.valueOf(shipperid);
                             boolean iserror = OVData.CreateShipperHdr(key, site,
                             String.valueOf(key), 
                              ddentity.getSelectedItem().toString(), // sh_cust
                              ddentity.getSelectedItem().toString(),  // sh_ship
                              expensenbr.getText().replace("'", ""), // sh_so
                              tbpo.getText().replace("'", ""),  // sh_po
                              tbpo.getText().replace("'", ""),  // sh_ref
                              dfdate.format(now), // duedate
                              dfdate.format(now),  // orddate
                              tbrmks.getText().replace("'", ""), // sh_rmks
                              "", "A");  // shipvia, ShipType

                     if (! iserror) {
                         for (int j = 0; j < detailtable.getRowCount(); j++) {
                             OVData.CreateShipperDet(String.valueOf(shipperid), detailtable.getValueAt(j, 1).toString(), "", "", "", "", "1", 
                                     detailtable.getValueAt(j, 3).toString(), "0", detailtable.getValueAt(j, 3).toString(), dfdate.format(now), 
                                     detailtable.getValueAt(j, 4).toString(), detailtable.getValueAt(j, 0).toString(), site, "", "", "0");
                         }
                        
                     }
                     
                   

                     // now confirm shipment
                     String[] message = OVData.confirmShipment(String.valueOf(shipperid), now);
                     if (message[0].equals("1")) { // if error
                         bsmf.MainFrame.show(message[1]);
                         error = true;
                     } 
                     
                                     
                     // now emulate AR payment
                     if (! error) {
                     String batchnbr = String.valueOf(OVData.getNextNbr("ar"));
                      st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_type, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_status, ar_bank, ar_curr, ar_base_curr, ar_site ) "
                        + " values ( " + "'" + ddentity.getSelectedItem().toString() + "'" + ","
                        + "'" + batchnbr + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + "P" + "'" + ","
                        + "'" + shipperid + "'" + ","
                        + "'" + tbrmks.getText() + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + OVData.getDefaultARAcct() + "'" + ","
                        + "'" + OVData.getDefaultARCC() + "'" + ","
                        + "'" + "c" + "'"  + ","
                        + "'" + OVData.getDefaultARBank() + "'" + ","
                        + "'" + curr + "'" + ","     
                        + "'" + curr + "'" + ","         
                        + "'" + site + "'"
                        + ")"
                        + ";");
                      
                      
                     
                      
               
                        for (int j = 0; j < detailtable.getRowCount(); j++) {
                            st.executeUpdate("insert into ard_mstr "
                                + "(ard_id, ard_cust, ard_ref, ard_line, ard_date, ard_amt, ard_amt_tax ) "
                                + " values ( " + "'" + batchnbr + "'" + ","
                                    + "'" + ddentity.getSelectedItem().toString() + "'" + ","
                                + "'" + shipperid + "'" + ","
                                + "'" + (j + 1) + "'" + ","
                                + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                                + "'" + detailtable.getValueAt(j, 3).toString() + "'"  + ","
                                + "'" + "0" + "'" 
                                + ")"
                                + ";");
                            
                           
                            
                        }
                    
                         // update AR entry for original invoices with status and open amt  
                        error = OVData.ARUpdate(batchnbr);
                        if (! error) {
                        error = OVData.glEntryFromARPayment(batchnbr, dcdate.getDate());
                        }
                     }
                    // end of emulate AR Payment
                      
                    
                    
                     
                     if (! error) {
                        bsmf.MainFrame.show("sell complete");
                     }
                    
                     
                     
                    }
                    
                    if (proceed && rbBuy.isSelected()) {
                        
                      st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_type, ap_ref, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_curr, ap_base_curr, ap_bank ) "
                        + " values ( " + "'" + ddentity.getSelectedItem() + "'" + ","
                              + "'" + site + "'" + ","
                        + "'" + expensenbr.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + "V" + "'" + ","
                        + "'" + tbpo.getText() + "'" + ","
                        + "'" + tbrmks.getText().replace("'", "") + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + "o" + "'"  + ","
                        + "'" + curr + "'"  + ","    
                        + "'" + curr + "'"  + "," 
                        + "'" + apbank + "'"
                        + ")"
                        + ";");
                      
                      
                       // lets create receiver
                        int receiverNbr = OVData.getNextNbr("receiver");
                        key = String.valueOf(receiverNbr);
                         st.executeUpdate("insert into recv_mstr "
                        + "(rv_id, rv_vend, "
                        + " rv_recvdate, rv_packingslip, rv_userid, rv_site, rv_terms, rv_ap_acct, rv_ap_cc) "
                        + " values ( " + "'" + key + "'" + ","
                        + "'" + ddentity.getSelectedItem() + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + "asset" + "'" + ","
                        + "'" + bsmf.MainFrame.userid.toString() + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'"
                        + ")"
                        + ";");
                      
                      
                      
                int amt = 0;
               // voucherdet:  "PO", "Line", "Part", "Qty", "voprice", "recvID", "recvLine", "Acct", "CC"
               //detailtable: "Line", "Part", "Qty", "Price", "Desc", "ImageFile"
                    for (int j = 0; j < detailtable.getRowCount(); j++) {
                        
                        // lets add item to database
                        OVData.addItemMasterMinimum(detailtable.getValueAt(j, 1).toString(), site, detailtable.getValueAt(j, 4).toString(), "A", detailtable.getValueAt(j, 3).toString());
                      //  if (! detailtable.getValueAt(j, 5).toString().isEmpty()) {
                       // OVData.addItemImage(detailtable.getValueAt(j, 1).toString(), detailtable.getValueAt(j, 5).toString());  
                       // }
                        // lets add each item to inventory
                        OVData.UpdateInventoryDiscrete(detailtable.getValueAt(j, 1).toString(), site,
                                "", "", Double.valueOf("1"));
                        // now lets add detail voucher
                        //amt = Integer.valueOf(detailtable.getValueAt(j, 3).toString());
                        
                       
                         
                         // now create recevier detail
                          st.executeUpdate("insert into recv_det "
                            + "(rvd_id, rvd_rline, rvd_part, rvd_po, rvd_poline, rvd_qty, rvd_voqty, "
                            + "rvd_listprice, rvd_disc, rvd_netprice,  "
                            + " rvd_loc, rvd_wh, rvd_serial, rvd_lot, rvd_cost, rvd_site, rvd_packingslip, rvd_date ) "
                            + " values ( " + "'" + key + "'" + ","
                            + "'" + String.valueOf(j + 1) + "'" + ","
                            + "'" + detailtable.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tbpo.getText() + "'" + ","
                            + "'" + String.valueOf(j + 1) + "'" + ","
                            + "'" + detailtable.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 2).toString() + "'" + ","  // go ahead and set receiver voucher qty         
                            + "'" + detailtable.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + "0" + "'" + ","
                            + "'" + detailtable.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + "" + "'" + ","
                            + "'" + "" + "'" + ","
                            + "'" + detailtable.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + site + "'" + ","        
                            + "'" + "asset" + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" 
                            + ")"
                            + ";");
                         
                         
                        
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_part, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + expensenbr.getText() + "'" + ","
                                + "'" + ddentity.getSelectedItem() + "'" + ","
                            + "'" + String.valueOf(receiverNbr) + "'" + ","
                            + "'" + String.valueOf(j + 1) + "'" + ","
                            + "'" + detailtable.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + tbref.getText() + "'" + ","        
                            + "'" + OVData.getDefaultAssetAcctAP() + "'" + ","
                            + "'" + OVData.getDefaultAssetCC() + "'" 
                            + ")"
                            + ";");
                  
                     }
                    
                    /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromCashTranBuy(expensenbr.getText(), dcdate.getDate());
                    
                    /* emulate cash payment */    
                        if (! error)
                        error = OVData.APExpense(dcdate.getDate(), OVData.getNextNbr("expensenumber"), expensenbr.getText(), tbpo.getText(), ddentity.getSelectedItem().toString(), actamt);
                        
                    if (error) {
                        bsmf.MainFrame.show("An error occurred");
                    } else {
                    bsmf.MainFrame.show("buy complete");
                    }
                    //reinitreceivervariables("");
                   
                    // btQualProbAdd.setEnabled(false);
                } // if rbbuy
                   
                 if (proceed && rbexpense.isSelected()) {
                     
                       st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_type, ap_ref, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_bank ) "
                        + " values ( " + "'" + ddentity.getSelectedItem() + "'" + ","
                              + "'" + site + "'" + ","
                        + "'" + expensenbr.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + "V" + "'" + ","
                        + "'" + tbpo.getText() + "'" + ","
                        + "'" + tbrmks.getText() + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + "o" + "'"  + ","
                        + "'" + apbank + "'"
                        + ")"
                        + ";");
               
               // "Line", "Item", "Qty", "Price", "Ref", "Acct"
                    for (int j = 0; j < detailtable.getRowCount(); j++) {
                       
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_part, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + expensenbr.getText() + "'" + ","
                                + "'" + ddentity.getSelectedItem() + "'" + ","
                            + "'" + "expense" + "'" + ","
                            + "'" +detailtable.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + tbpo.getText().toString() + "'" + ","
                            + "'" + detailtable.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + apcc + "'"
                            + ")"
                            + ";");
                  
                     }
                    
                    /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromVoucherExpense(expensenbr.getText(), dcdate.getDate());
                         
                        if (! error)
                        error = OVData.APExpense(dcdate.getDate(), OVData.getNextNbr("expensenumber"), expensenbr.getText(), tbpo.getText(), ddentity.getSelectedItem().toString(), actamt);
                        
                    if (error) {
                        bsmf.MainFrame.show("An error in expense module occurred");
                    } else {
                    bsmf.MainFrame.show("Expense Complete");
                    }
                     
                     
                     
                 }   // if rbexpense
                    
                    
                    
                    
                    if (proceed ) {
                     st.executeUpdate("insert into pos_mstr "
                        + "(pos_nbr, pos_entrydate, pos_entity, pos_entityname, pos_type, pos_key, pos_totqty, pos_totamt ) "
                        + " values ( " + "'" + expensenbr.getText() + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + "," 
                        + "'" + ddentity.getSelectedItem().toString() + "'" + ","
                        + "'" + lbname.getText() + "'" + ","
                        + "'" + trantype + "'" + ","       
                        + "'" + key + "'" + ","         
                        + "'" + df.format(actqty) + "'" + ","
                        + "'" + df.format(actamt) + "'" 
                        + ")"
                        + ";");
                     
                      for (int j = 0; j < detailtable.getRowCount(); j++) {
                      st.executeUpdate("insert into pos_det "
                                + "(posd_nbr, posd_line, posd_item, posd_desc, posd_ref, posd_qty, posd_listprice, posd_netprice ) "
                                + " values ( " + "'" + expensenbr.getText() + "'" + ","
                                + "'" + (j + 1) + "'" + ","
                                + "'" + detailtable.getValueAt(j, 1).toString() + "'"  + ","      
                                + "'" + detailtable.getValueAt(j, 4).toString() + "'"  + "," 
                                + "'" + detailtable.getValueAt(j, 5).toString() + "'"  + ","        
                                + "'" + detailtable.getValueAt(j, 2).toString() + "'"  + ","   
                                + "'" + detailtable.getValueAt(j, 3).toString() + "'"  + ","
                                + "'" + detailtable.getValueAt(j, 3).toString() + "'"  
                                + ")"
                                + ";");
                      }
                     
                    }
               
                     initvars(""); 
                        
                    
            } catch (SQLException s) {
                s.printStackTrace();
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void ddentityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddentityActionPerformed
       
        if (ddentity.getSelectedItem() != null )
        try {
            
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                if (rbBuy.isSelected()) {
                res = st.executeQuery("select vd_name as 'name' from vd_mstr where vd_addr = " + "'" + ddentity.getSelectedItem().toString() + "'" + ";");
                } else if (rbexpense.isSelected()) {
                res = st.executeQuery("select vd_name as 'name' from vd_mstr where vd_addr = " + "'" + ddentity.getSelectedItem().toString() + "'" + ";");
                } else {
                res = st.executeQuery("select cm_name as 'name' from cm_mstr where cm_code = " + "'" + ddentity.getSelectedItem().toString() + "'" + ";");   
                }
                while (res.next()) {
                    lbname.setText(res.getString("name"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot get entity name");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_ddentityActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = detailtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
             actamt -= Double.valueOf(detailtable.getModel().getValueAt(i,2).toString()) * Double.valueOf(detailtable.getModel().getValueAt(i,3).toString());
             actqty -= Double.valueOf(detailtable.getModel().getValueAt(i,2).toString());
            ((javax.swing.table.DefaultTableModel) detailtable.getModel()).removeRow(i);
           voucherline--;
        }
        tbactualamt.setText(actamt.toString());
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        if (dditem.getSelectedItem() != null && ! isLoad )
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                 
                if (rbexpense.isSelected()) {
                         res = st.executeQuery("select ac_desc from ac_mstr where ac_id = " + "'" + dditem.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        lbacct.setText(res.getString("ac_desc"));
                    }
                } else {
                    res = st.executeQuery("select it_desc from item_mstr where it_item = " + "'" + dditem.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        tbitemservice.setText(res.getString("it_desc"));
                    }
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
            bsmf.MainFrame.con.close();
         
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }//GEN-LAST:event_dditemActionPerformed

    private void rbBuyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbBuyItemStateChanged
        
        if (! isLoad) {
        
        ddentity.removeAllItems();
            
            ArrayList entity = new ArrayList();   
        if (rbBuy.isSelected()) {
             entity = OVData.getvendmstrlist();   
            lblentity.setText("Buy From");
            btaddentity.setText("New Vendor");
            dditem.setEnabled(false);
             btaddaccount.setVisible(false);
             btaddaccount.setEnabled(false);
            tbitemservice.setEnabled(true);
            tbqty.setText("1");
            btadditem.setEnabled(true);
            btdeleteitem.setEnabled(true);
          
           
             jPanel3.setBorder(BorderFactory.createLineBorder(Color.RED));
              detailtable.setModel(buymodel);
              lbtitle.setText("Buying Asset");
              lbtitle.setForeground(Color.red);
         
        for (int i = 0; i < entity.size(); i++) {
                    ddentity.addItem(entity.get(i));
                }
            ddentity.setSelectedIndex(0);
        }
        
        lbitem.setText("Item");
         lbacct.setText("");
         tbitemservice.setText("");
        
      }   
       
    }//GEN-LAST:event_rbBuyItemStateChanged

    private void tbactualamtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbactualamtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbactualamtActionPerformed

    private void btaddentityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddentityActionPerformed
        if (rbBuy.isSelected()) {
        reinitpanels("VendMstrMaint", true, "");
        } else {
        reinitpanels("MenuCustMstr", true, "");
        }
    }//GEN-LAST:event_btaddentityActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
        String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
       String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void rbSellItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbSellItemStateChanged
        
       if (! isLoad) {
        ArrayList entity = new ArrayList();  
        if (rbSell.isSelected()) {
            entity = OVData.getcustmstrlist();
            lblentity.setText("Sell To");
            btaddentity.setText("New Customer");
            dditem.setEnabled(true);
            tbitemservice.setEnabled(false);
             btaddaccount.setVisible(false);
             btaddaccount.setEnabled(false);
             tbitemservice.setText("");
            tbqty.setText("1");
             btadditem.setEnabled(true);
              btdeleteitem.setEnabled(true);
             jPanel3.setBorder(BorderFactory.createLineBorder(Color.BLUE));
              detailtable.setModel(sellmodel);
              lbtitle.setText("Selling Asset");
              lbtitle.setForeground(Color.blue);
              
               for (int i = 0; i < entity.size(); i++) {
                    ddentity.addItem(entity.get(i));
                }
            ddentity.setSelectedIndex(0);
         
        lbitem.setText("Item");    
          lbacct.setText("");   
        dditem.removeAllItems();
        ArrayList<String> myitems = OVData.getItemMasterACodeForCashTran();
        for (String code : myitems) {
            dditem.addItem(code);
        }
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
            
        
              
        }
        }
       
    }//GEN-LAST:event_rbSellItemStateChanged

    private void rbexpenseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbexpenseItemStateChanged
          if (! isLoad) {
        
        ddentity.removeAllItems();
            
            ArrayList entity = new ArrayList();   
        if (rbexpense.isSelected()) {
             entity = OVData.getvendmstrlist();   
            lblentity.setText("Buy From");
            btaddentity.setText("New Vendor");
            
            tbitemservice.setEnabled(true);
             btaddaccount.setVisible(true);
             btaddaccount.setEnabled(true);
             
            tbitemservice.setText("");
            tbqty.setText("1");
            btadditem.setEnabled(true);
            btdeleteitem.setEnabled(true);
          
           
             jPanel3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
              detailtable.setModel(expensemodel);
              lbtitle.setText("Expense");
              lbtitle.setForeground(Color.BLACK);
         
        for (int i = 0; i < entity.size(); i++) {
                    ddentity.addItem(entity.get(i));
                }
            ddentity.setSelectedIndex(0);
        
        lbitem.setText("Expense Account");
        
        dditem.setEnabled(true);
        dditem.removeAllItems();
        lbacct.setText("");
        ArrayList<String> myitems = OVData.getGLAcctExpenseDisplayOnly();
        for (String code : myitems) {
            dditem.addItem(code);
        }
        dditem.setSelectedIndex(0);
        
        }
      }   
    }//GEN-LAST:event_rbexpenseItemStateChanged

    private void btaddaccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddaccountActionPerformed
       String s = bsmf.MainFrame.input("Please input the account description: ");
       addExpenseAccount(s);
    }//GEN-LAST:event_btaddaccountActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddaccount;
    private javax.swing.JButton btaddentity;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btnew;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddentity;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JTable detailtable;
    private javax.swing.JTextField expensenbr;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lbitem;
    private javax.swing.JLabel lblentity;
    private javax.swing.JLabel lbname;
    private javax.swing.JLabel lbtitle;
    private javax.swing.JRadioButton rbBuy;
    private javax.swing.JRadioButton rbSell;
    private javax.swing.JRadioButton rbexpense;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
