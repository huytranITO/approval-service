package com.msb.bpm.approval.appr.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig {
  private final TaskExecutionProperties properties;

  @Primary
  @Bean
  public Executor getTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(properties.getPool().getCoreSize());
    executor.setMaxPoolSize(properties.getPool().getMaxSize());
    executor.setQueueCapacity(properties.getPool().getQueueCapacity());
    executor.setThreadNamePrefix(properties.getThreadNamePrefix());
    executor.setKeepAliveSeconds((int) properties.getPool().getKeepAlive().getSeconds());
    executor.initialize();
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }
}
