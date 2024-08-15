package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.constant.ApplicationConstant;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.OtherIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.PropertyBusinessIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.RentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.SalaryIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIndividualBusinessDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsOtherDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsPropertyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsRentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSalaryDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerTransactionIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.IncomeEvaluationEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.TotalAssetIncomeEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.Set;

import static com.msb.bpm.approval.appr.constant.IntegrationConstant.ANOTHER_ADDRESS_CODE;
import static com.msb.bpm.approval.appr.enums.checklist.AssetGroup.VEHICLE;

@Mapper
public interface ApplicationIncomeMapper {

  ApplicationIncomeMapper INSTANCE = Mappers.getMapper(ApplicationIncomeMapper.class);

  @Mapping(target = "explanation", source = "salaryIncome.explanation")
  @Mapping(target = "address.cityCode", source = "salaryIncome.cityCode")
  @Mapping(target = "address.districtCode", source = "salaryIncome.districtCode")
  @Mapping(target = "address.wardCode", source = "salaryIncome.wardCode")
  @Mapping(target = "address.cityValue", source = "salaryIncome.cityValue")
  @Mapping(target = "address.districtValue", source = "salaryIncome.districtValue")
  @Mapping(target = "address.wardValue", source = "salaryIncome.wardValue")
  @Mapping(target = "address.addressLine", source = "salaryIncome.addressLine")
  @Mapping(target = "incomeType", source = "salaryIncome.incomeType")
  @Mapping(target = "address.addressLinkId", source = "salaryIncome.addressLinkId")
  SalaryIncomeDTO toSalaryIncomeDTO(SalaryIncomeEntity salaryIncome);

  @Mapping(target = "address.cityCode", source = "individualEnterpriseIncome.cityCode")
  @Mapping(target = "address.districtCode", source = "individualEnterpriseIncome.districtCode")
  @Mapping(target = "address.wardCode", source = "individualEnterpriseIncome.wardCode")
  @Mapping(target = "address.cityValue", source = "individualEnterpriseIncome.cityValue")
  @Mapping(target = "address.districtValue", source = "individualEnterpriseIncome.districtValue")
  @Mapping(target = "address.wardValue", source = "individualEnterpriseIncome.wardValue")
  @Mapping(target = "address.addressLine", source = "individualEnterpriseIncome.addressLine")
  @Mapping(target = "incomeType", source = "individualEnterpriseIncome.incomeType")
  @Mapping(target = "address.addressLinkId", source = "individualEnterpriseIncome.addressLinkId")
  BusinessIncomeDTO toBizIncomeDTO(IndividualEnterpriseIncomeEntity individualEnterpriseIncome);

  PropertyBusinessIncomeDTO toPropertyBusinessIncomeDTO(
      PropertyBusinessIncomeEntity propertyBusinessIncome);

  @Mapping(target = "explanation", source = "rentalIncome.explanation")
  @Mapping(target = "incomeType", source = "rentalIncome.incomeType")
  @Mapping(target = "address.cityCode", source = "rentalIncome.cityCode")
  @Mapping(target = "address.districtCode", source = "rentalIncome.districtCode")
  @Mapping(target = "address.wardCode", source = "rentalIncome.wardCode")
  @Mapping(target = "address.cityValue", source = "rentalIncome.cityValue")
  @Mapping(target = "address.districtValue", source = "rentalIncome.districtValue")
  @Mapping(target = "address.wardValue", source = "rentalIncome.wardValue")
  @Mapping(target = "address.addressLine", source = "rentalIncome.assetAddress")
  @Mapping(target = "address.addressLinkId", source = "rentalIncome.addressLinkId")
  @Mapping(target = "addressType", expression = "java(getAddressTypeForCBT(rentalIncome))")
  @Mapping(target = "addressTypeValue", expression = "java(getAddressTypeValueForCBT(rentalIncome))")
  RentalIncomeDTO toRentalIncomeDTO(RentalIncomeEntity rentalIncome);

  @Mapping(target = "explanation", source = "otherIncome.explanation")
  @Mapping(target = "incomeType", source = "otherIncome.incomeType")
  OtherIncomeDTO toOtherIncomeDTO(OtherIncomeEntity otherIncome);

