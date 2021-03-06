package com.peihou.willgood.devicelist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.DialogLoad;
import com.peihou.willgood.pojo.Device;
import com.peihou.willgood.service.MQService;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 掉电记忆
 */
public class PowerLostMomoryActivity extends BaseActivity {

    @BindView(R.id.img_open)
    ImageView img_open;
    String deviceMac;
    int plMemory;
    @BindView(R.id.tv_name) TextView tv_name;
    int type=0;
    private Device device;
    @Override
    public void initParms(Bundle parms) {
        deviceMac = parms.getString("deviceMac");
        plMemory = parms.getInt("plMemory");
        type=parms.getInt("type");
        device= (Device) parms.getSerializable("device");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_power_lost_momory;
    }
    public static boolean running=false;
    String topicName;
    @Override
    public void initView(View view) {
        open=plMemory;
        if (open == 1) {
            img_open.setImageResource(R.mipmap.img_open);
        } else {
            img_open.setImageResource(R.mipmap.img_close);
        }
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";

        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

        receiver = new MessageEeceiver();
        IntentFilter filter = new IntentFilter("PowerLostMomoryActivity");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        running=true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;

    }

    int open = -1;//1为打开掉电记忆，0为关闭掉电记忆
    int click=0;//1为点击过，0没有点击过开关按钮
    int click2=0;
    @OnClick({R.id.img_back, R.id.img_open})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                Intent intent = new Intent();
                intent.putExtra("plMemory", plMemory);
                intent.putExtra("click",click);
                if (type==1){
                    setResult(1000, intent);
                }else if (type==2){
                    setResult(1001, intent);
                }
                finish();
                break;
            case R.id.img_open:
                click=1;
                if (open==1){
                    open=0;
//                    img_open.setImageResource(R.mipmap.img_close);
                    plMemory=0;

                }else {
                    open=1;
//                    img_open.setImageResource(R.mipmap.img_open);
                    plMemory=1;
                }
                if (device!=null){
                    device.setDeviceState(0);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    device.setPlMemory(plMemory);
                    mqService.sendBasic(topicName,device,0x03);
                    countTimer.start();
                    click2=1;
                }
                break;
        }
    }
    CountTimer countTimer = new CountTimer(2000, 1000);

    class CountTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setLoadDialog();
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
        }
    }
    DialogLoad dialogLoad;
    private void setLoadDialog() {
        if (dialogLoad != null && dialogLoad.isShowing()) {
            return;
        }

        dialogLoad = new DialogLoad(this);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.setLoad("正在加载,请稍后");
        dialogLoad.show();
    }
//    private PopupWindow popupWindow2;

//    public void popupmenuWindow3() {
//        if (popupWindow2 != null && popupWindow2.isShowing()) {
//            return;
//        }
//        View view = View.inflate(this, R.layout.progress, null);
//        TextView tv_load = view.findViewById(R.id.tv_load);
//        tv_load.setTextColor(getResources().getColor(R.color.white));
//
//            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        //添加弹出、弹入的动画
//        popupWindow2.setAnimationStyle(R.style.Popupwindow);
//        popupWindow2.setFocusable(false);
//        popupWindow2.setOutsideTouchable(false);
//        backgroundAlpha(0.6f);
//        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1.0f);
//            }
//        });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//        popupWindow2.showAtLocation(tv_name, Gravity.CENTER, 0, 0);
//        //添加按键事件监听
//    }
    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("plMemory", plMemory);
        intent.putExtra("click",click);
        if (type==1){
            setResult(1000, intent);
        }else if (type==2){
            setResult(1001, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (dialogLoad!=null && dialogLoad.isShowing()){
            dialogLoad.dismiss();
        }
    }

    private boolean bind;
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    MessageEeceiver receiver;

    class MessageEeceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String macAddress = intent.getStringExtra("macAddress");
            if (macAddress.equals(deviceMac)) {
                plMemory = intent.getIntExtra("plMemory", 0);
                device= (Device) intent.getSerializableExtra("device");
                if (plMemory == 0) {
                    open = 0;
                    img_open.setImageResource(R.mipmap.img_close);

                } else if (plMemory == 1) {
                    open = 1;
                    img_open.setImageResource(R.mipmap.img_open);
                }
                if (mqService!=null && click2==1){
                    mqService.starSpeech(deviceMac,3);
                    click2=0;
                }
            }
        }
    }
}
