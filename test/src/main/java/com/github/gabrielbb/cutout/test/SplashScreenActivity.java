package com.github.gabrielbb.cutout.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.github.gabrielbb.cutout.test.static_icon.EntryActivity;

public class SplashScreenActivity extends Activity {
String bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
      //  Fabric.with(SplashScreenActivity.this, new Crashlytics());
    /*  Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });
        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
       */
    new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            // This method will be executed once the timer is over
           Intent i = new Intent(SplashScreenActivity.this, EntryActivity.class);
             startActivity(i);
             finish();
   }}, 2000);
   }
}
