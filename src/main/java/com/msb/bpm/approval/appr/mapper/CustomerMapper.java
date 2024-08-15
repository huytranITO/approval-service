package com.msb.bpm.approval.appr.mapper;

import static com.msb.bpm.approval.appr.enums.application.AddressType.HK_THUONG_TRU;

import com.msb.bpm.approval.appr.client.customer.request.AddressMigrateVersionRequest;
import com.msb.bpm.approval.appr.client.customer.request.CustomerVersionRequest;
import com.msb.bpm.approval.appr.client.customer.request.IdentityMigrateVersionRequest;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
import com.msb.bpm.approval.appr.model.dto.CustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerDTO;
import com.msb.bpm.approval.appr.model.dto.CustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.EnterpriseCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.IndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerContactDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualCustomerDTO;
import com.msb.bpm.approval.appr.model.entity.CustomerAddressEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerContactEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerIdentityEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.EnterpriseCustomerEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualCustomerEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.msb.bpm.approval.appr.model.response.customer.SearchCustomerV3Response;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

  CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

  @Mapping(target = "fullName", expression = "java(getFullName(individualCustomer))")
  @Mapping(target = "addresses", source = "customer.customerAddresses")
  @Mapping(target = "identities", source = "customer.customerIdentitys")
  @Mapping(target = "mainIdentity", source = "customer.customerIdentitys", qualifiedByName = "mapToPrimaryIdentity")
  @Mapping(target = "mainAddress", source = "customer.customerAddresses", qualifiedByName = "mapToPrimaryAddress")
