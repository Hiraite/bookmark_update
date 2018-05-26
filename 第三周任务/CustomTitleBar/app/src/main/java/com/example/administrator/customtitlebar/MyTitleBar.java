package com.example.administrator.customtitlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTitleBar extends LinearLayout{

    private Button backButton, editButton;
    private Button cancelButton, selectAllButton, cancelSelectButton;
    private TextView titleTextView;
    private View view;
    private final static int DEFAULT_LEFT_BACKGROUND = 0;
    private final static int DEFAULT_RIGHT_BACKGROUND = 0;
    private final static float DEFAULT_TITLE_SIZE = 25;
    private final static int DEFAULT_TITLE_COLOR = Color.BLACK;
    private OnLeftAndRightClickListener listener;//监听点击事件

    public MyTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context, attrs);
        setStatusBar(context);
        setButtonListener();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if(null != attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTitleBar);
            int leftBackground = typedArray.getResourceId(R.styleable.MyTitleBar_backBackground, DEFAULT_LEFT_BACKGROUND);
            int rightBackground = typedArray.getResourceId(R.styleable.MyTitleBar_editBackground, DEFAULT_RIGHT_BACKGROUND);
            String titleText = typedArray.getString(R.styleable.MyTitleBar_titleText);
            float titleSize = typedArray.getDimension(R.styleable.MyTitleBar_titleTextSize, DEFAULT_TITLE_SIZE);
            int titleColor = typedArray.getColor(R.styleable.MyTitleBar_titleTextColor, DEFAULT_TITLE_COLOR);
            int cancelBackground = typedArray.getResourceId(R.styleable.MyTitleBar_cancelBackground, DEFAULT_LEFT_BACKGROUND);
            int selectAllBackground = typedArray.getResourceId(R.styleable.MyTitleBar_selectAllBackground, DEFAULT_RIGHT_BACKGROUND);
            int cancelSelectBackground = typedArray.getResourceId(R.styleable.MyTitleBar_cancelSelectBackground, DEFAULT_RIGHT_BACKGROUND);
            typedArray.recycle();

            backButton.setBackgroundResource(leftBackground);
            if(leftBackground != DEFAULT_LEFT_BACKGROUND) backButton.setText(null);
            editButton.setBackgroundResource(rightBackground);
            if(rightBackground != DEFAULT_RIGHT_BACKGROUND) editButton.setText(null);
            cancelButton.setBackgroundResource(cancelBackground);
            if(cancelBackground != DEFAULT_LEFT_BACKGROUND) cancelButton.setText(null);
            selectAllButton.setBackgroundResource(selectAllBackground);
            if(selectAllBackground != DEFAULT_RIGHT_BACKGROUND) selectAllButton.setText(null);
            cancelSelectButton.setBackgroundResource(cancelSelectBackground);
            if(cancelSelectBackground != DEFAULT_RIGHT_BACKGROUND) cancelSelectButton.setText(null);
            titleTextView.setText(titleText);
            titleTextView.setTextColor(titleColor);
            titleTextView.setTextSize(titleSize);
        }
    }

    private void initView(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        backButton = view.findViewById(R.id.backButton);
        editButton = view.findViewById(R.id.editButton);
        titleTextView = view.findViewById(R.id.titleText);
        cancelButton = view.findViewById(R.id.cancelButton);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        cancelSelectButton = view.findViewById(R.id.cancelSelectButton);
    }

    private void setStatusBar(Context context){
        //判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void setButtonListener(){
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBackButtonClick();//点击回调
                }
            }
        });
        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                selectAllButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                if (listener != null) {
                    listener.onEditButtonClick();
                }
            }
        });
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllButton.setVisibility(View.GONE);
                cancelSelectButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                if(listener != null){
                    listener.onCancelButtonClick();
                }
            }
        });
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllButton.setVisibility(View.GONE);
                cancelSelectButton.setVisibility(View.VISIBLE);
                if(listener != null){
                    listener.onSelectAllButtonClick();
                }
            }
        });
        cancelSelectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSelectButton.setVisibility(View.GONE);
                selectAllButton.setVisibility(View.VISIBLE);
                if(listener != null){
                    listener.onCancelSelectButtonClick();
                }
            }
        });
    }

    //按钮点击接口
    public interface OnLeftAndRightClickListener {
        void onBackButtonClick();

        void onEditButtonClick();

        void onCancelButtonClick();

        void onSelectAllButtonClick();

        void onCancelSelectButtonClick();
    }

    //设置监听器
    public void setOnLeftAndRightClickListener(OnLeftAndRightClickListener listener) {
        this.listener = listener;
    }

    public void setTitleText(String text){
        if(titleTextView != null){
            titleTextView.setText(text);
        }
    }

}
