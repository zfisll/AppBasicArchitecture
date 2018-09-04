package basic.app.com.basiclib.baseclass;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import basic.app.com.basiclib.utils.ClassUtil;


/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 基础Activity，实现了View和Presenter的绑定以及一些基础UI样式配置
 */
public abstract class UIBaseActivity<T extends BasePresenter> extends RxAppCompatActivity
        implements IStateView, IUIBaseConfig, IBaseView {

    private UIHelper mHelper; //ui布局helper
    private View mDataView; //最终数据展示界面
    private ProgressDialog mProgressDialog; //等待框，这里实现了一个默认的等待框，可以被覆写showWaiting和dismissWaiting改变样式和逻辑
    public T presenter; //界面对应的presenter

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (useNewArchitecture()) {
            mHelper = new UIHelper(this, this, this);
            setContentView(mHelper.initView());
            preInit(savedInstanceState); //做一些比View更基础的初始化动作，例如ActionBar
            initPresenter();
            if (!interceptInitLayout()) {
                initLayout(mHelper.getStateView().dataView);
            }
        }
    }

    /**
     * 是否拦截initLayout操作
     * 可用于比如进入界面时需要交易登录态，那就可以覆写些方法，没登录时拦截初始化操作，跳转去登录，登录完成后再执行初始化动作
     */
    public boolean interceptInitLayout() {
        return false;
    }

//    @Override
//    public int getRootViewBackgroundColor() {
//        return R.color.
//    }


    @Override
    public int getRootViewBackgroundColorResId() {
        return android.R.color.transparent;
    }

    @Override
    public boolean supportChangeSkin() {
        return false;
    }

    @Override
    public void setState(ViewState state) {
        mHelper.setState(state);
    }


    @Override
    public View getDataView() {
        if (mDataView == null) {
            mDataView = LayoutInflater.from(this).inflate(getLayoutResource(), null);
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


    @Override
    public void resetRefreshStatus() {
        mHelper.resetRefreshStatus();
    }


    /**
     * 提供给子类覆写做一些比View更基础的初始化动作，例如ActionBar
     */
    public void preInit(@Nullable Bundle savedInstanceState) {

    }

    @SuppressWarnings("unchecked")
    private void initPresenter() {
        try {
            presenter = (T) ClassUtil.getActualTypeClass(UIBaseActivity.this.getClass(), 0).newInstance();
            if (presenter != null) {
                presenter.view = this;
                presenter.activityProvider = this;
            }
        } catch (Exception e) {
            /* no-op */
        }
    }

    public UIHelper getHelper() {
        return mHelper;
    }

    @Override
    public void showWaiting(CharSequence msg, boolean canDismissByUser) {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(canDismissByUser);
        mProgressDialog.setCancelable(canDismissByUser);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();

    }

    @Override
    public void dismissWaiting() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

    }

    /**
     * 是否使用新框架进行布局和逻辑的初始化
     * 如果是旧项目引入该框架，则默认改为false，新增的Activity自己设置为true
     */
    public boolean useNewArchitecture() {
        return true;
    }

}
