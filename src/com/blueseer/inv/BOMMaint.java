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

package com.blueseer.inv;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.inv.invData.addPBM;
import com.blueseer.inv.invData.bom_mstr;
import static com.blueseer.inv.invData.deletePBM;
import static com.blueseer.inv.invData.getBOMMstr;
import static com.blueseer.inv.invData.getItemOutCost;
import static com.blueseer.inv.invData.getItemOvhCost;
import com.blueseer.inv.invData.pbm_mstr;
import static com.blueseer.inv.invData.updateCurrentItemCost;
import static com.blueseer.inv.invData.updatePBM;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble5;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
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
import javax.swing.event.TableModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vaughnte
 */
public class BOMMaint extends javax.swing.JPanel {

    /**
     * Creates new form BOMMaintPanel
     */
    
    // global variable declarations
                boolean isLoad = false;
                String site = "";
                String parent = "";
                String BomID = "";
                boolean bomexist = false;
                boolean newbomid = false;
                public static bom_mstr x = null;
                
     javax.swing.table.DefaultTableModel matlmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                        getGlobalColumnTag("item"), 
                        getGlobalColumnTag("operation"), 
                        getGlobalColumnTag("qtyper"), 
                        getGlobalColumnTag("currentcost"),
                        getGlobalColumnTag("standardcost")});
      
      javax.swing.event.TableModelListener ml = new javax.swing.event.TableModelListener() {
                    @Override
                    public void tableChanged(TableModelEvent tme) {
                        if (tme.getType() == TableModelEvent.UPDATE && (tme.getColumn() == 3 || tme.getColumn() == 4 )) {
                            callSimulateCost();
                        }
                        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };    
     
    public BOMMaint() {
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
             getRecord(new String[]{tbkey.getText(), ""}); 
             updateFormAddUpdate();
           } else if (this.type.equals("get")) {
             updateForm(this.key[0]);
             tbkey.requestFocus();
           } else {
             getRecord(new String[]{this.key[0], this.key[1]});  // add and update
             updateFormAddUpdate();
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
       bomexist = false;
       newbomid = false;
       site = "";
       parent = "";
       
       tbkey.setText("");
       tbkey.setEditable(true);
       tbkey.setForeground(Color.black);    
       
       tbbomid.setText("");
       tbbomdesc.setText("");
       cbdefault.setSelected(false);
       cbenabled.setSelected(false);
       tbparentcostCUR.setBackground(Color.white); 
       tbparentcostSTD.setBackground(Color.white);
       
       ddcomp.removeAllItems();
        ddop.removeAllItems();
        tbqtyper.setText("");
        tbref.setText("");
        lblcomp.setText("");
        tbtotmaterial.setText("");
        tbtotoperational.setText("");
        tbparentcostCUR.setText("");
        tbparentcostSTD.setText("");
        tbtotmaterialsim.setText("");
        tbtotoperationalsim.setText("");
        tbparentcostsim.setText("");
        tbovhout.setText("");
        tbcompcost.setText("");
        tbcomptype.setText("");
        
        tbrunrate.setEditable(false);
        tbsetuprate.setEditable(false);
        tbburdenrate.setEditable(false);
        tbcrewsize.setEditable(false);
        tbsetupsize.setEditable(false);
        tbpph.setEditable(false);
        tbpps.setEditable(false);
       
        matlmodel.setRowCount(0);
        matlmodel.addTableModelListener(ml);
        
          DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        Object root = model.getRoot();
    	while(!model.isLeaf(root))
    	{
    		model.removeNodeFromParent((MutableTreeNode)model.getChild(root,0));
    	}
        jTree1.setVisible(false);
       
       
        
          ArrayList<String> mylist = new ArrayList<String>();
              mylist = invData.getItemMasterAlllist();
               for (int i = 0; i < mylist.size(); i++) {
                    ddcomp.addItem(mylist.get(i));
               }
        
       isLoad = false; 
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
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
        setPanelComponentState(this, true);
        btdelete.setEnabled(false);
        btupdate.setEnabled(false);
        btadd.setEnabled(false);
        tbbomid.setEnabled(false);
        if (x[0].equals("0")) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
           tbkey.setEditable(false);
           tbkey.setForeground(Color.blue);
           btlookupbom.setEnabled(true);
           btadd.setEnabled(true);
           bomexist = true;
          
        } else if (x[0].equals("1")) {
           m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1146)};  // no bom found
                   tbkey.setForeground(Color.red); 
                   btlookupbom.setEnabled(false);
                   btadd.setEnabled(false);
                   bomexist = false;
        } else {
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1147)}; // no routing found
                    tbkey.setEditable(true);
                    tbkey.setForeground(Color.red); 
                    btlookupbom.setEnabled(false);
                    btadd.setEnabled(false);
                    bomexist = false;
        }
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
               
        

        if (tbkey.getText().isEmpty()) {
            b = false;
            bsmf.MainFrame.show(getMessageTag(1024, tbkey.getName()));
            tbkey.requestFocus();
            return b;
        }
        
        if (tbbomid.getText().isEmpty()) {
            b = false;
            bsmf.MainFrame.show(getMessageTag(1024, tbkey.getName()));
            tbbomid.requestFocus();
            return b;
        }
        
        if (tbqtyper.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, tbqtyper.getName()));
                    tbqtyper.requestFocus();
                    return b;
        }

        if (! OVData.isValidItem(tbkey.getText())) {
            b = false;
            tbkey.requestFocus();
            return b;
        }
        
        if (OVData.getDefaultBomID(tbkey.getText()).isEmpty() && ! cbdefault.isSelected()) {
            b = false;
            bsmf.MainFrame.show(getMessageTag(1166));
            cbdefault.requestFocus();
            return b;
        }
        
        if (OVData.getDefaultBomID(tbkey.getText()).equals(tbbomid.getText()) && ! cbdefault.isSelected()) {
            b = false;
            bsmf.MainFrame.show(getMessageTag(1168));
            cbdefault.requestFocus();
            return b;
        }

        if (ddcomp.getSelectedItem() == null) {
           b = false;
           bsmf.MainFrame.show(getMessageTag(1026, ddcomp.getName()));
           ddcomp.requestFocus();
           return b;
       } 

        if (! OVData.isValidItem(ddcomp.getSelectedItem().toString())) {
            b = false;
            ddcomp.requestFocus();
            bsmf.MainFrame.show(getMessageTag(1026, ddcomp.getName()));
            return b;
        }


        if (ddop.getSelectedItem() == null) {
           b = false;
           bsmf.MainFrame.show(getMessageTag(1026, ddop.getName()));
           ddop.requestFocus();
           return b;
        } 

        if (ddcomp.getSelectedItem().toString().toLowerCase().equals(tbkey.getText().toLowerCase())) {
           b = false;
           bsmf.MainFrame.show(getMessageTag(1069));
           ddcomp.requestFocus();
           return b;
        } 
               
        return b;
    }
        
    public void initvars(String[] arg) {
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btlookup.setEnabled(true);
         if (arg != null && arg.length > 0) {
            executeTask("get",arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
       
    }
    
    public String[] getRecord(String[] key) {
                         
        // lets first determine if there are any BOMs default or alternates
        BomID = OVData.getDefaultBomID(key[0]);
                
        // override with 2nd parameter from lookupUPBOM when we know exactly which BOM ID we want
        if (key.length > 1 && ! key[1].isEmpty()) {
         BomID = key[1];
        }
        if (key.length > 1 && key[1].isEmpty() && ! BomID.isEmpty()) {
            key[1] = BomID;
        }
        bom_mstr z = getBOMMstr(key);
        x = z;     
        
        boolean hasRouting =  getRouting(key[0]);
        String[] message = x.m();
        if (! hasRouting) {
          message[0] = "-1";
          message[1] = "No Routing found";
        } 
        
        
        
        return message;         
    }
    
    public String[] addRecord(String[] x) {
        String[] m = new String[2];
        m = addPBM(createRecord(), createRecordBomMstr(), true);
        return m;
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
        m = updatePBM(createRecord(), createRecordBomMstr());
        return m;
    }
    
    public String[] deleteRecord(String[] x) {
    String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
      DefaultMutableTreeNode comp = null;  
      Object o = jTree1.getLastSelectedPathComponent();
  //    if (o != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 2)) {
      if (o != null) {
       comp = (DefaultMutableTreeNode)o;
      }
      if (proceed && comp != null && comp.getLevel() == 2) {
         m = deletePBM(createRecord(comp.toString()), createRecordBomMstr(), OVData.getBomPbmCount(BomID)); 
         updateCurrentItemCost(tbkey.getText());  
      } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
    }
   
    public pbm_mstr createRecord() {
        pbm_mstr x = new pbm_mstr(null, tbkey.getText().toString(),
                ddcomp.getSelectedItem().toString(),
                bsformat("d", tbqtyper.getText(), "5").replace(defaultDecimalSeparator, '.'),
                ddop.getSelectedItem().toString(),
                tbref.getText(),
                tbcomptype.getText(), tbbomid.getText());
        return x;
    } 
    
    public pbm_mstr createRecord(String comp) {
        pbm_mstr x = new pbm_mstr(null, tbkey.getText().toString(),
                comp,
                bsformat("d", tbqtyper.getText(), "5").replace(defaultDecimalSeparator, '.'),
                ddop.getSelectedItem().toString(),
                tbref.getText(),
                tbcomptype.getText(), tbbomid.getText());
        return x;
    } 
    
    public bom_mstr createRecordBomMstr() {
        bom_mstr x = new bom_mstr(null, tbbomid.getText(),
                tbbomdesc.getText(),
                tbkey.getText(),
                BlueSeerUtils.boolToString(cbenabled.isSelected()),
                BlueSeerUtils.boolToString(cbdefault.isSelected()));
        return x;
    } 
        
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemMClassBrowseUtil(luinput.getText(),0, "it_item");
        } else {
         luModel = DTData.getItemMClassBrowseUtil(luinput.getText(),0, "it_desc");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), ""});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getGlobalColumnTag("description")); 
        
        
        
    }

    public void lookUpFrameBOM() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getBomBrowseUtil(luinput.getText(),0, "bom_id", tbkey.getText());
        } else {
         luModel = DTData.getBomBrowseUtil(luinput.getText(),0, "bom_desc", tbkey.getText());   
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
                initvars(new String[]{tbkey.getText(),target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblbomid", this.getClass().getSimpleName()), getClassLabelTag("lblbomdesc", this.getClass().getSimpleName())); 
        
        
        
    }

    public String[] updateForm(String key) {
     
      site = OVData.getDefaultSite();
        
      // initial x could be blank because of no BOM....need to manually set with key  
      if (x.bom_item().isEmpty()) {
          tbkey.setText(key);
          getOPs(key);
          parent = key;
          tblotsize.setText(invData.getItemLotSize(key));
          ddcomp.removeItem(key);  // remove parent from component list
      }  else {
          tbkey.setText(x.bom_item());
          getOPs(x.bom_item());
          parent = x.bom_item();
          tblotsize.setText(invData.getItemLotSize(x.bom_item()));
          ddcomp.removeItem(tbkey.getText());  // remove parent from component list
          tbbomid.setText(BomID);
          getComponents(x.bom_item(), BomID);
          bind_tree(x.bom_item(), BomID);
          callSimulateCost();
          getCostSets(x.bom_item());
          cbdefault.setSelected(BlueSeerUtils.ConvertStringToBool(x.bom_primary()));
          cbenabled.setSelected(BlueSeerUtils.ConvertStringToBool(x.bom_enabled()));
          tbbomdesc.setText(x.bom_desc());
          
      }
      
        
       setAction(x.m());
       
      return x.m();
    }
    
    public void updateFormAddUpdate() {
        bind_tree(tbkey.getText(), tbbomid.getText());
        getComponents(tbkey.getText(), tbbomid.getText());
        callSimulateCost();
        updateCurrentItemCost(tbkey.getText());
        getCostSets(tbkey.getText().toString());
      /*  
        String[] message = new String[2];
        message[0] = "";
        message[1] = "";
        message = x.m();
       boolean hasRouting =  getRouting(x.bom_item());
        if (! hasRouting) {
          message[0] = "-1";
          message[1] = "No BOM found";
        } 
        */
       setAction(x.m());
        
    }
    
    
    public void getCostSets(String parent) {
       
        tbparentcostSTD.setText(String.valueOf(bsFormatDouble5(invData.getItemCost(parent, "STANDARD", OVData.getDefaultSite()))));
             
        calcCost cur = new calcCost();
       
        ArrayList<Double> costlist = new ArrayList<Double>();
        costlist = cur.getTotalCost(parent, tbbomid.getText());
        
        
        
      //  double current = costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4);
        tbparentcostCUR.setText(bsFormatDouble5(costlist.get(0) + costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4)));
        tbtotoperational.setText(bsFormatDouble5(costlist.get(1) + costlist.get(2) + costlist.get(3) + costlist.get(4)));
     //   double standard = Double.valueOf(tbparentcostSTD.getText());
         if (! tbparentcostCUR.getText().equals(tbparentcostSTD.getText())) {
                 tbparentcostCUR.setBackground(Color.green);
                 tbparentcostSTD.setBackground(Color.yellow);
             } else {
                tbparentcostCUR.setBackground(Color.green); 
                tbparentcostSTD.setBackground(Color.green);
             }
     
    }
    
    public void callSimulateCost() {
         
        double pph = 0.00;
        double pps = 0.00;
        double pphsim = 0.00;
        double ppssim = 0.00;
        
        double runratesim = 0.00;
        double setupratesim = 0.00;
        double burdenratesim = 0.00;
        double crewsizesim = 0.00;
        double setupsizesim = 0.00;
        double lotsizesim = 0.00;
        
        if (! tbpphsim.getText().isEmpty())
        pphsim = bsParseDouble(tbpphsim.getText());
        
        if (! tbppssim.getText().isEmpty())
        ppssim = bsParseDouble(tbppssim.getText());
        
        if (! tbrunratesim.getText().isEmpty())
        runratesim = bsParseDouble(tbrunratesim.getText());
        if (! tbsetupratesim.getText().isEmpty())
        setupratesim = bsParseDouble(tbsetupratesim.getText());
        if (! tbburdenratesim.getText().isEmpty())
        burdenratesim = bsParseDouble(tbburdenratesim.getText());
        if (! tbcrewsizesim.getText().isEmpty())
        crewsizesim = bsParseDouble(tbcrewsizesim.getText());
        if (! tbsetupsizesim.getText().isEmpty())
        setupsizesim = bsParseDouble(tbsetupsizesim.getText());
        if (! tblotsize.getText().isEmpty())
        lotsizesim = bsParseDouble(tblotsize.getText());
        
        
        if (pphsim != 0) {
            pph = 1 / pphsim;
        }
        if (ppssim != 0) {
            pps = 1 / ppssim;
        }
        Object o = jTree1.getLastSelectedPathComponent();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
      
        double totalcost = 0.00;
        if (node != null) {
            
        totalcost = OVData.simulateCost("", 
                tbkey.getText(), 
                node.toString(), 
                runratesim, 
                setupratesim,
                burdenratesim,
                pph,
                pps,
                crewsizesim,
                setupsizesim,
                lotsizesim,
                false
        );
        }
       // bsmf.MainFrame.show(String.valueOf(totalcost));
        // now add material
        double matl = 0.00;
        for (int j = 0; j < matltable.getRowCount(); j++) {
             matl = matl + (bsParseDouble(matltable.getValueAt(j, 2).toString()) * bsParseDouble(matltable.getValueAt(j, 4).toString()) ); 
         }
        tbtotoperationalsim.setText(String.valueOf(bsFormatDouble5(totalcost)));
        totalcost += matl;
        tbtotmaterialsim.setText(String.valueOf(bsFormatDouble5(matl)));
        double ovhout = getItemOvhCost(tbkey.getText()) + getItemOutCost(tbkey.getText());
        totalcost += ovhout;
        tbovhout.setText(String.valueOf(bsFormatDouble5(ovhout)));
        tbparentcostsim.setText(String.valueOf(bsFormatDouble5(totalcost)));
                
    }
    
    public boolean getRouting(String item) {
        boolean hasRouting = false;
        String routing = "";
        routing = invData.getItemRouting(item);
        hasRouting = OVData.isValidRouting(routing);
        return hasRouting;
    }
    
    
    public void getOPs(String parent) {
        ddop.removeAllItems();
         ArrayList<String> ops = new ArrayList<String>();
              ops = OVData.getOperationsByItem(parent);
               for (int i = 0; i < ops.size(); i++) {
                ddop.addItem(ops.get(i));
               }
    }
    
    public static Integer level(TreeNode tree, TreeNode target){
    return level(tree, target, 0);
}

    public static Integer level(TreeNode tree, TreeNode target, int currentLevel) {
    Integer returnLevel = -1;        
    if(tree.toString().equals(target.toString())) {
        returnLevel = currentLevel;
    } else {
         Enumeration enumeration = tree.children();
        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            if((returnLevel = level((TreeNode)object, target, currentLevel + 1)) != -1){
                break;
            }                
        }
    }
    return returnLevel;

}
    
    public void getComponents(String parent, String bomid) {
        
         
       String site = invData.getItemSite(parent);
       
       double matlcost = 0.00;
       calcCost cur = new calcCost();
       matlcost = cur.getMtlCost(parent);
       
       tbtotmaterial.setText(String.valueOf(matlcost));
        try {
           Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                int i = 0;

                
                matlmodel.setRowCount(0);
                matltable.setModel(matlmodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT ps_child, ps_qty_per, ps_type, ps_op, a.itc_total as 'a.itc_total', b.itc_total as 'b.itc_total' " +
                        " FROM  pbm_mstr  " +
                        " left outer join item_cost a on a.itc_item = ps_child and a.itc_set = 'standard' and a.itc_site = " + "'" + site + "'" +
                        " left outer join item_cost b on b.itc_item = ps_child and b.itc_set = 'current' and b.itc_site = " + "'" + site + "'" +
                        " where ps_parent = " + "'" + parent + "'" + 
                        " and ps_bom = " + "'" + bomid + "'" +        
                        " order by ps_child ;");

                while (res.next()) {
                    i++;
                  //  matlcost += res.getDouble("ps_qty_per") * res.getDouble("itc_total");
                    matlmodel.addRow(new Object[]{
                                res.getString("ps_child"),
                                res.getString("ps_op"),
                                res.getDouble("ps_qty_per"),
                                res.getDouble("b.itc_total"),
                                res.getDouble("a.itc_total")
                            });
              
                }
                res.close();
               tbtotmaterial.setText(String.valueOf(bsFormatDouble5(matlcost)));
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
    
    public void getComponentDetail(String component) {
       
        tbcomptype.setText("");
        lblcomp.setText("");
        tbcompcost.setText("");
         
         try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                boolean proceed = true;
                int i = 0;
                String type = "";
                
           res = st.executeQuery("SELECT it_item, it_desc, it_code, itc_total from item_mstr left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                   " where it_item = " + "'" + 
                            ddcomp.getSelectedItem().toString() + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        tbcomptype.setText(res.getString("it_code"));
                        lblcomp.setText(res.getString("it_desc"));
                        tbcompcost.setText(String.valueOf(bsFormatDouble5(res.getDouble("itc_total"))));
                        
                    }
                   
             } catch (SQLException s) {
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                MainFrame.bslog(s);
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
   
    
    public void setcomponentattributes(String parent, String component, String op, String bomid) {
       
          tbqtyper.setText("");
          tbref.setText("");
        
        try {

          Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                boolean proceed = true;
                int i = 0;
                String type = "";
           res = st.executeQuery("SELECT ps_parent, ps_child, ps_op, ps_qty_per, it_desc, it_uom,  ps_ref FROM  pbm_mstr " +
                   " inner join item_mstr on it_item = ps_child " +
                   " where ps_parent = " + "'" + parent + "'" + 
                   " AND ps_child = " + "'" + component + "'" + 
                   " AND ps_op = " + "'" + op + "'" +
                   " AND ps_bom = " + "'" + bomid + "'" +        
                   ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        ddop.setSelectedItem(res.getString("ps_op"));
                        tbqtyper.setText(res.getString("ps_qty_per"));
                        tbref.setText(res.getString("ps_ref"));
                        ddcomp.setSelectedItem(res.getString("ps_child"));
                        
                        // set update button 
                        
                    }
                    if (i == 0) {
                        btadd.setEnabled(true);
                        btpdf.setEnabled(false);
                    } else {
                        btadd.setEnabled(true);
                        btpdf.setEnabled(true);
                    }
                    
              res = st.executeQuery("SELECT it_item, it_desc, it_code, itc_total from item_mstr left outer join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                   " where it_item = " + "'" + 
                            component + "'" + ";");
                    i = 0;
                    while (res.next()) {
                        i++;
                        tbcomptype.setText(res.getString("it_code"));
                        lblcomp.setText(res.getString("it_desc"));
                        tbcompcost.setText(String.valueOf(bsFormatDouble5(res.getDouble("itc_total"))));
                        
                    }       
                    
                    
             } catch (SQLException s) {
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                MainFrame.bslog(s);
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
    
    public void bind_tree(String parentpart, String bomid) {
      //  jTree1.setModel(null);
       
       // DefaultMutableTreeNode mynode = OVData.get_nodes_without_op(parentpart);
       DefaultMutableTreeNode mynode = OVData.get_op_nodes_new(parentpart, bomid);
      
       DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
        model.setRoot(mynode);
        
        jTree1.setVisible(true);
        // expand entire tree
        /*
        for (int i = 0; i < jTree1.getRowCount(); i++) {
        jTree1.expandRow(i);
        }
        */
        model.nodeChanged(mynode);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbqtyper = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ddcomp = new javax.swing.JComboBox();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        ddop = new javax.swing.JComboBox<>();
        lblcomp = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btpdf = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbcompcost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbcomptype = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        tbsetuprate = new javax.swing.JTextField();
        tbrunratesim = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsetupratesim = new javax.swing.JTextField();
        tbsetupsize = new javax.swing.JTextField();
        tbrunrate = new javax.swing.JTextField();
        tbsetupsizesim = new javax.swing.JTextField();
        tbcrewsizesim = new javax.swing.JTextField();
        tbburdenratesim = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbburdenrate = new javax.swing.JTextField();
        tbcrewsize = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbpph = new javax.swing.JTextField();
        tbpps = new javax.swing.JTextField();
        tblotsize = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tbpphsim = new javax.swing.JTextField();
        tbppssim = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        matltable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        tbparentcostSTD = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbtotoperational = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        tbtotoperationalsim = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        tbovhout = new javax.swing.JTextField();
        tbparentcostCUR = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        tbtotmaterial = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tbtotmaterialsim = new javax.swing.JTextField();
        tbparentcostsim = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        btroll = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        tbbomid = new javax.swing.JTextField();
        btlookupbom = new javax.swing.JButton();
        btnewbom = new javax.swing.JButton();
        tbbomdesc = new javax.swing.JTextField();
        cbdefault = new javax.swing.JCheckBox();
        cbenabled = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("BOM Maintenance"));
        jPanel2.setName("BOM Maintenance"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(1171, 620));

        jLabel3.setText("Qty Per");
        jLabel3.setName("lblqtyper"); // NOI18N

        jLabel2.setText("Component");
        jLabel2.setName("lblcomponent"); // NOI18N

        jLabel5.setText("Reference");
        jLabel5.setName("lblref"); // NOI18N

        tbqtyper.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyperFocusLost(evt);
            }
        });

        jLabel1.setText("Parent Item");
        jLabel1.setName("lblitem"); // NOI18N

        ddcomp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcompActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel4.setText("Operation");
        jLabel4.setName("lbloperation"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        tbkey.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbkeyFocusLost(evt);
            }
        });
        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btclear.setText("clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btpdf.setText("PDF");
        btpdf.setName("btpdf"); // NOI18N
        btpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpdfActionPerformed(evt);
            }
        });

        jLabel7.setText("BOM ID");
        jLabel7.setName("lblbomid"); // NOI18N

        jLabel8.setText("BOM Desc");
        jLabel8.setName("lblbomdesc"); // NOI18N

        jLabel6.setText("Comp Cost:");
        jLabel6.setName("lblcompcost"); // NOI18N

        jLabel9.setText("Comp Type:");
        jLabel9.setName("lblcomptype"); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Operational WorkCell/Dept"));
        jPanel5.setName("panelop"); // NOI18N

        tbrunratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrunratesimFocusLost(evt);
            }
        });

        jLabel12.setText("BurdenRate");
        jLabel12.setName("lblburdenrate"); // NOI18N

        tbsetupratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetupratesimFocusLost(evt);
            }
        });

        tbsetupsizesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetupsizesimFocusLost(evt);
            }
        });

        tbcrewsizesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcrewsizesimFocusLost(evt);
            }
        });

        tbburdenratesim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbburdenratesimFocusLost(evt);
            }
        });

        jLabel10.setText("RunRate");
        jLabel10.setName("lblrunrate"); // NOI18N

        jLabel11.setText("SetupRate");
        jLabel11.setName("lblsetuprate"); // NOI18N

        jLabel14.setText("Simulation");
        jLabel14.setName("lblsimulation"); // NOI18N

        jLabel15.setText("Production");
        jLabel15.setName("lblproduction"); // NOI18N

        jLabel19.setText("CrewSize");
        jLabel19.setName("lblcrewsize"); // NOI18N

        jLabel20.setText("SetupSize");
        jLabel20.setName("lblsetupsize"); // NOI18N

        jLabel13.setText("pcs/HR");
        jLabel13.setName("lblpcshour"); // NOI18N

        jLabel17.setText("Setup");
        jLabel17.setName("lblsetup"); // NOI18N

        jLabel18.setText("LotSize");
        jLabel18.setName("lbllotsize"); // NOI18N

        tbpphsim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpphsimFocusLost(evt);
            }
        });

        tbppssim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbppssimFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel15)
                .addGap(118, 118, 118))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel13)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbcrewsize, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(tbburdenrate)
                    .addComponent(tbrunrate)
                    .addComponent(tbsetuprate)
                    .addComponent(tbsetupsize, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbpph)
                    .addComponent(tbpps)
                    .addComponent(tblotsize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbppssim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbpphsim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsetupsizesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbcrewsizesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbburdenratesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbrunratesim, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbsetupratesim, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrunrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tbrunratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsetuprate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsetupratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbburdenrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbburdenratesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcrewsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsetupsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbsetupsizesim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(tbpphsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(tbppssim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        matltable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(matltable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
        );

        jLabel22.setText("Sim Cost:");
        jLabel22.setName("lblsimcost"); // NOI18N

        jLabel23.setText("Material:");
        jLabel23.setName("lblmaterial"); // NOI18N

        jLabel25.setText("Sim Mat:");
        jLabel25.setName("lblsimmat"); // NOI18N

        jLabel16.setText("Current Cost:");
        jLabel16.setName("lblcurrentcost"); // NOI18N

        jLabel24.setText("Operational:");
        jLabel24.setName("lbloperational"); // NOI18N

        jLabel26.setText("Sim Oper:");
        jLabel26.setName("lblsimoper"); // NOI18N

        jLabel21.setText("Default BOM:");
        jLabel21.setName("lblstandardcost"); // NOI18N

        jLabel27.setText("Ovh/Out:");
        jLabel27.setName("lblovhout"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbparentcostCUR)
                            .addComponent(tbparentcostSTD, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbtotmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtotoperational, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbovhout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotmaterialsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotoperationalsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbparentcostsim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtotmaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23)
                        .addComponent(tbtotmaterialsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotoperational, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(tbtotoperationalsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbparentcostCUR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(tbovhout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbparentcostSTD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(tbparentcostsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addContainerGap())
        );

        btroll.setText("Roll Cost");
        btroll.setName("btroll"); // NOI18N
        btroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrollActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btlookupbom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupbom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupbomActionPerformed(evt);
            }
        });

        btnewbom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnewbom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewbomActionPerformed(evt);
            }
        });

        cbdefault.setText("default");

        cbenabled.setText("enabled");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddcomp, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblcomp, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbkey, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                            .addComponent(tbqtyper, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbref, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcompcost, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcomptype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbbomdesc, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbbomid, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookupbom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnewbom, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbdefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbenabled)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 35, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btroll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btpdf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addGap(140, 140, 140))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btlookup)
                            .addComponent(btclear))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btlookupbom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbdefault)
                                .addComponent(cbenabled))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbbomid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addComponent(btnewbom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(tbbomdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddcomp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addComponent(lblcomp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqtyper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcompcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcomptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(btpdf)
                    .addComponent(btroll))
                .addContainerGap())
        );

        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N

        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 46, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(76, Short.MAX_VALUE))))
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText(), ""});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
    if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete" ,new String[]{tbkey.getText(), ""});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText(), ""});
    }//GEN-LAST:event_btupdateActionPerformed

    private void tbkeyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbkeyFocusLost
      /*
        if (OVData.isValidItem(tbpart.getText())) {
            getComponents(tbpart.getText());
            getOPs(tbpart.getText());
            bind_tree(tbpart.getText());
            lblparent.setText(OVData.getItemDesc(tbpart.getText()));
            } else {
                lblparent.setText("Invalid Parent Part");
                tbpart.requestFocus();
            }
        */
    }//GEN-LAST:event_tbkeyFocusLost

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
     //  bsmf.MainFrame.show("select:" + jTree1.getLastSelectedPathComponent().toString());
     //   bsmf.MainFrame.show("root:" + jTree1.getModel().getRoot());
     //   bsmf.MainFrame.show("isLeaf:" + jTree1.getModel().isLeaf(jTree1.getLastSelectedPathComponent()));
         
    //    Integer level = level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)jTree1.getLastSelectedPathComponent());
    //    bsmf.MainFrame.show("Level:" + level);
     
     
            tbrunrate.setText("");
            tbsetuprate.setText("");
            tbburdenrate.setText("");
            tbcrewsize.setText("");
            tbsetupsize.setText("");
            tbpph.setText("");
            tbpps.setText("");
            
            tbrunratesim.setText("");
            tbsetupratesim.setText("");
            tbburdenratesim.setText("");
            tbcrewsizesim.setText("");
            tbsetupsizesim.setText("");
            tbpphsim.setText("");
            tbppssim.setText("");
            
             
            
       Object o = jTree1.getLastSelectedPathComponent();
       if (o != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
      //  bsmf.MainFrame.show(node.getLevel() + "/" + node.getDepth());
        // let's lookup Operation elements if parent operation is clicked on
        if (parent != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 1) && (parent.toString().equals(tbkey.getText()))) {
            String[] e = OVData.getBOMParentOpElements(parent.toString(), node.toString());
            if (e != null) {
                double pph = Double.valueOf(e[5]);
                pph = 1 / pph;
                double pps = Double.valueOf(e[6]);
                
                if (pps != 0) 
                pps = 1 / pps;
                
                
                tbrunrate.setText(String.valueOf(currformatDouble(Double.valueOf(e[0]))));
                tbsetuprate.setText(String.valueOf(currformatDouble(Double.valueOf(e[1]))));
                tbburdenrate.setText(String.valueOf(currformatDouble(Double.valueOf(e[2]))));
                tbcrewsize.setText(e[3]);
                tbsetupsize.setText(e[4]);
                tbpph.setText(String.valueOf(bsFormatDouble(pph)));
                tbpps.setText(String.valueOf(bsFormatDouble(pps)));
                
                // init simulation
                 tbrunratesim.setText(String.valueOf(currformatDouble(Double.valueOf(e[0]))));
                tbsetupratesim.setText(String.valueOf(currformatDouble(Double.valueOf(e[1]))));
                tbburdenratesim.setText(String.valueOf(currformatDouble(Double.valueOf(e[2]))));
                tbcrewsizesim.setText(e[3]);
                tbsetupsizesim.setText(e[4]);
                tbpphsim.setText(String.valueOf(bsFormatDouble(pph)));
                tbppssim.setText(String.valueOf(bsFormatDouble(pps)));
                
                callSimulateCost();
            }
        }
        
         // let's set child selection fields and enable update/delete for immediate children only
      //  if (parent != null && (level((TreeNode)jTree1.getModel().getRoot(), (TreeNode)o) == 2) && (parent.toString().equals(tbkey.getText())) ) {
          if (parent != null && node.getLevel() == 2) {
           btupdate.setEnabled(true);
           btdelete.setEnabled(true);
           setcomponentattributes(tbkey.getText(), node.toString(), parent.toString(), tbbomid.getText());
        } else {
           btupdate.setEnabled(false);
           btdelete.setEnabled(false); 
        }
        
        
       }
       
     
       /*
       DefaultMutableTreeNode node = null;
       DefaultMutableTreeNode parent = null;
         Object o = jTree1.getLastSelectedPathComponent();
         
        if (o != null ) {
        node = (DefaultMutableTreeNode)o;
            if (! node.isRoot()) {
                if (node != null) {
                parent = (DefaultMutableTreeNode)node.getParent();
                }
               // bsmf.MainFrame.show(String.valueOf(parent.getLevel()));
                if (parent.getLevel() == 1 && parent != null) {
                setcomponentattributes(node.toString());
                }
            }
         }
       */
    }//GEN-LAST:event_jTree1ValueChanged

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText(), ""});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpdfActionPerformed
       if (! tbkey.getText().isEmpty())
        OVData.printBOMJasper(tbkey.getText(), tbbomid.getText(), tbtotmaterial.getText(), tbtotoperational.getText(), tbparentcostCUR.getText()); 
    }//GEN-LAST:event_btpdfActionPerformed

    private void ddcompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcompActionPerformed
         if (ddcomp.getSelectedItem() != null && ! isLoad)
            getComponentDetail(ddcomp.getSelectedItem().toString());
    }//GEN-LAST:event_ddcompActionPerformed

    private void tbrunratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrunratesimFocusLost
             String x = BlueSeerUtils.bsformat("", tbrunratesim.getText(), "2");
        if (x.equals("error")) {
            tbrunratesim.setText("");
            tbrunratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbrunratesim.requestFocus();
        } else {
            tbrunratesim.setText(x);
            tbrunratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbrunratesimFocusLost

    private void tbsetupratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetupratesimFocusLost
              String x = BlueSeerUtils.bsformat("", tbsetupratesim.getText(), "2");
        if (x.equals("error")) {
            tbsetupratesim.setText("");
            tbsetupratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbsetupratesim.requestFocus();
        } else {
            tbsetupratesim.setText(x);
            tbsetupratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbsetupratesimFocusLost

    private void tbburdenratesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbburdenratesimFocusLost
              String x = BlueSeerUtils.bsformat("", tbburdenratesim.getText(), "2");
        if (x.equals("error")) {
            tbburdenratesim.setText("");
            tbburdenratesim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbburdenratesim.requestFocus();
        } else {
            tbburdenratesim.setText(x);
            tbburdenratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbburdenratesimFocusLost

    private void tbcrewsizesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcrewsizesimFocusLost
        String x = BlueSeerUtils.bsformat("", tbcrewsizesim.getText(), "2");
        if (x.equals("error")) {
            tbcrewsizesim.setText("");
            tbcrewsizesim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbcrewsizesim.requestFocus();
        } else {
            tbcrewsizesim.setText(x);
            tbburdenratesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbcrewsizesimFocusLost

    private void tbsetupsizesimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetupsizesimFocusLost
        String x = BlueSeerUtils.bsformat("", tbsetupsizesim.getText(), "2");
        if (x.equals("error")) {
            tbsetupsizesim.setText("");
            tbsetupsizesim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbsetupsizesim.requestFocus();
        } else {
            tbsetupsizesim.setText(x);
            tbsetupsizesim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbsetupsizesimFocusLost

    private void tbpphsimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpphsimFocusLost
        String x = BlueSeerUtils.bsformat("", tbpphsim.getText(), "2");
        if (x.equals("error")) {
            tbpphsim.setText("");
            tbpphsim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbpphsim.requestFocus();
        } else {
            tbpphsim.setText(x);
            tbpphsim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbpphsimFocusLost

    private void tbppssimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbppssimFocusLost
         String x = BlueSeerUtils.bsformat("", tbppssim.getText(), "2");
        if (x.equals("error")) {
            tbppssim.setText("");
            tbppssim.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbppssim.requestFocus();
        } else {
            tbppssim.setText(x);
            tbppssim.setBackground(Color.white);
            callSimulateCost();
        }
    }//GEN-LAST:event_tbppssimFocusLost

    private void btrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrollActionPerformed
       OVData.setStandardCosts(site, parent);
       getCostSets(parent);
    }//GEN-LAST:event_btrollActionPerformed

    private void tbqtyperFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyperFocusLost
         String x = BlueSeerUtils.bsformat("", tbqtyper.getText(), "5");
        if (x.equals("error")) {
            tbqtyper.setText("");
            tbqtyper.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqtyper.requestFocus();
        } else {
            tbqtyper.setText(x);
            tbqtyper.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyperFocusLost

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btlookupbomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupbomActionPerformed
        lookUpFrameBOM();
    }//GEN-LAST:event_btlookupbomActionPerformed

    private void btnewbomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewbomActionPerformed
        newbomid = true;
        btnewbom.setEnabled(false);
        btlookupbom.setEnabled(false);
        btadd.setEnabled(true);
        tbbomid.setEnabled(true);
        if (! bomexist) {
            // must be first BOM...use item number as default bom id
            tbbomid.setText(tbkey.getText());
            cbdefault.setSelected(true);
            cbenabled.setSelected(true);
        } else {
            tbbomid.setText("");
            cbenabled.setSelected(true);
            cbdefault.setSelected(false);
            bsmf.MainFrame.show(getMessageTag(1167));
            tbbomid.requestFocus();
        }
    }//GEN-LAST:event_btnewbomActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupbom;
    private javax.swing.JButton btnewbom;
    private javax.swing.JButton btpdf;
    private javax.swing.JButton btroll;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbdefault;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox ddcomp;
    private javax.swing.JComboBox<String> ddop;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblcomp;
    private javax.swing.JTable matltable;
    private javax.swing.JTextField tbbomdesc;
    private javax.swing.JTextField tbbomid;
    private javax.swing.JTextField tbburdenrate;
    private javax.swing.JTextField tbburdenratesim;
    private javax.swing.JTextField tbcompcost;
    private javax.swing.JTextField tbcomptype;
    private javax.swing.JTextField tbcrewsize;
    private javax.swing.JTextField tbcrewsizesim;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tblotsize;
    private javax.swing.JTextField tbovhout;
    private javax.swing.JTextField tbparentcostCUR;
    private javax.swing.JTextField tbparentcostSTD;
    private javax.swing.JTextField tbparentcostsim;
    private javax.swing.JTextField tbpph;
    private javax.swing.JTextField tbpphsim;
    private javax.swing.JTextField tbpps;
    private javax.swing.JTextField tbppssim;
    private javax.swing.JTextField tbqtyper;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrunrate;
    private javax.swing.JTextField tbrunratesim;
    private javax.swing.JTextField tbsetuprate;
    private javax.swing.JTextField tbsetupratesim;
    private javax.swing.JTextField tbsetupsize;
    private javax.swing.JTextField tbsetupsizesim;
    private javax.swing.JTextField tbtotmaterial;
    private javax.swing.JTextField tbtotmaterialsim;
    private javax.swing.JTextField tbtotoperational;
    private javax.swing.JTextField tbtotoperationalsim;
    // End of variables declaration//GEN-END:variables
}
