package com.msb.bpm.approval.appr.service.application.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.AssetCommonInfoDTO;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.service.asset.AssetInfoService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostAssetInfoServiceImplTest {
    @Mock
    ObjectMapper objectMapper;
    @Mock
    AssetInfoService assetInfoService;

    @InjectMocks
    PostAssetInfoServiceImpl postAssetInfoService;

    PostAssetInfoRequest request;
    @BeforeEach
    public void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        request = JsonUtil.convertString2Object("{\n" +
                "    \"bpmId\": \"151-00010806\",\n" +
                "    \"type\": \"POST_ASSET_INFO\",\n" +
                "    \"collateral\": {\n" +
                "        \"applicationId\": \"151-00010806\",\n" +
                "        \"businessType\": \"BPM\",\n" +
                "        \"customerLoans\": [\n" +
                "            {\n" +
                "                \"customerId\": 27968,\n" +
                "                \"customerVersion\": 117,\n" +
                "                \"identityCode\": \"0010820098191\",\n" +
                "                \"cif\": null\n" +
                "            }\n" +
                "        ],\n" +
                "        \"assetData\": [\n" +
                "            {\n" +
                "                \"id\": 1636,\n" +
                "                \"status\": \"ACTIVE\",\n" +
                "                \"assetGroup\": null,\n" +
                "                \"assetGroupName\": \"Bất động sản\",\n" +
                "                \"assetType\": \"V401\",\n" +
                "                \"ownerTypeName\": \"Khách hàng\",\n" +
                "                \"assetTypeName\": \"Đất ở\",\n" +
                "                \"assetCode\": \"132123123\",\n" +
                "                \"ownerType\": \"V001\",\n" +
                "                \"assetName\": \"Bất động sản là Thửa đất số 123123123, tờ bản đồ số 123123123, tại 123123123, Xã Thanh Lương, Thị xã Nghĩa Lộ, Tỉnh Yên Bái theo GCN QSD đất số 123123123 do 123123 ngày 20/02/2024, đứng tên  Hoàng\",\n" +
                "                \"nextValuationDay\": null,\n" +
                "                \"addressLinkId\": \"V3xLQiMIbeWn\",\n" +
                "                \"applicationId\": \"151-00010806\",\n" +
                "                \"assetVersion\": 2,\n" +
                "                \"assetCodeCore\": null,\n" +
                "                \"mortgageStatus\": \"V001\",\n" +
                "                \"mortgageStatusName\": \"Đã thế chấp\",\n" +
                "                \"hasComponent\": true,\n" +
                "                \"assetAdditionalInfo\": {\n" +
                "                    \"components\": [\n" +
                "                        {\n" +
                "                            \"id\": 1338,\n" +
                "                            \"name\": \"test122\",\n" +
                "                            \"value\": \"123123123123\",\n" +
                "                            \"description\": null\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"ownerInfo\": [\n" +
                "                        {\n" +
                "                            \"customerId\": 1931,\n" +
                "                            \"customerRefCode\": \"0010820098191\",\n" +
                "                            \"customerName\": \" Hoàng\",\n" +
                "                            \"relationshipCode\": \"V017\",\n" +
                "                            \"relationshipName\": \"Khách hàng\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"collateralAssetInfo\": {\n" +
                "                        \"id\": 6734,\n" +
                "                        \"description\": null,\n" +
                "                        \"proposalCollateralValue\": \"12312312312312\",\n" +
                "                        \"valuationGuaranteed\": null,\n" +
                "                        \"ltv\": 100\n" +
                "                    },\n" +
                "                    \"valuationInfo\": null,\n" +
                "                    \"realEstateInfo\": {\n" +
                "                        \"id\": 3091,\n" +
                "                        \"provinceCode\": \"1\",\n" +
                "                        \"provinceName\": \"Tỉnh Yên Bái\",\n" +
                "                        \"districtCode\": \"656\",\n" +
                "                        \"districtName\": \"Thị xã Nghĩa Lộ\",\n" +
                "                        \"wardCode\": \"12727\",\n" +
                "                        \"wardName\": \"Xã Thanh Lương\",\n" +
                "                        \"streetNumber\": \"123123123\",\n" +
                "                        \"houseNumber\": null,\n" +
                "                        \"floor\": null,\n" +
                "                        \"landPlot\": \"123123123\",\n" +
                "                        \"mapLocation\": null,\n" +
                "                        \"investorInformation\": null,\n" +
                "                        \"isLoanPurpose\": null,\n" +
                "                        \"landParcel\": \"123123123\",\n" +
                "                        \"payMethod\": null,\n" +
                "                        \"buildingWork\": false,\n" +
                "                        \"futureAsset\": true,\n" +
                "                        \"description\": \"12312312312312\"\n" +
                "                    },\n" +
                "                    \"legalDocumentInfo\": {\n" +
                "                        \"id\": 5641,\n" +
                "                        \"description\": \"\",\n" +
                "                        \"docType\": \"V001\",\n" +
                "                        \"docName\": \"GCN QSD đất\",\n" +
                "                        \"docValue\": \"123123123\",\n" +
                "                        \"dateOfIssue\": \"2024-02-20T00:00:00\",\n" +
                "                        \"issuedBy\": \"123123\"\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 1771,\n" +
                "                \"status\": \"ACTIVE\",\n" +
                "                \"assetGroup\": \"V011\",\n" +
                "                \"assetGroupName\": \"Tiền\",\n" +
                "                \"assetType\": \"V101\",\n" +
                "                \"ownerTypeName\": \"Khách hàng\",\n" +
                "                \"assetTypeName\": \"Số dư tài khoản tại MSB\",\n" +
                "                \"assetCode\": \"123123123123\",\n" +
                "                \"ownerType\": \"V001\",\n" +
                "                \"assetName\": \"Số dư tài khoản tại MSB có định danh tài sản 123123123123 đứng tên  Hoàng\",\n" +
                "                \"nextValuationDay\": null,\n" +
                "                \"addressLinkId\": \"7JsAyLiM9bBX\",\n" +
                "                \"applicationId\": \"151-00010806\",\n" +
                "                \"assetVersion\": 2,\n" +
                "                \"assetCodeCore\": null,\n" +
                "                \"mortgageStatus\": \"V001\",\n" +
                "                \"mortgageStatusName\": \"Đã thế chấp\",\n" +
                "                \"hasComponent\": null,\n" +
                "                \"assetAdditionalInfo\": {\n" +
                "                    \"ownerInfo\": [\n" +
                "                        {\n" +
                "                            \"customerId\": 1931,\n" +
                "                            \"customerRefCode\": \"0010820098191\",\n" +
                "                            \"customerName\": \" Hoàng\",\n" +
                "                            \"relationshipCode\": \"V017\",\n" +
                "                            \"relationshipName\": \"Khách hàng\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"collateralAssetInfo\": {\n" +
                "                        \"id\": 6739,\n" +
                "                        \"description\": null,\n" +
                "                        \"proposalCollateralValue\": \"123123123123\",\n" +
                "                        \"valuationGuaranteed\": null,\n" +
                "                        \"ltv\": 1\n" +
                "                    },\n" +
                "                    \"otherInfo\": {\n" +
                "                        \"id\": 1235,\n" +
                "                        \"description\": \"sdfgdfsdfsdasdfasdf\"\n" +
                "                    },\n" +
                "                    \"valuationInfo\": null\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 1772,\n" +
                "                \"status\": null,\n" +
                "                \"assetGroup\": \"V011\",\n" +
                "                \"assetGroupName\": \"Tiền\",\n" +
                "                \"assetType\": \"V102\",\n" +
                "                \"ownerTypeName\": \"Khách hàng\",\n" +
                "                \"assetTypeName\": \"Các loại tiền mặt\",\n" +
                "                \"assetCode\": \"1235123452345\",\n" +
                "                \"ownerType\": \"V001\",\n" +
                "                \"assetName\": \"Các loại tiền mặt có định danh tài sản 1235123452345 đứng tên  Hoàng\",\n" +
                "                \"nextValuationDay\": null,\n" +
                "                \"addressLinkId\": \"E1BLd5pln8XZ\",\n" +
                "                \"applicationId\": null,\n" +
                "                \"assetVersion\": 1,\n" +
                "                \"assetCodeCore\": null,\n" +
                "                \"mortgageStatus\": \"V001\",\n" +
                "                \"mortgageStatusName\": \"Đã thế chấp\",\n" +
                "                \"hasComponent\": null,\n" +
                "                \"assetAdditionalInfo\": {\n" +
                "                    \"ownerInfo\": [\n" +
                "                        {\n" +
                "                            \"customerId\": 1931,\n" +
                "                            \"customerRefCode\": \"0010820098191\",\n" +
                "                            \"customerName\": \" Hoàng\",\n" +
                "                            \"relationshipCode\": \"V017\",\n" +
                "                            \"relationshipName\": \"Khách hàng\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"collateralAssetInfo\": {\n" +
                "                        \"id\": 5997,\n" +
                "                        \"description\": null,\n" +
                "                        \"proposalCollateralValue\": \"123451234123412\",\n" +
                "                        \"valuationGuaranteed\": null,\n" +
                "                        \"ltv\": \"1\"\n" +
                "                    },\n" +
                "                    \"otherInfo\": {\n" +
                "                        \"id\": 1152,\n" +
                "                        \"description\": \"ádfgsdfgdfsgdfgsdffgsdf\"\n" +
                "                    },\n" +
                "                    \"valuationInfo\": null\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}", PostAssetInfoRequest.class,mapper);
    }

    @Test
    void testGetTypeSuccess() {
        assertEquals(POST_ASSET_INFO, postAssetInfoService.getType());
    }

    @ParameterizedTest
    @CsvSource({"fail", "success", "invalid"})
    void testSaveAssetInfoSuccess(String caseEx) {
        String bpmId = "151-0001";
        if("success".equals(caseEx)) {
            AssetCommonInfoDTO response = Mockito.mock(AssetCommonInfoDTO.class);
            when(assetInfoService.upsertAsset(any())).thenReturn(response);
            Assertions.assertDoesNotThrow(() -> postAssetInfoService.execute(request, bpmId));
        }

        if("fail".equals(caseEx)) {
            when(assetInfoService.upsertAsset(any())).thenThrow(ApprovalException.class);
            Assertions.assertThrows(ApprovalException.class, () -> postAssetInfoService.execute(request, bpmId));
        }

        if("invalid".equals(caseEx)) {
            request.setCollateral(null);
            Assertions.assertThrows(ApprovalException.class, () -> postAssetInfoService.execute(request, bpmId));
        }
    }

    @Test
    void testThrowEx() {
        String bpmId = "151-0001";
        request.setCollateral(new Object());
        request.setAssetData(null);
        Assertions.assertDoesNotThrow(() -> postAssetInfoService.execute(request, bpmId));
    }
}
