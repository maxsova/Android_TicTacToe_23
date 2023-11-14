package com.example.tictactoe_20224612_3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.database.Cursor;
import java.util.ArrayList;

public class highScores extends AppCompatActivity {
    SQLiteDatabase db; // Declare the database object
    Button playAgainButton, exitButton; // Declare button objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        // Initialize the database
        db = openOrCreateDatabase("ScoresDB", MODE_PRIVATE, null);

        // Query the database for the top 7 high scores (lowest move count, latest games)
        String selectQuery = "SELECT name, score, datetime " +
                "FROM HighScores " +
                "WHERE name IS NOT NULL AND name <> '' " +
                "ORDER BY score ASC, id DESC " +
                "LIMIT 7;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<String> top7Scores = new ArrayList<>();
        // Read query results
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {//start reading iteration
                        String playerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        int playerScore = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
                        String datetimeString = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                        // Create a string representation of a score
                        String highScoreString = playerName + ",  " + playerScore + " moves " + datetimeString;
                        top7Scores.add(highScoreString);
                    } while (cursor.moveToNext());//move cursor to another line
                }
            } catch (Exception e) {
                // Handle any exceptions that may occur while retrieving data
                Log.e("CursorError", "Error retrieving data from cursor: " + e.getMessage());
            } finally {
                cursor.close(); // Close the cursor
            }
        }

        // Display the top 7 high scores in a ListView
        ListView listView = findViewById(R.id.highScoresListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, top7Scores);
        listView.setAdapter(adapter);

        // Handle onClick events
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gameScreen();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    // Play again
    public void gameScreen() {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }
}

