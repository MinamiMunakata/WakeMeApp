package com.minami_m.project.android.wakemeapp.Common.Handler;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.CustomTypefaceSpan;
import com.minami_m.project.android.wakemeapp.R;

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

    public static void setCustomFontOnMenuItems(Menu parentMenu, Context context) {
        MenuItem subMenu = parentMenu.findItem(R.id.custom_menu);
        Menu menu = subMenu.getSubMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            String menuTitle = menuItem.getTitle().toString();
            Typeface typeface = createFromAsset(context.getAssets(), BASIC);
            SpannableString spannableString = new SpannableString(menuTitle);
            // For demonstration purposes only, if you need to support < API 28 just use the CustomTypefaceSpan class only.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                TypefaceSpan typefaceSpan = typeface != null ?
                        new TypefaceSpan(typeface) :
                        new TypefaceSpan("sans-serif");
                spannableString.setSpan(typefaceSpan, 0, menuTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            } else {

                CustomTypefaceSpan customTypefaceSpan = typeface != null ?
                        new CustomTypefaceSpan(typeface) :
                        new CustomTypefaceSpan(Typeface.defaultFromStyle(Typeface.NORMAL));
                spannableString.setSpan(customTypefaceSpan, 0, menuTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            menuItem.setTitle(spannableString);

        }
    }
}
