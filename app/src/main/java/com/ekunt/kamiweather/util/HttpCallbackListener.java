package com.ekunt.kamiweather.util;

/**
 * 用回调服务器返回的结果
 * Created by E-Kunt on 2016/1/20.
 */
public interface HttpCallbackListener {

    /**
     * 处理服务器返回的信息。
     * @param response
     */
    void onFinish(String response);

    /**
     * 处理服务器访问错误
     * @param e
     */
    void onError(Exception e);
}
