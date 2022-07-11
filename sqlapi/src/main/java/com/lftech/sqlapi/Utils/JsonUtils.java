package com.lftech.sqlapi.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonUtils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> obj2Map(Object obj) {
        JsonNode jsonNode = objectMapper.valueToTree(obj);
        return objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> T json2Object(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }
}
