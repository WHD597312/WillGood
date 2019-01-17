package com.peihou.willgood.devicelist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;

public class DeviceListActivity extends BaseActivity {


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
}
