package com.scut.veriface.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by IACJ on 2017/12/22.
 */

public class MyToast {
    private static Toast toast;
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();

    }
}