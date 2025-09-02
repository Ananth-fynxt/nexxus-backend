package nexxus.integration.cron.jobs;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ReportJob {
  public void generate() {
    System.out.println("\uD83D\uDCCA Generating report at " + LocalDateTime.now());
  }
}

