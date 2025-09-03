package com.exxus.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class TriggerJobController {

    private final WebClient webClient;
    private final String schedulerBaseUrl;

    public TriggerJobController(WebClient.Builder builder,
                                @Value("${exxus.scheduler.base-url}") String schedulerBaseUrl) {
        this.webClient = builder.build();
        this.schedulerBaseUrl = schedulerBaseUrl;
    }

    @PostMapping("/jobs/trigger")
    public Mono<ResponseEntity<Void>> trigger() {
        return webClient
                .post()
                .uri(schedulerBaseUrl + "/internal/jobs/sample/trigger")
                .retrieve()
                .toBodilessEntity();
    }
}

