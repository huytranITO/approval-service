package com.msb.bpm.approval.appr.controller.api;

import com.msb.bpm.approval.appr.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/11/2023, Thursday
 **/
@RequestMapping("/customer")
public interface CustomerApi {

  ResponseEntity<ApiResponse> migrateCustomerVersion(@RequestParam(required = false) Long customerId);
}
