package com.msb.bpm.approval.appr.model.response.collateral;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AssetFileResDTO {
    private Long id;
    private String bucket;
    private String minioPath;
    private String fileName;
    private String fileType;
    private String fileSize;
    private String createdBy;
}
