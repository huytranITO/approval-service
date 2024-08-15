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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SmartChatAspectTest {

    @Mock
    SmartChatClient smartChatClient;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    SmartChatAspect smartChatAspect;


    @BeforeAll
    static void init() {}


    @Test
    public void testCreateRoom() {
        when(smartChatClient.createRoom(any(RoomRequest.class))).thenReturn(Mockito.mock(ApiResponse.class));
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(this,"BPM");
        assertNotNull(smartChatAspect.createRoom(createGroupEvent));
    }

    @Test
    public void testCloseRoom() {
        when(smartChatClient.closeRoom(anyString())).thenReturn(Mockito.mock(ApiResponse.class));
        CloseGroupEvent closeGroupEvent = new CloseGroupEvent(this,"BPM");
        assertNotNull(smartChatAspect.closeRoom(closeGroupEvent));
    }

    @Test
    public void testAddUserToRooms() {
        when(smartChatClient.addUserToRoom(any(AddUserToRoomRequest.class))).thenReturn(Mockito.mock(ApiResponse.class));
        AddUserToRoomEvent addUserToRoomEvent = new AddUserToRoomEvent(this,"BPM","BPM");
        assertNotNull(smartChatAspect.addUserToRooms(addUserToRoomEvent));
    }

    @ParameterizedTest
    @CsvSource({"false","true"})
    public void testAddListUserToRooms(String type) {
        AddUserToListRoomEvent addUserToListRoomEvent = new AddUserToListRoomEvent(this,Collections.singletonList("BPM"),"BPM");
        if(type.equalsIgnoreCase("false")){
            when(smartChatClient.addUserToRoom(any(AddUserToRoomRequest.class))).thenThrow(RuntimeException.class);
            Assertions.assertDoesNotThrow(() -> smartChatAspect.addListUserToRooms(addUserToListRoomEvent));
        }
        else {
            when(smartChatClient.addUserToRoom(any(AddUserToRoomRequest.class))).thenReturn(Mockito.mock(ApiResponse.class));
            Assertions.assertDoesNotThrow(() -> smartChatAspect.addListUserToRooms(addUserToListRoomEvent));
        }
    }

    @Test
    public void testAddListUserToRoomsFail() {
        AddUserToListRoomEvent addUserToListRoomEvent = new AddUserToListRoomEvent(this,Collections.singletonList("BPM"),"BPM");
        when(smartChatClient.addUserToRoom(any())).thenThrow(RuntimeException.class);
        Assertions.assertDoesNotThrow(() -> smartChatAspect.addListUserToRooms(addUserToListRoomEvent));
    }

}
