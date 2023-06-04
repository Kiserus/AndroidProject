package game.space.project;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import game.space.project.R;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mainLayout;
    public static boolean spaceshipIsAlive = true;
    int width, height;
    Context context;
    ImageView SpaceshipImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // фиксируем ориентацию экрана
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        context = mainLayout.getContext();

        /*SpaceshipImageView = findViewById(R.id.fighter);
        //  устанавливаем начальное положение
        RelativeLayout.LayoutParams initialPosition = new RelativeLayout.LayoutParams(SpaceshipImageView.getLayoutParams());
        initialPosition.leftMargin = (width / 2 - 70);
        initialPosition.topMargin = (int) (height * 0.9);
        SpaceshipImageView.setLayoutParams(initialPosition); // устанавливаем начальное положение корабля
        SpaceshipImageView.setOnTouchListener(onTouchListener());*/
        GameThread gameThread = new GameThread();
        gameThread.execute();
    }

    ArrayList<ImageView> bulletsArrayList = new ArrayList<>(); // хранит пули
    ArrayList<ImageView > asteroidArrayList = new ArrayList<>(); // хранит астероиды

    Set <Integer> bulletsDelatelist = new HashSet<>(); // хранит индексы пуль, которые нужно удалить
    Set <Integer> setDelateAsteroids = new HashSet<>(); // хранит индексы астероидов, которые нужно удалить

    class GameThread extends AsyncTask<Void, ImageView, Void> {

        Thread bulletMovement = new Thread() {
            @Override
            public void run() {
                while (spaceshipIsAlive) {
                    for (int i = 0; i < bulletsArrayList.size(); ++i) {
                        if (bulletsArrayList.get(i).getY() - bulletsArrayList.get(i).getWidth() > 0) {
                            bulletsArrayList.get(i).setY(bulletsArrayList.get(i).getY() - 1);
                        } else {
                            // записываем индекс удаляемой пули
                            mainLayout.removeView(bulletsArrayList.get(i));
                            //bulletsDelatelist.add(i);
                        }
                    }
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        Thread asteroidMovement = new Thread() {
            @Override
            public void run() {
                while (spaceshipIsAlive) {
                    for (int i = 0; i < asteroidArrayList.size(); ++i) {
                        if (asteroidArrayList.get(i).getY() - asteroidArrayList.get(i).getWidth() / 2 < height) {
                            asteroidArrayList.get(i).setY(asteroidArrayList.get(i).getY() + 1);
                        } else {
                            // записываем индекс удаляемого астероида
                            setDelateAsteroids.add(i);
                        }
                    }
                    try {
                        sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        boolean isAsteroid = true;

        @Override
        protected void onProgressUpdate(ImageView... values) {
            super.onProgressUpdate(values);
            mainLayout.addView(values[0]);

            ArrayList<Integer> arrayDelateBullets = new ArrayList<>(bulletsDelatelist);
            Collections.reverse(arrayDelateBullets);
            while (!arrayDelateBullets.isEmpty()) { // удаляем вышедшие за экран астероиды
                mainLayout.removeView(bulletsArrayList.get(arrayDelateBullets.get(0)));
                bulletsArrayList.remove(arrayDelateBullets.get(0));
                arrayDelateBullets.remove(0);
            }

            try {
                asteroidMovement.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<Integer> arrayDelateAsteroids = new ArrayList<>(setDelateAsteroids);
            Collections.reverse(arrayDelateAsteroids);
            while (!arrayDelateAsteroids.isEmpty()) { // удаляем вышедшие за экран астероиды
                mainLayout.removeView(asteroidArrayList.get(arrayDelateAsteroids.get(0)));
                asteroidArrayList.remove(arrayDelateAsteroids.get(0));
                arrayDelateAsteroids.remove(0);
            }


            /*for (int i = 0; i < asteroidArrayList.size(); ++i) {
                for (int j = 0; j < bulletsArrayList.size(); ++j) {
                    ImageView a = asteroidArrayList.get(i);
                    ImageView b = bulletsArrayList.get(j);
                    if (Math.sqrt(Math.pow(a.getY() + a.getWidth() / 2 - (b.getY() + b.getWidth() / 2), 2) +
                            Math.pow(a.getX() + a.getWidth() / 2 - (b.getX() + b.getWidth() / 2), 2)) <= a.getWidth() + b.getWidth()) {
                        Log.d(TAG, "a.width : " + a.getWidth() + " " + b.getWidth());
                        if (bulletMovement.isAlive())
                            bulletMovement.interrupt();
                        if (asteroidMovement.isAlive())
                            asteroidMovement.interrupt();
                        mainLayout.removeView(a);
                        mainLayout.removeView(b);
                        asteroidArrayList.remove(i);
                        bulletsArrayList.remove(j);
                        bulletMovement.start();
                        asteroidMovement.start();
                        //i--;
                        //j--;
                    }
                }
            }*/
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asteroidMovement.start();
            while (spaceshipIsAlive) {
                Random random = new Random();
                ImageView asteroid = new ImageView(context);
                int diametr = random.nextInt(800) + 100;
                RelativeLayout.LayoutParams asteroidLayoutParams = new RelativeLayout.LayoutParams(diametr, diametr);
                asteroid.setLayoutParams(asteroidLayoutParams);
                asteroid.setImageResource(R.drawable.asteroid_common);
                asteroidArrayList.add(asteroid);
                asteroid.setY(-diametr);
                asteroid.setX(random.nextInt(width + 2 * diametr) - diametr);
                publishProgress(asteroid);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

    //  Управление кораблём

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            int xDelta;
            int yDelta;

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