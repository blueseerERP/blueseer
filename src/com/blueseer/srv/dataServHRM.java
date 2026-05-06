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


import com.blueseer.hrm.hrmData;
import static com.blueseer.hrm.hrmData.addEmpTrain;
import static com.blueseer.hrm.hrmData.addEmployeeTransaction;
import static com.blueseer.hrm.hrmData.deleteEmpMstr;
import static com.blueseer.hrm.hrmData.deleteEmpTrain;
import static com.blueseer.hrm.hrmData.getEmpFormalNameByID;
import static com.blueseer.hrm.hrmData.getEmpIDByFormalName;
import static com.blueseer.hrm.hrmData.getEmpNameAll;
import static com.blueseer.hrm.hrmData.getEmpTrain;
import static com.blueseer.hrm.hrmData.getEmpTrainByCourse;
import static com.blueseer.hrm.hrmData.getEmpTrainRecords;
import static com.blueseer.hrm.hrmData.getEmployeeExceptions;
import static com.blueseer.hrm.hrmData.getEmployeeMstr;
import static com.blueseer.hrm.hrmData.getHrmRptPickerData;
import static com.blueseer.hrm.hrmData.getPayRecords;
import static com.blueseer.hrm.hrmData.isValidEmployeeID;
import static com.blueseer.hrm.hrmData.updateEmpTrain;
import static com.blueseer.hrm.hrmData.updateEmployeeTransaction;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
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
public class dataServHRM extends HttpServlet {
 
    
        
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
        
        case "addEmployeeTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            hrmData.emp_mstr em = om.readValue(ca[0], hrmData.emp_mstr.class); 
            hrmData.emp_exception[] sdarray = om.readValue(ca[1], hrmData.emp_exception[].class);
            ArrayList<hrmData.emp_exception> svlist = new ArrayList<hrmData.emp_exception>(Arrays.asList(sdarray));             
            response.getWriter().print(arrayToJson(addEmployeeTransaction(em, svlist))); 
            break;
            }
        
        case "updateEmployeeTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            hrmData.emp_mstr em = om.readValue(ca[0], hrmData.emp_mstr.class); 
            hrmData.emp_exception[] sdarray = om.readValue(ca[1], hrmData.emp_exception[].class);
            ArrayList<hrmData.emp_exception> svlist = new ArrayList<hrmData.emp_exception>(Arrays.asList(sdarray));             
            response.getWriter().print(arrayToJson(updateEmployeeTransaction(em, svlist))); 
            break;
            }
        
        case "deleteEmpMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            hrmData.emp_mstr em = om.readValue(sb.toString(), hrmData.emp_mstr.class);                    
            response.getWriter().print(arrayToJson(deleteEmpMstr(em))); 
            break;
            }
        
        case "getEmployeeMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            hrmData.emp_mstr x = getEmployeeMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getEmployeeExceptions" : {       
        ArrayList<hrmData.emp_exception> xd = getEmployeeExceptions(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
        
        case "getEmpTrainRecords" : {       
        ArrayList<hrmData.emp_train> xd = getEmpTrainRecords(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
        
        case "getEmpTrainByCourse" : {       
        ArrayList<hrmData.emp_train> xd = getEmpTrainByCourse(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
        
        
        case "getEmpNameAll" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getEmpNameAll()));
            break;
        }
        
        case "getEmpIDByFormalName" : {       
            response.getWriter().print(getEmpIDByFormalName(request.getHeader("param1")));
            break;
        }
        
        case "getEmpFormalNameByID" : {       
            response.getWriter().print(getEmpFormalNameByID(request.getHeader("param1")));
            break;
        }
        
        case "getHrmRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getHrmRptPickerData(x));  
        break;
        }
        
        case "isValidEmployeeID" : {
        response.getWriter().println(boolToJson(isValidEmployeeID(request.getHeader("param1")))); 
        break;
        }
        
        case "getPayRecords" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getPayRecords(request.getHeader("param1"))));
            break;
        }
        
        case "addEmpTrain" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            hrmData.emp_train[] sdarray = om.readValue(sb.toString(), hrmData.emp_train[].class);
            ArrayList<hrmData.emp_train> svlist = new ArrayList<hrmData.emp_train>(Arrays.asList(sdarray));             
            response.getWriter().print(arrayToJson(addEmpTrain(svlist))); 
            break;
            } 
    
        case "updateEmpTrain" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            hrmData.emp_train[] sdarray = om.readValue(sb.toString(), hrmData.emp_train[].class);
            ArrayList<hrmData.emp_train> svlist = new ArrayList<hrmData.emp_train>(Arrays.asList(sdarray));            
            response.getWriter().print(arrayToJson(updateEmpTrain(request.getHeader("param1"), svlist))); 
            break;
            }

        case "deleteEmpTrain" : {                 
      response.getWriter().print(arrayToJson(deleteEmpTrain(request.getHeader("param1"))));
      break;
    }
        
        case "getEmpTrain" : {
      String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
      hrmData.emp_train am = getEmpTrain(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServHRM for id: " + id);    
            
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
