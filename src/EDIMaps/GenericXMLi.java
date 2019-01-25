/*
   Copyright 2005-2017 Terry Evans Vaughn ("VCSCode").

With regard to the Blueseer Software:

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

For all third party components incorporated into the GitLab Software, those 
components are licensed under the original license provided by the owner of the 
applicable component.

 */

package EDIMaps;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDI;
import com.blueseer.edi.EDI.*;
import com.blueseer.utl.BlueSeerUtils;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import bsmf.MainFrame;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 *
 * @author vaughnte
 */
public class GenericXMLi {
        public static void MapXMLdata(File fXmlFile, String[] control, String senderid) throws SAXException, IOException, ParserConfigurationException {
        com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     ArrayList<edi850> e = new ArrayList<edi850>();
     String isaSenderID = "lou";
     String isaReceiverID = "avm";
     String gsSenderID = "lou";
     String gsReceiverID = "avm";
     String isaCtrlNum = "";
     String isaDate = "";
     String doctype = "xml";
     String docid = "";
     
        String po = "";
        String billto = "";
        String part = "";
        String uom = "";
        double discount = 0;
        double listprice = 0;
        double netprice = 0;
        boolean shiploop = false;
        
        DecimalFormat df = new DecimalFormat("#.0000");
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
        //optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();
        e.add(new edi850(isaSenderID, isaReceiverID, gsSenderID, gsReceiverID,
                              isaCtrlNum, isaDate, doctype, docid));
        int i = 0;
         NodeList nList = doc.getElementsByTagName("header");
        
         for (int temp = 0; temp < nList.getLength(); temp++) {
          Node nNode = nList.item(temp);
          if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              // create e record for each header
               
              Element eElement = (Element) nNode;
              billto = eElement.getElementsByTagName("customerid").item(0).getTextContent();
              po = eElement.getElementsByTagName("purchaseordernumber").item(0).getTextContent();
          
              
              e.get(i).setPO(po);
              e.get(i).setOVBillTo(billto);
              e.get(i).setPODate(eElement.getElementsByTagName("orderdate").item(0).getTextContent());
              e.get(i).setDueDate(eElement.getElementsByTagName("duedate").item(0).getTextContent());
              
          }
         }
             
          
          nList = doc.getElementsByTagName("address");
         for (int temp = 0; temp < nList.getLength(); temp++) {
          Node nNode = nList.item(temp);
          if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
              e.get(i).setShipToName(eElement.getElementsByTagName("addressname").item(0).getTextContent());
              e.get(i).setShipToLine1(eElement.getElementsByTagName("addressline1").item(0).getTextContent());
              e.get(i).setShipToLine2(eElement.getElementsByTagName("addressline2").item(0).getTextContent());
              e.get(i).setShipToLine3(eElement.getElementsByTagName("addressline3").item(0).getTextContent());
              e.get(i).setShipToCity(eElement.getElementsByTagName("addresscity").item(0).getTextContent());
              e.get(i).setShipToState(eElement.getElementsByTagName("addressstate").item(0).getTextContent());
              e.get(i).setShipToZip(eElement.getElementsByTagName("addresszip").item(0).getTextContent());
              e.get(i).setShipToCountry(eElement.getElementsByTagName("addresscountry").item(0).getTextContent());
          }
         }
          
           nList = doc.getElementsByTagName("item");
         for (int temp = 0; temp < nList.getLength(); temp++) {
          Node nNode = nList.item(temp);
          if (nNode.getNodeType() == Node.ELEMENT_NODE) { 
              Element eElement = (Element) nNode;
              e.get(i).setDetLine(i,eElement.getElementsByTagName("linenumber").item(0).getTextContent());
              e.get(i).setDetCustItem(i,eElement.getElementsByTagName("partnumber").item(0).getTextContent());
              e.get(i).setDetSku(i,eElement.getElementsByTagName("partnumber").item(0).getTextContent());
              e.get(i).setDetRef(i,"");
              e.get(i).setDetPO(i,po);
              e.get(i).setDetQty(i,eElement.getElementsByTagName("qtyordered").item(0).getTextContent());
              
              part = OVData.getPartFromCustCItem(billto, eElement.getElementsByTagName("partnumber").item(0).getTextContent());  
                  e.get(i).setDetItem(i,part);
                  uom = OVData.getUOMByPart(part);
                  e.get(i).setDetUOM(i,uom); 
                listprice = OVData.getPartPriceFromCust(billto, part, uom, OVData.getCustCurrency(billto));
                  e.get(i).setDetListPrice(i,df.format(listprice));
                discount = OVData.getPartDiscFromCust(billto);
                  e.get(i).setDetDisc(i,df.format(discount));
                netprice = OVData.getNetPriceFromListAndDisc(listprice, discount);
                  e.get(i).setDetNetPrice(i,df.format(netprice));
          }
         }
           
      
  
  /* Load Sales Order(s) */
         EDI.createOrderFromXML(e, control);
   
        }
}

         



