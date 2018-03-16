package com.filote.cosmin1.metrorex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.filote.cosmin1.metrorex.R;

import pl.droidsonroids.gif.GifDrawable;

public class StartGIf extends AppCompatActivity {

    GifDrawable gifDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_gif);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //aici animatie sau ceva

                startActivity(new Intent(StartGIf.this, LoginRegister.class));
                finish();

            }
        }, 3000);

    }


}
