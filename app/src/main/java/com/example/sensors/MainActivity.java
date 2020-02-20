package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public Ball ball=null;
    public Handler handler=new Handler();
    public Timer timer= null;
    public TimerTask timerTask=null;
    public int kepernyoSzelesseg;
    public int kepernyoMagassag;
    public android.graphics.PointF ballPos,ballSpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //teljesc képernyő, title bar eltűnik
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(0xFFFFFFFF, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout frameLayout=findViewById(R.id.frameLayout);

        //képernyőméret lekérése
        Display display=getWindowManager().getDefaultDisplay();
        kepernyoSzelesseg=display.getWidth();
        kepernyoMagassag=display.getHeight();
        ballPos=new android.graphics.PointF();
        ballSpd=new android.graphics.PointF();
        //labda pozicio és sebesség beállítása
        ballPos.x=kepernyoSzelesseg/2;
        ballPos.y=kepernyoMagassag/2;
        ballSpd.x=0;
        ballSpd.y=0;

        //labda elkészítése
        ball=new Ball(MainActivity.this,ballPos.x,ballPos.y,25);

        frameLayout.addView(ball);//labda hozzáadása a képernyőhöz
        frameLayout.invalidate();//meghívja az onDraw metódust a ball.java osztályban

        //sebességmérő esemény létrehozása
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //golyó sebességének változtatása
                ballSpd.x=-event.values[0];
                ballSpd.y=event.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        },((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_NORMAL);
        //onTouch Listener
        frameLayout.setOnTouchListener(new android.view.View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ballPos.x=event.getX();
                ballPos.y=event.getY();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        timer =new Timer();
        timerTask =new TimerTask() {
            @Override
            public void run() {
                ballPos.x+=ballSpd.x;
                ballPos.y+=ballSpd.y;

                //ha kiesik oldalról akkor a másik oldalról jöjjön elő
                if (ballPos.x>kepernyoSzelesseg) ballPos.x=0;
                if (ballPos.y>kepernyoMagassag) ballPos.y=0;
                if (ballPos.x<0) ballPos.x=kepernyoSzelesseg;
                if (ballPos.y<0) ballPos.y=kepernyoMagassag;

                ball.x=ballPos.x;
                ball.y=ballPos.y;

                ball.invalidate();
            }
        };
        timer.schedule(timerTask,10,10);
        super.onResume();
    }
}
