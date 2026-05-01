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


import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.addExpMstr;
import static com.blueseer.fap.fapData.deleteExpMstr;
import static com.blueseer.fap.fapData.getAPExpenseByAcct;
import static com.blueseer.fap.fapData.getAPExpenseByVendor;
import static com.blueseer.fap.fapData.getAPVoucherSet;
import static com.blueseer.fap.fapData.getCashTranChartBuySell;
import static com.blueseer.fap.fapData.getCashTranChartExpense;
import static com.blueseer.fap.fapData.getCashTranInvAssetTotal;
import static com.blueseer.fap.fapData.getExpMstr;
import static com.blueseer.fap.fapData.getFapRptPickerData;
import static com.blueseer.fap.fapData.getPOsummaryChargesTaxes;
import static com.blueseer.fap.fapData.getRecurringExpenseHistory;
import static com.blueseer.fap.fapData.getRecurringExpenseRecords;
import static com.blueseer.fap.fapData.getRecurringIncomeTotal;
import static com.blueseer.fap.fapData.getVoucherBrowseView;
import static com.blueseer.fap.fapData.updateAPVoucherStatus;
import static com.blueseer.fap.fapData.updateExpActive;
import static com.blueseer.fap.fapData.updateExpMstr;
import static com.blueseer.fap.fapData.updateRecurExp_Income;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServFAP extends HttpServlet {
    
        
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
    }

 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
       
    
    response.setContentType("text/plain");
    
    if (! confirmServerAuthAPI(request, authServ.hmuser)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(" br549 authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id"); 
    
    switch (id) {
        
        case "cashBuy" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> details = om.readValue(ca[0], ArrayList.class);
            String[] headers = om.readValue(ca[1], String[].class); 
            response.getWriter().print(arrayToJson(fapData.cashBuy(details, headers))); 
            break;
            }
        
        case "cashSell" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> details = om.readValue(ca[0], ArrayList.class);
            String[] headers = om.readValue(ca[1], String[].class); 
            response.getWriter().print(arrayToJson(fapData.cashSell(details, headers))); 
            break;
            }
        
        case "cashExpense" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> details = om.readValue(ca[0], ArrayList.class);
            String[] headers = om.readValue(ca[1], String[].class); 
            response.getWriter().print(arrayToJson(fapData.cashExpense(details, headers))); 
            break;
            }
        
        case "cashIncome" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> details = om.readValue(ca[0], ArrayList.class);
            String[] headers = om.readValue(ca[1], String[].class); 
            response.getWriter().print(arrayToJson(fapData.cashIncome(details, headers))); 
            break;
            }
        
        case "cashExpenseRecurring" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> details = om.readValue(ca[0], ArrayList.class);
            String[] headers = om.readValue(ca[1], String[].class); 
            response.getWriter().print(arrayToJson(fapData.cashExpenseRecurring(details, headers))); 
            break;
            }
        
        case "addExpMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fapData.exp_mstr am = objectMapper.readValue(sb.toString(), fapData.exp_mstr.class);            
            response.getWriter().print(arrayToJson(addExpMstr(am)));
            break;
          }
    
        case "updateExpMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fapData.exp_mstr am = objectMapper.readValue(sb.toString(), fapData.exp_mstr.class);            
            response.getWriter().print(arrayToJson(updateExpMstr(am)));
            break;
          }
    
        case "deleteExpMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fapData.exp_mstr am = objectMapper.readValue(sb.toString(), fapData.exp_mstr.class);            
            response.getWriter().print(arrayToJson(deleteExpMstr(am)));
            break;
          }
    
    
        case "getExpMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fapData.exp_mstr x = getExpMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "VoucherTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String ctype = ca[0];
            fapData.vod_mstr[] sdarray = om.readValue(ca[1], fapData.vod_mstr[].class);
            ArrayList<fapData.vod_mstr> vodlist = (sdarray == null) ? null : new ArrayList<fapData.vod_mstr>(Arrays.asList(sdarray)); 
            fapData.ap_mstr ap = om.readValue(ca[2], fapData.ap_mstr.class); 
            Boolean isvoid = Boolean.valueOf(ca[3]);
            response.getWriter().print(arrayToJson(fapData.VoucherTransaction(ctype, vodlist, ap, isvoid))); 
            break;
            }
        
        case "VouchAndPayTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String ctype = ca[0];
            fapData.vod_mstr[] sdarray = om.readValue(ca[1], fapData.vod_mstr[].class);
            ArrayList<fapData.vod_mstr> vodlist = (sdarray == null) ? null : new ArrayList<fapData.vod_mstr>(Arrays.asList(sdarray)); 
            fapData.ap_mstr ap = om.readValue(ca[2], fapData.ap_mstr.class); 
            Boolean isvoid = Boolean.valueOf(ca[3]);
            response.getWriter().print(arrayToJson(fapData.VouchAndPayTransaction(ctype, vodlist, ap, isvoid))); 
            break;
            }
        
        case "getVoucherBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4"), 
               request.getHeader("param5")
               };     
        response.getWriter().print(getVoucherBrowseView(it));  
        break;
        } 
       
        case "getAPVoucherSet" : {       
            fapData.VoucherAP shset = getAPVoucherSet(new String[]{request.getHeader("param1")});
            ObjectMapper om_shset = new ObjectMapper(); 
            String r = om_shset.writeValueAsString(shset);
            response.getWriter().print(r);
            break; 
        }
         
        case "getAPExpenseByVendor" : {
            response.getWriter().print(ArrayListStringArrayToJson(getAPExpenseByVendor(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"))));
            break;
        }
        
        case "getAPExpenseByAcct" : {
            response.getWriter().print(ArrayListStringArrayToJson(getAPExpenseByAcct(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"))));
            break;
        }
        
        case "getPOsummaryChargesTaxes" : {
            response.getWriter().print(arrayToJson(getPOsummaryChargesTaxes(request.getHeader("param1"))));   
            break;
        }
        
        case "updateAPVoucherStatus" : { 
            updateAPVoucherStatus(request.getHeader("param1"), request.getHeader("param2"));
            break;  
        }
        
        case "updateRecurExp_Income" : { 
            response.getWriter().print(updateRecurExp_Income(request.getHeader("param1"), request.getHeader("param2")));
            break;  
        }
        
        case "updateExpActive" : { 
            response.getWriter().print(updateExpActive(request.getHeader("param1"), request.getHeader("param2")));
            break;  
        }
        
        case "getRecurringIncomeTotal" : { 
            response.getWriter().print(doubleToJson(getRecurringIncomeTotal(request.getHeader("param1"))));
            break;  
        }
        
        case "getCashTranInvAssetTotal" : { 
            response.getWriter().print(doubleToJson(getCashTranInvAssetTotal()));
            break;  
        }
        
        
        case "getRecurringExpenseHistory" : {
            response.getWriter().print(ArrayListStringArrayToJson(getRecurringExpenseHistory(request.getHeader("param1"))));
            break;
        }
        
        case "getRecurringExpenseRecords" : {
            response.getWriter().print(ArrayListStringArrayToJson(getRecurringExpenseRecords(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
        
        case "getFapRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getFapRptPickerData(x));  
        break;
        }
        
        case "getCashTranBrowseView" : { 
      response.getWriter().print(fapData.getCashTranBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3")})); 
      break;
    } 
        
        case "getCashTranBrowseViewDet" : { 
      response.getWriter().print(fapData.getCashTranBrowseViewDet(request.getHeader("param1"))); 
      break; 
    }  
        
        case "getCashTranChartExpense" : {
            response.getWriter().print(ArrayListStringArrayToJson(getCashTranChartExpense(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"))));
            break;
        }
        
        case "getCashTranChartBuySell" : {
            response.getWriter().print(ArrayListStringArrayToJson(getCashTranChartBuySell(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"))));
            break;
        }
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServFAP for id: " + id);    
            
    }   
    
       
    }
   
    
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
