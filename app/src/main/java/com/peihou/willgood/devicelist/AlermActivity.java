package com.peihou.willgood.devicelist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.pojo.Alerm;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报警设置
 */
public class AlermActivity extends BaseActivity {


    @BindView(R.id.list_alerm)
    RecyclerView list_alerm;//报警视图列表
    private List<Alerm> list=new ArrayList<>();
    @BindView(R.id.btn_close) TextView btn_close;//关闭语音播报
    @BindView(R.id.btn_loop) TextView btn_loop;//循环语音播报
    @BindView(R.id.btn_thr) TextView btn_thr;//三次语音播报
    @BindView(R.id.img_switch) ImageView img_switch;//语音播报提示
    private MyAdapter adapter;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_alerm;
    }

    @Override
    public void initView(View view) {
        list.add(new Alerm("来电报警",0,true));
        list.add(new Alerm("断电报警",1,true));
        list.add(new Alerm("温度报警",2,true));
        list.add(new Alerm("湿度报警",3,true));
        list.add(new Alerm("电压报警",4,false));
        list.add(new Alerm("电流报警",5,true));
        list.add(new Alerm("功率报警",6,true));
        list.add(new Alerm("开关量报警",7,true));
        list_alerm.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MyAdapter(this,list);
        list_alerm.setAdapter(adapter);
    }

    int notify=0;//0为不提醒语音播报,1为提醒语音播报
    @OnClick({R.id.img_back,R.id.btn_close,R.id.btn_loop,R.id.btn_thr,R.id.img_switch,R.id.img_log})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_close:
                if (once==0){
                    break;
                }
                once=0;
                setVoiceMode();
                break;
            case R.id.btn_loop:
                if (once==1){
                    break;
                }
                once=1;
                setVoiceMode();
                break;
            case R.id.btn_thr:
                if (once==2){
                    break;
                }
                once=2;
                setVoiceMode();
                break;
            case R.id.img_switch:
                if (notify==0){
                    notify=1;
                    img_switch.setImageResource(R.mipmap.img_open);
                }else {
                    notify=0;
                    img_switch.setImageResource(R.mipmap.img_close);
                }
                break;
            case R.id.img_log:
                startActivity(AlermLogActivity.class);
                break;
        }
    }
    int once=2;//2时，为三次，1时为循环，0时是0次
    private void setVoiceMode(){
        if (once==0){
            btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_close.setTextColor(Color.parseColor("#ffffff"));
            btn_loop.setTextColor(Color.parseColor("#939393"));
            btn_thr.setTextColor(Color.parseColor("#939393"));
        }else if (once==1){
            btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
            btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_close.setTextColor(Color.parseColor("#939393"));
            btn_loop.setTextColor(Color.parseColor("#ffffff"));
            btn_thr.setTextColor(Color.parseColor("#939393"));
        }else if (once==2){
            btn_close.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_voice_unchecked));
            btn_thr.setBackground(getResources().getDrawable(R.drawable.shape_voice_checked));
            btn_close.setTextColor(Color.parseColor("#939393"));
            btn_loop.setTextColor(Color.parseColor("#939393"));
            btn_thr.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private List<Alerm> list;

        public MyAdapter(Context context, List<Alerm> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_alerm,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Alerm alerm=list.get(position);
            String name=alerm.getName();
            boolean open=alerm.isOpen();
            holder.tv_name.setText(name);
            if (open){
                holder.img_open.setImageResource(R.mipmap.img_open);
            }else {
                holder.img_open.setImageResource(R.mipmap.img_close);
            }
            holder.img_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alerm alerm2=list.get(position);
                    if (alerm2.isOpen()){
                        alerm2.setOpen(false);
                    }else {
                        alerm2.setOpen(true);
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.img_open) ImageView img_open;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
