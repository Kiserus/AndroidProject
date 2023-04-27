package game.space.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

    WindowMetrics windowMetrics = new WindowMetrics().g;

    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        ImageView fighter = findViewById(R.id.fighter);

        RelativeLayout.LayoutParams initialPosition = (RelativeLayout.LayoutParams) fighter.getLayoutParams();
        initialPosition.leftMargin = width / 2 - initialPosition.width / 2;
        initialPosition.topMargin = height - initialPosition.height / 2;
        Log.i(TAG, "ERROR" + initialPosition.leftMargin + " " + initialPosition.topMargin);
        fighter.setLayoutParams(initialPosition);
        fighter.setOnTouchListener(onTouchListener());
    }
    // Внезапный выход из игры
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
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }
}