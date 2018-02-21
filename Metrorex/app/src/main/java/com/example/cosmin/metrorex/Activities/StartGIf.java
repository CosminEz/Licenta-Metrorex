package com.example.cosmin.metrorex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.cosmin.metrorex.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class StartGIf extends AppCompatActivity {

    GifDrawable gifDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_gif);
        GifImageView gif = (GifImageView)findViewById(R.id.gif);








    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
