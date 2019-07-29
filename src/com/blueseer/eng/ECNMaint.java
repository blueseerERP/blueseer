/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.eng;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Component;
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
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
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
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
public class ECNMaint extends javax.swing.JPanel  {

  /*     
     public class TreePopup {
    private JScrollPane getTreeComponent() {
        JTree tree = new JTree() {
             public String getToolTipText(MouseEvent e) {
                         Object tip = null;
                         TreePath path = getPathForLocation(e.getX(), e.getY());
                         if (path != null) {
                              tip = path.getLastPathComponent();
                         }
                         return tip == null ? null : tip.toString();
                    }
        };
        ToolTipManager.sharedInstance().registerComponent(tree);
        PopupHandler handler = new PopupHandler(tree);
        tree.add(handler.getPopup());
        expand(tree, new TreePath(tree.getModel().getRoot()));
        return new JScrollPane(tree);
    }
 
    private void expand(JTree tree, TreePath path) {
        TreeNode node = (TreeNode)path.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            java.util.Enumeration e = node.children();
            while(e.hasMoreElements()) {
                TreeNode n = (TreeNode)e.nextElement();
                expand(tree, path.pathByAddingChild(n));
            }
        }
        tree.expandPath(path);
    }
 

      
    
  //  public static void main(String[] args) {
  //      TreePopup test = new TreePopup();
   //     JFrame f = new JFrame();
   //     f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   //     f.add(test.getTreeComponent());
   //     f.setSize(400,400);
   //     f.setLocation(200,200);
   //     f.setVisible(true);/
  //  }
    
    
}
*/
     
     
     
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
    
   
    
    
    public ECNMaint() {
        initComponents();
        
    }

