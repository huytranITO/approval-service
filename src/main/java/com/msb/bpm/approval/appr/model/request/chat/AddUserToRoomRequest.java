package com.msb.bpm.approval.appr.model.request.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AddUserToRoomRequest {
    private String groupName;
    private List<String> users;
}
