package com.peihou.willgood.devicelist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 添加设备
 */
public class AddDeviceActivity extends BaseActivity {


    @BindView(R.id.et_name) EditText et_name;//编辑设备名称
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_add_device;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
    @OnClick({R.id.back,R.id.bt_add_finish,R.id.image_scan})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.image_scan:
                startActivity(QRScannerActivity.class);
                break;
            case R.id.bt_add_finish:
                String name=et_name.getText().toString();
                if (TextUtils.isEmpty(name)){
                    ToastUtil.show(this,"设备IMEI不能为空",Toast.LENGTH_SHORT);
                    break;
                }
                ToastUtil.show(this,"添加成功",Toast.LENGTH_SHORT);
                finish();
                break;
        }
    }
}
