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

import static bsmf.MainFrame.bslog;
import com.blueseer.adm.admData;
import com.blueseer.edi.apiUtils;
import static com.blueseer.edi.ediData.isFTPidEnabled;
import static com.blueseer.edi.ediData.isValidAS2id;
import static com.blueseer.edi.ediData.isValidFTPid;
import static com.blueseer.utl.OVData.canReadDB;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.mail.smime.SMIMEException;
import org.quartz.DisallowConcurrentExecution;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author terryva
 */
@DisallowConcurrentExecution
public class jobFTP implements Job {
    public void execute(JobExecutionContext context)
	throws JobExecutionException {
        
        
        // register last run event with cron_mstr...
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        
        // check for DB connectivity
        if (! canReadDB()) {
           System.out.println("cannot read DB!!!: " + this.getClass().getName() +  " run time: " + now); 
           return;
        }
        
        
        admData.updateCronLastRun(context.getJobDetail().getKey().getName(), now);
        
        // The ftpID must be passed to this job in order to schedule push FTP comm of this id.
        // must be passed in 'param' key with 'value' = ftpid of ftp_mstr table
              
            
                
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String ftpid = dataMap.getString("param");
                
                System.out.println("jobFTP firing system method: " + ftpid + " run time: " + now);
                
                if (! ftpid.isBlank() && isValidFTPid(ftpid)) {
                    if (isFTPidEnabled(ftpid)) {
                        try {   
                            admData.runFTPClient(ftpid);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    } else {
                       System.out.println("FTP ID is disabled: " + ftpid + " length: " + ftpid.length());
                    }
                    
                } else {
                    System.out.println("Invalid or blank FTP ID: " + ftpid + " length: " + ftpid.length());
                }
                
			
		
	}
}
