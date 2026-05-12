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

import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.addCMCDet;
import static com.blueseer.ctr.cusData.addCMSDet;
import static com.blueseer.ctr.cusData.addCprMstr;
import static com.blueseer.ctr.cusData.addCupMstr;
import static com.blueseer.ctr.cusData.addCustMstrMass;
import static com.blueseer.ctr.cusData.addOrUpdateCprMstr;
import static com.blueseer.ctr.cusData.addOrUpdateCupMstr;
import static com.blueseer.ctr.cusData.addSlspMstr;
import static com.blueseer.ctr.cusData.addTermsMstr;
import static com.blueseer.ctr.cusData.addUpdateCMCtrl;
import static com.blueseer.ctr.cusData.deleteCMCDet;
import static com.blueseer.ctr.cusData.deleteCMSDet;
import static com.blueseer.ctr.cusData.deleteCprMstr;
import static com.blueseer.ctr.cusData.deleteCupMstr;
import static com.blueseer.ctr.cusData.deleteCustMstr;
import static com.blueseer.ctr.cusData.deleteSlspMstr;
import static com.blueseer.ctr.cusData.deleteTermsMstr;
import static com.blueseer.ctr.cusData.getCMCDet;
import static com.blueseer.ctr.cusData.getCMCtrl;
import static com.blueseer.ctr.cusData.getCMSDet;
import static com.blueseer.ctr.cusData.getCprDiscLists;
import static com.blueseer.ctr.cusData.getCprMstr;
import static com.blueseer.ctr.cusData.getCprPriceLists;
import static com.blueseer.ctr.cusData.getCupMstr;
import static com.blueseer.ctr.cusData.getCusRptPickerData;
import static com.blueseer.ctr.cusData.getCustBrowseView;
import static com.blueseer.ctr.cusData.getCustLabel;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.ctr.cusData.getCustName;
import static com.blueseer.ctr.cusData.getCustPriceBrowseView;
import static com.blueseer.ctr.cusData.getCustShipSet;
import static com.blueseer.ctr.cusData.getCustXrefBrowseView;
import static com.blueseer.ctr.cusData.getDiscountRecsByCust;
import static com.blueseer.ctr.cusData.getSalesRepBrowseView;
import static com.blueseer.ctr.cusData.getShipName;
import static com.blueseer.ctr.cusData.getSlspMstr;
import static com.blueseer.ctr.cusData.getTermsMstr;
import static com.blueseer.ctr.cusData.getcustmstrlist;
import static com.blueseer.ctr.cusData.getcustshipmstrlist;
import static com.blueseer.ctr.cusData.updateCMCDet;
import static com.blueseer.ctr.cusData.updateCMSDet;
import static com.blueseer.ctr.cusData.updateCprMstr;
import static com.blueseer.ctr.cusData.updateCupMstr;
import static com.blueseer.ctr.cusData.updateCustMstr;
import static com.blueseer.ctr.cusData.updateSlspMstr;
import static com.blueseer.ctr.cusData.updateTermsMstr;
import static com.blueseer.inv.invData.getBOMsByItemSite_mg;
import static com.blueseer.inv.invData.getLocationListByWarehouse;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getTableInfo;
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
public class dataServCUS extends HttpServlet {
 
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
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
        
