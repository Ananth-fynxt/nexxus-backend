package nexxus.riskrule.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import nexxus.riskrule.entity.RiskRulePsp;
import nexxus.riskrule.entity.RiskRulePspId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RiskRulePspRepository extends ReactiveCrudRepository<RiskRulePsp, RiskRulePspId> {

  @Query(
      "SELECT * FROM risk_rule_psps WHERE risk_rule_id = :riskRuleId AND risk_rule_version = :riskRuleVersion")
  Flux<RiskRulePsp> findByRiskRuleIdAndRiskRuleVersion(
      @Param("riskRuleId") String riskRuleId, @Param("riskRuleVersion") Integer riskRuleVersion);

  @Query("SELECT DISTINCT risk_rule_id FROM risk_rule_psps WHERE psp_id = :pspId")
  Flux<String> findRiskRuleIdsByPspId(@Param("pspId") String pspId);

  @Modifying
  @Query(
      "DELETE FROM risk_rule_psps WHERE risk_rule_id = :riskRuleId AND risk_rule_version = :riskRuleVersion")
  Mono<Integer> deleteByRiskRuleIdAndRiskRuleVersion(
      @Param("riskRuleId") String riskRuleId, @Param("riskRuleVersion") Integer riskRuleVersion);

  @Modifying
  @Query("DELETE FROM risk_rule_psps WHERE risk_rule_id = :riskRuleId")
  Mono<Integer> deleteAllByRiskRuleId(@Param("riskRuleId") String riskRuleId);

  @Modifying
  @Query(
      "INSERT INTO risk_rule_psps (risk_rule_id, risk_rule_version, psp_id) VALUES (:riskRuleId, :riskRuleVersion, :pspId)")
  Mono<Integer> insertRiskRulePsp(
      @Param("riskRuleId") String riskRuleId,
      @Param("riskRuleVersion") Integer riskRuleVersion,
      @Param("pspId") String pspId);
}
