package org.example.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "mapping")
public class MappingConfig {
    private Map<String, String> fields;  // Простые поля
    private Map<String, String> arrayFields;  // Поля из массива registers
    private String arrayPath; // Путь к массиву
}
