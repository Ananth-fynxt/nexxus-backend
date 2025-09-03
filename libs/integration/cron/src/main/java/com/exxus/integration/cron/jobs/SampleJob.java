package com.exxus.integration.cron.jobs;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleJob {

  private static final Logger log = LoggerFactory.getLogger(SampleJob.class);

  public void process() {
    log.info("SampleJob.process executed at {}", Instant.now());
  }
}
