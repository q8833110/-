package com.xiaolian.amigo.ui.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaolian.amigo.MvpApp;
import com.xiaolian.amigo.di.componet.DaggerOrderActivityComponent;
import com.xiaolian.amigo.di.componet.OrderActivityComponent;
import com.xiaolian.amigo.di.module.OrderActivityModule;
import com.xiaolian.amigo.ui.base.BaseActivity;


/**
 * 订单模块baseActivity
 *
 * @author caidong
 * @date 17/9/15
 */
public abstract class OrderBaseActivity extends BaseActivity {

    private OrderActivityComponent mActivityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityComponent = DaggerOrderActivityComponent.builder()
                .orderActivityModule(new OrderActivityModule(this))
                .applicationComponent(((MvpApp) getApplication()).getComponent())
                .build();

    }

    public OrderActivityComponent getActivityComponent() {
        return mActivityComponent;
    }
}
