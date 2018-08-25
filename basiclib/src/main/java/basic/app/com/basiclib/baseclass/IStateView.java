package basic.app.com.basiclib.baseclass;

import android.view.View;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 定义带状态View的行为
 */
public interface IStateView {
    enum ViewState {
        LOADING, ERROR, EMPTY, SUCCESS, EXTRA
    }

    View getDataView();    //获取数据视图

    View getEmptyView();   //获取为空视图

    View getErrorView();   //获取错误视图

    View getLoadingView(); //获取加载中视图

    void onLoadData();     //加载数据

    void setState(IStateView.ViewState state); //设置状态
}
