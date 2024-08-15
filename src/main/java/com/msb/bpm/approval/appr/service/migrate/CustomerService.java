package com.msb.bpm.approval.appr.service.migrate;

import java.util.Map;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/11/2023, Thursday
 **/
public interface CustomerService {

  Map<String, Object> updateCustomerVersion(Long customerId);
}
