package com.peihou.willgood.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.base.MyApplication;
import com.peihou.willgood.custom.ChangeDialog;
import com.peihou.willgood.custom.ExitLoginDialog;
import com.peihou.willgood.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood.devicelist.AlermActivity;
import com.peihou.willgood.devicelist.DeviceListActivity;
import com.peihou.willgood.devicelist.LinkedControlActivity;
import com.peihou.willgood.devicelist.LocationActivity;
import com.peihou.willgood.devicelist.LocationSetActivity;
import com.peihou.willgood.devicelist.PowerLostMomoryActivity;
import com.peihou.willgood.devicelist.SwichCheckActivity;
//import com.peihou.willgood.devicelist.TimerTaskActivity;
import com.peihou.willgood.devicelist.TimerTaskActivity;
import com.peihou.willgood.login.LoginActivity;
import com.peihou.willgood.pojo.Device;
import com.peihou.willgood.receiver.MQTTMessageReveiver;
import com.peihou.willgood.receiver.UtilsJPush;
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.service.ServiceUtils;
import com.peihou.willgood.util.NoFastClickUtils;
import com.peihou.willgood.util.TenTwoUtil;
import com.peihou.willgood.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import me.jessyan.autosize.internal.CustomAdapt;
import retrofit2.http.OPTIONS;

public class MainActivity extends BaseActivity implements CustomAdapt {


