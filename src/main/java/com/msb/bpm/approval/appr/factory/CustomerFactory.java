package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.EB;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.Customer.RB;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.customer.GetEnterpriseCustomerBuilder;
import com.msb.bpm.approval.appr.factory.builder.customer.GetIndividualCustomerBuilder;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;

public interface CustomerFactory {

  static ObjectBuilder<CustomerDTO, CustomerEntity> getCustomer(String customerType) {
    switch (customerType) {
      case RB:
        return new GetIndividualCustomerBuilder();
      case EB:
        return new GetEnterpriseCustomerBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }
}
