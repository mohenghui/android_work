package com.example.android_shixun;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class InputName extends Activity {
    private TextView textView;
    private EditText editText;
    private Button button;

    private static String DB_FILE="donttapthewhitetile.db",DB_TABLE="rankinglist";
    private SQLiteDatabase mDb=null;

    public int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.input_name);

        textView=(TextView)findViewById(R.id.msgtext);
        editText=(EditText)findViewById(R.id.inputname);
        button=(Button)findViewById(R.id.okbt);

        Bundle bundle = this.getIntent().getExtras();
        //改变文本框的文本内容
        score=bundle.getInt("key");
        textView.setText("You've got "+score+" points!");

        DbOpenHelper myhelper=new DbOpenHelper(this);
        mDb=myhelper.getWritableDatabase();

        Cursor cursor=mDb.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name='"+DB_TABLE+"'",null
        );
        if(cursor!=null){
            if(cursor.getCount()==0)
            {
                mDb.execSQL(
                        "CREATE TABLE "+DB_TABLE+"("+
                        "name TEXT NOT NULL,"+
                        "score INTEGER);"
                );
            }
        }
        cursor.close();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Serious? You don't have a name?", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else
                {
                    mDb.execSQL(
                            "INSERT INTO "+DB_TABLE+" values ('"+editText.getText().toString()+"',"+score+")"
                    );
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Saving data successfully!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent intent=new Intent(com.example.android_shixun.InputName.this,MainMenu.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        try{
            if((requestCode == 0123456) && (resultCode == Activity.RESULT_OK)){
                Bundle myResults=data.getExtras();
                score=myResults.getInt("key");
                textView.setText("You've got "+score+" points!");
            }
        }
        catch (Exception e)
        {
            textView.setText("Oops! - "+requestCode+" "+resultCode);
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
