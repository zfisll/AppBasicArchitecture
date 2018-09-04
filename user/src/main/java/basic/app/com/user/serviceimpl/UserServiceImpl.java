package basic.app.com.user.serviceimpl;


import android.support.v4.app.Fragment;

import org.jetbrains.annotations.NotNull;

import basic.app.com.routerservice.service.IUserService;
import basic.app.com.user.view.fragment.NewsFragment;
import basic.app.com.user.view.fragment.UserFragment;

public class UserServiceImpl implements IUserService {
    @NotNull
    @Override
    public Fragment getUserFragment() {
        return UserFragment.newFragment();
    }

    @NotNull
    @Override
    public Fragment getNewsFragment() {
        return NewsFragment.newFragment();
    }
}
