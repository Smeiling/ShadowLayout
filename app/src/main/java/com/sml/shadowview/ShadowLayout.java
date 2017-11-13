package com.sml.shadowview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Smeiling on 2017/11/13.
 */

public class ShadowLayout extends FrameLayout {

    private static final int DEFAULT_SHADOW_WIDTH = 8;
    private static final int DEFAULT_SHADOW_COLOR = 0xeeeeee;
    private static final int DEFAULT_SHADOW_RADIUS = 10;
    private static final int DEFAULT_CENTER_ALPHA = 255;

    /**
     * 阴影的宽度
     */
    private int shadowWidth;

    /**
     * 阴影颜色
     */
    private int shadowColor;

    /**
     * 阴影圆角半径
     */
    private int shadowRadius;

    /**
     * 阴影最小透明度
     */
    private int centerAlpha;

    private int contentPadding;
    private int red = 0;
    private int green = 0;
    private int blue = 0;

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
        shadowWidth = typedArray.getInt(R.styleable.ShadowLayout_shadowWidth, DEFAULT_SHADOW_WIDTH);
        shadowColor = typedArray.getInt(R.styleable.ShadowLayout_shadowColor, DEFAULT_SHADOW_COLOR);
        shadowRadius = typedArray.getInt(R.styleable.ShadowLayout_shadowRadius, DEFAULT_SHADOW_RADIUS);
        centerAlpha = typedArray.getInt(R.styleable.ShadowLayout_centerAlpha, DEFAULT_CENTER_ALPHA);

        typedArray.recycle();

        parseColor(shadowColor);
        contentPadding = dip2px(getContext(), shadowWidth);
        ShadowView shadowView = new ShadowView(context);
        addView(shadowView);
    }

    /**
     * 颜色获取
     *
     * @param shadowColor
     */
    private void parseColor(int shadowColor) {
        String hex = Integer.toHexString(shadowColor);
        if ("0".equals(hex) || hex.length() < 1) {
            return;
        }
        if (hex.length() <= 2) {
            blue = Integer.parseInt(hex.substring(0, hex.length()), 16);
        } else if (hex.length() <= 4) {
            green = Integer.parseInt(hex.substring(0, hex.length() - 2), 16);
            blue = Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16);
        } else if (hex.length() <= 6) {
            red = Integer.parseInt(hex.substring(0, hex.length() - 4), 16);
            green = Integer.parseInt(hex.substring(hex.length() - 4, hex.length() - 2), 16);
            blue = Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16);
        } else if (hex.length() <= 8) {
            red = Integer.parseInt(hex.substring(hex.length() - 6, hex.length() - 4), 16);
            green = Integer.parseInt(hex.substring(hex.length() - 4, hex.length() - 2), 16);
            blue = Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16);
        }
    }

    /**
     * 绘制位置调整
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        View child;

        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (i == 1) {
                child.layout(contentPadding,
                        contentPadding,
                        child.getMeasuredWidth() + contentPadding,
                        0 + child.getMeasuredHeight() + contentPadding);
            } else if (i == 0) {
                child.layout(0,
                        0,
                        child.getMeasuredWidth(),
                        0 + child.getMeasuredHeight());
            }
        }
    }

    /**
     * 调整子View大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            if (i == 1) {
                getChildAt(1).measure(widthMeasureSpec - 2 * contentPadding,
                        heightMeasureSpec - 2 * contentPadding);
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 自定义阴影View
     */
    public class ShadowView extends View {

        private int layoutWidth = 0;
        private int layoutHeight = 0;

        public ShadowView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            layoutWidth = getMeasuredWidth();
            layoutHeight = getMeasuredHeight();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setAntiAlias(true);

            RectF shadowRect;
            int alpha = centerAlpha / contentPadding;

            for (int i = 0; i < contentPadding; i++) {
                shadowRect = new RectF();
                shadowRect.left = i;
                shadowRect.top = i;
                shadowRect.right = layoutWidth - i;
                shadowRect.bottom = layoutHeight - i;
                paint.setColor(Color.argb(i * alpha, red, green, blue));
                canvas.drawRoundRect(shadowRect, shadowRadius, shadowRadius, paint);
            }
        }
    }
}
