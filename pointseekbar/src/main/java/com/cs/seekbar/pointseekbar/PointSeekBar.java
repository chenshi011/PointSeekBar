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
import android.widget.LinearLayout;

import static android.R.attr.left;
import static android.R.attr.x;

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
    private int mOrientation;

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
        mOrientation = a.getInt(R.styleable.PointSeekBar_android_orientation, LinearLayout.HORIZONTAL);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setOrientation(int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return mOrientation;
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

    OnPointSeekBarChangedListener _onPointSeekBarChangedListener;

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
        float offset = event.getX();
        if (mOrientation == LinearLayout.VERTICAL) {
            offset = event.getY();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setCurrentValueByLocation(offset);
                return true;
            case MotionEvent.ACTION_UP:
                onValueDone();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void onValueDone() {
        Log.d("PointSeekBar", "onProgressChanged:"+_progress);
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
        float halfSplit = (height + getPaddingTop() - getPaddingBottom()) * 1.0f / 2;
        if (mOrientation == LinearLayout.VERTICAL) {
            halfSplit =  (width + getPaddingLeft() - getPaddingRight()) * 1.0f / 2;
        }
        mBlockWidth = (width - getPaddingLeft() - getPaddingRight() - mBlockRadius * 2) * 1.0f / _max;
        if (mOrientation == LinearLayout.VERTICAL) {
            mBlockWidth = (height - getPaddingTop() - getPaddingBottom() - mBlockRadius * 2) * 1.0f / _max;
        }
        Path activePath = new Path();
        Path disabledPath = new Path();
        int left, top, right, bottom;
        if (mOrientation == LinearLayout.VERTICAL) {
            left = (int) (halfSplit - mProgressHeight / 2 + getPaddingLeft());
            top = (int) (mBlockRadius + getPaddingTop());
            right = (int) (halfSplit + mProgressHeight / 2 - getPaddingRight());
            bottom =  (int) (height - mBlockRadius - getPaddingBottom());
        }else {
            left = (int) (mBlockRadius + getPaddingLeft());
            top = (int) (halfSplit - mProgressHeight / 2 + getPaddingTop());
            right = (int) (width - mBlockRadius - getPaddingRight());
            bottom =  (int) (halfSplit + mProgressHeight / 2 - getPaddingBottom());
        }
        if (mOrientation == LinearLayout.VERTICAL) {
            activePath.addRect(left, top, right, top +  +  mBlockWidth * _progress,  Path.Direction.CW);
            disabledPath.addRect(left, top  +  mBlockWidth * _progress, right, bottom, Path.Direction.CW);
        }else {
            activePath.addRect(left, top, left +  mBlockWidth * _progress, bottom,  Path.Direction.CW);
            disabledPath.addRect(left +  mBlockWidth * _progress, top, right, bottom, Path.Direction.CW);
        }
        float center = 0;
        float offset = 0;
        for (int i = 0; i <= _max; i++) {
            offset = mOrientation == LinearLayout.VERTICAL ? top + i * mBlockWidth :  left + i * mBlockWidth;
            if (i < _progress) {
                if (mOrientation == LinearLayout.VERTICAL) {
                    activePath.addCircle(halfSplit, offset, mSmallRadius, Path.Direction.CW);
                }else {
                    activePath.addCircle(offset, halfSplit, mSmallRadius, Path.Direction.CW);
                }
            }else if (i > _progress) {
                if (mOrientation == LinearLayout.VERTICAL) {
                    disabledPath.addCircle(halfSplit, offset, mSmallRadius, Path.Direction.CW);
                }else {
                    disabledPath.addCircle(offset, halfSplit, mSmallRadius, Path.Direction.CW);
                }
            }else {
                center = offset;
            }
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressFullColor);
        canvas.drawPath(activePath, mPaint);
        mPaint.setColor(mProgressEmptyColor);
        canvas.drawPath(disabledPath, mPaint);
        //cur progress
        mPaint.setColor(mBlockBorderLineColor);
        if (mOrientation == LinearLayout.VERTICAL) {
            canvas.drawCircle(halfSplit, center, mBlockRadius, mPaint);
        }else {
            canvas.drawCircle(center, halfSplit, mBlockRadius, mPaint);
        }
        mPaint.setColor(mBlockBorderFillColor);
        if (mOrientation == LinearLayout.VERTICAL) {
            canvas.drawCircle(halfSplit, center, mBlockRadius - mBlockBorderWidth, mPaint);
        }else {
            canvas.drawCircle(center, halfSplit, mBlockRadius - mBlockBorderWidth, mPaint);
        }
    }
}


