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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
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
import com.peihou.willgood.service.MQService;
import com.peihou.willgood.util.NoFastClickUtils;
import com.peihou.willgood.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;
import retrofit2.http.OPTIONS;

public class MainActivity extends BaseActivity {


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
    private SharedPreferences sharedPreferences;

    DeviceDaoImpl deviceDao;

    @Override
    public void initParms(Bundle parms) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    Animation rotateAnimation;
    Device device;

    String deviceMac;
    String topicName;//设备发送主题
    MQTTMessageReveiver reveiver;

    @Override
    public void initView(View view) {
        application = (MyApplication) getApplication();
        deviceDao = new DeviceDaoImpl(getApplicationContext());
        List<Device> devices = deviceDao.findAllDevice();

        reveiver = new MQTTMessageReveiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(reveiver, filter);
        messageReceiver = new MessageReceiver();
        IntentFilter filter2 = new IntentFilter("MainActivity");
        filter2.addAction("offline");
        registerReceiver(messageReceiver, filter2);

        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);
        for (Device device2 : devices) {
            device = device2;
            device.setOnline(false);
            deviceDao.update(device);
            type = device.getSystem();
            break;
        }
        if (device != null) {
            deviceMac = device.getDeviceOnlyMac();
            setMode();
            topicName = "qjjc/gateway/" + deviceMac + "server_to_server";
        }

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        rotateAnimation.setInterpolator(new LinearInterpolator());

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
                    mqService.clearCountTimer();
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
        backgroundAlpha(0.6f);
        exitLoginDialog.show();
    }

    private void setExitLoginPage() {
        List<Activity> activities = application.getActivities();
        List<Activity> list = new ArrayList<>();
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


    int type = -1;//0为配电系统 1为配电系统

    private void setMode() {
        if (type == 1) {

            if (device.getOnline()) {
                if (isOpen1) {
                    tv1.setBackgroundResource(R.drawable.bg_fill5_green);
                    tv2.setBackgroundResource(R.drawable.bg_fill5_green);
                    tv3.setBackgroundResource(R.drawable.bg_fill5_green);
                    tv4.setBackgroundResource(R.drawable.bg_fill5_green);
                    tv5.setBackgroundResource(R.drawable.bg_fill5_green);
                    tv6.setBackgroundResource(R.drawable.bg_fill5_green);
                    imgRefresh1.setImageResource(R.mipmap.ic_refresh1);
                    tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_green));
                    tvTem.setTextColor(this.getResources().getColor(R.color.main_gray));
                    tvHum.setTextColor(this.getResources().getColor(R.color.main_gray));
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch1);
                    imgSwitch1.setImageResource(R.mipmap.ic_main_close1);
                } else {
                    tv1.setBackgroundResource(R.drawable.bg_fill5_gray);
                    tv2.setBackgroundResource(R.drawable.bg_fill5_gray);
                    tv3.setBackgroundResource(R.drawable.bg_fill5_gray);
                    tv4.setBackgroundResource(R.drawable.bg_fill5_gray);
                    tv5.setBackgroundResource(R.drawable.bg_fill5_gray);
                    tv6.setBackgroundResource(R.drawable.bg_fill5_gray);
                    imgRefresh1.setImageResource(R.mipmap.ic_refresh1);
                    imgRefresh2.setImageResource(R.mipmap.ic_refresh2);
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch1);
                    imgSwitch1.setImageResource(R.mipmap.ic_main_open1);
                    tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_gray));
                }
                double temp = device.getTemp();
                double hum = device.getHum();
                String s = "" + String.format("%.1f", temp);
                String s2 = "" + String.format("%.1f", hum);
                tvTem.setText(s);
                tvHum.setText(s2);
            } else {
                imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
                llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
            }
        } else if (type == 2) {
            if (device.getOnline()) {
                imgTem.setImageResource(R.mipmap.ic_tem2);
                imgHum.setImageResource(R.mipmap.ic_hum2);
                imgTem.setImageResource(R.mipmap.ic_tem2);
                imgHum.setImageResource(R.mipmap.ic_hum2);
                tvTem.setTextColor(this.getResources().getColor(R.color.main_black));
                tvHum.setTextColor(this.getResources().getColor(R.color.main_black));
                imgRefresh2.setImageResource(R.mipmap.ic_refresh1);
                tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_green));
                llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch1);

                if (device.getPlMemory() == 1) {
                    tv7.setBackgroundResource(R.drawable.bg_fill5_green);
                } else {
                    tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
                }
                if (isOpen2)
                    imgSwitch2.setImageResource(R.mipmap.ic_main_close1);
                else
                    imgSwitch2.setImageResource(R.mipmap.ic_main_open1);
                double temp = device.getTemp();
                double hum = device.getHum();
                String s = "" + String.format("%.1f", temp);
                String s2 = "" + String.format("%.1f", hum);
                tvTem.setText(s);
                tvHum.setText(s2);
            } else {
                imgSwitch2.setImageResource(R.mipmap.ic_main_open2);
                llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
                tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
            }
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
        tv_load.setTextColor(getResources().getColor(R.color.white));
        if (popupWindow2==null)
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
        running = true;
    }

    @OnClick({R.id.img_exit, R.id.img_add, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.ll_switch1, R.id.img_refresh1, R.id.ll_switch2, R.id.tv7, R.id.img_refresh2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                exitLoginDialog();
                break;
            case R.id.img_add:
                startActivity(DeviceListActivity.class);
                break;
            case R.id.tv1:
                if (type == 1) {
                    if (isOpen1) {
                        if (device.getOnline()) {
                            Intent timerIntent = new Intent(this, TimerTaskActivity.class);
                            timerIntent.putExtra("deviceMac", deviceMac);
                            startActivity(timerIntent);
                        } else {
                            ToastUtil.show(this, "设备已离线", 0);
                            if (mqService != null) {
                                mqService.getData(topicName, 0x11);
                            }

                        }
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                        break;
                    }
                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv2:
                if (type == 1) {
                    if (isOpen1) {
                        if (device.getOnline()) {
                            Intent linkIntent = new Intent(this, LinkedControlActivity.class);
                            linkIntent.putExtra("deviceMac", deviceMac);
                            linkIntent.putExtra("online", true);
                            startActivity(linkIntent);
                        } else {
                            ToastUtil.show(this, "设备已离线", 0);
                            if (mqService != null) {
                                mqService.getData(topicName, 0x11);
                            }

                        }
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                    }

                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv3:
                if (type == 1) {
                    if (isOpen1) {
                        if (device.getOnline()) {
                            Intent switchIntent = new Intent(this, SwichCheckActivity.class);
                            switchIntent.putExtra("deviceMac", deviceMac);
                            switchIntent.putExtra("online", true);
                            startActivity(switchIntent);
                        } else {
                            ToastUtil.show(this, "设备已离线", 0);
                            if (mqService != null) {
                                mqService.getData(topicName, 0x11);
                            }
                        }
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }
                        break;
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.tv4:
                if (type == 1) {
                    if (isOpen1) {
                        if (device.getOnline()) {
                            Intent alermIntent = new Intent(this, AlermActivity.class);
                            alermIntent.putExtra("deviceMac", deviceMac);
                            alermIntent.putExtra("online", true);
                            startActivity(alermIntent);
                        } else {
                            if (mqService != null) {
                                mqService.getData(topicName, 0x11);
                            }
                            ToastUtil.show(this, "设备已离线", 0);
                        }
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.tv5:
                if (type == 1) {
                    if (isOpen1) {
                        Intent locationIntent = new Intent(this, LocationActivity.class);
                        locationIntent.putExtra("deviceMac", deviceMac);
                        startActivity(locationIntent);
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                    }
                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.tv6:
                if (type == 1) {
                    if (isOpen1) {
                        Intent intent2 = new Intent(this, PowerLostMomoryActivity.class);
                        intent2.putExtra("open", device.getPlMemory());
                        startActivityForResult(intent2, 1000);
                    } else {
                        ToastUtil.show(this, "请打开配电系统", 0);
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.ll_switch1:
                if (type == 1) {
                    if (isOpen1) {
                        isOpen1 = false;
                        setMode();
                    } else {
                        isOpen1 = true;
                        setMode();
                    }
                } else
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                break;
            case R.id.img_refresh1:
                if (type == 1) {
                    imgRefresh1.startAnimation(rotateAnimation);
                    if (mqService != null) {
                        mqService.getData(topicName, 0x11);
                    }
                } else {
                    ToastUtil.show(this, "请添加配电系统设备", 0);
                }
                break;
            case R.id.ll_switch2:
                if (type == 2) {
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        ToastUtil.show(this, "请稍后...", 0);
                    } else {
                        if (isOpen2) {
                            device.setPrelineswitch(255);
                            device.setLastlines(255);
                        } else {
                            device.setPrelineswitch(0);
                            device.setLastlines(0);
                        }
                        if (mqService != null) {
                            mqService.sendBasic(topicName, device);
                            countTimer.start();
                        }
                    }
                } else {
                    ToastUtil.show(this, "请添加控制系统设备", 0);
                }
                break;
            case R.id.tv7:
                if (type == 2) {
                    Intent intent2 = new Intent(this, PowerLostMomoryActivity.class);
                    intent2.putExtra("open", device.getPlMemory());
                    startActivityForResult(intent2, 1000);
                } else
                    ToastUtil.show(this, "请添加控制系统设备", 0);
                break;
            case R.id.img_refresh2:
                if (type == 2) {
                    if (NoFastClickUtils.isFastClick()) {
                        imgRefresh2.startAnimation(rotateAnimation);
                        if (mqService != null) {
                            mqService.getData(topicName, 0x11);
                        }
                    } else {
                        ToastUtil.show(this, "请稍后...", 0);
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
        if (resultCode == 1000 && device != null) {
            int open = data.getIntExtra("open", 0);
            device.setPlMemory(open);
            deviceDao.update(device);
            setMode();
        }
    }

    MQService mqService;
    boolean bind = false;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            List<Device> devices = deviceDao.findAllDevice();
            for (Device device : devices) {
                String deviceMac = device.getDeviceOnlyMac();
                String topicName = "qjjc/gateway/" + deviceMac + "server_to_server";
                mqService.getData(topicName, 0x11);
                mqService.addCountTimer(deviceMac);
            }
            countTimer.start();
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
                    String macAddress = intent.getStringExtra("macAddress");
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        device.setOnline(false);
                        device.setTemp(0);
                        device.setHum(0);
                        device.setCurrent(0);
                        device.setVotage(0);
                        deviceDao.update(device);
                        setMode();
                    }
                } else {
                    String macAddress = intent.getStringExtra("macAddress");
                    if (macAddress.equals(deviceMac)) {
                        Device device2 = (Device) intent.getSerializableExtra("device");
                        if (device2 != null) {
                            device = device2;
                            isOpen2 = !isOpen2;
                            setMode();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static boolean running = false;

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
