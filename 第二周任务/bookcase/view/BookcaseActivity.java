package com.eebbk.onlineexercise.bookcase.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eebbk.onlineexercise.R;
import com.eebbk.onlineexercise.activity.BaseFragmentActivity;
import com.eebbk.onlineexercise.activity.SearchMainActivity;
import com.eebbk.onlineexercise.activity.WelcomeViewActivity;
import com.eebbk.onlineexercise.adapter.BookGridAdapter;
import com.eebbk.onlineexercise.bookcase.adapter.MyViewPagerAdapter;
import com.eebbk.onlineexercise.bookcase.presenter.BookcasePresenterImpl;
import com.eebbk.onlineexercise.bookcase.presenter.IBookcasePresenter;
import com.eebbk.onlineexercise.bookcase.util.BookcaseStartUtil;
import com.eebbk.onlineexercise.catalog.view.CatalogActivity;
import com.eebbk.onlineexercise.da.BooListDa;
import com.eebbk.onlineexercise.home.view.HomeActivity;
import com.eebbk.onlineexercise.pojo.ExamBookPojo;
import com.eebbk.onlineexercise.siftview.ServerRequest;
import com.eebbk.onlineexercise.siftview.SiftingViewPopupWindow;
import com.eebbk.onlineexercise.util.CompatibleManager;
import com.eebbk.onlineexercise.util.OnlineUtil;
import com.eebbk.onlineexercise.util.PreferenceUtil;
import com.eebbk.onlineexercise.util.eventcontroller.EventTriggeredController;
import com.eebbk.onlineexercise.view.MainMenuView;
import com.eebbk.onlineexercise.view.OnlineExerciseMainView;

import java.util.ArrayList;
import java.util.List;

/**
 * 书架界面 可以添加课本和试卷
 */

public class BookcaseActivity extends BaseFragmentActivity implements IBookcaseView, View.OnClickListener, OnItemClickListener {

    private static final String TAG = BookcaseActivity.class.getSimpleName();
    private final static int BOOK_GRIDVIEW_ID = 101010;
    private boolean mWifiFlag;
    private int mMenuFlag;
    private int mUpdateFlag;
    private int mUpdateExamFlag;
    private ImageButton mBookIv;
    private ImageButton mExamIv;
    private ImageView mExamSearchIv;
    private SiftingViewPopupWindow mSiftingView;

    private GridView mBookGridview;
    private OnlineExerciseMainView mOnlineExamView;
    private BookGridAdapter mBookAdapter;
    private List<ExamBookPojo> mBookList;
    private MainMenuView mMenu;

    private ViewPager mViewPager;// 页卡内容
    private List<View> pageviews;// Tab页面列表
    private ImageView mBackIv;
    private NetworkConnectChangedReceiver mNetworkChangeReceiver;
    private View myBookView;
    private View mNothingView;

