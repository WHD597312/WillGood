package com.peihou.willgood.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.peihou.willgood.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChangeDialog extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.et_name) EditText et_name;//编辑内容
    public ChangeDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change);
        unbinder=ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_cancel,R.id.btn_ensure})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_cancel:
                if (onNegativeClickListener!=null){
                    onNegativeClickListener.onNegativeClick();
                }
                break;
            case R.id.btn_ensure:
                if (onPositiveClickListener!=null){
                    content=et_name.getText().toString();
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;//为0时，为编辑内容 1为显示内容
    String content;//显示内容

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dialog","-->onStart");
        if (mode==0){
            et_name.setHint("编辑内容");
            et_name.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }else if (mode==1){
            et_name.setTextSize(20);
            et_name.setText("是否清除运动轨迹?");
            et_name.setFocusable(false);
            et_name.setFocusableInTouchMode(false);
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("dialog","-->onStop");
        unbinder.unbind();
    }

    private OnPositiveClickListener onPositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {


        this.onPositiveClickListener = onPositiveClickListener;
    }

    private OnNegativeClickListener onNegativeClickListener;

    public void setOnNegativeClickListener(OnNegativeClickListener onNegativeClickListener) {

        this.onNegativeClickListener = onNegativeClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }
}
