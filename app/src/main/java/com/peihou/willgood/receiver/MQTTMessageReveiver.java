package com.peihou.willgood.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.peihou.willgood.base.MyApplication;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.service.ServiceUtils;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.http.NetWorkUtil;


public class MQTTMessageReveiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (intent!=null){
            Log.i("JPushReceiver","-->"+intent.getAction());
        }
        if (intent!=null && intent.getAction().equals(Intent.ACTION_TIME_TICK)){
            boolean running2= ServiceUtils.isServiceRunning(context,"com.peihou.willgood.service.MQService");
            Log.i("ServiceUtils","-->"+running2);
            if (!running2){
                Intent intent2=new Intent(context, MQService.class);
                intent.putExtra("restart",1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                }else {
                    context.startService(intent);
                }
            }
        }
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            ToastUtil.showShort(context, "网络不可用");
            Intent noNet = new Intent("offline");
            noNet.putExtra("all", "all");
            context.sendBroadcast(noNet);
            //改变背景或者 处理网络的全局变量
        } else if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
            Intent mqttIntent = new Intent(context, MQService.class);
            mqttIntent.putExtra("reconnect", "reconnect");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mqttIntent);
            }else {
                context.startService(mqttIntent);
            }
        }
    }

}
