package com.peihou.willgood.devicelist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;


import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.custom.ChangeDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class LocationSetActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;//页面标题
    @BindView(R.id.tv_default_time)
    TextView tv_default_time;//地图默认刷新频率
    @BindView(R.id.tv_jog_auto)
    TextView tv_jog_auto;//清除运动轨迹
    @BindView(R.id.cb)
    CheckBox cb;
    @BindView(R.id.tv_1)
    TextView tv_1;
    @BindView(R.id.cb2)
    CheckBox cb2;
    @BindView(R.id.tv_2)
    TextView tv_2;
    @BindView(R.id.cb3)
    CheckBox cb3;
    @BindView(R.id.tv_3)
    TextView tv_3;
    @BindView(R.id.cb4)
    CheckBox cb4;
    @BindView(R.id.tv_4)
    TextView tv_4;
    @BindView(R.id.cb5)
    CheckBox cb5;
    @BindView(R.id.tv_5)
    TextView tv_5;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_jog_set;
    }

    @Override
    public void initView(View view) {
        tv_title.setText("地图设置");
        tv_default_time.setText("刷新频率");
        tv_jog_auto.setText("清除运动轨迹");

    }

    boolean checked = false;
    int choices = 10;

    @OnClick({R.id.img_back, R.id.rl_bottom, R.id.btn_submit, R.id.cb, R.id.tv_1, R.id.cb2, R.id.tv_2, R.id.cb3, R.id.tv_3, R.id.cb4, R.id.tv_4, R.id.cb5, R.id.tv_5})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_bottom:
                changeDialog();
                break;
            case R.id.btn_submit:
                finish();
                break;
            case R.id.cb:
                checked = cb.isChecked();
                if (checked) {
                    choices = 10;
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.tv_1:
                checked = cb.isChecked();
                if (checked) {
                    choices = 10;
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.cb2:
                checked = cb2.isChecked();
                if (checked) {
                    choices = 20;
                    cb.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.tv_2:
                checked = cb2.isChecked();
                if (checked) {
                    cb.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    choices = 20;
                } else {
                    choices = 10;
                }
                break;
            case R.id.cb3:
                checked = cb3.isChecked();
                if (checked) {
                    choices = 30;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.tv_3:
                checked = cb3.isChecked();
                if (checked) {
                    choices = 30;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.cb4:
                checked = cb4.isChecked();
                if (checked) {
                    choices = 60;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.tv_4:
                checked = cb4.isChecked();
                if (checked) {
                    choices = 60;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb5.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.cb5:
                checked = cb5.isChecked();
                if (checked) {
                    choices = 120;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
            case R.id.tv_5:
                checked = cb5.isChecked();
                if (checked) {
                    choices = 120;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                } else {
                    choices = 10;
                }
                break;
        }
    }

    //自定义点动时间
    ChangeDialog dialog;

    private void changeDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTips("是否清除运动轨迹?");
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

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
