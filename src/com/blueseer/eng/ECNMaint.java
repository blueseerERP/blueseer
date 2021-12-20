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

package com.blueseer.eng;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.eng.engData.addECNTransaction;
import static com.blueseer.eng.engData.deleteECNMstr;
import com.blueseer.eng.engData.ecn_mstr;
import com.blueseer.eng.engData.ecn_task;
import static com.blueseer.eng.engData.getECNMstr;
import static com.blueseer.eng.engData.getECNSequences;
import static com.blueseer.eng.engData.getECNTask;
import static com.blueseer.eng.engData.updateECNTransaction;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author vaughnte
 */
public class ECNMaint extends javax.swing.JPanel implements IBlueSeerT  {
  
     
    // global variable declarations
                boolean isLoad = false;
    
    // global datatablemodel declarations     
     javax.swing.table.DefaultTableModel taskmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("sequence"), 
                getGlobalColumnTag("owner"), 
                getGlobalColumnTag("task"), 
                getGlobalColumnTag("assigndate"), 
                getGlobalColumnTag("compdate"), 
                getGlobalColumnTag("status")
            });
      
      
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
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            if (taskmodel.getRowCount() > 0) {
            if (tasktable.getModel().getValueAt(row, 5).toString().compareTo(getGlobalProgTag("complete")) == 0) {
            setBackground(Color.green);
            //setEnabled(false);
            }
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private String columnname;
        private int myrow;
        private int mycol;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            columnname = String.valueOf(column);
            button.setText(label);
            //button.setText("approve");
            
            isPushed = true;
           
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // 
                // 
               //JOptionPane.showMessageDialog(button, columnname + ":" + label );
                // System.out.println(label + ": Ouch!");
              //  if ((jTable1.getColumnName(0).toString().compareTo("Shipper")) == 0) {
                    // approvereq(label);
              //  }
                myrow = tasktable.getSelectedRow();
                if (tasktable.getModel().getValueAt(myrow, 5).toString().compareTo(getGlobalProgTag("pending")) == 0) {
                    if (tasktable.getModel().getValueAt(myrow, 1).toString().compareTo(bsmf.MainFrame.userid) == 0) {
                      completetask(tbkey.getText(), tasktable.getValueAt(myrow, 0).toString());
                    } else {
                      bsmf.MainFrame.show(getMessageTag(1112));  
                    }
                    //bsmf.MainFrame.show(jTable1.getValueAt(myrow, 1).toString());
                }
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    
    class PopupHandler implements ActionListener,
                              PopupMenuListener {
    JTree tree;
    JPopupMenu popup;
    JMenuItem item;
    boolean overRoot = false;
    Point loc;
 
    public PopupHandler(JTree tree) {
        this.tree = tree;
        popup = new JPopupMenu();
        popup.setInvoker(tree);
        JMenu menu = new JMenu("Action");
        popup.add(menu);
        menu.add(getMenuItem("add pallet"));
        menu.add(getMenuItem("add item"));
        menu.add(getMenuItem("add container"));
        menu.add(getMenuItem("add container + item"));
        menu.add(getMenuItem("describe"));
        
        menu.add(item = getMenuItem("add file"));
        tree.addMouseListener(ma);
        popup.addPopupMenuListener(this);
    }
 
    private JMenuItem getMenuItem(String s) {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s.toUpperCase());
        menuItem.addActionListener(this);
        return menuItem;
    }
 
    public JPopupMenu getPopup() {
        return popup;
    }
 
      public File getfile() {
        final JFileChooser fc = new JFileChooser();
        File file = null;
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this.tree);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            File srcDir = new File(SourceDir);
            File destDir = new File("/home/vaughnte/temp");
          //   FileUtils.copyDirectoryToDirectory(srcDir, destDir);
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
           
        } else {
           System.out.println("cancelled");
        }
        return file;
    }
    
     private class PartInfo {
        public String partname;
        public String partqty;
 
        public PartInfo(String part, String qty) {
            partname = part;
            partqty = qty;
           
        }
 
        public String toString() {
            return partname;
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        TreePath path  = tree.getPathForLocation(loc.x, loc.y);
        //System.out.println("path = " + path);
        //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
        
       String type = "";
          DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)path.getLastPathComponent();
         if (! node.getAllowsChildren()) {
             type = "leaf";
             bsmf.MainFrame.show("cant add to file");
             //return;
         }
         
         if (node.isRoot())
              type = "root";
         
         if (tree.getModel().getChildCount(node) > 0) {
              bsmf.MainFrame.show(String.valueOf(tree.getModel().getChildCount(node)) + "/" + type);
         } 
         
        
         
        
        if(ac.equals("ADD PALLET")) {
            File myfile = getfile();
            addContainer(path, myfile);
        }
            
        if(ac.equals("ADD ITEM")) {
          addItem(path);
        }
        if(ac.equals("DESCRIBE")) {
             Object nodeInfo = node.getUserObject();
             PartInfo line = (PartInfo)nodeInfo;
          bsmf.MainFrame.show(line.partname + " " + line.partqty);
        }
        
       
    }
 
    private void addContainer(TreePath path, File myfile) {
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)path.getLastPathComponent();
       
        int count = parent.getChildCount();
        DefaultMutableTreeNode child =
            new DefaultMutableTreeNode(myfile.getName());
       
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.insertNodeInto(child, parent, count);
    }
 
    private void addItem(TreePath path) {
        String myitem = "";
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)path.getLastPathComponent();
        int[] rows = tasktable.getSelectedRows();
        for (int i : rows) {
          int count = parent.getChildCount();
          DefaultMutableTreeNode child =
          new DefaultMutableTreeNode(new PartInfo(tasktable.getModel().getValueAt(i, 0).toString(),
                                       tasktable.getModel().getValueAt(i, 3).toString()));
                                       
        child.setAllowsChildren(false);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.insertNodeInto(child, parent, count);
        }
        
       //  DefaultMutableTreeNode parent =
        //    (DefaultMutableTreeNode)path.getLastPathComponent();
        
        //int count = parent.getChildCount();
       // DefaultMutableTreeNode child =
        //    new DefaultMutableTreeNode(myitem);
      //  child.setAllowsChildren(false);
      //  DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      //  model.insertNodeInto(child, parent, count);
    }
 
    private MouseListener ma = new MouseAdapter() {
        private void checkForPopup(MouseEvent e) {
            if(e.isPopupTrigger()) {
                loc = e.getPoint();
                TreePath path  = tree.getPathForLocation(loc.x, loc.y);
                //System.out.printf("path = %s%n", path);
                if(path == null) {
                    e.consume();
                    return;
                }
                TreeNode root = (TreeNode)tree.getModel().getRoot();;
                overRoot = path.getLastPathComponent() == root;
                popup.show(tree, loc.x, loc.y);
            }
        }
 
        public void mousePressed(MouseEvent e)  { checkForPopup(e); }
        public void mouseReleased(MouseEvent e) { checkForPopup(e); }
        public void mouseClicked(MouseEvent e)  { checkForPopup(e); }
    };
  
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        item.setVisible(!overRoot);
    }
 
    public void popupMenuCanceled(PopupMenuEvent e) {}
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
}
    
    class SelectionListener implements TreeSelectionListener {
  public void valueChanged(TreeSelectionEvent se) {
    JTree tree = (JTree) se.getSource();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
        .getLastSelectedPathComponent();
        if (node.isLeaf() &&  node.getParent() != null) {
      Object nodeInfo = node.getUserObject();
             PopupHandler.PartInfo line = (PopupHandler.PartInfo)nodeInfo;
       //   tbpart.setText(line.partname);
       //   tbqty.setText(line.partqty);
    }
  }
}
    
    
    public ECNMaint() {
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
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get") && message[0].equals("1")) {
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
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
        tbkey.setText("");
         java.util.Date now = new java.util.Date();
        TreeScrollPanel.setVisible(false);  
        tree.setVisible(false);
        messglbl.setText("");
        tanotes.setText("");
        tbkey.setText("");  
        tbdrawing.setText("");
        tbdatecreate.setText("");
        
        if (ddengineer.getItemCount() > 0)
        ddengineer.setSelectedIndex(0);
        
        if (ddtask.getItemCount() > 0)
        ddtask.setSelectedIndex(0);
        
        if (ddpart.getItemCount() > 0)
        ddpart.setSelectedIndex(0);
                
        ddstatus.removeAllItems();
        ddstatus.addItem(getGlobalProgTag("open"));
        ddstatus.addItem(getGlobalProgTag("closed"));
        ddstatus.addItem(getGlobalProgTag("cancel"));
        ddstatus.addItem(getGlobalProgTag("hold"));
        ddstatus.setSelectedIndex(0);
        
        dctargetdate.setDate(now);
        tbrev.setText("");
        tbcustrev.setText("");
       
        
      if(((DefaultComboBoxModel)ddstatus.getModel()).getIndexOf("new") >= 0) {
       ddstatus.removeItem("new");
      }
        
        
        taskmodel.setRowCount(0);
        tasktable.setModel(taskmodel);
        tasktable.getColumn(getGlobalColumnTag("sequence")).setCellRenderer(new ButtonRenderer());
        tasktable.getColumn(getGlobalColumnTag("sequence")).setCellEditor(
                        new ButtonEditor(new JCheckBox()));
        
        
       
        ddengineer.removeAllItems();
        ddengineer.insertItemAt("", 0);
        ddengineer.setSelectedIndex(0);
        ArrayList poc = OVData.getusermstrlist();
        for (int i = 0; i < poc.size(); i++) {
            ddengineer.addItem(poc.get(i));
        }
        
        ddtask.removeAllItems();
        ddtask.insertItemAt("", 0);
        ddtask.setSelectedIndex(0);
        ArrayList task = OVData.getTaskMasterList();
        for (int i = 0; i < task.size(); i++) {
            ddtask.addItem(task.get(i));
        }
        
        ddpart.removeAllItems();
        ddpart.insertItemAt("", 0);
        ddpart.setSelectedIndex(0);
        ArrayList item = invData.getItemMasterAlllist();
        for (int i = 0; i < item.size(); i++) {
            ddpart.addItem(item.get(i));
        }
        
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
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
        if (x[0].equals("0")) { 
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(BlueSeerUtils.dbaction x) {
        boolean b = true;
                
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
               
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
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
     String[] m = addECNTransaction(createDetRecord(), createRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
       String[] m = new String[2];
        // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getECNSequences(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < tasktable.getRowCount(); j++) {
             if (tasktable.getValueAt(j, 0).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }
        m = updateECNTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteECNMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       
        ecn_mstr x = getECNMstr(key); 
        tbkey.setText(x.ecn_nbr());
        tbdatecreate.setText(x.ecn_createdate());
        tbrev.setText(x.ecn_rev());
        tbcustrev.setText(x.ecn_custrev());
        tbdrawing.setText(x.ecn_drawing());
        ddengineer.setSelectedItem(x.ecn_poc());
        ddtask.setSelectedItem(x.ecn_mstrtask());
        ddstatus.setSelectedItem(x.ecn_status());
        ddpart.setSelectedItem(x.ecn_item());
        dctargetdate.setDate(BlueSeerUtils.parseDate(x.ecn_targetdate()));
       
        // now detail
        taskmodel.setRowCount(0);
        ArrayList<ecn_task> z = getECNTask(key[0]);
        for (ecn_task d : z) {
            taskmodel.addRow(new Object[]{d.ecnt_seq(), d.ecnt_owner(), d.ecnt_task(),
                 d.ecnt_assigndate(), d.ecnt_closedate(), d.ecnt_status()});
        }
       // getTasks(ddtask.getSelectedItem().toString());
        setAction(x.m());
        return x.m();
       
    }
    
    public ecn_mstr createRecord() { 
        ecn_mstr x = new ecn_mstr(null, 
                tbkey.getText(),
                ddengineer.getSelectedItem().toString(),
                ddtask.getSelectedItem().toString(),
                ddstatus.getSelectedItem().toString(),
                BlueSeerUtils.setDateFormat(dctargetdate.getDate()),
                tbdatecreate.getText(),
                "",
                tbdrawing.getText(),
                ddpart.getSelectedItem().toString(),
                tbrev.getText(),
                tbcustrev.getText()
                );
        return x;
    }
    
    public ArrayList<ecn_task> createDetRecord() {
        ArrayList<ecn_task> list = new ArrayList<ecn_task>();
         for (int j = 0; j < tasktable.getRowCount(); j++) {
             ecn_task x = new ecn_task(null, 
                tbkey.getText(),
                ddtask.getSelectedItem().toString(),
                tasktable.getValueAt(j, 0).toString(),
                tasktable.getValueAt(j, 1).toString(),
                tasktable.getValueAt(j, 2).toString(),
                tasktable.getValueAt(j, 3).toString(),
                tasktable.getValueAt(j, 4).toString(), 
                tasktable.getValueAt(j, 5).toString(), 
                "" // notes...this is ignored in add/update...another mechanism updates the notes field
                );
        list.add(x);
         }
        return list;   
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
         luModel = DTData.getECNBrowseUtil(luinput.getText(),0, "ecn_nbr");
        
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
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName())); 
        
        
    }

    // custom funcs
    public void setTree(String shipid) {
        DefaultMutableTreeNode topTreeNode = new DefaultMutableTreeNode(shipid);
       DefaultTreeModel treeModel = new DefaultTreeModel(topTreeNode, true);
       tree.setModel(treeModel);
       ToolTipManager.sharedInstance().registerComponent(tree);
       ECNMaint.PopupHandler handler = new ECNMaint.PopupHandler(tree);
       tree.add(handler.getPopup());
       tree.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.addTreeSelectionListener(new SelectionListener());
       tree.setVisible(true);
    }
    
    public void completetask(String myid, String thissequence) {
        
        try {
            int mypo = 0;
            int mysequence = Integer.valueOf(thissequence);
            taskmodel.setRowCount(0);
            int i = 0;
            int nextsequence = 0;
            boolean islast = false;
            boolean isEmail = false;
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            
            try {
                // OK...lets determine if last sequence
                
               res = st.executeQuery("select * from ecn_task left outer join ecn_ctrl on ecnc_email <> '' where ecnt_nbr = "
                     + "'" + myid + "'" +  " order by ecnt_seq desc ;"); 
                 while (res.next()) {
                   i++;
                   isEmail = res.getBoolean("ecnc_email");
                   if (i == 1) {
                      if (mysequence == Integer.valueOf(res.getString("ecnt_seq"))) {
                         islast = true;
                      }
                   }
                   
                   if (! islast && mysequence == i) {
                       nextsequence = i + 1;
                       break;
                   }
                 }
                i = 0;
                
             
                
                // now...lets update task and set to complete 
                 st.executeUpdate("update ecn_task set ecnt_status = " +
                         "'" + getGlobalProgTag("complete") + "'" + " where " + 
                        " ecnt_nbr = " + "'" + myid + "'" + " AND " + 
                         "ecnt_seq = " + "'" + mysequence + "'" + ";");
                 
                
                // let's get the next sequence userid for email purposes
                 if (! islast) {
                 res = st.executeQuery("select * from ecn_task inner join user_mstr on " +
                          " user_id = ecnt_owner " + " inner join ecn_mstr on " +
                          " ecnt_nbr = ecn_nbr " +
                          "where " +
                          "ecnt_nbr = " + "'" + myid + "'" + " AND " +
                          "ecnt_seq = " + "'" + nextsequence + "'" + ";");
                 while (res.next()) {
                     String subject = "ECN Notice of Action";
                     String body = "ECN number " + myid + " requires your completion";
                     String requestor = "Eng POC = " + res.getString("ecn_poc");
                     String amount = "Task = " + res.getString("ecnt_task");
                     body = body + "\n" + requestor + "\n" + amount;
                     if (! res.getString("user_email").isEmpty())
                     OVData.sendEmail(res.getString("user_email"), subject, body, "");
                 }
                 
                 }
                 
                 // now...lets set next sequence to pending....if there is one
                 if (! islast) {
                 st.executeUpdate("update ecn_task set ecnt_status = " +
                         "'" + getGlobalProgTag("pending") + "'" + " where " + 
                        " ecnt_nbr = " + "'" + myid + "'" + " AND " + 
                         "ecnt_seq = " + "'" + nextsequence + "'" + ";");
                 }
                 
                 //finally....if is last sequence...then set entire Req to 'approved'
                 if (islast) {
                
                  st.executeUpdate("update ecn_mstr set ecn_status = " +
                        "'" + getGlobalProgTag("closed") + "'" + " where " + 
                        " ecn_nbr = " + "'" + myid + "'" +  ";");
                         ddstatus.setSelectedItem(getGlobalProgTag("closed"));
                         messglbl.setText("This ECN is now closed");
                       
                         if (isEmail)
                         sendEmailToAll(myid);
                 }
                 
                 if (islast)
                 bsmf.MainFrame.show(getMessageTag(1132));
                 
                 if (! islast)
                 bsmf.MainFrame.show(getMessageTag(1133));  
                 
                 // reinit the task list
                res = st.executeQuery("select * from ecn_task where ecnt_nbr = " + "'" + myid + "'" + " order by ecnt_seq ;");
                while (res.next()) {
                taskmodel.addRow(new Object[]{res.getString("ecnt_seq"), res.getString("ecnt_owner"), res.getString("ecnt_task"), res.getString("ecnt_assigndate"), res.getString("ecnt_closedate"), res.getString("ecnt_status")});
                }
                tasktable.setModel(taskmodel);
                tasktable.getColumn(getGlobalColumnTag("sequence")).setCellRenderer(new ButtonRenderer());
                tasktable.getColumn(getGlobalColumnTag("sequence")).setCellEditor(
                        new ButtonEditor(new JCheckBox()));
               
                

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
    
    public void sendEmailToAll(String myid) {
        
       int i = 0;
        try{
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select * from ecn_task " +
                        " inner join ecn_mstr on ecn_nbr = ecnt_nbr " +
                        " inner join user_mstr on " +
                          " user_id = ecnt_owner " +
                          "where " +
                          "ecnt_nbr = " + "'" + myid + "'" + 
                           ";");
                 while (res.next()) {
                     String subject = "ECN Notice of Closure";
                     String body = "ECN number " + myid + " is closed. \n";
                     body += "ECN Task ID " + res.getString("ecn_mstrtask") + "\n";
                     body += "Part Number: " + res.getString("ecn_item") + "\n";
                     
                     if (! res.getString("user_email").isEmpty())
                     OVData.sendEmail(res.getString("user_email"), subject, body, "");
                 }
               
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       
    }
    
    public void getTasks(String task) {
       
        taskmodel.setNumRows(0);
        try {
     
            int i = 0;
           
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               
                res = st.executeQuery("select * from task_det where taskd_id = " + "'" + task + "'" + " order by taskd_sequence;");
                while (res.next()) {
                  taskmodel.addRow(new Object[]{res.getString("taskd_sequence"), res.getString("taskd_owner"), res.getString("taskd_desc"), 
                      "", "", ""});
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

    public void getNotes(String ecn, String seq) {
        tanotes.setText("");
        try {
            int i = 0;
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            
            try {
               
                res = st.executeQuery("select * from ecn_task where ecnt_nbr = " + "'" + ecn + "'" 
                        + " and ecnt_seq = " + "'" + seq + "'" 
                        + ";");
                while (res.next()) {
                  tanotes.setText(res.getString("ecnt_notes"));
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
      
    public void updateNotes(String ecn, String seq) {
       
        try {
            int i = 0;
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            try {
               
                st.executeUpdate(" update ecn_task " +
                        " set ecnt_notes = " + "'" + tanotes.getText().replace("'", "") + "'" +
                        " where ecnt_nbr = " + "'" + ecn + "'" 
                        + " and ecnt_seq = " + "'" + seq + "'" 
                        + ";");
               
             bsmf.MainFrame.show(getMessageTag(1135,ecn));
             tanotes.setText("");   
                
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
   
      
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbdatecreate = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        tbdrawing = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tasktable = new javax.swing.JTable();
        btupdate = new javax.swing.JButton();
        tbrev = new javax.swing.JTextField();
        dctargetdate = new com.toedter.calendar.JDateChooser();
        tbcustrev = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        ddengineer = new javax.swing.JComboBox();
        ddtask = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tanotes = new javax.swing.JTextArea();
        ddstatus = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        messglbl = new javax.swing.JLabel();
        btnotes = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        TreeScrollPanel = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();

        jButton1.setText("jButton1");

        jButton2.setText("jButton2");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("ECN Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("ECN#");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel25.setText("DateCreated");
        jLabel25.setName("lblcreatedate"); // NOI18N

        jLabel26.setText("Drawing");
        jLabel26.setName("lbldrawing"); // NOI18N

        jLabel35.setText("Target Date");
        jLabel35.setName("lbltargetdate"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        tasktable.setModel(new javax.swing.table.DefaultTableModel(
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
        tasktable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tasktableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tasktable);

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        dctargetdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Customer Rev");
        jLabel27.setName("lblcustomerrev"); // NOI18N

        jLabel37.setText("Engineer POC");
        jLabel37.setName("lblpoc"); // NOI18N

        ddtask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtaskActionPerformed(evt);
            }
        });

        jLabel38.setText("Mstr Task ID");
        jLabel38.setName("lblmastertask"); // NOI18N

        jLabel39.setText("Internal Rev");
        jLabel39.setName("lblinternalrev"); // NOI18N

        jLabel40.setText("Item Number");
        jLabel40.setName("lblitem"); // NOI18N

        tanotes.setColumns(20);
        tanotes.setRows(5);
        jScrollPane2.setViewportView(tanotes);

        jLabel36.setText("Status");
        jLabel36.setName("lblstatus"); // NOI18N

        btnotes.setText("Update Notes");
        btnotes.setName("btupdatenotes"); // NOI18N
        btnotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnotesActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messglbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddengineer, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddtask, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrev, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dctargetdate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcustrev, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdatecreate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnotes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addGap(12, 12, 12)
                                .addComponent(btadd))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel24)
                        .addGap(5, 5, 5)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(btnew)
                    .addComponent(btclear)
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddengineer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dctargetdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(jLabel35))))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddtask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdatecreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jLabel25))))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbdrawing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40)
                            .addComponent(jLabel26))))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbcustrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel39)
                                .addComponent(tbrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel27))))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btupdate)
                        .addComponent(btnotes)
                        .addComponent(btdelete))
                    .addComponent(btadd)))
        );

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        TreeScrollPanel.setViewportView(tree);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TreeScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TreeScrollPanel)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       newAction("ecn");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void ddtaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtaskActionPerformed
         if (ddtask.getSelectedItem() != null)
        getTasks(ddtask.getSelectedItem().toString());
    }//GEN-LAST:event_ddtaskActionPerformed

    private void tasktableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tasktableMouseClicked
         int row = tasktable.rowAtPoint(evt.getPoint());
        int col = tasktable.columnAtPoint(evt.getPoint());
        
        if (col != 0) {
            getNotes(tbkey.getText(), tasktable.getValueAt(row, 0).toString());  
        }
    }//GEN-LAST:event_tasktableMouseClicked

    private void btnotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnotesActionPerformed
        int howmany = tasktable.getSelectedRowCount();
        if (howmany > 1) {
            bsmf.MainFrame.show(getMessageTag(1134));
            return;
        }
        
        
        int row = tasktable.getSelectedRow();
        if (row > -1) {
        updateNotes(tbkey.getText(), tasktable.getValueAt(row, 0).toString());
        }
    }//GEN-LAST:event_btnotesActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane TreeScrollPanel;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnotes;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dctargetdate;
    private javax.swing.JComboBox ddengineer;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox ddtask;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel messglbl;
    private javax.swing.JTextArea tanotes;
    private javax.swing.JTable tasktable;
    private javax.swing.JTextField tbcustrev;
    private javax.swing.JTextField tbdatecreate;
    private javax.swing.JTextField tbdrawing;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbrev;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
