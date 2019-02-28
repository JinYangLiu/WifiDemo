package com.ljy.wifidemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuJinYang
 * @date 2019/1/29
 */
public class WifiUtil {
    private static WifiUtil sInstance;
    // 上下文Context对象
    private Context mContext;
    // WifiManager对象
    private WifiManager mWifiManager;
    private WifiManager.WifiLock mWifiLock;
    private WifiUtil() { }

    public static WifiUtil getInstance() {
        if (null == sInstance) {
            sInstance = new WifiUtil();
        }
        return sInstance;
    }

    public void init(Context mContext) {
        this.mContext = mContext;
        mWifiManager = (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    // 1. 检查当前WIFI状态
    public void checkState() {
        if (mWifiManager.getWifiState() == 0) {
            Toast.makeText(mContext, "Wifi正在关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 1) {
            Toast.makeText(mContext, "Wifi已经关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 2) {
            Toast.makeText(mContext, "Wifi正在开启", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 3) {
            Toast.makeText(mContext, "Wifi已经开启", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "没有获取到WiFi状态", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断手机是否连接在Wifi上
     */
    public boolean isConnectWifi() {
        // 获取ConnectivityManager对象
        ConnectivityManager conMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo info = conMgr != null ? conMgr.getActiveNetworkInfo() : null;
        // 获取连接的方式为wifi
        NetworkInfo.State wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (info != null && info.isAvailable() && wifi == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }

    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else if (mWifiManager.getWifiState() == 2) {
            Toast.makeText(mContext, "亲，Wifi正在开启，不用再开了", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "亲，Wifi已经开启,不用再开了", Toast.LENGTH_SHORT).show();
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else if (mWifiManager.getWifiState() == 1) {
            Toast.makeText(mContext, "亲，Wifi已经关闭，不用再关了", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == 0) {
            Toast.makeText(mContext, "亲，Wifi正在关闭，不用再关了", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "请重新关闭", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 获取当前手机所连接的wifi信息
     */
    public WifiInfo getCurrentWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    /**
     * 添加一个网络并连接
     * 传入参数：WIFI发生配置类WifiConfiguration
     */
    private boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }

    /**
     * 搜索附近的热点信息，并返回所有热点为信息的SSID集合数据
     */
    public boolean startScan() {
        if (!checkPermission()) {
            return false;
        }
        if (!mWifiManager.isWifiEnabled()) {
            try {
                mWifiManager.setWifiEnabled(true);
            } catch (SecurityException e) {
                return false;
            }
        }
        return mWifiManager.startScan();

    }


    /**
     * android 6.0及以上需要先请求运行时权限，才可以扫描wifi
     *
     * @return
     */
    public boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) mContext, permissionsList.toArray(new String[permissionsList.size()]),
                    1001);
            return false;
        }
        return true;
    }

    /**
     * 得到手机搜索到的ssid集合，从中判断出设备的ssid（dssid）
     */
//    public List<String> accordSsid() {
//        List<String> s = getScanSSIDsResult();
//        List<String> result = new ArrayList<String>();
//        for (String str : s) {
//            if (checkDssid(str,"12345678")) {
//                result.add(str);
//            }
//        }
//        return result;
//    }

    /**
     * 检测指定ssid是不是匹配的ssid，目前支持GBELL，TOP,后续可添加。
     *
     * @param ssid
     * @return
     */
//    private boolean checkDssid(String ssid, String condition) {
//        if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(condition)) {
//            //这里条件根据自己的需求来判断，我这里就是随便写的一个条件
//            if (ssid.length() > 8 && (ssid.substring(0, 8).equals(condition))) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }

    /**
     * 连接wifi
     * 参数：wifi的ssid及wifi的密码
     */
    public boolean connectWifiTest(final String ssid, final String pwd) {
        boolean isSuccess = false;
        boolean flag = false;
        mWifiManager.disconnect();
        boolean addSucess = addNetwork(CreateWifiInfo(ssid, pwd, 3));
        if (addSucess) {
            while (!flag && !isSuccess) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                String currSSID = getCurrentWifiInfo().getSSID();
                if (currSSID != null) {
                    currSSID = currSSID.replace("\"", "");
                }
                int currIp = getCurrentWifiInfo().getIpAddress();
                if (currSSID != null && currSSID.equals(ssid) && currIp != 0) {
                    //这里还需要做优化处理，增强结果判断
                    isSuccess = true;
                } else {
                    flag = true;
                }
            }
        }
        return isSuccess;

    }

    /**
     * 创建WifiConfiguration对象
     * 分为三种情况：1没有密码;2用wep加密;3用wpa加密
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    private WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                             int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 锁定WifiLock
     */
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    /**
     * 解锁WifiLock
     */
    public void releaseWifiLock() {
        // 判断时候锁定
        if (isHeld()) {
            mWifiLock.acquire();
        }
    }

    /**
     * 判断wifi的锁是否持有
     */
    public boolean isHeld() {
        return mWifiLock.isHeld();
    }


    /**
     * 创建一个WifiLock
     * 经过查看网上的资料，知道在手机屏幕关闭之后，并且其他的应用程序没有在使用wifi的时候，
     * 系统大概在两分钟之后，会关闭wifi，使得wifi处于睡眠状态。
     * 这样的做法，有利于电源能量的节省和延长电池寿命等。
     * android为wifi提供了一种叫WifiLock的锁，能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态。
     * 这种锁，在下载一个较大的文件的时候，比较适合使用。
     * WifiLock Allows an application to keep the Wi-Fi radio awake
     */
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    /**
     * @param lockName 锁的名称
     * @param lockType 锁的类型
     *                 WIFI_MODE_FULL == 1
     *                 扫描，自动的尝试去连接一个曾经配置过的点
     *                 WIFI_MODE_SCAN_ONLY == 2
     *                 只剩下扫描
     *                 WIFI_MODE_FULL_HIGH_PERF = 3
     *                 在第一种模式的基础上，保持最佳性能
     * @return wifiLock
     */
    public void createWifiLock(String lockName, int lockType) {
        mWifiLock = mWifiManager.createWifiLock(lockType, lockName);
    }

}
