package com.meson.imageplayer;

import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class ImagePlayer {   
    private static final String TAG             = "ImagePlayer";
    
    public static final int REMOTE_EXCEPTION    = -0xffff;
    int TRANSACTION_INIT                        = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_SET_DATA_SOURCE             = IBinder.FIRST_CALL_TRANSACTION + 1;
    int TRANSACTION_SET_SAMPLE_SURFACE_SIZE     = IBinder.FIRST_CALL_TRANSACTION + 2;
    int TRANSACTION_SET_ROTATE                  = IBinder.FIRST_CALL_TRANSACTION + 3;
    int TRANSACTION_SET_SCALE                   = IBinder.FIRST_CALL_TRANSACTION + 4;
    int TRANSACTION_SET_ROTATE_SCALE            = IBinder.FIRST_CALL_TRANSACTION + 5;
    int TRANSACTION_SET_CROP_RECT               = IBinder.FIRST_CALL_TRANSACTION + 6;
    int TRANSACTION_START                       = IBinder.FIRST_CALL_TRANSACTION + 7;
    int TRANSACTION_PREPARE                     = IBinder.FIRST_CALL_TRANSACTION + 8;
    int TRANSACTION_SHOW                        = IBinder.FIRST_CALL_TRANSACTION + 9;
    int TRANSACTION_RELEASE                     = IBinder.FIRST_CALL_TRANSACTION + 10;
    public static final int STATUS_UNDEFINE = -1;
    public static final int STATUS_PRAPARE = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_RELEASE = 2;
    private ImagePlayerListener mListener;
    private int mCurStatus;
    private Context mContext;
    private IBinder mIBinder = null;
    public ImagePlayer(Context context,ImagePlayerListener listener){
        mContext = context;
        mListener = listener;
        mCurStatus = STATUS_UNDEFINE;
        try {
            Object object = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", new Class[] { String.class })
                    .invoke(null, new Object[] { "image.player" });
            mIBinder = (IBinder)object;
        }
        catch (Exception ex) {
            Log.e(TAG, "image player init fail:" + ex);
        }
        Log.d(TAG,"onCreate ImagePlayer");
        int ret = init();
        Log.d(TAG,"init() returned "+ret);
    }
    
    private int init(){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_INIT,
                                        data, reply, 0);
                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "init: ImagePlayerService is dead!:" + ex);
        }
        Log.d(TAG,"init() failsed");
        return REMOTE_EXCEPTION;
    }
    
    public int setDataSource(Uri uri){
        String scheme = uri.getScheme();
        if(scheme == null || scheme.equals("file")) {
           return _setDataSource("file://" + uri.getPath());
        }
        
        return REMOTE_EXCEPTION;
    }

    public int setDataSource(String path) {
        return _setDataSource("file://" + path);
    }

    private int _setDataSource(String path){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeString(path);
                mIBinder.transact(TRANSACTION_SET_DATA_SOURCE,
                                        data, reply, 0);
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
    
    public int setSampleSurfaceSize(int sampleSize, int surfaceW, int surfaceH){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeInt(sampleSize);
                data.writeInt(surfaceW);
                data.writeInt(surfaceH);
                mIBinder.transact(TRANSACTION_SET_SAMPLE_SURFACE_SIZE,
                                        data, reply, 0);
                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "setSampleSurfaceSize: ImagePlayerService is dead!:" + ex);
        }
        
        return REMOTE_EXCEPTION;
    }
    
    public int setRotate(float degrees, int autoCrop){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(degrees);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_ROTATE,
                                        data, reply, 0);
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
    
    public int setScale(float sx, float sy, int autoCrop){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(sx);
                data.writeFloat(sy);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_SCALE,
                                        data, reply, 0);
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
    
    public int setRotateScale(float degrees, float sx, float sy, int autoCrop){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeFloat(degrees);
                data.writeFloat(sx);
                data.writeFloat(sy);
                data.writeInt(autoCrop);
                mIBinder.transact(TRANSACTION_SET_ROTATE_SCALE,
                                        data, reply, 0);
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
    
    public int setCropRect(int cropX, int cropY, int cropWidth, int cropHeight){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                data.writeInt(cropX);
                data.writeInt(cropY);
                data.writeInt(cropWidth);
                data.writeInt(cropHeight);
                mIBinder.transact(TRANSACTION_SET_CROP_RECT,
                                        data, reply, 0);
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
    
    public int start(){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_START,
                                        data, reply, 0);
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
    
    public int prepare(){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_PREPARE,
                                        data, reply, 0);
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
    public int getRunningStatus(){
        return mCurStatus;
    }
    public int show(){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_SHOW,
                                        data, reply, 0);
                int result = reply.readInt();
                reply.recycle();
                data.recycle();
                mListener.onShow();
                mCurStatus = STATUS_PLAYING;
                return result;
            }
        } catch (RemoteException ex) {
            Log.e(TAG, "start: ImagePlayerService is dead!:" + ex);
        }
        
        return REMOTE_EXCEPTION;
    }

    public int release(){
        try {
            if (null != mIBinder) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.IImagePlayerService");
                mIBinder.transact(TRANSACTION_RELEASE,
                                        data, reply, 0);
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
    public interface ImagePlayerListener{
        public void onPrepared();
        public void onPlaying();
        public void onStoped();
        public void onShow();
        public void relseased();
    }
}
