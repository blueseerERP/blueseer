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

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.tags;
import static com.blueseer.edi.EDIMap.csvToSegment;
import static com.blueseer.edi.EDIMap.jsonTagsToSegment;
import static com.blueseer.edi.EDIMap.xmlTagsToSegments;
import static com.blueseer.edi.ediData.addDFStructureTransaction;
import static com.blueseer.edi.ediData.addMapStruct;
import static com.blueseer.edi.ediData.deleteDFStructure;
import com.blueseer.edi.ediData.dfs_det;
import com.blueseer.edi.ediData.dfs_mstr;
import static com.blueseer.edi.ediData.getDFSDet;
import static com.blueseer.edi.ediData.getDFSMstr;
import static com.blueseer.edi.ediData.isValidDFSid;
import static com.blueseer.edi.ediData.updateDFStructureTransaction;
import static com.blueseer.edi.ediData.updateMapStruct;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ConvertIntToYesNo;
import static com.blueseer.utl.BlueSeerUtils.ConvertTrueFalseToBoolean;
import static com.blueseer.utl.BlueSeerUtils.asciivalues;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
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
import static com.blueseer.utl.EDData.getEDIStructureDir;
import com.blueseer.utl.IBlueSeerT;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

/**
 *
 * @author vaughnte
 */
public class StructMaint extends javax.swing.JPanel  {    

    
    // global variable declarations
                boolean isLoad = false;
                public static dfs_mstr x = null;
                public static ArrayList<dfs_det> dfsdetlist = null;
    
