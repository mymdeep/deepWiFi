package com.deep.deepwifi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.deep.deepwifi.bean.ARPBean;
import com.deep.deepwifi.interfaces.ARPCallback;
import com.deep.deepwifi.interfaces.WiFiListener;
import com.deep.deepwifi.threads.ThreadUtils;
import com.deep.deepwifi.threads.UDPThread;


/**
 * Created by wangfei on 2018/3/20.
 */

public class DeepWifi {
    private static DeepWifi m_Instance;
    private Context mContext;
    WifiBroadCastReceiver receiver;
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;
    private WifiManager wifiManager;
    private List<WifiConfiguration> wifiConfigList =  new ArrayList<WifiConfiguration>();
    private DeepWifi(Context context)
    {
        mContext = context.getApplicationContext();
    }

    public static DeepWifi getInstance(Context value)
    {
        if(m_Instance==null){
            synchronized(DeepWifi .class){
                if(m_Instance==null){
                    m_Instance=new DeepWifi (value);
                }
            }
        }
        return m_Instance;
    }

    public List<WifiConfiguration> getWifiConfigList() {
        if (getManager() == null){
            return wifiConfigList;
        }
        wifiConfigList =  getManager().getConfiguredNetworks();
        return getManager().getConfiguredNetworks();
    }

