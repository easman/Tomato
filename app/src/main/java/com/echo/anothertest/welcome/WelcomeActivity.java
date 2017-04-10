package com.echo.anothertest.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.echo.anothertest.R;
import com.echo.anothertest.Main.MainActivity;
import com.echo.anothertest.utils.DecodeBitmapHelper;

/**
 * Created by Echo
 */

public class WelcomeActivity extends Activity {

    private ImageView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 隐藏导航栏，全屏化
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_welcome);

        //获取屏幕尺寸，加载位图
        welcome = (ImageView) findViewById(R.id.welcome);
        welcome.setImageBitmap(DecodeBitmapHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.fu,400,400));
        welcome.setClickable(false);

        //确定是否为第一次启动
        SharedPreferences sp = getSharedPreferences("First_Run", Context.MODE_PRIVATE);
        String content = sp.getString("First_Run", null);
        final Intent intent;
        if (content == null) {
            SharedPreferences.Editor editor = getSharedPreferences("First_Run", Context.MODE_PRIVATE).edit();
            editor.putString("First_Run", "First_Run");
            editor.apply();
            intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        } else {
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        }
        Handler handler = new Handler();
        //当计时结束,跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }
}
