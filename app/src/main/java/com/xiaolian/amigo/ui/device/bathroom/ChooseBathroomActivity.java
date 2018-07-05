package com.xiaolian.amigo.ui.device.bathroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomPresenter;
import com.xiaolian.amigo.ui.device.bathroom.intf.IChooseBathroomView;
import com.xiaolian.amigo.ui.widget.ZoomRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * 选择浴室
 *
 * @author zcd
 * @date 18/6/27
 */
public class ChooseBathroomActivity extends BathroomBaseActivity implements IChooseBathroomView {
    @Inject
    IChooseBathroomPresenter<IChooseBathroomView> presenter;

    private List<ChooseBathroomOuterAdapter.BathGroupWrapper> bathGroups = new ArrayList<>();

    private ZoomRecyclerView recyclerView;
    private ChooseBathroomAdapter innerAdapter;
    private ChooseBathroomOuterAdapter outerAdapter;
    private LinearLayout llLeft;
    private LinearLayout llRight;
    private TextView tvMissedBookingTime;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvLeft;
    private TextView tvRight;
    private ImageView ivHelp;
    /**
     * 当前是否处于选中状态 选中状态显示预约使用 非选中状态显示购买编码
     */
    private boolean isSelected = false;
    /**
     * 是否显示付费使用
     */
    private boolean isShowBuyUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bathroom);
        bindView();
        initRecyclerView();
    }

    private void bindView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());
        recyclerView = findViewById(R.id.recyclerView);
        llLeft = findViewById(R.id.ll_left);
        llRight = findViewById(R.id.ll_right);
        llLeft.setOnClickListener(v -> onLeftClick());
        llRight.setOnClickListener(v -> onRightClick());
        tvMissedBookingTime = findViewById(R.id.tv_missed_booking_time);
        ivLeft = findViewById(R.id.iv_left);
        ivRight = findViewById(R.id.iv_right);
        tvLeft = findViewById(R.id.tv_left);
        tvRight = findViewById(R.id.tv_right);
        ivHelp = findViewById(R.id.iv_help);
        ivHelp.setOnClickListener(v -> {
            isSelected = !isSelected;
            changeToBookingUse();
        });
    }

    private void initRecyclerView() {
        setMockData(bathGroups);
        outerAdapter = new ChooseBathroomOuterAdapter(this, R.layout.item_choose_bathroom_outer, bathGroups,
                (groupPosition, bathroomPosition) -> {
                    onSuccess(groupPosition + " " + bathroomPosition);
//                    isSelected = true;
                });
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        recyclerView.setLayoutParams(lp);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(outerAdapter);
        recyclerView.setOnGestureListener(new ZoomRecyclerView.OnGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector, float scaleFactor) {
                if (scaleFactor > 2
                        && !outerAdapter.isScaled()) {
                    outerAdapter.setScaled(true);
                } else if (scaleFactor < 2
                        && outerAdapter.isScaled()) {
                    outerAdapter.setScaled(false);
                }
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }
        });
    }

    private void changeToBookingUse() {
        if (!isSelected) {
            llRight.setVisibility(View.VISIBLE);
            tvMissedBookingTime.setVisibility(View.INVISIBLE);
            tvLeft.setText("购买编码");
            ivLeft.setImageResource(R.drawable.ic_bathroom_buy_code);
            tvRight.setText("扫一扫");
            ivRight.setImageResource(R.drawable.ic_bathroom_scan);
        } else {
            tvMissedBookingTime.setVisibility(View.VISIBLE);
            tvLeft.setText("预约使用");
            ivLeft.setImageResource(R.drawable.ic_bathroom_booking);
            if (isShowBuyUse) {
                llRight.setVisibility(View.VISIBLE);
                tvRight.setText("付费使用");
                ivRight.setImageResource(R.drawable.ic_bathroom_pay_use);
            } else {
                llRight.setVisibility(View.GONE);
            }
        }
    }

    private void onLeftClick() {
        if (isSelected) {
            startActivity(new Intent(this, BookingActivity.class));
        } else {
            startActivity(new Intent(this, BuyCodeActivity.class));
        }
    }

    private void onRightClick() {
        if (isSelected) {

        } else {
            startActivity(new Intent(this, PayUseActivity.class));
        }
    }

    private void setMockData(List<ChooseBathroomOuterAdapter.BathGroupWrapper> bathGroups) {
        List<ChooseBathroomAdapter.BathroomWrapper> inner1 = new ArrayList<>();
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("101", ChooseBathroomAdapter.BathroomStatus.NONE));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("102", ChooseBathroomAdapter.BathroomStatus.AVAILABLE));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("103", ChooseBathroomAdapter.BathroomStatus.NONE));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("104", ChooseBathroomAdapter.BathroomStatus.USING));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.ERROR));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.ERROR));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.ERROR));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.ERROR));
        inner1.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.ERROR));
        bathGroups.add(new ChooseBathroomOuterAdapter.BathGroupWrapper(inner1, "一层A"));
        List<ChooseBathroomAdapter.BathroomWrapper> inner2 = new ArrayList<>();
        inner2.add(new ChooseBathroomAdapter.BathroomWrapper("101", ChooseBathroomAdapter.BathroomStatus.NONE));
        inner2.add(new ChooseBathroomAdapter.BathroomWrapper("102", ChooseBathroomAdapter.BathroomStatus.ERROR));
        inner2.add(new ChooseBathroomAdapter.BathroomWrapper("103", ChooseBathroomAdapter.BathroomStatus.AVAILABLE));
        inner2.add(new ChooseBathroomAdapter.BathroomWrapper("104", ChooseBathroomAdapter.BathroomStatus.AVAILABLE));
        inner2.add(new ChooseBathroomAdapter.BathroomWrapper("105", ChooseBathroomAdapter.BathroomStatus.USING));
        bathGroups.add(new ChooseBathroomOuterAdapter.BathGroupWrapper(inner2, "一层B"));
    }
}
