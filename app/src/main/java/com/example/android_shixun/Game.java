package com.example.android_shixun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;



public class Game extends Activity {
    public Tile myTileBt[][]=new Tile[4][4];
    public int index=0;//指示标志
    public int score=0;
    public int width,height;
    public boolean isGameOver=false;
    public boolean isGameStart=false;

    private RelativeLayout gamelayout;
    private TextView TIME;

    public int Time=30;

    Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_interface);

        gamelayout=(RelativeLayout)findViewById(R.id.gamelayout);
        TIME=(TextView)findViewById(R.id.TIME);

        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        for(int i=index;i<4;i++)
        {
            Random ra=new Random();
            int random=ra.nextInt(4);
            for(int j=0;j<4;j++)
            {
                myTileBt[i][j]=new Tile(getBaseContext());
                boolean temp=false;
                if(j==random)
                {
                    temp=true;
                }
                myTileBt[i][j].setLocationAndTile(i,j,temp);
                gamelayout.addView(myTileBt[i][j].bt);
            }
        }

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what==1)
                {
                    TIME.setText(Time+"");//执行
                }
                else if(msg.what==2)
                {
                    String str=msg.obj.toString();
                    TIME.setText(str);
                }
            }
        };
        new Thread(new MyThread()).start();
    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            CallReadyMsg();
            isGameStart=true;
            isGameOver=false;
            while(!isGameOver)
            {
                Message m=new Message();
                m.what = 1;
                handler.sendMessage(m);
                if(Time<=0)
                {
                    isGameOver=true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Time--;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent mIntent=new Intent(com.example.android_shixun.Game.this,InputName.class);
            Bundle mBundle=new Bundle();
            mBundle.putInt("key",score);
            mIntent.putExtras(mBundle);
            startActivity(mIntent);
            finish();
        }
        public void CallReadyMsg()
        {
            Message m=new Message();
            m.what = 2;
            m.obj="Ready?";
            handler.sendMessage(m);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            m=new Message();
            m.what = 2;
            m.obj="GO!";
            handler.sendMessage(m);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        public void Toast(String str)
        {
            Looper.prepare();
            Toast toast = Toast.makeText(getApplicationContext(),
                    str, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Looper.loop();
        }
    }

    class Tile extends View {
        public boolean isBlackTile=false;
        public Button bt;
        public int x,y;

        public Tile(Context context) {
            super(context);
            bt=new Button(context);
            bt.setBackgroundColor(Color.TRANSPARENT);
            bt.setHeight(height/4);
            bt.setWidth(width/4);
            bt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isGameOver&&isGameStart)
                    {
                        if(isBlackTile)
                        {
                            score++;
                            if(x!=0)
                            {
                                isGameOver=true;
                            }
                            Random ra=new Random();
                            int random=ra.nextInt(4);
                            for(int i=0;i<4;i++)
                            {
                                if(i==random)
                                {
                                    myTileBt[index][i].isBlackTile=true;
                                    myTileBt[index][i].bt.setBackgroundColor(Color.BLACK);
                                }
                                else
                                {
                                    myTileBt[index][i].isBlackTile=false;
                                    myTileBt[index][i].bt.setBackgroundColor(Color.TRANSPARENT);
                                }
                                myTileBt[index][i].setTop();
                            }
                            int tempindex=index;
                            index=(index+1)%4;
                            for(int i=index;i!=tempindex;i=(i+1)%4)
                            {
                                for(int j=0;j<4;j++)
                                {
                                    myTileBt[i][j].movedown();
                                }
                            }
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                "Oops! You've got "+score+" points!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            isGameOver=true;
                        }
                    }
                }
            });
        }
        public void setLocationAndTile(int x, int y,boolean isBlack){
            this.x=x;
            this.y=y;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width/4, height/4-1);
            //set 四周距离
            params.leftMargin=(y*width/4);
            params.topMargin=((3-x)*height/4);
            bt.setLayoutParams(params);
            if(isBlack)
            {
                bt.setBackgroundColor(Color.BLACK);
                this.isBlackTile=true;
            }
            else
            {
                this.isBlackTile=false;
                bt.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        public void setTop()
        {
            this.x=3;
            setLayout((y*width/4),((3-x)*height/4));
        }
        public void movedown(){
            this.x--;
            setLayout((y*width/4),((3-x)*height/4));
        }
        public void setLayout(int x,int y)
        {
            ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(bt.getLayoutParams());
            margin.setMargins(x,y, x+margin.width, y+margin.height);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
            bt.setLayoutParams(layoutParams);
        }

    }
}



