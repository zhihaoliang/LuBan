package com.zhihaoliang.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 创建日期：2019/1/29
 * 描述:压缩的核心工具类
 * 作者:支豪亮
 */
class Engine {

    private static final int QUALITY = 60;

    void compress(List<CompressBean> list, Luban.Builder builder) {
        for (CompressBean compressBean : list) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = computeSize(compressBean.getOriginalPath(), builder);
            Bitmap bitmap = BitmapFactory.decodeFile(compressBean.getOriginalPath(), options);

            int angle = readPictureDegree(bitmap);
            bitmap = rotatingImage(bitmap, angle, builder.getLongSide());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (baos.size() <= builder.getFileSize()) {
                if (initResultErro(baos, bitmap, compressBean)) {
                    return;
                }
                continue;
            }

            if (builder.isFocusAlpha()) {
                baos = new ByteArrayOutputStream();
                //最多压缩60%，超过60%图片的清晰度会差很多
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos);
            }

            if (baos.size() <= builder.getFileSize()) {
                if (initResultErro(baos, bitmap, compressBean)) {
                    return;
                }
                continue;
            }

            int imageLongSize = Math.max(bitmap.getWidth(), bitmap.getHeight());
            double sacle = Math.sqrt(baos.size() * 1.0d / builder.getFileSize());

            int tarlongSide = (int) (imageLongSize / sacle);
            bitmap = rotatingImage(bitmap, 0, tarlongSide);

            int quality = builder.isFocusAlpha() ? QUALITY : 100;
            baos = new ByteArrayOutputStream();
            //最多压缩60%，超过60%图片的清晰度会差很多
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            if (initResultErro(baos, bitmap, compressBean)) {
                return;
            }
        }

    }

    //得到结果，并释放资源
    private boolean initResultErro(ByteArrayOutputStream stream, Bitmap bitmap, CompressBean compressBean) {
        try {
            bitmap.recycle();
            FileOutputStream fos = new FileOutputStream(new File(compressBean.getCompressPath()));
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();
            return false;
        } catch (IOException e) {
            compressBean.setErroMsg(e.getMessage());
            e.printStackTrace();
            return true;
        }

    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle, int longSide) {

        Point point = getPoint(bitmap, longSide);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        matrix.setScale(point.x * 1.0f / bitmap.getWidth(), point.y * 1.0f / bitmap.getHeight());


        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }


    private Point getPoint(Bitmap bitmap, int longSide) {
        int imWidth = bitmap.getWidth();
        int imHeight = bitmap.getHeight();

        int imLongSide = Math.max(bitmap.getWidth(), bitmap.getHeight());
        if (longSide >= imLongSide) {
            return new Point(imWidth, imHeight);
        }

        if (imWidth > imHeight) {
            return new Point(longSide, imHeight * longSide / imWidth);
        }

        return new Point(imWidth * longSide / imHeight, longSide);
    }


    private int computeSize(String pathName, Luban.Builder builder) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(pathName, options);
        int longSide = Math.max(options.outWidth, options.outHeight);
        return computeSize(longSide, builder);
    }

    private int computeSize(Bitmap bitmap, Luban.Builder builder) {
        int longSide = Math.max(bitmap.getWidth(), bitmap.getHeight());
        return computeSize(longSide, builder);
    }

    private int computeSize(int longSide, Luban.Builder builder) {
        int inSampleSize = longSide / builder.getLongSide();
        if (inSampleSize == 0) {
            return inSampleSize;
        }
        return inSampleSize;
    }


    //获取图片的旋转类
    private static int readPictureDegree(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            ExifInterface exifInterface = new ExifInterface(isBm);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
