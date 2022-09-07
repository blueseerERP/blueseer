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
import static com.blueseer.edi.ediData.isValidAS2id;
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

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author terryva
 */
public class jobAS2 implements Job {
    public void execute(JobExecutionContext context)
	throws JobExecutionException {
        
        
        // register last run event with cron_mstr...
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        admData.updateCronLastRun(context.getJobDetail().getKey().getName(), now);
        
        // The as2ID must be passed to this job in order to schedule push AS2 comm of this id.
        // must be passed in 'param' key with 'value' = as2id of as2_mstr table
        
        // NOTE:  this job only 'pushes' AS2 transmissions from source directory of as2id
        // inbounds are pushed from partner...and must be collected/processed via another mechanism
                
                
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String as2id = dataMap.getString("param");
                
                if (! as2id.isBlank() && isValidAS2id(as2id)) {
                    try {   
                        apiUtils.postAS2(as2id, false);
                    } catch (URISyntaxException ex) {
                        bslog(ex);
                    } catch (IOException ex) {
                        bslog(ex);
                    } catch (CertificateException ex) {
                        bslog(ex);
                    } catch (NoSuchProviderException ex) {
                        bslog(ex);
                    } catch (KeyStoreException ex) {
                        bslog(ex);
                    } catch (NoSuchAlgorithmException ex) {
                        bslog(ex);
                    } catch (UnrecoverableKeyException ex) {
                        bslog(ex);
                    } catch (CMSException ex) {
                        bslog(ex);
                    } catch (SMIMEException ex) {
                        bslog(ex);
                    } catch (Exception ex) {
                        bslog(ex);
                    }
                } else {
                    System.out.println("Invalid or blank AS2 ID: " + as2id + " length: " + as2id.length());
                }
                
			
		
	}
}
