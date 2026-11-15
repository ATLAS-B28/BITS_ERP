package com.example.bitserp.modules.automation.repository;

import com.example.bitserp.modules.automation.entity.AutomationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutomationSettingsRepository extends JpaRepository<AutomationSettings, Integer> {
    Optional<AutomationSettings> findByKey(String key);
}