//  @Mapping(target = "contacts", source = "customer.customerContacts")
  IndividualCustomerDTO toRBCustomer(CustomerEntity customer,
      IndividualCustomerEntity individualCustomer);

  EnterpriseCustomerDTO toEBCustomer(CustomerEntity customer,
      EnterpriseCustomerEntity enterpriseCustomer);

  @Mapping(target = "relationship", expression = "java(mapRelationshipNull(dto))")
  @Mapping(target = "refCusId", expression = "java(checkRefCusIdBlank(entity, dto))")
  @Mapping(target = "relationshipRefId", source = "relationshipRefId")
  @Mapping(target = "paymentGuarantee", source = "paymentGuarantee")
  void referenceCustomer(@MappingTarget CustomerEntity entity, CustomerDTO dto);

  default String checkRefCusIdBlank(CustomerEntity entity, CustomerDTO dto) {
    if (StringUtils.isBlank(entity.getRefCusId())) {
      if (StringUtils.isNotBlank(dto.getRefCusId())) {
        return dto.getRefCusId();
      } else {
        return UUID.randomUUID().toString();
      }
    }
    return entity.getRefCusId();
  }

  @Mapping(target = "firstName", expression = "java(splitFirstName(dto))")
  @Mapping(target = "lastName", expression = "java(splitLastName(dto))")
  void referenceIndividualCustomer(@MappingTarget IndividualCustomerEntity entity,
      IndividualCustomerDTO dto);

  void referenceEnterpriseCustomer(@MappingTarget EnterpriseCustomerEntity entity,
      EnterpriseCustomerDTO dto);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCustomerAddress(@MappingTarget CustomerAddressEntity e1, CustomerAddressEntity e2);

  Set<CustomerAddressEntity> toCustomerAddressEntities(Set<CustomerAddressDTO> customerAddresses);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCustomerIdentity(@MappingTarget CustomerIdentityEntity e1,
      CustomerIdentityEntity e2);

  Set<CustomerIdentityEntity> toCustomerIdentityEntities(
      Set<CustomerIdentityDTO> customerIdentities);


  default String getFullName(IndividualCustomerEntity individualCustomer) {
    if (individualCustomer != null) {
      return individualCustomer.getLastName() + " " + individualCustomer.getFirstName();
    }
    return null;
  }

  default String splitFirstName(IndividualCustomerDTO individualCustomer) {
    String fullName = individualCustomer.getFullName();
    if (StringUtils.isBlank(fullName)) {
      return "";
    }
    int idx = fullName.lastIndexOf(' ');
    if (idx < 0) {
      return fullName;
    }
    return fullName.substring(idx).trim();
  }

  default String splitLastName(IndividualCustomerDTO individualCustomer) {
    String fullName = individualCustomer.getFullName();
    if (StringUtils.isBlank(fullName)) {
      return "";
    }
    int idx = fullName.lastIndexOf(' ');
    if (idx < 0) {
      return "";
    }
    return fullName.substring(0, idx).trim();
  }

  default String mapRelationshipNull(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      return StringUtils.isNotBlank(customerDTO.getRelationship()) ? customerDTO.getRelationship()
          : "";
    }
    return "";
  }

  CustomerIdentityDTO toCustomerIdentityDTO(CustomerIdentityEntity customerIdentity);

  @Named("mapToPrimaryIdentity")
  default CustomerIdentityDTO mapToPrimaryIdentity(Set<CustomerIdentityEntity> listSource) {
    Optional<CustomerIdentityEntity> ops = listSource.stream().filter(entity -> entity.isPriority())
        .findFirst();

    return ops.isPresent() ? toCustomerIdentityDTO(ops.get()) : new CustomerIdentityDTO();
  }

  CustomerAddressDTO toCustomerAddressDTO(CustomerAddressEntity customerIdentity);

  @Named("mapToPrimaryAddress")
  default CustomerAddressDTO mapToPrimaryAddress(Set<CustomerAddressEntity> listSource) {
    Optional<CustomerAddressEntity> ops = listSource.stream()
        .filter(entity -> HK_THUONG_TRU.getValue().equalsIgnoreCase(entity.getAddressType()))
        .findFirst();

    return ops.isPresent() ? toCustomerAddressDTO(ops.get()) : new CustomerAddressDTO();
  }

  @Mapping(target = "fullName", expression = "java(getFullName(individualCustomerEntity))")
  @Mapping(target = "addresses", source = "customerEntity.customerAddresses")
  @Mapping(target = "identities", source = "customerEntity.customerIdentitys")
  @Mapping(target = "contacts", source = "customerEntity.customerContacts")
  @Mapping(target = "cifNo", source = "customerEntity.cif")
  @Mapping(target = "typeOfCustomer", source = "individualCustomerEntity.subject")
  @Mapping(target = "staffId", source = "individualCustomerEntity.employeeCode")
  CmsIndividualCustomerDTO toCmsCustomerDto(IndividualCustomerEntity individualCustomerEntity,
      CustomerEntity customerEntity);

  @Mapping(target = "fullName", expression = "java(getFullName(individualCustomerEntity))")
  @Mapping(target = "addresses", source = "customerEntity.customerAddresses")
  @Mapping(target = "identities", source = "customerEntity.customerIdentitys")
  @Mapping(target = "contacts", source = "customerEntity.customerContacts")
  @Mapping(target = "relationship",expression = "java(mapCustomerIdAndRelationship.get(customerEntity.getId()))")
  @Mapping(target = "staffId", source = "individualCustomerEntity.employeeCode")
  @Mapping(target = "typeOfCustomer", source = "individualCustomerEntity.subject")
  CmsCustomerRelationDTO toCmsCustomerRelationDto(IndividualCustomerEntity individualCustomerEntity,
      CustomerEntity customerEntity, Map<Long, String> mapCustomerIdAndRelationship);

  @Mapping(target = "identifierNumber", source = "customerIdentityEntity.identifierCode")
  CmsCustomerIdentityDTO entityToCmsCustomerIdentityDto(
      CustomerIdentityEntity customerIdentityEntity);

  CmsCustomerAddressDTO entityToCmsCustomerAddressDto(CustomerAddressEntity customerAddressEntity);

  CmsCustomerContactDTO entityToCmsCustomerContactDto(CustomerContactEntity customerContactEntity);

  @Mapping(target = "refEnterpriseId", source = "customerEntity.refCusId")
  @Mapping(target = "businessType", source = "enterpriseCustomerEntity.businessType")
  @Mapping(target = "businessRegistrationNumber", source = "enterpriseCustomerEntity.businessRegistrationNumber")
  @Mapping(target = "companyName", source = "enterpriseCustomerEntity.companyName")
  CmsEnterpriseRelationDTO toCmsEnterpriseRelationDto(EnterpriseCustomerEntity enterpriseCustomerEntity, CustomerEntity customerEntity);

  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  IndividualCustomerEntity copyIndividualCustomerEntity(IndividualCustomerEntity customerEntity);

  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  EnterpriseCustomerEntity copyEnterpriseCustomerEntity(EnterpriseCustomerEntity customerEntity);

  @Mapping(target = "id", ignore = true)
