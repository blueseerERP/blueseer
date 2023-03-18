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
package com.blueseer.utl;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.dbtype;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.edi.EDI;
import static com.blueseer.edi.EDI.escapeDelimiter;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 *
 * @author terryva
 */
public class EDData {
    
    
       
    public static boolean addEDIAttributeRecord(String sndid, String rcvid, String doc, String key, String value) {
        boolean myreturn = false;
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
                int i = 0;
                   res =  st.executeQuery("select exa_sndid from edi_attr where " +
                                           " exa_sndid = " + "'" + sndid + "'" +
                                           " and exa_rcvid = " + "'" + rcvid + "'" +
                                           " and exa_doc = " + "'" + doc + "'" +
                                           " and exa_key = " + "'" + key + "'" +
                                           ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                    st.executeUpdate(" insert into edi_attr " 
                      + "(exa_sndid, exa_rcvid, exa_doc, exa_key, exa_value ) " 
                      + " values ( " + 
                    "'" +  sndid + "'" + "," + 
                    "'" +  rcvid + "'" + "," +
                    "'" +  doc + "'" + "," +
                    "'" +  key + "'" + "," + 
                    "'" +  value + "'" 
                    +  ");"
                           );     
                   }
                   
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
    
    public static boolean addEDIPartner(ArrayList<String> list) {
                 boolean myreturn = false;
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
                int i = 0;
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(":", -1);
                    
                    
                   /* edp_partner */ 
                   res =  st.executeQuery("select edp_id from edp_partner where " +
                                           " edp_id = " + "'" + ld[0] + "'" + 
                                           ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                    st.executeUpdate(" insert into edp_partner " 
                      + "(edp_id, edp_desc ) " 
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'"  
                             +  ");"
                           );     
                   }
                    
                   /* edpd_partner */ 
                   res =  st.executeQuery("select edpd_parent from edpd_partner where " +
                                           " edpd_parent = " + "'" + ld[0] + "'" + 
                                           " and edpd_alias = " + "'" + ld[2] + "'" +
                                           ";");
                    j = 0;
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                    st.executeUpdate(" insert into edpd_partner " 
                      + "(edpd_parent, edpd_alias, edpd_default ) " 
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[2] + "'" + "," +
                    "'" + ld[3] + "'"
                             +  ");"
                           );     
                   } 
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
     
    public static boolean addEDIDocumentStructures(ArrayList<String> list) {
                 boolean myreturn = false;
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
                int i = 0;
                String[] ld = null;
                             
                               
                // now loop through comma delimited list and insert into item master table
                // skip if already in table.....keys are cust (cup_cust) and custitem (cup_citem)
                for (String rec : list) {
                    ld = rec.split(":", -1);
                    
                    
                   /* edp_partner */ 
                   res =  st.executeQuery("select edd_id from edi_doc where " +
                                           " edd_id = " + "'" + ld[0] + "'" + 
                                           ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                    st.executeUpdate(" insert into edi_doc " 
                      + "(edd_id, edd_desc, edd_type, edd_subtype, edd_segdelim, edd_priority, edd_landmark, edd_enabled ) " 
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +
                    "'" +  ld[3] + "'" + "," +
                    "'" +  ld[4] + "'" + "," +
                    "'" +  ld[5] + "'" + "," +
                    "'" +  ld[6] + "'" + "," +
                    "'" +  ld[7] + "'"         
                             +  ");"
                           );     
                   }
                    
                   /* edpd_partner */ 
                   res =  st.executeQuery("select edid_id from edi_docdet where " +
                                           " edid_id = " + "'" + ld[0] + "'" + 
                                           " and edid_role = " + "'" + ld[8] + "'" +
                                           " and edid_rectype = " + "'" + ld[9] + "'" +
                                           " and edid_valuetype = " + "'" + ld[10] + "'" +
                                           " and edid_tag = " + "'" + ld[16] + "'" +
                                           " and edid_value = " + "'" + ld[15] + "'" +        
                                           ";");
                    j = 0;
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                    st.executeUpdate(" insert into edi_docdet " 
                      + "(edid_id, edid_role, edid_rectype, edid_valuetype, edid_row, edid_col, edid_length, edid_regex, edid_value, edid_tag, edid_enabled ) " 
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[8] + "'" + "," +
                    "'" +  ld[9] + "'" + "," +
                    "'" +  ld[10] + "'" + "," +
                    "'" +  ld[11] + "'" + "," +
                    "'" +  ld[12] + "'" + "," +
                    "'" +  ld[13] + "'" + "," +
                    "'" +  ld[14] + "'" + "," +
                    "'" +  ld[15] + "'" + "," + 
                    "'" +  ld[16] + "'" + "," +
                    "'" + ld[17] + "'"
                             +  ");"
                           );     
                   } 
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
        
    public static boolean addEDIMstrRecord(ArrayList<String> list) {
                 boolean myreturn = false;
                  try {
            Class.forName(driver).newInstance();
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                String[] ld = null;
               
                for (String rec : list) {
                    ld = rec.split(":", -1);
                    
                   res =  st.executeQuery("select edi_id from edi_mstr where " +
                                           " edi_id = " + "'" + ld[0] + "'" + 
                                           " and edi_doc = " + "'" + ld[1] + "'" +
                                           " and edi_sndgs = " + "'" + ld[5] + "'" +
                                           " and edi_rcvgs = " + "'" + ld[8] + "'" +
                                           ";");
                    int j = 0;
                    while (res.next()) {
                        j++;
                    }
                    
                    
                    if (j == 0) {
                    st.executeUpdate(" insert into edi_mstr " 
                      + "(edi_id, edi_doc, edi_sndisa, edi_map, edi_sndq, edi_sndgs, edi_rcvisa, edi_rcvq, edi_rcvgs, " +
                        " edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, " +
                        " edi_filesuffix, edi_filepath, edi_version, edi_supcode, edi_doctypeout, edi_filetypeout, edi_ifs, edi_ofs, edi_fa_required ) "
                   + " values ( " + 
                    "'" +  ld[0] + "'" + "," + 
                    "'" +  ld[1] + "'" + "," +
                    "'" +  ld[2] + "'" + "," +  
                    "'" +  ld[3] + "'" + "," + 
                    "'" +  ld[4] + "'" + "," +
                    "'" +  ld[5] + "'" + "," +
                    "'" +  ld[6] + "'" + "," +
                    "'" +  ld[7] + "'" + "," +
                    "'" +  ld[8] + "'" + "," +
                    "'" +  ld[9] + "'" + "," +
                    "'" +  ld[10] + "'" + "," +
                    "'" +  ld[11] + "'" + "," +
                    "'" +  ld[12] + "'" + "," +
                    "'" +  ld[13] + "'" + "," +    
                    "'" +  ld[14] + "'" + "," +
                    "'" +  ld[15] + "'" + "," +
                    "'" +  ld[16] + "'" + "," + 
                    "'" +  ld[17] + "'" + "," + 
                    "'" +  ld[18] + "'" + "," + 
                    "'" +  ld[19] + "'" + "," + 
                    "'" +  ld[20] + "'" + "," +         
                    "'" +  ld[21] + "'"  
                             +  ");"
                           );     
                   }
                }    
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                myreturn = true;
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }  
                  return myreturn;
             } 
      
       
    public static String[] getDelimiters(String entity, String doctype) {
             String [] delimiters = new String[3];  // will hold 3 elements.... sd, ed, ud
           
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select * from edi_mstr where edi_id = " + "'" + entity + "'" + 
                        " AND edi_doc = " + "'" + doctype + "'" + ";");
                    while (res.next()) {
                       delimiters[0] = Character.toString((char) res.getInt("edi_segdelim") );
                       delimiters[1] = Character.toString((char) res.getInt("edi_eledelim") );
                       delimiters[2] = Character.toString((char) res.getInt("edi_subdelim") );
                    }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return delimiters;
       }
    
    public static HashMap<String, ArrayList<String[]>> getEDIFFSelectionRules() {
        HashMap<String, ArrayList<String[]>> hm = new HashMap<String, ArrayList<String[]>>();
        ArrayList<String> keys = new ArrayList<String>();  
        ArrayList<String[]> x = new ArrayList<String[]>();
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                
                res = st.executeQuery("select * from edi_doc  " +
                        " where edd_enabled = '1' "); 
                while (res.next()) {
                    keys.add(res.getString("edd_id"));
                }
                
                for (String z : keys) {
                    res = st.executeQuery("select * from edi_docdet " +
                            " inner join edi_doc on edd_id = edid_id " +
                            " where edid_enabled = '1' and edid_role = 'selection' and edid_id = " + "'" + z + "'"); 
                    while (res.next()) {
                           String[] s = new String[]{res.getString("edid_row"),
                                                     res.getString("edid_col"),
                                                     res.getString("edid_length"),
                                                     res.getString("edid_value"),
                                                     res.getString("edid_rectype"),
                                                     res.getString("edid_tag"),
                                                     res.getString("edid_xpath"),
                                                     res.getString("edid_tag"),
                                                     res.getString("edid_valuetype"),
                                                     res.getString("edd_segdelim"),
                                                     res.getString("edd_eledelim"),
                                                     res.getString("edid_regex")
                                                     
                           };
                           x.add(s);
                        }
                    ArrayList<String[]> xcopy = new ArrayList<String[]>(x);
                    hm.put(z, xcopy);
                    x.clear();
                }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return hm;
       }
    
    public static ArrayList<String[]> getEDIFFDataRules(String id) {
        
        ArrayList<String[]> x = new ArrayList<String[]>();
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                    res = st.executeQuery("select * from edi_docdet " +
                            " inner join edi_doc on edd_id = edid_id " +
                            " where edid_enabled = '1' " +
                            " and edid_role = 'data' " +
                            " and edid_id = " + "'" + id + "'"); 
                    while (res.next()) {
                           String[] s = new String[]{
                               res.getString("edid_tag"),
                               res.getString("edid_rectype"),
                               res.getString("edid_valuetype"),
                               res.getString("edid_row"),
                               res.getString("edid_col"),
                               res.getString("edid_length"),
                               res.getString("edid_regex"),
                               res.getString("edid_value"),
                               res.getString("edid_xpath"),
                               res.getString("edd_segdelim"),
                               res.getString("edd_eledelim")
                           };
                           x.add(s);
                        }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return x;
       }
    
    
    public static String getEDIFFLandmark(String id) {
             String x = "";
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select edd_landmark from edi_doc  " +
                              " where edd_id = " + "'" + id + "'" + 
                              ";");
                    while (res.next()) {
                       x = res.getString("edd_landmark");
                    }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return x;
       }
    
