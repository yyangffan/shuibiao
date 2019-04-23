package com.jiayuan.shuibiao.okhttp.builder;


import com.jiayuan.shuibiao.okhttp.OkHttpUtils;
import com.jiayuan.shuibiao.okhttp.request.OtherRequest;
import com.jiayuan.shuibiao.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