//  @Mapping(target = "application", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customerRelationShips", ignore = true)
  @Mapping(target = "refCusId", expression = "java(generateRefCusId(customerEntity))")
  @Mapping(target = "oldId", source = "id")
  CustomerEntity copyCustomerEntity(CustomerEntity customerEntity);

  default String generateRefCusId(CustomerEntity customerEntity) {
    if (CustomerType.EB.name().equalsIgnoreCase(customerEntity.getCustomerType())) {
      return null;
    }
    return UUID.randomUUID().toString();
  }

  List<CustomerEntity> copyCustomerEntities(List<CustomerEntity> customerEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "ldpIdentityId", ignore = true)
  CustomerIdentityEntity copyCustomerIdentityEntity(CustomerIdentityEntity customerIdentityEntity);

  Set<CustomerIdentityEntity> copyCustomerIdentityEntities(Set<CustomerIdentityEntity> customerIdentityEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "ldpAddressId", ignore = true)
  CustomerAddressEntity copyCustomerAddressEntity(CustomerAddressEntity customerAddressEntity);

  Set<CustomerAddressEntity> copyCustomerAddressEntities(Set<CustomerAddressEntity> customerAddressEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  CustomerContactEntity copyCustomerContactEntity(CustomerContactEntity customerContactEntity);

  Set<CustomerContactEntity> copyCustomerContactEntities(Set<CustomerContactEntity> customerContactEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "customerRefId", ignore = true)
  CustomerRelationShipEntity copyCustomerRelationShipEntity(CustomerRelationShipEntity customerRelationShipEntity);

  Set<CustomerRelationShipEntity> copyCustomerRelationShipEntities(Set<CustomerRelationShipEntity> customerRelationShipEntities);

  @Mapping(target = "refCustomerId", source = "customer.id")
  @Mapping(target = "cifNo", source = "customer.cif")
  @Mapping(target = "name", expression = "java(getFullName(individualCustomer))")
  @Mapping(target = "birthday", source = "individualCustomer.dateOfBirth")
  @Mapping(target = "national", source = "individualCustomer.nation")
  @Mapping(target = "maritalStatus", source = "individualCustomer.martialStatus")
  @Mapping(target = "customerSegment", source = "individualCustomer.subject")
  @Mapping(target = "staffId", source = "individualCustomer.employeeCode")
  @Mapping(target = "identityNumber", source = "mainIdentity.identifierCode")
  @Mapping(target = "issuedDate", source = "mainIdentity.issuedAt")
  @Mapping(target = "identityType", source = "mainIdentity.documentType")
  CustomerVersionRequest entityToCustomerRequest(CustomerEntity customer, IndividualCustomerEntity individualCustomer, CustomerIdentityEntity mainIdentity);

  List<IdentityMigrateVersionRequest> entityToIdentityDocumentRequest(List<CustomerIdentityEntity> customerIdentityEntities);

  @Mapping(target = "id", source = "refIdentityId")
  @Mapping(target = "identityNumber", source = "identifierCode")
  @Mapping(target = "type", source = "documentType")
  @Mapping(target = "issuedDate", source = "issuedAt")
  @Mapping(target = "isPrimary", source = "priority")
  IdentityMigrateVersionRequest entityToIdentityDocumentRequest(CustomerIdentityEntity customerIdentityEntity);

  List<AddressMigrateVersionRequest> entityToAddressRequest(List<CustomerAddressEntity> customerAddressEntities);

  @Mapping(target = "id", source = "refAddressId")
  AddressMigrateVersionRequest entityToAddressRequest(CustomerAddressEntity customerAddressEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "issuedPlace", ignore = true)
  @Mapping(target = "priority", source = "primary")
  @Mapping(target = "identifierCode", source = "identityNumber")
  @Mapping(target = "issuedAt", source = "issuedDate")
  @Mapping(target = "documentType", source = "type")
  @Mapping(target = "issuedPlaceValue", source = "issuedPlace")
  CustomerIdentityDTO toCustomerIdentityCommonDTO(SearchCustomerV3Response.IdentityDocumentsEbRes customerIdentity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "districtValue", source = "districtName")
  @Mapping(target = "cityValue", source = "cityName")
  @Mapping(target = "wardValue", source = "wardName")
  CustomerAddressDTO toCustomerAddressCommonDTO(SearchCustomerV3Response.AddressEbRes customerAddress);

  Set<CustomerIdentityDTO> toListCustomerIdentityCommonDTO(Set<SearchCustomerV3Response.IdentityDocumentsEbRes> customerAddress);

  Set<CustomerAddressDTO> toListCustomerAddressCommonDTO(Set<SearchCustomerV3Response.AddressEbRes> customerAddress);
}
