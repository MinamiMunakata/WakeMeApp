package com.minami_m.project.android.wakemeapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import static android.graphics.Typeface.createFromAsset;

public class CustomBasicTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String BASIC = "fonts/Brandon_reg.otf";

    public CustomBasicTextView(Context context) {
        super(context);
        setFont();
    }

    public CustomBasicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomBasicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    private void setFont() {
        Log.i("1234567890", "setFont: " + getContext().getAssets());
        Typeface font = createFromAsset(getContext().getAssets(), BASIC);
        setTypeface(font, Typeface.NORMAL);
    }
}
