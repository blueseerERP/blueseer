/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.prd;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.panelmap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 *
 * @author vaughnte
 */
public class TesterViewPanel extends javax.swing.JPanel {

    /**
     * Creates new form TesterViewPanel
     */
    
    
    
    final int MAX = 1000;
    public Dimension size;
    public Dimension panelsize;
    public Dimension scrollsize;
    public static String RESULT = "tubtraveler.pdf";
    public String userid = null;
    public Connection con = null;
    
    public String driver = "com.mysql.jdbc.Driver";
   
    public Boolean charterror = false;
     String fieldnames = "PartNo, ComputerName, DataDT, Data0, Data1,Data2," +
                "Data3, Data4, Data5, Data6, Data7, Data8, Data9," +
                "Data10, Data11, Data12, Data13, Data14, Data15, Data16, " +
               "Data17, Data18, Data19, Data20, Data21, " +
                "DataET, DataID, DataCP, DataPFV, DataBFV, " +
                "DataConnectors, DataLP1F, DataHP1F, DataLP2F, DataHP2F, " +
                "DataLP3F, DataHP3F, DataLP4F, DataHP4F, DataLP7F, " +
                "DataHP7F, DataLP8F, DataHP9F, DataLFAF, DataHFAF, " +
                "DataLFBF, DataHFBF, DataLET, DataHET, DataHCF, " +
                "DataHSF, DataHDF, Data_SL, Data_ETSL, Data_EL, " +
                "Data_EW, Data_TP, Data_P1P3CD, Data_P2P4CD, Data_PFOV, " +
                "index2, DataD2VMX, DataD1VMX, DataTouchVMX, DataVelVMX, " +
                "DataLD2VMX, DataHD2VMX, DataLD1VMX, DataHD1VMX, DataP7P8CD, " +
                "DataOilPart,DataP7, DataP8, CalcFC, DataDFM, DataDFV" ;
     String fieldnamesx = fieldnames.replace(" ","");
    
    
    public DefaultComboBoxModel mybox = new DefaultComboBoxModel(new String[]{""});
    javax.swing.table.DefaultTableModel blanktable = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "", "", "", ""
            });
    javax.swing.table.DefaultTableModel mytestermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "PartNo", "ComputerName", "DataDT", "Data0", "Data1", "Data2",
                "Data3", "Data4", "Data5", "Data6", "Data7", "Data8", "Data9",
                "Data10", "Data11", "Data12", "Data13", "Data14", "Data15", "Data16",
                "Data17", "Data18", "Data19", "Data20", "Data21",
                "DataET", "DataID", "DataCP", "DataPFV", "DataBFV",
                "DataConnectors", "DataLP1F", "DataHP1F", "DataLP2F", "DataHP2F",
                "DataLP3F", "DataHP3F", "DataLP4F", "DataHP4F", "DataLP7F",
                "DataHP7F", "DataLP8F", "DataHP9F", "DataLFAF", "DataHFAF",
                "DataLFBF", "DataHFBF", "DataLET", "DataHET", "DataHCF",
                "DataHSF", "DataHDF", "Data_SL", "Data_ETSL", "Data_EL",
                "Data_EW", "Data_TP", "Data_P1P3CD", "Data_P2P4CD", "Data_PFOV",
                "index2", "DataD2VMX", "DataD1VMX", "DataTouchVMX", "DataVelVMX",
                "DataLD2VMX", "DataHD2VMX", "DataLD1VMX", "DataHD1VMX", "DataP7P8CD",
                "DataOilPart", "DataP7", "DataP8", "CalcFC", "DataDFM", "DataDFV"
            });
    javax.swing.table.DefaultTableModel mytestermodelessential = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "PartNo", "ComputerName", "DataDT", "DataP7", "DataP8", "DataD1VMX", "DataD2VMX"
            });
    
    
      class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       searchTestStand(dfdate.format(jDateChooser1.getDate()).toString(), dfdate.format(jDateChooser2.getDate()).toString(), jTextField1.getText().toString());

           /* 
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            //Sleep for at least one second to simulate "startup".
            try {
                Thread.sleep(1000 + random.nextInt(2000));
            } catch (InterruptedException ignore) {}
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            
            */
            
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            btsearch.setEnabled(true);
           
            jProgressBar1.setVisible(false);
        }
    }
  
     class TaskExport extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       searchTestStandExport(dfdate.format(jDateChooser1.getDate()).toString(), dfdate.format(jDateChooser2.getDate()).toString(), jTextField1.getText().toString());

           /* 
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            //Sleep for at least one second to simulate "startup".
            try {
                Thread.sleep(1000 + random.nextInt(2000));
            } catch (InterruptedException ignore) {}
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            
            */
            
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            btexport.setEnabled(true);
            jProgressBar1.setVisible(false);
             bsmf.MainFrame.show("File has been exported");
        }
    }
      
     class TaskChartIt extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      
        Chartit(dfdate.format(jDateChooser1.getDate()).toString(), dfdate.format(jDateChooser2.getDate()).toString(), jTextField1.getText().toString());
       
           /* 
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            //Sleep for at least one second to simulate "startup".
            try {
                Thread.sleep(1000 + random.nextInt(2000));
            } catch (InterruptedException ignore) {}
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            
            */
            
            return null;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            jButton3.setEnabled(true);
            jProgressBar1.setVisible(false);
            if (! charterror) {
        //    bsmf.MainFrame.reinitpanels("ChartViewPanel", true, "");
            }       
          
        }
    }
    
     public boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    
     public void searchTestStand(String date1, String date2, String mypart) {
        try {
          
            
            int i = 0;
            int mycount = 0;
            double calcFC = 0.0;
           // date1.concat(" 00:00:00");
           // date2.concat(" 23:59:59");
            mytestermodel.setRowCount(0);
            mytestermodelessential.setRowCount(0);
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                    
                
                
                /* lets check the count */
                 if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
                   res = st.executeQuery("select count(*) as 'mycount' from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   } else {
                     res = st.executeQuery("select count(*) as 'mycount' from teststand where ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " AND DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   }
                while (res.next()) {
                    mycount = Integer.valueOf((res.getString("mycount")));
                }
                
                if (mycount > 65534) {
                  JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "output exceeds 65535 records...choose new range");     
                } else {
                   
                if (jCheckBoxEssential.isSelected()) {
                    
                  if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
                   res = st.executeQuery("select PartNo, ComputerName, DataDT, DataP7, DataP8, DataD1VMX, DataD2VMX from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   } else {
                     res = st.executeQuery("select PartNo, ComputerName, DataDT, DataP7, DataP8, DataD1VMX, DataD2VMX from teststand where ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " AND DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   }
                while (res.next()) {
                    
                     i++;
                    if (i > 65534) {
                     JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "output exceeds 65535 records...choose new range");   
                      break;
                      
                    }
                    calcFC = (Double.valueOf(res.getString("DataP7")) + Double.valueOf(res.getString("DataP8"))) / 2;
                    mytestermodelessential.addRow(new Object[]{res.getString("PartNo"), res.getString("ComputerName"), res.getString("DataDT"),
                                         res.getString("DataP7"), res.getString("DataP8"), res.getString("DataD1VMX"), res.getString("DataD2VMX")        
                                      });
                }  
                    
                  jTable1.setModel(mytestermodelessential);  
                    
                } else {
                
                if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
                  //  res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + 
                   //                         " ;");
                   res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   } else {
                     res = st.executeQuery("select * from teststand where ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " AND DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   }
                while (res.next()) {
                    i++;
                    if (i > 65534) {
                     JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "output exceeds 65535 records...choose new range");   
                      break;
                    }
                    calcFC = (Double.valueOf(res.getString("DataP7")) + Double.valueOf(res.getString("DataP8"))) / 2;
                    mytestermodel.addRow(new Object[]{res.getString("PartNo"), res.getString("ComputerName"), res.getString("DataDT"),
                                         res.getString("Data0"), res.getString("Data1"), res.getString("Data2"), res.getString("Data3"),        
                    res.getString("Data4"), res.getString("Data5"), res.getString("Data6"), res.getString("Data7"), res.getString("Data8"),
                    res.getString("Data9"), res.getString("Data10"), res.getString("Data11"), res.getString("Data12"), res.getString("Data13"),
                    res.getString("Data14"), res.getString("Data15"), res.getString("Data16"), res.getString("Data17"), res.getString("Data18"),
                    res.getString("Data19"), res.getString("Data20"), res.getString("Data21"),
                    res.getString("DataET"), res.getString("DataID"), res.getString("DataCP"), res.getString("DataPFV"), res.getString("DataBFV"),
                res.getString("DataConnectors"), res.getString("DataLP1F"), res.getString("DataHP1F"), res.getString("DataLP2F"), res.getString("DataHP2F"),
                res.getString("DataLP3F"), res.getString("DataHP3F"), res.getString("DataLP4F"), res.getString("DataHP4F"), res.getString("DataLP7F"),
                res.getString("DataHP7F"), res.getString("DataLP8F"), res.getString("DataHP9F"), res.getString("DataLFAF"), res.getString("DataHFAF"),
                res.getString("DataLFBF"), res.getString("DataHFBF"), res.getString("DataLET"), res.getString("DataHET"), res.getString("DataHCF"),
                res.getString("DataHSF"), res.getString("DataHDF"), res.getString("Data_SL"), res.getString("Data_ETSL"), res.getString("Data_EL"),
                res.getString("Data_EW"), res.getString("Data_TP"), res.getString("Data_P1P3CD"), res.getString("Data_P2P4CD"), res.getString("Data_PFOV"),
                res.getString("index2"), res.getString("DataD2VMX"), res.getString("DataD1VMX"), res.getString("DataTouchVMX"), res.getString("DataVelVMX"),
                res.getString("DataLD2VMX"), res.getString("DataHD2VMX"), res.getString("DataLD1VMX"), res.getString("DataHD1VMX"), res.getString("DataP7P8CD"),
                res.getString("DataOilPart"), res.getString("DataP7"), res.getString("DataP8"), calcFC , res.getString("DataDFM"), res.getString("DataDFV")
                    });
                }
                jTable1.setModel(mytestermodel);
                } // else if not essential
                
               //JOptionPane.showMessageDialog(mydialog, date1 + "  " + date2);
               JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Found " + String.valueOf(i) + " Recs");
                }
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
     
     
     
     public void searchTestStandExport(String date1, String date2, String mypart) {
        try {
            
            double calcFC = 0.0;
            FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
         BufferedWriter output;
            
            
            int i = 0;
           // date1.concat(" 00:00:00");
           // date2.concat(" 23:59:59");
            mytestermodel.setRowCount(0);
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                             
                   
                if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
                  //  res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + 
                   //                         " ;");
                   res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   } else {
                     res = st.executeQuery("select * from teststand where ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " AND DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                   }
               
                 
                    try {
            output = new BufferedWriter(new FileWriter(f));
               String myheader = "PartNo, ComputerName, DataDT, Data0, Data1, Data2, " +
                "Data3, Data4, Data5, Data6, Data7, Data8, Data9," +
                "Data10, Data11, Data12, Data13, Data14, Data15, Data16, " +
               "Data17, Data18, Data19, Data20, Data21, " +
                "DataET, DataID, DataCP, DataPFV, DataBFV, " +
                "DataConnectors, DataLP1F, DataHP1F, DataLP2F, DataHP2F, " +
                "DataLP3F, DataHP3F, DataLP4F, DataHP4F, DataLP7F, " +
                "DataHP7F, DataLP8F, DataHP9F, DataLFAF, DataHFAF, " +
                "DataLFBF, DataHFBF, DataLET, DataHET, DataHCF, " +
                "DataHSF, DataHDF, Data_SL, Data_ETSL, Data_EL, " +
                "Data_EW, Data_TP, Data_P1P3CD, Data_P2P4CD, Data_PFOV, " +
                "index2, DataD2VMX, DataD1VMX, DataTouchVMX, DataVelVMX, " +
                "DataLD2VMX, DataHD2VMX, DataLD1VMX, DataHD1VMX, DataP7P8CD, " +
                "DataOilPart, DataP7, DataP8, CalcFC, DataDFM, DataDFV" ;
               output.write(myheader + '\n');
                while (res.next()) {
                    i++;
                    if (i > 65534) {
                     JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "output exceeds 65535 records...truncating");   
                      break;
                    }
                     calcFC = (Double.valueOf(res.getString("DataP7")) + Double.valueOf(res.getString("DataP8"))) / 2;
                 
                    String newstring = res.getString("PartNo") + "," + res.getString("ComputerName") + "," + res.getString("DataDT") + "," + 
                                       res.getString("Data0") + "," + res.getString("Data1") + "," + res.getString("Data2") + "," +
                            res.getString("Data3") + "," + res.getString("Data4") + "," + res.getString("Data5") + "," +
                            res.getString("Data6") + "," + res.getString("Data7") + "," + res.getString("Data8") + "," +
                            res.getString("Data9") + "," + res.getString("Data10") + "," + res.getString("Data11") + "," +
                            res.getString("Data12") + "," + res.getString("Data13") + "," + res.getString("Data14") + "," +
                             res.getString("Data15") + "," + res.getString("Data16") + "," + res.getString("Data17") + "," +
                             res.getString("Data18") + "," + res.getString("Data19") + "," + res.getString("Data20") + "," +
                             res.getString("Data21") + "," +
                    res.getString("DataET") + "," + res.getString("DataID") + "," + res.getString("DataCP") + "," + res.getString("DataPFV") + "," + res.getString("DataBFV") + "," +
                res.getString("DataConnectors") + "," + res.getString("DataLP1F") + "," + res.getString("DataHP1F") + "," + res.getString("DataLP2F") + "," + res.getString("DataHP2F") + "," +
                res.getString("DataLP3F") + "," + res.getString("DataHP3F") + "," + res.getString("DataLP4F") + "," + res.getString("DataHP4F") + "," + res.getString("DataLP7F") + "," +
                res.getString("DataHP7F") + "," + res.getString("DataLP8F") + "," + res.getString("DataHP9F") + "," + res.getString("DataLFAF") + "," + res.getString("DataHFAF") + "," +
                res.getString("DataLFBF") + "," + res.getString("DataHFBF") + "," + res.getString("DataLET") + "," + res.getString("DataHET") + "," + res.getString("DataHCF") + "," +
                res.getString("DataHSF") + "," + res.getString("DataHDF") + "," + res.getString("Data_SL") + "," + res.getString("Data_ETSL") + "," + res.getString("Data_EL") + "," +
                res.getString("Data_EW") + "," + res.getString("Data_TP") + "," + res.getString("Data_P1P3CD") + "," + res.getString("Data_P2P4CD") + "," + res.getString("Data_PFOV") + "," +
                res.getString("index2") + "," + res.getString("DataD2VMX") + "," + res.getString("DataD1VMX") + "," + res.getString("DataTouchVMX") + "," + res.getString("DataVelVMX") + "," +
                res.getString("DataLD2VMX") + "," + res.getString("DataHD2VMX") + "," + res.getString("DataLD1VMX") + "," + res.getString("DataHD1VMX") + "," + res.getString("DataP7P8CD") + "," +
                res.getString("DataOilPart") + "," + res.getString("DataP7") + "," + res.getString("DataP8") + "," + String.valueOf(calcFC) + "," + res.getString("DataDFM") + "," + res.getString("DataDFV") ;
                  
                    
                    output.write(newstring + '\n');
                }
                output.close();
               
                } catch (IOException ex) {
                 
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "unable to write to file");
                }   
              } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
      public void Chartit(String date1, String date2, String mypart) {
        try {
            
            File f = new File(bsmf.MainFrame.temp + "chart.jpg");
        if(f.exists()) { 
            f.delete();
        }
            
            
            double calcFC = 0.0;
            charterror = false;
      
         int mynominal = 0;
         if (!isParsableToInt(jTextNominalValue.getText())) {
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Integers Only in Nominal");
                    charterror = true;
                    return;
                }
         if (! jTextNominalValue.getText().isEmpty()) {
             mynominal = Integer.valueOf(jTextNominalValue.getText());
         }
            
            int i = 0;
           // date1.concat(" 00:00:00");
           // date2.concat(" 23:59:59");
            
            mytestermodel.setRowCount(0);
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

            
            // if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
             //      JOptionPane.showMessageDialog(mydialog, "Select a tester");
              //     charterror = true;
               //    return ;
            // }
            
             if (jcbDataField.getSelectedItem().toString().compareTo("PartNo") == 0 ||
                 jcbDataField.getSelectedItem().toString().compareTo("ComputerName") == 0 ||   
                 jcbDataField.getSelectedItem().toString().compareTo("DataDT") == 0 ) {
                 JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Select a valid output field");
                 charterror = true;  
                 return ;
                   
             }
             
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                             
                   //  res = st.executeQuery("select * from teststand where ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " AND DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                   //                       "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                 
                
                
                if (jcbTester.getSelectedItem().toString().compareTo("ALL") == 0) {
                     res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" +  " ;");
                } else {
                    res = st.executeQuery("select * from teststand where DataDT >= " + "'" + date1 + "'" + " AND DataDT <= " + "'" + date2 + "'" + " AND PartNo like " + 
                                          "'" + jTextField1.getText().toString() + "%" + "'" + " AND ComputerName = " + "'" + jcbTester.getSelectedItem().toString() + "'" + " ;");
                }
                      
            
            XYSeries series = new XYSeries(jcbDataField.getSelectedItem().toString() + " For DateRange: " + date1 + " to " + date2 );
             XYSeries nominal = new XYSeries("nominal");
          
              
              
               double y_value = 0.0;
               double x_value = 0.0;
               DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                while (res.next()) {
                    i++;
                     calcFC = (Double.valueOf(res.getString("DataP7")) + Double.valueOf(res.getString("DataP8"))) / 2;
                 
                   // x_value = dfdate.parse(res.getString("PartNo")).getTime();
                    x_value = i;
                    if (jcbDataField.getSelectedItem().toString().compareTo("CalcFC") == 0) {
                      y_value = calcFC;  
                    } else {
                    y_value = Double.valueOf(res.getString(jcbDataField.getSelectedItem().toString()));
                    }
                            //y_value = Double.valueOf(res.getString("Data3"));
                    series.add(x_value, y_value);
                    nominal.add(x_value, mynominal);
                   
                }
                
              
                
                XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(nominal);
        
        JFreeChart chart = ChartFactory.createXYLineChart(jcbDataField.getSelectedItem().toString() + " For Tester " + jcbTester.getSelectedItem().toString() + " For Part '" + jTextField1.getText() + "'", "Time", jcbDataField.getSelectedItem().toString(), dataset, PlotOrientation.VERTICAL, true, true, false);
        
        try {
        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"), chart, 800, 600);
        } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
        }
        ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "chart.jpg");
        myicon.getImage().flush();   
       // ChartViewPanel.jLabelChartImage.setIcon(myicon);
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "sql code does not execute");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public TesterViewPanel() {
        initComponents();
        jProgressBar1.setVisible(false);
        
        for (String myfield : fieldnamesx.split(",")) {
           jcbDataField.addItem(myfield); 
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        btsearch = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        btexport = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jcbTester = new javax.swing.JComboBox();
        jcbDataField = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jTextNominalValue = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jCheckBoxEssential = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        jDateChooser1.setDateFormatString("yyyy-MM-dd HH:mm:ss");

        jDateChooser2.setDateFormatString("yyyy-MM-dd HH:mm:ss");

        btsearch.setText("Search");
        btsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsearchActionPerformed(evt);
            }
        });

        jTextField1.setToolTipText("Search Parts that Begin With");

        btexport.setText("Export");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(211, 218, 200));
        jLabel1.setForeground(new java.awt.Color(226, 233, 235));
        jLabel1.setText("DateFrom");

        jLabel2.setBackground(new java.awt.Color(211, 218, 200));
        jLabel2.setForeground(new java.awt.Color(226, 233, 235));
        jLabel2.setText("DateTo");

        jLabel3.setBackground(new java.awt.Color(211, 218, 200));
        jLabel3.setForeground(new java.awt.Color(226, 233, 235));
        jLabel3.setText("PartFilter");

        jButton3.setText("Chart");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jcbTester.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "AVMTS002", "AVMTS003", "AVMTS004", "AVMTS005", "AVMTS006 ", "AVMTS007", "AVMTS008", "AVMTS009", "AVMTS010", "AVMTS011", "AVMTS012", "AVMTS013", "AVMTS014", "AVMTS015", "AVMTS016", "AVMTS017", "AVMTS018", "AVMTS019", "AVMTS020", "AVMTS021", "AVMTS022", "AVMTS023", "AVMTS024", "AVMTS025", "AVMTS026", "AVMTS027", "AVMTS028", "AVMTS029", "AVMTS030", "AVMTS031", "AVMTS034", "AVMTS035", "AVMTS050" }));

        jLabel5.setForeground(new java.awt.Color(241, 247, 248));
        jLabel5.setText("(charting Only) DataField");

        jTextNominalValue.setText("0");

        jLabel6.setForeground(new java.awt.Color(241, 247, 248));
        jLabel6.setText("(charting only) Nominal");

        jLabel7.setForeground(new java.awt.Color(241, 247, 248));
        jLabel7.setText("Tester");

        jCheckBoxEssential.setForeground(new java.awt.Color(248, 252, 253));
        jCheckBoxEssential.setSelected(true);
        jCheckBoxEssential.setText("EssentialOnly");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 602, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(1, 1, 1)
                        .addComponent(jTextNominalValue, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxEssential))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcbDataField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbTester, 0, 127, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btexport)
                        .addComponent(btsearch)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btsearch)
                            .addComponent(jcbTester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btexport)
                            .addComponent(jcbDataField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jCheckBoxEssential))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextNominalValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addComponent(jButton3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsearchActionPerformed
        jProgressBar1.setVisible(true);
        jProgressBar1.setIndeterminate(true);
        btsearch.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        Task task = new Task();
        // task.addPropertyChangeListener(this);
        task.execute();

    }//GEN-LAST:event_btsearchActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
 jProgressBar1.setVisible(true);
        jProgressBar1.setIndeterminate(true);
        btexport.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        
        TaskExport task = new TaskExport();
        // task.addPropertyChangeListener(this);
        task.execute();
       
    }//GEN-LAST:event_btexportActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jProgressBar1.setVisible(true);
        jProgressBar1.setIndeterminate(true);
        jButton3.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        
        TaskChartIt task = new TaskChartIt();
        // task.addPropertyChangeListener(this);
        task.execute();
        
        

    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btexport;
    private javax.swing.JButton btsearch;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBoxEssential;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextNominalValue;
    private javax.swing.JComboBox jcbDataField;
    private javax.swing.JComboBox jcbTester;
    // End of variables declaration//GEN-END:variables
}
