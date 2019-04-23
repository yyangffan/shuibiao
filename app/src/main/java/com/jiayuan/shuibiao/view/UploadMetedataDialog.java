package com.jiayuan.shuibiao.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UploadMetedataDialog extends Dialog {
    public UploadMetedataDialog(@NonNull Context context) {
        super(context);
    }

    public UploadMetedataDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UploadMetedataDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }
}
