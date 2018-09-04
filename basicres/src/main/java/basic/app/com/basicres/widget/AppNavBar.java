package basic.app.com.basicres.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ViewUtil;
import basic.app.com.basicres.R;


/**
 * author : user_zf
 * date : 2018/8/29
 * desc : App通用的导航栏
 */
public class AppNavBar extends RelativeLayout {

    ImageView ivLeft;
    TextView tvLeft;
    TextView tvTitle;
    TextView tvButton;
    ImageView ivButton;

    public AppNavBar(Context context) {
        super(context);
        init();
    }

    public AppNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.common_topbar, this);
        ivLeft = ViewUtil.findViewById(this, R.id.ivLeft);
        tvLeft = ViewUtil.findViewById(this, R.id.tvLeft);
        tvTitle = ViewUtil.findViewById(this, R.id.tvTitle);
        tvButton = ViewUtil.findViewById(this, R.id.tvButton);
        ivButton = ViewUtil.findViewById(this, R.id.ivButton);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setIvLeft(@DrawableRes int id, OnClickListener listener) {
        ivLeft.setImageResource(id);
        ivLeft.setOnClickListener(listener);
        ivLeft.setVisibility(id != 0 ? VISIBLE : GONE);
    }

    public void setIvLeftVisibility(int visibility) {
        ivLeft.setVisibility(visibility);
    }

    public void setTvLeft(CharSequence text, OnClickListener listener) {
        tvLeft.setText(text);
        tvLeft.setOnClickListener(listener);
        tvLeft.setVisibility(TextUtils.isEmpty(text) ? GONE : VISIBLE);
    }

    public void setTvLeftVisibility(int visibility) {
        tvLeft.setVisibility(visibility);
    }

    public void setTvRight(CharSequence text, OnClickListener listener) {
        tvButton.setText(text);
        tvButton.setOnClickListener(listener);
        tvButton.setVisibility(TextUtils.isEmpty(text) ? GONE : VISIBLE);
    }

    public void setTvRightVisibility(int visibility) {
        tvButton.setVisibility(visibility);
    }

    public void setTvRightTextSize(int size) {
        tvButton.setTextSize(size);
    }

    public void setTvRightColor(@ColorRes int colorId) {
        tvButton.setTextColor(ResourceUtil.getColor(colorId));
    }

    public void setIvRight(@DrawableRes int id, OnClickListener listener) {
        ivButton.setImageResource(id);
        ivButton.setOnClickListener(listener);
        ivButton.setVisibility(id != 0 ? VISIBLE : GONE);
    }

    public void setIvRightVisibility(int visibility) {
        ivButton.setVisibility(visibility);
    }

}
