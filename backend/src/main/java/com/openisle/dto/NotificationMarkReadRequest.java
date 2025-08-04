package com.openisle.dto;

import lombok.Data;

import java.util.List;

/** Request to mark notifications as read. */
@Data
public class NotificationMarkReadRequest {
    private List<Long> ids;
}
