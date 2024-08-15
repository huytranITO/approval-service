package com.msb.bpm.approval.appr.service.cms.impl;


import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msb.bpm.approval.appr.client.camunda.CamundaProperties;
import com.msb.bpm.approval.appr.client.checklist.ChecklistClient;
import com.msb.bpm.approval.appr.client.collateral.CollateralClient;
import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.config.properties.MinioProperties.Bucket;
import com.msb.bpm.approval.appr.config.properties.MinioProperties.Minio;
import com.msb.bpm.approval.appr.model.dto.checklist.ChecklistDto;
import com.msb.bpm.approval.appr.model.dto.checklist.GroupChecklistDto;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerRelationShipEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.request.cms.PostCmsV2DocumentsRequest;
import com.msb.bpm.approval.appr.model.response.checklist.ChecklistBaseResponse;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse.Content;
import com.msb.bpm.approval.appr.model.response.checklist.SearchResponse.Data;
import com.msb.bpm.approval.appr.model.response.collateral.AssetRuleChecklistResponse;
import com.msb.bpm.approval.appr.repository.CustomerRepository;
import com.msb.bpm.approval.appr.repository.RuleVersionMappingRepository;
import com.msb.bpm.approval.appr.service.camunda.CamundaService;
import com.msb.bpm.approval.appr.service.checklist.impl.ChecklistServiceImpl;
import com.msb.bpm.approval.appr.service.intergated.CommonService;
import com.msb.bpm.approval.appr.service.minio.MinIOService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CmsBaseIntegrationServiceTest {

  private ChecklistBaseResponse<GroupChecklistDto> checklistBaseResponse;
  private GroupChecklistDto groupChecklistDto;
  private ChecklistDto checklistDto;
  private ApplicationEntity entityApp;
  @Mock
  private ObjectMapper objectMapper;
  @InjectMocks
  private CmsBaseIntegrationService cmsBaseIntegrationService;
  private PostCmsV2DocumentsRequest request;
  @InjectMocks
  private CmsPushDocumentsServiceImpl cmsPushDocumentsService;
  @Mock
  private RuleVersionMappingRepository ruleVersionMappingRepository;
  @Mock
  private ChecklistServiceImpl checklistService;
  @Mock
  private MinioProperties minioProperties;
  @Mock
  private MinIOService minIOService;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private CommonService commonService;
  @Mock
  private CamundaProperties camundaProperties;
  @Mock
  private CamundaService camundaService;
  @Mock
  private CollateralClient collateralClient;
  @Mock
  private ChecklistClient checklistClient;
  @BeforeEach
  public void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(javaTimeModule());
    CustomerEntity customer = new CustomerEntity();
    customer.setRefCustomerId(1270L);
    customer.setCustomerType("RB");
    customer.setRefCusId("bd109876ee-02");
    entityApp = new ApplicationEntity();
    entityApp.setId(6116L);
    entityApp.setRefId("bd109876ee-02");
    Set<CustomerRelationShipEntity> customerRelationShipEntities = new HashSet<>();
    CustomerRelationShipEntity customerRelationShipEntity = new CustomerRelationShipEntity();
    customerRelationShipEntity.setId(3L);
    customerRelationShipEntity.setRelationship("V001");
    customerRelationShipEntity.setCustomer(customer);
    customerRelationShipEntity.setCustomerRefId(1L);
    customerRelationShipEntities.add(customerRelationShipEntity);
    customer.setCustomerRelationShips(customerRelationShipEntities);

    ApplicationIncomeEntity incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(1l);
    incomeEntity.setLdpIncomeId("88bb7abd-3f21-11ee-9faf-0242ac120002");
    SalaryIncomeEntity salaryIncomeEntity = new SalaryIncomeEntity();
    salaryIncomeEntity.setId(1270l);
    salaryIncomeEntity.setLdpSalaryId("af6bd22d-3f21-11ee-9faf-0242ac120002");
    incomeEntity.getSalaryIncomes().add(salaryIncomeEntity);
    entityApp.getIncomes().add(incomeEntity);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(2l);
    incomeEntity.setLdpIncomeId("89fb718b-3f2e-11ee-9faf-0242ac120002");
    IndividualEnterpriseIncomeEntity businessEntity = new IndividualEnterpriseIncomeEntity();
    businessEntity.setId(2l);
    businessEntity.setLdpBusinessId("92f39280-3f2e-11ee-9faf-0242ac120002");
    incomeEntity.getIndividualEnterpriseIncomes().add(businessEntity);
    entityApp.getIncomes().add(incomeEntity);

    ApplicationIncomeEntity retail = new ApplicationIncomeEntity();
    RentalIncomeEntity rentalIncome = new RentalIncomeEntity();
    rentalIncome.setId(1270l);
    rentalIncome.setLdpRentalId("89fb718b-3f2e-11ee-9faf-0242ac120002");
    retail.getRentalIncomes().add(rentalIncome);
    entityApp.getIncomes().add(retail);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(3l);
    incomeEntity.setLdpIncomeId("c790fcb9-3f2e-11ee-9faf-0242ac120002");
    OtherIncomeEntity otherIncomeEntity = new OtherIncomeEntity();
    otherIncomeEntity.setId(3l);
    otherIncomeEntity.setLdpOtherId("cb14581d-3f2e-11ee-9faf-0242ac120002");
    incomeEntity.getOtherIncomes().add(otherIncomeEntity);
    entityApp.getIncomes().add(incomeEntity);

    incomeEntity = new ApplicationIncomeEntity();
    incomeEntity.setId(4l);
    incomeEntity.setLdpIncomeId("659039ef-418e-11ee-9faf-0242ac120002");
    PropertyBusinessIncomeEntity propertyBusinessIncome = new PropertyBusinessIncomeEntity();
    propertyBusinessIncome.setId(1270l);
    propertyBusinessIncome.setLdpPropertyBusinessId("6a6f803e-418e-11ee-9faf-0242ac120002");
    incomeEntity.getPropertyBusinessIncomes().add(propertyBusinessIncome);
    entityApp.getIncomes().add(incomeEntity);

    entityApp.setCustomer(customer);
    groupChecklistDto = JsonUtil.convertString2Object(
        "{\"completed\":false,\"requestCode\":\"151-00006116\",\"isCompleted\":false,\"listGroup\":[{\"id\":10,\"code\":\"10\",\"name\":\"Hồ sơ pháp lý người liên quan\",\"parentId\":1,\"orderDisplay\":3,\"domainType\":\"CUSTOMER\",\"domainObjectId\":1270,\"ruleVersion\":24},{\"id\":14,\"code\":\"14\",\"name\":\"Nguồn thu doanh nghiệp\",\"parentId\":3,\"orderDisplay\":4,\"domainType\":\"INCOME\",\"domainObjectId\":2080,\"ruleVersion\":19},{\"id\":2,\"code\":\"02\",\"name\":\"Hồ sơ vay vốn\",\"parentId\":null,\"orderDisplay\":2,\"domainType\":\"APPLICATION\",\"domainObjectId\":6116,\"ruleVersion\":24},{\"id\":7,\"code\":\"07\",\"name\":\"Hồ sơ khác\",\"parentId\":null,\"orderDisplay\":7,\"domainType\":\"APPLICATION\",\"domainObjectId\":6116,\"ruleVersion\":7},{\"id\":8,\"code\":\"08\",\"name\":\"Hồ sơ pháp lý khách hàng\",\"parentId\":1,\"orderDisplay\":1,\"domainType\":\"CUSTOMER\",\"domainObjectId\":1269,\"ruleVersion\":24},{\"id\":6,\"code\":\"06\",\"name\":\"Hồ sơ ngân hàng\",\"parentId\":null,\"orderDisplay\":5,\"domainType\":\"APPLICATION\",\"domainObjectId\":6116,\"ruleVersion\":22},{\"id\":5,\"code\":\"05\",\"name\":\"Hồ sơ phê duyệt\",\"parentId\":null,\"orderDisplay\":6,\"domainType\":\"APPLICATION\",\"domainObjectId\":6116,\"ruleVersion\":22},{\"id\":9,\"code\":\"09\",\"name\":\"Hồ sơ pháp lý người hôn phối\",\"parentId\":1,\"orderDisplay\":2,\"domainType\":\"CUSTOMER\",\"domainObjectId\":1277,\"ruleVersion\":24},{\"id\":1,\"code\":\"01\",\"name\":\"Hồ sơ pháp lý\",\"parentId\":null,\"orderDisplay\":1,\"domainType\":null,\"domainObjectId\":null,\"ruleVersion\":24},{\"id\":3,\"code\":\"03\",\"name\":\"Hồ sơ nguồn thu\",\"parentId\":null,\"orderDisplay\":3,\"domainType\":null,\"domainObjectId\":null,\"ruleVersion\":19}],\"listChecklist\":[{\"additionalDataChecklistId\":91062,\"code\":\"0205\",\"name\":\"Thư giới thiệu/Xác nhận trúng tuyển\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":7,\"checklistVersion\":0,\"checklistMappingId\":253,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9287,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9250,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":91063,\"code\":\"0204\",\"name\":\"Bản kê khai và cam kết mục đích SDV\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":5,\"checklistVersion\":0,\"checklistMappingId\":252,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9285,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9286,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9248,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9249,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":91060,\"code\":\"0202\",\"name\":\"Hợp đồng mua bán/Bảng báo giá/Bảng kê hàng hóa\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":250,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9282,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9245,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":91061,\"code\":\"0206\",\"name\":\"Hợp đồng/báo giá tổng chi phí dịch vụ (du lịch, khám/chữa bệnh, cưới hỏi)\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":254,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9288,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9251,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90851,\"code\":\"0101\",\"name\":\"CMND/CCCD/Hộ chiếu\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":220,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9238,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"nhant\",\"props\":null},{\"id\":9267,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9268,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9242,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9243,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9415,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"nhant\",\"props\":null}]},{\"additionalDataChecklistId\":92147,\"code\":\"0101\",\"name\":\"CMND/CCCD/Hộ chiếu\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":227,\"groupCode\":\"09\",\"domainObjectId\":1277,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9292,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9293,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90835,\"code\":\"0107\",\"name\":\"Hồ sơ khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":7,\"checklistVersion\":0,\"checklistMappingId\":226,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9279,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9280,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":92144,\"code\":\"0107\",\"name\":\"Hồ sơ khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":7,\"checklistVersion\":0,\"checklistMappingId\":233,\"groupCode\":\"09\",\"domainObjectId\":1277,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9298,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90853,\"code\":\"0310\",\"name\":\"Xác nhận về doanh số giao dịch/doanh thu\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":5,\"checklistVersion\":0,\"checklistMappingId\":267,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,XLS,XLSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9299,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9300,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9313,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9314,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9306,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9307,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90831,\"code\":\"0207\",\"name\":\"Hợp đồng/Hóa đơn/Báo giá/Hợp đồng nguyên tắc\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":4,\"checklistVersion\":0,\"checklistMappingId\":255,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9289,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9252,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":92143,\"code\":\"0105\",\"name\":\"Uy tín tín dụng\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":4,\"checklistVersion\":0,\"checklistMappingId\":231,\"groupCode\":\"09\",\"domainObjectId\":1277,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9296,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90839,\"code\":\"0102\",\"name\":\"Văn bản ủy quyền giao dịch/Mối quan hệ với người bảo lãnh\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":5,\"checklistVersion\":0,\"checklistMappingId\":221,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9269,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9270,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":92146,\"code\":\"0106\",\"name\":\"Ảnh chụp thực địa nơi cư trú\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":232,\"groupCode\":\"09\",\"domainObjectId\":1277,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9297,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90855,\"code\":\"0311\",\"name\":\"Pháp lý DN\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":268,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9301,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9315,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9308,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9320,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9321,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9322,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":92145,\"code\":\"0103\",\"name\":\"HKTT/Tạm trú/Thị thực/Hồ sơ cư trú khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":229,\"groupCode\":\"09\",\"domainObjectId\":1277,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9294,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9295,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90833,\"code\":\"0504\",\"name\":\"BBPD Hội đồng/Nghị quyết PD\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":237,\"groupCode\":\"05\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90854,\"code\":\"0501\",\"name\":\"Văn bản thẩm định\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":true,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":234,\"groupCode\":\"05\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90832,\"code\":\"0603\",\"name\":\"Hồ sơ ngân hàng khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":212,\"groupCode\":\"06\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90838,\"code\":\"0701\",\"name\":\"Tài liệu khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":209,\"groupCode\":\"07\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90837,\"code\":\"0503\",\"name\":\"Phiếu lấy ý kiến\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":236,\"groupCode\":\"05\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90836,\"code\":\"0507\",\"name\":\"Hồ sơ phê duyệt khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":7,\"checklistVersion\":0,\"checklistMappingId\":238,\"groupCode\":\"05\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90834,\"code\":\"0106\",\"name\":\"Ảnh chụp thực địa nơi cư trú\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":218,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9263,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9264,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90850,\"code\":\"0103\",\"name\":\"HKTT/Tạm trú/Thị thực/Hồ sơ cư trú khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":215,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9257,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9258,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90852,\"code\":\"0105\",\"name\":\"Uy tín tín dụng\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":4,\"checklistVersion\":0,\"checklistMappingId\":217,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9261,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9262,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90841,\"code\":\"0104\",\"name\":\"Tình trạng hôn nhân\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":223,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9273,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9274,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90828,\"code\":\"0314\",\"name\":\"Hình ảnh thực địa DN\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":4,\"checklistVersion\":0,\"checklistMappingId\":271,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9304,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9318,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9311,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":91059,\"code\":\"0203\",\"name\":\"Chứng từ vay nợ để thanh toán tiền\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":8,\"checklistVersion\":0,\"checklistMappingId\":251,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9283,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9284,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9246,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9247,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo4.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.12703514\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90846,\"code\":\"0106\",\"name\":\"Ảnh chụp thực địa nơi cư trú\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":225,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9277,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9278,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90848,\"code\":\"0312\",\"name\":\"BCTC/Tờ khai thuế\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":269,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,XLS,XLSX,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9302,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9316,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9309,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90827,\"code\":\"0313\",\"name\":\"Sao kê tài khoản dòng tiền DN\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":270,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG\",\"ruleVersion\":null,\"listFile\":[{\"id\":9303,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9317,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9310,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90842,\"code\":\"0201\",\"name\":\"Đề nghị CTD\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":249,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF\",\"ruleVersion\":null,\"listFile\":[{\"id\":9235,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9281,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9244,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90840,\"code\":\"0502\",\"name\":\"Văn bản phê duyệt\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":true,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":235,\"groupCode\":\"05\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90826,\"code\":\"0103\",\"name\":\"HKTT/Tạm trú/Thị thực/Hồ sơ cư trú khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":2,\"checklistVersion\":0,\"checklistMappingId\":222,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9271,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9272,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90829,\"code\":\"0105\",\"name\":\"Uy tín tín dụng\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":4,\"checklistVersion\":0,\"checklistMappingId\":224,\"groupCode\":\"10\",\"domainObjectId\":1270,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9275,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9276,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90844,\"code\":\"0209\",\"name\":\"Hồ sơ khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":9,\"checklistVersion\":0,\"checklistMappingId\":257,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9234,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9291,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo6.docx\",\"fileName\":\"hoSo6.docx\",\"fileType\":\"docx\",\"fileSize\":\"1.9386911\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9254,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo6.docx\",\"fileName\":\"hoSo6.docx\",\"fileType\":\"docx\",\"fileSize\":\"1.9386911\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90843,\"code\":\"0601\",\"name\":\"Tờ trình đề xuất\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":true,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":210,\"groupCode\":\"06\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF\",\"ruleVersion\":null,\"listFile\":null},{\"additionalDataChecklistId\":90830,\"code\":\"0315\",\"name\":\"Hồ sơ khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":6,\"checklistVersion\":0,\"checklistMappingId\":272,\"groupCode\":\"14\",\"domainObjectId\":2080,\"domainType\":\"INCOME\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9236,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"cj_bo_rm01\",\"props\":null},{\"id\":9305,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9319,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9312,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90849,\"code\":\"0208\",\"name\":\"Phương án kinh doanh\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":256,\"groupCode\":\"02\",\"domainObjectId\":6116,\"domainType\":\"APPLICATION\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9290,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9253,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo4.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90845,\"code\":\"0101\",\"name\":\"CMND/CCCD/Hộ chiếu\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":1,\"checklistVersion\":0,\"checklistMappingId\":213,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9237,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9343,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9344,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"họa nhá.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9255,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9256,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90847,\"code\":\"0107\",\"name\":\"Hồ sơ khác\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":7,\"checklistVersion\":0,\"checklistMappingId\":219,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,MSG,DOC,DOCX,XLS,XLSX,PPT,PPTX,PPS,PPSX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9265,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9266,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]},{\"additionalDataChecklistId\":90825,\"code\":\"0104\",\"name\":\"Tình trạng hôn nhân\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":false,\"isGenerated\":false,\"isError\":false,\"orderDisplay\":3,\"checklistVersion\":0,\"checklistMappingId\":216,\"groupCode\":\"08\",\"domainObjectId\":1269,\"domainType\":\"CUSTOMER\",\"maxFileSize\":30,\"fileType\":\"PDF,JPG,JEPG,GIF,PNG,DOC,DOCX\",\"ruleVersion\":null,\"listFile\":[{\"id\":9259,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"fileName\":\"hoSo3.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.10488129\",\"createdBy\":\"thietpt\",\"props\":null},{\"id\":9260,\"bucket\":\"bpm-sit\",\"minioPath\":\"mft/cjbo/20230823/151-00006116/hoSo5.pdf\",\"fileName\":\"hoSo5.pdf\",\"fileType\":\"pdf\",\"fileSize\":\"0.08835316\",\"createdBy\":\"thietpt\",\"props\":null}]}]}",
        GroupChecklistDto.class, objectMapper);
    checklistBaseResponse = new ChecklistBaseResponse<>();
    checklistBaseResponse.setData(groupChecklistDto);
    request = JsonUtil.convertString2Object(
        "{\"actionType\":\"New\",\"documents\":[{\"cmsDomainReferenceId\":\"bd109876ee-02\",\"docCode\":\"0101\",\"docName\":\"CMND/CCCD/Hộ chiếu\",\"group\":\"10\",\"files\":[{\"minioPath\":\"bpm-sit/mft/cjbo/20230823/151-00006116/hoSo3.pdf\",\"status\":\"SUCCESS\",\"fileName\":\"hoSo3.pdf\"}],\"metadata\":null}]}",
        PostCmsV2DocumentsRequest.class, objectMapper);

    checklistDto = JsonUtil.convertString2Object(
        "{\"additionalDataChecklistId\":null,\"code\":\"0101\",\"name\":\"CMND/CCCD/Hộ chiếu\",\"returnCode\":null,\"returnDate\":null,\"isRequired\":true,\"isGenerated\":true,\"isError\":false,\"orderDisplay\":null,\"checklistVersion\":null,\"checklistMappingId\":null,\"groupCode\":\"10\",\"domainObjectId\":null,\"domainType\":null,\"maxFileSize\":null,\"fileType\":null,\"ruleVersion\":24,\"listFile\":null}",
        ChecklistDto.class, objectMapper);
  }

  @Test
  void test_setAdditionIdAndChecklistMappingId_successful() throws Exception {
    List<AssetRuleChecklistResponse> assetRuleChecklistResponses = new ArrayList<>();
    Set<CustomerEntity> set = new HashSet<>();
    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setId(1L);
    customerEntity.setRefCusId("1");
    customerEntity.setCustomerType("RB");
    customerEntity.setRefCustomerId(1L);
    customerEntity.setRefCusId("bd109876ee-02");
    set.add(customerEntity);
    SearchResponse response = new SearchResponse();
    Data data = new Data();
    Content content1 = new Content();
    content1.setCode("0101");
    Content content2 = new Content();
    content2.setCode("testCode2");
    List<Content> contents = new ArrayList<>();
    contents.add(content1);
    contents.add(content2);
    data.setContent(contents);
    response.setData(data);
    when(collateralClient.getAssetRuleChecklist(any())).thenReturn(assetRuleChecklistResponses);
    when(customerRepository.findByIdIn(any())).thenReturn(Optional.of(set));
    when(checklistClient.search()).thenReturn(response);
    Assertions.assertDoesNotThrow(() ->
        cmsBaseIntegrationService.setAdditionIdAndChecklistMappingId(checklistBaseResponse, checklistDto, "bd109876ee-02", entityApp));
  }

  @Test
  void test_checkIfNotExistFile_successful() {
    Map<String, Object> mapSizeFile = new HashMap<>();
    mapSizeFile.put("mft/cjbo/20230823/151-00006199/hoSo1.png", "bpm-sit/mft/cjbo/20230823/151-00006199/hoSo1.png");
    Minio minio = new Minio();
    minio.setUrl("https://minio-nonprod.msb.com.vn");
    minio.setAccessKey("b4V1hBU7cpO91p6D");
    minio.setBucket("bpm-sit");
    Map<String, Bucket> bucketExt = new HashMap<>();
    Bucket bucket = new Bucket("bpm-sit","mft/");
    bucketExt.put("cms", bucket);
    minio.setBucketExt(bucketExt);
    when(minioProperties.getMinio()).thenReturn(minio);
    Assertions.assertDoesNotThrow(() ->
        cmsBaseIntegrationService.checkIfNotExistFile(request.getDocuments(), mapSizeFile,"bd109876ee-02"));
  }

  @Test
  void test_convertSizeFile_successful () {
    Assertions.assertDoesNotThrow(() ->
        cmsBaseIntegrationService.convertSizeFile(1L));
  }
  @Test
  void test_getSizeFileFromMap_successful() {
    Map<String, Object> mapSizeFile = new HashMap<>();
    mapSizeFile.put("mft/cjbo/20230823/151-00006199/hoSo1.png", "bpm-sit/mft/cjbo/20230823/151-00006199/hoSo1.png");
    Minio minio = new Minio();
    minio.setUrl("https://minio-nonprod.msb.com.vn");
    minio.setAccessKey("b4V1hBU7cpO91p6D");
    minio.setBucket("bpm-sit");
    Map<String, Bucket> bucketExt = new HashMap<>();
    Bucket bucket = new Bucket("bpm-sit","mft/");
    bucketExt.put("cms", bucket);
    minio.setBucketExt(bucketExt);
    when(minioProperties.getMinio()).thenReturn(minio);
    Assertions.assertDoesNotThrow(() ->
        cmsBaseIntegrationService.getSizeFileFromMap("bpm-sit/mft/cjbo/20230823/151-00006199/hoSo1.png", mapSizeFile));
  }

  @Test
  void test_getSizeFileAll_successful() {
    Minio minio = new Minio();
    minio.setUrl("https://minio-nonprod.msb.com.vn");
    minio.setAccessKey("b4V1hBU7cpO91p6D");
    minio.setBucket("bpm-sit");
    Map<String, Bucket> bucketExt = new HashMap<>();
    Bucket bucket = new Bucket("bpm-sit","mft/");
    bucketExt.put("cms", bucket);
    minio.setBucketExt(bucketExt);
    when(minioProperties.getMinio()).thenReturn(minio);
    Assertions.assertDoesNotThrow(() ->
        cmsBaseIntegrationService.getSizeFileAll("",request.getDocuments()));
  }

  public JavaTimeModule javaTimeModule() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS_FORMAT);
    LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
    LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
    javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

    formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
    LocalDateDeserializer dateDeserializer = new LocalDateDeserializer(formatter);
    LocalDateSerializer dateSerializer = new LocalDateSerializer(formatter);
    javaTimeModule.addDeserializer(LocalDate.class, dateDeserializer);
    javaTimeModule.addSerializer(LocalDate.class, dateSerializer);

    return javaTimeModule;
  }

}
