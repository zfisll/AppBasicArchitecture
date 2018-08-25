package basic.app.com.basiclib.baseclass;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayDeque;

import basic.app.com.basiclib.utils.ViewUtil;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 带状态（加载中、为空、错误、成功）的View，也可以通过setState方法切换状态
 */
public abstract class StateView extends SafeViewFlipper implements IStateView {

    private static final int INDEX_DATA = 0;

    public View loadingView;                // 等待的view（整个页面的加载态）
    public View errorView;                  // 错误的view（页面数据加载出错，整个页面错误态）
    public View emptyView;                  // 空的view（如列表页面数据为空，整个页面显示空白态）
    public View dataView;                   // 加载后的数据view（正常页面状态）
    public View extraView;                  // 扩展view（如页面未登录态，未开户态）
    public ViewState state;                 // 默认的状态
    private View mTarget;                   // 可滚动的view，用于解决滑动冲突
    private boolean isErrorClicked = true;  // 错误态是否可点击重新加载数据

    public StateView(Context context) {
        super(context);
        init();
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setState(ViewState.SUCCESS);
        ensureTarget();
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里设置监听是为了消费DOWN事件，才能触发MOVE事件经过SwipeRefreshLayout的onInterceptTouchEvent，才能正常看到下拉动作
                //如果不消费DOWN事件，DOWN事件会被SwipeRefreshLayout的onTouchEvent消费，
                //导致MOVE事件直接跳过onInterceptTouchEvent分发到onTouchEvent中，就看不到下拉动作*/
            }
        });
    }

    @Override
    public void setState(ViewState state) {
        //如果要设置的状态是loading或者当前状态和要设置的状态不一样时，才处理
        if (state == ViewState.LOADING || this.state != state) {
            this.state = state;
            showPagerView();
        }
    }

    /**
     * 是否开启错误view点击触发刷新功能，默认打开
     */
    public void enableErrorViewClick(boolean enable) {
        isErrorClicked = enable;
    }

    private void showPagerView() {
        switch (state) {
            case LOADING:
                if (loadingView == null) {
                    loadingView = getLoadingView();
                    if (loadingView != null) {
                        ViewUtil.removeSelfFromParent(loadingView);
                        addView(loadingView);
                    }
                }
                setDisplayedChild(indexOfChild(loadingView));
                onLoadData(); //开始执行加载数据操作
                break;
            case EMPTY:
                if (emptyView == null) {
                    emptyView = getEmptyView();
                    if (emptyView != null) {
                        ViewUtil.removeSelfFromParent(emptyView);
                        addView(emptyView);
                    }
                }
                setDisplayedChild(indexOfChild(emptyView));
                break;
            case ERROR:
                if (errorView == null) {
                    errorView = getErrorView();
                    if (errorView != null) {
                        ViewUtil.removeSelfFromParent(errorView);
                        addView(errorView);
                        errorView.setOnClickListener(isErrorClicked ? new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setState(ViewState.LOADING);
                            }
                        } : null);
                    }
                }
                setDisplayedChild(indexOfChild(errorView));
                break;
            case SUCCESS:
                if (dataView == null) {
                    dataView = getDataView();
                    if (dataView != null) {
                        ViewUtil.removeSelfFromParent(dataView);
                        addView(dataView, INDEX_DATA);
                    }
                }
                setDisplayedChild(INDEX_DATA);
                break;
            case EXTRA:
                if (extraView == null) {
                    extraView = new FrameLayout(getContext());
                    extraView.setId(ViewUtil.generateViewId());
                    if (extraView != null) {
                        ViewUtil.removeSelfFromParent(extraView);
                        addView(extraView);
                    }
                }
                setDisplayedChild(indexOfChild(extraView));
                break;
            default:
                break;
        }
    }

    /**
     * 确定用于判断canScrollVertically的View是哪一个，一般是ListView或ScrollView或webView(广度优先)
     */
    public void ensureTarget() {
        if (mTarget == null && dataView != null) {
            ArrayDeque<View> deque = new ArrayDeque<>();
            deque.add(dataView);
            while (!deque.isEmpty()) {
                View view = deque.poll();
                if (view != null) {
                    //显式指定View发须是ScrollView或者ListView或者webView（否则有可能找到一个TextView）
                    if (view.canScrollVertically(1) &&
                            (view instanceof ScrollView || view instanceof NestedScrollView || view instanceof ListView || view instanceof WebView
                                    || view instanceof RecyclerView)) {
                        mTarget = view;
                        break;
                    } else if (view instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) view;
                        for (int i = 0; i < group.getChildCount(); i++) {
                            View child = group.getChildAt(i);
                            deque.add(child);
                        }
                    }
                }
            }
        }
    }


    /**
     * 由于这是自定义View，用在SwipeRefreshLayout里需要重写canScrollVertically来判断是否可向上滚动
     */
    @Override
    public boolean canScrollVertically(int direction) {
        if (mTarget != null) {
            return mTarget.canScrollVertically(direction);
        }
        return super.canScrollVertically(direction);
    }
}
