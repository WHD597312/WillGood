package com.peihou.willgood.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peihou.willgood.R;
import com.peihou.willgood.activity.MainActivity;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood.pojo.UserInfo;
import com.peihou.willgood.receiver.UtilsJPush;
import com.peihou.willgood.util.Mobile;
import com.peihou.willgood.util.ToastUtil;
import com.peihou.willgood.util.http.BaseWeakAsyncTask;
import com.peihou.willgood.util.http.HttpUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {


    @BindView(R.id.tv_register) TextView tv_register;//注册
    @BindView(R.id.tv_login) TextView tv_login;//登录
    @BindView(R.id.btn_login) Button btn_login;//登录按钮
    @BindView(R.id.btn_forpswd) TextView btn_forpswd;//忘记密码
    @BindView(R.id.img_xianshi) ImageView img_xianshi;//隐藏密码
    @BindView(R.id.et_phone) EditText et_phone;//编辑电话
    @BindView(R.id.et_pswd) EditText et_pswd;//编辑密码
    DeviceDaoImpl deviceDao;


    private SharedPreferences sharedPreferences;
    int exit=0;
    @Override
    public void initParms(Bundle parms) {
        exit=parms.getInt("exit");
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
        sharedPreferences=getSharedPreferences("userInfo2",Context.MODE_PRIVATE);
        deviceDao=new DeviceDaoImpl(getApplicationContext());

        phone=sharedPreferences.getString("phone","");
        password=sharedPreferences.getString("password","");


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){
            try {
                if (exit==1){
                    sharedPreferences.edit().remove("password").commit();
                    password="";
                    UtilsJPush.stopJpush(this);
                }else if (exit==0){
                    int userId=sharedPreferences.getInt("userId",0);
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("login",1);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
//                    new LoginAsync(LoginActivity.this).execute(params);
//                    startActivity(DeviceListActivity.class);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        et_phone.setText(phone);
        et_pswd.setText(password);
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    int state=0;//界面的状态，当为0时为登录页面，为1时为注册页面
    int visible=0;//0为密码不可见，1为密码可见
    String phone;
    String password;
    Map<String,Object> params=new HashMap<>();
    int login=0;
    @OnClick({R.id.tv_login,R.id.tv_register,R.id.img_xianshi,R.id.btn_login,R.id.btn_forpswd})
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
                if (logining==1){
                    ToastUtil.showShort(this,"请稍等...");
                    break;
                }
                phone=et_phone.getText().toString();
                password=et_pswd.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    ToastUtil.showShort(this,"请输入手机号码");
                    break;
                }else {
                    if (!Mobile.isMobile(phone)){
                        ToastUtil.showShort(this,"不合法的手机号码");
                        break;
                    }
                }
                if (TextUtils.isEmpty(password)){
                    ToastUtil.showShort(this,"请输入密码");
                    break;
                }
                try {
                    if (!params.isEmpty())
                        params.clear();
                    if (state==0){
                        params.put("phone",phone);
                        params.put("password",password);
                        login=1;
                        UtilsJPush.resumeJpush(this);
                        new LoginAsync(LoginActivity.this).execute(params).get(3,TimeUnit.SECONDS);
                    } else if (state==1){
                        params.put("phone",phone);
                        params.put("registerType",0);
                        params.put("password",password);
                        new RegisterAsync(LoginActivity.this).execute(params).get(3,TimeUnit.SECONDS);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btn_forpswd:
                Intent intent=new Intent(this,ResetPswdActivity.class);
                startActivityForResult(intent,1001);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1001){
            et_phone.setText("");
            et_pswd.setText("");
        }
    }

    class RegisterAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,LoginActivity>{

        public RegisterAsync(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(LoginActivity activity,Map<String, Object>... maps) {
            logining=1;
            int code=0;
            String url=HttpUtils.ipAddress+"user/register";
            Map<String,Object> params=maps[0];
            String result=HttpUtils.requestPost(url,params);
            try {
                Log.i("result","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        deviceDao.deleteAll();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(LoginActivity activity,Integer code) {
            switch (code){
                case 100:
                   try {
                       params.clear();
                       params.put("phone",phone);
                       params.put("password",password);
                       new LoginAsync(LoginActivity.this).execute(params).get(3,TimeUnit.SECONDS);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                    break;
                case 10001:
                    logining=0;
                    ToastUtil.showShort(LoginActivity.this,"手机号已被注册");
                    break;
                    default:
                        logining=0;
                        ToastUtil.showShort(LoginActivity.this,"注册失败");
                        break;
            }
        }
    }
    private int logining=0;
    class LoginAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,LoginActivity> {

        public LoginAsync(LoginActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(LoginActivity activity,Map<String, Object>... maps) {
            int code=0;
            String url= HttpUtils.ipAddress+"user/login";
            logining=1;
            Map<String,Object> params2=maps[0];
            try {
                String result=HttpUtils.requestPost(url,params2);
                if (TextUtils.isEmpty(result)){
                    logining=0;
                    result=HttpUtils.requestPost(url,params2);
                }
                if (!TextUtils.isEmpty(result)){
                    Log.i("result","-->"+result);
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        String data=returnData.toString();
                        Gson gson=new Gson();
                        UserInfo userInfo=gson.fromJson(data,UserInfo.class);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        int userId=userInfo.getUserId();

                        String username=userInfo.getUsername();
                        String phone=userInfo.getPhone();
                        String password=userInfo.getPassword();
                        editor.putInt("userId",userId);
                        editor.putString("username",username);
                        editor.putString("phone",phone);
                        editor.putString("password",password);
                        Log.i("userId","-->"+userId);
                        JPushInterface.setAlias(LoginActivity.this,1,""+userId);
                        editor.commit();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(LoginActivity activity,Integer code) {
            switch (code){
                case 100:
                    try {
                        if (state==1){
                            ToastUtil.showShort(LoginActivity.this,"注册成功");
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            int userId=sharedPreferences.getInt("userId",0);
                            intent.putExtra("userId",userId);
                            startActivity(intent);
                        }else {
                            int userId=sharedPreferences.getInt("userId",0);
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("login",1);
                            intent.putExtra("userId",userId);
                            startActivity(intent);
//                            new LoadDeviceListAsync(LoginActivity.this).execute(params);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 10004:
                    logining=0;
                    ToastUtil.showShort(LoginActivity.this,"用户名或密码错误");
                    et_phone.setText("");
                    et_pswd.setText("");
                    break;
                    default:
                        if (login==1){
                            ToastUtil.showShort(LoginActivity.this,"登录失败");
                        }
                        break;
            }
        }
    }
    class IsExistUserAsync extends AsyncTask<Map<String,Object>,Void,Integer>{

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            String url=HttpUtils.ipAddress+"user/questPhoneIsExist";
            int code=0;
            Map<String,Object> params=maps[0];
            try {
                String result=HttpUtils.requestPost(url,params);
                Log.i("result","-->"+result);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 处理登录界面
     */
    private void setLoginModle(){
        et_phone.setText("");
        et_pswd.setText("");
        btn_forpswd.setVisibility(View.GONE);
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
