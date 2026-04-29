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


import static com.blueseer.edi.EDI.getFilesOfDir;
import static com.blueseer.edi.EDI.runEDI;
import static com.blueseer.edi.EDI.runEDIsingle;
import com.blueseer.edi.ediData;
import static com.blueseer.edi.ediData.addAS2Mstr;
import static com.blueseer.edi.ediData.addEDIXref;
import static com.blueseer.edi.ediData.addEdiMstr;
import static com.blueseer.edi.ediData.addMapMstr;
import static com.blueseer.edi.ediData.addUpdateEDIMeta;
import com.blueseer.edi.ediData.edi_ctrl;
import static com.blueseer.edi.ediData.getEDICtrl;
import static com.blueseer.edi.ediData.addupdateEDICtrl;
import static com.blueseer.edi.ediData.deleteAPIMstr;
import static com.blueseer.edi.ediData.deleteAS2Mstr;
import static com.blueseer.edi.ediData.deleteEDIMeta;
import static com.blueseer.edi.ediData.deleteEDIXref;
import static com.blueseer.edi.ediData.deleteEdiMstr;
import static com.blueseer.edi.ediData.deleteMapMstr;
import static com.blueseer.edi.ediData.getAPIDMeta;
import static com.blueseer.edi.ediData.getAPIDet;
import static com.blueseer.edi.ediData.getAPIMstr;
import static com.blueseer.edi.ediData.getAS2Mstr;
import static com.blueseer.edi.ediData.getDFSMstr;
import static com.blueseer.edi.ediData.getEDIDFSSet;
import static com.blueseer.edi.ediData.getEDIDocSet;
import static com.blueseer.edi.ediData.getEDIMetaValueDetail;
import static com.blueseer.edi.ediData.getEDIMetaValueHeader;
import static com.blueseer.edi.ediData.getEDIPartnerSet;
import static com.blueseer.edi.ediData.getEDIXref;
import static com.blueseer.edi.ediData.getEdiMstr;
import static com.blueseer.edi.ediData.getMapMstr;
import static com.blueseer.edi.ediData.getWorkFlowSet;
import static com.blueseer.edi.ediData.isAPIMethodUnique;
import static com.blueseer.edi.ediData.updateAS2Mstr;
import static com.blueseer.edi.ediData.updateEDIXref;
import static com.blueseer.edi.ediData.updateEdiMstr;
import static com.blueseer.edi.ediData.updateEdiMstrMM;
import static com.blueseer.edi.ediData.updateMapMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.blueseer.utl.EDData;
import static com.blueseer.utl.EDData.addAS2AttributeRecord;
import static com.blueseer.utl.EDData.addEDIAttributeRecord;
import static com.blueseer.utl.EDData.deleteEDIAttributeRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
public class dataServEDI extends HttpServlet {
 
    
        
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
        response.getWriter().println(" dataServEDI authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id");
    
    switch (id) {
        case "addEDIPartnerTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ediData.edpd_partner[] sdarray = om.readValue(ca[0], ediData.edpd_partner[].class);
            ArrayList<ediData.edpd_partner> sdlist = new ArrayList<ediData.edpd_partner>(Arrays.asList(sdarray)); 
            ediData.edp_partner sm = om.readValue(ca[1], ediData.edp_partner.class); 
            response.getWriter().print(arrayToJson(ediData.addEDIPartnerTransaction(sdlist, sm)));  
            break;
            }
        
