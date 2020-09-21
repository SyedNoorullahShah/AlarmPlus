package com.alarm.android.alarmplus.ui.alarmmode;

import androidx.gridlayout.widget.GridLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alarm.android.alarmplus.R;
import com.alarm.android.alarmplus.game.AI_Move;
import com.alarm.android.alarmplus.game.AI_Player;
import com.alarm.android.alarmplus.game.Players;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.alarm.android.alarmplus.game.Players.NO_VALUE;
import static com.alarm.android.alarmplus.game.Players.PLAYER_AI;
import static com.alarm.android.alarmplus.game.Players.PLAYER_HUMAN;

public class GameActivity extends AlarmModeActivity {

    private ImageButton[][] slots;
    private int turns = 2;
    public static int rounds = 1;
    private static int tot_rounds;
    public static String difficulty;
    private AI_Player ai_player;
    public static final String TIE = "tie";
    private boolean isTied = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        slots = new ImageButton[3][3];
        ai_player = new AI_Player(this);
        setGame();

        //first move for AI
        Random random = new Random();
        int y = random.nextInt(3);
        int x = random.nextInt(3);

        fillSlot(slots[y][x], PLAYER_AI);

    }


    public static void setGameValues(int rounds, String level) {
        tot_rounds = rounds;
        difficulty = level;
    }

    private void playAI() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AI_Move move = ai_player.playMove();

                fillSlot(slots[move.getY()][move.getX()], PLAYER_AI);
                if (turns >= 5) {
                    if (checkWinner())
                        return;
                }

                turns++;

                if (turns > 9) {
                    drawMatch();
                }

            }
        }, 1500);

    }

    private void setGame() {
        GridLayout board = findViewById(R.id.gridLayout);
        int row = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                slots[i][j] = new ImageButton(this);
                final ImageButton slot = slots[i][j];
                slot.setTag(NO_VALUE);
                setSlotStyle(slot);
                board.addView(slot, row + j);
                slot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("noor", "onClick: " + turns + "  " + rounds + "  " + tot_rounds);
                        startGame(slot);
                    }
                });
            }
            row += 3;
        }
    }

    private void startGame(ImageButton slot) {
        if ((turns % 2 != 0) || slot.getDrawable() != null) {       //exit if slot is already taken OR if it is AI's turn
            Log.d("noor", "startGame: ");
            return;
        }

        fillSlot(slot, PLAYER_HUMAN);

        if (turns >= 5) {
            if (checkWinner())
                return;
        }

        turns++;

        if (turns > 9) {
            drawMatch();
            return;
        }

        playAI();
    }

    private void drawMatch() {
        Toast.makeText(this, "Its a DRAW !", Toast.LENGTH_SHORT).show();
        rounds++;
        if (rounds <= tot_rounds) {
            Log.d("noor", "drawMatch: ");
            resetGame();
        } else {
            Log.d("noor", "HERE2: ");
            rounds = 1;
            closeAlarm();
        }
    }

    private boolean checkWinner() {
        String status = gameStatus();
        if (status != null && !status.equals(NO_VALUE)) {
            announceWinner(status);
            rounds++;
            if (rounds <= tot_rounds) {
                resetGame();

            } else {
                rounds = 1;
                closeAlarm();

            }
            return true;
        }
        return false;
    }

    private void announceWinner(String status) {
        if (status.equals(PLAYER_HUMAN))
            Toast.makeText(this, "Wow! Didn't expect you to win in sleep mode", Toast.LENGTH_SHORT).show();
        else if (status.equals(PLAYER_AI))
            Toast.makeText(this, "Computer wins! It's alright, you just woke up", Toast.LENGTH_SHORT).show();
    }


    private void resetGame() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                turns = 2;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                final ImageButton s1 = slots[i][j];
                                s1.setImageDrawable(null);
                                s1.setBackgroundColor(Color.TRANSPARENT);
                                s1.setTag(NO_VALUE);

                            }
                        }

                        Random random = new Random();
                        int y = random.nextInt(3);
                        int x = random.nextInt(3);

                        fillSlot(slots[y][x], PLAYER_AI);

                    }
                });
            }
        }, 1000);

    }


    public String gameStatus() {
        ImageButton s1, s2, s3;
        String status;
        isTied = true;

        for (int pos = 0; pos < 3; pos++) {
            s1 = slots[pos][pos];
            s2 = slots[pos][(pos + 1) % 3];
            s3 = slots[pos][(pos + 2) % 3];
            status = check(s1, s2, s3);
            if (status != null && !status.equals(NO_VALUE)) {
                return status;
            }

            s2 = slots[(pos + 1) % 3][pos];
            s3 = slots[(pos + 2) % 3][pos];
            status = check(s1, s2, s3);
            if (status != null && !status.equals(NO_VALUE)) {
                return status;
            }

            if (pos != 1) {
                s1 = slots[2 - pos][0];
                s2 = slots[1][1];
                s3 = slots[pos][2];
                status = check(s1, s2, s3);
                if (status != null && !status.equals(NO_VALUE)) {
                    return status;
                }

            }
        }
        return isTied == false ? null : TIE;
    }

    private String check(ImageButton s1, ImageButton s2, ImageButton s3) {
        if (!s1.getTag().equals(NO_VALUE) && !s2.getTag().equals(NO_VALUE) && !s3.getTag().equals(NO_VALUE)) {
            if (s1.getTag().equals(s2.getTag()) && s2.getTag().equals(s3.getTag())) {

                return s1.getTag().toString();
            } else {
                return null;
            }
        } else {
            isTied = false;
            return NO_VALUE;
        }

    }

    private void fillSlot(ImageButton slot, String player) {
        if (player.equals(PLAYER_AI)) {
            slot.setImageResource(R.drawable.x);
            slot.setTag(Players.PLAYER_AI);

        } else {
            slot.setImageResource(R.drawable.o);
            slot.setTag(Players.PLAYER_HUMAN);
        }
    }

    private void setSlotStyle(ImageButton character) {
        final float scale = getResources().getDisplayMetrics().density;
        int dp = 116;
        int pixels = (int) (dp * scale + 0.5f);
        character.setLayoutParams(new ViewGroup.LayoutParams(pixels, pixels));
        character.setBackgroundColor(Color.TRANSPARENT);
    }

    public void enterValue(int y, int x, String player) {
        slots[y][x].setTag(player);
    }

    public String getValue(int y, int x) {
        return slots[y][x].getTag().toString();
    }

}