    MyApplication application;
    @BindView(R.id.img_exit)
    ImageView imgExit;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.tv_switch1)
    TextView tvSwitch1;
    @BindView(R.id.ll_switch1)
    LinearLayout llSwitch1;
    @BindView(R.id.img_refresh1)
    ImageView imgRefresh1;
    @BindView(R.id.img_tem)
    ImageView imgTem;
    @BindView(R.id.tv_tem)
    TextView tvTem;
    @BindView(R.id.tv_hum)
    TextView tvHum;
    @BindView(R.id.img_hum)
    ImageView imgHum;
    @BindView(R.id.tv_switch2)
    TextView tvSwitch2;
    @BindView(R.id.ll_switch2)
    LinearLayout llSwitch2;
    @BindView(R.id.tv7)
    TextView tv7;
    @BindView(R.id.img_refresh2)
    ImageView imgRefresh2;


    boolean isOpen1 = false, isOpen2 = false;
    @BindView(R.id.img_switch1)
    ImageView imgSwitch1;
    @BindView(R.id.img_switch2)
    ImageView imgSwitch2;

    DeviceDaoImpl deviceDao;
    @BindView(R.id.tv_offline)
    TextView tv_offline;
    @BindView(R.id.tv_offline2)
    TextView tv_offline2;
    int userId;

    @Override
    public void initParms(Bundle parms) {
        userId = parms.getInt("userId");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    Animation rotateAnimation;
    Device device;//配电系统
    Device device2;//供电系统
    String deviceMac;
    String topicName;//配电设备发送主题
    String topicName2;//控制设备主题
    MQTTMessageReveiver reveiver;

    @Override
    public void initView(View view) {
        requestOverlayPermission();
        application = (MyApplication) getApplication();
        deviceDao = new DeviceDaoImpl(getApplicationContext());

        reveiver = new MQTTMessageReveiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(reveiver, filter);
        messageReceiver = new MessageReceiver();
        IntentFilter filter2 = new IntentFilter("MainActivity");
        filter2.addAction("offline");
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(messageReceiver, filter2);
        JPushInterface.stopPush(this);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

//        device = deviceDao.findDeviceByType(1);
//        device2 = deviceDao.findDeviceByType(2);
//        if (device != null) {
//            device.setOnline(false);
//            deviceDao.update(device);
//            setMode(1);
//        }
//        if (device2 != null) {
//            device2.setOnline(false);
//            deviceDao.update(device2);
//            setMode(2);
//        }


//        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
//        rotateAnimation.setInterpolator(new LinearInterpolator());

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onBackPressed() {
        application.removeAllActivity();
    }

    ExitLoginDialog exitLoginDialog;

    private void exitLoginDialog() {
        if (exitLoginDialog != null && exitLoginDialog.isShowing()) {
            return;
        }
        exitLoginDialog = new ExitLoginDialog(this);
        exitLoginDialog.setCanceledOnTouchOutside(false);
        exitLoginDialog.setOnNegativeClickListener(new ExitLoginDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                exitLoginDialog.dismiss();
            }
        });
        exitLoginDialog.setOnPositiveClickListener(new ExitLoginDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                exitLoginDialog.dismiss();
                if (mqService != null) {
//                    mqService.clearAllData();
                    mqService.cancelAllsubscibe();
//                    mqService.clearCountTimer();
                }

                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    popupWindow2.dismiss();
                }
                setExitLoginPage();
            }
        });
        exitLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        UtilsJPush.stopJpush(this);
        backgroundAlpha(0.6f);
        exitLoginDialog.show();
    }

    private void setExitLoginPage() {
        List<Activity> activities = application.getActivities();
        List<Activity> list = new ArrayList<>();
        if (mqService != null) {
//            mqService.clearCountTimer();
            mqService.cancelAllsubscibe();

        }
        for (Activity activity : activities) {
            if (!(activity instanceof LoginActivity)) {
                list.add(activity);
                Log.i("Activity22222", "-->LoginActivity");
            }
            if (!(activity instanceof DeviceListActivity)) {
                list.add(activity);
                Log.i("Activity22222", "-->MainActivity");
            }
        }

        application.removeActiviies(list);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("exit", 1);
        startActivity(intent);
    }


    int type = -1;//1为配电系统 2为配电系统
    int refresh = 0;
    private void setMode2(int type){
        if (type==1){
            tv1.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv2.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv3.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv4.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv5.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv6.setBackgroundResource(R.drawable.bg_fill5_gray);
            imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
            llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
            tv_offline.setVisibility(View.GONE);
            tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_gray));
        }else if (type==2){
            imgSwitch2.setImageResource(R.mipmap.img_jog_off);
            llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
            tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv_offline2.setVisibility(View.GONE);
            tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_gray));
        }
    }
    private void setMode(int type) {
        if (type == 1) {
            tv1.setBackgroundResource(R.drawable.bg_fill5_green);
            tv2.setBackgroundResource(R.drawable.bg_fill5_green);
            tv3.setBackgroundResource(R.drawable.bg_fill5_green);
            tv4.setBackgroundResource(R.drawable.bg_fill5_green);
            tv5.setBackgroundResource(R.drawable.bg_fill5_green);
            tv6.setBackgroundResource(R.drawable.bg_fill5_green);

            if (device.getOnline()) {
                tv_offline.setVisibility(View.GONE);
                if (isOpen1) {
                    imgRefresh1.setImageResource(R.mipmap.ic_refresh1);
                    tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_green));
                    tvTem.setTextColor(this.getResources().getColor(R.color.main_gray));
                    tvHum.setTextColor(this.getResources().getColor(R.color.main_gray));
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch1);
                    imgSwitch1.setImageResource(R.mipmap.ic_main_close1);
                } else {
                    imgRefresh1.setImageResource(R.mipmap.ic_refresh1);
                    imgRefresh2.setImageResource(R.mipmap.ic_refresh2);
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
                    imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
                    tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_gray));
                }
