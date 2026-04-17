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


import com.blueseer.eng.engData;
import static com.blueseer.eng.engData.getECNBrowseView;
import static com.blueseer.eng.engData.getECNBrowseViewDet;
import static com.blueseer.eng.engData.getECNMstr;
import static com.blueseer.eng.engData.getECNTask;
import static com.blueseer.eng.engData.getECNTaskSeq;
import static com.blueseer.eng.engData.getTaskBrowseView;
import static com.blueseer.eng.engData.getTaskBrowseViewDet;
import static com.blueseer.eng.engData.getTaskDet;
import static com.blueseer.eng.engData.getTaskDetSeq;
import static com.blueseer.eng.engData.getTaskMstr;
import com.blueseer.shp.shpData;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
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
public class dataServENG extends HttpServlet {
 
    
        
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
        
    case "addTaskTransaction" : {
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
            engData.task_det[] sdarray = objectMapper.readValue(ca[0], engData.task_det[].class);
            ArrayList<engData.task_det> xdet = new ArrayList<engData.task_det>(Arrays.asList(sdarray)); 
            engData.task_mstr xh = objectMapper.readValue(ca[1], engData.task_mstr.class);            
            response.getWriter().print(arrayToJson(engData.addTaskTransaction(xdet, xh)));  
            break;
    }
    
    case "updateTaskTransaction" : {
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
            ArrayList<String> lines = objectMapper.readValue(ca[1], ArrayList.class); 
            engData.task_det[] sdarray = objectMapper.readValue(ca[2], engData.task_det[].class);
            ArrayList<engData.task_det> xdet = new ArrayList<engData.task_det>(Arrays.asList(sdarray)); 
            engData.task_mstr xh = objectMapper.readValue(ca[3], engData.task_mstr.class);            
            response.getWriter().print(arrayToJson(engData.updateTaskTransaction(s, lines, xdet, xh)));  
            break;
    }
    
    case "deleteTaskMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            engData.task_mstr xh = objectMapper.readValue(sb.toString(), engData.task_mstr.class);            
            response.getWriter().print(arrayToJson(engData.deleteTaskMstr(xh)));  
            break;
    }
    
    case "getTaskMstr" :  {      
            engData.task_mstr x = getTaskMstr(new String[]{request.getHeader("param1")});
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
    
    case "getTaskDet" : { 
            ArrayList<engData.task_det> x = getTaskDet(request.getHeader("param1")); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getTaskDetSeq" :  {      
            engData.task_det x = getTaskDetSeq(request.getHeader("param1"), request.getHeader("param2"));
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
    
    
    
    case "addECNTransaction" : {
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
            engData.ecn_task[] sdarray = objectMapper.readValue(ca[0], engData.ecn_task[].class);
            ArrayList<engData.ecn_task> xdet = new ArrayList<engData.ecn_task>(Arrays.asList(sdarray)); 
            engData.ecn_mstr xh = objectMapper.readValue(ca[1], engData.ecn_mstr.class);            
            response.getWriter().print(arrayToJson(engData.addECNTransaction(xdet, xh)));   
            break;
    }
    
    case "updateECNTransaction" : {
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
            ArrayList<String> lines = objectMapper.readValue(ca[1], ArrayList.class); 
            engData.ecn_task[] sdarray = objectMapper.readValue(ca[2], engData.ecn_task[].class);
            ArrayList<engData.ecn_task> xdet = new ArrayList<engData.ecn_task>(Arrays.asList(sdarray)); 
            engData.ecn_mstr xh = objectMapper.readValue(ca[3], engData.ecn_mstr.class);            
            response.getWriter().print(arrayToJson(engData.updateECNTransaction(s, lines, xdet, xh)));  
            break;
    }
    
    case "deleteECNMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            engData.ecn_mstr xh = objectMapper.readValue(sb.toString(), engData.ecn_mstr.class);            
            response.getWriter().print(arrayToJson(engData.deleteECNMstr(xh)));  
            break;
    }
    
    case "getECNMstr" :  {      
            engData.ecn_mstr x = getECNMstr(new String[]{request.getHeader("param1")});
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
    
    case "getECNTask" : { 
            ArrayList<engData.ecn_task> x = getECNTask(request.getHeader("param1")); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getECNTaskSeq" :  {      
            engData.ecn_task x = getECNTaskSeq(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"));
            ObjectMapper om = new ObjectMapper(); 
            String r = om.writeValueAsString(x); 
            response.getWriter().print(r);
            break;    
        }
    
    
    
    case "getECNBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getECNBrowseView(it));  
        break;
        } 
       
    case "getECNBrowseViewDet" : {
        response.getWriter().print(getECNBrowseViewDet(request.getHeader("param1")));  
        break;
    } 
    
    case "getTaskBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getTaskBrowseView(it));  
        break;
        } 
       
    case "getTaskBrowseViewDet" : {
        response.getWriter().print(getTaskBrowseViewDet(request.getHeader("param1")));  
        break;
    } 
     
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServENG for id: " + id);    
            
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
