package com.xiaolian.amigo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * 搜索框
 *
 * @author caidong
 * @date 17/9/13
 */

public class SearchDialog extends Dialog implements TextWatcher {

    @BindView(R.id.et_search_content)
    EditText etSearchContent;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
//    @BindView(R.id.iv_clear)
//    ImageView ivClear;
    @BindView(R.id.rl_result)
    RelativeLayout rlResult;
    @BindView(R.id.fl_result_contain)
    FrameLayout flResultContain;
    @BindView(R.id.tv_no_result_tip)
    TextView tvNoResultTip;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    private OnSearchListener listener;

    Context context;

    public SearchDialog(AppCompatActivity context) {
        super(context, R.style.Search_Dialog);
        this.context = context;
//        setOwnerActivity((Activity)context);

        Window window = this.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.TOP);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        setCancelable(true);

        setContentView(R.layout.dialog_lost_and_found_search2);
        ButterKnife.bind(this);

        etSearchContent.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = etSearchContent.getText().toString().trim();
//        ivClear.setVisibility(input.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @OnEditorAction(R.id.et_search_content)
    boolean search(TextView v, int actionId, KeyEvent event) {
        // 判断如果用户输入的是搜索键
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            this.dismiss();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            if (listener != null) {
                listener.onSearch(etSearchContent.getText().toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        TransitionManager.beginDelayedTransition(llContainer, new ChangeBounds().setDuration(1000));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) etSearchContent.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        etSearchContent.setLayoutParams(lp);
        tvCancel.setVisibility(View.VISIBLE);

        rlResult.setVisibility(View.GONE);
        if (flResultContain.getChildCount() > 0) {
            flResultContain.removeAllViews();
        }
    }

    @Override
    public void dismiss() {
        TransitionManager.beginDelayedTransition(llContainer);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) etSearchContent.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        etSearchContent.setLayoutParams(lp);
        tvCancel.setVisibility(View.GONE);
        super.dismiss();
    }

    /**
     * 点击清除图标
     */
//    @OnClick({R.id.iv_clear, R.id.v_clear_holder})
//    void clear() {
//        etSearchContent.setText("");
//        ivClear.setVisibility(View.GONE);
//    }

//    @OnClick(R.id.iv_back)
//    void back() {
//        this.dismiss();
//    }

    @OnClick(R.id.tv_cancel)
    void cancelSearch() {
        this.dismiss();
    }

    public void showNoResult(String selectKey) {
        rlResult.setVisibility(View.VISIBLE);
    }

    public void showResult(View view) {
        rlResult.setVisibility(View.GONE);
        if (flResultContain.getChildCount() > 0) {
            flResultContain.removeAllViews();
        }
        view.setBackgroundResource(R.color.colorBackgroundGray);
        flResultContain.addView(view);
    }

    public void setSearchListener(OnSearchListener listener) {
        this.listener = listener;
    }

    public interface OnSearchListener {
        void onSearch(String searchStr);
    }
}
