package com.eebbk.onlineexercise.bookcase.model;

import com.eebbk.onlineexercise.pojo.ExamBookPojo;

import java.util.List;

public interface IBookcaseModelCallback {
    void onRequestExamDataSuccess(List<ExamBookPojo> mBookList);

    void onRequestExamDataFail();
}
