package com.xiaolian.amigo.ui.bonus;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.bonus.adaptor.BonusAdaptor;
import com.xiaolian.amigo.ui.bonus.intf.IBonusPresenter;
import com.xiaolian.amigo.ui.bonus.intf.IBonusView;
import com.xiaolian.amigo.ui.device.heater.HeaterActivity;
import com.xiaolian.amigo.util.Constant;
import com.xiaolian.amigo.util.RxHelper;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * 代金券Activity
 *
 * @author zcd
 * @date 17/9/17
 */
public class BonusActivity extends BonusBaseListActivity implements IBonusView {
    public static final String INTENT_KEY_BONUS_ACTION = "intent_key_bonus_action";
    public static final String INTENT_KEY_BONUS_RESULT = "intent_key_bonus_result";
    public static final String INTENT_KEY_BONUS_DEVICE_TYPE = "intent_key_bonus_device_type";
    public static final int ACTION_NORMAL = -1;
    public static final int ACTION_CHOOSE = 1;
    public static final String INTENT_IS_USE_BONUS = "intent_key_is_use_bonus";
    @Inject
    IBonusPresenter<IBonusView> presenter;

    /**
     * 订单列表
     */
    List<BonusAdaptor.BonusWrapper> bonuses = new ArrayList<>();

    BonusAdaptor adaptor;

    private int titleRes = R.string.my_bonus;
    private int subTitleRes = R.string.exchange_bonus;
    /**
     * 从我的代金券进入为普通模式 从热水澡页面的选择代金券进入为选择模式
     */
    private int action = ACTION_NORMAL;
    private Integer deviceType = null;
    private boolean refreshFlag = false;

    @Override
    protected void onRefresh() {
        page = Constant.PAGE_START_NUM;
        refreshFlag = true;
//        bonuses.clear();
        presenter.requestBonusList(page, deviceType == -1 ? null : deviceType, deviceType != -1);
    }

    @Override
    public void onLoadMore() {
        presenter.requestBonusList(page, deviceType == -1 ? null : deviceType, deviceType != -1);
    }

    @Override
    protected void setRecyclerView(RecyclerView recyclerView) {
        adaptor = new BonusAdaptor(this, R.layout.item_bonus, bonuses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);
    }

    @Override
    protected int setTitle() {
        return titleRes;
    }

    @Override
    protected int setSubTitle() {
        return subTitleRes;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //200毫秒
        RxHelper.delay(200, TimeUnit.MILLISECONDS)
                .subscribe(integer -> onRefresh());
    }

    @Override
    protected void initView() {
        switch (action) {
            case ACTION_CHOOSE:
                getSubTitle().setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
                getSubTitle().setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_IS_USE_BONUS, false);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                });

                //toolbar
                tvTitleThird.setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
                tvTitleThird.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.putExtra(INTENT_IS_USE_BONUS, false);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                });

                adaptor.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                        Intent intent = new Intent(BonusActivity.this, HeaterActivity.class);
                        intent.putExtra(INTENT_KEY_BONUS_RESULT, bonuses.get(position));
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                        return false;
                    }
                });
                break;
            case ACTION_NORMAL:
                getSubTitle().setOnClickListener(v -> startActivity(new Intent(
                        BonusActivity.this, BonusExchangeActivity.class)));

                //toolbar
                tvTitleThird.setOnClickListener(v -> startActivity(new Intent(
                        BonusActivity.this, BonusExchangeActivity.class)));

                getFooter().findViewById(R.id.tv_expired_entry).setOnClickListener(v ->
                        startActivity(new Intent(this, ExpiredBonusActivity.class)));
            default:
                break;
        }
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        presenter.onAttach(BonusActivity.this);
//        presenter.requestBonusList(page);
    }

    protected RecyclerView.Adapter getAdaptor() {
        adaptor = new BonusAdaptor(this, R.layout.item_bonus, bonuses);
        return adaptor;
    }

    @Override
    protected int setFooterLayout() {
        if (action == ACTION_CHOOSE) {
            return 0;
        }
        return R.layout.footer_bonus;
    }

    @Override
    public void addMore(List<BonusAdaptor.BonusWrapper> bonuses) {
        if (refreshFlag) {
            refreshFlag = false;
            this.bonuses.clear();
        }
        this.bonuses.addAll(bonuses);
        adaptor.notifyDataSetChanged();
    }

    @Override
    protected void setUp() {
        super.setUp();
        if (getIntent() != null) {
            action = getIntent().getIntExtra(INTENT_KEY_BONUS_ACTION, ACTION_NORMAL);
            deviceType = getIntent().getIntExtra(INTENT_KEY_BONUS_DEVICE_TYPE, -1);
            switch (action) {
                case ACTION_CHOOSE:
                    titleRes = R.string.choose_bonus;
                    subTitleRes = R.string.not_use_bonus;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
