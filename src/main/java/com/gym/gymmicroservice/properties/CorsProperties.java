package com.gym.gymmicroservice.properties;

import java.util.List;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Value
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    String allowedOrigins;
    List<String> allowedMethods;
    List<String> allowedHeaders;
    Boolean allowCredentials;
}
