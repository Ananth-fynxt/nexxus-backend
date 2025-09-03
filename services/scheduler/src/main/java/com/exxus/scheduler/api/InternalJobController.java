package com.exxus.scheduler.api;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exxus.integration.cron.jobs.SampleJob;

@RestController
@RequestMapping("/internal/jobs")
public class InternalJobController {

  private final JobScheduler jobScheduler;
  private final SampleJob sampleJob;

  public InternalJobController(JobScheduler jobScheduler, SampleJob sampleJob) {
    this.jobScheduler = jobScheduler;
    this.sampleJob = sampleJob;
  }

  @PostMapping("/sample/trigger")
  public ResponseEntity<Void> triggerSampleJob() {
    jobScheduler.enqueue(sampleJob::process);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }
}
