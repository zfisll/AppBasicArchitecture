package basic.app.com.user.helper.net;

import basic.app.com.user.model.bean.DialogInfo;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 定义接口数据结构包装类，用户组件所有接口结构一致
 */
public class BaseResponse<T> {
    public int errno;
    public String message;
    public T body;
    public DialogInfo dialogInfo;

    public boolean isSuccess() {
        return errno == 0;
    }
}
