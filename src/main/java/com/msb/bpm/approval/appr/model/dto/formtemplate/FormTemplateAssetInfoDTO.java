package com.msb.bpm.approval.appr.model.dto.formtemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import java.util.List;

@Getter
@Setter
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTemplateAssetInfoDTO {
    private List<FormTemplateAssetDTO> assets;
}
