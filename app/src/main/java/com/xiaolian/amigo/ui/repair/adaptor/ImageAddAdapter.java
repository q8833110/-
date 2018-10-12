package com.xiaolian.amigo.ui.repair.adaptor;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.util.Constant;
import com.xiaolian.amigo.util.GildeUtils;
import com.xiaolian.amigo.util.ScreenUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.Locale;

import lombok.Data;

/**
 * 添加图片
 *
 * @author zcd
 * @date 17/11/15
 */

public class ImageAddAdapter extends CommonAdapter<ImageAddAdapter.ImageItem> {
    private static final int DEFAULT_RES = R.drawable.image_add;
    private Context context;
    private int imageSize;
    private int viewWidth ;

    private boolean isNetImage ;
    public ImageAddAdapter(Context context, int layoutId, List<ImageItem> datas) {
        super(context, layoutId, datas);
        this.context = context;
        this.imageSize = ScreenUtils.dpToPxInt(context, 77);
    }

    public ImageAddAdapter(Context context, int layoutId, List<ImageItem> datas , boolean isNetImage) {
        super(context, layoutId, datas);
        this.context = context;
        this.imageSize = ScreenUtils.dpToPxInt(context, 77);
        this.isNetImage = isNetImage ;
    }


    public void setViewWidth(int viewWidth){
        this.viewWidth  = viewWidth ;
        notifyDataSetChanged();
    }



    @Override
    protected void convert(ViewHolder holder, ImageItem imageItem, int position) {
        if (viewWidth != 0){
            ImageView imageView = holder.getView(R.id.iv_image);
            if (imageView.getWidth() != viewWidth) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = viewWidth;
                params.height = viewWidth;
                imageView.setLayoutParams(params);
            }
        }
        if (TextUtils.isEmpty(imageItem.getImageUrl())) {
            ((ImageView) holder.getView(R.id.iv_image)).setImageDrawable(null);
            ((ImageView) holder.getView(R.id.iv_image)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.setImageResource(R.id.iv_image, DEFAULT_RES);
        } else {
            if (isNetImage){
                GildeUtils.setImage(context ,((ImageView)holder.getView(R.id.iv_image)) ,imageItem.getImageUrl());
                ((ImageView) holder.getView(R.id.iv_image)).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else {
                Glide.with(context).load(imageItem.getImageUrl())
                        .asBitmap()
                        .into((ImageView) holder.getView(R.id.iv_image));
                ((ImageView) holder.getView(R.id.iv_image)).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    @Data
    public static class ImageItem {
        private String imageUrl;

        public ImageItem() {
        }

        public ImageItem(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
