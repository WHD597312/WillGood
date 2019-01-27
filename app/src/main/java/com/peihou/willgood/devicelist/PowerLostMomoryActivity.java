package com.peihou.willgood.devicelist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 掉电记忆
 */
public class PowerLostMomoryActivity extends BaseActivity {

    @BindView(R.id.img_open)
    ImageView img_open;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_power_lost_momory;
    }

    @Override
    public void initView(View view) {

    }
    int open=1;//1为打开掉电记忆，0为关闭掉电记忆
    @OnClick({R.id.img_back,R.id.img_open})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_open:
                if (open==1){
                    open=0;
                    img_open.setImageResource(R.mipmap.img_close);
                }else {
                    open=1;
                    img_open.setImageResource(R.mipmap.img_open);
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
