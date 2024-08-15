package com.msb.bpm.approval.appr.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.chat.events.AddUserToListRoomEvent;
import com.msb.bpm.approval.appr.chat.events.AddUserToRoomEvent;
import com.msb.bpm.approval.appr.chat.events.CloseGroupEvent;
import com.msb.bpm.approval.appr.chat.events.CreateGroupEvent;
import com.msb.bpm.approval.appr.client.chat.SmartChatClient;
import com.msb.bpm.approval.appr.model.request.chat.AddUserToRoomRequest;
import com.msb.bpm.approval.appr.model.request.chat.RoomRequest;
import com.msb.bpm.approval.appr.model.response.ApiResponse;
import com.msb.bpm.approval.appr.service.AbstractBaseService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

@Configuration
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class SmartChatAspect extends AbstractBaseService {

    private final SmartChatClient smartChatClient;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    public ApiResponse createRoom(CreateGroupEvent createGroupEvent) {
        RoomRequest roomRequest = RoomRequest.builder().roomName(createGroupEvent.getRoomName()).members(Collections.singletonList(SecurityContextUtil.getCurrentUser())).build();
        log.info("START calling digi-smart-chat service to create room with request: {}", JsonUtil.convertObject2String(roomRequest, objectMapper));
        return smartChatClient.createRoom(roomRequest);
    }

    @Async
    @EventListener
    public ApiResponse closeRoom(CloseGroupEvent closeGroupEvent) {
        log.info("START calling digi-smart-chat service to close room with room name: {}", closeGroupEvent.getRoomName());
        return smartChatClient.closeRoom(closeGroupEvent.getRoomName());
    }
    @Async
    @EventListener
    public ApiResponse addUserToRooms(AddUserToRoomEvent addUserToRoomEvent) {
        log.info("START calling digi-smart-chat service to add user to room: {}", "roomName: "+ addUserToRoomEvent.getRoomName() +"-assignee:"+ addUserToRoomEvent.getAssignee()  );
        AddUserToRoomRequest request = AddUserToRoomRequest.builder()
                .users(Collections.singletonList(addUserToRoomEvent.getAssignee()))
                .groupName(addUserToRoomEvent.getRoomName())
                .build();
        return smartChatClient.addUserToRoom(request);
    }

    @Async
    @EventListener
    public void addListUserToRooms(AddUserToListRoomEvent addListUserToRoomEvent) {
        if (ObjectUtils.isNotEmpty(addListUserToRoomEvent.getRoomNames())) {
            for (String roomName : addListUserToRoomEvent.getRoomNames()) {
                try {
                    log.info("START calling digi-smart-chat service to add list user to roomName: {}, user: {}", roomName, addListUserToRoomEvent.getAssignee());
                    AddUserToRoomRequest request = AddUserToRoomRequest.builder()
                            .users(Collections.singletonList(addListUserToRoomEvent.getAssignee()))
                            .groupName(roomName)
                            .build();
                    smartChatClient.addUserToRoom(request);
                } catch (Exception e) {
                    log.error("addListUserToRooms error: ", e);
                }
            }
        }
    }

}
