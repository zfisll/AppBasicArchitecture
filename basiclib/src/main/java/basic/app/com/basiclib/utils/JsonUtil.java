package basic.app.com.basiclib.utils;

import android.text.TextUtils;
import android.util.LruCache;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import basic.app.com.basiclib.BuildConfig;
import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : Json工具类
 */
public class JsonUtil {

    public final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.addHandler(getProblemHandler());
    }


    public static DeserializationProblemHandler getProblemHandler() {
        return new DeserializationProblemHandler() {

            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                LogUtil.e("handleWeirdStringValue error:" + failureMsg + " errorValue is:" + valueToConvert);
                // 这里直接返回null也可以，如果要在解析失败情况下返回特定的数据就要参照如下的方式来处理
                return handleException(targetType);
            }

            @Override
            public Object handleWeirdNumberValue(DeserializationContext ctxt, Class<?> targetType, Number valueToConvert, String failureMsg) throws IOException {
                LogUtil.e("handleWeirdNumberValue error:" + failureMsg + " errorValue is:" + valueToConvert);
                return handleException(targetType);
            }

//            @Override
//            public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
//                LogUtil.e("handleUnexpectedToken error:" + failureMsg);
//                return null;
//            }
        };
    }

    /***/
    private static Object handleException(Class<?> targetType) {

        if (targetType == Integer.class || targetType == int.class) {
            return new Integer(0);
        } else if (targetType == Long.class || targetType == long.class) {
            return new Long(0);
        } else if (targetType == Double.class || targetType == double.class) {
            return new Double(0);
        } else if (targetType == Float.class || targetType == float.class) {
            return new Float(0);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return new Byte((byte) 0);
        } else if (targetType == Short.class || targetType == short.class) {
            return new Short((short) 0);
        } else if (targetType == Character.class || targetType == char.class) {
            return new Character((char) 0);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return new Boolean(false);
        }
        return null;
    }


    /**
     * json to entity
     *
     * @param json  json字符串
     * @param clazz 对应entityClass
     * @return
     */
    public static <T> T readJson2Entity(String json, Class<T> clazz) {

        if (!BuildConfig.DEBUG && TextUtils.isEmpty(json)) {  // 正式环境下，如果传入的是空串，则直接返回，避免print堆栈的性能开销
            LogUtil.e("输入的json的为空串");
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
        }
        return null;

    }

    public static <T> T json2List(String json, Class collectionClazz, Class elementClazz) {
        if (!BuildConfig.DEBUG && TextUtils.isEmpty(json)) {   // 正式环境下，如果传入的是空串，则直接返回，避免print堆栈的性能开销
            LogUtil.e("输入的json的为空串");
            return null;
        }

        try {
            JavaType javaType = getCollectionType(collectionClazz, elementClazz);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
        }
        return null;
    }

    //频繁使用，但是不变的数据，增加内存缓存，避免每次都序列化，json序列化越大效果越明显
    public static <T> T json2ListWithCache(String json, Class collectionClazz, Class elementClazz) {
        if (!BuildConfig.DEBUG && TextUtils.isEmpty(json)) {   // 正式环境下，如果传入的是空串，则直接返回，避免print堆栈的性能开销
            LogUtil.e("输入的json的为空串");
            return null;
        }
        T result =  getBeanFromCache(json,collectionClazz,elementClazz);
        if (result != null){
            return result;
        }
        try {

            JavaType javaType = getCollectionType(collectionClazz, elementClazz);
            result =  objectMapper.readValue(json, javaType);
            saveBeanToCache(json,collectionClazz,elementClazz,result);
            return result;
        } catch (Exception e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
        }
        return null;

    }

    private static LruCache<String,Object> beanCache = new LruCache<>(50);  //最多缓存50个结构到内存中

    private static <T> T getBeanFromCache(String json, Class collectionClazz, Class elementClazz){
        String key = json+collectionClazz.getName()+elementClazz.getName();
        Object object = beanCache.get(key);
        if (object != null){
            try {
                //缓存中有相同的数据
                LogUtil.i("--jie--缓存中有相同的数据,命中："+beanCache.hitCount()+"次");
                return (T) object;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private static <T> void saveBeanToCache(String json, Class collectionClazz, Class elementClazz,T value){
        String key = json+collectionClazz.getName()+elementClazz.getName();
        beanCache.put(key,value);
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


    /**
     * entity to json
     *
     * @param obj 对应entity实例
     */
    public static String writeEntity2JSON(Object obj) {
//
//        String jsonStr = "";
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            objectMapper.writeValue(baos, obj);
//            jsonStr = baos.toString();
//        } catch (IOException e) {
//            LogUtil.e(e, e.getMessage());
//            e.printStackTrace();
//        }
//        return jsonStr;
        //下面的方式，效率会有15%左右的提升
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LogUtil.e(e, e.getMessage());
        }
        return "";
    }

    /**
     * list to json
     *
     * @param list bean对应list
     */
    public static <T> String writeList2Json(List<T> list) {

        final Writer writer = new StringWriter();
        try {
            objectMapper.getFactory().createGenerator(writer).writeObject(list);
            writer.close();
            return writer.toString();
        } catch (IOException e) {
            LogUtil.e(e, e.getMessage());
        }
        return "";
    }

    /**
     * json to map
     */
    public static Map readJson2Map(String json) throws IOException {
        // 转义字符-异常
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return objectMapper.readValue(json, Map.class);
    }

    /**
     * JSON转换为List对象
     */
    public static List readJson2List(String json) {
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonParseException e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            LogUtil.e(e, e.getMessage() + " 错误的json:" + json);
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject putValue(JSONObject p, String name, boolean value) {
        try {
            p.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONObject putValue(JSONObject p, String name, double value) {
        try {
            p.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONObject putValue(JSONObject p, String name, int value) {
        try {
            p.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONObject putValue(JSONObject p, String name, long value) {
        try {
            p.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONObject putValue(JSONObject p, String key, Object value) {
        try {
            p.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONObject putValue(JSONObject p, String key, List<?> value) {
        try {
            JSONArray array = new JSONArray(objectMapper.writeValueAsString(value));
            p.put(key, array);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static JSONArray arrayFromString(String data) {
        JSONArray array = null;
        try {
            array = new JSONArray(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * 合并两个Array
     */
    public static JSONArray mergeArray(JSONArray array1, JSONArray array2) {
        if (array1 == null && array2 == null) {
            return null;
        } else if (array1 != null && array2 == null) {
            return array1;
        } else if (array1 == null) {
            return array2;
        } else {
            for (int i = 0; i < array2.length(); i++) {
                array1.put(array2.optJSONObject(i));
            }
            return array1;
        }
    }

    public static JSONObject objectFromString(String data) {
        JSONObject object = null;
        try {
            object = new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