    private IBookcasePresenter mBookcasePresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bookcase;
    }

    @Override
    public void init() {
        mMenuFlag = getIntent().getIntExtra(BookcaseStartUtil.ADD_TYPE, 0);

        mBookIv = (ImageButton) findViewById(R.id.book_iv);
        mExamIv = (ImageButton) findViewById(R.id.exam_iv);
        mExamSearchIv = (ImageView) findViewById(R.id.search_iv);
        mBackIv = (ImageView) findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(this);

        mSiftingView = (SiftingViewPopupWindow) findViewById(R.id.siftview);
        mSiftingView.setmHandler(handler);
        mViewPager = (ViewPager) findViewById(R.id.booklist_viewpager);
        searchIvShouldVisible();
        buttonSetListener();
        InitViewPager();
        mMenu = new MainMenuView(this, mViewPager, this);
        initOritation();
        mWifiFlag = OnlineUtil.hasInternet(this);
        registerReceiver();
        initPresenter();
    }

    private void initPresenter() {
        mBookcasePresenter = new BookcasePresenterImpl(this);
    }

    private void InitViewPager() {
        int width = mViewPager.getWidth();
        int height = mViewPager.getHeight();
        mViewPager.setBackgroundColor(Color.WHITE);

        pageviews = new ArrayList<View>();
        initBookView(width, height);
        initOnLineExamView(width, height);
        pageviews.add(myBookView);
        pageviews.add(mOnlineExamView);

        mViewPager.setAdapter(new MyViewPagerAdapter(pageviews));
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        paperChange(mMenuFlag);
        initTabBg(mMenuFlag);
    }

    private void initOnLineExamView(int width, int height) {
        LinearLayout.LayoutParams params;
        mOnlineExamView = new OnlineExerciseMainView(BookcaseActivity.this, null);
        params = new LinearLayout.LayoutParams(width, height);
        mOnlineExamView.setLayoutParams(params);
        mOnlineExamView.setBackgroundColor(Color.WHITE);
    }

    private void initBookView(int width, int height) {
        myBookView = LayoutInflater.from(this).inflate(R.layout.onlineexercise_syncexam, null);
        mNothingView = myBookView.findViewById(R.id.onlinedata_nothingview);
        mBookGridview = (GridView) myBookView.findViewById(R.id.book_content);
        mBookGridview.setNumColumns(3);
        mBookGridview.setOnItemClickListener(BookcaseActivity.this);
        mBookGridview.setSelector(R.drawable.bookitem_bg_xml);
        mBookGridview.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        myBookView.setLayoutParams(params);
    }

    private void buttonSetListener() {
        mBookIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OnlineUtil.hasInternet(BookcaseActivity.this)) {
                    OnlineUtil.showNoInternetRemindDialog(BookcaseActivity.this);
                    mWifiFlag = false;
                } else {
                    mWifiFlag = true;
                }
                if (mMenuFlag != 0) {
                    mMenuFlag = 0;
                    paperChange(mMenuFlag);
                }
            }
        });
        mExamIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OnlineUtil.hasInternet(BookcaseActivity.this)) {
                    OnlineUtil.showNoInternetRemindDialog(BookcaseActivity.this);
                    mWifiFlag = true;
                } else {
                    mWifiFlag = false;
                }
                if (mMenuFlag != 1) {
                    mMenuFlag = 1;
                    paperChange(mMenuFlag);
                }
            }
        });
        mExamSearchIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BookcaseActivity.this, SearchMainActivity.class);
                intent.putExtra(OnlineUtil.SUBJECT, PreferenceUtil.getExamParamsbyStringType(BookcaseActivity.this, ServerRequest.SUBJECT_NAME));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        OnlineUtil.print("onResume....");
        if (!mWifiFlag) {
            if (OnlineUtil.hasInternet(this)) {
                mWifiFlag = true;
                if (mMenuFlag == 0 && (mBookList == null || mBookList.isEmpty())) {
                    if (null != mSiftingView && 0 == mSiftingView.getInitFlag()) {
                        mSiftingView.setVisibility(View.VISIBLE);
                        mSiftingView.getBaseSiftData();

                    }
                } else if (mMenuFlag == 1 && mOnlineExamView != null) {
                    mOnlineExamView.setOnlineSiftingPopupWindow(View.VISIBLE);
                    if (mOnlineExamView.hasData()) {
                        mOnlineExamView.notifyDataSetChanged();
                    } else {
                        mOnlineExamView.getData();
                    }
                }
            } else {
                mWifiFlag = false;
                mSiftingView.setVisibility(View.GONE);
                mOnlineExamView.setOnlineSiftingPopupWindow(View.GONE);
            }
        } else {
            if (mMenuFlag == 0) {
                mSiftingView.setVisibility(View.VISIBLE);
                mOnlineExamView.setOnlineSiftingPopupWindow(View.GONE);
            } else {
                mSiftingView.setVisibility(View.GONE);
                mOnlineExamView.setOnlineSiftingPopupWindow(View.VISIBLE);
            }
        }
        super.onResume();
    }

    /**
     * 改变搜索按钮的可见性，只有在试卷页面才能进入搜索页面
     */
    private void searchIvShouldVisible() {
        mExamSearchIv.setVisibility(mMenuFlag == 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == SiftingViewPopupWindow.PARAMSCHANGE) {
                // 获取试卷
                mBookList = null;
                mUpdateFlag = 0;
                mUpdateExamFlag = 0;
                if (mSiftingView != null && mMenuFlag == 0) {
                    mSiftingView.setVisibility(View.VISIBLE);
                } else {
                    mSiftingView.setVisibility(View.GONE);
                }
                createContentView();
            } else if (msg.what == 1 || msg.what == 100) {
                // 获取book
                mBookcasePresenter.requestBookInfor(BookcaseActivity.this, mSiftingView);
            } else if (msg.what == SiftingViewPopupWindow.LOADERROR) {
                createContentView();
                if (mSiftingView != null) {
                    mSiftingView.dismissProgressDialog();
                }
            }
        }
    };

    @Override
    public void resetBookGridview(String gradeStr, String subjectStr, String mPublisherStr) {

        if (null == mBookList || mBookList.size() == 0) {
            mBookGridview.setVisibility(View.GONE);
            mNothingView.setVisibility(View.VISIBLE);
            if (!OnlineUtil.hasInternet(this)) {
                mWifiFlag = false;
                mNothingView.findViewById(R.id.noNetWork).setVisibility(View.VISIBLE);
                mNothingView.findViewById(R.id.noData).setVisibility(View.GONE);
            } else {
                mNothingView.findViewById(R.id.noNetWork).setVisibility(View.GONE);
                mNothingView.findViewById(R.id.noData).setVisibility(View.VISIBLE);
            }
            return;
        }

        mBookAdapter = new BookGridAdapter(this, mBookList, gradeStr, subjectStr, mPublisherStr);
        mBookGridview.setAdapter(mBookAdapter);
        mBookGridview.setVisibility(View.VISIBLE);
        mNothingView.setVisibility(View.GONE);
        if (mSiftingView != null) {
            mSiftingView.dismissProgressDialog();
        }
    }

    private void createContentView() {
        if (0 == mUpdateFlag) {
            mBookcasePresenter.getExamData(this);
            mUpdateFlag = 1;
        }
    }

    @Override
    public void getExamDataSuccess(List<ExamBookPojo> mBookList) {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = 1;
        this.mBookList = mBookList;
        msg.setData(data);
        handler.sendMessage(msg);
    }

    @Override
    public void getExamDataFail() {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = 100;
        msg.setData(data);
        handler.sendMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_SEARCH:
                if (mMenuFlag == 1) {
                    Intent intent = new Intent(BookcaseActivity.this, SearchMainActivity.class);
                    intent.putExtra(OnlineUtil.SUBJECT, PreferenceUtil.getExamParamsbyStringType(BookcaseActivity.this, ServerRequest.SUBJECT_NAME));

                    startActivity(intent);
                    ret = true;
                }
                break;

            case KeyEvent.KEYCODE_MENU:
                if (!mMenu.isShowiing()) {
                    mMenu.showMainMenuView();
                } else {
                    mMenu.dismissMainMenuView();
                }
                break;

            case KeyEvent.KEYCODE_BACK: {
                if (0 == mMenuFlag) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    setResult(RESULT_OK, intent);

                    finish();
                } else if (!mOnlineExamView.onKeyDown(keyCode, event)) {

                    Intent intent = new Intent(this, HomeActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
            break;

            default:
                ret = super.onKeyDown(keyCode, event);
                break;
        }

        return ret;
    }

    @Override
    protected void onDestroy() {
        if (mOnlineExamView != null) {
            mOnlineExamView.dismissSiftPopupProgressDialog();
        }
        unRegister();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_item_wallpaper:
                mMenu.dismissMainMenuView();
                Intent intent = new Intent(this, WelcomeViewActivity.class);
                startActivity(intent);
                break;
            case R.id.back_iv:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        if (!mMenu.isShowiing()) {
        } else {
            mMenu.dismissMainMenuView();
        }
        super.onPause();
    }

    // 在应用中使用方法如下：
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initOritation();
    }

    public void initOritation() {
        if (null == mBookGridview) {
            return;
        }

        int original = getResources().getConfiguration().orientation;
        if (original == Configuration.ORIENTATION_PORTRAIT) {
            mBookGridview.setNumColumns(2);
        } else if (original == Configuration.ORIENTATION_LANDSCAPE) {
            mBookGridview.setNumColumns(3);
        }
    }

    public void initTabBg(int position) {
        mViewPager.setCurrentItem(position);
        mBookIv.setBackgroundResource(position == 0 ? R.drawable.button_mybook_ret : R.drawable.bt_home_mybook);
        mExamIv.setBackgroundResource(position == 0 ? R.drawable.bt_home_myexercise : R.drawable.button_myexercise_ret);
    }

    // 当前选中项
    public void paperChange(int curSel) {
        switch (curSel) {
            case 0:
                mViewPager.setCurrentItem(0);
                mBookIv.setBackgroundResource(R.drawable.button_mybook_ret);
                mExamIv.setBackgroundResource(R.drawable.bt_home_myexercise);
                if (OnlineUtil.hasInternet(this)) {
                    if (mBookList == null || mBookList.isEmpty()) {
                        mSiftingView.getBaseSiftData();
                    }
                    mSiftingView.setVisibility(View.VISIBLE);
                    mWifiFlag = true;
                } else {
                    Message msg = new Message();
                    msg.what = SiftingViewPopupWindow.LOADERROR;
                    handler.sendMessage(msg);
                    mSiftingView.setVisibility(View.GONE);
                    mWifiFlag = false;
                }
                searchIvShouldVisible();
                break;

            case 1:
                mSiftingView.setVisibility(View.GONE);
                mViewPager.setCurrentItem(1);
                mBookIv.setBackgroundResource(R.drawable.bt_home_mybook);
                mExamIv.setBackgroundResource(R.drawable.button_myexercise_ret);
                if (OnlineUtil.hasInternet(this)) {
                    mOnlineExamView.setOnlineSiftingPopupWindow(View.VISIBLE);
                    mWifiFlag = true;
                } else {
                    mOnlineExamView.setOnlineSiftingPopupWindow(View.GONE);
                    mWifiFlag = false;
                }
                mOnlineExamView.getData();
                searchIvShouldVisible();
                break;

            default:
                paperChange(0);
                break;
        }
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {
            mMenuFlag = mViewPager.getCurrentItem();
            switch (mMenuFlag) {
                case 0:
                    paperChange(0);
                    break;
                case 1:
                    paperChange(1);
                    break;
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
            mMenuFlag = mViewPager.getCurrentItem();
            switch (mMenuFlag) {
                case 0:
                    paperChange(0);
                    break;
                case 1:
                    paperChange(1);
                    break;
            }
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        boolean canJump = EventTriggeredController.getInstance().doDelayTimeStrategy
                ("booklist_act_click_item", 500);
        if (!canJump) {
            return;
        }
        if (mBookList == null || mBookList.size() <= 0) {
            Log.e(TAG, "onClickItem: mBookList == null||mBookList.size()<=0");
            return;
        }
        BooListDa.clickItem(this, mBookList.get(index).getBookName());
        Intent intent = new Intent(BookcaseActivity.this, CatalogActivity.class);
        intent.putExtra(OnlineUtil.SUBJECT, PreferenceUtil.getBookParamsbyStringType(BookcaseActivity.this, ServerRequest.SUBJECT_NAME));
        intent.putExtra(OnlineUtil.MACHINEID, CompatibleManager.getBBKSn());
        intent.putExtra("book", mBookList.get(index));

        startActivity(intent);
    }

    private void registerReceiver() {
        mNetworkChangeReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeReceiver, filter);
    }

    private void unRegister() {
        unregisterReceiver(mNetworkChangeReceiver);
    }

    public class NetworkConnectChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "[PZH] isConnected hasInternet = " + OnlineUtil.hasInternet(context) + ",wifiFlag = " + mWifiFlag);
                if (OnlineUtil.hasInternet(context)) {
                    if (!mWifiFlag) {
                        if (mMenuFlag == 0 && (mBookList == null || mBookList.isEmpty())) {
                            Log.e(TAG, "[PZH]isConnected mMenuFlag = " + mMenuFlag);
                            if (null != mSiftingView) {
                                mSiftingView.getBaseSiftData();
                            }
                        } else if (mMenuFlag == 1 && (!mOnlineExamView.hasData())) {
                            Log.e(TAG, "[PZH] isConnected mMenuFlag = " + mMenuFlag);
                            mOnlineExamView.getData();
                        } else if (mMenuFlag == 0) {
                            mSiftingView.setVisibility(View.VISIBLE);
                        } else if (mMenuFlag == 1) {
                            mOnlineExamView.setOnlineSiftingPopupWindow(View.VISIBLE);
                        }
                        mWifiFlag = true;
                    }
                } else {
                    mWifiFlag = false;
                }
                Log.e(TAG, "[PZH] isConnected hasInternet = " + OnlineUtil.hasInternet(context) + ",wifiFlag = " + mWifiFlag);
            }
        }

    }
}
