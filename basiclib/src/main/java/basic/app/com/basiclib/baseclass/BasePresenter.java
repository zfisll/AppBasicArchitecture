package basic.app.com.basiclib.baseclass;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

public class BasePresenter<T extends IBaseView> {
    public T view;
    public LifecycleProvider<ActivityEvent> activityProvider; //用来管理Observable，绑定到Activity
    public LifecycleProvider<FragmentEvent> fragmentProvider; //用来管理Observable，绑定到Fragment
}
