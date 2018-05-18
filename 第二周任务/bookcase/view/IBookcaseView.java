package com.eebbk.onlineexercise.bookcase.view;

import com.eebbk.onlineexercise.pojo.ExamBookPojo;

import java.util.List;

public interface IBookcaseView {
    void resetBookGridview(String gradeStr, String subjectStr, String mPublisherStr);

    void getExamDataSuccess(List<ExamBookPojo> mBookList);

    void getExamDataFail();
}
