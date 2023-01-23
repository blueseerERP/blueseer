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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.edi.EDIMap.mxs;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 *
 * @author terryva
 */
public class MapperUtils {
   
    @EDI.AnnoDoc(desc = {"method pads value (String) with leading zeros as defined by padCount",
                        "Example:  padNumber(\"5343\",8) returns: 00005343"},
            params = {"String targetValue","Integer padCount"})  
    public static String padNumber(String invalue, int padCount) {
        String format = "%0" + String.valueOf(padCount) + "d";
        return String.format(format,Integer.valueOf(invalue));
    }
 
    @EDI.AnnoDoc(desc = {"method pads value (Integer) with leading zeros as defined by padCount",
                        "Example:  padNumber(5343,8) returns: 00005343"},
            params = {"Integer targetValue","Integer padCount"})  
    public static String padNumber(int invalue, int padCount) {
        String format = "%0" + String.valueOf(padCount) + "d";
        return String.format(format,Integer.valueOf(invalue));
    }
 
    @EDI.AnnoDoc(desc = {"method checks to see if value is numeric (integer or double)",
                        "Example:  isNumber(\"445.55\") returns: true",
                        "Example:  isNumber(\"a123\") returns: false"},
            params = {"String targetValue"})
    public static boolean isNumber(String i) {
        boolean r;
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            r = false;
        }
        
