package com.xiaolian.amigo.ui.lostandfound;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.tmp.activity.lostandfound.MyPublishActivity;
import com.xiaolian.amigo.tmp.activity.lostandfound.PublishFoundActivity;
import com.xiaolian.amigo.tmp.activity.lostandfound.PublishLostActivity;
import com.xiaolian.amigo.tmp.common.config.SpaceItemDecoration;
import com.xiaolian.amigo.tmp.common.util.ScreenUtils;
import com.xiaolian.amigo.tmp.component.dialog.SearchDialog;
import com.xiaolian.amigo.ui.lostandfound.adapter.LostAndFoundAdaptor;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundPresenter;
import com.xiaolian.amigo.ui.lostandfound.intf.ILostAndFoundView;
import com.xiaolian.amigo.util.Constant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 失物招领
 * <p>
 * Created by caidong on 2017/9/13.
 */
public class LostAndFoundActivity extends LostAndFoundBaseListActivity implements ILostAndFoundView {
    // 失物招领列表
    List<LostAndFoundAdaptor.LostAndFoundWapper> lostAndFounds = new ArrayList<>();

    LostAndFoundAdaptor adaptor;

    @Inject
    ILostAndFoundPresenter<ILostAndFoundView> presenter;
    /**
     * 发布招领
     */
    @BindView(R.id.tv_publish_found)
    TextView tv_publish_found;

    /**
     * 打开发布招领页面
     */
    @OnClick(R.id.tv_publish_found)
    void gotoPublishFound() {
        startActivity(new Intent(this, PublishFoundActivity.class));
    }

    /**
     * 发布失物
     */
    @BindView(R.id.tv_publish_lost)
    TextView tv_publish_lost;

    /**
     * 打开发布招领页面
     */
    @OnClick(R.id.tv_publish_lost)
    void gotoPublishLost() {
        startActivity(new Intent(this, PublishLostActivity.class));
    }
    /**
     * 我的发布
     */
    @BindView(R.id.tv_my_publish)
    TextView tv_my_publish;
    /**
     * 打开发布招领页面
     */
    @OnClick(R.id.tv_my_publish)
    void gotoMyPublish() {
        startActivity(new Intent(this, MyPublishActivity.class));
    }

    @Override
    protected void initData() {
        presenter.queryLostAndFoundList(page, null, null, Constant.PAGE_SIZE, 1);
    }

    @Override
    protected void initPresenter() {
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        presenter.onAttach(LostAndFoundActivity.this);
    }

    protected RecyclerView.Adapter getAdaptor() {
        adaptor = new LostAndFoundAdaptor(this, R.layout.item_lost_and_found, lostAndFounds);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dpToPxInt(this, 10)));
        return adaptor;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_lost_and_found;
    }

    // 点击搜索
    @OnClick(R.id.tv_search)
    void search() {
        SearchDialog dialog = new SearchDialog(this);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void addMoreLost(List<LostAndFoundAdaptor.LostAndFoundWapper> lost) {
        this.lostAndFounds.addAll(lost);
        adaptor.notifyDataSetChanged();
    }

    @Override
    public void addMoreFound(List<LostAndFoundAdaptor.LostAndFoundWapper> found) {
        this.lostAndFounds.addAll(found);
        adaptor.notifyDataSetChanged();
    }
}
