package basic.app.com.user.model.bean;

import android.text.TextUtils;

/**
 * author : user_zf
 * date : 18/5/15
 * desc : 接口弹框信息，任何接口返回该信息，都可以弹框
 */
public class DialogInfo {
    private static final String DIALOG_ONE_BUTTON = "1"; //单按钮对话框
    private static final String DIALOG_TWO_BUTTON = "2"; //多按钮对话框

    public String type; //弹窗类型 "1"-单按钮弹框 "2"-多按钮弹框
    public String title; //弹框标题文本
    public String message; //弹框内容文本
    public String positiveBtn; //弹框主按钮文本，默认"确定"
    public String negativeBtn; //弹框副按钮文本，默认"取消"
    public String gotoUrl; //主按钮跳转链接

    public boolean isTwoButton() {
        return TextUtils.equals(type, DIALOG_TWO_BUTTON);
    }
}
