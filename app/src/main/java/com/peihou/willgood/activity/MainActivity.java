package com.peihou.willgood.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.devicelist.AlermActivity;
import com.peihou.willgood.devicelist.DeviceListActivity;
import com.peihou.willgood.devicelist.LinkedControlActivity;
import com.peihou.willgood.devicelist.LocationActivity;
import com.peihou.willgood.devicelist.PowerLostMomoryActivity;
import com.peihou.willgood.devicelist.SwichCheckActivity;
import com.peihou.willgood.devicelist.TimerTaskActivity;
import com.peihou.willgood.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


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

    @Override
    public void initParms(Bundle parms) {
        String s = parms.getString("SSS");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {

    }

    List<String> list = new ArrayList<>();

    @Override
    public void doBusiness(Context mContext) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (getType() != 1) {
            tv1.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv2.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv3.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv4.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv5.setBackgroundResource(R.drawable.bg_fill5_gray);
            tv6.setBackgroundResource(R.drawable.bg_fill5_gray);
            imgRefresh1.setImageResource(R.mipmap.ic_refresh2);
            tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_gray));
            llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
            if (isOpen1)
                imgSwitch1.setImageResource(R.mipmap.ic_main_close2);
            else
                imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
        } else {
            tv1.setBackgroundResource(R.drawable.bg_fill5_green);
            tv2.setBackgroundResource(R.drawable.bg_fill5_green);
            tv3.setBackgroundResource(R.drawable.bg_fill5_green);
            tv4.setBackgroundResource(R.drawable.bg_fill5_green);
            tv5.setBackgroundResource(R.drawable.bg_fill5_green);
            tv6.setBackgroundResource(R.drawable.bg_fill5_green);
            imgRefresh1.setImageResource(R.mipmap.ic_refresh1);
            tvSwitch1.setTextColor(this.getResources().getColor(R.color.main_green));
            llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch1);
            if (isOpen1)
                imgSwitch1.setImageResource(R.mipmap.ic_main_close1);
            else
                imgSwitch1.setImageResource(R.mipmap.ic_main_open1);


        }
        if (getType() != 2) {
            tv7.setBackgroundResource(R.drawable.bg_fill5_gray);
            imgTem.setImageResource(R.mipmap.ic_tem2);
            imgHum.setImageResource(R.mipmap.ic_hum2);
            tvTem.setTextColor(this.getResources().getColor(R.color.main_gray));
            tvHum.setTextColor(this.getResources().getColor(R.color.main_gray));
            imgRefresh2.setImageResource(R.mipmap.ic_refresh2);
            tvSwitch2.setTextColor(this.getResources().getColor(R.color.main_gray));
            llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
            if (isOpen2)
                imgSwitch2.setImageResource(R.mipmap.ic_main_close2);
            else
                imgSwitch2.setImageResource(R.mipmap.ic_main_open2);
        } else {
            tv7.setBackgroundResource(R.drawable.bg_fill5_green);
            imgTem.setImageResource(R.mipmap.ic_tem1);
            imgHum.setImageResource(R.mipmap.ic_hum1);
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

    @OnClick({R.id.img_exit, R.id.img_add, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.ll_switch1, R.id.img_refresh1, R.id.ll_switch2, R.id.tv7, R.id.img_refresh2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                startActivity(LoginActivity.class);
                break;
            case R.id.img_add:
                startActivity(DeviceListActivity.class);
                break;
            case R.id.tv1:
                startActivity(TimerTaskActivity.class);
                break;
            case R.id.tv2:
                startActivity(LinkedControlActivity.class);
                break;
            case R.id.tv3:
                startActivity(SwichCheckActivity.class);
                break;
            case R.id.tv4:
                startActivity(AlermActivity.class);
                break;
            case R.id.tv5:
                startActivity(LocationActivity.class);
                break;
            case R.id.tv6:
                startActivity(PowerLostMomoryActivity.class);
                break;
            case R.id.ll_switch1:
                if (getType() != 1) {
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch2);
                    if (isOpen1)
                        imgSwitch1.setImageResource(R.mipmap.ic_main_close2);
                    else
                        imgSwitch1.setImageResource(R.mipmap.ic_main_open2);
                } else {
                    llSwitch1.setBackgroundResource(R.mipmap.bg_main_switch1);
                    if (isOpen1)
                        imgSwitch1.setImageResource(R.mipmap.ic_main_close1);
                    else
                        imgSwitch1.setImageResource(R.mipmap.ic_main_open1);
                }
                isOpen1 = !isOpen1;
                break;
            case R.id.img_refresh1:
                break;
            case R.id.ll_switch2:
                if (getType() != 2) {
                    llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch2);
                    if (isOpen2)
                        imgSwitch2.setImageResource(R.mipmap.ic_main_close2);
                    else
                        imgSwitch2.setImageResource(R.mipmap.ic_main_open2);
                } else {
                    llSwitch2.setBackgroundResource(R.mipmap.bg_main_switch1);
                    if (isOpen1)
                        imgSwitch2.setImageResource(R.mipmap.ic_main_close1);
                    else
                        imgSwitch2.setImageResource(R.mipmap.ic_main_open1);
                }
                isOpen2 = !isOpen2;
                break;
            case R.id.tv7:
                break;
            case R.id.img_refresh2:
                break;
        }
    }
}
