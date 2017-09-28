package com.xiaolian.amigo.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.util.CountDownButtonHelper;
import com.xiaolian.amigo.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册第一步
 * <p>
 * Created by zcd on 9/19/17.
 */

public class RegisterStep1Fragment extends Fragment {

    @BindView(R.id.bt_submit)
    Button bt_submit;

    @OnClick(R.id.bt_submit)
    void checkVerification() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity)getActivity()).setMobileAndCode(et_mobile.getText().toString(),
                    et_verification_code.getText().toString());
            ((LoginActivity)getActivity()).checkVerificationCode(et_mobile.getText().toString(),
                    et_verification_code.getText().toString());
        }
    }


    @BindView(R.id.et_mobile)
    EditText et_mobile;

    @BindView(R.id.bt_send_verification_code)
    Button bt_send_verification_code;

    @OnClick(R.id.bt_send_verification_code)
    void sendVerificationCode() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).sendVerificationCode(et_mobile.getText().toString());
        }
    }

    @BindView(R.id.et_verification_code)
    EditText et_verification_code;

    CountDownButtonHelper cdb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_registry_step1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtil.setEditHintAndSize(getString(R.string.mobile_hint), 14, et_mobile);
        ViewUtil.setEditHintAndSize(getString(R.string.verification_code_hint), 14, et_verification_code);

        cdb = new CountDownButtonHelper(bt_send_verification_code, "获取验证码", 60, 1);
        cdb.setOnFinishListener(() -> {
            if (getContext() != null) {
                bt_send_verification_code.setEnabled(true);
                bt_send_verification_code.setText("获取验证码");
                int color = ContextCompat.getColor(getContext(), R.color.colorFullRed);
                bt_send_verification_code.setTextColor(color);
                bt_send_verification_code.setBackgroundResource(R.drawable.bg_rect_red_stroke);
            }
        });
    }


    public void startTimer() {
        int color = ContextCompat.getColor(getContext(), R.color.colorDarkB);
        bt_send_verification_code.setTextColor(color);
        bt_send_verification_code.setBackgroundResource(R.drawable.bg_rect_gray_stroke);
        cdb.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (cdb != null) {
            cdb.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (et_mobile != null) {
            et_mobile.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_mobile, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}