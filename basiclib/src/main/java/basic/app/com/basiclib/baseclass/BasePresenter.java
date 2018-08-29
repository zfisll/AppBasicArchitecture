package basic.app.com.basiclib.baseclass;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

public class BasePresenter<T extends IBaseView> {
    public T view;
    public LifecycleProvider<ActivityEvent> provider; //用来管理Observable，绑定到Activity或者Fragment
}
