package com.example.tictactoe_20224612_3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game extends AppCompatActivity {
    //declare class variables
    Button homeButton, exitButton;
    boolean gameOn = true;
    private int activePlayer;
    private int firstPlayer;
    private int counter = 0;
    private String winnerStr;
    private int humanMoves = 0;
    private int computerMoves = 0;
    private int winnerMoves = 0;

    int[] cellState = {2, 2, 2, 2, 2, 2, 2, 2, 2};//initialyse cell states
    // cell states are: 2-Null, 0-O, 1-X

    // store winning combinations cell references in an array
    int[][] winCells = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize activePlayer randomly (0 for human, 1 for computer)
        Random random = new Random();
        activePlayer = random.nextInt(2); // Generates 0 or 1 randomly

        //The first player always plays naughts;
        // firstPlayer will be used later to determine if the winner is human
        firstPlayer = activePlayer;
        //counter = 0;

        //text to show if the computer plays crosses or naughts
        TextView status = findViewById(R.id.status);
        status.setText("You play " + (activePlayer == 0 ? "noughts" : "crosses"));

        //Handle onClick events from here
        homeButton = (Button) findViewById(R.id.homeButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                homeScreen();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
        //Log.d("GameInfo", "Active player " + activePlayer);//checking active player
        // Only if the computer starts the game, call computerMove to make its first move
        if (activePlayer == 1) {
            computerMove();
        }// if human plays first, no code is running until the player taps on the grid
        //From this point, player and computer logic are separated into two methods.No common method
        //like nextMove is necessary in this code design.
    }
    //This method handles human player moves.
    public void playerMove(View view) {
        ImageView img = (ImageView) view;
        int playedCell = Integer.parseInt(img.getTag().toString());

        // Check if the game is already over or the tapped cell is occupied
        if (!gameOn || cellState[playedCell] != 2) {
            return;
        }
        //Select either a cross or a naught
        //First condition determines if this move is the first and is therefore a naught
        if (counter == 0) {
            cellState[playedCell] = 0; // Set it to a naught for the first move
            img.setImageResource(R.drawable.o); // Set the image for "O" for the first move
        } else {
            // For subsequent moves, alternate between noughts (O) and crosses (X)
            //depending on whether the move count is even or odd
            if (counter % 2 == 0) {
                cellState[playedCell] = 0; // Set it to a naught for even moves
                img.setImageResource(R.drawable.o);
            } else {
                cellState[playedCell] = 1; // Set it to a cross for odd moves
                img.setImageResource(R.drawable.x);
            }
        }
        //once the move is actioned, increment counter and human moves count.
        counter++;
        humanMoves++;
        //Log.d("GameInfo", "counter after human " + counter);
        // Change the active player
        activePlayer = 1;
        // Check for a win condition after each move
        if (checkWin()){
            gameOn=false;//stop the game
        }else{
            computerMove();
        }
    }
    // This method handles automated computer moves
    private void computerMove() {
        //confirm if active player is computer
        if (activePlayer == 1) {
            //initialyse random generator and a list of empty cell references
            Random random = new Random();
            List<Integer> emptyCells = new ArrayList<>();
            // Find all the empty cells and store their indices in the emptyCells list
            for (int i = 0; i < cellState.length; i++) {
                if (cellState[i] == 2) {
                    emptyCells.add(i);
                }
            }
            //get a random empty cell from the list
            int randomIndex = random.nextInt(emptyCells.size());
            int randomCell = emptyCells.get(randomIndex);

            // Check if this is the first move and is therefore a nought
            if (counter == 0) {
                cellState[randomCell] = 0; // Set cell to a nought
                ImageView img = findViewById(R.id.imageView0 + randomCell);//concatenate imageview id
                img.setImageResource(R.drawable.o); // Set the image for "O" for the first move
            }else{//the figure is now set to either cross or a nought
                Integer potentialMove = nextMove();//initialyse potentialMove object
                if (potentialMove != null) {//a cell reference returned by nextMove()
                    // Block the opponent's next move by playing in the blank cell
                    if (counter % 2 == 0) {//Set it to a nought for even moves
                        cellState[potentialMove] = 0; // Set it to a nought to block the opponent
                        ImageView img = findViewById(R.id.imageView0 + potentialMove);
                        img.setImageResource(R.drawable.o);
                    }else {//Set it to a cross for odd moves
                        cellState[potentialMove] = 1; // Set it to a cross to block the opponent
                        ImageView img = findViewById(R.id.imageView0 + potentialMove);
                        img.setImageResource(R.drawable.x);
                    }
                } else {// For subsequent moves, alternate between noughts and crosses
                    if (counter % 2 == 0) {
                        cellState[randomCell] = 0; // Set it to a nought for even moves
                        ImageView img = findViewById(R.id.imageView0 + randomCell);
                        img.setImageResource(R.drawable.o);

                    } else {
                        cellState[randomCell] = 1; // Set it to a cross for odd moves
                        ImageView img = findViewById(R.id.imageView0 + randomCell);
                        img.setImageResource(R.drawable.x);
                    }
                }
            }
        }
        //after each move, increment counters
        counter++;
        computerMoves++;
        //Log.d("GameInfo", "counter after comp " + counter);
        // Change the active player
        activePlayer = 0;
        // Check for a win condition after each move
        if (checkWin()){
            gameOn=false;//stop the game
        }
    }

    //This method checks for winning conditions after each move
    private boolean checkWin() {
        // Check for a win combination of cell references as per winCells array
        for (int i = 0; i < winCells.length; i++) {
            int[] winPosition = winCells[i];
            if (cellState[winPosition[0]] == cellState[winPosition[1]] &&
                    cellState[winPosition[1]] == cellState[winPosition[2]] &&
                    cellState[winPosition[0]] != 2) {

                //Check if when the noughts win the first player was human or computer
                if (cellState[winPosition[0]] == 0) {
                    winnerStr = (firstPlayer == 0) ? "Human" : "Computer";//winner is human
                } else {
                    winnerStr = (firstPlayer == 0) ? "Computer" : "Human";//winner is computer
                }
                //save the winner move count
                winnerMoves=winnerStr == "Human"? humanMoves : computerMoves;

                // Reset the game and open results screen
                resetGame();
                resultsScreen();
                return true; // Return true if there's a win
            }
        }
        // Check for a draw if all 9 cells are filled and no one has won
        if (counter == 9) {
            // gameOn = false;
            TextView status = findViewById(R.id.status);
            status.setText("Match Draw");

            winnerStr="Match Draw";

            // Reset the game and open results screen
            resetGame();
            resultsScreen();
            return true; // Return true if it's a draw
        }

        return false; // Return false if there's no win or draw
    }
    //This method resets game
    public void resetGame() {
        // Clear the cellState array
        Arrays.fill(cellState, 2);
        //clear status text
        TextView status = findViewById(R.id.status);
        status.setText("");
        // Ready to start again
        gameOn = true;
    }
    //Open results screen
    public void resultsScreen () {
        Intent intent = new Intent(this, results.class);
        //pass two variables to results activity
        intent.putExtra("winner", winnerStr);
        intent.putExtra("moves", winnerMoves);
        startActivity(intent);
    }
    // Open home screen
    public void homeScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
       private Integer nextMove() {
        int playersFigure = (firstPlayer == 0) ? 0 : 1; // Determine the human's figure (0 for noughts, 1 for crosses)
        int nearWinCellNo;
        int nearWinBlankNo = -1; // Initialize to -1 to track the empty cell
        for (int[] winPosition : winCells) {
            nearWinCellNo = 0; // Reset to 0 for each win position
            for (int cell : winPosition) {

                if (cellState[cell] == playersFigure) { // Check for human's figure
                    nearWinCellNo++;
                    //Log.d("GameInfo", "in a row: " + nearWinCellNo);check how many counted in a row
                } else if (cellState[cell] == 2) { // Check for blank cell
                    nearWinBlankNo = cell; // Store the index of the blank cell
                }
            }
            if (nearWinCellNo == 2 && nearWinBlankNo != -1) {
                // Log the nextMove output for blocking
                //Log.d("GameInfo", "Next move: " + nearWinBlankNo);display next move cell
                return nearWinBlankNo; // Return the index of the blank cell for a potential block
            }
        }
        return null; // Return null if no potential move found
    }
}

