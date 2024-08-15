package com.msb.bpm.approval.appr.factory.builder.customer;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;

public class GetIndividualCustomerBuilder implements ObjectBuilder<CustomerDTO, CustomerEntity> {

  @Override
  public CustomerDTO build(CustomerEntity customer) {
    return CustomerMapper.INSTANCE.toRBCustomer(customer, customer.getIndividualCustomer());
  }
}
