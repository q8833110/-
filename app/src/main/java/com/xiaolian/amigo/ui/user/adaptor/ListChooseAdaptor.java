package com.xiaolian.amigo.ui.user.adaptor;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaolian.amigo.R;
import com.xiaolian.amigo.data.network.model.user.Residence;
import com.xiaolian.amigo.data.network.model.user.School;
import com.xiaolian.amigo.util.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Data;

/**
 * ListChooseAdapter
 *
 * @author zcd
 * @date 17/9/15
 */
public class ListChooseAdaptor extends RecyclerView.Adapter<ListChooseAdaptor.ViewHolder> {

    private static final String TAG = ListChooseAdaptor.class.getSimpleName();
    private List<Item> datas;

    private OnItemClickListener mOnItemClickListener;

    private int lastTickPostion = -1;

    private boolean checkDeviceExist = false;

    public interface OnItemClickListener {
        /**
         * 列表点击事件
         *
         * @param view     view
         * @param position 位置
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ListChooseAdaptor(List<Item> datas) {
        this.datas = datas;
    }

    public ListChooseAdaptor(List<Item> datas, boolean checkDeviceExist) {
        this.datas = datas;
        this.checkDeviceExist = checkDeviceExist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_choose, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvContent.setText(datas.get(holder.getAdapterPosition()).content);
        if (datas.get(holder.getAdapterPosition()).tick) {
            holder.ivTick.setVisibility(View.VISIBLE);
            lastTickPostion = holder.getAdapterPosition();
        } else {
            holder.ivTick.setVisibility(View.GONE);
        }
        if (checkDeviceExist) {
            if (datas.get(holder.getAdapterPosition()).isDeviceExist()) {
                holder.tvDeviceExist.setVisibility(View.GONE);
                onItemClick(holder);
            } else {
                holder.tvDeviceExist.setVisibility(View.VISIBLE);
            }
        }else{
            onItemClick(holder);
        }
    }

    /**
     * 设置click 事件，如果没有设备，不能点击
     * @param holder
     */
    private void onItemClick(ViewHolder holder) {
        holder.itemView.setOnClickListener(v -> {
            try {
                    mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
                if (lastTickPostion != -1) {
                    datas.get(lastTickPostion).tick = false;
                }
                datas.get(holder.getAdapterPosition()).tick = true;
                lastTickPostion = holder.getAdapterPosition();
                notifyDataSetChanged();
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_tick)
        ImageView ivTick;
        @BindView(R.id.tv_device_exist)
        TextView tvDeviceExist;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Data
    public static class Item implements Parcelable {
        String content;
        boolean tick = false;
        Long id;
        String extra;
        int deviceType;
        boolean deviceExist = true;
        String groupId ;
        String mac ;

        public Item(String content, boolean tick, Long id) {
            this(content, tick);
            this.id = id;
        }

        public Item(String content, int deviceType) {
            this(content, false);
            this.deviceType = deviceType;
        }

        public Item(String content, boolean tick) {
            this.content = content;
            this.tick = tick;
        }

        public Item(School school, boolean tick) {
            this.content = school.getSchoolName();
            this.id = school.getId();
            this.tick = tick;
        }

        public Item(Residence residence ) {
            this.content = residence.getName();
            this.extra = residence.getFullName();
            this.id = residence.getId();
            this.tick = false;
            this.deviceExist = !TextUtils.isEmpty(residence.getMacAddress());
            this.groupId = residence.getGroupId();
            this.mac = residence.getMacAddress() ;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.content);
            dest.writeByte(this.tick ? (byte) 1 : (byte) 0);
            dest.writeValue(this.id);
            dest.writeString(this.extra);
            dest.writeByte(this.deviceExist ? (byte) 1 : (byte) 0);
            dest.writeString(this.groupId);
        }

        protected Item(Parcel in) {
            this.content = in.readString();
            this.tick = in.readByte() != 0;
            this.id = (Long) in.readValue(Long.class.getClassLoader());
            this.extra = in.readString();
            this.deviceExist = in.readByte() != 0;
            this.groupId = in.readString();
        }

        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
}
