package com.lftech.sqlapi.pojo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.LongNode;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    protected UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        long id = (Long) ((LongNode) node.get("id")).numberValue();
        String username = node.get("username").asText();
        String roles = node.get("roles").toString();
        //String sessionId = node.get("sessionId").toString();
        long sessionId = (Long) ((LongNode) node.get("sessionId")).numberValue();
        boolean enabled = node.get("enabled").asBoolean();
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRoles(roles);
        user.setEnabled(enabled);
        user.setSessionId(sessionId);
        return user;
    }
}

