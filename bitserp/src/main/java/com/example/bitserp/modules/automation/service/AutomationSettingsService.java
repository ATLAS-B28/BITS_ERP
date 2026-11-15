package com.example.bitserp.modules.automation.service;

import com.example.bitserp.modules.automation.entity.AutomationSettings;
import com.example.bitserp.modules.automation.repository.AutomationSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutomationSettingsService {

    private final AutomationSettingsRepository repo;

    public String get(String key) {
        return repo.findByKey(key)
                .map(AutomationSettings::getValue)
                .orElse("");
    }
    public boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(get(key));
    }
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    @Transactional
    public void update(String key, String value, UUID updatedBy) {
        AutomationSettings settings = repo.findByKey(key)
                .orElseGet(() -> {
                    AutomationSettings s = new AutomationSettings();
                    s.setKey(key);
                    return s;
                });
        settings.setValue(value);
        settings.setUpdatedBy(updatedBy);
        repo.save(settings);
    }
    @Transactional
    public void updateAll(Map<String, String> updates, UUID updatedBy) {
        updates.forEach((key, value) -> {
            update(key,value,updatedBy);
        });
    }
    public List<AutomationSettings> getAll() {
        return repo.findAll();
    }
}
