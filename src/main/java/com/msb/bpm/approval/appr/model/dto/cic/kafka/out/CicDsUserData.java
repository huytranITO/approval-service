package com.msb.bpm.approval.appr.model.dto.cic.kafka.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CicDsUserData {
    @JsonProperty("SocketChannel")
    public String socketChannel;

    @JsonProperty("User")
    public String user;

    @JsonProperty("SpecialMission")
    public String specialMission;

    @JsonProperty("MyAction")
    public String myAction;

    @JsonProperty("SpecialMessage")
    public CicDsSpecialMessage specialMessage;
}