//                double temp = device.getTemp();
//                double hum = device.getHum();
//                String s = "" + String.format("%.1f", temp);
//                String s2 = "" + String.format("%.1f", hum);
//                tvTem.setText(s);
//                tvHum.setText(s2);
            } else {
                imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
                llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
                tv_offline.setVisibility(View.VISIBLE);
                tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_gray));
            }
        } else if (type == 2) {
            if (device2.getOnline()) {
                tv_offline2.setVisibility(View.GONE);
                imgTem.setImageResource(R.mipmap.ic_tem2);
                imgHum.setImageResource(R.mipmap.ic_hum2);
                imgTem.setImageResource(R.mipmap.ic_tem2);
                imgHum.setImageResource(R.mipmap.ic_hum2);
                tvTem.setTextColor(this.getResources().getColor(R.color.main_black));
                tvHum.setTextColor(this.getResources().getColor(R.color.main_black));
                imgRefresh2.setImageResource(R.mipmap.ic_refresh1);

                tv7.setBackgroundResource(R.drawable.bg_fill5_green);
                imgSwitch2.setImageResource(R.mipmap.img_jog_on);
                llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch1);
                tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_green));
//                if (device2.getPlMemory() == 1) {
//                } else {
//                    tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
//                }
//                if (isOpen2) {
//                    imgSwitch2.setImageResource(R.mipmap.img_jog_on);
//                    llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
//                    tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_green));
//                } else {
//                    imgSwitch2.setImageResource(R.mipmap.img_jog_off);
//                    llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
//                    tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_gray));
//
//                }
                double temp = device2.getTemp();
                double hum = device2.getHum();
                String s = "" + String.format("%.1f", temp);
                String s2 = "" + String.format("%.1f", hum);
                tvTem.setText(s);
                tvHum.setText(s2);
            } else {
                imgSwitch2.setImageResource(R.mipmap.img_jog_off);
                llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
                tv7.setBackgroundResource(R.drawable.bg_fill5_green);
                tv_offline2.setVisibility(View.VISIBLE);
                tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_gray));
            }
        }
    }

    CountTimer countTimer = new CountTimer(2000, 1000);

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 667;
    }

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
            popupmenuWindow3();
        }

        @Override
        public void onFinish() {
            if (popupWindow2 != null && popupWindow2.isShowing()) {
                popupWindow2.dismiss();
            }
        }
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private PopupWindow popupWindow2;

    public void popupmenuWindow3() {
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.progress, null);
        TextView tv_load = view.findViewById(R.id.tv_load);
        if (refresh == 1) {
            tv_load.setText("数据查询中!");
        } else {
            tv_load.setText("加载中...");
        }
