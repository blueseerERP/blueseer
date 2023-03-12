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
package com.blueseer.crn;


import com.blueseer.edi.EDI;
import static com.blueseer.edi.EDI.packageEnvelopes;
import com.blueseer.edi.EDILoad;
import com.blueseer.fgl.fglData;
import com.blueseer.utl.EDData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author terryva
 */
public class jobSys implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
      
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));        
                
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String param = dataMap.getString("param");
        System.out.println("jobSys firing system method: " + param + " run time: " + now);
                
        switch (param) {
            case "postgl":  {
                fglData.PostGL();
                break;
            }
            
            case "edi":  {
                EDILoad.runTranslation(null);
                break;
            }
                
            case "edi810o":  {
                ArrayList<String> invoices = EDData.getEDIInvoicesAutoExport();
                exportInvoices(invoices);
                break;
            }
                
            case "edi856o": {
                ArrayList<String> asns = EDData.getEDIASNsAutoExport();
                exportASNs(asns);
                break;
            }
            
            default: 
                System.out.println("Unkown paramter: " + param + " time: " + now);
        }
        
        now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));  
        System.out.println("jobSys completing system method: " + param + " end time: " + now);
        
        dataMap = null;
        now = null;
        param = null;
			
		
	}
    
    public void exportInvoices(ArrayList<String> list) {
        int l_error;
        for (String x : list) {
          l_error = EDI.Create810(x); 
          if (l_error == 0) { // success
            EDData.updateEDIInvoiceStatus(x);  // set export success 
          } 
        }
        packageEnvelopes();
    }
    
    public void exportASNs(ArrayList<String> list) {
        int l_error;
        for (String x : list) {
          l_error = EDI.Create856(x); 
          if (l_error == 0) { // success
            EDData.updateEDIASNStatus(x);  // set export success 
          } 
        }
        packageEnvelopes();
    }
    
}
