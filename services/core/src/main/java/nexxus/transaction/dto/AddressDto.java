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
public class AddressDto {
  @NotBlank(message = "Address line1 is required")
  private String line1;

  private String city;

  private String state;

  private String zipCode;

  private String country;
}
