package nexxus.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/jobs")
public class TriggerJobController {

  private final WebClient webClient;
  private final String schedulerBaseUrl;

  public TriggerJobController(
      WebClient.Builder webClientBuilder,
      @Value("${exxus.scheduler.base-url}") String schedulerBaseUrl) {
    this.webClient = webClientBuilder.build();
    this.schedulerBaseUrl = schedulerBaseUrl;
  }

  @PostMapping("/trigger")
  public Mono<ResponseEntity<Void>> triggerSampleJob() {
    return webClient
        .post()
        .uri(schedulerBaseUrl + "/internal/jobs/sample/trigger")
        .retrieve()
        .toBodilessEntity()
        .map(response -> new ResponseEntity<Void>(HttpStatus.ACCEPTED));
  }
}
