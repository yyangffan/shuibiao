package com.jiayuan.shuibiao.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;
import com.jiayuan.shuibiao.util.ScreenUtil;

public class PhotoView extends LinearLayout {

    private RequestManager mGlide;

    private ImageView imageView;

    private PhotoView photoView;

    public LinearLayout getParentLayout() {
        return parentLayout;
    }

    public void setParentLayout(LinearLayout parentLayout) {
        this.parentLayout = parentLayout;
    }

    private LinearLayout parentLayout;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    private Photo photo;

    private ImageView deleteBtn;

    public PhotoView(Context context) {
        this(context,null);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.photo_view, this);
        imageView = findViewById(R.id.imageView);
        deleteBtn = findViewById(R.id.deleteBtn);
        mGlide = Glide.with(context);
        photoView = this;
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLayout.removeView(photoView);
                if(parentLayout.getChildCount()<=3){
                    parentLayout.getChildAt(
                            parentLayout.getChildCount()-1).setVisibility(VISIBLE);
                }
            }
        });

        FrameLayout.LayoutParams  hint_page_params =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ScreenUtil.dip2px(context,150));
        hint_page_params.setMargins(0, 0, 0, ScreenUtil.dip2px(context, 10));//设置边距
        setLayoutParams(hint_page_params);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageView(Photo photo){
        this.photo = photo;
        mGlide.load(photo.path).into(imageView);
    }

}
