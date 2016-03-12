package com.example.adwait.minesweeper;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView timerView,mineCountView;
    ImageButton smileyImageView;
    TableLayout mineView;

    Tile tiles[][];
    int cellSize =100;

     int rowNum = 12,rowOffset=1;  // number of rows in the game
     int colNum = 8,colOffset=1;   // number of columns in the game
     int mineCount = 30;


     Handler timer = new Handler();
     int timeElapsed = 0;

    boolean timerInitalizerFlag,mineFlag,gameState;

    int minesToFind; 

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        mineCountView = (TextView) findViewById(R.id.MineCount);
        timerView = (TextView) findViewById(R.id.Timer);

        smileyImageView = (ImageButton) findViewById(R.id.Smiley);
        smileyImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                reset();
                startGame();
            }
        });

        mineView = (TableLayout)findViewById(R.id.MineField);

        displayMessage("Click smiley to start New Game", 2000, true, false);
    }

    private void startGame()
    {
        setupMines();
        displayMines();
        minesToFind = mineCount;
        gameState = false;
        timeElapsed = 0;
    }

    public void startTimer()
    {
        if (timeElapsed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer()
    {
        timer.removeCallbacks(updateTimeElasped);
    }



    private void displayMines()
    {
        for (int i = 1; i < (rowNum + rowOffset); i++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(mineView.getLayoutParams());
            for (int j = 1; j < (colNum + colOffset); j++)
            {
                tiles[i][j].setLayoutParams(new TableRow.LayoutParams(cellSize,cellSize));
                tableRow.addView(tiles[i][j]);
            }
            mineView.addView(tableRow);
        }
    }



    private void setupMines()
    {

        int rowBufferField=rowNum + rowOffset + colOffset;
        int colBufferField=colNum + rowOffset + colOffset;

        tiles = new Tile[rowBufferField][colBufferField];

        for (int i = 0; i < rowBufferField; i++)
        {
            for (int j = 0; j < colBufferField; j++)
            {
                tiles[i][j] = new Tile(this);
                tiles[i][j].setDefaults();

                final int thisRow = i;
                final int thisColumn = j;

                tiles[i][j].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (!timerInitalizerFlag)
                        {
                            startTimer();
                            timerInitalizerFlag = true;
                        }

                        if (!mineFlag)
                        {
                            mineFlag = true;
                            setMines(thisRow, thisColumn);
                        }

                        if (!tiles[thisRow][thisColumn].isTileFlagged())
                        {
                            patternUnblock(thisRow, thisColumn);

                            if (tiles[thisRow][thisColumn].hasMine())
                            {
                                exitGame(thisRow,thisColumn);
                            }

                            if (checkGameStatus())
                            {
                                winnner();
                            }
                        }
                    }
                });

                tiles[i][j].setOnLongClickListener(new View.OnLongClickListener()
                {
                    public boolean onLongClick(View view)
                    {

                        if (!tiles[thisRow][thisColumn].isTileCovered() && (tiles[thisRow][thisColumn].getNumberOfMinesInSorrounding() > 0) && !gameState)
                        {
                            int nearbyFlaggedBlocks = 0;
                            for (int i = -1; i < 2; i++)
                            {
                                for (int j = -1; j < 2; j++)
                                {
                                    if (tiles[thisRow + i][thisColumn + j].isTileFlagged())
                                    {
                                        nearbyFlaggedBlocks++;
                                    }
                                }
                            }

                            if (nearbyFlaggedBlocks == tiles[thisRow][thisColumn].getNumberOfMinesInSorrounding())
                            {
                                for (int i = -1; i < 2; i++)
                                {
                                    for (int j = -1; j < 2; j++)
                                    {

                                        if (!tiles[thisRow + i][thisColumn + j].isTileFlagged())
                                        {

                                            patternUnblock(thisRow + i, thisColumn + j);


                                            if (tiles[thisRow + i][thisColumn + j].hasMine())
                                            {

                                                exitGame(thisRow + i, thisColumn + j);
                                            }


                                            if (checkGameStatus())
                                            {

                                                winnner();
                                            }
                                        }
                                    }
                                }
                            }

                            return true;
                        }


                        if (tiles[thisRow][thisColumn].isClickable() && (tiles[thisRow][thisColumn].isEnabled() || tiles[thisRow][thisColumn].isTileFlagged()))
                        {
                            if (!tiles[thisRow][thisColumn].isTileFlagged() && !tiles[thisRow][thisColumn].isTileQuestionMarked())
                            {
                                tiles[thisRow][thisColumn].setBlockAsDisabled(false);
                                tiles[thisRow][thisColumn].setFlagIcon();
                                tiles[thisRow][thisColumn].setTileFlagged(true);
                                minesToFind--;
                                updateMineCountDisplay();
                            }
                            else if (!tiles[thisRow][thisColumn].isTileQuestionMarked())
                            {
                                tiles[thisRow][thisColumn].setBlockAsDisabled(true);
                                tiles[thisRow][thisColumn].setQuestionMarkIcon(true);
                                tiles[thisRow][thisColumn].setTileFlagged(false);
                                tiles[thisRow][thisColumn].setTileQuestionMarked(true);
                                minesToFind++; // increase mine count
                                updateMineCountDisplay();
                            }
                            else
                            {
                                tiles[thisRow][thisColumn].setBlockAsDisabled(true);
                                tiles[thisRow][thisColumn].clearAllIcons();
                                tiles[thisRow][thisColumn].setTileQuestionMarked(false);
                                if (tiles[thisRow][thisColumn].isTileFlagged())
                                {
                                    minesToFind++;
                                    updateMineCountDisplay();
                                }
                                tiles[thisRow][thisColumn].setTileFlagged(false);
                            }

                            updateMineCountDisplay();
                        }

                        return true;
                    }
                });
            }
        }
    }

    private boolean checkGameStatus()
    {
        for (int i = 1; i < rowNum + 1; i++)
        {
            for (int j = 1; j < colNum + 1; j++)
            {
                if (!tiles[i][j].hasMine() && tiles[i][j].isTileCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMineCountDisplay()
    {
        if (minesToFind < 0)
        {
            mineCountView.setText(Integer.toString(minesToFind));
        }
        else if (minesToFind < 10)
        {
            mineCountView.setText("00" + Integer.toString(minesToFind));
        }
        else if (minesToFind < 100)
        {
            mineCountView.setText("0" + Integer.toString(minesToFind));
        }
        else
        {
            mineCountView.setText(Integer.toString(minesToFind));
        }
    }

    private void reset()
    {
        stopTimer();
        timerView.setText("000");
        mineCountView.setText("030");
        smileyImageView.setBackgroundResource(R.drawable.happy_smiley);
        mineView.removeAllViews();
        timerInitalizerFlag = false;
        mineFlag = false;
        gameState = false;
        minesToFind = 0;
    }

    private void winnner()
    {
        stopTimer();
        timerInitalizerFlag = false;
        gameState = true;
        minesToFind = 0;
        smileyImageView.setBackgroundResource(R.drawable.win);

        updateMineCountDisplay();

        for (int i = 1; i < rowNum + 1; i++)
        {
            for (int j = 1; j < colNum + 1; j++)
            {
                tiles[i][j].setClickable(false);
                if (tiles[i][j].hasMine())
                {
                    tiles[i][j].setBlockAsDisabled(false);
                    tiles[i][j].setFlagIcon();
                }
            }
        }

        displayMessage("Congratulations \n You won in " + Integer.toString(timeElapsed) + " seconds!", 1000, false, true);
    }

    private void exitGame(int currentRow, int currentColumn)
    {
        gameState = true;
        stopTimer();
        timerInitalizerFlag = false;
        smileyImageView.setBackgroundResource(R.drawable.sad_smiley);

        for (int i = 1; i < (rowNum + rowOffset); i++)
        {
            for (int j = 1; j < (colNum + colOffset); j++)
            {
                if (tiles[i][j].hasMine() && !tiles[i][j].isTileFlagged())
                    tiles[i][j].setMineIcon(false);


                if (!tiles[i][j].hasMine() && tiles[i][j].isTileFlagged())
                    tiles[i][j].setFlagIcon();


                if (tiles[i][j].isTileFlagged())
                    tiles[i][j].setClickable(false);


                tiles[i][j].setEnabled(false);
            }
        }

        tiles[currentRow][currentColumn].triggerMine();

        displayMessage(" Oops !!! you clicked on a mine \n You tried for " + Integer.toString(timeElapsed) + " seconds! \n Click on the smiley to try again", 3000, false, false);
    }


    private void setMines(int currentRow, int currentColumn)
    {
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int i = 0; i < mineCount; i++)
        {
            mineRow = rand.nextInt(colNum);
            mineColumn = rand.nextInt(rowNum);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                if (tiles[mineColumn + 1][mineRow + 1].hasMine())
                {
                    i--;
                }
                tiles[mineColumn + 1][mineRow + 1].plantMine();
            }
            else
            {
                i--;
            }
        }

        int nearByMineCount;

        for (int i = 0; i < rowNum + 2; i++)
        {
            for (int j = 0; j < colNum + 2; j++)
            {

                nearByMineCount = 0;
                if ((i != 0) && (i != (rowNum + 1)) && (j != 0) && (j != (colNum + 1)))
                {

                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (tiles[i + previousRow][j + previousColumn].hasMine())
                            {
                                nearByMineCount++;
                            }
                        }
                    }

                    tiles[i][j].setSurroundingTiles(nearByMineCount);
                }
                else
                {
                    tiles[i][j].setSurroundingTiles(9);
                    tiles[i][j].OpenBlock();
                }
            }
        }
    }

    private void patternUnblock(int rowClicked, int columnClicked)
    {
        if (tiles[rowClicked][columnClicked].hasMine() || tiles[rowClicked][columnClicked].isTileFlagged())
        {
            return;
        }

        tiles[rowClicked][columnClicked].OpenBlock();

        if (tiles[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                // check all the above checked conditions
                // if met then open subsequent blocks
                if (tiles[rowClicked + i - 1][columnClicked + j - 1].isTileCovered()
                        && (rowClicked + i - 1 > 0) && (columnClicked + j - 1 > 0)
                        && (rowClicked + i - 1 < rowNum + 1) && (columnClicked + j - 1 < colNum + 1))
                {
                    patternUnblock(rowClicked + i - 1, columnClicked + j - 1 );
                }
            }
        }
        return;
    }


    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++timeElapsed;

            if (timeElapsed < 10)
            {
                timerView.setText("00" + Integer.toString(timeElapsed));
            }
            else if (timeElapsed < 100)
            {
                timerView.setText("0" + Integer.toString(timeElapsed));
            }
            else
            {
                timerView.setText(Integer.toString(timeElapsed));
            }

            timer.postAtTime(this, currentMilliseconds);

            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

    private void displayMessage(String message, int milliseconds, boolean useSmileImage, boolean useCoolImage)
    {
        Toast dialog = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        dialog.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout dialogView = (LinearLayout) dialog.getView();
        ImageView coolImage = new ImageView(getApplicationContext());
        if (useSmileImage)
        {
            coolImage.setImageResource(R.drawable.happy_smiley);
        }
        else if (useCoolImage)
        {
            coolImage.setImageResource(R.drawable.win);
        }
        else
        {
            coolImage.setImageResource(R.drawable.sad_smiley);
        }
        dialogView.addView(coolImage, 0);
        dialog.setDuration(milliseconds);
        dialog.show();
    }
}