  ActuallyIncomeDTO toActuallyIncomeDTO(ApplicationIncomeEntity income);

  @Mapping(target = "explanation", source = "income.explanation")
  ExchangeIncomeDTO toExchangeIncomeDTO(ApplicationIncomeEntity income);

  ApplicationIncomeEntity toActuallyIncomeEntity(ActuallyIncomeDTO actuallyIncome);

  ApplicationIncomeEntity toExchangeIncomeEntity(ExchangeIncomeDTO exchangeIncome);

  @Mapping(target = "cityCode", source = "address.cityCode")
  @Mapping(target = "districtCode", source = "address.districtCode")
  @Mapping(target = "wardCode", source = "address.wardCode")
  @Mapping(target = "cityValue", source = "address.cityValue")
  @Mapping(target = "districtValue", source = "address.districtValue")
  @Mapping(target = "wardValue", source = "address.wardValue")
  @Mapping(target = "addressLine", source = "address.addressLine")
  @Mapping(target = "addressLinkId", source = "address.addressLinkId")
  SalaryIncomeEntity toSalaryIncomeEntity(SalaryIncomeDTO salaryIncome);

  @Mapping(target = "cityCode", source = "address.cityCode")
  @Mapping(target = "districtCode", source = "address.districtCode")
  @Mapping(target = "wardCode", source = "address.wardCode")
  @Mapping(target = "cityValue", source = "address.cityValue")
  @Mapping(target = "districtValue", source = "address.districtValue")
  @Mapping(target = "wardValue", source = "address.wardValue")
  @Mapping(target = "assetAddress", source = "address.addressLine")
  @Mapping(target = "addressLinkId", source = "address.addressLinkId")
  RentalIncomeEntity toRentalIncomeEntity(RentalIncomeDTO rentalIncome);

  @Mapping(target = "cityCode", source = "address.cityCode")
  @Mapping(target = "districtCode", source = "address.districtCode")
  @Mapping(target = "wardCode", source = "address.wardCode")
  @Mapping(target = "cityValue", source = "address.cityValue")
  @Mapping(target = "districtValue", source = "address.districtValue")
  @Mapping(target = "wardValue", source = "address.wardValue")
  @Mapping(target = "addressLine", source = "address.addressLine")
  @Mapping(target = "addressLinkId", source = "address.addressLinkId")
  IndividualEnterpriseIncomeEntity toBizEntity(BusinessIncomeDTO businessIncome);

  OtherIncomeEntity toOtherIncomeEntity(OtherIncomeDTO otherIncome);