        case "updateEDIPartnerTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String x = ca[0];
            ediData.edpd_partner[] sdarray = om.readValue(ca[1], ediData.edpd_partner[].class);
            ArrayList<ediData.edpd_partner> sdlist = new ArrayList<ediData.edpd_partner>(Arrays.asList(sdarray)); 
            ediData.edp_partner sm = om.readValue(ca[2], ediData.edp_partner.class); 
            response.getWriter().print(arrayToJson(ediData.updateEDIPartnerTransaction(x, sdlist, sm)));  
            break;
            }
         
        case "deleteEDIPartner" : {
            response.getWriter().print(arrayToJson(ediData.deleteEDIPartner(request.getHeader("param1")
                    )));  
            break;
        }
            
        case "getEDIPartnerSet" : {       
        ediData.EDIPartnerSet x = getEDIPartnerSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        break;
        }
        
        
        case "addEDIDocTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ediData.edi_docdet[] sdarray = om.readValue(ca[0], ediData.edi_docdet[].class);
            ArrayList<ediData.edi_docdet> sdlist = new ArrayList<ediData.edi_docdet>(Arrays.asList(sdarray)); 
            ediData.edi_doc sm = om.readValue(ca[1], ediData.edi_doc.class); 
            response.getWriter().print(arrayToJson(ediData.addEDIDocTransaction(sdlist, sm)));   
            break;
            }
        
        case "updateEDIDocTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String x = ca[0];
            ediData.edi_docdet[] sdarray = om.readValue(ca[1], ediData.edi_docdet[].class);
            ArrayList<ediData.edi_docdet> sdlist = new ArrayList<ediData.edi_docdet>(Arrays.asList(sdarray)); 
            ediData.edi_doc sm = om.readValue(ca[2], ediData.edi_doc.class); 
            response.getWriter().print(arrayToJson(ediData.updateEDIDocTransaction(x, sdlist, sm)));   
            break;
            }
        
        case "deleteEDIDoc" : {
            response.getWriter().print(arrayToJson(ediData.deleteEDIDoc(request.getHeader("param1")
                    )));  
            break;
        }
                
        case "getEDIDocSet" : {       
        ediData.EDIDocSet x = getEDIDocSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        break;
        }
        
        
        case "addDFStructureTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ediData.dfs_det[] sdarray = om.readValue(ca[0], ediData.dfs_det[].class);
            ArrayList<ediData.dfs_det> sdlist = new ArrayList<ediData.dfs_det>(Arrays.asList(sdarray)); 
            ediData.dfs_mstr sm = om.readValue(ca[1], ediData.dfs_mstr.class); 
            response.getWriter().print(arrayToJson(ediData.addDFStructureTransaction(sdlist, sm)));   
            break; 
            }
        
        case "updateDFStructureTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String x = ca[0];
            ediData.dfs_det[] sdarray = om.readValue(ca[1], ediData.dfs_det[].class);
            ArrayList<ediData.dfs_det> sdlist = new ArrayList<ediData.dfs_det>(Arrays.asList(sdarray)); 
            ediData.dfs_mstr sm = om.readValue(ca[2], ediData.dfs_mstr.class); 
            response.getWriter().print(arrayToJson(ediData.updateDFStructureTransaction(x, sdlist, sm)));   
            break; 
            }
        
        case "deleteDFStructure" : {
            response.getWriter().print(arrayToJson(ediData.deleteDFStructure(request.getHeader("param1")
                    )));  
            break;
        }
        
        case "getEDIDFSSet" : {       
        ediData.DFSSet x = getEDIDFSSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        break;
        }
        
        
        case "addWkfTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ediData.wkfd_meta[] wkfdmarray = om.readValue(ca[0], ediData.wkfd_meta[].class);  
            ArrayList<ediData.wkfd_meta> wkfdmlist = new ArrayList<ediData.wkfd_meta>(Arrays.asList(wkfdmarray));  
            ediData.wkf_det[] wkfdarray = om.readValue(ca[1], ediData.wkf_det[].class);
            ArrayList<ediData.wkf_det> wkfdlist = new ArrayList<ediData.wkf_det>(Arrays.asList(wkfdarray)); 
            ediData.wkf_mstr wkf = om.readValue(ca[2], ediData.wkf_mstr.class); 
            response.getWriter().print(arrayToJson(ediData.addWkfTransaction(wkfdmlist, wkfdlist, wkf)));   
            break;
            }
        
        case "updateWkfMstrTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String x = ca[0];
            ediData.wkfd_meta[] wkfdmarray = om.readValue(ca[1], ediData.wkfd_meta[].class);  
            ArrayList<ediData.wkfd_meta> wkfdmlist = new ArrayList<ediData.wkfd_meta>(Arrays.asList(wkfdmarray));  
            ediData.wkf_det[] wkfdarray = om.readValue(ca[2], ediData.wkf_det[].class);
            ArrayList<ediData.wkf_det> wkfdlist = new ArrayList<ediData.wkf_det>(Arrays.asList(wkfdarray)); 
            ediData.wkf_mstr wkf = om.readValue(ca[3], ediData.wkf_mstr.class); 
            response.getWriter().print(arrayToJson(ediData.updateWkfMstrTransaction(x, wkfdmlist, wkfdlist, wkf)));     
            break;
            }
        
        case "deleteWkfMstr" : {
            response.getWriter().print(arrayToJson(ediData.deleteWkfMstr(request.getHeader("param1")
                    )));  
            break;
        }
        
        case "getWorkFlowSet" : {       
        ediData.WorkFlowSet x = getWorkFlowSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(x);
        response.getWriter().print(r);
        break;
        }
        
        
            
        case "addEDIXref" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_xref x = objectMapper.readValue(sb.toString(), ediData.edi_xref.class);            
            response.getWriter().print(arrayToJson(addEDIXref(x)));
            break;
          }
        
        case "updateEDIXref" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_xref x = objectMapper.readValue(sb.toString(), ediData.edi_xref.class);            
            response.getWriter().print(arrayToJson(updateEDIXref(x)));
            break;
          }
        
        case "deleteEDIXref" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_xref x = objectMapper.readValue(sb.toString(), ediData.edi_xref.class);            
            response.getWriter().print(arrayToJson(deleteEDIXref(x)));
            break;
          }
        
        case "getEDIXref" : { 
            String[] key = new String[]{request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"),
                request.getHeader("param5")}; 
            ediData.edi_xref x = getEDIXref(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addMapMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.map_mstr x = objectMapper.readValue(sb.toString(), ediData.map_mstr.class);            
            response.getWriter().print(arrayToJson(addMapMstr(x)));
            break;
          }
        
        case "updateMapMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.map_mstr x = objectMapper.readValue(sb.toString(), ediData.map_mstr.class);            
            response.getWriter().print(arrayToJson(updateMapMstr(x)));
            break;
          }
        
        case "deleteMapMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.map_mstr x = objectMapper.readValue(sb.toString(), ediData.map_mstr.class);            
            response.getWriter().print(arrayToJson(deleteMapMstr(x)));
            break;
          }
        
        
        case "getMapMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            ediData.map_mstr x = getMapMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getDFSMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            ediData.dfs_mstr x = getDFSMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getEdiMstr" : { 
            String[] key = new String[]{request.getHeader("param1"),
            request.getHeader("param2"),
            request.getHeader("param3"),
            request.getHeader("param4")}; 
            ediData.edi_mstr x = getEdiMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addEdiMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_mstr x = objectMapper.readValue(sb.toString(), ediData.edi_mstr.class);            
            response.getWriter().print(arrayToJson(addEdiMstr(x)));
            break;
          }
        
        case "updateEdiMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_mstr x = objectMapper.readValue(sb.toString(), ediData.edi_mstr.class);            
            response.getWriter().print(arrayToJson(updateEdiMstr(x)));
            break;
          }
        
        case "updateEdiMstrMM" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.map_mstr x = objectMapper.readValue(sb.toString(), ediData.map_mstr.class);            
            updateEdiMstrMM(x); 
            break;
          }
        
        case "deleteEdiMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.edi_mstr x = objectMapper.readValue(sb.toString(), ediData.edi_mstr.class);            
            response.getWriter().print(arrayToJson(deleteEdiMstr(x)));
            break;
          }
        
        
        case "getAS2Mstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            ediData.as2_mstr x = getAS2Mstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addAS2Mstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.as2_mstr x = objectMapper.readValue(sb.toString(), ediData.as2_mstr.class);            
            response.getWriter().print(arrayToJson(addAS2Mstr(x)));
            break;
          }
        
        case "updateAS2Mstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.as2_mstr x = objectMapper.readValue(sb.toString(), ediData.as2_mstr.class);            
            response.getWriter().print(arrayToJson(updateAS2Mstr(request.getHeader("param1"), x)));
            break;
          }
        
        case "deleteAS2Mstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.as2_mstr x = objectMapper.readValue(sb.toString(), ediData.as2_mstr.class);            
            response.getWriter().print(arrayToJson(deleteAS2Mstr(x)));
            break;
          }
        
        case "addAPITransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ediData.apid_meta sdarray = om.readValue(ca[0], ediData.apid_meta.class);
            ArrayList<ediData.apid_meta> sdlist = new ArrayList<ediData.apid_meta>(Arrays.asList(sdarray)); 
            ediData.api_mstr sm = om.readValue(ca[2], ediData.api_mstr.class); 
            ediData.api_det starray = om.readValue(ca[1], ediData.api_det.class);
            ArrayList<ediData.api_det> stlist = new ArrayList<ediData.api_det>(Arrays.asList(starray));
            response.getWriter().print(arrayToJson(ediData.addAPITransaction(sdlist, stlist, sm))); 
            }
            break; 
            
        case "updateAPITransaction" : {
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
            ediData.apid_meta[] sdarray = om.readValue(ca[2], ediData.apid_meta[].class);
            ArrayList<ediData.apid_meta> apidm = new ArrayList<>(Arrays.asList(sdarray)); 
            ediData.api_det[] starray = om.readValue(ca[3], ediData.api_det[].class);
            ArrayList<ediData.api_det> apid = new ArrayList<>(Arrays.asList(starray));
            ediData.api_mstr api = om.readValue(ca[4], ediData.api_mstr.class);
            response.getWriter().print(arrayToJson(ediData.updateAPITransaction(key, badlist, apidm, apid, api))); 
            }
            break;   
            
        case "updateAPIDetTransaction" : {
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
            ediData.apid_meta[] sdarray = om.readValue(ca[1], ediData.apid_meta[].class);
            ArrayList<ediData.apid_meta> apidm = new ArrayList<ediData.apid_meta>(Arrays.asList(sdarray));
            ediData.api_det[] starray = om.readValue(ca[1], ediData.api_det[].class);
            ArrayList<ediData.api_det> apid = new ArrayList<ediData.api_det>(Arrays.asList(starray));
            response.getWriter().print(arrayToJson(ediData.updateAPIDetTransaction(key, apidm, apid))); 
            }
            break;     
        
        case "deleteAPIMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ediData.api_mstr x = objectMapper.readValue(sb.toString(), ediData.api_mstr.class);            
            response.getWriter().print(arrayToJson(deleteAPIMstr(x)));
            break;
          }
        
        case "getAPIMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            ediData.api_mstr x = getAPIMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getAPIDets" : { 
            ArrayList<ediData.api_det> x = getAPIDet(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getAPIDet" : { 
            ediData.api_det x = getAPIDet(request.getHeader("param1"), request.getHeader("param2"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getAPIDMeta" : { 
            ArrayList<ediData.apid_meta> x = getAPIDMeta(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "exportInvoices" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String> list = om.readValue(sb.toString(), ArrayList.class); 
            response.getWriter().print(ArrayListStringArrayToJson(ediData.exportInvoices(list)));     
            break; 
            }
        
        case "exportASNs" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String> list = om.readValue(sb.toString(), ArrayList.class); 
            response.getWriter().print(ArrayListStringArrayToJson(ediData.exportASNs(list)));     
            break; 
            }
        
        case "exportACKs" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String> list = om.readValue(sb.toString(), ArrayList.class); 
            response.getWriter().print(ArrayListStringArrayToJson(ediData.exportACKs(list)));     
            break; 
            }
        
        case "exportPurchaseOrders" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String> list = om.readValue(sb.toString(), ArrayList.class); 
            response.getWriter().print(ArrayListStringArrayToJson(ediData.exportPurchaseOrders(list)));     
            break; 
            }
        

        
        case "getAPIMethodsList" :
            response.getWriter().print(ArrayListStringToJson(ediData.getAPIMethodsList(request.getHeader("param1"))));
            break;
        
        case "isAPIMethodUnique" : 
        response.getWriter().println(boolToJson(isAPIMethodUnique(request.getHeader("param1"), 
                request.getHeader("param2")))); 
        break; 
        
        case "getEDIInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getEDIInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
            
        case "getAS2AttributesList" :
            response.getWriter().print(ArrayListStringToJson(EDData.getAS2AttributesList(request.getHeader("param1"), request.getHeader("param2"))));
            break; 
            
        case "getEDIAttributesList" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIAttributesList(request.getHeader("param1"), 
                    request.getHeader("param2"), request.getHeader("param3"))));
            break;   
            
        case "getEDIInvoices" : {
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIInvoices(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param5")))));
            break;    
        }
        
        case "getEDIASNs" : {
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIASNs(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param5")))));
            break;    
        }
        
        case "getEDIPOs" : {
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIPOs(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param5")))));
            break;    
        }
        
        case "getEDIACKs" : {
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIACKs(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param5")))));
            break;    
        }
            
        case "readEDIRawFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.readEDIRawFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param3")),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6") )));
            break;
            
        case "getEDIRawFileByFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIRawFileByFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;     
            
        case "getEDIAckFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIAckFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3") )));
            break; 
            
        case "getEDISenderReceiverByDocTypeOUT" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDISenderReceiverByDocTypeOUT(request.getHeader("param1"))));
            break; 
            
        case "getDSFasString" :
            response.getWriter().print(ArrayListStringToJson(ediData.getDSFasString(request.getHeader("param1"))));
            break;
            
        case "getDFSasArray" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getDFSasArray(request.getHeader("param1"))));
            break; 
            
        case "getMapMstrList" :
            response.getWriter().print(ArrayListStringToJson(ediData.getMapMstrList(request.getHeader("param1"))));
            break;    
            
        case "getEDIBatchFromedi_file" :
            response.getWriter().print(EDData.getEDIBatchFromedi_file(request.getHeader("param1")));
            break;  
           
        case "getEDIPartnerDesc" :
            response.getWriter().print(EDData.getEDIPartnerDesc(request.getHeader("param1")));
            break;    
            
        case "getEDITransBrowseDocView" :
            response.getWriter().print(ediData.getEDITransBrowseDocView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7") )); 
            break; 
            
        case "getEDITransBrowseFileView" :
            response.getWriter().print(ediData.getEDITransBrowseFileView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7") )); 
            break;  
            
        case "getEDITransBrowseDetail" :
            response.getWriter().print(ediData.getEDITransBrowseDetail(request.getHeader("param1"), 
                    request.getHeader("param2") )); 
            break;
            
        case "getAS2LogView" :
            response.getWriter().print(ediData.getAS2LogView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4") )); 
            break; 
            
        case "getAPILogView" :
            response.getWriter().print(ediData.getAPILogView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4") )); 
            break; 
            
        case "getWKFLogView" :
            response.getWriter().print(ediData.getWKFLogView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4") )); 
            break;     
            
        case "getAPIBrowseView" :
            response.getWriter().print(ediData.getAPIBrowseView(request.getHeader("param1"), 
                    request.getHeader("param2"))); 
            break;
            
        case "getAPIBrowseDetView" :
            response.getWriter().print(ediData.getAPIBrowseDetView(request.getHeader("param1")));   
            break;    
            
        case "getAS2LogDetailDetail" :
            response.getWriter().print(ediData.getAS2LogDetailDetail(request.getHeader("param1"))); 
            break;
            
        case "getWKFLogDetail" :
            response.getWriter().print(ediData.getWKFLogDetail(request.getHeader("param1"))); 
            break;    
            
        case "getAPILogDetailDetail" :
            response.getWriter().print(ediData.getAPILogDetailDetail(request.getHeader("param1"))); 
            break;    
            
        case "getEDICtrl" : 
            edi_ctrl ec = getEDICtrl();
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(ec);
            response.getWriter().print(r);
            break;
            
        case "addupdateEDICtrl" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper omEDICtrl = new ObjectMapper();
            edi_ctrl ecvar = omEDICtrl.readValue(sb.toString(), edi_ctrl.class);            
            response.getWriter().print(arrayToJson(addupdateEDICtrl(ecvar)));
            break;
        }
            
        case "updateEDIASNStatus" :
            EDData.updateEDIASNStatus(request.getHeader("param1"), request.getHeader("param2"));
            response.getWriter().print(arrayToJson(new String[]{"0",""}));    
            break;
            
        case "deleteAS2attribute" :
            EDData.deleteAS2attribute(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"));
            response.getWriter().print(arrayToJson(new String[]{"0",""}));    
            break;    
           
        case "addAS2AttributeRecord" : 
        response.getWriter().println(boolToJson(addAS2AttributeRecord(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        
        case "addEDIAttributeRecord" : 
        response.getWriter().println(boolToJson(addEDIAttributeRecord(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4"),
                request.getHeader("param5")))); 
        break;
        
        case "deleteEDIAttributeRecord" : 
        response.getWriter().println(boolToJson(deleteEDIAttributeRecord(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
            
        case "getEDITPDefaults" :
            EDData.getEDITPDefaults(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"));
            response.getWriter().print(arrayToJson(new String[]{"0",""}));    
            break;    
            
        case "getEDIMetaValueDetail" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getEDIMetaValueDetail(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
            
        case "getEDIMetaValueAll" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getEDIMetaValueAll(request.getHeader("param1"))));
            break;    
            
        case "getEDIMetaValueHeader" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getEDIMetaValueHeader(request.getHeader("param1"))));
            break;
            
        case "getFilesOfDir" : 
        response.getWriter().print(arrayToJson(getFilesOfDir(request.getHeader("param1")))); 
        break;  
        
        case "runEDIsingle" : {
        InputStream inputStream = request.getInputStream(); // as byte array  
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }
        response.getWriter().print(arrayToJson(runEDIsingle(null,buf.toString(StandardCharsets.UTF_8.name()),"")));
        break; 
        }
        
        case "addUpdateEDIMeta" : 
        response.getWriter().println(boolToJson(addUpdateEDIMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        
        case "deleteEDIMeta" : 
        response.getWriter().println(boolToJson(deleteEDIMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        
        case "runEDI" : {
        String line; 
        StringBuilder sb = new StringBuilder();  
        BufferedReader reader = request.getReader();  // as string
        while ((line = reader.readLine()) != null) {  
        sb.append(line);
        }
        String[] files = sb.toString().split(",",-1);
        response.getWriter().print(ArrayListStringToJson(runEDI(null, files, "")));
        break; 
        }
            
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServEDI for id: " + id);    
            
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
