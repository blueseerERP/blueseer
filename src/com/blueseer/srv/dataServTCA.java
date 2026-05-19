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
import static com.blueseer.hrm.hrmData.updateEmpClockStatus;
import static com.blueseer.hrm.hrmData.updateEmpTrain;
import static com.blueseer.hrm.hrmData.updateEmployeeTransaction;
import com.blueseer.tca.tcaData;
import static com.blueseer.tca.tcaData.addClockCode;
import static com.blueseer.tca.tcaData.addTimeClock;
import static com.blueseer.tca.tcaData.deleteClockCode;
import static com.blueseer.tca.tcaData.getClockCode;
import static com.blueseer.tca.tcaData.getTimeClock;
import static com.blueseer.tca.tcaData.getTimeClockRec;
import static com.blueseer.tca.tcaData.getTimeClockSet;
import static com.blueseer.tca.tcaData.updateClockCode;
import static com.blueseer.tca.tcaData.updateTimeClock;
import static com.blueseer.tca.tcaData.updateTimeClockRec;
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
public class dataServTCA extends HttpServlet {
 
    
        
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
        
        case "addTimeClock" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.time_clock tc = om.readValue(sb.toString(), tcaData.time_clock.class);                          
            response.getWriter().print(arrayToJson(addTimeClock(tc))); 
            break;
            }
        
        case "updateTimeClock" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.time_clock tc = om.readValue(sb.toString(), tcaData.time_clock.class);                          
            response.getWriter().print(arrayToJson(updateTimeClock(tc))); 
            break;
            }
        
        case "updateTimeClockRec" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.time_clock tc = om.readValue(sb.toString(), tcaData.time_clock.class);                          
            response.getWriter().print(arrayToJson(updateTimeClockRec(tc))); 
            break;
            }
                
        case "getTimeClockRec" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            tcaData.time_clock x = getTimeClockRec(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getTimeClock" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            tcaData.time_clock x = getTimeClock(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getTimeClockSet" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            tcaData.TimeClockSet x = getTimeClockSet(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addClockCode" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.clock_code tc = om.readValue(sb.toString(), tcaData.clock_code.class);                          
            response.getWriter().print(arrayToJson(addClockCode(tc))); 
            break;
            }
        
        case "updateClockCode" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.clock_code tc = om.readValue(sb.toString(), tcaData.clock_code.class);                          
            response.getWriter().print(arrayToJson(updateClockCode(tc))); 
            break;
            }
        
        case "getClockCode" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            tcaData.clock_code x = getClockCode(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "deleteClockCode" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            tcaData.clock_code tc = om.readValue(sb.toString(), tcaData.clock_code.class);                          
            response.getWriter().print(arrayToJson(deleteClockCode(tc))); 
            break;
            }
                
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServTCA for id: " + id);    
            
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
