package com.minami_m.project.android.wakemeapp.Common.Listener;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public interface SignInListener {
    void setLoadingImage(RelativeLayout loadingBackground, ImageView loadingImg, Context context);

}
