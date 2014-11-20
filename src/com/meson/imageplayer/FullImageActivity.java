/**
 * @Package com.meson.imageplayer
 * @Description
 *
 * Copyright (c) Inspur Group Co., Ltd. Unpublished
 *
 * Inspur Group Co., Ltd.
 * Proprietary & Confidential
 *
 * This source code and the algorithms implemented therein constitute
 * confidential information and may comprise trade secrets of Inspur
 * or its associates, and any use thereof is subject to the terms and
 * conditions of the Non-Disclosure Agreement pursuant to which this
 * source code was originally received.
 */
package com.meson.imageplayer;

import android.app.Activity;

import android.content.ContentResolver;
import android.content.Intent;

import android.database.Cursor;

import android.graphics.Path;
import android.graphics.PixelFormat;

import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import android.provider.MediaStore;

import android.util.Log;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.View.OnClickListener;

import android.view.WindowManager;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;


/**
 * @ClassName FullImageActivity
 * @Description TODO
 * @Date 2014年5月19日
 * @Email
 * @Author
 * @Version V1.0
 */
public class FullImageActivity extends Activity implements ImagePlayer.ImagePlayerListener,
    View.OnClickListener, View.OnFocusChangeListener {
    public static final int DISPLAY_MENU_TIME = 5000;
    private static final int DISPLAY_PIC_MEAN = 6000;
    private static final boolean DEBUG = false;
    public static final String ACTION_REVIEW = "com.android.camera.action.REVIEW";
    public static final String KEY_GET_CONTENT = "get-content";
    public static final String KEY_GET_ALBUM = "get-album";
    public static final String KEY_TYPE_BITS = "type-bits";
    public static final String KEY_MEDIA_TYPES = "mediaTypes";
    public static final String KEY_DISMISS_KEYGUARD = "dismiss-keyguard";
    private static final String TAG = "FullImageActivity";
    private static final int DISMISS_PROGRESSBAR = 0;
    private static final int DISPLAY_SHOW = 1;
    private static final int DISMISS_MENU = 2;
    private boolean mPlayPicture;
    private RelativeLayout menu = null;
    private ImagePlayer mImageplayer;
    private SurfaceView mSurfaceView;
    private Animation outAnimation = null;
    private Animation leftInAnimation = null;
    private Animation menuInAnimation = null;
    private ProgressBar mLoadingProgress;
    private String mCurPicPath;
    private int mSlideIndex;
    private HandlerThread mShowHandlerThread;
    private Handler mShowHandler;
    private ImageButton mPlayBtn;
    private ImageButton mRotateL;
    private ImageButton mRotateR;
    private int mDegress;
    private ArrayList<Uri> mImageList = new ArrayList<Uri>();
    private LinearLayout mPlayLay;
    private LinearLayout mRotateLLay;
    private LinearLayout mRotateRlay;
    private Handler mUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case DISMISS_PROGRESSBAR:

                    if (mLoadingProgress != null) {
                        mLoadingProgress.setVisibility(View.GONE);
                    }

                    break;

                case DISPLAY_SHOW:

                    if (mLoadingProgress != null) {
                        mLoadingProgress.setVisibility(View.VISIBLE);
                    }

                    if (DEBUG) {
                        Log.d(TAG,
                            "handle Message mImageList size:" +
                            mImageList.size() + " mSlideIndex:" + mSlideIndex);
                    }

                    if ((mImageList != null) && (mImageList.size() > 0)) {
                        mCurPicPath = getPathByUri(mImageList.get(mSlideIndex));
                        mShowHandler.post(startPlayerRunnable);
                    }

                    break;

                case DISMISS_MENU:
                    displayMenu(false);

                    break;
                }
            }
        };

    private Runnable startPlayerRunnable = new Runnable() {
            public void run() {
                if (mCurPicPath != null) {
                    //mImageplayer.setDataSource(mCurPicPath);
                    //mImageplayer.setSampleSurfaceSize(1, 1280, 720);
                    //mImageplayer.start();
                    mImageplayer.prepareBuf(mCurPicPath);
                    mImageplayer.showBuf();
                }
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayPicture = false;

        if (getIntent().getBooleanExtra(KEY_DISMISS_KEYGUARD, false)) {
            getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        Intent intent = getIntent();
        String action = intent.getAction();

        if (!Intent.ACTION_VIEW.equalsIgnoreCase(action)) {
            System.exit(0);

            return;
        }

        Uri uri = intent.getData();
        String contentType = getContentType(intent);

        if (contentType == null) {
            Toast.makeText(this, R.string.no_such_item, Toast.LENGTH_LONG).show();

            return;
        }

        if (DEBUG) {
            Log.d(TAG, "uri:" + uri + " contentType:" + contentType);
        }

        if (uri == null) {
            findPicList();
        } else {
            mCurPicPath = getPathByUri(uri);

            if (DEBUG) {
                Log.d(TAG, "contentType:" + contentType);
            }
        }
    }

    public String getPathByUri(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, proj, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } else {
            String filePath = uri.getPath();
            filePath = filterPath(filePath);

            return filePath;
        }
    }

    private String filterPath(String filePath) {
        if (filePath.startsWith("/storage/emulated/0")) {
            return Environment.getLegacyExternalStorageDirectory()
                              .getAbsolutePath() +
            filePath.substring(filePath.lastIndexOf('/'), filePath.length());
        }

        return filePath;
    }

    private void findPathByUri(Uri uri, String[] type) {
        if (DEBUG) {
            Log.d(TAG, "findPathByUri uri is " + uri + " type is " + type);
        }

        if (uri == null) {
            return;
        }

        mSlideIndex = 0;

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        Cursor mCursor = this.managedQuery(uri, projection, selection, type,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        mImageList.clear();

        if (mCursor != null) {
            mCursor.moveToFirst();

            while (mCursor.getPosition() != mCursor.getCount()) {
                String data = mCursor.getString(mCursor.getColumnIndex(
                            MediaStore.Images.Media.DATA));

                if (DEBUG) {
                    Log.d(TAG, "data:" + data);
                }

                mImageList.add(Uri.parse(data));
                mCursor.moveToNext();
            }
        }
    }

    private void findPicList() {
        //Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] selectionArg = { "image/jpeg" };

        //if(mCurPicPath!=null ){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        findPathByUri(uri, selectionArg);
        //}
        mUIHandler.sendEmptyMessage(DISPLAY_SHOW);
    }

    private String getContentType(Intent intent) throws RuntimeException {
        String type = intent.getType();

        if (type != null) {
            return MediaUtil.MIME_TYPE_PANORAMA360.equals(type)
            ? MediaUtil.MIME_TYPE_JPEG : type;
        }

        Uri uri = intent.getData();

        if (uri == null) {
            return null;
        }

        try {
            return getContentResolver().getType(uri);
        } catch (Throwable t) {
            if (DEBUG) {
                Log.w(TAG, "get type fail", t);
            }

            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (DEBUG) {
            Log.d(TAG, "onKeyDown:" + keyCode);
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (menu.getVisibility() == View.VISIBLE) {
                mUIHandler.removeMessages(DISMISS_MENU);
                mUIHandler.sendEmptyMessage(DISMISS_MENU);
            } else {
                displayMenu(true);
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (menu.getVisibility() == View.VISIBLE) {
                mUIHandler.removeMessages(DISMISS_MENU);
                mUIHandler.sendEmptyMessage(DISMISS_MENU);
            } else {
                FullImageActivity.this.finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void displayMenu(boolean show) {
        if (!show) {
            menu.startAnimation(leftInAnimation);
            menu.setVisibility(View.GONE);
        } else {
            if (DEBUG) {
                Log.d(TAG, "displayMenu set menu visible");
            }

            menu.startAnimation(outAnimation);
            menu.setVisibility(View.VISIBLE);
            mPlayLay.requestFocus();
            mUIHandler.sendEmptyMessageDelayed(DISMISS_MENU, DISPLAY_MENU_TIME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.activity_main);
        menu = (RelativeLayout) findViewById(R.id.menu_layout);
        mLoadingProgress = (ProgressBar) findViewById(R.id.loading_image);
        outAnimation = AnimationUtils.loadAnimation(this,
                R.anim.menu_and_left_out);
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        menuInAnimation = AnimationUtils.loadAnimation(this, R.anim.menu_in);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview_show_picture);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        mSurfaceView.getHolder().setKeepScreenOn(true);
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().setFormat(257);
        mSurfaceView.getHolder().setFormat(258);
        mShowHandlerThread = new HandlerThread("AmlogicPlayer");
        mShowHandlerThread.start();

        mShowHandler = new Handler(mShowHandlerThread.getLooper()) {
                };
        mImageplayer = new ImagePlayer(this.getApplicationContext(), this);

        if (DEBUG) {
            Log.d(TAG,
                "mShowHandler==null?" + (mShowHandler == null) +
                " startPlayerRunnable==null?" + (startPlayerRunnable == null));
        }

        if (mCurPicPath != null) {
            mImageplayer.setDataSource(mCurPicPath);
            mImageplayer.prepare();
            mImageplayer.show();
        }

        //mShowHandler.post(startPlayerRunnable);
        mPlayLay = (LinearLayout) findViewById(R.id.lay_1);
        mRotateLLay = (LinearLayout) findViewById(R.id.lay_3);
        mRotateRlay = (LinearLayout) findViewById(R.id.lay_2);
        mPlayBtn = (ImageButton) findViewById(R.id.menu_picplay);
        mRotateL = (ImageButton) findViewById(R.id.menu_left_rotate);
        mRotateR = (ImageButton) findViewById(R.id.menu_right_rotate);
        mRotateRlay.setOnClickListener(this);
        mRotateLLay.setOnClickListener(this);
        mPlayLay.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mRotateL.setOnClickListener(this);
        mRotateR.setOnClickListener(this);
        mRotateRlay.setOnFocusChangeListener(this);
        mRotateLLay.setOnFocusChangeListener(this);
        mPlayLay.setOnFocusChangeListener(this);
        mUIHandler.sendEmptyMessageDelayed(DISMISS_MENU, DISPLAY_MENU_TIME);
        mUIHandler.sendEmptyMessageDelayed(DISMISS_PROGRESSBAR, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DEBUG) {
            Log.d(TAG, "onPause");
        }

        if (null != mImageplayer) {
            if (DEBUG) {
                Log.d(TAG, "onPause release");
            }

            mImageplayer.release();
            mImageplayer = null;
        }
    }

    /* (non-Javadoc)
     * @see com.meson.imageplayer.ImagePlayer.ImagePlayerListener#onPrepared()
     */
    @Override
    public void onPrepared() {
    }

    /* (non-Javadoc)
     * @see com.meson.imageplayer.ImagePlayer.ImagePlayerListener#onPlaying()
     */
    @Override
    public void onPlaying() {
    }

    /* (non-Javadoc)
     * @see com.meson.imageplayer.ImagePlayer.ImagePlayerListener#onStoped()
     */
    @Override
    public void onStoped() {
    }

    /* (non-Javadoc)
     * @see com.meson.imageplayer.ImagePlayer.ImagePlayerListener#onShow()
     */
    @Override
    public void onShow() {
        if (DEBUG) {
            Log.d(TAG, "onShow");
        }

        mDegress = 0;
        mUIHandler.sendEmptyMessageDelayed(DISMISS_PROGRESSBAR, 1000);

        if (mPlayPicture && (mImageList.size() > 1)) {
            if (DEBUG) {
                Log.d(TAG,
                    "onShow:" + mSlideIndex + "mImageList.size():" +
                    mImageList.size());
            }

            mSlideIndex = (++mSlideIndex) % (mImageList.size());

            if (DEBUG) {
                Log.d(TAG,
                    "onShow:" + mSlideIndex + "mImageList.size():" +
                    mImageList.size());
            }

            mUIHandler.sendEmptyMessageDelayed(DISPLAY_SHOW, DISPLAY_PIC_MEAN);
        }
    }

    /* (non-Javadoc)
     * @see com.meson.imageplayer.ImagePlayer.ImagePlayerListener#relseased()
     */
    @Override
    public void relseased() {
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.lay_1:
        case R.id.menu_picplay:

            if (!mPlayPicture) {
                mPlayLay.setBackgroundResource(R.drawable.highlight);
                mPlayPicture = true;
                findPicList();
            } else {
                mPlayLay.setBackgroundResource(R.drawable.menu_nofocus);
                mPlayPicture = false;
                mUIHandler.removeMessages(DISPLAY_SHOW);
            }

            break;

        case R.id.lay_3:
        case R.id.menu_left_rotate:

            if ((mImageplayer != null) &&
                    (mImageplayer.getRunningStatus() == ImagePlayer.STATUS_PLAYING)) {
                mDegress -= 90;
                mImageplayer.setRotate(mDegress % 360, 1);
            }

            break;

        case R.id.lay_2:
        case R.id.menu_right_rotate:

            if (mImageplayer != null) {
                mDegress += 90;
                mImageplayer.setRotate(mDegress % 360, 1);
            }

            break;
        }

        mUIHandler.removeMessages(DISMISS_MENU);
        mUIHandler.sendEmptyMessageDelayed(DISMISS_MENU, DISPLAY_MENU_TIME);
    }

    /* (non-Javadoc)
     * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mUIHandler.removeMessages(DISMISS_MENU);
            mUIHandler.sendEmptyMessageDelayed(DISMISS_MENU, DISPLAY_MENU_TIME);
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
            // TODO Auto-generated method stub
            if (DEBUG) {
                Log.v(TAG, "surfaceChanged");
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (DEBUG) {
                Log.v(TAG, "surfaceCreated");
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (DEBUG) {
                Log.v(TAG, "surfaceDestroyed");
            }
        }
    }
}