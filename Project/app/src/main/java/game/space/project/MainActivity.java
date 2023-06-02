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

        Asteroid() {
            Random random = new Random();
            this.diametr = random.nextInt(800) + 100;

            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.asteroid_common);
            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(diametr, diametr);
            imageView.setLayoutParams(relativeLayoutParams);
            imageView.setY(-diametr);
            imageView.setX(random.nextInt(width + 2 * diametr) - diametr);
        }
    } // конец класса Asteroid

    boolean spaceshipIsAlive = true;

    class Spaceship {
        ImageView spaceshipImageView;

        Spaceship(ImageView imageView) {
            spaceshipImageView = imageView;
        }

        class Bullet {
            int bulletDiametr = 20;
            ImageView bulletImageView;

            Bullet() {
                bulletImageView = new ImageView(context);
                bulletImageView.setImageResource(R.drawable.bullet);
                RelativeLayout.LayoutParams bulletLayoutParams = new RelativeLayout.LayoutParams(bulletDiametr, bulletDiametr);
                bulletImageView.setLayoutParams(bulletLayoutParams);
                bulletImageView.setX(spaceshipImageView.getX() + spaceshipImageView.getWidth() / 2 - bulletDiametr / 2);
                bulletImageView.setY(spaceshipImageView.getY());
            }
        }

        ArrayList<Bullet> bulletsArrayList = new ArrayList<>(); // хранит пули
        ArrayList<Asteroid> asteroidArrayList = new ArrayList<>(); // хранит астероиды

        class GameTask extends AsyncTask<Void, ImageView, Void> { // создаём новые пули в UI

            ArrayList<Integer> bulletsDelatelist = new ArrayList<Integer>(); // хранит индексы пуль, которые нужно удалить
            ArrayList<Integer> delateIndex = new ArrayList<>(); // хранит индексы астероидов, которые нужно удалить

            Thread asteroidMovement = new Thread() {
                @Override
                public void run() {
                    while (spaceshipIsAlive) {
                        for (int i = 0; i < asteroidArrayList.size(); ++i) {
                            if (asteroidArrayList.get(i).imageView.getY() - asteroidArrayList.get(i).diametr < height) {
                                asteroidArrayList.get(i).imageView.setY(asteroidArrayList.get(i).imageView.getY() + 1);
                            } else {
                                // записываем индекс удаляемого астероида
                                delateIndex.add(i);
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

            Thread bulletMovement = new Thread() {
                @Override
                public void run() {
                    while (spaceshipIsAlive) {
                        for (int i = 0; i < bulletsArrayList.size(); ++i) {
                            if (bulletsArrayList.get(i).bulletImageView.getY() + bulletsArrayList.get(i).bulletDiametr > 0) {
                                bulletsArrayList.get(i).bulletImageView.setY(bulletsArrayList.get(i).bulletImageView.getY() - 1);
                            } else {
                                // записываем индекс удаляемой пули
                                bulletsDelatelist.add(i);
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

            @Override
            protected void onProgressUpdate(ImageView... images) {
                mainLayout.addView(images[0]); // добавляем в UI изображение

                while (!bulletsDelatelist.isEmpty()) { // удаляем вышедшие за экран пули
                    mainLayout.removeView(bulletsArrayList.get(bulletsDelatelist.get(0)).bulletImageView);
                    bulletsArrayList.remove(bulletsDelatelist.get(0));
                    bulletsDelatelist.remove(0);
                }
                while (!delateIndex.isEmpty()) { // удаляем вышедшие за экран астероиды
                    mainLayout.removeView(asteroidArrayList.get(delateIndex.get(0)).imageView);
                    asteroidArrayList.remove(delateIndex.get(0));
                    delateIndex.remove(0);
                }
                super.onProgressUpdate();
            }

            @Override
            protected Void doInBackground(Void... params) {
                bulletMovement.start();
                asteroidMovement.start();
                while (spaceshipIsAlive) {
                    Bullet bullet = new Bullet();
                    bulletsArrayList.add(bullet);
                    publishProgress(bullet.bulletImageView); // запрос на добавление
                    if (new Random().nextInt(10) + 1 <= 2) {
                        Asteroid asteroid = new Asteroid();
                        asteroidArrayList.add(asteroid);
                        publishProgress(asteroid.imageView); // запрос на добавление
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        } // конец класса Spaceship
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
        ImageView SpaceshipImageView = findViewById(R.id.fighter);
        //  устанавливаем начальное положение
        RelativeLayout.LayoutParams initialPosition = new RelativeLayout.LayoutParams(SpaceshipImageView.getLayoutParams());
        initialPosition.leftMargin = (game.width / 2 - SpaceshipImageView.getWidth() / 2);
        initialPosition.topMargin = (int) (game.height * 0.9);
        SpaceshipImageView.setLayoutParams(initialPosition); // устанавливаем начальное положение корабля
        SpaceshipImageView.setOnTouchListener(onTouchListener());
        Thread gameThread = new Thread() {
            @Override
            public void run() {
                GameView.Spaceship spaceship = game.new Spaceship(SpaceshipImageView);
                GameView.Spaceship.GameTask gameTask = spaceship.new GameTask();
                gameTask.execute();
                super.run();
            }
        };
        gameThread.start();
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