package com.jiayuan.shuibiao.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.util.ScreenUtil;

public class ShowPhotoView extends LinearLayout {

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private ImageView imageView;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    private Photo photo;

    public ShowPhotoView(Context context) {
        this(context,null);
    }

    public ShowPhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.show_photo_view, this);
        imageView = findViewById(R.id.imageView);

        FrameLayout.LayoutParams  hint_page_params =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ScreenUtil.dip2px(context,150));
        hint_page_params.setMargins(0, 0, 0, ScreenUtil.dip2px(context, 10));//设置边距
        setLayoutParams(hint_page_params);
    }

    public ShowPhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




}
