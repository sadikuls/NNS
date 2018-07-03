package com.sadikul.nns.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sadikul.nns.R;
import com.sadikul.nns.Utils.PreferenceManager;


public class Splash extends AppCompatActivity {

    PreferenceManager preferenceManager=null;
    ImageView logoImage;
    Handler handler = new Handler();
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_splash);


        logoImage= (ImageView) findViewById(R.id.logo);
        bar= (ProgressBar) findViewById(R.id.splashprogressBar);
// Hide the status bar.
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        logoImage.setAnimation(animation);
        new Thread(new Runnable() {

            int i = 0;
            int progressStatus = 0;

            public void run() {
                while (progressStatus <= 100) {
                    progressStatus += doWork();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            bar.setProgress(progressStatus);
                            i++;
                        }
                    });
                }
                if(progressStatus>=100){
                    startActivity(new Intent(Splash.this,MainActivity.class));
                    finish();
                }
            }
            private int doWork() {

                return i += 3;
            }

        }).start();

    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
