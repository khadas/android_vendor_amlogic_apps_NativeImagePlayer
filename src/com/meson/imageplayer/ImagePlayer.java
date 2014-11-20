package com.droidlogic.imageplayer;

import android.content.Context;

import android.net.Uri;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import android.util.Log;


public class ImagePlayer {
    private static final String TAG = "ImagePlayer";
    public static final int REMOTE_EXCEPTION = -0xffff;
    public static final int STATUS_UNDEFINE = -1;
    public static final int STATUS_PRAPARE = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_RELEASE = 2;
    int TRANSACTION_INIT = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_SET_DATA_SOURCE = IBinder.FIRST_CALL_TRANSACTION + 1;
    int TRANSACTION_SET_SAMPLE_SURFACE_SIZE = IBinder.FIRST_CALL_TRANSACTION +
        2;
    int TRANSACTION_SET_ROTATE = IBinder.FIRST_CALL_TRANSACTION + 3;
    int TRANSACTION_SET_SCALE = IBinder.FIRST_CALL_TRANSACTION + 4;
    int TRANSACTION_SET_ROTATE_SCALE = IBinder.FIRST_CALL_TRANSACTION + 5;
    int TRANSACTION_SET_CROP_RECT = IBinder.FIRST_CALL_TRANSACTION + 6;
    int TRANSACTION_START = IBinder.FIRST_CALL_TRANSACTION + 7;
    int TRANSACTION_PREPARE = IBinder.FIRST_CALL_TRANSACTION + 8;
    int TRANSACTION_SHOW = IBinder.FIRST_CALL_TRANSACTION + 9;
    int TRANSACTION_RELEASE = IBinder.FIRST_CALL_TRANSACTION + 10;
    int TRANSACTION_PREPARE_BUF = IBinder.FIRST_CALL_TRANSACTION + 11;
    int TRANSACTION_SHOW_BUF = IBinder.FIRST_CALL_TRANSACTION + 12;
    private ImagePlayerListener mListener;
    private int mCurStatus;
    private Context mContext;
    private IBinder mIBinder = null;

    /**
     * Method to create a ImagePlayer,use {@link #init()} function to init image_player_service
     *
     * @param context maybe useful someday
     * @param callback interface
     */
    public ImagePlayer(Context context, ImagePlayerListener listener) {
        mContext = context;
        mListener = listener;
        mCurStatus = STATUS_UNDEFINE;

        try {
            Object object = Class.forName("android.os.ServiceManager")
                                 .getMethod("getService",
                    new Class[] { String.class })
                                 .invoke(null, new Object[] { "image.player" });
            mIBinder = (IBinder) object;
        } catch (Exception ex) {
            Log.e(TAG, "image player init fail:" + ex);
        }

        Log.d(TAG, "onCreate ImagePlayer");

        int ret = init();
        Log.d(TAG, "init() returned " + ret);
    }

