package com.deep.deepwifi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by wangfei on 2018/3/8.
 */

public class DeepWiFiAction {
    private Context mContext;
    private String TAG = this.getClass().getSimpleName();
    private String ipAddress = "";
    private int preLength = 24;
    private String dns = "";
    private String gateWay = "";
    public DeepWiFiAction(Context context){
        this.mContext = context;
    }
    public DeepWiFiAction setIP(String ip){
        ipAddress = ip;
        return this;
    }
    public DeepWiFiAction setDNS(String dns){
        this.dns = dns;
        return this;
    }
    public DeepWiFiAction setGateWay(String gateWay){
        this.gateWay = gateWay;
        return this;
    }

    public void configureIP(){

        if (mContext==null){
            Log.e(TAG,"mContext is null");
            return;
        }

        if (TextUtils.isEmpty(ipAddress)){
            Log.e(TAG,"ipAddress is null");
            return;
        }
        if (TextUtils.isEmpty(dns)){
            Log.e(TAG,"ipAddress is null");
            return;
        }
        if (TextUtils.isEmpty(gateWay)){
            Log.e(TAG,"ipAddress is null");
            return;
        }
        WifiConfiguration wifiConfig = getwifiConfig();
        if (wifiConfig==null){
            Log.e(TAG,"wifiConfig is null");
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < 11) {
            ContentResolver ctRes = mContext.getContentResolver();
            Settings.System
                .putInt(ctRes, Settings.System.WIFI_USE_STATIC_IP, 1);
            Settings.System.putString(ctRes, Settings.System.WIFI_STATIC_IP,
                ipAddress);
            Settings.System.putString(ctRes,
                Settings.System.WIFI_STATIC_NETMASK, "255.255.255.0");
            Settings.System.putString(ctRes,
                Settings.System.WIFI_STATIC_GATEWAY, gateWay);
            Settings.System.putString(ctRes, Settings.System.WIFI_STATIC_DNS1,
                dns);
            //Settings.System.putString(ctRes, Settings.System.WIFI_STATIC_DNS2,
            //    "61.134.1.9");
        }

        else {
            try {
                //setIpType("STATIC", wifiConfig);
                setIpAddress(InetAddress.getByName(ipAddress), preLength,
                    wifiConfig);
                setGateway(InetAddress.getByName(gateWay), wifiConfig);
                setDNS(InetAddress.getByName(dns), wifiConfig);
                L.e("setting ok");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
    private void setIpType(String ipType, WifiConfiguration wifiConfig)
        throws Exception {
        Field f = wifiConfig.getClass().getField("ipAssignment");
        f.set(wifiConfig, Enum.valueOf((Class<Enum>) f.getType(), ipType));
    }
    private void setDNS(InetAddress dns, WifiConfiguration wifiConfig)
        throws Exception {

        Object linkProperties = getFieldValue(wifiConfig, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(
            linkProperties, "mDnses");
        mDnses.clear();
        mDnses.add(dns);
    }
    private void setIpAddress(InetAddress ipAddress, int preLength,
                              WifiConfiguration wifiConfig) throws Exception {
        Object linkProperties = getFieldValue(wifiConfig, "linkProperties");
        if (linkProperties == null) {
            return;
        }
        Class<?> linkAddressClass = Class.forName("android.net.LinkAddress");
        Constructor<?> linkAddressConstrcutor = linkAddressClass
            .getConstructor(new Class[] { InetAddress.class, int.class });
        Object linkAddress = linkAddressConstrcutor.newInstance(ipAddress,
            preLength);
        ArrayList<Object> linkAddresses = (ArrayList<Object>) getDeclaredField(
            linkProperties, "mLinkAddresses");
        linkAddresses.clear();
        linkAddresses.add(linkAddress);
    }
    private Object getFieldValue(Object obj, String name) throws Exception {
        Field f = obj.getClass().getField(name);
        return f.get(obj);
    }
    private Object getDeclaredField(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }
    private void setGateway(InetAddress gateWay, WifiConfiguration wifiConfig)
        throws Exception {

        Object linkProperties = getFieldValue(wifiConfig, "linkProperties");
        if (linkProperties == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= 14) {


            Class<?> routeInfoClass = Class.forName("android.net.RouteInfo");

            Constructor<?> routeInfoConstructor = routeInfoClass
                .getConstructor(new Class[] { InetAddress.class });

            Object routeInfo = routeInfoConstructor.newInstance(gateWay);
            ArrayList<Object> routes = (ArrayList<Object>) getDeclaredField(
                linkProperties, "mRoutes");
            routes.clear();
            routes.add(routeInfo);
        }

        else {
            ArrayList<InetAddress> gateWays = (ArrayList<InetAddress>) getDeclaredField(
                linkProperties, "mGateWays");
            gateWays.clear();
            gateWays.add(gateWay);
        }
    }
    private WifiConfiguration getwifiConfig(){
        if (mContext==null){
            Log.e(TAG,"mContext is null");
            return null;
        }
        WifiConfiguration wifiConfig = null;
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager
            .getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks) {
            if (conf.networkId == connectionInfo.getNetworkId()) {
                wifiConfig = conf;
                break;
            }
        }
        return wifiConfig;
    }
}
