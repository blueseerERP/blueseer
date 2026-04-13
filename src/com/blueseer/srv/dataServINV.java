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

import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.addItemMasterMass;
import static com.blueseer.inv.invData.addItemMstr;
import static com.blueseer.inv.invData.addLocationMstr;
import static com.blueseer.inv.invData.addPLMstr;
import static com.blueseer.inv.invData.addQualMstr;
import static com.blueseer.inv.invData.addRoutingMstr;
import static com.blueseer.inv.invData.addUOMConvMstr;
import static com.blueseer.inv.invData.addUOMMstr;
import static com.blueseer.inv.invData.addUpdateINVCtrl;
import static com.blueseer.inv.invData.addWareHouseMstr;
import static com.blueseer.inv.invData.addWorkCenterMstr;
import static com.blueseer.inv.invData.addupdateBOMMstr;
import static com.blueseer.inv.invData.bind_tree;
import static com.blueseer.inv.invData.bind_tree_op;
import static com.blueseer.inv.invData.deleteItemMstr;
import static com.blueseer.inv.invData.deleteLocationMstr;
import static com.blueseer.inv.invData.deletePLMstr;
import static com.blueseer.inv.invData.deleteQualMstr;
import static com.blueseer.inv.invData.deleteRoutingMstr;
import static com.blueseer.inv.invData.deleteUOMConvMstr;
import static com.blueseer.inv.invData.deleteUOMMstr;
import static com.blueseer.inv.invData.deleteWareHouseMstr;
import static com.blueseer.inv.invData.deleteWorkCenterMstr;
import static com.blueseer.inv.invData.deleteZeroInventoryRecs;
import static com.blueseer.inv.invData.getBOMBrowseView;
import static com.blueseer.inv.invData.getBOMInit;
import static com.blueseer.inv.invData.getBOMMstr;
import static com.blueseer.inv.invData.getBOMParentOpElements;
import static com.blueseer.inv.invData.getBOMValidation;
import static com.blueseer.inv.invData.getBOMsByItemSite;
import static com.blueseer.inv.invData.getBOMsByItemSite_mg;
import static com.blueseer.inv.invData.getComponentByBomOp;
import static com.blueseer.inv.invData.getCurrentCost;
import static com.blueseer.inv.invData.getINVCtrl;
import static com.blueseer.inv.invData.getInMstr;
import static com.blueseer.inv.invData.getInvBrowseView;
import static com.blueseer.inv.invData.getInvMaintInit;
import static com.blueseer.inv.invData.getInvMaintInit_min;
import static com.blueseer.inv.invData.getInvMetaOperators;
import static com.blueseer.inv.invData.getInvRptPickerData;
import static com.blueseer.inv.invData.getInvValuationBrowseView;
import static com.blueseer.inv.invData.getInventoryQtyByItem;
import static com.blueseer.inv.invData.getItemBrowseView;
import static com.blueseer.inv.invData.getItemComponentDetail;
import static com.blueseer.inv.invData.getItemCost;
import static com.blueseer.inv.invData.getItemCostElements;
import static com.blueseer.inv.invData.getItemDataInit;
import static com.blueseer.inv.invData.getItemDetail;
import static com.blueseer.inv.invData.getItemImagesFile;
import static com.blueseer.inv.invData.getItemMaintInit;
import static com.blueseer.inv.invData.getItemMasterSchedlist;
import static com.blueseer.inv.invData.getItemMstr;
import static com.blueseer.inv.invData.getItemPrice;
import static com.blueseer.inv.invData.getItemPriceFromCust;
import static com.blueseer.inv.invData.getItemPriceFromVend;
import static com.blueseer.inv.invData.getItemQOHTotal;
import static com.blueseer.inv.invData.getItemQtyByWarehouseAndLocation;
import static com.blueseer.inv.invData.getItemRoutingBrowseView;
import static com.blueseer.inv.invData.getItemWFOPandDESC;
import static com.blueseer.inv.invData.getLocationListByWarehouse;
import static com.blueseer.inv.invData.getLocationMaintInit;
import static com.blueseer.inv.invData.getLocationMstr;
import static com.blueseer.inv.invData.getOrderMaintDetailEvent;
import static com.blueseer.inv.invData.getPLMstr;
import static com.blueseer.inv.invData.getQPRBrowseView;
import static com.blueseer.inv.invData.getQualMstr;
import static com.blueseer.inv.invData.getRecentTransByItem;
import static com.blueseer.inv.invData.getRoutingMstr;
import static com.blueseer.inv.invData.getRoutingMstrList;
import static com.blueseer.inv.invData.getRoutingOperations;
import static com.blueseer.inv.invData.getTotalCostElements;
import static com.blueseer.inv.invData.getTranMstr;
import static com.blueseer.inv.invData.getTranMstrBySerial;
import static com.blueseer.inv.invData.getUOMConvMstr;
import static com.blueseer.inv.invData.getUOMMstr;
import static com.blueseer.inv.invData.getWHLOCfromSerialNumber;
import static com.blueseer.inv.invData.getWareHouseMaintInit;
import static com.blueseer.inv.invData.getWareHouseMstr;
import static com.blueseer.inv.invData.getWorkCenterMstr;
import static com.blueseer.inv.invData.isBOMUnique;
import static com.blueseer.inv.invData.rebaseCurrentCost;
import static com.blueseer.inv.invData.resetBOMDefault;
import static com.blueseer.inv.invData.updateCurrentItemCost;
import static com.blueseer.inv.invData.updateItemMstr;
import static com.blueseer.inv.invData.updateLocationMstr;
import static com.blueseer.inv.invData.updatePLMstr;
import static com.blueseer.inv.invData.updateQualMstr;
import static com.blueseer.inv.invData.updateRoutingMstr;
import static com.blueseer.inv.invData.updateUOMConvMstr;
import static com.blueseer.inv.invData.updateUOMMstr;
import static com.blueseer.inv.invData.updateWareHouseMstr;
import static com.blueseer.inv.invData.updateWorkCenterMstr;
import static com.blueseer.ord.ordData.getOrderItemAllocatedQty;
import static com.blueseer.utl.BlueSeerUtils.ArrayListDoubleToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringStringArrToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.OVData.getCodeMstrValueList;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getTableInfo;
import com.blueseer.utl.TreeConverter;
import com.blueseer.utl.TreeConverter.MyNodePOJO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


