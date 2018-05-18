package com.eebbk.onlineexercise.bookcase.model;

import com.eebbk.onlineexercise.daoImpl.BookDaoImpl;
import com.eebbk.onlineexercise.pojo.ExamBookPojo;

import java.util.List;

public class BookcaseModelImpl implements IBookcaseModel, Runnable{
    private int sunbjectId;
    private String publisherStr;
    private int gradeId;
    private IBookcaseModelCallback callback;

    public BookcaseModelImpl(IBookcaseModelCallback callback) {
        this.callback = callback;
    }

    @Override
    public void requestExamData(int subjectId, String publisherStr, int gradeId) {
        this.sunbjectId = subjectId;
        this.publisherStr = publisherStr;
        this.gradeId = gradeId;

        Thread mExamThread = new Thread(this);
        mExamThread.start();
    }

    @Override
    public void run() {
        try {
            List<ExamBookPojo> mBookList = new BookDaoImpl().getBookList(sunbjectId, publisherStr, gradeId);
            callback.onRequestExamDataSuccess(mBookList);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onRequestExamDataFail();
        }
    }
}
