package com.echo.anothertest.Main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.anothertest.R;
import com.echo.anothertest.bean.Pomodori;

import java.util.List;

/**
 * Created by Echo
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Pomodori> pomodoris;
    private Context mContext;
    private int[] pomodoriImageID = new int[]{R.drawable.ic_pomodori_3p,
            R.drawable.ic_pomodori_2p,
            R.drawable.ic_pomodori_1p,
            R.drawable.ic_pomodori_0,
            R.drawable.ic_pomodori_1n,
            R.drawable.ic_pomodori_2n,
            R.drawable.ic_pomodori_3n};


    public MyAdapter(Context context, List<Pomodori> pomodoris) {
        this.mContext = context;
        this.pomodoris = pomodoris;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pomodori_image_car_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
        // 给ViewHolder设置元素
        Pomodori t = pomodoris.get(position);
        viewHolder.mTextWorkMinutes.setText("工作" + t.getWorkMinutes() + "分钟");
        viewHolder.mTextBreakMinutes.setText("休息" + t.getBreakMinutes() + "分钟");
        viewHolder.mTextJobDecription.setText(t.getJobDescription());
        viewHolder.mTextTotleTamatoRepeat.setText(t.getTotlePomodoriRepeat() + "个番茄");
        viewHolder.mTextNumberOfFinish.setText("已完成" + t.getNumberOfFinish() + "次");
        viewHolder.mTextNumberOfUnfinish.setText("未完成" + t.getNumberOfUnfinish() + "次");
        int value = t.getNumberOfUnfinish() - t.getNumberOfFinish();
        if (value < -10) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[0]);
        } else if (value >= -10 && value < -5) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[1]);
        } else if (value >= -5 && value < 0) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[2]);
        } else if (value == 0) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[3]);
        } else if (value > 0 && value <= 5) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[4]);
        } else if (value > 5 && value <= 10) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[5]);
        } else if (value > 10) {
            viewHolder.pomodoriImage.setImageResource(pomodoriImageID[6]);
        }
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return pomodoris == null ? 0 : pomodoris.size();
    }


    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextWorkMinutes, mTextJobDecription, mTextBreakMinutes, mTextTotleTamatoRepeat, mTextNumberOfFinish, mTextNumberOfUnfinish;
        public ImageView pomodoriImage;

        public ViewHolder(View v) {
            super(v);
            mTextWorkMinutes = (TextView) v.findViewById(R.id.card_work_minutes);
            mTextJobDecription = (TextView) v.findViewById(R.id.job_decription);
            mTextBreakMinutes = (TextView) v.findViewById(R.id.card_break_minutes);
            mTextTotleTamatoRepeat = (TextView) v.findViewById(R.id.card_totle_pomodori_repeat);
            mTextNumberOfFinish = (TextView) v.findViewById(R.id.number_of_finish);
            mTextNumberOfUnfinish = (TextView) v.findViewById(R.id.number_of_unfinish);
            pomodoriImage = (ImageView) v.findViewById(R.id.pomodori_image);


        }
    }
}
