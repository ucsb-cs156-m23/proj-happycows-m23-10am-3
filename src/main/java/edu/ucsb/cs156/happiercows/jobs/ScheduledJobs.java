package edu.ucsb.cs156.happiercows.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;
import edu.ucsb.cs156.happiercows.services.jobs.JobService;
import lombok.extern.slf4j.Slf4j;

/**
 * This class contains methods that are scheduled to run at certain times
 * to launch particular jobs.
 * 
 * The value of the <code>cron</code> parameter to the <code>&#64;Scheduled</code>
 * annotation is a Spring Boot cron expression, which is similar to
 * a Unix cron expression, but with an extra field at the beginning for
 * the seconds.
 * 
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html">Spring Cron Syntax</a>
 * 
 */

@Component("scheduledJobs")
@Slf4j
public class ScheduledJobs {

    @Autowired
    private JobService jobService;

    @Autowired
    UpdateCowHealthJobFactory updateCowHealthJobFactory;

    @Autowired
    MilkTheCowsJobFactory milkTheCowsJobFactory;

    @Autowired
    CommonStatsJobFactory commonStatsJobFactory;
    
    @Scheduled(cron = "${app.updateCowHealth.cron}", zone = "America/Los_Angeles")
    public void runUpdateCowHealthJobBasedOnCron() {
       log.info("runUpdateCowHealthJobBasedOnCron: running");

       JobContextConsumer updateCowHealthJob = updateCowHealthJobFactory.create();
       jobService.runAsJob(updateCowHealthJob);
    
       log.info("runUpdateCowHealthJobBasedOnCron: launched job");
    }

    @Scheduled(cron = "${app.milkTheCows.cron}", zone = "America/Los_Angeles")
    public void runMilkTheCowsJobBasedOnCron() {
       log.info("runMilkTheCowsJobBasedOnCron: running");

       JobContextConsumer milkTheCowsJob = milkTheCowsJobFactory.create();
       jobService.runAsJob(milkTheCowsJob);
    
       log.info("runMilkTheCowsJobBasedOnCron: launched job");
    }

    @Scheduled(cron = "${app.commonStats.cron}", zone = "America/Los_Angeles")
    public void runCommonStatsJobBasedOnCron() {
       log.info("runCommonStatsJobBasedOnCron: running");

       JobContextConsumer commonStatsJob = commonStatsJobFactory.create();
       jobService.runAsJob(commonStatsJob);
    
       log.info("runCommonStatsJobBasedOnCron: launched job");
    }
}
