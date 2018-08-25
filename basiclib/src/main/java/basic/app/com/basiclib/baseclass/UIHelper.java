package basic.app.com.basiclib.baseclass;

import android.content.Context;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import basic.app.com.basiclib.R;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 由于Activity和Fragment的UI初始化一样，所以抽离出一个ui helper，帮助两个控件进行UI的初始化
 */
public class UIHelper {

    private Context mContext; //上下文
    private IStateView mStateViewProxy; //状态view的实现，实现代理
    private IUIBaseConfig mBaseConfigImpl; //基础配置实现
    private StateView mStateView; //带状态的view，业务视图的根布局
    private SmartRefreshLayout mRefreshLayout; //下拉刷新控件

    public UIHelper(Context context, IStateView view, IUIBaseConfig config) {
        this.mContext = context;
        this.mStateViewProxy = view;
        this.mBaseConfigImpl = config;
    }

    /**
     * 初始化View，根据BaseConfig内容来决定用哪个view
     */
    public View initView() {
        mStateView = new StateView(mContext) {
            @Override
            public View getDataView() {
                return mStateViewProxy.getDataView();
            }

            @Override
            public View getEmptyView() {
                return mStateViewProxy.getEmptyView();
            }

            @Override
            public View getErrorView() {
                return mStateViewProxy.getErrorView();
            }

            @Override
            public View getLoadingView() {
                return mStateViewProxy.getLoadingView();
            }

            @Override
            public void onLoadData() {
                mStateViewProxy.onLoadData();
            }
        };
        int backgroundColorResId = mBaseConfigImpl.getRootViewBackgroundColorResId();

        //如果页面可下拉刷新，初始化时就套一个下拉刷新布局，不可刷新就直接使用stateView
        //要在皮肤上适配这个控件写的太麻烦，而且现部分在子类这里用的太混乱，所以直接在这个控件所在的父布局中设置背景，要支持换肤则把这个控件背景设置成透明的就可以了，也方便以后统一调整
        if (mBaseConfigImpl.isRefreshEnable()) {
            mRefreshLayout = (SmartRefreshLayout) View.inflate(mContext, R.layout.skin_smartrefreshlayout, null);
            mRefreshLayout.addView(mStateView);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    mStateViewProxy.onLoadData();
                }
            });
//            mStateView.setBackgroundResource(mBaseConfigImpl.supportChangeSkin()?R.color.transparent:backgroundColorResId);
            mRefreshLayout.setBackgroundResource(!mBaseConfigImpl.isRefreshEnable() ? android.R.color.transparent : backgroundColorResId);
            return mRefreshLayout;
        } else {
            mStateView.setBackgroundResource(mBaseConfigImpl.supportChangeSkin() ? android.R.color.transparent : backgroundColorResId);
            return mStateView;
        }
    }

    /**
     * 设置StateView的状态
     *
     * @param state {@link }
     */
    public void setState(IStateView.ViewState state) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
            //如果是loading状态禁用下拉刷新
            switch (state) {
                case LOADING:
                case EXTRA:
                    mRefreshLayout.setEnabled(false);
                    break;
                default:
                    mRefreshLayout.setEnabled(true);
                    break;
            }
        }
        mStateView.setState(state);
    }

    /**
     * 动态设置是否启用下拉刷新功能
     *
     * @param enable true可刷新 false不可刷新
     */
    public void enableRefresh(boolean enable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnabled(enable);
        }
    }


    /**
     * 重置理拉刷新状态
     */
    public void resetRefreshStatus() {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishRefresh();
        }
    }

    /**
     * 获取下拉刷新控件，便于子类做一些定制化处理，比如下拉刷新的样式
     */
    public SmartRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    /**
     * 获取StateView实例，便于子类做处理
     */
    public StateView getStateView() {
        return mStateView;
    }

    /**
     * 获取扩展view的id，用于添加替换Fragment
     */
    public int getExtraViewId() {
        View extra = mStateView.extraView;
        if (extra != null) {
            return extra.getId();
        }
        return -1;
    }

}
