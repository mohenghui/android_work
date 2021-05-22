package com.example.android_shixun;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class RankingList extends Activity {
    private LinearLayout namelayout;
    private TextView nametxt;
    private LinearLayout scorelayout;
    private TextView scoretxt;

    private static String DB_FILE="donttapthewhitetile.db",DB_TABLE="rankinglist";
    private SQLiteDatabase mDb=null;


    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_list);

        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        namelayout=(LinearLayout)findViewById(R.id.rankingname);
        scorelayout=(LinearLayout)findViewById(R.id.rankingscore);
        nametxt=(TextView)findViewById(R.id.txtname);
        scoretxt=(TextView)findViewById(R.id.txtscore);

        nametxt.setHeight(height/6);
        nametxt.setWidth(width/2);
        nametxt.setTextSize(height/30);
        scoretxt.setHeight(height/6);
        scoretxt.setWidth(width/2);
        scoretxt.setTextSize(height/30);

        DbOpenHelper myhelper=new DbOpenHelper(this);
        mDb=myhelper.getWritableDatabase();

        Cursor c=mDb.query(true,DB_TABLE,new String[]{"name","score"},null,null,null,null,"-score",null );
        if(c==null)
        {
            return;
        }
        if(c.getCount()==0){
            Toast toast = Toast.makeText(getApplicationContext(),"No data! Please playing game first!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            int tempindex=0;
            c.moveToFirst();
            do
            {
                TextView name=new TextView(this);
                name.setText(c.getString(0));
                name.setTextColor(Color.BLACK);
                name.setGravity(Gravity.CENTER);
                name.setHeight(height/11);
                namelayout.addView(name);
                TextView score=new TextView(this);
                score.setText(c.getString(1));
                score.setTextColor(Color.BLACK);
                score.setGravity(Gravity.CENTER);
                score.setHeight(height/11);
                scorelayout.addView(score);
                tempindex++;
            }while(c.moveToNext()&&tempindex<10);
        }
    }

    public void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public class DbOpenHelper extends SQLiteOpenHelper {
        public DbOpenHelper(Context context){
            super(context,DB_FILE,null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
