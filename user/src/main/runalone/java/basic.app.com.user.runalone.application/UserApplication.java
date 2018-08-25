package basic.app.com.user.runalone.application;

import android.app.Application;
import android.support.annotation.Nullable;

public class UserApplication extends Application {

    private static Application mAppCotext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppCotext = this;
    }

    @Nullable
    public static Application getAppContext() {
        return mAppCotext;
    }
}