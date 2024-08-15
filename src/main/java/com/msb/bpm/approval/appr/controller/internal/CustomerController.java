package com.msb.bpm.approval.appr.controller.internal;

import com.msb.bpm.approval.appr.controller.api.CustomerApi;
import com.msb.bpm.approval.appr.model.response.ApiRespFactory;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.migrate.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/11/2023, Thursday
 **/
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

  private final ApiRespFactory factory;
  private final CustomerService customerService;

  @GetMapping("/api/v1/migrate-data")
  @Override
  public ResponseEntity<ApiResponse> migrateCustomerVersion(Long customerId) {
    return factory.success(customerService.updateCustomerVersion(customerId));
  }
}
