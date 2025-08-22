package com.openisle.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConversationDto {
    private Long id;
    private String name;
    private boolean channel;
    private String avatar;
    private MessageDto lastMessage;
    private List<UserSummaryDto> participants;
    private LocalDateTime createdAt;
    private long unreadCount;
}