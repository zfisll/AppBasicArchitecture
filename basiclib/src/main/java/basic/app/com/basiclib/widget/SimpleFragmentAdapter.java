package basic.app.com.basiclib.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import basic.app.com.basiclib.utils.CollectionUtil;

/**
 * author : user_zf
 * date : 2018/8/31
 * desc : 适用于数量少，相对静态的ViewPager
 * 该类生成的每个Fragment都将保存在内存之中，处于limit之外，则销毁View，但不会销毁Framgent，下次回来继续加载该Framgent
 */
public class SimpleFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> list = new ArrayList<>();
    private List<String> tabTitles = new ArrayList<>();
    private boolean[] fragmentsUpdateFlags;
    private FragmentManager fm;

    public SimpleFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list.clear();
        this.list.addAll(list);
        fragmentsUpdateFlags = new boolean[list.size()];
        Arrays.fill(fragmentsUpdateFlags, false);
        this.fm = fm;
    }

    public SimpleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        String tag = fragment.getTag();

        // 如果这个Fragment需要更新
        if (fragmentsUpdateFlags[position % fragmentsUpdateFlags.length]) {
            FragmentTransaction ft = fm.beginTransaction();
            // 移除旧的fragment
            ft.remove(fragment);
            fragment = list.get(position % list.size());
            //添加新fragment时必须用前面获得的tag
            if (fragment.isAdded()) {  //增加已添加判断，否则会抛异常
                ft.remove(fragment).commitAllowingStateLoss();
                ft = fm.beginTransaction();
            }

            ft.add(container.getId(), fragment, tag);
            ft.attach(fragment);
            ft.commitAllowingStateLoss();
            // 复位更新标志
            fragmentsUpdateFlags[position % fragmentsUpdateFlags.length] = false;
        }

        return fragment;

    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    public void addData(List<Fragment> newData) {
        list.addAll(newData);
        Arrays.fill(fragmentsUpdateFlags, false);
        notifyDataSetChanged();
    }

    /**
     * 清空ViewPager中的内容
     */
    public void clearContent() {
        list.clear();
        Arrays.fill(fragmentsUpdateFlags, false);
        notifyDataSetChanged();
    }

    /**
     * 更新ViewPager数据源
     *
     * @param position 被更新fragment位置
     * @param fragment 新fragment
     */
    public void updateFragment(int position, Fragment fragment) {
        if (position < fragmentsUpdateFlags.length && position < list.size()) {
            fragmentsUpdateFlags[position] = true;
            list.set(position, fragment);
            // 刷新
            notifyDataSetChanged();
        }
    }

    /**
     * 更新ViewPager数据源，原理：清除缓存的fragment，然后重新创建，达到更新视图的目的
     * 缺点：会造成不必要的浪费->list<Fragment>，而且影响性能
     */
    public void setFragments(List<Fragment> mFragmentList) {
        if (!CollectionUtil.isEmpty(list)) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            for (Fragment f : this.list) {
                fragmentTransaction.remove(f);
            }
            fragmentTransaction.commitAllowingStateLoss();
            fm.executePendingTransactions();
            this.list.clear();
            this.list.addAll(mFragmentList);
            notifyDataSetChanged();
        }
    }

    /**
     * notifyDataSetChanged()方法中会调用该方法判断是否需要重建Fragment，该方法默认返回POSITION_UNCHANGED
     * 即notifiyDataSetChanged方法调用后不会有任何变化，而如果需要重建Fragment，则返回POSITION_NONE
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setTabTitles(List<String> tabTitles) {
        this.tabTitles.clear();
        this.tabTitles.addAll(tabTitles);
    }

    //设置tablayout标题
    @Override
    public CharSequence getPageTitle(int position) {
        if (tabTitles.size() > 0) {
            return tabTitles.get(position);
        }
        return "";
    }

    public List<Fragment> getData() {
        return list;
    }

}
