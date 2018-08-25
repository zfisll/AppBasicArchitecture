package basic.app.com.user.serviceimpl;

import android.app.Fragment;

import org.jetbrains.annotations.NotNull;

import basic.app.com.routerservice.service.IUserService;
import basic.app.com.user.view.fragment.UserFragment;

public class UserServiceImpl implements IUserService {
    @NotNull
    @Override
    public Fragment getUserFragment(@NotNull String userName, int age, @NotNull String hobby) {
        return UserFragment.newFragment(userName, age, hobby);
    }
}
