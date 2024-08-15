package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateAssetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleAssetResponse {
    private List<FormTemplateAssetDTO> assets =  new ArrayList<>();
    private Map<Long, List<String>> mapCreditAsset = new HashMap<>();
}

