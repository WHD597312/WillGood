package com.peihou.willgood.devicelist;

import android.content.Context;
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
import com.peihou.willgood.pojo.TimerTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 定时任务
 */
public class TimerTaskActivity extends BaseActivity {


    @BindView(R.id.list_timer) RecyclerView list_timer;//定时任务
    List<TimerTask> timerTasks=new ArrayList<>();
    TimerTaskAdapter adapter;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_timer_task;
    }

    @Override
    public void initView(View view) {
        timerTasks.add(new TimerTask(false,"线路一","00:00"));
        timerTasks.add(new TimerTask(false,"线路二","01:00"));
        timerTasks.add(new TimerTask(false,"线路三","03:00"));
        list_timer.setLayoutManager(new LinearLayoutManager(this));
        adapter=new TimerTaskAdapter(this,timerTasks);
        list_timer.setAdapter(adapter);

    }

    @Override
    public void doBusiness(Context mContext) {

    }
    @OnClick({R.id.img_back,R.id.img_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_add:
                startActivity(AddTimeActivity.class);
                break;
        }
    }
    class TimerTaskAdapter extends RecyclerView.Adapter<MyViewHoler>{

        private Context context;
        private List<TimerTask> timerTasks;

        public TimerTaskAdapter(Context context, List<TimerTask> timerTasks) {
            this.context = context;
            this.timerTasks = timerTasks;
        }

        @NonNull
        @Override
        public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_timer,null);
            return new MyViewHoler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoler holder, final int position) {
            TimerTask timerTask=timerTasks.get(position);
            if (timerTask!=null){
                boolean isOpen=timerTask.isOpen();
                if (isOpen){
                    holder.img_open.setImageResource(R.mipmap.img_open);
                }else {
                    holder.img_open.setImageResource(R.mipmap.img_close);
                }
                String name=timerTask.getName();
                String timer=timerTask.getTimer();
                holder.tv_name.setText(""+name);
                holder.tv_timer.setText(""+timer);
                holder.img_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimerTask timerTask2=timerTasks.get(position);
                        if (timerTask2.isOpen()){
                            timerTask2.setOpen(false);
                        }else {
                            timerTask2.setOpen(true);
                        }
                        timerTasks.set(position,timerTask2);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return timerTasks.size();
        }
    }
    class MyViewHoler extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_timer) TextView tv_timer;
        @BindView(R.id.img_open) ImageView img_open;
        @BindView(R.id.tv_name) TextView tv_name;
        public MyViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
