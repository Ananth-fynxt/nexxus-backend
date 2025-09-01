package nexxus.webhook.dto;

import nexxus.shared.constants.Status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWebhookDto {

  @NotBlank(message = "URL is required")
  private String url;

  @NotNull(message = "Retry count is required")
  @Min(value = 3, message = "Retry count must be greater than or equal to 3")
  @Max(value = 5, message = "Retry count must be less than or equal to 5")
  private Integer retry;

  @NotNull(message = "Status is required")
  private Status status;
}
