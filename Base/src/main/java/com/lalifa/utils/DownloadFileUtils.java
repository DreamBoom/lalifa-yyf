package com.lalifa.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.ResponseBody;

public class DownloadFileUtils {

    private static Context context;
    private static String filePath;
    private static String fileNames;
    private static String mSaveMessage = "失败";
    private final static String TAG = "PictureActivity";
    private static File myCaptureFile;
    //	private static ProgressDialog mSaveDialog = null;

    public interface downLoadListener {
        void callBack(String url);
    }

    private static downLoadListener mdownLoadListener;


    public static void downloadImg(Context contexts, String filePaths, String fileName, downLoadListener listener) {
        context = contexts;
        filePath = filePaths;
        fileNames = fileName;
        mdownLoadListener = listener;
        new Thread(saveFileRunnable).start();
    }

    private static Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (!TextUtils.isEmpty(filePath)) { //网络图片
                    try {
                        URL url = new URL(filePath);
                        //打开连接
                        URLConnection conn = url.openConnection();
                        //打开输入流
                        InputStream is = conn.getInputStream();
                        //获得长度
                       // int contentLength = conn.getContentLength();
                        //创建文件夹 MyDownLoad，在存储卡下
                        String dirName = context.getExternalFilesDir(null).getPath() + "/MyDownLoad/";
                        File file = new File(dirName);
                        //不存在创建
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        //下载后的文件名
                        String fileName =  dirName+fileNames;
                        myCaptureFile = new File(fileName);
                        if (myCaptureFile.exists()) {
                            myCaptureFile.delete();
                        }
                        //创建字节流
                        byte[] bs = new byte[1024];
                        int len;
                        OutputStream os = new FileOutputStream(fileName);
                        //写数据
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        //完成后关闭流
                        os.close();
                        is.close();
                        Log.e("run", "下载完成了~" + dirName);
                        mSaveMessage = "文件已经保存成功！";
                        if (mdownLoadListener != null) {
                            mdownLoadListener.callBack(myCaptureFile.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessageDelayed(messageHandler.obtainMessage(), 500);
        }
    };

    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//			Log.d(TAG, mSaveMessage);
          //  Toast.makeText(context, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 获取图片路径
     *
     * @throws IOException
     */
    public static String getFile() {
        return myCaptureFile.getAbsolutePath();

    }


}
