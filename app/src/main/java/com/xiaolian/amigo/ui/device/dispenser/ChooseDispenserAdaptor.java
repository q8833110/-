package com.xiaolian.amigo.ui.device.dispenser;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.enumeration.Device;
import com.xiaolian.amigo.data.enumeration.DispenserCategory;
import com.xiaolian.amigo.data.enumeration.DispenserWater;
import com.xiaolian.amigo.data.vo.ScanDeviceGroup;

import java.util.List;

import lombok.Data;

/**
 * 饮水机适配器
 *
 * @author zcd
 * @date 17/10/13
 */

public class ChooseDispenserAdaptor extends RecyclerView.Adapter<ChooseDispenserAdaptor.ViewHolder> {
    public interface OnItemClickListener {
        /**
         * 选择饮水机列表点击事件
         *
         * @param deviceNo    设备编号
         * @param supplierId  供应商id
         * @param isFavor     是否已收藏
         * @param residenceId 建筑id
         * @param usefor      水温
         * @param location    设备位置
         * @param price       价格
         */
        void onItemClick(String deviceNo, Long supplierId, Boolean isFavor, Long residenceId, String usefor, String location, Integer price , List<String> preOrderCopy , List<String> afterOrderCopy);
    }

    private OnItemClickListener itemClickListener;
    private int lastExpandPosition = -1;
    private List<ChooseDispenserAdaptor.DispenserWrapper> mData;
    private int layoutId;
    private Context context;
    private boolean expandAble = true;
    private Device deviceType;