    private int init() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_INIT, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "init: ImagePlayerService is dead!:" + ex);
        }

        Log.d(TAG, "init() failsed");

        return REMOTE_EXCEPTION;
    }

    /**
    * Sets the data source the data source as a content Uri.
    *
    * @param uri the Content URI of the data you want to play
    */
    public int setDataSource(Uri uri) {
        String scheme = uri.getScheme();

        if ((scheme == null) || scheme.equals("file")) {
            return _setDataSource("file://" + uri.getPath());
        }

        return REMOTE_EXCEPTION;
    }

    /**
       * Sets the data source (FilePath) to use. It is the caller's responsibility
       * to close the file descriptor. It is safe to do so as soon as this call returns.
       *
       * @param path the File full Path for the file you want to play
       */
    public int setDataSource(String path) {
        return _setDataSource("file://" + path);
    }

    private int _setDataSource(String path) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeString(path);
                mIBinder.transact(TRANSACTION_SET_DATA_SOURCE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "_setDataSource: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Sets the fixed size of the showing picture
     * After setDataSource to ImagePlayerService {@link #setDataSource(String path)},{@link #setDataSource(Uri uri)}
     * setSampleSurfaceSize can be called to set the picture fixed Size.Picture will be fullscreen without this function;
     *
     * @param sampleSize data sampling rate,most common value is 1
     * @param surfaceW fixed widht of picture
     * @param surfaceH fixed height of picture
     */
    public int setSampleSurfaceSize(int sampleSize, int surfaceW, int surfaceH) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeInt(sampleSize);
                data.writeInt(surfaceW);
                data.writeInt(surfaceH);
                mIBinder.transact(TRANSACTION_SET_SAMPLE_SURFACE_SIZE, data,
                    reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setSampleSurfaceSize: ImagePlayerService is dead!:" +
                ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
    * Sets Rotate of showing picture
    * After setDataSource to ImagePlayerService {@link #setDataSource(String path)},{@link #setDataSource(Uri uri)}
    * has been already called and {@link #show()} show picture successfully;Also, call be called after {@link #prepareBuf}{@link #showBuf}
    *
    * @param degrees rotate degress which can be 90/180/270
    * @param autoCrop 1&0 to decide autoCrop picture when rotate
    */
    public int setRotate(float degrees, int autoCrop) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(degrees);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_ROTATE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setRotate: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
    * Sets Scale of showing picture
    * After setDataSource to ImagePlayerService {@link #setDataSource(String path)},{@link #setDataSource(Uri uri)}
    * has been already called and {@link #show()} show picture successfully;Also, call be called after {@link #prepareBuf}{@link #showBuf}
    *
    * @param degrees rotate degress which can be 90/180/270
    * @param sx sacle to sx at width
    * @param sy scale to sy at height
    * @param autoCrop 1&0 to decide autoCrop picture when scale
    */
    public int setScale(float sx, float sy, int autoCrop) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(sx);
                data.writeFloat(sy);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_SCALE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setScale: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
    * Sets Scale and Rotate at the sametime of showing picture
    * After setDataSource to ImagePlayerService {@link #setDataSource(String path)},{@link #setDataSource(Uri uri)}
    * has been already called and {@link #show()} show picture successfully;Also, call be called after {@link #prepareBuf}{@link #showBuf}
    *
    * @param sx sacle to sx at width
    * @param sy scale to sy at height
    * @param autoCrop 1&0 to decide autoCrop picture when scale
    */
    public int setRotateScale(float degrees, float sx, float sy, int autoCrop) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(degrees);
                data.writeFloat(sx);
                data.writeFloat(sy);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_ROTATE_SCALE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setRotateScale: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    public int setCropRect(int cropX, int cropY, int cropWidth, int cropHeight) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeInt(cropX);
                data.writeInt(cropY);
                data.writeInt(cropWidth);
                data.writeInt(cropHeight);
                mIBinder.transact(TRANSACTION_SET_CROP_RECT, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setCropRect: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    public int start() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_START, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                mCurStatus = STATUS_PLAYING;

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "start: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Prepares the ImagePlayer buffer for the picture.
     *
     * After setting the datasource and the display surface, you need to
     * call prepare() to prepare buffer for the show.
     *
     */
    public int prepare() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_PREPARE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                mCurStatus = STATUS_PRAPARE;

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "start: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Show the picture prepared at first buffer.{@link #prepare()}
     * this function is with the use of prepare
     *
     * After setting the datasource and the prepare, you need to either
     * call show() or start() to show the picture
     *
     */
    public int show() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_SHOW, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "start: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Releases resources associated with this ImagePlayer object.
     * It is considered good practice to call this method when you're
     * done using the ImagePlayer. In particular, whenever an Activity
     * of an application is paused (its onPause() method is called),
     * or stopped (its onStop() method is called), this method should be
     * invoked to release the ImagePlayer object, unless the application
     * has a special need to keep the object around. In addition to
     * unnecessary resources (such as memory and instances of codecs)
     * being held, failure to call this method immediately if a
     * ImagePlayer object is no longer needed may also lead to
     * continuous battery consumption for mobile devices, and playback
     * failure for other applications if no multiple instances of the
     * same codec are supported on a device. Even if multiple instances
     * of the same codec are supported, some performance degradation
     * may be expected when unnecessary multiple instances are used
     * at the same time.
     */
    public int release() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_RELEASE, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                mListener.relseased();
                mCurStatus = STATUS_RELEASE;

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "release: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Prepares the second buffer for the second picture.
     *
     * After first display picture ,you can use prepareBuf and showBuf to show second buf picture.
     *
     * @param path The path of picture to prepare
     */
    public int prepareBuf(String path) {
        return _prepareBuf("file://" + path);
    }

    private int _prepareBuf(String path) {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeString(path);
                mIBinder.transact(TRANSACTION_PREPARE_BUF, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "prepareBuf: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
     * Show the second buffer for the second picture.
     *
     * After first display picture , showBuf can been called after prepareBuf to show second buf picture.
     *
     */
    public int showBuf() {
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_SHOW_BUF, data, reply, 0);

                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                mListener.onShow();
                mCurStatus = STATUS_PLAYING;

                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "release: ImagePlayerService is dead!:" + ex);
        }

        return REMOTE_EXCEPTION;
    }

    /**
    * this function is for UI switch
    */
    public int getRunningStatus() {
        return mCurStatus;
    }

    /**
    * Interface definition for a callback to be invoked when the display showing.
    * this function is for apk ui.you can remove it.
    */
    public interface ImagePlayerListener {
        public void onPrepared();

        public void onPlaying();

        public void onStoped();

        public void onShow();

        public void relseased();
    }
}
