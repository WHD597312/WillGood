package com.peihou.willgood.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.peihou.willgood.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ExitLoginDialog extends Dialog {
    Unbinder unbinder;
    public ExitLoginDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit_login);
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
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dialog","-->onStart");
    }
    private int inputType;

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
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
