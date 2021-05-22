package com.example.android_shixun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


import com.example.android_shixun.R;
import com.example.android_shixun.objects.Bird;
import com.example.android_shixun.objects.Floor;
import com.example.android_shixun.objects.Pipe;
import com.example.android_shixun.tool.Util;

import java.util.ArrayList;
import java.util.List;



public class FlappyBirdSurfaceView extends SurfaceView implements Callback, Runnable {


    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Thread mThread;
    private boolean isRunning;

    private int mGameWidth;//界面宽度
    private int mGameHeight;//界面高度
    private RectF mGamePanelRect = new RectF();
    private Bitmap mBackground;//背景图

    private Bitmap mBirdStart;//开始
    private Bitmap mBirdStop;//结束

    private Bird mBird;//小鸟
    private Bitmap mBirdBitmap;//小鸟图片

    private Paint mPaint;
    private Floor mFloor;//地面
    private Bitmap mFloorBitmap;//地面图片
    private int mSpeed;//移动速度

    private Bitmap mPipeTop;//上柱子
    private Bitmap mPipeBottom;//下柱子
    private RectF mPipeRect;//柱子的矩形形状
    private int mPipeWidth;//柱子的宽度

    private final int[] mNums = new int[]{R.drawable.n0, R.drawable.n1,
            R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9};//数字图片数组
    private Bitmap[] mNumBitmap;//数字数组
    private int mGrade = 0;//分数

//    private final int[] mBirdNums = new int[]{R.drawable.b0, R.drawable.b1,
//            R.drawable.b2, R.drawable.b3, R.drawable.b4,
//            R.drawable.b5, R.drawable.b6, R.drawable.b7};//小鸟图片数组
//    private Bitmap[] mBirdBitmaps;//小鸟

    private static final float RATIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    private int mSingleGradeWidth;//单位分数的宽度
    private int mSingleGradeHeight;//单位分数的高度
    private RectF mSingleNumRectF;//单位数的矩形
    private static final int PIPE_WIDTH = 60;//柱子的宽度
    private List<Pipe> mPipes = new ArrayList<>();//柱子的集合

    /**
     * 游戏状态
     * 等待、运行、停止
     */
    private enum GameStatus {
        WAITING, RUNNING, STOP
    }

    private GameStatus mStatus = GameStatus.WAITING;//初始默认游戏状态为等待
    private static final int TOUCH_UP_SIZE = -16;
    private int mTmpBirdDis;
    private final int mAutoDownSpeed = Util.dp2px(getContext(), 2);//小鸟自动下落速度
    private final int PIPE_DIS_BETWEEN_TWO = Util.dp2px(getContext(), 100);//柱子之间的距离
    private final int mBirdUpDis = Util.dp2px(getContext(), TOUCH_UP_SIZE);//鸟上飞的位移
    private int mTmpMoveDistance;//位移距离
    private List<Pipe> mNeedRemovePipe = new ArrayList<>();//柱子移除的集合
    private int mRemovedPipe = 0;

    public FlappyBirdSurfaceView(Context context) {
        this(context, null);
    }

