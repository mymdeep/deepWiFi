package com.deep.deepwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.deep.deepwifi.interfaces.WiFiListener;

/**
 * Created by wangfei on 2018/3/20.
 */

public class WifiBroadCastReceiver extends BroadcastReceiver {
    WiFiListener listener;
    public WifiBroadCastReceiver(WiFiListener listener){
            this.listener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener==null){
            L.e("listener is null");
            return;
        }
        switch (intent.getAction()){
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_DISABLED);
                if (wifiState == WifiManager.WIFI_STATE_DISABLED){
                    listener.onWifiSwitch(false);
                }else {
                    listener.onWifiSwitch(true);
                }

                break;
            case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                if (intent.getExtras()!=null){
                    Bundle b = intent.getExtras();
                    for (String k:b.keySet()){
                        L.e("k="+k+"  v="+b.get(k));
                    }
                }
                listener.onWifiScanResult();
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                if (intent.getExtras()!=null){
                    Bundle b = intent.getExtras();
                    for (String k:b.keySet()){
                        L.e("k="+k+"  v="+b.get(k));
                    }
                }
                listener.onWifiConnect();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                if (intent.getExtras()!=null){
                    Bundle b = intent.getExtras();
                    for (String k:b.keySet()){
                        L.e("k="+k+"  v="+b.get(k));
                    }
                }
                listener.onNetStateChange();
                break;
                default:
                    L.e(" lack of monitoring");
                    break;
        }
    }
}
