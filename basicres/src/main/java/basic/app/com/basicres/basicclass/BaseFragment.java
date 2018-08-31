package basic.app.com.basicres.basicclass;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;

import basic.app.com.basiclib.baseclass.BasePresenter;
import basic.app.com.basiclib.baseclass.UIBaseFragment;
import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ViewUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;
import basic.app.com.basicres.R;
import basic.app.com.basicres.view.MyProgressDialog;
import basic.app.com.basicres.view.PageStateView;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : Fragment基类，所有Fragment均需继承此类
 */
public abstract class BaseFragment<T extends BasePresenter> extends UIBaseFragment<T> implements View.OnClickListener {

    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = ResourceUtil.getString(R.string.refresh_pull_down);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = ResourceUtil.getString(R.string.refresh_refreshing);
        ClassicsHeader.REFRESH_HEADER_RELEASE = ResourceUtil.getString(R.string.refresh_release);
    }

    private Dialog mWaitingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity回收时Fragment没对状态进行处理，会导致显示异常，所以不做处理，让Fragment重建
//        setRetainInstance(true);
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
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(this.getClass().getSimpleName() + "_onDestroyView");

        if (useEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(this.getClass().getSimpleName() + "_onDestroy");
    }


    public void replaceFragment(int viewId, BaseFragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(viewId, fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 是否启用Fragment的页面统计，即把Fragment当面独立页面去统计
     * 默认是不开启统计的，只有少部分Fragment需要特别去统计
     * 类似主页面的Fragment是需要统计的，特别是ViewPager里的
     *
     * @return true 则开启统计， false 则不参与统计
     */
    public boolean isFragmentPageStatisticsEnable() {
        return false;
    }

    /**
     * 获取统计的名称，默认为类名，可自定义覆写
     */
    public String getPageStatisticsName() {
        return getClass().getSimpleName();
    }

    @Override
    public void onVisible() {
        super.onVisible();
        LogUtil.i(this.getClass().getSimpleName() + "_onVisible");
        // 如果开启了统计则调用三方统计记录数据
        if (isFragmentPageStatisticsEnable()) {
            // TODO: 2018/8/28 此处可以通过三方工具统计页面，注释代码采用talkingdata
//            TCAgent.onPageStart(getContext(), getPageStatisticsName());
        }
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
        // 如果开启了统计则调用三方统计记录数据
        if (isFragmentPageStatisticsEnable()) {
            // TODO: 2018/8/28 此处可以通过三方工具统计页面，注释代码采用talkingdata
//            TCAgent.onPageEnd(getContext(), getPageStatisticsName());
        }
    }

    //****************以下是新框架的覆写方法****************//

    private PageStateView mEmptyView;
    private PageStateView mErrorView;
    private PageStateView mLoadingView;

    /**
     * 本页面是否支持换肤（有些公共的图，比如空页面图标、错误图标等）
     */
    public boolean supportChangeSkin() {
        return false;
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }

    @Override
    public void initLayout(View view) {
        if (useEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (isRefreshEnable()) {
            // 统一设置下拉刷新样式
            getHelper().getRefreshLayout().setRefreshHeader(getRefreshHeader());
        }
    }

    /**
     * 单独抽出来，方便外部调用
     */
    public ClassicsHeader getRefreshHeader() {
        // 统一设置下拉刷新样式
        ClassicsHeader header = new ClassicsHeader(getActivity());
//        ClassicsHeader header = (ClassicsHeader) View.inflate(getContext(),R.layout.skin_classicsheader,null);
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
            mEmptyView = (PageStateView) View.inflate(getContext(), R.layout.page_state_view, null);
            mEmptyView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(getActivity(), 400) : 0);
            mEmptyView.setBackgroundResource(getStateViewBgColorRes());
        }
        if (isStateNestedScrollEnable()) {
            NestedScrollView scrollView = new NestedScrollView(getActivity());
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
            mEmptyView.setIcon(supportChangeSkin() ? R.drawable.ic_empty : R.drawable.ic_empty);
            mEmptyView.setText(getEmptyText());
        }
    }

    /**
     * 状态View是否支持嵌套滚动
     */
    public boolean isStateNestedScrollEnable() {
        return false;
    }

    @Override
    public View getErrorView() {
        if (mErrorView == null) {
            mErrorView = (PageStateView) View.inflate(getContext(), R.layout.page_state_view, null);
            mErrorView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(getActivity(), 400) : 0);
            mErrorView.setIcon(R.drawable.ic_error);
            mErrorView.setText(R.string.error_refresh);
            mErrorView.setBackgroundResource(getStateViewBgColorRes());
        }
        if (isStateNestedScrollEnable()) {
            NestedScrollView scrollView = new NestedScrollView(getActivity());
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
        // 如果是全屏的Fragment，则使用圆形等待
        // 如果不是全屏的，即是一小块内容，则使用纯文字的加载中提示更合适
        if (!isFullScreenFragment()) {
            if (mLoadingView == null) {
                mLoadingView = (PageStateView) View.inflate(getContext(), R.layout.page_state_view, null);
                mLoadingView.setIcon(0);
                mLoadingView.setText(R.string.loading_now);
                mLoadingView.setBackgroundResource(getStateViewBgColorRes());
            }
            return mLoadingView;
        } else {
            if (mLoadingView == null) {
                mLoadingView = (PageStateView) View.inflate(getContext(), R.layout.page_state_view, null);
                mLoadingView.setMinHeight(isStateNestedScrollEnable() ? DeviceUtil.dip2px(getActivity(), 400) : 0);
                mLoadingView.setBackgroundResource(getStateViewBgColorRes());
                mLoadingView.showLoading();
            }
            if (isStateNestedScrollEnable()) {
                NestedScrollView scrollView = new NestedScrollView(getActivity());
                ViewUtil.removeSelfFromParent(mLoadingView);
                scrollView.addView(mLoadingView);
                return scrollView;
            } else {
                return mLoadingView;
            }
        }
    }

    @Override
    public void onLoadData() {

    }

    @Override
    public void showWaiting(CharSequence msg, boolean canDismissByUser) {
        //如果当前fragment没有被加到activity中,就不处理显示等待框逻辑
        if (isAdded()) {
            if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
                return;
            }
            if (TextUtils.isEmpty(msg)) {
                msg = getString(R.string.loading_data);
            }
            mWaitingDialog = new MyProgressDialog(getActivity(), msg);
            mWaitingDialog.setCanceledOnTouchOutside(canDismissByUser);
            mWaitingDialog.setCancelable(canDismissByUser);
            mWaitingDialog.setOnDismissListener(this);
            mWaitingDialog.show();
        }
    }

    @Override
    public void dismissWaiting() {
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
        }
    }

    public @ColorRes
    int getStateViewBgColorRes() {
        return R.color.transparent;
    }

    public boolean useEventBus() {
        return false;
    }

    public boolean isFullScreenFragment() {
        return true;
    }

    public CharSequence getEmptyText() {
        return getString(R.string.empty_default);
    }
}
