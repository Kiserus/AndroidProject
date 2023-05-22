package game.space.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

class GameView {
    Activity activity;
    Context context;
    DisplayMetrics displayMetrics;
    int width;
    int height;
    Thread gameThread;

    GameView(Context current, Activity activity) {
        this.context = current;
        this.activity = activity;
    }

    void setDisplayMetrics(Context context) {
        displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();

    class Asteroid {
        Asteroid asteroid;
        int radius;
        int x;
        int y;
        int speed; // юзаем класс вместо ImageView из-за скорости (у каждого индивидуальная)
        int time;
        ImageView imageView;
        RelativeLayout relativeLayout;
        RelativeLayout.LayoutParams relativeLayoutParams;
        Thread moving;

        Asteroid() { // add
            Random random = new Random();
            this.radius = random.nextInt(1000) + 100;
            this.x = random.nextInt(width);
            this.y = radius;
            this.speed = 1;
            this.time = 10;

            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.asteroid_common);
            relativeLayout = (RelativeLayout) activity.findViewById(R.id.main);
            relativeLayoutParams = new RelativeLayout.LayoutParams(radius, radius);

            relativeLayoutParams.leftMargin = this.x;
            relativeLayoutParams.topMargin = this.y;
            relativeLayoutParams.width = this.radius;
            relativeLayoutParams.height = this.radius;

            imageView.setLayoutParams(relativeLayoutParams); // где-то тут ошибка
            relativeLayout.addView(imageView);
        }

        void setThreadMoving() { // Движение астероида
            moving = new Thread() {
                @Override
                public void run() {
                    try {
                        while (imageView.getY() - radius < height) {
                            imageView.setY(imageView.getY() + speed);
                            sleep(time);
                        }
                        asteroids.remove(asteroid);
                        moving.interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.run();
                }
            };
        }

    }

    void setGameThread() { // Игра
        gameThread = new Thread() {
            int count = 0;
            @Override
            public void run() {
                try {
                    Asteroid a = new Asteroid();
                    a.setThreadMoving();
                    a.moving.start();
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
    }
}


public class MainActivity extends AppCompatActivity {

    boolean planeIsAlive = true;
    private ViewGroup mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        GameView game = new GameView(this, this);
        ImageView fighter = findViewById(R.id.fighter);
        //  устанавливаем начальное положение
        RelativeLayout.LayoutParams initialPosition = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initialPosition.leftMargin = (mainLayout.getWidth() / 2 - fighter.getWidth() / 2);
        initialPosition.topMargin = (int)(mainLayout.getHeight() * 0.9);
        fighter.setLayoutParams(initialPosition);
        fighter.setOnTouchListener(onTouchListener());

        setContentView(R.layout.activity_main);
        GameView gameView = new GameView(this, this);
        gameView.setDisplayMetrics(this);
        gameView.setGameThread();
        gameView.gameThread.start();
    }
    //  Внезапный выход из игры
    @Override
    protected void onDestroy() {
        super.onDestroy();
        planeIsAlive = false;
    }

    //  Управление

    private int xDelta;
    private int yDelta;

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (x - xDelta + view.getWidth() <= mainLayout.getWidth() // исключаем случай выхода за границы экрана изображения
                            && y - yDelta + view.getHeight() <= mainLayout.getHeight()
                            && x - xDelta >= 0
                            && y - yDelta >= 0) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                            layoutParams.leftMargin = x - xDelta;
                            layoutParams.topMargin = y - yDelta;
                            layoutParams.rightMargin = 0;
                            layoutParams.bottomMargin = 0;
                            view.setLayoutParams(layoutParams);
                        }
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }
}