    public FlappyBirdSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);//保持屏幕常亮

        initBitmaps();

        mPipeWidth = Util.dp2px(getContext(), PIPE_WIDTH);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }


    @Override
    public void run() {

        while (isRunning) {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();
            try {
                if ((end - start) < 50) {
                    mThread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 重写view大小改变的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mGameWidth = w;
        mGameHeight = h;
        mGamePanelRect.set(0, 0, w, h);
        mBird = new Bird(getContext(), mGameWidth, mGameHeight, mBirdBitmap);
        mFloor = new Floor(mGameWidth, mGameHeight, mFloorBitmap);

        mPipeRect = new RectF(0, 0, mPipeWidth, mGameHeight);

        mSingleGradeHeight = (int) (h * RATIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
        Pipe pipe = new Pipe(getWidth(), getHeight(), mPipeTop, mPipeBottom);
        mPipes.add(pipe);
    }


    /**
     * 触摸响应的事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            switch (mStatus) {
                case WAITING:
                    mGrade = 0;
                    mRemovedPipe = 0;
                    mStatus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTmpBirdDis = mBirdUpDis;
                    break;
            }
        }
        return true;
    }

    /**
     * 停止、运动的逻辑
     */
    private void logic() {
        switch (mStatus) {
            case RUNNING:
                mSpeed = Util.dp2px(getContext(), 2);
                mGrade = 0;
                mFloor.setX(mFloor.getX() - mSpeed);
                logicPipe();
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTmpBirdDis);
                mGrade += mRemovedPipe;
                for (Pipe pipe : mPipes) {
                    if (pipe.getPipeX() + mPipeWidth < mBird.getX()) {
                        mGrade++;
                    }
                }
                checkGameOver();
                break;
            case STOP:
                mSpeed = 0;
                if (mBird.getY() < mFloor.getY() - mBird.getBirdHeight()) {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else {
                    mStatus = GameStatus.WAITING;
                    initPosition();
                }
                break;
            default:
                break;
        }

    }

    /**
     *
     */
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                switch (mStatus){
                    case WAITING:
                        drawBackground();
                        drawPipes();
                        drawFloor();
                        drawStart();
                        drawBird();
                        drawGrades();
                        mFloor.setX(mFloor.getX() - mSpeed);
                        break;

                    case RUNNING:
                        drawBackground();
                        drawPipes();
                        drawFloor();
                        drawBird();
                        drawGrades();
                        mFloor.setX(mFloor.getX() - mSpeed);
                        break;

                    case STOP:
                        drawBackground();
                        drawPipes();
                        drawFloor();
                        drawBird();
//                        drawStop();
                        drawGrades();
                        mFloor.setX(mFloor.getX() - mSpeed);
//                        isRunning = true;
                        break;
                }

            }
        } catch (Exception e) {
            Log.i("FlappyBird", "出现错误");
        } finally {
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void initBitmaps() {
        mBackground = loadImageByResId(R.drawable.background);
        mBirdStart = loadImageByResId(R.drawable.start);
        mBirdStop = loadImageByResId(R.drawable.gameover);
        mFloorBitmap = loadImageByResId(R.drawable.floor);
        mPipeTop = loadImageByResId(R.drawable.pipe_above);
        mPipeBottom = loadImageByResId(R.drawable.pipe_below);
        mBirdBitmap = loadImageByResId(R.drawable.bird);
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNumBitmap.length; i++) {
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }
    }

    /**
     * 绘制背景
     */
    private void drawBackground() {
        mCanvas.drawBitmap(mBackground, null, mGamePanelRect, null);
    }

    /**
     * 绘制小鸟
     */
    private void drawBird() {
        mBird.draw(mCanvas);
    }

    /**
     * 绘制地面
     */
    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    /**
     * 绘制柱子
     */
    private void drawPipes() {
        for (Pipe pipe : mPipes) {
            pipe.setPipeX(pipe.getPipeX() - 2 * mSpeed);
            pipe.draw(mCanvas, mPipeRect);
        }
    }

    /**
     * 绘制分数
     */
    private void drawGrades() {
        String grade = mGrade + "";
        mCanvas.save();
        mCanvas.translate(mGameWidth / 2 - grade.length() * mSingleGradeWidth / 2,
                1f / 8 * mGameHeight);
        for (int i = 0; i < grade.length(); i++) {
            String numStr = grade.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
            mCanvas.translate(mSingleGradeWidth, 0);
        }
        mCanvas.restore();
    }

    private void drawStart() {
        mCanvas.drawBitmap(mBirdStart, null, mGamePanelRect, null);
    }

    private void drawStop() {
        mCanvas.drawBitmap(mBirdStop, null, mGamePanelRect, null);
        isRunning = false;
    }

    /**
     * 添加上、下柱子的逻辑方法
     */
    private void logicPipe() {
        for (Pipe pipe : mPipes) {
            if (pipe.getPipeX() < -mPipeWidth) {
                mNeedRemovePipe.add(pipe);
                mRemovedPipe++;
                continue;
            }
        }
        mPipes.removeAll(mNeedRemovePipe);
        mNeedRemovePipe.clear();

        mTmpMoveDistance += mSpeed;
        if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
            Pipe pipe = new Pipe(getWidth(), getHeight(), mPipeTop, mPipeBottom);
            mPipes.add(pipe);
            mTmpMoveDistance = 0;
        }
    }

    /**
     * 加载静态资源的方法
     * @param id
     * @return
     */
    private Bitmap loadImageByResId(int id) {
     return BitmapFactory.decodeResource(getResources(), id);
     }

     /**
     * 判断游戏是否结束
     */
    private void checkGameOver() {

        if (mBird.getY() > mFloor.getY() - mBird.getBirdHeight()) {
            mStatus = GameStatus.STOP;
        }
        for (Pipe wall : mPipes) {
            if (wall.getPipeX() + mPipeWidth < mBird.getX()) {
                continue;
            }
            if (wall.touchBird(mBird)) {
                mStatus = GameStatus.STOP;
                break;
            }
        }
    }

    /**
     * 初始化坐标位置
     */
    private void initPosition() {
        mPipes.clear();
        mNeedRemovePipe.clear();
        mBird.setY(mGameHeight * 3 / 7);
        mTmpBirdDis = 0;
        mTmpMoveDistance = 0;
    }
}
