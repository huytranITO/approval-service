package com.msb.bpm.approval.appr.service.minio;

import com.msb.bpm.approval.appr.model.dto.statisticfile.UploadFileResult;
import io.minio.ObjectWriteResponse;
import java.io.InputStream;
import java.util.Map;

public interface MinIOService {
  void copyBucketByFilePathMinIO(String filePath, String prefix, String bucketSrc);
  Map<String, Object> getSizeFileMinIOByPathFile(String pathFile);

  ObjectWriteResponse uploadFileMinIO(InputStream inputStream, String path);

  void uploadPdfFile(String bpmApplicationId, byte[] reportFileData, UploadFileResult uploadFileResult);
}
