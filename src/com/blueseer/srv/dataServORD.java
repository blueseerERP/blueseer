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


import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.addUpdateORCtrl;
import static com.blueseer.ord.ordData.addUpdateSOMeta;
import static com.blueseer.ord.ordData.applyOrderChange;
import static com.blueseer.ord.ordData.billTransAll;
import static com.blueseer.ord.ordData.deleteSOMeta;
import static com.blueseer.ord.ordData.getBillBrowseView;
import static com.blueseer.ord.ordData.getBillDet;
import static com.blueseer.ord.ordData.getBillMstr;
import static com.blueseer.ord.ordData.getBillSAC;
import static com.blueseer.ord.ordData.getORCtrl;
import static com.blueseer.ord.ordData.getOrdRptPickerData;
import static com.blueseer.ord.ordData.getOrderBrowseView;
import static com.blueseer.ord.ordData.getOrderChangeBrowseDetail;
import static com.blueseer.ord.ordData.getOrderChangeBrowseView;
import static com.blueseer.ord.ordData.getOrderChangeExport;
import static com.blueseer.ord.ordData.getOrderChangeReportData;
import static com.blueseer.ord.ordData.getOrderDet;
import static com.blueseer.ord.ordData.getOrderDetailExport;
import static com.blueseer.ord.ordData.getOrderDetailExportNew;
import static com.blueseer.ord.ordData.getOrderItemBrowseView;
import static com.blueseer.ord.ordData.getOrderMstr;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import static com.blueseer.ord.ordData.getOrderReportData;
import static com.blueseer.ord.ordData.getOrderSourceBrowseView;
import static com.blueseer.ord.ordData.getOrderSourceBrowseViewDet;
import static com.blueseer.ord.ordData.getQuoteBrowseView;
import static com.blueseer.ord.ordData.getQuoteDet;
import static com.blueseer.ord.ordData.getQuoteMstr;
import static com.blueseer.ord.ordData.getQuoteSAC;
import static com.blueseer.ord.ordData.getSOMetaData;
import static com.blueseer.ord.ordData.getSVOrderTotalTax;
import static com.blueseer.ord.ordData.getServiceOrderBrowseView;
import static com.blueseer.ord.ordData.getServiceOrderDet;
import static com.blueseer.ord.ordData.getServiceOrderMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
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
public class dataServORD extends HttpServlet {
    
        
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
        case "addOrderTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ordData.sod_det[] sdarray = om.readValue(ca[0], ordData.sod_det[].class);
            ArrayList<ordData.sod_det> sdlist = (sdarray == null) ? null : new ArrayList<ordData.sod_det>(Arrays.asList(sdarray)); 
            ordData.so_mstr sm = om.readValue(ca[1], ordData.so_mstr.class); 
            ordData.so_tax[] starray = om.readValue(ca[2], ordData.so_tax[].class);
            ArrayList<ordData.so_tax> stlist = (starray == null) ? null : new ArrayList<ordData.so_tax>(Arrays.asList(starray));
            ordData.sod_tax[] sodtarray = om.readValue(ca[3], ordData.sod_tax[].class);
            ArrayList<ordData.sod_tax> sodtlist = (sodtarray == null) ? null : new ArrayList<ordData.sod_tax>(Arrays.asList(sodtarray));  
            ordData.sos_det[] sosdarray = om.readValue(ca[4], ordData.sos_det[].class);
            ArrayList<ordData.sos_det> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.sos_det>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.addOrderTransaction(sdlist, sm, stlist, sodtlist, sosdlist))); 
            break;
            }
             
            
        case "updateOrderTransaction" : {
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
            ordData.sod_det[] sdarray = om.readValue(ca[2], ordData.sod_det[].class);
            ArrayList<ordData.sod_det> sdlist = new ArrayList<ordData.sod_det>(Arrays.asList(sdarray)); 
            ordData.so_mstr sm = om.readValue(ca[3], ordData.so_mstr.class); 
            ordData.so_tax[] starray = om.readValue(ca[4], ordData.so_tax[].class);
            ArrayList<ordData.so_tax> stlist = (starray == null) ? null : new ArrayList<ordData.so_tax>(Arrays.asList(starray));
            ordData.sod_tax[] sodtarray = om.readValue(ca[5], ordData.sod_tax[].class);
            ArrayList<ordData.sod_tax> sodtlist = (sodtarray == null) ? null : new ArrayList<ordData.sod_tax>(Arrays.asList(sodtarray));  
            ordData.sos_det[] sosdarray = om.readValue(ca[6], ordData.sos_det[].class);
            ArrayList<ordData.sos_det> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.sos_det>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.updateOrderTransaction(key, badlist, sdlist, sm, stlist, sodtlist, sosdlist))); 
            }
            break;  
          
        case "addBillingTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ordData.bill_det[] sdarray = om.readValue(ca[0], ordData.bill_det[].class);
            ArrayList<ordData.bill_det> sdlist = new ArrayList<ordData.bill_det>(Arrays.asList(sdarray)); 
            ordData.bill_mstr sm = om.readValue(ca[1], ordData.bill_mstr.class); 
            ordData.bill_sac[] sosdarray = om.readValue(ca[2], ordData.bill_sac[].class);
            ArrayList<ordData.bill_sac> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.bill_sac>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.addBillingTransaction(sdlist, sm, sosdlist))); 
            break;
            }
        
        case "updateBillingTransaction" : {
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
            ordData.bill_det[] detarray = om.readValue(ca[2], ordData.bill_det[].class);
            ArrayList<ordData.bill_det> detlist = new ArrayList<ordData.bill_det>(Arrays.asList(detarray)); 
            ordData.bill_mstr x = om.readValue(ca[3], ordData.bill_mstr.class); 
            ordData.bill_sac[] sosdarray = om.readValue(ca[4], ordData.bill_sac[].class);
            ArrayList<ordData.bill_sac> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.bill_sac>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.updateBillingTransaction(key, badlist, detlist, x, sosdlist))); 
        break;    
        }
         
         case "addQuoteTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ordData.quo_det[] sdarray = om.readValue(ca[0], ordData.quo_det[].class);
            ArrayList<ordData.quo_det> sdlist = new ArrayList<ordData.quo_det>(Arrays.asList(sdarray)); 
            ordData.quo_mstr sm = om.readValue(ca[1], ordData.quo_mstr.class); 
            ordData.quo_sac[] sosdarray = om.readValue(ca[2], ordData.quo_sac[].class);
            ArrayList<ordData.quo_sac> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.quo_sac>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.addQuoteTransaction(sdlist, sm, sosdlist))); 
            break;
            }
        
        case "updateQuoteTransaction" : {
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
            ordData.quo_det[] detarray = om.readValue(ca[2], ordData.quo_det[].class);
            ArrayList<ordData.quo_det> detlist = new ArrayList<ordData.quo_det>(Arrays.asList(detarray)); 
            ordData.quo_mstr x = om.readValue(ca[3], ordData.quo_mstr.class); 
            ordData.quo_sac[] sosdarray = om.readValue(ca[4], ordData.quo_sac[].class);
            ArrayList<ordData.quo_sac> sosdlist = (sosdarray == null) ? null : new ArrayList<ordData.quo_sac>(Arrays.asList(sosdarray));
            response.getWriter().print(arrayToJson(ordData.updateQuoteTransaction(key, badlist, detlist, x, sosdlist)));  
        break;    
        }
         
            
        case "deleteOrderMstr" :
            response.getWriter().print(arrayToJson(ordData.deleteOrderMstr(request.getHeader("param1")
                    )));  
            break; 
            
        case "deleteBillMstr" :
            response.getWriter().print(arrayToJson(ordData.deleteBillMstr(request.getHeader("param1")
                    )));  
            break;
            
        case "deleteQuoteMstr" :
            response.getWriter().print(arrayToJson(ordData.deleteQuoteMstr(request.getHeader("param1")
                    )));  
            break;    
        
        case "deleteServiceOrderMstr" :
            response.getWriter().print(arrayToJson(ordData.deleteServiceOrderMstr(request.getHeader("param1")
                    )));  
            break;     
        
        
        case "addServiceOrderTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ordData.svd_det[] sdarray = om.readValue(ca[0], ordData.svd_det[].class);
            ArrayList<ordData.svd_det> svlist = new ArrayList<ordData.svd_det>(Arrays.asList(sdarray));  
            ordData.sv_mstr sv = om.readValue(ca[1], ordData.sv_mstr.class); 
            ordData.sos_det[] sosdarray = om.readValue(ca[2], ordData.sos_det[].class);
            ArrayList<ordData.sos_det> sosdlist = new ArrayList<ordData.sos_det>(Arrays.asList(sosdarray)); 
            response.getWriter().print(arrayToJson(ordData.addServiceOrderTransaction(svlist, sv, sosdlist))); 
            break;
            }
         
        case "updateServiceOrderTransaction" : {
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
            ordData.svd_det[] sdarray = om.readValue(ca[2], ordData.svd_det[].class);
            ArrayList<ordData.svd_det> svlist = new ArrayList<ordData.svd_det>(Arrays.asList(sdarray));   
            ordData.sv_mstr sv = om.readValue(ca[3], ordData.sv_mstr.class); 
            ordData.sos_det[] sosdarray = om.readValue(ca[4], ordData.sos_det[].class);
            ArrayList<ordData.sos_det> sosdlist = new ArrayList<ordData.sos_det>(Arrays.asList(sosdarray)); 
            response.getWriter().print(arrayToJson(ordData.updateServiceOrderTransaction(key, badlist, svlist, sv, sosdlist))); 
            }
            break;  
            
        case "getOrderBrowseInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getOrderBrowseInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
            
        case "getSalesOrderInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getSalesOrderInit(request.getHeader("param1"), request.getHeader("param2"))));
            break; 
            
        case "getServiceOrderInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getServiceOrderInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
            
        case "getBillingInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getBillingInit(request.getHeader("param1"), request.getHeader("param2"))));
            break; 
            
        case "getQuoteInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getQuoteInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;     
            
        case "exportOrderDetail" : 
        response.getWriter().print(getOrderDetailExportNew(request.getHeader("fromdate"), 
                request.getHeader("todate"), 
                request.getHeader("fromcust"), 
                request.getHeader("tocust"), 
                request.getHeader("site"))); 
        break;

        case "exportOrderChange" :
        response.getWriter().println(getOrderChangeExport(request.getHeader("fromdate"), 
                request.getHeader("todate"), 
                request.getHeader("fromcust"), 
                request.getHeader("tocust"), 
                request.getHeader("site"))); 
        break;

        case "getOrderBrowseView" : {
        String[] x = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("site"), 
               request.getHeader("datetype"),
               request.getHeader("po")
               };     
        response.getWriter().print(getOrderBrowseView(x));  
        break;
        }
        
        case "getServiceOrderBrowseView" : {
        String[] x = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("site")
               };     
        response.getWriter().print(getServiceOrderBrowseView(x));  
        break;
        }
        
        case "getOrderItemBrowseView" : {
        String[] x = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("fromnbr"), 
               request.getHeader("tonbr"),
               request.getHeader("fromitem"), 
               request.getHeader("toitem"),
               request.getHeader("site")
               };     
        response.getWriter().print(getOrderItemBrowseView(x));  
        break;
        }
        
        case "getQuoteBrowseView" : {
        String[] x = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("fromnbr"), 
               request.getHeader("tonbr"),
               request.getHeader("active"), 
               request.getHeader("site")
               };     
        response.getWriter().print(getQuoteBrowseView(x));  
        break;
        }
        
        case "getBillBrowseView" : {
        String[] x = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("fromnbr"), 
               request.getHeader("tonbr"),
               request.getHeader("active"), 
               request.getHeader("site")
               };     
        response.getWriter().print(getBillBrowseView(x));  
        break;
        }
        
        case "getOrderSourceBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4")
               };     
        response.getWriter().print(getOrderSourceBrowseView(x));  
        break;
        }
        
        case "getOrderSourceBrowseViewDet" : {
        response.getWriter().print(getOrderSourceBrowseViewDet(request.getHeader("param1")));  
        break;
        }
        
        case "validateOrderDetail" :
            response.getWriter().print(arrayToJson(ordData.validateOrderDetail(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7")
                    )));  
            break;
            
        case "orderToInvoice" :
            response.getWriter().print(arrayToJson(ordData.orderToInvoice(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")
                    )));  
            break;    
                        
        case "getOrderBrowseDetail" :
            response.getWriter().print(ordData.getOrderBrowseDetail(request.getHeader("param1")));  
            break;
           
       
        case "getBillDet" : {       
        ArrayList<ordData.bill_det> xd = getBillDet(request.getHeader("param1"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
        
        case "getORCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            ordData.order_ctrl x = getORCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
            
        case "getBillSAC" : {       
        ArrayList<ordData.bill_sac> xd = getBillSAC(request.getHeader("param1"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        }
            
        case "getServiceOrderBrowseDetail" :
            response.getWriter().print(ordData.getServiceOrderBrowseDetail(request.getHeader("param1")));  
            break;    
        
        case "getQuoteBrowseDetail" :
            response.getWriter().print(ordData.getQuoteBrowseDetail(request.getHeader("param1")));  
            break;
            
        case "getBillBrowseDetail" :
            response.getWriter().print(ordData.getBillBrowseDetail(request.getHeader("param1"), request.getHeader("param2")));  
            break;    
            
        case "getOrderChangeBrowseDetail" :
            response.getWriter().print(ordData.getOrderChangeBrowseDetail(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")));  
            break;    
            
        case "getOrderLines" :
            response.getWriter().print(ArrayListStringToJson(ordData.getOrderLines(request.getHeader("param1"))));  
            break;
            
        case "getBillLines" :
            response.getWriter().print(ArrayListStringToJson(ordData.getBillLines(request.getHeader("param1"))));  
            break; 
            
        case "getQuoteLines" :
            response.getWriter().print(ArrayListStringToJson(ordData.getQuoteLines(request.getHeader("param1"))));  
            break;    
            
        case "getSOMetaNotes" :
            response.getWriter().print(ArrayListStringToJson(ordData.getSOMetaNotes(request.getHeader("param1"), request.getHeader("param2"))));  
            break;    
            
        case "getOrderLineInfo" :
            response.getWriter().print(arrayToJson(ordData.getOrderLineInfo(request.getHeader("param1"),
                    request.getHeader("param2")
                    )));  
            break;    
        
        case "getServiceOrderLines" :
            response.getWriter().print(ArrayListStringToJson(ordData.getServiceOrderLines(request.getHeader("param1"))));  
            break;    

        case "getOrderChangeBrowseView" : {
        String[] ocr = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("site"), 
               request.getHeader("posearch"),
               request.getHeader("isdetached")               
               };     
        response.getWriter().print(getOrderChangeBrowseView(ocr));
        }
        break;

        case "getOrderMstrSet" : {       
        ordData.salesOrder cs = getOrderMstrSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(cs);
        response.getWriter().print(r);
        }
        break; 
        
        case "getOrderMstr" : {       
        ordData.so_mstr so = getOrderMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(so);
        response.getWriter().print(r);
        break;
        }
                
        case "getOrderDetline" : {       
        ordData.sod_det sd = getOrderDet(request.getHeader("param1"), request.getHeader("param2"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(sd);
        response.getWriter().print(rsd);
        break;
        }
        
        case "getOrderDet" : { 
            ArrayList<ordData.sod_det> x = getOrderDet(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
       
        
        case "getQuoteMstr" : {       
        ordData.quo_mstr x = getQuoteMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        break;
        }
                
        case "getQuoteDet" : {       
        ArrayList<ordData.quo_det> xd = getQuoteDet(request.getHeader("param1"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        }
        
        case "getQuoteSAC" : {       
        ArrayList<ordData.quo_sac> xd = getQuoteSAC(request.getHeader("param1"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        }
        
        case "getBillMstr" : {       
        ordData.bill_mstr x = getBillMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        }
        break;
        
        case "getServiceOrderMstr" : {       
        ordData.sv_mstr sv = getServiceOrderMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(sv);
        response.getWriter().print(r);
        }
        break;
        
        case "getServiceOrderDet" : { 
            ArrayList<ordData.svd_det> x = getServiceOrderDet(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getSVOrderTotalTax" : {
            response.getWriter().print(doubleToJson(getSVOrderTotalTax(request.getHeader("param1"))));  
            break;
        }
        
        case "billTransAll" :        
            response.getWriter().print(billTransAll());
            break; 
        
        case "applyOrderChange" :
            ordData.applyOrderChange(request.getHeader("param1"), request.getHeader("param2"));
            break;

        case "updateOrderChangeStatus" :
            ordData.updateOrderChangeStatus(request.getHeader("param1"), request.getHeader("param2"));
            break;
            
        case "updateOrderStatus" :
            ordData.updateOrderStatus(request.getHeader("param1"), request.getHeader("param2"));
            break;  
            
        case "updateServiceOrderType" :
            ordData.updateServiceOrderType(request.getHeader("param1"), request.getHeader("param2"));
            break;    
            
        case "updateOrderStatusByPO" :
            ordData.updateOrderStatusByPO(request.getHeader("param1"), request.getHeader("param2"));
            break;  
            
        case "addUpdateSOMetaNotes" :
            String[] notelines = request.getHeader("param3").split("=_=", -1);
            ordData.addUpdateSOMetaNotes(request.getHeader("param1"), request.getHeader("param2"), notelines);
            break;    
            
        case "addUpdateSOMeta" : 
        response.getWriter().println(boolToJson(addUpdateSOMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break; 
        
        case "deleteSOMeta" : 
        response.getWriter().println(boolToJson(deleteSOMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        
        case "getOrderPrintData" :
            response.getWriter().print(ordData.getOrderPrintData(request.getHeader("param1")));    
            break;
            
        case "getPickPrintData" :
            response.getWriter().print(ordData.getPickPrintData(request.getHeader("param1")));    
            break;     
            
        case "getServiceOrderPrintData" :
            response.getWriter().print(ordData.getServiceOrderPrintData(request.getHeader("param1")));    
            break;    
            
        case "getSOMetaData" :        
            response.getWriter().print(ArrayListStringArrayToJson(getSOMetaData(request.getHeader("param1"))));
            break;  
            
        case "getServiceOrderChartData" :
        response.getWriter().print(ArrayListStringArrayToJson(ordData.getServiceOrderChartData(request.getHeader("param1"), request.getHeader("param2"))));
        break;
        
        case "addUpdateORCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ordData.order_ctrl x = objectMapper.readValue(sb.toString(), ordData.order_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateORCtrl(x)));
            break;
          }
        
        case "getOrdRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getOrdRptPickerData(x));  
        break;
        }
        
            
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServORD for id: " + id);    
            
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
