package com.example.android_shixun;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 此类 implements View.OnClickListener 之后，
 * 就可以把onClick事件写到onCreate()方法之外
 * 这样，onCreate()方法中的代码就不会显得很冗余
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        // 初始化控件对象
        Button mBtMainLogout = findViewById(R.id.bt_main_logout);
        Button bire_start = findViewById(R.id.bt_bird);
        Button baikuai_start = findViewById(R.id.bt_baikuai);
        // 绑定点击监听器
        mBtMainLogout.setOnClickListener(this);
        bire_start.setOnClickListener(this);
        baikuai_start.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.bt_main_logout) {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
            finish();
        }
         else if (view.getId() == R.id.bt_bird) {
            Intent intent = new Intent(this, FlappyBirdActivity.class);
            startActivity(intent);
            Toast.makeText(this, "欢迎进入fly bird,游戏愉快", Toast.LENGTH_SHORT).show();
//            finish();
        }
        else if (view.getId() == R.id.bt_baikuai) {
            Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);
//            Toast.makeText(this, "游戏正在研发，请耐心等候！", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "欢迎进入别踩白块儿,游戏愉快", Toast.LENGTH_SHORT).show();
//            finish();
        }
    }
}
