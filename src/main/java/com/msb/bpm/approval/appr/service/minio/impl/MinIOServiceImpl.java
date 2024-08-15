package com.msb.bpm.approval.appr.service.minio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.msb.bpm.approval.appr.constant.Constant.YYYY_MM_DD_FORMAT;

import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import com.msb.bpm.approval.appr.enums.statisticfile.FileType;
import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.model.dto.statisticfile.UploadFileResult;
import com.msb.bpm.approval.appr.service.minio.MinIOService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.Util;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinIOServiceImpl implements MinIOService {

  private final MinioProperties minioProperties;
  private final MinioClient minioClient;
  private final ObjectMapper objectMapper;

  @Override
  public void copyBucketByFilePathMinIO(String filePath, String prefix, String bucketSrc) {
    log.info("copyBucketByFilePathMinIO START with filePath={}, prefix={}, bucketSrc={}",
        filePath, prefix, bucketSrc);
    try {
      minioClient.copyObject(
          CopyObjectArgs.builder()
              .bucket(minioProperties.getMinio().getBucket())
              .object(filePath.replace(prefix, ""))
              .source(
                  CopySource.builder()
                      .bucket(bucketSrc)
                      .object(filePath)
                      .build())
              .build());
      log.info("copyBucketByFilePathMinIO END with filePath={}, prefix={}, bucketSrc={}",
          filePath, prefix, bucketSrc);
    } catch (Exception ex) {
      log.error("copyBucketByFilePathMinIO END with filePath={}, prefix={}, bucketSrc={}, Error:",
          filePath, prefix, bucketSrc, ex);
      throw new ApprovalException(DomainCode.COPY_BUCKET_FILE_MINIO_ERROR, new Object[]{filePath});
    }
  }

  @Override
  public Map<String, Object> getSizeFileMinIOByPathFile(String pathFile) {
    log.info("getSizeFileMinIOStartBy START with pathFile={}", pathFile);
    Map<String, Object> mapFileSize = new HashMap<>();
    try {
      Iterable<Result<Item>> results = minioClient.listObjects(
          ListObjectsArgs.builder()
              .bucket(minioProperties.getMinio().getBucket())
              .prefix(pathFile)
              .build());
      for (Result<Item> item : results) {
        log.info("getSizeFileMinIOStartBy with objectName={}, size={}", item.get().objectName(), item.get().size());
        mapFileSize.put(item.get().objectName(), item.get().size());
      }
      log.info("getSizeFileMinIOStartBy END with pathFile={}, mapFileSize={}", pathFile,
          JsonUtil.convertObject2String(mapFileSize, objectMapper));
    } catch (Exception ex) {
      log.error("getSizeFileMinIOStartBy END with pathFile={}, Error:", pathFile, ex);
      throw new ApprovalException(DomainCode.GET_SIZE_FILE_MINIO_ERROR, new Object[]{pathFile});
    }
    return mapFileSize;
  }

  public ObjectWriteResponse uploadFileMinIO(InputStream inputStream, String path) {
    try {
      ObjectWriteResponse uploadRes =
          minioClient.putObject(
              PutObjectArgs.builder().stream(inputStream, inputStream.available(), -1)
                  .bucket(minioProperties.getMinio().getBucket())
                  .object(path)
                  .contentType("application/pdf")
                  .build());
      return uploadRes;
    } catch (Exception e) {
      log.error("Error on upload file to MinIO");
      throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
    }
  }

  @Override
  public void uploadPdfFile(String bpmApplicationId,
      byte[] reportFileData,
      UploadFileResult uploadFileResult) {
    try {
      long current = System.currentTimeMillis();
      String fileId = UUID.randomUUID() + FileType.PDF.getFileType();
      File outputPDF = new File(Arrays.toString(reportFileData));

      String path =
          minioProperties.getMinio().getFilePathBase() + "/" + Util.getCurrDate(YYYY_MM_DD_FORMAT)
              + "/"+ bpmApplicationId + "/" + fileId;
      log.info("upload pdf file to: {} ", path);
      InputStream inputStream = new ByteArrayInputStream(reportFileData);
      uploadFileMinIO(inputStream, path);
      inputStream.close();

      log.info("generate file takes times: [{}]", System.currentTimeMillis() - current);
      uploadFileResult.setPath(path);
      uploadFileResult.setFileSize(outputPDF.length());
      uploadFileResult.setFileId(fileId);
      uploadFileResult.setFileType(FileType.PDF.getFileType());
      uploadFileResult.setBucketName(minioProperties.getMinio().getBucket());
    } catch (Exception ex) {
      log.error("Error on upload file to MinIO", ex);
      throw new ApprovalException(DomainCode.INTERNAL_SERVICE_ERROR);
    }
  }
}
