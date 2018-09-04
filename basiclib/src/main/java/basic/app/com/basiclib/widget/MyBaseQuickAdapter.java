package basic.app.com.basiclib.widget;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : rvAdapter基类
 */
public abstract class MyBaseQuickAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public MyBaseQuickAdapter(int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        super.setNewData(data);
        if (getRecyclerView() != null && isLoadMoreEnable()) {
            disableLoadMoreIfNotFullPage();
        }
    }

    public void setDatas(List<T> data) {
        if (data != null) {
            super.mData.clear();
            super.mData.addAll(data);
        }
    }
}
