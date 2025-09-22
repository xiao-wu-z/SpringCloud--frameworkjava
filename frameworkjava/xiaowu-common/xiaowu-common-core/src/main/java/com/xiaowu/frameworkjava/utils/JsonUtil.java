package com.xiaowu.frameworkjava.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.xiaowu.frameworkjava.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    //对象转换器
    private static ObjectMapper OBJECT_MAPPER;


    static {
        OBJECT_MAPPER =
                JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                        .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
                        .configure(MapperFeature.USE_ANNOTATIONS, false)
                        .addModule(new JavaTimeModule())
                        .addModule(new SimpleModule()
                                .addSerializer(LocalDateTime.class,
                                        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConstants.STANDARD_FORMAT)))
                                .addDeserializer(LocalDateTime.class,
                                        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(CommonConstants.STANDARD_FORMAT))))
                        .defaultDateFormat(new SimpleDateFormat(CommonConstants.STANDARD_FORMAT))
                        .serializationInclusion(JsonInclude.Include.NON_NULL)
                        .build();
    }

    /**
     * 将对象转换为Json字符串
     * @param obj 对象
     * @return Json格式字符串
     * @param <T> 对象类型
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     * @param obj 对象
     * @return 美化的Json格式字符串
     * @param <T> 对象类型
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将Json字符串转换为对象
     * @param str Json字符串
     * @param clazz 对象类型
     * @param <T> 对象类型
     * @return 对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : OBJECT_MAPPER.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }


    /**
     * 将Json字符串转换为list对象，支持list嵌套简单对象
     * @param str Json字符串
     * @param clazz 对象类型
     * @return 对象列表
     * @param <T> 对象类型
     */
    public static <T> List<T> string2List(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                .constructParametricType(List.class, clazz);
        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (JsonProcessingException e) {
            log.warn("Parse String to List error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将Json字符串转换为map对象，支持map嵌套简单对象
     * @param str Json字符串
     * @param valClass value类型
     * @return map对象
     * @param <T> value类型
     */
    public static <T> Map<String, T> string2Map(String str, Class<T> valClass) {
        if (str == null || str.length() <= 0 || valClass == null) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                .constructParametricType(LinkedHashMap.class, String.class, valClass);
        try {
            return OBJECT_MAPPER.readValue(str, javaType);
        }  catch (JsonProcessingException e) {
            log.warn("Parse String to Map error : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将Json字符串转换为对象，支持复杂对象，包括list和map嵌套
     * @param str Json字符串
     * @param valueTypeRef 对象模板信息
     * @return 对象
     **/
    public static <T> T string2Obj(String str, TypeReference<T> valueTypeRef) {
        if (str == null || str.length() <= 0 || valueTypeRef == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(str, valueTypeRef);
        } catch (JsonProcessingException e) {
            log.warn("Parse String to Object error : {}", e.getMessage());
            return null;
        }
    }
}
