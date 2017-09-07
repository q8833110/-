package com.xiaolian.amigo.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xiaolian.amigo.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyi on 2017/9/7.
 */

public class Formlayout extends LinearLayout {

    List<ClearableEditText> editTexts = new ArrayList<>(6);
    Map<String, String> results = new HashMap<>(6);

    private int defaultKey;

    Button submitButton;

    public Formlayout(Context context) {
        super(context);
    }

    public Formlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Formlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initEditText() {
        editTexts.clear();
        getEditTexts(this);
    }

    /**
     * 获取form 表单的 key->value
     *
     * @return
     */
    public Map<String, String> getDataMap() {
        defaultKey = 0;
        results.clear();
        for (EditText editText : editTexts) {
            Object tag = editText.getTag();
            String r = editText.getText().toString();
            if ((tag == null || !tag.equals("null")) && TextUtils.isEmpty(r)) {
                showMsg(editText.getHint().toString());
                return null;
            }
            if (tag == null || tag.equals("null")) {
                defaultKey++;
                results.put(defaultKey + "", r);
            } else {
                results.put(tag.toString(), r);
            }
        }
        return results;
    }

    private void getEditTexts(ViewGroup vg) {
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof ClearableEditText) {
                ClearableEditText editText = (ClearableEditText) view;
                editText.addTextChangedListener(new EditTextWatcher(editText));

                editTexts.add(editText);
            } else if (view instanceof ViewGroup) {
                getEditTexts((ViewGroup) view);
            }
            if (view instanceof Button && view.getId() == R.id.bt_submit) {
                submitButton = (Button) view;
            }
        }
    }

    protected void showMsg(String hintMessage) {
        Toast.makeText(getContext(), hintMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initEditText();
    }

    final class EditTextWatcher implements TextWatcher {
        private ClearableEditText editText;

        EditTextWatcher(ClearableEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (TextUtils.getTrimmedLength(text) > 0) {
                editText.validated = true;
            } else {
                editText.validated = false;
            }

            //可以想办法去掉循环，提高性能
            boolean allValidated = true;
            for (ClearableEditText clearableEditText : editTexts) {
                allValidated &= clearableEditText.validated;
            }
            submitButton.setEnabled(allValidated);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
