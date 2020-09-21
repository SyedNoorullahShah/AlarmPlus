package com.alarm.android.alarmplus.game;

import com.alarm.android.alarmplus.ui.alarmmode.GameActivity;

import java.util.ArrayList;
import java.util.Random;

import static com.alarm.android.alarmplus.game.Players.*;

public class AI_Player {

    private GameActivity activeGame;

    public AI_Player(GameActivity gameActivity) {
        activeGame = gameActivity;
    }

    public AI_Move playMove() {
        return getBestMove(PLAYER_AI);
    }

    private AI_Move getBestMove(String player) {
        String status = activeGame.gameStatus();

        if (status != null) {
            Random generator = new Random();

            switch (status) {
                case PLAYER_AI:
                    int score = GameActivity.difficulty.equals("Hard") ? 10 : generator.nextInt();
                    return new AI_Move(score);
                case PLAYER_HUMAN:
                    score = GameActivity.difficulty.equals("Hard") ? -10 : generator.nextInt();
                    return new AI_Move(score);
                case GameActivity.TIE:
                    score = GameActivity.difficulty.equals("Hard") ? 0 : generator.nextInt();

                    return new AI_Move(score);
            }
        }

        ArrayList<AI_Move> moves = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (activeGame.getValue(i, j).equals(NO_VALUE)) {
                    activeGame.enterValue(i, j, player);
                    AI_Move temp_move = new AI_Move();

                    if (player.equals(PLAYER_AI)) {
                        temp_move.setScore(getBestMove(PLAYER_HUMAN).getScore());
                        temp_move.setY(i);
                        temp_move.setX(j);
                    } else {
                        temp_move.setScore(getBestMove(PLAYER_AI).getScore());
                        temp_move.setY(i);
                        temp_move.setX(j);
                    }

                    moves.add(temp_move);
                    activeGame.enterValue(i, j, NO_VALUE);
                }

            }
        }

        int bestMove = 0;
        int bestScore;

        if (player.equals(PLAYER_AI)) {
            bestScore = -100;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).getScore() > bestScore) {
                    bestScore = moves.get(i).getScore();
                    bestMove = i;
                }
            }
        } else if (player.equals(PLAYER_HUMAN)) {
            bestScore = 100;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).getScore() < bestScore) {
                    bestScore = moves.get(i).getScore();
                    bestMove = i;
                }
            }
        }
        return moves.get(bestMove);
    }
}
