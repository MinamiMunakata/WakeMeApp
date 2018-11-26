package com.minami_m.project.android.wakemeapp;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import javax.annotation.Nullable;

import static android.graphics.Typeface.createFromAsset;

public class FontStyleHandler {
    private static final String TITLE = "fonts/Gotham-Bold.ttf";
    private static final String BASIC_BOLD = "fonts/Brandon_bld.otf";
    private static final String BASIC = "fonts/Brandon_reg.otf";

    public static void setFont(Context context, TextView textView, Boolean isTitle, @Nullable Boolean isBold) {
        Typeface font;
        if (isTitle) {
            font = createFromAsset(context.getAssets(), TITLE);
        } else if (isBold) {
            font = createFromAsset(context.getAssets(), BASIC_BOLD);
        } else {
            font = createFromAsset(context.getAssets(), BASIC);
        }
        textView.setTypeface(font);
    }
}
