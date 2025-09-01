package nexxus.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto {
  @NotBlank(message = "Phone code is required")
  private String code;

  @NotBlank(message = "Phone number is required")
  private String number;
}
