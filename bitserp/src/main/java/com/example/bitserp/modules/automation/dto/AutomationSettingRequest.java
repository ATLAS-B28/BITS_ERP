package com.example.bitserp.modules.automation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class AutomationSettingRequest {
    private Map<String, String> settings;
}
