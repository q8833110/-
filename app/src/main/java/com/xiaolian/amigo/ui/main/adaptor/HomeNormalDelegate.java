package com.xiaolian.amigo.ui.main.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.widget.DotFlashView;
import com.xiaolian.amigo.util.Constant;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * 主页列表
 *
 * @author zcd
 * @date 17/10/11
 */

public class HomeNormalDelegate implements ItemViewDelegate<HomeAdaptor.ItemWrapper> {
    private static final String TAG = HomeNormalDelegate.class.getSimpleName();
    /**
     * Allows to remember the last item shown on screen
     */
    private int lastPosition = -1;
    private Context context;

    public HomeNormalDelegate(Context context) {
        this.context = context;
    }


    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_home;
    }

    @Override
    public boolean isForViewType(HomeAdaptor.ItemWrapper item, int position) {
        return item.getType() == HomeAdaptor.NORMAL_TYPE;
    }

    @Override
    public void convert(ViewHolder holder, HomeAdaptor.ItemWrapper itemWrapper, int position) {
        if (itemWrapper.isActive()) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
        holder.getView(R.id.rl_item).setBackgroundResource(itemWrapper.getRes());
        holder.setText(R.id.tv_device_title, itemWrapper.getDeviceName());
        holder.setText(R.id.tv_desc, itemWrapper.getDesc());
        if (itemWrapper.isExistOrder()){
            String stauts = "";
            switch (itemWrapper.getStatus()){
                case Constant.WAIT_STATUS:
                    stauts = "正在排队";
                    break;
                case Constant.TIMEOUT_STATUS:
                    stauts = "等待洗浴";
                    break;
                case Constant.USING:
                    stauts = "正在洗浴";
                    break;
            }
            if (!TextUtils.isEmpty(stauts)) {
                holder.setText(R.id.tv_device_title, stauts);
                holder.getView(R.id.tv_prepay).setVisibility(View.GONE);
                if (itemWrapper.isUsing() ) {
                    holder.getView(R.id.dfv_dot).setVisibility(View.VISIBLE);
                    ((DotFlashView) holder.getView(R.id.dfv_dot)).startAnimation();
                } else {
                    holder.getView(R.id.dfv_dot).setVisibility(View.GONE);
                    ((DotFlashView) holder.getView(R.id.dfv_dot)).endAnimation();
                }

            }else{
                holder.setText(R.id.tv_device_title, itemWrapper.getDeviceName());
                holder.getView(R.id.tv_prepay).setVisibility(View.GONE);
                holder.getView(R.id.dfv_dot).setVisibility(View.GONE);
                ((DotFlashView) holder.getView(R.id.dfv_dot)).endAnimation();
            }
        }else {
            if (itemWrapper.getPrepaySize() != 0) {
                holder.getView(R.id.tv_prepay).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_prepay, "(有" + itemWrapper.getPrepaySize() + "笔未找零金额)");
                holder.getView(R.id.tv_desc).setVisibility(View.GONE);

                holder.setText(R.id.tv_device_title, itemWrapper.getDeviceName());
                holder.getView(R.id.dfv_dot).setVisibility(View.GONE);
                ((DotFlashView) holder.getView(R.id.dfv_dot)).endAnimation();

            } else {
                holder.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_prepay).setVisibility(View.GONE);


                if (itemWrapper.isUsing() ) {
                    holder.setText(R.id.tv_device_title, "正在使用" + itemWrapper.getDeviceName());
                    holder.getView(R.id.dfv_dot).setVisibility(View.VISIBLE);
                    ((DotFlashView) holder.getView(R.id.dfv_dot)).startAnimation();
                } else {
                    holder.setText(R.id.tv_device_title, itemWrapper.getDeviceName());
                    holder.getView(R.id.dfv_dot).setVisibility(View.GONE);
                    ((DotFlashView) holder.getView(R.id.dfv_dot)).endAnimation();
                }
            }

        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