    public WifiManager getManager(){
        if (mContext==null){
            return null;
        }
        if (wifiManager == null){
            wifiManager = (WifiManager)mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager;

    }
    public int getWifiState(){
        if (getManager()==null){
            return 0;
        }
        return getManager().getWifiState();
    }
    public void WifiOpen(){
        if (getManager()==null){
            return ;
        }
        if(!getManager().isWifiEnabled()){
            getManager().setWifiEnabled(true);
        }
    }
    public void registerWifiBroadCast(WiFiListener listener){
        if (receiver==null){
            receiver = new WifiBroadCastReceiver(listener);
        }
        mContext.registerReceiver(receiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mContext.registerReceiver(receiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        mContext.registerReceiver(receiver,new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
        mContext.registerReceiver(receiver,new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

    }
    public void unRegisterWifiBroadCast(){
        if (mContext!=null&&receiver!=null){
            mContext.unregisterReceiver(receiver);
        }

    }
    public void WifiClose(){
        if (getManager()==null){
            return ;
        }
        if(!getManager().isWifiEnabled()){
            getManager().setWifiEnabled(false);
        }
    }
    public void WifiStartScan(){
        if (getManager()==null){
            return ;
        }
        getManager().startScan();
    }
    public List<ScanResult> getScanResults(){
        if (getManager()==null){
            return new ArrayList<ScanResult>();
        }
        return getManager().getScanResults();//得到扫描结果
    }
    public List<ScanResult> getScanRealResults(){
        if (getManager()==null){
            return new ArrayList<ScanResult>();
        }
        ArrayList<ScanResult> results = new ArrayList<ScanResult>();
        for (ScanResult r: getManager().getScanResults()){
            if (!TextUtils.isEmpty(r.SSID)){
                results.add(r);
            }
        }
        return results;
    }
    public boolean connect(int wifiId){
        if (getManager()==null){
            return false;
        }
        wifiConfigList = getWifiConfigList();
        for(int i = 0; i < getWifiConfigList().size(); i++){
            WifiConfiguration wifi = wifiConfigList.get(i);
            if(wifi.networkId == wifiId){

                return getManager().enableNetwork(wifiId, true);
            }
        }
        return false;
    }
    public boolean connect(String ssid){
        if (getManager()==null){
            return false;
        }
        wifiConfigList = getWifiConfigList();
        for(int i = 0; i < getWifiConfigList().size(); i++){
            WifiConfiguration wifi = wifiConfigList.get(i);
            if(wifi.SSID.equals(ssid)){
                return getManager().enableNetwork(wifi.networkId, true);
            }
        }
        return false;
    }

    /**建立连接的信息
     * @return
     */
    public WifiInfo getConnectedInfo(){
        if (getManager()==null){
            return null;
        }
        return getManager().getConnectionInfo();
    }

    public String getConnectedMac(){
        if (getConnectedInfo()==null){
            return "";
        }

        return getConnectedInfo().getMacAddress();
    }
    public String getConnectedBSSID(){
        if (getConnectedInfo()==null){
            return "";
        }

        return getConnectedInfo().getBSSID();
    }
    public String getConnectedSSID(){
        if (getConnectedInfo()==null){
            return "";
        }

        return getConnectedInfo().getSSID();
    }
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public int getConnectedFrequency(){
        if (getConnectedInfo()==null){
            return 0;
        }

        return getConnectedInfo().getFrequency();
    }
    public String getIP(){
        if (getConnectedInfo()==null){
            return "";
        }

        return Formatter.formatIpAddress(getConnectedInfo().getIpAddress());
    }
    public int getSpeed(){
        if (getConnectedInfo()==null){
            return 0;
        }

        return getConnectedInfo().getLinkSpeed();
    }
    public int getRssi(){
        if (getConnectedInfo()==null){
            return 0;
        }

        return getConnectedInfo().getRssi();
    }
    public SupplicantState getSupplicanState(){
        if (getConnectedInfo()==null){
            return null;
        }

        return getConnectedInfo().getSupplicantState();
    }
    public int getNetId(){
        if (getConnectedInfo()==null){
            return 0;
        }

        return getConnectedInfo().getNetworkId();
    }
    public void getLAN_IP(ARPCallback callback) throws InterruptedException {
        String[] ips = getIP().split("[.]");
        ArrayList<Future<Integer>> list =new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<ips.length- 1; i++){
            sb.append(ips[i]).append(".");
        }
        final ExecutorService e = ThreadUtils.getPool(5);
        String ip_keep = sb.toString();


            for (int i=2; i<=255; i++) {
                String newip = ip_keep+String.valueOf(i);
                exeUDP(newip,e,list);
            }
            int count = 0;
            for (Future<Integer> f:list){
                try {
                    count+=f.get();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
            }
            L.e("count = "+count);
           callback.callback(readArp());

    }
    private ArrayList<ARPBean> readArp() {
        ArrayList<ARPBean> as = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(
                new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) {
                        continue;
                    }
                    ARPBean b = new ARPBean();
                    b.ip = ip;
                    b.mac = mac;
                    as.add(b);


                } catch (Exception e) {
                }
            }
            br.close();

        } catch(Exception e) {
        }
        return as;
    }
    private void discover(int level) {
        //if (level<1 || level >4){
        //    level = 1;
        //}
        //final int finalLevel = level;
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        String[] ips = getIP().split("[.]");
        //
        //
        //        StringBuilder sb = new StringBuilder();
        //        for (int i = 0; i<ips.length- finalLevel; i++){
        //            sb.append(ips[i]).append(".");
        //        }
        //        final ExecutorService e = ThreadUtils.getPool(5);
        //        String ip_keep = sb.toString();
        //        boolean end = false;
        //        for (int j =2;j<=255;j++){
        //            String l2 = ip_keep;
        //            if (finalLevel >=2){
        //                l2 = ip_keep+String.valueOf(j)+".";
        //            }
        //            for (int i=2; i<=255; i++) {
        //                String newip = l2+String.valueOf(i);
        //                if (finalLevel == 1){
        //                    if (i == 255){
        //                        end = true;
        //                    }
        //                    else {
        //                        end = false;
        //                    }
        //                }
        //                else if (finalLevel == 2){
        //                    if (i == 255&& j == 255){
        //                        end = true;
        //                    }
        //                    else {
        //                        end = false;
        //                    }
        //                }
        //                exeUDP(newip,e);
        //            }
        //            if (finalLevel <2){
        //                break;
        //            }
        //        }
        //
        //    }
        //}).start();





    }
    private void exeUDP(String newip,ExecutorService e,ArrayList<Future<Integer>> l){
        L.e("ip="+newip);
        if (newip.equals(getIP()))
        {
            return;
        }
        Callable<Integer> callable = new UDPThread(newip);
        Future<Integer> f = e.submit(callable);
        l.add(f);

    }
    public boolean turnOnWifiAp(String ssid, String password,HotPointType Type) {

        //配置热点信息。
        WifiConfiguration wcfg = new WifiConfiguration();
        wcfg.SSID = new String(ssid);
        wcfg.networkId = 1;
        wcfg.allowedAuthAlgorithms.clear();
        wcfg.allowedGroupCiphers.clear();
        wcfg.allowedKeyManagement.clear();
        wcfg.allowedPairwiseCiphers.clear();
        wcfg.allowedProtocols.clear();

        if(Type == HotPointType.WIFICIPHER_NOPASS) {
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
            wcfg.wepKeys[0] = "";
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wcfg.wepTxKeyIndex = 0;
        } else if(Type == HotPointType.WIFICIPHER_WPA) {
            //密码至少8位，否则使用默认密码
            if(null != password && password.length() >= 8){
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = "00000000";
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if(Type == HotPointType.WIFICIPHER_WPA2) {
            //密码至少8位，否则使用默认密码
            if(null != password && password.length() >= 8){
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = "00000000";
            }
            wcfg.hiddenSSID = true;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(4);
            //wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        }
        try {
            Method method = getManager().getClass().getMethod("setWifiApConfiguration",
                wcfg.getClass());
            Boolean rt = (Boolean)method.invoke(getManager(), wcfg);

        } catch (NoSuchMethodException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return setWifiApEnabled();
    }
    private boolean setWifiApEnabled() {
        //开启wifi热点需要关闭wifi
        while(getManager().getWifiState() != WifiManager.WIFI_STATE_DISABLED){
            getManager().setWifiEnabled(false);
            try {
                Thread.sleep(200);
            } catch (Exception e) {

                return false;
            }
        }
        //// 确保wifi 热点关闭。
        //while(getWifiState() != WIFI_AP_STATE_DISABLED){
        //    L.e("222");
        //    try {
        //        Method method1 = getManager().getClass().getMethod("setWifiApEnabled",
        //            WifiConfiguration.class, boolean.class);
        //        method1.invoke(getManager(), null, false);
        //
        //        Thread.sleep(200);
        //    } catch (Exception e) {
        //
        //        return false;
        //    }
        //}

        //开启wifi热点
        try {
            Method method1 = getManager().getClass().getMethod("setWifiApEnabled",
                WifiConfiguration.class, boolean.class);
            method1.invoke(getManager(), null, true);
            Thread.sleep(200);
        } catch (Exception e) {

            return false;
        }
        return true;
    }
    public void closeWifiAp() {
        if (getWifiState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method = getManager().getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke( getManager());
                Method method2 =  getManager().getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke( getManager(), config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
