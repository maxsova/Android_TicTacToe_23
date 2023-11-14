package com.example.tictactoe_20224612_3;

import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.database.Cursor;

public class results extends AppCompatActivity {
    Button saveScoreButton, playAgainButton, exitButton;
    SQLiteDatabase db; // Declare the database object
    private String playerName = "";
    private int playerMoves = 0;
    String winner = "";
    int moves = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize the database in the onCreate method
        db = openOrCreateDatabase("ScoresDB", MODE_PRIVATE, null);
       //db.execSQL("DROP TABLE IF EXISTS HighScores");
        // Create the HighScores table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS HighScores (id INTEGER PRIMARY KEY, name TEXT, score INTEGER, datetime TEXT)");

        // Handle onClick events from here
        saveScoreButton = (Button) findViewById(R.id.saveScoreButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        saveScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enteredName = findViewById(R.id.PlayerName);
                playerName = enteredName.getText().toString();

                if (playerName.isEmpty()) {//no name entered
                    // Check the HighScores table for the latest player name
                    playerName = getLatestPlayerName();

                   if (playerName == null) {//no saved names exist
                  //Colour the background red as a warning
                        enteredName.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                        return;
                    }
                    // Reset the background color to the default
                    enteredName.setBackgroundColor(getResources().getColor(android.R.color.white));
                }

                // Get the current datetime
                String datetime = getCurrentDatetime();

                // Insert a row in the database with name, score, and datetime
                insertHighScore(playerName, playerMoves, datetime);

                highScoresScreen(); // Open High Scores Screen
            }
        });

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameScreen();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();//close all activities
                System.exit(0);//exit the app
            }
        });

        // Values of the winner and winning score received from the game activity
        Intent intent = getIntent();
        winner = intent.getStringExtra("winner");
        moves = intent.getIntExtra("moves", 0);

        // Display the winner
        TextView winnerTextView = findViewById(R.id.winner);
        TextView enterNameLabel = findViewById(R.id.enterNameLabel);

        if (winner != null) {
            if (winner.equals("Human")) {
                winnerTextView.setText("You won in " + moves + " moves!");
            } else if (winner.equals("Computer")) {
                winnerTextView.setText("You lost!");
                enterNameLabel.setText("");
            } else if (winner.equals("Match Draw")) {
                winnerTextView.setText("It's a draw!");
                enterNameLabel.setText("");
            }
        }
        // Save playerMoves as a score to save in the database
        playerMoves = moves;
    }

    // Play again
    public void gameScreen() {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    // View high scores
    public void highScoresScreen() {
        Intent intent = new Intent(this, highScores.class);
        startActivity(intent);
    }

    // Method to insert a high score into the database with datetime
    public void insertHighScore(String playerName, int playerMoves, String datetime) {
        String insertQuery = "INSERT INTO HighScores (name, score, datetime) VALUES ('" + playerName + "', " + playerMoves + ", '" + datetime + "')";
        db.execSQL(insertQuery);
    }

    // Method to get the current datetime formatted in a casual way, no seconds or milliseconds
    private String getCurrentDatetime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy 'at' HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Method to get the latest player name from the HighScores table
    private String getLatestPlayerName() {
        String latestPlayerName = null;
        //look up one top name with the highest row id
        String selectQuery = "SELECT name FROM HighScores ORDER BY id DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            latestPlayerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
        }
        //return the latest player name or null if no names found
        return latestPlayerName;
    }
}
