package game.space.project;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

class GameView {
    Activity activity;
    Context context;
    int width;
    int height;
    RelativeLayout mainLayout;

    GameView(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mainLayout = activity.findViewById(R.id.main);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    public class Asteroid {
        int diametr;
        ImageView imageView;
        RelativeLayout relativeLayout;
        RelativeLayout.LayoutParams relativeLayoutParams;

        Asteroid() {
            Random random = new Random();
            this.diametr = random.nextInt(1000) + 100;

            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.asteroid_common);
            relativeLayout = (RelativeLayout) activity.findViewById(R.id.main);
            relativeLayoutParams = new RelativeLayout.LayoutParams(diametr, diametr);

            imageView.setLayoutParams(relativeLayoutParams);
            imageView.setY(-diametr);
            imageView.setX(random.nextInt(width + 2 * diametr) - diametr);
        }
    } // конец класса Asteroid

    boolean planeIsAlive = true;

    class CreateAsteroids extends AsyncTask<Void, Asteroid, Void> { // создаём новые астероиды в UI

        ArrayList<Asteroid> asteroidArrayList = new ArrayList<Asteroid>();

        Thread asteroidMovement = new Thread() {
            @Override
            public void run() {
                while (planeIsAlive) {
                    for (int i = 0; i < asteroidArrayList.size(); ++i) {
                        if (asteroidArrayList.get(i).imageView.getY() + asteroidArrayList.get(i).diametr < height) {
                            asteroidArrayList.get(i).imageView.setY(asteroidArrayList.get(i).imageView.getY() + 1);
                        } else {
                            // удаление imageView
                            asteroidArrayList.get(i).imageView.setImageDrawable(null);
                            asteroidArrayList.remove(i);
                            i--;
                        }
                    }
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        @Override
        protected void onProgressUpdate(Asteroid... asteroids) {
            mainLayout.addView(asteroids[0].imageView); // добавляем в UI изображение
            super.onProgressUpdate();
        }

        @Override
        protected Void doInBackground(Void... params) {
            asteroidMovement.start();
            while (planeIsAlive) {
                Asteroid asteroid = new Asteroid();
                asteroidArrayList.add(asteroid);
                publishProgress(asteroid);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}


public class MainActivity extends AppCompatActivity {

    private ViewGroup mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // фиксируем ориентацию экрана
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        GameView game = new GameView(this, this);
        ImageView fighter = findViewById(R.id.fighter);
        //  устанавливаем начальное положение
        RelativeLayout.LayoutParams initialPosition = new RelativeLayout.LayoutParams(fighter.getLayoutParams());
        initialPosition.leftMargin = (game.width / 2 - fighter.getWidth() / 2);
        initialPosition.topMargin = (int) (game.height * 0.9);
        fighter.setLayoutParams(initialPosition); // устанавливаем начальное положение корабля
        fighter.setOnTouchListener(onTouchListener());
        //game.setThreadSpaceship(fighter); // осуществление движение корабля
        GameView.CreateAsteroids asteroidTask = game.new CreateAsteroids();
        asteroidTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //  Управление кораблём

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