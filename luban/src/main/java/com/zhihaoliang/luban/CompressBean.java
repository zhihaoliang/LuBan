package com.zhihaoliang.luban;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建日期：2019/1/29
 * 描述:图片压缩的存储的方式
 * 作者:支豪亮
 */
public class CompressBean implements Parcelable {

    static final String ERRO_MSG = "要压缩文件异常，请查看【list】中的详情信息";

    static final String ERRO_WRITE_FILE = "图片写入目标目录异常";

    static final String MSG_FILE_NULL = "要压缩文件路径为空";

    static final String MSG_FILE_UNEXIST = "要压缩文件不存在";

    static final String MSG_FILE_DIRECTORY = "要压缩的文件是文件夹";

    /**
     * 图片未压缩的路径
     */
    private String originalPath;
    /**
     * 压缩后的图片路径
     */
    private String compressPath;
    /**
     * 压缩失败的原因
     */
    private String erroMsg;

    public CompressBean(String originalPath, String compressPath, String erroMsg) {
        this.originalPath = originalPath;
        this.compressPath = compressPath;
        this.erroMsg = erroMsg;
    }

    protected CompressBean(Parcel in) {
        originalPath = in.readString();
        compressPath = in.readString();
        erroMsg = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalPath);
        dest.writeString(compressPath);
        dest.writeString(erroMsg);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CompressBean> CREATOR = new Creator<CompressBean>() {
        @Override
        public CompressBean createFromParcel(Parcel in) {
            return new CompressBean(in);
        }

        @Override
        public CompressBean[] newArray(int size) {
            return new CompressBean[size];
        }
    };

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getErroMsg() {
        return erroMsg;
    }

    public void setErroMsg(String erroMsg) {
        this.erroMsg = erroMsg;
    }

    @Override
    public String toString() {
        return "CompressBean{" +
                "originalPath='" + originalPath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", erroMsg='" + erroMsg + '\'' +
                '}';
    }
}
