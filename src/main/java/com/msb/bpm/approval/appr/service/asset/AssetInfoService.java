package com.msb.bpm.approval.appr.service.asset;

import com.msb.bpm.approval.appr.model.request.data.PostAssetInfoRequest;

public interface AssetInfoService {
    Object upsertAsset(PostAssetInfoRequest req);
}
