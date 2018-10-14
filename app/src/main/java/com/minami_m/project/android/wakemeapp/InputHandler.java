package com.minami_m.project.android.wakemeapp;

import android.text.TextUtils;
import android.widget.EditText;

public class InputHandler {
    public static boolean isValidFormName(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) return false;
        return true;
    }

    public static boolean isValidFormEmail(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) return false;
        return true;
    }

    public static boolean isValidFormPW(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) return false;
        return true;
    }
}
