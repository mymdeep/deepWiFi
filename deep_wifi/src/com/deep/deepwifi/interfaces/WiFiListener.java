package com.deep.deepwifi.interfaces;

/**
 * Created by wangfei on 2018/3/20.
 */

public interface WiFiListener {
    /**
     * wifi 开关监听
     */
    public void onWifiSwitch(boolean isTurnOn);

    /**
     * 扫描结果
     */
    public void onWifiScanResult();

    /**
     * wifi连接结果
     */
    public void onWifiConnect();

    /**
     * wifi状态变化
     */
    public void onNetStateChange();
}
