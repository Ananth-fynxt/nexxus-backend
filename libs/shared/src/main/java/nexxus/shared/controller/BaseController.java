package nexxus.shared.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import nexxus.shared.dto.PaginationRequest;

/** Base controller providing common functionality for all controllers */
public abstract class BaseController {

  /**
   * Create a PaginationRequest from query parameters This method can be used in all controllers for
   * consistent pagination
   */
  protected PaginationRequest createPaginationRequest(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    return PaginationRequest.of(page, size, sortBy, sortDir);
  }

  /**
   * Create a Pageable object from query parameters Alternative method for direct Pageable creation
   */
  protected Pageable createPageable(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {

    return PaginationRequest.of(page, size, sortBy, sortDir).toPageable();
  }
}
