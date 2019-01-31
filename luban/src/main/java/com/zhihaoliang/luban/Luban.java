package com.zhihaoliang.luban;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 创建日期：2019/1/29
 * 描述:鲁班的对外接口，建造者模式简化设置方式
 * 作者:支豪亮
 */
public class Luban implements Handler.Callback {

    private Builder mBuilder;

    private Handler mHandler;

    private Luban(Builder builder) {
        mBuilder = builder;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }


    private void launch(final Context context) {

        if (mBuilder.onCompressListener == null) {
            return;
        }

        if (mBuilder.originalPaths == null || mBuilder.originalPaths.size() == 0) {
            mBuilder.onCompressListener.onCompressError(null, "要压缩的文件为空");
            return;
        }

        if (mBuilder.targetDir == null) {
            mBuilder.onCompressListener.onCompressError(null, "存放的图片的目录为空");
            return;
        }

        File targetDir = new File(mBuilder.targetDir);
        if (targetDir.isFile()) {
            mBuilder.onCompressListener.onCompressError(null, "存放的图片的目录为文件");
            return;
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }


        final List<CompressBean> listResult = new ArrayList<>();
        String erroResult = null;
        for (String originalPath : mBuilder.originalPaths) {
            if (TextUtils.isEmpty(originalPath)) {
                String msg = addCompressBean(listResult, originalPath, CompressBean.MSG_FILE_NULL, erroResult);
                if (!TextUtils.isEmpty(msg)) {
                    erroResult = msg;
                }
                continue;
            }

            File file = new File(originalPath);
            if (!file.exists()) {
                String msg = addCompressBean(listResult, originalPath, CompressBean.MSG_FILE_UNEXIST, erroResult);
                if (!TextUtils.isEmpty(msg)) {
                    erroResult = msg;
                }
                continue;
            }

            if (file.isDirectory()) {
                String msg = addCompressBean(listResult, originalPath, CompressBean.MSG_FILE_DIRECTORY, erroResult);
                if (!TextUtils.isEmpty(msg)) {
                    erroResult = msg;
                }
                continue;
            }

            File targetFile = new File(targetDir, System.currentTimeMillis() + getFileName());
            CompressBean compressBean = new CompressBean(originalPath, targetFile.getPath(), null);
            listResult.add(compressBean);
        }
        mHandler = new Handler(Looper.getMainLooper(), this);
        mBuilder.onCompressListener.onCompressStart();
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                new Engine().compress(listResult, mBuilder);
                Message message = Message.obtain();
                message.obj = listResult;
                mHandler.sendMessage(message);
            }
        });

    }

    private static String addCompressBean(List<CompressBean> listResult, String originalPath, String erroMsg, String erroResult) {
        CompressBean compressBean = new CompressBean(originalPath, null, erroMsg);
        if (TextUtils.isEmpty(erroResult)) {
            if (!TextUtils.isEmpty(erroMsg)) {
                return CompressBean.ERRO_MSG;
            }
        }
        listResult.add(compressBean);
        return null;
    }


    private String getFileName() {
        if (mBuilder.focusAlpha) {
            //如果要压缩图片质量-返货jgp
            return System.currentTimeMillis() + ".jgp";
        }

        return System.currentTimeMillis() + ".png";
    }

    @Override
    public boolean handleMessage(Message msg) {
        List<CompressBean> listResult = (List<CompressBean>) msg.obj;

        for (CompressBean compressBean : listResult) {
            if (!TextUtils.isEmpty(compressBean.getErroMsg())) {
                mBuilder.onCompressListener.onCompressError(listResult, CompressBean.ERRO_WRITE_FILE);
                return true;
            }
        }

        mBuilder.onCompressListener.onCompressSuccess(listResult);
        return true;
    }

    public static class Builder {
        private Context context;
        //压缩后的目的路径
        private String targetDir;
        //压缩后图片的大小 为100k左右
        private int fileSize = 100 << 10;
        //压缩后文件的最大的尺寸
        private int longSide = 1080;
        //压缩的通知
        private OnCompressListener onCompressListener;
        //要压缩图片的原始的路径
        private List<String> originalPaths;
        //对图片的压缩可以通过四个方面进行压缩
        //1.对图片清晰度的压缩,也就是每一像素所占资源的大小进行压缩，及调色板颜色数量的减少(PNG是无损图片，不能对PNG格式尽心操作)
        //2.对图片的宽高的压缩
        //3.对图片的格式进行进行压缩（不考虑）
        //是否对图片的质量进行压缩
        private boolean focusAlpha = false;

        private void initTargetDir() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                targetDir = context.getExternalCacheDir().getAbsolutePath();
            } else {
                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
                File file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
                targetDir = file.getAbsolutePath();
            }
        }

        Builder(Context context) {
            this.context = context;
            initTargetDir();
        }


        private Luban build() {
            return new Luban(this);
        }

        public Builder setTargetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }

        public Builder setFileSize(int fileSize) {
            this.fileSize = fileSize << 10;
            return this;
        }

        public Builder setLongSide(int longSide) {
            this.longSide = longSide;
            return this;
        }

        public Builder setOnCompressListener(OnCompressListener onCompressListener) {
            this.onCompressListener = onCompressListener;
            return this;
        }

        public Builder setOriginalPaths(String... originalPaths) {
            if (originalPaths != null && originalPaths.length != 0) {
                this.originalPaths = Arrays.asList(originalPaths);
            }
            return this;
        }

        public Builder setOriginalPaths(List<String> originalPaths) {
            this.originalPaths = originalPaths;
            return this;
        }


        public Builder setFocusAlpha(boolean focusAlpha) {
            this.focusAlpha = focusAlpha;
            return this;
        }

        public int getFileSize() {
            return fileSize;
        }


        public int getLongSide() {
            return longSide;
        }


        public boolean isFocusAlpha() {
            return focusAlpha;
        }

        public void launch() {
            build().launch(context);
        }

    }

}
