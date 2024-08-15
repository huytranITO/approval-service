package com.msb.bpm.approval.appr.service.application.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.service.BaseService;
import com.msb.bpm.approval.appr.service.asset.AssetInfoService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.PostBaseRequest.POST_ASSET_INFO;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostAssetInfoServiceImpl extends AbstractBaseService
        implements BaseService<Object, PostAssetInfoRequest> {
    public static final String ASSET_DATA = "assetData";
    private final ObjectMapper objectMapper;
    private final AssetInfoService assetInfoService;
    @Override
    public String getType() {
        return POST_ASSET_INFO;
    }

    @Override
    public Object execute(PostAssetInfoRequest postAssetInfoInfoRequest, Object... obj) {
        log.info("PostAssetInfoServiceImpl.execute() start with bpmId : {} , request : [{}]", obj[0],
                JsonUtil.convertObject2String(postAssetInfoInfoRequest, objectMapper));
        if(ObjectUtils.isEmpty(postAssetInfoInfoRequest)
                || ObjectUtils.isEmpty(postAssetInfoInfoRequest.getCollateral())) {
            throw new ApprovalException(DomainCode.ASSET_ERROR, new Object[]{"Data not null"});
        }

        return assetInfoService.upsertAsset(postAssetInfoInfoRequest);
    }

    private boolean validateRequest(PostAssetInfoRequest postAssetInfoInfoRequest) {
        try {
            if(ObjectUtils.isEmpty(BeanUtils.getProperty(postAssetInfoInfoRequest.getCollateral(), ASSET_DATA))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.info("END PostAssetInfoServiceImpl.execute() exception", e);
            return false;
        }
    }
}
