package basic.app.com.basicres.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basicres.R;

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 通用的列表分割线(LinearLayoutManager)，可以根据需求扩展该类
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private float mHeight;
    private float mMarginLeft;
    @ColorRes
    private int mColorRes;
    @ColorRes
    private int mBackColorRes;
    private boolean mIgnoreLastDivider;

    private RecyclerViewDivider(float height, float marginLeft, @ColorRes int colorRes, boolean ignoreLastDivider, @ColorRes int backColorRes) {
        this.mHeight = height;
        this.mMarginLeft = marginLeft;
        this.mColorRes = colorRes;
        this.mIgnoreLastDivider = ignoreLastDivider;
        this.mBackColorRes = backColorRes;
    }

    /**
     * RecyclerViewDivider的建造类
     */
    public static class Builder {
        private float height = 0.5f;
        private float marginLeft = 17;
        private boolean isIgnoreLastDivider = false;
        @ColorRes
        private int colorRes = R.color.ui_divider_primary;
        @ColorRes
        private int backColorRes = R.color.transparent;

        /**
         * 设置分割线高度
         *
         * @param height 单位dp，默认值为0.5
         */
        public Builder setDividerHeight(float height) {
            this.height = height;
            return this;
        }

        /**
         * 设置左边距
         *
         * @param marginLeft 单位dp，默认值为17
         */
        public Builder setMarginLeft(float marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        /**
         * 是否忽略最后一条分割线
         *
         * @param isIgnoreLastDivider true=不显示 false=显示
         */
        public Builder setIgnoreLastDivider(boolean isIgnoreLastDivider) {
            this.isIgnoreLastDivider = isIgnoreLastDivider;
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param colorRes 颜色资源id
         */
        public Builder setDividerColorRes(@ColorRes int colorRes) {
            this.colorRes = colorRes;
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param backColorRes 颜色资源id
         */
        public Builder setDividerBackColorRes(@ColorRes int backColorRes) {
            this.backColorRes = backColorRes;
            return this;
        }

        /**
         * 根据参数创建ItemDecoration对象
         */
        public RecyclerViewDivider build() {
            return new RecyclerViewDivider(height, marginLeft, colorRes, isIgnoreLastDivider, backColorRes);
        }
    }

    /**
     * 该方法主要用来绘制分割线，分别计算出分割线的上下左右所处位置
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        //获取header和footer的数量，并且获取headerView
        int headerCount = 0;
        int footerCount = 0;
        View headerView = null;
        if (parent.getAdapter() instanceof BaseQuickAdapter) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) parent.getAdapter();
            headerCount = adapter.getHeaderLayoutCount();
            footerCount = adapter.getFooterLayoutCount();
            if (headerCount > 0) {
                headerView = adapter.getHeaderLayout();
            }
        }
        int size = parent.getChildCount() - footerCount - (mIgnoreLastDivider ? 1 : 0);
        Paint paint = new Paint();
        for (int i = 0; i < size; i++) {
            // 跳过头部的分割线
            View item = parent.getChildAt(i);
            if (headerView != null && headerView.hashCode() == item.hashCode()) {
                continue;
            }
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            int dividerHeight = DeviceUtil.dip2px(parent.getContext(), mHeight);
            if (mBackColorRes != R.color.transparent) {
                paint.setColor(ResourceUtil.getColor(mBackColorRes));
                c.drawRect(0f, top, parent.getMeasuredWidth(), top + dividerHeight, paint);
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(DeviceUtil.dip2px(parent.getContext(), mMarginLeft), top, parent.getMeasuredWidth(), top + dividerHeight, paint);
            } else {
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(DeviceUtil.dip2px(parent.getContext(), mMarginLeft), top, parent.getMeasuredWidth(), top + dividerHeight, paint);
            }
        }
    }

    /**
     * 设置item之间的偏移量，一般情况下跟divider的高度一致就行了，需要和onDraw方法配合使用
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, DeviceUtil.dip2px(parent.getContext(), mHeight));
    }
}
