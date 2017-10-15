package com.xiaolian.amigo.ui.main.adaptor;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.base.aspect.SingleClick;
import com.xiaolian.amigo.ui.widget.DotFlashView;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * <p>
 * Created by zcd on 10/11/17.
 */

public class HomeNormalDelegate implements ItemViewDelegate<HomeAdaptor.ItemWrapper> {
    private static final String TAG = HomeNormalDelegate.class.getSimpleName();
    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_home;
    }

    @Override
    public boolean isForViewType(HomeAdaptor.ItemWrapper item, int position) {
        return item.getType() == 1;
    }

    @Override
    public void convert(ViewHolder holder, HomeAdaptor.ItemWrapper itemWrapper, int position) {
        if (itemWrapper.isActive()) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
        ((RelativeLayout)holder.getView(R.id.rl_item)).setBackgroundResource(itemWrapper.getRes());
        holder.setText(R.id.tv_device_title, itemWrapper.getDeviceName());
        holder.setText(R.id.tv_desc, itemWrapper.getDesc());
        holder.setTextColor(R.id.tv_desc, Color.parseColor(itemWrapper.getDescColor()));
        if (itemWrapper.getPrepaySize() != 0) {
            holder.getView(R.id.tv_prepay).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_prepay, "(有" + itemWrapper.getPrepaySize() + "笔未找零金额)");
        } else {
            holder.getView(R.id.tv_prepay).setVisibility(View.GONE);
        }
        if (itemWrapper.isUsing()) {
            ((DotFlashView)holder.getView(R.id.dfv_dot)).setVisibility(View.VISIBLE);
            ((DotFlashView)holder.getView(R.id.dfv_dot)).startAnimation();
        } else {
            ((DotFlashView)holder.getView(R.id.dfv_dot)).setVisibility(View.GONE);
            ((DotFlashView)holder.getView(R.id.dfv_dot)).endAnimation();
        }
    }
}
