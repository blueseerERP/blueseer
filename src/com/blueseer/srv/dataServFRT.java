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


import com.blueseer.frt.frtData;
import static com.blueseer.frt.frtData.addBrokerMstr;
import static com.blueseer.frt.frtData.addCFOStatus;
import static com.blueseer.frt.frtData.addCarrierMstr;
import static com.blueseer.frt.frtData.addCodeFreight;
import static com.blueseer.frt.frtData.addDriverMstr;
import static com.blueseer.frt.frtData.addOrUpdateCodeFreight;
import static com.blueseer.frt.frtData.addUpdateCFOCtrl;
import com.blueseer.frt.frtData.brk_mstr;
import com.blueseer.frt.frtData.car_mstr;
import com.blueseer.frt.frtData.cfo_det;
import com.blueseer.frt.frtData.cfo_mstr;
import com.blueseer.frt.frtData.cfo_status;
import com.blueseer.frt.frtData.code_freight;
import static com.blueseer.frt.frtData.deleteBrokerMstr;
import static com.blueseer.frt.frtData.deleteCarrierMstr;
import static com.blueseer.frt.frtData.deleteCodeFreight;
import static com.blueseer.frt.frtData.deleteDriverMstr;
import com.blueseer.frt.frtData.drv_mstr;
import com.blueseer.frt.frtData.frt_ctrl;
import static com.blueseer.frt.frtData.getBrokerMstr;
import static com.blueseer.frt.frtData.getCFOBrowseView;
import static com.blueseer.frt.frtData.getCFOBrowseViewDet;
import static com.blueseer.frt.frtData.getCFOCtrl;
import static com.blueseer.frt.frtData.getCFODet;
import static com.blueseer.frt.frtData.getCFOLines;
import static com.blueseer.frt.frtData.getCFOMstr;
import static com.blueseer.frt.frtData.getCFOStatus;
import static com.blueseer.frt.frtData.getCFOStatusInit;
import static com.blueseer.frt.frtData.getCFOStatusList;
import static com.blueseer.frt.frtData.getCarrierMstr;
import static com.blueseer.frt.frtData.getCodeFreight;
import static com.blueseer.frt.frtData.getDriverMstr;
import static com.blueseer.frt.frtData.getMakeModel;
import static com.blueseer.frt.frtData.updateBrokerMstr;
import static com.blueseer.frt.frtData.updateCFORejection;
import static com.blueseer.frt.frtData.updateCarrierMstr;
import static com.blueseer.frt.frtData.updateCodeFreight;
import static com.blueseer.frt.frtData.updateDriverMstr;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.blueseer.utl.OVData;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class dataServFRT extends HttpServlet {
 
    
        
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
        
    case "addCFOTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            frtData.cfo_det[] sdarray = objectMapper.readValue(ca[0], frtData.cfo_det[].class);
            ArrayList<frtData.cfo_det> xdet = new ArrayList<frtData.cfo_det>(Arrays.asList(sdarray)); 
            frtData.cfo_mstr xh = objectMapper.readValue(ca[1], frtData.cfo_mstr.class);  
            frtData.cfo_item[] oiarray = objectMapper.readValue(ca[2], frtData.cfo_item[].class);
            ArrayList<frtData.cfo_item> oidet = new ArrayList<frtData.cfo_item>(Arrays.asList(oiarray)); 
            frtData.cfo_sos[] osarray = objectMapper.readValue(ca[3], frtData.cfo_sos[].class);
            ArrayList<frtData.cfo_sos> osdet = new ArrayList<frtData.cfo_sos>(Arrays.asList(osarray));  
            response.getWriter().print(arrayToJson(frtData.addCFOTransaction(xdet, xh, oidet, osdet)));  
            break;
    }
    
    case "updateCFOTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String s = objectMapper.readValue(ca[0], String.class);
            String v = objectMapper.readValue(ca[1], String.class);
            ArrayList<String> lines = objectMapper.readValue(ca[2], ArrayList.class); 
            frtData.cfo_det[] sdarray = objectMapper.readValue(ca[3], frtData.cfo_det[].class);
            ArrayList<frtData.cfo_det> xdet = new ArrayList<frtData.cfo_det>(Arrays.asList(sdarray)); 
            frtData.cfo_mstr xh = objectMapper.readValue(ca[4], frtData.cfo_mstr.class);  
            frtData.cfo_item[] oiarray = objectMapper.readValue(ca[5], frtData.cfo_item[].class);
            ArrayList<frtData.cfo_item> oidet = new ArrayList<frtData.cfo_item>(Arrays.asList(oiarray)); 
            frtData.cfo_sos[] osarray = objectMapper.readValue(ca[6], frtData.cfo_sos[].class);
            ArrayList<frtData.cfo_sos> osdet = new ArrayList<frtData.cfo_sos>(Arrays.asList(osarray));  
            response.getWriter().print(arrayToJson(frtData.updateCFOTransaction(s, v, lines, xdet, xh, oidet, osdet)));  
            break;
    }
    
    case "deleteCFOMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            frtData.cfo_mstr xh = objectMapper.readValue(sb.toString(), frtData.cfo_mstr.class);            
            response.getWriter().print(arrayToJson(frtData.deleteCFOMstr(xh)));  
            break;
    }
    
    case "addCarrierMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      car_mstr x = objectMapper.readValue(sb.toString(), car_mstr.class);            
      response.getWriter().print(arrayToJson(addCarrierMstr(x)));
      break;
    }
    
    case "updateCarrierMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      car_mstr x = objectMapper.readValue(sb.toString(), car_mstr.class);            
      response.getWriter().print(arrayToJson(updateCarrierMstr(x)));
      break;
    }
    
    case "deleteCarrierMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      car_mstr x = objectMapper.readValue(sb.toString(), car_mstr.class);            
      response.getWriter().print(arrayToJson(deleteCarrierMstr(x)));
      break;
    }
    
    case "getCarrierMstr" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      car_mstr x = getCarrierMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    case "addBrokerMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.brk_mstr am = objectMapper.readValue(sb.toString(), frtData.brk_mstr.class);            
      response.getWriter().print(arrayToJson(addBrokerMstr(am)));
      break;
    }
    
    case "updateBrokerMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      brk_mstr x = objectMapper.readValue(sb.toString(), brk_mstr.class);            
      response.getWriter().print(arrayToJson(updateBrokerMstr(x)));
      break;
    }
    
    case "deleteBrokerMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      brk_mstr x = objectMapper.readValue(sb.toString(), brk_mstr.class);            
      response.getWriter().print(arrayToJson(deleteBrokerMstr(x)));
      break;
    }
    
    case "getBrokerMstr" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      brk_mstr x = getBrokerMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    case "addDriverMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.drv_mstr am = objectMapper.readValue(sb.toString(), frtData.drv_mstr.class);            
      response.getWriter().print(arrayToJson(addDriverMstr(am)));
      break;
    }
    
    case "updateDriverMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      drv_mstr x = objectMapper.readValue(sb.toString(), drv_mstr.class);            
      response.getWriter().print(arrayToJson(updateDriverMstr(x)));
      break;
    }
    
    case "deleteDriverMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      drv_mstr x = objectMapper.readValue(sb.toString(), drv_mstr.class);            
      response.getWriter().print(arrayToJson(deleteDriverMstr(x)));
      break;
    }
    
    case "getDriverMstr" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      drv_mstr x = getDriverMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    case "addCodeFreight" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.code_freight am = objectMapper.readValue(sb.toString(), frtData.code_freight.class);            
      response.getWriter().print(arrayToJson(addCodeFreight(am)));
      break;
    }
    
    case "addOrUpdateCodeFreight" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.code_freight am = objectMapper.readValue(sb.toString(), frtData.code_freight.class);            
      response.getWriter().print(arrayToJson(addOrUpdateCodeFreight(am)));
      break;
    }
    
    case "updateCodeFreight" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      code_freight x = objectMapper.readValue(sb.toString(), code_freight.class);            
      response.getWriter().print(arrayToJson(updateCodeFreight(x)));
      break;
    }
    
    case "deleteCodeFreight" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      code_freight x = objectMapper.readValue(sb.toString(), code_freight.class);            
      response.getWriter().print(arrayToJson(deleteCodeFreight(x)));
      break;
    }
    
    case "getCodeFreight" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      code_freight x = getCodeFreight(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    case "addUpdateCFOCtrl" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.frt_ctrl am = objectMapper.readValue(sb.toString(), frtData.frt_ctrl.class);            
      response.getWriter().print(arrayToJson(addUpdateCFOCtrl(am)));
      break;
    }
    
    case "getCFOCtrl" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      frt_ctrl x = getCFOCtrl(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
        
    case "addCFOStatus" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      frtData.cfo_status am = objectMapper.readValue(sb.toString(), frtData.cfo_status.class);            
      response.getWriter().print(arrayToJson(addCFOStatus(am)));
      break;
    }
    
    case "getCFOStatus" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      cfo_status x = getCFOStatus(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    
    case "getCFOMstr" : {
      String[] key = new String[]{request.getHeader("param1")}; 
      cfo_mstr x = getCFOMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(x);
      response.getWriter().print(r);
      break;
    }
    
    case "getCFODet" : {       
        ArrayList<cfo_det> xd = getCFODet(request.getHeader("param1"), request.getHeader("param2"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 

    case "getCFOStatusList" : {       
        ArrayList<cfo_status> xd = getCFOStatusList(request.getHeader("param1"), request.getHeader("param2"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 

    case "getFreightCodeByCodeKey" : {
            response.getWriter().print(frtData.getFreightCodeByCodeKey(request.getHeader("param1"), request.getHeader("param2")));   
            break; 
    }
    
    case "getCFODefaultRevision" : {
            response.getWriter().print(frtData.getCFODefaultRevision(request.getHeader("param1")));   
            break; 
    }
    
    case "getCFOCust" : {
            response.getWriter().print(frtData.getCFOCust(request.getHeader("param1")));   
            break; 
    }
    
    case "getCFOCustfonbr" : {
            response.getWriter().print(frtData.getCFOCustfonbr(request.getHeader("param1")));   
            break; 
    }
    
    
    case "getCFOStatusInit" : {
      response.getWriter().print(ArrayListStringArrayToJson(getCFOStatusInit()));
      break;
    }
    
    case "getMakeModel" : {
      response.getWriter().print(ArrayListStringToJson(getMakeModel(request.getHeader("param1"))));
      break;
    }
    
    case "getCFOLines" : {
      response.getWriter().print(ArrayListStringToJson(getCFOLines(request.getHeader("param1"))));
      break;
    }
    
    case "updateFreightInvoice" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String[]> al = om.readValue(ca[0], new TypeReference<ArrayList<String[]>>() {});
            frtData.updateFreightInvoice(al, ca[1], ca[2]);    
            break;
        }
    
    case "updateCFORejection" : {
        response.getWriter().println(boolToJson(updateCFORejection(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3")))); 
        break;
        }

    case "getCFOBrowseView" : {
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
        response.getWriter().print(getCFOBrowseView(it));  
        break;
        } 
       
    case "getCFOBrowseViewDet" : {
        response.getWriter().print(getCFOBrowseViewDet(request.getHeader("param1"), request.getHeader("param2")));  
        break;
    } 
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServFRT for id: " + id);    
            
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
