package com.msb.bpm.approval.appr.config;

import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

  @NotNull
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(SecurityContextUtil.getCurrentUser());
  }
}
