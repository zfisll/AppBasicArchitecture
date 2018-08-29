package basic.app.com.basicres.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import basic.app.com.basicres.R;

/**
 * Created by user_zf on 17/1/7.
 */

public class MyProgressDialog extends Dialog {


    public MyProgressDialog(Context context, CharSequence message){
        super(context, R.style.auto_size_dialog);
        setContentView(R.layout.dialog_my_progress);
        initView(message);
    }


    private void initView(CharSequence message) {
        TextView tvTip = findViewById(R.id.tvTip);
        tvTip.setText(message);
    }

    public static MyProgressDialog newInstance(Context context, String message){
        return new MyProgressDialog(context, message);
    }

}
