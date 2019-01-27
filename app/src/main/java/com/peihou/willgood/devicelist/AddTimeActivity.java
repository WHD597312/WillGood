package com.peihou.willgood.devicelist;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood.pojo.Line;
import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.util.ToastUtil;
import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加定时任务
 */
public class AddTimeActivity extends BaseActivity {

    @BindView(R.id.grid_line) GridView grid_line;//线路控件
    @BindView(R.id.loop_hour) LoopView loop_hour;//小时控件
    @BindView(R.id.loop_min) LoopView loop_min;//分钟控件
    @BindView(R.id.loop_second) LoopView loop_second;//秒控件
    @BindView(R.id.tv_1) TextView tv_1;//周一
    @BindView(R.id.tv_2) TextView tv_2;//周2
    @BindView(R.id.tv_3) TextView tv_3;//周3
    @BindView(R.id.tv_4) TextView tv_4;//周4
    @BindView(R.id.tv_5) TextView tv_5;//周5
    @BindView(R.id.tv_6) TextView tv_6;//周6
    @BindView(R.id.tv_7) TextView tv_7;//周日
    @BindView(R.id.btn_only_one) TextView btn_only_one;//执行一次
    List<String> hours=new ArrayList<>();//小时集合
    List<String> mins=new ArrayList<>();//分钟集合
    List<String> seconds=new ArrayList<>();//秒集合

    MyAdapter adapter;
    List<Line> list=new ArrayList<>();//线路集合

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_add_time;
    }

    @Override
    public void initView(View view) {
        for (int i = 0; i <=23 ; i++) {
            hours.add(""+i);
        }
        for (int i = 0; i <=23 ; i++) {
            hours.add(""+i);
        }
        for (int i = 0; i <=11 ; i++) {
            hours.add(""+i);
        }
        for (int i = 0; i <=59 ; i++) {
            mins.add(""+i);
        }
        for (int i = 0; i <=59 ; i++) {
            seconds.add(""+i);
        }

        tv_1.setTag(1);
        tv_2.setTag(0);
        tv_3.setTag(0);
        tv_4.setTag(0);
        tv_5.setTag(0);
        tv_6.setTag(0);
        tv_7.setTag(0);
        loop_hour.setItems(hours);
        loop_hour.setCenterTextColor(Color.parseColor("#666666"));
        loop_hour.setOuterTextColor(Color.parseColor("#AFAFAF"));
        loop_hour.setTextSize(18);
        loop_hour.setInitPosition(0);


        loop_min.setItems(mins);
        loop_min.setCenterTextColor(Color.parseColor("#666666"));
        loop_min.setOuterTextColor(Color.parseColor("#AFAFAF"));
        loop_min.setTextSize(18);
        loop_min.setInitPosition(0);



        loop_second.setItems(seconds);
        loop_second.setCenterTextColor(Color.parseColor("#666666"));
        loop_second.setOuterTextColor(Color.parseColor("#AFAFAF"));
        loop_second.setTextSize(18);
        loop_second.setInitPosition(0);



        list.add(new Line(false,"线路一"));
        list.add(new Line(false,"线路二"));
        list.add(new Line(false,"线路三"));
        list.add(new Line(false,"线路四"));
        list.add(new Line(false,"线路五"));
        adapter=new MyAdapter(this,list);
        grid_line.setAdapter(adapter);
        grid_line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Line line=list.get(position);
                boolean click=line.isOnClick();
                if (click){
                    line.setOnClick(false);
                }else {
                    line.setOnClick(true);
                }
                list.set(position,line);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    int onClick=0;
    int only=0;//1为只有一次，0可以重复
    @OnClick({R.id.img_back,R.id.btn_only_one,R.id.img_add,R.id.tv_1,R.id.tv_2,R.id.tv_3,R.id.tv_4,R.id.tv_5,R.id.tv_6,R.id.tv_7})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_only_one:
                if (only==0){
                    only=1;
                    btn_only_one.setTextColor(Color.parseColor("#ffffff"));
                    btn_only_one.setBackground(getResources().getDrawable(R.drawable.shape_item_green));
                    setWeekView(tv_1,1);
                    setWeekView(tv_2,1);
                    setWeekView(tv_3,1);
                    setWeekView(tv_4,1);
                    setWeekView(tv_5,1);
                    setWeekView(tv_6,1);
                    setWeekView(tv_7,1);
                }else {
                    only=0;
                    btn_only_one.setTextColor(Color.parseColor("#575757"));
                    btn_only_one.setBackground(getResources().getDrawable(R.drawable.shape_only_one));
                }
                break;
            case R.id.img_add:
                ToastUtil.show(this,"添加成功",Toast.LENGTH_SHORT);
                finish();
                break;
            case R.id.tv_1:
                setOnlyOne();
                onClick= (int) tv_1.getTag();
                setWeekView(tv_1,onClick);
                break;
            case R.id.tv_2:
                setOnlyOne();
                onClick= (int) tv_2.getTag();
                setWeekView(tv_2,onClick);
                break;
            case R.id.tv_3:
                setOnlyOne();
                onClick= (int) tv_3.getTag();
                setWeekView(tv_3,onClick);
                break;
            case R.id.tv_4:
                setOnlyOne();
                onClick= (int) tv_4.getTag();
                setWeekView(tv_4,onClick);
                break;
            case R.id.tv_5:
                setOnlyOne();
                onClick= (int) tv_5.getTag();
                setWeekView(tv_5,onClick);
                break;
            case R.id.tv_6:
                setOnlyOne();
                onClick= (int) tv_6.getTag();
                setWeekView(tv_6,onClick);
                break;
            case R.id.tv_7:
                setOnlyOne();
                onClick= (int) tv_7.getTag();
                setWeekView(tv_7,onClick);
                break;
        }
    }
    private void setOnlyOne(){
        only=0;
        btn_only_one.setTextColor(Color.parseColor("#575757"));
        btn_only_one.setBackground(getResources().getDrawable(R.drawable.shape_only_one));
    }
    private void setWeekView(TextView tv,int onClick){
        if (onClick==1){
            tv.setTag(0);
            tv.setTextColor(Color.parseColor("#575757"));
            tv.setBackground(getResources().getDrawable(R.drawable.shape_week_gray));
        }else if (onClick==0){
            tv.setTag(1);
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setBackground(getResources().getDrawable(R.drawable.shape_week));
        }
    }
    class MyAdapter extends BaseAdapter{

        private Context context;
        private List<Line> list;

        public MyAdapter(Context context, List<Line> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Line getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView==null){
                convertView=View.inflate(context,R.layout.item_line,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            Line line=getItem(position);
            String name=line.getName();
            boolean onClick=line.isOnClick();
            viewHolder.tv_line.setText(name+"");
            if (onClick){
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_item_green));
                viewHolder.tv_line.setTextColor(Color.parseColor("#ffffff"));
            }else {
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_item_gray));
                viewHolder.tv_line.setTextColor(Color.parseColor("#818181"));
            }

            return convertView;
        }
        class ViewHolder{
            @BindView(R.id.tv_line) TextView tv_line;
            public ViewHolder(View view){
                ButterKnife.bind(this,view);
            }
        }
    }
}
