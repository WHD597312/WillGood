package com.peihou.willgood.login;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood.R;
import com.peihou.willgood.activity.MainActivity;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.util.DisplayUtil;
import com.peihou.willgood.util.Mobile;
import com.peihou.willgood.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.tv_register) TextView tv_register;//注册
    @BindView(R.id.tv_login) TextView tv_login;//登录
    @BindView(R.id.btn_login) Button btn_login;//登录按钮
    @BindView(R.id.btn_forpswd) TextView btn_forpswd;//忘记密码
    @BindView(R.id.img_xianshi) ImageView img_xianshi;//隐藏密码
    @BindView(R.id.et_phone) EditText et_phone;//编辑电话
    @BindView(R.id.et_pswd) EditText et_pswd;//编辑密码



    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_login;
    }

    public void initView(View view) {
        tv_login.setTextSize(22);
        tv_register.setTextSize(16);
        tv_login.getPaint().setFakeBoldText(true);
        tv_register.getPaint().setFakeBoldText(false);
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    int state=0;//界面的状态，当为0时为登录页面，为1时为注册页面
    int visible=0;//0为密码不可见，1为密码可见
    @OnClick({R.id.tv_login,R.id.tv_register,R.id.img_xianshi,R.id.btn_login})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_login:
                if (state==0)
                    break;
                state=0;
                setLoginModle();
                break;
            case R.id.tv_register:
                if (state==1)
                    break;
                state=1;
                setRegisterModle();
                break;
            case R.id.img_xianshi:
                if (state==0){
                    if (visible==0){
                        img_xianshi.setImageResource(R.mipmap.xianshi);
                        et_pswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//设置密码可见
                        visible=1;
                    }else if (visible==1){
                        img_xianshi.setImageResource(R.mipmap.yincang);
                        et_pswd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见
                        visible=0;
                    }
                }
                break;
            case R.id.btn_login:
                String phone=et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    ToastUtil.showShort(this,"请输入手机号码");
                    break;
                }else {
                    if (!Mobile.isMobile(phone)){
                        ToastUtil.showShort(this,"不合法的手机号码");
                        break;
                    }
                    startActivity(MainActivity.class);
                }
                break;
        }
    }


    /**
     * 处理登录界面
     */
    private void setLoginModle(){
        et_phone.setText("");
        et_pswd.setText("");
        btn_forpswd.setVisibility(View.VISIBLE);
        btn_login.setText("登录");
        tv_login.setTextSize(22);
        tv_register.setTextSize(16);
        img_xianshi.setVisibility(View.VISIBLE);
        tv_login.getPaint().setFakeBoldText(true);
        tv_register.getPaint().setFakeBoldText(false);
        img_xianshi.setImageResource(R.mipmap.yincang);
        et_pswd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见
        visible=0;
    }

    /**
     * 处理注册界面
     */
    private void setRegisterModle(){
        et_phone.setText("");
        et_pswd.setText("");
        btn_forpswd.setVisibility(View.GONE);
        btn_login.setText("注册");
        tv_login.getPaint().setFakeBoldText(false);
        tv_register.getPaint().setFakeBoldText(true);
        img_xianshi.setVisibility(View.GONE);
        tv_login.setTextSize(16);
        tv_register.setTextSize(22);
    }
}
