package com.msb.bpm.approval.appr.model.request.cifi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CIFISearchRequest {
    private String channel;
    private String channelRequestId;
}
