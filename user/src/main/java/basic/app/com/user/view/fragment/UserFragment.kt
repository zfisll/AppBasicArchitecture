package basic.app.com.user.view.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import basic.app.com.user.R
import com.luojilab.component.componentlib.router.ui.UIRouter
import kotlinx.android.synthetic.main.fragment_user.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 展示用户信息页面
 */
class UserFragment : Fragment() {

    private var mUserName: String? = null
    private var mUserAge: Int? = null
    private var mUserHobby: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserName = arguments?.getString(USER_NAME)
        mUserAge = arguments?.getInt(USER_AGE)
        mUserHobby = arguments?.getString(USER_HOBBY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvName.text = mUserName
        tvAge.text = mUserAge.toString()
        tvHobby.text = mUserHobby

        tvGotoMain.onClick {
            UIRouter.getInstance().openUri(activity, "DDComp://app/main", null)
        }
    }

    companion object {
        private const val USER_NAME = "USER_NAME"
        private const val USER_AGE = "USER_AGE"
        private const val USER_HOBBY = "USER_HOBBY"

        @JvmStatic
        fun newFragment(userName: String, userAge: Int, userHobby: String): UserFragment {
            val bundle = Bundle()
            bundle.putString(USER_NAME, userName)
            bundle.putInt(USER_AGE, userAge)
            bundle.putString(USER_HOBBY, userHobby)
            val fragment = UserFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
