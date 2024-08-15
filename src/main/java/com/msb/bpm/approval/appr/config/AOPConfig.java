package com.msb.bpm.approval.appr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class AOPConfig {

  @Bean
  public ExpressionParser getExpression() {
    return new SpelExpressionParser();
  }

  @Bean
  public EvaluationContext getParserContext() {
    return new StandardEvaluationContext();
  }
}
