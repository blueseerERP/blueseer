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
package com.blueseer.sch;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author terryva
 */
public class schData {
    
    public static plan_mstr getPlanMstr(String[] x) {
        plan_mstr r = null;
        String[] m = new String[2];
        String sql = "select * from plan_mstr where plan_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new plan_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new plan_mstr(m, res.getString("plan_nbr"), res.getString("plan_item"), res.getString("plan_site"),
                    res.getString("plan_qty_req").replace('.',defaultDecimalSeparator), res.getString("plan_qty_comp").replace('.',defaultDecimalSeparator), res.getString("plan_qty_sched").replace('.',defaultDecimalSeparator), 
                    res.getString("plan_date_create"), res.getString("plan_date_due"), res.getString("plan_date_sched"),
                    res.getString("plan_status"), res.getString("plan_rmks"), res.getString("plan_order"), res.getString("plan_line"), 
                    res.getString("plan_type"), res.getString("plan_cell"), res.getString("plan_is_sched")
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new plan_mstr(m);
        }
        return r;
    }
    
    
    // misc functions 
    public static String getPlanItem(String serialno) {

          // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
          // assume it's false i.e. hasn't been scanned.
        String x = "";
        String sql = "select plan_item from plan_mstr where plan_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, serialno);  
            try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
                   x = res.getString("plan_item");
               }
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        } 
      return x;
  }
    
    public static double getPlanSchedQty(String serialno) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      double myreturn = 0;

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
             res = st.executeQuery("select plan_qty_sched from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                     + " ;");
           while (res.next()) {
               myreturn = res.getDouble("plan_qty_sched");
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
                con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
      return myreturn;
  }

    public static double getPlanDetTotQtyByOp(String serialno, String op) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      double x = 0;

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
             res = st.executeQuery("select sum(pland_qty) as 'mysum' from pland_mstr where pland_parent = " + "'" + serialno + "'" 
                     + " AND pland_op = " + "'" + op + "'"
                     + " ;");
           while (res.next()) {
               x = res.getDouble("mysum");
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
                con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
      return x;
  }
       
    public static int getPlanStatus(String serialno) {
          
          // -1 plan_status is void
          // 0 plan_status is open
          // 1 plan_status is closed
          
          int myreturn = 0;
          
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
                 res = st.executeQuery("select plan_status from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                   myreturn = res.getInt("plan_status");
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
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
    
    public static String getPlanStatusMnemonic(int status) {
          
          // -1 plan_status is void
          // 0 plan_status is open
          // 1 plan_status is closed/complete
          
          String x = "unknown";
          if (status == 0) {
              x = getGlobalProgTag("open");
          }
          if (status == -1) {
              x = getGlobalProgTag("void");
          }
          if (status == 1) {
              x = getGlobalProgTag("complete");
          }
          return x;
      }
     
    public static String getPlanIsSchedMnemonic(int issched) {
          
          // 
          // 0 plan_is_sched is no
          // 1 plan_is_sched is yes
          
          String x = "unknown";
          if (issched == 0) {
              x = "no";
          }
          if (issched == 1) {
              x = "yes";
          }
          return x;
      }
          
    public static void updatePlanStatus(String serialno, String value) {
          try {

            
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_status = " + "'" + value + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
      
    public static void updatePlanQty(String serialno, double qty) {
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_qty_comp = " + "'" + qty + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
      
    public static void updatePlanQtyByOp(String serialno, int qty, String op, String ref, String cell) {
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_qty_comp = " + "'" + qty + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
          
    public static boolean isPlan(String serialno) {
          
          // From perspective of "does it exist"
          // assume it's false i.e. it doesnt exist.
          boolean myreturn = false;
          int i = 0;
          
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
                 res = st.executeQuery("select plan_nbr from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                 i++; 
               }
               if (i > 0) {
                   myreturn = true;
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
     
    public static String orderPlanStatus(String order) {
      String x = "unknown";
      int summation = 0;
      int scheduled = 0;
      int linecount = 0;
      int nullcount = 0;
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
             res = st.executeQuery("select sod_nbr, sod_line, plan_order, plan_line, plan_status, plan_is_sched " +
                     " from sod_det " +
                     " left outer join plan_mstr on plan_order = sod_nbr and plan_line = sod_line " +
                     " where sod_nbr = " + "'" + order + "'" 
                     + " ;");
           while (res.next()) {
               linecount++;
               if (res.getString("plan_status") == null) {
                   nullcount++;
               } else {
                   summation += res.getInt("plan_status");
                   scheduled += res.getInt("plan_is_sched");
               }


           }

           if (summation == linecount && scheduled == linecount && nullcount == 0) {
               x = "complete";
           }
           if (summation == 0 && nullcount == 0 && scheduled == linecount) {
               x = "planned";
           }
           if (summation == 0 && nullcount >= 0 && scheduled == 0) {
               x = "unplanned";
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
                con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
      return x;
  }

    public static boolean updatePlanOrder(String order, String schedqty, String cell, String scheddate, String status) {
      boolean myreturn = false;  
      try {

           if (status.equals(getGlobalProgTag("open"))) { status = "0"; }
           if (status.equals(getGlobalProgTag("close"))) { status = "1"; }
           if (status.equals(getGlobalProgTag("void"))) { status = "-1"; }


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
             res = st.executeQuery("select plan_status from plan_mstr where plan_nbr = " + "'" + order + "'" 
                     + " ;");
           while (res.next()) {
                if (res.getInt("plan_status") > 0 || res.getInt("plan_status") < 0)
                    proceed = false;
           }

           if (proceed) {
                    st.executeUpdate("update plan_mstr set "
                        + "plan_cell =  " + "'" + cell + "'" + ","
                        + "plan_qty_sched =  " + "'" + schedqty.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "plan_date_sched =  " + "'" + scheddate + "'" + ","        
                        + "plan_status = " + "'" + status + "'"
                        + " where plan_nbr = " + "'" + order + "'" 
                        + ";");
                    myreturn = true;
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
                con.close();
            }
    } catch (Exception e) {
        MainFrame.bslog(e);
    }
      return myreturn;
  }

    
     public record plan_mstr(String[] m, String plan_nbr, String plan_item,
        String plan_site, String plan_qty_req, String plan_qty_comp, String plan_qty_sched,
        String plan_date_create, String plan_date_due, String plan_date_sched, String plan_status,
        String plan_rmks, String plan_order, String plan_line, String plan_type, String plan_cell,
        String plan_is_sched) {
        public plan_mstr(String[] m) {
            this(m, "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "");
        }
    }
     
    
    
}
