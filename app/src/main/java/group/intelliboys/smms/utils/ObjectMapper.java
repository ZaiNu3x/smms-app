package group.intelliboys.smms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

import group.intelliboys.smms.orm.data.User;


public class ObjectMapper {
    private static com.fasterxml.jackson.databind.ObjectMapper mapper;

    public static Map<?, ?> convertJsonToMapObject(String json) throws JsonProcessingException {
        if (mapper == null) {
            mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        }
        return mapper.readValue(json, Map.class);
    }

    public static User convertMapObjectToUser(Map<?, ?> map) throws JsonProcessingException {
        if (mapper == null) {
            mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        }
        return mapper.convertValue(map, User.class);
    }

    public static List<Map<String, Object>> convertJsonToListOfMap(String json) throws JsonProcessingException {
        if (mapper == null) {
            mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        }
        return mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }
}
