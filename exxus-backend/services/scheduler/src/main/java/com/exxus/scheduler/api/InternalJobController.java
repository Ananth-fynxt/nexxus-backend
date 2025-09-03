package com.exxus.scheduler.api;

import com.exxus.integration.cron.jobs.SampleJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/jobs/sample")
public class InternalJobController {

    private final JobScheduler jobScheduler;
    private final SampleJob sampleJob;

    public InternalJobController(JobScheduler jobScheduler, SampleJob sampleJob) {
        this.jobScheduler = jobScheduler;
        this.sampleJob = sampleJob;
    }

    @PostMapping("/trigger")
    public ResponseEntity<Void> trigger() {
        jobScheduler.enqueue(sampleJob::process);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}

