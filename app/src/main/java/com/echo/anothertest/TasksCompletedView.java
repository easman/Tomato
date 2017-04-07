package com.echo.anothertest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Echo
 */

public class TasksCompletedView extends View {

    // 画实心圆的画笔
    //private Paint mCirclePaint;
    // 画圆环的画笔
    private Paint mRingPaint;
    // 圆形颜色
    //private int mCircleColor;
    // 圆环颜色
    private int mRingColor;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    private float tempStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress;
    // 当前进度
    private int mProgress;

    public TasksCompletedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义的属性
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TasksCompletedView, 0, 0);
        mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
        mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
        tempStrokeWidth = mStrokeWidth;
        //mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
        mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);

        mRingRadius = mRadius + mStrokeWidth / 2;
    }

    private void initVariable() {
//        mCirclePaint = new Paint();
//        mCirclePaint.setAntiAlias(true);
//        mCirclePaint.setColor(mCircleColor);
//        mCirclePaint.setStyle(Paint.Style.FILL);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(tempStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;

//        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

        if (mProgress > 0) {
            RectF oval = new RectF();
            oval.left = (mXCenter - mRingRadius);
            oval.top = (mYCenter - mRingRadius);
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval, -90, ((float) mProgress / mTotalProgress) * 360, false, mRingPaint);
        }
    }

    public void setmTotalProgress(int mTotalProgress) {
        this.mTotalProgress = mTotalProgress;
    }

    public boolean justUsedStroke = true;
    float val = 1f;
    public void setProgress(int progress) {
        if (justUsedStroke) {
            tempStrokeWidth = mStrokeWidth;
            justUsedStroke = false;
        }
        mRingPaint.setStrokeWidth(tempStrokeWidth);
        mProgress = progress;
        postInvalidate();
    }

    public void setProgressWithStroke(int progress) {
        if (!justUsedStroke) {
            justUsedStroke = true;
            val = 0.3f;
        }
        tempStrokeWidth += val;
        val += 0.3f;
        mRingPaint.setStrokeWidth(tempStrokeWidth);
        mProgress = progress;
        postInvalidate();
    }
}
