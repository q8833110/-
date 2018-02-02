package com.xiaolian.amigo.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ObjectsCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.ComplaintType;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.network.model.order.OrderDetailRespDTO;
import com.xiaolian.amigo.ui.base.WebActivity;
import com.xiaolian.amigo.ui.order.intf.IOrderDetailPresenter;
import com.xiaolian.amigo.ui.order.intf.IOrderDetailView;
import com.xiaolian.amigo.util.CommonUtil;
import com.xiaolian.amigo.util.Constant;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 消费账单详情
 *
 * @author caidong
 * @date 17/9/18
 */
public class OrderDetailActivity extends OrderBaseActivity implements IOrderDetailView {
    private static final int ORDER_ERROR_STATUS = 3;
    @Inject
    IOrderDetailPresenter<IOrderDetailView> presenter;

    /**
     * 账单标题
     */
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    /************** 正常账单内容 ****************/
    @BindView(R.id.ll_order_normal)
    LinearLayout llOrderNormal;
    @BindView(R.id.rl_use_bonus)
    RelativeLayout rlUseBonus;
    /**
     * 代金券抵扣
     */
    @BindView(R.id.tv_bonus_remark)
    TextView tvBonusRemark;
    /**
     * 预付金额
     */
    @BindView(R.id.tv_prepay)
    TextView tvPrepay;
    /**
     * 实际扣款
     */
    @BindView(R.id.tv_actual_debit)
    TextView tvActualDebit;
    @BindView(R.id.rl_actual_debit)
    RelativeLayout rlActualDebit;
    /**
     * 实际消费
     */
    @BindView(R.id.tv_consume)
    TextView tvConsume;
    /**
     * 找零金额
     */
    @BindView(R.id.tv_odd)
    TextView tvOdd;


    /************** 异常账单内容 ****************/
    @BindView(R.id.tv_order_error_tip)
    TextView tvOrderErrorTip;
    @BindView(R.id.ll_order_error)
    LinearLayout llOrderError;
    @BindView(R.id.rl_back_bonus)
    RelativeLayout rlBackBonus;
    /**
     * 退还代金券
     */
    @BindView(R.id.tv_back_bonus)
    TextView tvBackBonus;
    /**
     * 退还金额
     */
    @BindView(R.id.tv_back_amount)
    TextView tvBackAmount;

    /************** 基础信息 *****************/
    /**
     * 使用时间
     */
    @BindView(R.id.tv_time)
    TextView tvTime;
    /**
     * 使用时长
     */
    @BindView(R.id.tv_used_time)
    TextView tvUsedTime;
    @BindView(R.id.rl_used_time)
    RelativeLayout rlUsedTime;
    /**
     * 设备位置
     */
    @BindView(R.id.tv_device_location)
    TextView tvDeviceLocation;
    /**
     * 订单号
     */
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    /**
     * 是否最低消费
     */
    @BindView(R.id.tv_order_no_use_tip)
    TextView tvOrderNoUseTip;

    private Long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);

        setUp();
        presenter.onAttach(this);
        presenter.getOrder(orderId);
    }

    @Override
    protected void setUp() {
        Intent intent = getIntent();
        this.orderId = intent.getLongExtra(Constant.EXTRA_KEY, -1);
    }

    /**
     * 投诉
     */
    @OnClick(R.id.tv_complaint)
    public void complaint() {
        presenter.checkComplaint();
    }

    /**
     * 复制
     */
    @OnClick(R.id.tv_copy)
    public void copy() {
        CommonUtil.copy(tvOrderNo.getText().toString(), getApplicationContext());
        onSuccess(R.string.copy_success);
    }

    @Override
    public void showNoUseTip() {
        tvOrderNoUseTip.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_order_no_use_tip)
    public void toNoUseHelp() {
        startActivity(new Intent(this, WebActivity.class)
                .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_NO_USE_HELP));
    }

    @Override
    public void toComplaint() {
        String token = presenter.getToken();
        OrderDetailRespDTO order = presenter.getOrder();
        startActivity(new Intent(this, WebActivity.class)
                .putExtra(WebActivity.INTENT_KEY_URL, Constant.H5_COMPLAINT
                        + "?token=" + token
                        + "&orderId=" + order.getId()
                        + "&orderNo=" + order.getOrderNo()
                        + "&orderType="
                        + ComplaintType.getComplaintTypeByDeviceType(
                        Device.getDevice(order.getDeviceType())).getType()));
    }

    @Override
    public void renderView(OrderDetailRespDTO order) {
        // 设置基础信息
        tvTime.setText(CommonUtil.stampToDate(order.getCreateTime()));
        tvDeviceLocation.setText(String.format("%s %s",
                Device.getDevice(order.getDeviceType()).getDesc(), order.getLocation()));
        if (Device.getDevice(order.getDeviceType()) == Device.DRYER) {
            rlUsedTime.setVisibility(View.VISIBLE);
            tvUsedTime.setText(order.getUseTime());
        }
        tvOrderNo.setText(order.getOrderNo());
        if (ObjectsCompat.equals(order.getStatus(), ORDER_ERROR_STATUS)) {
            // 异常账单
            tvOrderErrorTip.setVisibility(View.VISIBLE);
            llOrderNormal.setVisibility(View.GONE);
            llOrderError.setVisibility(View.VISIBLE);
            // 是否有代金券
            if (TextUtils.isEmpty(order.getBonus())) {
                // 没有代金券
                rlBackBonus.setVisibility(View.GONE);
                tvConsume.setTextColor(ContextCompat.getColor(this, R.color.colorFullRed));
                tvActualDebit.setTextColor(ContextCompat.getColor(this, R.color.colorDark6));
                rlActualDebit.setVisibility(View.GONE);
            } else {
                // 有代金券
                rlBackBonus.setVisibility(View.VISIBLE);
                tvBackBonus.setText(order.getBonus());
                tvConsume.setTextColor(ContextCompat.getColor(this, R.color.colorDark6));
                tvActualDebit.setTextColor(ContextCompat.getColor(this, R.color.colorFullRed));
            }
            tvBackAmount.setText(order.getPrepay());
        } else {
            // 正常账单
            tvOrderErrorTip.setVisibility(View.GONE);
            llOrderNormal.setVisibility(View.VISIBLE);
            llOrderError.setVisibility(View.GONE);
            // 是否有代金券
            if (TextUtils.isEmpty(order.getBonus())) {
                // 没有代金券
                rlUseBonus.setVisibility(View.GONE);
                tvConsume.setTextColor(ContextCompat.getColor(this, R.color.colorFullRed));
                tvActualDebit.setTextColor(ContextCompat.getColor(this, R.color.colorDark6));
                rlActualDebit.setVisibility(View.GONE);
            } else {
                // 有代金券
                rlUseBonus.setVisibility(View.VISIBLE);
                tvBonusRemark.setText(getString(R.string.minus, order.getBonus()));
                tvConsume.setTextColor(ContextCompat.getColor(this, R.color.colorDark6));
                tvActualDebit.setTextColor(ContextCompat.getColor(this, R.color.colorFullRed));
            }
            tvConsume.setText(order.getConsume());
            tvPrepay.setText(order.getPrepay());
            tvOdd.setText(order.getOdd());
            tvActualDebit.setText(getString(R.string.minus, order.getActualDebit()));
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
