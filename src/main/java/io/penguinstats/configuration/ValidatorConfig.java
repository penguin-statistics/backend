package io.penguinstats.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "validator")
public class ValidatorConfig {
    private Map<String, Boolean> configMap;
}