      javax.swing.table.DefaultTableModel taskmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Sequence", "Owner", "Task", "AssignDate", "CompDate", "Status"
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
            if (tasktable.getModel().getValueAt(row, 5).toString().compareTo("complete") == 0) {
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
                if (tasktable.getModel().getValueAt(myrow, 5).toString().compareTo("pending") == 0) {
                    if (tasktable.getModel().getValueAt(myrow, 1).toString().compareTo(bsmf.MainFrame.userid) == 0) {
                 completetask(ecnnbr.getText(), tasktable.getValueAt(myrow, 0).toString());
                    } else {
                      bsmf.MainFrame.show("you do not have access to complete this action");  
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

    
    
      public void completetask(String myid, String thissequence) {
        
        try {
            int mypo = 0;
            int mysequence = Integer.valueOf(thissequence);
            taskmodel.setRowCount(0);
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int nextsequence = 0;
            boolean islast = false;
            boolean isEmail = false;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

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
                 st.executeUpdate("update ecn_task set ecnt_status = 'complete' where " + 
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
                 st.executeUpdate("update ecn_task set ecnt_status = 'pending' where " + 
                        " ecnt_nbr = " + "'" + myid + "'" + " AND " + 
                         "ecnt_seq = " + "'" + nextsequence + "'" + ";");
                 }
                 
                 //finally....if is last sequence...then set entire Req to 'approved'
                 if (islast) {
                
                  st.executeUpdate("update ecn_mstr set ecn_status = 'closed' where " + 
                        " ecn_nbr = " + "'" + myid + "'" +  ";");
                         ddstatus.setSelectedItem("closed");
                         messglbl.setText("This ECN is now closed");
                       
                         if (isEmail)
                         sendEmailToAll(myid);
                 }
                 
                 if (islast)
                 bsmf.MainFrame.show("Last task is complete...ECN is now closed");
                 
                 if (! islast)
                 bsmf.MainFrame.show("You have completed your task");  
                 
                 // reinit the task list
                res = st.executeQuery("select * from ecn_task where ecnt_nbr = " + "'" + myid + "'" + " order by ecnt_seq ;");
                while (res.next()) {
                taskmodel.addRow(new Object[]{res.getString("ecnt_seq"), res.getString("ecnt_owner"), res.getString("ecnt_task"), res.getString("ecnt_assigndate"), res.getString("ecnt_closedate"), res.getString("ecnt_status")});
                }
                tasktable.setModel(taskmodel);
                tasktable.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
                tasktable.getColumn("Sequence").setCellEditor(
                        new ButtonEditor(new JCheckBox()));
               
                

            } catch (SQLException s) {
               bsmf.MainFrame.show("Unable to Complete Task");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
     public void sendEmailToAll(String myid) {
        
       int i = 0;
        try{
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try{
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
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
                 bsmf.MainFrame.show("Cannot send email to engineers");
            }
            bsmf.MainFrame.con.close();
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
       
    }
      
      public void initvars(String arg) {
        
          java.util.Date now = new java.util.Date();
        TreeScrollPanel.setVisible(false);  
        tree.setVisible(false);
        messglbl.setText("");
        tanotes.setText("");
        ecnnbr.setText("");  
        tbdrawing.setText("");
        tbdatecreate.setText("");
        
        if (ddengineer.getItemCount() > 0)
        ddengineer.setSelectedIndex(0);
        
        if (ddtask.getItemCount() > 0)
        ddtask.setSelectedIndex(0);
        
        if (ddpart.getItemCount() > 0)
        ddpart.setSelectedIndex(0);
        
        if (ddstatus.getItemCount() > 0)
        ddstatus.setSelectedIndex(0);
        
        
        dctargetdate.setDate(now);
        tbrev.setText("");
        tbcustrev.setText("");
       
        
      if(((DefaultComboBoxModel)ddstatus.getModel()).getIndexOf("new") >= 0) {
       ddstatus.removeItem("new");
      }
        
        
        taskmodel.setRowCount(0);
        tasktable.setModel(taskmodel);
        tasktable.getColumn("Sequence").setCellRenderer(new ButtonRenderer());
        tasktable.getColumn("Sequence").setCellEditor(
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
        ArrayList item = OVData.getItemMasterAlllist();
        for (int i = 0; i < item.size(); i++) {
            ddpart.addItem(item.get(i));
        }
        
        

          if (! arg.isEmpty()) {
            getECN(arg);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btbrowse.setEnabled(true);
              ecnnbr.setEnabled(true);
          }
        
    }
      
      public void enableAll() {
          ddengineer.setEnabled(true);
          ddtask.setEnabled(true);
          ddstatus.setEnabled(true);
          ddpart.setEnabled(true);
          tbrev.setEnabled(true);
          tbcustrev.setEnabled(true);
          tbdrawing.setEnabled(true);
          dctargetdate.setEnabled(true);
          tbdatecreate.setEnabled(true);
          tasktable.setEnabled(true);
          tanotes.setEnabled(true);
          btadd.setEnabled(true);
          btedit.setEnabled(true);
          btnew.setEnabled(true);
          btbrowse.setEnabled(true);
          ecnnbr.setEnabled(true);
      }
      
         public void disableAll() {
          ddengineer.setEnabled(false);
          ddtask.setEnabled(false);
          ddstatus.setEnabled(false);
          ddpart.setEnabled(false);
          tbrev.setEnabled(false);
          tbcustrev.setEnabled(false);
          tbdrawing.setEnabled(false);
          dctargetdate.setEnabled(false);
          tbdatecreate.setEnabled(false);
          tasktable.setEnabled(false);
          tanotes.setEnabled(false);
          btadd.setEnabled(false);
          btedit.setEnabled(false);
          btnew.setEnabled(false);
          btbrowse.setEnabled(false);
          ecnnbr.setEnabled(false);
      }
      
      public void getECN(String ecn) {
        
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
           
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select * from ecn_mstr where ecn_nbr = " + "'" + ecn + "'" + ";");
                while (res.next()) {
                    i++;
                    tbdatecreate.setText(res.getString("ecn_createdate"));
                    tbrev.setText(res.getString("ecn_rev"));
                    tbcustrev.setText(res.getString("ecn_custrev"));
                    tbdrawing.setText(res.getString("ecn_drawing"));
                    ddengineer.setSelectedItem(res.getString("ecn_poc"));
                    ddtask.setSelectedItem(res.getString("ecn_mstrtask"));
                    ddstatus.setSelectedItem(res.getString("ecn_status"));
                    ddpart.setSelectedItem(res.getString("ecn_item"));
                    dctargetdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("ecn_targetdate")));
                    ecnnbr.setText(res.getString("ecn_nbr"));
          
                }

                // set enabled properties per POC or otheruser
                // all components should be disabled at this point
                if (ddengineer.getSelectedItem().toString().compareTo(bsmf.MainFrame.userid) == 0) {
                    enableAll();
                } else {
                    tasktable.setEnabled(true);
                    tanotes.setEnabled(true);
                }
                
                
                // setdisabled for specific buttons regardless of user
                btnew.setEnabled(false);
                btadd.setEnabled(false);
                btbrowse.setEnabled(false);
                ecnnbr.setEnabled(false);
                
                res = st.executeQuery("select * from ecn_task where ecnt_nbr = " + "'" + ecn + "'" + " order by ecnt_seq;");
                while (res.next()) {
                  taskmodel.addRow(new Object[]{res.getString("ecnt_seq"), res.getString("ecnt_owner"), res.getString("ecnt_task"), res.getString("ecnt_assigndate"), res.getString("ecnt_closedate"), res.getString("ecnt_status")});
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to retrieve ECN Mstr and Tasks");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
  
      public void getTasks(String task) {
       
        if (ddstatus.getSelectedItem().toString().compareTo("open") == 0)
            return;
          
        taskmodel.setNumRows(0);
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
           
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
                res = st.executeQuery("select * from task_det where taskd_id = " + "'" + task + "'" + " order by taskd_sequence;");
                while (res.next()) {
                  taskmodel.addRow(new Object[]{res.getString("taskd_sequence"), res.getString("taskd_owner"), res.getString("taskd_desc"), 
                      "", "", ""});
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to retrieve Task Mstr Details");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

       public void getNotes(String ecn, String seq) {
        tanotes.setText("");
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
           
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
                res = st.executeQuery("select * from ecn_task where ecnt_nbr = " + "'" + ecn + "'" 
                        + " and ecnt_seq = " + "'" + seq + "'" 
                        + ";");
                while (res.next()) {
                  tanotes.setText(res.getString("ecnt_notes"));
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to retrieve ECN Task Notes");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
        public void updateNotes(String ecn, String seq) {
       
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
           
            
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
               
                st.executeUpdate(" update ecn_task " +
                        " set ecnt_notes = " + "'" + tanotes.getText().replace("'", "") + "'" +
                        " where ecnt_nbr = " + "'" + ecn + "'" 
                        + " and ecnt_seq = " + "'" + seq + "'" 
                        + ";");
               
             bsmf.MainFrame.show("updated Notes for ECN=" + ecn + " and Seq=" + seq);
             tanotes.setText("");   
                
            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to update ECN Task Notes");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
   
 /*
   public void WalkShipper(TreeNode root) {
    if (tree.getModel().getRoot().toString().equals(root.toString()))
    OVData.CreateAdvancedShipperHeader(root.toString(), site.getText(), billto.getText(), tbrev.getText(), "1", "s", dctargetdate.getDate().toString(), tbdatecreate.getText(), ddshipvia.getSelectedItem().toString(), tbcustrev.getText());
   
    OVData.CreateAdvancedShipperDetail(root.toString(), site.getText(), billto.getText(), tbrev.getText(), "1", "s", dctargetdate.getDate().toString(), tbdatecreate.getText(), ddshipvia.getSelectedItem().toString(), tbcustrev.getText());
    Enumeration children = root.children();
    if (children != null) {
      while (children.hasMoreElements()) {
        WalkShipper((TreeNode) children.nextElement());
      }
    }
  }
    
   */
      
      
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        ecnnbr = new javax.swing.JTextField();
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
        btedit = new javax.swing.JButton();
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
        btbrowse = new javax.swing.JButton();
        TreeScrollPanel = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("ECN Maintenance"));

        jLabel24.setText("ECN#");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel25.setText("DateCreated");

        jLabel26.setText("Drawing");

        jLabel35.setText("Target Date");

        btadd.setText("Add ECN");
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

        btedit.setText("Edit ECN");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        dctargetdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Customer Rev");

        jLabel37.setText("Engineer POC");

        ddtask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtaskActionPerformed(evt);
            }
        });

        jLabel38.setText("Mstr Task ID");

        jLabel39.setText("Internal Rev");

        jLabel40.setText("Item Number");

        tanotes.setColumns(20);
        tanotes.setRows(5);
        jScrollPane2.setViewportView(tanotes);

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "open", "hold", "closed", "cancelled" }));

        jLabel36.setText("Status");

        btnotes.setText("Update Notes");
        btnotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnotesActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel24)
                        .addGap(5, 5, 5)
                        .addComponent(ecnnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(btnew)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel36)
                        .addGap(12, 12, 12)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(messglbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnotes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btedit)
                                .addGap(12, 12, 12)
                                .addComponent(btadd))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(51, 51, 51)
                                    .addComponent(jLabel37)
                                    .addGap(12, 12, 12)
                                    .addComponent(ddengineer, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(60, 60, 60)
                                    .addComponent(jLabel35)
                                    .addGap(12, 12, 12)
                                    .addComponent(dctargetdate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(57, 57, 57)
                                    .addComponent(jLabel38)
                                    .addGap(12, 12, 12)
                                    .addComponent(ddtask, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(102, 102, 102)
                                    .addComponent(jLabel25)
                                    .addGap(12, 12, 12)
                                    .addComponent(tbdatecreate, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(54, 54, 54)
                                    .addComponent(jLabel40)
                                    .addGap(12, 12, 12)
                                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(37, 37, 37)
                                    .addComponent(jLabel26)
                                    .addGap(12, 12, 12)
                                    .addComponent(tbdrawing, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(61, 61, 61)
                                    .addComponent(jLabel39)
                                    .addGap(12, 12, 12)
                                    .addComponent(tbrev, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(62, 62, 62)
                                    .addComponent(jLabel27)
                                    .addGap(12, 12, 12)
                                    .addComponent(tbcustrev, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ecnnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnew)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel36)))
                            .addComponent(btbrowse)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(messglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
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
                    .addComponent(tbrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcustrev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel27))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btedit)
                        .addComponent(btnotes))
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
         
        initvars("");
        
        ecnnbr.setText(String.valueOf(OVData.getNextNbr("ecn")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                
                dctargetdate.setDate(now);
                tbdatecreate.setText(dfdate.format(now));
                
                // tbDateShippedSM.setEnabled(false);
                
              
                enableAll();
                if(((DefaultComboBoxModel)ddstatus.getModel()).getIndexOf("new") == -1) {
                  ddstatus.addItem("new");
                }
                ddstatus.setSelectedItem("new");
                ddstatus.setEnabled(false);
                btnew.setEnabled(false);
                btbrowse.setEnabled(false);
                btedit.setEnabled(false);
                ecnnbr.setEnabled(false);
                
                
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
          try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                if (proceed) {
                    st.executeUpdate("insert into ecn_mstr "
                        + "(ecn_nbr, ecn_poc, ecn_mstrtask, ecn_status, "
                        + " ecn_targetdate, ecn_createdate, ecn_closedate, "
                        + " ecn_drawing, ecn_item, ecn_rev, ecn_custrev ) "
                        + " values ( " + "'" + ecnnbr.getText().toString() + "'" + ","
                        + "'" + ddengineer.getSelectedItem().toString() + "'" + ","
                        + "'" + ddtask.getSelectedItem().toString() + "'" + ","
                        + "'" + "open" + "'" + ","
                        + "'" + dfdate.format(dctargetdate.getDate()).toString() + "'" + ","
                        + "'" + tbdatecreate.getText() + "'" + ","
                        +  null + ","
                        + "'" + tbdrawing.getText() + "'" + ","
                        + "'" + ddpart.getSelectedItem().toString() + "'" + ","
                        + "'" + tbrev.getText() + "'" + ","
                        + "'" + tbcustrev.getText() + "'" 
                        + ")"
                        + ";");

                  //    "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice"
                    
                    for (int j = 0; j < tasktable.getRowCount(); j++) {
                        st.executeUpdate("insert into ecn_task "
                            + "(ecnt_nbr, ecnt_mstrid, ecnt_seq, ecnt_owner, ecnt_task, "
                            + "ecnt_assigndate, ecnt_closedate, ecnt_status ) "
                            + " values ( " 
                            + "'" + ecnnbr.getText().toString() + "'" + ","
                            + "'" + ddtask.getSelectedItem().toString() + "'" + ","
                            + "'" + tasktable.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tasktable.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tasktable.getValueAt(j, 2).toString() + "'" + ","
                            + null + ","
                            + null + "," 
                            + "'" + "pending" + "'"    
                            + ")"
                            + ";");

                    }
                    bsmf.MainFrame.show("ECN has been added");
                  
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Cannot add ECN");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
       
    }//GEN-LAST:event_bteditActionPerformed

    private void ddtaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtaskActionPerformed
         if (ddtask.getSelectedItem() != null)
        getTasks(ddtask.getSelectedItem().toString());
    }//GEN-LAST:event_ddtaskActionPerformed

    private void tasktableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tasktableMouseClicked
         int row = tasktable.rowAtPoint(evt.getPoint());
        int col = tasktable.columnAtPoint(evt.getPoint());
        
        if (col != 0) {
            getNotes(ecnnbr.getText(), tasktable.getValueAt(row, 0).toString());  
        }
    }//GEN-LAST:event_tasktableMouseClicked

    private void btnotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnotesActionPerformed
        int howmany = tasktable.getSelectedRowCount();
        if (howmany > 1) {
            bsmf.MainFrame.show("Select only one Sequence");
            return;
        }
        
        
        int row = tasktable.getSelectedRow();
        if (row > 0) {
        updateNotes(ecnnbr.getText(), tasktable.getValueAt(row, 0).toString());
        }
    }//GEN-LAST:event_btnotesActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "ecnmaint,ecn_nbr");
    }//GEN-LAST:event_btbrowseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane TreeScrollPanel;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnotes;
    private com.toedter.calendar.JDateChooser dctargetdate;
    private javax.swing.JComboBox ddengineer;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox ddtask;
    private javax.swing.JTextField ecnnbr;
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
    private javax.swing.JTextField tbrev;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
