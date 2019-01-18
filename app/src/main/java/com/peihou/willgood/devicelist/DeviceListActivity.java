package com.peihou.willgood.devicelist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.util.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListActivity extends BaseActivity {


//    @BindView(R.id.view_main)
//    View viewMain;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.tv_ps)
    TextView tvPs;
    @BindView(R.id.image_right_ps)
    ImageView imageRightPs;
    @BindView(R.id.rl_ps)
    RelativeLayout rlPs;
    @BindView(R.id.tv_ks)
    TextView tvKs;
    @BindView(R.id.image_right_ks)
    ImageView imageRightKs;
    @BindView(R.id.rl_ks)
    RelativeLayout rlKs;
    @BindView(R.id.rl_body)
    RelativeLayout rlBody;


    private SharedPreferences sharedPreferences;
    /*
     * 保存手机里面的名字
     */private SharedPreferences.Editor editor;

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
        sharedPreferences = getSharedPreferences("my",
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        if(getType()==1){
            tvPs.setTextColor(Color.parseColor("#09c585"));
            imageRightPs.setImageResource(R.mipmap.right_arrow_green);
        }
        if(getType()==2){
            tvKs.setTextColor(Color.parseColor("#09c585"));
            imageRightKs.setImageResource(R.mipmap.right_arrow_green);
        }
    }

    @OnClick({R.id.img_back,R.id.rl_ps, R.id.rl_ks})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_ps:
                editor.putInt("type",1);
                editor.commit();
                finish();
                break;
            case R.id.rl_ks:
                editor.putInt("type",2);
                editor.commit();
                finish();
                break;
        }
    }
}
