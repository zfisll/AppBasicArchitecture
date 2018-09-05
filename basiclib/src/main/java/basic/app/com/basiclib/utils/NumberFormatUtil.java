package basic.app.com.basiclib.utils;

import java.math.BigDecimal;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/9/5
 * desc : 数字格式化工具
 */
public class NumberFormatUtil {
    /**
     * 格式化百分比：*100，保留两位小数，添加百分号后缀
     */
    public static String formatPercent(Object obj) {
        try {
            double percent = NullUtils.null2DoubleZero(obj);
            percent *= 100;
            BigDecimal b = new BigDecimal(percent);
            String result = b.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            return result + "%";
        } catch (NumberFormatException e) {
            LogUtil.e(e, e.getMessage());
            return "0.00%";
        }
    }
}
