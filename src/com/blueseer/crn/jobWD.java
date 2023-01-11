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
import com.blueseer.adm.admData.cron_mstr;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;

/**
 *
 * @author terryva
 */
public class jobWD implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Scheduler myscheduler = context.getScheduler();
        
	String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")); 		
	/*			
        JobKey key = context.getJobDetail().getKey();  
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String prog = dataMap.getString("prog");
	*/
        
        
        // let's get all cron tasks that have the mod flag set and are enabled
        ArrayList<cron_mstr> list = admData.getCronMstrMod();
        
        // now loop through list and update trigger of each task in list
        for (cron_mstr cm : list) {
            try {
                JobKey jk = new JobKey(cm.cron_jobid(), cm.cron_group());
                
                System.out.println("cron job has been modified: " + jk.getName() + " time: " + now);
                
                // delete if it already exists
                if (myscheduler.checkExists(jk)) {
                    myscheduler.deleteJob(jk);
                    System.out.println("deleting old cron job: " + jk.getName() + " time: " + now);
                }
                
                // if not enabled...then skip it
                if (cm.cron_enabled().equals("0")) {
                    System.out.println("cron job has been disabled: " + jk.getName() + " time: " + now);
                    admData.updateCronJobID(cm.cron_jobid(), "0");
                    continue;
                }
                    Class cls = Class.forName(cm.cron_prog());
                    JobDetail job = JobBuilder.newJob(cls)
                            .withIdentity(jk)
                            .usingJobData("param", cm.cron_param())
                            .build();
                    CronTriggerImpl trigger = new CronTriggerImpl();
                    trigger.setName(cm.cron_jobid()); 
                    trigger.setCronExpression(cm.cron_expression());  
                    myscheduler.scheduleJob(job, trigger);
                    System.out.println("adding new cron job: " + jk.getName() + " time: " + now);
                
                admData.updateCronJobID(cm.cron_jobid(), "0");
                
            } catch (SchedulerException ex) {
                bslog(ex);
            } catch (ParseException ex) {
                bslog(ex);
            } catch (ClassNotFoundException ex) {
                bslog(ex);
            }
        }
        
        System.out.println("List of current Jobs and Triggers: " + " time: " + now);
        
        try {
            
            for (String groupName : myscheduler.getJobGroupNames()) {
                for (JobKey jobKey : myscheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    List<Trigger> triggers = (List<Trigger>) myscheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " Next Run: " + nextFireTime);
                }
            }
        } catch (SchedulerException ex) {
            bslog(ex);
        }
        
       
    }

}
