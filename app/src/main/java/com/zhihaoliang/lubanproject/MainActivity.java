package com.zhihaoliang.lubanproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhihaoliang.luban.CompressBean;
import com.zhihaoliang.luban.Luban;
import com.zhihaoliang.luban.OnCompressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Handler.Callback, OnCompressListener {
    //展示压缩后的图片
    private ImageView mImVImage;
    //展示压缩前的图片信息
    private TextView mTxtVOrgin;
    //展示压缩后的图片信息
    private TextView mTxtVTaget;

    //图片的原始路径
    private static final String ORGIN_PATH = "/storage/sdcard1/DCIM/Camera/IMG_20190130_171813.jpg";

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtVTaget = findViewById(R.id.txtV_tage_info);
        mTxtVOrgin = findViewById(R.id.txtV_orgin_info);
        mImVImage = findViewById(R.id.imV_image);

        mHandler = new Handler(this);

        AsyncTask.SERIAL_EXECUTOR.execute(new MyRunable(ORGIN_PATH));
    }


    @Override
    public boolean handleMessage(Message msg) {
        String info = (String) msg.obj;
        if (msg.arg1 == 0) {
            mTxtVOrgin.setText(info);

            Luban.with(this).
                    setFileSize(100).
                    setFocusAlpha(true).
                    setLongSide(1080).
                    setOriginalPaths(ORGIN_PATH).
                    setOnCompressListener(this).
                    setTargetDir(initTargetDir()).launch();
        } else {
            mTxtVTaget.setText(info);
        }
        Log.e("==", info);
        return true;
    }

    @Override
    public void onCompressStart() {
        Log.e("==", "图片开始压缩");
    }

    private String initTargetDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return getExternalCacheDir().getAbsolutePath();
        } else {
            final String cacheDir = "/Android/data/" + getPackageName() + "/cache/";
            File file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
            return file.getAbsolutePath();
        }
    }

    @Override
    public void onCompressSuccess(List<CompressBean> list) {
        if (list == null) {
            return;
        }

        for (CompressBean compressBean : list) {
            Log.e("==", compressBean.toString());
        }

        Glide.with(this)
                .load(new File(list.get(0).getCompressPath()))
                .into(mImVImage);


        AsyncTask.SERIAL_EXECUTOR.execute(new MyRunable(list.get(0).getCompressPath()));

    }

    @Override
    public void onCompressError(List<CompressBean> list, String erroMsg) {
        if (!TextUtils.isEmpty(erroMsg)) {
            Log.e("==", erroMsg);
        }

        if (list == null) {
            return;
        }

        for (CompressBean compressBean : list) {
            Log.e("==", compressBean.toString());
        }
    }

    private class MyRunable implements Runnable {

        private String path;

        MyRunable(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeFile(path, options);
            File file = new File(path);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Message msg = Message.obtain();
                msg.obj = String.format("width = %d ======> height = %d ======> file.size = %d", options.outWidth, options.outHeight, fileInputStream.available());
                msg.arg1 = path.equals(ORGIN_PATH)?0:1;
                mHandler.sendMessage(msg);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
