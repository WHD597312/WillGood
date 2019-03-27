package com.peihou.willgood.devicelist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.activity.MainActivity;
import com.peihou.willgood.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设备列表
 */
public class DeviceListActivity extends BaseActivity {

    @BindView(R.id.tv_ps) TextView tv_ps;//配电系统
    @BindView(R.id.image_right_ps) ImageView image_right_ps;//配电系统右键头
    @BindView(R.id.tv_ks) TextView tv_ks;//控电系统
    @BindView(R.id.image_right_ks) ImageView image_right_ks;//控电系统右键头
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_list;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ps==1){
            setPsMode();
        }else if (ps==2){
            setKsMode();
        }
    }

    int ps=0;//为0时，表示没有操作任何系统，为1是表示操作配电系统，为2时表示操作控电系统
    @OnClick({R.id.img_back,R.id.rl_ps,R.id.rl_ks})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_ps:
                ps=1;
                Intent intent=new Intent(this,QRScannerActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.rl_ks:
                ps=2;
                setKsMode();
                Intent intent2=new Intent(this,QRScannerActivity.class);
                intent2.putExtra("type",2);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 设置配电系统逻辑
     */

    private void setPsMode(){
        tv_ps.setTextColor(Color.parseColor("#09C585"));
        tv_ks.setTextColor(Color.parseColor("#4b4b4b"));
        image_right_ps.setImageResource(R.mipmap.right_arrow_green);
        image_right_ks.setImageResource(R.mipmap.right_arrow_black);
    }

    /**
     * 设置空点系统逻辑
     */
    private void setKsMode(){
        tv_ks.setTextColor(Color.parseColor("#09C585"));
        tv_ps.setTextColor(Color.parseColor("#4b4b4b"));
        image_right_ks.setImageResource(R.mipmap.right_arrow_green);
        image_right_ps.setImageResource(R.mipmap.right_arrow_black);
    }
}
