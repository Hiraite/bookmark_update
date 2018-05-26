package com.example.administrator.surfaceviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;


public class FrameAnimationView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Timer mTimer;
    private int[] mBitmapResourceIds;
    private int mCurrentIndex;
    private int mDuration = 30;

    private boolean mIsDestroy;
    private static final String TAG = "FrameAnimationView";

    public FrameAnimationView(Context context) {
        this(context, null);
    }

    public FrameAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mIsDestroy && mBitmapResourceIds != null) {
            mIsDestroy = false;
            this.startAnimation();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsDestroy = true;
    }


    private void draw() {
        if (mBitmapResourceIds == null || mBitmapResourceIds.length == 0) {
            Log.e(TAG, "bitmapResourceIds NULL");
            return;
        }
        mCanvas = mSurfaceHolder.lockCanvas();

        drawCanvas();

        if (mCanvas != null) {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    //todo onmessure 图片自适应大小
    private void drawCanvas() {
        if (mCanvas != null && mSurfaceHolder != null) {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mBitmap = BitmapFactory.decodeResource(getResources(), mBitmapResourceIds[mCurrentIndex]);
            mCanvas.drawBitmap(mBitmap,
                    (getWidth() - mBitmap.getWidth()) / 2 > 0 ? (getWidth() - mBitmap.getWidth()) / 2 : 0,      //todo 获取left top
                    (getHeight() - mBitmap.getHeight()) / 2 > 0 ? (getHeight() - mBitmap.getHeight()) / 2 : 0,
                    null);
            if (mCurrentIndex == mBitmapResourceIds.length - 1) {
                mCurrentIndex = 0;
            } else {
                mCurrentIndex++;
            }
        }
    }

    public void startAnimation() {
        if (!mIsDestroy) {
            mCurrentIndex = 0;
            stop();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    draw();
                }
            }, 0, mDuration);
        } else {
            Log.e(TAG, "surfaceHolder is destroyed");
        }
    }

    public void setBitmapResourceIds(int[] bitmapResourceIds) {
        this.mBitmapResourceIds = bitmapResourceIds;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
