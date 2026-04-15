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


import com.blueseer.lbl.lblData;
import static com.blueseer.lbl.lblData.addLabelZebraMstr;
import static com.blueseer.lbl.lblData.deleteLabelZebraMstr;
import static com.blueseer.lbl.lblData.getLabelBrowseDetView;
import static com.blueseer.lbl.lblData.getLabelBrowseView;
import static com.blueseer.lbl.lblData.getLabelMstr;
import static com.blueseer.lbl.lblData.getLabelMstrByStrID;
import static com.blueseer.lbl.lblData.getLabelSerialDisplay;
import static com.blueseer.lbl.lblData.getLabelZebraMstr;
import com.blueseer.lbl.lblData.label_zebra;
import static com.blueseer.lbl.lblData.updateLabelZebraMstr;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
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
public class dataServLBL extends HttpServlet {
 
    
        
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
            
        case "addLabelZebraMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            label_zebra am = objectMapper.readValue(sb.toString(), label_zebra.class);            
            response.getWriter().print(arrayToJson(addLabelZebraMstr(am)));
            break;
        }
        
        case "updateLabelZebraMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            label_zebra am = objectMapper.readValue(sb.toString(), label_zebra.class);            
            response.getWriter().print(arrayToJson(updateLabelZebraMstr(am)));
            break;
        }
        
        case "deleteLabelZebraMstr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            label_zebra am = objectMapper.readValue(sb.toString(), label_zebra.class);            
            response.getWriter().print(arrayToJson(deleteLabelZebraMstr(am)));
            break;
        }
        
        case "getLabelZebraMstrX" : {
            String[] key = new String[]{request.getHeader("key")}; 
            label_zebra am = getLabelZebraMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(am);
            response.getWriter().print(r);
            break;
        }
        
        case "addMultiLabelTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<lblData.label_det> sdlist = null;
            if (ca[0] != null) {
            lblData.label_det[] sdarray = objectMapper.readValue(ca[0], lblData.label_det[].class);
                if (sdarray != null) {
                sdlist = new ArrayList<>(Arrays.asList(sdarray));
                }
            }
            lblData.label_mstr[] starray = objectMapper.readValue(ca[1], lblData.label_mstr[].class);
            ArrayList<lblData.label_mstr> stlist = new ArrayList<>(Arrays.asList(starray));
            response.getWriter().print(arrayToJson(lblData.addMultiLabelTransaction(sdlist, stlist)));  
            break;
        }
            
        case "deleteLabelByShipper" : {
            lblData.deleteLabelByShipper(request.getHeader("param1"));
            break; 
        }
            
        case "updateLabelStatus" : {
            lblData.updateLabelStatus(request.getHeader("param1"), request.getHeader("param2"));
            break;    
        }
            
        case "getLabelMultiPrintData" : {
            response.getWriter().print(lblData.getLabelMultiPrintData(request.getHeader("param1")));    
            break;   
        }
        
        case "getJobTicketPrintData" : {
            response.getWriter().print(lblData.getJobTicketPrintData(request.getHeader("param1")));    
            break;   
        }
        
        case "getJobOperationPrintData" : {
            response.getWriter().print(lblData.getJobOperationPrintData(request.getHeader("param1"), request.getHeader("param2")));    
            break;   
        }
        
        case "getLabelBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4"), 
               request.getHeader("param5"), 
               request.getHeader("param6")
               };     
        response.getWriter().print(getLabelBrowseView(it));  
        break;
        } 
            
        case "getLabelBrowseDetView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2")
               };     
        response.getWriter().print(getLabelBrowseDetView(it));  
        break;
        } 
                
        case "getLabelZebraMstr" : {       
        lblData.label_zebra lz = getLabelZebraMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(lz);
        response.getWriter().print(r);
        break;
        }
        
        case "getLabelMstr" : {       
        lblData.label_mstr lm = getLabelMstr(request.getHeader("param1"));
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(lm);
        response.getWriter().print(r);
        break;
        }
         
        case "getLabelMstrByStrID" : {       
        lblData.label_mstr lm = getLabelMstrByStrID(request.getHeader("param1"));
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(lm);
        response.getWriter().print(r);
        break;
        }
        
        
        case "getLabelSerialDisplay" : {       
            response.getWriter().print(getLabelSerialDisplay(request.getHeader("param1")));
            break; 
        }
        
        case "getLabelTableRecs" : {
            response.getWriter().print(ArrayListStringArrayToJson(lblData.getLabelTableRecs(request.getHeader("param1"))));
            break;
        }
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServLBL for id: " + id);    
            
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