//        tv_load.setTextColor(getResources().getColor(R.color.white));

        popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //添加弹出、弹入的动画
        popupWindow2.setAnimationStyle(R.style.Popupwindow);
        popupWindow2.setFocusable(false);
        popupWindow2.setOutsideTouchable(false);
        backgroundAlpha(0.5f);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow2.showAsDropDown(et_wifi, 0, -20);
        popupWindow2.showAtLocation(tv1, Gravity.CENTER, 0, 0);
        //添加按键事件监听

    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean running2 = ServiceUtils.isServiceRunning(this, "com.peihou.willgood.service.MQService");
        if (!running2) {
            Intent intent = new Intent(this, MQService.class);
            intent.putExtra("restart", 1);
            startService(intent);
        }
        device = deviceDao.findDeviceByType(1);
        device2 = deviceDao.findDeviceByType(2);
        if (device != null && device2 != null) {
            UtilsJPush.stopJpush(this);
        } else {
            UtilsJPush.resumeJpush(this);
        }
        if (device != null) {
            device.setOnline(false);
            deviceDao.update(device);
            setMode(1);
        }else {
            setMode2(1);
        }
        if (device2 != null) {
            device2.setOnline(false);
            deviceDao.update(device2);
            setMode(2);
        }else {
            setMode2(2);
        }
        if (mqService != null) {
            if (device != null) {
                String deviceMac = device.getDeviceOnlyMac();
                mqService.connectMqtt(deviceMac);
            }
            if (device2 != null) {
                String deviceMac = device2.getDeviceOnlyMac();
                mqService.connectMqtt(deviceMac);
            }
        }
        running = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private int click = -1;
    private int click2 = -1;
    int onSwitch = -1;
    int onSwitch2 = -1;

    @OnClick({R.id.img_exit, R.id.img_add, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.ll_switch1, R.id.img_refresh1, R.id.ll_switch2, R.id.tv7, R.id.img_refresh2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                exitLoginDialog();
                break;
            case R.id.img_add:
                Intent intent = new Intent(this, DeviceListActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("device",device);
                intent.putExtra("device2",device2);
                startActivityForResult(intent,101);
                break;
            case R.id.tv1:
                if (device != null) {
                    refresh = 0;
                    if (device.getOnline()) {
                        Intent timerIntent = new Intent(this, TimerTaskActivity.class);
                        timerIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                        startActivity(timerIntent);
                    } else {
                        ToastUtil.show(this, "设备已离线", 0);
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }
                    }

                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv2:
                if (device != null) {
                    refresh = 0;
                    if (device.getOnline()) {
                        Intent linkIntent = new Intent(this, LinkedControlActivity.class);
                        linkIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                        linkIntent.putExtra("online", true);
                        startActivity(linkIntent);
                    } else {
                        ToastUtil.show(this, "设备已离线", 0);
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }

                    }


                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv3:
                if (device != null) {
                    refresh = 0;
                    if (device.getOnline()) {
                        Intent switchIntent = new Intent(this, SwichCheckActivity.class);
                        switchIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                        switchIntent.putExtra("online", true);
                        startActivity(switchIntent);
                    } else {
                        ToastUtil.show(this, "设备已离线", 0);
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }
                    }

                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.tv4:
                if (device != null) {
                    refresh = 0;
                    if (device.getOnline()) {
                        Intent alermIntent = new Intent(this, AlermActivity.class);
                        alermIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                        alermIntent.putExtra("online", true);
                        startActivity(alermIntent);
                    } else {
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }
                        ToastUtil.show(this, "设备已离线", 0);
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.tv5:
                if (device != null) {
                    refresh = 0;
                    Intent locationIntent = new Intent(this, LocationActivity.class);
                    locationIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                    startActivity(locationIntent);

                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv6:
                if (device != null) {
                    refresh = 0;
                    Intent intent2 = new Intent(this, PowerLostMomoryActivity.class);
                    intent2.putExtra("plMemory", device.getPlMemory());
                    intent2.putExtra("deviceMac", device.getDeviceOnlyMac());
                    intent2.putExtra("type", 1);

                    startActivityForResult(intent2, 1000);

                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.ll_switch1:
                if (device != null) {
                    refresh = 0;
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        ToastUtil.showShort(this, "请稍后...");
                        break;
                    }
                    if (!device.getOnline()) {
                        if (mqService != null) {
                            String topicName = "qjjc/gateway/" + device.getDeviceOnlyMac() + "/server_to_client";
                            mqService.getData(topicName, 0x11);
                        }
                        countTimer.start();
                        ToastUtil.show(this, "设备已离线", 0);
                        break;
                    }
                    String topicName = "qjjc/gateway/" + device.getDeviceOnlyMac() + "/server_to_client";
                    if (isOpen1) {
                        device.setDeviceState(0);
                        int[] preLines = TenTwoUtil.changeToTwo(device.getPrelineswitch());
                        preLines[0] = 0;
                        onSwitch = 0;
                        int preLineSwitch = TenTwoUtil.changeToTen2(preLines);
                        device.setPrelineswitch(preLineSwitch);
                        device.setPrelinesjog(0);
                        device.setLastlinesjog(0);
                        mqService.sendBasic(topicName, device);
                        click = 1;
                        countTimer.start();
                    } else {
                        device.setDeviceState(1);
                        int[] preLines = TenTwoUtil.changeToTwo(device.getPrelineswitch());
                        preLines[0] = 1;
                        onSwitch = 1;

                        int preLineSwitch = TenTwoUtil.changeToTen2(preLines);
                        device.setPrelineswitch(preLineSwitch);
                        device.setPrelinesjog(0);
                        device.setLastlinesjog(0);
                        mqService.sendBasic(topicName, device);
                        click = 1;
                        countTimer.start();
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.img_refresh1:
                if (device != null) {
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        ToastUtil.show(this, "请稍后...", 0);
                    } else {
//                        imgRefresh1.startAnimation(rotateAnimation);
                        if (mqService != null) {
                            String topicName = "qjjc/gateway/" + device.getDeviceOnlyMac() + "/server_to_client";
                            mqService.getData(topicName, 0x11);
                            refresh = 1;
                            countTimer.start();
                        }
                        break;
                    }
                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.ll_switch2:
                if (device2 != null) {
                    refresh = 0;
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        ToastUtil.showShort(this, "请稍后...");
                        break;
                    }
                    if (!device2.getOnline()) {
                        if (mqService != null) {
                            String topicName = "qjjc/gateway/" + device2.getDeviceOnlyMac() + "/server_to_client";
                            mqService.getData(topicName, 0x11);
                        }
                        countTimer.start();

                        ToastUtil.show(this, "设备已离线", 0);
                        break;
                    }
                    if (isOpen2) {
                        int[] preLines = TenTwoUtil.changeToTwo(device2.getPrelinesjog());
                        preLines[0] = 0;
                        onSwitch2 = 1;
                        int preLineSwitch = TenTwoUtil.changeToTen2(preLines);
                        device2.setPrelinesjog(preLineSwitch);
                    } else {
                        int[] preLines = TenTwoUtil.changeToTwo(device2.getPrelinesjog());
                        preLines[0] = 1;
                        onSwitch2 = 1;
                        int preLineSwitch = TenTwoUtil.changeToTen2(preLines);
                        device2.setPrelinesjog(preLineSwitch);
                    }
                    if (mqService != null) {
                        String topicName = "qjjc/gateway/" + device2.getDeviceOnlyMac() + "/server_to_client";
                        mqService.sendBasic(topicName, device2);
                        click2 = 1;
                        countTimer.start();
                    }

                } else {
                    ToastUtil.show(this, "请添加控制系统设备", 0);
                }
                break;
            case R.id.tv7:
                if (device2 != null) {
                    refresh = 0;
                    Intent intent2 = new Intent(this, PowerLostMomoryActivity.class);
                    intent2.putExtra("plMemory", device2.getPlMemory());
                    intent2.putExtra("deviceMac", device2.getDeviceOnlyMac());
                    intent2.putExtra("type", 2);
                    startActivityForResult(intent2, 1001);
                } else
                    ToastUtil.show(this, "请添加控制系统设备", 0);
                break;
            case R.id.img_refresh2:
                if (device2 != null) {
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        ToastUtil.show(this, "请稍后...", 0);
                        break;
                    } else {
//                        imgRefresh2.startAnimation(rotateAnimation);
                        if (mqService != null) {
                            String topicName = "qjjc/gateway/" + device2.getDeviceOnlyMac() + "/server_to_client";
                            mqService.getData(topicName, 0x11);
                            refresh = 1;
                            countTimer.start();
                            break;
                        }
                    }
                } else {
                    ToastUtil.show(this, "请添加控制系统设备", 0);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (data != null && data.hasExtra("click")) {
                int click = data.getIntExtra("click", 0);
                if (click == 1) {
                    if (resultCode == 1000 && device != null) {
                        int plMemory = data.getIntExtra("plMemory", 0);
                        device.setPlMemory(plMemory);
                        deviceDao.update(device);
                        setMode(1);
                    } else if (resultCode == 1001 && device2 != null) {
                        int plMemory = data.getIntExtra("plMemory", 0);
                        device2.setPlMemory(plMemory);
                        deviceDao.update(device2);
                        setMode(2);
                    }
                }
            }

    }

    MQService mqService;
    boolean bind = false;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            List<Device> devices = new ArrayList<>();

            if (device != null) {
                devices.add(device);
            }
            if (device2 != null) {
                devices.add(device2);
            }
            if (mqService != null) {
                mqService.stopMedia();
                mqService.unsubscribeAll(deviceDao.findAllDevice());
            }
            if (mqService != null && !devices.isEmpty()) {
                mqService.subscribeAll(devices);
                for (Device device : devices) {
                    String deviceMac = device.getDeviceOnlyMac();
//                    mqService.addCountTimer(deviceMac);
                    String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    mqService.getData(topicName, 0x11);
//                    mqService.addCountTimer(deviceMac);
                }
                countTimer.start();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    MessageReceiver messageReceiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if ("offline".equals(action)) {
                    if (intent.hasExtra("all")) {
                        if (device != null) {
                            device.setOnline(false);
                            device.setTemp(0);
                            device.setHum(0);
                            device.setCurrent(0);
                            device.setVotage(0);
                            deviceDao.update(device);
                            setMode(1);
                        }
                        if (device2 != null) {
                            device2.setOnline(false);
                            device2.setTemp(0);
                            device2.setHum(0);
                            deviceDao.update(device2);
                            setMode(2);
                        }
                    }
                    if (mqService!=null){
                        mqService.stopMedia();
                    }
                    onSwitch=-1;
                    onSwitch2=-1;
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        popupWindow2.dismiss();
                    }
                } else {
                    String macAddress = intent.getStringExtra("macAddress");
                    if (device != null && macAddress.equals(device.getDeviceOnlyMac())) {

                        Device device3 = (Device) intent.getSerializableExtra("device");
                        device = device3;
                        int firstLineSwitch = intent.getIntExtra("firstLineSwitch", -1);
                        if (device3.getPrelineswitch() >= 128) {
                            isOpen1 = true;
                            setMode(1);
                        } else if (device3.getPrelineswitch() < 128) {
                            isOpen1 = false;
                            setMode(1);
                        }
                        if (mqService != null && onSwitch == 1) {
                            mqService.starSpeech(macAddress, 0);
                            onSwitch = -1;
                        } else if (mqService != null && onSwitch == 0) {
                            mqService.starSpeech(macAddress, 1);
                            onSwitch = -1;
                        }
                        if (popupWindow2 != null && popupWindow2.isShowing()) {
                            popupWindow2.dismiss();
                        }
                    } else if (device2 != null && macAddress.equals(device2.getDeviceOnlyMac())) {

                        Device device3 = (Device) intent.getSerializableExtra("device");
                        device2 = device3;
                        int firstLineSwitch = intent.getIntExtra("firstLineSwitch", -1);
                        if (device3.getPrelinesjog() >= 128) {
                            isOpen2 = true;
                        } else if (device3.getPrelinesjog() < 128) {
                            isOpen2 = false;
                        }

                        setMode(2);
                        if (mqService != null && onSwitch2 == 1) {
                            mqService.starSpeech(macAddress, 2);
                            onSwitch2 = -1;
                        }
                        if (popupWindow2 != null && popupWindow2.isShowing()) {
                            popupWindow2.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static boolean running = false;
    int stop = 0;

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        click = 0;
        stop = 1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            popupWindow2.dismiss();
        }

        if (reveiver != null) {
            unregisterReceiver(reveiver);
        }
        if (bind) {
            unbindService(connection);
        }
        if (messageReceiver != null) {
            unregisterReceiver(messageReceiver);
        }
    }

    private static final int REQUEST_OVERLAY = 4444;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                MyApplication.floating = 0;
                changeDialog();
            } else {
                MyApplication.floating = 1;
            }
        }
    }

    ChangeDialog dialog;

    private void changeDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);


        dialog.setMode(1);
        dialog.setTitle("权限申请");
        dialog.setTips("请打开悬浮窗权限!");

        backgroundAlpha(0.4f);
        dialog.setOnNegativeClickListener(new ChangeDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new ChangeDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_OVERLAY);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        dialog.show();
    }
}
