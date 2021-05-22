package com.example.android_shixun.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.android_shixun.tool.Util;


public class Bird {

    private static final float RATIO_BIRD_POSITION_HEIGHT = 3 / 7F;

    private static final int RATIO_BIRD_SIZE = 35;

    private int x;
    private int y;
    private int mBirdWidth;
    private int mBirdHeight;

    private Bitmap mBitmap;
    private RectF mRectF = new RectF();

    public Bird(Context context, int gameWidth, int gameHeight, Bitmap bitmap) {
        this.mBitmap = bitmap;
        x = gameWidth / 2 - bitmap.getWidth() / 2;
        y = (int) (gameHeight * RATIO_BIRD_POSITION_HEIGHT);
        mBirdWidth = Util.dp2px(context, RATIO_BIRD_SIZE);
        mBirdHeight = (int) (mBirdWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
    }

    public void draw(Canvas canvas) {
        mRectF.set(x, y, x + mBirdWidth, y + mBirdHeight);
        canvas.drawBitmap(mBitmap, null, mRectF, null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBirdWidth() {
        return mBirdWidth;
    }

    public int getBirdHeight() {
        return mBirdHeight;
    }

}
