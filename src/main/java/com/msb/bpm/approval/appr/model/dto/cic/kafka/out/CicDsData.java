package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CicDsData {
    @JsonProperty("UserData")
    public CicDsUserData userData;

    @JsonProperty("ActionArray")
    public List<List<List<String>>> actionArray;
}
