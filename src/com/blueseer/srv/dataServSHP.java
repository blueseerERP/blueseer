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


import com.blueseer.adm.admData;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.addUpdateSHCtrl;
import static com.blueseer.shp.shpData.addUpdateShipMeta;
import static com.blueseer.shp.shpData.getSHCtrl;
import static com.blueseer.shp.shpData.getShipMstr;
import static com.blueseer.shp.shpData.getShipperDetBrowseView;
import static com.blueseer.shp.shpData.getShipperItemBrowseView;
import static com.blueseer.shp.shpData.getShipperMstrSet;
import static com.blueseer.shp.shpData.getShpRptPickerData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServSHP extends HttpServlet {
 
    
        
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
        case "getShipperInit" : {
            response.getWriter().print(ArrayListStringArrayToJson(shpData.getShipperInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
            
        case "addShipperTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
           // ArrayList<shpData.ship_det> sd = objectMapper.readValue(ca[0], ArrayList.class);
            shpData.ship_det[] sdarray = objectMapper.readValue(ca[0], shpData.ship_det[].class);
            ArrayList<shpData.ship_det> sdlist = new ArrayList<shpData.ship_det>(Arrays.asList(sdarray)); 
            shpData.ship_mstr sm = objectMapper.readValue(ca[1], shpData.ship_mstr.class); 
           // ArrayList<shpData.ship_tree> st = objectMapper.readValue(ca[2], ArrayList.class); 
            shpData.ship_tree[] starray = objectMapper.readValue(ca[2], shpData.ship_tree[].class);
            ArrayList<shpData.ship_tree> stlist = new ArrayList<shpData.ship_tree>(Arrays.asList(starray));
            response.getWriter().print(arrayToJson(shpData.addShipperTransaction(sdlist, sm, stlist)));  
            break;
        }
            
        case "updateShipTransaction" : {
            String line_ast;
            StringBuilder sb_ast = new StringBuilder();  
            BufferedReader reader_ast = request.getReader();  // as string
            while ((line_ast = reader_ast.readLine()) != null) {  
            sb_ast.append(line_ast);
            } 
            reader_ast.close();
            ObjectMapper om_ast = new ObjectMapper();
            String[] caast = sb_ast.toString().split("=_=", -1);
            ArrayList<String> starrayast = om_ast.readValue(caast[0], ArrayList.class); 
            shpData.ship_det[] sdarrayast = om_ast.readValue(caast[1], shpData.ship_det[].class);
            ArrayList<shpData.ship_det> sdlistast = new ArrayList<shpData.ship_det>(Arrays.asList(sdarrayast)); 
            shpData.ship_mstr smast = om_ast.readValue(caast[2], shpData.ship_mstr.class); 
            response.getWriter().print(arrayToJson(shpData.updateShipTransaction(starrayast, sdlistast, smast)));    
            break; 
        }
            
        case "deleteShipMstr" : {
            response.getWriter().print(arrayToJson(shpData.deleteShipMstr(request.getHeader("param1"))));  
            break;   
        }
        
        case "confirmShipperTransaction" : {
            response.getWriter().print(arrayToJson(shpData.confirmShipperTransaction(request.getHeader("param1"),request.getHeader("param2"),parseDate(request.getHeader("param3")))));  
            break;
        }
            
        case "getShipperSAC" : {
            response.getWriter().print(ArrayListStringArrayToJson(shpData.getShipperSAC(request.getHeader("param1"))));
            break;
        }
            
        case "getShipperMstrSet" : {       
            shpData.Shipper shset = getShipperMstrSet(new String[]{request.getHeader("param1")});
            ObjectMapper om_shset = new ObjectMapper(); 
            String r = om_shset.writeValueAsString(shset);
            response.getWriter().print(r);
            break; 
        }
            
        case "getSHCtrl" :  {      
            shpData.ship_ctrl x = getSHCtrl(new String[]{request.getHeader("param1")});
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
            
        case "addUpdateSHCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            shpData.ship_ctrl x = objectMapper.readValue(sb.toString(), shpData.ship_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateSHCtrl(x)));
            break;
          }
        
        case "getShipMstr" :  {      
            shpData.ship_mstr x = getShipMstr(new String[]{request.getHeader("param1")});
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
        
        case "getShipperBrowseView" : {
            response.getWriter().print(shpData.getShipperBrowseView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7") )); 
            break;
        }
            
        case "getShipperDetBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4"), 
               request.getHeader("param5")
               };     
        response.getWriter().print(getShipperDetBrowseView(it));  
        break;
        }     
        
        case "getShipperItemBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4"), 
               request.getHeader("param5"),
               request.getHeader("param6"),
               request.getHeader("param7"),
               request.getHeader("param8")
               };     
        response.getWriter().print(getShipperItemBrowseView(it));  
        break;
        }     
            
        case "getShipperBrowseDetail" : {
            response.getWriter().print(shpData.getShipperBrowseDetail(request.getHeader("param1")));  
            break; 
        }
            
        case "getShipperLineNumbers" : {
            response.getWriter().print(ArrayListStringToJson(shpData.getShipperLineNumbers(request.getHeader("param1"))));  
            break;    
        }
            
        case "updateShipperSAC" : {
            shpData.updateShipperSAC(request.getHeader("param1"));
            break;   
        }
            
        case "getShipperPrintData" : {
            response.getWriter().print(shpData.getShipperPrintData(request.getHeader("param1"), request.getHeader("param2")));  
            break;
        }
            
        case "getInvoicePrintData" : {
            response.getWriter().print(shpData.getInvoicePrintData(request.getHeader("param1"),
                    request.getHeader("param2")));    
            break; 
        }
            
        case "getShipperHeader" : {
            response.getWriter().print(arrayToJson(shpData.getShipperHeader(request.getHeader("param1"))));  
            break;
        }
            
        case "getShipperLines" : {
            response.getWriter().print(ArrayListStringArrayToJson(shpData.getShipperLines(request.getHeader("param1"))));
            break; 
        }
            
        case "getShpRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getShpRptPickerData(x));  
        break;
        }
         
        case "addUpdateShipMeta" : {
        response.getWriter().println(boolToJson(addUpdateShipMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"),
                request.getHeader("param4")))); 
        break;
        }

            
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServSHP for id: " + id);    
            
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
