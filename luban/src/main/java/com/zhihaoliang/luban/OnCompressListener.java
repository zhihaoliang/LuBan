package com.zhihaoliang.luban;

import java.util.List;

/**
 * 创建日期：2019/1/29
 * 描述: 图片压缩的监听
 * 作者:支豪亮
 */
public interface OnCompressListener {
    /**
     * 当压缩开始调用的方法
     */
    void onCompressStart();
    /**
     * 压缩成功的回调
     * @param list 表示压缩的后的结果
     */
    void onCompressSuccess(List<CompressBean> list);
    /**
     * 压缩失败的回调
     * @param list 表示压缩的后的结果
     * @param erroMsg 失败的原因
     */
    void onCompressError(List<CompressBean> list,String erroMsg);
}
