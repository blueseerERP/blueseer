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
package com.blueseer.hrm;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author terryva
 */
public class hrmData {
    
    public static String[] addEmployeeMstr(emp_mstr x) {
        String[] m = new String[2];
        String sqlSelect = "select * from emp_mstr where emp_nbr = ?";
        String sqlInsert = "insert into emp_mstr (emp_nbr, emp_lname, emp_fname, "
                        + " emp_mname, emp_dept, emp_status, emp_startdate, emp_shift, emp_type, "
                            + " emp_gender, emp_jobtitle, emp_ssn, emp_autoclock, emp_active, emp_rate, emp_profile, "
                            + " emp_acct, emp_routing, emp_payfrequency, emp_efla_days, "
                            + " emp_vac_days, emp_vac_taken, emp_addrline1, emp_addrline2, emp_city, "
                            + " emp_state, emp_country, emp_zip, emp_phone, emp_emer_contact, emp_emer_phone, emp_dob, emp_termdate, emp_clockin, emp_supervisor) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); "; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setString(1, x.emp_nbr);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setString(1, x.emp_nbr);
            psi.setString(2, x.emp_lname);
            psi.setString(3, x.emp_fname);
            psi.setString(4, x.emp_mname);
            psi.setString(5, x.emp_dept);
            psi.setString(6, x.emp_status);
            psi.setString(7, x.emp_startdate);
            psi.setString(8, x.emp_shift);
            psi.setString(9, x.emp_type);
            psi.setString(10, x.emp_gender);
            psi.setString(11, x.emp_jobtitle);
            psi.setString(12, x.emp_ssn);
            psi.setString(13, x.emp_autoclock);
            psi.setString(14, x.emp_active);
            psi.setString(15, x.emp_rate);
            psi.setString(16, x.emp_profile);
            psi.setString(17, x.emp_acct);
            psi.setString(18, x.emp_routing);
            psi.setString(19, x.emp_payfrequency);
            psi.setString(20, x.emp_efla_days);
            psi.setString(21, x.emp_vac_days);
            psi.setString(22, x.emp_vac_taken);
            psi.setString(23, x.emp_addrline1);
            psi.setString(24, x.emp_addrline2);
            psi.setString(25, x.emp_city);
            psi.setString(26, x.emp_state);
            psi.setString(27, x.emp_country);
            psi.setString(28, x.emp_zip);
            psi.setString(29, x.emp_phone);
            psi.setString(30, x.emp_emer_contact);
            psi.setString(31, x.emp_emer_phone);
            psi.setString(32, x.emp_dob);
            psi.setString(33, x.emp_termdate);
            psi.setString(34, x.emp_clockin);
            psi.setString(35, x.emp_supervisor);
            
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
        
    public static String[] updateEmployeeMstr(emp_mstr x) {
        System.out.println(x.emp_nbr() + "/" + x.emp_dept);
        String[] m = new String[2];
        String sql = "update emp_mstr set emp_lname = ?, emp_fname = ?, "
                        + " emp_mname = ?, emp_dept = ?, emp_status = ?, emp_startdate = ?, emp_shift = ?, emp_type = ?, "
                            + " emp_gender = ?, emp_jobtitle = ?, emp_ssn = ?, emp_autoclock = ?, emp_active = ?, emp_rate = ?, emp_profile = ?, "
                            + " emp_acct = ?, emp_routing = ?, emp_payfrequency = ?, emp_efla_days = ?, "
                            + " emp_vac_days = ?, emp_vac_taken = ?, emp_addrline1 = ?, emp_addrline2 = ?, emp_city = ?, "
                            + " emp_state = ?, emp_country = ?, emp_zip = ?, emp_phone = ?, emp_emer_contact = ?, emp_emer_phone = ?, emp_dob = ?, emp_termdate = ?, emp_supervisor = ? " +
                              " where emp_nbr = ? ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(34, x.emp_nbr);
            ps.setString(1, x.emp_lname);
            ps.setString(2, x.emp_fname);
            ps.setString(3, x.emp_mname);
            ps.setString(4, x.emp_dept);
            ps.setString(5, x.emp_status);
            ps.setString(6, x.emp_startdate);
            ps.setString(7, x.emp_shift);
            ps.setString(8, x.emp_type);
            ps.setString(9, x.emp_gender);
            ps.setString(10, x.emp_jobtitle);
            ps.setString(11, x.emp_ssn);
            ps.setString(12, x.emp_autoclock);
            ps.setString(13, x.emp_active);
            ps.setString(14, x.emp_rate);
            ps.setString(15, x.emp_profile);
            ps.setString(16, x.emp_acct);
            ps.setString(17, x.emp_routing);
            ps.setString(18, x.emp_payfrequency);
            ps.setString(19, x.emp_efla_days);
            ps.setString(20, x.emp_vac_days);
            ps.setString(21, x.emp_vac_taken);
            ps.setString(22, x.emp_addrline1);
            ps.setString(23, x.emp_addrline2);
            ps.setString(24, x.emp_city);
            ps.setString(25, x.emp_state);
            ps.setString(26, x.emp_country);
            ps.setString(27, x.emp_zip);
            ps.setString(28, x.emp_phone);
            ps.setString(29, x.emp_emer_contact);
            ps.setString(30, x.emp_emer_phone);
            ps.setString(31, x.emp_dob);
            ps.setString(32, x.emp_termdate);
            ps.setString(33, x.emp_supervisor);
            // we do not update emp_clockin
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] deleteEmployeeMstr(emp_mstr x) { 
       String[] m = new String[2];
        String sql = "delete from emp_mstr where emp_nbr = ?; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, x.emp_nbr);
        int rows = ps.executeUpdate();
        m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
      
    public static emp_mstr getEmployeeMstr(String[] x) {
        emp_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from emp_mstr where emp_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new emp_mstr(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new emp_mstr(m, 
                        res.getString("emp_nbr"), 
                        res.getString("emp_lname"),
                        res.getString("emp_fname"),
                        res.getString("emp_mname"),
                        res.getString("emp_dept"),
                        res.getString("emp_status"),
                        res.getString("emp_startdate"),
                        res.getString("emp_shift"),
                        res.getString("emp_type"),
                        res.getString("emp_gender"),
                        res.getString("emp_jobtitle"),
                        res.getString("emp_ssn"),
                        res.getString("emp_autoclock"),
                        res.getString("emp_active"),
                        res.getString("emp_rate"),
                        res.getString("emp_profile"),
                        res.getString("emp_acct"),
                        res.getString("emp_routing"),
                        res.getString("emp_payfrequency"),
                        res.getString("emp_efla_days"),
                        res.getString("emp_vac_days"),
                        res.getString("emp_vac_taken"),
                        res.getString("emp_addrline1"),
                        res.getString("emp_addrline2"),
                        res.getString("emp_city"),
                        res.getString("emp_state"),
                        res.getString("emp_country"),
                        res.getString("emp_zip"),
                        res.getString("emp_phone"),
                        res.getString("emp_emer_contact"),
                        res.getString("emp_emer_phone"),
                        res.getString("emp_dob"),
                        res.getString("emp_termdate"),
                        res.getString("emp_clockin"),
                        res.getString("emp_supervisor")        
                        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new emp_mstr(m);
        }
        return r;
    }
   
    public static ArrayList<emp_exception> getEmployeeExceptions(String[] x) {
        ArrayList<emp_exception> list = new ArrayList<emp_exception>();
        emp_exception r = null;
        String[] m = new String[2];
        String sql = "select * from emp_exception where empx_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new emp_exception(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new emp_exception(m, 
                        res.getString("empx_nbr"), 
                        res.getString("empx_desc"),
                        res.getString("empx_type"),
                        res.getString("empx_acct"),
                        res.getString("empx_cc"),
                        res.getString("empx_amttype"),
                        res.getString("empx_amt")   
                        );
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new emp_exception(m);
        }
        return list;
    }
   
    // misc
    public static ArrayList<String[]> getHRInit() {
        String defaultsite = "";
        ArrayList<String[]> lines = new ArrayList<String[]>();
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
        
            
            res = st.executeQuery("select ov_site, ov_currency from ov_mstr;" );
            while (res.next()) {
               String[] s = new String[2];
               s[0] = "currency";
               s[1] = res.getString("ov_currency");
               lines.add(s);
               s = new String[2];
               s[0] = "site";
               s[1] = res.getString("ov_site");
               lines.add(s);
               defaultsite = s[1];
            }
            
             res = st.executeQuery("select site_site from site_mstr;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "sites";
               s[1] = res.getString("site_site");
               lines.add(s);
            }
            
            res = st.executeQuery("select shf_id from shift_mstr;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "shifts";
               s[1] = res.getString("shf_id");
               lines.add(s);
            }
            
            res = st.executeQuery("select dept_id from dept_mstr;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "departments";
               s[1] = res.getString("dept_id");
               lines.add(s);
            }
            
           
            res = st.executeQuery("select code_key from code_mstr where code_code = 'state' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "states";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
            
            
            res = st.executeQuery("select code_key from code_mstr where code_code = 'country' order by code_key ;");
            while (res.next()) {
                String[] s = new String[2];
               s[0] = "countries";
               s[1] = res.getString("code_key");
               lines.add(s);
            }
           
            
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
        }
    }
    catch (Exception e){
        MainFrame.bslog(e);
    }
        return lines;
    }

