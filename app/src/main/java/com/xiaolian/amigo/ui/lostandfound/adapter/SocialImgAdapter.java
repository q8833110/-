package com.xiaolian.amigo.ui.lostandfound.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaolian.amigo.R;
import com.xiaolian.amigo.ui.widget.photoview.AlbumItemActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SocialImgAdapter extends CommonAdapter<String> {
    private Context context ;
    private List<String> datas ;
    public SocialImgAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
        this.context = context ;
        this.datas = datas ;
    }

    @Override
    protected void convert(ViewHolder holder, String s, int position) {
        if (position != -1 && position < datas.size()){
            String imgUrl = datas.get(position);
            Glide.with(context).load(imgUrl)
                    .asBitmap()
                    .placeholder(R.drawable.ic_picture_error)
                    .error(R.drawable.ic_picture_error)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) holder.getView(R.id.img));
        }

        holder.getView(R.id.img).setOnClickListener(v -> {
            if (datas != null) {
                Intent intent = new Intent(context, AlbumItemActivity.class);
                intent.putExtra(AlbumItemActivity.EXTRA_CURRENT, 0);
                intent.putStringArrayListExtra(AlbumItemActivity.EXTRA_TYPE_LIST, (ArrayList<String>) datas);
                context.startActivity(intent);
            }
        });
    }
}
