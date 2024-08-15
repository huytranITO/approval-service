//  package com.msb.bpm.approval.appr.service.application.impl;

//  import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_FIELD_INFO;
//  import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_FORMAT;
//  import static com.msb.bpm.approval.appr.constant.Constant.DD_MM_YYYY_HH_MM_SS_FORMAT;
//  import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//  import static org.junit.jupiter.api.Assertions.assertEquals;
//  import static org.junit.jupiter.api.Assertions.assertNotNull;
//  import static org.junit.jupiter.api.Assertions.assertThrows;
//  import static org.mockito.ArgumentMatchers.anyString;
//  import static org.mockito.Mockito.when;

//  import com.fasterxml.jackson.annotation.JsonInclude.Include;
//  import com.fasterxml.jackson.core.JsonParser;
//  import com.fasterxml.jackson.core.json.JsonReadFeature;
//  import com.fasterxml.jackson.databind.DeserializationFeature;
//  import com.fasterxml.jackson.databind.ObjectMapper;
//  import com.fasterxml.jackson.databind.SerializationFeature;
//  import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//  import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//  import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//  import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//  import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//  import com.msb.bpm.approval.appr.config.StringTrimModuleConfig;
//  import com.msb.bpm.approval.appr.exception.ApprovalException;
//  import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
//  import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
//  import com.msb.bpm.approval.appr.model.request.data.PostFieldInformationRequest;
//  import com.msb.bpm.approval.appr.repository.ApplicationRepository;
//  import com.msb.bpm.approval.appr.util.JsonUtil;
//  import java.time.LocalDate;
//  import java.time.LocalDateTime;
//  import java.time.format.DateTimeFormatter;
//  import java.util.Optional;
//  import org.junit.jupiter.api.BeforeEach;
//  import org.junit.jupiter.api.Test;
//  import org.junit.jupiter.params.ParameterizedTest;
//  import org.junit.jupiter.params.provider.CsvSource;
//  import org.mockito.InjectMocks;
//  import org.mockito.Mock;
//  import org.mockito.MockitoAnnotations;

//  /**
//   * @author : Manh Nguyen Van (CN-SHQLQT)
//   * @mailto : manhnv8@msb.com.vn
//   * @created : 22/10/2023, Sunday
//   **/
//  class PostFieldInformationServiceImplTest {

//    @Mock
//    private ApplicationRepository applicationRepository;

//    @InjectMocks
//    private PostFieldInformationServiceImpl postFieldInformationService;

//    private PostFieldInformationRequest postFieldInformationRequest;

