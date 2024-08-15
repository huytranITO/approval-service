package com.msb.bpm.approval.appr.service.cache;

import static com.msb.bpm.approval.appr.constant.Constant.Cache.ESB_ACCOUNT_INFO;

import com.msb.bpm.approval.appr.client.esb.EsbCoreClient;
import com.msb.bpm.approval.appr.model.request.esb.CommonInfoRequest;
import com.msb.bpm.approval.appr.model.request.esb.EsbRequest;
import com.msb.bpm.approval.appr.model.response.esb.EsbResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EsbServiceCache {

  private final EsbCoreClient esbCoreClient;

  /*@Cacheable(cacheNames = ESB_ACCOUNT_INFO, key = "#cifNumber", unless="#result == null")*/
  public EsbResponse callGetEsbAccountInfo(String cifNumber) {
    EsbRequest request = new EsbRequest();
    CommonInfoRequest commonInfoRequest = new CommonInfoRequest();
    request.setCommonInfo(commonInfoRequest);
    request.setCif(cifNumber);
    return esbCoreClient.callGetEsbAccountInfo(request);
  }
}
