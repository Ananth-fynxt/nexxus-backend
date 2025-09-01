package nexxus.psp.repository;

import java.time.LocalDateTime;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.MaintenanceWindow;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MaintenanceWindowRepository
    extends ReactiveCrudRepository<MaintenanceWindow, String> {

  @Modifying
  @Query(
      "INSERT INTO maintenance_windows (id, psp_id, flow_action_id, start_at, end_at, \"status\", created_at, updated_at, created_by, updated_by) VALUES (:id, :pspId, :flowActionId, :startAt, :endAt, :status, :createdAt, :updatedAt, :createdBy, :updatedBy)")
  Mono<Void> insertMaintenanceWindow(
      @Param("id") String id,
      @Param("pspId") String pspId,
      @Param("flowActionId") String flowActionId,
      @Param("startAt") LocalDateTime startAt,
      @Param("endAt") LocalDateTime endAt,
      @Param("status") String status,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt,
      @Param("createdBy") String createdBy,
      @Param("updatedBy") String updatedBy);

  @Query("SELECT * FROM maintenance_windows WHERE psp_id = :pspId")
  Flux<MaintenanceWindow> findByPspId(@Param("pspId") String pspId);

  @Modifying
  @Query("DELETE FROM maintenance_windows WHERE psp_id = :pspId")
  Mono<Void> deleteByPspId(@Param("pspId") String pspId);
}
