package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationFieldInformationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.CustomerCmsAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSalaryDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationFieldInformationMapper {

  ApplicationFieldInformationMapper INSTANCE = Mappers.getMapper(
      ApplicationFieldInformationMapper.class);

  Set<ApplicationFieldInformationDTO> toFieldInformations(
      Set<ApplicationFieldInformationEntity> fieldInformations);

  List<ApplicationFieldInformationDTO> toFieldInformations(
      List<ApplicationFieldInformationEntity> fieldInformations);

  ApplicationFieldInformationDTO toApplicationFieldInformationDTO(
      ApplicationFieldInformationEntity entity);

  @Mapping(target = "placeType", source = "addressType")
  ApplicationFieldInformationDTO cmsAddressToFieldInformationDTO(
      CustomerCmsAddressDTO customerAddressDTO);


  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "application", ignore = true)
  ApplicationFieldInformationEntity copyApplicationFieldInformationEntity(ApplicationFieldInformationEntity applicationFieldInformationEntity);

  Set<ApplicationFieldInformationEntity> copyApplicationFieldInformationEntities(Set<ApplicationFieldInformationEntity> applicationFieldInformationEntities);

  @Mapping(target = "placeType", source = "cmsCustomerAddress.addressType")
  @Mapping(target = "relationship", source = "relationship")
  @Mapping(target = "executor", source = "executor")
  ApplicationFieldInformationEntity v2CmsAddressToFieldInformation(String relationship, String executor, CmsCustomerAddressDTO cmsCustomerAddress);

  @Mapping(target = "placeType", constant = "V003")
  @Mapping(target = "relationship", source = "relationship")
  @Mapping(target = "executor", source = "executor")
  ApplicationFieldInformationEntity v2CmsSalaryAddressToFieldInformation(String relationship, String executor, CmsSalaryDTO salary);

  @Mapping(target = "placeType", constant = "V005")
  @Mapping(target = "relationship", source = "relationship")
  @Mapping(target = "executor", source = "executor")
  ApplicationFieldInformationEntity v2CmsIndividualBusinessAddressToFieldInformation(String relationship, String executor, CmsIndividualBusinessDTO individualBusiness);

  @Mapping(target = "placeType", constant = "V005")
  @Mapping(target = "relationship", source = "relationship")
  @Mapping(target = "executor", source = "executor")
  ApplicationFieldInformationEntity v2CmsEnterpriseBusinessAddressToFieldInformation(String relationship, String executor, CmsEnterpriseBusinessDTO enterpriseBusiness);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceFieldInformationEntity(@MappingTarget ApplicationFieldInformationEntity e1, ApplicationFieldInformationEntity e2);

}