//    @BeforeEach
//    public void setUp() {
//      MockitoAnnotations.openMocks(this);
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(
//          JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),
//          true);
//      mapper.setSerializationInclusion(Include.ALWAYS);
//      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
//      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//      mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//      mapper.registerModule(new StringTrimModuleConfig());
//      mapper.registerModule(javaTimeModule());
//      postFieldInformationRequest = JsonUtil.convertString2Object("{\n"
//          + "  \"type\": \"POST_FIELD_INFO\",\n"
//          + "  \"fieldInformations\": [\n"
//          + "    {\n"
//          + "      \"placeType\": \"V001\",\n"
//          + "      \"placeTypeValue\": \"Địa chỉ hộ khẩu thường trú\",\n"
//          + "      \"relationship\": \"V001\",\n"
//          + "      \"relationshipValue\": \"Người hôn phối không bảo lãnh trả nợ\",\n"
//          + "      \"timeAt\": \"22/10/2023\",\n"
//          + "      \"instructor\": \"1\",\n"
//          + "      \"executor\": \"thietpt\",\n"
//          + "      \"result\": \"V001\",\n"
//          + "      \"resultValue\": \"Phù hợp với thông tin kê khai\",\n"
//          + "      \"cityCode\": \"50\",\n"
//          + "      \"cityValue\": \"Thành phố Cần Thơ\",\n"
//          + "      \"districtCode\": \"107\",\n"
//          + "      \"districtValue\": \"Huyện Cờ Đỏ\",\n"
//          + "      \"wardCode\": \"1458\",\n"
//          + "      \"wardValue\": \"Thị trấn Cờ Đỏ\",\n"
//          + "      \"addressLine\": null,\n"
//          + "      \"addressLinkId\": \"4gbXlyag5ovz\",\n"
//          + "      \"edit\": false,\n"
//          + "      \"orderDisplay\": 0,\n"
//          + "      \"id\": 1\n"
//          + "    },\n"
//          + "    {\n"
//          + "      \"placeType\": \"V002\",\n"
//          + "      \"placeTypeValue\": \"Địa chỉ sinh sống hiện tại\",\n"
//          + "      \"relationship\": \"V004\",\n"
//          + "      \"relationshipValue\": \"Mẹ ruột/ mẹ nuôi hợp pháp\",\n"
//          + "      \"timeAt\": \"\",\n"
//          + "      \"instructor\": \"\",\n"
//          + "      \"executor\": \"thietpt\",\n"
//          + "      \"result\": null,\n"
//          + "      \"resultValue\": \"\",\n"
//          + "      \"cityCode\": \"37\",\n"
//          + "      \"cityValue\": \"Thành phố Hải Phòng\",\n"
//          + "      \"districtCode\": \"556\",\n"
//          + "      \"districtValue\": \"Huyện Cát Hải\",\n"
//          + "      \"wardCode\": \"1282\",\n"
//          + "      \"wardValue\": \"Xã Đồng Bài\",\n"
//          + "      \"addressLine\": null,\n"
//          + "      \"addressLinkId\": \"ECIg4AbwKpVv\",\n"
//          + "      \"edit\": true,\n"
//          + "      \"orderDisplay\": 1\n"
//          + "    }\n"
//          + "  ],\n"
//          + "  \"bpmId\": \"151-00007416\"\n"
//          + "}", PostFieldInformationRequest.class, mapper);
//    }

//    @Test
//    void test_getType_should_be_ok() {
//      assertEquals(POST_FIELD_INFO, postFieldInformationService.getType());
//    }

//    @Test
//    void test_saveData_should_be_ok() {
//      ApplicationFieldInformationEntity updateFieldInformationEntity = new ApplicationFieldInformationEntity();
//      updateFieldInformationEntity.setId(1L);

//      ApplicationFieldInformationEntity deleteFieldInformationEntity = new ApplicationFieldInformationEntity();
//      deleteFieldInformationEntity.setId(2L);

//      ApplicationEntity applicationEntity = new ApplicationEntity();
//      applicationEntity.getFieldInformations().add(updateFieldInformationEntity);
//      applicationEntity.getFieldInformations().add(deleteFieldInformationEntity);

//      assertDoesNotThrow(() -> postFieldInformationService.saveData(applicationEntity, postFieldInformationRequest));
//    }

//    @ParameterizedTest
//    @CsvSource({"false,unknown2,","true,unknown,","false,unknown,9999","false,unknown,0000"})
//    void test_execute_should_be_ok(boolean notFoundApp, String currentAssignee, String currentStatus) {
//      if (notFoundApp) {
//        assertThrows(ApprovalException.class, () -> postFieldInformationService.execute(postFieldInformationRequest, "151-00000001"));
//      } else {
//        ApplicationEntity applicationEntity = new ApplicationEntity();
//        applicationEntity.setAssignee(currentAssignee);
//        applicationEntity.setStatus(currentStatus);
//        when(applicationRepository.findByBpmId(anyString())).thenReturn(Optional.of(applicationEntity));
//        if ("unknown2".equals(currentAssignee) || "9999".equals(currentStatus)) {
//          assertThrows(ApprovalException.class, () -> postFieldInformationService.execute(postFieldInformationRequest, "151-00000001"));
//        } else {
//          test_saveData_should_be_ok();
//          assertNotNull(postFieldInformationService.execute(postFieldInformationRequest, "151-00000001"));
//        }
//      }
//    }

//    public JavaTimeModule javaTimeModule() {
//      JavaTimeModule javaTimeModule = new JavaTimeModule();

//      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS_FORMAT);
//      LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
//      LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);
//      javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
//      javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

//      formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_FORMAT);
//      LocalDateDeserializer dateDeserializer = new LocalDateDeserializer(formatter);
//      LocalDateSerializer dateSerializer = new LocalDateSerializer(formatter);
//      javaTimeModule.addDeserializer(LocalDate.class, dateDeserializer);
//      javaTimeModule.addSerializer(LocalDate.class, dateSerializer);

//      return javaTimeModule;
//    }
//  }
