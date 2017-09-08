package com.xiaolian.amigo.activity.wallet;

import android.os.Bundle;
import android.widget.ListView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.activity.BaseActivity;
import com.xiaolian.amigo.activity.wallet.adaptor.PrepayAdaptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by caidong on 2017/9/8.
 */

public class PrepayActivity extends BaseActivity {

    static List<PrepayAdaptor.Prepay> prepays = new ArrayList<PrepayAdaptor.Prepay>() {
        {
            add(new PrepayAdaptor.Prepay("热水器：美女专用", "预付10000", "明年12:30"));
            add(new PrepayAdaptor.Prepay("热水器：美女专用", "预付10000", "明年12:30"));
            add(new PrepayAdaptor.Prepay("热水器：美女专用", "预付10000", "明年12:30"));
        }
    };

    @BindView(R.id.lv_prepays)
    ListView lv_prepays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_prepay);
        ButterKnife.bind(this);

        PrepayAdaptor adapter = new PrepayAdaptor(this, R.layout.item_wallet_prepay, prepays);
        lv_prepays.setAdapter(adapter);
    }
}