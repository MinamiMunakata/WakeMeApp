package com.minami_m.project.android.wakemeapp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
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

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
