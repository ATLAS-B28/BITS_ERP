package com.example.bitserp.modules.automation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String title;
    private String message;
    private String type;
    private String referenceType;
    private UUID referenceId;
    private Boolean isRead;
    private OffsetDateTime createdAt;
}
