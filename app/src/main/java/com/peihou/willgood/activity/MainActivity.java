package com.peihou.willgood.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.base.MyApplication;
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
import com.peihou.willgood.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;

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

    Device device;

    String deviceMac;
    @Override
    public void initView(View view) {
        application= (MyApplication) getApplication();
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        List<Device> devices=deviceDao.findAllDevice();
        for(Device device2:devices){
            device=device2;
            type=device.getSystem();
            break;
        }
        if (device!=null){
            deviceMac=device.getDeviceOnlyMac();
            setMode();
        }
    }

    List<String> list = new ArrayList<>();

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onBackPressed() {
        List<Activity> activities=application.getActivities();
        List<Activity> list=new ArrayList<>();
        for (Activity activity:activities){
            if (!(activity instanceof LoginActivity)) {
                list.add(activity);
                Log.i("Activity22222","-->LoginActivity");
            }
            if (!(activity instanceof MainActivity)) {
                list.add(activity);
                Log.i("Activity22222","-->MainActivity");
            }
        }
        application.removeActiviies(list);
        Intent intent=new Intent(this,LoginActivity.class);
        intent.putExtra("exit",1);
        startActivity(intent);
    }


    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // TODO: add setContentView(...) invocation
//        ButterKnife.bind(this);
//    }


    int type=-1;//0为配电系统 1为配电系统
    private void setMode(){
        if (type==1) {
            if (isOpen1){
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

            }else {
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
            }
        } else if (type==2){
            imgTem.setImageResource(R.mipmap.ic_tem2);
            imgHum.setImageResource(R.mipmap.ic_hum2);
            tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
            imgTem.setImageResource(R.mipmap.ic_tem2);
            imgHum.setImageResource(R.mipmap.ic_hum2);
            tvTem.setTextColor(this.getResources().getColor(R.color.main_black));
            tvHum.setTextColor(this.getResources().getColor(R.color.main_black));
            imgRefresh2.setImageResource(R.mipmap.ic_refresh1);
            tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_green));
            llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch1);
            if (isOpen2)
                imgSwitch2.setImageResource(R.mipmap.ic_main_close1);
            else
                imgSwitch2.setImageResource(R.mipmap.ic_main_open1);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        setMode();
    }

    @OnClick({R.id.img_exit, R.id.img_add, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.ll_switch1, R.id.img_refresh1, R.id.ll_switch2, R.id.tv7, R.id.img_refresh2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                Intent intent=new Intent(this,LoginActivity.class);
                intent.putExtra("exit",1);
                startActivity(intent);
                break;
            case R.id.img_add:
                startActivity(DeviceListActivity.class);
                break;
            case R.id.tv1:
                if (type==1){
                    if (isOpen1){
                        Intent timerIntent=new Intent(this,TimerTaskActivity.class);
                        timerIntent.putExtra("deviceMac",deviceMac);
                        startActivity(timerIntent);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                        break;
                    }
                }else {
                    ToastUtil.show(this,"请添加配电系统设备",0);
                }
                break;
            case R.id.tv2:
                if (type==1){
                    if (isOpen1){
                        Intent linkIntent=new Intent(this,LinkedControlActivity.class);
                        linkIntent.putExtra("deviceMac",deviceMac);
                        startActivity(linkIntent);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                    }

                }else {
                    ToastUtil.show(this,"请添加配电系统设备",0);
                }
                break;
            case R.id.tv3:
                if (type==1){
                    if (isOpen1){
                        Intent switchIntent=new Intent(this,SwichCheckActivity.class);
                        switchIntent.putExtra("deviceMac",deviceMac);
                        startActivity(switchIntent);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                        break;
                    }
                }
                else
                    ToastUtil.show(this,"请添加配电系统设备",0);
                break;
            case R.id.tv4:
                if (type==1){
                    if (isOpen1){
                        Intent alermIntent=new Intent(this,AlermActivity.class);
                        alermIntent.putExtra("deviceMac",deviceMac);
                        startActivity(alermIntent);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                    }
                }
                else
                    ToastUtil.show(this,"请添加配电系统设备",0);
                break;
            case R.id.tv5:
                if (type==1){
                    if (isOpen1){
                        startActivity(LocationActivity.class);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                    }
                }else{
                    ToastUtil.show(this,"请添加配电系统设备",0);
                }
                break;
            case R.id.tv6:
                if (type==1){
                    if (isOpen1){
                        startActivity(PowerLostMomoryActivity.class);
                    }else {
                        ToastUtil.show(this,"请打开配电系统",0);
                    }
                }
                else
                    ToastUtil.show(this,"请添加配电系统设备",0);
                break;
            case R.id.ll_switch1:
                if (type ==1) {
                    if (isOpen1){
                        isOpen1=false;
                        setMode();
                    }else {
                        isOpen1=true;
                        setMode();
                    }
                }else
                    ToastUtil.show(this,"请添加配电系统设备",0);
                break;
            case R.id.img_refresh1:
                break;
            case R.id.ll_switch2:
                if (type==2) {
                    if (isOpen2){
                        isOpen2=false;
                        setMode();
                    }
                    else{
                        isOpen2=true;
                        setMode();
                    }
                }else {
                    ToastUtil.show(this,"请添加控制系统设备",0);
                }
                break;
            case R.id.tv7:
                if (type==2)
                    startActivity(PowerLostMomoryActivity.class);
                else
                    ToastUtil.show(this,"请添加控制系统设备",0);
                break;
            case R.id.img_refresh2:
                break;
        }
    }
}
