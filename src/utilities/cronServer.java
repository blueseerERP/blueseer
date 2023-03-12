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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    //public static Scheduler scheduler = null;
	
	public static void main(String[] args) throws Exception {
	
        Scheduler scheduler = null; 
		
	bsmf.MainFrame.setConfig();	
	tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
        
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
       
        Path path = FileSystems.getDefault().getPath("conf/quartz.properties");
        if (! Files.exists(path)) {
            scheduler = new StdSchedulerFactory().getScheduler();
        } else {
            StdSchedulerFactory sf = new StdSchedulerFactory("conf/quartz.properties");
            scheduler = sf.getScheduler();  
        }
       
        
        // ...deploy all 'enabled' tasks in cron_mstr
	ArrayList<cron_mstr> list = admData.getCronMstrEnabled();
        ArrayList<String> jobids = new ArrayList<String>();
        
        System.out.println("Number of initial cron jobs: " + list.size() + " time: " + now );
        if (list.size() > 0) {
            for (cron_mstr cm : list) {
                jobids.add(cm.cron_jobid());
                try {
                    JobKey jk = new JobKey(cm.cron_jobid(), cm.cron_group());
                    System.out.println("JobKey: " + jk + " time: " + now);
                    if (scheduler.checkExists(jk)) {
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
                    System.out.println("starting cron job: " + jk + " time: " + now );
                    scheduler.scheduleJob(job, trigger);
                    
                } catch (SchedulerException ex) {
                    Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                            Logger.getLogger(jobWD.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            admData.updateCronJobIDMulti(jobids, "0");
            
        }
    	
        
        // now...build and trigger WatchDog class for cron job updates	
        JobDetail jobwd = JobBuilder.newJob(jobWD.class)
            .withIdentity("jobWD", "groupWD")
            .build();
    	CronTriggerImpl triggerWD = new CronTriggerImpl();
    	triggerWD.setName("triggerWD");
    	triggerWD.setCronExpression("0 0/1 * * * ?");  // set to run every 1 minute
        scheduler.scheduleJob(jobwd, triggerWD);
        
        
    	scheduler.start();
        
        
        }	
}