                // global datatablemodel declarations       
     javax.swing.table.DefaultTableModel detailmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Segment", "Parent", "LoopCount", "Group", "LandMark", "Field", "Desc", "Min", "Max", "Align", "Status", "Type"
            });
   
    public StructMaint() {
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
           } else if (this.type.equals("get")) {
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
    
    public void setComponentDefaultValues() {
        isLoad = true;
        
       detailmodel.setRowCount(0);
       tabledetail.setModel(detailmodel);
       tabledetail.getTableHeader().setReorderingAllowed(false);
        
        tbdelimiter.setText("");
        tbkey.setText("");
        tbdesc.setText("");
        tbversion.setText("");
        ddfiletype.setSelectedIndex(0);
        dddoctype.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoctype.addItem(mylist.get(i));
        } 
        dddoctype.insertItemAt("", 0);
        dddoctype.setSelectedIndex(0);
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btcopy.setEnabled(false);
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
     
    public boolean validateInput(dbaction x) {
        Map<String,Integer> f = OVData.getTableInfo("dfs_mstr");
        int fc;

        fc = checkLength(f,"dfs_id");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }

         fc = checkLength(f,"dfs_desc");
        if (tbdesc.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbdesc.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"dfs_version");
        if (tbversion.getText().length() > fc) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbversion.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"dfs_doctype");
        if (dddoctype.getSelectedItem().toString().length() > fc || dddoctype.getSelectedItem().toString().isBlank()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            dddoctype.requestFocus();
            return false;
        }
        
        fc = checkLength(f,"dfs_filetype");
        if (ddfiletype.getSelectedItem().toString().length() > fc || ddfiletype.getSelectedItem().toString().isBlank() ) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            ddfiletype.requestFocus();
            return false;
        }
       
        /*
         if (! BlueSeerUtils.isFile(EDData.getEDIStructureDir(), tbkey.getText())) {
                    bsmf.MainFrame.show(getMessageTag(1145,tbkey.getText()));
                    tbkey.requestFocus();
                    return false;
        }
        */ 
        if (! checkStructure(tbkey.getText())) {
            int errornum = 0;
            if (x.toString().equals("add")) {
                errornum = 1011;
            }
            if (x.toString().equals("update")) {
                errornum = 1012;
            }
            bsmf.MainFrame.show(getMessageTag(errornum,tbkey.getText()));
            tbkey.requestFocus();
            return false;
        } 
        
      return true;
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
   
    public String[] getRecord(String[] key) {
        x = getDFSMstr(key);  
        dfsdetlist = getDFSDet(key[0]); 
        return x.m();
    }
    
    public dfs_mstr createRecord(String copykey) { 
        String k = (copykey == null) ? tbkey.getText() : copykey;
        dfs_mstr x = new dfs_mstr(null, k,
                tbdesc.getText(),
                tbversion.getText(),
                dddoctype.getSelectedItem().toString(),
                ddfiletype.getSelectedItem().toString(),
                tbdelimiter.getText(),
                "" // misc
                );
        /* potential validation mechanism...would need association between record field and input field
        for(Field f : x.getClass().getDeclaredFields()){
        System.out.println(f.getName());
        }
        */
        return x;
    }
     
    public ArrayList<dfs_det> createDetRecord(String copykey) {
        String k = (copykey == null) ? tbkey.getText() : copykey;
        ArrayList<dfs_det> list = new ArrayList<dfs_det>();
         for (int j = 0; j < tabledetail.getRowCount(); j++) {
             dfs_det x = new dfs_det(null, 
                k,
                tabledetail.getValueAt(j, 0).toString(),
                tabledetail.getValueAt(j, 1).toString(),
                (tabledetail.getValueAt(j, 2).toString().isBlank()) ? "0" : tabledetail.getValueAt(j, 2).toString(),
                String.valueOf(BlueSeerUtils.boolToInt(ConvertTrueFalseToBoolean(tabledetail.getValueAt(j, 3).toString()))),
                String.valueOf(BlueSeerUtils.boolToInt(ConvertTrueFalseToBoolean(tabledetail.getValueAt(j, 4).toString()))),
                tabledetail.getValueAt(j, 5).toString(),
                tabledetail.getValueAt(j, 6).toString(),
                (tabledetail.getValueAt(j, 7).toString().isBlank()) ? "0" : tabledetail.getValueAt(j, 7).toString(),
                (tabledetail.getValueAt(j, 8).toString().isBlank()) ? "0" : tabledetail.getValueAt(j, 8).toString(),
                tabledetail.getValueAt(j, 9).toString(),
                tabledetail.getValueAt(j, 10).toString(),
                tabledetail.getValueAt(j, 11).toString()
                );
       
        list.add(x);
         }
        return list;   
    }
    
    
    public String[] addRecord(String[] key) {
         String[] m = addDFStructureTransaction(createDetRecord(null), createRecord(null));
         return m;
    }
        
    public String[] updateRecord(String[] key) {
         String[] m = updateDFStructureTransaction(key[0], createDetRecord(null), createRecord(null));
         return m;
    }
    
    public String[] deleteRecord(String[] key) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteDFStructure(createRecord(null)); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getMapStructBrowseUtil(luinput.getText(),0, "dfs_id");  
        } else {
         luModel = DTData.getMapStructBrowseUtil(luinput.getText(),0, "dfs_desc");   
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
                initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
        
    }

    public void lookUpFrameASCIIele() {
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0 || column == 1) {
                ludialog.dispose();
                tbdelimiter.setText(target.getValueAt(row,0).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
        luModel = DTData.getASCIIChartDT(0, 128);
        luTable.setModel(luModel);
        callDialog();
        
        
    }
   
    
    public void updateForm() {
        tbdesc.setText(x.dfs_desc());
        tbkey.setText(x.dfs_id());
        tbversion.setText(x.dfs_version());
        dddoctype.setSelectedItem(x.dfs_doctype()); 
        ddfiletype.setSelectedItem(x.dfs_filetype()); 
        tbdelimiter.setText(x.dfs_delimiter());
        setAction(x.m()); 
        
        // now detail
        detailmodel.setRowCount(0);
        for (dfs_det dfsd : dfsdetlist) {
                    detailmodel.addRow(new Object[]{
                      dfsd.dfsd_segment(),   
                      dfsd.dfsd_parent(),
                      dfsd.dfsd_loopcount(),
                      ConvertIntToYesNo(Integer.valueOf(dfsd.dfsd_isgroup())),
                      ConvertIntToYesNo(Integer.valueOf(dfsd.dfsd_islandmark())),
                      dfsd.dfsd_field(),
                      dfsd.dfsd_desc(),
                      dfsd.dfsd_min(),
                      dfsd.dfsd_max(),
                      dfsd.dfsd_align(),
                      dfsd.dfsd_status(),
                      dfsd.dfsd_type()
                     
                  });
                }
        
    }
    
    // misc methods
    public boolean checkStructure(String structureName) {
        String dirpath;
        boolean isgood = true;
        List<String> lines = new ArrayList<>();
        dirpath = cleanDirString(EDData.getEDIStructureDir()) + structureName;
        Path path = FileSystems.getDefault().getPath(dirpath);
        File file = path.toFile();
        long count = 0;
        long numoferrors = 0;
        if (file != null && file.exists()) {
                try {   
                    lines = Files.readAllLines(file.toPath());
                    int k = 0;
                    for (String s : lines) {
                        k++;
                        if (s.startsWith("#")) {
                            continue;
                        }
                        count = s.chars().filter(ch -> ch == ',').count();
                        if (count != 11) {
                            isgood = false;
                            numoferrors++;
                        }
                        
                    }
                } catch (MalformedInputException m) {
                    bsmf.MainFrame.show("Structure file may not be UTF-8 encoded");
                    isgood = false;
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                    isgood = false;
                }   
        }
        if (numoferrors > 0) {
           bsmf.MainFrame.show("Number of lines with inaccurate comma delimiter count: " + numoferrors); 
        }
        
        return isgood;
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < tabledetail.getRowCount(); j++) {
            current = Integer.valueOf(tabledetail.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
    
    public File getfile(String title) {
        
        File file = null;
        JFileChooser jfc = new JFileChooser(FileSystems.getDefault().getPath("edi").toFile());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle(title);
        int returnVal = jfc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = jfc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
               if (! file.exists()) {
                 return null;
               }
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelmaint = new javax.swing.JPanel();
        lblid = new javax.swing.JLabel();
        lbldesc = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        tbdesc = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        tbversion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dddoctype = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        ddfiletype = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tbparent = new javax.swing.JTextField();
        tbsegment = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbisgroup = new javax.swing.JCheckBox();
        cbislandmark = new javax.swing.JCheckBox();
        tbloopcount = new javax.swing.JTextField();
        ddtype = new javax.swing.JComboBox<>();
        tbfield = new javax.swing.JTextField();
        tbfielddesc = new javax.swing.JTextField();
        tbmin = new javax.swing.JTextField();
        tbmax = new javax.swing.JTextField();
        ddstatus = new javax.swing.JComboBox<>();
        ddalign = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btaddrow = new javax.swing.JButton();
        btupdaterow = new javax.swing.JButton();
        btdeleterow = new javax.swing.JButton();
        btimport = new javax.swing.JButton();
        btdown = new javax.swing.JButton();
        btup = new javax.swing.JButton();
        bttransform = new javax.swing.JButton();
        btdownload = new javax.swing.JButton();
        btinsertrow = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        btcopy = new javax.swing.JButton();
        tbdelimiter = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        btlookupele = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        panelmaint.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI File Structure Register"));
        panelmaint.setName("panelmaint"); // NOI18N

        lblid.setText("Structure File");
        lblid.setName("lblid"); // NOI18N

        lbldesc.setText("Desc");
        lbldesc.setName("lbldesc"); // NOI18N

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

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.setName("btlookup"); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel1.setText("Version");
        jLabel1.setName("lblversion"); // NOI18N

        jLabel2.setText("Doc Type");

        ddfiletype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "", "CSV", "FF", "JSON", "X12", "UNE", "XML" }));

        jLabel3.setText("File Type");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Record/Field Maintenance"));

        jLabel4.setText("Segment");

        jLabel5.setText("Parent");

        cbisgroup.setText("Grouping");

        cbislandmark.setText("LandMark");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N", "F", "A" }));

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "M", "O", "X" }));

        ddalign.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "+" }));

        jLabel6.setText("Type");

        jLabel7.setText("Status");

        jLabel8.setText("Align");

        jLabel9.setText("Max");

        jLabel10.setText("Min");

        jLabel11.setText("Desc");

        jLabel12.setText("Field");

        jLabel13.setText("LoopCount");

        btaddrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddrow.setToolTipText("Add Row");
        btaddrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddrowActionPerformed(evt);
            }
        });

        btupdaterow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/change.png"))); // NOI18N
        btupdaterow.setToolTipText("Update Row");
        btupdaterow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdaterowActionPerformed(evt);
            }
        });

        btdeleterow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleterow.setToolTipText("Delete Row");
        btdeleterow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleterowActionPerformed(evt);
            }
        });

        btimport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/input.png"))); // NOI18N
        btimport.setToolTipText("Import");
        btimport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btimportActionPerformed(evt);
            }
        });

        btdown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/download.png"))); // NOI18N
        btdown.setToolTipText("Move Down");
        btdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdownActionPerformed(evt);
            }
        });

        btup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/upload.png"))); // NOI18N
        btup.setToolTipText("Move Up");
        btup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupActionPerformed(evt);
            }
        });

        bttransform.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gear.png"))); // NOI18N
        bttransform.setToolTipText("Transform");
        bttransform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttransformActionPerformed(evt);
            }
        });

        btdownload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/output.png"))); // NOI18N
        btdownload.setToolTipText("Export");
        btdownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdownloadActionPerformed(evt);
            }
        });

        btinsertrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rightdoc.png"))); // NOI18N
        btinsertrow.setToolTipText("Insert Blank Row");
        btinsertrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinsertrowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbloopcount, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbislandmark)
                    .addComponent(cbisgroup))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbparent, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbfield, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbfielddesc, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbmax, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddalign, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbmin, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bttransform, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btup, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdown, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdaterow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btaddrow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btinsertrow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleterow, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(btimport, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdownload, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbsegment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbparent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbloopcount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addGap(6, 6, 6)))
                    .addComponent(tbfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddalign, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cbislandmark))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbfielddesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbisgroup)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbmax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))
                            .addComponent(bttransform)
                            .addComponent(btup)
                            .addComponent(btupdaterow)
                            .addComponent(btaddrow)
                            .addComponent(btdeleterow)
                            .addComponent(btimport)
                            .addComponent(btdown)
                            .addComponent(btdownload)
                            .addComponent(btinsertrow))))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabledetail);

        btcopy.setText("Copy");
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        tbdelimiter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdelimiterFocusLost(evt);
            }
        });

        jLabel14.setText("Delimiter");

        btlookupele.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupele.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupeleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelmaintLayout = new javax.swing.GroupLayout(panelmaint);
        panelmaint.setLayout(panelmaintLayout);
        panelmaintLayout.setHorizontalGroup(
            panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmaintLayout.createSequentialGroup()
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelmaintLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblid)
                            .addComponent(lbldesc)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelmaintLayout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addComponent(dddoctype, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddfiletype, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelmaintLayout.createSequentialGroup()
                                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelmaintLayout.createSequentialGroup()
                                        .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(64, 64, 64)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookupele, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelmaintLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btcopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(panelmaintLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        panelmaintLayout.setVerticalGroup(
            panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmaintLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblid))
                    .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btclear))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddfiletype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelmaintLayout.createSequentialGroup()
                        .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(tbdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelmaintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadd)
                            .addComponent(btupdate)
                            .addComponent(btdelete)
                            .addComponent(btcopy)))
                    .addGroup(panelmaintLayout.createSequentialGroup()
                        .addComponent(btlookupele)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        add(panelmaint);
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
      if (! btadd.isEnabled())
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btaddrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddrowActionPerformed
         String group = BlueSeerUtils.ConvertIntToYesNo(BlueSeerUtils.boolToInt(cbisgroup.isSelected()));
         String landmark = BlueSeerUtils.ConvertIntToYesNo(BlueSeerUtils.boolToInt(cbislandmark.isSelected()));
                    
       
            detailmodel.addRow(new Object[]{ 
                tbsegment.getText(),
                tbparent.getText(),
                tbloopcount.getText(),
                group,
                landmark,
                tbfield.getText(),
                tbfielddesc.getText(),
                tbmin.getText(),
                tbmax.getText(),
                ddalign.getSelectedItem().toString(),
                ddstatus.getSelectedItem().toString(),
                ddtype.getSelectedItem().toString()
            });
       
       
       tbsegment.setText("");
       tbparent.setText("");
        tbloopcount.setText("");
        cbisgroup.setSelected(false);
        cbislandmark.setSelected(false);
        tbfield.setText("");
        tbfielddesc.setText("");
        tbmin.setText("");
        tbmax.setText("");
        ddalign.setSelectedIndex(0);
        ddstatus.setSelectedIndex(0);
        ddtype.setSelectedIndex(0);
       
    }//GEN-LAST:event_btaddrowActionPerformed

    private void btdeleterowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleterowActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeleterowActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        // element, percent, type, enabled
        tbsegment.setText(tabledetail.getValueAt(row, 0).toString());
        tbparent.setText(tabledetail.getValueAt(row, 1).toString());
        tbloopcount.setText(tabledetail.getValueAt(row, 2).toString());
        cbisgroup.setSelected(ConvertTrueFalseToBoolean(tabledetail.getValueAt(row, 3).toString()));
        cbislandmark.setSelected(ConvertTrueFalseToBoolean(tabledetail.getValueAt(row, 4).toString()));
        tbfield.setText(tabledetail.getValueAt(row, 5).toString());
        tbfielddesc.setText(tabledetail.getValueAt(row, 6).toString());
        tbmin.setText(tabledetail.getValueAt(row, 7).toString());
        tbmax.setText(tabledetail.getValueAt(row, 8).toString());
        ddalign.setSelectedItem(tabledetail.getValueAt(row, 9).toString());
        ddstatus.setSelectedItem(tabledetail.getValueAt(row, 10).toString());
        ddtype.setSelectedItem(tabledetail.getValueAt(row, 11).toString());
    }//GEN-LAST:event_tabledetailMouseClicked

    private void btupdaterowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdaterowActionPerformed
      //  int line = 0;
      //  line = getmaxline();
      //  line++;
        
        int[] rows = tabledetail.getSelectedRows();
        if (rows.length != 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
                return;
        }
        
        String group = BlueSeerUtils.ConvertIntToYesNo(BlueSeerUtils.boolToInt(cbisgroup.isSelected()));
        String landmark = BlueSeerUtils.ConvertIntToYesNo(BlueSeerUtils.boolToInt(cbislandmark.isSelected()));
          
        
        for (int i : rows) {
                tabledetail.setValueAt(tbsegment.getText(), i, 0);
                tabledetail.setValueAt(tbparent.getText(), i, 1);
                tabledetail.setValueAt(tbloopcount.getText(), i, 2);
                tabledetail.setValueAt(group, i, 3);
                tabledetail.setValueAt(landmark, i, 4);
                tabledetail.setValueAt(tbfield.getText(), i, 5);
                tabledetail.setValueAt(tbfielddesc.getText(), i, 6);
                tabledetail.setValueAt(tbmin.getText(), i, 7);
                tabledetail.setValueAt(tbmax.getText(), i, 8);
                tabledetail.setValueAt(ddalign.getSelectedItem().toString(), i, 9);
                tabledetail.setValueAt(ddstatus.getSelectedItem().toString(), i, 10);
                tabledetail.setValueAt(ddtype.getSelectedItem().toString(), i, 11);
        }
        
    }//GEN-LAST:event_btupdaterowActionPerformed

    private void btimportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btimportActionPerformed
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1178));
        if (proceed) {
        List<String> lines = new ArrayList<>();
        File file = getfile("Open Structure File");
        if (file != null && file.exists()) {
                try {   
                    lines = Files.readAllLines(file.toPath());
                } catch (MalformedInputException m) {
                    bslog(m);
                    bsmf.MainFrame.show("Structure file may not be UTF-8 encoded");
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                }   
        }
        String v11 = "";
        detailmodel.setRowCount(0);
        for (String s : lines) {
            if (s.startsWith("#")) {
                continue;
            }
            String[] arr = s.split(",", -1);
            if (arr != null && arr.length > 10) {
                if (arr.length < 12) {
                    v11 = "";
                } else {
                    v11 = arr[11];
                }
                detailmodel.addRow(new Object[]{
                    arr[0],
                    arr[1],
                    arr[2],
                    arr[3],
                    arr[4],
                    arr[5],
                    arr[6],
                    arr[7],
                    arr[8],
                    arr[9],
                    arr[10],
                    v11
                  });
            }
        }
        } // if proceed
    }//GEN-LAST:event_btimportActionPerformed

    private void btdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdownActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        if (rows == null || rows.length == 0) {
            bsmf.MainFrame.show(getMessageTag(1029));
            return;
        }
        if (rows.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        for (int i : rows) {
            if (i + 1 < tabledetail.getRowCount() ) {
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).moveRow(i, i, i + 1);
            tabledetail.setRowSelectionInterval(i + 1, i + 1);
            }
        }
    }//GEN-LAST:event_btdownActionPerformed

    private void btupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        if (rows == null || rows.length == 0) {
            bsmf.MainFrame.show(getMessageTag(1029));
            return;
        }
        if (rows.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1095));
            return;
        }
        for (int i : rows) {
            if (i > 0 && rows.length == 1) {
            ((javax.swing.table.DefaultTableModel) tabledetail.getModel()).moveRow(i, i, i - 1);
             tabledetail.setRowSelectionInterval(i - 1, i - 1);
            }
        }
    }//GEN-LAST:event_btupActionPerformed

    private void bttransformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttransformActionPerformed
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1178));
        if (proceed) {
        List<String[]> lines = null;
        String filecontent = "";
        File file = getfile("Open Target File");
        if (file != null && file.exists()) {
                try {   
                    //lines = Files.readAllLines(file.toPath());
                    filecontent = new String(Files.readAllBytes(file.toPath()));
                    if (filecontent.startsWith("{") || filecontent.startsWith("[")) {
                     lines = jsonTagsToSegment(filecontent);
                    } else if (filecontent.startsWith("<")) {
                     lines = xmlTagsToSegments(filecontent);   
                    } else if (ddfiletype.getSelectedItem().toString().equals("CSV")) {
                     lines = csvToSegment(filecontent);    
                    } else {
                        bsmf.MainFrame.show("sample file must be csv, xml or json structure...unrecognized format");
                        return;
                    }
                    
                } catch (MalformedInputException m) {
                    bslog(m);
                    bsmf.MainFrame.show("Structure file may not be UTF-8 encoded");
                } catch (IOException ex) {
                    bslog(ex);
                    bsmf.MainFrame.show("Error...check data/app.log");
                }   
        }
        String v11 = "";
        detailmodel.setRowCount(0);
        
        for (String[] arr : lines) {
          //  if (s.startsWith("#")) {
          //      continue;
         //   }
           // String[] arr = s.split(",", -1);
        //   String z = String.join(",",arr);
         //  System.out.println(z);
         //  System.out.println("HERE!!! " + arr.length);
            if (arr != null && arr.length > 10) {
                
                if (arr.length < 12) {
                    v11 = "";
                } else {
                    v11 = arr[11];
                }
                
                detailmodel.addRow(new Object[]{
                    arr[0],
                    arr[1],
                    arr[2],
                    arr[3],
                    arr[4],
                    arr[5],
                    arr[6],
                    arr[7],
                    arr[8],
                    arr[9],
                    arr[10],
                    v11
                  });
                
                
            }
            
        }
        tabledetail.setModel(detailmodel);
        } // if proceed
    }//GEN-LAST:event_bttransformActionPerformed

    private void btdownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdownloadActionPerformed
        
        String filename = "file." + Long.toHexString(System.currentTimeMillis()) + ".csv";
        Path path = FileSystems.getDefault().getPath(cleanDirString(EDData.getEDIStructureDir()) + filename);
        BufferedWriter output = null;
         try {
             if (tabledetail.getRowCount() > 0) {
             output = new BufferedWriter(new FileWriter(path.toFile()));
             String line = "";
             for (int j = 0; j < tabledetail.getRowCount(); j++) {
               line = tabledetail.getValueAt(j, 0).toString() + "," +
                       tabledetail.getValueAt(j, 1).toString() + "," +
                       tabledetail.getValueAt(j, 2).toString() + "," +
                       tabledetail.getValueAt(j, 3).toString() + "," +
                       tabledetail.getValueAt(j, 4).toString() + "," +
                       tabledetail.getValueAt(j, 5).toString() + "," +
                       tabledetail.getValueAt(j, 6).toString() + "," +
                       tabledetail.getValueAt(j, 7).toString() + "," +
                       tabledetail.getValueAt(j, 8).toString() + "," +
                       tabledetail.getValueAt(j, 9).toString() + "," +
                       tabledetail.getValueAt(j, 10).toString() + "," +
                       tabledetail.getValueAt(j, 11).toString();    
               output.write(line + "\n");
             }    
             bsmf.MainFrame.show("File written to: " + path.toString());
             } 
         } catch (IOException ex) {
             bsmf.MainFrame.show("unable to write to: " + path.toString());
         } finally {
             if (output != null) {
                 try {
                     output.close();
                 } catch (IOException ex) {
                     bslog(ex);
                 }
             }
         }
    }//GEN-LAST:event_btdownloadActionPerformed

    private void tbkeyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbkeyFocusLost
        if (! isLoad) {
            if (btadd.isEnabled() && ! btupdate.isEnabled() && isValidDFSid(tbkey.getText())) {
                bsmf.MainFrame.show(getMessageTag(1180));
                tbkey.setText("");
                tbkey.requestFocus();
            }
        }
    }//GEN-LAST:event_tbkeyFocusLost

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
        String newkey = bsmf.MainFrame.input("Enter new key: ");
        if (! newkey.isBlank() && ! isValidDFSid(newkey)) {
         String[] m = addDFStructureTransaction(createDetRecord(newkey), createRecord(newkey));
         x = getDFSMstr(new String[]{newkey});  
         dfsdetlist = getDFSDet(newkey);
         updateForm();
        } else {
            bsmf.MainFrame.show("key is already in use");
        }
    }//GEN-LAST:event_btcopyActionPerformed

    private void btlookupeleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupeleActionPerformed
        lookUpFrameASCIIele();
    }//GEN-LAST:event_btlookupeleActionPerformed

    private void tbdelimiterFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdelimiterFocusLost
             String x = BlueSeerUtils.bsformat("", tbdelimiter.getText(), "0");
        if (x.equals("error")) {
            tbdelimiter.setText("");
            tbdelimiter.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbdelimiter.requestFocus();
        } else {
            tbdelimiter.setText(x);
            tbdelimiter.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbdelimiterFocusLost

    private void btinsertrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinsertrowActionPerformed
        int[] rows = tabledetail.getSelectedRows();
        if (rows != null && rows.length == 1  ) {
            detailmodel.insertRow(rows[0] + 1, new Object[]{ 
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ddalign.getSelectedItem().toString(),
                ddstatus.getSelectedItem().toString(),
                ddtype.getSelectedItem().toString()
            });
        }
    }//GEN-LAST:event_btinsertrowActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddrow;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleterow;
    private javax.swing.JButton btdown;
    private javax.swing.JButton btdownload;
    private javax.swing.JButton btimport;
    private javax.swing.JButton btinsertrow;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btlookupele;
    private javax.swing.JButton btnew;
    private javax.swing.JButton bttransform;
    private javax.swing.JButton btup;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdaterow;
    private javax.swing.JCheckBox cbisgroup;
    private javax.swing.JCheckBox cbislandmark;
    private javax.swing.JComboBox<String> ddalign;
    private javax.swing.JComboBox<String> dddoctype;
    private javax.swing.JComboBox<String> ddfiletype;
    private javax.swing.JComboBox<String> ddstatus;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbldesc;
    private javax.swing.JLabel lblid;
    private javax.swing.JPanel panelmaint;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField tbdelimiter;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbfield;
    private javax.swing.JTextField tbfielddesc;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbloopcount;
    private javax.swing.JTextField tbmax;
    private javax.swing.JTextField tbmin;
    private javax.swing.JTextField tbparent;
    private javax.swing.JTextField tbsegment;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
