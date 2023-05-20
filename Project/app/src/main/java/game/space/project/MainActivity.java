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

class Asteroid {
    Context context;
    void setContext(Context current) {
        this.context = current;
    }
    Random random = new Random();
    int radius = random.nextInt(10) + 5;
    int x;
    int y;
    int speed;
    Bitmap bitmap;
    void setSize() {
        Bitmap buffer = BitmapFactory.decodeResource(context.getResources(), R.drawable.asteroid_common);
        bitmap = Bitmap.createScaledBitmap(buffer, (int)(radius), (int)(radius), false);
        buffer.recycle();
    }

    void create(Asteroid asteroid) {
        ImageView imageView = new ImageView(context);

    }
}

class GameView {
    Context context;
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;

    GameView(Context current) {
        this.context = current;
    }

    ArrayList <Asteroid> asteroids = new ArrayList<Asteroid>();

    Thread game = new Thread() {
        @Override
        public void run() {
            try {
                Asteroid a = new Asteroid();
                a.setSize(context);
                asteroids.add(a);
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    };
    int getWidth() {
        return width;
    }
}


public class MainActivity extends AppCompatActivity {

    boolean planeIsAlive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        GameView game = new GameView(this);
        ImageView fighter = findViewById(R.id.fighter);
        //  устанавливаем начальное положение
        RelativeLayout.LayoutParams initialPosition = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initialPosition.leftMargin = (game.width / 2 - fighter.getWidth() / 2);
        initialPosition.topMargin = (int)(game.height * 0.9);
        fighter.setLayoutParams(initialPosition);
        fighter.setOnTouchListener(onTouchListener());
    }
    //  Внезапный выход из игры
    @Override
    protected void onDestroy() {
        super.onDestroy();
        planeIsAlive = false;
    }

    //  Управление
    private ViewGroup mainLayout;
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