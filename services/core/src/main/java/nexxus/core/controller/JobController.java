package nexxus.core.controller;

import lombok.RequiredArgsConstructor;
import nexxus.integration.cron.jobs.ReportJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

  private final JobScheduler jobScheduler;
  private final ReportJob reportJob;

  @PostMapping("/enqueue")
  public String enqueueReportJob() {
    jobScheduler.enqueue(reportJob::generate);
    return "Report job enqueued!";
  }
}

