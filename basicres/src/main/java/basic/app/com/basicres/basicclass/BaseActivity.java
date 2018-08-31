package basic.app.com.basicres.basicclass;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;

import basic.app.com.basiclib.baseclass.BasePresenter;
import basic.app.com.basiclib.baseclass.UIBaseActivity;
import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ViewUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;
import basic.app.com.basicres.R;
import basic.app.com.basicres.view.AppNavBar;
import basic.app.com.basicres.view.MyProgressDialog;
import basic.app.com.basicres.view.PageStateView;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 基类Activity,所有 Activity都应该继承该类
 */
public abstract class BaseActivity<T extends BasePresenter> extends UIBaseActivity<T> {

    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = ResourceUtil.getString(R.string.refresh_pull_down);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = ResourceUtil.getString(R.string.refresh_refreshing);
        ClassicsHeader.REFRESH_HEADER_RELEASE = ResourceUtil.getString(R.string.refresh_release);
    }

    private boolean mDestroyed;
    private Dialog mWaitingDialog;

    protected OnBackPressedListener onBackPressedListener;
    private PageStateView mEmptyView;
    private PageStateView mErrorView;
    private PageStateView mLoadingView;

    public void setOnBackPressedListener(
            OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    /**
     * 初始化组件
     */
    public void initComp() {
    }

    /**
     * 获取页面标签
     */
    public String getPageTag() {
        return getClass().getName();
    }

    /**
     * 初始化数据
     *
     * @param bundle 意图中传入Bundle或其他
     */
    public void initData(Bundle bundle) {
    }

    /**
     * 恢复数据
     */
    public void restoreData(Bundle bundle) {
    }

    /**
     * 保存数据
     *
     * @return Bundle
     */
    public Bundle saveData(Bundle bundle) {
        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        LogUtil.i(this.getClass().getSimpleName() + "_onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(this.getClass().getSimpleName() + "_onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(this.getClass().getSimpleName() + "_onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        LogUtil.i(this.getClass().getSimpleName() + "_onDestroy");

        if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public boolean isDestroyed() {
        return mDestroyed;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            if (onBackPressedListener.doBack()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public interface OnBackPressedListener {
        Boolean doBack();
    }

    public void replaceFragment(int viewId, BaseFragment fragment) {
        replaceFragment(viewId, fragment, null);
    }

    public void replaceFragment(int viewId, BaseFragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(viewId, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    public boolean isFragmentCreated(BaseFragment fragment) {
        return fragment != null && fragment.getView() != null;
    }

    public BasePresenter getPresenter() {
        return null;
    }


    //****************以下是新框架的覆写方法****************//

    public AppNavBar appNavBar;

    @Override
    public int getLayoutResource() {
        if (isSingleFragmentActivity()) {
            return R.layout.activity_base;
        }
        return 0;
    }

    @Override
    public void preInit(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);//5.0以上去掉阴影
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            // 业务端可以通过getNavBar自定义导航栏，不使用默认的AppNavBar
            View view = getNavBar();
            if (view == null) {
                appNavBar = new AppNavBar(this);
                view = appNavBar;
                appNavBar.setTitle(getTitle().equals(DeviceUtil.getApplicationName(this)) ? "" : getTitle());
                appNavBar.setIvLeft(R.drawable.ic_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
            getSupportActionBar().setCustomView(view, new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            //actionbar设置custom view时，默认两边有空隙，故要去掉
            Toolbar parent = (Toolbar) getSupportActionBar().getCustomView().getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    @Override
    public void initLayout(View view) {
        if (useEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (isRefreshEnable()) {
            getHelper().getRefreshLayout().setRefreshHeader(getRefreshHeader());
        }
    }

    /**
     * 单独抽出来，方便外部调用
     */
    public ClassicsHeader getRefreshHeader() {
        // 统一设置下拉刷新样式
//        ClassicsHeader header = new ClassicsHeader(getContext());
        ClassicsHeader header = (ClassicsHeader) View.inflate(this, R.layout.skin_smartrefreshlayout, null);
        header.setEnableLastTime(false);
        header.setDrawableSize(13);
        header.setTextSizeTitle(14);
        header.setDrawableMarginRight(10);
        header.setFinishDuration(0);
        header.setProgressResource(R.drawable.ic_refresh);
        return header;
    }

    @Override
    public View getEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (PageStateView) View.inflate(this, R.layout.page_state_view, null);
            mEmptyView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(this, 400) : 0);
            mEmptyView.setBackgroundResource(getStateViewBgColorRes());

        }
        if (isStateNestedScrollEnable()) {
            NestedScrollView scrollView = new NestedScrollView(this);
            ViewUtil.removeSelfFromParent(mEmptyView);
            scrollView.addView(mEmptyView);
            return scrollView;
        } else {
            return mEmptyView;
        }
    }

    @Override
    public void setState(ViewState state) {
        super.setState(state);
        // 当设置为空状态时，设置提前配置好的空文案
        if (state == ViewState.EMPTY && mEmptyView != null) {
            mEmptyView.setIcon(R.drawable.ic_empty);
            mEmptyView.setBackgroundResource(getStateViewBgColorRes());
            mEmptyView.setText(getEmptyText());
        }
    }

    @Override
    public View getErrorView() {
        if (mErrorView == null) {
//            mErrorView = new PageStateView(getContext(), isStateNestedScrollEnable() ? UIUtil.dip2px(400) : 0);
            mErrorView = (PageStateView) View.inflate(this, R.layout.page_state_view, null);
            mErrorView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(this, 400) : 0);
            mErrorView.setIcon(R.drawable.ic_error);
            mErrorView.setText(R.string.error_refresh);
            mErrorView.setBackgroundResource(getStateViewBgColorRes());
        }
        if (isStateNestedScrollEnable()) {
            NestedScrollView scrollView = new NestedScrollView(this);
            ViewUtil.removeSelfFromParent(mErrorView);
            scrollView.addView(mErrorView);
            mErrorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setState(ViewState.LOADING);
                }
            });
            return scrollView;
        } else {
            return mErrorView;
        }
    }

    @Override
    public View getLoadingView() {
        if (mLoadingView == null) {
//            mLoadingView = new PageStateView(getContext(), isStateNestedScrollEnable() ? UIUtil.dip2px(400) : 0);
            mLoadingView = (PageStateView) View.inflate(this, R.layout.page_state_view, null);
            mLoadingView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(this, 400) : 0);
            mLoadingView.setBackgroundResource(getStateViewBgColorRes());
            mLoadingView.showLoading();
        }
        if (isStateNestedScrollEnable()) {
            NestedScrollView scrollView = new NestedScrollView(this);
            ViewUtil.removeSelfFromParent(mLoadingView);
            scrollView.addView(mLoadingView);
            return scrollView;
        } else {
            return mLoadingView;
        }
    }

    /**
     * 状态View是否支持嵌套滚动
     */
    public boolean isStateNestedScrollEnable() {
        return false;
    }

    @Override
    public void onLoadData() {

    }

    @Override
    public void showWaiting(CharSequence msg, boolean canDismissByUser) {
        //如果当前fragment没有被加到activity中,就不处理显示等待框逻辑
        if (!isDestroyed() && !isFinishing()) {
            if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
                return;
            }
            if (TextUtils.isEmpty(msg)) {
                msg = getString(R.string.loading_data);
            }
            mWaitingDialog = new MyProgressDialog(this, msg);
            mWaitingDialog.setCanceledOnTouchOutside(canDismissByUser);
            mWaitingDialog.setCancelable(canDismissByUser);
            mWaitingDialog.show();
        }
    }

    @Override
    public void dismissWaiting() {
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
        }
    }

    /**
     * 点击EditText以外的任何区域隐藏键盘
     *
     * @param ev 用户事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if (hideKeyboardWhenTouchOutsideEditText()) {
                    ViewUtil.dismissSoftKeyboard(this);
                }
            }
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }

    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v     视图
     * @param event 事件
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            return isOutsideOfView(v, event);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 事件是否在view的外边
     */
    public boolean isOutsideOfView(View v, MotionEvent event) {
        if (v == null || event == null) {
            return false;
        }

        int[] l = {0, 0};
        v.getLocationOnScreen(l);
        int left = l[0], top = l[1], bottom = top + v.getMeasuredHeight(), right = left
                + v.getMeasuredWidth();
        return !(event.getX() >= left && event.getX() <= right
                && event.getY() >= top && event.getY() <= bottom);
    }

    /**
     * 是否隐藏软键盘当EditText失去焦点时，默认隐藏，如果不需要隐藏，则置为false
     */
    public boolean hideKeyboardWhenTouchOutsideEditText() {
        return true;
    }

    public View getNavBar() {
        return null;
    }

    /**
     * 使用默认的appNavBar时设置标题
     */
    public void setPageTitle(@StringRes int resId) {
        if (appNavBar != null) {
            appNavBar.setTitle(ResourceUtil.getString(resId));
        }
    }

    public boolean useEventBus() {
        return false;
    }

    public boolean isSingleFragmentActivity() {
        return false;
    }

    public int getSingleFragmentHolderId() {
        return R.id.single_fragment_holder;
    }

    public CharSequence getEmptyText() {
        return getString(R.string.empty_default);
    }

    /**
     * 获取用于统计的Activity名称，在同一个Activity统计多个业务时需要复写该方法
     */
    public String getStatisticsName() {
        return getClass().getSimpleName();
    }

    public @ColorRes
    int getStateViewBgColorRes() {
        return R.color.transparent;
    }
}