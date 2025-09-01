package nexxus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"nexxus", "nexxus.shared"})
public class NexxusApplication {
  public static void main(String[] args) {
    SpringApplication.run(NexxusApplication.class, args);
  }
}
