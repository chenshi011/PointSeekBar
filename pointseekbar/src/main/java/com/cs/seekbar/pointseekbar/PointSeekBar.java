package com.cs.seekbar.pointseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chenshi on 2017/5/18.
 */

public class PointSeekBar extends View {
    private float mSmallRadius = 8;
    private float mProgressHeight = 8;
    private float mBlockRadius = 32;
    private float mBlockBorderWidth = 2;
    private int mBlockBorderLineColor = Color.BLACK;
    private int mBlockBorderFillColor = Color.WHITE;
    private int mProgressFullColor = Color.BLACK;
    private int mProgressEmptyColor = Color.GRAY;
    private Paint mPaint;
    private int _progress = 0;
    private int _max = 5;
    private float mBlockWidth;
    public PointSeekBar(Context context) {
        this(context, null, R.style.PointSeekBar_Default);
    }
    public PointSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.PointSeekBar_Default);
    }
    public PointSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PointSeekBar, defStyleAttr,
                R.style.PointSeekBar_Default);
        mSmallRadius  = a.getDimension(R.styleable.PointSeekBar_point_small_radius, 5);
        mProgressHeight = a.getDimension(R.styleable.PointSeekBar_point_progress_height, 8);
        mBlockRadius = a.getDimension(R.styleable.PointSeekBar_point_block_radius, 10);
        mBlockBorderWidth  = a.getDimension(R.styleable.PointSeekBar_point_border_line_width, 2);
        mBlockBorderLineColor = a.getColor(R.styleable.PointSeekBar_point_border_line_color, Color.BLACK);
        mBlockBorderFillColor = a.getColor(R.styleable.PointSeekBar_point_border_fill_color, Color.WHITE);
        mProgressFullColor = a.getColor(R.styleable.PointSeekBar_point_progress_full_color, Color.BLACK);
        mProgressEmptyColor = a.getColor(R.styleable.PointSeekBar_point_progress_empty_color, Color.GRAY);
        _progress = a.getInt(R.styleable.PointSeekBar_point_progress, 0);
        _max = a.getInt(R.styleable.PointSeekBar_point_max, 5);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public int getProgress() {
        return _progress;
    }

    public void setProgress(int progress) {
        if (this._progress != progress) {
            this._progress = progress;
            invalidate();
        }
    }

    public int getMax() {
        return _max;
    }

    public void setMax(int max) {
        if (this._max != max) {
            this._max = max;
            invalidate();
        }
    }

    private OnPointSeekBarChangedListener _onPointSeekBarChangedListener;

    public void setOnSeekBarChangedListener(
            OnPointSeekBarChangedListener onPointSeekBarChangedListener) {
        this._onPointSeekBarChangedListener = onPointSeekBarChangedListener;
    }

    public interface OnPointSeekBarChangedListener {
        /**
         * @param progress
         */
        void onProgressChanged(PointSeekBar pointSeekBar, int progress);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setCurrentValueByLocation(x);
                return true;
            case MotionEvent.ACTION_UP:
                onValueDone();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void onValueDone() {
        if (_onPointSeekBarChangedListener != null) {
            _onPointSeekBarChangedListener.onProgressChanged(this, _progress);
        }
    }

    private void setCurrentValueByLocation(float x) {
        int block = 0;
        if (x > 0) {
            block = (int) (x % mBlockWidth > 0 ? x / mBlockWidth
                    : x / mBlockWidth - 1);
            block = block > _max ? _max : block;
        } else {
            block = 0;
        }
        setProgress(block);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float verticalSplit = (height + getPaddingTop() - getPaddingBottom()) * 1.0f / 2;
        mBlockWidth = (width - getPaddingLeft() - getPaddingRight() - mBlockRadius * 2) * 1.0f / _max;
        Path activePath = new Path();
        Path disabledPath = new Path();
        int left = (int) (getPaddingLeft() + mBlockRadius);
        int top = (int) (verticalSplit - mProgressHeight / 2);
        int right = (int) (width - getPaddingRight() - mBlockRadius);
        int bottom =  (int) (verticalSplit + mProgressHeight / 2);
        activePath.addRect(left, top, left +  mBlockWidth * _progress, bottom, Path.Direction.CW);
        disabledPath.addRect(left +  mBlockWidth * _progress, top, right, bottom, Path.Direction.CW);
        float curX = 0;
        for (int i = 0; i <= _max; i++) {
            float x = left + i * mBlockWidth;
            if (i < _progress) {
                activePath.addCircle(x, verticalSplit, mSmallRadius, Path.Direction.CW);
            }else if (i > _progress) {
                disabledPath.addCircle(x, verticalSplit, mSmallRadius, Path.Direction.CW);
            }else {
                curX = x;
            }
        }
        if(_progress < _max){
            disabledPath.addCircle(right, verticalSplit, mSmallRadius, Path.Direction.CW);
        }else {
            activePath.addCircle(right, verticalSplit, mSmallRadius, Path.Direction.CW);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressFullColor);
        canvas.drawPath(activePath, mPaint);
        mPaint.setColor(mProgressEmptyColor);
        canvas.drawPath(disabledPath, mPaint);

        //cur progress
        mPaint.setColor(mBlockBorderLineColor);
        canvas.drawCircle(curX, verticalSplit, mBlockRadius, mPaint);
        mPaint.setColor(mBlockBorderFillColor);
        canvas.drawCircle(curX, verticalSplit, mBlockRadius - mBlockBorderWidth, mPaint);
    }

}

