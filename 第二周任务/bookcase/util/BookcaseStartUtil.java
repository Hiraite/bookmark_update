package com.eebbk.onlineexercise.bookcase.util;

import android.app.Activity;
import android.content.Intent;

import com.eebbk.onlineexercise.bookcase.view.BookcaseActivity;

/**
 * Created by Administrator on 2018/1/20.
 */

public class BookcaseStartUtil {
    public static String ADD_TYPE = "add_type";

    public static void startActivityForResult(Activity activity, int code, int type) {
        Intent intent = new Intent();
        intent.setClass(activity, BookcaseActivity.class);
        intent.putExtra(ADD_TYPE, type);
        activity.startActivityForResult(intent, code);
    }
}