    public static String getEmpFormalNameByID(String id) {
        String x = "";
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
                res = st.executeQuery("select emp_lname, emp_fname from emp_mstr " +
                        " where emp_nbr = " + "'" + id + "'" + ";");
                while (res.next()) {
                    x = res.getString("emp_lname") + ", " + res.getString("emp_fname");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;

    }
    
    public static ArrayList getEmpFormalNameByID(ArrayList<String> list) {
        ArrayList myarray = new ArrayList();
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
                for (String empid : list) {
                    res = st.executeQuery("select emp_nbr, emp_lname, emp_fname from emp_mstr " +
                        " where emp_active = '1' and emp_nbr = " + "'" + empid + "'" +
                        " order by emp_lname ;");
                    while (res.next()) {
                        myarray.add(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                    }
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    public static String getEmpIDByFormalName(String formalname) {
        String x = "";
        String[] fn = formalname.split(",", -1);
        if (fn == null || fn.length < 2) {
            return "";
        }
        
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
                res = st.executeQuery("select emp_nbr from emp_mstr " +
                        " where emp_lname = " + "'" + fn[0].trim() + "'" +
                        " and emp_fname = " + "'" + fn[1].trim() + "'" + ";");
                while (res.next()) {
                    x = res.getString("emp_nbr");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;

    }
    
    
    public static ArrayList getempmstrlist() {
        ArrayList myarray = new ArrayList();
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
                res = st.executeQuery("select emp_nbr from emp_mstr order by emp_nbr ;");
                while (res.next()) {
                    myarray.add(res.getString("emp_nbr"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getEmpNameByDept(String dept) {
        ArrayList myarray = new ArrayList();
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
                res = st.executeQuery("select emp_nbr, emp_lname, emp_fname from emp_mstr " +
                        " where emp_dept = " + "'" + dept + "'" + " order by emp_lname ;");
                while (res.next()) {
                    myarray.add(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    public static ArrayList getEmpNameAll() {
        ArrayList myarray = new ArrayList();
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
                res = st.executeQuery("select emp_nbr, emp_lname, emp_fname from emp_mstr " +
                        " where emp_active = '1' " +
                        " order by emp_lname ;");
                while (res.next()) {
                    myarray.add(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return myarray;

    }

    
    public static boolean isValidEmployeeID(String id) {
        boolean x = false;
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
                res = st.executeQuery("select emp_nbr from emp_mstr where emp_nbr = " + "'" + id + "'" + ";");
                if (res.isBeforeFirst()) {
                    x = true;
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;

    }

    
    public record emp_mstr(String[] m, String emp_nbr, String emp_lname, String emp_fname,
                     String emp_mname, String emp_dept, String emp_status, String emp_startdate, 
                     String emp_shift, String emp_type, String emp_gender, String emp_jobtitle, 
                     String emp_ssn, String emp_autoclock, String emp_active, String emp_rate, 
                     String emp_profile, String emp_acct, String emp_routing, String emp_payfrequency, 
                     String emp_efla_days, String emp_vac_days, String emp_vac_taken, String emp_addrline1, 
                     String emp_addrline2, String emp_city, String emp_state, String emp_country, 
                     String emp_zip, String emp_phone, String emp_emer_contact, String emp_emer_phone, 
                     String emp_dob, String emp_termdate, String emp_clockin, String emp_supervisor) {
        public emp_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "");
        }
    }
    
    public record emp_exception(String[] m, String empx_nbr, String empx_desc, String empx_type,
        String empx_acct, String empx_cc, String empx_amttype, String empx_amt) {
        public emp_exception(String[] m) {
            this(m, "", "", "", "", "", "", "");
        }
    }
    
    
}
