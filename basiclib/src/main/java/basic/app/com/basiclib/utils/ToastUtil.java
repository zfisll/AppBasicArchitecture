package basic.app.com.basiclib.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : Toast提示工具，解决了多次弹Toast的问题
 */
public class ToastUtil {

    private static Toast mToast;

    /**
     * 在 UI 线程弹 toast
     *
     * @param text 提示的文字
     */
    @SuppressLint("CheckResult")
    public static void showToast(final String text) {
        if (!TextUtils.isEmpty(text) && AppUtil.getApp().getApplicationContext() != null) {
            Observable.just(text)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (mToast == null) {
                                mToast = Toast.makeText(AppUtil.getApp().getApplicationContext(), text, Toast.LENGTH_SHORT);
                            } else {
                                mToast.setText(text);
                            }
                            mToast.show();
                        }
                    });
        }
    }

    /**
     * 在 UI 线程弹 toast
     *
     * @param stringId 提示文字的资源 id
     */
    public static void showToast(int stringId) {
        showToast(ResourceUtil.getString(stringId));
    }

}
