package basic.app.com.basiclib.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : 算术工具，用来计算加减乘除运算
 */
public class MathUtil {

    /**
     * 进行加法运算
     */
    public static BigDecimal add(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return new BigDecimal("0");
            }
            if (TextUtils.isEmpty(d2)) {
                return new BigDecimal(d1);
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            return b1.add(b2);
        } catch (Exception e){
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行减法运算
     */
    public static BigDecimal sub(String d1, String d2, int len) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return new BigDecimal("0");
            }
            if (TextUtils.isEmpty(d2)) {
                return new BigDecimal(d1);
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            return round(b1.subtract(b2).toString(), len);
        } catch (Exception e){
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行减法运算
     */
    public static BigDecimal sub(String d1, String d2) {
        return sub(new BigDecimal(d1),new BigDecimal(d2));
    }

    /**
     * 进行减法运算
     */
    public static BigDecimal sub(BigDecimal b1, BigDecimal b2) {
        try {
            return b1.subtract(b2);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行减法运算
     */
    public static String subStr(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return "0";
            }
            if (TextUtils.isEmpty(d2)) {
                return d1;
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = b1.subtract(b2);
            return result.toString();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return "0";
        }
    }

    /**
     * 进行减法运算
     */
    public static String subStr(String d1, String d2, int len) {
        try {
            if(TextUtils.isEmpty(d1)){
                return "0";
            }
            if(TextUtils.isEmpty(d2)){
                return d1;
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = round(b1.subtract(b2).toString(), len);
            return result.toString();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return "0";
        }
    }

    /**
     * 比较大小运算(d1>=d2:true  d1<d2:false)
     */
    public static boolean greaterOrEqualThan(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return false;
            }
            if (TextUtils.isEmpty(d2)) {
                return true;
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = round(b1.subtract(b2).toString(), 5);
            return result.compareTo(new BigDecimal("0")) != -1;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }

    /**
     * 比较大小运算(d1>d2:true  d1<=d2:false)
     */
    public static boolean greaterThan(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return false;
            }
            if (TextUtils.isEmpty(d2)) {
                return true;
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = round(b1.subtract(b2).toString(), 5);
            return result.compareTo(new BigDecimal("0")) == 1;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }

    /**
     * 比较大小运算(d1<d2:true  d1>=d2:false)
     */
    public static boolean lessThan(String d1, String d2) {
        return !greaterOrEqualThan(d1, d2);
    }

    /**
     * 比较大小运算(d1<=d2:true  d1>d2:false)
     */
    public static boolean lessOrEqualThan(String d1, String d2) {
        return !greaterThan(d1, d2);
    }

    /**
     * 比较大小运算(d1=d2:true else:fale)
     */
    public static boolean equalThan(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1)) {
                return false;
            }
            if (TextUtils.isEmpty(d2)) {
                return false;
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = round(b1.subtract(b2).toString(), 5);
            if (result.compareTo(new BigDecimal("0")) == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }

    /**
     * 进行乘法运算
     */
    public static BigDecimal mul(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1) || TextUtils.isEmpty(d2)) {
                return new BigDecimal("0");
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            return b1.multiply(b2);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行乘法运算
     */
    public static String mulStr(String d1, String d2) {
        try {
            if (TextUtils.isEmpty(d1) || TextUtils.isEmpty(d2)) {
                return "0";
            }
            BigDecimal b1 = new BigDecimal(d1);
            BigDecimal b2 = new BigDecimal(d2);
            BigDecimal result = b1.multiply(b2);
            return result.toString();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return "0";
        }
    }

    /**
     * 取模运算
     */
    public static BigDecimal[] modStr(String d1, String d2){
        try {
            BigDecimal bg = new BigDecimal(d2);
            BigDecimal om2 = new BigDecimal(d1);
            return om2.divideAndRemainder(bg);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal[]{new BigDecimal("0"), new BigDecimal("0")};
        }
    }

    /**
     * 进行除法运算,四舍五入
     */
    public static BigDecimal div(String str1, String str2, int len) {
        return div(str1,str2,len, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 进行除法运算,四舍五入
     */
    public static BigDecimal div(BigDecimal b1, BigDecimal b2, int len) {
        return div(b1,b2,len, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 进行除法运算
     */
    public static BigDecimal div(String str1, String str2, int len,int roundingMode) {
        try {
            BigDecimal b1 = new BigDecimal(str1);
            BigDecimal b2 = new BigDecimal(str2);
            return div(b1, b2, len, roundingMode);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行除法运算
     */
    public static BigDecimal div(BigDecimal b1, BigDecimal b2, int len,int roundingMode) {
        try {
            if (b2.compareTo(new BigDecimal("0")) == 0) {
                return new BigDecimal("0");
            }
            return b1.divide(b2, len, roundingMode);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }

    /**
     * 进行四舍五入操作
     */
    public static BigDecimal round(String d, int len) {
        try {
            BigDecimal b1 = new BigDecimal(d);
            BigDecimal b2 = new BigDecimal(1);
            // 任何一个数字除以1都是原数字
            // ROUND_HALF_UP是BigDecimal的一个常量，表示进行四舍五入的操作
            return b1.divide(b2, len, BigDecimal.
                    ROUND_HALF_UP);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return new BigDecimal("0");
        }
    }


    /**
     * 判断一个double数据是否大于0
     *
     * @param v double数据
     * @return 结果
     */
    public static boolean isPositive(double v) {
        double precision = 0.000001;
        if (v - 0 > precision) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断一个double数据是否小于0
     *
     * @param v double数据
     * @return 结果
     */
    public static boolean isNegative(double v) {
        double precision = -0.000001;
        if (v - 0 < precision) {
            return true;
        } else {
            return false;
        }
    }

}
