package com.energyplatform.auth_service.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@AllArgsConstructor
@ConfigurationProperties(prefix = "mongodb")
public class MongoConfigProperties {

    private final String uri;
    private final Map<String,String> databases;

}
