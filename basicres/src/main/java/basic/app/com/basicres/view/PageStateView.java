package basic.app.com.basicres.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ViewUtil;
import basic.app.com.basicres.R;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 页面状态View，可以设置空白态和错误态
 */
public class PageStateView extends ConstraintLayout {

    private TextView mTvIconMsg;
    ProgressBar bar;

    public PageStateView(Context context) {
        super(context);
        init(0);
    }

    public PageStateView(Context context, int minHeight) {
        super(context);
        init(minHeight);
    }

    public PageStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(0);
    }


    public PageStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(0);
    }

    private void init(int minHeight) {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_state, this);
        if (minHeight > 0) {
            setMinHeight(minHeight);
        }
        mTvIconMsg = ViewUtil.findViewById(this, R.id.tv_icon_msg);
    }

    public void setText(@StringRes int id) {
        setText(ResourceUtil.getString(id));
    }


    public void setText(CharSequence text) {
        mTvIconMsg.setVisibility(VISIBLE);
        mTvIconMsg.setText(text);
    }

    public void setTextColor(@ColorRes int id) {
        mTvIconMsg.setTextColor(ResourceUtil.getColor(id));
    }

    public void setIcon(@DrawableRes int id) {
        mTvIconMsg.setVisibility(VISIBLE);
        mTvIconMsg.setCompoundDrawablesWithIntrinsicBounds(0, id, 0, 0);
    }

    /**
     * 显示加载中状态
     */
    public void showLoading() {
        mTvIconMsg.setVisibility(GONE);
        bar = new ProgressBar(getContext());
        bar.getIndeterminateDrawable().setColorFilter(ResourceUtil.getColor(R.color.ui_primary), PorterDuff.Mode.SRC_IN);
        int size = DeviceUtil.dip2px(getContext(), 50);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(size, size);
        lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.verticalBias = 0.4F;
        bar.setLayoutParams(lp);
        addView(bar);
    }

}
