package nexxus.shared.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Reusable pagination request DTO for all readAll APIs */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

  @Builder.Default private int page = 0;

  @Builder.Default private int size = 10;

  @Builder.Default private String sortBy = "createdAt";

  @Builder.Default private String sortDir = "desc";

  /** Convert to Spring's Pageable object */
  public Pageable toPageable() {
    Sort sort =
        sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    return PageRequest.of(page, size, sort);
  }

  /** Create a PaginationRequest from query parameters */
  public static PaginationRequest of(int page, int size, String sortBy, String sortDir) {
    return PaginationRequest.builder()
        .page(page)
        .size(size)
        .sortBy(sortBy != null ? sortBy : "createdAt")
        .sortDir(sortDir != null ? sortDir : "desc")
        .build();
  }

  /** Create default PaginationRequest */
  public static PaginationRequest defaultRequest() {
    return PaginationRequest.builder().build();
  }
}