        try {
            Double.parseDouble(i);
            return true;
        } catch (NumberFormatException nfe) {
            r = false;
        }
        return r;
    }
    
    
    @EDI.AnnoDoc(desc = {"method converts targetValue to a String",
                        "Example:  string(55) returns: \"55\" as String"},
            params = {"String targetValue"})  
    public static String snum(Object invalue) {
        if (invalue == null) {
		  return "";	
        }
        if (invalue instanceof Integer) {
		  return String.valueOf((Integer) invalue);	
        }
        if (invalue instanceof Double) {
               //   System.out.println("double...");
                  return String.valueOf((Double) invalue);	
        }
		return invalue.toString();
    }

    @EDI.AnnoDoc(desc = {"method formats parameter 1 (passed as String) to a specified number of decimals places (precision).",
                        "Example:  bsformat(\"4.3552\",\"3\") returns: 4.355"},
            params = {"String targetValue","String precision"})  
    public static String formatNumber(String invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        
        if (invalue.isEmpty()) {
           return "0";
        }
        if (precision.equals("2")) {
         pattern = "#0.00"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000";    
        } else if (precision.equals("0")) {
         pattern = "#0";   
        } else if (precision.equals("1")) {
         pattern = "#0.0";   
        } else {
         pattern = "#0.00";    
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        try {   
            outvalue = df.format(df.parse(invalue));
        } catch (ParseException ex) {
            outvalue = "error";
        }
       
        return outvalue;
    }
    
    @EDI.AnnoDoc(desc = {"method formats parameter 1 (passed as Double) to a specified number of decimals places (precision).",
                        "Example:  bsformat(4.3552,\"3\") returns: 4.355"},
            params = {"Double targetValue","String precision"})  
    public static String formatNumber(Double invalue, String precision) {
        String pattern = "";
        String outvalue = "";
        
        if (invalue == null) {
           return "0";
        }
        if (precision.equals("2")) {
         pattern = "#0.00"; 
        } else if (precision.equals("3")) {
         pattern = "#0.000";  
        } else if (precision.equals("4")) {
         pattern = "#0.0000";   
        } else if (precision.equals("5")) {
         pattern = "#0.00000";    
        } else if (precision.equals("0")) {
         pattern = "#0";   
        } else if (precision.equals("1")) {
         pattern = "#0.0";   
        } else {
         pattern = "#0.00";    
        }
       
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());    
        df.applyPattern(pattern);
        outvalue = df.format(invalue); 
        return outvalue;
       
    }
    
    
    @EDI.AnnoDoc(desc = {"method returns current datetime in format: yyyyMMddHHmmssSSS."},
            params = {"None"})  
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
    
    @EDI.AnnoDoc(desc = {"method converts date to standard mysql date format 'yyyy-mm-dd'.",
                        "Example:  convertDate(\"yyyyMMdd\",\"20170101\") returns: 2017-01-01",
                        "Supports only yyyyMMdd, yyMMdd, MMddyy, yyyy-MM-dd hh:mm:ss "},
            params = {"String format","String inValue"})  
    public static String convertDate(String format, String indate) {
       String mydate = "";
        if (format.equals("yyyyMMdd") && indate.length() == 8) {
           mydate = indate.substring(0,4) + "-" + indate.substring(4,6) + "-" + indate.substring(6);
        }
        if (format.equals("yyMMdd") && indate.length() == 6 ) {
           mydate = "20" + indate.substring(0,2) + "-" + indate.substring(2,4) + "-" + indate.substring(4);
        }
        if (format.equals("MMddyy") && indate.length() == 6 ) {
           mydate = "20" + indate.substring(4) + "-" + indate.substring(0,2) + "-" + indate.substring(2,4);
        }
        if (format.equals("yyyy-MM-dd hh:mm:ss") && indate.length() == 19) {
           mydate = indate.substring(0,10);
        }
       return mydate;
    }
    
    @EDI.AnnoDoc(desc = {"method adds/subtracts days to source date.  Output date format is governed by 2nd parameter",
                        "Example:  addDays(\"20210101\", \"yyyyMMdd\",5) returns: 20210106",
                        "Example:  addDays(\"20210101\", \"yyyyMMdd\",-5) returns: 20201226",
                        "NOTE:  input date format and output date format must match!!",
                        "NOTE:  date format can be yyyyMMdd or yyyy-MM-dd",
                        "NOTE:  if cannot be parsed, returns original date string"},
            params = {"String date", "String format","long Days"})  
    public static String addDays(String date, String format, long days) {
         LocalDate x;
         try {
              x = LocalDate.parse(date, DateTimeFormatter.ofPattern(format)); 
              x = x.plusDays(days);
              return x.format(DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException ex) {
              return date;
            }
    }
    
    
    
@EDI.AnnoDoc(desc = {"method writes out a message to the log table for this transaction event",
                     "Example:  write(\"some message\");"},
                 params = {"String messg"})     
    public static void write(String messg)  {
        mxs.add(new String[]{"map",messg});
    }
    

    @EDI.AnnoDoc(desc = {"method lookups a generic code_mstr (xref) data with field1 and returns string of field2",
                        "Example:  getXREF(\"key1\") returns: 2nd field of code_mstr table",
                        "generic codes can be imported with Mass Data Import utility"},
            params = {"String key1"})  
    public static String getXREF(String field1) {
      String x = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + field1 + "'" + ";");
                           
               while (res.next()) {
                    x = res.getString("code_key");
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    @EDI.AnnoDoc(desc = {"method lookups a generic code_mstr (xref) data with field1 and field2 returning field3",
                        "Example:  getXREF(\"key1\",\"key2\") returns: 3nd field of code_mstr table",
                        "generic codes can be imported with Mass Data Import utility"},
            params = {"String key1, String key2"})  
    public static String getXREF(String field1, String field2) {
      String x = "";
        try{
            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                res = st.executeQuery("select code_key from code_mstr where code_code = " + "'" + field1 + "'" +
                        " and code_key = " + "'" + field2 + "'" + ";");
                           
               while (res.next()) {
                    x = res.getString("code_value");
                }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    @EDI.AnnoDoc(desc = {"method checks to see if parameter is not empty and not null...returns boolean",
                     "Example:  isOK(\"somestring\");  ...returns true if not empty and not null...otherwise false"},
                 params = {"String messg"})     
    public static boolean isOK(String var)  {
        return (var != null && ! var.isEmpty());
    }
    
    
}
