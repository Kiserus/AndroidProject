package game.space.project;

import static game.space.project.R.id.goToSecond;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MenuActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView goToMainActivity = findViewById(goToSecond);
        goToMainActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        goToMainActivity.setImageResource(R.drawable.buttonplay2);
                        break;
                    case MotionEvent.ACTION_UP:
                        goToMainActivity.setImageResource(R.drawable.buttonplay1);
                        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }
}