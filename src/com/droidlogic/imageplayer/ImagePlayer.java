/******************************************************************
*
*Copyright (C) 2012  Amlogic, Inc.
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
******************************************************************/
package com.droidlogic.imageplayer;

import android.content.Context;

import android.net.Uri;
import com.droidlogic.app.ImagePlayerManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import android.util.Log;


public class ImagePlayer extends ImagePlayerManager {
        private static final String TAG = "ImagePlayer";
        public static final int REMOTE_EXCEPTION = -0xffff;
        public static final int STATUS_UNDEFINE = -1;
        public static final int STATUS_PRAPARE = 0;
        public static final int STATUS_PLAYING = 1;
        public static final int STATUS_RELEASE = 2;
        private ImagePlayerListener mListener;
        private int mCurStatus;
        private Context mContext;

        /**
         * Method to create a ImagePlayer,use {@link #init()} function to init image_player_service
         *
         * @param context maybe useful someday
         * @param callback interface
         */
        public ImagePlayer ( Context context, ImagePlayerListener listener ) {
            super ( context );
            mContext = context;
            mListener = listener;
            mCurStatus = STATUS_UNDEFINE;
        }


        public int start() {
            int ret = super.start();
            mCurStatus = STATUS_PLAYING;
            return ret;
        }

        /**
         * Prepares the ImagePlayer buffer for the picture.
         *
         * After setting the datasource and the display surface, you need to
         * call prepare() to prepare buffer for the show.
         *
         */
        public int prepare() {
            int ret = super.prepare();
            mCurStatus = STATUS_PRAPARE;
            return ret;
        }

        public int release() {
            int ret = super.release();
            mListener.relseased();
            mCurStatus = STATUS_RELEASE;
            return ret;
        }

        /**
         * Show the second buffer for the second picture.
         *
         * After first display picture , showBuf can been called after prepareBuf to show second buf picture.
         *
         */
        public int showBuf() {
            int ret = super.showBuf();
            mListener.onShow();
            mCurStatus = STATUS_PLAYING;
            return ret;
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
