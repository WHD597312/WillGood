package com.peihou.willgood.devicelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.peihou.willgood.R;
import com.peihou.willgood.base.BaseActivity;
import com.peihou.willgood.pojo.Line;
import com.peihou.willgood.pojo.Link;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LinkedSetActivity extends BaseActivity {


    @BindView(R.id.gv_line)
    GridView gv_line;//线路网格布局
    private List<Line> lines=new ArrayList<>();//线路集合
    @BindView(R.id.slide_bar)
    RangeSeekBar slide_bar;
    @BindView(R.id.btn_once) TextView btn_once;//单次触发
    @BindView(R.id.btn_loop) TextView btn_loop;//循环触发
    LinesAdapter adapter;
    int type=0;
    @Override
    public void initParms(Bundle parms) {
        type=parms.getInt("type");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_set;
    }

    String s="";
    @Override
    public void initView(View view) {
        if (type==0){
            s="温度";
        }else if (type==1){
            s="湿度";
        } else if (type==2){
            s="开关量";
        }else if (type==3){
            s="电流";
        }else if (type==4){
            s="电压";
        }

        lines.add(new Line(true,"线路1"));
        lines.add(new Line(false,"线路2"));
        lines.add(new Line(false,"线路3"));
        lines.add(new Line(false,"线路4"));
        lines.add(new Line(false,"线路5"));
        lines.add(new Line(false,"线路6"));
        lines.add(new Line(false,"线路7"));
        lines.add(new Line(false,"线路8"));

        adapter=new LinesAdapter(this,lines);
        gv_line.setAdapter(adapter);
        gv_line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Line line=lines.get(position);
                if (line.isOnClick()){
                    line.setOnClick(false);
                }else {
                    line.setOnClick(true);
                }
                lines.set(position,line);
                adapter.notifyDataSetChanged();
            }
        });
        slide_bar.setTypeface(Typeface.DEFAULT_BOLD);
        slide_bar.setIndicatorTextDecimalFormat("0");


        slide_bar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                Log.i("isLeft","-->"+isLeft);
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                Log.i("isLeft","-->"+isLeft);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @OnClick({R.id.img_back,R.id.btn_once,R.id.btn_loop})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_once:
                if (touch==0){
                    break;
                }
                touch=0;
                setTouchMode();
                break;
            case R.id.btn_loop:
                if (touch==1){
                    break;
                }
                touch=1;
                setTouchMode();
                break;
        }
    }


    int touch=0;//为0是单次触发，1为多次触发
    private void setTouchMode(){
        if (touch==0){
            btn_once.setTextColor(Color.parseColor("#ffffff"));
            btn_once.setBackground(getResources().getDrawable(R.drawable.shape_once));
            btn_loop.setTextColor(Color.parseColor("#939393"));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_loop));
        }else if (touch==1){
            btn_once.setTextColor(Color.parseColor("#939393"));
            btn_once.setBackground(getResources().getDrawable(R.drawable.shape_loop));
            btn_loop.setTextColor(Color.parseColor("#ffffff"));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }
    }
    class LinesAdapter extends BaseAdapter {

        private Context context;
        private List<Line> list;

        public LinesAdapter(Context context, List<Line> list) {
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
                convertView=View.inflate(context,R.layout.item_line2,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            Line line=getItem(position);
            boolean onClick=line.isOnClick();
            String name=line.getName();
            viewHolder.tv_line.setText(name+"");
            if (onClick){
                viewHolder.tv_line.setTextColor(Color.parseColor("#ffffff"));
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_once));
            }else {
                viewHolder.tv_line.setTextColor(Color.parseColor("#939393"));
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_loop));
            }
            return convertView;
        }
    }
    class ViewHolder{
        @BindView(R.id.tv_line) TextView tv_line;
        public ViewHolder(View itemView){
            ButterKnife.bind(this ,itemView);
        }
    }
}
