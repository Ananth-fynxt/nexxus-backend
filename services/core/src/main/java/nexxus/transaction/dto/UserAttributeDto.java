package nexxus.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserAttributeDto {
  @NotBlank(message = "User ID is required")
  private String id;

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  private String email;

  private String tag;

  private String accountType;

  @NotNull(message = "Phone is required")
  private PhoneDto phone;

  @NotNull(message = "Address is required")
  private AddressDto address;
}
