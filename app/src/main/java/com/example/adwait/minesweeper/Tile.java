package com.example.adwait.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

/**
 * Created by Adwait on 2/27/2016.
 */
public class Tile extends Button {

    boolean tileCovered;
    boolean tileMine;
    boolean tileFlagged;
    boolean tileQuestionMarked;
    boolean tileClickable;
    int surroundingTiles;

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setDefaults()
    {
        tileCovered = true;
        tileMine = false;
        tileFlagged = false;
        tileQuestionMarked = false;
        tileClickable = true;
        surroundingTiles = 0;
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_size));
        this.setBackgroundResource(R.drawable.closed_tile);
        setBoldFont();
    }


    public void adjustSurroundingMines(int number)
    {
        this.setBackgroundResource(R.drawable.open_tile);

        updateMines(number);
    }


    public void setMineIcon(boolean enabled)
    {


        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.open_mine);
        }
        else
        {
            this.setBackgroundResource(R.drawable.selected_mine);
            this.setTextColor(Color.BLACK);
        }
    }


    public void setFlagIcon()
    {
            this.setBackgroundResource(R.drawable.flagged_tile);

    }


    public void setQuestionMarkIcon(boolean enabled)
    {
        this.setText("?");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.closed_tile);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }


    public void setBlockAsDisabled(boolean enabled)
    {
        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.open_tile);
        }
        else
        {
            this.setBackgroundResource(R.drawable.closed_tile);
        }
    }


    public void clearAllIcons()
    {
        this.setText("");
    }


    private void setBoldFont()
    {
        this.setTypeface(null, Typeface.BOLD);
    }


    public void OpenBlock()
    {

        if (!tileCovered)
            return;

        setBlockAsDisabled(false);

        tileCovered = false;


        if (hasMine())
        {
            setMineIcon(false);
        }
        else
        {
            adjustSurroundingMines(surroundingTiles);
        }
    }


    public void updateMines(int text)
    {
        if (text != 0)
        {
            this.setText(Integer.toString(text));

            switch (text)
            {
                case 1:
                    this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }
    }

    public void plantMine()
    {
        tileMine = true;
    }


    public void triggerMine()
    {
        setMineIcon(true);
        this.setTextColor(Color.RED);
    }


    public boolean isTileCovered()
    {
        return tileCovered;
    }


    public boolean hasMine()
    {
        return tileMine;
    }


    public void setSurroundingTiles(int number)
    {
        surroundingTiles = number;
    }


    public int getNumberOfMinesInSorrounding()
    {
        return surroundingTiles;
    }


    public boolean isTileFlagged()
    {
        return tileFlagged;
    }


    public void setTileFlagged(boolean tileFlagged)
    {
        this.tileFlagged = tileFlagged;
    }


    public boolean isTileQuestionMarked()
    {
        return tileQuestionMarked;
    }


    public void setTileQuestionMarked(boolean tileQuestionMarked)
    {
        this.tileQuestionMarked = tileQuestionMarked;
    }


    public boolean isClickable()
    {
        return tileClickable;
    }


    public void setClickable(boolean clickable)
    {
        tileClickable = clickable;
    }

}
