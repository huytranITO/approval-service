package com.msb.bpm.approval.appr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

  private final ObjectMapper objectMapper;

  private final TokenProvider tokenProvider;

  private final ApiRespFactory apiRespFactory;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
        .antMatchers("/test/**", "/internal/api/v1/camunda/**").permitAll()
        .anyRequest().authenticated().and().httpBasic().and()
        .addFilterBefore(new JwtFilter(apiRespFactory, tokenProvider, objectMapper),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .antMatchers("/swagger-ui/**", "/swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**", "/swagger-resources",
            "/swagger-resources/**","/swagger-ui.html")
        .antMatchers(HttpMethod.POST, "/bpm/approval/external")
        .antMatchers(HttpMethod.POST, "/bpm/approval/internal");
  }


}
