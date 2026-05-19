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

import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.SetStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.BlueSeerUtils.parseDate;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.UpdateInventoryLocationTransfer;
import static com.blueseer.utl.OVData.addItemCostRec;
import static com.blueseer.utl.OVData.addMenuToAllUsers;
import static com.blueseer.utl.OVData.addMenuToUser;
import static com.blueseer.utl.OVData.addSysMetaDataNoUnique;
import static com.blueseer.utl.OVData.addUpdateSysMeta;
import static com.blueseer.utl.OVData.autoclock;
import static com.blueseer.utl.OVData.copyUserPerms;
import static com.blueseer.utl.OVData.createMRPByLevel;
import static com.blueseer.utl.OVData.createMRPZeroLevel;
import static com.blueseer.utl.OVData.createPlanFromDemand;
import static com.blueseer.utl.OVData.createPlanFromFcst;
import static com.blueseer.utl.OVData.createPlanFromServiceOrder;
import static com.blueseer.utl.OVData.deleteAllMRP;
import static com.blueseer.utl.OVData.deleteMenuToAllUsers;
import static com.blueseer.utl.OVData.deleteMenuToUser;
import static com.blueseer.utl.OVData.deleteSysMeta;
import static com.blueseer.utl.OVData.getChartRptPickerData;
import static com.blueseer.utl.OVData.getClockCodes;
import static com.blueseer.utl.OVData.getCodeDescByCode;
import static com.blueseer.utl.OVData.getCodeKeyByCode;
import static com.blueseer.utl.OVData.getCodeMstrKeyList;
import static com.blueseer.utl.OVData.getCodeMstrValueList;
import static com.blueseer.utl.OVData.getCodeValueByCode;
import static com.blueseer.utl.OVData.getCodeValueByCodeKey;
import static com.blueseer.utl.OVData.getExchangeBaseValue;
import static com.blueseer.utl.OVData.getExchangeRate;
import static com.blueseer.utl.OVData.getJasperByGroup;
import static com.blueseer.utl.OVData.getJasperFuncByTitle;
import static com.blueseer.utl.OVData.getMenuRecs;
import static com.blueseer.utl.OVData.getMenusAsTree;
import static com.blueseer.utl.OVData.getMenusOfUsersListArray;
import static com.blueseer.utl.OVData.getNextLevelpsmstr;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getOperationsByItem;
import static com.blueseer.utl.OVData.getProdLineInvAcct;
import static com.blueseer.utl.OVData.getRollCostInfo;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.getSystemAttachmentDirectory;
import static com.blueseer.utl.OVData.getTableInfo;
import static com.blueseer.utl.OVData.getTaxAmtApplicableByItem;
import static com.blueseer.utl.OVData.getTaxPercentElementsApplicableByItem;
import static com.blueseer.utl.OVData.getTermsResults;
import static com.blueseer.utl.OVData.getUsersOfMenusList;
import static com.blueseer.utl.OVData.getWeekNbrByDate;
import static com.blueseer.utl.OVData.getmenutree;
import static com.blueseer.utl.OVData.getpsmstrcompSerialized;
import static com.blueseer.utl.OVData.getpsmstrparents;
import static com.blueseer.utl.OVData.getpsmstrparents2;
import static com.blueseer.utl.OVData.getzerolevelpsmstr;
import static com.blueseer.utl.OVData.isAutoPost;
import static com.blueseer.utl.OVData.isClockScanCard;
import static com.blueseer.utl.OVData.isDuplicateNavCode;
import static com.blueseer.utl.OVData.isGLPeriodClosed;
import static com.blueseer.utl.OVData.isValidBank;
import static com.blueseer.utl.OVData.isValidCurrency;
import static com.blueseer.utl.OVData.isValidCustPriceRecordExists;
import static com.blueseer.utl.OVData.isValidCustShipTo;
import static com.blueseer.utl.OVData.isValidCustomer;
import static com.blueseer.utl.OVData.isValidFreightOrderNbr;
import static com.blueseer.utl.OVData.isValidGLAcct;
import static com.blueseer.utl.OVData.isValidGLcc;
import static com.blueseer.utl.OVData.isValidItem;
import static com.blueseer.utl.OVData.isValidLocation;
import static com.blueseer.utl.OVData.isValidOperation;
import static com.blueseer.utl.OVData.isValidOrder;
import static com.blueseer.utl.OVData.isValidPanel;
import static com.blueseer.utl.OVData.isValidPrinter;
import static com.blueseer.utl.OVData.isValidProdLine;
import static com.blueseer.utl.OVData.isValidProfile;
import static com.blueseer.utl.OVData.isValidRouting;
import static com.blueseer.utl.OVData.isValidShift;
import static com.blueseer.utl.OVData.isValidShipper;
import static com.blueseer.utl.OVData.isValidSite;
import static com.blueseer.utl.OVData.isValidTerms;
import static com.blueseer.utl.OVData.isValidUOM;
import static com.blueseer.utl.OVData.isValidUOMConversion;
import static com.blueseer.utl.OVData.isValidVendAddr;
import static com.blueseer.utl.OVData.isValidVendPriceRecordExists;
import static com.blueseer.utl.OVData.isValidVendor;
import static com.blueseer.utl.OVData.isValidWarehouse;
import static com.blueseer.utl.OVData.isValidWorkCenter;
import static com.blueseer.utl.OVData.setStandardCosts;
import static com.blueseer.utl.OVData.simulateCost;
import static com.blueseer.utl.OVData.sourceOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 *
 * @author terryva
 */
