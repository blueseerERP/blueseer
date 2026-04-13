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
import static com.blueseer.adm.admData.addSiteMstr;
import static com.blueseer.adm.admData.deleteSiteMstr;
import static com.blueseer.adm.admData.getSiteMstr;
import static com.blueseer.adm.admData.updateSiteMstr;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;

import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.blueseer.vdr.venData;
import static com.blueseer.vdr.venData.addOrUpdateVdpMstr;
import static com.blueseer.vdr.venData.addUpdateVDCtrl;
import static com.blueseer.vdr.venData.addVDCDet;
import static com.blueseer.vdr.venData.addVdpMstr;
import static com.blueseer.vdr.venData.addVendMstr;
import static com.blueseer.vdr.venData.addVendMstrMass;
import static com.blueseer.vdr.venData.addVprMstr;
import static com.blueseer.vdr.venData.deleteVDCDet;
import static com.blueseer.vdr.venData.deleteVdpMstr;
import static com.blueseer.vdr.venData.deleteVendMstr;
import static com.blueseer.vdr.venData.deleteVprMstr;
import static com.blueseer.vdr.venData.getVDCDet;
import static com.blueseer.vdr.venData.getVDCtrl;
import static com.blueseer.vdr.venData.getVdpMstr;
import static com.blueseer.vdr.venData.getVenRptPickerData;
import static com.blueseer.vdr.venData.getVendBrowseView;
import static com.blueseer.vdr.venData.getVendMstr;
import static com.blueseer.vdr.venData.getVendPriceBrowseView;
import static com.blueseer.vdr.venData.getVendShipSet;
import static com.blueseer.vdr.venData.getVendXrefBrowseView;
import static com.blueseer.vdr.venData.getVprMstr;
import static com.blueseer.vdr.venData.getVprPriceLists;
import static com.blueseer.vdr.venData.updateVDCDet;
import static com.blueseer.vdr.venData.updateVdpMstr;
import static com.blueseer.vdr.venData.updateVendMstr;
import static com.blueseer.vdr.venData.updateVprMstr;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServVDR extends HttpServlet {
 
    
        
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
        response.getWriter().println(" br549edipost authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id");
    
    switch (id) {
             
        case "addVendMstrMass" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<String> sdarray = objectMapper.readValue(sb.toString(), ArrayList.class);
            response.getWriter().print(arrayToJson(addVendMstrMass(sdarray, request.getHeader("param1"))));
            break;
        }
        
        case "addVendMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            venData.vd_mstr x = objectMapper.readValue(ca[0], venData.vd_mstr.class);   
            ArrayList<String[]> list = objectMapper.readValue(ca[1], ArrayList.class); 
            response.getWriter().print(arrayToJson(addVendMstr(x, list)));
            break;
          }
           
        case "updateVendMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vd_mstr x = objectMapper.readValue(sb.toString(), venData.vd_mstr.class);            
            response.getWriter().print(arrayToJson(updateVendMstr(x)));
            break;
          }
        
        case "deleteVendMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vd_mstr x = objectMapper.readValue(sb.toString(), venData.vd_mstr.class);            
            response.getWriter().print(arrayToJson(deleteVendMstr(x)));
            break;
          }
        
        case "getVendMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            venData.vd_mstr x = getVendMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getVendBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3")
               };     
        response.getWriter().print(getVendBrowseView(x));  
        break;
        }
        
        case "getVendMaintInit" : {
            response.getWriter().print(ArrayListStringArrayToJson(venData.getVendMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        } 
        
        case "getVendShipSet" :  {      
            venData.VendShipSet vd = getVendShipSet(new String[]{request.getHeader("param1"), request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(vd);
            response.getWriter().print(r);
            break;  
        }
        
        case "addVDCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdc_det x = objectMapper.readValue(sb.toString(), venData.vdc_det.class);            
            response.getWriter().print(arrayToJson(addVDCDet(x)));
            break;
          }
        
        case "updateVDCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdc_det x = objectMapper.readValue(sb.toString(), venData.vdc_det.class);            
            response.getWriter().print(arrayToJson(updateVDCDet(x)));
            break;
          }
        
        case "deleteVDCDet" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdc_det x = objectMapper.readValue(sb.toString(), venData.vdc_det.class);            
            response.getWriter().print(arrayToJson(deleteVDCDet(x)));
            break;
          }
        
        case "getVDCDets" : { 
            ArrayList<venData.vdc_det> x = getVDCDet(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getVenRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getVenRptPickerData(x));  
        break;
        }
        
        case "addVdpMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdp_mstr x = objectMapper.readValue(sb.toString(), venData.vdp_mstr.class);            
            response.getWriter().print(arrayToJson(addVdpMstr(x)));
            break;
          }
        
        case "addOrUpdateVdpMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdp_mstr x = objectMapper.readValue(sb.toString(), venData.vdp_mstr.class);              
            response.getWriter().print(arrayToJson(addOrUpdateVdpMstr(x)));
            break;
          }
        
        case "updateVdpMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdp_mstr x = objectMapper.readValue(sb.toString(), venData.vdp_mstr.class);              
            response.getWriter().print(arrayToJson(updateVdpMstr(x)));
            break;
          }
        
        case "deleteVdpMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vdp_mstr x = objectMapper.readValue(sb.toString(), venData.vdp_mstr.class);              
            response.getWriter().print(arrayToJson(deleteVdpMstr(x)));
            break;
          }
        
        case "getVdpMstr" :  {      
            venData.vdp_mstr vdp = getVdpMstr(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(vdp);
            response.getWriter().print(r);
            break;  
        }
        
        case "addUpdateVDCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vd_ctrl x = objectMapper.readValue(sb.toString(), venData.vd_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateVDCtrl(x)));
            break;
          }
        
        case "getVDCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            venData.vd_ctrl x = getVDCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addVprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vpr_mstr x = objectMapper.readValue(sb.toString(), venData.vpr_mstr.class);            
            response.getWriter().print(arrayToJson(addVprMstr(x)));
            break;
          }
        
        case "updateVprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vpr_mstr x = objectMapper.readValue(sb.toString(), venData.vpr_mstr.class);            
            response.getWriter().print(arrayToJson(updateVprMstr(x)));
            break;
          }
        
        case "deleteVprMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            venData.vpr_mstr x = objectMapper.readValue(sb.toString(), venData.vpr_mstr.class);            
            response.getWriter().print(arrayToJson(deleteVprMstr(x)));
            break;
          }
        
        case "getVprMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            venData.vpr_mstr x = getVprMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getVprPriceLists" : { 
            ArrayList<venData.vpr_mstr> x = getVprPriceLists(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
        case "getVendPriceBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getVendPriceBrowseView(x));  
        break;
        }
        
        case "getVendXrefBrowseView" : {
        String[] x = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getVendXrefBrowseView(x));  
        break;
        }
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServVDR for id: " + id);    
            
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
