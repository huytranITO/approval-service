package com.msb.bpm.approval.appr.model.request.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RoomRequest {
    private String roomType;
    private String roomName;
    private List<String> members;
}
