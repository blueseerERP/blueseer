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
package utilities;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import com.blueseer.adm.admData.cron_mstr;
import com.blueseer.crn.jobWD;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl; 
/**
 *
 * @author terryva
 */
public class cronServer {
    public static Scheduler scheduler = null;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	//	JobDetailImpl job = new JobDetailImpl();  
    //	job.setName("dummyJobName");
    //	job.setJobClass(HelloJob.class);
    	
		
	bsmf.MainFrame.setConfig();	
	tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
        
	// first...build and trigger WatchDog class for cron job updates	
        JobDetail jobwd = JobBuilder.newJob(jobWD.class)
            .withIdentity("jobWD", "groupWD")
            .build();
    	CronTriggerImpl triggerWD = new CronTriggerImpl();
    	triggerWD.setName("triggerWD");
    	triggerWD.setCronExpression("* * * * * ?");  // set to run every 1 minute...could also use 0/1 * * * * ?
    	
        //schedule it
    	scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	scheduler.scheduleJob(jobwd, triggerWD);
        
        
        // now...deploy all 'enabled' tasks in cron_mstr
	ArrayList<cron_mstr> list = admData.getCronMstrEnabled();
        if (list.size() > 0) {
            for (cron_mstr cm : list) {
                try {
                    JobKey jk = new JobKey(cm.cron_jobid(), cm.cron_group());
                    if (! scheduler.checkExists(jk)) {
                     continue;
                    }
                    scheduler.deleteJob(jk);
                    Class cls = Class.forName(cm.cron_prog());
                    JobDetail job = JobBuilder.newJob(cls)
                            .withIdentity(jk)
                            .usingJobData("param", cm.cron_param())
                            .build();
                    CronTriggerImpl trigger = new CronTriggerImpl();
                    trigger.setName(cm.cron_jobid()); 
                    trigger.setCronExpression(cm.cron_expression());  
                    scheduler.scheduleJob(job, trigger);
                } catch (SchedulerException ex) {
                    Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                            Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    	
        }	
}