    ChooseDispenserAdaptor(Context context, int layoutId,
                           List<DispenserWrapper> datas,
                           Device deviceType) {
        this.mData = datas;
        this.context = context;
        this.layoutId = layoutId;
        this.deviceType = deviceType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChooseDispenserAdaptor.DispenserWrapper dispenserWrapper =
                mData.get(holder.getAdapterPosition());
        expandAble = DispenserCategory.getCategoryByType(dispenserWrapper.getDeviceGroup().getCategory())
                != DispenserCategory.MULTI;
        if (deviceType == Device.DRYER) {
            expandAble = false;
        }
        holder.tvLocation.setText(dispenserWrapper.getLocation());
        if (!expandAble) {
            holder.ivArrow.setVisibility(View.GONE);
        } else {
            holder.ivArrow.setVisibility(View.VISIBLE);
        }
        holder.tvTitle.setText(Device.getDevice(dispenserWrapper.getDeviceGroup().getType()).getDesc());
        holder.tvTitle.setTextColor(ContextCompat.getColor(context,
                Device.getDevice(dispenserWrapper.getDeviceGroup().getType()).getColorRes()));
        holder.rlTop.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != -1 && holder.getAdapterPosition() < mData.size()) {
                expandAble = DispenserCategory.getCategoryByType(mData.get(holder.getAdapterPosition()).getDeviceGroup().getCategory())
                        != DispenserCategory.MULTI;
            }
            if (deviceType == Device.DRYER) {
                expandAble = false;
            }
            if (!expandAble && itemClickListener != null) {
                itemClickListener.onItemClick(dispenserWrapper.getDeviceGroup().getWater().get(0).getMacAddress(),
                        dispenserWrapper.getDeviceGroup().getWater().get(0).getSupplierId(),
                        dispenserWrapper.isFavor(),
                        dispenserWrapper.getResidenceId(),
                        DispenserWater.ALL.getType(),
                        dispenserWrapper.getLocation(),
                        dispenserWrapper.getDeviceGroup().getWater().get(0).getPrice(),
                        dispenserWrapper.getDeviceGroup().getPreOrderCopy() ,
                        dispenserWrapper.getDeviceGroup().getAfterOrderCopy());
                return;
            }
            if (lastExpandPosition != -1 && lastExpandPosition < mData.size()) {
                mData.get(lastExpandPosition).setExpanded(false);
            }
            boolean expand;
            if (lastExpandPosition == holder.getAdapterPosition()) {
                expand = false;
                mData.get(holder.getAdapterPosition()).setExpanded(false);
                lastExpandPosition = -1;
            } else {
                expand = true;
                mData.get(holder.getAdapterPosition()).setExpanded(true);
                lastExpandPosition = holder.getAdapterPosition();
            }
            ObjectAnimator anim =
                    ObjectAnimator.ofFloat(holder.ivArrow,
                            "rotation", expand ? 0f : -180f, expand ? 180f : 0f);
            anim.setDuration(200);
            anim.start();
            notifyDataSetChanged();
        });
        if (dispenserWrapper.isExpanded()) {
            holder.vDivide.setVisibility(View.VISIBLE);
            holder.rlBottom.setVisibility(View.VISIBLE);
            lastExpandPosition = holder.getAdapterPosition();
        } else {
            if (holder.rlBottom.getVisibility() == View.VISIBLE) {
                ObjectAnimator anim =
                        ObjectAnimator.ofFloat(holder.ivArrow,
                                "rotation", -180f, 0f);
                anim.setDuration(200);
                anim.start();
            }
            holder.vDivide.setVisibility(View.GONE);
            holder.rlBottom.setVisibility(View.GONE);
        }
        for (TextView textView : holder.tvWater) {
            textView.setVisibility(View.GONE);
        }
        for (int i = 0; i < dispenserWrapper.getDeviceGroup().getWater().size(); i++) {
            final int waterPosition = i;
            String tempName = dispenserWrapper.getDeviceGroup().getWater().get(i).getName();
            if (TextUtils.isEmpty(tempName)) {
                holder.tvWater[i].setText(
                        DispenserWater.getTemperature(dispenserWrapper.getDeviceGroup().getWater()
                                .get(i).getUsefor()).getDesc());
            } else {
                holder.tvWater[i].setText(tempName);
            }
            holder.tvWater[i].setVisibility(View.VISIBLE);
            holder.tvWater[i].setBackgroundResource(DispenserWater.getTemperature(dispenserWrapper.getDeviceGroup().getWater()
                    .get(i).getUsefor()).getBackgroundDrawable());
            holder.tvWater[i].setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(dispenserWrapper.getDeviceGroup().getWater().get(waterPosition).getMacAddress(),
                            dispenserWrapper.getDeviceGroup().getWater().get(waterPosition).getSupplierId(),
                            dispenserWrapper.isFavor(),
                            dispenserWrapper.getResidenceId(),
                            dispenserWrapper.getDeviceGroup().getWater().get(waterPosition).getUsefor(),
                            dispenserWrapper.getLocation(),
                            dispenserWrapper.getDeviceGroup().getWater().get(waterPosition).getPrice(),
                            dispenserWrapper.getDeviceGroup().getPreOrderCopy() ,
                            dispenserWrapper.getDeviceGroup().getAfterOrderCopy());
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation;
        TextView tvTitle;
        RelativeLayout rlTop;
        View vDivide;
        RelativeLayout rlBottom;
        TextView[] tvWater;
        ImageView ivArrow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvWater = new TextView[]{
                    itemView.findViewById(R.id.tv_cold_water),
                    itemView.findViewById(R.id.tv_ice_water),
                    itemView.findViewById(R.id.tv_hot_water)
            };
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            tvLocation = itemView.findViewById(R.id.tv_location);
            rlTop = itemView.findViewById(R.id.rl_top);
            vDivide = itemView.findViewById(R.id.v_divide);
            rlBottom = itemView.findViewById(R.id.rl_bottom);
        }
    }

    public static class DispenserWrapper {
        /**
         * 设备位置
         **/
        String location;
        ScanDeviceGroup deviceGroup;
        boolean expanded = false;
        boolean favor = false;
        Long residenceId;

        private List<String> preOrderCopy ;

        private List<String> afterOrderCopy ;

        public DispenserWrapper(ScanDeviceGroup device) {
            this.favor = device.getFavor();
            this.location = device.getLocation();
            this.deviceGroup = device;
            this.residenceId = device.getResidenceId();
            this.preOrderCopy =  device.getPreOrderCopy();
            this.afterOrderCopy = device.getPreOrderCopy();
        }


        public List<String> getPreOrderCopy() {
            return preOrderCopy;
        }

        public void setPreOrderCopy(List<String> preOrderCopy) {
            this.preOrderCopy = preOrderCopy;
        }

        public List<String> getAfterOrderCopy() {
            return afterOrderCopy;
        }

        public void setAfterOrderCopy(List<String> afterOrderCopy) {
            this.afterOrderCopy = afterOrderCopy;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public ScanDeviceGroup getDeviceGroup() {
            return deviceGroup;
        }

        public void setDeviceGroup(ScanDeviceGroup deviceGroup) {
            this.deviceGroup = deviceGroup;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public boolean isFavor() {
            return favor;
        }

        public void setFavor(boolean favor) {
            this.favor = favor;
        }

        public Long getResidenceId() {
            return residenceId;
        }

        public void setResidenceId(Long residenceId) {
            this.residenceId = residenceId;
        }
    }

}
