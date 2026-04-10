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
package com.blueseer.srv;

import static com.blueseer.adm.admData.getLoginInit;
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.addUpdateAPCtrl;
import static com.blueseer.fap.fapData.getAPCtrl;
import com.blueseer.far.farData;
import static com.blueseer.far.farData.addUpdateARCtrl;
import static com.blueseer.far.farData.getARCtrl;
import static com.blueseer.far.farData.getARMstr;
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.fgl.fglData.addAcctMstr;
import static com.blueseer.fgl.fglData.addBankMstr;
import static com.blueseer.fgl.fglData.addCurrMstr;
import static com.blueseer.fgl.fglData.addDeptMstr;
import static com.blueseer.fgl.fglData.addExcMstr;
import static com.blueseer.fgl.fglData.addGL;
import static com.blueseer.fgl.fglData.addUpdateGLCal;
import static com.blueseer.fgl.fglData.addUpdateGLCtrl;
import static com.blueseer.fgl.fglData.addUpdatePAYCtrl;
import static com.blueseer.fgl.fglData.deleteAcctMstr;
import static com.blueseer.fgl.fglData.deleteBankMstr;
import static com.blueseer.fgl.fglData.deleteCurrMstr;
import static com.blueseer.fgl.fglData.deleteDeptMstr;
import static com.blueseer.fgl.fglData.deleteExcMstr;
import static com.blueseer.fgl.fglData.deleteGL;
import static com.blueseer.fgl.fglData.deletePayProfile;
import static com.blueseer.fgl.fglData.deleteTaxMstr;
import com.blueseer.fgl.fglData.exc_mstr;
import static com.blueseer.fgl.fglData.getAcctMstr;
import static com.blueseer.fgl.fglData.getBankMstr;
import static com.blueseer.fgl.fglData.getCurrMstr;
import static com.blueseer.fgl.fglData.getDeptMstr;
import static com.blueseer.fgl.fglData.getExcMstr;
import static com.blueseer.fgl.fglData.getFINInit;
import static com.blueseer.fgl.fglData.getFglRptPickerData;
import static com.blueseer.fgl.fglData.getGLAcctListRangeWCurrTypeDesc;
import static com.blueseer.fgl.fglData.getGLBalByYearByPeriod;
import static com.blueseer.fgl.fglData.getGLCal;
import static com.blueseer.fgl.fglData.getGLCalForPeriod;
import static com.blueseer.fgl.fglData.getGLCalForPeriodRange;
import static com.blueseer.fgl.fglData.getGLCalYearsRange;
import static com.blueseer.fgl.fglData.getGLCtrl;
import static com.blueseer.fgl.fglData.getGLHist;
import static com.blueseer.fgl.fglData.getGLTran;
import static com.blueseer.fgl.fglData.getGLTranCount;
import static com.blueseer.fgl.fglData.getPAYCtrl;
import static com.blueseer.fgl.fglData.getPayProfile;
import static com.blueseer.fgl.fglData.getPayProfileDet;
import static com.blueseer.fgl.fglData.getTaxDet;
import static com.blueseer.fgl.fglData.getTaxMstr;
import static com.blueseer.fgl.fglData.get_pie_EmpPayByDate;
import static com.blueseer.fgl.fglData.get_pie_EmpTypePayByDate;
import static com.blueseer.fgl.fglData.updateAcctMstr;
import static com.blueseer.fgl.fglData.updateBankMstr;
import static com.blueseer.fgl.fglData.updateCurrMstr;
import static com.blueseer.fgl.fglData.updateDeptMstr;
import static com.blueseer.fgl.fglData.updateExcMstr;
import static com.blueseer.fgl.fglData.updateReconGLRecord;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getExchangeBaseValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServFIN extends HttpServlet {
 
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    response.setContentType("text/plain");
        
    if (! confirmServerAuth(request)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("br549 authorization failed");
        return;
    }
    
        
    if (request.getParameter("id") == null || request.getParameter("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id");  
      return;
    }
        
        String id = request.getParameter("id");
        
        response.setStatus(HttpServletResponse.SC_OK);
        
        
        /*
        if (id.equals("getAccountBalanceReport")) {
           String[] keys = new String[]{
               request.getParameter("year"), 
               request.getParameter("period"), 
               request.getParameter("site"), 
               request.getParameter("iscc"), 
               request.getParameter("intype"), 
               request.getParameter("fromacct"), 
               request.getParameter("toacct")  
           }; 
           
           for (String k : keys) {
               if (k == null) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing param");  
                   return;
               }
           }
           
           String r = getAccountBalanceReport(keys);
           
           if (r == null || r.isBlank()) {
             response.getWriter().println("no return for: " + String.join(",",keys));   
           } else {
             response.getWriter().println(r);   
           }
        } 
        */
        
    }

 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    response.setContentType("text/plain");
    
    if (! confirmServerAuthAPI(request, authServ.hmuser)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(" br549finpost authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id");
    
    switch (id) {
     
    case "getLoginInit" : { 
      String user = request.getHeader("user");          
      response.getWriter().print(ArrayListStringArrayToJson(getLoginInit(user)));
      break;
    }
          
    case "addAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(addAcctMstr(am)));
      break;
    }
    
    case "updateAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(updateAcctMstr(am)));
      break;
    }
    
    case "deleteAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(deleteAcctMstr(am)));
      break;
    }
    
    case "getAcctMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      AcctMstr am = getAcctMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "addDeptMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.dept_mstr am = objectMapper.readValue(sb.toString(), fglData.dept_mstr.class);            
      response.getWriter().print(arrayToJson(addDeptMstr(am)));
      break;
    }
    
    case "updateDeptMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.dept_mstr am = objectMapper.readValue(sb.toString(), fglData.dept_mstr.class);            
      response.getWriter().print(arrayToJson(updateDeptMstr(am)));
      break;
    }
    
    case "deleteDeptMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.dept_mstr am = objectMapper.readValue(sb.toString(), fglData.dept_mstr.class);            
      response.getWriter().print(arrayToJson(deleteDeptMstr(am)));
      break;
    }
        
    case "getDeptMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.dept_mstr am = getDeptMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "addBankMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.BankMstr am = objectMapper.readValue(sb.toString(), fglData.BankMstr.class);            
      response.getWriter().print(arrayToJson(addBankMstr(am)));
      break;
    }
    
    case "updateBankMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.BankMstr am = objectMapper.readValue(sb.toString(), fglData.BankMstr.class);            
      response.getWriter().print(arrayToJson(updateBankMstr(am)));
      break;
    }
    
    case "deleteBankMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.BankMstr am = objectMapper.readValue(sb.toString(), fglData.BankMstr.class);            
      response.getWriter().print(arrayToJson(deleteBankMstr(am)));
      break;
    }
        
    case "getBankMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.BankMstr am = getBankMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "addCurrMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.CurrMstr am = objectMapper.readValue(sb.toString(), fglData.CurrMstr.class);            
      response.getWriter().print(arrayToJson(addCurrMstr(am)));
      break;
    }
    
    case "updateCurrMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.CurrMstr am = objectMapper.readValue(sb.toString(), fglData.CurrMstr.class);            
      response.getWriter().print(arrayToJson(updateCurrMstr(am)));
      break;
    }
    
    case "deleteCurrMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.CurrMstr am = objectMapper.readValue(sb.toString(), fglData.CurrMstr.class);            
      response.getWriter().print(arrayToJson(deleteCurrMstr(am)));
      break;
    }
      
    case "getCurrMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.CurrMstr am = getCurrMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "addExcMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.exc_mstr am = objectMapper.readValue(sb.toString(), fglData.exc_mstr.class);            
      response.getWriter().print(arrayToJson(addExcMstr(am)));
      break;
    }
    
    case "updateExcMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.exc_mstr am = objectMapper.readValue(sb.toString(), fglData.exc_mstr.class);            
      response.getWriter().print(arrayToJson(updateExcMstr(am)));
      break;
    }
    
    case "deleteExcMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.exc_mstr am = objectMapper.readValue(sb.toString(), fglData.exc_mstr.class);            
      response.getWriter().print(arrayToJson(deleteExcMstr(am)));
      break;
    }
      
    
    case "getExcMstr" : {
      String base = request.getHeader("base"); 
      ArrayList<exc_mstr> emlist = getExcMstr(base);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(emlist);
      response.getWriter().print(r);
      break;
    }
        
    case "getARMstr" : { 
      String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
      farData.ar_mstr am = getARMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "addGLpair" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_pair x = objectMapper.readValue(sb.toString(), fglData.gl_pair.class);            
      response.getWriter().print(arrayToJson(addGL(x)));
      break;
    }
    
    case "addGLtran" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_tran x = objectMapper.readValue(sb.toString(), fglData.gl_tran.class);            
      response.getWriter().print(arrayToJson(addGL(x)));
      break;
    }
    
    case "addGLtrans" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_tran[] x = objectMapper.readValue(sb.toString(), fglData.gl_tran[].class);   
      ArrayList<fglData.gl_tran> list = new ArrayList<fglData.gl_tran>(Arrays.asList(x));       
      response.getWriter().print(arrayToJson(addGL(list)));
      break;
    }
    
    case "addUpdateGLCal" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_cal x = objectMapper.readValue(sb.toString(), fglData.gl_cal.class);            
      response.getWriter().print(arrayToJson(addUpdateGLCal(x)));
      break;
    }
    
    case "addUpdateGLCtrl" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_ctrl x = objectMapper.readValue(sb.toString(), fglData.gl_ctrl.class);            
      response.getWriter().print(arrayToJson(addUpdateGLCtrl(x)));
      break;
    }
    
    case "addUpdatePAYCtrl" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.pay_ctrl x = objectMapper.readValue(sb.toString(), fglData.pay_ctrl.class);            
      response.getWriter().print(arrayToJson(addUpdatePAYCtrl(x)));
      break;
    }
    
    
    case "getGLTran" : {       
        ArrayList<fglData.gl_tran> xd = getGLTran(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
    
    case "getGLHist" : {       
        ArrayList<fglData.gl_hist> xd = getGLHist(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
    
    case "getGLTranCount" : {
            response.getWriter().print(intToJson(getGLTranCount())); 
            break;    
        }
    
    case "deleteGL" : {
            response.getWriter().print(arrayToJson(deleteGL(request.getHeader("param1"))));  
            break;
        }
    
    case "addTaxTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            fglData.taxd_mstr[] sdarray = om.readValue(ca[0], fglData.taxd_mstr[].class);
            ArrayList<fglData.taxd_mstr> txd = (sdarray == null) ? null :new ArrayList<fglData.taxd_mstr>(Arrays.asList(sdarray)); 
            fglData.tax_mstr tx = om.readValue(ca[1], fglData.tax_mstr.class);
            response.getWriter().print(arrayToJson(fglData.addTaxTransaction(txd, tx)));     
            break; 
            }   
    
    case "updateTaxTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String key = ca[0];
            ArrayList<String> badlist = om.readValue(ca[1], ArrayList.class);
            fglData.taxd_mstr[] sdarray = om.readValue(ca[2], fglData.taxd_mstr[].class);
            ArrayList<fglData.taxd_mstr> txd = (sdarray == null) ? null :new ArrayList<fglData.taxd_mstr>(Arrays.asList(sdarray)); 
            fglData.tax_mstr tx = om.readValue(ca[3], fglData.tax_mstr.class);
            response.getWriter().print(arrayToJson(fglData.updateTaxTransaction(key, badlist, txd, tx)));     
            break; 
            }   
    
    case "deleteTaxMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fglData.tax_mstr x = objectMapper.readValue(sb.toString(), fglData.tax_mstr.class);            
            response.getWriter().print(arrayToJson(deleteTaxMstr(x)));
            break;
          }
    
    case "getTaxMstr" :  {      
            fglData.tax_mstr tx = getTaxMstr(new String[]{request.getHeader("param1")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(tx);
            response.getWriter().print(r);
            break;  
        }
    
    case "getTaxDet" : {
      String param1 = request.getHeader("param1"); 
      ArrayList<fglData.taxd_mstr> emlist = getTaxDet(param1);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(emlist);
      response.getWriter().print(r);
      break;
    }    
    
    case "getTaxLines" : {
            response.getWriter().print(ArrayListStringToJson(fglData.getTaxLines(request.getHeader("param1"))));  
            break;
    }
   
    case "addPayProfileTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            fglData.pay_profdet[] sdarray = om.readValue(ca[0], fglData.pay_profdet[].class);
            ArrayList<fglData.pay_profdet> txd = (sdarray == null) ? null :new ArrayList<fglData.pay_profdet>(Arrays.asList(sdarray)); 
            fglData.pay_profile tx = om.readValue(ca[1], fglData.pay_profile.class);
            response.getWriter().print(arrayToJson(fglData.addPayProfileTransaction(txd, tx)));     
            break; 
            }   
    
    case "updatePayProfileTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String key = ca[0];
            ArrayList<String> badlist = om.readValue(ca[1], ArrayList.class);
            fglData.pay_profdet[] sdarray = om.readValue(ca[0], fglData.pay_profdet[].class);
            ArrayList<fglData.pay_profdet> txd = (sdarray == null) ? null :new ArrayList<fglData.pay_profdet>(Arrays.asList(sdarray)); 
            fglData.pay_profile tx = om.readValue(ca[1], fglData.pay_profile.class);
            response.getWriter().print(arrayToJson(fglData.updatePayProfileTransaction(key, badlist, txd, tx)));     
            break; 
            }   
    
    case "deletePayProfile" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fglData.pay_profile x = objectMapper.readValue(sb.toString(), fglData.pay_profile.class);            
            response.getWriter().print(arrayToJson(deletePayProfile(x)));
            break;
          }
    
    case "getPayProfile" :  {      
            fglData.pay_profile tx = getPayProfile(new String[]{request.getHeader("param1")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(tx);
            response.getWriter().print(r);
            break;  
        }
    
    case "getPayProfileDet" : {
      String param1 = request.getHeader("param1"); 
      ArrayList<fglData.pay_profdet> emlist = getPayProfileDet(param1);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(emlist);
      response.getWriter().print(r);
      break;
    }    
    
    case "getPayProfileLines" : {
            response.getWriter().print(ArrayListStringToJson(fglData.getPayProfileLines(request.getHeader("param1"))));  
            break;
    }
        
    
    case "getExpenseBrowseView" : { 
      response.getWriter().print(fglData.getExpenseBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
      break;
    } 
    
    case "getPayRollBrowseView" : { 
      response.getWriter().print(fglData.getPayRollBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")})); 
      break;
    } 
    
    case "getPayRollBrowseDetView" : { 
      response.getWriter().print(fglData.getPayRollBrowseDetView(request.getHeader("param1"), 
                    request.getHeader("param2"))); 
      break;
    } 
    
    case "getEarningsView" : { 
      response.getWriter().print(fglData.getEarningsView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
      break;
    }  
    
    case "getDeductionsView" : { 
      response.getWriter().print(fglData.getDeductionsView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
      break;
    }  
    
    
    case "get_pie_EmpPayByDate" : { 
      response.getWriter().print(ArrayListStringArrayToJson(get_pie_EmpPayByDate(request.getHeader("param1"), 
                    request.getHeader("param2"))));
      break;
    }
    
    case "get_pie_EmpTypePayByDate" : { 
      response.getWriter().print(ArrayListStringArrayToJson(get_pie_EmpTypePayByDate(request.getHeader("param1"), 
                    request.getHeader("param2"))));
      break;
    }
    
    case "getGlTranBrowseView" : { 
      response.getWriter().print(fglData.getGlTranBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6")})); 
      break;
    } 
    
    case "getInvoiceBrowseView" : { 
      response.getWriter().print(fglData.getInvoiceBrowseView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6") ));
      break;
    } 
    
    case "getAccountBalanceView" : { 
      response.getWriter().print(fglData.getAccountBalanceView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7"),
                    request.getHeader("param8")} ));
      break;
    } 
    
    case "getTrialBalanceView" : { 
      response.getWriter().print(fglData.getTrialBalanceView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3")})); 
      break;
    } 
    
    case "getAccountBalanceDetView" : { 
      response.getWriter().print(fglData.getAccountBalanceDetView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    bsParseInt(request.getHeader("param4")),
                    bsParseInt(request.getHeader("param5"))));
      break;
    } 
    
    case "getAccountActivityYearView" : { 
      response.getWriter().print(fglData.getAccountActivityYearView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")} ));
      break;
    } 
    
    case "getReconAcctBrowseView" : { 
      response.getWriter().print(fglData.getReconAcctBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")} ));
      break;
    } 
    
    
    case "getInvoiceBrowseDetail" : { 
      response.getWriter().print(fglData.getInvoiceBrowseDetail(request.getHeader("param1"))); 
      break;
    }     
    
    case "getFINInit" : {
      response.getWriter().print(ArrayListStringArrayToJson(getFINInit(request.getHeader("param1"), request.getHeader("param2"))));
      break;
    }  
    
    case "getGLAcctListRangeWCurrTypeDesc" : {
      response.getWriter().print(ArrayListStringArrayToJson(getGLAcctListRangeWCurrTypeDesc(request.getHeader("param1"), request.getHeader("param2"))));
      break;
    } 
    
    case "PostGL" : { 
      fglData.PostGL();
      response.getWriter().print(arrayToJson(new String[]{BlueSeerUtils.SuccessBit, getMessageTag(1125)}));
      break;
    }
    
    case "getGLCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fglData.gl_ctrl x = getGLCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getGLCal" : { 
            String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
            fglData.gl_cal x = getGLCal(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getPAYCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fglData.pay_ctrl x = getPAYCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    
    case "getARCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            farData.ar_ctrl x = getARCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getAPCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fapData.ap_ctrl x = getAPCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "addUpdateAPCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fapData.ap_ctrl x = objectMapper.readValue(sb.toString(), fapData.ap_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateAPCtrl(x)));
            break;
          }
    
    case "addUpdateARCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            farData.ar_ctrl x = objectMapper.readValue(sb.toString(), farData.ar_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateARCtrl(x)));
            break;
          }
    
    case "getGLCalForDate" :
            response.getWriter().print(arrayToJson(fglData.getGLCalForDate(BlueSeerUtils.parseDate(request.getHeader("param1")))));   
            break;
            
    case "getGLAcctDescType" : 
            response.getWriter().print(arrayToJson(fglData.getGLAcctDescType(request.getHeader("param1"))));   
            break; 
            
    case "deleteGLIC" : 
            response.getWriter().print(arrayToJson(fglData.deleteGLIC(request.getHeader("param1"))));   
            break;        
            
    case "getGLAcctDesc" : 
            response.getWriter().print(fglData.getGLAcctDesc(request.getHeader("param1")));   
            break;    
            
   case "getGLCCDesc" : 
            response.getWriter().print(fglData.getGLCCDesc(request.getHeader("param1")));   
            break;             
            
    case "getGLCalForPeriod" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalForPeriod(bsParseInt(request.getHeader("param1")), bsParseInt(request.getHeader("param2")))));
      break;
    }         
    
    case "getGLCalForPeriodRange" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalForPeriodRange(bsParseInt(request.getHeader("param1")), bsParseInt(request.getHeader("param2")),
              bsParseInt(request.getHeader("param3")))));
      break;
    }  
    
    case "getGLBalByYearByPeriod" : {
      response.getWriter().print(ArrayListStringToJson(getGLBalByYearByPeriod(bsParseInt(request.getHeader("param1")), 
              bsParseInt(request.getHeader("param2")),
              bsParseInt(request.getHeader("param3")),
              bsParseInt(request.getHeader("param4")),
              request.getHeader("param5"),
              BlueSeerUtils.ConvertStringToBool(request.getHeader("param6")),
              BlueSeerUtils.ConvertStringToBool(request.getHeader("param7")))));
      break;
    }  
    
    case "getGLCalYearsRange" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalYearsRange()));
      break;
    } 
    
    case "getFglRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getFglRptPickerData(x));  
        break;
        }
    
    case "updateReconGLRecord" : {
        String line;
        StringBuilder sb = new StringBuilder();  
        BufferedReader reader = request.getReader();  // as string
        while ((line = reader.readLine()) != null) {  
        sb.append(line);
        } 
        reader.close();
        ObjectMapper om = new ObjectMapper();
        ArrayList<String> al = om.readValue(sb.toString(), new TypeReference<ArrayList<String>>() {});
        response.getWriter().print(boolToJson(updateReconGLRecord(al))); 
        break;
    }
    
    case "addPayRoll" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String[]> al = om.readValue(sb.toString(), new TypeReference<ArrayList<String[]>>() {});
            String[] arr = request.getHeader("param1").split("," , -1);
            response.getWriter().print(arrayToJson(fglData.addPayRoll(al, arr)));    
            break; 
        }
    
    default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServFIN for id: " + id);    
            
    }   
       
    } // doPost
     
    
    private String getHeaders(HttpServletRequest request) {
    
    StringBuilder requestHeaders = new StringBuilder();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String hd = headerNames.nextElement();
                requestHeaders.append("Header  " + hd).append("  Value  " + request.getHeader(hd)).append("\n");
            }
    return requestHeaders.toString();
}


}
