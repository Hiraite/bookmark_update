package com.eebbk.onlineexercise.bookcase.presenter;

import android.content.Context;
import com.eebbk.onlineexercise.bookcase.model.BookcaseModelImpl;
import com.eebbk.onlineexercise.bookcase.model.IBookcaseModel;
import com.eebbk.onlineexercise.bookcase.model.IBookcaseModelCallback;
import com.eebbk.onlineexercise.bookcase.view.IBookcaseView;
import com.eebbk.onlineexercise.pojo.EditionPojo;
import com.eebbk.onlineexercise.pojo.ExamBookPojo;
import com.eebbk.onlineexercise.pojo.GradePojo;
import com.eebbk.onlineexercise.pojo.SubjectPojo;
import com.eebbk.onlineexercise.siftview.ServerRequest;
import com.eebbk.onlineexercise.siftview.SiftingViewPopupWindow;
import com.eebbk.onlineexercise.util.PreferenceUtil;

import java.util.List;

public class BookcasePresenterImpl implements  IBookcasePresenter, IBookcaseModelCallback {
    private IBookcaseView  mIBookcaseView;
    private IBookcaseModel mIbookcaseModel;

    public BookcasePresenterImpl(IBookcaseView iBookcaseView) {
        this.mIBookcaseView = iBookcaseView;
        initModel();
    }

    private void initModel(){
        mIbookcaseModel = new BookcaseModelImpl(this);
    }

    @Override
    public void requestBookInfor(Context context, SiftingViewPopupWindow mSiftingView) {
        String gradeStr = getGradeStr(PreferenceUtil.getBookParamsbyIntType(context, ServerRequest.GRADE_ID), mSiftingView);
        String subjectStr = getSubjectStr(PreferenceUtil.getBookParamsbyStringType(context, ServerRequest.SUBJECT_NAME), mSiftingView);
        String mPublisherStr = getPressStr(PreferenceUtil.getBookParamsbyStringType(context, ServerRequest.PUBLISHER_NAME), mSiftingView);
        mIBookcaseView.resetBookGridview(gradeStr, subjectStr, mPublisherStr);
    }

    @Override
    public void getExamData(Context context) {
        int subjectId = PreferenceUtil.getBookParamsbyIntType(context, ServerRequest.SUBJECT_ID);
        String publisherStr = PreferenceUtil.getBookParamsbyStringType(context, ServerRequest.PUBLISHER_NAME);
        int gradeId = PreferenceUtil.getBookParamsbyIntType(context, ServerRequest.GRADE_ID);
        mIbookcaseModel.requestExamData(subjectId, publisherStr, gradeId);
    }

    @Override
    public void onRequestExamDataSuccess(List<ExamBookPojo> mBookList) {
        mIBookcaseView.getExamDataSuccess(mBookList);
    }

    @Override
    public void onRequestExamDataFail() {
        mIBookcaseView.getExamDataFail();
    }

    private String getGradeStr(int id, SiftingViewPopupWindow mSiftingView) {
        String str = "";
        List<GradePojo> gradeList = mSiftingView.getGradeList();
        if (null == gradeList) {
            return str;
        }
        try {
            for (int i = 0; i < gradeList.size(); i++) {
                String pressId = gradeList.get(i).getId();
                String idStr = id + "";
                if (pressId.equals(idStr)) {
                    str = gradeList.get(i).getName();
                }
            }
            return str;
        } catch (Exception e) {
            System.out.println("error press id : " + id);
            return str;
        }
    }

    private String getSubjectStr(String subject, SiftingViewPopupWindow mSiftingView) {
        String str = "";
        List<SubjectPojo> subjectList = mSiftingView.getSubjectList();
        if (null == subjectList) {
            return str;
        }
        try {
            for (int i = 0; i < subjectList.size(); i++) {
                String englishName = subjectList.get(i).getSubjectName();
                if (englishName.contains(subject)) {
                    str = subjectList.get(i).getSubjectName();
                }
            }
            return str;
        } catch (Exception e) {
            System.out.println("error subject id : " + subject);
            return str;
        }
    }

    private String getPressStr(String mPublisherId2, SiftingViewPopupWindow mSiftingView) {
        String pressStr = mPublisherId2;
        List<EditionPojo> publisherGridViewList = mSiftingView.getPressList();
        if (null == publisherGridViewList) {
            return pressStr;
        }
        try {
            for (int i = 0; i < publisherGridViewList.size(); i++) {
                String pressId = publisherGridViewList.get(i).getId();
                String idStr = mPublisherId2 + "";
                if (pressId.equals(idStr)) {
                    pressStr = publisherGridViewList.get(i).getName();
                }
            }
            return pressStr;
        } catch (Exception e) {
            System.out.println("error press id : " + mPublisherId2);
            return pressStr;
        }
    }
}
