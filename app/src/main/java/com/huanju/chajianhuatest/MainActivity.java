package com.huanju.chajianhuatest;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author 刘镓旗
 * @date 17/2/21
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bbb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里说一下，我们真正要启动的Activity不是直接用类名.class，因为
                //我们这个应用里根本没有这个类
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.huanju.chajiandemo",
                        "com.huanju.chajiandemo.TestActivity"));
                startActivity(intent);
            }
        });
    }

}
