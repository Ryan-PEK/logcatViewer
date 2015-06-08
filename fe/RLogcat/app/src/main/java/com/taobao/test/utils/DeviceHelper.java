package com.taobao.test.utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import org.json.JSONObject;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by hongyanyin on 5/9/15.
 */
public class DeviceHelper {

    //private static final String HOST = "http://10.62.62.187:3000";    //home
    //private static final String HOST = "http://192.168.31.110:3000";  //development
    private static final String HOST = "http://10.125.50.115:3000";     //production
    private static final String DEVICE_METHOD = "device";
    private static final String FETCH_STOP_METHOD = "fetcher/stop";

    private static String getWifiIP(Activity activity)
    {
        int i = ((WifiManager)activity.getSystemService("wifi")).getConnectionInfo().getIpAddress();
        int j = i & 0xff;
        int k = 0xff & i >> 8;
        int l = 0xff & i >> 16;
        int i1 = 0xff & i >> 24;
        return (new StringBuilder(String.valueOf(j))).append(".").append(k).append(".").append(l).append(".").append(i1).toString();
    }

    private static String getWifiIP(Context context)
    {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }


    private static String getIMEI(Context context){

        TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    private static String getOsVersion()
    {
        return "android " + Build.VERSION.RELEASE;
    }

    private static String getDeviceModel()
    {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public static DeviceInfo getSystemInfo(Context context)
    {
        DeviceInfo info  = new DeviceInfo();

        info.setImei(getIMEI(context));
        info.setIp(getWifiIP(context));
        info.setOs(getOsVersion());
        info.setDeviceModel(getDeviceModel());

        return info;
    }

    public static String registerDevice(DeviceInfo info)
    {
        String registerUrl = String.format("%s/%s", HOST, DEVICE_METHOD);
        String result = HttpHelper.post(registerUrl, info);
        return result;
    }

    public static String getDeviceStatus(String imei)
    {
        String getStatusUrl = String.format("%s/%s/%s", HOST, DEVICE_METHOD, imei);
        String result = HttpHelper.get(getStatusUrl);
        return result;
    }

    public static String stopFetcher(String imei)
    {
        String stopFetcherUrl = String.format("%s/%s", HOST, FETCH_STOP_METHOD);
        String result = HttpHelper.post(stopFetcherUrl, "{'imei': " + imei + "}");
        return result;
    }

    public static String parseDeviceStatus(String deviceJson)
    {
        if(deviceJson == null || deviceJson.length() == 0 || deviceJson.equalsIgnoreCase("NULL"))
        {
            return "";
        }

        try {
            JSONObject reader = new JSONObject(deviceJson);
            return reader.getString("status");
        }catch(Throwable e)
        {
            return "";
        }
    }
}
