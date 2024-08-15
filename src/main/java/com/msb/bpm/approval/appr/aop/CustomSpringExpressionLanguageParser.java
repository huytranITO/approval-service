package com.msb.bpm.approval.appr.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/5/2023, Tuesday
 **/
@Configuration
@RequiredArgsConstructor
public class CustomSpringExpressionLanguageParser {
  private final ExpressionParser expressionParser;
  private final EvaluationContext evaluationContext;

  public Object getDynamicValue(String[] parameterNames, Object[] args, String key) {

    for (int i = 0; i < parameterNames.length; i++) {
      evaluationContext.setVariable(parameterNames[i], args[i]);
    }
    return expressionParser.parseExpression(key).getValue(evaluationContext, Object.class);
  }

}
