package basic.app.com.basicres.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.ViewUtil;
import basic.app.com.basicres.R;

/**
 * author : user_zf
 * date : 18/4/28
 * desc : 弹窗规范，所有的弹窗都通过该代理创建，如果已有元素不能满足，可以通过serView()方法设置自定义view
 * 现有view：DialogEditView、DialogIconView、DialogOrderView、DialogWarrantOrderView、DialogExchangeView
 */
public class DialogBuilderProxy extends AlertDialog.Builder {

    private CharSequence title;
    private CharSequence positiveBtnText;
    private CharSequence negativeBtnText;
    private CharSequence msg;
    private View customContentView;
    private DialogInterface.OnClickListener mPositiveButtonListener;
    private DialogInterface.OnClickListener mNegativeButtonListener;
    private boolean cancleable = true;
    private Context mContext;
    private int mWidth = 0; //对话框的宽度

    public DialogBuilderProxy(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * 设置对话框标题的resId
     */
    @Override
    public DialogBuilderProxy setTitle(@StringRes int titleId) {
        return setTitle(ResourceUtil.getString(titleId));
    }

    /**
     * 设置对话框标题，如果不设置标题，则不显示标题
     */
    @Override
    public DialogBuilderProxy setTitle(@Nullable CharSequence title) {
        this.title = title;
        return this;
    }

    /**
     * 设置提示内容的resId
     */
    @Override
    public DialogBuilderProxy setMessage(@StringRes int messageId) {
        return setMessage(ResourceUtil.getString(messageId));
    }

    /**
     * 设置提示内容，如果不设置，则不显示。message还会根据是否有title来显示不同的样式
     */
    @Override
    public DialogBuilderProxy setMessage(@Nullable CharSequence message) {
        this.msg = message;
        return this;
    }

    /**
     * 设置"确定"按钮的文案resId和点击事件
     */
    @Override
    public DialogBuilderProxy setPositiveButton(@StringRes int textId, DialogInterface.OnClickListener listener) {
        return setPositiveButton(ResourceUtil.getString(textId), listener);
    }

    /**
     * 设置"确定"按钮的文案和点击事件，如果不设置，则不显示"确定"按钮
     */
    @Override
    public DialogBuilderProxy setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        positiveBtnText = text;
        mPositiveButtonListener = listener;
        return this;
    }

    /**
     * 设置"取消"按钮的文案resId和点击事件
     */
    @Override
    public DialogBuilderProxy setNegativeButton(@StringRes int textId, DialogInterface.OnClickListener listener) {
        return setNegativeButton(ResourceUtil.getString(textId), listener);
    }

    /**
     * 设置"取消"按钮的文案和点击事件，如果不设置，则不显示"取消"按钮
     */
    @Override
    public DialogBuilderProxy setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        negativeBtnText = text;
        mNegativeButtonListener = listener;
        return this;
    }

    @Override
    public DialogBuilderProxy setView(int layoutResId) {
        return setView(LayoutInflater.from(getContext()).inflate(layoutResId, null));
    }

    /**
     * 已有元素不能满足需求，可以设置自定义的bodyView
     */
    @Override
    public DialogBuilderProxy setView(View view) {
        customContentView = view;
        return this;
    }

    /**
     * 设置是否可以点击对话框以外的地方消失对话框
     */
    @Override
    public AlertDialog.Builder setCancelable(boolean cancelable) {
        this.cancleable = cancelable;
        return this;
    }

    /**
     * 设置对话框的宽度，如果不设置，则使用系统默认的宽度
     */
    public DialogBuilderProxy setWidth(int width) {
        this.mWidth = width;
        return this;
    }

    @Override
    public AlertDialog show() {
        // 弹窗前判断context有效性，避免BadTokenException
        if (AppUtil.isContextInvalid(getContext())) {
            return null;
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
        FrameLayout customBody = ViewUtil.findViewById(view, R.id.fl_builder_body);
        if (customContentView != null) {
            customBody.addView(customContentView);
        }
        TextView tvTitle = ViewUtil.findViewById(view, R.id.tv_builder_title);
        View vDivider = ViewUtil.findViewById(view, R.id.v_divider_1);
        View horDivider = ViewUtil.findViewById(view, R.id.v_builder_divider);
        TextView tvNegative = ViewUtil.findViewById(view, R.id.tvNegative);
        TextView tvPositive = ViewUtil.findViewById(view, R.id.tvPositive);
        View buttonLayout = ViewUtil.findViewById(view, R.id.dialog_button_layout);
        RelativeLayout rlRoot = ViewUtil.findViewById(view, R.id.rlRoot);

        //设置标题
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        //设置确认取消按钮
        if (TextUtils.isEmpty(negativeBtnText) && TextUtils.isEmpty(positiveBtnText)) {
            buttonLayout.setVisibility(View.GONE);
            horDivider.setVisibility(View.GONE);
        } else {
            horDivider.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(negativeBtnText)) {
                tvNegative.setVisibility(View.GONE);
                vDivider.setVisibility(View.GONE);
            } else {
                tvNegative.setText(negativeBtnText);
            }
            if (TextUtils.isEmpty(positiveBtnText)) {
                tvPositive.setVisibility(View.GONE);
                vDivider.setVisibility(View.GONE);
            } else {
                tvPositive.setText(positiveBtnText);
            }
            //确定和取消按钮同时存在，则确定按钮加粗
            if (!TextUtils.isEmpty(positiveBtnText) && !TextUtils.isEmpty(negativeBtnText)) {
                TextPaint paint = tvPositive.getPaint();
                paint.setFakeBoldText(true);
            }
        }

        //设置对话框提示内容
        TextView tvMsg = ViewUtil.findViewById(view, R.id.tv_builder_message);
        if (TextUtils.isEmpty(msg)) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setText(msg);
            tvMsg.setTextSize(TextUtils.isEmpty(title) ? 15 : 14);
            tvMsg.setTextColor(ResourceUtil.getColor(TextUtils.isEmpty(title) ? R.color.ui_text_2 : R.color.ui_text_3));
        }

        super.setView(view);
        final AlertDialog dialog = create();
        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mNegativeButtonListener != null) {
                    mNegativeButtonListener.onClick(dialog, AlertDialog.BUTTON_NEGATIVE);
                }
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mPositiveButtonListener != null) {
                    mPositiveButtonListener.onClick(dialog, AlertDialog.BUTTON_POSITIVE);
                }
            }
        });
        dialog.show();
        dialog.setOwnerActivity((Activity) mContext);
        dialog.setCanceledOnTouchOutside(cancleable);
        //设置宽度
        if (mWidth > 0 && dialog.getWindow() != null) { //设置了宽度
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = mWidth;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }
        return dialog;
    }
}
