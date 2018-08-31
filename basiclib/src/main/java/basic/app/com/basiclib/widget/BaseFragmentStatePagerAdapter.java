package basic.app.com.basiclib.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author : user_zf
 * date : 2018/8/31
 * desc : 基础FragmentStatePagerAdapter，适用于数量多、数据动态性较大的ViewPager，
 * 该类只保留limit之内的页面，当离开limit之后，就会被消除释放资源，回来的时候会生成新的页面，不会占用大量内存
 */
public abstract class BaseFragmentStatePagerAdapter<T> extends FragmentStatePagerAdapter {
    private List<T> mDataList = new ArrayList<>();

    public BaseFragmentStatePagerAdapter(FragmentManager fm, List<T> list) {
        super(fm);
        this.mDataList.clear();
        this.mDataList.addAll(list);
    }

    @Override
    public Fragment getItem(int position) {
        return createFragment(position, mDataList.get(position));
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 创建Fragment
     */
    public abstract Fragment createFragment(int position, T t);

    public List<T> getDataList() {
        return mDataList;
    }
}
