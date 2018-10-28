package com.minami_m.project.android.wakemeapp;

import android.widget.ProgressBar;

public interface ProgressbarListener {
    void showProgress(ProgressBar progressBar);
    void dismissProgress(ProgressBar progressBar);
}
