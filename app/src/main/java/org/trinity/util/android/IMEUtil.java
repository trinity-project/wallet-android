package org.trinity.util.android;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class IMEUtil {
    public static synchronized void hideIME(View trigger) {
        InputMethodManager imm = (InputMethodManager) trigger.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(trigger.getApplicationWindowToken(), 0);
        }
    }
}
