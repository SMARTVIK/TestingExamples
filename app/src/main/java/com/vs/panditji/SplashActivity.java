package com.vs.panditji;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(ApplicationDataController.getInstance().getUserId()==null){
                    finish();
                    startActivity(new Intent(SplashActivity.this,SignInActivity.class));
                }else{
                    finish();
                    startActivity(new Intent(SplashActivity.this,MainScreen.class));
                }

            }
        },2000);

    }

}