public class dataServOV extends HttpServlet {
 
    
        
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
        
        case "getMenusAsTree" : { 
            DefaultMutableTreeNode x = getMenusAsTree(request.getHeader("param1"), request.getHeader("param2"));
            ObjectMapper objectMapper = new ObjectMapper(); 
            String r = "";
            if (x != null && x.getChildCount() > 0) {
            r = objectMapper.writeValueAsString(x);
            } 
            System.out.println(r);
            response.getWriter().print(r);
            break;
          }
        
        case "loadTranHistByTable" : {
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
            LinkedHashMap<String,String[]> lhm = om.readValue(ca[1], new TypeReference<LinkedHashMap<String,String[]>>() {});
            response.getWriter().print(boolToJson(OVData.loadTranHistByTable(al, lhm))); 
            break;
        }
        
        case "CommitSchedules" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            ArrayList<String[]> al = om.readValue(sb.toString(), new TypeReference<ArrayList<String[]>>() {});
            response.getWriter().print(intToJson(OVData.CommitSchedules(al, request.getHeader("param1"))));  
            break;
        }
        
        
        case "TRHistIssDiscrete" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] arr = om.readValue(sb.toString(), String[].class);
            response.getWriter().print(boolToJson(OVData.TRHistIssDiscrete(arr))); 
            break;
        }
        
        case "updateItemlevel" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            ArrayList<String> al = om.readValue(ca[0], new TypeReference<ArrayList<String>>() {});
            int level = bsParseInt(ca[1]);
            OVData.updateItemlevel(al, level);    
            break;
        }
        
        case "getCodeMstrValueList" :        
            response.getWriter().print(ArrayListStringToJson(getCodeMstrValueList(request.getHeader("code"))));
            break;
            
        case "getCodeMstrKeyList" :        
            response.getWriter().print(ArrayListStringToJson(getCodeMstrKeyList(request.getHeader("code"))));
            break;
            
        case "getClockCodes" :        
            response.getWriter().print(ArrayListStringToJson(getClockCodes()));
            break;     
            
        case "getOperationsByItem" :        
            response.getWriter().print(ArrayListStringToJson(getOperationsByItem(request.getHeader("param1"))));
            break; 
            
            
        case "getTaxPercentElementsApplicableByItem" :        
            response.getWriter().print(ArrayListStringArrayToJson(getTaxPercentElementsApplicableByItem(request.getHeader("param1"))));
            break;
            
        case "getpsmstrcompSerialized" :        
            response.getWriter().print(ArrayListStringToJson(getpsmstrcompSerialized(request.getHeader("param1"))));
            break;   
            
        case "getpsmstrparents" :        
            response.getWriter().print(SetStringToJson(getpsmstrparents(request.getHeader("param1"))));
            break;   
            
        case "getpsmstrparents2" :        
            response.getWriter().print(SetStringToJson(getpsmstrparents2(request.getHeader("param1"))));
            break;     
            
        case "getmenutree" :        
            response.getWriter().print(ArrayListStringToJson(getmenutree(request.getHeader("param1"))));
            break;
            
        case "getSysMetaData" :        
            response.getWriter().print(ArrayListStringToJson(getSysMetaData(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3"))));
            break; 
            
        case "getSysMetaDataArray" :        
            response.getWriter().print(ArrayListStringArrayToJson(getSysMetaData(request.getHeader("param1"))));
            break;
            
        case "getJasperByGroup" :        
            response.getWriter().print(ArrayListStringArrayToJson(getJasperByGroup(request.getHeader("param1"))));
            break;    
            
        case "getSysMetaData2Param" :        
            response.getWriter().print(ArrayListStringArrayToJson(getSysMetaData(request.getHeader("param1"), request.getHeader("param2"))));
            break;    
            
        case "getMenuRecs" :        
            response.getWriter().print(ArrayListStringArrayToJson(getMenuRecs()));
            break;    
            
        case "getSysMetaValue" :        
            response.getWriter().print(getSysMetaValue(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3")));
            break; 
            
        case "getJasperFuncByTitle" :        
            response.getWriter().print(getJasperFuncByTitle(request.getHeader("param1"),request.getHeader("param2")));
            break;    
            
        case "getCodeValueByCodeKey" :        
            response.getWriter().print(getCodeValueByCodeKey(request.getHeader("param1"),request.getHeader("param2")));
            break;    
            
        case "getCodeDescByCode" :        
            response.getWriter().print(getCodeDescByCode(request.getHeader("param1")));
            break; 
        
        case "getCodeKeyByCode" :        
            response.getWriter().print(getCodeKeyByCode(request.getHeader("param1")));
            break; 
           
        case "getCodeValueByCode" :        
            response.getWriter().print(getCodeValueByCode(request.getHeader("param1")));
            break;     
            
        case "getExchangeRate" :        
            response.getWriter().print(getExchangeRate(request.getHeader("param1"),request.getHeader("param2")));
            break;   
            
        case "getSystemAttachmentDirectory" :        
            response.getWriter().print(getSystemAttachmentDirectory());
            break;    
            
        case "getNextNbr" : 
            response.getWriter().print(intToJson(getNextNbr(request.getHeader("param1")))); 
            break;
            
        case "deleteAllMRP" : {
            response.getWriter().print(intToJson(deleteAllMRP(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")))); 
            break;    
        }
        
        case "createMRPByLevel" : {
            response.getWriter().print(intToJson(createMRPByLevel(bsParseInt(request.getHeader("param1")),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;    
        }
        
        case "createMRPZeroLevel" : {
            response.getWriter().print(intToJson(createMRPZeroLevel(request.getHeader("param1")))); 
            break;    
        }
        
        case "createPlanFromDemand" : {
            response.getWriter().print(intToJson(createPlanFromDemand(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")))); 
            break;    
        }
         
        case "createPlanFromFcst" : {
            response.getWriter().print(intToJson(createPlanFromFcst(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;    
        }
        
        
        case "getExchangeBaseValue" : {
            response.getWriter().print(doubleToJson(getExchangeBaseValue(request.getHeader("param1"),
                    request.getHeader("param2"),
                    bsParseDouble(request.getHeader("param3"))))); 
            break;    
        }
        
        case "simulateCost" : {
            response.getWriter().print(doubleToJson(simulateCost(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    bsParseDouble(request.getHeader("param4")),
                    bsParseDouble(request.getHeader("param5")),
                    bsParseDouble(request.getHeader("param6")),
                    bsParseDouble(request.getHeader("param7")),
                    bsParseDouble(request.getHeader("param8")),
                    bsParseDouble(request.getHeader("param9")),
                    bsParseDouble(request.getHeader("param10")),
                    bsParseDouble(request.getHeader("param11")),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param12"))))); 
            break;    
        }
        
        
        case "getWeekNbrByDate" :        
            response.getWriter().print(ArrayListStringToJson(getWeekNbrByDate(request.getHeader("param1"), request.getHeader("param2"))));
            break; 
            
        case "getRollCostInfo" :        
            response.getWriter().print(ArrayListStringToJson(getRollCostInfo(request.getHeader("param1"))));
            break;    
            
        case "getzerolevelpsmstr" :        
            response.getWriter().print(ArrayListStringToJson(getzerolevelpsmstr()));
            break;
            
        case "getNextLevelpsmstr" :        
            response.getWriter().print(ArrayListStringToJson(getNextLevelpsmstr(BlueSeerUtils.bsParseInt(request.getHeader("param1")))));
            break;    
        
        case "addUpdateSysMeta" : {
        response.getWriter().print(boolToJson(addUpdateSysMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        }
        
        case "deleteSysMeta" : {
        response.getWriter().print(boolToJson(deleteSysMeta(request.getHeader("param1"), 
                request.getHeader("param2"), 
                request.getHeader("param3"), 
                request.getHeader("param4")))); 
        break;
        }
        
        case "sourceOrder" :
            response.getWriter().print(arrayToJson(sourceOrder(bsParseInt(request.getHeader("param1")))));
        break; 
        
        case "getTermsResults" :
            response.getWriter().print(arrayToJson(getTermsResults(parseDate(request.getHeader("param1")), request.getHeader("param2"))));
        break; 
            
        case "isDuplicateNavCode" : {
        response.getWriter().print(boolToJson(isDuplicateNavCode(request.getHeader("param1"), request.getHeader("param2")))); 
        break;
        }
            
        case "isAutoPost" : {
        response.getWriter().print(boolToJson(isAutoPost())); 
        break;
        }    
        
        case "isValidItem" : {
        response.getWriter().print(boolToJson(isValidItem(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidUOM" : {
        response.getWriter().print(boolToJson(isValidUOM(request.getHeader("param1")))); 
        break;
        }
          
        case "isValidGLAcct" : {
        response.getWriter().print(boolToJson(isValidGLAcct(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidGLcc" : {
        response.getWriter().print(boolToJson(isValidGLcc(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidCurrency" : {
        response.getWriter().print(boolToJson(isValidCurrency(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidBank" : {
        response.getWriter().print(boolToJson(isValidBank(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidProfile" : {
        response.getWriter().print(boolToJson(isValidProfile(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidTerms" : {
        response.getWriter().print(boolToJson(isValidTerms(request.getHeader("param1")))); 
        break;
        }
        
        case "isValidCustShipTo" : {
            response.getWriter().print(boolToJson(isValidCustShipTo(request.getHeader("param1"), request.getHeader("param2")))); 
            break; 
        }
            
        case "isValidPrinter" : {
            response.getWriter().print(boolToJson(isValidPrinter(request.getHeader("param1")))); 
            break; 
        }
            
        case "isValidPanel" : {
            response.getWriter().print(boolToJson(isValidPanel(request.getHeader("param1")))); 
            break;  
        }
            
        case "isValidShipper" : {
            response.getWriter().print(boolToJson(isValidShipper(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidOrder" : {
            response.getWriter().print(boolToJson(isValidOrder(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidFreightOrderNbr" : {
            response.getWriter().print(boolToJson(isValidFreightOrderNbr(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidSite" : {
            response.getWriter().print(boolToJson(isValidSite(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidCustomer" : {
            response.getWriter().print(boolToJson(isValidCustomer(request.getHeader("param1")))); 
            break;    
        }
          
        case "isValidVendor" : {
            response.getWriter().print(boolToJson(isValidVendor(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidVendAddr" : {
            response.getWriter().print(boolToJson(isValidVendAddr(request.getHeader("param1"), request.getHeader("param2")))); 
            break; 
        }
        
        case "isValidLocation" : {
            response.getWriter().print(boolToJson(isValidLocation(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidWarehouse" : {
            response.getWriter().print(boolToJson(isValidWarehouse(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidProdLine" : {
            response.getWriter().print(boolToJson(isValidProdLine(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidRouting" : {
            response.getWriter().print(boolToJson(isValidRouting(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidWorkCenter" : {
            response.getWriter().print(boolToJson(isValidWorkCenter(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidShift" : {
            response.getWriter().print(boolToJson(isValidShift(request.getHeader("param1")))); 
            break;    
        }
        
        case "isValidOperation" : {
            response.getWriter().print(boolToJson(isValidOperation(request.getHeader("param1"), request.getHeader("param2")))); 
            break;    
        }
        
        case "isValidUOMConversion" : {
            response.getWriter().print(boolToJson(isValidUOMConversion(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3")))); 
            break;    
        }
        
        case "isValidVendPriceRecordExists" : {
            response.getWriter().print(boolToJson(isValidVendPriceRecordExists(request.getHeader("param1"), 
                    request.getHeader("param2"), 
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;    
        }
        
        case "isValidCustPriceRecordExists" : {
            response.getWriter().print(boolToJson(isValidCustPriceRecordExists(request.getHeader("param1"), 
                    request.getHeader("param2"), 
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;    
        }
        
        case "getTaxAmtApplicableByItem" : {
            response.getWriter().print(doubleToJson(getTaxAmtApplicableByItem(request.getHeader("param1"),
                    bsParseDouble(request.getHeader("param2")))));   
            break;
        }
            
        case "createPlanFromServiceOrder" : {
            response.getWriter().print(doubleToJson(createPlanFromServiceOrder(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param4")))));   
            break;    
        }
            
        case "getTableInfo" : 
            response.getWriter().print(HashMapStringIntegerToJson(getTableInfo(new String[]{request.getHeader("param1")})));
            break;  
            
        case "getTaxPercentElementsApplicableByTaxCode" :        
            response.getWriter().print(ArrayListStringArrayToJson(OVData.getTaxPercentElementsApplicableByTaxCode(request.getHeader("param1"))));
            break;   
            
        case "addSysMetaDataNoUnique" : {
        response.getWriter().print(boolToJson(addSysMetaDataNoUnique(request.getHeader("param1"), 
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4")))); 
        break;   
        }
        
        case "autoclock" : {
        response.getWriter().print(boolToJson(autoclock(bsParseInt(request.getHeader("param1"))))); 
        break;   
        }
        
        case "isClockScanCard" : {
        response.getWriter().print(boolToJson(isClockScanCard())); 
        break;   
        }
        
        case "getProdLineInvAcct" : {       
            response.getWriter().print(getProdLineInvAcct(request.getHeader("param1")));
            break; 
        }
            
        case "addItemCostRec" : {
            response.getWriter().print(arrayToJson(addItemCostRec(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                bsParseDouble(request.getHeader("param4")),
                bsParseDouble(request.getHeader("param5")),
                bsParseDouble(request.getHeader("param6")),
                bsParseDouble(request.getHeader("param7"))))); 
            break;
        } 
        
        case "setStandardCosts" : {
            response.getWriter().print(arrayToJson(setStandardCosts(
                request.getHeader("param1"),
                request.getHeader("param2")))); 
            break;
        } 
        
        case "isGLPeriodClosed" : {
        response.getWriter().print(boolToJson(isGLPeriodClosed(request.getHeader("param1")))); 
        break;
        }
        
        
        case "getMenusOfUsersListArray" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getMenusOfUsersListArray(request.getHeader("param1"))));
            break;  
        }
        
        case "getUsersOfMenusList" : { 
            response.getWriter().print(ArrayListStringToJson(getUsersOfMenusList(request.getHeader("param1"))));
            break;  
        }
        
        case "copyUserPerms" : { 
            copyUserPerms(request.getHeader("param1"), request.getHeader("param2"));
            break;  
        }
        
        case "addMenuToAllUsers" : { 
            addMenuToAllUsers(request.getHeader("param1"), BlueSeerUtils.ConvertStringToBool(request.getHeader("param2")));
            break;  
        }
        
        case "addMenuToUser" : { 
            response.getWriter().print(addMenuToUser(request.getHeader("param1"), request.getHeader("param2"), BlueSeerUtils.ConvertStringToBool(request.getHeader("param3"))));
            break;  
        }
        
        case "deleteMenuToUser" : { 
            response.getWriter().print(deleteMenuToUser(request.getHeader("param1"), request.getHeader("param2")));
            break;  
        }
        
        case "deleteMenuToAllUsers" : { 
            deleteMenuToAllUsers(request.getHeader("param1"));
            break;  
        }
        
        case "UpdateInventoryLocationTransfer" : { 
            UpdateInventoryLocationTransfer(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    bsParseDouble(request.getHeader("param7")));
            break;  
        }
        
        case "getChartRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getChartRptPickerData(x));    
        break;
        } 
        
        
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServOV for id: " + id);    
             
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
