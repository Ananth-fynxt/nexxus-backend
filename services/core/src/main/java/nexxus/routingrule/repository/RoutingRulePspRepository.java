package nexxus.routingrule.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.routingrule.entity.RoutingRulePsp;
import nexxus.routingrule.entity.RoutingRulePspId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoutingRulePspRepository
    extends ReactiveCrudRepository<RoutingRulePsp, RoutingRulePspId> {

  @Modifying
  @Query(
      "INSERT INTO routing_rule_psps (routing_rule_id, routing_rule_version, psp_id, psp_value) VALUES (:routingRuleId, :routingRuleVersion, :pspId, :pspValue)")
  Mono<Integer> insertPsp(
      @Param("routingRuleId") String routingRuleId,
      @Param("routingRuleVersion") Integer routingRuleVersion,
      @Param("pspId") String pspId,
      @Param("pspValue") Integer pspValue);

  @Modifying
  @Query("DELETE FROM routing_rule_psps WHERE routing_rule_id = :routingRuleId")
  Mono<Integer> deleteAllByRoutingRuleId(@Param("routingRuleId") String routingRuleId);

  @Query(
      "SELECT * FROM routing_rule_psps WHERE routing_rule_id = :routingRuleId AND routing_rule_version = :routingRuleVersion")
  Flux<RoutingRulePsp> findByRoutingRuleIdAndRoutingRuleVersion(
      @Param("routingRuleId") String routingRuleId,
      @Param("routingRuleVersion") Integer routingRuleVersion);
}
