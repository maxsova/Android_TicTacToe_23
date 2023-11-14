package com.example.tictactoe_20224612_3;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
public class MainActivity extends AppCompatActivity {
    Button startButton,highScoresButton, exitButton;//declare button objects
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Handle onClick events from here
        startButton = (Button) findViewById(R.id.startButton);
        highScoresButton = (Button) findViewById(R.id.highScoresButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                gameScreen();
            }
        });

        highScoresButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                highScoresScreen();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }
    //Open other activities
    public void gameScreen(){
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }
    public void highScoresScreen(){
        Intent intent = new Intent(this, highScores.class);
        startActivity(intent);
    }
}