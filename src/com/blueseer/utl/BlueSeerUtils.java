/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
package com.blueseer.utl;
import bsmf.MainFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 *
 * @author vaughnte
 */
public class BlueSeerUtils {
    
    public static DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public static ImageIcon clickflag = new ImageIcon(BlueSeerUtils.class.getResource("/images/flag.png")); 
    public static ImageIcon clickbasket = new ImageIcon(BlueSeerUtils.class.getResource("/images/basket.png")); 
    public static  ImageIcon clickprint = new ImageIcon(BlueSeerUtils.class.getResource("/images/print.png"));
    public static  ImageIcon clickclock = new ImageIcon(BlueSeerUtils.class.getResource("/images/clock.png"));
    public static  ImageIcon clickchart = new ImageIcon(BlueSeerUtils.class.getResource("/images/chart.png"));
    public static  ImageIcon clickcoffee = new ImageIcon(BlueSeerUtils.class.getResource("/images/coffee.png"));
    public static  ImageIcon clickgear = new ImageIcon(BlueSeerUtils.class.getResource("/images/gear.png"));
    public static  ImageIcon clicktrash = new ImageIcon(BlueSeerUtils.class.getResource("/images/trash.png"));
    public static  ImageIcon clickrefresh = new ImageIcon(BlueSeerUtils.class.getResource("/images/refresh.png"));
    public static  ImageIcon clickvoid = new ImageIcon(BlueSeerUtils.class.getResource("/images/void.png"));
    public static  ImageIcon clickmail = new ImageIcon(BlueSeerUtils.class.getResource("/images/mail.png"));
    public static  ImageIcon clicklock = new ImageIcon(BlueSeerUtils.class.getResource("/images/lock.png"));
    public static  ImageIcon clickcheck = new ImageIcon(BlueSeerUtils.class.getResource("/images/check.png"));
    public static  ImageIcon clicknocheck = new ImageIcon(BlueSeerUtils.class.getResource("/images/nocheck.png"));
    
    public static String addRecordInit = "add mode";
    
    public static String getRecordSuccess = "update mode";
    public static String addRecordSuccess = "record added successfully";
    public static String updateRecordSuccess = "record updated successfully";
    public static String deleteRecordSuccess = "record deleted successfully";
    
    public static String getRecordError = "record not found";
    public static String addRecordError = "record not added";
    public static String updateRecordError = "record not updated";
    public static String deleteRecordError = "record not deleted";
    
    public static String addRecordAlreadyExists = "record already exists";
    public static String deleteRecordCanceled = "delete transaction cancelled";
    
    public static String getRecordSQLError = "sql error in function getRecord";
    public static String addRecordSQLError = "sql error in function addRecord";
    public static String updateRecordSQLError = "sql error in function updateRecord";
    public static String deleteRecordSQLError = "sql error in function deleteRecord";
    
    public static String getRecordConnError = "sql conn error in getRecord";
    public static String addRecordConnError = "sql conn error in addRecord";
    public static String updateRecordConnError = "sql conn error in updateRecord";
    public static String deleteRecordConnError = "sql conn error in deleteRecord";
    
    public static String SuccessBit = "0";
    public static String ErrorBit = "1";
    