        case "addCustMstrMass" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<String> sdarray = objectMapper.readValue(sb.toString(), ArrayList.class);
            response.getWriter().print(arrayToJson(addCustMstrMass(sdarray, request.getHeader("param1"))));
            break;
          }
        
        case "addCustomerTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            cusData.cm_mstr cm = om.readValue(ca[0], cusData.cm_mstr.class);
            ArrayList<String[]> list = om.readValue(ca[1], ArrayList.class); 
            cusData.cms_det cms = om.readValue(ca[2], cusData.cms_det.class);
            response.getWriter().print(arrayToJson(cusData.addCustomerTransaction(cm, list, cms)));     
            break; 
            }    
        
        case "updateCustMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cm_mstr x = objectMapper.readValue(sb.toString(), cusData.cm_mstr.class);            
            response.getWriter().print(arrayToJson(updateCustMstr(x)));
            break;
          }
        
        case "deleteCustMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cm_mstr x = objectMapper.readValue(sb.toString(), cusData.cm_mstr.class);            
            response.getWriter().print(arrayToJson(deleteCustMstr(x)));
            break;
          }
        
        case "getCustMstr" :  {      
            cusData.cm_mstr cm = getCustMstr(new String[]{request.getHeader("param1")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cm);
            response.getWriter().print(r);
            break;  
        }
        
        case "getcustmstrlist" :   {     
            response.getWriter().print(ArrayListStringToJson(getcustmstrlist()));
            break;
        }
        
        case "getcustshipmstrlist" :   {     
            response.getWriter().print(ArrayListStringToJson(getcustshipmstrlist(request.getHeader("param1"))));
            break;
        }
            
        case "getCustShipSet" :  {      
            cusData.CustShipSet cs = getCustShipSet(new String[]{request.getHeader("param1"), request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cs);
            response.getWriter().print(r);
            break;  
        }
            
        case "getDiscountRecsByCust" :    {    
            response.getWriter().print(ArrayListStringArrayToJson(cusData.getDiscountRecsByCust(request.getHeader("param1"))));
            break;  
        }
        
        case "getCustLabel" :  {       
            response.getWriter().print(getCustLabel(request.getHeader("param1")));
            break;    
        }
        
        case "getCustName" :  {       
            response.getWriter().print(getCustName(request.getHeader("param1")));
            break;    
        }
        
        case "getShipName" :  {       
            response.getWriter().print(getShipName(request.getHeader("param1"), request.getHeader("param2")));
            break;    
        }
        
        
        case "getCustMaintInit" : {
            response.getWriter().print(ArrayListStringArrayToJson(cusData.getCustMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
        
        case "getCMCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            cusData.cm_ctrl x = getCMCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addCMSDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cms_det x = objectMapper.readValue(sb.toString(), cusData.cms_det.class);            
            response.getWriter().print(arrayToJson(addCMSDet(x)));
            break;
          }
        
        case "updateCMSDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cms_det x = objectMapper.readValue(sb.toString(), cusData.cms_det.class);            
            response.getWriter().print(arrayToJson(updateCMSDet(x)));
            break;
          }
        
        case "deleteCMSDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cms_det x = objectMapper.readValue(sb.toString(), cusData.cms_det.class);            
            response.getWriter().print(arrayToJson(deleteCMSDet(x)));
            break;
          }
        
        case "getCMSDets" : { 
            ArrayList<cusData.cms_det> x = getCMSDet(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getCMSDet" :  {      
            cusData.cms_det cms = getCMSDet(request.getHeader("param1"), request.getHeader("param2"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cms);
            response.getWriter().print(r);
            break;  
        }
        
        case "deleteCMSDet_x" : { 
            response.getWriter().print(arrayToJson(deleteCMSDet(request.getHeader("param1"), request.getHeader("param2"))));
            break;
          }
        
        case "addCMCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cmc_det x = objectMapper.readValue(sb.toString(), cusData.cmc_det.class);            
            response.getWriter().print(arrayToJson(addCMCDet(x)));
            break;
          }
        
        case "updateCMCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cmc_det x = objectMapper.readValue(sb.toString(), cusData.cmc_det.class);            
            response.getWriter().print(arrayToJson(updateCMCDet(x)));
            break;
          }
        
        case "deleteCMCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cmc_det x = objectMapper.readValue(sb.toString(), cusData.cmc_det.class);            
            response.getWriter().print(arrayToJson(deleteCMCDet(x)));
            break;
          }
        
        case "getCMCDets" : { 
            ArrayList<cusData.cmc_det> x = getCMCDet(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addCprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cpr_mstr x = objectMapper.readValue(sb.toString(), cusData.cpr_mstr.class);            
            response.getWriter().print(arrayToJson(addCprMstr(x)));
            break;
          }
        
        case "updateCprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cpr_mstr x = objectMapper.readValue(sb.toString(), cusData.cpr_mstr.class);            
            response.getWriter().print(arrayToJson(updateCprMstr(x)));
            break;
          }
        
        case "deleteCprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cpr_mstr x = objectMapper.readValue(sb.toString(), cusData.cpr_mstr.class);            
            response.getWriter().print(arrayToJson(deleteCprMstr(x)));
            break;
          }
        
        case "getCprMstr" :  {      
            cusData.cpr_mstr cpr = getCprMstr(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cpr);
            response.getWriter().print(r);
            break;  
        }
        
        case "addCupMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cup_mstr x = objectMapper.readValue(sb.toString(), cusData.cup_mstr.class);            
            response.getWriter().print(arrayToJson(addCupMstr(x)));
            break;
          }
        
        case "addOrUpdateCupMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cup_mstr x = objectMapper.readValue(sb.toString(), cusData.cup_mstr.class);            
            response.getWriter().print(arrayToJson(addOrUpdateCupMstr(x)));
            break;
          }
        
        case "updateCupMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cup_mstr x = objectMapper.readValue(sb.toString(), cusData.cup_mstr.class);            
            response.getWriter().print(arrayToJson(updateCupMstr(x)));
            break;
          }
        
        case "deleteCupMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cup_mstr x = objectMapper.readValue(sb.toString(), cusData.cup_mstr.class);            
            response.getWriter().print(arrayToJson(deleteCupMstr(x)));
            break;
          }
        
        case "getCupMstr" :  {      
            cusData.cup_mstr cup = getCupMstr(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cup);
            response.getWriter().print(r);
            break;  
        }
        
        case "getCprPriceLists" : { 
            ArrayList<cusData.cpr_mstr> x = getCprPriceLists(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getCprDiscLists" : { 
            ArrayList<cusData.cpr_mstr> x = getCprDiscLists(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addUpdateCMCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cm_ctrl x = objectMapper.readValue(sb.toString(), cusData.cm_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateCMCtrl(x)));
            break;
          }
        
        case "addOrUpdateCprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cpr_mstr x = objectMapper.readValue(sb.toString(), cusData.cpr_mstr.class);            
            response.getWriter().print(arrayToJson(addOrUpdateCprMstr(x)));
            break;
          }
        
        case "getCustBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3")
               };     
        response.getWriter().print(getCustBrowseView(x));  
        break;
        }
        
        case "getCustXrefBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getCustXrefBrowseView(x));  
        break;
        }
        
        case "getCustPriceBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getCustPriceBrowseView(x));  
        break;
        }
        
        case "getSalesRepBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5")
               };     
        response.getWriter().print(getSalesRepBrowseView(x));  
        break;
        }
        
        case "getCusRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getCusRptPickerData(x));  
        break;
        }
        
        case "addTermsMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cust_term x = objectMapper.readValue(sb.toString(), cusData.cust_term.class);            
            response.getWriter().print(arrayToJson(addTermsMstr(x)));
            break;
          }
        
        case "updateTermsMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cust_term x = objectMapper.readValue(sb.toString(), cusData.cust_term.class);            
            response.getWriter().print(arrayToJson(updateTermsMstr(x)));
            break;
          }
        
        case "deleteTermsMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cust_term x = objectMapper.readValue(sb.toString(), cusData.cust_term.class);            
            response.getWriter().print(arrayToJson(deleteTermsMstr(x)));
            break;
          }
        
        case "getTermsMstr" :  {      
            cusData.cust_term cpr = getTermsMstr(new String[]{request.getHeader("param1")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cpr);
            response.getWriter().print(r);
            break;  
        }
        
        case "addSlspMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.slsp_mstr x = objectMapper.readValue(sb.toString(), cusData.slsp_mstr.class);            
            response.getWriter().print(arrayToJson(addSlspMstr(x)));
            break;
          }
        
        case "updateSlspMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.slsp_mstr x = objectMapper.readValue(sb.toString(), cusData.slsp_mstr.class);            
            response.getWriter().print(arrayToJson(updateSlspMstr(x)));
            break;
          }
        
        case "deleteSlspMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.slsp_mstr x = objectMapper.readValue(sb.toString(), cusData.slsp_mstr.class);            
            response.getWriter().print(arrayToJson(deleteSlspMstr(x)));
            break;
          }
        
        case "getSlspMstr" :  {      
            cusData.slsp_mstr cup = getSlspMstr(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cup);
            response.getWriter().print(r);
            break;  
        }
        
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServCUS for id: " + id);    
            
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
