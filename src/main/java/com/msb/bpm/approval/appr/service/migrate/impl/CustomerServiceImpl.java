package com.msb.bpm.approval.appr.service.migrate.impl;

import com.msb.bpm.approval.appr.client.customer.CustomerClient;
import com.msb.bpm.approval.appr.client.customer.request.CommonMigrateVersionRequest;
import com.msb.bpm.approval.appr.client.customer.request.CommonMigrateVersionRequest.CustomerWrapRequest;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse;
import com.msb.bpm.approval.appr.client.customer.response.CommonMigrateVersionResponse.CustomerVersionResponse;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.mapper.CustomerMapper;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.service.migrate.CustomerService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 9/11/2023, Thursday
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;

  private final CustomerClient customerClient;

  @Override
  @Transactional
  public Map<String, Object> updateCustomerVersion(Long customerId) {
    Map<String, Object> mapData = new HashMap<>();
    mapData.put("customerSize", 0);
    mapData.put("customerSuccess", 0);
    mapData.put("customerNotHaveIdCommon", 0);

    // Lấy danh sách customer RB cần migrate
    // - Nếu param customerId = null thì lấy toàn bộ customter có version = null
    // - Nếu param customerId != null thì lấy toàn bộ customer có refCustomerId = customerId & version = null
    return Optional.of(customerRepository.findAllByVersionIsNull(customerId, CustomerType.RB.name()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(customerEntities -> {
          mapData.put("customerSize", customerEntities.size());
          long count = customerEntities.stream().filter(x -> x.getRefCustomerId() == null).count();
          mapData.put("customerNotHaveIdCommon", count);

          // Filter những customer có refCustomerId
          List<CustomerEntity> filterCustomerEntities = customerEntities.stream()
              .filter(x -> x.getRefCustomerId() != null)
              .collect(Collectors.toList());

          // Convert list customer thành map
          // Map với key = refCustomerId
          Map<Long, List<CustomerEntity>> groupCustomerEntities = filterCustomerEntities.stream()
                  .collect(Collectors.groupingBy(CustomerEntity::getRefCustomerId));

          AtomicReference<Integer> countSuccess = new AtomicReference<>(0);

          groupCustomerEntities.forEach((k, v) -> {

            // Tạo request gửi sang customer-additional service
            CommonMigrateVersionRequest request = new CommonMigrateVersionRequest();
            request.setCustomerId(k);
            v.forEach(customerEntity -> request.getCustomers().add(buildCustomerRequest(customerEntity)));

            // Call api migrate
            CommonMigrateVersionResponse response = customerClient.migrateCustomerVersion(request);

            // Set lại version theo customerId và lưu lại
            if (response != null && response.getData() != null && CollectionUtils.isNotEmpty(response.getData()
                .getCustomers())) {

              Map<Long, Integer> versionMap = response.getData().getCustomers()
                  .stream()
                  .collect(Collectors.toMap(CustomerVersionResponse::getRefCustomerId,
                      CustomerVersionResponse::getVersion));

              v.forEach(customerEntity -> customerEntity.setVersion(versionMap.get(customerEntity.getId())));

              customerRepository.saveAll(v);

              // Update biến ghi nhận số bản ghi update version thành công
              countSuccess.updateAndGet(v1 -> v1 + v.size());
            }
          });

          mapData.put("customerSuccess", countSuccess.get());

          return mapData;
        })
        .orElse(mapData);
  }

  public CustomerWrapRequest buildCustomerRequest(CustomerEntity customerEntity) {
    CustomerWrapRequest customerRequest = new CustomerWrapRequest();

    List<CustomerIdentityEntity> identityEntities = new ArrayList<>(
        customerEntity.getCustomerIdentitys());

    List<CustomerAddressEntity> addressEntities = new ArrayList<>(
        customerEntity.getCustomerAddresses());

    customerRequest.setCustomer(CustomerMapper.INSTANCE.entityToCustomerRequest(customerEntity,
        customerEntity.getIndividualCustomer(), null));

    customerRequest.setIdentityDocuments(
        CustomerMapper.INSTANCE.entityToIdentityDocumentRequest(identityEntities));

    customerRequest.setAddresses(CustomerMapper.INSTANCE.entityToAddressRequest(addressEntities));

    return customerRequest;
  }
}