    public static String bsformat(String type, String invalue, String precision) {
        String outvalue = "";
        DecimalFormat df = null;
        if (invalue.isEmpty()) {
            outvalue = "0";
            return "";
        }
       
        if (precision.equals("2")) {
         df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US)); 
        } else if (precision.equals("3")) {
         df = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.US));  
        } else if (precision.equals("4")) {
         df = new DecimalFormat("#0.0000", new DecimalFormatSymbols(Locale.US));   
        } else if (precision.equals("5")) {
         df = new DecimalFormat("#0.00000", new DecimalFormatSymbols(Locale.US));    
         } else if (precision.equals("0")) {
         df = new DecimalFormat("#0", new DecimalFormatSymbols(Locale.US));    
        } else {
         df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));    
        }
        try {
        outvalue = df.format(Double.valueOf(invalue));
        } catch(NumberFormatException e) {
            outvalue = "error";
        }
        return outvalue;
    }
    
    public static String convertDateFormat(String format, String indate) {
       String mydate = "";
        if (format.equals("yyyyMMdd") && indate.length() == 8) {
           mydate = indate.substring(0,4) + "-" + indate.substring(4,6) + "-" + indate.substring(6);
        }
        if (format.equals("yyMMdd") && indate.length() == 6 ) {
           mydate = "20" + indate.substring(0,2) + "-" + indate.substring(2,4) + "-" + indate.substring(4);
        }
        if (format.equals("yyyy-MM-dd hh:mm:ss") && indate.length() == 19) {
           mydate = indate.substring(0,10);
        }
       return mydate;
    }
    
  
    
    public static boolean isSet(ArrayList list, Integer index) {
     return index != null && index >=0 && index < list.size() && list.get(index) != null;
     }
    
    public static boolean isSet(String[] list, Integer index) {
     return index != null && index >=0 && index < list.length && list[index] != null;
     }
     
    public static boolean ConvertStringToBool(String i) {
        boolean b;
        if (i != null && i.equals("1") ) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }
     
    public static String ConvertIntToYesNo(int i) {
        String s;
        if (i == 1) {
            s = "YES";
        } else {
            s = "NO";
        }
        return s;
    }
      
    public static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    
    public static String xNull(String mystring) {
       String returnstring = "";
       returnstring = (mystring == null) ? "" : mystring;
       return returnstring;
   }
      
    public static String convertToX(boolean i) {
        String mystring = null;
        if (i) {
            mystring = "X";
        } else {
            mystring = "";
        }
        return mystring;

    }

    public static boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static boolean isParsableToDouble(String i) {
        try {
            Double.parseDouble(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public static String setDateFormat(Date date) {
       String mydate = "";
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       return mydate = sdf.format(date);
    }
            
    public static boolean isValidDateStr(String date) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setLenient(false);
      sdf.parse(date);
    }
    catch (ParseException e) {
      return false;
    }
    catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }
    
    public static boolean isMoneyFormat(String value) {
     boolean myreturn = false;
     int i = value.lastIndexOf('.');
     if(i != -1 && value.substring(i + 1).length() == 2)
         myreturn = true;
     return myreturn;
  }
    
    public class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {
        int rowHeight = 0;  // current max row height for this scan
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column)
        {
            setText((String) value);
            setWrapStyleWord(true);
            setLineWrap(true);
          //  setBackground(Color.YELLOW);

          // current table column width in pixels
        int colWidth = table.getColumnModel().getColumn(column).getWidth();

        // set the text area width (height doesn't matter here)
        setSize(new Dimension(colWidth, 1)); 

        // get the text area preferred height and add the row margin
        int height = getPreferredSize().height + table.getRowMargin();

            // ensure the row height fits the cell with most lines
            if (column == 0 || height > rowHeight) {
                table.setRowHeight(row, height);
                rowHeight = height;
            }
           return this;
            }

}
    
    public static class FormatRenderer extends DefaultTableCellRenderer
{
	private Format formatter;

	/*
	 *   Use the specified formatter to format the Object
	 */
	public FormatRenderer(Format formatter)
	{
		this.formatter = formatter;
	}

	public void setValue(Object value)
	{
		//  Format the Object before setting its value in the renderer

		try
		{
			if (value != null)
				value = formatter.format(value);
		}
		catch(IllegalArgumentException e) {}

		super.setValue(value);
	}

	/*
	 *  Use the default date/time formatter for the default locale
	 */
	
}
  
    public static class NumberRenderer extends FormatRenderer
{
	/*
	 *  Use the specified number formatter and right align the text
	 */
	public NumberRenderer(NumberFormat formatter)
	{
                
		super(formatter);
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(4);
		setHorizontalAlignment( SwingConstants.RIGHT );
                
	}

        
        public static NumberRenderer getNumberRenderer()
	{
		return new NumberRenderer( NumberFormat.getNumberInstance());
	}
	/*
	 *  Use the default currency formatter for the default locale
	 */
	public static NumberRenderer getCurrencyRenderer()
	{
         
          return new NumberRenderer( NumberFormat.getCurrencyInstance());
        }

	/*
	 *  Use the default integer formatter for the default locale
	 */
	public static NumberRenderer getIntegerRenderer()
	{
		return new NumberRenderer( NumberFormat.getIntegerInstance() );
	}

	/*
	 *  Use the default percent formatter for the default locale
	 */
	public static NumberRenderer getPercentRenderer()
	{
		return new NumberRenderer( NumberFormat.getPercentInstance() );
	}
}
  
    
    
     public static void startTask(String[] message) {
        bsmf.MainFrame.MainProgressBar.setVisible(true);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(true);
        bsmf.MainFrame.MainProgressBar.setBackground(Color.BLUE);
        message(message);
     }
     
     public static void endTask(String[] message) {
        bsmf.MainFrame.MainProgressBar.setVisible(false);
        bsmf.MainFrame.MainProgressBar.setIndeterminate(false);
        message(message);
     }
     
     public static void message(String[] message) {
         bsmf.MainFrame.messagelabel.setText(message[1]);
         if (message[0].equals("1")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.RED); 
         } else if (message[0].equals("2")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.decode("#006600")); 
         } else if (message[0].equals("3")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.decode("#6600CC")); 
         } else if (message[0].equals("0")) {
            bsmf.MainFrame.messagelabel.setForeground(Color.BLUE);  
         } else {
            bsmf.MainFrame.messagelabel.setForeground(Color.BLACK);   
         }
     }
     
     public static void messagereset() {
         bsmf.MainFrame.messagelabel.setForeground(Color.BLACK);
         bsmf.MainFrame.messagelabel.setText("");
     }
    
     
     
}