    public static String getEDIFFDocType(String id) {
             String x = "";
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select edd_type from edi_doc  " +
                              " where edd_id = " + "'" + id + "'" + 
                              ";");
                    while (res.next()) {
                       x = res.getString("edd_type");
                    }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return x;
       }
    
    public static String getEDIFFSubType(String id) {
             String x = "";
            try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select edd_subtype from edi_doc  " +
                              " where edd_id = " + "'" + id + "'" + 
                              ";");
                    while (res.next()) {
                       x = res.getString("edd_subtype");
                    }
           } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
          return x;
       }
    
    
        /** Returns 21 element Array with Customer (billto) specific EDI setup information
         * 
         * @param billto
         * @param doctype
         * @param dir
         * @return Array with 0=ISA, 1=ISAQUAL, 2=GS, 3=BS_ISA, 4=BS_ISA_QUAL, 5=BS_GS, 6=ELEMDELIM, 7=SEGDELIM, 8=SUBDELIM, 9=FILEPATH, 10=FILEPREFIX, 11=FILESUFFIX,
         * @return 12=X12VERSION, 13=SUPPCODE, 14=edi_doctypeout, 15=edi_filetypeout, 16=edi_ifs, 17=edi_ofs, 
         * @return 18=ediid, 19=edidoc, 20=envelopeall
         */
    public static String[] getEDITPDefaults(String doctype, String gssndid, String gsrcvid) {
           
                    
             String[] mystring = new String[]{"","","","","","","0","0","0","","","","","","","","","","","","","","","",""};
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select * from edi_mstr where " + 
                        " edi_doc = " + "'" + doctype + "'" 
                      +  " AND edi_sndgs = " + "'" + gssndid + "'"  
                      +  " AND edi_rcvgs = " + "'" + gsrcvid + "'"          
                        + ";");
                    while (res.next()) {
                       mystring[0] = res.getString("edi_sndisa");
                        mystring[1] = res.getString("edi_sndq");
                        mystring[2] = res.getString("edi_sndgs");
                        mystring[3] = res.getString("edi_rcvisa") ;
                        mystring[4] = res.getString("edi_rcvq") ;
                        mystring[5] = res.getString("edi_rcvgs");
                        mystring[6] = res.getString("edi_eledelim");
                        mystring[7] = res.getString("edi_segdelim") ;
                        mystring[8] = res.getString("edi_subdelim") ;
                        mystring[9] = res.getString("edi_filepath");
                        mystring[10] = res.getString("edi_fileprefix");
                        mystring[11] = res.getString("edi_filesuffix");
                        mystring[12] = res.getString("edi_version");
                        mystring[13] = res.getString("edi_supcode");
                        mystring[14] = res.getString("edi_doctypeout");
                        mystring[15] = res.getString("edi_filetypeout");
                        mystring[16] = res.getString("edi_ifs");
                        mystring[17] = res.getString("edi_ofs");
                        mystring[18] = res.getString("edi_id");
                        mystring[19] = res.getString("edi_doc");
                        mystring[20] = res.getString("edi_envelopeall");
                        mystring[21] = res.getString("edi_filetype");
                        mystring[22] = res.getString("edi_una");
                        mystring[23] = res.getString("edi_ung");
                        mystring[24] = res.getString("edi_map");
                    }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mystring;
        
         }
    
    public static ArrayList<String> getEDIAttributesList(String doctype, String sndid, String rcvid) {
           
             ArrayList<String> x = new ArrayList<String>();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select * from edi_attr where exa_sndid = " + "'" + sndid + "'" + 
                        " AND exa_rcvid = " + "'" + rcvid + "'" + 
                        " AND exa_doc = " + "'" + doctype + "'" +
                              ";");
                    while (res.next()) {
                       x.add(res.getString("exa_key") + ":" + res.getString("exa_value"));
                    }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return x;
        
         }
    
    public static int getEDIControlNbr(String doctype, String sndid, String rcvid, String key) {
       int nbr = 0;
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
                
              //  if (! dbtype.equals("sqlite")) {
                con.setAutoCommit(false);
               // }
                
                if (dbtype.equals("sqlite")) {
               res = st.executeQuery("select exa_value as 'num' from edi_attr " +
                        " where exa_sndid = " + "'" + sndid + "'" + 
                        " AND exa_rcvid = " + "'" + rcvid + "'" + 
                        " AND exa_doc = " + "'" + doctype + "'" +
                        " AND exa_key = " + "'" + key + "'" +        
                              ";");
                } else {
                res = st.executeQuery("select exa_value as 'num' from edi_attr " +
                        " where exa_sndid = " + "'" + sndid + "'" + 
                        " AND exa_rcvid = " + "'" + rcvid + "'" + 
                        " AND exa_doc = " + "'" + doctype + "'" +
                        " AND exa_key = " + "'" + key + "'" +     
                              " for update;");
                }
                while (res.next()) {
                   nbr = res.getInt("num") + 1;
                }
                st.executeUpdate(
                       " update edi_attr set exa_value = " + "'" + nbr + "'" +
                       " where exa_sndid = " + "'" + sndid + "'" + 
                        " AND exa_rcvid = " + "'" + rcvid + "'" + 
                        " AND exa_doc = " + "'" + doctype + "'" +
                        " AND exa_key = " + "'" + key + "'" +         
                              ";");
                
              //  if (! dbtype.equals("sqlite")) {
                con.setAutoCommit(true);
              //  }
               
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
            }
            finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return nbr;
        
    }
    
    
    public static String[] getEDI997SystemDefaults() {
           
                    
             String[] mystring = new String[15];
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                   
                      res = st.executeQuery("select * from edi_mstr where edi_id = " + "'" + OVData.getDefaultSite() + "'" + 
                        " AND edi_doc = " + "'" + "997" + "'" + ";");
                    while (res.next()) {
                       mystring[0] = res.getString("edi_sndisa");
                        mystring[1] = res.getString("edi_sndq");
                        mystring[2] = res.getString("edi_sndgs");
                        mystring[3] = res.getString("edi_rcvisa") ;
                        mystring[4] = res.getString("edi_rcvq") ;
                        mystring[5] = res.getString("edi_rcvgs");
                        mystring[6] = res.getString("edi_eledelim");
                        mystring[7] = res.getString("edi_segdelim") ;
                        mystring[8] = res.getString("edi_subdelim") ;
                        mystring[9] = res.getString("edi_filepath");
                        mystring[10] = res.getString("edi_fileprefix");
                        mystring[11] = res.getString("edi_filesuffix");
                        mystring[12] = res.getString("edi_version");
                        mystring[13] = res.getString("edi_supcode");
                        mystring[14] = res.getString("edi_dir");
                    }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        return mystring;
        
         } 
        
    public static String getEDIMap(String doctype, String sndid, String rcvid) {
      
           String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_map from edi_mstr where  " + 
                        " edi_doc = " + "'" + doctype + "'" + 
                        " AND edi_sndgs = " + "'" + sndid + "'" + 
                        " AND edi_rcvgs = " + "'" + rcvid + "'" +         
                                ";");
               while (res.next()) {
                   mystring = res.getString("edi_map");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
     
    public static String getEDIMap(String doctype, String structure, String sndid, String rcvid) {
      
           String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_map from edi_mstr where  " + 
                        " edi_doc = " + "'" + doctype + "'" + 
                        " AND edi_ifs = " + "'" + structure + "'" +        
                        " AND edi_sndgs = " + "'" + sndid + "'" + 
                        " AND edi_rcvgs = " + "'" + rcvid + "'" +         
                                ";");
               while (res.next()) {
                   mystring = res.getString("edi_map");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    
    public static String getEDICustDir(String doctype, String sndid, String rcvid) {
      
           String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_filepath from edi_mstr where " + 
                        " edi_sndgs = " + "'" + sndid + "'" +
                        " AND edi_rcvgs = " + "'" + rcvid + "'" +            
                        " AND edi_doc = " + "'" + doctype + "'" + 
                                ";");
               while (res.next()) {
                   mystring = res.getString("edi_filepath");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
        
    public static String getEDIFuncAck(String doctype, String sndid, String rcvid) {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_fa_required from edi_mstr where edi_sndgs = " + "'" + sndid + "'" + 
                        " AND edi_rcvgs = " + "'" + rcvid + "'" + 
                        " AND edi_doc = " + "'" + doctype + "'" +        
                                ";");
               while (res.next()) {
                   mystring = res.getString("edi_fa_required");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
              
    public static ArrayList getEDIUniqueTPID() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_sndisa from edi_mstr group by edi_sndisa order by edi_sndisa; ");
               while (res.next()) {
                   mylist.add(res.getString("edi_sndisa"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    public static ArrayList getEDIPartnerDocIDs() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edi_id from edi_mstr group by edi_id order by edi_id; ");
               while (res.next()) {
                   mylist.add(res.getString("edi_id"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    public static ArrayList<String[]> getEDIPartnerDocSet(String id) {
       ArrayList<String[]> mylist = new ArrayList<String[]>();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select * from edi_mstr " +
                        " where edi_id = " + "'" + id + "'" + " order by edi_id; ");
               while (res.next()) {
                   mylist.add(new String[]{res.getString("edi_id"), 
                       res.getString("edi_doc"), 
                       res.getString("edi_sndgs"), 
                       res.getString("edi_rcvgs")
                   });
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    public static ArrayList<String> getEDIInvoices(String fromnbr, String tonbr, String fromdate, String todate, boolean override) {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                if (override) {
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " +  
                        " where sh_id >= " + "'" + fromnbr + "'" + 
                        " and sh_id <= " + "'" + tonbr + "'" +
                        " and sh_confdate >= " + "'" + fromdate + "'" +
                        " and sh_confdate <= " + "'" + todate + "'" +
                        " and sh_status = '1' and cm_is810export = '1'  ;");   
                } else {
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " + 
                        " where sh_id >= " + "'" + fromnbr + "'" + 
                        " and sh_id <= " + "'" + tonbr + "'" +
                        " and sh_confdate >= " + "'" + fromdate + "'" +
                        " and sh_confdate <= " + "'" + todate + "'" +
                        " and sh_status = '1' and cm_is810export = '1' " +
                        " and sh_export_810 = '0' ;");   
                }
                
               while (res.next()) {
                   mylist.add(res.getString("sh_id"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    public static ArrayList<String> getEDIInvoicesAutoExport() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " + 
                        " where sh_export_810 = '0' and " + 
                        " sh_status = '1' and " +
                        " cm_is810export = '1' ;");   
                
                
               while (res.next()) {
                   mylist.add(res.getString("sh_id"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    
    public static ArrayList<String> getEDIASNs(String fromnbr, String tonbr, String fromdate, String todate, boolean override) {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                if (override) {
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " +  
                        " where sh_id >= " + "'" + fromnbr + "'" + 
                        " and sh_id <= " + "'" + tonbr + "'" +
                        " and sh_confdate >= " + "'" + fromdate + "'" +
                        " and sh_confdate <= " + "'" + todate + "'" +
                        " and sh_status = '1' and cm_is856export = '1' ;");   
                } else {
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " +  
                        " where sh_id >= " + "'" + fromnbr + "'" + 
                        " and sh_id <= " + "'" + tonbr + "'" +
                        " and sh_confdate >= " + "'" + fromdate + "'" +
                        " and sh_confdate <= " + "'" + todate + "'" +
                        " and sh_status = '1' and cm_is856export = '1'  " +
                        " and sh_export_856 = '0' ;");   
                }
                
               while (res.next()) {
                   mylist.add(res.getString("sh_id"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
        
    public static ArrayList<String> getEDIASNsAutoExport() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
               
                 res = st.executeQuery("select sh_id from ship_mstr  " +
                        " inner join cm_mstr on cm_code = sh_cust " +  
                        " where sh_export_856 = '0'  "  + 
                        " and sh_status = '1' and cm_is856export = '1'  " +
                        ";");   
                
                
               while (res.next()) {
                   mylist.add(res.getString("sh_id"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    
    public static ArrayList<String> getEDIPOs(String fromnbr, String tonbr, String fromdate, String todate, boolean override) {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                if (override) {
                 res = st.executeQuery("select po_nbr from po_mstr  " +
                        " inner join vd_mstr on vd_addr = po_vend " + 
                        " where po_nbr >= " + "'" + fromnbr + "'" + 
                        " and po_nbr <= " + "'" + tonbr + "'" +
                        " and po_ord_date >= " + "'" + fromdate + "'" +
                        " and po_ord_date <= " + "'" + todate + "'" +
                        " and po_confirm = '1' and vd_is850export = '1' " +        
                        " ;");   
                } else {
                 res = st.executeQuery("select po_nbr from po_mstr  " +
                        " inner join vd_mstr on vd_addr = po_vend " + 
                        " where po_nbr >= " + "'" + fromnbr + "'" + 
                        " and po_nbr <= " + "'" + tonbr + "'" +
                        " and po_ord_date >= " + "'" + fromdate + "'" +
                        " and po_ord_date <= " + "'" + todate + "'" +
                        " and po_confirm = '1' and vd_is850export = '1' " +        
                        " and po_export_850 = '0' ;");   
                }
                
               while (res.next()) {
                   mylist.add(res.getString("po_nbr"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }

    public static ArrayList<String> getEDIACKs(String fromnbr, String tonbr, String fromdate, String todate, boolean override) {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                
                if (override) {
                 res = st.executeQuery("select so_nbr from so_mstr  " +
                        " inner join cm_mstr on cm_code = so_cust " + 
                        " where so_nbr >= " + "'" + fromnbr + "'" + 
                        " and so_nbr <= " + "'" + tonbr + "'" +
                        " and so_ord_date >= " + "'" + fromdate + "'" +
                        " and so_ord_date <= " + "'" + todate + "'" +
                        " and so_confirm = '1' and cm_is855export = '1' " +        
                        " ;");   
                } else {
                 res = st.executeQuery("select so_nbr from so_mstr  " +
                        " inner join cm_mstr on cm_code = so_cust " +  
                        " where so_nbr >= " + "'" + fromnbr + "'" + 
                        " and so_nbr <= " + "'" + tonbr + "'" +
                        " and so_ord_date >= " + "'" + fromdate + "'" +
                        " and so_ord_date <= " + "'" + todate + "'" +
                        " and so_confirm = '1' and cm_is855export = '1' " +          
                        " and so_export_855 = '0' ;");   
                }
                
               while (res.next()) {
                   mylist.add(res.getString("so_nbr"));
                }
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
        
    
    public static void updateEDIPOStatus(String po) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update po_mstr set po_export_850 = '1' " +
                 " where po_nbr = " + "'" + po + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    public static void updateEDIPOStatus(ArrayList<String> orders) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
            for (String x : orders) {
            st.executeUpdate(
                 " update po_mstr set po_export_850 = '1' " +
                 " where po_nbr = " + "'" + x + "'" + ";" );
            }
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    
    public static void updateEDIASNStatus(String shipper) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update ship_mstr set sh_export_856 = '1' " +
                 " where sh_id = " + "'" + shipper + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    public static void updateEDIASNStatus(ArrayList<String> shippers) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
            for (String s : shippers) {
             st.executeUpdate(
                 " update ship_mstr set sh_export_856 = '1' " +
                 " where sh_id = " + "'" + s + "'" + ";" );
             }
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    
    public static void updateEDIOrderStatus(String order) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update so_mstr set so_export_855 = '1' " +
                 " where so_nbr = " + "'" + order + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    public static void updateEDIOrderStatus(ArrayList<String> orders) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
            for (String x : orders) { 
            st.executeUpdate(
                 " update so_mstr set so_export_855 = '1' " +
                 " where so_nbr = " + "'" + x + "'" + ";" );
            }
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    
    public static void updateEDIInvoiceStatus(String shipper) {
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           st.executeUpdate(
                 " update ship_mstr set sh_export_810 = '1' " +
                 " where sh_id = " + "'" + shipper + "'" + ";" );
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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
    
    public static void updateEDIInvoiceStatus(ArrayList<String> shippers) {
       try{
        Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
        Statement st = con.createStatement();
        try{
           for (String s : shippers) { 
               st.executeUpdate(
                     " update ship_mstr set sh_export_810 = '1' " +
                     " where sh_id = " + "'" + s + "'" + ";" );
           }
        }
        catch (SQLException s){
             MainFrame.bslog(s);
        } finally {
            
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

    
    public static ArrayList getEDIPartners() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edp_id from edp_partner order by edp_id; ");
               while (res.next()) {
                   mylist.add(res.getString("edp_id"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
       
    public static ArrayList getEDITradeIDs() {
       ArrayList mylist = new ArrayList();
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edpd_alias from edpd_partner order by edpd_alias; ");
               while (res.next()) {
                   mylist.add(res.getString("edpd_alias"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mylist;
        
    }
    
    
    public static String getEDIPartnerFromAlias(String alias) {
       String x = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edp_id from edp_partner " +
                        " inner join edpd_partner on edpd_parent = edp_id " +
                        " where edpd_alias = " + "'" + alias + "'" + 
                        " order by edp_id; ");
               while (res.next()) {
                   x = res.getString("edp_id");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
             
    
    public static String getEDIXrefIn(String isaid, String gsid, String editype, String addrcode) {
             String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from edi_xref where " +
                              " exr_tpid = " + "'" + isaid + "'" + 
                              " AND exr_gsid = " + "'" + gsid + "'" +
                              " AND exr_tpaddr = " + "'" + addrcode + "'" + 
                              " AND exr_type = " + "'" + editype + "'" +        
                                ";");
                    while (res.next()) {
                       mystring = res.getString("exr_ovaddr");
                    }
           }
            catch (SQLException s) {
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
        return mystring;
        
    }
    
    public static String[] getEDIXrefOut(String bsaddr, String editype) {
             String mystring[] = new String[]{"","","","",""};
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                      res = st.executeQuery("select * from edi_xref where " +
                              " exr_ovaddr = " + "'" + bsaddr + "'" + 
                              " AND exr_type = " + "'" + editype + "'" +        
                                ";");
                    while (res.next()) {
                       mystring[0] = res.getString("exr_tpid");
                       mystring[1] = res.getString("exr_gsid");
                       mystring[2] = res.getString("exr_tpaddr");
                       mystring[3] = res.getString("exr_ovaddr");
                       mystring[4] = res.getString("exr_type");
                    }
           }
            catch (SQLException s) {
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
        return mystring;
        
    }
    
    public static String getBSDocTypeFromStds(String invalue) {
       String x = "??";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select eds_bsdoc from edi_stds " +
                        " where eds_doc = " + "'" + invalue + "'" + 
                        " order by eds_bsdoc; ");
               while (res.next()) {
                   x = res.getString("eds_bsdoc");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
        
    public static String getEDIDocTypeFromStds(String gs) {
       String x = "??";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select eds_doc from edi_stds " +
                        " where eds_gs = " + "'" + gs + "'" + 
                        " order by eds_gs; ");
               while (res.next()) {
                   x = res.getString("eds_doc");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }
    
    public static String getEDIGSTypeFromStds(String doc) {
       String x = "??";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select eds_gs from edi_stds " +
                        " where eds_doc = " + "'" + doc + "'" + 
                        " order by eds_doc; ");
               while (res.next()) {
                   x = res.getString("eds_gs");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }

    public static String getEDIGSTypeFromBSDoc(String doc) {
       String x = "??";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select eds_gs from edi_stds " +
                        " where eds_bsdoc = " + "'" + doc + "'" + 
                        " order by eds_bsdoc; ");
               while (res.next()) {
                   x = res.getString("eds_gs");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }

    public static String getEDIDocTypeFromBSDoc(String doc) {
       String x = "??";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select eds_doc from edi_stds " +
                        " where eds_bsdoc = " + "'" + doc + "'" + 
                        " order by eds_bsdoc; ");
               while (res.next()) {
                   x = res.getString("eds_doc");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return x;
        
    }

    
    public static char[] readEDIRawFileIntoCbuf(Path filepath) {
        char[] cbuf = null;
        try {
            File file = filepath.toFile();
               if (! file.exists()) {
                 return null;
               }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
            cbuf = new char[(int) file.length()];
            reader.read(cbuf,0,cbuf.length); 
            reader.close();
        } catch (IOException ex) {
            bslog(ex);
        }
       return cbuf;
    }
    
    
    public static List<String> readEDIRawFileIntoArrayList(Path filepath)  {
       ArrayList<String> segments = new ArrayList<String>();
       try {
           if (OVData.getSystemFileServerType().toString().equals("S")) {  // if Samba type
               NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
               SmbFile smbfile;

                   smbfile = new SmbFile(filepath.toString(), auth);

                   if (! smbfile.exists()) {
                     return segments;
                   }
               BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
               char[] cbuf = new char[(int) smbfile.length()];
               reader.read(cbuf,0,cbuf.length); 
               reader.close();
               segments = EDData.parseFile(cbuf, smbfile.getName());
           } else {

               File file = filepath.toFile();
                   if (! file.exists()) {
                     return segments;
                   }

               BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
               char[] cbuf = new char[(int) file.length()];
               reader.read(cbuf,0,cbuf.length); 
               reader.close();
               segments = EDData.parseFile(cbuf, file.getName());
           }
       } catch (MalformedURLException ex) {
            bslog(ex);
           } catch (SmbException ex) {
            bslog(ex);
        } catch (UnknownHostException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        }
       return segments;
    }
        
    public static ArrayList readEDIRawFileIntoArrayList(String filename, String dir) throws MalformedURLException, SmbException, UnknownHostException, IOException {
       ArrayList<String> segments = new ArrayList<String>();
       String path = "";
       
        
         path =  cleanDirString(EDData.getEDIBatchDir()) + filename; 
      
       
       if (OVData.getSystemFileServerType().toString().equals("S")) {  // if Samba type
           NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
           SmbFile smbfile = new SmbFile(path, auth);
               if (! smbfile.exists()) {
                 bsmf.MainFrame.show("File is unavailable (samba)");
                 return segments;
               }
           BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
           char[] cbuf = new char[(int) smbfile.length()];
           reader.read(cbuf,0,cbuf.length); 
           reader.close();
           segments = EDData.parseFile(cbuf, smbfile.getName());
       } else {
           
           File file = new File(path);
               if (! file.exists()) {
                 bsmf.MainFrame.show("File is unavailable");
                 return segments;
               }
               
           BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
           char[] cbuf = new char[(int) file.length()];
           reader.read(cbuf,0,cbuf.length); 
           reader.close();
           segments = EDData.parseFile(cbuf, file.getName());
       }
       return segments;
    }
       
    public static ArrayList readEDIRawFileByDoc(String filename, String dir, boolean wholefile, String beg, String end, String seg) throws MalformedURLException, SmbException, UnknownHostException, IOException {
       ArrayList<String> segments = new ArrayList<String>();
       String path = "";
        
        
         path =  dir + "/" + filename; 
      
       
       if (OVData.getSystemFileServerType().toString().equals("S")) {  // if Samba type
           NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
           SmbFile smbfile = new SmbFile(path, auth);
               if (! smbfile.exists()) {
                 bsmf.MainFrame.show("File is unavailable (samba)");
                 return segments;
               }
           BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
           char[] cbuf = new char[(int) smbfile.length()];
           reader.read(cbuf,0,cbuf.length); 
           reader.close();
           segments = EDData.parseFile(cbuf, smbfile.getName());
       } else {
           
          
           
           File file = new File(path);
           long max = file.length();
               if (! file.exists()) {
                 bsmf.MainFrame.show("File is unavailable: " + path);
                 return segments;
               }
           
           int diff = (Integer.valueOf(end) - Integer.valueOf(beg));
           if (wholefile || end.equals("0")) {
               beg = "0";
               diff = (int) max;
           }
           byte[] bytesRead = new byte[diff]; 
           RandomAccessFile rf = new RandomAccessFile(path, "r");
           rf.seek(Integer.valueOf(beg));
           rf.read(bytesRead);
	   String DOC = new String(bytesRead); 
           rf.close();
           String delim = "";
           int x = Integer.valueOf(seg);
           delim = String.valueOf((char) x);
           delim = EDI.escapeDelimiter(delim);
          
	   
           String[] sarr = DOC.split(delim, -1);
           
           for (int i = 0; i < sarr.length; i++) {
           segments.add(sarr[i]);
           }
      
               
               
        //   BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
      //     char[] cbuf = new char[(int) file.length()];
       //    reader.read(cbuf,0,cbuf.length); 
       //    reader.close();
       //    segments = OVData.parseFile(cbuf, file.getName());
       }
       return segments;
    }
             
    public static ArrayList readEDIRawFileLiveDirIntoArrayList(String filename, String dir) throws MalformedURLException, SmbException, UnknownHostException, IOException {
       ArrayList<String> segments = new ArrayList<String>();
       String path = "";
       if (dir.equals("In")) {
         path =  cleanDirString(EDData.getEDIInDir()) + filename; 
       } else {
         path =  cleanDirString(EDData.getEDIOutDir()) + filename;   
       }
       
       if (OVData.getSystemFileServerType().toString().equals("S")) {  // if Samba type
           NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
           SmbFile smbfile = new SmbFile(path, auth);
               if (! smbfile.exists()) {
                 bsmf.MainFrame.show("File is unavailable");
                 return segments;
               }
           BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new SmbFileInputStream(smbfile))));
           char[] cbuf = new char[(int) smbfile.length()];
           reader.read(cbuf,0,cbuf.length); 
           reader.close();
           segments = EDData.parseFile(cbuf, smbfile.getName());
       } else {
           File file = new File(path);
               if (! file.exists()) {
                 bsmf.MainFrame.show("File is unavailable");
                 return segments;
               }
           BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
           char[] cbuf = new char[(int) file.length()];
           reader.read(cbuf,0,cbuf.length); 
           reader.close();
           segments = EDData.parseFile(cbuf, file.getName());
       }
       return segments;
    }
       
    public static String getEDIgsid() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_gsid from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_gsid");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getAS2id() {
       String mystring = "";
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
                res = st.executeQuery("select edic_as2id from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_as2id");
                }
           }
            catch (SQLException s) {
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
        return mystring;
        
    }
    
    public static String getAS2url() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_as2url from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_as2url");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getSystemEncKey() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_enckey from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_enckey");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getSystemSignKey() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_signkey from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_signkey");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    
    public static String getEDIOutDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_outdir from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_outdir");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
     
    public static String getEDIBatchDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_batch from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_batch");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
     
    public static String getEDIStructureDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_structure from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_structure");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
    public static String getEDIMapDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_mapdir from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_mapdir");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
    
     
    public static String getEDIInDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_indir from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_indir");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
      
    public static String getEDIInArch() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_inarch from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_inarch");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
      
    public static String getEDIErrorDir() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_errordir from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_errordir");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
      
      
    public static String getEDIOutArch() {
       String mystring = "";
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_outarch from edi_ctrl ;");
               while (res.next()) {
                   mystring = res.getString("edic_outarch");
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return mystring;
        
    }
      
    public static boolean isEDIArchFlag() {
       boolean isArchive = true;
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_archyesno from edi_ctrl ;");
               while (res.next()) {
                   isArchive = BlueSeerUtils.ConvertStringToBool(res.getString("edic_archyesno"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isArchive; 
        
    }
      
    public static boolean isEDIDeleteFlag() {
       boolean isDelete = true;
        try{
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                

                res = st.executeQuery("select edic_delete from edi_ctrl ;");
               while (res.next()) {
                   isDelete = BlueSeerUtils.ConvertStringToBool(res.getString("edic_delete"));
                }
               
           }
            catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
        }
        return isDelete; 
        
    }
      
      
    public static void updateEDILogWith997(ArrayList<String> docs, String ackdoctype, String ackgsctrlnum, String[] control) {
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                // control = senderid + "," + doctype + "," + controlnum + "," + stctrlnum + "," + filename + "," + gsctrlnum; 
                String[] c = control;
                for (String doc : docs) {
                        st.executeUpdate("update edi_log set " 
                            + " elg_ack = '1', " 
                            + " elg_ackfile = " + "'" + c[3] + "'" 
                            + " where elg_gsctrlnum = " + "'" + ackgsctrlnum + "'" 
                            + " and elg_stctrlnum = " + "'" + doc + "'"
                            + " and elg_doc = " + "'" + ackdoctype + "'"   
                            + " and elg_dir = '1' "             
                            + ";");
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
      
      
      
    public static void writeEDILog(String[] control, String severity, String message) {
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                String[] c = control;
              
                 // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                
                        st.executeUpdate("insert into edi_log ( elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_isa, elg_doc, elg_map, elg_file, elg_batch, elg_ctrlnum, elg_gsctrlnum, elg_stctrlnum, elg_ref ) "
                            + " values ( " 
                            + "'" + c[22] + "'" + ","    
                            + "'" + c[16] + "'" + ","
                            + "'" + severity + "'" + ","
                            + "'" + message + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[2] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + c[24] + "'" + ","        
                            + "'" + c[4] + "'" + ","
                            + "'" + c[5] + "'" + ","
                            + "'" + c[6] + "'" + ","
                            + "'" + c[7] + "'"
                            + ")"
                            + ";");
            } catch (SQLException s) {
                 MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
    
    public static void writeEDILogMulti(String[] control, ArrayList<String[]> messages) {
          try {
            Class.forName(driver);
            Connection con = null;
            PreparedStatement ps = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            try {
                String[] c = control;
                String sqlInsert = "insert into edi_log ( elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_isa, elg_doc, elg_map, elg_file, elg_batch, elg_ctrlnum, elg_gsctrlnum, elg_stctrlnum, elg_ref ) "
                            + " values ( ?,?,?,?,?,?,?,?,?,?,?,?,?); " ; 
                ps = con.prepareStatement(sqlInsert);
                // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                
                for (String[] s : messages) {
                            ps.setString(1, c[22]);
                            ps.setString(2, c[16]);
                            ps.setString(3, s[0]);
                            ps.setString(4, s[1]);
                            ps.setString(5, c[0]);
                            ps.setString(6, c[1]);
                            ps.setString(7, c[2]);
                            ps.setString(8, c[3]);
                            ps.setString(9, c[24]);      
                            ps.setString(10, c[4]);
                            ps.setString(11, c[5]);
                            ps.setString(12, c[6]);
                            ps.setString(13, c[7]);
                            ps.executeUpdate();
                }  
            } catch (SQLException s) {
                 MainFrame.bslog(s);
            } finally {
               if (ps != null) ps.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
    
    
    public static void writeEDIFileLog(String[] control) {
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                String[] c = control;
              
                        st.executeUpdate("insert into edi_file ( edf_comkey, edf_file, edf_batch, edf_dir, edf_status ) "
                            + " values ( " 
                            + "'" + c[22] + "'" + ","    
                            + "'" + c[3] + "'" + ","
                            + "'" + c[24] + "'" + ","        
                            + "'" + c[26] + "'" + ","
                            + "'" + "info" + "'"  
                            + ")"
                            + ";");
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
   
    public static void updateEDIFileLogStatus(String comkey, String sender, String filetype, String doctype) {
        String x = "success";  
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                
                res = st.executeQuery("select elg_severity from edi_log " +
                        " where elg_comkey = " + "'" + comkey + "'" + " order by elg_id;");
                while (res.next()) {
                    if (res.getString("elg_severity").toLowerCase().equals("error")) {
                        x = "error";
                    }
                    if (res.getString("elg_severity").toLowerCase().equals("success")) {
                       x = "success";; 
                    }
                    // last one wins...order by is important!
                }
                        st.executeUpdate("update edi_file set " 
                            + " edf_status = " + "'" + x + "'" + "," 
                            + " edf_partner = " + "'" + sender + "'" + "," 
                            + " edf_filetype = " + "'" + filetype + "'" + "," 
                            + " edf_doctype = " + "'" + doctype + "'"         
                            + " where edf_comkey = " + "'" + comkey + "'"  
                            + ";");
            } catch (SQLException s) {
                 MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
   
    public static void updateEDIFileLogStatusManual(String comkey) {
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                st.executeUpdate("update edi_file set " 
                    + " edf_status = 'success' "       
                    + " where edf_comkey = " + "'" + comkey + "'"  
                    + ";");
            } catch (SQLException s) {
                 MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
   
    
    public static String getEDIBatchFromedi_file(String comkey) {
        String x = "";
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
              
                 // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                res = st.executeQuery("select edf_batch from edi_file where " +
                        " edf_comkey = " + "'" + comkey + "'" + 
                        ";" );
               while (res.next()) {
                   x = res.getString("edf_batch");
                }    
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static String getEDIAckFileFromEDIIDX(String key) {
        String x = "";
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
              
                 // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                res = st.executeQuery("select edx_ackfile from edi_idx where " +
                        " edx_id = " + "'" + key + "'" + 
                        ";" );
               while (res.next()) {
                   x = res.getString("edx_ackfile");
                }    
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static String[] getEDIDocPositionEDIIDX(String key) {
        String[] x = new String[]{"","","","",""};
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
              
                 // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                 res = st.executeQuery("select edx_isastart, edx_isaend, edx_docstart, edx_docend, edx_segdelim from edi_idx where " +
                        " edx_id = " + "'" + key + "'" + 
                        ";" );
               while (res.next()) {
                   x[0] = res.getString("edx_isastart");
                   x[1] = res.getString("edx_isaend");
                   x[2] = res.getString("edx_docstart");
                   x[3] = res.getString("edx_docend");
                   x[4] = res.getString("edx_segdelim");
                }    
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static String[] getEDIDocPositionEDIIDXcomkey(String comkey) {
        String[] x = new String[]{"","","","",""};
        try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
              
                 // controlarray in this order : senderid, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                 res = st.executeQuery("select edx_isastart, edx_isaend, edx_docstart, edx_docend, edx_segdelim from edi_idx where " +
                        " edx_comkey = " + "'" + comkey + "'" + 
                        ";" );
               while (res.next()) {
                   x[0] = res.getString("edx_isastart");
                   x[1] = res.getString("edx_isaend");
                   x[2] = res.getString("edx_docstart");
                   x[3] = res.getString("edx_docend");
                   x[4] = res.getString("edx_segdelim");
                }    
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return x;
    }
    
    public static int writeAS2Log(String[] c) {
            int returnkey = 0;
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                      if (dbtype.equals("sqlite")) {
                        st.executeUpdate("insert into as2_log ( as2l_parent, as2l_id, as2l_dir, " +
                                " as2l_status, as2l_messg, as2l_datetime, as2l_mdn ) " 
                            + " values ( " 
                            + "'" + c[0] + "'" + ","  // parent master...master record has '0'
                            + "'" + c[1] + "'" + ","  // partner id
                            + "'" + c[2] + "'" + ","        // dir
                            + "'" + c[3] + "'" + "," // status
                            + "'" + c[4] + "'" + ","  // messg
                            + "'" + c[5] + "'" + ","  // ts
                            + "'" + c[6] + "'" // mdn
                            + ")"
                            + ";");
                      } else {
                          st.executeUpdate("insert into as2_log ( as2l_parent, as2l_id, as2l_dir, " +
                                " as2l_status, as2l_messg, as2l_datetime, as2l_mdn ) " 
                            + " values ( " 
                            + "'" + c[0] + "'" + ","  // parent master...master record has '0'...else has master record as2l_logid
                            + "'" + c[1] + "'" + ","  // partner id
                            + "'" + c[2] + "'" + ","        // dir
                            + "'" + c[3] + "'" + "," // status
                            + "'" + c[4] + "'" + ","  // messg
                            + "'" + c[5] + "'" + ","  // ts
                            + "'" + c[6] + "'" // mdn
                            + ")"
                            + ";", Statement.RETURN_GENERATED_KEYS);
                      }
                        ResultSet rs = st.getGeneratedKeys();
                        while (rs.next()) {
                         returnkey = rs.getInt(1);
                        }
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return returnkey;
      }
    
    public static int writeAS2LogStop(String[] c) {
          // this method writes both the parent and detail records within as2_log
          // for scenarios where the AS2 listening server has insufficient info and bails
          // Detail is an unfortuanate reproduction of Parent
          int returnkey = 0;
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                      if (dbtype.equals("sqlite")) {
                        st.executeUpdate("insert into as2_log ( as2l_parent, as2l_id, as2l_dir, " +
                                " as2l_status, as2l_messg, as2l_datetime, as2l_mdn ) " 
                            + " values ( " 
                            + "'" + c[0] + "'" + ","  // parent master...master record has '0'
                            + "'" + c[1] + "'" + ","  // partner id
                            + "'" + c[2] + "'" + ","        // dir
                            + "'" + c[3] + "'" + "," // status
                            + "'" + c[4] + "'" + ","  // messg
                            + "'" + c[5] + "'" + ","  // ts
                            + "'" + c[6] + "'" // mdn
                            + ")"
                            + ";");
                      } else {
                          st.executeUpdate("insert into as2_log ( as2l_parent, as2l_id, as2l_dir, " +
                                " as2l_status, as2l_messg, as2l_datetime, as2l_mdn ) " 
                            + " values ( " 
                            + "'" + c[0] + "'" + ","  // parent master...master record has '0'...else has master record as2l_logid
                            + "'" + c[1] + "'" + ","  // partner id
                            + "'" + c[2] + "'" + ","        // dir
                            + "'" + c[3] + "'" + "," // status
                            + "'" + c[4] + "'" + ","  // messg
                            + "'" + c[5] + "'" + ","  // ts
                            + "'" + c[6] + "'" // mdn
                            + ")"
                            + ";", Statement.RETURN_GENERATED_KEYS);
                      }
                    ResultSet rs = st.getGeneratedKeys();
                    while (rs.next()) {
                     returnkey = rs.getInt(1);
                    }
                   
                    // now detail reproduction with as2l_logid of above parent return key
                    // browse report needs this parent/detail structure...sadly
                    st.executeUpdate("insert into as2_log ( as2l_parent, as2l_status, as2l_messg ) " 
                    + " values ( " 
                    + "'" + String.valueOf(returnkey) + "'" + ","  // as2l_logid of parent record
                    + "'" + c[3] + "'" + ","  // status
                    + "'" + c[4] + "'"        // message
                    + ")"
                    + ";");    
                        
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return returnkey;
      }
    
    
    public static int writeAS2LogDetail(ArrayList<String[]> x) {
            int returnkey = 0;
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                String messg;
                for (String[] c : x) {
                    if (c[2].length()>= 200) {
                        messg = c[2].substring(0,200);
                    } else {
                        messg = c[2];
                    }
                st.executeUpdate("insert into as2_log ( as2l_parent, as2l_status, as2l_messg ) " 
                    + " values ( " 
                    + "'" + c[0] + "'" + ","  // as2l_logid of parent record
                    + "'" + c[1] + "'" + ","  // status
                    + "'" + messg + "'"        // message
                    + ")"
                    + ";");
                }   
                
               // now update parent status
               // if last entry = 'error'...then parent status = error...otherwise 'success'
               // if last entry = 'passive'...then set parent to 'passive' mod date: 20220906 TEV
               if (x.get(x.size() - 1)[1].equals("error")) {
               st.executeUpdate("update as2_log set " +
                            " as2l_status = " + "'" + "error" + "'" +
                            " where as2l_logid = " + "'" + x.get(0)[0] + "'" +        
                            ";");
               } else if (x.get(x.size() - 1)[1].equals("passive")) {
                st.executeUpdate("update as2_log set " +
                            " as2l_status = " + "'" + "passive" + "'" +
                            " where as2l_logid = " + "'" + x.get(0)[0] + "'" +        
                            ";");   
               } else {
                st.executeUpdate("update as2_log set " +
                            " as2l_status = " + "'" + "success" + "'" +
                            " where as2l_logid = " + "'" + x.get(0)[0] + "'" +        
                            ";");   
               }
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return returnkey;
      }
    
    
    public static void updateAS2LogStatus(String key, String status) {
            
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                     
                    st.executeUpdate("update as2_log set " +
                            " as2l_status = " + "'" + status + "'" +
                            " where as2l_logid = " + "'" + key + "'" +        
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
         
      }
    
    public static void updateAS2LogMDNFile(String key, String mdn) {
            
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                     
                    st.executeUpdate("update as2_log set " +
                            " as2l_mdn = " + "'" + mdn + "'" +
                            " where as2l_logid = " + "'" + key + "'" +        
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
         
      }
    
    public static int writeEDIIDX(String[] c) {
            int returnkey = 0;
          try {
            Class.forName(driver);
            Connection con = null;
            PreparedStatement ps = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            
            try {
            String sqlInsert = "insert into edi_idx ( edx_comkey, edx_sender, edx_receiver, " +
                                " edx_infiletype, edx_indoctype, edx_inbatch, " +
                                " edx_outfiletype, edx_outdoctype, edx_outbatch, " +
                                " edx_ctrlnum, edx_gsctrlnum, edx_stctrlnum, " +
                                " edx_isastart, edx_isaend, edx_docstart, edx_docend, " +
                                " edx_outisastart, edx_outisaend, edx_outdocstart, edx_outdocend, " + 
                                "  edx_ref, " +
                                " edx_indir, edx_infile, edx_outdir, edx_outfile, " +
                                " edx_ackfile, edx_ack, " +
                                " edx_segdelim, edx_elmdelim, edx_subdelim, " +
                                " edx_outsegdelim, edx_outelmdelim, edx_outsubdelim, " + 
                                " edx_status )" +
                                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ";
                            ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS );    
                            ps.setString(1, c[22]);
                            ps.setString(2, c[0]); // sender
                            ps.setString(3, c[21]);     // receiver
                            ps.setString(4, c[28]); // infiletype
                            ps.setString(5, c[1]);  // indoctype
                            ps.setString(6, c[24]);  // inbatch
                            ps.setString(7, c[29]); // outfiletype
                            ps.setString(8, c[15]);  // outdoctype
                            ps.setString(9, c[25]);  // outbatch
                            ps.setString(10, c[4]);  // isactrlnum
                            ps.setString(11, c[5]); // gsctrlnum
                            ps.setString(12, c[6]);  // st ctrlnum
                            ps.setString(13, c[17]);  // isastart
                            ps.setString(14, c[18]); // isaend
                            ps.setString(15, c[19]);   // docstart
                            ps.setString(16, c[20]);  //docend
                            ps.setString(17, c[31]);  // outisastart
                            ps.setString(18, c[32]);  // outisaend
                            ps.setString(19, c[33]);  // outdocstart
                            ps.setString(20, c[34]);  // outdocend        
                            ps.setString(21, c[7]);   //ref
                            ps.setString(22, c[26]);    // indir 
                            ps.setString(23, c[3]);  // infile
                            ps.setString(24, c[27]);    // outdir
                            ps.setString(25, c[8]);       // outfile
                            ps.setString(26, "");  // ack file   ...need to do
                            ps.setString(27, "0");  // ack yes or no 1 or 0        ....need to do
                            ps.setInt(28, Integer.valueOf(c[9].toString()));
                            ps.setInt(29, Integer.valueOf(c[10].toString()));
                            ps.setInt(30, Integer.valueOf(c[11].toString()));
                            ps.setInt(31, Integer.valueOf(c[35].toString()));
                            ps.setInt(32, Integer.valueOf(c[36].toString()));
                            ps.setInt(33, Integer.valueOf(c[37].toString()));       
                            ps.setString(34, "success");  // status 
                            ps.executeUpdate();
            
                
                        ResultSet rs = ps.getGeneratedKeys();
                        while (rs.next()) {
                         returnkey = rs.getInt(1);
                        }
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (ps != null) ps.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return returnkey;
      }
       
    public static void updateEDIIDX(int key, String[] c) {
            
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                    st.executeUpdate("update edi_idx set " +
                            " edx_sender = " + "'" + c[0] + "'" + "," +
                            " edx_receiver = " + "'" + c[21] + "'" + "," +
                            " edx_infiletype = " + "'" + c[28] + "'" + "," +
                            " edx_indoctype = " + "'" + c[1] + "'" + "," +
                            " edx_inbatch = " + "'" + c[24] + "'" + "," +
                            " edx_outfiletype = " + "'" + c[29] + "'" + "," +
                            " edx_outdoctype = " + "'" + c[15] + "'" + "," +
                            " edx_outbatch = " + "'" + c[25] + "'" + "," +
                            " edx_ctrlnum = " + "'" + c[4] + "'" + "," +
                            " edx_gsctrlnum = " + "'" + c[5] + "'" + "," +
                            " edx_stctrlnum = " + "'" + c[6] + "'" + "," +
                            " edx_isastart = " + "'" + c[17] + "'" + "," +
                            " edx_isaend = " + "'" + c[18] + "'" + "," +
                            " edx_docstart = " + "'" + c[19] + "'" + "," +
                            " edx_docend = " + "'" + c[20] + "'" + "," +
                            " edx_outisastart = " + "'" + c[31] + "'" + "," +
                            " edx_outisaend = " + "'" + c[32] + "'" + "," +
                            " edx_outdocstart = " + "'" + c[33] + "'" + "," +
                            " edx_outdocend = " + "'" + c[34] + "'" + "," +        
                            " edx_ref = " + "'" + c[7] + "'" + "," +
                            " edx_indir = " + "'" + c[26] + "'" + "," +
                            " edx_infile = " + "'" + c[3] + "'" + "," +
                            " edx_outdir = " + "'" + c[27] + "'" + "," +
                            " edx_outfile = " + "'" + c[8] + "'" + "," +
                            " edx_ackfile = " + "'" + "" + "'" + "," +
                            " edx_ack = " + "'" + "0" + "'" + "," +
                            " edx_segdelim = " + "'" + Integer.valueOf(c[9].toString()) + "'" + "," +
                            " edx_elmdelim = " + "'" + Integer.valueOf(c[10].toString()) + "'" + "," +  
                            " edx_subdelim = " + "'" + Integer.valueOf(c[11].toString()) + "'" + "," + 
                            " edx_outsegdelim = " + "'" + Integer.valueOf(c[35].toString()) + "'" + "," +
                            " edx_outelmdelim = " + "'" + Integer.valueOf(c[36].toString()) + "'" + "," +  
                            " edx_outsubdelim = " + "'" + Integer.valueOf(c[37].toString()) + "'" + "," + 
                            " edx_status = " + "'" + c[23] + "'"  + 
                            " where edx_id = " + "'" + key + "'" +        
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
         
      }
    
    public static void updateEDIIDXStatus(int key, String status) {
            
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                     
                    st.executeUpdate("update edi_idx set " +
                            " edx_status = " + "'" + status + "'" +
                            " where edx_id = " + "'" + key + "'" +        
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
         
      }
    
    public static void updateEDIIDXAcks(ArrayList<String[]> keys) {
            // keys is a string[] with doctype, groupctrlnum, docctrlnum, ackfile 
          try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                
                
                    for (String[] key : keys) { 
                    st.executeUpdate("update edi_idx set " +
                            " edx_ackfile = " + "'" + key[3] + "'" + "," +
                            " edx_ack = '1' " +        
                            " where edx_outdoctype = " + "'" + key[0] + "'" +     
                            " and edx_gsctrlnum = " + "'" + key[1] + "'" +
                            " and edx_stctrlnum = " + "'" + key[2] + "'" + 
                            ";");
                    }
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
         
      }
    
    public static int updateEDIIDXAcksAllGroup(String doctype, String groupid, String senderid, String ackfile) {
        // some 997s do not send ST specific...but GS (group specific)...update all doc of GS group ID
            // keys is a string[] with doctype, groupctrlnum, ackfile 
            int count = 0;
            try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                   count = st.executeUpdate("update edi_idx set " +
                            " edx_ackfile = " + "'" + ackfile + "'" + "," +
                            " edx_ack = '1' " +        
                            " where edx_outdoctype = " + "'" + doctype + "'" +     
                            " and edx_gsctrlnum = " + "'" + groupid + "'" +
                            " and edx_receiver = " + "'" + senderid + "'" +   // sender should be receiver of original outbound transmission     
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return count; 
      }
    
    public static int updateEDIIDXAcksAllGroupCONTRL(String doctype, String controlnbr, String senderid, String ackfile) {
        // some 997s do not send ST specific...but GS (group specific)...update all doc of GS group ID
            // keys is a string[] with doctype, groupctrlnum, ackfile 
            int count = 0;
            try {
            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                   count = st.executeUpdate("update edi_idx set " +
                            " edx_ackfile = " + "'" + ackfile + "'" + "," +
                            " edx_ack = '1' " +        
                            " where edx_outdoctype = " + "'" + doctype + "'" +     
                            " and edx_ctrlnum = " + "'" + controlnbr + "'" +
                            " and edx_receiver = " + "'" + senderid + "'" +   // sender should be receiver of original outbound transmission     
                            ";");
                        
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (st != null) st.close();
               con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        return count; 
      }
    
    
    public static ArrayList parseFile(char[] cbuf, String filename)   {
    ArrayList<String> doc = new ArrayList<String>();
    
    if (cbuf == null || cbuf.length <= 3) {
        return doc;
    }
    
    char flddelim = 0;
    char subdelim = 0;
    char segdelim = 0;
    
    String flddelim_str = "";
    String subdelim_str = "";
    String segdelim_str = "";
    
    /* lets set the delimiters X12 */
    if (cbuf[0] == 'I' &&
        cbuf[1] == 'S' &&
        cbuf[2] == 'A') {
           flddelim = cbuf[103];
           subdelim = cbuf[104];
           segdelim = cbuf[105];
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
        
          // special case for 0d0a ...if so use both characters as segment delimiter
           if (String.format("%02x",(int) cbuf[105]).equals("0d") && String.format("%02x",(int) cbuf[106]).equals("0a")) {
            segdelim_str = String.valueOf(cbuf[105]) + String.valueOf(cbuf[106]);  
           }
        
        
        
        if (flddelim_str.equals("*")) {
            flddelim_str = "\\*";
        }
        if (flddelim_str.equals("^")) {
            flddelim_str = "\\^";
        }
              
    } 
    
     /* lets set the delimiters EDIFACT */
    if (cbuf[0] == 'U' &&
        cbuf[1] == 'N' &&
        cbuf[2] == 'B') {
           flddelim = '+';
           subdelim = ':';
           segdelim = '\'';
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
        
        if (flddelim_str.equals("*")) {
            flddelim_str = "\\*";
        }
        if (flddelim_str.equals("^")) {
            flddelim_str = "\\^";
        }
              
    } 
    
    /* lets set delimiters for CSV */
    if (filename.toUpperCase().endsWith(".CSV") ) {
           flddelim = ',';
           subdelim = ':';
           segdelim = '\n';
        flddelim_str = String.valueOf(flddelim);
        subdelim_str = String.valueOf(subdelim);
        segdelim_str = String.valueOf(segdelim);
    }
    
    /* lets set delimiters for XML */
    if (filename.toUpperCase().endsWith(".XML") ) {
           segdelim = '\n';
        segdelim_str = String.valueOf(segdelim);
    }
    
    
    
    StringBuilder segment = new StringBuilder();
    if (filename.toUpperCase().endsWith(".XML") ) {
        for (int i = 0; i < cbuf.length; i++) {
        segment.append(cbuf[i]);
        }
        doc.add(segment.toString());
    } else {
        
    for (int i = 0; i < cbuf.length; i++) {
        if (cbuf[i] == segdelim) {
            doc.add(segment.toString());
            segment.delete(0, segment.length());
        } else {
            if (! (String.format("%02x",(int) cbuf[i]).equals("0d") || String.format("%02x",(int) cbuf[i]).equals("0a")) ) {
                segment.append(cbuf[i]);
            } 
        }
    } 
    
    }
    
    return doc;
    }
   
    public static ArrayList parseCbuf(char[] cbuf, String[] delims)   {
    // delims should be in seg,ele,sub order
    ArrayList<String> doc = new ArrayList<String>();
    char segdelim = (char) delims[0].charAt(0);
    StringBuilder segment = new StringBuilder();
    
    for (int i = 0; i < cbuf.length; i++) {
        if (cbuf[i] == segdelim) {
            doc.add(segment.toString());
            segment.delete(0, segment.length());
        } else {
            if (! (String.format("%02x",(int) cbuf[i]).equals("0d") || String.format("%02x",(int) cbuf[i]).equals("0a")) ) {
                segment.append(cbuf[i]);
            } 
        }
    } 
    
   
    
    return doc;
    }
   
    public static ArrayList<String> cbufToList(char[] cbuf, String[] delims) {
        ArrayList<String> x = new ArrayList<String>();
        x = EDData.parseCbuf(cbuf, delims);
        return x;
    }
    
    
    public static String[] getDelimiters(char[] cbuf, String filename)   {
    // returns as string characters "*" ...not decimal
        
        String[] x = null;
    
    char flddelim = 0;
    char subdelim = 0;
    char segdelim = 0;
    
    
    /* lets set the delimiters X12 */
    if (cbuf[0] == 'I' &&
        cbuf[1] == 'S' &&
        cbuf[2] == 'A') {
           flddelim = cbuf[103];
           subdelim = cbuf[104];
           segdelim = cbuf[105];
           x = new String[]{String.valueOf(segdelim), String.valueOf(flddelim), String.valueOf(subdelim)};
        
        
          // special case for 0d0a ...if so use both characters as segment delimiter
           
           if (String.format("%02x",(int) cbuf[105]).equals("0d") && String.format("%02x",(int) cbuf[106]).equals("0a")) {
            x[0] = String.valueOf(cbuf[105]) + String.valueOf(cbuf[106]);  
           }
          
        
        
        /*
        if (x[0].equals("*")) {
            x[0] = "\\*";
        }
        if (x[0].equals("^")) {
            x[0] = "\\^";
        }
        */
        return x;
    } 
    
    StringBuilder sb = new StringBuilder();
    sb.append(cbuf, 0, 3);
    String una_unb = new String(cbuf,9,3); // file may be prefixed with UNA service tag...EX: UNAxxxxx'UNB
    if (sb.toString().equals("UNB") || ( sb.toString().equals("UNA") && una_unb.equals("UNB")) ) {
         
         // 39 = '
         // 58 = :
         // 43 = +
         // 63 = ?
         // char segdelim = (char) Integer.valueOf("39").intValue(); 
         if (sb.toString().equals("UNA")) {
             segdelim = cbuf[8];
             flddelim = cbuf[4];
             subdelim = cbuf[3];
         } else {
           flddelim = '+';
           subdelim = ':';
           segdelim = '\'';
         }
    
           
           
        x = new String[]{String.valueOf(segdelim), String.valueOf(flddelim), String.valueOf(subdelim)};
           
        x[0] = escapeDelimiter(x[0]);
        x[1] = escapeDelimiter(x[1]);
        x[2] = escapeDelimiter(x[2]);
       
        return x;     
    } 
    
    /* lets set delimiters for CSV */
    if (filename.toUpperCase().endsWith(".CSV") ) {
           flddelim = ',';
           subdelim = ':';
           segdelim = '\n';
        x = new String[]{String.valueOf(segdelim), String.valueOf(flddelim), String.valueOf(subdelim)};
        
        return x;
    }
    
    /* lets set delimiters for XML */
    if (filename.toUpperCase().endsWith(".XML") ) {
           segdelim = '\n';
        x = new String[]{String.valueOf(segdelim),"",""};
        return x;
    }
    
    if (x == null) {
           segdelim = '\n';
        x = new String[]{String.valueOf(segdelim),"",""};
    }
    
    return x;
    }
       
     
    public static boolean CreateShipperHdrEDI(String[] control, String nbr, String bol, String billto, String shipto, String so, String po, String shipdate, String orddate, String remarks, String shipvia ) {
          boolean isError = false; 
          
          try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                // initialize ord and due date if blank
                if (orddate.isEmpty() || orddate == null) {
                    orddate = dfdate.format(now);
                }
                if (shipdate.isEmpty() || shipdate == null) {
                    shipdate = dfdate.format(now);
                }
                
                if (! BlueSeerUtils.isValidDateStr(orddate)) {
                    if (orddate.length() == 8) {
                        DateFormat format = new SimpleDateFormat("yyyyMMdd");
                        Date mydate = format.parse(orddate);
                        orddate = BlueSeerUtils.setDateFormat(mydate);
                    }  
                    if (orddate.length() == 6) {
                        DateFormat format = new SimpleDateFormat("yyMMdd");
                        Date mydate = format.parse(orddate);
                        orddate = BlueSeerUtils.setDateFormat(mydate);
                    }   
                }
                
                if (! BlueSeerUtils.isValidDateStr(shipdate)) {
                    if (shipdate.length() == 8) {
                        DateFormat format = new SimpleDateFormat("yyyyMMdd");
                        Date mydate = format.parse(shipdate);
                        shipdate = BlueSeerUtils.setDateFormat(mydate);
                    }  
                    if (shipdate.length() == 6) {
                        DateFormat format = new SimpleDateFormat("yyMMdd");
                        Date mydate = format.parse(shipdate);
                        shipdate = BlueSeerUtils.setDateFormat(mydate);
                    }   
                }
                
                
                // get billto specific data
                String acct = "";
                String cc = "";
                String terms = "";
                String carrier = "";
                String onhold = "";
                String curr = "";
                String taxcode = "";
                String site = OVData.getDefaultSite();
                
                res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + billto + "'" + " ;");
               while (res.next()) {
                   i++;
                   acct = res.getString("cm_ar_acct");
                   cc = res.getString("cm_ar_cc");
                   carrier = res.getString("cm_carrier");
                   terms = res.getString("cm_terms");
                   taxcode = res.getString("cm_tax_code");
                   onhold = res.getString("cm_onhold");
                   curr = res.getString("cm_curr");
                }
                if (i == 0) {
                    proceed = false;
                    writeEDILog(control, "ERROR", " Unknown Billto=" + billto);
                }
                if (acct.isEmpty() || cc.isEmpty() || terms.isEmpty()) {
                    proceed = false;
                    writeEDILog(control, "ERROR", "cust " + billto + " Acct or CC or Terms is empty");
                   
                }

                if (! shipvia.isEmpty()) {
                    carrier = shipvia;
                }
                
                
                
                if (proceed) {
                    st.executeUpdate("insert into ship_mstr " 
                        + " (sh_id, sh_cust, sh_ship,"
                        + " sh_shipdate, sh_po_date, sh_bol, sh_po, sh_rmks, sh_userid, sh_site, sh_curr, sh_cust_terms, sh_taxcode, sh_ar_acct, sh_ar_cc ) "
                        + " values ( " + "'" + nbr + "'" + "," 
                        + "'" + billto + "'" + "," 
                        + "'" + shipto + "'" + ","
                        + "'" + shipdate + "'" + ","
                        + "'" + orddate + "'" + ","
                        + "'" + bol + "'" + "," 
                        + "'" + po + "'" + "," 
                        + "'" + remarks + "'" + "," 
                        + "'" + bsmf.MainFrame.userid + "'" + "," 
                        + "'" + site + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + taxcode + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'"
                        + ");" );
                } // if proceed
                else {
                    isError = true;
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                isError = true;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return isError;
      } 
    
     
       public static void CreateFOTDETFrom990i(String[] control, String nbr, String scac, String yesno, String reasoncode ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                String[] c = control;
                //   c = senderid + "," + doctype + "," + controlnum + "," + docid + "," + filename; 
                if (proceed) {
                        st.executeUpdate("insert into fot_det "
                            + "(fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_status, fot_remarks, fot_date ) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + c[4] + "-" + c[6] + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + yesno + "'" + ","
                            + "'" + reasoncode + "'" + ","
                            + "'" + dfdate.format(now) + "'" 
                            + ")"
                            + ";");
                        
                     // now lets attempt to update the fo_mstr with response.   
                     String status = "";
                     if (yesno.equals("E") || yesno.equals("D")) {
                         status = "Declined";
                     }
                     if (yesno.equals("A")) {
                         status = "Accepted";
                     }
                     
                      st.executeUpdate("update fo_mstr set "
                            + "fo_status = " + "'" + status + "'" 
                            + " where fo_nbr = " + "'" + nbr + "'" 
                            + ";");
                        
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }  
       
        public static void CreateFOTDETFrom204i(String[] control, String nbr, String remarks, String custfo ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                String[] c = control;
                //   c = senderid + "," + doctype + "," + controlnum + "," + docid + "," + filename; 
                if (proceed) {
                        st.executeUpdate("insert into fot_det "
                            + "(fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_dir, fot_remarks, fot_date ) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + custfo + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + "In" + "'" + ","
                            + "'" + remarks + "'" + ","
                            + "'" + dfdate.format(now) + "'" 
                            + ")"
                            + ";");
                        
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }  
       
       public static void CreateFOTDETFrom220i(String[] control, String nbr, String scac, String yesno, String remarks, String amount ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                String[] c = control;
                //   c = senderid + "," + doctype + "," + controlnum + "," + docid + "," + filename; 
                if (proceed) {
                    
                      // determine appropriate status
                     String status = "";
                     if (yesno.equals("44")) {
                         status = "Rejected";
                     }
                     if (yesno.equals("11")) {
                         status = "Accepted";
                     }
                    
                        st.executeUpdate("insert into fot_det "
                            + "(fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_status, fot_remarks, fot_amt, fot_date ) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + c[4] + "-" + c[6] + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + status + "'" + ","
                            + "'" + remarks + "'" + ","
                            + "'" + amount + "'" + ","
                            + "'" + dfdate.format(now) + "'" 
                            + ")"
                            + ";");
                        
                        
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }  
       
       public static String CreateFOMSTRFrom204i(String[] control, String custfo, String carrier, String equiptype, String remarks, String bol, String cust, String tpid, String weight, String ref ) {
           String fo = "";           
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
              
                //   c = senderid + "," + doctype + "," + controlnum + "," + docid + "," + filename; 
                if (proceed) {
                    
                         res = st.executeQuery("select fo_custfo from fo_mstr where fo_custfo = " + "'" + custfo + "'" 
                         + " AND fo_dir = 'In' "
                         + " ;");
                           while (res.next()) {
                               i++;
                           }
                    
                        // if i == 0 must be new customer 204...insert fo_mstr and fod_det   
                       if (i == 0) {    
                        fo = String.valueOf(OVData.getNextNbr("fo"));
                        st.executeUpdate("insert into fo_mstr "
                            + "(fo_nbr, fo_dir, fo_type, fo_tpid, fo_cust, fo_custfo, fo_carrier, fo_carrier_assigned, fo_equipment_type, fo_rmks, fo_status, fo_bol, fo_date, fo_weight, fo_ref ) "
                            + " values ( " + "'" + fo + "'" + ","
                            + "'" + "In" + "'" + ","
                            + "'" + "tender" + "'" + ","        
                            + "'" + tpid + "'" + ","
                            + "'" + cust + "'" + ","
                            + "'" + custfo + "'" + ","
                            + "'" + carrier + "'" + ","
                            + "'" + carrier + "'" + ","
                            + "'" + equiptype + "'" + ","
                            + "'" + remarks + "'" + ","
                            + "'" + "Open" + "'" + ","        
                            + "'" + bol + "'" + ","
                            + "'" + dfdate.format(now) + "'" + ","
                            + "'" + weight + "'" + ","
                            + "'" + ref + "'"
                            + ")"
                            + ";");
                        
                        
                        // create the fot_det
                        CreateFOTDETFrom204i(control, fo, remarks, custfo );
                        EDData.writeEDILog(control, "INFO", "FONbr: " + custfo);
                       } else {
                           EDData.writeEDILog(control, "INFO", "duplicate: " + custfo);
                       }
                        
                        
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
           return fo;
      }  
       
       public static String CancelFOFrom204i(String custfo ) {
           String fo = "";           
           try {

            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
          
            try {
                
                String status = "";
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
               
              
                    
                         res = st.executeQuery("select fo_custfo, fo_status from fo_mstr where fo_custfo = " + "'" + custfo + "'" 
                         + " AND fo_dir = 'In' "
                         + " ;");
                           while (res.next()) {
                               i++;
                               status = res.getString("fo_status");
                           }
                           
                           if (! status.equals("InTransit")) {
                              st.executeUpdate("update fo_mstr set fo_status = 'Cancelled' where fo_custfo = " + "'" + custfo + "'" + ";"); 
                           }
                        
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
           return fo;
      }  
      
         
      
        public static void CreateFODDETFrom204i(String fo, String line, String type, String shipper, String delvdate, String delvtime, String shipdate, String shiptime,
                String addrcode, String addrname, String addrline1, String addrline2, String addrcity, String addrstate, String addrzip,
                String units, String boxes, String weight, String weightuom, String ref, String remarks ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                if (weight.isEmpty())
                    weight = "0";
                if (units.isEmpty())
                    units = "0";
                
                
                if (proceed) {
                        st.executeUpdate("insert into fod_det "
                            + "(fod_nbr, fod_line, fod_type, fod_shipper, fod_delvdate, fod_delvtime, fod_shipdate, fod_shiptime, " 
                            + " fod_code, fod_name, fod_addr1, fod_addr2, fod_city, fod_state, fod_zip, " 
                            + " fod_units, fod_boxes, fod_weight, fod_wt_uom, fod_ref, fod_remarks ) "
                            + " values ( " + "'" + fo + "'" + ","
                            + "'" + line + "'" + ","
                            + "'" + type + "'" + ","
                            + "'" + shipper + "'" + ","
                            + "'" + delvdate + "'" + ","
                            + "'" + delvtime + "'" + ","
                            + "'" + shipdate + "'" + ","
                            + "'" + shiptime + "'" + ","        
                            + "'" + addrcode + "'" + ","
                            + "'" + addrname + "'" + ","
                            + "'" + addrline1 + "'" + ","
                            + "'" + addrline2 + "'" + ","
                            + "'" + addrcity + "'" + ","
                            + "'" + addrstate + "'" + ","
                            + "'" + addrzip + "'" + ","
                            + "'" + units + "'" + ","
                            + "'" + boxes + "'" + ","
                            + "'" + weight + "'" + ","
                            + "'" + weightuom + "'" + ","
                            + "'" + ref + "'" + ","
                            + "'" + remarks + "'"
                            + ")"
                            + ";");
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }  
       
       public static void CreateFOTDETFrom214i(String[] control, String nbr, String scac, String pronbr, String status, String remarks, String lat, String lon, String equipmentnbr, String equipmenttype, String apptdate, String appttime ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                String[] c = control;
                //   c = senderid + "," + doctype + "," + controlnum + "," + docid + "," + filename; 
                if (proceed) {
                    
                      // determine appropriate status
                     String s = "";
                     if (status.equals("AF")) {
                         s = "Pickup";
                     }
                     if (status.equals("X6")) {
                         s = "Transit";
                     }
                     if (status.equals("X1")) {
                         s = "Delivery";
                     }
                     if (status.equals("SD")) {
                         s = "Delay";
                     }
                    
                        st.executeUpdate("insert into fot_det "
                            + "(fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_status, fot_remarks, fot_lat, fot_lon, fot_equipnbr, fot_equiptype, fot_apptdate, fot_appttime, fot_date, fot_dir ) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + c[4] + "-" + pronbr + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + s + "'" + ","
                            + "'" + remarks + "'" + ","
                            + "'" + lat + "'" + ","
                            + "'" + lon + "'" + ","
                            + "'" + equipmentnbr + "'" + ","
                            + "'" + equipmenttype + "'" + ","
                            + "'" + apptdate + "'" + ","
                            + "'" + appttime + "'" + ","
                            + "'" + dfdate.format(now) + "'" + "," 
                            + "'" + "In" + "'"
                            + ")"
                            + ";");
                        
                        
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }   
       
       public static void CreateFreightEDIRecs(String[] c, String nbr ) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
               
              // controlarray in this order : entity, doctype, map, filename, isacontrolnum, gsctrlnum, stctrlnum, ref ; 
                if (proceed) {
                        st.executeUpdate("insert into fot_det "
                            + "(fot_nbr, fot_uniqueid, fot_partnerid, fot_doctype, fot_docfile, fot_date ) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + c[4] + "'" + ","
                            + "'" + c[0] + "'" + ","
                            + "'" + c[1] + "'" + ","
                            + "'" + c[3] + "'" + ","
                            + "'" + dfdate.format(now) + "'" 
                            + ")"
                            + ";");
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }  
       
      
      public static boolean CreateSalesOrderHdr(String[] control, String nbr, String billto, String shipto, String po, String duedate, String orddate, String remarks, String shipvia ) {
          boolean isError = false; 
          
          try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                
                // initialize ord and due date if blank
                if (orddate.isEmpty() || orddate == null) {
                    orddate = dfdate.format(now);
                }
                if (duedate.isEmpty() || duedate == null) {
                    duedate = dfdate.format(now);
                }
                
                if (! BlueSeerUtils.isValidDateStr(orddate)) {
                    if (orddate.length() == 8) {
                        DateFormat format = new SimpleDateFormat("yyyyMMdd");
                        Date mydate = format.parse(orddate);
                        orddate = BlueSeerUtils.setDateFormat(mydate);
                    }  
                    if (orddate.length() == 6) {
                        DateFormat format = new SimpleDateFormat("yyMMdd");
                        Date mydate = format.parse(orddate);
                        orddate = BlueSeerUtils.setDateFormat(mydate);
                    }   
                }
                
                if (! BlueSeerUtils.isValidDateStr(duedate)) {
                    if (duedate.length() == 8) {
                        DateFormat format = new SimpleDateFormat("yyyyMMdd");
                        Date mydate = format.parse(duedate);
                        duedate = BlueSeerUtils.setDateFormat(mydate);
                    }  
                    if (duedate.length() == 6) {
                        DateFormat format = new SimpleDateFormat("yyMMdd");
                        Date mydate = format.parse(duedate);
                        duedate = BlueSeerUtils.setDateFormat(mydate);
                    }   
                }
                
                
                // get billto specific data
                String acct = "";
                String cc = "";
                String terms = "";
                String carrier = "";
                String onhold = "";
                String site = OVData.getDefaultSite();
                
                res = st.executeQuery("select * from cm_mstr where cm_code = " + "'" + billto + "'" + " ;");
               while (res.next()) {
                   i++;
                   acct = res.getString("cm_ar_acct");
                   cc = res.getString("cm_ar_cc");
                   carrier = res.getString("cm_carrier");
                   terms = res.getString("cm_terms");
                   onhold = res.getString("cm_onhold");
                }
                if (i == 0) {
                    proceed = false;
                    writeEDILog(control, "ERROR", " Unknown Billto=" + billto);
                }
                if (acct.isEmpty() || cc.isEmpty() || terms.isEmpty()) {
                    proceed = false;
                    writeEDILog(control, "ERROR", "cust " + billto + " Acct or CC or Terms is empty");
                   
                }

                if (! shipvia.isEmpty()) {
                    carrier = shipvia;
                }
                
                
                
                if (proceed) {
                    st.executeUpdate("insert into so_mstr "
                        + "(so_nbr, so_cust, so_ship, so_po, so_ord_date, so_due_date, "
                        + "so_create_date, so_userid, so_status,"
                        + "so_rmks, so_terms, so_ar_acct, so_ar_cc, so_shipvia, so_type, so_site, so_onhold ) "
                        + " values ( " + "'" + nbr + "'" + ","
                        + "'" + billto + "'" + ","
                        + "'" + shipto + "'" + ","
                        + "'" + po + "'" + ","
                        + "'" + orddate + "'" + ","
                        + "'" + duedate + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + "edi" + "'" + ","
                        + "'" + "open" + "'" + ","
                        + "'" + remarks + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + acct + "'" + ","
                        + "'" + cc + "'" + ","
                        + "'" + carrier + "'" + ","
                        + "'DISCRETE'" + ","
                        + "'" + site + "'" + ","
                        + "'" + onhold + "'"
                        + ")"
                        + ";");
                } // if proceed
                else {
                    isError = true;
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                isError = true;
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return isError;
      }
      
      public static void CreateSalesOrderDet(String nbr, String billto, String part, String custpart, String skupart, String po, String qty, String listprice, String discpercent, String netprice, String duedate, String ref, String line) {
           try {

            Class.forName(driver);
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
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String site = OVData.getDefaultSite();

                if (proceed) {
                        st.executeUpdate("insert into sod_det "
                            + "(sod_nbr, sod_item, sod_site, sod_po, sod_ord_qty, sod_netprice, sod_listprice, sod_disc, sod_due_date, "
                            + "sod_shipped_qty, sod_custitem, sod_status, sod_line) "
                            + " values ( " + "'" + nbr + "'" + ","
                            + "'" + part + "'" + ","
                            + "'" + site + "'" + ","
                            + "'" + po + "'" + ","
                            + "'" + qty + "'" + ","
                            + "'" + netprice + "'" + ","
                            + "'" + listprice + "'" + ","
                            + "'" + discpercent + "'" + ","
                            + "'" + duedate + "'" + ","
                            + '0' + "," + "'" + custpart + "'" +  "," 
                            + "'" + "open" + "'" + ","
                            + "'" + line + "'"
                            + ")"
                            + ";");
                } // if proceed
            }  catch (SQLException s) {
                MainFrame.bslog(s);
           } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
       
      public static String GetBlanketOrderLine(String[] control, String shipto, String part, String po) {
          String myreturn = "";
          boolean proceed = true;
          String order = "";
          String line = "";
          try {

            Class.forName(driver);
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                
                // determine sales order number from shipto and blanket type  NOTE:  shipto/blanket should be unique
                 res = st.executeQuery("select so_nbr from so_mstr where so_ship = " + "'" + shipto + "'" 
                         + " AND so_type = 'BLANKET' "
                         + " ;");
               while (res.next()) {
                   order = res.getString("so_nbr");
                   i++;
               }
               if (i == 0) {
                   writeEDILog(control, "ERROR", "No blanket order found for " + shipto  );
                   proceed = false;
               } else {
                   res = st.executeQuery("select sod_line from sod_det where sod_nbr = " + "'" + order + "'" 
                         + " AND sod_item = " + "'" + part + "'" 
                         + " AND sod_po = " + "'" + po + "'" 
                         + " AND sod_status = 'OPEN' "                           
                         + " ;");
                   while (res.next()) {
                   line = res.getString("sod_line");
                   i++;
                   }
                   
                   if (line.isEmpty()) {
                       writeEDILog(control, "ERROR", "No open order line found for " + order + "/" + shipto + "/" + part + "/" + po  );
                       proceed = false;
                   }
               }
               
            if (proceed)
                  myreturn = order + "," + line;
              
            }  catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
      
    
    
}
