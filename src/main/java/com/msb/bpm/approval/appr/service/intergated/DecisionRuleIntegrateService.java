package com.msb.bpm.approval.appr.service.intergated;

import com.msb.bpm.approval.appr.enums.rule.RuleCode;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 29/5/2023, Monday
 **/
public interface DecisionRuleIntegrateService {

  Object getDecisionRule(Long applicationId, RuleCode ruleCode, Object payloadData);

}
