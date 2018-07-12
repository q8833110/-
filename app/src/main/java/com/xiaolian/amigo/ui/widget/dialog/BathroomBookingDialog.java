package com.xiaolian.amigo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.widget.CircleProgressView;

/**
 * @author zcd
 * @date 18/7/6
 */
public class BathroomBookingDialog extends Dialog {
    private Context context;
    private CircleProgressView circleProgressView;

    public BathroomBookingDialog(@NonNull Context context) {
        super(context, R.style.AlertDialogStyle);
        Window window = this.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.CENTER);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_bathroom_booking);
        circleProgressView = $(R.id.circleProgressView);
    }

    public void finishProcess() {
        circleProgressView.setProgressInTime(100, 300);
    }

    @Override
    public void show() {
        super.show();
        circleProgressView.runProgressAnim(1000);
        circleProgressView.postDelayed(this::finishProcess, 1500);
    }

    private <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }
}