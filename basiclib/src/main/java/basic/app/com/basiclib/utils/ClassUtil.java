package basic.app.com.basiclib.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : Class相关工具类
 */
public class ClassUtil {

    /**
     * 获取第{index}个泛型的类名
     *
     * @param entity 要获取参数的类
     * @param index  第几个参数
     */
    public static Class getActualTypeClass(Class entity, int index) {
        ParameterizedType type = (ParameterizedType) entity.getGenericSuperclass();
        if (type.getActualTypeArguments()[index] instanceof Class) {
            return (Class) type.getActualTypeArguments()[index];
        }
        return null;
    }


    /**
     * 合并两个bean中非空的属性，仅支持相同class的合并，暂不支持跨对象合并
     *
     * @param sourceBean 提供更新数据的bean
     * @param targetBean 最终合入数据的bean
     */
    public static <T> T combineBean(T sourceBean, T targetBean) {
        if (targetBean == null) {
            return sourceBean;
        }
        Class sourceBeanClass = sourceBean.getClass();

        Field[] sourceFields = sourceBeanClass.getDeclaredFields();
        Field[] targetFields = sourceBeanClass.getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            Field targetField = targetFields[i];
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (!(sourceField.get(sourceBean) == null)) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetBean;
    }
}
