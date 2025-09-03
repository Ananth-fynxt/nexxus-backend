package com.exxus.scheduler;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.exxus.integration.cron.jobs.SampleJob;

@SpringBootApplication(scanBasePackages = "com.exxus")
public class SchedulerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchedulerApplication.class, args);
  }

  @Bean
  CommandLineRunner scheduleMinutelyJob(JobScheduler jobScheduler, SampleJob sampleJob) {
    return args ->
        jobScheduler.scheduleRecurrently(
            "sample-job-minutely", "*/1 * * * *", () -> sampleJob.process());
  }
}
