package com.exxus.integration.cron.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleJob {

    private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);

    public void process() {
        logger.info("SampleJob executed at {}", System.currentTimeMillis());
    }
}

