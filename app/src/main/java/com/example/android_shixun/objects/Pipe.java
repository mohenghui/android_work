package com.example.android_shixun.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;


public class Pipe {

    private static final float RATIO_BETWEEN_UP_DOWN = 1 / 5F;//上下柱子间隙比例

    private static final float RATIO_PIPE_MAX_HEIGHT = 5 / 10F;//柱子最大高度比例

    private static final float RATIO_PIPE_MIN_HEIGHT = 1 / 10F;//柱子最小高度比例

    private static Random random = new Random();//随机数

    private int mPipeX;//柱子的X坐标
    private int mPipeHeight;//柱子的高度
    private int mSpaceBetweenTwoPipes;//两个柱子之间的间隙
    private Bitmap mTopPipeBitmap;//上柱子
    private Bitmap mBottomPipeBitmap;//下柱子

    /**
     *
     * @param pipeX
     * @param gameHeight
     * @param topPipeBitmap
     * @param bottomPipeBitmap
     */
    public Pipe(int pipeX, int gameHeight, Bitmap topPipeBitmap, Bitmap bottomPipeBitmap) {
        mPipeX = pipeX;
        randomHeight(gameHeight);
        mSpaceBetweenTwoPipes = (int) (gameHeight * RATIO_BETWEEN_UP_DOWN);
        mTopPipeBitmap = topPipeBitmap;
        mBottomPipeBitmap = bottomPipeBitmap;
    }

    /**
     * 柱子的高度随机
     * @param gameHeight
     */
    private void randomHeight(int gameHeight) {
        mPipeHeight = random
                .nextInt((int) (gameHeight * (RATIO_PIPE_MAX_HEIGHT - RATIO_PIPE_MIN_HEIGHT)));
        mPipeHeight = (int) (mPipeHeight + gameHeight * RATIO_PIPE_MIN_HEIGHT);
    }

    /**
     * 绘制柱子的方法
     * @param canvas
     * @param rect
     */
    public void draw(Canvas canvas, RectF rect) {
        canvas.save();
        canvas.translate(mPipeX, -(rect.bottom - mPipeHeight));
        canvas.drawBitmap(mTopPipeBitmap, null, rect, null);
        canvas.translate(0, rect.bottom + mSpaceBetweenTwoPipes);
        canvas.drawBitmap(mBottomPipeBitmap, null, rect, null);
        canvas.restore();
    }

    /**
     * 柱子与鸟的碰撞判断
     * @param mBird
     * @return
     */
    public boolean touchBird(Bird mBird) {
        if (mBird.getX() + mBird.getBirdWidth()> mPipeX
                && (mBird.getY() < mPipeHeight || mBird.getY() + mBird.getBirdHeight() > mPipeHeight
                + mSpaceBetweenTwoPipes)) {
            return true;
        }
        return false;
    }

    public int getPipeX() {
        return mPipeX;
    }

    public void setPipeX(int pipeX) {
        this.mPipeX = pipeX;
    }

}