  PropertyBusinessIncomeEntity toPropertyBusinessIncomeEntity(
      PropertyBusinessIncomeDTO propertyBusinessIncome);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "salaryIncomes", ignore = true)
  @Mapping(target = "individualEnterpriseIncomes", ignore = true)
  @Mapping(target = "rentalIncomes", ignore = true)
  @Mapping(target = "otherIncomes", ignore = true)
  @Mapping(target = "propertyBusinessIncomes", ignore = true)
  void referenceApplicationIncomeEntity(@MappingTarget ApplicationIncomeEntity e1,
      ApplicationIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  void referenceSalaryIncomeEntity(@MappingTarget SalaryIncomeEntity e1, SalaryIncomeEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpSalaryId")
  @Mapping(target = "incomeType")
  @Mapping(target = "incomeTypeValue")
  @Mapping(target = "incomeOwner")
  @Mapping(target = "incomeOwnerValue")
  @Mapping(target = "incomeOwnerName")
  @Mapping(target = "companyName")
  @Mapping(target = "position")
  @Mapping(target = "payType")
  @Mapping(target = "payTypeValue")
  @Mapping(target = "laborTypeValue")
  @Mapping(target = "cityCode")
  @Mapping(target = "cityValue")
  @Mapping(target = "districtCode")
  @Mapping(target = "districtValue")
  @Mapping(target = "wardCode")
  @Mapping(target = "wardValue")
  @Mapping(target = "addressLine")
  @Mapping(target = "phoneWork")
  @Mapping(target = "refCustomerId")
  void cmsReferenceSalaryIncomeEntity(@MappingTarget SalaryIncomeEntity e1, SalaryIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  void referenceRentalIncomeEntity(@MappingTarget RentalIncomeEntity e1, RentalIncomeEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpRentalId")
  @Mapping(target = "incomeType")
  @Mapping(target = "incomeTypeValue")
  @Mapping(target = "incomeOwner")
  @Mapping(target = "incomeOwnerValue")
  @Mapping(target = "incomeOwnerName")
  @Mapping(target = "assetType")
  @Mapping(target = "assetTypeValue")
  @Mapping(target = "assetOwner")
  @Mapping(target = "rentalPurpose")
  @Mapping(target = "rentalPurposeValue")
  @Mapping(target = "assetAddress")
  @Mapping(target = "renter")
  @Mapping(target = "renterPhone")
  @Mapping(target = "rentalPrice")
  @Mapping(target = "refCustomerId")
  void cmsReferenceRentalIncomeEntity(@MappingTarget RentalIncomeEntity e1, RentalIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  void referenceIndividualEnterpriseIncomeEntity(@MappingTarget IndividualEnterpriseIncomeEntity e1,
      IndividualEnterpriseIncomeEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpBusinessId")
  @Mapping(target = "incomeType")
  @Mapping(target = "incomeTypeValue")
  @Mapping(target = "incomeOwner")
  @Mapping(target = "incomeOwnerValue")
  @Mapping(target = "incomeOwnerName")
  @Mapping(target = "businessRegistrationNumber")
  @Mapping(target = "companyName")
  @Mapping(target = "mainBusinessSector")
  @Mapping(target = "cityCode")
  @Mapping(target = "cityValue")
  @Mapping(target = "districtCode")
  @Mapping(target = "districtValue")
  @Mapping(target = "wardCode")
  @Mapping(target = "wardValue")
  @Mapping(target = "addressLine")
  @Mapping(target = "businessPlaceOwnership")
  @Mapping(target = "businessPlaceOwnershipValue")
  @Mapping(target = "businessExperience")
  @Mapping(target = "capitalContributionRate")
  @Mapping(target = "refCustomerId")
  void cmsReferenceIndividualEnterpriseIncomeEntity(@MappingTarget IndividualEnterpriseIncomeEntity e1,
      IndividualEnterpriseIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  void referenceOtherIncomeEntity(@MappingTarget OtherIncomeEntity e1, OtherIncomeEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpOtherId")
  @Mapping(target = "incomeType")
  @Mapping(target = "incomeTypeValue")
  @Mapping(target = "incomeOwner")
  @Mapping(target = "incomeOwnerValue")
  @Mapping(target = "incomeOwnerName")
  @Mapping(target = "incomeDetail")
  @Mapping(target = "incomeDetailValue")
  @Mapping(target = "incomeInfo")
  @Mapping(target = "refCustomerId")
  void cmsReferenceOtherIncomeEntity(@MappingTarget OtherIncomeEntity e1, OtherIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "customerTransactionIncomes", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  void referencePropertyBusinessIncomeEntity(@MappingTarget PropertyBusinessIncomeEntity e1,
      PropertyBusinessIncomeEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpPropertyBusinessId")
  @Mapping(target = "incomeType")
  @Mapping(target = "incomeTypeValue")
  @Mapping(target = "incomeOwner")
  @Mapping(target = "incomeOwnerValue")
  @Mapping(target = "incomeOwnerName")
  @Mapping(target = "refCustomerId")
  void cmsReferencePropertyBusinessIncomeEntity(@MappingTarget PropertyBusinessIncomeEntity e1,
      PropertyBusinessIncomeEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "propertyBusinessIncome", ignore = true)
  void referenceCustomerTransactionIncomeEntity(@MappingTarget CustomerTransactionIncomeEntity e1,
      CustomerTransactionIncomeEntity e2);

  @Mapping(target = "ldpIncomeId", source = "refIncomeId")
  @Mapping(target = "incomeRecognitionMethod", source = "incomeMethod")
  @Mapping(target = "conversionMethod", source = "conversionMethod")
  @Mapping(target = "recognizedIncome", source = "recognizedIncome")
  ApplicationIncomeEntity cmsIncomeToEntity(CmsIncomeDTO cmsIncome);

  @Mapping(target = "refIncomeId", source = "ldpIncomeId")
  @Mapping(target = "incomeMethod", source = "incomeRecognitionMethod")
  CmsIncomeDTO entityToCmsIncome(ApplicationIncomeEntity applicationIncomeEntity);

  @Mapping(target = "ldpSalaryId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  SalaryIncomeEntity cmsSalaryToEntity(CmsSalaryDTO cmsSalary);

  @Mapping(target = "refIncomeItemId", source = "salaryIncome.ldpSalaryId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(salaryIncome.getRefCustomerId()))")
  @Mapping(target = "phoneWork", source = "salaryIncome.phoneWork")
  CmsSalaryDTO entityToCmsSalaryIncome(SalaryIncomeEntity salaryIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "ldpRentalId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  RentalIncomeEntity cmsRentalToEntity(CmsRentalIncomeDTO cmsRental);

  @Mapping(target = "refIncomeItemId", source = "rentalIncome.ldpRentalId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(rentalIncome.getRefCustomerId()))")
  CmsRentalIncomeDTO entitiesToCmsRentalIncome(RentalIncomeEntity rentalIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "ldpBusinessId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  IndividualEnterpriseIncomeEntity cmsIndividualToEntity(
      CmsIndividualBusinessDTO cmsIndividualBusiness);

  @Mapping(target = "refIncomeItemId", source = "individualEnterpriseIncome.ldpBusinessId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(individualEnterpriseIncome.getRefCustomerId()))")
  @Mapping(target = "businessPlaceOwnership",source = "individualEnterpriseIncome.businessPlaceOwnership")
  @Mapping(target = "businessExperience",source = "individualEnterpriseIncome.businessExperience")
  CmsIndividualBusinessDTO entityToCmsIndividualIncome(
      IndividualEnterpriseIncomeEntity individualEnterpriseIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "refIncomeItemId", source = "individualEnterpriseIncome.ldpBusinessId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(individualEnterpriseIncome.getRefCustomerId()))")
  @Mapping(target = "businessOwnerStatus",source = "individualEnterpriseIncome.businessPlaceOwnership")
  @Mapping(target = "businessExperience",source = "individualEnterpriseIncome.businessExperience")
  CmsEnterpriseBusinessDTO entiCmsEnterpriseBusinessDto(
      IndividualEnterpriseIncomeEntity individualEnterpriseIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "ldpBusinessId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  @Mapping(target = "businessPlaceOwnership", source = "businessOwnerStatus")
  IndividualEnterpriseIncomeEntity cmsEnterpriseToEntity(
      CmsEnterpriseBusinessDTO cmsEnterpriseBusinessDTO);

  @Mapping(target = "refIncomeItemId", source = "individualEnterpriseIncome.ldpBusinessId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(individualEnterpriseIncome.getRefCustomerId()))")
  CmsEnterpriseBusinessDTO entityToCmsEnterpriseIncome(
      IndividualEnterpriseIncomeEntity individualEnterpriseIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "ldpOtherId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  OtherIncomeEntity cmsOtherToEntity(CmsOtherDTO cmsOther);

  @Mapping(target = "refIncomeItemId", source = "otherIncome.ldpOtherId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(otherIncome.getRefCustomerId()))")
  CmsOtherDTO entityToCmsOtherIncome(OtherIncomeEntity otherIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "ldpPropertyBusinessId", source = "refIncomeItemId")
  @Mapping(target = "refCustomerId", source = "customerAdditionalId")
  PropertyBusinessIncomeEntity cmsPropertyToEntity(CmsPropertyIncomeDTO cmsPropertyIncome);

  @Mapping(target = "refIncomeItemId", source = "propertyBusinessIncome.ldpPropertyBusinessId")
  @Mapping(target = "refCusId", expression = "java(mapRefCustomerIdAndRefCusId.get(propertyBusinessIncome.getRefCustomerId()))")
  CmsPropertyIncomeDTO entityToCmsPropertyIncome(
      PropertyBusinessIncomeEntity propertyBusinessIncome,
      Map<Long, String> mapRefCustomerIdAndRefCusId);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "application", ignore = true)
  ApplicationIncomeEntity copyApplicationIncomeEntity(ApplicationIncomeEntity applicationIncomeEntity);

  Set<ApplicationIncomeEntity> copyApplicationIncomeEntities(Set<ApplicationIncomeEntity> applicationIncomeEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  @Mapping(target = "ldpSalaryId", ignore = true)
  @Mapping(target = "oldId", source = "id")
  SalaryIncomeEntity copySalaryIncomeEntity(SalaryIncomeEntity salaryIncome);

  Set<SalaryIncomeEntity> copySalaryIncomeEntities(Set<SalaryIncomeEntity> salaryIncomes);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  @Mapping(target = "ldpBusinessId", ignore = true)
  @Mapping(target = "oldId", source = "id")
  IndividualEnterpriseIncomeEntity copyIndividualEnterpriseIncomeEntity(IndividualEnterpriseIncomeEntity individualEnterpriseIncome);

  Set<IndividualEnterpriseIncomeEntity> copyIndividualEnterpriseIncomeEntities(Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  @Mapping(target = "ldpOtherId", ignore = true)
  @Mapping(target = "oldId", source = "id")
  OtherIncomeEntity copyOtherIncomeEntity(OtherIncomeEntity otherIncome);

  Set<OtherIncomeEntity> copyOtherIncomeEntities(Set<OtherIncomeEntity> otherIncomeEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  @Mapping(target = "ldpRentalId", ignore = true)
  @Mapping(target = "oldId", source = "id")
  RentalIncomeEntity copyRentalIncomeEntity(RentalIncomeEntity rentalIncomeEntity);

  Set<RentalIncomeEntity> copyRentalIncomeEntities(Set<RentalIncomeEntity> rentalIncomeEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationIncomes", ignore = true)
  @Mapping(target = "ldpPropertyBusinessId", ignore = true)
  @Mapping(target = "oldId", source = "id")
  PropertyBusinessIncomeEntity copyPropertyBusinessIncomeEntity(PropertyBusinessIncomeEntity propertyBusinessIncomeEntity);

  Set<PropertyBusinessIncomeEntity> copyPropertyBusinessIncomeEntities(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomeEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "propertyBusinessIncome", ignore = true)
  CustomerTransactionIncomeEntity copyCustomerTransactionIncomeEntity(CustomerTransactionIncomeEntity customerTransactionIncomeEntity);

  Set<CustomerTransactionIncomeEntity> copyCustomerTransactionIncomeEntities(Set<CustomerTransactionIncomeEntity> customerTransactionIncomeEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  IncomeEvaluationEntity copyIncomeEvaluationEntity(IncomeEvaluationEntity incomeEvaluation);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "incomeEvaluation", ignore = true)
  TotalAssetIncomeEntity copyTotalAssetIncomeEntity(TotalAssetIncomeEntity totalAssetIncomeEntity);

  Set<TotalAssetIncomeEntity> copyTotalAssetIncomeEntities(Set<TotalAssetIncomeEntity> totalAssetIncomeEntities);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpIncomeId")
  @Mapping(target = "incomeRecognitionMethod")
  @Mapping(target = "incomeRecognitionMethodValue")
  @Mapping(target = "conversionMethod")
  @Mapping(target = "conversionMethodValue")
  @Mapping(target = "recognizedIncome")
  void cmsReferenceApplicationIncomeEntity(@MappingTarget ApplicationIncomeEntity e1,
      ApplicationIncomeEntity e2);

  default String getAddressTypeForCBT(RentalIncomeEntity rentalIncome) {
    if (VEHICLE.getCategoryCode().equalsIgnoreCase(rentalIncome.getAssetType())) {
      return ApplicationConstant.CBT_DEFAULT_ADDRESS.ADDRESS_TYPE;
    }
    return null;
  }

  default String getAddressTypeValueForCBT(RentalIncomeEntity rentalIncome) {
    if (VEHICLE.getCategoryCode().equalsIgnoreCase(rentalIncome.getAssetType())) {
      return ApplicationConstant.CBT_DEFAULT_ADDRESS.ADDRESS_TYPE_VALUE;
    }
    return null;
  }

}
