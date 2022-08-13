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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void execute(JobExecutionContext context)
			throws JobExecutionException {
			//	JobDataMap dataMap = context.getJobDetail().getJobDataMap();
				System.out.println("I'm in " + new Date() );
				
				SchedulerContext schedulerContext = null;
				Scheduler myscheduler = null;
		        try {
		        	myscheduler = context.getScheduler();
		            schedulerContext = context.getScheduler().getContext();
		        } catch (SchedulerException e1) {
		            e1.printStackTrace();
		        }
				
		       
				
				
				JobKey key = context.getJobDetail().getKey();
				JobDataMap dataMap = context.getJobDetail().getJobDataMap();
				String prog = dataMap.getString("prog");
				BufferedReader reader = null;
				try {
							
				File cf = new File("test.txt");
		    	reader =  new BufferedReader(new FileReader(cf)); 
				String line;
				ArrayList<String[]> counter = new ArrayList<String[]>();
				while ((line = reader.readLine()) != null) {
					if (! line.isEmpty()) {
						System.out.println("file info: " + line);
					counter.add(line.split(":",-1));
					}
				}
			
				if (myscheduler != null && myscheduler.isStarted()) {
					System.out.println("job key info: " + key.toString() + "/" + key.getName());
				}
				
				if (counter.get(0)[0].equals("boo")) {
					myscheduler.deleteJob(new JobKey("job2", "group1"));
					JobDetail job = JobBuilder.newJob(TestJob.class)
			    		    .withIdentity("job2", "group1")
			    		    .usingJobData("prog", "...in hellojob")
			    		    .build();
					CronTriggerImpl trigger2 = new CronTriggerImpl();
			    	trigger2.setName("dummyTriggerName2");
			    	trigger2.setCronExpression("0/30 * * * * ?");
				    myscheduler.scheduleJob(job, trigger2);  
				}
				
				for (String groupName : myscheduler.getJobGroupNames()) {

				     for (JobKey jobKey : myscheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
								
					  String jobName = jobKey.getName();
					  String jobGroup = jobKey.getGroup();
						
					  
					  
					  //get job's trigger
					  List<Trigger> triggers = (List<Trigger>) myscheduler.getTriggersOfJob(jobKey);
					  Date nextFireTime = triggers.get(0).getNextFireTime(); 

						System.out.println("[jobName] : " + jobName + " [groupName] : "
							+ jobGroup + " - " + nextFireTime);

					  }

				 }
				
				
				
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (reader != null) {
						   reader.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}

}
