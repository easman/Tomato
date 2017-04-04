package com.echo.anothertest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Echo
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Tomato> tomatos;
    private Context mContext;

    public MyAdapter(Context context, List<Tomato> tomatos) {
        this.mContext = context;
        this.tomatos = tomatos;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tamato_car_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
        // 给ViewHolder设置元素
        Tomato t = tomatos.get(position);
        viewHolder.mTextWorkMinutes.setText("工作"+t.getWorkMinutes()+"分钟");
        viewHolder.mTextBreakMinutes.setText("休息"+t.getBreakMinutes()+"分钟");
        viewHolder.mTextJobDecription.setText(t.getJobDescription());
        viewHolder.mTextTotleTamatoRepeat.setText(t.getTotleTamatoRepeat()+"个番茄");
        viewHolder.mTextNumberOfFinish.setText("已完成"+t.getNumberOfFinish()+"次");
        viewHolder.mTextNumberOfUnfinish.setText("未完成"+t.getNumberOfUnfinish()+"次");
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return tomatos == null ? 0 : tomatos.size();
    }


    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextWorkMinutes,mTextJobDecription,mTextBreakMinutes,mTextTotleTamatoRepeat,mTextNumberOfFinish,mTextNumberOfUnfinish;
        public ViewHolder(View v) {
            super(v);
            mTextWorkMinutes= (TextView) v.findViewById(R.id.card_work_minutes);
            mTextJobDecription = (TextView) v.findViewById(R.id.job_decription);
            mTextBreakMinutes = (TextView) v.findViewById(R.id.card_break_minutes);
            mTextTotleTamatoRepeat = (TextView) v.findViewById(R.id.card_totle_tomato_repeat);
            mTextNumberOfFinish = (TextView) v.findViewById(R.id.number_of_finish);
            mTextNumberOfUnfinish = (TextView) v.findViewById(R.id.number_of_unfinish);

        }
    }
}
