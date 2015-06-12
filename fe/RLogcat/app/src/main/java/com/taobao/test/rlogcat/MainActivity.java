package com.taobao.test.rlogcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.test.services.DeviceStatusPollingService;
import com.taobao.test.utils.DeviceHelper;
import com.taobao.test.utils.DeviceInfo;

public class MainActivity extends ActionBarActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private TextView txtInstruction = null;
    private TextView txtMessageTitle = null;
    private TextView txtImei = null;
    private TextView txtMessage = null;
    private Button btnStart = null;
    private Button btnStop = null;
    private BroadcastReceiver receiver = null;
    private DeviceInfo deviceInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init controls
        this.initControls();

        //初始化设备信息
        this.initDeviceInfo();

        //启动polling service
        this.startPollService();
    }

    private void initControls()
    {
        this.txtInstruction = (TextView)this.findViewById(R.id.txt_instruction);
        this.txtInstruction.setText(R.string.instructions);
        this.txtMessageTitle = (TextView)this.findViewById(R.id.txt_message_title);
        this.txtMessageTitle.setText(R.string.message_title);
        this.txtImei = (TextView)this.findViewById(R.id.txt_imei);
        this.txtMessage = (TextView)this.findViewById(R.id.txt_message);
        this.txtMessage.setText("");
        this.btnStart = (Button)this.findViewById(R.id.start_button);
        this.btnStop =  (Button)this.findViewById(R.id.stop_button);

        this.initControlEvents();
    }

    private void initControlEvents()
    {
        this.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册设备并执行收集
                (new RegisterDeviceTask()).execute();
            }
        });

        this.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册设备并执行收集
                (new StopFetcherTask()).execute();
            }
        });
    }

    private void initDeviceInfo()
    {
         this.deviceInfo = DeviceHelper.getSystemInfo(getApplication());
         this.txtImei.setText("IMEI: "+ deviceInfo.getImei());
    }

    private void initReceiver()
    {
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(DeviceStatusPollingService.STATUS_MESSAGE);
                txtMessage.setText("设备状态：" + s + "\n" + txtMessage.getText());
                //从后台service获取服务端的设备的status，更新
                showButtonBasedDeviceStatus(s);
            }
        };
    }

    private void startPollService()
    {
        this.initReceiver();

        Intent i = new Intent(this, DeviceStatusPollingService.class);
        this.startService(i);

        DeviceStatusPollingService.setServiceAlarm(this, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_qr_scanner) {
            scanQR((SearchView) MenuItemCompat.getActionView(item));
            //scanQR(item.getActionView());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(DeviceStatusPollingService.STATUS_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    private class RegisterDeviceTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                if(deviceInfo == null){
                    return "获取机型信息失败！";
                }

                if(deviceInfo.getImei() == null || deviceInfo.getImei().length() ==0){
                    return "此工具目前不能支持您的机型，请联系工具owner，谢谢！";
                } else if (deviceInfo.getImei().equals("000000000000000")){
                    return "此工具目前不支持模拟器，请使用真机，谢谢！";
                }

                String result = DeviceHelper.registerDevice(deviceInfo);
                return result;

            } catch (Throwable e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtMessage.setText(result);
            if(result !=null && result.length() >0 && !result.equalsIgnoreCase("NULL"))
            {
                String deviceStatus = DeviceHelper.parseDeviceStatus(result);
                showButtonBasedDeviceStatus(deviceStatus);
            }
        }
    }

    private class StopFetcherTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            try {
                if(deviceInfo == null){
                    return "";
                }
                String result = DeviceHelper.stopFetcher(deviceInfo.getImei());
                showButtonBasedDeviceStatus(result);
                return result;
            } catch (Throwable e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtMessage.setText(result);
        }
    }

    private void enableFetcherButton()
    {
        btnStop.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
    }

    private void disableFetcherButton(){
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
    }

    private void showButtonBasedDeviceStatus(String status)
    {
        if(status == null)
        {
            return;
        }

        switch(status)
        {
            case "在线":
            case "未认证":
            case "日志传输中断":
            case "已停止":
            case "离线":
            case "":
                enableFetcherButton();
                break;

            case "日志传输中":
            case "已经连接":
            case "注册成功":
            case "客户端ip已改变":
                disableFetcherButton();
                break;
        }
    }

    public void scanBar(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
