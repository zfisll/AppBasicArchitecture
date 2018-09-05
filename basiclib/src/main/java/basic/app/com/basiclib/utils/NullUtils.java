package basic.app.com.basiclib.utils;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/9/5
 * desc : 空对象处理
 */
public class NullUtils {

    /**
     * 空对象String转""
     */
    public static String null2String(Object object) {

        if (object == null) {
            return "";
        }

        return object.toString();
    }

    /**
     * 空对象 long转0
     */
    public static long null2LongZero(Object object) {

        if (object == null || "".equals(object.toString())) {
            return 0;
        }
        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    /**
     * 空对象 Integer转0
     */
    public static int null2Zero(Object object) {
        if (object == null || "".equals(object.toString())) {
            return 0;
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException e) {
            try {
                return (int) Double.parseDouble(object.toString());
            } catch (NumberFormatException e1) {
            }
        }
        return 0;
    }

    /**
     * 空对象 转"0"
     */
    public static String null2ZeroStr(Object object) {

        if (object == null || "".equals(object.toString())) {
            return 0 + "";
        }

        return object.toString();
    }

    /**
     * 空对象 转"0"，保留2位小数点
     */
    public static String dealNull2ZeroStr(Object object) {

        if (object == null || "".equals(object.toString())) {
            return 0 + "";
        } else {
            String res = object.toString();
            if (res.length() >= 2) {
                return res.substring(0, res.length() - 2);
            }
            return res;
        }
    }

    /**
     * double数据字符串转int数据字符串
     */
    public static String doubleStrToIntStr(Object object) {

        if (object == null || object.equals("") || NullUtils.null2String(object).toLowerCase().equals("null")) {
            return "0";
        }
        try {
            object = (int) NullUtils.null2DoubleZero(object) + "";
        } catch (Exception e) {
            LogUtil.e(e, e.getMessage());
        }

        return object.toString();
    }

    /**
     * 空对象Double转0
     */
    public static double null2DoubleZero(Object object) {

        if (object == null || "".equals(object.toString())) {
            return 0;
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    /**
     * 空对象Double转0
     */
    public static float null2FloatZero(Object object) {

        if (object == null || "".equals(object.toString())) {
            return 0;
        }
        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException e) {
            return 0;
        }

    }
}
