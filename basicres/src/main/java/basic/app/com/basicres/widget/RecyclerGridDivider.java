package basic.app.com.basicres.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basicres.R;

/**
 * author : user_zf
 * date : 18/4/16
 * desc : 通用的列表分割线(GridLayoutManager)，可以根据需求扩展该类
 */
public class RecyclerGridDivider extends RecyclerView.ItemDecoration {

    private float mHeight;
    private float mHorizontalMargin;
    private float mVerticalMargin;
    @ColorRes
    private int mColorRes;
    @ColorRes
    private int mBackColorRes;

    private RecyclerGridDivider(float height, float horizontalMargin, float verticalMargin, @ColorRes int colorRes, @ColorRes int backColorRes) {
        this.mHeight = height;
        this.mHorizontalMargin = horizontalMargin;
        this.mVerticalMargin = verticalMargin;
        this.mColorRes = colorRes;
        this.mBackColorRes = backColorRes;
    }

    /**
     * RecyclerViewDivider的建造类
     */
    public static class Builder {
        private float height = 0.5f;
        private float horizontalMargin = 0;
        private float verticalMargin = 0;
        @ColorRes
        private int colorRes = R.color.ui_divider_primary;
        //背景颜色，如果有值，那么先绘制背景颜色，再绘制colorRes，为了解决margin的问题
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
         * 设置垂直边距
         */
        public Builder setHorizontalMargin(float horizontalMargin) {
            this.horizontalMargin = horizontalMargin;
            return this;
        }

        /**
         * 设置水平边距
         */
        public Builder setVerticalMargin(float verticalMargin) {
            this.verticalMargin = verticalMargin;
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
         * 设置分割线背景颜色
         *
         * @param backColorRes 背景颜色资源id
         */
        public Builder setDividerBackColorRes(@ColorRes int backColorRes) {
            this.backColorRes = backColorRes;
            return this;
        }

        /**
         * 根据参数创建ItemDecoration对象
         */
        public RecyclerGridDivider build() {
            return new RecyclerGridDivider(height, horizontalMargin, verticalMargin, colorRes, backColorRes);
        }
    }

    /**
     * 该方法主要用来绘制分割线，分别计算出分割线的上下左右所处位置
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    /**
     * 绘制垂直方向分割线
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        Paint paint = new Paint();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop() - lp.topMargin + DeviceUtil.dip2px(parent.getContext(), mHorizontalMargin);
            int bottom = child.getBottom() + lp.bottomMargin - DeviceUtil.dip2px(parent.getContext(), mHorizontalMargin);
            int left = child.getRight() + lp.rightMargin;
            int right = left + DeviceUtil.dip2px(parent.getContext(), mHeight);
            if (mBackColorRes != R.color.transparent) { //设置了背景色
                paint.setColor(ResourceUtil.getColor(mBackColorRes));
                c.drawRect(left, child.getTop() - lp.topMargin, right, child.getBottom() + lp.bottomMargin, paint);
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(left, top, right, bottom, paint);
            } else { //未设置背景色
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    /**
     * 绘制水平方向分割线
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        Paint paint = new Paint();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - lp.leftMargin + DeviceUtil.dip2px(parent.getContext(), mVerticalMargin);
            int right = child.getRight() + lp.rightMargin + DeviceUtil.dip2px(parent.getContext(), mHeight) - DeviceUtil.dip2px(parent.getContext(), mVerticalMargin);
            int top = child.getBottom() + lp.bottomMargin;
            int bottom = top + DeviceUtil.dip2px(parent.getContext(), mHeight);
            if (mBackColorRes != R.color.transparent) {
                paint.setColor(ResourceUtil.getColor(mBackColorRes));
                c.drawRect(child.getLeft() - lp.leftMargin, top, child.getRight() + lp.rightMargin, bottom, paint);
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(left, top, right, bottom, paint);
            } else {
                paint.setColor(ResourceUtil.getColor(mColorRes));
                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }


    /**
     * 设置item之间的偏移量，一般情况下跟divider的高度一致就行了，需要和onDraw方法配合使用
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int columnCount = getColumnCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int position = parent.getChildLayoutPosition(view);
        boolean isLastColumn = isLastColumn(parent, position, columnCount);
        boolean isLastRow = isLastRow(parent, position, columnCount, childCount);
        if (isLastColumn) { //最后一列，不绘制右边的分割线
            if (!isLastRow) {
                outRect.set(0, 0, 0, DeviceUtil.dip2px(parent.getContext(), mHeight));
            }
        } else if (isLastRow(parent, position, columnCount, childCount)) { //最后一行，不绘制底部的分割线
            outRect.set(0, 0, DeviceUtil.dip2px(parent.getContext(), mHeight), 0);
        } else {
            outRect.set(0, 0, DeviceUtil.dip2px(parent.getContext(), mHeight), DeviceUtil.dip2px(parent.getContext(), mHeight));
        }
    }


    /**
     * 获取列数
     */
    private int getColumnCount(RecyclerView parent) {
        int columnCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            columnCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return columnCount;
    }

    /**
     * 是否最后一列，最后一列不用绘制右边的分割线
     */
    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否最后一行，最后一行不用绘制底部分割线
     */
    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int mod = childCount % spanCount;
            int lastRowFirstPos = childCount - (mod == 0 ? spanCount : mod);
            if (pos >= lastRowFirstPos) {
                return true;
            }
        }
        return false;
    }
}
