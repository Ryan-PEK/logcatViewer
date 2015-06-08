package com.taobao.test.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.taobao.test.utils.DeviceHelper;
import com.taobao.test.utils.DeviceInfo;

import org.json.JSONObject;

public class DeviceStatusPollingService extends IntentService {

    private static final String TAG = "DeviceStatusPollingService";
    private static final int POLL_INTERVAL_IN_MILLISECOND = 3 * 1000;

    private LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

    public static final  String STATUS_RESULT = "com.taobao.test.services.DeviceStatusPollingService.REQUEST_PROCESSED";
    public static final  String STATUS_MESSAGE = "com.taobao.test.services.DeviceStatusPollingService.RESULT_MSG";

    public DeviceStatusPollingService() {
        super(TAG);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(STATUS_RESULT);
        if(message != null)
            intent.putExtra(STATUS_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //read log
        this.readDeviceStatus();
    }

    private Runnable reader = new Runnable() {
        @Override
        public void run() {
            readDeviceStatus();
        }
    };

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, DeviceStatusPollingService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL_IN_MILLISECOND, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
}

    //read status from server
    private void readDeviceStatus()
    {
        try {
            DeviceInfo deviceInfo = DeviceHelper.getSystemInfo(this);
            String result = DeviceHelper.getDeviceStatus(deviceInfo.getImei());
            String status = "";
            if(result != null && result.length() > 0 && !result.equalsIgnoreCase("NULL")) {
                JSONObject reader = new JSONObject(result);
                status = reader.getString("status");
            }
            this.sendResult(status);
        }
        catch (Throwable e)
        {
            this.sendResult("获取设备状态出错!");
        }
    }
}
