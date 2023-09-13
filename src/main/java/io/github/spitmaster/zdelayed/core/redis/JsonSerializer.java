package io.github.spitmaster.zdelayed.core.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.spitmaster.zdelayed.exceptions.ZdelayedException;

class JsonSerializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.findAndRegisterModules();
    }

    //对象转json字符串
    static String toJSONString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ZdelayedException("Zdelayed method's parameter cannot be json serialized, don't use complex structure object as delayed method's parameter", e);
        }
    }

    //json字符串转java对象
    static <T> T parseObject(String jsonStr, Class<T> objType) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, objType);
        } catch (JsonProcessingException e) {
            throw new ZdelayedException("Zdelayed DelayedTask's args cannot be deserialized, don't use complex structure object as delayed method's parameter", e);
        }
    }

}
