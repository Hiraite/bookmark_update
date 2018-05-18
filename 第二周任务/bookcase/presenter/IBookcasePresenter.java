package com.eebbk.onlineexercise.bookcase.presenter;

import android.content.Context;

import com.eebbk.onlineexercise.siftview.SiftingViewPopupWindow;

/**
 * Created by Administrator on 2018/5/18.
 */
public interface IBookcasePresenter {
    void requestBookInfor(Context context, SiftingViewPopupWindow mSiftingView);

    void getExamData(Context context);
}
