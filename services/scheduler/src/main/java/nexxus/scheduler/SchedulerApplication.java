package nexxus.scheduler;

import nexxus.integration.cron.jobs.ReportJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"nexxus.scheduler", "nexxus.integration"})
public class SchedulerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SchedulerApplication.class, args);
  }

  @Bean
  CommandLineRunner scheduleJobs(JobScheduler scheduler, ReportJob reportJob) {
    return args -> scheduler.scheduleRecurrently("report-job", Cron.hourly(), reportJob::generate);
  }
}