/**
 *
 * @author terryva
 */
public class dataServINV extends HttpServlet {
 
    
        
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
        
        case "addItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(addItemMstr(x)));
            break;
          }
        
        case "updateItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(updateItemMstr(x)));
            break;
          }
        
        case "deleteItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(deleteItemMstr(x)));
            break;
          }
        
        case "getItemMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.item_mstr x = getItemMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addupdateBOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.bom_mstr x = objectMapper.readValue(sb.toString(), invData.bom_mstr.class);            
            response.getWriter().print(arrayToJson(addupdateBOMMstr(x)));
            break;
          }
        
        case "getBOMMstr" : { 
            String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
            invData.bom_mstr x = getBOMMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addPBM" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
          
            invData.pbm_mstr pm = objectMapper.readValue(ca[0], invData.pbm_mstr.class); 
            invData.bom_mstr bm = objectMapper.readValue(ca[1], invData.bom_mstr.class); 
            boolean b = objectMapper.readValue(ca[2], boolean.class); 
            response.getWriter().print(arrayToJson(invData.addPBM(pm, bm, b)));  
            break;
        }
        
        case "updatePBM" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
          
            invData.pbm_mstr pm = objectMapper.readValue(ca[0], invData.pbm_mstr.class); 
            invData.bom_mstr bm = objectMapper.readValue(ca[1], invData.bom_mstr.class); 
            response.getWriter().print(arrayToJson(invData.updatePBM(pm, bm)));  
            break;
        }
        
        case "deletePBM" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
          
            invData.pbm_mstr pm = objectMapper.readValue(ca[0], invData.pbm_mstr.class); 
            invData.bom_mstr bm = objectMapper.readValue(ca[1], invData.bom_mstr.class);
            response.getWriter().print(arrayToJson(invData.deletePBM(pm, bm)));  
            break;
        }
        
        
        case "addPLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(addPLMstr(x)));
            break;
          }
        
        case "updatePLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(updatePLMstr(x)));
            break;
          }
        
        case "deletePLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(deletePLMstr(x)));
            break;
          }
        
        case "getPLMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.pl_mstr x = getPLMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addQualMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.qual_mstr x = objectMapper.readValue(sb.toString(), invData.qual_mstr.class);            
            response.getWriter().print(arrayToJson(addQualMstr(x)));
            break;
          }
        
        case "updateQualMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.qual_mstr x = objectMapper.readValue(sb.toString(), invData.qual_mstr.class);            
            response.getWriter().print(arrayToJson(updateQualMstr(x)));
            break;
          }
        
        case "deleteQualMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.qual_mstr x = objectMapper.readValue(sb.toString(), invData.qual_mstr.class);            
            response.getWriter().print(arrayToJson(deleteQualMstr(x)));
            break;
          }
        
        case "getQualMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.qual_mstr x = getQualMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
        case "addWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(addWareHouseMstr(x)));
            break;
          }
        
        case "updateWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(updateWareHouseMstr(x)));
            break;
          }
        
        case "deleteWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(deleteWareHouseMstr(x)));
            break;
          }
        
        case "getWareHouseMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wh_mstr x = getWareHouseMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(addLocationMstr(x)));
            break;
          }
        
        case "updateLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(updateLocationMstr(x)));
            break;
          }
        
        case "deleteLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(deleteLocationMstr(x)));
            break;
          }
        
        case "getLocationMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.loc_mstr x = getLocationMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(addUOMMstr(x)));
            break;
          }
        
        case "updateUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(updateUOMMstr(x)));
            break;
          }
        
        case "deleteUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(deleteUOMMstr(x)));
            break;
          }
        
        case "getUOMMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.uom_mstr x = getUOMMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addUOMConvMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.conv_mstr x = objectMapper.readValue(sb.toString(), invData.conv_mstr.class);            
            response.getWriter().print(arrayToJson(addUOMConvMstr(x)));
            break;
          }
        
        case "updateUOMConvMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.conv_mstr x = objectMapper.readValue(sb.toString(), invData.conv_mstr.class);            
            response.getWriter().print(arrayToJson(updateUOMConvMstr(x)));
            break;
          }
        
        case "deleteUOMConvMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.conv_mstr x = objectMapper.readValue(sb.toString(), invData.conv_mstr.class);            
            response.getWriter().print(arrayToJson(deleteUOMConvMstr(x)));
            break;
          }
        
        case "getUOMConvMstr" : { 
            String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
            invData.conv_mstr x = getUOMConvMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
        case "addRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(addRoutingMstr(x)));
            break;
          }
        
        case "addRoutingMstrByArray" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr[] sdarray = objectMapper.readValue(sb.toString(), invData.wf_mstr[].class);
            ArrayList<invData.wf_mstr> sdlist = new ArrayList<invData.wf_mstr>(Arrays.asList(sdarray)); 
            response.getWriter().print(arrayToJson(addRoutingMstr(sdlist)));
            break;
          }
        
        case "updateRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(updateRoutingMstr(x)));
            break;
          }
        
        case "deleteRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(deleteRoutingMstr(x)));
            break;
          }
        
        case "getRoutingMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wf_mstr x = getRoutingMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(addWorkCenterMstr(x)));
            break;
          }
        
        case "updateWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(updateWorkCenterMstr(x)));
            break;
          }
        
        case "deleteWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(deleteWorkCenterMstr(x)));
            break;
          }
        
        case "getWorkCenterMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wc_mstr x = getWorkCenterMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getINVCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.inv_ctrl x = getINVCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getTranMstr" : { 
            invData.tran_mstr x = getTranMstr(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getTranMstrBySerial" : { 
            invData.tran_mstr x = getTranMstrBySerial(request.getHeader("param1"), request.getHeader("param2"));
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getWHLOCfromSerialNumber" : {       
            response.getWriter().print(arrayToJson(getWHLOCfromSerialNumber(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
        
        case "getBOMValidation" : {       
            response.getWriter().print(arrayToJson(getBOMValidation(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"))));
            break;
        }
        
        case "getBOMsByItemSite_mg" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getBOMsByItemSite_mg(request.getHeader("param1"))));
            break;
        }
        
        case "getInvMetaOperators" : {       
            response.getWriter().print(ArrayListStringToJson(getInvMetaOperators(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
        
        case "getRoutingOperations" : {       
            response.getWriter().print(ArrayListStringToJson(getRoutingOperations(request.getHeader("param1"))));
            break;
        }
        
        
        case "getBOMsByItemSite" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getBOMsByItemSite(request.getHeader("param1"))));
            break;
        }
        
        case "isBOMUnique" : {
        response.getWriter().println(boolToJson(isBOMUnique(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3")))); 
        break;
        }
            
        case "getLocationListByWarehouse" : {       
            response.getWriter().print(ArrayListStringToJson(getLocationListByWarehouse(request.getHeader("param1"))));
            break;    
        }
        
        case "getItemMasterSchedlist" : {       
            response.getWriter().print(ArrayListStringToJson(getItemMasterSchedlist()));
            break;    
        }
        
        case "getItemWFOPandDESC" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getItemWFOPandDESC(request.getHeader("param1"))));
            break;    
        }
            
        case "getSysMetaData" :   {     
            response.getWriter().print(ArrayListStringToJson(getSysMetaData(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3"))));
            break;    
        }
            
        case "getNextNbr" : {
            response.getWriter().print(intToJson(getNextNbr(request.getHeader("param1")))); 
            break;
        }
            
        case "getItemQtyByWarehouseAndLocation" : {
            response.getWriter().print(doubleToJson(getItemQtyByWarehouseAndLocation(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"))));  
            break;
        }
        
        case "getItemCost" : {
            response.getWriter().print(doubleToJson(getItemCost(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"))));  
            break;
        }
            
        case "getItemQOHTotal" : {
            response.getWriter().print(doubleToJson(getItemQOHTotal(request.getHeader("param1"),
                    request.getHeader("param2"))));  
            break; 
        }
            
        case "getOrderItemAllocatedQty" : {
            response.getWriter().print(doubleToJson(getOrderItemAllocatedQty(request.getHeader("param1"),
                    request.getHeader("param2"))));  
            break;    
        }
            
        case "getTableInfo" : {
            response.getWriter().print(HashMapStringIntegerToJson(getTableInfo(new String[]{request.getHeader("param1")})));
            break; 
        }
           
        case "getItemMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getItemMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getLocationMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getLocationMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getWareHouseMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getWareHouseMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getInvMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInvMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getInvMaintInit_min" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInvMaintInit_min(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getBOMInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getBOMInit(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"))));
            break;  
        }
        
        case "getBOMBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3")
               };     
        response.getWriter().print(getBOMBrowseView(it));  
        break;
        }
        
        case "getItemBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromitem"), 
               request.getHeader("toitem"), 
               request.getHeader("fromclass"), 
               request.getHeader("toclass"), 
               request.getHeader("site")
               };     
        response.getWriter().print(getItemBrowseView(it));  
        break;
        }
        
        case "getQPRBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromvend"), 
               request.getHeader("tovend"), 
               request.getHeader("site"),
               request.getHeader("item")
               };     
        response.getWriter().print(getQPRBrowseView(it));  
        break;
        }
        
        case "getInvBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromitem"), 
               request.getHeader("toitem"), 
               request.getHeader("site"), 
               request.getHeader("serial")
               };     
        response.getWriter().print(getInvBrowseView(it));  
        break;
        }
        
        case "getInvValuationBrowseView" : {
        String[] it = new String[]{
               request.getHeader("param1"), 
               request.getHeader("param2"), 
               request.getHeader("param3"), 
               request.getHeader("param4")
               };     
        response.getWriter().print(getInvValuationBrowseView(it));  
        break;
        }
        
        case "getItemRoutingBrowseView" : {
        response.getWriter().print(getItemRoutingBrowseView(request.getHeader("param1")));  
        break;
        }
        
        
        case "getItemDataInit" : {
            response.getWriter().print(HashMapStringStringArrToJson(getItemDataInit(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"))));
            break;  
        }
            
        case "getItemPrice" : {
            response.getWriter().print(arrayToJson(getItemPrice(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"),
                request.getHeader("param5"),
                request.getHeader("param6"))));
            break;
        }
            
        case "getOrderMaintDetailEvent" : {
            response.getWriter().print(arrayToJson(getOrderMaintDetailEvent(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"),
                request.getHeader("param5"),
                request.getHeader("param6"))));
            break;    
        }
        
        case "getItemDetail" : {
            response.getWriter().print(arrayToJson(getItemDetail(request.getHeader("param1"))));
            break;    
        }
        
        case "getRoutingMstrList" : { 
            ArrayList<invData.wf_mstr> x = getRoutingMstrList(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getInMstr" : { 
            ArrayList<invData.in_mstr> x = getInMstr(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getItemComponentDetail" : {
            response.getWriter().print(arrayToJson(getItemComponentDetail(request.getHeader("param1"), request.getHeader("param2"))));
            break;    
        }
        
        case "getComponentByBomOp" : {
            response.getWriter().print(arrayToJson(getComponentByBomOp(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"))));
            break;    
        }
        
        case "getCurrentCost" :   {     
            response.getWriter().print(ArrayListDoubleToJson(getCurrentCost(request.getHeader("param1"))));
            break;    
        }
        
        case "getTotalCostElements" :   {     
            response.getWriter().print(ArrayListDoubleToJson(getTotalCostElements(request.getHeader("param1"))));
            break;    
        }
        
        case "getItemCostElements" :   {     
            response.getWriter().print(ArrayListDoubleToJson(getItemCostElements(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"))));
            break;    
        }
        
        case "getItemImagesFile" : { 
            response.getWriter().print(ArrayListStringToJson(getItemImagesFile(request.getHeader("param1"))));
            break;  
        }
          
        case "getRecentTransByItem" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getRecentTransByItem(request.getHeader("param1"))));
            break;  
        }
        
        case "getInventoryQtyByItem" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInventoryQtyByItem(request.getHeader("param1"))));
            break;  
        }
        
        case "getBOMParentOpElements" : { 
            response.getWriter().print(arrayToJson(getBOMParentOpElements(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "bind_tree_op" : { 
            DefaultMutableTreeNode x = bind_tree_op(request.getHeader("param1"));
            MyNodePOJO pojoRoot = TreeConverter.convertToPOJO(x);
            ObjectMapper objectMapper = new ObjectMapper(); 
            String r = "";
            if (x != null && x.getChildCount() > 0) {
            r = objectMapper.writeValueAsString(pojoRoot);
            } 
            response.getWriter().print(r);
            break;
          }
        
        case "bind_tree" : { 
            DefaultMutableTreeNode x = bind_tree(request.getHeader("param1"), request.getHeader("param2"), null);
            MyNodePOJO pojoRoot = TreeConverter.convertToPOJO(x);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = "";
            if (x != null && x.getChildCount() > 0) {
            r = objectMapper.writeValueAsString(pojoRoot);
            } 
            response.getWriter().print(r);
            break;
          }
        
        case "rebaseCurrentCost" : { 
            rebaseCurrentCost(request.getHeader("param1"),
                    bsParseDouble(request.getHeader("param2")),
                    bsParseDouble(request.getHeader("param3")),
                    bsParseDouble(request.getHeader("param4")));
            break;  
        }
        
        case "updateCurrentItemCost" : { 
            updateCurrentItemCost(request.getHeader("param1"));
            break;  
        }
        
        case "resetBOMDefault" : { 
            resetBOMDefault(request.getHeader("param1"));
            break;  
        }
        
        case "deleteZeroInventoryRecs" : { 
            deleteZeroInventoryRecs();
            break;  
        }
        
        case "inventoryAdjustmentTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
          
            invData.tran_mstr tm = objectMapper.readValue(ca[0], invData.tran_mstr.class); 
            invData.in_mstr in = objectMapper.readValue(ca[1], invData.in_mstr.class); 
            fglData.gl_pair gv = objectMapper.readValue(ca[2], fglData.gl_pair.class); 
            response.getWriter().print(arrayToJson(invData.inventoryAdjustmentTransaction(tm, in, gv)));  
            break;
        }
        
        case "addItemMasterMass" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<String> sdarray = objectMapper.readValue(sb.toString(), ArrayList.class);
            response.getWriter().print(arrayToJson(addItemMasterMass(sdarray, request.getHeader("param1"))));
            break;
          }
        
        case "addUpdateINVCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.inv_ctrl x = objectMapper.readValue(sb.toString(), invData.inv_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateINVCtrl(x)));
            break;
          }
        
        case "getItemPriceFromCust" : {
            response.getWriter().print(arrayToJson(getItemPriceFromCust(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"))));  
            break;
        }
        
        case "getItemPriceFromVend" : {
            response.getWriter().print(arrayToJson(getItemPriceFromVend(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"))));  
            break;
        }
        
        case "getInvRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getInvRptPickerData(x));  
        break;
        }
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServINV for id: " + id);    
            
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


