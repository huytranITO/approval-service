package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RootCicDsMessage {
    @JsonProperty("UserHeader")
    private CicDsUserHeader userHeader;

    @JsonProperty("Data")
    private CicDsData data;

    @JsonIgnore
    public boolean hasData() {
        return data != null
                && !CollectionUtils.isEmpty(data.getActionArray())
                && !CollectionUtils.isEmpty(data.getActionArray().get(0))
                && data.getActionArray().get(0).size() > 1;
    }
}
