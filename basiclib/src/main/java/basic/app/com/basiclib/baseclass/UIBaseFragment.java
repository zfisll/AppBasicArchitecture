package basic.app.com.basiclib.baseclass;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluestone.common.baseclass.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import basic.app.com.basiclib.utils.ClassUtil;
import basic.app.com.basiclib.utils.CollectionUtil;


/**
 * Created by dylan on 5/8/17.
 * 基础Fragment，实现了View和Presenter的绑定以及一些基础UI样式配置
 */
public abstract class UIBaseFragment<T extends BasePresenter> extends Fragment
        implements IStateView, IUIBaseConfig, IBaseView, DialogInterface.OnDismissListener {

    public boolean isInitDone = false; //是否初始化完成
    public boolean isLazyLoadTricked = false; //是否触发过懒加载
    public T presenter; //界面对应的presenter
    private UIHelper mHelper; //ui布局helper
    private View mDataView; //最终数据展示界面
    private ProgressDialog mProgressDialog; //等待框，这里实现了一个默认的等待框，可以被覆写showWaiting和dismissWaiting改变样式和逻辑
    // 记录当前Fragment的所有子Fragment
    private List<Fragment> mFragments = new ArrayList<>();
    // 增加可见不可见的标识位，避免重复调用，当调用onVisible后会被置为true，调用onInvisible后置为false
    private boolean visibleFlag = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 监听所有子Fragment的生命周期
        getChildFragmentManager().registerFragmentLifecycleCallbacks(mLifecycleCallbacks, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isInitDone = false;
        mHelper = new UIHelper(getContext(), this, this);
        return mHelper.initView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 为了兼容旧的使用方法，判断如果helper为空则不执行，因为旧写法要重写onCreateView,没有实例化helper
        if (mHelper != null) {
            init();
        }
    }

    private void init() {
        initPresenter();
        initLayout(mHelper.getStateView().dataView);
        isInitDone = true;
        isLazyLoadTricked = false;
        // 初始化完成后，判断fragment是否可见，满足条件就去执行加载数据动作
        if (isLazyLoadEnable() && isFragmentVisible() && isParentFragmentVisible(this)) {
            isLazyLoadTricked = true;
            if (getLoadingView() == null) {
                onLoadData();
            } else {
                setState(ViewState.LOADING);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // onResume并不代表fragment可见
        // 如果是在viewpager里，就需要判断getUserVisibleHint，不在viewpager时，getUserVisibleHint默认为true
        // 如果是其它情况，就通过isHidden判断，因为show/hide时会改变isHidden的状态
        // 所以，只有当fragment原来是可见状态时，进入onResume就回调onVisible
        // 另外，增加了父Fragment的可见判断，如果父Fragment不可见，那子Fragment也不可见
        // visibleFlag是用来避免重复调用的
        if (isFragmentVisible() && isParentFragmentVisible(this) && !visibleFlag) {
            onVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // onPause时也需要判断，如果当前fragment在viewpager中不可见，就已经回调过了，onPause时也就不需要再次回调onInvisible了
        // 所以，只有当fragment是可见状态时进入onPause才加调onInvisible
        // 另外，增加对父Fragment的可见判断，如果父Fragment不可见，那子Fragment本来就不可见，就不需要再调用onInvisible了
        // visibleFlag是用来避免重复调用的
        if (getUserVisibleHint() && !isHidden() && isParentFragmentVisible(this) && visibleFlag) {
            onInvisible();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getChildFragmentManager().unregisterFragmentLifecycleCallbacks(mLifecycleCallbacks);
    }

    /**
     * 当fragment与viewpager、FragmentPagerAdapter一起使用时，切换页面时会调用此方法
     *
     * @param isVisibleToUser 是否对用户可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        boolean change = isVisibleToUser != getUserVisibleHint();
        super.setUserVisibleHint(isVisibleToUser);
        // 在viewpager中，创建fragment时就会调用这个方法，但这时还没有resume，为了避免重复调用visible和invisible，
        // 只有当fragment状态是resumed并且初始化完毕后才进行visible和invisible的回调
        if (isResumed() && change) {
            if (isFragmentVisible() && isParentFragmentVisible(this)) {
                onVisible();
            } else {
                onInvisible();
            }
        }
    }

    /**
     * 当使用show/hide方法时，会触发此回调
     *
     * @param hidden fragment是否被隐藏
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isFragmentVisible() && isParentFragmentVisible(this)) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 判断当前fragment是否可见
     *
     * @return true 可见 false 不可见
     */
    public boolean isFragmentVisible() {
        return isResumed() && getUserVisibleHint() && !isHidden();
    }

    /**
     * 判断ViewPager内的Fragment是否可见
     * 需要判断其父Fragment是否可见，因为Viewpager中第一个元素默认可见，如果此时其父fragment不可见，此时就会出错
     */
    public boolean isViewPagerFragmentVisible() {
        return isFragmentVisible() && isParentFragmentVisible(this);
    }

    /**
     * 判断父Fragment是否可见
     *
     * @return true 可见 false 不可见
     */
    public boolean isParentFragmentVisible(Fragment fragment) {
        // 如果传入的fragment为null，则直接返回true
        if (fragment == null) {
            return true;
        }
        // 查找父Fragment，判断父Fragment的可见状态
        Fragment parent = fragment.getParentFragment();
        boolean parentFragmentVisible = true;
        if (parent != null && parent instanceof UIBaseFragment) {
            parentFragmentVisible = ((UIBaseFragment) parent).isFragmentVisible();
        }
        // 如果父Fragment不可见，就直接返回false了，如果父Fragment可见，则递归查询，直到没有父Fragment为止
        return parentFragmentVisible && isParentFragmentVisible(parent);
    }

    /**
     * 可见回调
     */
    public void onVisible() {
        LogUtil.info(getClass().getSimpleName() + " onVisible");
        visibleFlag = true;
        //如果开启了懒加载，并同时满足初始化完成以及未触发过懒加载，就去执行加载数据动作
        if (isLazyLoadEnable() && !isLazyLoadTricked && isInitDone) {
            isLazyLoadTricked = true;
            if (getLoadingView() == null) {
                onLoadData();
            } else {
                setState(ViewState.LOADING);
            }
        }
        // 遍历子fragment，如果子fragment处于可见状态，则手动触发回调
        if (!CollectionUtil.isEmpty(mFragments)) {
            for (Fragment fragment : mFragments) {
                if (fragment != null && fragment instanceof UIBaseFragment) {
                    UIBaseFragment baseFragment = (UIBaseFragment) fragment;
                    // 这里只处理可见状态的fragment，因为不可见的已经处理过了，只有嵌套的可见fragment要手动处理
                    if (baseFragment.isFragmentVisible() && baseFragment.isParentFragmentVisible(baseFragment)
                            && !baseFragment.getVisibleFlag()) {
                        baseFragment.onVisible();
                    }
                }
            }
        }
    }

    /**
     * 不可见回调
     */
    public void onInvisible() {
        LogUtil.info(getClass().getSimpleName() + " onInvisible");
        visibleFlag = false;
        // 遍历子fragment，如果子fragment处于可见状态，则手动触发回调
        if (!CollectionUtil.isEmpty(mFragments)) {
            for (Fragment fragment : mFragments) {
                if (fragment != null && fragment instanceof UIBaseFragment) {
                    UIBaseFragment baseFragment = (UIBaseFragment) fragment;
                    // 这里只处理可见状态的fragment，因为不可见的已经处理过了，只有嵌套的可见fragment要手动处理
                    // 注意，这里没有增加父Fragment的判断是因为走到这里时，说明本Fragment已经是不可见了，
                    // 那对于他的子Fragment来说，isParentFragmentVisible肯定返回false
                    if (baseFragment.isFragmentVisible() && baseFragment.getVisibleFlag()) {
                        baseFragment.onInvisible();
                    }
                }
            }
        }
        // 不可见回调
    }

    public boolean getVisibleFlag() {
        return visibleFlag;
    }

//    @Override
//    public int getRootViewBackgroundColor() {
//        return Color.TRANSPARENT;
//    }


    @Override
    public boolean supportChangeSkin() {
        return false;
    }

    @Override
    public int getRootViewBackgroundColorResId() {
        return android.R.color.transparent;
    }

    @Override
    public void setState(ViewState state) {
        mHelper.setState(state);
    }



    @Override
    public View getDataView() {
        if (mDataView == null && getContext() != null) {
            mDataView = LayoutInflater.from(getContext()).inflate(getLayoutResource(), null);
        }
        return mDataView;
    }

    /**
     * 是否启用下拉刷新
     */
    @Override
    public boolean isRefreshEnable() {
        return false;
    }

    /**
     * 是否启用懒加载模式，用于ViewPager
     * 懒加载开启后不会直接进入加载数据的操作，需要可见后才会触发
     */
    public boolean isLazyLoadEnable() {
        return false;
    }



    @Override
    public void resetRefreshStatus() {
        mHelper.resetRefreshStatus();
    }

    /**
     * 获取uiHelper实例
     */
    public UIHelper getHelper() {
        return mHelper;
    }

    /**
     * 重置懒加载标识，重置后下次可见时还会再调用加载
     */
    public void resetLazyTrickFlag() {
        setUserVisibleHint(false);
        isLazyLoadTricked = false;
    }

    /**
     * 动态根据presenter类型生成实例，并执行presenter和view的绑定
     */
    @SuppressWarnings("unchecked")
    public void initPresenter() {
        try {
            presenter = (T) ClassUtil.getActualTypeClass(UIBaseFragment.this.getClass(), 0).newInstance();
            if (presenter != null) {
                presenter.view = this;
            }
        } catch (Exception e) {
            LogUtil.error(e,e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showWaiting(CharSequence msg, boolean canDismissByUser) {
        //如果当前fragment没有被加到activity中,就不处理显示等待框逻辑
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                return;
            }
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(canDismissByUser);
            mProgressDialog.setCancelable(canDismissByUser);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setOnDismissListener(this);
            mProgressDialog.show();
        }
    }

    @Override
    public void dismissWaiting() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    FragmentManager.FragmentLifecycleCallbacks mLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            mFragments.add(f);
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentDestroyed(fm, f);
            mFragments.remove(f);
        }
    };
}
