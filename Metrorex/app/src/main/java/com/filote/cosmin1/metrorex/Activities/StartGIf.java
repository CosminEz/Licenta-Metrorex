package com.filote.cosmin1.metrorex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.filote.cosmin1.metrorex.R;

import pl.droidsonroids.gif.GifDrawable;

public class StartGIf extends AppCompatActivity {

    GifDrawable gifDrawable;
    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_gif);
        splash = (ImageView)findViewById(R.id.splashscreen);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(2300);

        splash.setAnimation(fadeIn);



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
