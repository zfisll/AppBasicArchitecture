package basic.app.com.basiclib.baseclass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 捕获ViewFlipper#onDetachedFromWindow中unregisterReceiver的异常
 */
public class SafeViewFlipper extends ViewFlipper {
    public SafeViewFlipper(Context context) {
        super(context);
    }

    public SafeViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}
