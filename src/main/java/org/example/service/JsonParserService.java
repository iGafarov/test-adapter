package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.configuration.MappingConfig;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class JsonParserService {
    private final ObjectMapper objectMapper;
    private final MappingConfig mappingConfig;

    public JsonParserService(ObjectMapper objectMapper, MappingConfig mappingConfig) {
        this.objectMapper = objectMapper;
        this.mappingConfig = mappingConfig;
    }

    public Map<String, Object> parseJson(String json) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, String> entry : mappingConfig.getFields().entrySet()) {
            JsonNode valueNode = getJsonValue(rootNode, entry.getValue());
            if (valueNode != null) {
                result.put(entry.getKey(), valueNode.asText());
            }
        }

        String arrayPath = mappingConfig.getArrayPath();
        JsonNode registersNode = getJsonValue(rootNode, arrayPath);

        if (registersNode != null && registersNode.isArray()) {
            for (JsonNode register : registersNode) {
                Map<String, String> registerData = new HashMap<>();
                for (Map.Entry<String, String> entry : mappingConfig.getArrayFields().entrySet()) {
                    registerData.put(entry.getKey(), register.path(entry.getKey()).asText());
                }
                result.put(register.path("registerType").asText(), registerData);
            }
        }

        return result;
    }

    private JsonNode getJsonValue(JsonNode rootNode, String path) {
        String[] keys = path.split("\\.");
        JsonNode currentNode = rootNode;
        for (String key : keys) {
            currentNode = currentNode.path(key);
            if (currentNode.isMissingNode()) return null;
        }
        return currentNode;
    }
}

