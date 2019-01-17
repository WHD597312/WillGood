package com.peihou.willgood.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;

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


    boolean isOpen1=false,isOpen2=false;

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
//
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R.id.img_exit, R.id.img_add, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.ll_switch1, R.id.img_refresh1, R.id.ll_switch2, R.id.tv7, R.id.img_refresh2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                break;
            case R.id.img_add:
                break;
            case R.id.tv1:
                break;
            case R.id.tv2:
                break;
            case R.id.tv3:
                break;
            case R.id.tv4:
                break;
            case R.id.tv5:
                break;
            case R.id.tv6:
                break;
            case R.id.ll_switch1:
                if(getType()!=1){
                    if(isOpen1==false){
                        llSwitch1.setBackgroundResource(R.mipmap.bg_main_open2);
                    }else
                        llSwitch1.setBackgroundResource(R.mipmap.bg_main_close2);
                }else {
                    if(isOpen1==false)
                        llSwitch1.setBackgroundResource(R.mipmap.bg_main_open1);
                    else
                        llSwitch1.setBackgroundResource(R.mipmap.bg_main_close1);
                }
                isOpen1=!isOpen1;
                break;
            case R.id.img_refresh1:
                break;
            case R.id.ll_switch2:
                if(getType()!=2){
                    if(isOpen2==false){
                        llSwitch2.setBackgroundResource(R.mipmap.bg_main_open2);
                    }else
                        llSwitch2.setBackgroundResource(R.mipmap.bg_main_close2);
                }else {
                    if(isOpen2==false)
                        llSwitch2.setBackgroundResource(R.mipmap.bg_main_open1);
                    else
                        llSwitch2.setBackgroundResource(R.mipmap.bg_main_close1);
                }
                isOpen2=!isOpen2;
                break;
            case R.id.tv7:
                break;
            case R.id.img_refresh2:
                break;
        }
    }
}
