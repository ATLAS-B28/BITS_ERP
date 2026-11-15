package com.example.bitserp.modules.automation.controller;


import com.example.bitserp.modules.automation.dto.AutomationSettingRequest;
import com.example.bitserp.modules.automation.entity.AutomationSettings;
import com.example.bitserp.modules.automation.service.AutomationSettingsService;
import com.example.bitserp.shared.dto.ApiResponse;
import com.example.bitserp.shared.entity.BaseEntity;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/automation")
@RequiredArgsConstructor
public class AutomationController {

    private final AutomationSettingsService settingsService;
    private final UserRepository userRepository;

    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<List<AutomationSettings>>> getSettings() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        settingsService.getAll()
                )
        );
    }

    @PutMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateSettings(
            @RequestBody AutomationSettingRequest request,
            @AuthenticationPrincipal String email
            ) {
        UUID userId = userRepository.findByEmail(email)
                .map(BaseEntity::getId).orElseThrow();
        settingsService.updateAll(request.getSettings(), userId);
        return ResponseEntity.ok(ApiResponse.ok("Settings updated", null));
    }
}
