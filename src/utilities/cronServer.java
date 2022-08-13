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
import com.blueseer.crn.TestJob;
import com.blueseer.crn.jobWD;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
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
    	
		// build watchdog job
		
		
		
		JobDetail jobwd = JobBuilder.newJob(jobWD.class)
    		    .withIdentity("job1", "group1")
    		    .usingJobData("prog", "...in jobwd")
    		    .build();
    		    
		
    	JobDetail job = JobBuilder.newJob(TestJob.class)
    		    .withIdentity("job2", "group1")
    		    .usingJobData("prog", "...in hellojob")
    		    .build();
    	
    	CronTriggerImpl trigger1 = new CronTriggerImpl();
    	trigger1.setName("dummyTriggerName1");
    	trigger1.setCronExpression("0/5 * * * * ?");
    	
    	CronTriggerImpl trigger2 = new CronTriggerImpl();
    	trigger2.setName("dummyTriggerName2");
    	trigger2.setCronExpression("0/10 * * * * ?");
    	
    	
    	//schedule it
    	scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	scheduler.scheduleJob(jobwd, trigger1);
    	scheduler.scheduleJob(job, trigger2);
    	
        }	
}
