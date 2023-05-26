package com.example.circleprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.circleprogressbar.R;

/**
 * Created by Jackie on 2023/5/25
 */
public class CircleProgressBar extends View {

    private boolean isRing;
    private Paint mPaint;        // 画笔
    private RectF mRectF;        // 圆弧的矩形区域
    private float mProgress = 0; // 当前进度
    private final int mMaxProgress = 100;    // 最大进度值
    private int mCircleWidth = 10;           // 圆环宽度
    private int mCircleColor = Color.BLUE;   // 圆环颜色
    private int mCircleBgColor = Color.GRAY; // 圆环背景颜色
    private int mTextColor = Color.BLUE;     // 文字颜色
    private int mTextSize = 24;  // 文字大小
    private Handler mHandler;    //动画效果
    private int mTextStyle = Typeface.NORMAL; // 文字样式

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
            isRing = typedArray.getBoolean(R.styleable.CircleProgressBar_cpb_is_ring, true);

            mCircleWidth = typedArray.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_circle_width, 10);
            mCircleColor = typedArray.getColor(R.styleable.CircleProgressBar_cpb_circle_color, Color.parseColor("#5491FF"));
            mCircleBgColor = typedArray.getColor(R.styleable.CircleProgressBar_cpb_circle_bg_color, Color.parseColor("#E7E7E7"));

            mTextColor = typedArray.getColor(R.styleable.CircleProgressBar_cpb_text_color, Color.parseColor("#5491FF"));
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_text_size, 24);
            mTextStyle = typedArray.getInt(R.styleable.CircleProgressBar_cpb_text_style, Typeface.NORMAL);

            mProgress = typedArray.getFloat(R.styleable.CircleProgressBar_cpb_progress, 0f);
            typedArray.recycle();
        }

        init();
    }

    private void init() {
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setColor(mCircleColor);

        // 初始化矩形区域
        mRectF = new RectF();

        // 设置 View 的背景为透明色
        setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacksAndMessages();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //确保设置margin后绘制图形完整，只是边距同样会被扩大,要适当调整
        int marginTopBottom = 0;
        if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
            marginTopBottom = lp.topMargin + lp.bottomMargin;
        }

        int width = measureSpec(widthMeasureSpec, 100);
        int height = measureSpec(heightMeasureSpec + marginTopBottom, Math.max(width, 100) + marginTopBottom);
        setMeasuredDimension(width, height);
    }

    protected int measureSpec(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(result, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取 View 的宽度和高度
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        int radius = Math.min(halfWidth, halfHeight);
        float halfStrokeWidth = mCircleWidth / 2f;
        float angle = 360f * mProgress / mMaxProgress;

        // 设置画笔
        mPaint.setColor(mCircleBgColor);
        mPaint.setDither(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mCircleWidth);
        //设置图形为空心
        mPaint.setStyle(isRing ? Paint.Style.STROKE : Paint.Style.FILL);
        // 画背景
        canvas.drawCircle(halfWidth, halfHeight, radius - halfStrokeWidth, mPaint);

        // 画当前进度的圆环
        mPaint.setColor(mCircleColor);
        mRectF.top = halfHeight - radius + halfStrokeWidth;
        mRectF.bottom = halfHeight + radius - halfStrokeWidth;
        mRectF.left = halfWidth - radius + halfStrokeWidth;
        mRectF.right = halfWidth + radius - halfStrokeWidth;
        canvas.drawArc(mRectF, 0, angle, !isRing, mPaint);

        // 设置文字画笔
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.defaultFromStyle(mTextStyle));

        // 绘制进度文字
        String text = mProgress + "%";
        float textWidth = mPaint.measureText(text);
        float textPercentWidth = mPaint.measureText("%");
        float y = halfHeight - (mPaint.ascent() + mPaint.descent()) / 2f;
        canvas.drawText(String.valueOf(mProgress), halfWidth - textWidth / 2f + textPercentWidth / 4f, y, mPaint);

        // 绘制进度 %
        mPaint.setTextSize(mTextSize / 1.5f);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        canvas.drawText("%", halfWidth + textWidth / 2f - textPercentWidth + textPercentWidth / 4f + 2, y - 1, mPaint);
    }

    /**
     * 设置进度值
     *
     * @param progress 进度值，取值范围：0-100
     */

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(final float progress, boolean isAnimate) {
        removeCallbacksAndMessages();
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("进度值必须在0-100之间");
        }

        if (isAnimate) {
            mProgress = 0;
            mHandler = new Handler();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgress = mProgress + 1;
                    if (mProgress >= progress) {
                        mProgress = progress;
                        invalidate();
                        removeCallbacksAndMessages();
                    } else {
                        invalidate();
                        mHandler.postDelayed(this, 10);
                    }
                }
            });
        } else {
            mProgress = progress;
            invalidate();
        }
    }

    private void removeCallbacksAndMessages() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
